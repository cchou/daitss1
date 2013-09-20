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


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.EmptyPackageException;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.FileRole;
import edu.fcla.daitss.file.distributed.IntegrityTaskManager;
import edu.fcla.daitss.file.global.GlobalFile;
import edu.fcla.daitss.file.global.GlobalFiles;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.report.Reporter;
import edu.fcla.daitss.storage.StorageHelper;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.Silo;

/**
 * A Submission Information Package
 */
public class SIP extends InformationPackage {

	/**
	 * The descriptor created on ingest describing all files and only files to
	 * be archived (this does not include normalized files).
	 */
	private METSDescriptor aipDescriptor;


	
	/**
	 * Sets this.packagePath equal to packagePath, which is the absolute path
	 * to the package directory before it's copied to the working directory.
	 * Ex: &quot;/DLArch2/ready/UFE0000001/&quot;.  Determines packageName based on
	 * packagePath (ex: &quot;UFE0000001&quot;), sets this.packageName.
     * 
     * @param packagePath absolute path to the package\
     * @throws FatalException
	 */
	public SIP(String packagePath) throws FatalException{
		// initialize superclass variables.
		super(packagePath);
        // create and set the intellectual entity
        setIntEnt(new IntEntity());        
		// set the state of this SIP
		setState(InformationPackage.SIP_LITE);
	} // end SIP        
	
