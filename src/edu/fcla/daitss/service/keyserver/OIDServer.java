/*
 * DAITSS Copyright (C) 2007 University of Florida
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package edu.fcla.daitss.service.keyserver;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.MySqlConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;


import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.net.MalformedURLException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Controls the generation and distribution of keys (DFIDs and IEIDs)
 * to files and intellectual objects. It keeps track of the last key given out.
 * A portion of this class is meant to be used through RMI.
 *
 * @author Althea Liang
 *
 */
public class OIDServer extends UnicastRemoteObject implements KeyServer{

	/**
	 *
	 */
	private static final long serialVersionUID = -5015421620893941640L;

	/**
	 * DFID represents the DataFile objects (physical files).
	 */
	private static class DFID extends ID {
		/*
		 * The leading charactor of the key,'F' for DataFile, 'X' for dummy value
		 * 'F' for BitStream
		 */
		static final char leadChar = 'F';

		/**
		 * The method actually checks the syntax of a BitStream key.
		 * --Called by OIDServer.isValidBSID()
		 * @param bsKey : BitStream key of the format Fyyyymmdd_AAAAAA_0[1-7]
		 * @return ture if valid, false otherwise.
		 */
		static private boolean isValidBsid(String bsKey){

			if (bsKey.length()>24 || bsKey.length()<18){
				return false;
			}

			String fileID = bsKey.substring(0,16);
			boolean vld = DFID.isValidId(fileID);

			String serialID = bsKey.substring(16);
			vld = vld && serialID.matches("_[0-9]{1,7}");
			return vld;
		}

		/**
		 * The method actually checks the syntax of a DataFile key.
		 * --Called by OIDServer.isValidDFID(). A valid DFID must start
		 * with 'F' or 'X'
		 * @param key
		 * @return true if the syntax of the given DataFile key is correct
		 */
		static private boolean isValidId(String key) {
			return key.matches(leadChar + "\\d{8}_[A-Z]{6}")
					&& isValidDate(key.substring(1,9));
		}

		/**
		 * Constructor, taking the Alphabetic and time part of the ID
		 * @param yd : The time part of the ID
		 * @param al : The alphabetic part of the ID
		 */
		private DFID(String yd, String al){
			super(yd,al);
		}

		/**
		 * To distribute a new DFID, i.e., the incremented last-used DFID.
		 * Concatenate 'F' with the alphabetic portion and the time portion of
		 * the incremented key.
		 *
		 * @return ?
		 * @throws FatalException
		 */
		protected synchronized String getNextKey()throws FatalException{

			this.incrementKey();
			byte leading[] = {DFID.leadChar};
			return
				new String(leading) + new String(this.lastTimeId) + "_" + new String(this.lastAlphaId);
		}

		/**
		 * @return its DFID
		 */
		public String toString() {
			return leadChar + new String(lastTimeId) + "_" + new String(lastAlphaId);
		}
	}

	/**
	 * EntityID represents entity IDs.
	 *
	 */
	private static class EntityID {

		/**
		 * @param args
		 */
		public static void main(String[] args) {
			String id = "abcdefghij--nruvwxwz12345_";
			System.out.println(id + " is a valid entity id: " + isValidId(id));
		}

		/**
		 * @param id
		 * @return boolean variable
		 */
		public static boolean isValidId(String id){
                  return (id.length() <= 32);
		}
	}

	/**
	 * Common parent class of DFID and IEID
	 * @author althea
	 *
	 */
	private static abstract class ID {

		/**
		 * class constant
		 */
		public static final int ALPHA_LENTH = 6;
		/**
		 * class constant
		 */
		public static final String MAX_ALPHA = "ZZZZZZ";
		/**
		 * class constant
		 */
		public static final String MIN_ALPHA = "AAAAAA";

