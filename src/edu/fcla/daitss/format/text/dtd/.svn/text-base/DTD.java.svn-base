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
 */
package edu.fcla.daitss.format.text.dtd;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.entity.Agreements;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.IntEntity;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.file.distributed.Distributed;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.text.Text;
import edu.fcla.daitss.format.text.TextFile;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;


/**
 * 
 * DTD represents an XML DTD file.
 * It is treated as a variation of a text file with the following exceptions: we
 * can recognize links to external files and the character encoding may or
 * may not be specified in the XML text declaration at the beginning of the file.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class DTD extends TextFile implements Distributed {
    
	/**
	 * Attribute represents an attribute specific to DTD files.
	 * Note that the length of the values of these constants have to be 
	 * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
	 * they have to be unique in the FORMAT_ATTRIBUTES database table.
	 * 
	 * @author Andrea Goethals, FCLA
	 *
	 */
	public static class Attribute {
	}
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.text.dtd.DTD";
	
	/**
     * XML DTD version 1.0
     */
    public static final String VERSION_1_0 = "1.0";

	/**
	 * Determines whether or not the file is a DTD file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a DTD file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
	 * Determines whether or not the file is a DTD file when metadata about
	 * this file is available.
	 * 
	 * @return 	whether or not its a DTD file					
	 * @param 	filePath absolute path to an existing readable file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, DataFile)";
			
		// well-formed until proven otherwise	
		boolean isType = true;
		
		// check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(CLASSNAME,
			methodName,
			"Illegal argument",
			"filePath: " + filePath,
			new FatalException("Not an existing, readable absolute path to a file"));
		}
		
		// check to see if input file is binary or text
		if (!TextFile.isType(filePath, _metadata)) {
			isType = false;
		}
		else {
		
			DTDParser tempParser = new DTDParser();
			DTD tempDTD = new DTD(filePath);
			tempDTD.dSubset = new DTDSubset(tempDTD);
			
			// see if it's well-formed (don't load external files)
			try {
				tempParser.parse(tempDTD, DTDParser.ACTION_IS_WELLFORMED);
			} catch (FileNotFoundException e) {
				// not that this should happen - we've already checked that
				// we can read the file
				isType = false;
				Informer.getInstance().fail(CLASSNAME,
					methodName,
					"Illegal argument",
					"filePath: " + filePath,
					new FatalException("Not an existing, readable absolute path to a file"));
			} catch (IOException e) {
				isType = false;
			} catch (DTDSyntaxException e) {
				isType = false;
			} catch (DTDWriterException e){
				// not going to happen because we don't write out to a file
				// during this parse
			}
			
			tempParser = null;
			tempDTD = null;
		}
		return isType;
	}
	
	/**
	 * @param args
	 * @throws PackageException
	 * @throws FatalException
	 */
	public static void main(String[] args)
		throws PackageException, FatalException {
			
		//String testFile = "/apps/testfiles/dtd/book.dtd";
		//String testFile = "/apps/testfiles/dtd/XMLSchema.dtd";
		//String testFile = "/apps/testfiles/dtd/contexts.dtd";
		//String testFile = "/apps/testfiles/dtd/datatypes.dtd";
		//String testFile = "/apps/testfiles/dtd/design.dtd";
		String testFile = "/daitss/dev/data/ingest/work/UF00003061/book_2.dtd";
		String initDesc = "/daitss/dev/data/ingest/work/UF00003061/UF00003061.xml";
		
		//String testFile = "/home/andrea/work/testfiles/dtd/book.dtd";
		
		// not well-formed
		//String testFile = "/apps/xerces-2_6_2/data/personal.dtd";
		
		if (DTD.isType(testFile, null)) {
		    SIP sip = new SIP("/daitss/dev/data/ingest/work/UF00003061/");
		    //sip.setInitialDescriptor(new METSDescriptor("/daitss/dev/data/ingest/in/UF00000001/UF00003061.xml", sip));
			DTD dtdFile = new DTD(testFile, sip);
			
			// do something meaningless with the DataFileFactory to instantiate it
			
			
			// set some things here for testing that would normally be set 
			// by the DataFileFactory
			SIP theSip = (SIP) dtdFile.getIp();
			METSDescriptor md = new METSDescriptor(initDesc, sip);
			theSip.setInitialDescriptor(md);
/*			IntEntity ie = new IntEntity();
			ie.setAcccount("FDA");
			ie.setSubAccountId(new Long(0));
			ie.setProject("FDA");
			sip.setIntEnt(ie);
*/			sip.setMigratedDir("fakeDir");
			sip.setNormalizedDir("fakeDir");
			DataFileFactory.getInstance(sip).getClass();
			
			dtdFile.getIp().setLinksDir("/daitss/dev/data/ingest/work/UF00003061/links_20041208164413");
			dtdFile.setOid("F20040101_AAAAAA");
			dtdFile.setOrigin(DataFile.ORIG_DEPOSITOR);
			dtdFile.setPresLevel(Agreements.PRES_LEV_FULL);
			
			//System.out.println("File " + testFile + " is a DTD file");
				
			dtdFile.extractMetadata();
			dtdFile.migrate();
			dtdFile.normalize();
			dtdFile.localize();
			dtdFile.localize();
			
			System.out.println(dtdFile);
		} else {
			// not a DTD
			System.out.println("File " + testFile + " is not a DTD file");
		}
	}
	
	/**
	 * The single DTD subset in this file.
	 */
	private DTDSubset dSubset = null;
	
	/**
	 * Only to be used by isType(String).
	 * 
	 * @param path
	 * @throws FatalException
	 */
	protected DTD(String path) throws FatalException {
		super(path);
	}

	/**
	 * The constructor to call for an existing DTD file when metadata about
	 * this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public DTD(String path, InformationPackage ip) throws FatalException {
		super(path, ip); 
		
		this.setByteOrder(DataFile.BYTE_ORDER_NA);
		
		this.setMediaType(MimeMediaType.MIME_APP_XMLDTD);
		this.setFileExt(Extension.EXT_XMLDTD);
		
		// NOTE: using the only version of XML so far,
		// since DTDs are specified in the XML specification
		this.setMediaTypeVersion(VERSION_1_0);
		
		this.anomsPossible = null;
		this.anomsPossible = new DTDAnomalies();
	}

	/**
	 * The constructor to call for an existing DTD file when metadata about
	 * this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata a lite DataFile containing metadata about this DataFile
	 * @throws FatalException
	 */
	public DTD(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Evaluate the members set by parsing the file to set other members.
	 * 
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		super.evalMembers();
		
	}
	
	/**
	 * Extracts the technical metadata from the file,
	 * retrieves its externally linked-to files, and
	 * validates itself.
	 * 
	 * @throws PackageException
	 * @throws FatalException
	 */
	public void extractMetadata() throws PackageException, FatalException {
	  
		// first parse, evalMembers, retrieveLinks
		super.extractMetadata();
		
		// now note when linked-to files don't exist
		// (didn't do this in parse() so that we could wait til after retrieveLinks())
		this.parseAndLoad();
	}

	/**
	 * @return the DTD file's single bitstream
	 */
	public DTDSubset getDSubset() {
		return dSubset;
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
		String methodName = "localize()";
		
	    boolean continueLocalizing = localizeSetup();
	    
	    // exit early if this file should not be localized
	    if (!continueLocalizing){
	        return;
	    }

        String specProc = null; // specific transformation procedure for event reporting
        String genProc = null; // general transformation procedure
        String locForm = null; // localization format
        String newFilePath = null; // new localized version of this file
        
        // set the general transformation procedure
        // note that the localization state was set in localizeSetup() above
        // note that the localization pass 2 state can only be set to
        // CALLED in localizeSetup and before its called its set to NOT_CALLED
        switch(this.getStateLoc2()){
	    	case Procedure.NOT_CALLED: 
	    	    // starting phase 1
        	    genProc = Procedure.TRANSFORM_LOC_1;
        	    break;
        	case Procedure.CALLED:
        	    // starting phase 2
        	    genProc = Procedure.TRANSFORM_LOC_2;
        	    break;
        }

        // we're going to store the localized file at the same directory
        // location as the other file
        boolean keepLocalizing = 
            transformStart(genProc, 
                    this.getFormatCode(), 
                    false); // don't put it in a separate directory

        // get the localization form from the configuration files
        if (keepLocalizing) {
        	String formProp = "LOC_" + this.getFormatCode() + "_FORMAT";
            locForm = 
                ArchiveProperties.getInstance().getArchProperty(formProp);
        }
        
        // create the configuration file-indicated localization format
        if (keepLocalizing && locForm.equals("XMLDTD_LOC_1")) {
        	
        	specProc = Procedure.PROC_XMLDTD_LOC_1;
            
            // make sure that there are links worth localizing
        	keepLocalizing = false;
            for (Iterator iter = this.getLinks().iterator(); iter.hasNext();) {
                Link lk = (Link) iter.next();
                if (lk.getStatus().equals(Link.STATUS_SUCCESSFUL)) {
                	keepLocalizing = true;
                    break;
                }
            }

            if (keepLocalizing) {
                // determine the localized file's name
                // if in phase 1
                
                if (this.getStateLoc2() == Procedure.NOT_CALLED){
                    // PHASE 1
                    
                    File thisFile = new File(this.getPath());
                    
                    // make sure that this file doesn't already exist
                    int i=1;
                    // try the ideal name first
                    newFilePath = thisFile.getParent() + File.separator +
            			FileUtil.stripExtFromTitle(this.getFileTitle()) + "_LOC." + 
						this.getFileExt();
                    File newFile = new File(newFilePath);
                    boolean exists = newFile.exists();
                    while (exists){
                    	newFilePath = thisFile.getParent() + File.separator +
                        	FileUtil.stripExtFromTitle(this.getFileTitle()) + "_LOC-" + i + "." + 
                        	this.getFileExt();
                        newFile = new File(newFilePath);
                        exists = newFile.exists();
                        i++;
                    }
                    
                    // clean up
                    newFile = null;
                    thisFile = null;
                    
                } else {
                    // PHASE 2
                    
                    // want to use the name used in phase 1
                    newFilePath = this.getLocalizedFilePath();
                    
                    try {
                        // delete the file created in phase 1 (only created 
                    	// it in the first phase to make sure it could be created 
                    	// so that we know that other files can point to this new 
                    	// localized file that will be created in phase 2)
                        
                        FileUtil.delete(newFilePath);
                    } catch (IOException e1) {
                    	Informer.getInstance().error(
                        		this,
                        		methodName,
                        		"Unable to delete temp file",
                        		"newFilePath: " + newFilePath,
                        		e1);										
                    }
                }
                
                // parse the file, recognize its links, replace
                // in memory with links to relative and/or localized
                // files and write it out to a new file (newFilePath)
                DTDParser dParser = new DTDParser();
                boolean parseFailed = false;
        		
        		try {
                    dParser.parse(this, DTDParser.ACTION_REP_LKS, newFilePath);
                    
                    // check that the file was created
                    File newF = new File(newFilePath);
                    if (!newF.exists()){
                    	throw new DTDWriterException("Localized file " + 
                    			newFilePath + " not created.");
                    }
                    
                    // save this name so that we use the same
                    // name in phase 2 and also so that the
                    // link replacer will know what name to use
                    // for this file.
                    this.setLocalizedFilePath(newFilePath);
                    
                    // it succeeded (parsed successfully)
                    if (this.getStateLoc2() == Procedure.CALLED){
                    	this.setStateLoc2(Procedure.SUCCEEDED);
                    } else {
                    	this.setStateLoc1(Procedure.SUCCEEDED);
                    }
                    
                } catch (FileNotFoundException e) {
                    parseFailed = true;
                } catch (FatalException e) {
                    parseFailed = true;
                } catch (IOException e) {
                    parseFailed = true;
                } catch (DTDSyntaxException e) {
				    	Informer.getInstance().warning(this, methodName,
						"DTDSyntaxException at Line:" +  e.getRow() + ", Message: " + e.getMessage(), "");
                    parseFailed = true;
                } catch (DTDWriterException e) {
                    parseFailed = true;
                }
                
                if (parseFailed){
                   	// did not parse successfully
                	if (this.getStateLoc2() == Procedure.CALLED){
                    	this.setStateLoc2(Procedure.FAILED);
                    } else {
                    	this.setStateLoc1(Procedure.FAILED);
                    }
                	
                    // problem - lets record this as an unsuccessful
                    // normalization and delete the created file if it exists.
                    File theNewFile = new File(newFilePath);
                    if (theNewFile.exists()){
                    	Informer.getInstance().warning(this,
                                methodName,
                                "Deleting file because of localization problem",
                                "File: " + newFilePath);
                        try {
                            //theNewFile.delete();
                            FileUtil.delete(newFilePath);
                        } catch (IOException e1) {
                        	Informer.getInstance().error(
                            		this,
                            		methodName,
                            		"Unable to delete file with localization problem",
                            		"newFilePath: " + newFilePath,
                            		e1);										
                        }
                    }
                    
                    // because we're setting this to null the normalizeEnd
                    // method will record it as an unsuccessful event
                    newFilePath = null;
                }
                
                if (this.getStateLoc2() == Procedure.NOT_CALLED){
                    // PHASE 1
                    
                    // if this file is global,
                    // add the new file's absolute path to the package-wide
                    // set of global files so that the DataFileFactory will
                    // know to treat the derived file as global.
                    if (newFilePath != null && this.isGlobal()){
                        DataFileFactory.getInstance().addFutureGlobalPath(newFilePath);
                    }
                    
                    // done with first pass of localization
                    Informer.getInstance().info(this,
                            methodName,
                            "Done " + genProc + " at " + DateTimeUtil.now(),
                            "File: " + this.getPath(),
                            false);
                } else {
                    // PHASE 2
                    
                    // create DataFile, relationships, events
                    transformEnd(genProc, newFilePath, specProc);
                }
            } else {
                // no links
                Informer.getInstance().info(this,
                        methodName,
                        "Done " + genProc + " at " + DateTimeUtil.now() + 
                        " (NO LINKS - NO LOCALIZATION)",
                        "File: " + this.getPath(),
                        false);
            }
        } else {
            // put other localization procedures here
            
        }
	}

	/**
	 * temporary fix for the DTD localization
	 *
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void localize_tempFix() throws FatalException, PackageException {
		String methodName = "localize()";
		
	    boolean continueLocalizing = localizeSetup();
	    
	    // exit early if this file should not be localized
	    if (!continueLocalizing){
	        return;
	    }

        String specProc = null; // specific transformation procedure for event reporting
        String genProc = null; // general transformation procedure
        String locForm = null; // localization format
        String newFilePath = null; // new localized version of this file
        
        // set the general transformation procedure
        // note that the localization state was set in localizeSetup() above
        // note that the localization pass 2 state can only be set to
        // CALLED in localizeSetup and before its called its set to NOT_CALLED
        switch(this.getStateLoc2()){
	    	case Procedure.NOT_CALLED: 
	    	    // starting phase 1
        	    genProc = Procedure.TRANSFORM_LOC_1;
        	    break;
        	case Procedure.CALLED:
        	    // starting phase 2
        	    genProc = Procedure.TRANSFORM_LOC_2;
        	    break;
        }

        // we're going to store the localized file at the same directory
        // location as the other file
        boolean keepLocalizing = 
            transformStart(genProc, 
                    this.getFormatCode(), 
                    false); // don't put it in a separate directory

        // get the localization form from the configuration files
        if (keepLocalizing) {
        	String formProp = "LOC_" + this.getFormatCode() + "_FORMAT";
            locForm = 
                ArchiveProperties.getInstance().getArchProperty(formProp);
        }
        
        boolean parseFailed = false;
        // create the configuration file-indicated localization format
        if (keepLocalizing && locForm.equals("XMLDTD_LOC_1")) {
        	specProc = Procedure.PROC_XMLDTD_LOC_1;
            // make sure that there are links worth localizing
        	keepLocalizing = false;
            for (Iterator iter = this.getLinks().iterator(); iter.hasNext();) {
                Link lk = (Link) iter.next();
                if (lk.getStatus().equals(Link.STATUS_SUCCESSFUL)) {
                	keepLocalizing = true;
                    break;
                }
            }

            if (keepLocalizing) {
                // determine the localized file's name if in phase 1
                
                if (this.getStateLoc2() == Procedure.NOT_CALLED) {
                	// PHASE 1 
                   File thisFile = new File(this.getPath()); 
                    // make sure that this file doesn't already exist
                    int i=1;
                    // try the ideal name first
                    newFilePath = thisFile.getParent() + File.separator +
            			FileUtil.stripExtFromTitle(this.getFileTitle()) + "_LOC." + this.getFileExt();
                    File newFile = new File(newFilePath);
                    boolean exists = newFile.exists();
                    while (exists){
                    	newFilePath = thisFile.getParent() + File.separator +
                        	FileUtil.stripExtFromTitle(this.getFileTitle()) + "_LOC-" + i + "." + this.getFileExt();
                        newFile = new File(newFilePath);
                        exists = newFile.exists();
                        i++;
                    }
                    
                    // clean up
                    newFile = null;
                    thisFile = null;
                    
                } else {
                    // PHASE 2
                    
                    // want to use the name used in phase 1
                    newFilePath = this.getLocalizedFilePath();                   
                    try {
                        // delete the file created in phase 1 (only created it in the first phase to make 
                        // sure it could be created so that we know that other files can point to this new localized file
                        // that will be created in phase 2)
                        FileUtil.delete(newFilePath);
                    } catch (IOException e1) {
                    	Informer.getInstance().error(this, methodName, "Unable to delete temp file",
                    			"newFilePath: " + newFilePath, e1);										
                    }
                }
        		try {
                    BufferedReader inFile = new BufferedReader(new FileReader(this.getPath()));
                    BufferedWriter outFile = new BufferedWriter(new FileWriter(newFilePath));
                    String strline;
                    String localName;
                    
                    strline = inFile.readLine();
                    // go through each line in the DTD file
                    while (strline != null) {
	        			// get the localized name in each link
	        		    for (Iterator iter = this.getLinks().iterator(); iter.hasNext();) {
	        		        Link lk = (Link) iter.next();
	        		        if (lk.getStatus().equals(Link.STATUS_SUCCESSFUL)) {
			        			// retrieve the datafile object for the associated link
			            		DataFile df = super.getDfFromLinkAlias(lk.getLinkAlias());
			            		// df could be null, double check.    
			            		if (df != null) {         			
			            			if (df.getLocalizedFilePath() != null)
			            				localName = FileUtil.getRelPathFrom(this.getPath(), df.getLocalizedFilePath()); 
			            			else
			            				localName = FileUtil.getRelPathFrom(this.getPath(), df.getPath());
			            	    // replaced the link alias with the localized name
			            		strline = FileUtil.replaceString(strline, lk.getLinkAlias(), localName);
			            		outFile.write(strline);
			            		outFile.newLine();
			            		}
	        		        }
	        		    }
                    	strline = inFile.readLine();
                    }
                    inFile.close();
                    outFile.close();
 
                    // save this name so that we use the same name in phase 2 and also so that the
                    // link replacer will know what name to use for this file.
                    this.setLocalizedFilePath(newFilePath);
                    
                    // it succeeded (parsed successfully)
                    if (this.getStateLoc2() == Procedure.CALLED){
                    	this.setStateLoc2(Procedure.SUCCEEDED);
                    } else {
                    	this.setStateLoc1(Procedure.SUCCEEDED);
                    }                   
                } catch (IOException e) {
                    //parseFailed = true;
                } 
                
                if (parseFailed){
                   	// did not parse successfully
                	if (this.getStateLoc2() == Procedure.CALLED){
                    	this.setStateLoc2(Procedure.FAILED);
                    } else {
                    	this.setStateLoc1(Procedure.FAILED);
                    }
                	
                    // problem - lets record this as an unsuccessful
                    // normalization and delete the created file if it exists.
                    File theNewFile = new File(newFilePath);
                    if (theNewFile.exists()){
                    	Informer.getInstance().warning(this, methodName,
                            "Deleting file because of localization problem",
                            "File: " + newFilePath);
                        try {
                            FileUtil.delete(newFilePath);
                        } catch (IOException e1) {
                        	Informer.getInstance().error(this, methodName,
                            	"Unable to delete file with localization problem",
                            	"newFilePath: " + newFilePath,e1);										
                        }
                    }
                    
                    // because we're setting this to null the normalizeEnd
                    // method will record it as an unsuccessful event
                    newFilePath = null;
                }
                
                if (this.getStateLoc2() == Procedure.NOT_CALLED){
                    // PHASE 1
                    
                    // if this file is global, add the new file's absolute path to the package-wide
                    // set of global files so that the DataFileFactory will
                    // know to treat the derived file as global.
                    if (newFilePath != null && this.isGlobal()){
                        DataFileFactory.getInstance().addFutureGlobalPath(newFilePath);
                    }
                    
                    // done with first pass of localization
                    Informer.getInstance().info(this, methodName,
                        "Done " + genProc + " at " + DateTimeUtil.now(),
                        "File: " + this.getPath(), false);
                } else {
                    // PHASE 2
                    
                    // create DataFile, relationships, events
                    transformEnd(genProc, newFilePath, specProc);
                }
            } else {
                // no links
                Informer.getInstance().info(this,methodName,
                        "Done " + genProc + " at " + DateTimeUtil.now() + 
                        " (NO LINKS - NO LOCALIZATION)",
                        "File: " + this.getPath(),  false);
            }
        } 
	}
	/**
	 * Parses itself and sets format-specific members. 
	 * Extracts the technical metadata from the DTD file, 
	 * populates its members with this
	 * metadata and recognizes and records any external
	 * links (schema, etc.). Does not add anomalies if it
	 * can't find external files because this willl be
	 * done in <code>parseAndLoad</code> after its links
	 * have been retrieved.
	 * 
	 * Already assumes its a DTD, so you should have called
	 * <code>isType</code> before parse.
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
	    String methodName = "parse()";
	    
	    Informer.getInstance().info(this,
                methodName,
                "Beginning to parse: " + DateTimeUtil.now(),
                "File: " + this.getPath(),
                false);
	    
		// first parse it as plain text	    
		super.parse();
		
		// remove the plain text bitstream from this file
		Text tempText = (Text) this.removeBitstream(0);
		
		// start building info about the DTD document in this file
		this.dSubset = new DTDSubset(this);
		this.addBitstream(dSubset);
		
		// set the DTDSubset's members to that of what was found by parsing
		// this file as plain text
		this.dSubset.setCharSet(tempText.getCharSet());
		this.dSubset.setCharSetOrigin(tempText.getCharSetOrigin());
		this.dSubset.setNatlLang(tempText.getNatlLang());
		this.dSubset.setLineBreak(tempText.getLineBreak());
		this.dSubset.setNumLines(tempText.getNumLines());
		
		DTDParser dParser = new DTDParser();
		
		try {
			dParser.parse(this, DTDParser.ACTION_EXT_META);
		} catch (FileNotFoundException e) {
			Informer.getInstance().fail(CLASSNAME,
					methodName,
					"Illegal argument",
					"filePath: " + this.getPath(),
					new FatalException("Not an existing, readable absolute path to a file"));
		} catch (IOException e) {
			Informer.getInstance().fail(this,
					methodName,
					"IOException",
					"filePath: " + this.getPath(),
					e);
		} catch (FatalException e) {
			throw e;
		} catch (DTDSyntaxException e) {
			// don't add anomaly for the syntax exception as external files are not loaded during
			// this parse.  
		} catch (DTDWriterException e) {
			// not going to happen because we don't write out to
			// a file during this parse
		}
	}         
	
	/**
	 * Parses itself making sure that linked-to files exist. 
	 * If they don't, anomalies are added.
	 * 
	 * Already assumes its a DTD, so you should have called
	 * <code>isType</code> before parse.
	 * 
	 * @throws FatalException
	 */
	protected void parseAndLoad() throws FatalException {
	    String methodName = "parseAndLoad()";
	    
	    Informer.getInstance().info(this,
                methodName,
                "Beginning to parse and load external files: " + DateTimeUtil.now(),
                "File: " + this.getPath(),
                false);
	    
	    DTDParser dParser = new DTDParser();
	    
		try {
			dParser.parse(this, DTDParser.ACTION_CHECK_EXT_FILES);
		} catch (FileNotFoundException e) {
			Informer.getInstance().fail(CLASSNAME, methodName,
					"Illegal argument",
					"filePath: " + this.getPath(),
					new FatalException("Not an existing, readable absolute path to a file"));
		} catch (IOException e) {
			Informer.getInstance().fail(this, methodName,
					"IOException",
					"filePath: " + this.getPath(),
					e);
		} catch (FatalException e) {
			throw e;
		} catch (DTDSyntaxException e) {
			this.addAnomaly(this.getAnomsPossible().getSevereElement(
					DTDAnomalies.DTD_BAD_SYNTAX));
		} catch (DTDWriterException e){
			// not going to happen because we don't write out to a 
			// file during this parse
		}
	    
	}
	
	 public Document toXML() throws FatalException {
	        // Document.
	        Document doc = super.toXML();

	        // Namespace.
	        String namespace = doc.getNamespaceURI();

	        // Root element.
	        Element rootElement = doc.getDocumentElement();
	        
	        return doc;
	    }
	
} // end DTDFile
