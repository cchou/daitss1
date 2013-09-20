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
package edu.fcla.daitss.format.image.jpeg2000;

import java.io.File;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.format.image.ImageMetadataConflicts;
import edu.fcla.daitss.format.image.jpeg2000.box.Box;
import edu.fcla.daitss.format.image.jpeg2000.box.Jpeg2000Signature;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * Jpeg2000 represents the common elements among the
 * file formats specified in the JPEG 2000 version 1 specification.
 * 
 * @author Andrea Goethals, FCLA
 */
public abstract class Jpeg2000 extends DataFile {
    
    /**
     * Attribute represents an attribute specific to JPEG 2000 files.
     * Note that the length of the values of these constants have to be 
     * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
     * they have to be unique in the FORMAT_ATTRIBUTES database table.
     * 
     * @author Andrea Goethals, FCLA
     *
     */
    public static class Attribute {
        
        /**
         * File contains intellectual property rights metadata.
         */
        public static final String HAS_IPR_MD = "IMAGE_JP2_HAS_IPR_MD";
        
        /**
         * File contains at least 1 XML box.
         */
        public static final String HAS_XML_MD = "IMAGE_JP2_HAS_XML_MD";
        
    }
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.image.jpeg2000.Jpeg2000";
	
    /**
     * The codestreams contained by the file.
     */
    protected Vector codeStreams = null;
    
    /**
     * For a JP2 the codestream retruend is the only
     * codestream in the file. For a JPX this returns
     * just the first codestream which will have all the
     * codestream defaults set in it. 
     * @return Jpeg2000Image document
     */
    public Jpeg2000Image getCodeStream(){
        return (Jpeg2000Image) codeStreams.elementAt(0);
    }
    
    
    /**
	 * Determines whether or not the file is a JPEG2000 file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a <FORMAT> file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
	 * Determines whether or not the file is a JPEG2000 file when metadata about
	 * this file is available. Determines that it is some kind of JPEG2000 file
	 * by reading the first 12 bytes of the file looking for a JPEG2000 Signature box.
	 * (should exist for all JPEG-2000 family files)
	 * TODO: jpx files allowing this box to occur anywhere as long
	 * as its not within another box
	 * 
	 * @return a boolean with <code>true</code> if it is a JPEG 2000 file,
	 *               otherwise <code>false</code>
	 * @param filePath	an absolute path to an existing file
	 * @param _metadata a lite DataFile containing metadata about 
	 * 		this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, 
			METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, METSDocument)";
		boolean isType = false;
		
		// check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Illegal argument",
				"filePath: " + filePath,
				new FatalException(
						"Not an existing, readable absolute path to a file"));
		}
		
		ByteReader tempReader = null;
		File f = new File(filePath);
		tempReader = new ByteReader(f, "r");

		// read the first 12 bytes, 4 at a time
		// assume it is a JPEG 2000 file until an unexpected byte is seen
		if (f.length() >= Jpeg2000Signature.FIXED_BOX_LENGTH){
			boolean done = false;
			int i=0;
			isType = true;
			while (!done && i<Jpeg2000Signature.FIXED_CONTENT.length){
				int readInt = 
					(int) tempReader.readBytes(4, DataFile.BYTE_ORDER_BE);
				if (readInt != Jpeg2000Signature.FIXED_CONTENT[i]){
					done = true;
					isType = false;
				}
				i++;
			}
		}
		
		tempReader.close();
		tempReader = null;
		f = null;
		
		return isType;
	} 
	
	 /**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws PackageException
	 * @throws FatalException
	 */
	public static void main(String[] args) 
		throws PackageException, FatalException {
	}

	
	/**
	 * Reads and returns a JPEG 2000 box.
	 * A box is the building block of JPEG 2000 file
	 * formats. 
	 * Note that it is the responsibility of the caller to
	 * this method to check that there are at least 8
	 * bytes to read. If the first four bytes have the value
	 * 0x0001, then there must be at least 16 bytes to read.
	 * 
	 * After calling this method the reader's file pointer is sitting
	 * at the beginning of the box content (DBox field).
	 * 
	 * @param reader a ByteReader reading an existing file
	 * @return the parsed JPEG 2000 box
	 * @throws FatalException
	 */
	public static Box readBoxHeader(ByteReader reader)
		throws FatalException {
		Box box = null;
		
		String endian = DataFile.BYTE_ORDER_BE;
        
        // record the byte offset of this box in case its needed
        long offset = reader.getFilePointer();
		
		int lboxValue = (int) reader.readBytes(Box.FIXED_LBOX_LENGTH, endian);
		int tboxValue = (int) reader.readBytes(Box.FIXED_TBOX_LENGTH, endian);
		
		// there's 3 cases for determining the length of the box,
		// depending on the value in the LBox field
		// note that values 2-7 are reserved for ISO use
		
		// Case 1: value == 1 (length found in the XLBox field)
		if (lboxValue == 1){
			long xlboxValue = reader.readBytes(
					Box.FIXED_XLBOX_LENGTH, endian);
			box = new Box(lboxValue, tboxValue, xlboxValue);
		// case 2: value == 0 (length not known when file was written;
		// box contains all bytes until the end of the file
		} else if (lboxValue == 0) {
			box = new Box(reader.bytesLeft(), lboxValue, tboxValue);
		// case 3: value > 8 (value is the length of the box, including
		//	the LBox and TBox fields
		} else {
			box = new Box(lboxValue, tboxValue);
		} 
		
        box.setFileOffset(offset);
        
		return box;
	}
	
	/**
	 * Whether or not to continue parsing this file 
	 * (might as well stop after seeing weirdness)
	 */
	public boolean continueParsing = true;

	/**
	 * @param path
	 * @param ip
	 * @throws FatalException
	 */
	public Jpeg2000(String path, InformationPackage ip) throws FatalException {
		super(path, ip);
		
		this.setByteOrder(DataFile.BYTE_ORDER_BE);
		
		// only one version so far
		this.setMediaTypeVersion("1.0");
        
        codeStreams = new Vector();
		
		this.anomsPossible = null;
		this.anomsPossible = new Jpeg2000Anomalies();
		
		metadataConfPossible = null;
		metadataConfPossible = new ImageMetadataConflicts();
	}
	
	/**
	 * @param path
	 * @param ip
	 * @param _metadata
	 * @throws FatalException
	 */
	public Jpeg2000(String path, InformationPackage ip, METSDocument _metadata)
			throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * @return boolean type
	 */
	public boolean continueParsing() {
		return continueParsing;
	}


	/**
	 * Parses itself and sets format-specific members. For roots of distributed
	 * objects, also recognizes links to external files and creates Link
	 * objects and adds them to the links Vector using addLink(Link). 
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		
	}
	
	/**
	 * @param continueParsing
	 */
	public void setContinueParsing(boolean continueParsing) {
		this.continueParsing = continueParsing;
	}

	/**
	 * Puts all the file's members and their values in a String.
	 * 
	 * @return all the members and their values
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
        
		boolean isTrue;
        
        isTrue = this.hasFormatAttribute(Jpeg2000.Attribute.HAS_IPR_MD);
        sb.append("Has IPR metadata: " +  isTrue + "\n");
    
        isTrue = this.hasFormatAttribute(Jpeg2000.Attribute.HAS_XML_MD);
        sb.append("Has XML metadata: " +  isTrue + "\n");
		
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
	        
	        // TODO waiting for dbupdate
	        
	        return doc;
	    }
}
