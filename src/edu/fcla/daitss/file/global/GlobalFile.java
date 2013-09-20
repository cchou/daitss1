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
package edu.fcla.daitss.file.global;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.entity.GlobalFilePackage;
import edu.fcla.daitss.entity.Relationship;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.ArchiveMessageDigest;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;

/**
 * A file that is shared by multiple packages.
 */
public class GlobalFile extends DataFile {

	/** 
	 * @param filePath 
	 * @return Boolean 
	 * @throws FatalException 
	 * 
	 */
	public static boolean isType(String filePath) throws FatalException {
	    return false;
	}
	
	/**
	 * @param filePath 
	 * @param _metadata 
	 * @return boolean
	 * @throws FatalException 
	 * 
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
	    return false;
	}

	/**
	 * Storage for the message digest values of the GlobalFile's children
	 */
	private Vector children = null;
	
	/**
	 * Number of datafiles referring to (sharing) this datafile.
	 */
	private long count;
	
	/**
	 * Whether or not this file was encountered for the first time by daitss 
	 * (not already stored in the archive).
	 */
	private boolean isNew = false;

	/**
	 * Constructor to be used to create GlobalFile objects from 
	 * database query (hence fileTitle arg, but no absPath arg).
	 * 
	 * @param dfid
	 * @param amd1
	 * @param count
	 * @throws FatalException
	 */
	public GlobalFile(String dfid, 
		ArchiveMessageDigest amd1,
		long count) throws FatalException {
	    
		super();
		// initialize superclass members that are needed by GlobalFile, 
		// but aren't initialized in the no-arg super constructor.
		this.mesgDigests = new Vector(1);		
		this.setOid(dfid);
		this.setIsGlobal(true);
		this.addMesgDigest(amd1);
		this.setCount(count);	
	}
	
	/**
	 * Constructor to be used to create GlobalFile objects from 
	 * physical files (hence absPath arg). Sets DFID, path, fileTitle, isGlobal = true,
	 * messageDigests for MD_TYPE_1 and MD_TYPE_2, and count
	 * 
	 * @param dfid DataFile ID
	 * @param absPath absolute path to the file
	 * @param gfp package containing this global file
	 * @param count number of packages using this global file
	 * @throws FatalException
	 */
	public GlobalFile(String dfid, 
			String absPath, GlobalFilePackage gfp,		
			long count) throws FatalException {		
		// the super constructor will set the path, title, and message digests
		super(absPath, gfp);		
		this.setIsGlobal(true);
		this.setOid(dfid);	
		this.setCount(count);
	} // end GlobalObject        
	

	/** 
	 * Updates the database for GlobalFile objects. If the GlobalFile has
	 * already been archived then its use_count is updated. If the GlobalFile
	 * is new, then a new record is inserted in to the database.
	 * @param archiveAction 
	 * @return  integer primitive
	 * @throws FatalException 
	 * 
	 */
	public int dbUpdate(byte archiveAction) throws FatalException {
	    String methodName = "dbUpdate()";
	    
	    int rowsAffected = 0;

	    if (!ArchiveManager.isValidAction(archiveAction)) {
	        Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "archiveAction: " + archiveAction ,
                    new IllegalArgumentException());
	    }	    
	    
	    // get the shared connection
	    TransactionConnection tcon = DBConnection.getSharedConnection();
	    
