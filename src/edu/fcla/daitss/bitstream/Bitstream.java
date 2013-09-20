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
package edu.fcla.daitss.bitstream;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.severe.element.MetadataConflicts;
import edu.fcla.daitss.util.Informer;

/**
 * Represents a group of bits within a file. The bits make up some
 * kind of complete content (an image, an audio track, a text section,
 * a video, etc.).
 * 
 * All Bitstream subclasses have to have a corresponding entry in the 
 * database configuration file's list of valid bitstream tables.
 */
public class Bitstream {
	
	/**
	 * False
	 */
	public static final String FALSE = "FALSE";
	
	/**
	 * An offset to a byte from the beginning of a file. 
	 */
	public static final String LOC_FILE_BYTE_OFFSET = "FILE_BYTE_OFFSET";
	
	/**
	 * An offset to a byte from the beginning of the uncompressed header for the bitstream.
	 */
	public static final String LOC_UNCOMPRESSED_HEADER_BYTE_OFFSET = "UNCOMPRESSED_HEADER_BYTE_OFFSET";
	
	/**
	 * A line number (first line is the 0th line). 
	 */
	public static final String LOC_LINE_NUM = "LINE_NUM";
	
	/**
	 * A range of line numbers (first line is the 0th line).
	 * The range is delimited by a '-' (ex: 0-9). 
	 */
	public static final String LOC_LINE_RANGE = "LINE_RANGE";
	
	/**
	 * The location type is not applicable. 
	 */
	public static final String LOC_NA = "N/A";
	
	/**
	 * The maximum length of a Bitstream table name.
	 */
	protected static final int MAX_BSTABLE_LENGTH = 64;
	
	/**
	 * Maximum length of a location String
	 */
	private static final int MAX_LOCATION_LENGTH = 255;
	
	/**
	 * maximum valid sequence number.
	 * NOTE: this needs to correlate with DataFile.MAX_NUM_BS
	 */
	public static final int MAX_SEQUENCE = 9999999;
	
	/**
	 * minimum valid sequnce number
	 */
	public static final int MIN_SEQUENCE = 0;
	
	/**
	 * DAITSS was the origin. Used in several Bitstream members to specify the 
	 * character set's origin, etc.
	 */
	public static final String ORIG_ARCHIVE = "ARCHIVE";

	/**
	 * The customer was the origin. Used in several Bitstream members to specify the 
	 * character set's origin, etc.
	 */
	public static final String ORIG_DEPOSITOR = "DEPOSITOR";
	
	/**
	 * 
	 */
	public static final String ORIG_NA = "N/A";
	
	/**
	 * True
	 */
	public static final String TRUE = "TRUE";

	/**
	 * Unknown
	 */
	public static final String UNKNOWN = "UNKNOWN";
	/**
	 * constant bit rate
	 */
	public static final String CBR = "CBR";	
	/**
	 * variable bit rate
	 */
	public static final String VBR = "VBR";	
	
	/**
	 * This bitstream's unique ID. 
	 */
	private String bsid = "";
	
	/**
	 * The database table name containing bitstream-format-specific
	 * information about this bitstream.
	 */
	private String bsTable = null;
	
	/**
	* A code designating the data compression scheme used on 
	* this bitstream. 
	*/
	protected Compression compression = null;
	
	/**
	 * Represents the file that this bitstream is in.
	 */
	private DataFile df = null;
	
	/**
	 * This bitstream's data file's unique ID. The default is an empty String.
	 */
	private String dfid = "";
	
	/**
	 * The location within a file that this bitstream starts.
	 * The default is null.
	 */
	private String location = null;
	
	/**
	 * The type of location within a file that this bitstream uses to
	 * identify its location. The default is "UNKNOWN".
	 */
	private String locationType = UNKNOWN;

	/** 
	* A list of the profiles this image complies with.
	* Contains String objects.
	*/
	protected Vector profiles = null;
	
