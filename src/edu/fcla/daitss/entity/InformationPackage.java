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

package edu.fcla.daitss.entity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import com.sun.java_cup.internal.parse_action;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.InvalidPackageException;
import edu.fcla.daitss.NoInitialDescriptorException;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.exception.SilentPackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.FileRole;
import edu.fcla.daitss.file.MessageDigestCalculator;
import edu.fcla.daitss.file.global.GlobalFile;
import edu.fcla.daitss.file.global.GlobalFiles;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.storage.Tar;
import edu.fcla.daitss.storage.TarException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.Silo;
import edu.fcla.jmserver.StorageException;

/**
 * @author vicary
 * An Information Package (superclass of a SIP, AIP and DIP).
 * 
 */
public abstract class InformationPackage {
    
    /**
     * Pattern for the name of an archive-generated descriptor (AIP descriptor).
     */
    protected static String ARCHIVE_DESC_PATTERN = "AIP_\\d{4}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])([0-5][0-9]){2}_LOC[.]xml";
	
    /**
     * Maximum supported <code>packageName</code> value.
     */
    public static final int MAX_LEN_PACKAGE_NAME = 32;
    
    /** State of a SIP just after it has been successfully built */
    public static final byte SIP_BUILT = 4;
    /** State of a SIP just after extractContent() has been called on its initialDescriptor */
    public static final byte SIP_DESC_CONTENT_EXTRACTED = 2;
    /** State of a SIP just after its constructor is called*/
    public static final byte SIP_LITE = 1;
    /** State of a SIP just after it has been successfully migrated */
    public static final byte SIP_MIGRATED = 5;
    /** State of a SIP just after it has been successfully normalized */
    public static final byte SIP_NORMALIZED = 6;
    /** State of a SIP after it has been rejected */
    public static final byte SIP_REJECTED = 9;
    /** State of a SIP just after successful validation */
    public static final byte SIP_VALIDATED = 3;
    /** State of a SIP just after the metadata for its IntEnt and all DataFiles have
     *  been stored in the database
     */
    public static final byte SIP_WRITTEN_TO_DATABASE = 8;
    /** State of a SIP just after its DataFile objects have been stored to tape */
    public static final byte SIP_WRITTEN_TO_STORAGE = 7;
    //	static variables for package state, if a new state constant
    // is added to the class, then it must also be added to the 
    // packageStates array member 
    /** UNKNOWN package state, the default state */
    public static final byte UNKNOWN = 0;

    // array for storing statuses, primarily used to validate
    // a state passed to setState(byte)
    private static byte[] packageStates =
    {
	UNKNOWN,
	SIP_LITE,
	SIP_DESC_CONTENT_EXTRACTED,
	SIP_VALIDATED,
	SIP_BUILT,
	SIP_MIGRATED,
	SIP_NORMALIZED,
	SIP_WRITTEN_TO_STORAGE,
	SIP_WRITTEN_TO_DATABASE,
	SIP_REJECTED };    
             
    
    /**
     * Represents the archive-generated metadata file (AIP descriptor) that came with this package 
     * when it was (re)ingested.
     */
    private METSDescriptor archiveDescriptor;

    private Vector <DataFile> dataFilePrecedenceList = null;
    /**
     * A HashTable of DataFile objects contained in this InformationPackage,
     * keyed by the absolute path to the existing files. After the
     * ContentObjectFactory has created all DataFiles for this package (i.e.
     * the IP has reached the end of the submitted files), a call to this.dfl =
     * cof.getDataFiles will return a Vector of DataFiles and set the member
     * dfl. Later the dfl will be used to create the SIPDescriptor.
     */
    private Hashtable <String, DataFile> dfl;

    /**
     * A Hashtable to store all GlobalFile objects used by this IP, keyed by absolute path
     */
    protected Hashtable<String, GlobalFile> globalFiles;
	
    /**
     * A Hashtable for correlating URIs to GlobalFile objects
     * used in this SIP
     */
    protected Hashtable<String, GlobalFile> globalNameLookup;
    /**
     * Whether or not this package has an AIP descriptor created by the archive 
     */
    protected boolean hasArchiveDescriptor = false;
    /**
     * Whether or not this package has an initial descriptor.
     */
    protected boolean hasInitialDescriptor = false;
    /**
     * Represents the metadata file that came with this package when it was
     * ingested.
     */
    private METSDescriptor initialDescriptor;

    /**
     * The intellectual entity for this package.
     */
    private IntEntity intEnt = null;

    /**
     * The absolute path to the directory for storing files that
     * are downloaded as a result of retrieving links for any file in this
     * package. Do include the final slash after the directory.
     */
    private String linksDir = null;

    /**
     * The absolute path to the directory for storing files that
     * are created during migration for any file in this package. 
     * Do include the final slash after the directory.
     */
    private String migratedDir = null;

    /**
     * The absolute path to the directory for storing files that
     * are created during normalization for any file in this package.
     * Do include the final slash after the directory.
     */
    private String normalizedDir = null;

    /**
     * Set to true if this InformationPackage has already been archived 
     */
    private boolean packageArchived = false;    
    
    /**
     * Unqualified package name.
     */
    protected String packageName;

    /**
     * Absolute path to this package including package name before it gets
     * moved to the working directory. Ex: &quot;/DLArch2/ready/UFE0000001/&quot; .
     */
    private String packagePath;

    // state variable
    private byte state = UNKNOWN;

    /**
     * The absolute path to the package as it is being processed including the
     * IEID directory that encapsulates all submitted and downloaded files. Ex for
     * ingest: &quot;/daitss/data/ingest/work/UFE0000001/E20050101_123456&quot;.
     */
    private String workingPath = null;

    protected List<Copy> copies;
    
    protected String copyMD5;
    
    /**
     * Constructor. Initializes members.
     * 
     * @param packagePath Absolute path to the package directory
     * @throws FatalException
     */
    public InformationPackage(String packagePath) throws FatalException {
	this.dfl = new Hashtable<String, DataFile>();
	this.globalFiles = new Hashtable<String, GlobalFile>();
	this.globalNameLookup = new Hashtable<String, GlobalFile>();
        
        // set this.packagePath
        setPackagePath(packagePath);
        // set this.workPath the working path is currently the package path
        setWorkingPath(getPackagePath());
        // set the package name
        setPackageName(
		       InformationPackage.convertPathToPackageName(getPackagePath()));
    }

