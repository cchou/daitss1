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

/** Java class "GlobalObjects.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss.file.global;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.ArchiveMessageDigest;
import edu.fcla.daitss.file.NoMdType1Exception;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;

/**
 * GlobalFiles class
 */
public class GlobalFiles {

	/**
	 * <p>
	 * Instance used for singleton
	 * </p>
	 */
	private static GlobalFiles instance = null;

	/**
	 * A convenience method to destroy the GlobalFiles object since a new 
	 * factory is needed by each package.
	 */
	public static void die() {
	    if (instance != null) {
	        instance = null;
	    }	    
	}

	/**
	 * If (this.instance == null) this.instance = new Instance(); return this.instance;
	 * 
	 * @return an instance of GlobalFiles
	 * @throws FatalException
	 */
	public static synchronized GlobalFiles getInstance() 
		throws FatalException{
			
		if (instance == null){
			instance = new GlobalFiles();
		}
		return instance;
	} // end getInstance        
	
	/**
	 * 
	 * @param args not used
	 * @throws PackageException 
	 * @throws FatalException
	 */
	public static void main(String[] args) throws PackageException, FatalException{
	    System.out.println(GlobalFiles.getInstance());

	    GlobalFile newGf =	new GlobalFile("F20041026_AAAAAB", 
                new ArchiveMessageDigest("sha-1", "11122333444"),
                1000);
	    newGf.setNew(true);
	    
	    GlobalFiles.getInstance().addGlobalFile(newGf);
	    
	    TransactionConnection tcon = DBConnection.getSharedConnection();
	    tcon.startTransaction();
	    GlobalFiles.getInstance().dbUpdate(ArchiveManager.ACTION_INGEST);
	    tcon.commitTransaction();
	}

	/**
	 * Stores the global file names listed in the config file. 
	 */
	private Vector globalFileNames = null;

	/**
	 * Stores checksum as the key, a GlobalObject as the value.
	 */
	private Hashtable globalFiles = null;

	/**
	 * <p>
	 * Sets this.newGlobalFiles = new Vector(); this.globalFiles = new HashTable();
	 * </p>
	 * <p>
	 * 
	 * @throws FatalException
	 */
	private GlobalFiles() throws FatalException {
		initGlobalFileNames();
		initGlobalFiles();	
	} // end GlobalObjects        