	/**
	 * The primary role of this bitstream in the file or related to other bitstreams in the
	 * file.
	 */
	private String role = BitstreamRole.UNKNOWN;
	
	/**
	 * This bitstream's sequence order among all the bitstreams
	 * in this file. The first bitstream gets sequence number 1.
	 */
	private int sequence = 0;

	/**
	 * Construct a Bitstream, set some members and build
	 * some needed data structures.
	 * 
	 * @param df the file containing this bitstream
	 * @throws FatalException
	 */
	public Bitstream(DataFile df) throws FatalException {
		if (df == null) {
			Informer.getInstance().fail(this, "Bitstream(DataFile)",
				"Null argument", "argument: DataFile", 
				new FatalException("DataFile can't be null"));
		}
		this.setDf(df);
		
		// build needed data structures
		this.profiles = new Vector();
		
		// set member default values
		compression = new Compression(Compression.COMP_UNKNOWN);
	}
	
	/**
	 * Add a profile that this bitstream complies with to
	 * the bitstream.
	 * 
	 * @param _profile a profile
	 */
	public void addProfile(String _profile) {
		if (_profile != null && !this.profiles.contains(_profile)){
			this.profiles.add(_profile);
		}
	}
	
	/**
	 * @param sipMetadata
	 * @throws FatalException
	 */
	public void compareMetadata(METSDocument sipMetadata) throws FatalException{
		// check compression
		String _compression = sipMetadata.getCompressionName();
		if (_compression != null) {
			// Daitss use "NONE" for all uncompressed data.  As the compression defined in 
			// the descriptor could use "uncompressed", it shall be set to NONE to have a valid
			// comparison.
			if (_compression.toLowerCase().equals("uncompressed"))
				_compression = Compression.COMP_NONE;
			
			// try to take the compression value defined in the descriptor since the parser didn't retrieve any.
			if (compression.equals(Compression.COMP_UNKNOWN)) {
				try {
					compression.setCompression(_compression);
				} catch (FatalException e) {
					 Informer.getInstance().warning(this,"compareMetadata",
				        "undefined compression value from descriptor: " + _compression, getDf().getPath());
				}
			} else if (!compression.toString().toLowerCase().equals(_compression.toLowerCase())) {
	    		// record the type of metadata conflict
	    		getDf().addMetadataConflict(getDf().getMetadataConflictsPossible().getSevereElement(
	    			MetadataConflicts.COMPRESSION_MISMATCH));
	            Informer.getInstance().warning(this,"compareMetadata",
	                "compression extracted from  " + getDf().getFileTitle() + " does not match submitted value. ",
	                getDf().getPath() + "\n\t\textracted value: " + compression + "\n\t\tdescriptor value: " + _compression);
			}
		}
	}
	/**
	 * Checks that an Object argument is not equal to null. If the argument is equal
	 * to null the application's fail routine is invoked.
	 * @param method a method name with its parameters
	 * @param argName the name of the parameter variable
	 * @param arg the Object that is the value of the parameter variable
	 * @param caller the class calling this method
	 * @throws FatalException
	 */
	protected void checkForNullObjectArg 
		(String method, String argName, Object arg, String caller) 
		throws FatalException  {
		if (arg == null) {
			Informer.getInstance().fail(
				this, "checkForNullObjectArg(String, String, Object, String)",
				"Null argument",
				"Argument " + argName + " (originally given to " + caller + "." + method + "())",
				new FatalException("Null argument"));
		}
	}
	
