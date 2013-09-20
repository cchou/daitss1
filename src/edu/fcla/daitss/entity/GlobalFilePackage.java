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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.FileRole;
import edu.fcla.daitss.file.global.GlobalFile;
import edu.fcla.daitss.file.global.GlobalFiles;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.storage.StorageHelper;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.Silo;

/**
 * The GlobalFilePackage class is used for storing and ingesting
 * newly identified global files. New global files may be recognized 
 * (based on file title) during ingest of a deposited package (and
 * possibly during migration or dissemination, although this has 
 * yet to be determined). The DataFileFactory stores an instance of 
 * GlobalFilePackage.
 *  
 * @author vicary
 */
public class GlobalFilePackage extends InformationPackage {
	
	// the information package whose processing resulted in the creation of
	// this GlobalFilePackage
	private InformationPackage ip = null;
	
	private Hashtable <String, GlobalFile> newGlobalFiles = null;
    
	/**
	 * @param ip 
	 * @throws FatalException 
	 * @throws PackageException 
	 * 
	 */
	public GlobalFilePackage(InformationPackage ip) 
		throws FatalException, PackageException {
		super(ip.getPackagePath());
		String methodName = "GlobalFilePackage(InformationPackage)";
		// make sure ip isn't null
		if (ip == null) {
		    Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "ip: " + ip,
                    new IllegalArgumentException());
		}
		
		// set the ip
		setIp(ip);
		// init the newGlobalFiles Vector
		this.newGlobalFiles = new Hashtable <String, GlobalFile> ();
		// create the IntEntity
		IntEntity ie = new IntEntity();
		ie.setPackageName(ip.getPackageName());
		// the AP for a GlobalFilePackage's intellectual entity will be the archive's AP
		ie.setAp(Agreements.getApId(ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT"),
		        ArchiveProperties.getInstance().getArchProperty("DAITSS_PROJECT")));
		