	/**
	 * Descriptor desc = findInitialDescriptor(); if (desc == null) and
	 * (ArchiveProperties.getInstance().getProperty(&quot;CREATE_DESC&quot;) == true)
	 * then call desc = METSDescriptor.createInitialDescriptor(packagePath,
	 * packageName+&quot;.xml&quot;, DEFAULT_ACCOUNT, DEFAULT_SUBACCOUNT, DEFAULT_PROJECT))
	 * 
	 * else if (desc == null) and
	 * (ArchiveProperties.getInstance().getProperty(&quot;CREATE_DESC&quot;) == false)
	 * then throw DescriptorNotFoundException
	 * 
	 * if (desc != null) call result = desc.isValid(); set
	 * this.hasInitialDescriptor to true and set its initialDescriptor member
	 * to desc.
	 * 
	 * Then it calls initialDescriptor.extractContent(); call dflLite =
	 * initialDescriptor.getDflLite();
	 * 
	 * calls Db.dbStartTransaction();
	 * 
	 * create a new DataFileFactory using new DataFileFactory(dflLite), sets
	 * this.iEnt = initialDescriptor.getIEnt();
	 * 
	 * calls this.agreements.getAoCode(this.iEnt.getAccount(),
	 * this.iEnt.getProject()) -  if it returns an integer set
	 * it in this.iEnt.ap and continue, else throw the UnknownApException.
	 * 
	 * calculate directory names for linksDir, migratedDir and normalizedDir
	 * using a timestamp scheme (ex: links_[timestamp]) and set each in this SIP
	 * using setLinksDir(String), etc.;
	 * 
	 * recursively iterates through the packagePath creating a DataFile object
	 * df for each physical file (using this.dff.createDataFile(filename,
	 * lObject)) (might throw a PackageException); (NOTE: do not iterate
	 * through the linksDir, migratedDir or normalizedDir - DataFile objects
	 * are built for the files in these directories by other DataFile objects).
	 * Set each df.account, df.subaccount, df.project, and df.ap equal to the
	 * lObject's.
	 * 
	 * after iterating through the directory calls this.dfl = dff.getDfl();
	 * calls sipDescriptorLite = initialDescriptor.createSIPDescriptor(dfl,
	 * filename); calls sipDescriptor =
	 * dff.createDataFile(sipDescriptorLite.getFileName(), lObject) to create
	 * the DataFile object for the aipDescriptor (might throw a
	 * PackageException); call this.lObject.setDescDFID(sipDataFile.getOid());
	 * get the dfl again (this.dfl = dff.getDfl()). Iterate through the dfl and
	 * if the datafile's ap == -1 set the ap to FCLA's AP code and set the
	 * account to &quot;FCLA&quot; and project to &quot;DAITSS&quot;.
	 * 
	 * @throws PackageException
     * @throws FatalException
	 */
	public void build() throws PackageException, FatalException {
		String methodName = "build()";
        // see if there's an AIP descriptor, implying that the package is 
        // being re-ingested
        findArchiveDescriptor();        
		// make sure initial descriptor is present, if none is 
		// found, an exception will be thrown
		findInitialDescriptor();
		// create archive directories
		createArchiveDirectories();
		// parse the intitial descriptor to get lite DataFile objects 
		// and IntEntity
		getInitialDescriptor().extractContent();
		// reset SIP state
		setState(InformationPackage.SIP_DESC_CONTENT_EXTRACTED);
		// set the IntEntity for this SIP
        getInitialDescriptor().populateIntEntity(getIntEnt());
		// setIntEnt(getInitialDescriptor().getIntEnt());
		// create the DataFileFactory
		// get the nearly-complete list of DataFiles (note:
		// the aipDescriptor itself has not been created yet
		// and will have to be added to the DataFile list 
		// separately
		setDfl(DataFileFactory.getInstance(this)
			.createDataFiles(ArchiveManager.ACTION_INGEST));
				
		// check dfl size
		if (getDfl().size() < 1) {
			// the package contains no archivable files, which is extremely 
			// unlikley since all packages have descriptors which are xml files
			// and all partners must have agreements for xml files. But just 
			// in case...			 
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
	 * Converts a Vector representing new global files to a Hashtable that uses the absolute path
	 * to the GlobalFile as the key and the GlobalFile itself as the value. If an element of the 
	 * Vector is not a GlobalFile, a FatalException will be thrown.
     * 
	 * @param v A Vector of GlobalFiles
	 * @return Hashtable absolutePath -> GlobalFile
     * @throws FatalException
	 */
	private Hashtable<String, GlobalFile> convertGlobalFilesVectorToHashtable(Vector v) throws FatalException {
		String methodName = "convertGlobalFilesVectorToHashtable(Vector)";
		Hashtable<String, GlobalFile> ht = null;
		Iterator iter = v.iterator();
		while (iter.hasNext()) {
			Object o =  iter.next();
			if (!(o instanceof GlobalFile)) {
				Informer.getInstance().fail(
					this,
					methodName,
					"Unexpected object type in Vector - expected GlobalFile",
					o.getClass().getName(),
					new ClassCastException("Expected GlobalFile object"));
			}
			GlobalFile gf = (GlobalFile)o;
			// if there are two GlobalFile objects with the same path (i.e. GlobalFile added more than
			// once), then the first GlobalFile will be overwritten with the next GlobalFile
			ht.put(gf.getPath(), gf);
		}		
		return ht;
	}
	
	/**
     * Method to persist the SIP in the database.
     *  
     * @param gfp
     * @return the number of rows affected by the insert/update
     * @throws FatalException
     * @throws PackageException
     */
    private int dbUpdate(GlobalFilePackage gfp) throws FatalException, PackageException{
        String methodName = "dbUpdate(SIP, GlobalFilePackage)";
        int rowsAffected = 0;
                
        // create new ingest event for SIP
        Events.getInstance().add(new Event(getIntEnt().getOid(), 
                Event.EVENT_INGESTED, DateTimeUtil.now(), "SIP ingest",Event.OUTCOME_SUCCESS,
                "", null));
        
        // start the transaction
        TransactionConnection tcon = DBConnection.getSharedConnection();
        tcon.startTransaction();
        
        // insert the sip's intellectual entity
        rowsAffected +=  getIntEnt().dbUpdate(ArchiveManager.getInstance().getArchiveAction());
                
        // insert the gfp's intellectual entity
        if (gfp != null && gfp.getNewGlobalFiles().size() != 0) {
            // create new ingest event for GFP
            Events.getInstance().add(new Event(gfp.getIntEnt().getOid(), 
                    Event.EVENT_INGESTED, DateTimeUtil.now(), "GFP ingest",Event.OUTCOME_SUCCESS,
                    "", null));         
            rowsAffected += gfp.getIntEnt().dbUpdate(ArchiveManager.getInstance().getArchiveAction());
        }
        
        // insert all data files
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
        
        // insert the relationships
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
	 * @return The AIP desciptor for this SIP once it has been 
	 * converted to an AIP
	 */
	public METSDescriptor getAipDescriptor() {
		return aipDescriptor;
	}
    
    
    private DataFile generateSipAipDesc() throws FatalException, PackageException{
        
        String aipDescPath = FileUtil.getUniqueName(getWorkingPath()  
            + ArchiveProperties.getInstance().getArchProperty("AIP_DESC_NAME")
            + "_" + DateTimeUtil.getDateTimeStamp()  + "." + Extension.EXT_XML);                
                
        METSDescriptor aipDesc = METSDescriptor.createAipDescriptor(aipDescPath, this);
        // set the intellectual entity
        aipDesc.setIntEnt(getIntEnt());
        
        DataFile df = DataFileFactory.getInstance(this).createDataFile(null, aipDesc.getPath(), false, aipDesc.getPath());
        // set DataFile's role for AIPDescriptor
        df.setRole(FileRole.ROLE_DESCRIPTOR_AIP);
        return df;
    }
    

    /**
     * Ingests a SIP. Handles most aspects of SIP ingest including 
     * transformations (migration, normalization, localization), package
     * moving, descriptor creation, and database updating.
     * 
     * @throws FatalException
     * @throws PackageException
     * @throws IOException
     */
	public void ingest() throws FatalException, PackageException, IOException {
        String methodName = "ingest(SIP)";
        Informer.getInstance().info2("SIP ingest began at " + DateTimeUtil.now(),
                "SIP: " + getPackageName());
        
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
        
        buildCopies();
        
        Informer.getInstance().info(this,
                methodName,
                "Creating AIP descriptor",
                getPackageName(),
                false);
        
        // create AIPDescriptor and add to dfl                              
        DataFile aipDescDf = generateSipAipDesc();             
                        
        // create GFPDescriptor and add to dfl
        GlobalFilePackage gfp = DataFileFactory.getInstance(this).getGfp();
        DataFile gfpDescDf = null;
        if (gfp != null) {
            Informer.getInstance().info(this,
                    methodName,
                    "Creating GFP descriptor",
                    gfp.getPackageName(),
                    false);

            gfpDescDf = gfp.generateGfpAipDesc();
        }                                       
        
        //***********************************************************
        // aip descriptor and gfp descriptor now contain information 
        // about all Sip files including their migrated, normalized, and 
        // localized versions
        //***********************************************************
        //***********************************************************
        // dfl contains all SIP files including their migrate, normalized, 
        // and localized versions, the aip descriptor and its links, and
        // the gfp descriptor and its links
        //***********************************************************
        
        
        // migrate, normalize, localize all files created as a result of 
        // aip and gfp descriptor creation (i.e. the links and their transformed 
        // versions)
        transform();

        //***********************************************************
        // dfl now contains the SIP and all of its transformed files, 
        // the aip descriptor and all of its transformed versions, the
        // aip descriptor's links and all of their transformed versions, 
        // the gfp descriptor and all of its transformed versions, and
        // the gfp descriptors links and all of their transformed files.
        //***********************************************************
        
        
        // remove the aip and gfp descriptors and their localized versions 
        // from the dfl (by checksum and absPath)
        // remove aip desc
        aipDescDf = DataFileFactory.getInstance().removeDfByAbsPath(aipDescDf.getPath(), true);
        // remove the localized version of the aipDesc
        DataFileFactory.getInstance().removeDfByAbsPath(aipDescDf.getLocalizedFilePath(), true);
        // clear the old aipDescDf
        aipDescDf = null;
        
        // remove the gfp desc
        if (gfpDescDf != null) {
            // remove the gfp desc from the collection of all DataFiles
            gfpDescDf = DataFileFactory.getInstance().removeDfByAbsPath(gfpDescDf.getPath(), true);
            // remove all global references to the global file: gfp desc
            removeGlobalReferences(gfpDescDf, gfp, this);                               
            
            // remove the localized version of the gfp desc from the collection of all DataFiles
            DataFile locGfpDesc = DataFileFactory.getInstance().removeDfByAbsPath(gfpDescDf.getLocalizedFilePath(), true);
                               
            // remove all references to the global file: localized gfp desc
            if (locGfpDesc != null) {                
                removeGlobalReferences(locGfpDesc, gfp, this);
            }                                                               
            
            // clear the old gfp desc
            gfpDescDf  = null;
            locGfpDesc = null;
        }
        
        //***********************************************************
        // dfl now contains the SIP and all of its transformed files, 
        // the aip descriptor's links and all of its transformed versions, 
        // and the gfp descriptors links and all of its transformed files.
        //***********************************************************                               
        
        // recreate the aipDesc
        aipDescDf = generateSipAipDesc();
                                            
        // recreate the gfpDesc
        if (gfp != null && gfp.getNewGlobalFiles().size() != 0) {
            gfpDescDf = gfp.generateGfpAipDesc();
        }
        
        
        // re-localize the package (which now contains the AIP descriptor(s) and 
        // all of their links)
        // It is very important to localize the entire package again, rather than
        // just the descriptors themselves, in order that children be localized
        // before their parents
        localize();
        
        
        //***********************************************************
        // dfl now contains the SIP and all of its transformed files,
        // the new aip descriptor and its localized version,
        // the aip descriptor's links and all of its transformed versions
        // (note the links from the original aip desc are the same as those
        // from the new aip desc), 
        // the new gfp descriptor and its localized version, 
        // and the gfp descriptors links and all of its transformed files
        // (same situation as sip descriptor links).
        //***********************************************************                               
        
        // remove the aip desc from the dfl, leaving only its localized version
        // which is the new aip desc
        DataFileFactory.getInstance().removeDfByAbsPath(aipDescDf.getPath(), true);
        
        // remove the gfp desc from the dfl, leaving only its localized version
        // which is the new gfp desc
        if (gfpDescDf != null) {                
            // remove the gfp desc from the collection of all DataFiles
            DataFileFactory.getInstance().removeDfByAbsPath(gfpDescDf.getPath(), true);
            // remove all references to the gfp desc, remember
            // we're really interested in the localized version.                
            removeGlobalReferences(gfpDescDf, gfp, this);                
        }
                
        //***********************************************************
        // dfl now contains the SIP and all of its transformed files,
        // the localized version of the new aip descriptor,
        // the aip descriptor's links and all of its transformed versions
        // (note the links from the original aip desc are the same as those
        // from the new aip desc), 
        // the localized version of the new gfp descriptor, 
        // and the gfp descriptors links and all of its transformed files
        // (same situation as sip descriptor links).
        //***********************************************************       
        
        // check Distributed object integrity and record results in database
        new IntegrityTaskManager(
                DataFileFactory.getInstance().getDflChecksum().values()).process();                 
        
        // move SIP to output directory
        moveToOutputDir();
                        
        // move GFP to output directory 
        if (gfp != null && gfp.getNewGlobalFiles().size() != 0) {  
            gfp.storeLocally();
            gfp.buildCopies();
            gfp.moveToOutputDir();                  
        }                               
            
        // all contents of SIP and GlobalFilePackage have been moved, delete the 
        // directories remaining in the work dir
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_INGESTED_FROM_WORK_DIR").equals("true")){
            clearWorkDir();
        }
                        
        // copy package to long-term storage if desired
        store();
                       
        // start transaction, update tables
        dbUpdate(gfp);
         
        /* Report on this ingest */
        Reporter.reportIngest(getIntEnt(), false);               
                        
        // delete package from input and output directories
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_INGESTED_FROM_WORK_DIR").equals("true")){
            clearWorkDir();
        }

        // clear out directory
        clearOutDir();
       
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_INGESTED_FROM_IN_DIR").equals("true")) {
            FileUtil.delete(ArchiveProperties.getInstance().
                    getArchProperty("INGEST_INPATH") + getPackageName());
        }
        
        Informer.getInstance().info(this,
                methodName,
                "SIP ingest finished at " + DateTimeUtil.now(),
                "SIP: " + getPackageName(),
                true);
    
	} // end ingest        