    /**
     * Converts a packagePath to a packageName. The path is expected to 
     * be of the form &quot;/DLData/UF/.../UF00000000/&quot; and the packageName
     * returned will be of the form &quot;UF00000000&quot;.
     * 
     * @param packagePath
     * @return the packageName represented by the packagePath argument.
     * 
     */
    public static String convertPathToPackageName(String packagePath) {
	// determine package name
	// set to temp var
	String pName = packagePath.trim();
	// strip trailing separator if present
	if (pName.endsWith(File.separator)) {
	    pName = pName.substring(0, pName.length() - 1);
	}
	// strip everything before the last separator, this is the
	// package name
	return pName.substring(pName.lastIndexOf(File.separator) + 1);
    }

    /**
     * Determines if <code>packageName</code> fits the pattern
     * expected for package names.
     * 
     * @param packageName
     * @return boolean
     */
    public static boolean isValidPackageName(String packageName) {
        if (packageName.length() > MAX_LEN_PACKAGE_NAME || 
	    packageName.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Returns true if b is a valid state, else returns false.
     * 
     * @param b a byte representing a state
     * @return boolean 
     */public static boolean isValidState(byte b) {
	 for (int i = 0; i < packageStates.length; i++) {
	     if (b == packageStates[i])
		 return true;
	 }
	 // if control made it here, then b is not a 
	 // recognized status
	 return false;
     }
	
    /**
     * Method to add a GlobalFile to the globalFiles Vector. 
     * GlobalFile objects must be unique within the  globalFiles
     * Vector, so if the GlobalFile is already present, it is 
     * not added.
     *
     * @param gf a GlobalFile object to add 
     * @param absPath the absolute path to the GlobalFile
     */
    public void addGlobalFile(String absPath, GlobalFile gf) {
	globalFiles.put(absPath, gf);
    }

    /**
     * Adds a GlobalFile and its identifier to the globalNameLookup
     * Hashtable. If the uri argument (the key) is repeated, its value
     * will be overwritten. 
     * 
     * @param uri a identifier used as a key in the globalNameLookup Hashtable
     * @param gf a GlobalFile used as a value in the globalNameLookup Hashtable
     */
    public void addToGlobalNameLookup(String uri, GlobalFile gf) {
	this.getGlobalNameLookup().put(uri, gf);
    }
    
    /**
     * Delete everything in workPath
     * @throws FatalException
     */ 
    protected void clearWorkDir() throws FatalException {
        // get the work path
        String workPath = ArchiveProperties.getInstance().getArchProperty("INGEST_WORKPATH");       
        // delete everything in the workPath
        File d = new File(workPath);
        File[] contents = d.listFiles();
        for (int i=0;i<contents.length;i++) {
            try {
                // delete this file or directory
                if (!FileUtil.delete(contents[i].getAbsolutePath())) {
                    // creating an error here is problematic since 
                    // control will eventually return and perform the 
                    // same task which just failed. Just log it for
                    // human inspection
                    Informer.getInstance().warning(this, 
						   "clearWorkDir()",
						   "Unable to delete contents of workDir",
						   "path: " + contents[i].getAbsolutePath());                      
                }   
            }
            catch (IOException ioe){
                // this only happens when the path to delete
                // doesn't exist. most likely programmer error
                Informer.getInstance().fail(this, 
					    "clearWorkDir()",
					    "Error deleting contents of workDir",
					    "path: " + contents[i].getAbsolutePath(),
					    ioe);   
            }
        }
        // cleanup
        contents = null;
        d = null;
    }   
    
    /**
     *  Delete everything in outPath
     *  @throws FatalException
     */
    protected void clearOutDir() throws FatalException {
        // get the work paths
        String outPath = ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH");     
        // delete everything in the workPath
        File d = new File(outPath);
        File[] contents = d.listFiles();
        for (int i=0;i<contents.length;i++) {
            try {
                // delete this file or directory
                if (!FileUtil.delete(contents[i].getAbsolutePath())) {
                    // creating an error here is problematic since 
                    // control will eventually return and perform the 
                    // same task which just failed. Just log it for
                    // human inspection
                    Informer.getInstance().warning(this, 
						   "clearOutDir()",
						   "Unable to delete contents of outDir",
						   "path: " + contents[i].getAbsolutePath());                      
                }   
            }
            catch (IOException ioe){
                // this only happens when the path to delete
                // doesn't exist. most likely programmer error
                Informer.getInstance().fail(this, 
					    "clearOutDir()",
					    "Error deleting contents of outDir",
					    "path: " + contents[i].getAbsolutePath(),
					    ioe);   
            }
        }
        // cleanup
        contents = null;
        d = null;
    }

    
    
    /**
     * Copies the package at fromPath to toPath. Does not reset
     * this.packagePath. If fromPath does not exist or is not readable or is
     * not a directory throw an UnknownPathException. If toPath already exists
     * throw a DirectoryExistsException. If the copy fails for any reason
     * including unexpected exceptions throw a CopyFailedException.
     * @param toPath ...
     * @throws FatalException
     * @throws PackageException
     */
    public void copy(String toPath)
	throws FatalException, PackageException {
	// make sure fromPath is an existing directory (readability
	String methodSig = "copy(String,String)";
	String fromPath = this.getPackagePath();
		
	File src = new File(fromPath);
	if (!src.exists()) {
	    Informer.getInstance().fail(
					this,
					methodSig,
					"Invalid fromPath argument (does not exist).",
					fromPath,
					new IllegalArgumentException(fromPath + " does not exist."));
	}
	if (!src.isDirectory()) {
	    Informer.getInstance().fail(
					this,
					methodSig,
					"Invalid fromPath argument (not a directory).",
					fromPath,
					new IllegalArgumentException(
								     fromPath + " is not a directory."));
	}

	// make sure the destination directory doesn't already exist
	File dest = new File(toPath);
	if (dest.exists()) {
	    Informer.getInstance().fail(
					this,
					methodSig,
					"Invalid toPath argument (path already exists).",
					toPath,
					new IllegalArgumentException(
								     toPath + " is an existing file or directory."));
	}
	// attempt to copy the package
	try {
	    FileUtil.copy(fromPath, toPath);
	} catch (IOException ioe) {
	    Informer.getInstance().error(
					 this,
					 methodSig,
					 "FileUtil.copy() failed.",
					 "Copy from " + fromPath + " to " + fromPath,
					 ioe);
	}
		
	// clean up
	src = null;
	dest = null;
		
    } // end copy         

    /**
     * Method to create directories needed by DAITSS for processing
     * packages.
     * 
     * @throws PackageException
     * @throws FatalException
     */
    protected void createArchiveDirectories() throws PackageException, FatalException {			
	this.createNormalizedDir();
	this.createMigratedDir();
	this.createLinksDir();
    }

    /**
     * Creates a new directory to store downloaded links in.
     * 
     * @throws PackageException
     * @throws FatalException
     */
    protected void createLinksDir() throws PackageException, FatalException {
        String methodName = "createLinksDir()";

        // create the links directory directory name
        String linksDir = this.getWorkingPath() 
            + "links"
            + "_" + DateTimeUtil.getDateTimeStamp();                    
        try {
            File f = new File(linksDir);
            // check for preexistence of directory name 
            if (f.exists()) {
                Informer.getInstance().error(this,
					     methodName,
					     "Unable to create links directory: name already in use",
					     linksDir,
					     new PackageException("Links directory name already in use")
					     );          
            }
            // now make the directory. note: since using
            // mkdir(), only the last directory in the 
            // path will be made, if it becomes necessary to 
            // create intermediate directories, then change this to 
            // mkdirs()
            f.mkdir();
            this.setLinksDir(linksDir);
        }
        catch (Exception e) {
            Informer.getInstance().error(this,
					 methodName,
					 "Links directory creation failed",
					 linksDir,
					 e
					 );
        }
            
    }
    
    /**
     * Creates a new directory to store migrated files in.
     * 
     * @throws PackageException
     * @throws FatalException
     */
    protected void createMigratedDir() throws PackageException, FatalException {
        String methodName = "createMigratedDir()";
                
        // create the migrated directory directory name
        String migratedDir = this.getWorkingPath() 
            + "migrated"
            + "_" + DateTimeUtil.getDateTimeStamp();                    
        try {
            File f = new File(migratedDir);
            // check for preexistence of directory name 
            if (f.exists()) {
                Informer.getInstance().error(this,
					     methodName,
					     "Unable to create migrated directory: name already in use",
					     migratedDir,
					     new PackageException("Migrated directory name already in use")
					     );          
            }
            // now make the directory. note: since using
            // mkdir(), only the last directory in the 
            // path will be made, if it becomes necessary to 
            // create intermediate directories, then change this to 
            // mkdirs()
            f.mkdir();
            this.setMigratedDir(migratedDir);
        }
        catch (Exception e) {
            Informer.getInstance().error(this,
					 methodName,
					 "Migrated directory creation failed",
					 migratedDir,
					 e
					 );
        }

    }
    
    /**
     * Creates a new directory to store normalized files in.
     * 
     * @throws PackageException
     * @throws FatalException
     */
    protected void createNormalizedDir() throws PackageException, FatalException {
        String methodName = "createNormalizedDir()";
                
        // create the links directory directory name
        String normalizedDir = this.getWorkingPath() 
            + "normalized"
            + "_" + DateTimeUtil.getDateTimeStamp();                    
        try {
            File f = new File(normalizedDir);
            // check for preexistence of directory name 
            if (f.exists()) {
                Informer.getInstance().error(this,
					     methodName,
					     "Unable to create normalized directory: name already in use",
					     normalizedDir,
					     new PackageException("Normalized directory name already in use")
					     );          
            }
            // now make the directory. note: since using
            // mkdir(), only the last directory in the 
            // path will be made, if it becomes necessary to 
            // create intermediate directories, then change this to 
            // mkdirs()
            f.mkdir();
            this.setNormalizedDir(normalizedDir);
        }
        catch (Exception e) {
            Informer.getInstance().error(this,
					 methodName,
					 "Normalized directory creation failed",
					 normalizedDir,
					 e
					 );
        }       
    }

	
    /**
     * Method to persist an InformationPackage to the database.
     *  
     * @param archiveAction
     * @return int
     * @throws FatalException
     * @throws PackageException
     */
    public int dbUpdate(byte archiveAction) 
	throws FatalException {
	    
	String methodName = "dbUpdateGlobalFiles(byte)";
	    
	// check input
	if (!ArchiveManager.isValidAction(archiveAction)) {
	    Informer.getInstance().fail(
					this,
					methodName,
					"Illegal argument",
					"archiveAction: " + archiveAction,
					new IllegalArgumentException());
	}	    	    
	    	    	    
	int rowsAffected = 0;
	    
	switch (archiveAction) {
        case ArchiveManager.ACTION_INGEST:
            
            TransactionConnection tcon = DBConnection.getSharedConnection();
            
            // get all global files used by this package
            Enumeration e = this.getGlobalFiles().elements();
	    while (e.hasMoreElements()) {
		GlobalFile gf = (GlobalFile) e.nextElement();
    	        String tbl = ArchiveDatabase.TABLE_INT_ENTITY_GLOBAL_FILE;

    	        DBConnection.NameValueVector colVals = new DBConnection.NameValueVector();
    	        // set the IEID value
    	        colVals.addElement(ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_IEID, this.getIntEnt().getOid());
    	        // set the DFID value for the global file being used by this package
    	        colVals.addElement(ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_DFID, gf.getOid());
    	        // issue the insert
    	        rowsAffected = rowsAffected + tcon.insert(tbl, colVals);
	    }
            
            
            break;

        default:
            Informer.getInstance().fail(this,
					methodName,
					"Unimplemented archive action",
					"archiveAction: " + archiveAction,
					new FatalException());
            break;
        }
	    
	    
	return rowsAffected;
    }
	    
    
    /**
     * Deletes an InformationPackage from <code>fromPath</code>.
     * 
     * @param fromPath Absolute path to a directory of files.
     * @throws FatalException
     * @throws PackageException
     */
    public void delete(String fromPath)
	throws FatalException, PackageException {
	// check argument
	File f = new File(fromPath);
	if (!f.exists()) {
	    Informer.getInstance().fail(
					this,
					"delete(String)",
					"Path to delete does not exist.",
					fromPath,
					new IllegalArgumentException(
								     fromPath + " is not an existing file or directory."));
	}

	try {
	    if (!FileUtil.delete(fromPath)) {
		// delete failed somewhere
		Informer.getInstance().error(
					     this,
					     "delete(String)",
					     "Delete unsuccessful.",
					     fromPath,
					     new IOException(
							     "Unable to delete file or directory: " + fromPath));
	    }
	} catch (IOException ioe) {
	    // delete received an illegal path argument,
	    // should never happen since the path has already been
	    // checked
	    Informer.getInstance().fail(
					this,
					"delete(String)",
					"Error while trying to delete " + fromPath,
					fromPath,
					ioe);
	}
		
	// clean up
	f = null;
    }

    /**
     * Looks in the packagePath and its subdirectories for a descriptor file that was 
     * created by the archive. Presence of a previous archive-generated descriptor implies
     * that this package has already been ingested. If so, this SIP is also an existing
     * AIP.
     * The file name should be named AIP_[timestamp]_LOC.xml. If it is found the method
     * calls the METSDescriptor(packagePath, filePath) constructor and sets
     * hasArchiveDescriptor to true, and returns the Descriptor. If it doesn't
     * find it it returns null.
     *
     * @return METSDescriptor
     * @throws FatalException
     * @throws PackageException
     */
    protected METSDescriptor findArchiveDescriptor() throws FatalException, PackageException {
        Vector <String> files = null;
        try {
            files = FileUtil.scanForFiles(this.getPackagePath(), 
					  Pattern.compile(ARCHIVE_DESC_PATTERN), true);
        } catch (IOException ioe) {
            // if an IOException was returned, then the directory
            // does not exist or is unreadable. Since these attribures
            // have already been checked in validatePackage(), then
            // something serious has gone wrong.
            Informer.getInstance().fail(
					this,
					"findArchiveDescriptor()",
					"Unable to read directory",
					"packageDirectory: " + getPackagePath(),
					ioe);
        }    
        
        // search through the collection of returned files and
        // select the one directly in the package directory. If there
        // is none then select the one with the shortest path 
        // (this will be the tie-breaker)
        String descPath = null;
        METSDescriptor mDesc = null;
        
        for (int i = 0; i < files.size(); i++) {
            File fParent = new File(files.elementAt(i)).getParentFile(); 
            File fPackage = new File(this.getWorkingPath()); 
            
            if (fParent.getAbsolutePath().equals(fPackage.getAbsolutePath())) {
                // this is the archive-created descriptor in the package directory
                // note that the "package" directory in this case refers to package + IEID 
                descPath = files.elementAt(i);
                break;
            }
            else if (descPath == null 
		     || new StringTokenizer(files.elementAt(i), File.separator).countTokens() <
		     new StringTokenizer(descPath, File.separator).countTokens()) {
                descPath = files.elementAt(i);
            }                                        
        }        
        
        if (descPath != null) {
            mDesc = new METSDescriptor(descPath, this);                   
            this.hasArchiveDescriptor = true;
            // set this package's initial descriptor
            this.setArchiveDescriptor(mDesc);
            this.setPackageArchived(true);           
        }
        // return the descriptor
        return mDesc;
    } // end findInitialDescriptor        

    /**
     * Looks in the packagePath and its subdirectories for a descriptor file.
     * The file name should be named &quot;packageName&quot;+.xml. If it finds it it
     * calls the METSDescriptor(packagePath, filePath) constructor and sets
     * hasInitialDescriptor to true, and returns the Descriptor. If it doesn't
     * find it it returns null.
     *
     * @return METSDescriptor
     * @throws FatalException
     * @throws PackageException
     */
    protected METSDescriptor findInitialDescriptor() throws FatalException, PackageException {        
        Vector files = null;
    	String expectedDescName = getPackageName() + ".xml";
    	try {
	    files = FileUtil.findFile(expectedDescName, getPackagePath(), true);
    	} catch (IOException ioe) {
	    // if an IOException was returned, then the directory
	    // does not exist or is unreadable. Since these attribures
	    // have already been checked in validatePackage(), then
	    // something serious has gone wrong.
	    Informer.getInstance().fail(
					this,
					"findInitialDescriptor()",
					"Unable to read directory",
					"packageDirectory: " + getPackagePath(),
					ioe);
    	}
    
    	// search through the collection of returned files and
    	// select the one directly in the package directory. If there
    	// is none then select the one with the shortest path 
    	// (this will be the tie-breaker)
    	String descPath = null;
    	for (int i = 0; i < files.size(); i++) {
    		
	    if (files.elementAt(i).toString()
		.equals(getPackagePath() + expectedDescName)) {
		// this is the descriptor directly in the packageDirectory.
		// stop looking
		descPath = (String) files.elementAt(i);
		break;
	    }
    		
	    if (descPath == null
		|| files.elementAt(i).toString().length()
		< descPath.length()) {
		// either the descName hasn't been set, or a
		// file with a shorter path was found
		descPath = (String) files.elementAt(i);
	    }
		
    	}
    
    	METSDescriptor mDesc = null;
    	if (descPath != null) {
	    mDesc = new METSDescriptor(descPath, this);
	    hasInitialDescriptor = true;
	    // set this package's intitial descriptor
	    this.setInitialDescriptor(mDesc);
    	}
    	else {
	    // throw a NoIntitialDescriptorException
	    Informer.getInstance().error(this, 
					 "findInitialDescriptor()",
					 "No initial descriptor found",
					 this.getPackageName(),
					 new NoInitialDescriptorException("No initial descriptor found for " + this.getPackageName()));			
    	}
    	// return the descriptor
    	return mDesc;
    } // end findInitialDescriptor        

    /**
     * 
     * @return METSDescriptor
     */
    public METSDescriptor getArchiveDescriptor() {
        return archiveDescriptor;
    }

    /**
     * A Hashtable of DataFile objects contained in this InformationPackage. After
     * the ContentObjectFactory has created all DataFiles for this package
     * (i.e. the IP has reached the end of the submitted files), a call to
     * this.dfl = cof.getDataFiles will return a Vector of DataFiles and set
     * the member dfl. Later the dfl will be used to create the SIPDescriptor.
     * 
     * @return Hashtable
     */
    public Hashtable <String, DataFile> getDfl() {
	return dfl;
    } // end getDfl
	
    /**
     * Method to access contents of the globalFiles Hashtable. 
     * 
     * @param absPath an absolute path to a physical file
     * @return a GlobalFile object that has <code>absPath</code> as the key, or NULL if absPath is not an
     * existing key.
     */
    public GlobalFile getGlobalFile(String absPath) {
	return globalFiles.get(absPath);
    }

    /**
     * @return Hashtable of absPath->GlobalFile
     */
    public Hashtable<String, GlobalFile> getGlobalFiles() {
	return globalFiles;
    }

    /**
     * @return Hashtable
     */
    public Hashtable<String, GlobalFile> getGlobalNameLookup() {
	return globalNameLookup;
    }     

    /**
     * Represents the metadata file that came with this package when it was
     * ingested.
     * 
     * @return METSDescriptor
     */
    public METSDescriptor getInitialDescriptor() {
    	return initialDescriptor;
    } // end getInitialDescriptor        

    /**
     * @return IntEntity
     */
    public IntEntity getIntEnt() {
	return intEnt;
    }

    /**
     * @return 	the absolute path to the directory containing files
     * 				downloaded from the Internet by DAITSS related
     * 				to any files in the same package as this object
     */
    public String getLinksDir() {
	return this.linksDir;
    }

    /**
     * @return	the absolute path to the directory containing
     * 				migrated versions of files in the same package as
     * 				this object
     */
    public String getMigratedDir() {
	return this.migratedDir;
    }

    /**
     * @return	the absolute path to the directory containing
     * 				normalized versions of files in the same package as
     * 				this object
     */
    public String getNormalizedDir() {
	return this.normalizedDir;
    }   

    /**
     * Unqualified package name.
     * 
     * @return the InformationPackage's pacakge name
     */
    public String getPackageName() {
	return packageName;
    } // end getPackageName        

    /**
     * Absolute path to this package including package name before the package
     * was processed. Does not included the internal IEID directory.
     * Ex: &quot;/daitss/data/ingest/in/UFE0000001/&quot;.
     * 
     * @return String
     */
    public String getPackagePath() {
	return packagePath;
    } // end getPackagePath        

    /**
     * @return the InformationPackage's state
     */
    public byte getState() {
	return state;
    }

    /**
     * <p>
     * Absolute path to this package including package name and 
     * the internal IEID directory. 
     * Ex: &quot;/daitss/data/ingest/in/UFE0000001/E20050101_123456&quot;.
     * </p>
     * @return String 
     */
    public String getWorkingPath() {
	return workingPath;
    } // end getWorkPath        
	
    /**
     * Returns true if the DataFile <code>df</code> has already been entered into the 
     * globalFiles Hashtable.
     * @param df
     * @return boolean
     */
    public boolean hasGlobalFile(DataFile df) {
	return globalFiles.containsValue(df);
    }
		
    /**
     * Returns true if the files represented by <code>absPath</code> has already been entered into the 
     * globalFiles Hashtable.
     * @param absPath
     * @return boolean
     */
    public boolean hasGlobalFile(String absPath){
	return globalFiles.containsKey(absPath);
    }
	
    /**
     * Transactionally moves a package and its contents from <code>fromPath</code> to 
     * <code>toPath</code> using this.copy(fromPath, toPath) and then
     * this.delete(fromPath).  Keep the directory
     * structure for the package and its contents the same as it was before
     * moving it. If the move is successful return <code>true</code>, else return
     * <code>false</code>.
     *
     * @param toPath
     * @throws FatalException
     * @throws PackageException
     * @return boolean true if move was complete and successful
     */
    public boolean move(String toPath)
	throws FatalException, PackageException {
	String methodName = "move(String)";
		
	// get a snapshot (copy) of the dfl (the real dfl will be altered later)
	Hashtable <String, DataFile> ht = new Hashtable<String, DataFile>(DataFileFactory.getInstance().getDflAbsPath());
	Enumeration e = ht.elements();
	while(e.hasMoreElements()) {
	    // get the next data file
	    DataFile df = (DataFile) e.nextElement();

	    // the first condition is for moving SIPs, the second is for moving GFPs		    
	    if (((this instanceof SIP || this instanceof AIP) && !df.isGlobal() && !df.getRole().equals(FileRole.ROLE_DESCRIPTOR_GFP))
		|| (this instanceof GlobalFilePackage && (df.isGlobal() || df.getRole().equals(FileRole.ROLE_DESCRIPTOR_GFP)))) {		            
	    	// get the DataFile's current path
			String src = df.getPath();
			// determine the destination path
			String dest = toPath;
			if (!dest.endsWith(File.separator)) {
			    dest = dest + File.separator;
			}

			dest = dest + src.substring(getPackagePath().length());
						
			// create destination directories
			File d = new File(dest);
			d.getParentFile().mkdirs();
						
			try {
			    // copy the file
			    FileUtil.copyFile(src, dest);
			    // update references in the real dfl
			    // the file will be re-added so do not remove references 
			    DataFile realDf = DataFileFactory.getInstance().removeDfByAbsPath(src, false);
			    realDf.setPath(dest);
			    // get the DataFile's primary checksum value
			    String checksum = realDf.getMesgDigestValue(ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"));
			    DataFileFactory.getInstance().addDfToDfl(dest, checksum, realDf);
	
			} catch (IOException e1) {
			    Informer.getInstance().error(
							 this,
							 methodName,
							 "Error copying file",
							 "from: " + src + " to: " + dest,
							 e1);
			}
					
	    } else {
			if (!(this instanceof SIP) && !(this instanceof GlobalFilePackage) && !(this instanceof AIP)) {
			    Informer.getInstance().fail(this,
							methodName,
							"move not implemented for InformationPackage type",
							this.getClass().getName(),
							new FatalException());
			}		       
	    }		    
	}
		
	// update the package path
	this.setPackagePath(toPath);		
		
	// if we made it here, then the copy and the delete were successful
	return true;
    } // end move        
	
	
    /**
     * Convenience method to print the contents of the 
     * DataFile list as a String
     *
     */
    public  void printDfl() {
	System.out.println(">>>>>>>>>>>>>>>>>>>>>IP DFL<<<<<<<<<<<<<<<<<<<");

	if (dfl == null) {
	    System.out.println("dfl is null");
	} else if (dfl.isEmpty()) {
	    System.out.println("dfl is empty");
	} else {
	    System.out.println("Files in dfl: " + dfl.size());
	    Enumeration e = this.dfl.elements();
	    int dfCount = 0;
	    while (e.hasMoreElements()) {
		DataFile df = (DataFile) e.nextElement();
		dfCount++;
		System.out.println(">>>>>>>>>DATA FILE " + dfCount + "<<<<<<<<<<\n");
		System.out.println(df);
	    }

	}
    }

    /**
     * Convenience method to print the contents of the 
     * globalFiles Hashtable as a String
     *
     */
    public void printGlobalFiles() {
	System.out.println(">>>>>>>>>>>>>>>>>>>>>IP GlobalFiles<<<<<<<<<<<<<<<<<<<");
	if (getGlobalFiles() == null) {
	    System.out.println("globalFiles is null");
	}
	else if (getGlobalFiles().size() == 0 ) {
	    System.out.println("globalFiles is empty");
	}
	else {
	    System.out.println("Global files in globalFiles: " + getGlobalFiles().size());
	    Enumeration e = getGlobalFiles().elements();
	    while (e.hasMoreElements()) {
		GlobalFile gf= (GlobalFile) e.nextElement();
		System.out.println(gf.toString());
	    }
	}
    }

    /**
     * Convenience method to print the contents of the 
     * globalNameLookup  Hashtable as a String
     */
    public void printGlobalNameLookup() {
	System.out.println(">>>>>>>>>>>>>>>>>>>>>IP globalNameLookup<<<<<<<<<<<<<<<<<<<");
	if (getGlobalNameLookup() == null) {
	    System.out.println("globalNameLookup is null");
	}
	else if (getGlobalNameLookup().size() == 0 ) {
	    System.out.println("globalNameLookup is empty");
	}
	else {
	    System.out.println("Global files in globalNameLookup: " + getGlobalNameLookup().size());
	    Enumeration e = getGlobalNameLookup().elements();
	    while (e.hasMoreElements()) {
		GlobalFile gf= (GlobalFile) e.nextElement();
		System.out.println(">>>>>>>>>GLOBAL FILE<<<<<<<<<<\n" + gf);
	    }
	}		
    }

    /**
     * Rejects a package by calling IP.move(REJECT_DIR) and logging the errors.
     * TBD where the REJECT_DIR is stored. Logs something.
     *
     * @throws FatalException
     */
    public abstract void reject() throws FatalException;


    /**
     * Removes a global files from the globalFiles collection
     * 
     * @param absPath
     * @return GlobalFile
     */
    public GlobalFile removeGlobalFile(String absPath) {
        return this.globalFiles.remove(absPath);        
    }     
    
    
    /**
     * Removes references to <code>globaFile</code> from all
     * global file storage collections in a SIP, GlobalFilePackage,
     * and GlobalFiles singleton.
     * 
     * @param globalFile
     * @param gfp
     * @param s
     * @throws FatalException
     */
    protected void removeGlobalReferences(DataFile globalFile, 
					  GlobalFilePackage gfp, SIP s) throws FatalException {
        // 3) remove the gfp desc from the collection of all GlobalFiles                
        GlobalFiles.getInstance().removeGlobaFile(
						  globalFile.getMesgDigestValue(
										ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1")));
        // 4) remove the gfp desc from gfp.newGlobalFiles
        gfp.removeNewGlobalFile(globalFile.getPath());

        // 5) remove the gfp desc from the gfp's collection of GlobalFiles
        gfp.removeGlobalFile(globalFile.getPath());                
        
        // 6) remove the gfp desc from the sip's collection of GlobalFiles
        s.removeGlobalFile(globalFile.getPath());
        
    }

    /**
     * 
     * @param archvieDescriptor
     */
    public void setArchiveDescriptor(METSDescriptor archvieDescriptor) {
        this.archiveDescriptor = archvieDescriptor;
    }
    
    /**
     * A Hashtable of DataFile objects contained in this InformationPackage. After
     * the DataFileFactory has created all DataFiles for this package
     * (i.e. the IP has reached the end of the submitted files), a call to
     * this.dfl = cof.getDataFiles will return a Vector of DataFiles and set
     * the member dfl. Later the dfl will be used to create the SIPDescriptor.
     * 
     * @param _dfl
     */
    public void setDfl(Hashtable<String, DataFile> _dfl) {
	dfl = _dfl;
    } // end setDfl       

    /**
     * Represents the metadata file that came with this package when it was
     * ingested.
     * 
     * @param _initialDescriptor
     */
    public void setInitialDescriptor(METSDescriptor _initialDescriptor) {
    	initialDescriptor = _initialDescriptor;
    } // end setInitialDescriptor        

    /**
     * @param entity
     */
    protected void setIntEnt(IntEntity entity) {
	intEnt = entity;
    }

    /**
     * Sets the absolute path to the directory for storing files that
     * are downloaded as a result of retrieving links for any file in this
     * package. Do include the final slash after the directory.
     * 
     * @param	_linksDir	an absolute path to a directory (with a trailing '/')
     */
    public void setLinksDir(String _linksDir) {
	FileUtil.isGoodDir(_linksDir);
	if (!_linksDir.endsWith(File.separator)) {
	    linksDir = _linksDir + File.separator;
	} else {
	    linksDir = _linksDir;
	}
    }

    /**
     * Sets the absolute path to the directory for storing files that
     * are created during migration for any file in this package. 
     * Do include the final slash after the directory.
     * 
     * @param	_migratedDir	an absolute path to a directory (with a trailing '/')
     */
    public void setMigratedDir(String _migratedDir) {
	FileUtil.isGoodDir(_migratedDir);
	if (!_migratedDir.endsWith(File.separator)) {
	    migratedDir = _migratedDir + File.separator;
	} else {
	    migratedDir = _migratedDir;
	}
    }

    /**
     * Sets the absolute path to the directory for storing files that
     * are created during normalizationfor any file in this package. 
     * Do include the final slash after the directory.
     * 
     * @param	_normalizedDir	an absolute path to a directory (with a trailing '/')
     */
    public void setNormalizedDir(String _normalizedDir) {
	FileUtil.isGoodDir(_normalizedDir);
	if (!_normalizedDir.endsWith(File.separator)) {
	    normalizedDir = _normalizedDir + File.separator;
	} else {
	    normalizedDir = _normalizedDir;
	}

    } // end updateHighKey              

    /**
     * Unqualified package name.
     * 
     * @param packageName
     * @throws FatalException
     */
    public void setPackageName(String packageName) throws FatalException {
	String methodName = "setPackageName(String)";
	if (!isValidPackageName(packageName)) {
	    Informer.getInstance().fail(
					this,
					methodName,
					"Illegal argument",
					"packageName: " + packageName,
					new IllegalArgumentException());
	}
	this.packageName = packageName;
    } // end setPackageName        

    /**
     * Absolute path to this package including package name before the package
     * was processed. Ex: &quot;/DLArch2/ready/UFE0000001/&quot;.
     * 
     * @param _packagePath
     */
    public void setPackagePath(String _packagePath) {
	if (!_packagePath.endsWith(File.separator)) {
	    packagePath = _packagePath + File.separator;
	} else {
	    packagePath = _packagePath;
	}
    } // end setPackagePath        

    /**
     * Sets the <code>state</code> member of this InformationPackage.
     * @param b the value to apply to <code>state</code>
     * @throws FatalException if <code>b</code> is not a valid state.
     */
    protected void setState(byte b) throws FatalException {
	String methodName = "setState(byte)";
	if (!isValidState(b)) {
	    Informer.getInstance().fail(
					this,
					methodName,
					"Illegal argument",
					"b: " + b,
					new IllegalArgumentException());
	}
	state = b;
    }

    /**
     * Represents ...
     * 
     * @param _workPath
     */
    public void setWorkingPath(String _workPath) {
    	if (! _workPath.endsWith(File.separator)) {
    		workingPath = _workPath + File.separator;
    	} else {
    		workingPath = _workPath;
    	}
    }
	    
    /**
	 * TODO punting the SilentPackageException handling
     * Write this package to storage.
     * @throws FatalException
     * @throws PackageException
     */
    public void store() throws PackageException, FatalException {
    	File outDir = new File(ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH"));

    	File ipDir = new File(outDir, getIntEnt().getOid());
    	
    	// delete old copies 
		if (this instanceof AIP) {
			AIP a = (AIP)this;
			if (a.copies.size() > 0) {
				a.deleteOldCopies();	
			} else {
				Informer.getInstance().fail("No active silos available", a.getIntEnt().getOid());
			}
		}
    	storeAsCopies(ipDir, getCopies(), this);
		
		GlobalFilePackage gfp = DataFileFactory.getInstance().getGfp();
    	if (gfp != null) {
    		File gfpDir = new File(outDir, DataFileFactory.getInstance().getGfp().getIntEnt().getOid());
    		if (gfpDir.isDirectory()) {
    			storeAsCopies(gfpDir, getGfpCopies(), DataFileFactory.getInstance().getGfp());
    		}
    	}
    }


	private List<Copy> getGfpCopies() throws FatalException {
    	return DataFileFactory.getInstance().getGfp().getCopies();
	}
    
	public String getCopyMD5() {
		return copyMD5;
	}

	public void setCopyMD5(String md5) {
		copyMD5 = md5;
	}

	private void storeAsCopies(File dir, List<Copy> copies, InformationPackage ip) throws FatalException, PackageException {
		
        try {
        	File tarball = tarUpDir(dir);
            for(Copy copy : copies){       
            	
            	// AIP && exists - replace
            	// AIP && not exists - put
            	// AIP && 
            	// remove the existing copy
            	if (this instanceof AIP && copy.exists()) {
                	Informer.getInstance().info("Deleting existing copy at " + copy.url(), dir.getPath());
            		copy.delete();
            		Informer.getInstance().info("Existing copy " + copy.url() + " deleted", dir.getPath());
            	}
            	
            	Informer.getInstance().info("Putting tarball at " + copy.url(), dir.getPath());
            	String md5 = MessageDigestCalculator.md5(new BufferedInputStream(new FileInputStream(tarball)));
            	ip.setCopyMD5(md5);
                copy.put(tarball, "application/tar");
            	Informer.getInstance().info("Tarball stored at " + copy.url(), dir.getPath());
            }
            
            Informer.getInstance().info("Cleaning up tarbll of AIP", tarball.getPath());
            tarball.delete();
        } catch (IOException e) {
            Informer.getInstance().fail("problem storing aip (io)", "Storing Package", e);
        } catch (TarException e) {
            Informer.getInstance().fail("problem storing aip (tar)", "Storing Package", e);
        } catch (StorageException e) {
            Informer.getInstance().fail("problem storing aip (silo)", "Storing Package", e);
        }
    }

    private File tarUpDir(File dir) throws IOException, FatalException, TarException {
	File tarball = File.createTempFile("storage", ".tar");
	Informer.getInstance().info("Creating tarball of AIP", tarball.getPath());
	Tar tar = new Tar(tarball);
	tar.directory(dir.getParentFile());
	tar.create(new File(dir.getName()));
	Informer.getInstance().info("Tarball of AIP created", tarball.getPath());
	return tarball;
    }
	
    protected List<Copy> getCopies() {
        return copies;
    }

    protected String tarballName() {
        return String.format("%s", getIntEnt().getOid());
    }

    /**
     * Simply returns the class name of the specific type 
     * of InformationPackage.
     * 
     * @return String
     */
    public String toString() {
	return this.getClass().getName();
    }
	
    /**
     * @return Document
     * @throws FatalException
     */
    public Document toXML() throws FatalException {
	    
	/* document node */
	Document doc = XPaths.newDaitssDoc();
	    
	/* daitss:daitss element */ 
	Element rootElement = doc.getDocumentElement();
	    
	/* Namespace */
	String namespace = rootElement.getNamespaceURI();
	    
        /* INT_ENTITY_GLOBAL_FILES */        
        for (Enumeration e = this.getGlobalFiles().elements(); e.hasMoreElements();){
            GlobalFile gf = (GlobalFile) e.nextElement();
            
            String dfid = gf.getOid();
            String ieid = this.getIntEnt().getOid();
            
    	    Element iegfElement = (Element) rootElement.appendChild(doc.createElementNS(namespace, "INT_ENTITY_GLOBAL_FILE"));
    	    
    	    Element ieidElement = (Element) iegfElement.appendChild(doc.createElementNS(namespace, "IEID"));
    	    ieidElement.appendChild(doc.createTextNode(ieid != null ? ieid : ""));            			    
    	    
    	    Element dfidElement = (Element) iegfElement.appendChild(doc.createElementNS(namespace, "DFID"));
    	    dfidElement.appendChild(doc.createTextNode(dfid != null ? dfid : ""));
        }
        
        return doc;
    }

    /**
     * 
     * @return boolean
     */
    public boolean isPackageArchived() {
        return packageArchived;
    }
    
    /**
     * All InformationPackage subclasses need an ingest method
     * 
     * @throws FatalException
     * @throws PackageException
     * @throws IOException
     */
    public abstract void ingest() throws FatalException, PackageException, IOException; 
    
    
    /**
     * Sets the packageArchived flag to true.
     * 
     * @param packageArchived
     */
    public void setPackageArchived(boolean packageArchived) {
        this.packageArchived = packageArchived;
    }

    /**
     * Moves the package to the SIP output directory. Note that InformationPackage.move(...)
     * will update the package path to the new path.
     * 
     * @throws FatalException
     * @throws PackageException
     * @return true if the move was complete, else false
     */
    public boolean moveToOutputDir() throws FatalException, PackageException {
        String methodName = "moveToOutputDir()";
        String outputPath = ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH");
        Informer.getInstance().info(this,
				    methodName,
				    "Moving package to output directory: " + outputPath,
				    this.getPackageName(),
				    false);
    	return this.move(outputPath);	  
    }

    /**
     * Copies the package directory and its contents (currently at
     * this.packagePath) to the working directory by calling
     * this.copy(this.getPackagePath(),
     * ArchiveProperties.getinstance().getProperty(INGEST_WORKPATH) +
     * this.getPackageName() + &quot;/&quot;). Sets this.workPath to the new location of
     * the package. Ex: &quot;/darchive/ingest/work/UFE0000500/&quot;.
     * 
     * @throws PackageException
     * @throws FatalException
     */
    public void copyToWorkDir() throws FatalException, PackageException {       
    	Informer.getInstance().info(this,
				    "copyToWorkDir()",
				    "Copying package to work directory",
				    this.getPackageName(),
				    false);
        // store the future working path
        String workingDir = ArchiveProperties.getInstance().getArchProperty("INGEST_WORKPATH")
            + this.getPackageName() + File.separator + getIntEnt().getOid();
    	
        // store the working path only up to work/ directory
        String workingDirShort = ArchiveProperties.getInstance().getArchProperty("INGEST_WORKPATH");
        
        //Check to see if work directory contains another package. The work directory may only contain 
        //one or zero packages. If there is > 0 packages in the work directory, a fatal exception will be thrown.         
        	
        //get object for working directory
        File workingDirShortObject = new File(workingDirShort);
        
        //check to ensure that workingDir is in fact a directory
        if(workingDirShortObject.list() == null) {
	    Informer.getInstance().fail("InformationPackage", 
					"copyToWorkDir", 
					"Working directory: " + workingDirShort + " is not a directory or cannot be accessed.",
					"Directory: " + workingDirShort,
					new FatalException());	        	
        	
        }
        
        //check to ensure that there are no files in work directory.
    	if (workingDirShortObject.list().length > 0) {
	    Informer.getInstance().fail("InformationPackage", 
					"copyToWorkDir", 
					"Files exist in work directory prior to copy",
					"Directory: " + workingDirShort,
					new FatalException());	        	
    	}
            
        // copy the package
    	this.copy(workingDir);
        // RESET paths after copy is successful
        // reset the packagePath
        this.setPackagePath(ArchiveProperties.getInstance().getArchProperty("INGEST_WORKPATH")
			    + this.getPackageName());
    	// update the work path
    	this.setWorkingPath(workingDir);
    	// issue informational message
    	Informer.getInstance().info(this,
				    "copyToWorkDir()",
				    "Package copied to work directory",
				    this.getPackageName(),
				    false);
    } // end copyToWorkDir        
    
    
    /**
     * Validates this SIP as submitted. Checks:
     * - existence and read/write access permissions for the directory (else
     * throw a PathNotFoundException)
     * 
     *
     * @throws FatalException
     * @throws PackageException
     */
    public void validatePackage() throws FatalException, PackageException {
        File f = new File(this.getPackagePath());
        // make sure package exists
        if (!f.exists()) {
            // the package directory doesn't exist, most likely programmer error
            Informer.getInstance().fail(
					this,
					"validatePackage()",
					"Package is invalid (path does not exist).",
					this.getPackagePath(),
					new IllegalArgumentException(
								     "Directory " + this.getPackagePath() + " does not exist."));
        }
        // make sure path is a directory
        if (!f.isDirectory()) {
            // the path provided is not to a directory, most likely programmer error
            Informer.getInstance().fail(
					this,
					"validatePackage()",
					"Package is invalid (path is not a directory).",
					this.getPackagePath(),
					new IllegalArgumentException(
								     this.getPackagePath() + " is not a directory."));
        }
        // make sure package is readable
        if (!f.canRead()) {
            // the path provided is not readable
            Informer.getInstance().error(
					 this,
					 "validatePackage()",
					 "Package is invalid (package directory is unreadable).",
					 this.getPackagePath(),
					 new InvalidPackageException(this.getPackagePath() + " is not readable."));
        }
        // make sure package is writable
        if (!f.canWrite()) {
            // the path provided is not writable
            Informer.getInstance().error(
					 this,
					 "validatePackage()",
					 "Package is invalid (package directory is unwritable).",
					 this.getPackagePath(),
					 new InvalidPackageException(this.getPackagePath() + " is not writable."));
        }

        // update this sip's state
        this.setState(InformationPackage.SIP_VALIDATED);
    } // end validatePackage
    
    
    /**
     * For each DataFile in dfl, if (DataFile.getPreservationLevel() == 2) call
     * DataFile.migrate(). After this has been done for each DataFile in the
     * dfl, call this.setDfl(dff.getDfl()).
     * 
     * @throws FatalException
     * @throws PackageException
     */
    protected void migrate() throws PackageException, FatalException{
	    
	Hashtable<String, DataFile> dff = new Hashtable<String, DataFile>(DataFileFactory.getInstance(this).getDflChecksum());
	    
	Enumeration e = dff.elements();
		
	while (e.hasMoreElements()) {
	    DataFile df = (DataFile) e.nextElement();
	    df.migrate();
	}	   	    	
		
	this.setState(InformationPackage.SIP_MIGRATED);
    } // end migrate        

    /**
     * <p>
     * For each DataFile in dfl, if (DataFile.getPreservationLevel() == 2) call
     * DataFile.normalize(). After this has been done for each DataFile in the
     * dfl, set this.setDfl(dff.getDfl()).
     * </p>
     * 
     * @throws PackageException
     * @throws FatalException
     */
    protected void normalize() throws PackageException, FatalException {	
	    
	Hashtable <String, DataFile> dff = new Hashtable <String, DataFile> (DataFileFactory.getInstance(this).getDflChecksum());
	    
	Enumeration e = dff.elements();
		
	while (e.hasMoreElements()) {
	    DataFile df = (DataFile) e.nextElement();
	    df.normalize();
	}	   	    	
		
	this.setState(InformationPackage.SIP_NORMALIZED);
    } // end normalize        
	
    protected Vector createDataFilePrecedenceList(Enumeration e) {
	    
	this.dataFilePrecedenceList = new Vector<DataFile>();
		
	while (e.hasMoreElements()) {
	    DataFile df = (DataFile) e.nextElement();
	    addDataFileToPrecedenceList(df, false);
	}
		
	return this.dataFilePrecedenceList;
    }
	
	
    private void addDataFileToPrecedenceList(DataFile df, boolean isChild) {
	if (df instanceof GlobalFile) {
	    // if this is a pre-existing GlobalFile, it (and all of its links) has been 
	    // localized, no need to continue
	    return;
	}
	    
	if (this.dataFilePrecedenceList.contains(df)) {
	    if (!isChild) {
		// the parent data file and therfore all of its children have
		// already been added to the list	        
		return;
	    }

            // if this is a child, it should be removed (and then later
            // re-added) so that it will precede all of its parents
            this.dataFilePrecedenceList.remove(df);

	} 
	    
	if (isChild) {
	    // add the data file to the beginning of the list
	    this.dataFilePrecedenceList.add(0, df);
	}
	else {
	    // this is a parent, add it to the end of the list
	    this.dataFilePrecedenceList.add(df);
	}
	    	   	
	// get all the links for this DataFile
	Collection links = df.getLinkAliasTable().values();
	    
	// iterate through links and recursively add children before their parents
	if (links != null && links.size() > 0) {
	    Iterator iter = links.iterator();	
            while (iter.hasNext()) {
                DataFile linkDf = (DataFile)iter.next();
                // continue recursively so that all children are added before 
                // their parents
                addDataFileToPrecedenceList(linkDf, true);
            }
	}		    		    		   	    
    }

} // end InformationPackage
