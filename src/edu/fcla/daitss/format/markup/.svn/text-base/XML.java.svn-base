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
package edu.fcla.daitss.format.markup;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;

/**
 * 
 * XML represents an XML file.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class XML extends MLFile {
	
	/**
	 * Attribute represents attributes specific to XML files.
	 */
	public static class Attribute {
		
		/**
		 * Uses a DTD.
		 */
		public static final String HAS_DTD = "APP_XML_HAS_DTD";
		
		/**
		 * Uses a W3D Xml schema.
		 */
		public static final String HAS_W3D_SCHEMA = "APP_XML_HAS_W3D_SCHEMA";
	}
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
		"edu.fcla.daitss.format.markup.XML";
	
    /**
     * XML version 1.0
     */
    public static final String VERSION_1_0 = "1.0";
	
	/**
	 * Determines whether or not a root element is described by the
	 * Library of Congress' XML schema mets.xsd.
	 * For this method to determine that it is Mets-described, the XML
	 * document must include the schemaLocation or noNamespaceSchemaLocation
	 * atribute in the XMLInstance namespace in the root element
	 * to point to a schema with a recognized Mets namespace. 
	 * The recognized Mets namespaces are listed in
	 * the configXml file in the property <code>NS_METS</code>.
	 * 
	 * @param filePath an absolute path to an existing file
	 * @return whether or not the root element schema is Mets-described
	 * @throws FatalException
	 */
	public static boolean isMets(String filePath) 
		throws FatalException {
		boolean result = false;
		
		if (XML.isType(filePath, null)) {
			XML tempXML = new XML(filePath, new SIP("/daitss/test/AB12345678"));
			result = tempXML.isMets();
			
			tempXML = null;
		}
		
		return result;
	}
	
	/**
	 * Determines whether or not the file is an XML file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its an XML file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}

	/**
	 * Determines if the file is an XML file when metadata about
	 * this file is available.
	 *  
	 * The strategy here is to assume it's XML and see if an XML
	 * SAX parser can read it through it without throwing any parsing errors.
	 * Parsing errors will be thrown if it tries to parse non-XML files.
	 * It will stop parsing the file as soon as it knows it's not XML
	 * so it's fairly fast.
	 * 
	 * To be considered XML it must be well-formed. It does
	 * not require the XML declaration. 
	 * A minimally-passing XML file could contain only:
	 * <a/>
	 * 
	 * According to the XML 1.0 spec, second edition 
	 * (http://www.w3.org/TR/2000/REC-xml-20001006), 
	 * there are three conditions a document must meet to be
	 * considered well-formed XML document:
	 *    1. Taken as a whole, it matches the production labeled "document".
	 *    2. It meets all the well-formedness constraints given in this specification.
	 *    3. Each of the parsed entities which is referenced directly or indirectly 
	 * 		within the document is well-formed.
	 * 
	 * To meet the first requirement it must contain one or more elements.
	 * 
	 * @return 	whether or not its an XML file					
	 * @param 	filePath absolute path to an existing readable file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, DataFile)";
		
		boolean isType = true;
		
		// check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(CLASSNAME,
			methodName,
			"Illegal argument",
			"filePath: " + filePath,
			new FatalException("Not an existing, readable absolute path to a file"));
		}
		
		XMLSAXParser tempParser = new XMLSAXParser();
		XML tempXML = new XML(filePath);
		
		// see if it's well-formed (don't load external schemas)
		try {
			tempParser.parse(tempXML, XMLSAXParser.ACTION_IS_WELLFORMED);
		} catch (SAXParseException e1) {
			// problem found when parsing the file -not an XML file
			isType = false;
		} catch (SAXException e1) {
			// a general SAX error or warning
			isType = false;
		}
		
		tempParser = null;
		tempXML = null;

		return isType;
	}         
	
	/**
	 * Test driver, used for validating an XML file.
	 * 
	 * @param args
	 *  args[0] -p
	 *  args[1] packagePath (such as /daitss/dev/data/ingest/in/WF00000133/)
	 *  args[2] -f
	 *  args[3] filename (i.e. WF00000133.xml)
	 *  
	 * @throws FatalException
	 * @throws PackageException
	 */
	public static void main(String[] args) throws PackageException,
		FatalException {
		// check for command line options	
		String testFile = null;
		String packagePath = null;
		
		if (args.length == 4 && args[0].equals("-p") && args[2].equals("-f")) {
			packagePath = args[1];
			testFile = args[1] + args[3];
		}
		else {
			printUsage();
			System.exit(1);
		}
		
		// isType, parsing, validation test code
		if (XML.isType(testFile, null)) {
			SIP sip = new SIP(packagePath);
			XML xFile = new XML(testFile, sip);
			System.out.println(testFile + " is valid: " + xFile.isValid());
			System.out.println(xFile);
		} else {
			// not well-formed XML
			System.out.println("File " + testFile + " is not an XML file");
		}
	}
	
	/**
	 * Print the command line usage of this class.
	 */
	private static void printUsage() {
		System.out.println("java XML [-p packagePath] [-f filename]");
	}
	
	/**
	 * The single XML document in this file.
	 */
	private XMLDocument doc = null;
	
	/**
	 * Character encoding
	 */
	private String encoding = null;
	
	/**
	 * Absolute path to the localized version of this file.
	 */
	//private String locAbsPath = null;
	
	/**
	 * A SAXParser that can parse this file.
	 */
	private XMLSAXParser parser = null;
	
	/**
	 * Only to be used by isType(String).
	 * 
	 * @param path
	 * @throws FatalException
	 */
	private XML(String path) throws FatalException {
		super(path);
	}

	/**
	 * The constructor to call for an existing XML file when external metadata about
	 * this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public XML(String path, InformationPackage ip) throws FatalException {
		super(path, ip);
		
		this.setByteOrder(DataFile.BYTE_ORDER_NA);
		this.setMediaType(MimeMediaType.MIME_APP_XML);
		this.setFileExt(Extension.EXT_XML);
		
		// NOTE: there's currently only one version of XML - the version can only
		// be assumed until another version is published.
		this.setMediaTypeVersion(VERSION_1_0);
		
		this.anomsPossible = null;
		this.anomsPossible = new XMLAnomalies();
	}        
	
	/**
	 * The constructor to call for an existing XML file when external metadata about
	 * this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata a lite DataFile containing metadata about this DataFile
	 * @throws FatalException
	 */
	public XML(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}  
	
	
	/**
	 * Evaluates its members and sets other members depending
	 * on the results of the evaluation. 
	 *
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		super.evalMembers();
		
		// see if it has no schema
		if (!this.hasFormatAttribute(XML.Attribute.HAS_DTD) && 
				!this.hasFormatAttribute(XML.Attribute.HAS_W3D_SCHEMA) && 
			this.getDoc().getSchemaType().equals(MLDocument.ST_UNKNOWN)) {
				this.getDoc().setSchemaType(MLDocument.ST_NA);
		}
		
		// change default xml file extension if applicable
		String theML = this.getDoc().getMarkupLanguage();
		if (theML != null && !theML.equals("")) {
			if (NamespaceURI.isXmlSchemaNs(theML)) {
				// its an XML Schema - change its file extension
				this.setFileExt(Extension.EXT_XSD);
			} else if (NamespaceURI.isXslNs(theML)) {
				// its an XML Stylesheet - change its file extension
				this.setFileExt(Extension.EXT_XSL);
			}
		}
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
		
		// now validate
		this.validate();
		
		// loop through all the links to see if there is link for root schema.
        for (Iterator iter = this.getLinks().iterator(); iter.hasNext();) {
            Link lk = (Link) iter.next();
            // found a link to a root schme
            if (lk.isRootSchema()) {
            	DataFile df = this.getDfFromLinkAlias(lk.getLinkAlias());
            	// record the root schema dfid
            	if (df != null)
            		this.getDoc().setSchemaDfid(df.getOid());
            }
		}
	}
	
	/**
	 * Extracts the technical metadata from the file
	 * and validates itself. Used for testing purposes only 
	 * so that externally lined-to files are not actually downloaded.
	 * @throws PackageException 
	 * 
	 * @throws FatalException
	 */
	public void extractMetadataNoRetrieve() throws PackageException, FatalException {
		// first parse, evalMembers, retrieveLinks
		super.extractMetadataNoRetrieve();
		
		// now validate
		this.validate();
	}

	/**
	 * @return the single XML document in this file
	 */
	public XMLDocument getDoc() {
		return this.doc;
	}

	/**
	 * @return the character encoding
	 */
	public String getEncoding() {
		return this.encoding;
	}

    /**
     * @return Returns the normAbsPath.
     */
	/*
    public String getLocAbsPath() {
        return this.locAbsPath;
    }*/
	
	/**
	 * Determines whether or not a root element is described by the
	 * Library of Congress' XML schema mets.xsd.
	 * For this method to determine that it is Mets-described, the XML
	 * document must include the schemaLocation or noNamespaceSchemaLocation
	 * atribute in the XMLInstance namespace in the root element
	 *  to point to a schema with a recognized Mets namespace. 
	 * 
	 * The recognized Mets namespaces are listed in
	 * the configSystem file in the property <code>NS_METS</code>.
	 * NOTE: This method should only be called on Xml files that have
	 * passed the XML.isType() test. In other words, it should only be
	 * called on well-formed XML files.
	 * 
	 * @return whether or not the root element schema is Mets
	 * @throws FatalException
	 */
	public boolean isMets() throws FatalException {
		boolean result = false;
		
		XML tempXml = new XML(this.getPath(), this.getIp());
		
		tempXml.parseInit();
		tempXml.parse();
		tempXml.parseEnd();
		
		if (tempXml.getBitstreams() != null &&
			tempXml.getBitstreams().size() == 1) {
			XMLDocument tempDoc = 
				(XMLDocument)tempXml.getBitstreams().elementAt(0);
			String theML = tempDoc.getMarkupLanguage();
			if (theML != null && !theML.equals("") &&
				NamespaceURI.isMetsNamespace(theML)) {
					result = true;
			} 
			tempDoc = null;
		}
		tempXml = null;
		
		return result;
	}
	
	/**
	 * Determines the validation state of an XML file.
	 * The state can be any of:
	 * 
	 * 	MLDocument.VALID_FALSE (Found to be not valid.)
	 * 
	 * 	MLDocument.VALID_NA (Validity is not applicable
	 * 		because it has no schema.)
	 * 
	 * 	MLDocument.VALID_TRUE (Found to be valid.)
	 * 
	 * 	MLDocument.VALID_UNCHECKED (Did not try to validate it
	 * 		because either it wasn't well-formed or the file
	 * 		wasn't found or readable or there were other 
	 * 		errors trying to parse it.)
	 * 
	 * @return the validation state of this XML file.
	 * @throws FatalException
	 * @throws PackageException
	 */
	public String isValid() throws PackageException, FatalException {
		String state = MLDocument.VALID_UNCHECKED;
		XML tempXml = null;
		
		tempXml = new XML(this.getPath(), this.getIp());
		
		// using a dummy dfid here because this XML object
		// is only temporary. the dfid is necessary because
		// in the parse method a bitstream will be added to
		// this XML object; its bsid will be set which relies 
		// on this XML object having a valid dfid pattern.
		tempXml.setOid("F20040101_AAAAAA");
		
		// see if it has a schema
		tempXml.parseInit();
		tempXml.parse();
		tempXml.parseEnd();
		
		// validate it if applicable
		tempXml.validate();
			
		if (tempXml != null && tempXml.getDoc() != null){
			state = tempXml.getDoc().getValidationState();
		}
		
		return state;
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
	 * TODO: decide if we are going to localize invalid files.
	 * Right now any localized files that are found to be invalid
	 * will cause the package to be rejected. Does this mean that we
	 * want to not localize invalid files? Or should we reject all
	 * packages containing any invalid xml until we're sure they
	 * really are invalid (not a temp. network problem, etc.)
	 *
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void localize() throws FatalException, PackageException {
		String methodName = "localize()";				
		
	    boolean continueLocalizing = localizeSetup(); // sets the loc. call state
	    
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
        if (keepLocalizing){
            String formProp = "LOC_" + this.getFormatCode() + "_FORMAT";
            locForm = 
                ArchiveProperties.getInstance().getArchProperty(formProp);
        }

        // create the configuration file-indicated localization format
        if (keepLocalizing && locForm.equals("XML_LOC_1")) {
        	
        	specProc = Procedure.PROC_XML_LOC_1;
            
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
                    String additionalExt = FileUtil.getAdditionalExtension(
                    		this.getFileTitle(), this.getFileExt());
                    newFilePath = thisFile.getParent() + File.separator +
                		FileUtil.stripExtFromTitle(this.getFileTitle()) + "_LOC." + this.getFileExt();
                   if (additionalExt.length() != 0)
                    	newFilePath = newFilePath + additionalExt;
                    
                    File newFile = new File(newFilePath);
                    boolean exists = newFile.exists();
                    while (exists){
                    	newFilePath = thisFile.getParent() + File.separator +
                			FileUtil.stripExtFromTitle(this.getFileTitle()) + "_LOC-" + 
                			i + "." + this.getFileExt();
                    	if (additionalExt.length() != 0)
                    		newFilePath = newFilePath + additionalExt;
                    	
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
                        // delete the file created in phase 1
                        // (only created it in the first phase to make 
                        // sure it could be created so that we know that 
                        // other files can point to this new localized file
                        // that will be created in phase 2)
                        FileUtil.delete(newFilePath);
                    } catch (IOException e1) {
                    	Informer.getInstance().error(
                        		this,
                        		methodName,
                        		"Unable to delete phase 1 localization file",
                        		"newFilePath: " + newFilePath,
                        		e1);										
                    }
                }
                
                // parse the file, recognize its links, replace
                // in memory with links to relative and/or localized
                // files and write it out to a new file
                this.parser = new XMLSAXParser();
                boolean parseFailed = false;
                
                try {
                    this.parser.parse(this, 
                            XMLSAXParser.ACTION_REP_LKS,
                            newFilePath);
                    
                    // check that the file was created
                    File newF = new File(newFilePath);
                    if (!newF.exists()){
                    	throw new XMLWriterException("Localized file " + 
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
                    
                } catch (SAXException e) {
                	parseFailed = true;
                } catch (XMLWriterException e) {
                	parseFailed = true;
                }
                
                if (parseFailed) {
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
                    // create DataFile, add events and relationships
                    transformEnd(genProc, newFilePath, specProc);
                    
                    // check validation state of localized file, 
                    // if the original file is valid but the localized copy is invalid,
                    // the package should be rejected
                    if (this.getLocToDf() != null && 
                    	this.getDoc().getValidationState().equals(MLDocument.VALID_TRUE) &&
                        ((XML)this.getLocToDf()).getDoc().getValidationState().equals(MLDocument.VALID_FALSE) ) {
                        	Informer.getInstance().error(this,
                                methodName,
                                "The original file is valid but the localized file is not valid ",
                                "File: " + this.getLocToDf().getPath(),                                
                                new PackageException("Localized files can't be invalid if the original is valid"));
                    }                      
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
	 * Parses itself and sets format-specific members. 
	 * Parses using a SAX parser. Extracts the technical metadata
	 * from the XML file, populates its members with this
	 * metadata and recognizes and records any external
	 * links (schema, etc.). 
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		String methodName = "parse()";
		
		// start building info about the XML document in this file
		this.doc = new XMLDocument(this);
		this.addBitstream(doc);
		
		// set generic XML bitstream info that you don't
		// even need to parse the file to know
		this.doc.setMarkupBasis(MLDocument.MB_XML);
		this.doc.setBsTable(ArchiveDatabase.TABLE_BS_MARKUP);
		this.doc.getCompression().setCompression(Compression.COMP_NOT_APPLICABLE);
		this.doc.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		this.doc.setLocation("0");
		this.doc.setRole(BitstreamRole.DOC_MAIN);
		
		this.parser = new XMLSAXParser();
		
		try {
			// extract metadata
			this.parser.parse(this, XMLSAXParser.ACTION_EXT_META);
		} catch (SAXParseException e) {
			// general problem parsing this file - won't try to validate it later
			this.addAnomaly(this.getAnomsPossible().
					getSevereElement(XMLAnomalies.XML_BAD_FORMAT));
			//keepParsingFile = false;
		} catch (SAXException e) {
			//	not sure if we should really fail here or reject the package.
			// willl fail until a legitimate reason to just reject the package is found.
			//keepParsingFile = false;
			
			// unwrap the exception if applicable
			Exception x = e;
			if (e.getException() != null){
				x = e.getException();
			}
			
			Informer.getInstance().fail(this,methodName,
				"Met with a general SAX Exception while extracting metadata",
				"file: " + this.getPath(), x);
		}	
		
		this.parser = null;
	}      

	/**
	 * @param _encoding character encoding
	 */
	public void setEncoding(String _encoding) {
		this.encoding = _encoding;
	}
    
    /**
     * @param normAbsPath The normAbsPath to set.
     */
	/*
    public void setLocAbsPath(String normAbsPath) {
        this.locAbsPath = normAbsPath;
    }*/

	/**
	 * 
	 * @return the members of this class as a String
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		boolean isTrue;
		
		isTrue = this.hasFormatAttribute(XML.Attribute.HAS_DTD);
		sb.append("Has a DTD: " +  isTrue + "\n");
	
		isTrue = this.hasFormatAttribute(XML.Attribute.HAS_W3D_SCHEMA);
		sb.append("Has a W3D XML Schema: " +  isTrue + "\n");
		
		return sb.toString();
	}
	
	 /** 
	 * @see edu.fcla.daitss.file.DataFile#toXML()
	 */
	public Document toXML() throws FatalException {
	        // Document.
	        Document doc = super.toXML();

	        // Namespace.
	        String namespace = doc.getNamespaceURI();

	        // Root element.
	        Element rootElement = doc.getDocumentElement();
	        
	        return doc;
	    }
	
	/**
	 * Validates the XML file if it has a DTD or other type of schema.
	 * Assumes that all the resources it needs to validate are either 
	 * accessible via URLs or are in the correct relative path location.
	 * Note: the file has to be parsed first before it can be validated,
	 * so call parse() before this method.
	 * 
	 * To validate an XML file from another class use the public
	 * method <code>isValid()</code> in this same class.
	 * 
	 * @throws PackageException when a localized file is found to be invalid
	 * @throws FatalException
	 */
	private void validate() throws PackageException, FatalException {
		String methodName = "validate()";
		boolean keepParsingFile = true;
		
		Informer.getInstance().info(this, methodName,
				"Beginning to validate at " + DateTimeUtil.now(),
				"file: " + this.getPath(), false);
		
		// validate if applicable
		if (this.hasFormatAttribute(XML.Attribute.HAS_DTD) && 
			this.hasFormatAttribute(XML.Attribute.HAS_W3D_SCHEMA)) {
			// has a DTD and W3C XML schema - invalid without having to validate
			this.addAnomaly(this.getAnomsPossible().
					getSevereElement(XMLAnomalies.XML_MULT_SCHEMA_TYPES));
			// TODO: shouldn't this set isValid to false or at least
			// set keepParsingFile to false???
		}
			
		if (keepParsingFile &&  // true
				(this.hasFormatAttribute(XML.Attribute.HAS_DTD) || 
				this.hasFormatAttribute(XML.Attribute.HAS_W3D_SCHEMA))){
			// has either a DTD or a W3C schema - validate parse it
			this.parser = null;
			this.parser = new XMLSAXParser();
			boolean isValid = true;
			
			try {
				// validate
				this.parser.parse(this, XMLSAXParser.ACTION_VALIDATE);			
			} catch (SAXParseException e) {
				// general problem parsing this file
				keepParsingFile = false;
				this.doc.setValidationState(MLDocument.VALID_FALSE);
				isValid = false;
				// we are treating exceptions when validating as the file
				// being not valid. we already recorded that the file was not
				// valid but we'll log a warning that a SAXException was thrown
				 Informer.getInstance().warning(this, methodName,
					 "Met with a SAXParseException during validating at line: " + e.getLineNumber() + 
					 ", column: " + e.getColumnNumber() + ", SystemId: " + e.getSystemId() + 
					 ", PublicId: " + e.getPublicId() + ", Message:" + e.getMessage(),
					 "file: " + this.getPath());
				this.addAnomaly(this.getAnomsPossible().getSevereElement(XMLAnomalies.XML_BAD_FORMAT));
				
			} catch (SAXException e) {
				keepParsingFile = false;
				this.doc.setValidationState(MLDocument.VALID_FALSE);
				isValid = false;
				
				// unwrap the exception if applicable
				Exception x = e;
				if (e.getException() != null){
					x = e.getException();
				}
				
				// we are treating exceptions when validating as the file
				// being not valid. we already recorded that the file was not
				// valid but we'll log a warning that a SAXException was thrown
				Informer.getInstance().warning(this,  methodName,
					 "Met with a general SAXException when validating: " + x.getMessage(),
					 "file: " + this.getPath());
				this.addAnomaly(this.getAnomsPossible().getSevereElement(XMLAnomalies.XML_BAD_FORMAT));
			}
			 
			if (isValid) {
			    this.doc.setValidationState(MLDocument.VALID_TRUE);
			}
		} else if (keepParsingFile && 
				!this.hasFormatAttribute(XML.Attribute.HAS_DTD) && 
				!this.hasFormatAttribute(XML.Attribute.HAS_W3D_SCHEMA)) {
			// has neither a DTD nor a W3C XML schema
			this.doc.setValidationState(MLDocument.VALID_NA);
		} 
		
		Informer.getInstance().info(this, methodName, 
				"Done validating at " + DateTimeUtil.now(),
				"file: " + this.getPath() + " Validation state: " 
				+ this.doc.getValidationState(), false);
        
		this.parser = null;
	}
} 