	    // if this GlobalFile has already been archived then
	    // update its use_count
	    if (!isNew()) {	
		    // init update/insert vars
	        String tbl = ArchiveDatabase.TABLE_GLOBAL_FILE;
		    Vector cols = new Vector();
		    Vector vals = new Vector();
		    String where = "";

		    cols.add(ArchiveDatabase.COL_GLOBAL_FILE_USE_COUNT);	        
	        vals.add(new Long(this.getCount()));	       
	        where = ArchiveDatabase.COL_GLOBAL_FILE_DFID + " = " + SqlQuote.escapeString(this.getOid());	        
	        
	        // issue the update
	        rowsAffected = rowsAffected + tcon.update(tbl, cols, vals, where);
	        
	    } else {	    	        
	        // if this GlobalFile is new, then insert a new reocrd
	        String tbl = ArchiveDatabase.TABLE_GLOBAL_FILE;
	        DBConnection.NameValueVector colVals = new DBConnection.NameValueVector();
	        // set the DFID value
	        colVals.addElement(ArchiveDatabase.COL_GLOBAL_FILE_DFID, this.getOid());
	        // set the USE_COUNT value
	        colVals.addElement(ArchiveDatabase.COL_GLOBAL_FILE_USE_COUNT, new Long(this.getCount()));
	        
	        // issue the insert
	        rowsAffected = rowsAffected + tcon.insert(tbl, colVals);
	    }