		/**
		 * Given the date part of a key (8 chars),test if it conforms to "yyyyMMdd"
		 * @param dateKey Date part of a key
		 * @return true if dateKey is valid date false otherwise
		 */
		protected static boolean isValidDate(String dateKey){
			String year = dateKey.substring(0,4);
			String month = dateKey.substring(4,6);
			String day = dateKey.substring(6,8);

			if (year.matches("\\d{4}")){
				try{
					if (Integer.parseInt(month)<=12
					&& Integer.parseInt(month)>=1
					&& Integer.parseInt(day)<=31
					&& Integer.parseInt(day)>=1)
						return true;
				}
				catch(NumberFormatException nfe){
					return false;
				}
			}
			return false;
		}

		/**
		 * 'F' for DataFile, 'X' for dummy value
		 * 'E' for IntEntity, 'X' for dummy value
		 * 'F' for BitStream
		 */
		//protected static char leadChar;

		/**
		 * Last used Id's Alphabetic part, of format "AAAAAA".
		 */
		protected byte[] lastAlphaId;

		/**
		 * Last used Id's time part, of format "YYYYMMDD",
		 * which corresponds to the time when the last leadChar is assigned.
		 */
		protected byte[] lastTimeId;

		/**
		 * increment the key by "1"
		 */

		/**
		 * Constructor, taking the Alphabetic and time part of the ID
		 * @param yd : The time part of the ID
		 * @param al : The alphabetic part of the ID
		 */
		protected ID(String yd, String al){
			this.lastTimeId = yd.getBytes();
			this.lastAlphaId = al.getBytes();
		}
		/**
		 *
		 * @return the next usable key
		 * @throws FatalException
		 */
		protected abstract String getNextKey() throws FatalException;

		/**
		 * Increment the alphabetic part of the key
		 * -- Called by incrementKey()
		 */
		protected void incrementAlpha(){
			//System.out.println("incrementAlpha called");
			for (int i = ID.ALPHA_LENTH - 1; i >0; i--){
				if (lastAlphaId[i] == 'Z'){
					lastAlphaId[i] = 'A';
				}
				else {
					lastAlphaId[i] += 1;
					break;
				}
			}
		}

		/**
			 * To increment the last used Id represented by lastAlphaId and lastTimeId--
			 * if the current date is greater than lastTimeId, start over for the new date
			 * if the current date is the same as lastTimeId, increment the Alphabetic part
			 * --
			 * Called by getNextKey()
			 * @throws FatalException
			 */
		protected void incrementKey() throws FatalException{
			//cannot made static: this must override superclass's abstract class. while a class method can't be overriden

			String methodName = "incrementKey()";

			/* Get current year,month,date */
			Calendar cal = Calendar.getInstance();
			Calendar nowCal = Calendar.getInstance();
			nowCal.clear();
			nowCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
			nowCal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
			nowCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
			Date nowTime = nowCal.getTime();

			/* Get format for DFID.lastTimeId */
			SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getInstance();
			sdf.applyPattern("yyyyMMdd");

			try{
				String timeStr = new String(this.lastTimeId);
				Date lastTime = sdf.parse(timeStr);

				/* Assign the first key for the day */
				if (nowTime.after(lastTime)){
					String nowTimeStr = sdf.format(nowTime);
					this.lastTimeId = nowTimeStr.getBytes();
					this.lastAlphaId = (new String("AAAAAA")).getBytes();
				}

				/* Last key's date is ahead of current date:error */
				else if(nowTime.before(lastTime)){
					Informer.getInstance().fail(
						this,
						methodName,
						"Time conflict in keys assignment:",
						"OIDServer failure",
						new FatalException());
				}

				/*Increment the last key for the requests in the rest of the day */
				else{
					if (lastAlphaId.equals(MAX_ALPHA)){
						Informer.getInstance().fail(
							this,
							methodName,
							"Running out of keys for the day:",
							"OIDServer failure",
							new FatalException());
					}
					/* Normal */
					this.incrementAlpha();
				}
			}
			catch (ParseException pe){
				Informer.getInstance().fail(
					this,
					methodName,
					"Bad internal ID member, lastAlphaId:",
					"OIDServer failure",
					pe);
			}
		}
	}

	/**
	 *
	 * IEID represents intellectual object IDs.
	 */
	private static class IEID extends ID {