	/**
	 * fill in the database column-value pairs for this bitstream
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
	
		columns.add(ArchiveDatabase.COL_BITSTREAM_BSID);
		values.add(this.getBsid());
		if (this.getBsTable() != null) {
			columns.add(ArchiveDatabase.COL_BITSTREAM_BS_TABLE);
			values.add( this.getBsTable());
		}
		columns.add(ArchiveDatabase.COL_BITSTREAM_COMPRESSION);
		values.add(this.getCompression().toString());
		columns.add(ArchiveDatabase.COL_BITSTREAM_DFID);
		values.add(this.getDfid());
		if (this.getLocation() != null) {
			columns.add(ArchiveDatabase.COL_BITSTREAM_LOCATION);
			values.add(this.getLocation());
		}
		columns.add(ArchiveDatabase.COL_BITSTREAM_LOCATION_TYPE);
		values.add(this.getLocationType());
		columns.add(ArchiveDatabase.COL_BITSTREAM_ROLE);
		values.add(this.getRole());
		columns.add(ArchiveDatabase.COL_BITSTREAM_SEQUENCE);
		values.add(new Long(this.getSequence()));
	}
	
	/**
	 * Insert this bistream and its dependency into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		int counterBS = 0; // number of Bitstream table rows affected
		int counterBP = 0; // total number of Bitstream Profile rows affected
		TransactionConnection tcon = DBConnection.getSharedConnection();
	    DBConnection.NameValueVector values = new DBConnection.NameValueVector();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		counterBS = tcon.insert(ArchiveDatabase.TABLE_BS, columns, colValues);
		
		for (Iterator iter = this.getProfiles().iterator(); iter.hasNext();){
		    values.removeAllElements();
		    String prof = (String) iter.next();
		    values.addElement(
		            ArchiveDatabase.COL_BITSTEAM_BS_PROFILE_BS_PROFILE, prof);
		    values.addElement(
		            ArchiveDatabase.COL_BITSTEAM_BS_PROFILE_BSID, this.getBsid());
		    
			// insert the records and get back the number of rows inserted
		    counterBP = counterBP + tcon.insert(ArchiveDatabase.TABLE_BITSTEAM_BS_PROFILE, values);
		}
		return counterBS + counterBP;
	}
	
	/**
	 * update the bistream entry in the database for this bitstream
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		int counterBS = 0; // number of Bitstream table rows affected
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BITSTREAM_BSID, SqlQuote.escapeString(this.getBsid()));
		counterBS = tcon.update(ArchiveDatabase.TABLE_BS, columns, colValues, whereClause);

		return counterBS;
	}
	

	/**
     * Builds an xml representation of the meta data of this Bitsream.
     * 
     *  @throws
     *         FatalException
     * @return Document object
     */
	public Document toXML() throws FatalException {
          
        // Document.
        Document doc = XPaths.newDaitssDoc();
        
        // Root element.
        Element rootElement = doc.getDocumentElement();        
        
        // Namespace.
        String namespace = rootElement.getNamespaceURI();
        
        /* Bitstream */
        Element bitstreamElement = doc.createElementNS(namespace, "BITSTREAM");
        rootElement.appendChild(bitstreamElement);
        
        /* DFID */
        Element dfidElement = doc.createElementNS(namespace, "DFID");
        String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
        Text dfidText = doc.createTextNode(dfidValue);
        dfidElement.appendChild(dfidText);
        bitstreamElement.appendChild(dfidElement);
        
        /* BSID */
        Element bsidElement = doc.createElementNS(namespace, "BSID");
        String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
        Text bsidText = doc.createTextNode(bsidValue);
        bsidElement.appendChild(bsidText);
        bitstreamElement.appendChild(bsidElement);
        
        /* Sequence */
        Element sequenceElement = doc.createElementNS(namespace, "SEQUENCE");
        String sequenceValue = Long.toString(this.getSequence());
        Text sequenceText = doc.createTextNode(sequenceValue);
        sequenceElement.appendChild(sequenceText);
        bitstreamElement.appendChild(sequenceElement);
        
        /* Location */
        if(this.getLocation() != null) {
	        Element locationElement = doc.createElementNS(namespace, "LOCATION");
	        String locationValue = this.getLocation();
	        Text locationText = doc.createTextNode(locationValue);
	        locationElement.appendChild(locationText);
	        bitstreamElement.appendChild(locationElement);
        }
        
        /* Location Type */
        Element locationTypeElement = doc.createElementNS(namespace, "LOCATION_TYPE");
        String locationTypeValue = (this.getLocationType() != null ? this.getLocationType() : "");
        Text locationTypeText = doc.createTextNode(locationTypeValue);
        locationTypeElement.appendChild(locationTypeText);
        bitstreamElement.appendChild(locationTypeElement);

        /* Compression */
        Element compressionElement = doc.createElementNS(namespace, "COMPRESSION");
        String compressionValue = (this.getCompression().toString() != null ? this.getCompression().toString() : "" );
        Text compressionText = doc.createTextNode(compressionValue);
        compressionElement.appendChild(compressionText);
        bitstreamElement.appendChild(compressionElement);

        /* Bitstream Table */
        if (this.getBsTable() != null) {
            Element bsTableElement = (Element) bitstreamElement.appendChild(doc.createElementNS(namespace, "BS_TABLE"));
            bsTableElement.appendChild(doc.createTextNode(this.getBsTable()));
        }
        
        /* Role */
        Element roleElement = doc.createElementNS(namespace, "ROLE");
        String roleValue = (this.getRole() != null ? this.getRole() : "");
        Text roleText = doc.createTextNode(roleValue);
        roleElement.appendChild(roleText);
        bitstreamElement.appendChild(roleElement);
        
        return doc;
    }
	
