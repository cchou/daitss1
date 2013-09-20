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

/** Java class "AIP.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss.entity;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.EmptyPackageException;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.FileRole;
import edu.fcla.daitss.file.distributed.IntegrityTaskManager;
import edu.fcla.daitss.file.global.GlobalFiles;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.report.Reporter;
import edu.fcla.daitss.storage.StorageHelper;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.DisseminationDelivery;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.Silo;
import edu.fcla.jmserver.StorageException;

/**
 * An Archive Information Package.
 */
public class AIP extends InformationPackage {

    public List<Copy> oldCopies;
    
	/**
     * Constructor 
     * @param packagePath
     * @throws FatalException
     */
    public AIP(String packagePath) throws FatalException {
        super(packagePath);
    }
    
    /**
     * generate the new AIP descriptor for the re-ingest
     * @return
     * @throws FatalException
     * @throws PackageException
     */
   private DataFile generateAipDesc() throws FatalException, PackageException{
        
        String aipDescPath = FileUtil.getUniqueName(getWorkingPath()  
            + ArchiveProperties.getInstance().getArchProperty("AIP_DESC_NAME")
            + "_" + DateTimeUtil.getDateTimeStamp()  + "." + Extension.EXT_XML);                
                
        File oldDesc = new File(getArchiveDescriptor().getPath());
        METSDescriptor aipDesc = METSDescriptor.createAipDescriptorForReIngest(
        		aipDescPath, this, oldDesc);
        // set the intellectual entity
        aipDesc.setIntEnt(getIntEnt());
        
        DataFile df = DataFileFactory.getInstance(this).createDataFile(null, 
        		aipDesc.getPath(), false, aipDesc.getPath());
        // set DataFile's role for AIPDescriptor
        df.setRole(FileRole.ROLE_DESCRIPTOR_AIP);
        return df;
    }
    
