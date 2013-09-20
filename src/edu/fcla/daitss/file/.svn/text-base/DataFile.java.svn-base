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
/** 
 * 
 */
package edu.fcla.daitss.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.MySqlConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.entity.AIP;
import edu.fcla.daitss.entity.Agreements;
import edu.fcla.daitss.entity.ArchiveEntity;
import edu.fcla.daitss.entity.Event;
import edu.fcla.daitss.entity.Events;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.Relationship;
import edu.fcla.daitss.entity.Relationships;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.distributed.Distributed;
import edu.fcla.daitss.file.global.GlobalFile;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.markup.XML;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.format.text.dtd.DTD;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Inhibitors;
import edu.fcla.daitss.severe.element.Limitations;
import edu.fcla.daitss.severe.element.MetadataConflicts;
import edu.fcla.daitss.severe.element.Quirks;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.severe.element.Severity;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;

/**
 * Represents a single physical data file in no particular file format. Each subclass of
 * this class represents a DataFile in a particular file format. 
 * 
 * Many of the setters in this class check that the parameters can be supported
 * by the database that will store this class' members. There are essentially 2 groups
 * of these setters - those that call <code>Informer.fail</code> when the input
 * is too large, and those that modify the input so that it can be recorded in the database
 * (after recording the anomaly and logging a warning). This second group is
 * treated differently because its input comes from data submitted to the archive
 * and is beyond the control of this program. The first group causes a fatal error
 * either because it was input that was controllable by daitss programs or because
 * it is information considered so vital that the information can not be modified. 
 */

public class DataFile extends ArchiveEntity {
    
	/**
	 * Big-endian byte order (most significant bit first)
	 */
	public static final String BYTE_ORDER_BE = "BE";

	/**
	 * Little-endian byte order (least significant bit first)
	 */
	public static final String BYTE_ORDER_LE = "LE";

	/**
	 * Byte order is not applicable.
	 * For example the file is a mix of little and big-endian order.
	 */
	public static final String BYTE_ORDER_NA = "N/A";

	/**
	 * Unknown byte order
	 */
	public static final String BYTE_ORDER_UNKNOWN = "UNKNOWN";
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.file.DataFile";
    
    /**
     * Default Date
     */
    public static final String DEFAULT_DATE = "0000-00-00 00:00:00";
    /**
     * Default File Size
     */
    public final long DEFAULT_FILE_SIZE = -1;
	/**
	 * False
	 */
	public static final String FALSE = "FALSE";

	/**
	 * Latest copy date
	 */
	protected static final String MAX_COPY_DATE = "9999-12-31 23:59:59";

	/**
	 * Maximum supported creator program length
	 */
	protected static final int MAX_CPROG_LENGTH = 255;

	/**
	 * Latest create date
	 */
	protected static final String MAX_CREATE_DATE = "9999-12-31 23:59:59";

	/**
	 * Maximum supported content type length 
	 */
	protected static final int MAX_CTYPE_LENGTH = 255;

	/**
	 * Maximum supported file extension length
	 * (not including the stopgap '.')
	 */
	protected static final int MAX_FILEEXT_LENGTH = 8;

	/**
	 * Maximum supported file title length
	 */
	protected static final int MAX_FILETITLE_LENGTH = 255;
	
	/**
	 * Maximum supported format attribute name length
	 */
	protected static final int MAX_FORMATATTRIBUTE_LENGTH = 64;
	
	/**
	 * Maximum supported format code length
	 */
	protected static final int MAX_FORMATCODE_LENGTH = 255;
	
	/**
	 * Maximum supported format variation name length
	 */
	protected static final int MAX_FORMATVARIATION_LENGTH = 20;

	/**
	 * Maximum supported file size
	 */
	protected static final long MAX_FSIZE = 9223372036854775807L;

	/**
	 * Maximum supported media type length
	 */
	protected static final int MAX_MTYPE_LENGTH = 200;

	/**
	 * Maximum supported media type version length
	 */
	protected static final int MAX_MTYPEVERS_LENGTH = 32;

	/**
	 * Maximum supported node name length
	 */
	protected static final int MAX_NODE_LENGTH = 64;

	/**
	 * Maximum supported number of bitstreams.
	 * Note: this needs to correlate with Bitstream.MAX_SEQUENCE
	 */
	protected static final int MAX_NUM_BS = 9999999;
	
	/**
	 * Number of XMLDocuments that can be stored about this file
	 * in the <code>xmlMetadata</code> array.
	 */
	private static final int MAX_NUM_XML_METADATA = 10;
	
	/**
	 * Maximum supported URI (of this file if it was downloaded) length
	 */
	protected static final int MAX_ORIGINAL_URI_LENGTH=255;

	/**
	 * Maximum supported path length
	 */
	protected static final int MAX_PATH_LENGTH = 255;

	/**
	 * Earliest copy date
	 */
	protected static final String MIN_COPY_DATE = "1000-01-01 00:00:00";

	/**
	 * Earliest create date
	 */
	protected static final String MIN_CREATE_DATE = "1000-01-01 00:00:00";

	/**
	 * Minimum supported file size (means unknown)
	 */
	protected static final long MIN_FSIZE = -1;

	/**
	 * Minimum supported number of bitstreams.
	 */
	protected static final int MIN_NUM_BS = 0;

	/**
	 * DAITSS was the origin. Used in several DataFile members to specify the 
	 * file's origin, the checksum's origin, etc.
	 */
	public static final String ORIG_ARCHIVE = "ARCHIVE";

	/**
	 * The customer was the origin. Used in several DataFile members to specify the 
	 * file's origin, the checksum's origin, etc.
	 */
	public static final String ORIG_DEPOSITOR = "DEPOSITOR";

	/**
	 * The file was downloaded by DAITSS from the Internet.
	 */
	public static final String ORIG_INTERNET = "INTERNET";

	/**
	 * The file's origin is unknown (not set yet).
	 */
	public static final String ORIG_UNKNOWN = "UNKNOWN";

	/**
	 * True
	 */
	public static final String TRUE = "TRUE";

	/**
	 * Unknown
	 */
	public static final String UNKNOWN = "UNKNOWN";

	/**
	 * Determines whether or not the file is of the format type 
	 * that this class represents.
	 * All subclasses of DataFile must have this method
	 * (enforced by <code>DataFileValidator</code>).
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return whether or not the file is in the format that this class represents
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
		//System.out.println("In DataFile isType");
		return isType(filePath, null);
	}

	/**
	 * Determines whether or not the file is of the format type that this class represents.
	 * The DataFile class represents all format types so this 
	 * method will always return true. 
	 * All subclasses of DataFile must override this method
	 * (enforced by <code>DataFileValidator</code>).
	 * 
	 * @return whether or not the file is in the format that this class represents					
	 * @param 	filePath absolute path to an existing readable file
	 * @param	_metadata metadata about 
	 * 			this DataFile (subclasses of DataFile will examine it)
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata)
		throws FatalException {
		String methodName = "isType(String, DataFile)";
		//System.out.println("In DataFile isType");
		
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Invalid argument",
				"filePath: " + filePath,
				new FatalException("Not an existing, readable absolute path to a file"));
		}
		return true;
	}

	/**
	 * Determines whether or not an XML metadata type is valid
	 * in the archive.
	 * 
	 * @param type XML metadata type
	 * @return whether or not the type is valid
	 */
	public static boolean isValidXmlMetadataType(int type) {
	    boolean isValid = false;
	    
	    if ((type == Descriptor.TYPE_SIP_ARCHIVE ||
				type == Descriptor.TYPE_SIP_DEPOSITOR) &&
				type >= 0 &&
				type < MAX_NUM_XML_METADATA){
				    isValid = true;
		}
				
		return isValid;
	}

	/**
	 * Test driver
	 * 
	 * @param args not used
	 * @throws FatalException
	 * @throws PackageException
	 */
	public static void main(String[] args) throws PackageException,
		FatalException {
		
		String testFile = "/daitss/dev/data/ingest/in/UFE0000601/";

		if (DataFile.isType(testFile)){
			System.out.println("Is a DataFile");
			DataFile df =
				new DataFile(
					testFile,
					new SIP("/daitss/dev/data/ingest/in/UFE0000601"),
					null);
			
			
			//System.out.println("Has 3: " + df.hasFormatAttribute(3));
			
			//df.extractMetadata();
		} else {
			System.out.println("Is not a DataFile");
		}
	}

	/**
	 * Format-specific anomalies
	 */
	private Vector anomalies = null;
	
	/**
	 * A list of all known generic anomalies, not necessarily found
	 * in this file
	 */
	protected Anomalies anomsPossible = null;

	/**
	 * The bitstreams contained in this file. Stored as <code>Bitstream</code> 
	 * objects or sublasses of the <code>Bitstream</code> class.
	 */
	private Vector bitstreams = null;

	/**
	 * The byte order of this file. Valid orders are
	 * <code>BYTE_ORDER_UNKNOWN</code> for unknown order, 
	 * <code>BYTE_ORDER_BE</code> for big-endian order, 
	 * <code>BYTE_ORDER_LE</code> for little-endian order and
	 * <code>BYTE_ORDER_NA</code> for not applicable.
	 */
	private String byteOrder = BYTE_ORDER_UNKNOWN;

	/**
	 * Whether or not this file can be deleted.
	 * Valid values are <code>TRUE</code> or <code>FALSE</code>.
	 */
	private String canDelete = FALSE;

	/*
	 * Removed - bugzilla bug 39
	 * A code indicating the file's origin and creation purpose. The provenance 
	 * of the file can be determined by starting at the end (far right) of the string 
	 * and moving left. For example, ???MNMO" means a migrated version of a 
	 * normalized version of a migrated version of a partner-contributed 
	 * original file.
	 */
	//private String contentType = "";

	/**
	 * The date and time the last copy of this file was created. On ingest this is the
	 * time that this file was written to permanent storage. This date is used to determine
	 * when media migrations are necessary.
	 */
	private String copyDate = DEFAULT_DATE;

	/**
	 * The date and time that this digital file was created. For files not created by DAITSS 
	 * for which the creation date/time is unknown, this should be some time near but before
	 * the copyDate member.
	 */
	private String createDate = DEFAULT_DATE;

	/**
	 * The name of the software program that was used to create this file, if known.
	 * The software version may or may not be included in this String.
	 */
	private String creatorProg = "";

	/**
	 * The DataFile that this normalized/migrated file was normalized/migrated from.
	 */
	private DataFile derivedFrom = null;

	/**
	 * The DFID of the most current migrated version of this digital object. If
	 * the digital object has never been migrated this is the same value as the 
	 * <code>oid</code> member.
	 */
	private String dipVersion = "";

	/**
	 * The file extension of the archival object in storage. This extension may 
	 * or may not be the extension the file originally had. File extensions are 
	 * 'normalized' before they are archived. Each subclass of 
	 * <code>DataFile</code> should set this. The extension does not include
	 * the stopgap ('.').
	 */
	private String fileExt = "";

	/**
	 * The name of the file (file title) before it was given a DAITSS DFID.
	 * This does not include any system paths. For example, if this
	 * file was called "/daitss/daitss/in/ingest/UFE00000001/Smith.pdf",
	 * this member would equal "Smith.pdf". If this file never had a name
	 * before it was given a DAITSS DFID, this member would equal the
	 * DFID + "." + file extension, if it has a file extension, otherwise just
	 * the DFID.
	 */
	private String fileTitle = "";
	
	/**
	 * Format-specific file attributes of this file. 
	 * Contains String objects.
	 */
	private Vector formatAttributes = null;
	
	/**
	 * A code representing this file's format (media type, version,
	 * and possibly variation of the media type).
	 */
	private String formatCode = "";
	
	/**
	 * This file's particular variation on its format,
	 * if applicable. Used to distinguish between formats that
	 * have the same media type and version but different
	 * variations on it.
	 */
	private String formatVariation = "";

	/**
	 * The intellectual entity ID (IEID) to which this data file belongs.
	 */
	private String ieid = null;

	/**
	 * A list of coded values indicating any known features of the archival
	 * object intended to inhibit access, copying or migration, such as
	 * encryption, watermarking or content protection. 
	 * Contains Integer objects.
	 */
	private Vector inhibitors = null;

	/**
	 * The Information Package (SIP, etc.) that this file is part of.
	 */
	private InformationPackage ip = null;

	/**
	 * Whether or not this file is really a reference to a common, shared
	 * file in the archive instead of local to this package.
	 */
	private boolean isGlobal = false;

	/**
	 * Used to designate files that have so many errors that they are
	 * not worth a continued parse. For example it is possible for files
	 * that can have pointers to data anywhere in the file to continuously
	 * point to the same locations and to end up in an endless loop.
	 */
	private boolean isHopelessFile = false;

	/**
	 * whether the data file has been archived already.  Used upon re-ingest
	 */
	protected boolean hasArchived = false;
	
	/**
	 * Whether the data file object need to be refreshed.
	 */
	protected boolean needRefresh = false;
	
	/**
	 * Whether or not this file is obsolete. Valid values are 
	 * <code>TRUE</code> or <code>FALSE</code>.
	 */
	private String isObsolete = FALSE;

	/**
	 * Specifies whether or not this data file is the root data file of a
	 * distributed object. Valid values are <code>UNKNOWN</code>,
	 * <code>TRUE</code> or <code>FALSE</code>.
	 */
	private String isRoot = FALSE;
    
	
	/**
	 * Associates 'link aliases' with their relative paths to this
	 * DataFile. Relative paths recorded in this table will eventually
	 * be stored in the database, however it is not always possible
	 * to determine the relative path at the time of dbUpdate. This
	 * is due to the fact that if links reference pre-existing GlobalFiles,
	 * those GlobalFiles are not re-ingested and so they will not be 
	 * in the output (or work) directory at the time of dbUpdate. Relative
	 * paths must be recorded at the time that the link is added. 
	 * 
	 */
	private Hashtable linkAliasRelPathTable = null;
	
	/**
	 * Associates 'link aliases' with DataFile objects.
	 * A link alias is a way that this file referred to another file from within
	 * this file. For example, the alias could be a URL that when dereferenced
	 * refers to a DataFile object.
	 */
	private Hashtable linkAliasTable = null;
		
	/**
	 * Contains Link objects representing the references this file makes to
	 * other files. Only applicable to subclasses of DataFile that implement
	 * the edu.fcla.datss.content.Distributed interface.
	 */
	private Vector links = null;
	
	/**
	 * Absolute path to this file or to a file associated with this file
	 * whose external links have been 'localized'. If this file has no
	 * external links or if it has links but the links were not 'resolved'
	 * at the time of ingest than this member has the same value as
	 * <code>path</code>. If this file has links that were resolved at
	 * the time of ingest than this path is different than <code>path</code>.
	 * In this second case this path will be to a copy of this file 
	 * whose links have been 'relativized' and point to other localized
	 * file paths.
	 * This is set after the first phase of localization.
	 */
	private String localizedFilePath = null;
	
	/**
	 * Thie message digest value of the localized version of this DataFile
	 */
	private String localizedMdValue = null;
	/**
	 * Reference to the DataFile this file was localized from, if applicable.
	 */
	private DataFile locFromDf = null;
	
	/**
	 * Reference to the DataFile this file was localized to, if applicable.
	 */
	private DataFile locToDf = null;

	/**
	 * The MIME media type and subtype of this data file.
	 */
	private String mediaType = "application/octet-stream";

	/**
	 * The version of the <code>mediaSubtype</code>, if applicable. This would, 
	 * for example, be &quot;5.0&quot; if this is a TIFF 5.0 file.
	 */
	private String mediaTypeVersion = "";

	/**
	 * Message digests calculated on this file.
	 */
	protected Vector mesgDigests = null;

	/**
	 * A list of coded values indicating any conflicts between external
	 * metadata describing this file and metadata internal to this file. 
	 * Contains Integer objects.
	 */
	private Vector metadataConflicts = null;

	protected MetadataConflicts metadataConfPossible = null;
	/**
	 * The origin of this file. Valid values are <code>ORIG_ARCHIVE</code>,
	 * <code>ORIG_DEPOSITOR</code>, <code>ORIG_INTERNET</code>, 
	 * and <code>ORIG_UNKNOWN</code> (for DataFile objects whose origin has not yet been set).
	 */
	private String origin = ORIG_UNKNOWN;
	
	/**
	 * The URI of this file if the file's origin is <code>ORIG_INTERNET</code>.
	 */
	private String originalUri = null;
	
	/**
	 * Relative path of this file within its package immediately
	 * before it is written to storage. This path does not include the 
	 * package directory (IEID name) itself. For example, if
	 * the path to this file just before it is written to storage
	 * is "/daitss/prod/data/ingest/out/E20041031_AAAAAA/UF00001111/1.tif"
	 * then its package path is "1.tif".
	 */
	private String packagePath = "";

	/**
	 * Absolute path to the file (ex: &quot;/archive/UFE0000001/images/1.jpg&quot;).
	 * This should be current for the file at any time when the file is 'outside of'
	 * permanent storage, for example while it is being ingested or migrated.
	 * Note: Should be updated anytime the file is moved during processing!
	 */
	private String path = "";

	/**
	 * The level of preservation for this data file. Valid values are
	 * <code>PRES_LEV_NONE</code> which means 'do not archive', 
	 * <code>PRES_LEV_BIT</code> which means 'bit-level' preservation
	 * <code>PRES_LEV_FULL</code> which means full preservation, and
	 * <code>PRES_LEV_UNKNOWN</code> (when this hasn't been
	 * determined yet for a file).
	 */
	private String presLevel = Agreements.PRES_LEV_UNKNOWN;
	
    /**
	 * A list of descriptions of any loss in functionality or change in the look
	 * and feel of the archival object resulting from the preservation
	 * processes and procedures implemented by the archive. For example, if a
	 * PDF file is normalized to TIFF images, hyperlinks and embedded scripts
	 * no longer work. Contains Integer objects.
	 */
	private Vector quirks = null;

	/**
	 * A list of descriptions of any current preservation limitation in the archive.  
	 */
	private Vector limitations = null;
	
	protected Limitations limitationsPossible = null;
	/**
	 * A reader of bytes for use in parsing this file
	*/
	protected ByteReader reader = null;

	/**
	 * The primary role of this file in the package or related to other files in the
	 * package.
	 */
	private String role = FileRole.ROLE_UNKNOWN;

	/**
	 * The size of the data file in bytes.
	 * -1 means that the size is unknown
	 * min size: <code>MIN_FSIZE</code>
	 * max size: <code>MAX_FSIZE</code>
	 */
	private long size = DEFAULT_FILE_SIZE;
	
	/**
	 * Localization 1 state - whether or not this file
	 * was told to start the first phase of localization
	 * (regardless of whether or not it actually did 
	 * localize).
	 */
	private int stateLoc1 = Procedure.NOT_CALLED;
	
	/**
	 * Localization 2 state - whether or not this file
	 * was told to start the second phase of localization
	 * (regardless of whether or not it actually did 
	 * localize).
	 */
	private int stateLoc2 = Procedure.NOT_CALLED;
	
	/**
	 * Migration state - whether or not this file
	 * was told to migrate (regardless of whether or
	 * not it actually did migrate).
	 */
	private int stateMig = Procedure.NOT_CALLED;
	
	/**
	 * Normalization state - whether or not this file
	 * was told to normalize (regardless of whether or
	 * not it actually did normalize).
	 */
	private int stateNorm = Procedure.NOT_CALLED;
	
