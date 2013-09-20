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

/** Java class "IPList.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss.entity;

import java.io.File;
import java.sql.SQLException;
import java.util.Vector;

import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.exception.SilentPackageException;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.MessageDigestCalculator;
import edu.fcla.daitss.file.global.GlobalFiles;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.report.Reporter;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DisseminationDelivery;
import edu.fcla.daitss.util.Informer;

/**
 * A collection of Information Packages.
 */
public abstract class IPList {

	/**
	 * Represents the absolute path to the directory containing the archive
	 * objects of interest. For ingest this is the directory containing the
	 * ingest packages (subdirectories).
	 */
	private String inPath;

	/**
	 * Represents the absolute path to the directory where the archive objects
	 * should be put after processing the objects in the workDir directory. For
	 * ingest this is the directory containing the files to copy to tape.
	 */
	private String outPath;

    
    /**
     * The collection of information packages. In essence, this is the "List"
     * of an IPList
     */
    private Vector <InformationPackage> infoPkgs = null;
	
    /**
     * Represents the absolute path to the directory where the archive objects
     * are actually processed and modified. 
     */
    private String workPath;

    /**
     * Method to add an InformationPackage to this IPList's internal storage.
     * @param ip
     * @throws FatalException
     */
    protected void addInfoPackage(InformationPackage ip) throws FatalException {
    	String methodName = "addInfoPackage(InformationPackage)";
    	// check input
    	if (ip == null) {
    		Informer.getInstance().fail(
    			this,
    			methodName,
    			"Illegal argument",
    			"ip: " +ip,
    			new IllegalArgumentException("InformationPackage argument is null"));
    	}
    	infoPkgs.add(ip);
    }

    /**
     * Creates a list of IP-lites. Reads the INGEST_INPATH and creates an 
     * IP based on the name of each first-level directory (a package). At this 
     * point, the only member of the IP that is set is packagePath, so
     * it is considered to be a partial-IP (IP-lite). The IP itself
     * is not built until IPList.process()
     * 
     * @throws FatalException
     */
    public void build() throws FatalException {
    	String methodName = "build()";
    	Informer.getInstance().info(
    		this,
    		methodName,
    		"Begin building of IPList",
    		"IPList",
    		false);
    
    	// initialize SIP storage vector
    	this.infoPkgs = new Vector<InformationPackage>();
    	// get the ingest in_path. no need to check if it exists, becuase 
    	// ArchiveProperties has already checked.
       	String rinPath = ArchiveProperties.getInstance().getArchProperty("REINGEST_INPATH");
    	// get all first-level files and directories in ingest/rin
    	File f = new File(rinPath);
    	File[] files = f.listFiles();
    	
       	for (int i=0;i<files.length;i++){
    		if (files[i].isDirectory()){
    			// this package should has already been ingested
                AIP aip = new AIP(files[i].getAbsolutePath());
                METSDescriptor md = null;
                
                try {
                    // try to find an archive descriptor, all AIPs will have one
                    md = aip.findArchiveDescriptor();
                    // add the information package to the list to be processed
        			addInfoPackage(aip);
        			Informer.getInstance().info(this,"build()",
        				"Adding " + files[i].getAbsolutePath() + " to IPList",
        				aip.getPackageName(), false);
                }
                catch (PackageException pe) {
                    Informer.getInstance().warning(this, methodName, "Error encountered while locating and/or validating ",
                            aip.getPackageName());
                    md = null;
                }
    		}
    	}
       	f = null;
       	files = null;
    	String inPath = ArchiveProperties.getInstance().getArchProperty("INGEST_INPATH");
       	f = new File(inPath);
    	// get all first-level files and directories in ingest/in
    	files = f.listFiles();
    	for (int i=0;i<files.length;i++){
    		if (files[i].isDirectory()){
    			// this package should be for ingest
                SIP sip = new SIP(files[i].getAbsolutePath());
                // add the information package to the list to be processed
    			addInfoPackage(sip);
    			Informer.getInstance().info(this, "build()",
    				"Adding " + files[i].getAbsolutePath() + " to IPList",
    				sip.getPackageName(), false);
    		}
    	}
    	
    	// cleanup		
    	f = null;
    	files = null;
    }

