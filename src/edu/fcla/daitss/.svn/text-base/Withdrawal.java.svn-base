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
package edu.fcla.daitss;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.entity.Event;
import edu.fcla.daitss.entity.light.DataFile;
import edu.fcla.daitss.entity.light.IntEntity;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.InvalidWithdrawalTypeException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.report.Reporter;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.StorageException;

/**
 * Atomic unit of withdrawal.
 * 
 * @author franco
 * 
 * 
 */
public class Withdrawal extends ArchiveWorker {

	/**
	 * @author franco
	 * 
	 */
	public enum Type {
		/**
		 * archive requested
		 */
		archive("WA"),
		
		/**
		 * account requested
		 */
		owner("WO");
		
		private String code;
		
		Type(String code) { this.code = code; };
		
		/**
		 * @return the code of this withdrawal type
		 */
		public String getCode() { return this.code; };
	}

	/**
	 * @param args
	 * @throws FatalException 
	 */
	public static void main(String[] args) throws FatalException {
		
		// parameters
		String ieid = System.getProperty("daitss.withdrawal.ieid");
		String type = System.getProperty("daitss.withdrawal.type");
		int contactId = Integer.parseInt(System.getProperty("daitss.withdrawal.contactId"));		
		
		try {
			
			Withdrawal w = null;
			
			// prepare the withdrawal
			try {
				if(type.equalsIgnoreCase("archive")) {
					w = new Withdrawal(ieid, Type.archive, contactId);
				} else if (type.equalsIgnoreCase("account")) {
					w = new Withdrawal(ieid, Type.owner, contactId);
				} else {
					throw new InvalidWithdrawalTypeException(type);
				}
			} catch (WithdrawalException e) {
				Informer.getInstance().error("Withdrawal could not be processed", "Preparing Withdrawal", e);
			}
		
			// run the withdrawal
			w.process();
			
		} catch (PackageException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (FatalException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}

		Informer.getInstance().info(
				Withdrawal.class.getName(),
				new Throwable().getStackTrace()[0].getMethodName(),
				"Withdrawal of " + ieid + " successfull",
				"Withdrawing",
				true);
		
		// exit gracefully
		System.exit(0);
	}

	private String account;

	private int contactId;

	private String ieid;

	private IntEntity intEntity;

	private Type type;

    private List<Copy> copies;

	/**
	 * @param ieid
	 *            the intellectual entity identifier.
	 * @param type
	 *            the type of withdrawal.
	 * @param contactId 
	 *            the id of the contact requesting the withdrawal.
	 * @throws FatalException
	 * @throws WithdrawnException
	 * @throws FatalException
	 * @throws NotArchivedException 
	 * @throws WithdrawalPermissionException 
	 */
	public Withdrawal(String ieid, Type type, int contactId) throws WithdrawnException, FatalException, NotArchivedException, WithdrawalPermissionException {
		
		setTempDir();
		
		ArchiveManager.getInstance().register(ArchiveManager.ACTION_WITHDRAWAL, this);
		this.ieid = ieid;
		this.type = type;
		this.contactId = contactId;

		// check if package exists
		if (!exists()) {
			throw new NotArchivedException(ieid);
		}
		
		// check if contact can withdraw package
		this.account = getPackageAccount();
		if (!contactCanWithdrawal(account)) {
			throw new WithdrawalPermissionException(ieid, contactId);
		}
				
		// check of package is already withdrawn
		if (isWithdrawn()) {
			throw new WithdrawnException(ieid);
		}
		
		// check if its a GFP
		if (isGlobalPackage()) {
			throw new WithdrawnException("intellectual entity " + ieid + " is a global package");
		}
		
		intEntity = new IntEntity(ieid);
		copies = intEntity.copies();
	}

	/**
	 * @return	true if the package exists in the archive
	 * @throws FatalException 
	 */
	private boolean exists() throws FatalException {
		boolean exists = false;
		try {
			String sql = String.format(
							"SELECT * FROM %s WHERE %s = %s",
							ArchiveDatabase.TABLE_INT_ENTITY,
							ArchiveDatabase.COL_INT_ENTITY_IEID, SqlQuote.escapeString(ieid));
			DBConnection conn = DBConnection.getConnection();
			ResultSet r = conn.executeQuery(sql);
			if (r.first()) {
				exists = true;
			} else {
				exists = false;
			}
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot determine if " + ieid + " exists archive",
					"Withdrawing", e);
		}
		return exists;		
	}