		/*
		 * The leading charactor of the key, 'E' for IntEntity, 'X' for dummy value
		 */
		static final char leadChar = 'E';

		/**
		 * The method actually checks the syntax of a IntEntity key
		 * -- Called by OIDServer.isValidIEID()
		 *
		 * @param key
		 * @return boolean
		 */
		private static boolean isValidId(String key) {
			return key.matches(leadChar + "\\d{8}_[A-Z]{6}")
					&& isValidDate(key.substring(1,9));
		}

		/**
		 * Constructor, taking the Alphabetic and time part of the ID
		 * @param yd : The time part of the ID
		 * @param al : The alphabetic part of the ID
		 */
		private IEID(String yd, String al){
			super(yd, al);
		}

		/**
		 * Return the incremented the last used key.
		 * Cantatenate 'E' with the alphabetic portion and the time portion of
		 * the incremented key.
		 * @return String object
		 * @throws FatalException
		 */
		protected synchronized String getNextKey()throws FatalException{
			this.incrementKey();
			byte leading[] = {IEID.leadChar};
			return
				new String(leading) + new String(this.lastTimeId) + "_" + new String(this.lastAlphaId);
		}
	}

	/**
	 * This class name used for logging from static methods.
	 */
	private static final String CLASSNAME
		= "edu.fcla.daitss.service.keyserver.OIDServer";

	/**
	 * Failure exit code.
	 */
	public static int OIDSERVER_FAILURE = 1;

	/**
	 * Maximum event ID that the database can support.
	 */
	private static final long MAX_EVENT_ID = 9223372036854775807L;

	/**
	 * DataFile unique ID.
	 */
	private static DFID dfid;

	/**
	 * Intellectual object unique ID.
	 */
	private static IEID ieid;

	/**
	 * Unique event ID.
	 */
	private long eventId;

	/**
	 * RMI-registered OIDServer name.
	 */
	static String serverName;