	/**
	 * Storage prep state - whether or not this file
	 * was told to prepare for storage.
	 */
	private int statePrepStor = Procedure.NOT_CALLED;
	
	
	/**
	 * The size in bytes of the file to be archived after all
	 * storage procedures have been conducted on it.
	 */
	private long storageSize = 0;

	/**
	 * The total number of anomalies found in this file so
	 * far, regardless of whether they are repeated types of
	 * anomalies already recorded.
	 */
	private int totalNumAnoms = 0;
	
	/**
	 * Sets of metadata (METSDocument objects) in XML Mets format 
	 * that describe this file.
	 * The index of the METSDocument object indicates the origin of
	 * the metadata:
	 * 
	 * INDEX			ORIGIN
	 * ------------------------------
	 * 0				SIP descriptor (submitted by depositor)
	 * 1				SIP descriptor (created by the archive)
	 * 2				File parsing
	 */
	private METSDocument[] xmlMetadata = null;

	/**
	 * The only use for this constructor is by some of the <code>isType</code>
	 * methods in subclasses of DataFile and by the DataFileValidator.
	 */
	protected DataFile() {
	}

	/**
	 * Constructor for use when metadata about this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public DataFile(String path, InformationPackage ip) throws FatalException {

		String methodName = "DataFile(String, InformationPackage)";
		
		// build up the set of DataFile subclasses that are exempt from
		// having all the required DataFile methods. this set should be
		// kept to a minimum as most all DataFile subclasses must have
		// these methods
		//String classFatalException = new GlobalFile().getClass().getName();
		
		// check that this DataFile class/subclass contains all the
		// required DataFile methods. This is necessary to prevent
		// a DataFile subclass from simply inheriting methods it should have
		// implemented.
		if (!DataFileValidator.isValidDataFile(this.getClass())){
			Informer.getInstance().fail(this,
					methodName,
					"Invalid DataFile class/subclass",
					"class: " + this.getClass().getName(),
					new FatalException("Doesn't contain all required DataFile methods."));
		}
		
		// check that the filePath arg is non-null, absolute, readable and to a file
		if (!FileUtil.isGoodFile(path)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid argument",
				"path: " + path,
				new FatalException("File is null, not readable or not a file"));
		}

		// build members
		this.anomalies = new Vector();
		this.bitstreams = new Vector();
		this.formatAttributes = new Vector();
		this.inhibitors = new Vector();
		this.linkAliasTable = new Hashtable();
		this.linkAliasRelPathTable = new Hashtable();
		this.links = new Vector();
		this.mesgDigests = new Vector();
		this.metadataConflicts = new Vector();
		this.quirks = new Vector();
		this.limitations = new Vector();

		this.xmlMetadata = new METSDocument[MAX_NUM_XML_METADATA];

		// build a list of all possible generic anomalies, metadata conflict and limitations
		// not necessarily found in this file
		this.anomsPossible = new Anomalies();
		metadataConfPossible = new MetadataConflicts();
		limitationsPossible = new Limitations();
		
		//	set basic members (everything that can be determined without parsing)
		// go through the setters so that they can check the args
		this.setIp(ip);
		
		File tempFile = new File(path);
		this.setFileTitle(tempFile.getName());
		this.setPath(path);
		
	    // set the path to this file relative to the package directory
		this.setPackagePath(
		        FileUtil.stripRoot(
		                this.getPath(), this.getIp().getWorkingPath()));
		
		// the localized file path will start out the same as the file's path
		// and then change later if necessary
		this.setLocalizedFilePath(path);
		
		try {
			this.setSize(FileUtil.getSizeBytes(path));
		} catch (IOException e) {
			Informer.getInstance().fail(
				this,
				methodName,
				"IO Error",
				"Can't get file size for " + path,
				e);
		}
		
		// see how many types of message digests to store.
		int numMDs = -1;
		try {
            numMDs = Integer.parseInt(
                    ArchiveProperties.getInstance().getArchProperty("MD_NUM"));
            if (numMDs < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e1) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Configuration file error",
                    "MD_NUM: " + ArchiveProperties.getInstance().getArchProperty("MD_NUM"),
                    new FatalException("MD_NUM must be a positive number."));
        }
		
        // see what kind of message digests to calculate
        for (int i=1; i<=numMDs; i++) {
            String mdType = ArchiveProperties.getInstance().getArchProperty("MD_TYPE_" + i);
             
            try {
	            if (mdType.equals(ArchiveMessageDigest.ALG_SHA1)){
	                ArchiveMessageDigest md =
	        			MessageDigestCalculator.getMessageDigest(
	        				path,
	        				ArchiveMessageDigest.ALG_SHA1, 
	        				false);
	        		this.addMesgDigest(md);
	            } else if (mdType.equals(ArchiveMessageDigest.ALG_MD5)){
	                ArchiveMessageDigest md =
	        			MessageDigestCalculator.getMessageDigest(
	        				path,
	        				ArchiveMessageDigest.ALG_MD5, 
	        				false);
	        		this.addMesgDigest(md);
	            } else if (mdType.equals(ArchiveMessageDigest.ALG_SHA256)){
	                ArchiveMessageDigest md =
	        			MessageDigestCalculator.getMessageDigest(
	        				path,
	        				ArchiveMessageDigest.ALG_SHA256,
	        				false);
	        		this.addMesgDigest(md);
	            } else if (mdType.equals(ArchiveMessageDigest.ALG_SHA384)){
	                ArchiveMessageDigest md =
	        			MessageDigestCalculator.getMessageDigest(
	        				path,
	        				ArchiveMessageDigest.ALG_SHA384, 
	        				false);
	        		this.addMesgDigest(md);
	            } else if (mdType.equals(ArchiveMessageDigest.ALG_SHA512)){
	                ArchiveMessageDigest md =
	        			MessageDigestCalculator.getMessageDigest(
	        				path,
	        				ArchiveMessageDigest.ALG_SHA512,
	        				false);
	        		this.addMesgDigest(md);
	            }
            }
            catch (PackageException e)
            {
            	throw new FatalException(e.getMessage());
            }
        }
		tempFile = null;
	}

	/**
	 * Constructor for use when metadata about this file is available.
	 * This constructor can also be used when there is no metadata available
	 * for the corresponding file by sending in null for the _liteDataFile
	 * parameter.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata possibly null metadata about this DataFile
	 * @throws FatalException
	 */
	public DataFile(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}

	/**
	 * Adds an anomaly to the list of known anomalies in this file. Only
	 * adds unique anomalies to the list.
	 * Note: Had to make this public so that the bitstreams within a file
	 * can add anomalies to the file.
	 * 
	 * @param newAnom	an anomaly found in this file
	 * @throws FatalException
	 */
	public void addAnomaly(SevereElement newAnom) throws FatalException {
		this.addSevereElement(newAnom, this.anomalies, "anomaly");

		//	increment the total number of anomalies found in this file - used
		// to stop the parsing of a file with way too many errors.
		this.setTotalNumAnoms(getTotalNumAnoms() + 1);
		if (getTotalNumAnoms()
			>= Integer.parseInt(
				ArchiveProperties.getInstance().getArchProperty(
					"ANOMALIES_TOO_MANY"))) {
			System.out.println("num anomalies: " + getTotalNumAnoms());
			// let 'listening' parsers know that they can stop parsing 
			// the file if it hasn't already been done, and log the
			// fact that this file contains too many anomalies
			if (!isHopelessFile){
				this.setHopelessFile(true);
			}
			// record the fact that this file has too many anomalies
			// in its member metadata
			this.addSevereElement(this.getAnomsPossible().
				getSevereElement(Anomalies.FILE_TOO_MANY_ANOMS), 
				this.anomalies, "anomaly");
		}
	}