    /**
     * Ingests an AIP. Handles most aspects of AIP ingest(re-ingest) including 
     * transformations (migration, normalization, localization), package
     * moving, AIP descriptor creation, and database updating.
     * 
     * @throws FatalException
     * @throws PackageException
     * @throws IOException
     */
    public void ingest() throws FatalException, PackageException, IOException {
        String methodName = "ingest(AIP)";
        Informer.getInstance().info(this,
                methodName,
                "AIP ingest began at " + DateTimeUtil.now(),
                "AIP: " + getPackageName(),
                true);
        
        // the archive descriptor is most likely already set, only search
        // for one if necessary
        if (this.hasArchiveDescriptor == false || this.getArchiveDescriptor() == null) {
            findArchiveDescriptor();
        }
       
        // AIPs must know their pre-existing IntEntities before being 
        // copied to the work directory
        // read the descriptor for this package's ieid
        String ieid = getIEIDFromDescriptor(getArchiveDescriptor());
        // set members based on the contents of the ARCHIVE descriptor
        buildFromDB(ieid);                
        // store input directory before copying so that
        // package can later be deleted from the input directory
        String packageInputDir = getPackagePath(); 
        // copy package to the working directory                
        copyToWorkDir();                
        // validate package
        validatePackage();
        // build package
        build();            
        // migrate, normalize, and localize files
        transform();
        
        // load the copies from the database, make new copies for any silos not used in old ingest
        loadCopies();
        
        Informer.getInstance().info(this, methodName, "Creating AIP descriptor for reingest", 
    	getPackageName(),false);
        
        // create AIPDescriptor and add to dfl                              
        DataFile aipDescDf = generateAipDesc();             
        
        // create GFPDescriptor and add to dfl
        GlobalFilePackage gfp = DataFileFactory.getInstance(this).getGfp();

        //***************************************************************************
        // dfl contains all SIP files including their migrate, normalized, 
        // and localized versions, the aip descriptor and its links
        //***************************************************************************
        
        // migrate, normalize, localize all files created as a result of aip 
        // and gfp descriptor creation (i.e. the links and their transformed versions)
        transform();

        //***************************************************************************
        // dfl now contains the SIP and all of its transformed files, 
        // the aip descriptor and all of its transformed versions, the
        // aip descriptor's links and all of their transformed version
        //***************************************************************************
                
        // remove the aip descriptors and their localized versions from 
        // the dfl (by checksum and absPath)
        aipDescDf = DataFileFactory.getInstance().removeDfByAbsPath(aipDescDf.getPath(), true);
        // remove the localized version of the aipDesc
        DataFileFactory.getInstance().removeDfByAbsPath(aipDescDf.getLocalizedFilePath(), true);
        // clear the old aipDescDf
        aipDescDf = null;


        //***************************************************************************
        // dfl now contains the SIP and all of its transformed files, 
        // the aip descriptor's links and all of its transformed versions
        //***************************************************************************                               
        
        // recreate the aipDesc
        aipDescDf = generateAipDesc();
        
        // re-localize the package (which now contains the AIP descriptor(s) and 
        // all of their links).  Need to localize the entire package again such that
        // the children be localized before their parents
        localize();
                
        //***************************************************************************
        // dfl now contains the SIP and all of its transformed files, the new aip 
        // descriptor and its localized version, the aip descriptor's links and all 
        // of its transformed versions (note the links from the original aip desc are 
        // the same as those from the new aip desc), 
        //***************************************************************************                               
        
        // remove the aip desc from the dfl, leaving only its localized version
        // which is the new aip desc
        DataFileFactory.getInstance().removeDfByAbsPath(aipDescDf.getPath(), true);
         
        //***************************************************************************
        // dfl now contains the SIP and all of its transformed files,
        // the localized version of the new aip descriptor,
        // the aip descriptor's links and all of its transformed versions
        // (note the links from the original aip desc are the same as those
        // from the new aip desc).
        //***************************************************************************       
        
        // check Distributed object integrity and record results in database
        new IntegrityTaskManager(
                DataFileFactory.getInstance().getDflChecksum().values()).process();                 
               
     
        // clean up disposable files
        setDisposables();
        
        // move AIP to output directory
        moveAIPToOutputDir();
        
        if (gfp != null && gfp.getNewGlobalFiles().size() != 0) {  
            gfp.storeLocally();
            gfp.moveToOutputDir();                                               
        }
		
        // all contents of SIP and GlobalFilePackage have been moved, delete the 
        // directories remaining in the work dir
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_INGESTED_FROM_WORK_DIR").equals("true")){
            clearWorkDir();
        }
        
        // copy AIP to Dissemination output location
        String account = getIntEnt().getAcccount();
        File aip = new File(ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH"));
   
    	DisseminationDelivery outputter = new DisseminationDelivery(getPackageName(), account, aip);
		outputter.process();
		
        // copy package to long-term storage if desired
        store();
                              
        // start transaction, update tables
        dbUpdate(gfp);
        
        // Report on this re-ingest
        Reporter.reportIngest(getIntEnt(), true);               
                       
        // delete package from input and output directories
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_INGESTED_FROM_WORK_DIR").equals("true")){
            clearWorkDir();
        }

        // clear out directory
        clearOutDir();
        