	/**
	 * Returns this bitstream's unique ID.
	 * 
	 * @return this bitstream's unique ID
	 */
	public String getBsid() {
		return bsid;
	}

	/**Returns the database table name containing bitstream-format-specific
	 * information about this bitstream.
	 *  
	 * @return the database table name containing bitstream-format-specific
	 * information about this bitstream
	 */
	public String getBsTable() {
		return bsTable;
	}
	
	/**
	 * Adds a compression scheme to the list of compression schemes known
	 * to be used on this file. If this compression scheme is already in the list,
	 * this method silently ignores the request. Checks that the compression
	 * code is a valid one. If the compression being added is 'no compression'
	 * or 'unknown compression' it removes all other compression entries. If
	 * the compression being added is anything other than 'no compression'
	 * or 'unknown compression' it removes any 'no compression' and 'unknown
	 * compression' entries.
	 * 
	 * @param compression	the code for a compression scheme known to be used 
	 * 										in this file
	 */
	/*
	public void addCompression(int newComp) {
		// check that the compression code is valid
		if (!Compression.isValidCompression(newComp)) {
			Informer.getInstance().fail(
				this,
				"DataFile.addCompression(int) given a bad argument",
				"compression: " + newComp,
				new IllegalArgumentException("Not a valid compression"));
		}
		Integer compInt = new Integer(newComp);
		// check if this is a repeat
		if (!compressions.contains(compInt)) {
			// this compression is not already in the list
			if (newComp == Compression.NONE ||
				newComp == Compression.UNKNOWN) {
				// remove all other entries
				this.compressions.clear();
			} else {
				// there is compression and we know what type
				// remove any 'none' or 'unknown' entries
				// doesn't matter if they aren't in the list
				compressions.remove(new Integer(Compression.NONE));
				compressions.remove(new Integer(Compression.UNKNOWN));
			}
			//	add it
			compressions.add(compInt);
		}
	}
*/

	/**
	 * Returns the compression algorithm used in this file. The values in
	 * the list are constants in the edu.fcla.daitss.bitstream.util.Compression 
	 * class.
	 * 
	 * @return the compression algorithm used in this bitstream
	 * @see edu.fcla.daitss.bitstream.Compression
	 */
	public Compression getCompression() {
		return this.compression;
	}         

	/**
	 * @return the DataFile object representing the file
	 * 	containing this bitstream.
	 */
	public DataFile getDf() {
		return df;
	}