	private void buildCopies() throws FatalException {
	    copies = new Vector<Copy>();
	    List<Silo> silos = StorageHelper.activeSilos();
	    if (silos.isEmpty()) {
	        Informer.getInstance().fail("No active silos in Archive", "making storage urls", new FatalException());
	    }
        for (Silo silo : silos) {
        	copies.add(new Copy(silo, tarballName()));    
        }
    }

    /**
	 * Localizes all data files in the dfl. This is a 2-pass operation, localize
	 * needs to be called twice for every data file. This method's visibility is 
	 * package-level so that SIPList can access it.
     * 
     * @throws FatalException
     * @throws PackageException
	 */
	void localize() throws FatalException, PackageException {
	    // create a snapshot of the dfl
	    Hashtable<String, DataFile> ht = new Hashtable<String, DataFile>(DataFileFactory.getInstance().getDflAbsPath());
		Enumeration e = ht.elements();
		
		// reorder the DataFile objects so that all children are 
		// localized before their parent
		Vector files = createDataFilePrecedenceList(e);
		
		// localize pass 1
		//System.out.println(">>>>>>>>>>>>>LOCALIZE PASS 1<<<<<<<<<<<<<<<<");
		Iterator iter = files.iterator();	
        while (iter.hasNext()) {
            DataFile df = (DataFile)iter.next();
            df.localize();
        }
		
        ht = new Hashtable(DataFileFactory.getInstance().getDflAbsPath());
        e = ht.elements();
        files = createDataFilePrecedenceList(e);
        iter = null;
		// localize pass 2
		//System.out.println(">>>>>>>>>>>>>LOCALIZE PASS 2<<<<<<<<<<<<<<<<");
		iter = files.iterator();	
        while (iter.hasNext()) {
            DataFile df = (DataFile)iter.next();
            df.localize();
        }
		
        
        
		/*
		System.out.println(">>>>>>>>>>>>>LOCALIZE PASS 1<<<<<<<<<<<<<<<<");
		while (e.hasMoreElements()) {
			DataFile df = (DataFile) e.nextElement();
			df.localize();
		}	    
		
		e = null;
		e = ht.elements();
		System.out.println(">>>>>>>>>>>>>LOCALIZE PASS 2<<<<<<<<<<<<<<<<");
		while (e.hasMoreElements()) {
			DataFile df = (DataFile) e.nextElement();
			df.localize();
		}	
		*/		    	
	}	
	

	


	/**
	 * Rejects a package. If (delFromWorkPath == true) it calls
	 * this.delete(this.workPath); if (delFromOutPath == true) it calls
	 * this.delete(this.outPath); and logs the errors. Calls
	 * this.dbRollbackTransaction() and then this.dbDisconnect().
	 *
     * @throws FatalException
	 */
	public void reject() throws FatalException {
		this.setState(InformationPackage.SIP_REJECTED);
	} // end reject        

	/**
	 * Sets the aipDescriptor
	 * @param descriptor
	 */
	public void setAipDescriptor(METSDescriptor descriptor) {
		aipDescriptor = descriptor;
	}

	/**
     * Returns a String representation of the SIP
     * @return String 
	 */
    public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getPackagePath());
		return sb.toString();
	}

	
	/**
	 * Transforms this SIP by migrating, normalizing, and localizing all files.
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void transform() throws FatalException, PackageException{
	    this.migrate();
	    this.normalize();
	    this.localize();
	}
    
	

} // end SIP