		setIntEnt(ie);
		// set the packagePath
		setPackagePath(ip.getPackagePath() + getIntEnt().getOid());
		// set the packageName
		setPackageName(ip.getPackageName());
        setWorkingPath(getPackagePath());
	}

	/**
	 * @param ip 
	 * @param gfpIeid - the ieid of the global file package
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	public GlobalFilePackage(InformationPackage ip, String gfpIeid) 
		throws FatalException, PackageException {
		super(ip.getPackagePath());
		String methodName = "GlobalFilePackage(InformationPackage)";
		// make sure ip isn't null
		if (ip == null) {
		    Informer.getInstance().fail( this,methodName,
                "Illegal argument","ip: " + ip, new IllegalArgumentException());
		}
		
		// set the ip
		setIp(ip);
		// init the newGlobalFiles Vector
		this.newGlobalFiles = new Hashtable <String, GlobalFile> ();
		// create the IntEntity
		IntEntity ie;
        try {
            // set the IntEntity object based on DB query for ieid
    		ie = IntEntity.buildFromDB(gfpIeid);
    		ie.setPackageName(ip.getPackageName());
    		// the AP for a GlobalFilePackage's intellectual entity will be the archive's AP
    		ie.setAp(Agreements.getApId(ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT"),
    		        ArchiveProperties.getInstance().getArchProperty("DAITSS_PROJECT")));
    		
    		setIntEnt(ie);
        } catch (NoSuchIEIDException e) {
            Informer.getInstance().error(this, methodName,
                    "IEID does not exist in the database",
                    "ieid: " + gfpIeid,
                    e);
        }

		// set the packagePath
		setPackagePath(ip.getPackagePath() + getIntEnt().getOid());
		// set the packageName
		setPackageName(ip.getPackageName());
        setWorkingPath(getPackagePath());
	}
	/**
	 * Adds a GlobalFile object to the newGlobalFiles Vector.
	 * If the GlobalFile is already present, no change is made to 
	 * the newGlobalFiles Vector. 
	 * 
	 * @param absPath absolute path to the new GlobalFile
	 * @param newGf a GlobalFile object to be added
	 */
	public void addNewGlobalFile(String absPath, GlobalFile newGf) {
		this.newGlobalFiles.put(absPath, newGf);		
	}    
	
	/**
	 * <p>
	 * Moves a GlobalFilePackage and its contents from its current location (packagePath) to 
	 * <code>toPath</code> by copying each GlobalFile to the new location. Once all GlobalFiles
	 * have been copied, they are deleted from their original location.  Keep the directory
	 * structure for the package and its contents the same as it was before
	 * moving it. Since all of the GlobalFiles in this package have been moved, the keys of the 
	 * globalFiles Hashtable will be inaccurate so a new Hashtable must be created with the correct 
	 * key values. The new Hashtable will overwrite the current globalFiles Hashtable. The same is 
	 * true of the dfl.
	 * If extricate is successful return <code>true</code>, else return
	 * <code>false</code>.
	 * </p>
	 * 
	 */
	/*
	public boolean extricate(String toPath) throws FatalException, PackageException {
		String methodName = "extricate(String)";
		String destPath = toPath;
		if (!destPath.endsWith(File.separator)) {
		    destPath = destPath + File.separator;
		}
		
		Enumeration e = getGlobalFiles().keys();
		String[] files = new String[getGlobalFiles().size()];
		int index = 0;
		while (e.hasMoreElements()) {
			files[index] = (String) e.nextElement();
			index++;
		}
				
		for (int i=0;i<files.length;i++) {
			String absPath = files[i];
			String destAbsPath = destPath +  absPath.substring(getPackagePath().length()) ;
			
			try {
				FileUtil.copyFile(absPath, destAbsPath);				
				
				// *************************************************************************
				// dereference the GlobalFile from the globalFiles hastable
				GlobalFile gf = (GlobalFile)getGlobalFiles().remove(absPath);								
				// change the GlobalFile's path
				gf.setPath(destAbsPath);			
				// add file to the new Hashtable
				// ht.put(destAbsPath, gf);
				addGlobalFile(destAbsPath, gf);
								
				//*************************************************************************
				// dereference the GlobalFile from the newGlobalFiles Hashtable				
				GlobalFile newGf = (GlobalFile)newGlobalFiles.remove(absPath);
				// change the GlobalFile's path
				gf.setPath(destAbsPath);
				// re-add the updated GlobalFile to newGlobalFiles using the new key
				addNewGlobalFile(destAbsPath, newGf);
				
				//	*************************************************************************
				// dereference the GlobalFile's DataFile counterpart and change its path too
				Hashtable dfl = DataFileFactory.getInstance().getDflAbsPath();
				// remove the DataFile (if there is one) from the dfl
				DataFile df = (DataFile)dfl.remove(absPath);
				if (df != null) {
					// update the DataFile's path
					df.setPath(destAbsPath);
					// put DataFile back in the dfl
					// NOTE since the dfl is passed by reference, this is actually changing the 
					// DataFileFactory's dfl as well as the SIPs dfl
					dfl.put(destAbsPath, df); 
				}
				
				// delete the globalFile from the package that initially contained it
				File f = new File(absPath);
				if (!f.delete()) {
					Informer.getInstance().error(
						this,
						methodName,
						"Unable to delete GlobalFile from the pacakge in which it was submitted",
						"absPath: " + absPath,
						new IOException("Unable to delete file"));
				}
			
			} catch (IOException ioe) {
				Informer.getInstance().error(
					this,
					methodName,
					"Error copying GlobalFile",
					"absPath: " + absPath,
					ioe);
			}						
		}
		// set the packagePath to the new packagePath
		this.setPackagePath(destPath);			
		return true;
	}
	*/
	/**
	 * <p>
	 * Unqualified package name. 
	 * Overrides InformationPackage.setPackageName(String)
	 * </p>
	 * 
	 */
	/*
	public void setPackageName(String packageName) throws FatalException {
		String methodName = "setPackageName(String)";
		if (!OIDServer.isValidIeid(packageName)){
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"packageName: " + packageName,
				new IllegalArgumentException());			
		}
		this.packageName = packageName;			
	} // end setPackageName
	*/
	/**
	 * 
	 * 
	 * @return InformationPackage object
	 */
    public InformationPackage getIp() {
        return ip;
    }
    
    /**
     * 
     * @return Datafile object
     * @throws FatalException
     * @throws PackageException
     */
    DataFile generateGfpAipDesc() 
        throws FatalException, PackageException {
    
        String gfpDescPath = FileUtil.getUniqueName(this.getWorkingPath() 
            + ArchiveProperties.getInstance().getArchProperty("GFP_DESC_NAME")  
            + "_" + DateTimeUtil.getDateTimeStamp() + "." + Extension.EXT_XML);
        
        METSDescriptor gfpDesc = METSDescriptor.createAipDescriptor(gfpDescPath, this);
        // set the intellectual entity
        gfpDesc.setIntEnt(getIntEnt());
        
        // GFP descriptors should be counted as global files
        DataFile df = DataFileFactory.getInstance().createDataFile(null, gfpDesc.getPath(), true, gfpDesc.getPath());
        //DataFile df = DataFileFactory.getInstance().createDataFile(null, gfpDesc.getPath(), false, gfpDesc.getPath());
        // set the role
        df.setRole(FileRole.ROLE_DESCRIPTOR_GFP);
        return df;
    }
    

	/**
	 * @return the newGlobalFiles Vector
	 */
	public Hashtable <String, GlobalFile> getNewGlobalFiles() {
		return newGlobalFiles;
	}
	

	/**
	 * Moves the GlobalFilePackage to the output directory. 
	 * @return boolean
	 * @throws FatalException
	 * @throws PackageException
	 */

	public boolean moveToOutputDir() throws FatalException, PackageException {
        // only move package to the output directory if there are new files to move
        if (getNewGlobalFiles().size() != 0) {
            return this.move(ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH") + this.getIntEnt().getOid());
        }
        return false;
	}
	
    /**
     * Removes a global files from the newGlobalFiles collection
     * 
     * @param absPath
     * @return GlobalFile object
     */
    public GlobalFile removeNewGlobalFile(String absPath) {
        return this.newGlobalFiles.remove(absPath);        
    }    
    
    /**
     * @param ip
     */
    public void setIp(InformationPackage ip) {
        this.ip = ip;
    }
    
    void storeLocally() throws PackageException, FatalException {
        
        String methodName = "storeLocally()";
        
	    String globalLocDir = ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR");
        
        Enumeration e = getNewGlobalFiles().elements();
		while(e.hasMoreElements()) {
		    // get the next data file
		    GlobalFile gf = (GlobalFile) e.nextElement();
		    
	    	String locCopy = globalLocDir + gf.getIp().getIntEnt().getOid() + File.separator
	    					+ gf.getPackagePath();
	    		
	    	try {
				FileUtil.copyFile(gf.getPath(), locCopy);
			} catch (IOException e1) {
				Informer.getInstance().error(this, 
						methodName, 
						"Unable to copy global file to local storage", 
						"fromPath: " + gf.getPath() + " toPath: " + locCopy, 
						e1);
			}			    
		}
    }
    
    /**
     * @see edu.fcla.daitss.entity.InformationPackage#toXML()
     */
    public Document toXML() throws FatalException {
        Document doc = super.toXML();
        Element rootElement = doc.getDocumentElement();
        
        /* Merge in Global Files */
        Document gfDoc = GlobalFiles.getInstance().toXML();
        NodeList nodes = XPaths.selectNodeList(gfDoc, XPaths.Daitss.ALL_CHILDREN);
        for(int i = 0; i < nodes.getLength(); i++) {           
            rootElement.appendChild(doc.importNode(nodes.item(i), true));
        }
        
        return doc;
    }

	/* (non-Javadoc)
	 * @see edu.fcla.daitss.entity.InformationPackage#ingest()
	 */
	@Override
	public void ingest() throws FatalException, PackageException, IOException {
	    // Intelligent Design
	}

	/* (non-Javadoc)
	 * @see edu.fcla.daitss.entity.InformationPackage#reject()
	 */
	@Override
	public void reject() throws FatalException {
	    // Intelligent Design
	}

	public void buildCopies() throws FatalException {
	    copies = new Vector<Copy>();
	    List<Silo> silos = StorageHelper.activeSilos();
	    if (silos.isEmpty()) {
	        Informer.getInstance().fail("No Silos in Archive", "making storage urls", new FatalException());
	    }
        for (Silo silo : silos) {
        	copies.add(new Copy(silo, tarballName()));    
        }
	}

	public String getCopyMD5() {
		return copyMD5;
	}
       
}