        // delete the AIP package from reingest (rin) directory
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_INGESTED_FROM_IN_DIR").equals("true")) {
            FileUtil.delete(ArchiveProperties.getInstance().
                    getArchProperty("REINGEST_INPATH") + getPackageName());
        }
        
        Informer.getInstance().info(this,
                methodName,
                "AIP reingest finished at " + DateTimeUtil.now(),
                "AIP: " + getPackageName(),
                true);
    } // end ingest        
    
    private void loadCopies() throws FatalException {
    	// oldCopies: all copies in the DB
		oldCopies = StorageHelper.copies(getIntEnt().getOid());
		copies = new Vector<Copy>();
    	// copies: all the active silos
		for (Silo silo : StorageHelper.activeSilos()) {
			copies.add(new Copy(silo, oldCopies.get(0).path()));
		}
	}
 
	void deleteOldCopies() throws FatalException {
		for(Copy copy : oldCopies) {
			try {
				// delete the copy from the database
				int copyId = StorageHelper.dbCopyId(copy);
				TransactionConnection scon = DBConnection.getSharedConnection();
				scon.startTransaction();
				StorageHelper.dbDeleteCopy(scon, copyId);
				copy.delete();
				scon.commitTransaction();
			} catch (StorageException e) {
				Informer.getInstance().fail("Could not delete copy, storage error", copy.toString(), e);
			} catch (SQLException e) {
				Informer.getInstance().fail("Could not delete copy, database error", copy.toString(), e);
			}
		}	
	}
    
	/**
	 * mark disposable files,  including old AIP descriptor and intermediate migrated files.
	 * @throws FatalException
	 * 
	 */
	private void setDisposables() throws FatalException {
		
		// open a database connection
		TransactionConnection con = DBConnection.getSharedConnection();
		
		// mark the old AIP descriptor as disposable
		METSDescriptor oldAIPDescriptor = this.getArchiveDescriptor();
		String path = this.getWorkingPath() + oldAIPDescriptor.getFileTitle();
		DataFile descDF = DataFileFactory.getInstance().getDfByAbsPath(path);
		if (descDF != null)
		    descDF.setIsObsolete("TRUE");
		else
		    Informer.getInstance().info(path + " is not marked as obsolete", "cannot find AIP descriptor in data file factory");
		
        // select which files are disposable
		// get $dfids from migration relationships for these datafiles
		/*
		 * select DFID_1 as pred, DFID_2 as succ
		 * from RELATIONSHIP r, DATA_FILE df
		 * where r.REL_TYPE = 'MIGRATE_TO' and
		 *       df.IEID = $ieid and
		 *       df.DFID in (r.DFID_1, r.DFID_2);
		 */
		Map<String, String> migrations = new Hashtable<String, String>();
		try {
			String select = String.format("%s as pred, %s as succ", ArchiveDatabase.COL_RELATIONSHIP_DFID_1, ArchiveDatabase.COL_RELATIONSHIP_DFID_1);
			String from = String.format("%s r, %s df", ArchiveDatabase.TABLE_RELATIONSHIP, ArchiveDatabase.TABLE_DATA_FILE);
			String whereRelType = String.format("r.%s = 'MIGRATE_TO'", ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE);
			String whereIeid = String.format("df.%s = %s", ArchiveDatabase.COL_DATA_FILE_IEID, SqlQuote.escapeString(getIntEnt().getOid()));
			String whereDfid = String.format("df.%s in (r.%s, r.%s)", ArchiveDatabase.COL_DATA_FILE_IEID, ArchiveDatabase.COL_RELATIONSHIP_DFID_1, ArchiveDatabase.COL_RELATIONSHIP_DFID_1);
			String sql = String.format("select %s from %s where %s and %s and %s", select, from, whereRelType, whereIeid, whereDfid);
			
			ResultSet r = con.executeQuery(sql);
			while(r.next()) {
				String pred = r.getString("pred");
				String succ = r.getString("succ");
				migrations.put(pred, succ);
			}
			r.close();
		} catch (SQLException e) {
			StackTraceElement element = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(this, element.getMethodName(), "database exception", "cleaning disposable files", e);
		}
		
		Set<String> predicessors = migrations.keySet();
		Collection<String> successors = migrations.values();
		Set<String> intermediates = new HashSet<String>();
		intermediates.addAll(predicessors);
		intermediates.retainAll(successors);

        // mark them obsolete 
		if(!intermediates.isEmpty()) {
			List<String> iList = new Vector<String>(intermediates);
			Hashtable <String, DataFile> ht = new Hashtable<String, DataFile>(DataFileFactory.getInstance().getDflAbsPath());
			Enumeration e = ht.elements();
			while(e.hasMoreElements()) {
			    // get the next data file
			    DataFile df = (DataFile) e.nextElement();
			    String dfid = df.getOid();
			    if (iList.contains(dfid))
			    	df.setIsObsolete("TRUE");
			}
		}
	}
	
    /**
	 * @throws FatalException
	 * 
	 */
	private void cleanDisposablesFromDatabase() throws FatalException {
		
		// open a database connection
		TransactionConnection con = DBConnection.getSharedConnection();
		
        // select which files are disposable
		
		// get $dfids from migration relationships for these datafiles
		/*
		 * select DFID_1 as pred, DFID_2 as succ
		 * from RELATIONSHIP r, DATA_FILE df
		 * where r.REL_TYPE = 'MIGRATE_TO' and
		 *       df.IEID = $ieid and
		 *       df.DFID in (r.DFID_1, r.DFID_2);
		 */
		Map<String, String> migrations = new Hashtable<String, String>();
		try {
			String select = String.format("%s as pred, %s as succ", ArchiveDatabase.COL_RELATIONSHIP_DFID_1, ArchiveDatabase.COL_RELATIONSHIP_DFID_1);
			String from = String.format("%s r, %s df", ArchiveDatabase.TABLE_RELATIONSHIP, ArchiveDatabase.TABLE_DATA_FILE);
			String whereRelType = String.format("r.%s = 'MIGRATE_TO'", ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE);
			String whereIeid = String.format("df.%s = %s", ArchiveDatabase.COL_DATA_FILE_IEID, SqlQuote.escapeString(getIntEnt().getOid()));
			String whereDfid = String.format("df.%s in (r.%s, r.%s)", ArchiveDatabase.COL_DATA_FILE_IEID, ArchiveDatabase.COL_RELATIONSHIP_DFID_1, ArchiveDatabase.COL_RELATIONSHIP_DFID_1);
			String sql = String.format("select %s from %s where %s and %s and %s", select, from, whereRelType, whereIeid, whereDfid);
			
			ResultSet r = con.executeQuery(sql);
			while(r.next()) {
				String pred = r.getString("pred");
				String succ = r.getString("succ");
				migrations.put(pred, succ);
			}
			r.close();
		} catch (SQLException e) {
			StackTraceElement element = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(this, element.getMethodName(), "database exception", "cleaning disposable files", e);
		}
		
		Set<String> predicessors = migrations.keySet();
		Collection<String> successors = migrations.values();
		Set<String> intermediates = new HashSet<String>();
		intermediates.addAll(predicessors);
		intermediates.retainAll(successors);

        // mark them obsolete & clean thier storage descriptions
		if(!intermediates.isEmpty()) {
			List<String> iList = new Vector<String>(intermediates);
			String first = iList.get(0);
			List<String> rest = iList.subList(1, iList.size() - 1);
			StringBuffer buffer = new StringBuffer(first);
			for (String dfid : rest) {
				buffer.append(", " + SqlQuote.escapeString(dfid));
			}
			String disposables = buffer.toString();
		
			/*
			 * update DATA_FILE
			 * set IS_OBSOLETE = 'TRUE'
			 * where DFID in $disposables;
			 */
			String updateSql = String.format("update %s set %s = 'TRUE' where %s in (%s)", 
					ArchiveDatabase.TABLE_DATA_FILE, 
					ArchiveDatabase.COL_DATA_FILE_IS_OBSOLETE, 
					ArchiveDatabase.COL_DATA_FILE_DFID,
					disposables);			
			
			try {
				con.executeQuery(updateSql);
			} catch (SQLException e) {
				StackTraceElement element = new Throwable().getStackTrace()[0];
				Informer.getInstance().fail(this, element.getMethodName(), "database exception", "cleaning disposable files", e);
			}
		}
		
		// commit the transaction
		con.commitTransaction();
	}

	/**
     * Builds the AIP object based on DB query
     * 
     * @param ieid The ieid of the package used to query the db
     * @throws PackageException
     * @throws FatalException
     */
    private void buildFromDB(String ieid) throws PackageException, FatalException {
        String methodName = "buildFromDB(String)";
        if (ieid == null) {
            Informer.getInstance().fail(
            		this,
                    methodName,
                    "Illegal argument",
                    "ieid: " + null,
                    new IllegalArgumentException());
        }
        try {
            // set the IntEntity object based on DB query for ieid
            this.setIntEnt(IntEntity.buildFromDB(ieid));
        } catch (NoSuchIEIDException e) {
            Informer.getInstance().error(this,
                    methodName,
                    "IEID does not exist in the database",
                    "ieid: " + ieid,
                    e);
        }
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
            + this.getPackageName() + File.separator;
        // copy the package
        this.copy(workingDir);
        // RESET paths after copy is successful
        // reset the packagePath
        this.setPackagePath(ArchiveProperties.getInstance().getArchProperty("INGEST_WORKPATH")
                + this.getPackageName());
        // update the work path
        this.setWorkingPath(workingDir + getIntEnt().getOid());
        // issue informational message
        Informer.getInstance().info(this,
            "copyToWorkDir()",
            "Package copied to work directory",
            this.getPackageName(),
            false);
    } // end copyToWorkDir        
           
    
    /**
     * Returns this AIP's IEID based on the value provided in the descriptor mDesc.
     * 
     * Precondition: mDesc is a valid, METS-based descriptor
     * Postcondition: mDesc has 1 IEID and this.IEID is returned; mDesc has 0 IEIDs, this.IEID is
     * not set, and a PackageException is thrown; mDesc has more than 1 IEID, this.IEID is not
     * set, and a PackageException is thrown.
     * 
     * @param mDesc
     * @return an ieid if only one exists in the descriptor, or null if 0 or multiple ieids exist (an exception is also thrown in this case)
     * @throws PackageException
     * @throws FatalException
     */
    private String getIEIDFromDescriptor(METSDescriptor mDesc) throws PackageException, FatalException {
        String methodName = "getIEIDFromDescriptor(METSDescriptor)";
        // get intellectual entity ids from the descriptor
        Vector <String> ieids = mDesc.getMetsDocument().getIntEntityIDs();
        // make sure there's only one IEID
        switch (ieids.size()) {            
            case 0: 
                // no IEIDs in descriptor - throw an error
                Informer.getInstance().error(this,
                        methodName,
                        "Unable to select IEID from descriptor: descriptor contains no IEIDs.",
                        mDesc.getPath(),
                        new PackageException());
                break;
            case 1:
                // exactly 1 IEID in descriptor - just right
                return ieids.firstElement();
            default:
                // more than 1 IEID in descriptor - which one to use? throw an error
                Informer.getInstance().error(this,
                        methodName,
                        "Unable select IEID from descriptor: descriptor contains multiple IEIDs.",
                        mDesc.getPath(),
                        new PackageException());
                break;
        }
        
        // if case1 not reached, method returns null
        return null;
    }
    
    /**
     * @throws PackageException
     * @throws FatalException
     */
    private void build() throws PackageException, FatalException {
        String methodName = "build()";
        
        // the archive descriptor is most likely already set, only search
        // for one if necessary
        if (this.hasArchiveDescriptor == false || this.getArchiveDescriptor() == null) {
            findArchiveDescriptor();
            
        }
        
        // set this to already be archived
        getArchiveDescriptor().setHasArchived(true);
        
        // make sure INITIAL descriptor is present, if none is 
        // found, an exception will be thrown
        findInitialDescriptor();
        
        IntEntity intE = getIntEnt();
        if (intE != null)
        	intE.setDescPath(getInitialDescriptor().getPath());
        
        // create archive directories
        createArchiveDirectories();
        
        // Set the working directory such that XML Validator know where to look for the schema files
        String path = getArchiveDescriptor().getPath();
        Integer index = path.lastIndexOf("/");
        path = path.substring(0, index);
        System.setProperty("user.dir", path);
        // parse the intitial descriptor to get lite DataFile objects
        getInitialDescriptor().extractContent();

        // reset SIP state
        setState(InformationPackage.SIP_DESC_CONTENT_EXTRACTED);
        
        // get the nearly-complete list of DataFiles (note:
        // the aipDescriptor itself has not been created yet
        // and will have to be added to the DataFile list 
        // separately
        setDfl(DataFileFactory.getInstance(this).createAipDataFiles());

        // check dfl size
        if (getDfl().size() < 1) {
            // the package contains no archivable files, which is extremely 
            // unlikley since all packages have descriptors which are xml files
            // and all partners must have agreements for xml files. But just in case...            
            Informer.getInstance().error(
                this,
                methodName,
                "DataFile list is empty",
                getPackageName(),
                new EmptyPackageException("Package does not contain any \'archivable\' data files"));
        }
                        
        // set the state of this package
        setState(InformationPackage.SIP_BUILT);     
    } // end build        
        
	/**
	 * Transforms this AIP by migrating, normalizing, and localizing all files.
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 */
	private void transform() throws FatalException, PackageException{
	    this.migrate();
	    this.normalize();
	    this.localize();
	}
	
	/**
	 * Localizes all data files in the dfl. This is a 2-pass operation, localize
	 * needs to be called twice for every data file. 
     * @throws FatalException
     * @throws PackageException
	 */
	private void localize() throws FatalException, PackageException {
	    // create a snapshot of the dfl
	    Hashtable<String, DataFile> ht = new Hashtable<String, DataFile>(DataFileFactory.getInstance().getDflAbsPath());
		Enumeration e = ht.elements();
		
		// reorder the DataFile objects so that all children are 
		// localized before their parent
		Vector files = createDataFilePrecedenceList(e);
		
		// localize pass 1
		Iterator iter = files.iterator();	
        while (iter.hasNext()) {
            DataFile df = (DataFile)iter.next();
            // only localized those data files that has not archived yet
            if (!df.hasArchived())
            	df.localize();
            else
            	df.locateLocalizedCopy();
        }
		
        ht = new Hashtable(DataFileFactory.getInstance().getDflAbsPath());
        e = ht.elements();
        files = createDataFilePrecedenceList(e);
        iter = null;
		// localize pass 2
		iter = files.iterator();	
        while (iter.hasNext()) {
            DataFile df = (DataFile)iter.next();
            // only localized those data files that has not archived yet
            if (!df.hasArchived())
            	df.localize();
            else
            	df.locateLocalizedCopy();
        }
	}
	
    /**
     * Moves the package to the AIP output directory. 
     * @throws FatalException
     * @throws PackageException
     * @return true if the move was complete, else false
     */
    public boolean moveAIPToOutputDir() throws FatalException, PackageException {
    String methodName = "moveToOutputDir()";
    String outputPath = ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH");
    Informer.getInstance().info(this,
			    methodName,
			    "Moving package to output directory: " + outputPath,
			    this.getPackageName(),
			    false);
    
	// get a snapshot (copy) of the dfl (the real dfl will be altered later)
	Hashtable <String, DataFile> ht = new Hashtable<String, DataFile>(DataFileFactory.getInstance().getDflAbsPath());
	Enumeration e = ht.elements();
	while(e.hasMoreElements()) {
	    // get the next data file
	    DataFile df = (DataFile) e.nextElement();

	    // only copy non-disposable files over
	    if (!df.isObsolete().equalsIgnoreCase("TRUE")) {		            
	    	// get the DataFile's current path
			String src = df.getPath();
			// determine the destination path
			String dest = outputPath;
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
			    Informer.getInstance().info(df.getPath() + " is not moved", "file not moved");	       
	    }		    
	}
		
	// update the package path
	this.setPackagePath(outputPath);		
		
	// if we made it here, then the copy and the delete were successful
	return true;
    }
	/**
	 * Rejects a package by calling IP.move(REJECT_DIR) and logging the errors.
	 * TBD where the REJECT_DIR is stored. Logs something.
	 */
	public void reject() throws FatalException {
		this.setState(InformationPackage.SIP_REJECTED);
	} // end reject        

	/**
	 * 
     * Method to persist the AIP in the database.
     *  
     * @param gfp
     * @return the number of rows affected by the insert/update
     * @throws FatalException
     * @throws PackageException
     */
    private int dbUpdate(GlobalFilePackage gfp) throws FatalException, PackageException{
        String methodName = "dbUpdate(GlobalFilePackage)";
        int rowsAffected = 0;
                
        // create new ingest event for SIP
        Events.getInstance().add(new Event(getIntEnt().getOid(), 
                Event.EVENT_INGESTED, DateTimeUtil.now(), "SIP re-ingest",Event.OUTCOME_SUCCESS,
                "", null));
        
        // start the transaction
        TransactionConnection tcon = DBConnection.getSharedConnection();
        tcon.startTransaction();
                
        // insert the gfp's intellectual entity
        if (gfp != null && gfp.getNewGlobalFiles().size() != 0) {
            // create new ingest event for GFP
            Events.getInstance().add(new Event(gfp.getIntEnt().getOid(), 
                    Event.EVENT_INGESTED, DateTimeUtil.now(), "GFP ingest",Event.OUTCOME_SUCCESS,
                    "", null));         
            rowsAffected += gfp.getIntEnt().dbUpdate(ArchiveManager.getInstance().getArchiveAction());
        }
        
        // insert/update all data files
        rowsAffected += DataFileFactory.getInstance(this).dbUpdate(ArchiveManager.getInstance().getArchiveAction());
        
        // insert/update all global files (old and new)
        rowsAffected += GlobalFiles.getInstance().dbUpdate(ArchiveManager.getInstance().getArchiveAction());
        
        // insert the global files used by the sip (INT_ENTITY_GLOBAL_FILE)
        rowsAffected += super.dbUpdate(ArchiveManager.getInstance().getArchiveAction());
        
        // insert the global files used by the gfp (INT_ENTITY_GLOBAL_FILE)
        if (gfp != null && gfp.getNewGlobalFiles().size() != 0) {   
            rowsAffected += gfp.dbUpdate(ArchiveManager.getInstance().getArchiveAction());
        }
        
        // insert the events               
        rowsAffected += Events.getInstance().dbUpdate(ArchiveManager.getInstance().getArchiveAction());     
        
        // insert relationships
        rowsAffected += Relationships.getInstance().dbUpdate(ArchiveManager.getInstance().getArchiveAction());      
        
		// insert the IEID & gfp copies
		StorageHelper.dbInsertCopies(tcon, this.getIntEnt().getOid(), copies, getCopyMD5());
		
    	if (DataFileFactory.getInstance().getGfp() != null && DataFileFactory.getInstance().getGfp().copies != null) {
    		GlobalFilePackage gfpg = DataFileFactory.getInstance().getGfp();
    		List<Copy> gCopies = gfpg.copies;
    		String gOid = gfpg.getIntEnt().getOid();
    		String gfpCopyMD5 = gfpg.getCopyMD5();
    		StorageHelper.dbInsertCopies(tcon, gOid, gCopies, gfpCopyMD5);
    	}

        Informer.getInstance().info(this, 
                methodName, 
                "Start transaction commit", 
                "Commit transaction", 
                false);
        
        // commit the transaction
        tcon.commitTransaction();
        
        Informer.getInstance().info(this, 
                methodName, 
                "End transaction commit", 
                "Commit transaction", 
                false);
        
        try {
            tcon.close();
        } catch (SQLException e) {
            Informer.getInstance().warning(this,
                    methodName,
                    "Error closing database connection: " + e.getMessage(),
                    "Shared transacton connection");
        }
        
        return rowsAffected;
    }
    
    /**
     * Main method for testing.
     * 
     * @param args Command line arguments
     * @throws PackageException
     * @throws FatalException
     */
    public static void main(String[] args) throws PackageException, FatalException {
        String packagePath = "/daitss/dev/data/ingest/in/WF00000028";
        AIP aip = new AIP(packagePath);
        aip.copyToWorkDir();
        try {
            aip.ingest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("IntEntity: " + aip.getIntEnt());
    }
} // end AIP
