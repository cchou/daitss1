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
/** Java class "DataFileFactory.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import metadata.identification.FFIdent;
import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.entity.Agreements;
import edu.fcla.daitss.entity.Event;
import edu.fcla.daitss.entity.Events;
import edu.fcla.daitss.entity.GlobalFilePackage;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.IntEntity;
import edu.fcla.daitss.entity.Relationships;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.global.GlobalFile;
import edu.fcla.daitss.file.global.GlobalFiles;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.miscfile.pdf.PdfLimitations;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.severe.element.Limitations;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.VirusChecker;
import edu.fcla.daitss.util.VirusFoundException;

/**
 * Creates DataFile objects representing particular formats of data files.
 */
public class DataFileFactory {
	/**
	 * Class name used for Informer calls from static methods.
	 */
	private static final String CLASSNAME = "edu.fcla.daitss.file.util.DataFileFactory";

	/**
	 * Stores the singleton instance.
	 */
	private static DataFileFactory instance = null;
    
	/**
	 * A utility method to add unique Objects from <code>arr</code> to 
	 * <code>v</code>
	 * @param v The Vector to which the objects in <code>arr</code> are added
	 * @param arr An array of objects that are to be added to <code>v</code>
	 * @throws FatalException
	 */
	private static void addUniqueElementsToVector(Vector v, Object[] arr) 
		throws FatalException {
		String methodName = "addUniqueElementsToVector(Vector,Object[])";
		// check input
		if (v == null) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Illegal argument",
				"v: " +v,
				new IllegalArgumentException());
		}
		// only add unique elements to the vector 
		if (arr != null) {			
			for (int i=0;i<arr.length;i++) {
				boolean alreadyPresent = false;
				Object o = arr[i];
				// see if the object is already in the Vector
				Iterator iter = v.iterator();
				while (iter.hasNext()) {
					if(o == (Object)iter.next()) {
						alreadyPresent = true;
						break;
					}
				}
				if (!alreadyPresent) {
					v.add(o);
				}
			}
		}
	}
		
	/**
	 * A convenience method to destroy the DataFileFactory since a new 
	 * factory is needed by each package.
	 */
	public static void die() {
	    if (instance != null) {
	        instance = null;
	    }	    
	}

	/**
	 * Method to return the singleton instance of the DataFileFactory.
	 * DataFileFactory.getInstance(InformationPackage) must be called before this method
	 * can be called. This is used primarily by DataFile objects that
	 * need to create other DataFile objects as part of link retrieval
	 * or for migration or normalization.
	 * 
	 * @return the singleton DataFileFactory instance
	 * @throws FatalException
	 */
	
	public static synchronized DataFileFactory getInstance() throws FatalException {
		String method = "getInstance()";
		if (instance == null) {
			Informer.getInstance().fail(CLASSNAME, method,  
				"Unable to create DataFileFactory. Some version of DataFileFactory.getInstance(InformationPackage)" +
				"must be called first.","DataFileFactory", new FatalException());				
		}		
		return instance;
	}
	
	/**
	 * Method to return the singleton instance of the DataFileFactory.
	 * This must be called before DataFileFactory.getInstance() can be
	 * called by a DataFile.
	 * @param ip 
	 * 
	 * @return the singleton DataFileFactory instance
	 * @throws FatalException 
	 */
	public static synchronized DataFileFactory getInstance(InformationPackage ip) throws FatalException {
		if (instance == null){
			instance = new DataFileFactory(ip);
		}
		return instance;		
	}
	
	/**
	 * Main method.
	 * @param args
	 * @throws PackageException 
	 * @throws FatalException 
	 */
	public static void main(String[] args) 
		throws PackageException, FatalException {
		
		//SIP s = new SIP("\\DLArch\\UF12345678\\");
		//IntEntity ie = new IntEntity();
		
		//DataFileFactory dff = DataFileFactory.getInstance(s);
			
		//String absPath = "C:\\Program Files\\eclipse\\workspace\\daitss\\ingest\\in\\UF00003061\\1.jpg";

		//ArchiveMessageDigest amd = MessageDigestCalculator.calcMessageDigest(
		//	absPath, ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"));
		//String checksum = amd.getValue();
		//System.out.println(checksum);


		//DataFile df = dff.generateNewDataFile(absPath);	
		//DataFile df = dff.createDataFile(absPath, ie, false);
		//System.out.println(df.toString());
	 	
	 	/*
		absPath = "C:\\Program Files\\eclipse\\workspace\\daitss\\ingest\\in\\UF00005200\\2.jpg";
		df = dff.generateNewDataFile(absPath);
		System.out.println(df.toString());
	
		absPath = "C:\\Program Files\\eclipse\\workspace\\daitss\\ingest\\in\\UF00005200\\1.tif";
		df = dff.generateNewDataFile(absPath);
		System.out.println(df.toString());
	
		absPath = "C:\\Program Files\\eclipse\\workspace\\daitss\\ingest\\in\\UF00005200\\10.tif";
		df = dff.generateNewDataFile(absPath);
		System.out.println(df.toString());
		
		absPath = "C:\\Program Files\\eclipse\\workspace\\daitss\\ingest\\in\\UF00005200\\10";
		df = dff.generateNewDataFile(absPath);
		System.out.println(df.toString());
		*/		
	}

	/**
	 * A HashTable of DataFile objects created by this DataFileFactory
	 * keyed by absolute path to the file.
	 */
	private Hashtable <String, DataFile> dflAbsPath = null;

	/**
	 * A HashTable of DataFile objects created by this DataFileFactory
	 * keyed by file checksum.
	 */
	private Hashtable <String, DataFile> dflChecksum = null;

	/**
	 * A hashtable containing partially-populated DataFile objects passed to
	 * the constructor.
	 */
	private Hashtable<String, METSDocument> dflLite = null;

    /**
     * FFIdent format identifier. Used when DAITSS parsers are unable to
     * determine format type.
     */
    private final FFIdent formatIdentifier;
	
	/**
	 * Absolute paths of files that should be global when created.
	 * These paths are specific to this package. These are used to
	 * indicate global files when they can't be determined by their
	 * file title (as stored in the configuration files) nor their
	 * parent (file linking to this file).
	 */
	private Vector futureGlobalPaths = null;
	
	/**
	 * Absolute paths of files that should be treated as created
	 * by the archive when created. 
	 * These paths are specific to this package. These are used to
	 * indicate files created by the archive when they can't be 
	 * determined otherwise. One situation where this happens is
	 * when a file is transformed (migrated/normalized/localized)
	 * into more than one file. The resulting container file
	 * can easily be treated as an archive-generated file, but
	 * the files that the container file links to can not.
	 * It also worth noting that not every file linked to by
	 * the container file will be added to this set - just those
	 * created by the archive. For example the container file will
	 * most likely link to schema files not created by the
	 * archive along with the new archive-created files.
	 */
	private Vector futureOriginArchivePaths = null;

	/**
	 * A GlobalFilePackage used to store new GlobalFile objects 
	 * once they are identified and created.
	 */
	private GlobalFilePackage gfp = null;

	
	private Vector importedGlobalFiles = null;

	/**
	 * IP member for which this DataFileFactory is working
	 */
	private InformationPackage ip = null;
	
	/**
	 * A HashTable of DataFile objects represented by links contained
	 * in files within this DataFileFactory's SIP. It is used to correlate
	 * links with DataFile objects. The key is a link alias and the value 
	 * is a DataFile object.
	 */
	private Hashtable uriNameLookup = null;

	/** 
     * Constructor. Deprecated (for now 2/3/2004).
     * @param dflLite
     * @throws FatalException
     */
	private DataFileFactory(Hashtable dflLite) throws FatalException {
		setDflLite(dflLite);
		// initialize the dfls
		this.dflAbsPath = new Hashtable <String, DataFile> ();
		this.dflChecksum = new Hashtable <String, DataFile> ();
		// iniitialize the uriNameLookup
		this.uriNameLookup = new Hashtable();
		// initialize the importedGlobalFiles Vector
		this.importedGlobalFiles = new Vector();
        // initialize the FFIdent member
        try {
            this.formatIdentifier = new FFIdent(
                    new File(ArchiveProperties.getInstance()
                            .getArchProperty("FFIDENT_CONFIG_PATH")));
        } catch (IllegalArgumentException e) {
            throw new FatalException("Path to FFIdent configuration file is null");
        } catch (FileNotFoundException e) {
            throw new FatalException("Path to FFIdent configuration file does not exist");
        } catch (IOException e) {
            throw new FatalException("Error reading FFIdent configuration file");
        }
        
		// initialize static classes used by the DataFileFactory
		MimeMediaType.init();
		Extension.init();
		
	} // end DataFileFactory        

	/**
	 * Constructor for SIPs. Assumes that the sip has
	 * already extracted content from its initialDescriptor
	 * @param ip 
	 * @throws FatalException 
	 */
	private DataFileFactory(InformationPackage ip) throws FatalException{
		this.setIp(ip);
		// initialize the set of package-specific global file paths
		this.futureGlobalPaths = new Vector();
		// initialize the set of package-specific archive-created paths
		this.futureOriginArchivePaths = new Vector();
		// initialize the dfls
		this.dflAbsPath = new Hashtable<String, DataFile>();
		this.dflChecksum = new Hashtable<String, DataFile>();
		// iniitialize the uriNameLookup
		this.uriNameLookup = new Hashtable();
		// initialize DflLite
		this.dflLite = new Hashtable<String, METSDocument>();
		// initialize the importedGlobalFiles Vector
		this.importedGlobalFiles = new Vector();
        // initialize the FFIdent member
        try {
            this.formatIdentifier = new FFIdent(
                    new File(ArchiveProperties.getInstance()
                            .getArchProperty("FFIDENT_CONFIG_PATH")));
        } catch (IllegalArgumentException e) {
            throw new FatalException("Path to FFIdent configuration file is null");
        } catch (FileNotFoundException e) {
            throw new FatalException("Path to FFIdent configuration file does not exist");
        } catch (IOException e) {
            throw new FatalException("Error reading FFIdent configuration file");
        }
		// initialize static classes used by the DataFileFactory
		MimeMediaType.init();
		Extension.init();
	}

	/**
	 * Method to add DataFile objects to both dfls. This should be used very carelfully 
	 * outside of this class.
	 * @param absPath 
	 * @param checksum 
	 * 
	 * @param df
	 * @throws FatalException 
	 */
	public void addDfToDfl(String absPath, String checksum, DataFile df)
		throws FatalException {

		String methodName = "addDfToDfl(String,String,DataFile)";
		// if there is already an object stored for this key,
		// this method will overwrite it.
		
		// check fileName input
		if (absPath == null || absPath.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid fileName argument",
				"fileName: " + absPath,
				new IllegalArgumentException("String argument fileName is null or empty"));
		}
		// check checksum input
		if (checksum == null || checksum.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid fileName argument",
				"checksum: " + checksum,
				new IllegalArgumentException("String argument checksum is null or empty"));
		}
		// check df input
		if (df == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid df (DataFile) argument passed to DataFileFactory.addDataFile(String,DataFile)",
				"df: null",
				new IllegalArgumentException("DataFile argument is null"));
		}
		// put() will throw a NullPointerException if the key or object is 
		// null, but since input has already been checked, it is not
		// necessary to catch the Exception
		getDflAbsPath().put(absPath, df);
		getDflChecksum().put(checksum, df);
	} // end addDataFile        
	
	/**
	 * Method to add DataFile to uriNameLookup. This should be done for 
	 * all DataFile objects using DFID + "." + extension as well as for
	 * URIs found as links.
	 *  
	 * @param uri
	 * @param df
	 * @throws FatalException 
	 */
	public void addDfToUriNameLookup(String uri, DataFile df)
		throws FatalException {
		String methodName = "addDfToUriNameLookup(String,DataFile)";
		// if there is already an object stored for this key,
		// this method will overwrite it.

		// check uri input
		if (uri == null || uri.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid uri argument",
				"uri: " + uri,
				new IllegalArgumentException("String argument uri is null or empty"));
		}
		// check df input
		if (df == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Invalid df (DataFile) argument",
				"df: null",
				new IllegalArgumentException("DataFile argument is null"));
		}
		// put() will throw a NullPointerException if the key or object is 
		// null, but since input has already been checked, it is not
		// necessary to catch the Exception
		this.getUriNameLookup().put(uri, df);
	} // end addDataFile        
	
	/**
	 * Add a path to the set of all package-specific paths that should
	 * be treated as global files.
	 * 
	 * @param path absolute path to file
	 */
	public void addFutureGlobalPath(String path) {
	    if (path != null && !"".equals(path) &&
	            !futureGlobalPaths.contains(path)){
	        futureGlobalPaths.add(path);
	    }
	}
	
	/**
	 * Add a path to the set of all package-specific paths that should
	 * be treated as created by the archive.
	 * 
	 * @param path absolute path to file
	 */
	public void addFutureOriginArchivePath(String path) {
	    if (path != null && !"".equals(path) &&
	            !futureOriginArchivePaths.contains(path)){
	        futureOriginArchivePaths.add(path);
	    }
	}

	/**
	 * Checks to see if the file at <code>absPath</code> contains a virus. If a 
	 * virus is identified, a PackageException is generated, else the method returns
	 * quietly.
	 * 
	 * @param absPath absolute path to the file to check
	 * @throws PackageException
	 * @throws FatalException
	 */
	private void checkForVirus(String absPath) 
		throws PackageException, FatalException {
		String methodName = "checkForVirus(String)";
		// first, check the file for presence of virus
		if (VirusChecker.hasVirus(absPath)) {
			// informer call will throw PackageException and 
			// exit method
			Informer.getInstance().error(
				this,
				methodName,
				"Virus found",
				absPath,
				new VirusFoundException());
		}
	}

	/**
	 * Creates a DataFile object dfo from a physical file using the
	 * IntEntity and the relevant portion of the descriptor (the DataFile
	 * object stored in the dflLite member that was passed in the constructor)
	 * gotten by getLiteDataFile(fileName) if available. Pseudocode is in
	 * separate documentation.
	 * 
	 * Gets an instance of the Agreements class to get the
	 * preservation level.
	 * 
	 * @return a DataFile with ...
	 * @param parentDf the DataFile that is the parent of the file represented by absPath
	 * @param absPath the absolute path to a physical file
	 * @param isGlobal true if the file represented by filePath is known to be a global file
	 * @param origUri the URI of the file respresented by <code>absPath</code> if was 
	 * was downloaded from the internet   
	 * @throws FatalException
	 * @throws PackageException
	 */
	public DataFile createDataFile(
		DataFile parentDf,
		String absPath,
		boolean isGlobal,
		String origUri)
		throws PackageException, FatalException {
				    
		String methodName = "createDataFile(DataFile, String, boolean, String)";
		
		// check input
		if (absPath == null || absPath.trim().equals("")){
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"absPath: " + absPath,
				new IllegalArgumentException());			
		}
				
		// init the return value
		DataFile df = null;
		// init a GlobalFile in case one needs to be created
		GlobalFile gf = null;
		// initialize the newGlobalFile flag, if a new globalFile is
		// identified, this flag will be set to true
		boolean newGlobalFile = false;
		
		// calculate the checksum (using the primary Archive message digest 
		// algorithm) to be used later 
		ArchiveMessageDigest amd = MessageDigestCalculator.getMessageDigest(
			absPath, ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"),
			false);
		String checksum = amd.getValue();
		// check to see if this file (or a copy of it) has already been seen
		df = searchForDfInDfl(absPath, checksum);
		
		if (df == null) {
			// the file has not already been seen, now check to see if it's 
			// a recognized GlobalFile
			if (GlobalFiles.getInstance().isGlobalFile(checksum)) {
			    // NOTE: the following call will actually move the 
			    // newly-downloaded version of the GlobalFile to 
			    // the same relative path in to which the original
			    // copy was downloaded
				df = handleExistingGlobalFile(absPath, checksum);
				updateDFStorage(df, checksum, parentDf);
			}
			else {            
			    // this file hasn't been seen before, so it must be checked for viruses
			    // Since a DataFile object has not been created (and therefore no
                // origin has been set), we need to infer the origin.
                boolean checkedForVirus = false;
                
                if (!isFutureOriginArchivePath(absPath) 
                        || ArchiveProperties.getInstance()
                            .getArchProperty("VIRUS_CHECK_ORIGIN_ARCHIVE").equals("true")) {
                    checkForVirus(absPath);
                    // set flag so event can be added later
                    checkedForVirus = true;
                }
                // storage for the final path of the file - if it's a GlobalFile, the 
                // physical file will be moved to the GlobalFilePackage
                String finalPath = absPath;
                // its not an existing GlobalFile, check to see if it should
				// be a new GlobalFile based on file title (or by the isGlobal flag)
				if(GlobalFiles.getInstance().isGlobalName(			
					new File(finalPath).getName()) 
					|| isGlobal || (parentDf != null && parentDf.isGlobal()) 
					|| isFutureGlobalPath(finalPath)) {
					// create a new GlobalFile
					gf = generateNewGlobalFile(finalPath);
					// set the flag
					newGlobalFile = true;
                    // reset the final path
                    finalPath = gf.getPath();
                }
				
				// no matter what, create a new DataFile and 
				// set its members
				df = generateNewDataFile(finalPath);
				// set members, including origin
				setDfMembers(df, newGlobalFile, origUri, parentDf);				
																
				// if the preservation level is NONE, then delete the DataFile object
				// presLevel was set during setDfMembers(...)
				if (df.getPresLevel().equals(Agreements.PRES_LEV_NONE) && newGlobalFile == false) {
					// this is not a globalFile and its preservation level is none for this AP
					// log message that a DataFile with preservation level of NONE has been encountered
				    Informer.getInstance().info(
						this,
						methodName,
						"File format has preservation level of NONE. File will not be archived.",
						"finalPath: " + finalPath,
						false);
					// set the return DataFile to null					
					df = null;					
				}
				else {			
					// the file is global or
					// the preservation level is not NONE, the file should
					// be archived

					// this call must happen before links are retrieved in order to avert cycles in 
					// distributed objects
					updateDFStorage(df, checksum, parentDf);
					
					// if a new GlobalFile has been created, set its DFID
					if (gf != null) {
						gf.setOid(df.getOid());
					}
					// add a new Event to the Events singleton
                    if (checkedForVirus) {
                        Events.getInstance().add(
    							new Event(df.getOid(), 
    							        Event.EVENT_CHECKED_FOR_VIRUS, 
    							        DateTimeUtil.now(),
    							        "Checked for virus during DataFile creation", 
    							        Event.OUTCOME_SUCCESS, 
    							        "", // note
    							        null)); // no related ID
                    }

					// extract metadata - will also retrieve links for df					
					df.extractMetadata();									
				}				
			}			
		}
		else {			
			// create the GlobalFilePackage if needed
			if (getGfp() == null) {
				// need to create the GlobalFilePackage
				GlobalFilePackage gfp = new GlobalFilePackage(getIp());
	            setGfp(gfp);
	            // make the GFP directory inside the IP's package dir, this makes 
	            // linking easier
	            new File(gfp.getWorkingPath()).mkdirs();            
			}
			    
	        // copy the file at absPath to the GFP directory.  This is required in order
			// for the parent distributed file to be validated successfully.
            String gfPackagePath = absPath.substring(getIp().getWorkingPath().length()); 
	            
	        String destPath = getGfp().getWorkingPath() + gfPackagePath;

	        // only copy the file if the destination path and original path are different        
	        if (!absPath.equals(destPath)) {
	            try {    
	                FileUtil.copyFile(absPath, destPath);
	            } catch (IOException e) {
	                Informer.getInstance().error(this,
	                        methodName,
	                        "Unable to copy global file to GlobalFilePackage",
	                        "destPath: " + destPath,
	                        new PackageException());
	            }
	        }
		    if (parentDf != null && parentDf.getOrigin().equals(DataFile.ORIG_ARCHIVE) && 
		    		(df.getCreateDate() == null || df.getCreateDate().equals(DataFile.DEFAULT_DATE))) {
		        df.setCreateDate(DateTimeUtil.now());
		    }
		}

		return df;
	} // end createDataFile        
	
	/**
	 * Creates a hashtable of DataFile Objects for all files 
	 * in an Information package 
	 * 
     * @param archiveAction
	 * @return Hashtable of DataFile objects
     * @throws FatalException
     * @throws PackageException
	 */
	public Hashtable<String, DataFile> createDataFiles(byte archiveAction)
		throws FatalException, PackageException {
		String methodName = "createDataFiles(byte)";
		// declaration for the return value
		Hashtable<String, DataFile> ht = null;
		// check input
		if (!ArchiveManager.isValidAction(archiveAction)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"archiveAction: " + archiveAction,
				new IllegalArgumentException("Invalid archive action"));			
		}
		
		switch (archiveAction) {
			case ArchiveManager.ACTION_INGEST :
				ht = createSipDataFiles();
				break;
			case ArchiveManager.ACTION_DISSEMINATE :
				Informer.getInstance().fail(
					this,
					methodName,
					"createDataFiles has not been implemented for ACTION_DISSEMINATE",
					ArchiveManager.getActionText(archiveAction),
					new IllegalArgumentException("createDataFiles has not been implemented for ACTION_DISSEMINATE"));
				break;
			default :
				Informer.getInstance().fail(
					this,
					methodName,
					"Unknown or unsupported archiveAction argument",
					ArchiveManager.getActionText(archiveAction),
					new IllegalArgumentException(
						"createDataFiles has not been implemented for "
							+ ArchiveManager.getActionText(archiveAction)));
				break;
		}
		return ht;
	}

	/**
	 * Creates a Hashtable of DataFile objects contained by a SIP.
	 * Assumes that the sip has already extracted content from its initialDescriptor.
	 * 
	 * @return Hashtable containing DataFile objects
	 * @throws FatalException
	 * @throws PackageException
	 */
	private Hashtable<String, DataFile> createSipDataFiles()
		throws FatalException, PackageException {
		// method name String for Informer calls			
		String methodName = "createSIPDataFiles()";
		// make sure the SIP is ready to be processed
		if (getIp() == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Data files cannot be created because the ip is null",
				getIp().getPackagePath(),
				new IllegalArgumentException("SIP is null, unable to create data files"));
		} 
		else if (
			getIp().getState()
				< InformationPackage.SIP_DESC_CONTENT_EXTRACTED) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Data files cannot be created until intitialDescriptor content has been extracted",
                getIp().getPackagePath(),
				new UnexpectedPackageStateException("Data files cannot be created until intitialDescriptor content has been extracted"));
		}

		// store the DflLite (remember the SIP must have already
		// extracted the content of its InitialDescriptor)
		// if (getIp() instanceof SIP)
		setDflLite(getIp().getInitialDescriptor().getDflLite());
		// get all file under ip.packagePath and create DataFile objects for each one.
		Vector files = new Vector();

		try {
			files =
				FileUtil.scanForFiles(getIp().getPackagePath(), "", true);
		} catch (IOException e) {
			// an error occurred while reading the directory contents
			Informer.getInstance().error(
				this,
				methodName,
				"Error encountered reading files in package",
				"getIp().getPackagePath(): " + getIp().getPackagePath(),
				e);
		}

		// create DataFile objects for every file in the package
		Iterator i = files.iterator();
		
		while (i.hasNext()) {
			// filePath represents the absolute path to the file
			String filePath = (String) i.next();
			// add the new datafile to the hashtable. if DataFile already
			// exists in hashtable (because it is shared by more than one
			// file) then the newest version will overwrite the previous 
			// version in the hashtable. All these data files were submitted
			// in the SIP, therefore none have linkUri's
			DataFile df = createDataFile(null, filePath, false, null);
		}
		return this.getDflAbsPath();
	}
	
	/**
	 * Creates a Hashtable of DataFile objects contained by a SIP.
	 * Assumes that the sip has already extracted content from its initialDescriptor.
	 * 
	 * @return Hashtable containing DataFile objects
	 * @throws FatalException
	 * @throws PackageException
	 */
	public Hashtable<String, DataFile> createAipDataFiles()
		throws FatalException, PackageException {
		// method name String for Informer calls			
		String methodName = "createAIPDataFiles()";
		// make sure the SIP is ready to be processed
		if (getIp() == null) {
			Informer.getInstance().fail(this, methodName,
				"Data files cannot be created because the ip is null",
				getIp().getPackagePath(),
				new IllegalArgumentException("AIP (SIP for re-ingest) is null, unable to create data files"));
		} 
		else if (getIp().getState()< InformationPackage.SIP_DESC_CONTENT_EXTRACTED) {
			Informer.getInstance().fail(this, methodName,
				"Data files cannot be created until intitialDescriptor content has been extracted",
                getIp().getPackagePath(),
				new UnexpectedPackageStateException("Data files cannot be created until initialDescriptor content has been extracted"));
		}

		// store the DflLite (remember the AIP must have already
		// extracted the content of its InitialDescriptor)
		setDflLite(getIp().getInitialDescriptor().getDflLite());
		// get all file under ip.packagePath and create DataFile objects for each one.
		Vector files = new Vector();
		Vector subDirs = new Vector();

		try {
			files = FileUtil.scanForFiles(getIp().getPackagePath(), "", true);
			subDirs = FileUtil.getSubDirectories(getIp().getPackagePath());
		} catch (IOException e) {
			// an error occurred while reading the directory contents
			Informer.getInstance().error(this, methodName,
				"Error reading files in package", "getIp().getPackagePath(): " 
				+ getIp().getPackagePath(),e);
		}

		// create DataFile objects for every file in the package
		Iterator i = files.iterator();
		String ieid = "";
		
		while (i.hasNext()) {
			// filePath represents the absolute path to the file
			String filePath = (String) i.next();
			// find the ieid for this file
			Iterator dirItr = subDirs.iterator();
			boolean found = false;
			while (dirItr.hasNext() && !found) {
				String dirName = (String)dirItr.next();
				if (filePath.contains(dirName)) {
					found = true;
					ieid = dirName;
				}
			}
			// create the new datafile 
			DataFile df = generateNewDataFile(filePath);
			df.setHasArchived(true);
			df.setIeid(ieid);	
			//restored members from db.
			df.readFromDB();		
			// get the ap code
			int apId = getApId(getIp().getIntEnt(), df.isGlobal());
			// set the ap code
			df.setAp(apId);
			// calculate the checksum (using the primary Archive message digest algorithm)
			ArchiveMessageDigest amd = MessageDigestCalculator.getMessageDigest(
				filePath, ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"),false);
			String checksum = amd.getValue();
			// add it to DataFileFactory
			updateDFStorage(df, checksum, null);
		}
		
	    Enumeration e = this.getDflAbsPath().elements();
	    while (e.hasMoreElements()) {            
	        DataFile df = (DataFile) e.nextElement();
			// only need to re-parse the non-global data files
			if (!df.isGlobal()) {
				// if the format for this data file has been changed since the last ingest/re-ingest,
				// we need to retrieve the links
				if (df.needRefresh())
					df.extractMetadata();
				else
					df.extractMetadataNoRetrieve();
			}
		}
		return this.getDflAbsPath();
	}
	
	/**
     * @param archiveAction
     * @return integer
     * @throws FatalException
     * @throws PackageException
	 */
	public int dbUpdate(byte archiveAction) throws FatalException, PackageException {
	    String methodName = "dbUpdate(byte)";
	    if (!ArchiveManager.isValidAction(archiveAction)) {
	        Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "archiveAction: " + archiveAction,
                    new IllegalArgumentException());
	    }
	    
	    int rowsAffected = 0;
	    Enumeration e = this.getDflAbsPath().elements();
	    while (e.hasMoreElements()) {            
	        DataFile df = (DataFile) e.nextElement();
            // Ingest of the DataFile is now complete. Create new ingest event for the DataFile.
            Events.getInstance().add(new Event(df.getOid(), Event.EVENT_INGESTED, DateTimeUtil.now(), 
            		"DataFile ingest",Event.OUTCOME_SUCCESS,"", null));                        
	        rowsAffected += df.dbUpdate(archiveAction);
	    }	    
	    
	    return rowsAffected;
	}
	
	/**
	 * Mehtod to create a new DataFile object (or subtype) based on 
	 * an input file. First the specific DataFile class or subclass
	 * is determined (by the getFileType(String) method and then
	 * an instance of that class is created and returned.
	 *  
	 * @param absPath an absolute path to an existing file
	 * @return a DataFile object for with which the file at
	 * <code>absPath</code> is compatible
	 * @throws FatalException
	 */
	private DataFile generateNewDataFile(String absPath) 
		throws FatalException {
			
		String methodName = "generateNewDataFile(String)";
		// check input
		if (absPath == null || absPath.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"absPath: " +absPath,
				new IllegalArgumentException());
		}		
		File f = new File(absPath);
		if (!(f.exists() && f.isFile())) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument, file does not exist or is not a file",
				"absPath: " +absPath,
				new IllegalArgumentException());
		}
		// cleanup
		f  = null;
		
		// check to see if there's a lite DataFile. If so, that
		// may help with the Class determination
		METSDocument liteDf = getLiteDataFile(absPath);
		// attempt to get the class for this file, if no compatible 
		// class is found, a PackageException will be generated by this
		// call
		Class c = getFileType(absPath, liteDf);

		// get the Constructor for the class
		// create the parm types to retrieve the right constructor	
		Class[] parms = {String.class, 
				InformationPackage.class,
				METSDocument.class};	
		// declare the constructor
		Constructor con = null;
		// dynamically access the constructor 
		try {
			con = c.getConstructor(parms);
		} 
		catch (SecurityException e) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Security exception encountered while retrieving constructor from class",
				c.getName(),
				e);
		} 
		catch (NoSuchMethodException e) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Expected constructor not found",
				c.getName(),
				e);
		}
		
		// if control made it here, then the constructor was found
		// create the arguments array
		Object[] initargs = {absPath, getIp(), liteDf};	
		// the DataFile object to be returned
		DataFile df = null;
		try {
			// try to instantiate a real object
			df = (DataFile) con.newInstance(initargs);
			
			// now check the returned class and create a real instance of 
			// the class. NOTE: if the class returned is the DataFile() class,
			// log it
			if (df.getClass().getName().equals(DataFile.class.getName())){
				Informer.getInstance().info(
					this,
					methodName,
					"Generic DataFile created",
					"absPath: " + absPath,
					true);
			}	
			else {			    
				Informer.getInstance().info(
					this,
					methodName,
					"Specific DataFile created: " + df.getClass().getName(),
					"absPath: " + absPath,
					true);
			}
			
		} 
		catch (Exception e) {
			// a number of Exceptions may be thrown here, all of which
			// are considered to be programmer error and so are lumped 
			// together for brevity
			Informer.getInstance().fail(
				this,
				methodName,
				"Error creating new DataFile instance",
				con.toString(),
				e);
		}			
		return df;
	}

	/**
	 * Method to create a new GlobalFile. First, if no GlobalPackage  
	 * exists, it is created. Next, the GlobalFile object is created
	 * (without the DFID which has yet to be determined). The GlobalFile
	 * is then added to the GlobalFilePackage, to GlobalFiles.globalFiles,
	 * to SIP.globalNameLookup, and to SIP.globalFiles.
	 * @param absPath 
	 * @return GlobalFile object
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	private GlobalFile generateNewGlobalFile(String absPath)
		throws FatalException, PackageException {
	    
		String methodName = "generateNewGlobalFile(String,String)";
		// check input
		if (absPath == null || absPath.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"absPath: " +absPath,
				new IllegalArgumentException());
		}
		
		// the GlobalFile to be returned
		GlobalFile gf = null;				
		// create the GlobalFilePackage if needed
		if (getGfp() == null) {
			// need to create the GlobalFilePackage
			GlobalFilePackage gfp = new GlobalFilePackage(getIp());
            setGfp(gfp);
            // make the GFP directory inside the IP's package dir, this makes 
            // linking easier
            new File(gfp.getWorkingPath()).mkdirs();            
		}
        String gfPackagePath = absPath.substring(getIp().getWorkingPath().length());             
        String destPath = getGfp().getWorkingPath() + gfPackagePath;

        // only copy the file if the destination path and original path are different        
        if (!absPath.equals(destPath)) {
            try {    
                FileUtil.copyFile(absPath, destPath);
            } catch (IOException e) {
                Informer.getInstance().error(this,
                        methodName,
                        "Unable to copy global file to GlobalFilePackage",
                        "destPath: " + destPath,
                        new PackageException());
            }
        }
		// initialize the new GlobalFile
		gf = new GlobalFile(OIDServer.getNewDfid(), destPath, getGfp(), 1);
		// set the global file's intellectual entity id
		gf.setIeid(getGfp().getIntEnt().getOid());
		// record the fact that this is a new global file
		gf.setNew(true);
		// add new GlobalFile to the GlobalFile package
		getGfp().addNewGlobalFile(destPath, gf);
		// add globalFile to gfp.globalFiles
		getGfp().addGlobalFile(destPath, gf);
		// add to GlobalFiles singleton
		GlobalFiles.getInstance().addGlobalFile(gf);
		// add GlobalFile to SIP.globalNameLookup
		getIp().addToGlobalNameLookup(destPath, gf);
		// add to SIP.globalFiles
		getIp().addGlobalFile(destPath, gf);
		// save the global file locally (this will happen for localized, migrated and 
		// normalized global files as well)
		// saveGlobalFileToLocalStorage(gf);
		// return the newly created GlobalFile
		return gf;		
	}
	
	/**
	 * Method to determine the Account/Project id based on  
	 * the account and project context. If isGlobal == true, 
	 * then the AP context will be the archive itself. 
	 *  
	 * @param ie the intellectual entity context
	 * @param isGlobal set true if the file is to be owned by the archive 
	 * @return integer
	 * @throws PackageException 
	 * @throws FatalException
	 */
	private int getApId(IntEntity ie, boolean isGlobal) 
		throws PackageException, FatalException {
		String methodName = "getApId(IntEntity,boolean)";
		if (ie == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"ie: " +ie,
				new IllegalArgumentException("Null argument for IntEntit ie"));
		}
		
		// get the AP code based on the account and project context
		// if the scope is Global, then daitss will be the account context
		return Agreements.getApId(
			(isGlobal?ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT"):ie.getAcccount()),
			(isGlobal?ArchiveProperties.getInstance().getArchProperty("DAITSS_PROJECT"):ie.getProject()));		
	}
	
	/**
	 * Returns an array of possible Class objects corresponding to 
	 * an extension.
	 * @param ext 
	 * 
	 * @return Class array
	 * @throws FatalException 
	 */
	private Class[] getClassesByExt(String ext) throws FatalException {
		String methodName = "getClassesByExt(String)";		
		// check input
		if (ext == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"ext: " + ext,
				new IllegalArgumentException());
		}
		// temp storage for return value
		Vector v = new Vector();
		
		// get the normal mimeType priority list for ext
		String[] mimeTypes = getMimeTypes(ext);		
		addUniqueElementsToVector(v, mimeTypes);
		
		// since extensions don't really give us much other than
		// the priority of mime types to try, we need to get all
		// mimetypes as though the file came in with no extension
		mimeTypes = getMimeTypes(Extension.EXT_NONE);
		addUniqueElementsToVector(v, mimeTypes);
		
		// all possible mime types have been added to v in order of
		// priority. now those mime types need to be translated 
		// to classes and returned.
		Vector classVector = new Vector();
		Iterator iter = v.iterator();
		while (iter.hasNext()) {
			String mimeType = (String)iter.next();
			Class[] classes = getClassesByMimeType(mimeType);			
			addUniqueElementsToVector(classVector, classes);
		} 
		
		// cast all elements of the Vector to a Class and enter
		// in return array.
		Class[] rClasses = new Class[classVector.size()];
		for (int i=0;i<classVector.size();i++) {
			rClasses[i] = (Class)classVector.elementAt(i);
		}
				
		return rClasses;
	}

	/**
	 * Returns an array of class objects correcponding to a particular
	 * mime type. In all but one case, the class array returned will have
	 * a length of 1 because the mapping from mime type to class is
	 * generally 1-1. The exception is for the mime type
	 * <code>MimeMediaType.MIME_APP_UNK</code> which maps to all possible
	 * classes known to DAITSS.
	 *  
	 * @param mimeType
	 * @return Class array
	 * @throws FatalException
	 */
	private Class[] getClassesByMimeType(String mimeType) throws FatalException{
		String methodName = "getClassesByMimeType(String)";
		// if mimetype isn't specified, set it to empty string
		if (mimeType == null) {
			mimeType = "";
		}

		// store the mime type being requested
		String theType = mimeType;

		if (!MimeMediaType.isSupportedType(mimeType)) {
			// if the mimetype is unrecognized, treat it as
			// an unknown type
			theType = MimeMediaType.MIME_APP_UNK;
		}

		Hashtable ht = MimeMediaType.getClassLookup();
		// search on theType (may or may not be the same
		// as the mimeType arg)
		String[] classNames = (String[])ht.get(theType.toLowerCase());
		if (classNames == null || classNames.length < 1) {
			Informer.getInstance().fail(
				this,
				methodName,
				"No class priority list for mime type",
				"mimeType: " + theType,
				new IllegalArgumentException());
		}
		Class[] rClasses = new Class[classNames.length];
		for (int i=0;i<classNames.length;i++) {					
			try {
				rClasses[i] = Class.forName(classNames[i]);
			} 
			catch (ClassNotFoundException e) {
				Informer.getInstance().fail(
					this,
					methodName,
					"Class not found",
					"class: "+ classNames[0],
					e);
			}
		}
		return rClasses;
	}

	/**
	 * Returns a DataFile from the dflAbsPath HashTable keyed by the fileName.
	 * 
	 * @return a DataFile with key equal to absPath, or NULL if no
	 * such key exists
	 * @param absPath absolute path to a physical file
	 */
	public DataFile getDfByAbsPath(String absPath) {
		return (DataFile) getDflAbsPath().get(absPath);
	} // end getDataFile        

	/**
	 * Returns a DataFile from the dflAbsPath HashTable keyed by the fileName.
	 * @param checksum 
	 * 
	 * @return a DataFile with key equal to absPath, or NULL if no
	 * such key exists
	 */
	public DataFile getDfByChecksum(String checksum) {
		return (DataFile) getDflChecksum().get(checksum);
	} // end getDataFile        

	/**
	 * Returns a DataFile (represented by a link) from the uriNameLookup HashTable 
	 * keyed on link alias.
	 * 
	 * @return a DataFile with key equal to uri, or NULL if no
	 * such key exists
	 * @param uri a link alias
	 */
	public DataFile getDfByLinkAlias(String uri) {
		return (DataFile) getUriNameLookup().get(uri);
	} // end getDataFile        

	/**
	 * Method to access DataFile objects stored in the uriNameLookup Hashtable.
	 * 
	 * @param uri uri for a file (URL, file, ...)
	 * @return a DataFile if the uri is a key in the uriNameLookup
	 * Hashtable, or else null
	 */
	public DataFile getDfByUri(String uri) {
	    if (uri == null) {
	        return null;
	    }
		return (DataFile)getUriNameLookup().get(uri);
	}


	/**
	 * A HashTable of DataFile objects created by this ContentObjectFactory
	 * keyed by the absolute path to the files.
     * @return The DFL keyed by absolute path
	 */
	public Hashtable <String, DataFile> getDflAbsPath() {
		return dflAbsPath;
	} // end getDfl        

	/**
	 * A HashTable of DataFile objects created by this DataFileFactory
	 * keyed by checksum.
     * @return The DFL keyed by checksum
	 */
	public Hashtable <String, DataFile> getDflChecksum() {
		return dflChecksum;
	} // end getDfl        

	/**
	 * A hashtable containing partially-populated DataFile objects passed to
	 * the constructor.
     * @return Hashtable <String, METSDocument>
	 */
	public Hashtable <String, METSDocument> getDflLite() {
		return dflLite;
	} // end getDflLite        

	/**
	 * Determines the appropriate subClass of a DataFile that a physical file
	 * should be. Looks at the file extension and the corresponding DataFile in
	 * the dflLite if it exists to guess the file type. Creates a priority list
	 * of types of data files based on its guess then calls
	 * DataFile.isType(String type) for each type in its priority list until
	 * the first true is returned. If no match can be found an
	 * UnknownTypeException is thrown.
	 * @param absPath
     * @param dfLite
	 * @return a Class for which the file at <code>absPath</code> is valid
	 * @throws FatalException 
	 */	
	private Class getFileType(String absPath, METSDocument dfLite) 
		throws FatalException {
		// the Class to be returned
		Class c = null;
		
		String methodName = "getFileType(String)";
		// check input
		if (absPath == null || absPath.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"absPath: " +absPath,
				new IllegalArgumentException());
		}
		
		// METSDocument
		// check to see if mime type is provided.
		// DataFile dfLite = getLiteDataFile(absPath);		
		// CASE 1: mime type provided in lite DataFile 
		if (dfLite != null) {
			// a lite DataFile exists
			c = testForClass(absPath, 
				getClassesByMimeType(dfLite.getFirstMimeType()),dfLite);
		}
	
		
		// CASE 2: no lite DataFile or no mime type in lite DataFile, 
		// work off of extension. Since getClassesByExt returns
		// a priority list containing all DataFile subclass and the
		// DataFile class itself, this call will test all possible 
		// classes
		if (c == null) {
			c = testForClass(absPath,
				getClassesByExt(FileUtil.getExtension(absPath)),dfLite);
		}		
		
		if (c == null) {
			// no compatible DataFile type has been found, this
			// should never happen since all files should be valid
			// as DataFile objects, but if this does occur, notification
			// is imperative.
			Informer.getInstance().fail(
				this,
				methodName,
				"File not compatible with any known DataFile classes",
				"absPath: " + absPath,
				new UnknownFileTypeException());
		}
		
		return c;
	}

	/**
	 * @return GlobalFilePackage
	 */
	public GlobalFilePackage getGfp() {
		return gfp;
	}


    /**
     * @return Returns the importedGlobalFiles.
     */
    public Vector getImportedGlobalFiles() {
        return importedGlobalFiles;
    }

	/**
	 * @return InformationPackage object
	 */
	public InformationPackage getIp() {
		return ip;
	}

	/**
	 * Returns all the info contained in a descriptor file for a particular
	 * file (passed in as a parameter). Obtained by finding this fileName key
	 * in the fileInfo Hashtable.
	 * 
	 * @return a METSFileInfo with ...
	 * @param absPath
     * @throws FatalException
	 */
	private METSDocument getLiteDataFile(String absPath) 
		throws FatalException {
		String methodName = "getLiteDataFile(String)";
		if (absPath == null || absPath.trim().equals("")) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"absPath: " +absPath,
				new IllegalArgumentException());			
		}

		if (this.hasLiteDataFile(absPath)) {
			return this.dflLite.get(absPath);
		} else {
			return null;
		}
	} // end getLiteDataFile        

	/**
	 * Returns an array of possible MIME types based on <code>ext</code>.
	 * 
	 * @param ext
	 * @return an array of mime types corresponding to <code>ext</code>
	 * 
	 * @throws FatalException
	 */
	private String[] getMimeTypes(String ext) 
		throws FatalException{			
		String methodName = "getTypes(String)";
		// check input
		if (ext == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"ext: " +ext,
				new IllegalArgumentException());
		}
		// get the expected mimeTypes for this extension
		Hashtable ht = Extension.getMimeLookup();
		String[] mimeTypes = (String[])ht.get(ext);						
				
		return mimeTypes;
	}

	/**
	 * @return Hashtable object
	 */
	public Hashtable getUriNameLookup() {
		return uriNameLookup;
	}    


	/**
	 * Method to handle a file recognized as an existing GlobalFile
	 * based on checksum. The GlobalFile will be retrieved from the 
	 * GlobalFiles singleton, added to the SIPs collection of GlobalFiles
	 * (if not already present), adds to the absolute path and the GlobalFile
	 * to the SIPs globalNameLookup, and finally deletes the physical 
	 * file at absPath (we already have the GlobalFile, the file at
	 * absPath is a repeat). 
	 * @param absPath 
	 *  
	 * @param checksum
	 * @return the GlobalFile with checksum equal to <code>checksum</code> 
	 * @throws FatalException if no GlobalFile exists with checksum 
	 * equal to <code>checksum</code>
	 * @throws PackageException 
	 */
	private GlobalFile handleExistingGlobalFile(String absPath, String checksum) 
		throws FatalException, PackageException {
		
	    String methodName = "handleGlobalFile(String,String)";
		// retrieve the GlobalFile
		GlobalFile gf = GlobalFiles.getInstance().getGlobalFile(checksum);	
		
		if (gf == null) {
			// this should never happen since the GlobalFile should be
			// identified as an existing GlobalFile before this method
			// is called, but just in case ...
			Informer.getInstance().fail(this,
				methodName,
				"No GlobalFile with the given checksum is recognized by the GlobalFiles object",
				"path: " + absPath + " checksum: " + checksum,
				new IllegalArgumentException());
		}
				
		
		// bring the localized version of the global file (and all of 
		// its children recursively) into the package
		importGlobalFile(gf, absPath);
		
		// return the GlobalFile
		return gf;
	}
	
	/**
	 * Checks to see if a file at <code>absPath</code> has
	 * already been identified and entered in the absolute path dfl.
	 * 
	 * @param absPath the absolute path to use for lookup
	 * @return true if a file with this absolute path has already been encountered by 
	 * the DataFileFactory, otherwise false.
	 */
	public boolean hasDfByAbsPath(String absPath) {
		return getDflAbsPath().containsKey(absPath);
	}

	/**
	 * Checks to see if a file with checksum equal to <code>checksum</code> has
	 * already been identified and entered in the checksum dfl.
	 * 
	 * @param checksum the checksum to use for lookup
	 * @return true if a file with this checksum has already been encountered by 
	 * the DataFileFactory, otherwise false.
	 */
	public boolean hasDfByChecksum(String checksum) {
		return getDflChecksum().containsKey(checksum);		
	}

	/**
	 * Returns true if there is a DataFile object in the dflLite for a given
	 * file in the package, else returns false. There will be a corresponding
	 * DataFile object in the dflLite if the file was referenced in the initial
	 * descriptor file (at least for ingest).
	 * 
	 * @return a boolean with ...
	 * @param fileName ...
	 */
	private boolean hasLiteDataFile(String fileName) {
	    boolean hasIt = false;
	    
	    if (this.dflLite != null &&  this.dflLite.containsKey(fileName)) {
			hasIt = true;
		} 
		
		return hasIt;
	}         
	
	
	private void importGlobalChildren(GlobalFile gf) throws PackageException, FatalException {
	    String methodName = "importGlobalChildren(GlobalFile)";
	    
	    // iterate through all the global file's children
	    Iterator iter = gf.getChildren().iterator();	
        while (iter.hasNext()) {
            // get the message digest for the child
            String childMD = (String)iter.next();	
            // dereference the GlobalFile representing the child
            GlobalFile gfChild = GlobalFiles.getInstance().getGlobalFile(childMD);
            // import the child to the package
            String childPath = gfChild.getPath();
            importGlobalFile(gfChild, childPath);
        }
	
	}

	
	private void importGlobalFile(GlobalFile gf, String absPath) 
		throws PackageException, FatalException{

        String methodName = "importGlobalFile(GlobalFile, absPath)";    

        // need to short circuit here if this GlobalFile has 
	    // already been imported into the package - no need to 
	    // waste the work.	    
	    String mdValue = gf.getMesgDigestValue(ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"));
	    if (this.getImportedGlobalFiles().contains(mdValue)) {
	        // this GlobalFile has already been imported, no need to do it again
	        return;
	    }
	   	    
	    // move the new copy of the GlobalFile to the relative directory
		// that contained the original copy of the GlobalFile
		String newAbsPath = this.getIp().getPackagePath();
		// add the separator if needed
		if (!newAbsPath.endsWith(File.separator)) {
		    newAbsPath = newAbsPath + File.separator;
		}
		// add the original rel path and IEID 
		newAbsPath = newAbsPath + gf.getIeid() + File.separator + gf.getPackagePath();
		//newAbsPath = newAbsPath + gf.getPackagePath();
						
		// move the file if necessary	
		if (!absPath.equals(newAbsPath)) {
			try {
	            FileUtil.copy(absPath, newAbsPath);	  
	            // record the fact that the global file has been imported
	            this.getImportedGlobalFiles().add(mdValue);
	        } catch (IOException e) {
	            Informer.getInstance().error(this,
	                    methodName,
	                    "Error moving pre-existing GlobalFile to its original path",
	                    "fromPath : " + absPath + " toPath: " + newAbsPath,
	                    e);
	        }
		}
		
		
		// set the GlobalFile's new full path
		gf.setPath(newAbsPath);	
		// increment the global file's use count
		gf.setCount(gf.getCount() + 1);
		// add reference to this GlobalFile in the SIP
		this.ip.addGlobalFile(newAbsPath, gf);
		// add the GlobalFile and its path to the SIP's lookup table
		this.ip.addToGlobalNameLookup(newAbsPath, gf);
		
        // recursively bring in all of the global file's children
        importGlobalChildren(gf);
        
	    // bring in the localized version (if there is one) of the globalfile
	    importLocalizedGlobalFile(gf);				   				
	}
	
	/**
	 * 
	 * @param gf 
	 * @param localGlobalFile the absolute path to the global file to copy into the package
	 * @return the absolute path the copy of the global file in the package
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	/*
	private String copyGlobalToPackage(String localGlobalFile) 
		throws FatalException, PackageException {
	    
	    String methodName = "copyGlobalToPackage(String)";
	    
	    String destPath = null;

        String locCopyRelPath = localGlobalFile.substring(
                ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR").length());
        
        destPath = this.getIp().getPackagePath();       
        destPath = (destPath.endsWith(File.separator)?destPath:destPath + File.separator);        
        destPath += locCopyRelPath;
        
        try {
            // copy the file to the current package
            FileUtil.copy(localGlobalFile, destPath);
        } catch (IOException e) {
            Informer.getInstance().error(this,
                    methodName,
                    "Error copying global file to current package",
                    "localGlobalFile: " + localGlobalFile + " destPath: " + destPath,
                    e);
        }
	    
	    
	    return destPath;
	}
	*/
	
	private void importLocalizedGlobalFile(GlobalFile gf) 
		throws FatalException, PackageException{

	    String methodName = "importLocalizedGlobalFile(GlobalFile)";
	    
	    String localCopy = gf.getLocalizedFilePath();	 
	    String localMdValue = gf.getLocalizedMdValue();
	    
        // set the path to the GlobalFile's localized version
        // NOTE: this value may be reset later during importLocalizedGlobalFile(...)
        // Since this method short circuits when the GlobalFile and its 
        // localized version are the same, the localized path will still point to the 
        // copy in the globals directory, which causes problems with link replacement
        // during localization. To avoid this situation, it's best to set the localized
        // path here, even if it may be overwritten later.
        gf.setLocalizedFilePath(gf.getPath());        
        
		// short circuit if this file has already been imported
	    if (this.getImportedGlobalFiles().contains(localMdValue)) {
	        return;
	    }
		
		// dereference the GlobalFile representation 
		GlobalFile localGf = GlobalFiles.getInstance().getGlobalFile(localMdValue);		
		if (localGf == null) {
			// this should never happen since the GlobalFile should be
			// identified as an existing GlobalFile before this method
			// is called, but just in case ...
			Informer.getInstance().fail(this,
				methodName,
				"No GlobalFile with the given checksum is recognized by the GlobalFiles object",
				"path: " + localCopy + " checksum: " + localMdValue,
				new FatalException());
		}
				
	    // copy the localized version of the file to the current package
	    String destPath = "";
	    
        String locCopyRelPath = localCopy.substring(
                ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR").length());
         
        destPath = this.getIp().getPackagePath();       
        destPath = (destPath.endsWith(File.separator)?destPath:destPath + File.separator);        
        destPath += locCopyRelPath;
               
        try {
            // copy the file to the current package
            FileUtil.copy(localCopy, destPath);
            this.getImportedGlobalFiles().add(localMdValue);
        } catch (IOException e) {
            Informer.getInstance().error(this,
                    methodName,
                    "Error copying global file to current package",
                    "localCopy: " + localCopy + " destPath: " + destPath,
                    e);
        }
	    
		// reset the localized global file's current path
		localGf.setPath(destPath);
		// reset the localized global file's localized path (to itself)?
	    localGf.setLocalizedFilePath(destPath);
        // reset the GlobalFile's localized version path
        gf.setLocalizedFilePath(destPath);
        
		// add reference to this GlobalFile in the SIP
		this.ip.addGlobalFile(destPath, localGf);
		// add the GlobalFile and its path to the SIP's lookup table
		this.ip.addToGlobalNameLookup(destPath, localGf);
                
	    // import the localized GlobalFile's children
        importGlobalChildren(localGf);
	}
	
	/**
	 * Determines if a file path should be treated as
	 * a global file for this package.
	 * 
	 * @param path absolute path to file
	 * @return whether or not it should be treated global for
	 * 	this package
	 */
	private boolean isFutureGlobalPath(String path) {
	    boolean itIs = false;
	    
	    if (path != null && 
	            futureGlobalPaths.contains(path)){
	        itIs = true;
	    }
	    
	    return itIs;
	}
	
	/**
	 * Determines if a file path should be treated as
	 * created by the archive for this package.
	 * 
	 * @param path absolute path to file
	 * @return whether or not it should be treated as created
	 * by the archive
	 */
	private boolean isFutureOriginArchivePath(String path) {
	    boolean itIs = false;
	    
	    if (path != null && 
	            futureOriginArchivePaths.contains(path)){
	        itIs = true;
	    }
	    
	    return itIs;
	}

	/**
	 * Convenience method to print the contents of the 
	 * DataFile list (by checksum and by absPath) as a String
	 *
	 */
	public  void printDfl() {
	    int dfCount = 0;
	    
		System.out.println(">>>>>>>>>>>>>>>>>>>>>DataFileFactory DFL by absPath<<<<<<<<<<<<<<<<<<<");

		if (dflAbsPath == null) {
			System.out.println("dfl is null");
		} else if (dflAbsPath.isEmpty()) {
			System.out.println("dfl is empty");
		} else {
			System.out.println("Files in dfl: " + dflAbsPath.size());
			Enumeration <DataFile> e = dflAbsPath.elements();
			while (e.hasMoreElements()) {
				DataFile df = e.nextElement();
				dfCount++;
				System.out.println(">>>>>>>>>DATA FILE " + dfCount + "<<<<<<<<<<\n");
				System.out.println(df);
			}

		}
	}

	/**
	 * Convenience method to print the contents of the uriNameLookup Hashtable
	 */
	public void printUriNameLookup() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>DataFileFactory uriNameLookup<<<<<<<<<<<<<<<<<<<");

		if (uriNameLookup == null) {
			System.out.println("uriNameLookup is null");
		} else if (uriNameLookup.isEmpty()) {
			System.out.println("uriNameLookup is empty");
		} else {
			System.out.println("Entries in uriNameLookup: " + uriNameLookup.size());
			Enumeration e = uriNameLookup.elements();
			while (e.hasMoreElements()) {
				DataFile df = (DataFile) e.nextElement();
				System.out.println(">>>>>>>>>DATA FILE<<<<<<<<<<\n");
				System.out.println(df);
			}
		}		
		
	}
	

	/**
	 * Removes a file from the DFL keyed by absolute path and by checksum. This method 
	 * calls removeDfByChecksum in order to maintain consistency.
	 * 
	 * @param absPath
	 * @param removeReferences specifies whether all references to the DataFile should be 
	 * deleted from Relationships and Events
	 * 
	 * @return The DataFile object being removed, or null if no such DataFile exists
	 * for this absolute path.
	 * 
	 * 
	 * @throws FatalException
	 */
	public DataFile removeDfByAbsPath(String absPath, boolean removeReferences) throws FatalException {
	    String methodName = "removeDfByAbsPath(String)";
	    if (absPath == null || absPath.equals("")) {
	        Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "absPath: " + absPath,
                    new IllegalArgumentException());
	    }
	    DataFile df =  this.dflAbsPath.remove(absPath);
	    
	    if (df != null) {
	        String checksum = df.getMesgDigestValue(
	                ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1"));
	        DataFile df2 = null;
	        if (checksum != null) {
	            df2 = removeDfByChecksum(checksum); 
	        }
	        
	        if (checksum == null || df2 == null) {
	        	Informer.getInstance().fail(this,
                        methodName,
                        "File removed from DFL keyed by absolute path does not exist in DFL keyed by message digest value. DFLs out-of-sync.",
                        "file path: " + absPath,
                        new FatalException());
	        }
	    }
	    
	    // remove references from Relationships and Events
	    if (df != null && removeReferences) {
	        Relationships.getInstance().removeReferences(df.getOid());
	        Events.getInstance().removeReferences(df.getOid());	        
	    }
	    
	    return df;
	}
	
	/**
	 * Removes a DataFile from the DFL keyed by primary checksum value.
	 * 
	 * @param checksum
	 * @return A DataFile if one exists with given checksum, else null
	 * @throws FatalException
	 */
	private DataFile removeDfByChecksum(String checksum) throws FatalException {
	    String methodName = "removeDfByChecksum()";
	    if (checksum == null || checksum.equals("")) {
	        Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "checksum: " + checksum,
                    new IllegalArgumentException());
	    }
	    return this.dflChecksum.remove(checksum);	    
	}

	/**
	 * Searches for a DataFile in both Dfls (dflChecksum and dflAbsPath). If
	 * it is found in either Dfl, the DataFile is returned. If not, null is returned.
	 * The primary function for this method is to be called from createDataFile(String,IntEntity,boolean).
	 * Use caution when calling this method as it will delete physical files under certain
	 * conditions. 
	 * 
	 * @param absPath
	 * @param checksum
	 * @return Datafile object
	 * @throws FatalException 
	 */
	private DataFile searchForDfInDfl(String absPath, String checksum) 
		throws FatalException {
			
		String methodName = "searchForDfInDfl(String,String)";
		
		boolean hasAbsPath = hasDfByAbsPath(absPath);
		boolean hasChecksum = hasDfByChecksum(checksum);

		if (!hasAbsPath && !hasChecksum) {
			// this file (or a copy of it) has not been seen yet
			return null;
		}
		else if (!hasAbsPath && hasChecksum){
			// a copy of this file has already been encountered, delete
			// this copy
			Informer.getInstance().info(this,
				methodName, 
				"Identical DataFile already processed in package",
				absPath,
				false);
			
			return getDfByChecksum(checksum);
		}
		else {
			// this absPath has already been encountered. Do not delete this file.
			return getDfByAbsPath(absPath);
		}
	}

	/**
	 * A hashtable containing partially-populated DataFile objects passed to
	 * the constructor.
     * @param _dflLite
	 */
	public void setDflLite(Hashtable<String, METSDocument> _dflLite) {
		dflLite = _dflLite;
	} // end setDflLite        

	/**
	 * Method to set (some) members of a new DataFile. Members will be
	 * set based on whether or not this DataFile is also a new global file.
	 * New global files will have different values for the members. 
	 * 
	 * @param df the DataFile whose members will be set
	 * @param newGlobalFile specifies whether this new DataFile is 
	 * also a new global file
	 * @param origUri the URI of the DataFile <code>df</code> if the file was downloaded 
	 * @param parentDf the parent of the newly-created file or null if there is no parent
     * @throws PackageException
     * @throws FatalException
	 */
	private void setDfMembers(DataFile df, boolean newGlobalFile, String origUri, 
	        DataFile parentDf) 
		throws PackageException, FatalException {
			
		String methodName = "setDfMembers(DataFile,boolean)";
		// check input
		if (df == null){
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"df: " +df,
				new IllegalArgumentException());
			
		}
		
		// set members with resepct to being global or not
		// get the ap code
		int apId = getApId(getIp().getIntEnt(), newGlobalFile);
		// set the ap code
		df.setAp(apId);
		
        // if this is a generic data file, try using FFIdent to identify its mimetype 
        if (df.getClass().getName().equals(DataFile.class.getName())) {
            String mimeType = this.formatIdentifier.idPrimaryMimeType(
                    new File(df.getPath()));
            if (mimeType != null) {
                df.setMediaType(mimeType);
                // now set the DataFile's extension (usually done
                // in DataFile subclasses, but since this file didn't
                // meet the requirements for a specific subclass, it
                // will not be set)
                if (df.getFileTitle().lastIndexOf(".") > 1) {
                    df.setFileExt(df.getFileTitle().substring(
                            df.getFileTitle().lastIndexOf(".") + 1));
                }
            } else {
            	// record a limitation for files DAITSS cannot identify.  The primary 
            	// purpose of this limitation is to record a warnign in the ingest report
            	df.addLimitation(df.getLimitationsPossible().getSevereElement(
        			Limitations.FILE_UNKNOWN));
            }
        }
        
		// PRESERVATION LEVEL 
		// note that preservation level is also based on the file
		// path, if it's normalized or migrated, then the preservation 
		// level is full
		if (df.getPath().startsWith(getIp().getMigratedDir())
			|| df.getPath().startsWith(getIp().getNormalizedDir())) {				
			df.setPresLevel(Agreements.PRES_LEV_FULL);
		}
		else {
			// get the preservation level
			String presLevel = Agreements.getPreservationLevel(
				apId,
				df.getMediaType());		
			// set the preservation level		
			df.setPresLevel(presLevel);			
		}

		// set isGlobal based on newGlobalFile arg
		df.setIsGlobal(newGlobalFile);
		// set the ieid
		df.setIeid(newGlobalFile?getGfp().getIntEnt().getOid():getIp().getIntEnt().getOid());		

		// set members that don't pertain to being global		
		// set the DFID
		// if the data file has already been archived, reconstruct it from the database
		if (df.hasArchived())
			df.readFromDB();
		else {
			// otherwise, get a new DFid from the OIDServer
			df.setOid(OIDServer.getNewDfid());
		
			// set the ORIGIN, based on argument		
			if(origUri != null) {
				df.setOrigin(DataFile.ORIG_INTERNET);	
				df.setOriginalUri(origUri);
			} 
			else if (isFutureOriginArchivePath(df.getPath())) {
			    df.setOrigin(DataFile.ORIG_ARCHIVE);
			}
			else {
				// file was submitted by depositor
				df.setOrigin(DataFile.ORIG_DEPOSITOR);
			}		
			
			// set the creation date if the archive created it.
			// note that if the file contains internal metadata to let
			// us know the more accurate creation time then the more
			// accurate time will overwrite this one during the 
			// extractMetadata, which is what we want
			if (df.getOrigin().equals(DataFile.ORIG_ARCHIVE)){
			    df.setCreateDate(DateTimeUtil.now());
			}
		}
	}

	/**
	 * @param gfp
	 */
	private void setGfp(GlobalFilePackage gfp) {
		this.gfp = gfp;
	}

	/**
	 * @param ip
	 */
	public void setIp(InformationPackage ip) {
		this.ip = ip;
	}

	/**
	 * Method to determine if the file at <code>absPath</code> meets
	 * the type requirements of any of the Classes in <code>classes</code>.
	 * The first Class object in the array with which the file is 
	 * compatible is returned. Null is returned if the file does not
	 * meet the type requirements for any of the Class objects in 
	 * <code>classes</code>.
	 * 
	 * @param absPath absolute path to an existing file
	 * @param classes an array of Class objects 
	 * @param liteDf a 'lite' DataFile created from a descriptor that may help 
	 * determine the type of class
	 * @return a Class object from <code>classes</code> if the file
	 * is compatible, or <code>null<code> if the file is not compatible
	 * with any of the classes
	 * @throws FatalException
	 */
	private Class testForClass(String absPath, Class[] classes, METSDocument liteDf) 
		throws FatalException {
		String methodName = "testForClass(String,Class[])";
		// declare the return value
		Class c = null;
		// check input
		if (absPath == null || absPath.trim() == "") {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"absPath: " +absPath,
				new IllegalArgumentException());
		}
		// check for existence of the file and that it is a file 
		// (not a directory)
		File f = new File(absPath);
		if (!(f.exists() && f.isFile())) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument, path does not exist or is not a file",
				"absPath: " +absPath,
				new IllegalArgumentException());			
		}
		// check the input array
		if (classes == null || classes.length < 1) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument, array is null or empty",
				"classes: " +classes,
				new IllegalArgumentException());
		}
		
		// iterate through the classes and test for compatibility		
		for (int i=0;i<classes.length;i++) {
			Informer.getInstance().info(
				this, 
				methodName,
				"Testing for class compatability: " + classes[i].getName(), 
				absPath, 
				false);
			
			
			Method m = null;
			try {
				// construct the arg type list to access the 
				// class's isType method
				Class[] parms = {String.class, METSDocument.class};								
				// attempt to get the isType method
				m = classes[i].getMethod("isType",parms);
				Object[] args = {absPath, liteDf};
				// invoke the isType(String) method to test for
				// compatibility
				if(((Boolean)m.invoke(this, args)).booleanValue()){
					c = classes[i];
					break;
				}
			} 
			catch (Exception e) {
				// There are many different Exceptions that may 
				// be caught here, but all of them are considered
				// to be fatal, so just handle them all the same way
				Informer.getInstance().fail(
					this,
					methodName,
					"Error testing for class compatibility",
					"Method name " + classes[i].getName() + ": " + m.getName(),
					e);
			} 

		}
		
		// clean up
		f = null;
		
		// if control made it here, then the file at absPath is 
		// not compatible with any of the classes in the classes array
		return c;
	}


	/**
	 * Updates data structures used to store DataFile objects. These structures 
	 * include dflAbsPath, dflChecksum, uriNameLookup, and structures used to 
	 * track GlobalFile objects in an existing GlobalFilePackage
	 *  
	 * @param df The DataFile object to store
	 * @param checksum The checksum of DataFile df using the primary message digest algorithm
	 * @param parentDf The distributed parent of df, if any
	 * @throws FatalException
	 */
	private void updateDFStorage(DataFile df, String checksum, DataFile parentDf) 
		throws FatalException {
		// to be done for all files (including GlobalFile objects)
		if (df != null) {
			// a real DataFile was located or created
			// add DataFile to uriNameLookup table
			addDfToUriNameLookup(df.getOid() + "." + df.getFileExt(), df);	
			// also add its local absolute path to the Uri lookup
			URI uri = new File(df.getPath()).toURI();
			addDfToUriNameLookup(uri.toString(), df);
			if (!(df instanceof GlobalFile)) {
				// this is not a GlobalFile so add it to dfl(s)
				addDfToDfl(df.getPath(), checksum, df);	
			} 
			else {
				// this is a GlobalFile and it may be referenced by a new GlobalFile,
				// if so the referenced, pre-existing GlobalFile is needed by the GlobalFilePackage
				// so add it. First check to make sure that the gfp exists and that a parentDf
				// was supplied as an argument. This GlobalFile will not be added to the 
				// GFP's global file list if its parent is not also in the list.				 
				if (parentDf != null && gfp != null && gfp.hasGlobalFile(parentDf)) {
					gfp.addGlobalFile(df.getPath(), (GlobalFile)df);
				}
			}
		}		
	}
} // end DataFileFactory