	/**
	 * @return	the unique id (dfid) of this bitstream's file
	 */
	public String getDfid() {
		return this.dfid;
	}

	/**
	 * Returns the location of this bitstream within the file. Used in conjunction with
	 * <code>locationType</code>.
	 * 
	 * @return the location of this bitstream within the file
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the type of location that <code>getLocation()</code>
	 * refers to.
	 * 
	 * @return the type of location that <code>getLocation()</code>
	 * refers to
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
		* Returns this image's Tiff profiles it complies with
		* 
		* @return	the list of Tiff profiles
		*/
	public Vector getProfiles() {
		return this.profiles;
	}

	/**
	 * @return the primary role of this bitstream in the file
	 */
	public String getRole() {
		return this.role;
	}

	/**
	 * @return this bitstream's sequence order among all bitstreams in this file
	 */
	public int getSequence() {
		return this.sequence;
	}
	
	/**
	 * Determines whether a location type is valid.
	 * Note that this allows the "UNKNOWN" location type to be valid.
	 * 
	 * @param locType the type of referencing locations of bitstreams in the file
	 * @return <code>true</code> if the location type is valid, else
	 * 	<code>false</code>
	 */
	protected boolean isValidLocationType(String locType) {
		boolean result = false;
		if (locType != null && 
				(locType.equals(LOC_FILE_BYTE_OFFSET) ||
				locType.equals(LOC_UNCOMPRESSED_HEADER_BYTE_OFFSET) ||
				locType.equals(LOC_NA) ||
				locType.equals(UNKNOWN))){
					result = true;
		}
		return result;
	}

	/**
	 * Sets this bitstream's unique ID (bsid). 
	 * 
	 * @param _bsid this bitstream's unique ID
	 * @throws FatalException
	 */
	public void setBsid(String _bsid) throws FatalException{
		// check that the argument is not null
		this.checkForNullObjectArg("setBSID(String _bsid)",
			"_bsid", _bsid, this.getClass().getName());
			
		if (!OIDServer.isValidBsid(_bsid)){
			Informer.getInstance().fail(this, "setBSID(String)",
			"Illegal argument",
			"_bsid: " + _bsid,
			new FatalException("Not a valid bsid"));
		} 
		
		this.bsid = _bsid;
	}

	/**
	 * Sets the database table name containing bitstream-format-specific
	 * information about this bitstream. 
	 * 
	 * @param _bsTable the database table name containing 
	 * 	bitstream-format-specific information about this bitstream
	 * @throws FatalException
	 */
	public void setBsTable(String _bsTable) throws FatalException{
		//	check that the argument is not null
		if (_bsTable == null || 
			_bsTable.length() > MAX_BSTABLE_LENGTH ||
			!ArchiveDatabase.isValidBitstreamTable(_bsTable)){
			// illegal bitstream table name
			Informer.getInstance().fail(this, "setBsTable(String)",
			"Illegal argument",
			"_bsTable: " + _bsTable,
			new FatalException("Not a valid bitstream table name"));			
		}
		this.bsTable = _bsTable;
	}
	
	/**
	 * @param file
	 */
	public void setDf(DataFile file) {
		df = file;
	}

	/**
	 * @param _dfid	the unique id (dfid) of this bitstream's file
	 * @throws FatalException
	 */
	public void setDfid(String _dfid) throws FatalException{
		//	check that the argument is not null
		this.checkForNullObjectArg("setDfid(String _dfid)",
				 "_dfid", _dfid, this.getClass().getName());		
		
		if (!OIDServer.isValidDfid(_dfid)){
			Informer.getInstance().fail(this,
				"setDFID(String)", "Illegal argument",
				"_dfid: " + _dfid, 
				new FatalException("Not a valid dfid."));
		}
		
		this.dfid = _dfid;
		
	}