	/**
	 * Returns the path to the input directory
     * @return String
	 */
	public String getInPath() {
		return inPath;
	} // end getInPath        

	/**
	 * Returns the path to the output directory
     * @return String
	 */
	public String getOutPath() {
		return outPath;
	} // end getOutPath   

    /**
     * @return Vector contains InformationPackage objects
     */
    public Vector <InformationPackage> getInfoPkgs() {
    	return infoPkgs;
    }

    /**
     * Destroys and constructs singleton objects used by a single package. They
     * need to be destroyed so that state information is not maintained across
     * packages. 
     * 
     * @throws FatalException
     */
    public void init() throws FatalException {
    	String methodName = "init()";
    	Informer.getInstance().info(
    		this,
    		methodName,
    		"Initializing objects that are not shared among packages",
    		"SIPList",
    		false);	
    
    	// destroy previous Events instance
    	Events.die();
    	// initialize Events instance
    	Events.getInstance(); 
    	
    	// destroy previous Relationships instance
    	Relationships.die();
    	// initialize Relationships instance
    	Relationships.getInstance();		
    
    	// destroy the DataFileFactory (which will also destroy a
    	// GlobalFilePackage if one exists).		
        DataFileFactory.die();
        		
    	// init the GlobalFiles 
    	GlobalFiles.die();
    	// force GlobalFiles to be rebuilt from DB
    	GlobalFiles.getInstance();
    	
    	// delete previous package's message digests
    	MessageDigestCalculator.die();	
    }        

    /**
     * Calls ArchiveProperties.getInstance().getProperty(&quot;INGEST_INPATH&quot;),
     * reads the INGEST_INPATH for all first-level directories (packages). If
     * the INGEST_INPATH does not exist create a LogEntry logEntry and then
     * throw new UnknownPathException(logEntry). If there are no directories in
     * the INGEST_INPATH just return.
     * 
     * For each package (subdirectory in INGEST_INPATH) do all the following:
     * 
     * try to {
     * 
     * create a SIP object using sip = new SIP(packagePath);
     * 
     * sip.copyToWorkDir(); sip.build(); sip.migrate(), sip.normalize(), 
     * sip.ingest(), sip.move(sip.getWorkPath(),
     * ArchiveProperties.getInstance().getProperty(&quot;INGEST_OUTPATH&quot;));
     * 
     * } catch PackageExceptions and FatalExceptions; calls sip.reject() for
     * either one; if it was a PackageException continue on with the next
     * package; if it was a FatalException rethrow it.
     * 
     * @throws FatalException
     */
    public void process() throws FatalException { 
    	String methodName = "process()";
    	
    	if (this.getInfoPkgs() == null || this.getInfoPkgs().size() == 0) {
    		Informer.getInstance().info2("There are no packages to process", "Ingest \'in\' directory contains no SIPs");
    	} else {
			Informer.getInstance().info("Begin processing of IPList","SIPList");			
	    	
	    	        
	        for (InformationPackage ip : this.getInfoPkgs()) {	        	
	    		// initialize/reset package specific objects
	    		init();
	    		
	            // ingest the InformationPackage
	            try {
	                ip.ingest();
	            } catch (PackageException pe) {
	         
	            	// if this is re-ingest, give them the package anyway but still reject it
	            	if (ip instanceof AIP) {
	            		String account = ip.getIntEnt().getAcccount();
	                    File sip = new File(ArchiveProperties.getInstance().getArchProperty("REINGEST_INPATH"), ip.getPackageName());
	            		DisseminationDelivery delivery = new DisseminationDelivery(ip.getPackageName(), account, sip);
	            		delivery.process();
	            	}
	            	
	            	// reject the package
	                rejectPackage(ip);
	                
	                //mail account holder if package exception is not a SilentPackageException
	                
	                String account;

	                if(pe instanceof SilentPackageException) { 
	                	account = ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT");
	                }	                
	                
	                else {		                
	                	// determine the account for this package error		                
		                if (ip instanceof AIP 
		                		|| ip.getIntEnt() == null 
		                		|| ip.getIntEnt().getAcccount() == null 
		                		|| ip.getIntEnt().getAcccount().trim().equals("")) {             
		                    account = ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT");
		                } else {
		                    account =  ip.getIntEnt().getAcccount();
		                }
	                }    
	                	                    
	                // Report the package error
	                Reporter.reportError(pe, ip, account);		                
	                
	            } catch (NullPointerException e) {
	            	e.printStackTrace();
	                rejectPackage(ip);  
	            } catch (FatalException fe) {
	                fe.printStackTrace();
	                rejectPackage(ip); 
	            } catch (Exception e){
	                // an unexpected Exception has been encountered
	                // It should have already been caught, but it wasn't
	                // so this is programmer error and therefore fatal.
	            	
	            	// print the stack trace
	            	e.printStackTrace();
	                rejectPackage(ip);
	                Informer.getInstance().fail(
	                    this,
	                    methodName,
	                    "Unexpected exception: " + e.getMessage(),
	                    e.getClass().getName(),
	                    e);                             
	            }            		
	    	}
    	}
    }        