	/**
	 * Gets a new DFID from the remote OIDServer. This mitigates the rmi
	 * interface.
	 *
	 * @return a new DFID
	 * @throws FatalException
	 */
	public static String getNewDfid() throws FatalException {
		String methodName = "getNewDfid()";
		String id = "";
		String serverName = null;

		try {
			serverName =
				ArchiveProperties.getInstance().getArchProperty("RMI_URL");
			KeyServer ks = (KeyServer) Naming.lookup(serverName);

			id = ks.getNextDfid();

		} catch (MalformedURLException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Bad URL",
				"URL: " + serverName,
				e);
		} catch (RemoteException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Remote exception",
				"remote class: OIDServer",
				e);
		} catch (NotBoundException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Not bound exception",
				"serverName: " + serverName,
				e);
		}

		return id;
	}

	/**
	 * Gets a new DFID from the remote OIDServer. This mitigates the rmi
	 * interface.
	 *
	 * @return a new DFID
	 * @throws FatalException
	 */
	public static long getNewEventId() throws FatalException {
		String methodName = "getNewEventId()";
		long id = 0;
		String serverName = null;

		try {
			serverName =
				ArchiveProperties.getInstance().getArchProperty("RMI_URL");
			KeyServer ks = (KeyServer) Naming.lookup(serverName);

			id = ks.getNextEventId();

		} catch (MalformedURLException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Bad URL",
				"URL: " + serverName,
				e);
		} catch (RemoteException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Remote exception",
				"remote class: OIDServer",
				e);
		} catch (NotBoundException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Not bound exception",
				"serverName: " + serverName,
				e);
		}
		return id;
	}

	/**
	 * Gets a new DFID from the remote OIDServer. This mitigates the rmi
	 * interface.
	 *
	 * @return a new DFID
	 * @throws FatalException
	 */
	public static String getNewIeid() throws FatalException {
		String methodName = "getNewIeid()";
		String id = "";
		String serverName = null;

		try {
			serverName =
				ArchiveProperties.getInstance().getArchProperty("RMI_URL");
			KeyServer ks = (KeyServer) Naming.lookup(serverName);

			id = ks.getNextIeid();

		} catch (MalformedURLException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Bad URL",
				"URL: " + serverName,
				e);
		} catch (RemoteException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Remote exception",
				"remote class: OIDServer",
				e);
		} catch (NotBoundException e) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Not bound exception",
				"serverName: " + serverName,
				e);
		}

		return id;
	}

	/**
	 * Checks that a BSID passed to it has the right syntax.
	 *
	 * @param bsid Bitstream unique ID
	 * @return whether or not the BSID's form is valid
	 */
	public static boolean isValidBsid(String bsid){
		return DFID.isValidBsid(bsid);
	}

	/**
	 * Checks that a DFID passed to it has the right syntax.
	 * Does not check that the DFID itself is valid in
	 * terms of the database. If the <code>dfid</code> is not valid,
	 * false is returned.
	 * Note: if the syntax is incorrect, it is left to the caller to throw a
	 * FatalException.
	 *
	 * @param key : The dfid to be checked
	 * @return whether or not the DFIF form is valid
	 */
	public static boolean isValidDfid(String key){
		return DFID.isValidId(key);
	}

	/**
	 * Checks that an EntityID passed to it has the right syntax: 2 upper-case characters +
	 * 8 digits, or 3 upper-case characters + 7 digits.
	 * Does not check that the EntityID itself is valid in
	 * terms of the database. If the <code>entityID</code> is not valid, false is returned.
	 * Note: if the syntax is incorrect, it is left to the caller to throw a new
	 * FatalException.
	 *
	 * @param key : The entityId to be checked
	 * @return whether or not the entity ID form is valid
	 */
	public static boolean isValidEntityid(String key){
		return EntityID.isValidId(key);
	}

	/**
	 * Checks the validity of an event ID.
	 *
	 * @param id event ID
	 * @return whether or not the event ID is valid
	 */
	public static boolean isValidEventId(long id){
		return ((id > 0 && id <= MAX_EVENT_ID)?true:false);
	}


	/**
	 * Checks that an IEID passed to it has the right syntax: "E" + 8 upper-case
	 * alphabetic digits. Does not check that the IEID itself is valid in
	 * terms of the database. If the <code>ieid</code> is not valid, false is returned.
	 * Note: if the syntax is incorrect, it is left to the caller to throw a new
	 * FatalException.
	 *
	 * @param key : the ieid to be checked
	 * @return whether or not the IEID form is valid
	 */
	public static boolean isValidIeid(String key){
		return IEID.isValidId(key);
	}

	/**
	 * Starts up the OIDServer and registers its server name with
	 * the RMI registry. Meant to be called before other applications that
	 * use its ID services.
	 *
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException{

		String methodName = "main(String[])";

		try{

		    System.out.println("CHECKING FOR EXTSTENCE OF A SecurityManager");
			if (System.getSecurityManager() == null){
			    System.out.println("ABOUT TO CREATE RMISecurityManager instance");
			    System.setSecurityManager(new RMISecurityManager());
			}

			System.out.println("ABOUT TO CREATE OIDServer instance");
			int port = Integer.parseInt(ArchiveProperties.getInstance().getArchProperty("RMI_PORT"));
			System.out.println();

			System.out.println("port number for RMI clients: " + port);
			KeyServer oidServer = new OIDServer(port);

			System.out.println();
			System.out.println();
			System.out.println("OIDServer.serverName: " + OIDServer.serverName);
			System.out.print("rebinding: " + OIDServer.serverName + "...");
			Naming.rebind(OIDServer.serverName, oidServer);
			System.out.println("done");


		} catch (RemoteException e){
			Informer.getInstance().fail(
				CLASSNAME, methodName, "Can't invoke Remote Method Call",
				"Quitting ...", e);
			System.exit(OIDSERVER_FAILURE);
		} catch (MalformedURLException e){
			Informer.getInstance().fail(
				CLASSNAME, methodName, "Bad registration name",
				"URL: " + OIDServer.serverName, e);
			System.exit(OIDSERVER_FAILURE);
		}
	}

	/**
	 *
	 * @param port
	 * @throws RemoteException
	 * @throws FatalException
	 */
	@SuppressWarnings("synthetic-access")
	public OIDServer(int port) throws RemoteException, FatalException {
		super(port);

		String methodName = "OIDServer()";

		/* Create the informer and the log for the KeyServer*/
		Informer.getInstance(ArchiveManager.ACTION_KEYSERVER);

		System.out.println("**********AFTER INFORMER.getINstance()");

		/* Construct the OIDServer name registered with the RMI registry*/
		serverName
				= ArchiveProperties.getInstance().getArchProperty("RMI_URL");

		System.out.println("SERVER NAME: " + serverName);

		String tbl ="";//datafile table name
		//String tbl2 = "";//IntEntity file table name

		tbl = ArchiveDatabase.TABLE_DATA_FILE;
		//tbl2 = ArchiveDatabase.TABLE_INT_ENTITY;

		try{
			/*
			 * To initialize dfid
			 */
			/* Look up max(DFID) in DATA_FILE table */
			DBConnection cnn = DBConnection.getConnection();
			Vector cols = new Vector();
			cols.addElement(ArchiveDatabase.COL_DATA_FILE_DFID);
			Informer.getInstance().info(this, methodName,
										"Getting max DFID from "+ tbl +" table",
										"Initializing OIDServer", true);
			ResultSet rs = cnn.retrieveMax(cols, tbl, "");

			String keyStr = "";

		    rs.first();
			if (rs.getRow() == 0 || rs.getString(MySqlConnection.MAX_RESULT) == null) {
			    // no records in DATA_FILE table
				// assign first DFID ever
				Informer.getInstance().info(this,
						methodName,
						"No max DFID in " + tbl + ", generating first key",
						"table: " + tbl,
						false);

				DecimalFormat df = new DecimalFormat("0000");

				StringBuffer sb = new StringBuffer();

				sb.append(DFID.leadChar);
				sb.append(df.format(DateTimeUtil.getYear()-1));

				df = new DecimalFormat("00");
				sb.append(df.format(DateTimeUtil.getMonth()));
				sb.append(df.format(DateTimeUtil.getDayOfMonth()));

				sb.append("_");
				sb.append(ID.MIN_ALPHA);

				keyStr = sb.toString();


			} else {
				keyStr = rs.getString(MySqlConnection.MAX_RESULT);
			}

			Informer.getInstance().info(this,methodName,
										"max DFID from "+ tbl +" table is "+ keyStr,
										"Initializing OIDServer", true);

			StringTokenizer st = new StringTokenizer(keyStr,"_");
			String timeStr = st.nextToken().substring(1);
			String alphaStr = st.nextToken();
			OIDServer.dfid = new DFID(timeStr, alphaStr);

			/*
			 * To initialize ieid
			 */
			/* Look up max(IEID) in INT_ENTITY table */
			tbl = ArchiveDatabase.TABLE_INT_ENTITY;
			cols = new Vector();
			cols.addElement(ArchiveDatabase.COL_INT_ENTITY_IEID);

			Informer.getInstance().info(this,methodName,
										"Getting max IEID from "+ tbl +" table",
										"Initializing OIDServer", true);
			rs = cnn.retrieveMax(cols, tbl, "");

		    rs.first();
			if (rs.getRow() == 0 || rs.getString(MySqlConnection.MAX_RESULT) == null) {
			    // no records in DATA_FILE table
				// assign first DFID ever
				Informer.getInstance().info(this,
						methodName,
						"No max DFID in " + tbl + ", generating first key",
						"table: " + tbl,
						false);

				DecimalFormat df = new DecimalFormat("0000");

				StringBuffer sb = new StringBuffer();

				sb.append(IEID.leadChar);
				sb.append(df.format(DateTimeUtil.getYear()-1));

				df = new DecimalFormat("00");
				sb.append(df.format(DateTimeUtil.getMonth()));
				sb.append(df.format(DateTimeUtil.getDayOfMonth()));

				sb.append("_");
				sb.append(ID.MIN_ALPHA);

				keyStr = sb.toString();


			} else {
				keyStr = rs.getString(MySqlConnection.MAX_RESULT);
			}

			Informer.getInstance().info(this,methodName,
										"max IEID from "+ tbl +" table is "+ keyStr,
										"Initializing OIDServer", true);

			st = new StringTokenizer(keyStr,"_");
			alphaStr = st.nextToken().substring(1);
			timeStr = st.nextToken();
			OIDServer.ieid = new IEID(alphaStr, timeStr);


			// GET THE EVENT ID
			tbl = ArchiveDatabase.TABLE_EVENT;
			cols = new Vector();
			cols.addElement(ArchiveDatabase.COL_EVENT_ID);

			Informer.getInstance().info(this,methodName,
										"Getting max ID from "+ tbl +" table",
										"Initializing OIDServer", true);
			rs = cnn.retrieveMax(cols, tbl, "");

		    rs.first();
			if (rs.getRow() == 0 || rs.getString(MySqlConnection.MAX_RESULT) == null) {
			    // no records in DATA_FILE table
				// assign first DFID ever
				Informer.getInstance().info(this,
						methodName,
						"No max ID in " + tbl + ", generating first key",
						"table: " + tbl,
						false);
				this.eventId = 0;

			} else {
				this.eventId = rs.getLong(MySqlConnection.MAX_RESULT);
			}

			Informer.getInstance().info(this,methodName,
										"max ID from "+ tbl +" table is "+ this.eventId,
										"Initializing OIDServer", true);
			cnn.close();

		} catch (SQLException se){
			Informer.getInstance().fail(
				this,
				methodName,
				"Failure in retrieving the max DFID or IEID:",
				"ResultSet iteration",
				se);
		}
	}



	/**
	 * Call this method to receive a new unique DFID. Although
	 * the method is public due to requirements of the Remote
	 * Interface that the OIDServer implements, IT IS NOT TO BE CALLED
	 * OUTSIDE OF THIS CLASS.
	 *
	 * @return the next unused DFID
	 * @throws FatalException
	 * @throws RemoteException
	 */
	public String getNextDfid() throws RemoteException, FatalException{

		String methodName = "getNextDFID()";
		String nextKey = "";
		try{
			nextKey = dfid.getNextKey();
		} catch(FatalException fe){
			Informer.getInstance().fail(
				this,
				methodName,
				"Can't get next DFID",
				fe.getMessage(),
				fe);
		}
		return nextKey;
	}


	/**
	 * Call this method to receive a new unique Entity ID
	 *
	 * @return next unused entity ID
	 * @throws FatalException
	 * @throws RemoteException
	 */
	public String getNextEntityId() throws RemoteException, FatalException{

		return ieid.getNextKey();

	}


	/**
	 * Call this method to receive a new unique IEID. Although
	 * the method is public due to requirements of the Remote
	 * Interface that the OIDServer implements, IT IS NOT TO BE CALLED
	 * OUTSIDE OF THIS CLASS.
	 *
	 * @return the next available event id
	 */
	public long getNextEventId() {
	    this.eventId++;
	    return this.eventId;
	}

	/**
	 * Call this method to receive a new unique IEID. Although
	 * the method is public due to requirements of the Remote
	 * Interface that the OIDServer implements, IT IS NOT TO BE CALLED
	 * OUTSIDE OF THIS CLASS.
	 *
	 * @return next unused IEID
	 * @throws FatalException
	 * @throws RemoteException
	 */
	public String getNextIeid() throws RemoteException, FatalException{

		String methodName = "getNextIEID()";
		String nextKey = "";
		try{
			nextKey = ieid.getNextKey();
		}
		catch(FatalException fe){
			Informer.getInstance().fail(
				this,
				methodName,
				"OIDServer function failure: ",
				fe.getMessage(),
				fe);
		}
		return nextKey;
	}

	/**
	 * Returns the locally-stored RMI-registered name.
	 * It was read from the configuration file and stored
	 * locally in the constructor.
	 *
	 * @return OIDServer Url that is registered with the RMI Registry
	 */
	public String getServerName(){
		return OIDServer.serverName;
	}

}