	/**
	 * <p>
	 * Add a GlobalFile to the globalFiles hashtable
	 * </p>
	 * @param gf ...
	 * @throws FatalException
	 */
	public void addGlobalFile(GlobalFile gf) throws FatalException {
		String methodName = "addGlobalFile(GlobalFile)";
		// get the md_type_1 message digest value		
		String mdValue = gf.getMesgDigestValue(
			ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1")); 
		// all GlobalFiles must have a MD_TYPE_1 message digest
		if (mdValue == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"No MD_TYPE_1 message digest exists for GlobalFile",
				gf.getPath(),
				new NoMdType1Exception());
		}
		
		this.globalFiles.put(mdValue, gf);
	} // end add        
    
    
    /**
     * Removes a global file from the GlobalFiles collection. Uses a checksum
     * value to remove the file. Checksum is expected to be an MD_TYPE_1 message
     * digest value (as are all checksums used as keys for hashtable collections 
     * in daitss).
     *
     * @param checksum
     * @return GlobalFile
     * @throws FatalException 
     */
    public GlobalFile removeGlobaFile(String checksum) throws FatalException {
        String methodName = "removeGlobalFile()";
        if (checksum == null || checksum.equals("")) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "checksum: " + checksum,
                    new IllegalArgumentException());                        
        }        
        return (GlobalFile) this.globalFiles.remove(checksum);
    }
    

	/**
	 * @param archiveAction
	 * @return integer
	 * @throws FatalException
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
	    
	    Enumeration e = globalFiles.elements();
		while (e.hasMoreElements()) {
			GlobalFile gf = (GlobalFile) e.nextElement();
			rowsAffected = rowsAffected + gf.dbUpdate(archiveAction);
		}		    
	    
	    return rowsAffected;
	}
           
	/**
	 * Checks the globalFiles hashtable to see if a 
	 * GlobalFile with the specified checksum exists. If 
	 * the GlobalFile is found, it is returned, if not, null
	 * is returned.
	 * 
	 * @param checksum
	 * @return a GlobalFile object if one exists with <code>checksum</code>,
	 * else null.
	 */
	public GlobalFile getGlobalFile(String checksum){
		GlobalFile gf = (GlobalFile) globalFiles.get(checksum);			
		return gf;
	}
	
	/**
	 * Accessor for the globalFiles Hashtable
	 * @return all globalFiles
	 */
	public Hashtable getGlobalFiles() {
		return globalFiles;
	} // end getObjects        

	/**
	 * Initializes the globalFileNames Vector with recognized file names (title + ext)
	 * as prescribed in the config file.
	 * 
	 * @throws FatalException
	 */
	private void initGlobalFileNames() throws FatalException {
		// initialize storage Vector
		this.globalFileNames = new Vector();
		// read in the global file names
		String gNames = ArchiveProperties.getInstance().getArchProperty("GLOBAL_FILES");
		// parse the property value
		StringTokenizer st = new StringTokenizer(gNames, ",");
		// enter names into storage Vector
		while (st.hasMoreTokens()){
			this.globalFileNames.add(st.nextToken());
		}        
    }

	/**
	 * Initializes the globalFiles Hashtable by populating it with previously 
	 * archived GlobalFile objects as stored in the database.
	 * 
	 * @throws FatalException
	 */
	private void initGlobalFiles() throws FatalException{
	    String methodName = "initGlobalFiles()";
	    this.globalFiles = new Hashtable();
	    
		// objects will be keyed by SHA-1 checksum which has already been 
		// calculated for every file.
		// enter into globalFiles hashtable 
		DBConnection cnn = DBConnection.getConnection();
		
		// create the select clause
		Vector cols = new Vector();
		cols.add(ArchiveDatabase.TABLE_GLOBAL_FILE + "." + 
		        ArchiveDatabase.COL_GLOBAL_FILE_DFID);
		cols.add(ArchiveDatabase.COL_GLOBAL_FILE_USE_COUNT);
		cols.add(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE);
		cols.add(ArchiveDatabase.COL_DATA_FILE_IEID);
		cols.add(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
		cols.add(ArchiveDatabase.COL_DATA_FILE_FILE_TITLE);
		cols.add(ArchiveDatabase.COL_DATA_FILE_FILE_EXT);
		cols.add(ArchiveDatabase.COL_DATA_FILE_PRES_LEVEL);

		// create the from clause
		String from = ArchiveDatabase.TABLE_GLOBAL_FILE + ", " +
					ArchiveDatabase.TABLE_MESSAGE_DIGEST + ", " + 
					ArchiveDatabase.TABLE_DATA_FILE;
		
		// get the primary message digest type
		String mdType = ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1");
		
		// create the where clause
		
		String where = ArchiveDatabase.TABLE_GLOBAL_FILE + "." +
					ArchiveDatabase.COL_GLOBAL_FILE_DFID + " = " +
					ArchiveDatabase.TABLE_MESSAGE_DIGEST + "." + 
					ArchiveDatabase.COL_MESSAGE_DIGEST_DFID + " AND " +
					ArchiveDatabase.COL_MESSAGE_DIGEST_CODE + " = " +
					SqlQuote.escapeString(mdType) + " AND " +
					ArchiveDatabase.TABLE_GLOBAL_FILE + "." + 
					ArchiveDatabase.COL_GLOBAL_FILE_DFID + " = " +
					ArchiveDatabase.TABLE_DATA_FILE + "." + 
					ArchiveDatabase.COL_DATA_FILE_DFID;			
				
		// execute the query
		ResultSet rs = cnn.retrieve(cols, from, where);
		
		// move to the first result
	    try {
            while (rs.next() == true) {
                // iterate through the results and add to the GlobalFiles hashtable
                String dfid = rs.getString(ArchiveDatabase.COL_GLOBAL_FILE_DFID);
                long use_count = rs.getLong(ArchiveDatabase.COL_GLOBAL_FILE_USE_COUNT);
                String checksum = rs.getString(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE);                
                String ieid = rs.getString(ArchiveDatabase.COL_DATA_FILE_IEID);                
                // its package path now includes its IEID
                //String packagePath = ieid + File.separator + rs.getString(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
                String packagePath = rs.getString(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
                String fileTitle = rs.getString(ArchiveDatabase.COL_DATA_FILE_FILE_TITLE);
                String fileExt = rs.getString(ArchiveDatabase.COL_DATA_FILE_FILE_EXT);
                String presLevel = rs.getString(ArchiveDatabase.COL_DATA_FILE_PRES_LEVEL);
				                
                // create new GlobalFile object and add it to the hashtable
                GlobalFile gf = new GlobalFile(dfid, new ArchiveMessageDigest(mdType, checksum), use_count);
                gf.setIeid(ieid);                 
                gf.setPackagePath(packagePath);
                gf.setPath(ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR") 
                        + ieid + File.separator
                        + packagePath);
                gf.setFileTitle(fileTitle);
                gf.setFileExt(fileExt);
                gf.setIsGlobal(true);
				gf.setPresLevel(presLevel);
				
                // determine and set the path to the GlobalFile's localized version 
                gf.findLocalizedPath();                                    		   
    		    // determine and store the MD_TYPE_1 message digest values for all children
                gf.findChildren();

                // this GlobalFile has already been archived
                gf.setNew(false);
                addGlobalFile(gf);
            }
	                        
        } catch (SQLException e) {
            Informer.getInstance().fail(
				this,
				methodName,
				"Error iterating through ResultSet",
				"ResultSet iteration",
				e);        
        } catch (PackageException e ) {
        	throw new FatalException(e.getMessage());
        }
        
        try {
        	rs.close();
            cnn.close();            
        } catch (SQLException e) {
            Informer.getInstance().warning(this,
                    methodName,
                    "Error closing database connection: " + e.getMessage(),
                    cnn.toString());
        }
        
        cnn = null;
    }

	/**
	 * Checks the globalFiles hashtable to see if a 
	 * GlobalFile with the specified checksum exists. If 
	 * the checksum is found, true is returned, if not, false
	 * is returned.
	 * 
	 * @param checksum
	 * @return true if <code>checksum</code> is associated with a GlobalFile
	 * else false.
	 */
	public boolean isGlobalFile(String checksum){
		GlobalFile gf = (GlobalFile) globalFiles.get(checksum);			
		return (globalFiles.containsKey(checksum)?true:false);
	}
	
	/**
	 * Specifies whether or not the fileName agument is listed as a
	 * commonly-used file name in the configuration file. Used for knowing if
	 * this link might be a file that's already archived.
	 * 
	 * @param fileTitle the file title
	 * @return true if fileName is a recognized global file name, otherwise false
	 */
	public boolean isGlobalName(String fileTitle) {		
		Iterator i = this.globalFileNames.iterator();
		while (i.hasNext()){
			// perform case-insensitive comparison
			if (((String) i.next()).equalsIgnoreCase(fileTitle)){
				return true;
			}
		}
		// if we made it here, then the requested name wasn't found
		return false;
	} // end isGlobalName       

	/** 
	 * Converts the contents of GlobalFiles to a String 
	 * @return String object
	 */
	public String toString() {
	    StringBuffer sb = new StringBuffer("");
	    
	    sb.append("\nGlobalFiles: globalFileNames");
	    Iterator iter = this.globalFileNames.iterator();	
        while (iter.hasNext()) {
            sb.append("\n\t" + (String)iter.next());	
        }

	    sb.append("\nGlobalFiles: globalFiles");
		Enumeration e = globalFiles.elements();
		while (e.hasMoreElements()) {
			GlobalFile gf = (GlobalFile) e.nextElement();
			sb.append("\n\t" + gf);
		}	    	    
	    
	    return sb.toString();
	}
	
	/**
	 * Generate an XML representation of the global files table.  
	 * @return Document object
	 * @throws FatalException
	 */
	public Document toXML() throws FatalException {
	    /* create the daits document */
	    Document doc = XPaths.newDaitssDoc();
	    Element root = doc.getDocumentElement();
	    
	    Collection gfs = globalFiles.values();
	    for (Iterator iter = gfs.iterator(); iter.hasNext();) {
            GlobalFile gf = (GlobalFile) iter.next();
            Document d = gf.toXML();
            Node node = XPaths.selectSingleNode(d, XPaths.Daitss.GLOBAL_FILE);
            if (node != null) {
            	node = doc.importNode(node, true);
            	root.appendChild(node);
            }
        }
	    return doc;
	}
	
} // end GlobalObjects