    /**
     * Method to reject a package after a PackageException has
     * been encountered. Need to log information about the SIP.
     * Always need to delete the contents of the
     * work and output directories. Check the SIP's state to determine
     * if other operations need to be performed (transaction rollback,
     * removal of package from storage, etc.).
     * 
     * @param ip An InformationPackage object
     * @throws FatalException
     */
    protected void rejectPackage(InformationPackage ip) 
        throws FatalException {             
        
        String methodName = "rejectPackage(InformationPackage)";
     
        if (ArchiveProperties.getInstance().
                getArchProperty("COPY_REJECTS_TO_REJECT_DIR").equals("true")) {
            try {
                // move package to reject directory
                ip.copy(ArchiveProperties.getInstance().getArchProperty("INGEST_REJECTPATH") + ip.getPackageName());
            } catch (PackageException pkge){       
                Informer.getInstance().fail(this.getClass().getName(),
                        methodName, 
                        "Unable to move rejected package to reject directory", 
                        ip.getPackagePath(), 
                        pkge);
            }
        }
        
        // the last thing to happen to a rejected SIP
        ip.setState(InformationPackage.SIP_REJECTED);
        try {
            // Rollback the current transaction
            TransactionConnection tcon = DBConnection.getSharedConnection();
            tcon.rollbackTransaction();
            tcon.close();
        } catch (SQLException e) {
            // this exception can be silently ignored
            Informer.getInstance().warning(this, methodName, e.toString(), "Error rolling back transaction");
        }
        
        // delete eveything from the output dir
        ip.clearOutDir();

        // delete (if specified in config file) everything from the work directory        
        if (ArchiveProperties.getInstance().
                getArchProperty("DELETE_REJECTS_FROM_WORK_DIR").equals("true")) {
            ip.clearWorkDir();
        }
        
        // force a message to the log 
        Informer.getInstance().info(this,
                methodName,
                "SIP rejected",
                ip.getPackageName(),
                true);                        
    }    
    

	/**
     * Sets the input path for information packages
     * 
     * @param _inPath absolute path to input directory
	 */
	public void setInPath(String _inPath) {
		inPath = _inPath;
	} // end setInPath        

	/**
     * Set the output path for successfully processed information packages
     * 
	 * @param _outPath
	 */
	public void setOutPath(String _outPath) {
		outPath = _outPath;
	} // end setOutPath        

} // end IPList