	/*
	 * Returns true if the int entity is a global package
	 */
	private boolean isGlobalPackage() throws FatalException {
		List<String> globalIeids = new Vector<String>();
		try {
			DBConnection con = DBConnection.getConnection();
			String sql  = String.format("select distinct %s from %s where %s=true",
					ArchiveDatabase.COL_DATA_FILE_IEID,
					ArchiveDatabase.TABLE_DATA_FILE,
					ArchiveDatabase.COL_DATA_FILE_IS_GLOBAL);
			
			ResultSet set = con.executeQuery(sql);
			while(set.next()) {
				String globalIeid = set.getString(ArchiveDatabase.COL_DATA_FILE_IEID);
				globalIeids.add(globalIeid);
			}
			
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot select global file packages",
					"Withdrawing",
					e);
		}
		
		boolean isGlobal;
		if(globalIeids.contains(ieid)) {
			isGlobal = true;
		} else {
			isGlobal = false;
		}
		return isGlobal;
	}

	private boolean contactCanWithdrawal(String account) throws FatalException {
		boolean canRequestWithdrawal = false;
		try {
			DBConnection con = DBConnection.getConnection();
			String sql = String.format("select %s from %s where %s = %s and %s = %s", 
					ArchiveDatabase.COL_OUTPUT_REQUEST_CAN_REQUEST_WITHDRAWAL,
					ArchiveDatabase.TABLE_OUTPUT_REQUEST,
					ArchiveDatabase.COL_OUTPUT_REQUEST_CONTACT,
					SqlQuote.escapeInt(contactId),
					ArchiveDatabase.COL_OUTPUT_REQUEST_ACCOUNT,
					SqlQuote.escapeString(account));
			ResultSet set = con.executeQuery(sql);
			if(set.first()){
				canRequestWithdrawal = set.getBoolean(ArchiveDatabase.COL_OUTPUT_REQUEST_CAN_REQUEST_WITHDRAWAL);
			} else {
				// undefined is the same as saying no
				canRequestWithdrawal = false;
			}
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot select withdrawal permissions for contact id: " + contactId,
					"Withdrawing",
					e);
		}
		return canRequestWithdrawal;
	}

	/**
	 * @return a list of data files for this int entity
	 */
	public List<DataFile> getDataFiles() {
		return intEntity.getDataFiles();
	}

	/**
	 * @return the ieid to be withdrawn
	 */
	public String getIeid() {
		return ieid;
	}

	private String getPackageAccount() throws FatalException, WithdrawnException {
		String account = null;
		try {
			DBConnection con = DBConnection.getConnection();
			String sql = String.format(
					"select ap.%s from %s a, %s ap where a.%s = %s and ap.%s = a.%s", 
					ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT,
					ArchiveDatabase.TABLE_ADMIN,
					ArchiveDatabase.TABLE_ACCOUNT_PROJECT,
					ArchiveDatabase.COL_ADMIN_OID,
					SqlQuote.escapeString(ieid),
					ArchiveDatabase.COL_ACCOUNT_PROJECT_ID,
					ArchiveDatabase.COL_ADMIN_ACCOUNT_PROJECT);

			ResultSet set = con.executeQuery(sql);
			if(set.first()){
				account = set.getString(ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT);
			} else {
				throw new WithdrawnException("No account associated with int entity " + ieid);
			}
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot select account for int entity: " + ieid,
					"Withdrawing",
					e);
		}
		return account;
	}

	private boolean isWithdrawn() throws FatalException {
		boolean withdrawn = false;
		try {
			String sql = String.format(
							"SELECT * FROM %s WHERE %s = %s AND (%s = %s OR %s = %s)",
							ArchiveDatabase.TABLE_EVENT,
							ArchiveDatabase.COL_EVENT_OID, SqlQuote.escapeString(ieid),
							ArchiveDatabase.COL_EVENT_EVENT_TYPE, SqlQuote.escapeString(Type.archive.getCode()),
							ArchiveDatabase.COL_EVENT_EVENT_TYPE, SqlQuote.escapeString(Type.owner.getCode()));
			DBConnection conn = DBConnection.getConnection();
			ResultSet r = conn.executeQuery(sql);
			if (r.first()) {
				withdrawn = true;
			} else {
				withdrawn = false;
			}
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot select withdrawal event from database for " + ieid,
					"Withdrawing", e);
		}
		return withdrawn;
	}

	/**
	 * Execute the actual withdrawal.
	 * 
	 * @throws FatalException
	 */
	public void process() throws FatalException {

		try {
	        TransactionConnection scon = DBConnection.getSharedConnection();
	        deleteMetaData(scon);
	        recordEvent(scon, Event.OUTCOME_SUCCESS);
	        scon.commitTransaction();
			scon.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Database Problem",	"Withdrawing" + this.intEntity.getIeid(), e);
		}
		deleteCopies();		
		Reporter.reportWithdrawal(this);
	}
	
	private void deleteCopies() throws FatalException {

        List<Copy> residualCopies = new Vector<Copy>();

        for (Copy copy : copies){
            try {
                copy.delete();
            } catch(StorageException e) {
                residualCopies.add(copy);
            }
        }
    
        if (residualCopies.size() > 0) {
            Informer.getInstance().warning("incomplete storage removal, report sent to default account","withdrawing" + this.intEntity.getIeid());
            Reporter.reportIncompleteContentRemoval(ieid, residualCopies);
        }

    }

    private void deleteMetaData(TransactionConnection scon) throws FatalException {
        intEntity.deleteGlobalFileReferences(scon);
        intEntity.deleteMetaDataFiles(scon);
        intEntity.deleteCopies(scon);
    }

    /**
	 * Method to handle Withdrawal error (when PackageException is encountered)
	 */
	public void error() {
	    // Intelligent Design
	}
	
	/** 
	 * Method to handle Withdrawal program failure (when a 
	 * FatalException is encountered).
	 */	
	public void fail() {
		// stop everything
		System.exit(1);		
	}
	
	private void recordEvent(TransactionConnection scon, String outcome) throws FatalException {
		Map<String, String> m = new Hashtable<String, String>();		
		m.put(ArchiveDatabase.COL_EVENT_ID, Long.toString(OIDServer.getNewEventId()));
		m.put(ArchiveDatabase.COL_EVENT_OID, ieid);
		m.put(ArchiveDatabase.COL_EVENT_EVENT_TYPE, this.type.getCode());
		m.put(ArchiveDatabase.COL_EVENT_DATE_TIME, DateTimeUtil.now());
		m.put(ArchiveDatabase.COL_EVENT_EVENT_PROCEDURE, "AIP withdrawal");
		m.put(ArchiveDatabase.COL_EVENT_OUTCOME, outcome);
		m.put(ArchiveDatabase.COL_EVENT_NOTE, String.format("Withdrawal of %s in account %s requested by %s", ieid, account, getContactInfo()));

		Vector<String> cols = new Vector<String>(m.size());
		Vector<String> vals = new Vector<String>(m.size());
		for (String col : m.keySet()) {
			cols.add(col);
			vals.add(m.get(col));
		}
		scon.insert(ArchiveDatabase.TABLE_EVENT, cols, vals);
	}

	private String getContactInfo() throws FatalException {
		String contactInfo = null;
		try {
			String sql = String.format(
							"SELECT %s, %s FROM %s WHERE %s = %s",
							ArchiveDatabase.COL_CONTACT_EMAIL,
							ArchiveDatabase.COL_CONTACT_NAME,
							ArchiveDatabase.TABLE_CONTACT,
							ArchiveDatabase.COL_CONTACT_ID,
							SqlQuote.escapeInt(contactId));
			DBConnection conn = DBConnection.getConnection();
			ResultSet r = conn.executeQuery(sql);
			if (r.first()) {
				contactInfo = String.format("%s <%s>",
						r.getString(ArchiveDatabase.COL_CONTACT_NAME),
						r.getString(ArchiveDatabase.COL_CONTACT_EMAIL));
			} else {
				Informer.getInstance().fail(this,
						new Throwable().getStackTrace()[0].getMethodName(),
						"No contact information found for contact id " + contactId,
						"Withdrawing", new FatalException());

			}
			r.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot select contact information for contact id " + contactId,
					"Withdrawing", e);
		}
		return contactInfo;
	}

	/**
	 * @return the account of this int entity
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @return the package name of this int entity
	 */
	public String getPackageName() {
		return intEntity.getPackageName();
	}
	
}