	/**
	 * Sets the location of this bitstream within the file.
	 * The location must be less than 256 characters.
	 * 
	 * @param _location the location of this bitstream within the file
	 * @throws FatalException
	 */
	public void setLocation(String _location) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg("setLocation(String _location)",
				 "_location", _location, this.getClass().getName());
		if (_location.length() > MAX_LOCATION_LENGTH) {
			Informer.getInstance().fail(
				this, "setLocation(String)",
				"Location too long",
				"_location: " + _location,
				new FatalException("Illegal bitstream location"));
		}
		location = _location;
	}

	/**
	 * Sets the type of location used in the <code>location</code>
	 * member.
	 * 
	 * @param _locationType the type of location used in the 
	 * 		<code>location</code> member
	 * @throws FatalException
	 * 
	 */
	public void setLocationType(String _locationType) throws FatalException{
		if (!isValidLocationType(_locationType)){
			Informer.getInstance().fail(this, "setLocationType(String)",
				"Illegal argument",
				"_locationType: " + _locationType,
				new FatalException("Not a valid location type"));			
		}
		locationType = _locationType;
	}

	/**
	 * Sets a image's profiles. 
	 * 
	 * @param _profiles	a list of image profiles
	 * @throws FatalException
	 */
	public void setProfiles(Vector _profiles) throws FatalException {
		// check that the Vector is not null
		this.checkForNullObjectArg("setProfiles(Vector _profiles)", 
			"_profiles", _profiles, this.getClass().getName());
		this.profiles = _profiles;
	}

	/**
	 * @param _role the primary role of this bitstream in the file
	 * @throws FatalException
	 */
	public void setRole(String _role) throws FatalException {
		if (_role == null || !BitstreamRole.isValidRole(_role)){
			Informer.getInstance().fail(this, "setRole(String)",
				"Illegal argument",
				"_role: " + _role,
				new FatalException("Not a valid bitstream role"));
		}
		this.role = _role;
	}

	/**
	 * Sets a bitstream's sequence number among all bitstreams in 
	 * the file. Note that a maximum number of file bitstreams has been
	 * set - see <code>MAX_SEQUENCE</code>. If a file has more 
	 * bitreams than this maximum value allows, the application's
	 * fail routines will be called.
	 * 
	 * @param _sequence this bitstream's sequence order 
	 * 									among all bitstreams in this file
	 * @throws FatalException
	 */
	public void setSequence(int _sequence) throws FatalException {
		if (_sequence < MIN_SEQUENCE || 
			_sequence > MAX_SEQUENCE){
			Informer.getInstance().fail(this, "setSequence(long)",
				"Illegal argument",
				"_sequence: " + _sequence,
				new FatalException("Not a valid sequence number"));
		}
		this.sequence =_sequence;
	}
	
	/**
	 * Returns all of this bitstream's members and their values.
	 * 
	 * @return the members of this class as a String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		
		Iterator iter = null;
		int count = 0;
		
		sb.append("\tBSID: " + this.getBsid() + "\n");
		sb.append("\tBitstream's DFID: " + this.getDfid() + "\n");
		sb.append("\tBitstreamTable: " + this.getBsTable() + "\n");
		sb.append("\tCompression: " + this.getCompression() + "\n");
		sb.append("\tLocation: " + getLocation() + "\n");
		sb.append("\tLocationType: " + this.getLocationType() + "\n");
		sb.append("\tProfiles:");
		if (this.getProfiles() != null && this.getProfiles().size() > 0) {
			sb.append("\n");
			iter = this.getProfiles().iterator();
			count = 0;
			while (iter.hasNext()){
				String p = (String)iter.next();
				sb.append("\t\tProfile " + count + ": " + p + "\n");
				count++;
			}
		} else {
			sb.append(this.getProfiles() + "\n");
		}
		sb.append("\tRole for Bitstream: " + this.getRole() + "\n");
		sb.append("\tSequence: " + this.getSequence() + "\n\n");
		return sb.toString();
	}

}