	/**
	 * Adds a non-null Bitstream object to the list of bitstreams found
	 * in a file. If the Bitstream parameter is null, DAITSS' fail
	 * routine is invoked.
	 * 
	 * @param stream	represents a bitstream found in a file
	 * @throws FatalException
	 */
	protected void addBitstream(Bitstream stream) throws FatalException {
		String methodName = "addBitstream(Bitstream)";
		
		if (stream == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Null argument",
				"stream: null",
				new FatalException("Stream can't be null"));
		}
		// make sure that we aren't exceeding the max supported
		// number of bitstreams (really just need to check if
		// (this.bitstreams.size() == MAX_NUM_BS) here but
		// being conservative ...
		if (this.bitstreams.size() >= MAX_NUM_BS) {
			// we can't support this large a number of bitstreams - log the 
			// anomaly and don't add bitstream
			//SevereElement fa =
			//	Anomalies.getSevereElement(
			//		Anomalies.FILE_OVRLMT_NUM_BITSTREAMS);
			SevereElement fa =
				anomsPossible.getSevereElement(
					Anomalies.FILE_OVRLMT_NUM_BITSTREAMS);
			this.addAnomaly(fa);
			// NOT ADDING BITSTREAM!!
		} else {
			//	set the bitstream's sequence order number (starts at 1 not 0)
			stream.setSequence(bitstreams.size() + 1);
			// set Bitstream's unique ID (BSID) 
			// ID starts from DFID_0 not DFID_1
			stream.setBsid(this.getOid() + "_" + bitstreams.size());
			// set the DFID in the bitstream
			stream.setDfid(this.getOid());
			this.bitstreams.add(stream);
		}
	}
	
	/**
	 * Adds a format-specific attribute to this file's
	 * list of format attributes. If it already has this attribute
	 * in its list it silently ignores the request.
	 * 
	 * @param attribute format attribute
	 * @throws FatalException
	 */
	public void addFormatAttribute(String attribute)
		throws FatalException {
	    String methodName = "addFormatAttribute(String)";
	    
	    if (attribute == null || attribute.equals("")) {
	        Informer.getInstance().fail(this,
                    methodName,
                    "Illegal argument",
                    "attribute: " + attribute,
                    new FatalException("Not a valid format attribute"));
	    }
	    if (this.formatAttributes != null && !this.formatAttributes.contains(attribute)){
	        this.formatAttributes.add(attribute);
	    }
	}

	/**
	 * Adds an access inhibitor to the list of known access inhibitors in this file. If this
	 * inhibitor is already contained in the list, this method silently ignores the request.
	 * Checks that the inhibitor code is valid. 
	 * 
	 * @param newInhib a code for an access inhibitor known to exist in this file
	 * @throws FatalException
	 */
	protected void addInhibitor(SevereElement newInhib) throws FatalException {
		this.addSevereElement(newInhib, this.inhibitors, "inhibitor");
	}

	/**
	 * Adds a Link object to the <code>links</code> member, representing a link 
	 * to another resource from this file. The link may or may not be retrieved.
	 * If the Link parameter is null, the fail routine is invoked.
	 * 
	 * @param link Link object representing an attainable linked-to file
	 * @throws FatalException
	 */
	public void addLink(Link link) throws FatalException {
		// check that the argument is not null
		this.checkForNullObjectArg(
			"addLink(Link link)",
			"link",
			link,
			this.getClass().getName());
		this.links.add(link);
	} // end addLink        

	/**
	 * Associates an identifier (link alias aka URI) with a non-null DataFile.
	 * Used to remember how this file refers to other files from within its
	 * contents, if applicable. If an attempt were made to associate 
	 * the same identifier with 2 different DataFile objects, the second would
	 * 'win'. No attempt is made to see if this identifier is already in the table.
	 * It shouldn't be the case that the same identifier could be associated 
	 * with 2 different DataFile objects from within this file. The application's
	 * link resolution algorithm would have associated the same DataFile
	 * with a given link alias, so this seems a non-issue.
	 * 
	 * @param linkAlias	a URI
	 * @param df	a non-null DataFile
	 * @throws FatalException
	 */
	protected void addLinkAliasDfMapping(String linkAlias, DataFile df)
		throws FatalException {
		String methodName = "addLinkAliasEntry(String, DataFile)";
		
		if (linkAlias == null || linkAlias.length() == 0 || df == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"linkAlias: " + linkAlias + " df: " + df,
				new FatalException("Bad link name or null DataFile."));
		}
		this.linkAliasTable.put(linkAlias, df);
		String linkRelPath = FileUtil.getRelPathFrom(this.getPath(),df.getPath());
		this.linkAliasRelPathTable.put(linkAlias, linkRelPath);
	}

	/**
	 * Adds a ArchiveMessageDigest object to the collection of message digests
	 * calculated on this file.
	 * 
	 * @param md	a ArchiveMessageDigest object
	 * @throws FatalException
	 */
	protected void addMesgDigest(ArchiveMessageDigest md)
		throws FatalException {
		String methodName = "addMesgDigest(ArchiveMessageDigest)";
		
		if (md != null) {
			this.mesgDigests.add(md);
		} else {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"File: " + this.getPath(),
				new FatalException("Message digest must be non-null"));
		}
	}

	/**
	 * Adds a metadata conflict to the list of known metadata conflicts in this file. If this
	 * metadata conflict is already contained in the list, this method silently ignores the 
	 * request. Checks that the metadata conflict code is valid - if it's not the
	 * application's fail routine is called.
	 * 
	 * @param newConflict a code for a metadata conflict known to exist in this file
	 * @throws FatalException
	 */
	public void addMetadataConflict(SevereElement newConflict)
		throws FatalException {
		this.addSevereElement(newConflict, this.metadataConflicts, "mConflict");
	}

	/**
	 * Adds a quirk to the list of quirks known about this file. If this quirk already exists
	 * in the list, the request is silently ignored. 
	 * 
	 * @param newQuirk	a loss in functionality or look and feel of this file
	 * 							as compared to the file it was derived from.
	 * @throws FatalException
	 */
	protected void addQuirk(SevereElement newQuirk) throws FatalException {
		this.addSevereElement(newQuirk, this.quirks, "quirk");
	}

	/**
	 * Adds a limitation to the list of limitations known about this file. 
	 * If this limitation already exists in the list, the request is silently ignored. 
	 * 
	 * @param newLimitation	a loss in functionality or look and feel of this file
	 * 							as compared to the file it was derived from.
	 * @throws FatalException
	 */
	public void addLimitation(SevereElement newLimitation) throws FatalException {
		this.addSevereElement(newLimitation, this.limitations, "limitation");
	}
	
	/**
	 * Adds a severe element to a list of the severe elements found in a file.
	 * Used to record anomalies, quirks, metadata conflicts and inhibitors for a file.
	 * 
	 * @param newElem	the code name for the severe element to add
	 * @param severeElems	the list of severe elements that this severe element should
	 * 										be added to
	 * @param type type of element
	 * @throws FatalException
	 */
	private void addSevereElement(
		SevereElement newElem,
		Vector severeElems,
		String type)
		throws FatalException {
		String methodName = 
			"addSevereElement(SevereElement, Vector, String)";

		// check that the arguments are not null
		if (newElem == null || severeElems == null || type == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Null argument",
				"arg: newElem, severeElems or type",
				new FatalException("Arguments can't be null"));
		}

		// check that it is a valid severe element code	
		if ((type.equals("anomaly")
			//&& !Anomalies.isValidSevereElement(newElem.getName()))
			&& !anomsPossible.isValidSevereElement(newElem.getName()))
			|| (type.equals("inhibitor")
				&& !Inhibitors.isValidSevereElement(newElem.getName()))
			|| (type.equals("mConflict")
				&& !metadataConfPossible.isValidSevereElement(newElem.getName()))
			|| (type.equals("quirk")
				&& !Quirks.isValidSevereElement(newElem.getName()))
			|| (type.equals("limitation")
				&& !limitationsPossible.isValidSevereElement(newElem.getName()))) {
			// stop the press!
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid argument",
				"newElem: " + newElem.getName(),
				new FatalException("Not a valid severe element"));
		}

		// check if this severe element already exists in the list of known 
		// severe elements for this file
		Iterator iter = severeElems.iterator();
		boolean isNewElem = true;
		boolean seenEnough = false; // time-saver
		while (iter.hasNext() && !seenEnough) {
			SevereElement oldElem = (SevereElement) iter.next();
			// only need to have the same names to be duplicates
			if (oldElem.getName().equals(newElem.getName())) {
				seenEnough = true;
				isNewElem = false;
				// it's a repeat of a known severe element in this file
			}
		}
		if (isNewElem) {
			severeElems.add(newElem); // it's an original severe element

			// check to see if the preservation level of the file should be downgraded
			// based on the severity of this severe element 
			if (newElem.getSeverity().equals(Severity.SEVERITY_BIT)
					&& (this.getPresLevel().equals(Agreements.PRES_LEV_FULL)
						|| this.getPresLevel().equals(Agreements.PRES_LEV_UNKNOWN))) {
				Informer.getInstance().info(this,
						methodName,
						"Preservation change from " +
							this.getPresLevel() + " to " +
							newElem.getSeverity(),
						"file: " + this.getPath() + 
							" with anomaly " + newElem.getName(),
						true);
                				
                this.setPresLevel(Agreements.PRES_LEV_BIT);
                
                Events.getInstance().add(
                        new Event(this.getOid(), 
                        Event.EVENT_CHANGED_PRES_DOWN,
                        DateTimeUtil.now(),
                        "Adding severe element to DataFile",
                        Event.OUTCOME_NOT_APPLICABLE,
                        "Changed preservation level to BIT due to presence of severe element",
                        null));                
			}
		}
	}
	
	/**
	 * Stores metadata about this file to a set of all metadata
	 * known about this file at some point (ex: at the start of ingest,
	 * after parsing it, etc.)
	 * 
	 * @param type the type and origin of the metadata
	 * @param metadata metadata about this file
	 * @throws FatalException
	 */
	protected void addXmlMetadata(int type, METSDocument metadata) 
		throws FatalException {
		String methodName = "addXmlMetadata(int, METSDocument)";
		
		// check the validity of the parameters
		if (metadata == null ||
			!isValidXmlMetadataType(type) ) {
			Informer.getInstance().fail(this,
					methodName,
					"Illegal argument",
					"metadata: " + metadata + " type: " + type,
					new FatalException("Not a valid type or metadata is null"));
		}
		
		this.xmlMetadata[type] = metadata;
	}
	
    
    /**
     * Sets the DataFile's role based on certain characteristics.
     *  
     * @throws FatalException
     */
    private void assignRole() throws FatalException {
    	String ipPath = "";
    	
    	if (this.getIp() instanceof SIP)
    		ipPath = ((SIP)this.getIp()).getInitialDescriptor().getPath();
    	else if (this.getIp() instanceof AIP)
       		ipPath = ((AIP)this.getIp()).getInitialDescriptor().getPath();
        if (this.getPath().equals(ipPath)) {
            this.setRole(FileRole.ROLE_DESCRIPTOR_SIP);
        }
        else if (this.isSchema()) {
            this.setRole(FileRole.ROLE_SCHEMA);
        }
        else {
            this.setRole(FileRole.ROLE_CONTENT_FILE);
        }        
        
    }
    
	/**
	 * Determines and assigns this file's format code, using its
	 * media type, version and format variation.
	 * 
	 * @throws FatalException
	 */
	public void calcFormatCode() 
		throws FatalException {
	    String methodName = "calcFormatCode()";
	    
	    String code = null;
	    
	    // media type
	    String mType = this.getMediaType();
	    
	    // media type version
	    String mVersion = this.getMediaTypeVersion();
	    if (mVersion == null) {
	        mVersion = "";
	    }
	    
	    // format variation
	    String variation = this.getFormatVariation();
	    if (variation == null) {
	        variation = "";
	    }
	    
	    code = Format.getFormatCode(mType,
	            mVersion, variation);
	    if (code == null || code.equals("") ||
	            code.length() > MAX_FORMATCODE_LENGTH) {
	        Informer.getInstance().fail(this,
                    methodName,
                    "Invalid format code",
                    "format code: " + code + 
                    " file: " + this.getPath(),
                    new FatalException("Format code doesn't meet constraints."));
	    }
	    
	    this.formatCode = code;
	    
	}

	/**
	 * @return <code>TRUE</code> if this file can be
	 * 	deleted, else <code>FALSE</code>
	 */
	public String canDelete() {
		return this.canDelete;
	}
	
	/**
	 * Checks that this file really is in the file format
	 * represented by this file class. Really an instance wrapper
	 * to a static class so DataFile subclasses will inherit this
	 * method to call their static version of isType. 
	 * This method is potentially called in <code>extractMetadata</code>.
	 * 
	 * @param path absolute path to this file
	 * @param metadata information about this file
	 * @return whether or not this file really is in the file
	 * 	format represented by this file.
	 * @throws FatalException
	 */
	protected boolean checkIsType(String path, METSDocument metadata)
		throws FatalException {
		return isType(path, metadata);
	}
    
    /**
     * Compares the message digests stored in this DataFile against,
     * message digests provided externally. If a DataFile has a digest
     * of the same type as a digest in <code>digests</code> then 
     * a PackageException is thrown, since message digest is considered
     * critical metadata.
     * 
     * @param digests
     * @throws PackageException
     * @throws FatalException
     */
    protected void compareMessageDigests(Vector<ArchiveMessageDigest> digests) 
            throws PackageException, FatalException {
        String procedure = "compareMessageDigests";
        String methodName = "compareMessageDigests(Vector<ArchiveMessageDigest>)";
        
        // for each message digest type in digests, see if a 
        // message digest of the same type has been calculated for this
        // DataFile. If so, compare them.        
        for (ArchiveMessageDigest extMd : digests) {
            String archValue = getMesgDigestValue(extMd.getCode());
            if (archValue != null){
                // an archive-calculated checksum of type extMd.getCode() exists
                if (archValue.equalsIgnoreCase(extMd.getValue())) {
                    // the checksum matches!
                    Events.getInstance().add(
                            new Event(this.getOid(), 
                                    Event.EVENT_VERIFIED_CHECKSUM,
                                    DateTimeUtil.now(),
                                    procedure,
                                    Event.OUTCOME_SUCCESS,
                                    "Compared archive-calculated checksum to submitted checksum. Type: " + extMd.getCode(), 
                                    null));
                }
                else {
                    // the checksum values do not match -> package error
                    Events.getInstance().add(
                            new Event(this.getOid(), 
                                    Event.EVENT_VERIFIED_CHECKSUM,
                                    DateTimeUtil.now(),
                                    procedure,
                                    Event.OUTCOME_FAIL,
                                    "Compared archive-calculated checksum to submitted checksum. Type: " + extMd.getCode(), 
                                    null));
                    
                    Informer.getInstance().error(this,
                            methodName,
                            "Checksum calculated for " + this.getFileTitle() + " does not match submitted checksum. " +
                            		"Possible file corruption or package transmission error.",
                            this.getPath() + "\n\t\tMD type: " + extMd.getCode() + "\n\t\tcalculated value: " + archValue + "\n\t\tdescriptor value: " + extMd.getValue(),
                            new PackageException());
                }
            }// archValue == null, so no digest of this type was found         
        }        
    }
    
    /**
     * Compares the mime type stored in this DataFile against,
     * the  mime type in the descriptor. If there is a conflict
     * file a warning which shall report the conflict in ingest report.
     * 
     * @param _mimeType
     * @throws FatalException
     */
    protected void compareMimeType(String _mimeType) throws FatalException {
    	// check only if the descriptor contains the "Mime Type" metadata
    	if (_mimeType != null) {
    		// strip off the version if there is one included
    		String[] mimeTypeSubstrings = _mimeType.split("\\s");     
    		_mimeType = mimeTypeSubstrings[0];
	    	if (!this.mediaType.toLowerCase().equals(_mimeType.toLowerCase())) {
	    		// record the type of metadata conflict
	    		addMetadataConflict(metadataConfPossible.getSevereElement(
	    			MetadataConflicts.MIME_TYPE_MISMATCH));
	            Informer.getInstance().warning(this, "compareMimeType",
	                "mime type extracted from  " + this.getFileTitle() + " does not match submitted value. ",
	                this.getPath() + "\n\t\textracted value: " + mediaType + "\n\t\tdescriptor value: " + _mimeType);
	    	}
    	}
    }
    
    /**
     * Compares the create date metadata stored in this DataFile against,
     * the create date in the descriptor.  Ignore it if there is a conflict
     * @param _createDate
     * @throws FatalException
     */
    protected void compareCreateDate(String _createDate) throws FatalException {
    	// check only if the descriptor contains the "Mime Type" metadata
    	if (_createDate != null) {
      		if (createDate.equals(DEFAULT_DATE)) {
	    		// use the value in the descriptor if it wasn't retrieved during parsing.
      			createDate = _createDate;
      			// no need to check for conflict as the conflict will be ignored if there is one
    		}
    	}
    }
    
    /**
     * Compares the creator program stored in this DataFile against,
     * the creator program in the descriptor. If there is a conflict
     * file a warning which shall report the conflict in ingest report.
     * 
     * @param _creatorProg
     * @throws FatalException
     */
    protected void compareCreatorProgram(String _creatorProg) throws FatalException {
    	// check only if the descriptor contains the "Creator" metadata
    	if (_creatorProg != null) {
    		if (creatorProg.length() == 0) {
	    		// use the value in the descriptor if it wasn't retrieved during parsing.
    			creatorProg = _creatorProg;
    		}
    		else if (!creatorProg.toLowerCase().equals(_creatorProg.toLowerCase())) {
	    		// record the type of metadata conflict
	    		addMetadataConflict(metadataConfPossible.getSevereElement(
	    			MetadataConflicts.CREATOR_PROGRAM_MISMATCH));
	            Informer.getInstance().warning(this,"compareCreatorProgram",
	                "creator program extracted from  " + this.getFileTitle() + " does not match submitted value. ",
	                this.getPath() + "\n\t\textracted value: " + creatorProg + "\n\t\tdescriptor value: " + _creatorProg);
	    	}
    	}
    }
    
    /**
     * Compares the file size stored in this DataFile against,
     * the file size in the descriptor. If there is a conflict
     * file a warning which shall report the conflict in ingest report.
     * @param _fileSize
     * @throws FatalException
     */
    protected void compareFileSize(String _fileSize) throws FatalException {
    	// check only if the descriptor contains the "File Size" metadata
    	if (_fileSize != null) {
    		Long lFileSize = new Long(_fileSize);
	    	long fileSize = lFileSize.longValue();
	
	    	if (size == DEFAULT_FILE_SIZE) {
	    		// use the value in the descriptor if it wasn't retrieved during parsing.
	    		size = fileSize;
	    	}
	    	else if (size != fileSize) {
	    		// record the type of metadata conflict
	    		addMetadataConflict(metadataConfPossible.getSevereElement(
	    			MetadataConflicts.FILE_SIZE_MISMATCH));
	            Informer.getInstance().warning(this, "compareFileSize",
	                "file size extracted from  " + this.getFileTitle() + " does not match submitted value. ",
	                this.getPath() + "\n\t\textracted value: " + size + "\n\t\tdescriptor value: " + _fileSize);
	    	}
    	}
    }
    
    /**
     * Compares metadata submitted for this data file against metadata
     * that was parsed. If there is a mismatch in critical metadata (like
     * checksum for instance), a PackageException will be thrown resulting
     * in rejection of the containing package. A mismatch in non-critical 
     * metadata will result in a warning. In either case, the conflict should
     * be stored by calling addMetadataConflict(...).
     * 
     * @throws PackageException
     * @throws FatalException
     */
    protected void compareMetadata() throws PackageException, FatalException{
        // check for metadata for this file as extracted from a SIP descriptor,
        // if metadata has been submitted, it will be stored in this.xmlMetadata
        // at the index position Descriptor.TYPE_SIP_DEPOSITOR.
        // if no SIP metadata exists, there's nothing to compare, exit early.
        if (getXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR) == null) {
            return;
        } 
        else {
            METSDocument sipMetadata = getXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR);
            // compare the message digests
            compareMessageDigests(sipMetadata.getMessageDigests()); 
            // compare MIME_TYPE
            compareMimeType(sipMetadata.getFirstMimeType());
            // compare create_date
            compareCreateDate(sipMetadata.getCreateDate());
            // compare file size
            compareFileSize(sipMetadata.getFileSize());
            // comapre creator program
            compareCreatorProgram(sipMetadata.getCreator());
            
            // compare the metadata stored in the bitstream level.
            Iterator itr = bitstreams.iterator();
            while (itr.hasNext()) {
            	Bitstream bitstream = (Bitstream) itr.next();
            	bitstream.compareMetadata(sipMetadata);
            }
        }                   
    }
    
	
	/**
	 * fill in the database column-value pairs for the data file
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_DATA_FILE_BYTE_ORDER); 
		values.add(this.getByteOrder());
		columns.add(ArchiveDatabase.COL_DATA_FILE_CAN_DELETE);
		values.add(this.canDelete());
		columns.add(ArchiveDatabase.COL_DATA_FILE_CREATE_DATE);
		values.add(this.getCreateDate());                        
		columns.add((ArchiveDatabase.COL_DATA_FILE_CREATOR_PROG));
		values.add(this.getCreatorProg());
		columns.add((ArchiveDatabase.COL_DATA_FILE_DFID));
		values.add(this.getOid());
		columns.add(ArchiveDatabase.COL_DATA_FILE_DIP_VERSION);
		values.add(this.getDipVersion());
		columns.add(ArchiveDatabase.COL_DATA_FILE_FILE_COPY_DATE);
		values.add(this.getCopyDate());
		columns.add((ArchiveDatabase.COL_DATA_FILE_FILE_EXT));
		values.add(this.getFileExt());
		columns.add((ArchiveDatabase.COL_DATA_FILE_FILE_TITLE));
		values.add(this.getFileTitle());
		columns.add(ArchiveDatabase.COL_DATA_FILE_FORMAT);
		values.add(this.getFormatCode());
		columns.add(ArchiveDatabase.COL_DATA_FILE_IEID);
		values.add(this.getIeid());
		columns.add(ArchiveDatabase.COL_DATA_FILE_IS_GLOBAL);
		values.add(new Boolean(this.isGlobal));
		columns.add(ArchiveDatabase.COL_DATA_FILE_IS_OBSOLETE);
		values.add(this.isObsolete());
		columns.add(ArchiveDatabase.COL_DATA_FILE_IS_ROOT);
		values.add(this.isRoot());
		if (this.getOriginalUri() != null) {
			columns.add(ArchiveDatabase.COL_DATA_FILE_ORIG_URI);
			values.add(this.getOriginalUri());
		}
		columns.add(ArchiveDatabase.COL_DATA_FILE_ORIGIN);
		values.add(this.getOrigin());
		columns.add(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
		values.add(this.getPackagePath());
		columns.add(ArchiveDatabase.COL_DATA_FILE_PRES_LEVEL);
		values.add(this.getPresLevel());
		columns.add(ArchiveDatabase.COL_DATA_FILE_ROLE);
		values.add(this.getRole());
		columns.add(ArchiveDatabase.COL_DATA_FILE_SIZE);
		values.add(new Long(this.getSize()));
	}

	/**
	 * update the data file entry in the database for this data file
	 * @return number of records updated
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
		int counterBS = 0; // number of Bitstream table rows affected
		int counterDF = 0; // number of Datafile table rows affected
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
			ArchiveDatabase.COL_DATA_FILE_DFID, SqlQuote.escapeString(this.getOid()));
		counterDF = tcon.update(ArchiveDatabase.TABLE_DATA_FILE, columns, colValues, whereClause);
		
		
		// tell each bitstream to dbupdate, remembering the number of affected rows
		if (bitstreams != null && bitstreams.size() > 0) {
			for (Iterator it = bitstreams.iterator(); it.hasNext();) {
				Bitstream bitStrm = (Bitstream) it.next();
				counterBS += counterBS + bitStrm.dbUpdate();
			}
		}
		return counterDF + counterBS;
	}
	
	/**
	 * delete this data file from the database
	 * @throws FatalException
	 */
	public void dbDelete() throws FatalException{
		String whereClause = String.format("%s = %s",
			ArchiveDatabase.COL_DATA_FILE_DFID, SqlQuote.escapeString(this.getOid()));
		TransactionConnection tcon = DBConnection.getSharedConnection();
		
		// since the DATA_FILE table has cascading delete on it, this statement
		// will delete all its dependencies, including bitstream, severe elements...
		tcon.delete(ArchiveDatabase.TABLE_DATA_FILE, whereClause);
	}
	
	/**
	 * Insert this data file and all its dependencies into the database.
	 * @return number of records inserted
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException {
		if (!needRefresh)
			super.dbInsert();
		int counterAll = 0; // total number of rows affected
		int counterDI = 0; // number of Distributed table rows affected
		int counterDF = 0; // number of datafile table rows affected
		int counterFA = 0; // number of datafile format attributes table rows affected
		int counterMD = 0; // number of message digest table rows affected
		int counterSE = 0; // number of severe element rows affected
		int counterBS = 0; // number of Bitstream table rows affected
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
		MySqlConnection.NameValueVector values = new MySqlConnection.NameValueVector();
		
        // if the create date has not been determined yet, then it should be set to the 
        // time that the record was inserted into the database according to the data dictionary
        // definition of create_date
        if (this.getCreateDate().equals(DEFAULT_DATE)) 
            this.setCreateDate(DateTimeUtil.now());
        
		fillDBValues(columns, colValues);
		
        counterDF = tcon.insert(ArchiveDatabase.TABLE_DATA_FILE, columns, colValues);

        // anomalies
		for (Iterator itr = this.getAnomalies().iterator(); itr.hasNext();){
		    values.removeAllElements();
		    String anom = ((SevereElement)itr.next()).getName();
		    values.addElement(
		            ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_SEV_ELEM, anom);
		    values.addElement(
		            ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_DFID, this.getOid());
		    
		    // insert the records and get back the number inserted
		    counterSE += counterSE + tcon.insert(ArchiveDatabase.TABLE_DATA_FILE_SEV_ELEM, values);
		}
	
		// format attributes
		for (Iterator itr = this.getFormatAttributes().iterator(); itr.hasNext();){
		    values.removeAllElements();
		  
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_FORMAT_ATT_DFID, 
		            this.getOid());
		    String att = (String)itr.next();
		    values.addElement(
		            ArchiveDatabase.COL_DATA_FILE_FORMAT_ATT_FORMAT_ATT, 
		            att);
		    
		    // insert the records and get back the number inserted
		    counterFA += counterFA + tcon.insert(ArchiveDatabase.TABLE_DATA_FILE_FORMAT_ATT, values);
		}
	
		// inhibitors
		for (Iterator itr = this.getInhibitors().iterator(); itr.hasNext();){
		    values.removeAllElements();
		    
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_DFID, 
		            this.getOid());
		    String inhib = ((SevereElement)itr.next()).getName();
		    values.addElement(
		            ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_SEV_ELEM, inhib);
		    
		    // insert the records and get back the number inserted
		    counterSE += counterSE + tcon.insert(ArchiveDatabase.TABLE_DATA_FILE_SEV_ELEM, values);
		}
	
		// links
		StringBuffer bufSuccessLinks = new StringBuffer(); // successfully resolved links
		StringBuffer bufIgnoredLinks = new StringBuffer(); // ignored links
		StringBuffer bufBrokenLinks = new StringBuffer(); // broken links
		
		// iterate through the broken and ignored links
		for (Iterator itr = this.getLinks().iterator(); itr.hasNext();){
		    Link lk = (Link) itr.next();
	    	
		    if (lk.getStatus().equals(Link.STATUS_BROKEN)) {
		        bufBrokenLinks.append(lk.getLinkAlias() + "|");
		    } else if (lk.getStatus().equals(Link.STATUS_IGNORED)) {
		        bufIgnoredLinks.append(lk.getLinkAlias() + "|");
		    } else if (lk.getStatus().equals(Link.STATUS_UNKNOWN)) {
		    	Informer.getInstance().fail(this,
						"dbInsert",
						"Illegal link status",
						"status: " + lk.getStatus() + " file: " + this.getPath(),
						new FatalException("Not a valid link status"));
				
		    }
		}
		
		// remove last '|' from broken and ignored link buffers
		if (bufIgnoredLinks.length() > 0){
		    bufIgnoredLinks = 
		        new StringBuffer(bufIgnoredLinks.substring(0, bufIgnoredLinks.length()-1));
		}
		if (bufBrokenLinks.length() > 0){
		    bufBrokenLinks = 
		        new StringBuffer(bufBrokenLinks.substring(0, bufBrokenLinks.length()-1));
		}
		
		// iterate through the successfully retrieved links			
		for (Enumeration e = getLinkAliasRelPathTable().keys(); e.hasMoreElements();) {
		    	String theAlias = (String)e.nextElement();
		        bufSuccessLinks.append("[");
			    bufSuccessLinks.append(theAlias);
			    bufSuccessLinks.append("|");
			    
			    String relPath = (String) this.getLinkAliasRelPathTable().get(theAlias);
			    bufSuccessLinks.append(relPath);
			    bufSuccessLinks.append("]");
		}
		    
	    values.removeAllElements();
	    values.addElement(ArchiveDatabase.COL_DISTRIBUTED_PARENT, this.getOid());
	    values.addElement(ArchiveDatabase.COL_DISTRIBUTED_BROKEN_LINKS,bufBrokenLinks.toString());
	    values.addElement(ArchiveDatabase.COL_DISTRIBUTED_IGNORED_LINKS,bufIgnoredLinks.toString());
	    values.addElement(ArchiveDatabase.COL_DISTRIBUTED_LINKS,bufSuccessLinks.toString());
		    
		// insert the records and get back the number inserted
	    counterDI += tcon.insert(ArchiveDatabase.TABLE_DISTRIBUTED, values);
	
		// metadata conflicts
		for (Iterator itr = this.getMetadataConflicts().iterator(); itr.hasNext();){
		    values.removeAllElements();
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_DFID,this.getOid());
		    String conf = ((SevereElement)itr.next()).getName();
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_SEV_ELEM,conf);
		    
		    // insert the records and get back the number inserted
		    counterSE += counterSE + tcon.insert(ArchiveDatabase.TABLE_DATA_FILE_SEV_ELEM, values);
		}
	
		// quirks
		for (Iterator itr = this.getQuirks().iterator(); itr.hasNext();){
		    values.removeAllElements();
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_DFID,this.getOid());
		    String qk = ((SevereElement)itr.next()).getName();
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_SEV_ELEM, qk);
		    
		    // insert the records and get back the number inserted
		    counterSE += counterSE + tcon.insert(ArchiveDatabase.TABLE_DATA_FILE_SEV_ELEM, values);
		}

		// limitations
		for (Iterator itr = this.getLimitations().iterator(); itr.hasNext();){
		    values.removeAllElements();
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_DFID,
		            this.getOid());
		    String limitation = ((SevereElement)itr.next()).getName();
		    values.addElement(ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_SEV_ELEM, limitation);
		    
		    // insert the records and get back the number inserted
		    counterSE += counterSE + tcon.insert(ArchiveDatabase.TABLE_DATA_FILE_SEV_ELEM, values);
		}

		// message digests
		for (Iterator itr = this.getMesgDigests().iterator(); itr.hasNext();) {
			ArchiveMessageDigest amdgst = (ArchiveMessageDigest) itr.next();
			values.removeAllElements();
			values.addElement(ArchiveDatabase.COL_MESSAGE_DIGEST_CODE, amdgst.getCode());
			values.addElement(ArchiveDatabase.COL_MESSAGE_DIGEST_DFID, this.getOid());
			values.addElement(ArchiveDatabase.COL_MESSAGE_DIGEST_ORIGIN, amdgst.getOrigin());
			values.addElement(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE, amdgst.getValue());
			// insert the records and get back the number of rows inserted
			counterMD += counterMD + tcon.insert( ArchiveDatabase.TABLE_MESSAGE_DIGEST, values);
		}
		
		// Tell each bitstream to dbInsert, remembering the number of affected rows
		if (bitstreams != null && bitstreams.size() > 0) {
			for (Iterator it = bitstreams.iterator(); it.hasNext();) {
				Bitstream bitStrm = (Bitstream) it.next();
				counterBS += counterBS + bitStrm.dbInsert();
			}
		}

		counterAll = counterDF + counterDI + counterFA +  counterMD + counterSE + counterBS;
		return counterAll;
	}
	
	/**
	 * Used to add database statements to an existing open 
	 * database transaction.
	 * All subclasses of DataFile must override this method
	 * (enforced by <code>DataFileValidator</code>).
	 * This method should be called AFTER extractMetadata,
	 * otherwise no guarantees..
	 * 
	 * @param archiveAction
	 *            general function DAITSS is performing
	 * @return number of records affected
	 * @throws FatalException
	 */
	public int dbUpdate(byte archiveAction) throws FatalException {
		String methodName = "dbUpdate(byte)";
		int counterAll = 0; // total number of rows affected

		//	do a different database update depending on the archive action
		switch (archiveAction) {
			case ArchiveManager.ACTION_INGEST:
				// decide what kind of database operation is needed.
				if (hasArchived) { // if this data file has already been archived (reingest)
					if (needRefresh) { // if this data file need database refresh (format has been changed)
						dbDelete(); // delete its records and all of its existing children.
						dbInsert(); // insert its records and its decendents.
					} else
						counterAll = dbUpdate(); // just update the metadata for new updated metadata.
				} else // this is an unarchived data file, insert its record and all of its children.
					counterAll = dbInsert();
	
				break;

			case ArchiveManager.ACTION_MIGRATE: // fall through
			case ArchiveManager.ACTION_NORMALIZE: // fall through
			case ArchiveManager.ACTION_DISSEMINATE:
				Informer.getInstance().fail(this, methodName,
						"Invalid argument",
						"archiveAction: " + archiveAction,
						new FatalException("Unimplemented archiveAction"));
				break;
			default:
				// unknown archive action - fatal error
				Informer.getInstance().fail(this, methodName, "Invalid argument",
						"archiveAction: " + archiveAction,
						new FatalException("Not a valid archive action"));
		}
			
		// return the total number of records affected
		return counterAll;
	}       

	/**
	 * Read from the database to reset the members of the data file object
	 * @throws FatalException
	 */
	public void readFromDB() throws FatalException{
		DBConnection conn;
		Timestamp ts;
		try {
			conn = DBConnection.getConnection();
		} catch (FatalException e) {
			throw new RuntimeException(e);
		}
		
		String sql = String.format(
				"select * from %s where %s = %s and %s = %s",
				ArchiveDatabase.TABLE_DATA_FILE,
				ArchiveDatabase.COL_DATA_FILE_IEID,
				SqlQuote.escapeString(ieid),
				ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH,
				SqlQuote.escapeString(this.getPackagePath())
				);
		try {
			System.out.println(sql);
			ResultSet results = conn.executeQuery(sql);
			if (!results.first()) {
				throw new RuntimeException(this.getPackagePath() + " does not exist in database");
			}
			// restore the data file members from its database record
			setOid( results.getString(ArchiveDatabase.COL_DATA_FILE_DFID) );
			ts = results.getTimestamp(ArchiveDatabase.COL_DATA_FILE_CREATE_DATE);
			if (ts != null)
				setCreateDate(DateTimeUtil.Timestamp2Arch(ts));
			ts = results.getTimestamp(ArchiveDatabase.COL_DATA_FILE_FILE_COPY_DATE);
			if (ts != null)
				setCopyDate(DateTimeUtil.Timestamp2Arch(ts));
		    setDipVersion(results.getString(ArchiveDatabase.COL_DATA_FILE_DIP_VERSION));
			setOrigin(results.getString(ArchiveDatabase.COL_DATA_FILE_ORIGIN) );
			String originUrl = results.getString(ArchiveDatabase.COL_DATA_FILE_ORIG_URI);
			if (originUrl != null)
				setOriginalUri(originUrl);
			setFormatCode(results.getString(ArchiveDatabase.COL_DATA_FILE_FORMAT));
			setIsGlobal(results.getString(ArchiveDatabase.COL_DATA_FILE_IS_GLOBAL) );
			setIsObsolete(results.getString(ArchiveDatabase.COL_DATA_FILE_IS_OBSOLETE) );
			setRole(results.getString(ArchiveDatabase.COL_DATA_FILE_ROLE));
			setPresLevel(results.getString(ArchiveDatabase.COL_DATA_FILE_PRES_LEVEL));
			results.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
		// perform the post processing for the database persistence.
		// check the format code to see if the data file object need to be refreshed.
		checkFormatCode();
	}
	
	/**
	 * Determine if the format for this data file has been changed.  This could happen 
	 * if new format are being supported since the last ingest (generic->format1) or 
	 * there is an improvement on parsers(format1->format2)
	 * @return
	 */
	private void checkFormatCode() {
		String mimeType = Format.getMimeTypeInFormat(this.getFormatCode());
		// See if there is a mismatch between the media type stored in the database and
		// the media type identified with the current parsers.  
		if (mimeType != this.mediaType)  
			needRefresh = true;
		else
			needRefresh = false;
	}
	
	public boolean needRefresh() {return needRefresh; }
	/**
	 * Deletes the actual file on disk represented by this DataFile object.
	 * Returns the result of deleting the file: <code>true</code> if the file
	 * was deleted, else <code>false</code>. The DataFile's <code>file</code>
	 * member must be set before calling this method.
	 * 
	 * @return <code>true</code> if this file was successfully deleted, else
	 *         <code>false</code>
	 * @throws FatalException
	 * @throws PackageException 
	 */
	public final boolean delete() throws FatalException, PackageException {
		String methodName = "delete()";
		
		boolean result = false;
		
		File f = new File(this.getPath());

		// check that the file exists
		if (f == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Can not delete file",
				"file path: " + this.getPath(),
				new FatalException("File to delete is null"));
		} else {
			try {
                //result = f.delete();
                result = FileUtil.delete(this.getPath());
            } catch (IOException e) {
            	Informer.getInstance().error(
                		this,
                		methodName,
                		"Unable to delete data file",
                		"path: " + this.getPath(),
                		new IOException("Unable to delete file: " + this.getPath()));										
            }
		}
		
		f = null;

		return result;
	}
	
	/**
	 * Do any preparation to the physical file for storage.
	 * This is meant to be overwritten by subclasses of DataFile
	 * for which compression, etc. is performed before storage.
	 */
	protected void doStoragePrep() {
	    // the default sorage prep is to do nothing
	    
	    // storagePrep Vector will stay empty
	    
	    // storage size is same as size when ingested
	    this.setStorageSize(this.getSize());
	}

	/**
	 * Evaluates this DataFile's members and sets other members 
	 * depending on the results of the evaluation.
	 * All subclasses of DataFile must override this method
	 * (enforced by <code>DataFileValidator</code>).
	 *  
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		
		// see if its a root of a distributed object
		if (!(this instanceof Distributed) ||
				this.getLinks().isEmpty()) {
			this.setIsRoot(DataFile.FALSE);
		} else {
			this.setIsRoot(DataFile.TRUE);
		}
		
		// assign a format code to this file
		this.calcFormatCode();		
	}

	/**
	 * Extracts the technical metadata from this file and populates this DataFile's
	 * members with this metadata. Not applicable to generic DataFiles - just
	 * subclasses of DataFile.
	 * 
	 * @throws PackageException
	 * @throws FatalException
	 */
	public void extractMetadata() throws PackageException, FatalException {
		String methodName = "extractMetadata()";
		
		// see if we should check the file's format before
		// parsing it. this slows down the parse when the isType
		// check is performed but is provided here for situations
		// where you can't control whether or not isType is
		// called before extractMetadata
		if (ArchiveProperties.getInstance().
				getArchProperty("ENFORCE_FILE_TYPE_CHECK").equals("true")){
			//System.out.println("Enforcing type check");
			// enforce the file format type check
			if (!this.checkIsType(this.getPath(), null)){
				Informer.getInstance().fail(this,
					methodName,
					"Illegal parse call",
					"file: " + this.getPath(),
					new FatalException("Check format type before calling parse."));
			}
		}
		
		this.parseInit();
		this.parse();
		this.parseEnd();
		this.evalMembers();
        this.compareMetadata();
        this.assignRole();
		
		if (this instanceof Distributed) {
			this.retrieveLinks();
		}
	}
	
	/**
	 * Extracts metadata but doesn't actually retrieve links.
	 * Used for testing purposes only.
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void extractMetadataNoRetrieve() 
		throws PackageException, FatalException {
		this.parseInit();
		this.parse();
		this.parseEnd();
		this.evalMembers();
		this.compareMetadata();
        this.assignRole();
	}

	/**
	 * Returns the file's anomalies as a Vector of SevereElement objects
	 * 
	 * @return the file's anomalies
	 */
	public Vector getAnomalies() {
		return this.anomalies;
	}

	/**
	 * @return Returns the anomsPossible.
	 */
	public Anomalies getAnomsPossible() {
		return anomsPossible;
	}

	/**
	 * @return the Bitstreams found in this file
	 */
	public Vector getBitstreams() {
		return this.bitstreams;
	}

	/**
	 * Returns the byte order of this file. 
	 * 
	 * @return	the byte order of this file
	 */
	public String getByteOrder() {
		return this.byteOrder;
	}
	
    /**
     * @return Returns the canDelete.
     */
    public String getCanDelete() {
        return canDelete;
    }

	/**
	 * Returns the date and time the last copy of the file was created. On ingest this is 
	 * the time that this file was written to permanent storage.
	 * 
	 * @return the date and time the last copy of this file was created
	 */
	public String getCopyDate() {
		return this.copyDate;
	}

	/**
	 * Returns the date and time that the digital file was created. For files not created by 
	 * DAITSS for which the creation date/time is unknown, this should be some time 
	 * near but before the copyDate member.
	 * 
	 * @return the date and time that the digital file was created
	 */
	public String getCreateDate() {
		return this.createDate;
	}

	/**
	 * Returns the name of the software program that created the data file
	 * (if known). If it is not known it returns null.
	 * Program version may or may not be part of this String.
	 * 
	 * @return	the name of the program that created this data file, or null.
	 */
	public String getCreatorProg() {
		return this.creatorProg;
	}

	/**
	 * Returns the DataFile that this normalized/migrated file was normalized/migrated 
	 * from. Will return null if this file was not derived from another file.
	 * 
	 * @return the DataFile that this file was derived from
	 */
	public DataFile getDerivedFrom() {
		return this.derivedFrom;
	}

	/**
	 * Returns the DataFile object that is associated with the identifier
	 * (link alias) that this file uses to refer to the other file (DataFile object).
	 * If the link alias is not in the list of known identifiers found in this
	 * file, null will be returned. The callee must check that a non-null
	 * DataFile was returned.
	 * 
	 * @param alias	a URI
	 * @return	a DataFile if the URI is associated with a DataFile, or null
	 * @throws FatalException
	 */
	public DataFile getDfFromLinkAlias(String alias)
		throws FatalException {
		String methodName = "getDfFromLinkAliasTable(String)";
		
		if (alias == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Null argument",
				"arg: alias",
				new FatalException("arg alias can not be null"));
		}
		DataFile df = null;
		if (this.linkAliasTable.containsKey(alias)) {
			df = (DataFile) this.linkAliasTable.get(alias);
		}
		return df;
	}

	/**
	 * Returns the DFID of the most current migrated version of this digital object. 
	 * If the digital object has never been migrated this is the same value as the 
	 * <code>oid</code> member.
	 * 
	 * @return the DFID of the most current migrated version of this digital object.
	 */
	public String getDipVersion() {
		return this.dipVersion;
	}

	/**
	 * Returns the file extension that this file has in long-term storage excluding the
	 * stopgap (ex: pdf).
	 * 
	 * @return 	the file extension of this file in long-term storage
	 */
	public String getFileExt() {
		return this.fileExt;
	}

	/**
	 * Returns the name of the file (file title).
	 * 
	 * @return 	the file title
	 */
	public String getFileTitle() {
		return this.fileTitle;
	}
	
	/**
	 * @return  the file's format-specific attributes.
	 */
	public Vector getFormatAttributes() {
		return this.formatAttributes;
	}
	
    /**
     * @return the code for this file's format.
     */
    public String getFormatCode() {
        return this.formatCode;
    }
    
    /**
     * @return the format variation
     */
    public String getFormatVariation() {
        return this.formatVariation;
    }

	/**
	 * Returns the Intellectual Entity ID (IEID) associated with this file.
	 * 
	 * @return the Intellectual Entity ID (IEID) associated with this DataFile
	 */
	public String getIeid() {
		return this.ieid;
	}

	/**
	 * Returns a list of coded values indicating any known features of the archival
	 * object intended to inhibit access, copying or migration, such as
	 * encryption, watermarking or content protection.
	 * 
	 * @return a list of the inhibitors in this file
	 */
	public Vector getInhibitors() {
		return this.inhibitors;
	}

	/**
	 * @return the Information Package that this file is part of
	 */
	public InformationPackage getIp() {
		return this.ip;
	}

	/**
	 * @return the mappings this file has between it's link aliases
	 * 	and the associated DataFiles
	 */
	public Hashtable getLinkAliasTable() {
		return this.linkAliasTable;
	}

	/**
	 * Returns the Link objects representing the references this file makes to
	 * other files. Only applicable to subclasses of DataFile that implement
	 * the edu.fcla.daitss.file.Distributed interface. 
	 * 
	 * @return the links that this DataFile references
	 */
	public Vector getLinks() {
		return this.links;
	}
	
    /**
     * @return Returns the localizedFilePath.
     */
    public String getLocalizedFilePath() {
        return localizedFilePath;
    }
	
    /**
     * @return Returns the locFromDf.
     */
    public DataFile getLocFromDf() {
        return locFromDf;
    }
    
    /**
     * @return Returns the locToDf.
     */
    public DataFile getLocToDf() {
        return locToDf;
    }

	/**
	 * Returns the MIME media type and subtype of this file.
	 * 
	 * @return the MIME media type and subtype of this file
	 */
	public String getMediaType() {
		return this.mediaType;
	}

	/**
	 * Returns the version of the MIME media subtype if applicable.
	 * 
	 * @return MIME media subtype version or an empty String.
	 */
	public String getMediaTypeVersion() {
		return this.mediaTypeVersion;
	}

	/**
	 * Returns the digital mesgDigests calculated on this file.
	 * 
	 * @return the digital mesgDigests calculated on this file
	 */
	public Vector getMesgDigests() {
		return this.mesgDigests;
	}

	/**
	 * Returns the message digest value given the code name
	 * for a message digest algorithm.  
	 * 
	 * @param _code	a code for the message digest type
	 * @return a message digest value if one is present for this code,
	 * 	or null if none exists for this code.
	 * @throws FatalException
	 */
	public String getMesgDigestValue(String _code) throws FatalException {
		
		String methodName = "getMesgDigestValue(String)";
		
		if (!ArchiveMessageDigest.isValidMdCode(_code)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_code: " + _code,
				new FatalException("Not a valid message digest code"));
		}

		String value = null;
		Vector mds = this.getMesgDigests();
		boolean foundIt = false;

		if (mds != null) {
			Iterator iter = mds.iterator();
			while (!foundIt && iter.hasNext()) {
				ArchiveMessageDigest md = (ArchiveMessageDigest) iter.next();
				if (md.getCode().equals(_code)) {
					foundIt = true;
					value = md.getValue();
				}
			}
		}

		return value;
	}

	/**
	 * Returns the conflicts between the internal and external metadata
	 * about this file.
	 * 
	 * @return the conflicts between the internal and external metadata
	 * about this file.
	 */
	public Vector getMetadataConflicts() {
		return this.metadataConflicts;
	}

	public MetadataConflicts getMetadataConflictsPossible() {
		return metadataConfPossible;
	}
	/**
	 * Gets a new DFID from the OIDServer and sets its DFID to this.
	 * 
	 * @throws FatalException
	 */
	public void getNewDfid() throws FatalException {
		this.setOid(OIDServer.getNewDfid());
	}

	/**
	 * Returns the origin of this file.
	 * 
	 * @return the origin of this file
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * @return the URI that this file was identified by if the file's
	 * 	origin is <code>ORIG_INTERNET</code>.
	 */
	public String getOriginalUri() {
		return this.originalUri;
	}
	
    /**
     * @return Returns the packagePath.
     */
    public String getPackagePath() {
        return packagePath;
    }

	/**
	 * Returns the absolute path to this file 
	 * (ex: &quot;/archive/UFE0000001/images/1.jpg&quot;).
	 * 
	 * @return the absolute path to this file
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Returns the preservation level associated with this file.
	 * 
	 * @return the preservation level
	 */
	public String getPresLevel() {
		return this.presLevel;
	}

	/**
	 * Returns a list of descriptions of any loss in functionality or change in the look
	 * and feel of the archival object resulting from the preservation
	 * processes and procedures implemented by the archive.
	 * 
	 * @return a list of the quirks about this file
	 */
	public Vector getQuirks() {
		return this.quirks;
	}

	/**
	 * Returns a list of archival limitations during processing this data file 
	 * 
	 * @return a list of the limitations about this file
	 */
	public Vector getLimitations() {
		return this.limitations;
	}
	
	/**
	 * @return Returns the anomsPossible.
	 */
	public Limitations getLimitationsPossible() {
		return limitationsPossible;
	}
	
	/**
	 * Returns the primary role of this file in the package or related to other files in 
	 * the package.
	 * 
	 * @return the primary role of this file
	 */
	public String getRole() {
		return this.role;
	}

	/**
	 * Returns the size of this file in bytes.
	 * 
	 * @return the size of this file in bytes
	 */
	public long getSize() {
		return this.size;
	}
	
    /**
     * @return Returns the first phase of localization 
     * call state.
     */
    public int getStateLoc1() {
        return stateLoc1;
    }
    
    /**
     * @return Returns the second phase of localization 
     * call state.
     */
    public int getStateLoc2() {
        return stateLoc2;
    }
    
    /**
     * @return Returns the migration call state.
     */
    public int getStateMig() {
        return stateMig;
    }
    
    /**
     * @return Returns the normalization call state.
     */
    public int getStateNorm() {
        return stateNorm;
    }
	
    /**
     * @return Returns the statePrepStor.
     */
    public int getStatePrepStor() {
        return statePrepStor;
    }
	
	/**
	 * @return Returns the storageSize.
	 */
	public long getStorageSize() {
		return this.storageSize;
	}

	/**
	 * @return the number of anomalies (not necessarily unique types
	 * of anomalies) found while parsing this file
	 */
	public int getTotalNumAnoms() {
		return totalNumAnoms;
	}
	
	/**
	 * Returns a particular set of metadata about this file.
	 * The types that can be asked for are stored as constants in the 
	 * <code>Descriptor</code> interface. If an incorrect type is
	 * asked for a <code>FatalException</code> will be thrown. If
	 * a valid type of metadata is asked for but it doesn't exist for
	 * the DataFile, null will be returned.
	 * 
	 * @param type
	 * @return METSDocument object
	 * @throws FatalException
	 */
	public METSDocument getXmlMetadata(int type)
		throws FatalException {
	    String methodName = "getXmlMetadata(int)";
	    METSDocument metadata = null;
	    
	    // check that its a valid metadata type
	    if (!isValidXmlMetadataType(type)){
	        Informer.getInstance().fail(this,
                    methodName,
                    "Invalid argument",
                    "type: " + type,
                    new FatalException("Not a valid Xml metadata type"));
	    }
	    
	    metadata = this.xmlMetadata[type];
	    
	    return metadata;
	}
	
	/**
	 * Determines whether or not a file has a particular
	 * file format attribute stored in its list of file
	 * attributes.
	 * 
	 * @param attribute format attribute
	 * @return whether or not the attribute is stored
	 * 	in its list of attributes about itself
	 */
	public boolean hasFormatAttribute(String attribute) {
	    boolean hasIt = true;
	    
	    Vector atts = this.getFormatAttributes();
	    if (atts == null || atts.isEmpty() ||
	            !atts.contains(attribute)) {
	        hasIt = false;
	    }
	    
	    return hasIt;
	}
	
	/**
	 * 
	 * @return whether or not this file is a metadata descriptor file
	 */
	public boolean isDescriptor() {
		boolean isDesc = false;
		
		if (getRole().equals(FileRole.ROLE_DESCRIPTOR_AIP) ||
			getRole().equals(FileRole.ROLE_DESCRIPTOR_DIP) ||
			getRole().equals(FileRole.ROLE_DESCRIPTOR_SIP) ||
			getRole().equals(FileRole.ROLE_DESCRIPTOR_GFP)){
				isDesc = true;
		}
		
		return isDesc;
	}

	/**
	 * @return <code>true</code> if this file is a global file,
	 * 	<code>false</code> if it is not
	 */
	public boolean isGlobal() {
		return isGlobal;
	}

	/**
	 * Can be used by parsers to determine if they should continue 
	 * parsing a file. If a file was found to have too many anomalies 
	 * then this method will return <code>true</code>.
	 * 
	 * @return true if this file is hopeless (too many anomalies to record
	 * 	them all), false otherwise
	 */
	public boolean isHopelessFile() {
		return isHopelessFile;
	}

	/**
	 * @return <code>TRUE</code> if this file is obsolete,
	 * 	else <code>FALSE</code>
	 */
	public String isObsolete() {
		return this.isObsolete;
	}

	/**
	 * Returns <code>TRUE</code> if this DataFile is the root of a distributed
	 * object, <code>FALSE</code> if it is not, or <code>UNKNOWN</code>
	 * if it is not known yet.
	 * 
	 * @return whether or not this DataFile is the root of a distributed object
	 */
	public String isRoot() {
		return this.isRoot;
	}

	/**
	 * If applicable (file has links to external files and at least
	 * one link was resolved/retrieved), performs a localization phase.
	 * Phase 1 or 2 is performed, depending on the <code>locPhase</code>
	 * of the DataFile.
	 * 
	 * Phase 1 of localizing a set of files:
	 * make a copy of this file and in the copy write the links as 
	 * relative paths to other files in this package. These other
	 * files may contain non-relative paths to remote files outside
	 * the archive.
	 *
	 * Phase 2 of localizing a set of files:
	 * make a copy of this file and in the copy write the links as 
	 * relative paths to other "localized files". These other 
	 * localized files contain relative paths to files contained
	 * by the archive, at least for those links that were resolved
	 * or retrieved.
	 *
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void localize() throws FatalException, PackageException {
	    // we can throw away return value because it is only useful
	    // to DataFile subclasses that actually implement a 
	    // localize method.
	    	    
	    localizeSetup(); // sets localization call states
	}
	
	/**
	 * locate the localized files for this data file.  This need to be done for re-ingest in order for the 
	 * localized schema to use the correct link.
	 * @return
	 * @throws FatalException
	 */
	public boolean locateLocalizedCopy() throws FatalException{
		boolean found = false;
		
        File thisFile = new File(this.getPath());
        // construct the localized file path
        int index = this.getFileTitle().indexOf('.');
        
        String fileExt =this.getFileTitle().substring(index+1);
		String localizedFilename = FileUtil.stripExtFromTitle(this.getFileTitle()) 
			+ "_LOC." + fileExt;
      
		try {
			Vector files = FileUtil.findFile(localizedFilename, thisFile.getParent(), false);
			if (files != null && files.size() > 0) {
				// found at least one, use the first one found.  There should be only one any.
				String localizedPath = files.firstElement().toString();
				setLocalizedFilePath(localizedPath);
				found = true;
			}
		} catch (IOException e) {
			throw new FatalException(e.getMessage());
		}

		return found;
	}
	/**
	 * Determines whether a localization routine should be performed
	 * and stores the fact that a localization phase has already been called 
	 * for this file.
	 * 
	 * Localization should not continue for any file that has already 
	 * had both phases of localization called on it. This can happen 
	 * because ingest has to happen in multiple passes while acquiring 
	 * and creating auxiliary files. It is easiest to keep the 
	 * localization calls dumb and tell everything to localize and the 
	 * ones that already have can quietly quit localization.
	 * 
	 * @return whether we should continue with localization routines
	 */
	protected boolean localizeSetup(){
	    boolean continueLocalizing = true;
	    
	    if (this.getStateLoc2() != Procedure.NOT_CALLED ||
	    		this.getStateLoc1() == Procedure.FAILED ||
				!this.getPresLevel().equals(Agreements.PRES_LEV_FULL)){
	    	// its either already localized, succeeded, failed, or
	    	// its preservation level doesn't merit localization
	        continueLocalizing = false;
	    } else if (hasArchived) {
	    	// if this data file has already been archived (indicates a re-ingest)
	    	// no need to localize
	    	continueLocalizing = false;
	    } else {
	    	// second phase of localization hasn't been called yet
	    	
		    // see which phase of localization this is
		    switch (this.getStateLoc1()){
		    	case Procedure.SUCCEEDED:
		    	    // Starting Phase 2
		    	    this.setStateLoc2(Procedure.CALLED);
		    	case Procedure.NOT_CALLED:
		    	    // Starting Phase 1
		    	    this.setStateLoc1(Procedure.CALLED);
		    }
	    }
    
	    return continueLocalizing;
	}

	/**
	 * Creates a file or group of files that is the migrated version of
	 * this DataFile. This is used by the subclasses of DataFile and
	 * not the DataFile class itself.
	 * All subclasses of DataFile must override this method
	 * (enforced by <code>DataFileValidator</code>).
	 * 
	 * @throws PackageException
	 * @throws FatalException
	 */
	public void migrate() throws PackageException, FatalException {
	    // we can throw away return value because it is only useful
	    // to DataFile subclasses that actually implement a 
	    // migration method.
	    migrateSetup(); 
	}
	
	/**
	 * Determines whether a migration routine should be performed
	 * and stores the fact that migration has already been called 
	 * for this file.
	 * 
	 * Migration should not continue for any file that has already 
	 * had migration called on it. This can happen because ingest
	 * has to happen in multiple passes while acquiring and creating
	 * auxiliary files. It is easiest to keep the migration calls
	 * dumb and tell everything to migrate and the ones that already
	 * have can quietly quit migration.
	 * 
	 * Migration should also not happen for the AIP/GFP Descriptor. It
	 * was a design decision to not create obsolete AIP/GFP descriptors
	 * (i.e. they should have no need to be migrated right after
	 * they created.)
	 * 
	 * @return whether we should continue with migration routines
	 */
	protected boolean migrateSetup(){
	    boolean continueMigration = true;

	    if (this.getStateMig() == Procedure.CALLED ||
	    		!this.getPresLevel().equals(Agreements.PRES_LEV_FULL) ||
	            this.getRole().equals(FileRole.ROLE_DESCRIPTOR_AIP) ||
	            this.getRole().equals(FileRole.ROLE_DESCRIPTOR_GFP)){
	        continueMigration = false;
	    } else if (hasArchived) {
	    	// if this data file has already been archived (indicates a re-ingest)
	    	// determine whether we need to migrate this file 
	    	boolean migrated = hasMigrated();
	    	if (migrated)
	    		continueMigration = false;
	    }
	  
	    this.setStateMig(Procedure.CALLED);
	    
	    return continueMigration;
	}
	
	/**
	 * whether this data file has already been migrated.  This method should
	 * only be called for data file that has already been archived (hasArchive = true)
	 * @return
	 */
	public boolean hasMigrated() {
		boolean yes = false;
    	
		// exit early if this data file has not been archived (indicate the first ingest)
		if (!hasArchived)
			return false;
		
    	// determine whether we need to migrate this file by looking up the
    	// relationship table
		DBConnection conn;
		try {
			conn = DBConnection.getConnection();
		} catch (FatalException e) {
			throw new RuntimeException(e);
		}
		
		String sql = String.format(
			"select %s from %s where %s = %s and %s = %s",
			ArchiveDatabase.COL_RELATIONSHIP_DFID_2,
			ArchiveDatabase.TABLE_RELATIONSHIP,
			ArchiveDatabase.COL_RELATIONSHIP_DFID_1,
			SqlQuote.escapeString(this.getOid()),
			ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE,
			SqlQuote.escapeString(Relationship.REL_MIGRATED_TO)
			);
		try {
			ResultSet results = conn.executeQuery(sql);
			// if this data file has already been migrated
			if (results.first()) 
				yes = true;
			results.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
		}
		return yes;
	}
	/**
	 * Creates a file or group of files that is the normalized version of
	 * this DataFile. This is used by the subclasses of DataFile and
	 * not the DataFile class itself.
	 * All subclasses of DataFile must override this method
	 * (enforced by <code>DataFileValidator</code>).
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void normalize() throws PackageException, FatalException {
	    // we can throw away return value because it is only useful
	    // to DataFile subclasses that actually implement a 
	    // migration method.
	    normalizeSetup(); 
	}
	
	/**
	 * Determines whether a normalization routine should be performed
	 * and stores the fact that normalization has already been called 
	 * for this file.
	 * 
	 * Normalization should not continue for any file that has already 
	 * had normalization called on it. This can happen because ingest
	 * has to happen in multiple passes while acquiring and creating
	 * auxiliary files. It is easiest to keep the normalization calls
	 * dumb and tell everything to normalize and the ones that already
	 * have can quietly quit normalization.
	 * 
	 * Normalization should also not happen for the AIP/GFP Descriptor. It
	 * was a design decision to not create obsolete AIP/GFP descriptors
	 * (i.e. they should have no need to be normalized right after
	 * they created.)
	 * 
	 * @return whether we should continue with normalization routines
	 */
	protected boolean normalizeSetup(){
		
	    boolean continueNormalization = true;
	    
	    if (this.getStateNorm() == Procedure.CALLED ||
	    		!this.getPresLevel().equals(Agreements.PRES_LEV_FULL) ||
	            this.getRole().equals(FileRole.ROLE_DESCRIPTOR_AIP) ||
	            this.getRole().equals(FileRole.ROLE_DESCRIPTOR_GFP)){
	        continueNormalization = false;
	    } 
	    
	    this.setStateNorm(Procedure.CALLED);
	    
	    return continueNormalization;
	}
	
	/**
	 * Parses itself and sets format-specific members. For roots of distributed
	 * objects, also recognizes links to external files and creates Link
	 * objects and adds them to the links Vector using addLink(Link).
	 * This is used by the subclasses of DataFile and not the DataFile class itself.
	 * All subclasses of DataFile must override this method
	 * (enforced by <code>DataFileValidator</code>).
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		// do nothing - wouldn't know how to parse a generic file!
	}

	/**
	 * Performs common tasks after parsing a file like closing it.
	 * 
	 * @throws FatalException
	 */
	protected void parseEnd() throws FatalException {
		this.reader.close();
		this.reader = null;
		
		// record an anomaly if there is no bitstream that we can identify in this datafile.
		// however, only do it if this is not a generic data file object.
		if (!this.getClass().getName().equals(DataFile.class.getName())) {
			if (this.getBitstreams().size() <= 0) {
				SevereElement ja = this.getAnomsPossible().
					getSevereElement(Anomalies.FILE_NO_BITSTREAM);
				this.addAnomaly(ja);
			}
		}
	}

	/**
	 * Begins the parsing procedure by checking that the file is good, and opening the
	 * file in read-only mode.
	 * 
	 * @throws FatalException
	 */
	protected void parseInit() throws FatalException {
		String methodName = "parseInit()";
		
		//	check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(this.getPath())) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Can't parse file",
				"filePath: " + this.getPath(),
				new FatalException("Not an existing, readable absolute path to a file"));
		}
		reader = null;
		// start reading the file in read-only format
		File tempFile = new File(this.getPath());
		reader = new ByteReader(tempFile, "r");
		tempFile = null;
	}
		
	/**
	 * Remove a particular bitstream from the set of bitstreams contained
	 * by this file. Doesn't physically change the file, just the bitstreams
	 * member.
	 * 
	 * @param index the index of the bitstream to remove
	 * @return whether or not the bitstream was removed (if it
	 * 	didn't exist in the set it can't remove it)
	 * @throws FatalException
	 */
	protected Bitstream removeBitstream(int index) 
		throws FatalException {
		String methodName = "removeBitstream(int)";
		
		if (this.getBitstreams() == null || 
			this.getBitstreams().elementAt(index) == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"index: " + index + " bitstreams:  " + this.getBitstreams(),
				new FatalException("Invalid index or null bitstreams vector"));
		}
		return (Bitstream) this.bitstreams.remove(index);
	}
	
	/**
	 * Removes a format-specific attribute from a file's list
	 * of attributes about itself. If the list does not contain the
	 * attribute it silently ignores the request.
	 * 
	 * @param attribute format attribute
	 */
	public void removeFormatAttribute(String attribute) {
	    if (this.formatAttributes != null) {
	    	this.formatAttributes.remove(attribute);
	    }
	}

	/**
	 * Tries to associate a physical file with every link in this file that we 
	 * want to retrieve. The physical file might already be archived or it
	 * might have been submitted with this package, or it might have to
	 * be downloaded from the Internet.
	 * 
	 * The reason why this can throw a <code>PackageException</code>
	 * is because it calls <code>DataFileFactory.createDataFile</code>
	 * which virus checks a file and can throw a <code>PackageException</code>
	 * when the file has a virus.
	 * 
	 * Even though this is public it is only meant to be called by this class
	 * or subclasses of this class. Couldn't make it protected because
	 * the <code>Distributed</code> interface has this method signature,
	 * and methods in interfaces have to be public.
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void retrieveLinks() throws PackageException, FatalException {
		String methodName = "retrieveLinks()";
		
		Informer.getInstance().info(this,
                methodName,
                "Starting to retrieve links",
                "File with possible links: " + this.getPath(),
                false);
		
		if (this.getLinks() != null && this.getLinks().size() > 0) {
			Iterator linkIter = links.iterator();
			while (linkIter.hasNext()) {
				Link link = (Link) linkIter.next();
				
				// see if we should retrieve the link
				// the logic of whether we should or should not retrieve the link
				// is as follows: the configuration file (configPolicies) contains places for setting whether
				// a given type of link should be retrieved. the file parsers
				// read these settings and when they recognize links they set
				// whether or not each link should be retrieved.
				if (link.shouldRetrieve()) {
					boolean foundFile = false; // whether or not the link was resolved to a physical file
					DataFile newDf = null; // pointer to DataFile associated with the link
					String newDfPath = null; // absolute path of a just-downloaded file
					
					// see if the file associated with this link
					// has already been seen by the DataFileFactory.
					// the only case where this can return a non-null
					// DataFile is when the link is a URL that has already
					// been seen in this package.
					DataFile oldDf = link.getDataFile();
					
					if (oldDf == null) {
						// still needs to be resolved locally or retrieved from the Internet
						newDfPath = link.retrieve();
						if (newDfPath == null) {
						    // cases:
						    // 1. link had an absolute path (we don't even try to get it)
						    // 2. link had an unrecognized syntax (we don't even try to get it)
						    // 3. link had a relative path and we couldn't locate the associated file
						    // 4. link had a URL and we couln't locate the associated file
							link.setStatus(Link.STATUS_BROKEN);
							// foundFile is already set to false, will exit without any further actions
						} else {
						    // cases:
						    // 1. link had a relative path and we found the associated file
						    // 2. link had a URL and we just downloaded the associated file
						    
						    // now try to create a DataFile for it
						    // (we may already have done so if the link
						    // has a relative path; if it has a URL we would have
						    // gotten the data file back earlier if we had already created it)
							
							// see if we should send in a URI for the downloaded
							// file so that the DataFileFactory will know that
							// this was a downloaded file
							String linkURI = null;
							if (link.getLinkOrigin().equals(DataFile.ORIG_INTERNET)) {
								    linkURI = link.getTheUrl().toString();
								if (linkURI == null || linkURI.equals("")){
									Informer.getInstance().fail(
										this,
										methodName,
										"Invalid URI",
										"linkURI: " + linkURI,
										new FatalException("Not a valid URI"));
								}
							}
							
							newDf = 
								DataFileFactory.getInstance().createDataFile(
								        this, // file containing the link
								        newDfPath, // abs. local path to linked to file
								        this.isGlobal(), 
								        linkURI); // original URI of link if the link was a URL
							
							if (newDf == null) {
								// DataFileFactory couldn't create it
								link.setStatus(Link.STATUS_BROKEN);
								// foundFile is already set to false, will exit without any further actions
							} else {
							    
								// cases:
							    // 1. DataFileFactory just created it
							    // 2. This was an existing global file
							    // 3. DataFileFactory had created this new global file already
								foundFile = true;
								
								// if this is a newly downloaded file that we didn't
								// already have in the archive - record the download event
								if (link.getLinkType().equals(Link.TYPE_HTTP_URL) && 
								        link.getTheUrl() != null &&
								        !(newDf instanceof GlobalFile)) {
								    // was retrieved - record this event
								    String procedure = Procedure.PROC_DOWNLOADED_LINK;
								    Event retrieveEvent = new Event(newDf.getOid(),
								            Event.EVENT_DOWNLOADED_LINK,
								            DateTimeUtil.now(),
								            procedure, 
								            Event.OUTCOME_SUCCESS, 
								            "File downloaded as: " + newDfPath, 
								            this.getOid());
								    Events.getInstance().add(retrieveEvent);
								}
								
							}
						}
					} else { // oldDf != null
						// DataFileFactory has already seen this file and the link
					    // was a URL already seen in this package.
					    
						// we'll still have to record the relationships between this
						// file and the DataFile though
					    
						newDf = oldDf;
						newDfPath = newDf.getPath();
						foundFile = true;
					}
					
					if (foundFile) {
						link.setStatus(Link.STATUS_SUCCESSFUL);
						
						// this is the root of a distributed object
						this.setIsRoot(TRUE);
						
						// but sometimes it is
						if (link.getLinkType().equals(Link.TYPE_HTTP_URL) && link.getTheUrl() != null){
							// remind the DataFileFactory that this URL points to a local DataFile
							DataFileFactory.getInstance().addDfToUriNameLookup(link.getTheUrl().toString(), newDf);
						} else if (link.getLinkType().equals(Link.TYPE_REL_PATH)){
							// remind the DataFileFactory that this file on disk (absolute path) points 
							// to a local DataFile
							URI uri = new File(newDfPath).toURI();
							DataFileFactory.getInstance().addDfToUriNameLookup(uri.toString(), newDf);							
							//DataFileFactory.getInstance().addDfToUriNameLookup(newDfPath, newDf);
						}
						
						// record the fact that the link alias refers to a local DataFile in 
						// case this link alias appears more than once in this file;
						// this will also be used to localize file later
						this.addLinkAliasDfMapping(link.getLinkAlias(), newDf);
						
						// record the parent-child relationship between this file and
						// the linked to file
						Relationships.getInstance().add(new Relationship(newDf.getOid(),
							this.getOid(), Relationship.REL_CHILD));
										
					}					
				} else {
					// we don't retrieve this file
					link.setStatus(Link.STATUS_IGNORED);
				}
			}			
		}		
	}

	
	/**
	 * Stores a local copy of a localized, global DataFile in the directory
	 * specified by LOCAL_GLOBAL_DIR. Localized versions of global files are 
	 * stored locally for easy access and reduced processing across multiple
	 * references.
	 *  
	 * @throws FatalException
	 * @throws PackageException
	 */
	/*
	protected void saveLocalizedVersion() throws FatalException, PackageException {
	    
	    String methodName = "saveLocalizedVersion()";
	   			    	    
	    if (!this.isGlobal()) {
	        Informer.getInstance().fail(this,
                    methodName,
                    "Method called in incorrect context",
                    "Method only applicable for DataFile objects that are recognized golbal files",
                    new IllegalArgumentException());
	    }		    
	    
    	String globalLocDir = ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR");
    	String locCopy = globalLocDir + this.getIp().getIntEnt().getOid() + File.separator
    					+ this.getPackagePath();
    		
    	try {
			FileUtil.copyFile(this.getPath(), locCopy);
		} catch (IOException e1) {
			Informer.getInstance().error(this, 
					methodName, 
					"Unable to copy localized version of global file to local storage", 
					"fromPath: " + this.getPath() + " toPath: " + locCopy, 
					e1);
		}	    
	}
	*/
	
	/**
	 * Set the non-null list of bitstreams contained in this file
	 * 
	 * @param _bitstreams a list of bitstreams contained in this file
	 * @throws FatalException
	 */
	protected void setBitstreams(Vector _bitstreams) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setBitstreams(Vector _bitstreams)",
			"_bitstreams",
			_bitstreams,
			this.getClass().getName());
		this.bitstreams = _bitstreams;
	}

	/**
	 * Set the byte order of this file. Valid values:
	 * <code>BYTE_ORDER_LE</code>,
	 * <code>BYTE_ORDER_BE</code>, 
	 * <code>BYTE_ORDER_NA</code>, or
	 * <code>BYTE_ORDER_UNKNOWN</code>.
	 * 
	 * @param _byteOrder	the byte order of this file
	 * @throws FatalException
	 */
	public void setByteOrder(String _byteOrder) throws FatalException {
		String methodName = "setByteOrder(String)";
		
		if (_byteOrder == null
			|| (!_byteOrder.equals(BYTE_ORDER_BE)
				&& !_byteOrder.equals(BYTE_ORDER_LE)
				&& !_byteOrder.equals(BYTE_ORDER_NA)
				&& !_byteOrder.equals(BYTE_ORDER_UNKNOWN))) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_byteOrder: " + _byteOrder,
				new FatalException("Not a valid byte order"));
		}
		this.byteOrder = _byteOrder;
	}

	/**
	 * @param _canDelete <code>TRUE</code> if this file can be
	 * 	deleted, else <code>FALSE</code>
	 * 
	 * @throws FatalException
	 */
	public void setCanDelete(String _canDelete) throws FatalException {
		String methodName = "setCanDelete(String)";
		
		if (_canDelete == null
			|| (!_canDelete.equals(TRUE) && !_canDelete.equals(FALSE))) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_canDelete: " + _canDelete,
				new FatalException("Not a valid _canDelete value"));
		}
		this.canDelete = _canDelete;
	}

	/*
	 * Sets a code indicating the type of object and the archive function
	 * that created it or encountered it. 
	 * Note: We want this to fail if the argument is longer than can be supported
	 * by the database because this is vital information and can not be
	 * truncated!
	 * 
	 * probably can delete
	 * 
	 * @param _contentType	a code representing the type of file this is and the
	 * 										archive function that created it.
	 * @throws FatalException
	 Bug 41, removed
	public void setContentType(String _contentType) throws FatalException {
		String methodName = "setContentType(String)";
		
		if (_contentType == null || _contentType.length() > MAX_CTYPE_LENGTH) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_contentType: " + _contentType,
				new FatalException("Not a valid content type"));
		}
		// check that it is a valid content type in terms of characters used
		checkIsContentType(_contentType);
		this.contentType = _contentType;
	}*/

	/**
	 * Set the date and time the last copy of the file was created. On ingest this is the
	 * time that this file was written to permanent storage.
	 * 
	 * @param _copyDate	a timestamp representing the time this file was written to
	 * 									permanent storage
	 * @throws FatalException
	 */
	public void setCopyDate(String _copyDate) throws FatalException {
		String methodName = "setCopyDate(String)";
		
		if (_copyDate != null
			&& DateTimeUtil.isInArchiveDateFormat(_copyDate)
			&& DateTimeUtil.isLaterThan(_copyDate, MIN_COPY_DATE)
			&& DateTimeUtil.isLaterThan(MAX_COPY_DATE, _copyDate)) {
			this.copyDate = _copyDate;
		} else {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_copyDate: " + _copyDate,
				new FatalException("Not a valid copy date"));
		}
	}

	/**
	 * Set the date and time that the digital file was created. For files not created by DAITSS 
	 * for which the creation date/time is unknown, this should be some time near but before
	 * the copyDate member.
	 * 
	 * @param _createDate	a timestamp representing the time when this file was created
	 * 										if known, else the time of ingest
	 * @throws FatalException
	 */
	public void setCreateDate(String _createDate) throws FatalException {
		String methodName = "setCreateDate(String)";
		
		if (_createDate != null
			&& DateTimeUtil.isInArchiveDateFormat(_createDate)
			&& DateTimeUtil.isLaterThan(_createDate, MIN_CREATE_DATE)
			&& DateTimeUtil.isLaterThan(MAX_CREATE_DATE, _createDate)) {
			this.createDate = _createDate;
		} else {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_createDate: " + _createDate,
				new FatalException("Not a valid create date"));
		}
	}

	/**
	 * Sets the name of the software program used to create this file.
	 * 
	 * @param _program	the name of the program that was used to create this file
	 * @throws FatalException
	 */
	protected void setCreatorProg(String _program) throws FatalException {
		String methodName = "setCreatorProg(String)";
		
		if (_program == null){
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_program: " + _program,
				new FatalException("Not a valid creator program"));
		}
		if ( _program.length() > MAX_CPROG_LENGTH) {
			// the program name is longer than we can support
			// record this anomaly, log a warning and then truncate it
			//SevereElement da = 
			//	Anomalies.getSevereElement(Anomalies.FILE_OVERLMT_CREATOR_PROG);
			SevereElement da = 
				anomsPossible.getSevereElement(Anomalies.FILE_OVRLMT_CREATOR_PROG);
			this.addAnomaly(da);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Truncating creator program name (too long)",
				"file: " + this.getPath());
			_program = _program.substring(0, MAX_CPROG_LENGTH);
		} 
		this.creatorProg = _program;
	}

	/**
	 * Sets the DataFile that this normalized/migrated file was normalized/migrated from.
	 * Not applicable to original files.
	 * 
	 * @param _derivedFrom	the Datafile that this DataFile was derived from
	 * @throws FatalException
	 */
	public void setDerivedFrom(DataFile _derivedFrom) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setDerivedFrom(DataFile _derivedFrom)",
			"_derivedFrom",
			_derivedFrom,
			this.getClass().getName());
		this.derivedFrom = _derivedFrom;
	}             

	/**
	 * Sets the DFID of the "best, most useable" version of this digital object. If
	 * the digital object has never been migrated this is the same value as the 
	 * <code>DFID</code> member. Does not check that this is in a valid DFID
	 * format, but does check that it is not null.
	 * 
	 * @param _DIPVersion	the DFID of the latest version of this file
	 * @throws FatalException
	 */
	public void setDipVersion(String _DIPVersion) throws FatalException {
		String methodName = "setDipVersion(String)";
		
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setDIPVersion(String _DIPVersion)",
			"_DIPVersion",
			_DIPVersion,
			this.getClass().getName());

		this.dipVersion = _DIPVersion;
	}

	/**
	 * Sets the file extension of the file name as it is in long-term storage AFTER performing
	 * any storage preparation (excluding the  stopgap) ('.'),  ex: &quot;pdf&quot;. 
	 * If the file contains more than one stopgap, this
	 * is the portion after the last stopgap, for example &quot;gz&quot; for the file
	 * &quot;file.tar.gz&quot;.
	 * 
	 * @param _fileExt	the file extension of this file in long-term storage
	 * @throws FatalException
	 */
	public void setFileExt(String _fileExt) throws FatalException {
		String methodName = "setFileExt(String)";
		
		if (_fileExt != null && _fileExt.length() <= MAX_FILEEXT_LENGTH) {
			this.fileExt = _fileExt;
		} else {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_fileExt: " + _fileExt,
				new FatalException("Not a valid file extension"));
		}
	}

	/**
	 * Sets the name of the file (file title).
	 * 
	 * @param _fileTitle	the file title
	 * @throws FatalException
	 */
	public void setFileTitle(String _fileTitle) throws FatalException {
		String methodName = "setFileTitle(String)";
		
		if (_fileTitle == null || _fileTitle.equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_fileTitle: " + _fileTitle,
				new FatalException("Not a valid file title"));
		}
		if (_fileTitle.length() > MAX_FILETITLE_LENGTH) {
			// longer than we can support - record the anomaly, log a warning and truncate it
			//SevereElement da = 
			//	Anomalies.getSevereElement(Anomalies.FILE_OVERLMT_FILE_TITLE);
			SevereElement da = 
				anomsPossible.getSevereElement(Anomalies.FILE_OVRLMT_FILE_TITLE);
			this.addAnomaly(da);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Truncating file title (too long)",
				"file: " + this.getPath());
			_fileTitle = _fileTitle.substring(0, MAX_FILETITLE_LENGTH);
		} 
		this.fileTitle = _fileTitle;
	}
	
    /**
     * Sets this file's format code. 
     * ONLY used when it's necessary for a DataFile subclass
     * to override DataFile's implementation of 
     * calcFormatCode. 
     * 
     * @param formatCode The formatCode to set.
     */
    protected void setFormatCode(String formatCode) {
        this.formatCode = formatCode;
}
	
    /**
     * @param formatVariation this file's variation on its format.
     * @throws FatalException
     */
    public void setFormatVariation(String formatVariation) 
    	throws FatalException {
        String methodName = "setFormatVariation()";
        
        if (formatVariation == null ||
                formatVariation.length() > MAX_FORMATVARIATION_LENGTH) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Invalid argument",
                    "formatVariation: " + formatVariation,
                    new FatalException("Not a valid format variation"));
        }
        this.formatVariation = formatVariation;
    }

	/**
	 * Stores a flag that parsers can use to know when a file has
	 * too many anomalies and they can stop parsing it. Also
	 * logs this fact as a warning.
	 * 
	 * @param _isHopelessFile specifies that a file has too many 
	 * 	anomalies to continue parsing it
	 * @throws FatalException
	 */
	public void setHopelessFile(boolean _isHopelessFile) 
		throws FatalException {
		String methodName = "setHopelessFile(boolean)";
		
		Informer.getInstance().warning(this,
				methodName,
				"File contains too many anomalies.",
				"file: " + this.getPath() + " num. of anoms.: " +
					this.getTotalNumAnoms());
		this.isHopelessFile = _isHopelessFile;
	}

	/**
	 * Sets the Intellectual Entity ID (IEID) to which this file belongs if this
	 * is a file, or for this package if this is not a file.
	 *
	 * @param _ieid	the Intellectual Entity ID (IEID)
	 * @throws FatalException
	 */
	public void setIeid(String _ieid) throws FatalException {
		String methodName = "setIeid(String)";
		
		//	check that the argument is not null
		this.checkForNullObjectArg("setIeid(String _ieid)",
				 "_ieid", _ieid, this.getClass().getName());	
		
		if (!OIDServer.isValidIeid(_ieid)){
			Informer.getInstance().fail(this,
				methodName, "Illegal argument",
				"_ieid: " + _ieid, 
				new FatalException("Not a valid IEID."));
		}
		
		this.ieid = _ieid;
	}

	/**
	 * @param _ip	the Information Package that this file is part of
	 * @throws FatalException
	 */
	public void setIp(InformationPackage _ip) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setIp(InformationPackage)",
			"_ip",
			_ip,
			this.getClass().getName());
		this.ip = _ip;
	}

	/**
	 * whether this data file has already been archived.
	 * @param _hasArchived
	 */
	public void setHasArchived(boolean _hasArchived) {
		hasArchived = _hasArchived;
	}
	/**
	 * @param _isGlobal whether or not this file is a global file
	 */
	public void setIsGlobal(boolean _isGlobal){
		this.isGlobal = _isGlobal;
	}

	public void setIsGlobal(String _isGlobal){
		if (_isGlobal.equals(TRUE))
			this.isGlobal = true;
		else if (_isGlobal.equals(FALSE))
			this.isGlobal = false;
	}
	
	/**
	 * @param _isObsolete <code>TRUE</code> if this file is obsolete,
	 * 	else <code>FALSE</code>
	 * @throws FatalException
	 */
	public void setIsObsolete(String _isObsolete) throws FatalException {
		String methodName = "setIsObsolete(String)";
		
		if (_isObsolete == null
			|| (!_isObsolete.equals(TRUE) && !_isObsolete.equals(FALSE))) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_isObsolete: " + _isObsolete,
				new FatalException("Not a valid _isObsolete value"));
		}
		this.isObsolete = _isObsolete;
	}

	/**
	 * Sets whether or not this DataFile is the root of a distributed object
	 * 
	 * @param _isRoot	<code>TRUE</code> if this DataFile is the root of 
	 * 	a distributed object, <code>FALSE</code> if it is not
	 * @throws FatalException
	 */
	public void setIsRoot(String _isRoot) throws FatalException {
		String methodName = "setIsRoot(String)";
		
		if (_isRoot != null
			&& (_isRoot.equals(TRUE)
				|| _isRoot.equals(FALSE)
				|| _isRoot.equals(UNKNOWN))) {
			this.isRoot = _isRoot;
		} else {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_isRoot: " + _isRoot,
				new FatalException("Not a valid isRoot value"));
		}
	}
    
    /**
     * @param localizedFilePath The localizedFilePath to set.
     */
    public void setLocalizedFilePath(String localizedFilePath) {
        this.localizedFilePath = localizedFilePath;
    }
    
    /**
     * @param locFromDf The locFromDf to set.
     */
    public void setLocFromDf(DataFile locFromDf) {
        this.locFromDf = locFromDf;
    }
    
    /**
     * @param locToDf The locToDf to set.
     */
    public void setLocToDf(DataFile locToDf) {
        this.locToDf = locToDf;
    }

	/**
	 * Sets the MIME media type and subtype for this data file. 
	 * 
	 * @param _mediaType	the MIME media type and subtype for this file
	 * @throws FatalException
	 */
	public void setMediaType(String _mediaType) throws FatalException {
		String methodName = "setMediaType(String)";
		
		boolean probablyValid = true, provedValid = false;

		if (_mediaType == null || _mediaType.length() > MAX_MTYPE_LENGTH) {
			probablyValid = false;
		}

		if (probablyValid) {
			if (MimeMediaType.isValidType(_mediaType)) {
				provedValid = true;
			}
		}

		if (!provedValid) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_mediaType: " + _mediaType,
				new FatalException("Not a valid media type"));
		}
		this.mediaType = _mediaType;
	}

	/**
	 * Sets the version of the <code>mediaSubtype</code>. This would, for example,
	 * be &quot;6.0&quot; if this file was a TIFF 6.0 file. If there is no version, this 
	 * should be an empty String, not a null String.
	 * 
	 * @param _version	the version of this file's MIME media subtype
	 * @throws FatalException
	 */
	public void setMediaTypeVersion(String _version) throws FatalException {
		String methodName = "setMediaTypeVersion(String)";
		
		if (_version == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_version: " + _version,
				new FatalException("Not a valid version"));
		}	
		if (_version.length() > MAX_MTYPEVERS_LENGTH) {
			// the version is longer than we can support
			// record this anomaly, log a warning and then truncate it
			//SevereElement da = 
			//	Anomalies.getSevereElement(Anomalies.FILE_OVERLMT_MEDIA_TYPE_VERSION);
			SevereElement da = 
				anomsPossible.getSevereElement(Anomalies.FILE_OVRLMT_MEDIA_TYPE_VERSION);
			this.addAnomaly(da);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Truncating version (too long)",
				"file: " + this.getPath());
			_version = _version.substring(0, MAX_MTYPEVERS_LENGTH);			
		} 
		this.mediaTypeVersion = _version;
	}

	/**
	 * Sets the origin of this file. Valid values are <code>ORIG_ARCHIVE</code>,
	 * <code>ORIG_DEPOSITOR</code>, <code>ORIG_UNKNOWN</code> 
	 * and <code>ORIG_INTERNET</code>.
	 * 
	 * @param _origin	the origin of this file
	 * @throws FatalException
	 */
	public void setOrigin(String _origin) throws FatalException {
		String methodName = "setOrigin(String)";
		
		if (_origin == null
			|| (!_origin.equals(ORIG_ARCHIVE)
				&& !_origin.equals(ORIG_DEPOSITOR)
				&& !_origin.equals(ORIG_UNKNOWN)
				&& !_origin.equals(ORIG_INTERNET))) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_origin: " + _origin,
				new FatalException("Not a valid origin"));
		}
		this.origin = _origin;
	}

	/**
	 * @param _originalURI the URI that this file was identified by if the file's
	 * 	origin is <code>ORIG_INTERNET</code>.
	 * 
	 * @throws FatalException
	 */
	public void setOriginalUri(String _originalURI) 
		throws FatalException {
		String methodName = "setOriginalURI(String)";
			
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setOriginalURI(String _originalURI)",
			"_originalURI",
			_originalURI,
			this.getClass().getName());
		if (_originalURI.length() > MAX_ORIGINAL_URI_LENGTH) {
			// the URI is longer than we can support
			// log this anomaly and then truncate it
			//SevereElement da = 
			//	Anomalies.getSevereElement(Anomalies.FILE_OVERLMT_ORIG_URI);
			SevereElement da = 
				anomsPossible.getSevereElement(Anomalies.FILE_OVRLMT_ORIG_URI);
			this.addAnomaly(da);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Truncating original URI (too long)",
				"file: " + this.getPath());
			_originalURI = _originalURI.substring(0, MAX_ORIGINAL_URI_LENGTH);
		}
		originalUri = _originalURI;
	}
    
    /**
     * @param packagePath The packagePath to set.
     */
    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

	/**
	 * Sets the absolute path to this file
	 * 
	 * @param _path	an absolute path to an existing readable file
	 * @throws FatalException
	 */
	public void setPath(String _path) throws FatalException {
		String methodName = "setPath(String)";
		
		if (!FileUtil.isGoodFile(_path) && _path.length() <= MAX_PATH_LENGTH) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_path: " + _path,
				new FatalException("Not a valid path"));
		}
		this.path = _path;
	}

	/**
	 * Sets the level of preservation for this data file.
	 * 
	 * @param _presLevel	the preservation level for this file
	 * @throws FatalException
	 */
	public void setPresLevel(String _presLevel) throws FatalException {
		String methodName = "setPresLevel(String)";
		
		if (_presLevel == null || 
			!Agreements.isValidArchivePresLevel(_presLevel)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_presLevel: " + _presLevel,
				new FatalException("Not a valid preservation level"));
		}
		this.presLevel = _presLevel;
	}

	/**
	 * Sets the primary role of this file in the package or related to other files in 
	 * the package.
	 * 
	 * @param _role	the primary role of this file
	 * @throws FatalException
	 */
	public void setRole(String _role) throws FatalException {
		String methodName = "setRole(String)";
		
		// check that the role is non-null and valid
		if (!FileRole.isValidRole(_role)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"role: " + role,
				new FatalException("Not a valid role"));
		}
		this.role = _role;
	}

	/**
	 * Sets the size of this data file in bytes. Note that there is a minimum
	 * and maximum supported file size.
	 * 
	 * @param _size	the size of this file in bytes
	 * @throws FatalException
	 */
	public void setSize(long _size) throws FatalException {
		String methodName = "setSize(long)";
		
		if (_size < MIN_FSIZE) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"size: " + _size,
				new FatalException("Not a valid file byte size"));
		}
		if (_size > MAX_FSIZE) {
			// we can't support this large a file size - record the anomaly and
			// log a warning and reduce size to max recordable
			//SevereElement fa =
			//	Anomalies.getSevereElement(Anomalies.FILE_OVRLMT_SIZE);
			SevereElement fa =
				anomsPossible.getSevereElement(Anomalies.FILE_OVRLMT_SIZE);
			this.addAnomaly(fa);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing file size (too large to record)",
				"file: " + this.getPath());
			_size = MAX_FSIZE;
		}
		this.size = _size;
	}
    
    /**
     * @param stateLoc1 The stateLoc1 to set.
     */
    public void setStateLoc1(int stateLoc1) {
        this.stateLoc1 = stateLoc1;
    }
    /**
     * @param stateLoc2 The stateLoc2 to set.
     */
    public void setStateLoc2(int stateLoc2) {
        this.stateLoc2 = stateLoc2;
    }
    /**
     * @param stateMig The stateMig to set.
     */
    public void setStateMig(int stateMig) {
        this.stateMig = stateMig;
    }
    /**
     * @param stateNorm The stateNorm to set.
     */
    public void setStateNorm(int stateNorm) {
        this.stateNorm = stateNorm;
    }
    
    /**
     * @param statePrepStor The statePrepStor to set.
     */
    public void setStatePrepStor(int statePrepStor) {
        this.statePrepStor = statePrepStor;
    }
	
	/**
	 * @param storageSize The storageSize to set.
	 */
	public void setStorageSize(long storageSize) {
		this.storageSize = storageSize;
	}

	/**
	 * @param _totalNumAnoms number of anomalies found in this file
	 */
	public void setTotalNumAnoms(int _totalNumAnoms) {
		this.totalNumAnoms = _totalNumAnoms;
	}
	
	/**
	 * Constructs a String of all the DataFile's members.
	 * 
	 * @return the members of this class as a String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("");

		Iterator iter = null;
		Enumeration e = null;
		int count = 0;
		sb.append(this.getClass() + "\n");
		sb.append(this.getPath() + "\n");
		sb.append("Anomalies: ");
		if (this.anomalies != null && anomalies.size() > 0) {
			iter = anomalies.iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				SevereElement theAnom = (SevereElement) iter.next();
				sb.append(
					"\tAnomaly" + count + ": " + theAnom.getName() + "\n");
				count++;
			}
			//sb.append("\tAnomalies as a String: " + getAnomalyNames() + "\n");
		} else {
			sb.append(getAnomalies() + "\n");
		}
		int numBitstreams = 0;
		if (this.getBitstreams() != null) {
			numBitstreams = this.getBitstreams().size();
		}
		sb.append("Number of bitstreams: " + numBitstreams + "\n");
		if (numBitstreams > 0) {
			Iterator bsIter = this.getBitstreams().iterator();
			int curBitStream = 1;
			while (bsIter.hasNext()) {
				Bitstream bs = (Bitstream) bsIter.next();
				sb.append("Bitstream " + curBitStream + ":\n");
				sb.append(bs);
				curBitStream++;
			}
		}
		sb.append("ByteOrder: " + getByteOrder() + "\n");
		sb.append("Can delete: " + getCanDelete() + "\n");
		//sb.append("ContentType: " + getContentType() + "\n");
		sb.append("CopyDate: " + getCopyDate() + "\n");
		sb.append("CreateDate: " + getCreateDate() + "\n");
		sb.append("CreatorProg: " + getCreatorProg() + "\n");
		sb.append("DerivedFrom: " + getDerivedFrom() + "\n");
		sb.append("DFID: " + getOid() + "\n");
		sb.append("dipVersion: " + getDipVersion() + "\n");
		sb.append("FileExt: " + getFileExt() + "\n");
		sb.append("FileTitle: " + getFileTitle() + "\n");
		sb.append("Format attributes: ");
		if (this.getFormatAttributes() != null && 
		        this.getFormatAttributes().size() > 0) {
			iter = this.getFormatAttributes().iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				String theAtt = (String) iter.next();
				sb.append(
					"\tAttribute" + count + ": " + theAtt + "\n");
				count++;
			}
		} else {
			sb.append(getFormatAttributes() + "\n");
		}
		sb.append("Format code: " + getFormatCode() + "\n");
		sb.append("Format variation: " + getFormatVariation() + "\n");
		sb.append("IEID: " + getIeid() + "\n");
		sb.append("Information Package: ");
		if (this.getIp() != null) {
			sb.append("(not null)\n");
		} else {
			sb.append(this.getIp() + "\n");
		}
		sb.append("Inhibitors: ");
		if (this.inhibitors != null && inhibitors.size() > 0) {
			iter = inhibitors.iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				SevereElement theInhib = (SevereElement) iter.next();
				sb.append("\tInhibitor" + count + ": " + theInhib.getName() + "\n");
				count++;
			}
		} else {
			sb.append(getInhibitors() + "\n");
		}
		sb.append("IsGlobal: " + isGlobal() + "\n");
		sb.append("IsHopelessFile: " + isHopelessFile() + "\n");
		sb.append("IsObsolete: " + isObsolete() + "\n");
		sb.append("IsRoot: " + isRoot() + "\n");
		sb.append("LinkAliasTable: ");
		if (this.getLinkAliasTable() != null && !this.getLinkAliasTable().isEmpty()) {
			e = getLinkAliasTable().keys();
			count = 1;
			sb.append("\n");
			while (e.hasMoreElements()) {
				 String theLinkAlias = (String) e.nextElement();
				 DataFile theDf = (DataFile) this.getLinkAliasTable().get(theLinkAlias.toString());
				sb.append("\tLinkAlias" + count + ": " + theLinkAlias + 
					" DataFile's DFID: " + theDf.getOid() + "\n");
				count++;
			}			
		} else {
			sb.append(getLinkAliasTable() + "\n");
		}
		sb.append("Links: ");
		if (this.getLinks() != null && links.size() > 0) {
			iter = links.iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				Link theLink = (Link) iter.next();
				sb.append("\tLink" + count + ": " + theLink + "\n");
				count++;
			}
		} else {
			sb.append(getLinks() + "\n");
		}
		sb.append("Localized file path: " + this.getLocalizedFilePath() + "\n");
		sb.append("Localized from DFID: " + 
		   ((this.getLocFromDf() == null) ? "null":this.getLocFromDf().getOid()) + "\n");
		sb.append("Localized to DFID: " + 
		   ((this.getLocToDf() == null) ? "null":this.getLocToDf().getOid()) + "\n");
		sb.append("MediaType: " + getMediaType() + "\n");
		sb.append("MediaTypeVersion: " + getMediaTypeVersion() + "\n");
		sb.append("Message Digests: ");
		if (this.getMesgDigests() != null && this.getMesgDigests().size() > 0){
			iter = getMesgDigests().iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				ArchiveMessageDigest theDigest = (ArchiveMessageDigest) iter.next();
				sb.append("\tMessage Digest"+ count + ": " + theDigest + "\n");
				count++;
			}			
		} else {
			sb.append(this.getMesgDigests() + "\n");
		}
		sb.append("MetadataConflicts: ");
		if (this.metadataConflicts != null && metadataConflicts.size() > 0) {
			iter = metadataConflicts.iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				SevereElement theMConflict = (SevereElement) iter.next();
				sb.append("\tMetadata conflict" + count + ": " + theMConflict.getName() + "\n");
				count++;
			}
		} else {
			sb.append(getMetadataConflicts() + "\n");
		}
		sb.append("Origin: " + getOrigin() + "\n");
		sb.append("OriginalURI: " + getOriginalUri() + "\n");
		sb.append("AP: " + getAp() + "\n");
		sb.append("Package path: " + getPackagePath() + "\n");
		sb.append("Path: " + getPath() + "\n");
		sb.append("PreservationLevel: " + getPresLevel() + "\n");
		sb.append("Quirks: ");
		if (this.quirks != null && quirks.size() > 0) {
			iter = quirks.iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				SevereElement theQuirk = (SevereElement) iter.next();
				sb.append("\tQuirk" + count + ": " + theQuirk.getName() + "\n");
				count++;
			}
		} else {
			sb.append(getQuirks() + "\n");
		}
		if (this.limitations != null && limitations.size() > 0) {
			iter = limitations.iterator();
			count = 1;
			sb.append("\n");
			while (iter.hasNext()) {
				SevereElement theLimitation = (SevereElement) iter.next();
				sb.append("\tLimitations" + count + ": " + theLimitation.getName() + "\n");
				count++;
			}
		} else {
			sb.append(getLimitations() + "\n");
		}
		sb.append("Role of file: " + getRole() + "\n");
		sb.append("Size: " + getSize() + "\n");
		sb.append("State of localization 1 call: " + 
		        Procedure.getCallStateAsString(getStateLoc1()) + "\n");
		sb.append("State of localization 2 call: " + 
		        Procedure.getCallStateAsString(getStateLoc2()) + "\n");
		sb.append("State of migration call: " + 
		        Procedure.getCallStateAsString(getStateMig()) + "\n");
		sb.append("State of normalization call: " + 
		        Procedure.getCallStateAsString(getStateNorm()) + "\n");
		sb.append("State of storage prep call: " + 
		        Procedure.getCallStateAsString(getStatePrepStor()) + "\n");
		
		return sb.toString();
	}

	/**
	 * Output this class' metadata member to XML.
	 * 
	 * @return the metadata 
	 * @throws FatalException
	 */
	public Document toXML() throws FatalException {

        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        // Namespace. p.getArchProperty("NS_METS")
        String namespace = rootElement.getNamespaceURI();

        
        // DATA_FILE.
        Element dataFileElement = doc.createElementNS(namespace, "DATA_FILE");
        rootElement.appendChild(dataFileElement);

        /* DFID */
        Element dfidElement = doc.createElementNS(namespace, "DFID");
        String dfidValue = (this.getOid() != null ? this.getOid() : "");
        Text dfidText = doc.createTextNode(dfidValue);
        dfidElement.appendChild(dfidText);
        dataFileElement.appendChild(dfidElement);

        /* IEID. */
        Element ieidElement = doc.createElementNS(namespace, "IEID");
        String ieidValue = (this.getIeid() != null ? this.getIeid() : "");
        Text ieidText = doc.createTextNode(ieidValue);
        ieidElement.appendChild(ieidText);
        dataFileElement.appendChild(ieidElement);
        
        /* Create Date */
        Element createDateElement = doc.createElementNS(namespace, "CREATE_DATE");
        String createDateValue = (this.getCreateDate() != null ? this.getCreateDate() : "");
        Text createDateText = doc.createTextNode(createDateValue);
        createDateElement.appendChild(createDateText);
        dataFileElement.appendChild(createDateElement);

        /* File Copy Date */
        Element fileCopyDateElement = doc.createElementNS(namespace, "FILE_COPY_DATE");
        String fileCopyDateValue = (this.getCopyDate() != null ? this
                .getCopyDate() : "");
        Text fileCopyDateText = doc.createTextNode(fileCopyDateValue);
        fileCopyDateElement.appendChild(fileCopyDateText);
        dataFileElement.appendChild(fileCopyDateElement);
        
        /* Dip Version */
        Element dipVersionElement = doc.createElementNS(namespace, "DIP_VERSION");
        String dipVersionValue = (this.getDipVersion() != null && !this.getDipVersion().equals("") ? this.getDipVersion() : this.getOid());
        Text dipVersionText = doc.createTextNode(dipVersionValue);
        dipVersionElement.appendChild(dipVersionText);
        dataFileElement.appendChild(dipVersionElement);

        /* Origin */
        Element originElement = doc.createElementNS(namespace, "ORIGIN");
        String originValue = (this.getOrigin() != null ? this.getOrigin() : "");
        Text originText = doc.createTextNode(originValue);
        originElement.appendChild(originText);
        dataFileElement.appendChild(originElement);

        /* Original Uri */
        if (this.getOriginalUri() != null) {
            Element origUriElement = doc.createElementNS(namespace, "ORIG_URI");
            String origUriValue = this.getOriginalUri();
            Text origUriText = doc.createTextNode(origUriValue);
            origUriElement.appendChild(origUriText);
            dataFileElement.appendChild(origUriElement);
        }
              
        /* Package Path */
        Element packagePathElement = doc.createElementNS(namespace, "PACKAGE_PATH");
        String packagePathValue = (this.getPackagePath() != null ? this.getPackagePath( ): "");
        Text packagePathText = doc.createTextNode(packagePathValue);
        packagePathElement.appendChild(packagePathText);
        dataFileElement.appendChild(packagePathElement);
        
        /* File Title */
        Element fileTitleElement = doc.createElementNS(namespace, "FILE_TITLE");
        String fileTitleValue = (this.getFileTitle() != null ? this.getFileTitle() : "");
        Text fileTitleText = doc.createTextNode(fileTitleValue);
        fileTitleElement.appendChild(fileTitleText);
        dataFileElement.appendChild(fileTitleElement);

        /* File Extension */
        Element fileExtElement = doc.createElementNS(namespace, "FILE_EXT");
        String fileExtValue = (this.getFileExt() != null ? this.getFileExt()
                : "");
        Text fileExtText = doc.createTextNode(fileExtValue);
        fileExtElement.appendChild(fileExtText);
        dataFileElement.appendChild(fileExtElement);

        /* Format. */
        Element formatCodeElement = doc.createElementNS(namespace, "FORMAT");
        String formatCodeValue = (this.getFormatCode() != null ? this.getFormatCode() : "");
        Text formatCodeText = doc.createTextNode(formatCodeValue);
        formatCodeElement.appendChild(formatCodeText);
        dataFileElement.appendChild(formatCodeElement);
        
        /* Removed, bug 39
         * Content Type 
        Element contentTypeElement = doc.createElementNS(namespace, "CONTENT_TYPE");
        String contentTypeValue = (this.getContentType() != null ? this.getContentType() : "");
        Text contentTypeText = doc.createTextNode(contentTypeValue);
        contentTypeElement.appendChild(contentTypeText);
        dataFileElement.appendChild(contentTypeElement);
        */
        
        
        /* Creator Prog */
        Element creatorProgElement = doc.createElementNS(namespace, "CREATOR_PROG");
        String creatorProgValue = (this.getCreatorProg() != null ? this.getCreatorProg() : "");
        Text creatorProgText = doc.createTextNode(creatorProgValue);
        creatorProgElement.appendChild(creatorProgText);
        dataFileElement.appendChild(creatorProgElement);

        /* Size */
        Element sizeElement = doc.createElementNS(namespace, "SIZE");
        String sizeValue = Long.toString(this.getSize());
        Text sizeText = doc.createTextNode(sizeValue);
        sizeElement.appendChild(sizeText);
        dataFileElement.appendChild(sizeElement);

        /* Byte Order */
        Element byteOrderElement = doc.createElementNS(namespace, "BYTE_ORDER");
        String byteOrderValue = (this.getByteOrder() != null ? this.getByteOrder() : "");
        Text byteOrderText = doc.createTextNode(byteOrderValue);
        byteOrderElement.appendChild(byteOrderText);
        dataFileElement.appendChild(byteOrderElement);        
                
        /* Is Root */
        Element isRootElement = doc.createElementNS(namespace, "IS_ROOT");
        String isRootValue = (this.isRoot() != null ? this.isRoot() : "");
        Text isRootText = doc.createTextNode(isRootValue);
        isRootElement.appendChild(isRootText);
        dataFileElement.appendChild(isRootElement);

        /* Is Global */
        Element isGlobalElement = doc.createElementNS(namespace, "IS_GLOBAL");
        String isGlobalValue = Boolean.toString(this.isGlobal()).toUpperCase();
        Text isGlobalText = doc.createTextNode(isGlobalValue);
        isGlobalElement.appendChild(isGlobalText);
        dataFileElement.appendChild(isGlobalElement);

        /* Is Obsolete */
        Element isObsoleteElement = doc.createElementNS(namespace, "IS_OBSOLETE");
        String isObsoleteValue = (this.isObsolete() != null ? this.isObsolete().toUpperCase() : "");
        Text isObsoleteText = doc.createTextNode(isObsoleteValue);
        isObsoleteElement.appendChild(isObsoleteText);
        dataFileElement.appendChild(isObsoleteElement);

        /* Can Delete */
        Element canDeleteElement = doc.createElementNS(namespace, "CAN_DELETE");
        String canDeleteValue = (this.canDelete() != null ? this.canDelete().toUpperCase() : "");
        Text canDeleteText = doc.createTextNode(canDeleteValue);
        canDeleteElement.appendChild(canDeleteText);
        dataFileElement.appendChild(canDeleteElement);

        /* Role */
        Element roleElement = doc.createElementNS(namespace, "ROLE");
        String roleValue = (this.getRole() != null ? this.getRole() : "");
        Text roleText = doc.createTextNode(roleValue);
        roleElement.appendChild(roleText);
        dataFileElement.appendChild(roleElement);

        /* Preservation Level */
        Element presLevelElement = doc.createElementNS(namespace, "PRES_LEVEL");
        String presLevelValue = (this.getPresLevel() != null ? this.getPresLevel() : "");
        Text presLevelText = doc.createTextNode(presLevelValue);
        presLevelElement.appendChild(presLevelText);
        dataFileElement.appendChild(presLevelElement);

        /* End DataFile Element */
        
        /* Severe Elements: Anomalies, Inhibitors, Quirks, Metadata Conflicts, Limitations */
        
        /* Anomalies */
        for (Iterator iter = this.getAnomalies().iterator(); iter.hasNext();) {
            SevereElement anomaly = (SevereElement) iter.next();
                
            Element anomalyElement = doc.createElementNS(namespace, "SEVERE_ELEMENT");
            rootElement.appendChild(anomalyElement);
            
            /* CODE */
            Element code = (Element) anomalyElement.appendChild(doc.createElementNS(namespace, "CODE"));
            code.appendChild(doc.createTextNode(anomaly.getName()));
            
            /* TYPE */
            Element type = (Element) anomalyElement.appendChild(doc.createElementNS(namespace, "TYPE"));
            type.appendChild(doc.createTextNode("ANOMALY"));
            
            /* SEVERITY */
            Element severity = (Element) anomalyElement.appendChild(doc.createElementNS(namespace, "SEVERITY"));
            severity.appendChild(doc.createTextNode(anomaly.getSeverity()));
            
            /* DESCRIPTION */
            Element description = (Element) anomalyElement.appendChild(doc.createElementNS(namespace, "DESCRIPTION"));
            description.appendChild(doc.createTextNode(anomaly.getLongDescription()));
        }
        
        /* Inhibitiors */
        for (Iterator iter = this.getInhibitors().iterator(); iter.hasNext();) {
            SevereElement inhibitor = (SevereElement) iter.next();

            Element inhibElement = doc.createElementNS(namespace,
                    "SEVERE_ELEMENT");
            rootElement.appendChild(inhibElement);

            /* CODE */
            Element code = (Element) inhibElement.appendChild(doc.createElementNS(
                    namespace, "CODE"));
            code.appendChild(doc.createTextNode(inhibitor.getName()));

            /* TYPE */
            Element type = (Element) inhibElement.appendChild(doc.createElementNS(
                    namespace, "TYPE"));
            type.appendChild(doc.createTextNode("INHIBITOR"));

            /* SEVERITY */
            Element severity = (Element) inhibElement.appendChild(doc.createElementNS(
                    namespace, "SEVERITY"));
            severity.appendChild(doc.createTextNode(inhibitor.getSeverity()));

            /* DESCRIPTION */
            Element description = (Element) inhibElement.appendChild(doc.createElementNS(
                    namespace, "DESCRIPTION"));
            description.appendChild(doc.createTextNode(inhibitor.getLongDescription()));
        }
        
        /* Metadata Conflicts */
        for (Iterator iter = this.getMetadataConflicts().iterator(); iter.hasNext();) {
            SevereElement mdConflict = (SevereElement) iter.next();

            Element mdConflictElement = doc.createElementNS(namespace, "SEVERE_ELEMENT");
            rootElement.appendChild(mdConflictElement);

            /* CODE */
            Element code = (Element) mdConflictElement.appendChild(doc.createElementNS(namespace, "CODE"));
            code.appendChild(doc.createTextNode(mdConflict.getName()));

            /* TYPE */
            Element type = (Element) mdConflictElement.appendChild(doc.createElementNS(namespace, "TYPE"));
            type.appendChild(doc.createTextNode("METADATA_CONFLICT"));

            /* SEVERITY */
            Element severity = (Element) mdConflictElement.appendChild(doc.createElementNS(namespace, "SEVERITY"));
            severity.appendChild(doc.createTextNode(mdConflict.getSeverity()));

            /* DESCRIPTION */
            Element description = (Element) mdConflictElement.appendChild(doc.createElementNS(namespace, "DESCRIPTION"));
            description.appendChild(doc.createTextNode(mdConflict.getLongDescription()));
        }
        
	    /* Quirks */
        for (Iterator iter = this.getQuirks().iterator(); iter.hasNext();) {
            SevereElement quirk = (SevereElement) iter.next();

            Element quirkElement = doc.createElementNS(namespace,"SEVERE_ELEMENT");
            rootElement.appendChild(quirkElement);

            /* CODE */
            Element code = (Element) quirkElement.appendChild(doc.createElementNS(namespace, "CODE"));
            code.appendChild(doc.createTextNode(quirk.getName()));

            /* TYPE */
            Element type = (Element) quirkElement.appendChild(doc.createElementNS(namespace, "TYPE"));
            type.appendChild(doc.createTextNode("QUIRK"));

            /* SEVERITY */
            Element severity = (Element) quirkElement.appendChild(doc.createElementNS(namespace, "SEVERITY"));
            severity.appendChild(doc.createTextNode(quirk.getSeverity()));

            /* DESCRIPTION */
            Element description = (Element) quirkElement.appendChild(doc.createElementNS(namespace, "DESCRIPTION"));
            description.appendChild(doc.createTextNode(quirk.getLongDescription()));
        }

	    /* Limitations */
        for (Iterator iter = this.getLimitations().iterator(); iter.hasNext();) {
            SevereElement limitation = (SevereElement) iter.next();

            Element limitationElement = doc.createElementNS(namespace,"SEVERE_ELEMENT");
            rootElement.appendChild(limitationElement);

            /* CODE */
            Element code = (Element) limitationElement.appendChild(doc.createElementNS(namespace, "CODE"));
            code.appendChild(doc.createTextNode(limitation.getName()));

            /* TYPE */
            Element type = (Element) limitationElement.appendChild(doc.createElementNS(namespace, "TYPE"));
            type.appendChild(doc.createTextNode("LIMITATION"));

            /* SEVERITY */
            Element severity = (Element) limitationElement.appendChild(doc.createElementNS(namespace, "SEVERITY"));
            severity.appendChild(doc.createTextNode(limitation.getSeverity()));

            /* DESCRIPTION */
            Element description = (Element) limitationElement.appendChild(doc.createElementNS(namespace, "DESCRIPTION"));
            description.appendChild(doc.createTextNode(limitation.getLongDescription()));
        }

        /* End Severe Elements*/
        
      /* Format Attributes */
        for (Iterator iter = this.getFormatAttributes().iterator(); iter.hasNext();) {
            String att = (String) iter.next();

            /* Format Attributes */
            Element formatAttributesElement = doc.createElementNS(namespace, "DATA_FILE_FORMAT_ATTRIBUTE");
            rootElement.appendChild(formatAttributesElement);
            
            /* DFID */
            Element name = (Element) formatAttributesElement.appendChild(doc.createElementNS(namespace, "DFID"));
            name.appendChild(doc.createTextNode(this.getOid()));
                        
            /* FORAMT_ATTRIBUTE */
            Element format = (Element) formatAttributesElement.appendChild(doc.createElementNS(namespace, "FORMAT_ATTRIBUTE"));
            format.appendChild(doc.createTextNode(att));            
        }
      

        /* Message Digests */
        for (Iterator iter = this.getMesgDigests().iterator(); iter.hasNext();) {
            ArchiveMessageDigest amdgst = (ArchiveMessageDigest) iter.next();

            /* Message Digest */
            Element messageDigestElement = doc.createElementNS(namespace, "MESSAGE_DIGEST");
            rootElement.appendChild(messageDigestElement);

            /* Dfid */
            Element mdDfidElement = doc.createElementNS(namespace, "DFID");
            String mdDfidValue = (this.getOid() != null ? this.getOid() : "" );
            Text mdDfidText = doc.createTextNode(mdDfidValue);
            mdDfidElement.appendChild(mdDfidText);
            messageDigestElement.appendChild(mdDfidElement);
            
            /* Code */
            Element codeElement = doc.createElementNS(namespace, "CODE");
            String codeValue = (amdgst.getCode() != null ? amdgst.getCode() : "" );
            Text codeText = doc.createTextNode(codeValue);
            codeElement.appendChild(codeText);
            messageDigestElement.appendChild(codeElement);

            /* Value */
            Element valueElement = doc.createElementNS(namespace, "VALUE");
            String valueValue = (amdgst.getValue() != null ? amdgst.getValue() : "" );
            Text valueText = doc.createTextNode(valueValue);
            valueElement.appendChild(valueText);
            messageDigestElement.appendChild(valueElement);

            /* Origin */
            Element mdOriginElement = doc.createElementNS(namespace, "ORIGIN");
            String mdOriginValue = (amdgst.getOrigin() != null ? amdgst.getOrigin() : "" );
            Text mdOriginText = doc.createTextNode(mdOriginValue);
            mdOriginElement.appendChild(mdOriginText);
            messageDigestElement.appendChild(mdOriginElement);
        }
        
        
        /* Bitstreams */        
        for (Iterator iter = this.bitstreams.iterator(); iter.hasNext();) {
            Bitstream bitStrm = (Bitstream) iter.next();
            Document bitDoc = bitStrm.toXML();
            NodeList ns = XPaths.selectNodeList(bitDoc, XPaths.Daitss.ALL_CHILDREN);
            for (int i = 0; i < ns.getLength(); i++) {
                Element bsElement = (Element) doc.importNode(ns.item(i), true);
                rootElement.appendChild(bsElement);
            }
        }
                
        // Return the DOM Document.
        return doc;
    }
	
	/**
	 * Performs the final steps of file migration, normalization
	 * or localization. This includes creating DataFile objects
	 * from the newly-created files, recording relationships and
	 * associated events.
	 * 
	 * Note that if more than 1 file was created in the transformation,
	 * 1 must have internal links to the others. This 'parent file'
	 * is the one that should be passed in as the <code>absPath</code>.
	 * 
	 * When the transformation was unsuccessful the absPath may be null.
	 * 
	 * @param transformType transformation type
	 * @param newFilePath absolute path to the newly created file or
	 * 	the 'parent' file if more than 1 file was created.
	 * @param procedure transformation procedure
	 * @throws FatalException
	 * @throws PackageException
	 */
	protected void transformEnd(String transformType, String newFilePath, 
	        String procedure) throws PackageException, FatalException {
	    String methodName = "transformEnd(String, String, String)";	    
	    
		boolean keepTransforming = true;
		Event e = null;
		
		// check args (newFilePath can be null)
		if (transformType == null ||
		        procedure == null ){
			Informer.getInstance().fail(this,
					methodName,
					"Illegal argument",
					"transformType: " + transformType + 
					" procedure: " + procedure,
					new FatalException("Transform and procedure can't be null"));
		}
		
		// this method should never be called during the first phase
		// of localization, but lets make sure...
		if (transformType.equals(Procedure.TRANSFORM_LOC_1)){
		    return;
		}
		
        // let the DataFileFactory know that this new file was 
        // genererated by the archive
        DataFileFactory.getInstance().addFutureOriginArchivePath(newFilePath);        
        
		// determine the associated event and relationship types
		String eventType = null;
		String relationshipType = null;
		if (transformType.equals(Procedure.TRANSFORM_MIG)){
		    eventType = Event.EVENT_MIGRATED_TO;
		    relationshipType = Relationship.REL_MIGRATED_TO;
	    } else if (transformType.equals(Procedure.TRANSFORM_NORM)){
	        eventType = Event.EVENT_NORMALIZED_TO;
	        relationshipType = Relationship.REL_NORM_TO;
	    } else if (transformType.equals(Procedure.TRANSFORM_LOC_2)){
	        eventType = Event.EVENT_LOCALIZED_TO;
	        relationshipType = Relationship.REL_LOCALIZED_TO;
	    } 
		
		if (newFilePath == null || !FileUtil.isGoodFile(newFilePath)) {
			// add an unsuccessful event
		    // this Event object can be anonymous because we aren't
		    // adding a Relationship object.
		    Events.getInstance().add(new Event(this.getOid(),
		            eventType,
		            DateTimeUtil.now(),
		            procedure, 
		            Event.OUTCOME_FAIL, 
		            "No files created", 
		            null)); // null relId
		    
		    Informer.getInstance().warning(this,
                    methodName,
                    "Done " + transformType + " at " + DateTimeUtil.now(),
                    "File: " + this.getPath() + " (UNSUCCESSFUL EVENT)");
			
			keepTransforming = false; // (end)
		}
		
		DataFile newDf = null;
		
        // *************************************************************
        // This looks like a good place to "throw away" the results
        // of normalization (i.e. don't make DatFile objects if the  
        // transformation type is a normalization). 
        // *************************************************************
		if (keepTransforming && !transformType.equals(Procedure.TRANSFORM_NORM)) {
			// create the data file using a null origUri because
		    // this wasn't a downloaded file        
			
			newDf = 
			    DataFileFactory.getInstance(this.getIp()).createDataFile(
			        this, // parent this file was derived from
					newFilePath, // new file path
					this.isGlobal(), // child inherits parent's isGlobal
					null); // original URI of new file if downloaded (it wasn't)
			
			if (newDf == null || newDf.getMediaType().equals(MimeMediaType.MIME_APP_UNK)) {
			    // DataFileFactory could not create this file if df is null 
			    // or DataFileFactory created a DataFile without a known media type
				// (will this ever happen??)
			    
			    Informer.getInstance().fail(
			            this,
                        methodName,
                        "Created an " + MimeMediaType.MIME_APP_UNK + " DataFile",
                        "New file: " + newFilePath + " transformed from " + 
						this.getPath() + 
                        " with format " + this.getFormatCode(),
                        new FatalException("Transformation error"));
			} else { 
			    // DataFileFactory created a DataFile with a known 
			    // media type
			    
			    // add successful event
			    e = new Event(this.getOid(),
			            eventType,
			            DateTimeUtil.now(),
			            procedure, 
			            Event.OUTCOME_SUCCESS, 
			            "File derived from: " + this.getPath(), // note
			            newDf.getOid());
			    Events.getInstance().add(e);
			    
			    // add relationship
			    Relationships.getInstance().add(new Relationship(this.getOid(),
			            newDf.getOid(), relationshipType, e));
			    
			    // give the new file the same role, transformation states
			    // as this file
			    newDf.setCreateDate(DateTimeUtil.now());
			    newDf.setOrigin(DataFile.ORIG_ARCHIVE);
			    newDf.setRole(this.getRole());
			    newDf.setDerivedFrom(this);
			    newDf.setStateLoc1(this.getStateLoc1());
			    newDf.setStateLoc2(this.getStateLoc2());
			    newDf.setStateMig(this.getStateMig());
			    newDf.setStateNorm(this.getStateNorm());
		
			    if (transformType.equals(Procedure.TRANSFORM_LOC_2)){
				    // set a reference in the new file to the file
				    // it was localized from
				    newDf.setLocFromDf(this);
				    
				    // set a reference in this file to the file it was localized to
				    // don't know if anyone will use this one but it doesn't cost much
				    this.setLocToDf(newDf);
			    }
			    else if (transformType.equals(Procedure.TRANSFORM_NORM))
			    	// record the quirks that were introduced during the normalization process
			    	newDf.recordNormQuirks();
			}
		}
		
        if (keepTransforming){
            Informer.getInstance().info(this,
                    methodName,
                    "Done " + transformType + " at " + DateTimeUtil.now(),
                    "File: " + this.getPath(),
                    false);
        }
	}
	
	/**
	 * Assumes that the preservation level is full.
	 * 
	 * @param transformType transformation type
	 * @param format file format
	 * @param useSepDir whether to put new files in a separate directory
	 * @return whether or not to continue transforming
	 * @throws FatalException
	 */
	protected boolean transformStart(String transformType, String format, 
	        boolean useSepDir) throws FatalException {
		String methodName = "transformStart(String, String, boolean)";
		
		boolean keepTransforming = true;
		
		// check args
		if (transformType == null || format == null ||
				!Format.isValidFormat(format)){
			Informer.getInstance().fail(this,
					methodName,
					"Illegal argument",
					"transformType: " + transformType + 
					" format: " + format,
					new FatalException("Not a valid transform or format code"));
		}
		
		// see if we should continue of we are in the second localization pass.
		// we shouldn't if the first pass failed. we know it failed if the
		// localized path of thisfile is the same as its path
		/*
		if (transformType.equals(Procedure.TRANSFORM_LOC_2) &&
		        this.getLocalizedFilePath().equals(this.getPath())){
		    keepTransforming = false;
		}
		*/
		
		String transDir = null; // where to put new files
		
		/*
		# NOTE: MIGRATION, NORMALIZATION, AND LOCALIZATION POLICIES ARE DETERMINED
		# BY PRESERVATION LEVEL SPECIFIED IN FORMAL AGREEMENTS. IF A FORMAT/ASAP 
		# COMBINATION MAPS TO FULL PRESERVATION, THEN FILES OF THAT FORMAT WILL 
		# ALWAYS BE MIGRATED, NORMALIZED, AND LOCALIZED (IF ALSO A DISTRIBUTED FILE).
		# SINCE THIS INFORMATION IS SPEFICIED IN THE DATABASE AND REPRESENTS A BINDING
		# CONTRACT, IT SHOULD NOT BE OVERRIDDEN BY CONFIGURATION SETTINGS. -CTV
		*/
        
		String prefix = null; // config file property prefix
		
	    if (transformType.equals(Procedure.TRANSFORM_MIG)){
	        prefix = "MIG_";
	        transDir = this.getIp().getMigratedDir();
	    } else if (transformType.equals(Procedure.TRANSFORM_NORM)){
	        prefix = "NORM_";
	        transDir = this.getIp().getNormalizedDir();
	    } else if (transformType.equals(Procedure.TRANSFORM_LOC_1)){
	    	prefix = "LOC_";
	    }
	    // only going to check this for the first phase of localization,
	    // not the second
	    
	    if (prefix != null){
	    	// see if there's a config policy to transform this format	
	    	String doNorm = prefix + format;
			if (ArchiveProperties.getInstance().isArchProperty(doNorm)){
			    if (ArchiveProperties.getInstance().
						getArchProperty(doNorm).equals("true")){
					keepTransforming = true;
				} else {
					keepTransforming = false;
				}
			}
	    }
				
		//	make sure there's a place to put the transformed file(s)
		// if we're supposed to put it in a separate directory
        if (keepTransforming && useSepDir &&
                (transDir == null || !FileUtil.isGoodDir(transDir))) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Directory problem",
                    transformType + " dir.: " + transDir + 
                    " file: " + this.getPath(),
                    new FatalException("No place to put transformed files."));
        }
        
        if (keepTransforming){
            Informer.getInstance().info(this,
                    methodName,
                    "Beginning to " + transformType + " at " + 
						DateTimeUtil.now(),
                    "File: " + this.getPath(),
                    false);
        } else {
        	Informer.getInstance().info(this,
					methodName,
					"Not doing a " + transformType,
					"File: " + this.getPath(),
					false);
        }
		
		return keepTransforming;
	}
	
	/**
	 * Record the quirks introduced by the normalization process.  All DtatFile subclasses
	 * who would like to record normlization quirks shall override this method to 
	 * add quirks to the datafile object.
	 * This method is called by transformEnd method after the normalization process.
	 */
	public void recordNormQuirks() throws FatalException {
	}
	
    /**
     * @return Returns the localizedMdValue.
     */
    public String getLocalizedMdValue() {
        return localizedMdValue;
    }
    /**
     * @param localizedMdValue The localizedMdValue to set.
     */
    public void setLocalizedMdValue(String localizedMdValue) {
        this.localizedMdValue = localizedMdValue;
    }
    /**
     * @return Returns the linkAliasRelPathTable.
     */
    public Hashtable getLinkAliasRelPathTable() {
        return linkAliasRelPathTable;
    }

    /**
     * Returns true if the DataFile is a DTD or XSD. 
     * @return boolean
     */
    public boolean isSchema() {
        boolean isSchema = false;
        if (this instanceof DTD) {
           isSchema = true;
        }
        else if (this instanceof XML && this.getFileExt().equalsIgnoreCase("xsd")) {
            isSchema = true;
        }
        return isSchema;
    }
    
    /**
     * Whether this data file has already been archived.
     * @return
     */
    public boolean hasArchived() {
    	return hasArchived;
    }
}