	    return rowsAffected;
	}
	
	protected void evalMembers() throws FatalException {
		// Intelligent Design
	}
	
	/**
	 * Method to query and store the message digest value (MD_TYPE_1) for each
	 * child of this GlobalFile (children are defined as externally referenced
	 * resources) 
	 *
	 * @throws FatalException
	 */
	protected void findChildren() throws FatalException {
	    
	    String methodName = "findChildren()";
	    
	    // initialize the children Vector
	    this.children = new Vector();
	    
	    String query = "SELECT " + ArchiveDatabase.TABLE_RELATIONSHIP + "." + ArchiveDatabase.COL_RELATIONSHIP_DFID_1
	    	+ ", " + ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE
	    	+ " FROM " + ArchiveDatabase.TABLE_RELATIONSHIP 
	    	+ ", " + ArchiveDatabase.TABLE_MESSAGE_DIGEST
	    	+ " WHERE " + ArchiveDatabase.TABLE_RELATIONSHIP + "." + ArchiveDatabase.COL_RELATIONSHIP_DFID_2
	    	+ " = " + SqlQuote.escapeString(this.getOid())
            + " AND " + ArchiveDatabase.TABLE_RELATIONSHIP + "." + ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE
            + " = \'" + ArchiveDatabase.VALUE_RELATIONSHIP_REL_TYPE_CHILD_OF + "\'"                        
            + " AND " + ArchiveDatabase.TABLE_RELATIONSHIP + "." + ArchiveDatabase.COL_RELATIONSHIP_DFID_1
	    	+ " = " + ArchiveDatabase.TABLE_MESSAGE_DIGEST + "." + ArchiveDatabase.COL_MESSAGE_DIGEST_DFID
	    	+ " AND " + ArchiveDatabase.TABLE_MESSAGE_DIGEST + "." + ArchiveDatabase.COL_MESSAGE_DIGEST_CODE
	    	+ " = " + SqlQuote.escapeString(ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"));
	    
	    try {
            DBConnection c = DBConnection.getConnection();
            ResultSet rs = c.executeQuery(query);
            
            while (rs.next()) {
                String mdValue = rs.getString(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE);
                this.children.add(mdValue);
            }
            rs.close();
            c.close();
            c = null;
            
        } catch (SQLException e) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Error in database query or result set iteration",
                    query,
                    e);            
        }
	} 
	
	/**
	 * Method to determine the local path (in the LOCAL_GLOBAL_DIR) to the 
	 * localized version of the file. If the GlobalFile was not localized
	 * on ingest, then it does not use external links and therefore it is
	 * already localized. In this case, the localized version of the GlobalFile
	 * is simply the copy of the file stored in LOCAL_GLOBAL_DIR. 
	 * 
	 * Note: this method must access the database to determine if the GlobalFile
	 * has been localized or not.  
	 * 
	 * @throws FatalException
	 */
	protected void findLocalizedPath() throws FatalException {

	    String methodName = "findLocalizedPath()";
	    
	    String globalLocDir = ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR");
		// get the pacakge path of the Localized version of this global file
		// set the GlobalFile's localized path to the localized version of itself
		// query the db to get the name of this global file's localized version
		String locFilePath = null;
		String locMdValue = null;  		
		// it's necessary to get the MAX DFID because there is a possiblility that the 
		// global file was localized more than once (if localization techniques are improved
		// for example)
		String query = 	"SELECT MAX(" + ArchiveDatabase.COL_RELATIONSHIP_DFID_2 + "), " 
			+ ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH 
			+ ", " + ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME
			+ ", " + ArchiveDatabase.TABLE_DATA_FILE + "." + ArchiveDatabase.COL_INT_ENTITY_IEID
			+ ", " + ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE
			+ " FROM " + ArchiveDatabase.TABLE_RELATIONSHIP 
			+ " , " + ArchiveDatabase.TABLE_DATA_FILE
			+ " , " + ArchiveDatabase.TABLE_INT_ENTITY
			+ " , " + ArchiveDatabase.TABLE_MESSAGE_DIGEST
			+ " WHERE " + ArchiveDatabase.COL_RELATIONSHIP_DFID_1 + "=" + SqlQuote.escapeString(this.getOid()) 
			+ " AND " + ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE + "=\'" + Relationship.REL_LOCALIZED_TO + "\'"
    		+ " AND " + ArchiveDatabase.COL_RELATIONSHIP_DFID_2 + "=" + ArchiveDatabase.TABLE_DATA_FILE + "." + ArchiveDatabase.COL_DATA_FILE_DFID
    		+ " AND " + ArchiveDatabase.TABLE_DATA_FILE + "." + ArchiveDatabase.COL_DATA_FILE_DFID
    		+ " = " + ArchiveDatabase.TABLE_MESSAGE_DIGEST  + "." + ArchiveDatabase.COL_MESSAGE_DIGEST_DFID
    		+ " AND " + ArchiveDatabase.TABLE_MESSAGE_DIGEST + "." + ArchiveDatabase.COL_MESSAGE_DIGEST_CODE 
    		+ " = " + SqlQuote.escapeString(ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"))
    		+ " AND " + ArchiveDatabase.TABLE_INT_ENTITY + "." + ArchiveDatabase.COL_INT_ENTITY_IEID
    		+ "=" + ArchiveDatabase.TABLE_DATA_FILE + "." + ArchiveDatabase.COL_DATA_FILE_IEID
    		+ " GROUP BY " + ArchiveDatabase.COL_RELATIONSHIP_DFID_2;
		     
        DBConnection conn = null;
		try {
            conn = DBConnection.getConnection();
            ResultSet rs2 = conn.executeQuery(query);        		        		
            // get the results. if no results, then this global file has no localized version (no links)
            while (rs2.next()) {
                String locDfid = rs2.getString("MAX(" + ArchiveDatabase.COL_RELATIONSHIP_DFID_2 + ")");
                String locPackagePath = rs2.getString(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
                String locPackageName = rs2.getString(ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME);
                String locIeid = rs2.getString(ArchiveDatabase.TABLE_DATA_FILE + "." + ArchiveDatabase.COL_INT_ENTITY_IEID);
                locMdValue = rs2.getString(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE);  		            		    
                locFilePath = globalLocDir + locIeid + File.separator + locPackagePath; 
            }
            rs2.close();
        } catch (SQLException e) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Error in database query or result set iteration",
                    query,
                    e);
        } finally {
            try {
                conn.close();
            }catch (SQLException e) {
                Informer.getInstance().warning(this,
                        methodName,
                        "Error closing database connection: " + e.getMessage(),
                        conn.toString());
            }            
            conn = null;
            
        }
		
		
		if (locFilePath == null) {
		    // file was not localized, this is OK - the localized file is the 
		    // global file itself. Set its localized path to the local copy of
		    // itself
		    locFilePath = globalLocDir + this.getIeid() + File.separator + this.getPackagePath();		   
            //locFilePath = globalLocDir + this.getPackagePath();           
		    locMdValue = this.getMesgDigestValue(ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"));
		}
	
		// make sure the file exists
	    File f = new File(locFilePath);
	    if (!f.exists() || !f.isFile()) {
	        Informer.getInstance().fail(this,
                    methodName,
                    "Unable to locate localized version of GlobalFile: " + this.getFileTitle(),
                    locFilePath,
                    new FatalException());
	    }        		    

	    // set the path to the global file's localized version
	    this.setLocalizedFilePath(locFilePath);        		            		    	    
	    this.setLocalizedMdValue(locMdValue);
	}
	
	/**
	 * Returns the number of datafiles referring to (sharing) this datafile.
	 * 
	 * @return the number of datafiles referring to (sharing) this datafile
	 */
	public long getCount() {
		return count;
	} // end getCount        

	/**
	 * @return whether or not it is a new global file
	 */
	public boolean isNew() {
		return isNew;
	}
	
	/**
	 * @throws PackageException 
	 * @throws FatalException 
	 * 
	 */
	public void migrate() throws PackageException, FatalException {
	    // Intelligent Design
	}

	/**
	 * @throws PackageException 
	 * @throws FatalException 
	 */
	public void normalize() throws PackageException, FatalException {
		// do nothing
	}	
	
	protected void parse() throws FatalException {
	    // Intelligent Design
	}

	/**
	 * Set the number of datafiles referring to (sharing) this datafile.
	 * 
	 * @param count the number of datafiles referring to (sharing) this datafile
	 * @throws FatalException
	 */
	public void setCount(long count) throws FatalException {
		String methodName = "setCount(long)";
		if (count < 0) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument, count < 0",
				"count: " + count,
				new IllegalArgumentException());	
		}		
		this.count = count;
	} // end setCount        

	/**
	 * @param b whether or not its a new global file
	 */
	public void setNew(boolean b) {
		isNew = b;
	}
	
	/**
	 * @return String object
	 * 
	 */
	public String toString() {
	    StringBuffer sb = new StringBuffer("");
	    sb.append("GlobalFile: ");
	    sb.append("\n\t\tDFID: " + getOid());
	    sb.append("\n\t\tMD Value: ");
	    sb.append((getMesgDigests().elementAt(0) ==  null? "null": 
	        ((ArchiveMessageDigest) getMesgDigests().elementAt(0)).getValue()));
	    sb.append("\n\t\tMD Algorithm: ");
	    sb.append((getMesgDigests().elementAt(0) ==  null? "null": 
	        ((ArchiveMessageDigest) getMesgDigests().elementAt(0)).getCode()));
	    sb.append("\n\t\tFile title: ");
	    sb.append(this.getFileTitle());
	    
	    
	    return sb.toString();
	}

	/**
	 * @return Document object
	 * @throws FatalException
	 * 
	 */
	public Document toXML() throws FatalException {
	    Document doc = XPaths.newDaitssDoc();
	    Element root = doc.getDocumentElement();
	    String namespace = root.getNamespaceURI();
	    	    
	    /* Global File */
	    Element globalFile = (Element) root.appendChild(doc.createElementNS(namespace, "GLOBAL_FILE"));
	    
        /* DFID */
        Element dfid = (Element) globalFile.appendChild(doc.createElementNS(namespace, "DFID"));
                dfid.appendChild(doc.createTextNode(this.getOid()));
        
        /* Use Count */
        Element useCount = (Element) globalFile.appendChild(doc.createElementNS(namespace, "USE_COUNT"));
        useCount.appendChild(doc.createTextNode(Long.toString(this.getCount())));
        
        return doc;
	}

    /**
     * @return Returns the children.
     */
    public Vector getChildren() {
        return children;
    }
} // end GlobalObject
