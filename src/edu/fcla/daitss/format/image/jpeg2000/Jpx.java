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
/*
 * Created on Aug 3, 2004
 *
 * 
 */
package edu.fcla.daitss.format.image.jpeg2000;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.image.jpeg2000.box.Box;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.Informer;

/**
 * Jpx represents the JPX file format that was specified
 * in part 2 of the JPEG 2000 version 1 specification.
 * 
 * @author Andrea Goethals, FCLA
 */
public class Jpx extends Jpeg2000 {
    
    /**
     * Attribute represents an attribute specific to Jpx files.
     * Note that the length of the values of these constants have to be 
     * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
     * they have to be unique in the FORMAT_ATTRIBUTES database table.
     * 
     * @author Andrea Goethals, FCLA
     *
     */
    public static class Attribute {
        
        /**
         * File contains image creation metadata.
         * See 15444-2 Appendix N
         */
        public static final String HAS_CREATION_MD = "IMAGE_JPX_HAS_CREATION_MD";
        
        /**
         * File contains content description metadata.
         * See 15444-2 Appendix N
         */
        public static final String HAS_DESCRIPTION_MD = 
            "IMAGE_JPX_HAS_DESCRIPTION_MD";
        
        /**
         * File contains history metadata.
         * See 15444-2 Appendix N
         */
        public static final String HAS_HISTORY_MD = "IMAGE_JPX_HAS_HISTORY_MD";
        
    }
	
	/**
	 * Equivalent to a major version number of the JPX format.
	 * This value appears in the Brand field of a File Type box
	 * for formats that are completely defined by the JPX spec.
	 */
	public static final int BRAND_JPX = 0x6A707820;
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.image.jpeg2000.Jpx";
	
	/**
     * JPX version 1.0
     */
    public static final String VERSION_1_0 = "1.0";
	
	/**
	 * Determines whether or not the file is a JPX file when metadata about
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
	 * Determines whether or not the file is a JP2 file when metadata about
	 * this file is available. Determines that it is a JP2 file
	 * by:
	 * First seeing if it's a JPEG 2000 format. All JPEG 2000
	 * file formats start with the following 12 bytes:
	 * 0X0000 0X000C 0X6A50 0X2020 0X0D0A 0X870A 
	 * 
	 * If the file has that then we see if its a JPX file. In a
	 * JPX file, the JPEG 2000 signature is followed by the File
	 * Type box. The value of the Brand field in this box is
	 * 'jpx\040'. Note that we are seeing if this file is entirely
	 * described by the JPX spec. It is possible that this file has
	 * a value other than 'jpx\040' but lists 'jpx\040' in the
	 * Compatibility field in the File Type box. For now we will
	 * not consider those files JPX files.
	 * 
	 * @return <code>true</code> if it is a JPX,
	 *               otherwise <code>false</code>
	 * @param filePath	an absolute path to an existing file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		
		// first see if its a JPEG 2000 file format
		boolean isType = Jpeg2000.isType(filePath, _metadata);
		
		// see if we should continue on with this
		if (isType) {
			isType = false;
			
			// now see if its a JPX
			ByteReader tempReader = null;
			File f = new File(filePath);
			tempReader = new ByteReader(f, "r");
			
			// skip past the first 12 bytes
			tempReader.skipBytes(12);
			
			// look for the File Type Box
			Box nextBox = readBoxHeader(tempReader);
			if (nextBox.getType() == BoxType.TYPE_FTYP){
				// look at brand
				int brand = 
					(int) tempReader.readBytes(4, DataFile.BYTE_ORDER_BE);
				if (brand == BRAND_JPX){
					isType = true;
				}
			}
			
			tempReader.close();
			tempReader = null;
			f = null;
		}
		
		return isType;
	}

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) 
		throws FatalException {
		String testFile = "/apps/testfiles/J2KP4files/testfiles_jp2/file1.jp2";
		
		if (Jpx.isType(testFile, null)) {
			System.out.println("Is a Jpx");
			Jpx jFile = new Jpx(testFile, new SIP("/daitss/test/AB12345678"));
			jFile.setOid("F20040101_AAAAAA");
			//jFile.extractMetadata();
			//System.out.println(jFile);
		} else {
			System.out.println("Is not a Jpx");
		}
	}

	/**
	 * The codestream (image) being parsed/processed currently
	 * by the parser. A JPX file can contain multiple codestreams.
	 */
	private Jp2Image codeStream = null;
	
	/**
	 * The constructor to call for an existing JPX file when metadata about
	 * this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public Jpx(String path, InformationPackage ip) throws FatalException {
		super(path, ip);
		
		this.setMediaType(MimeMediaType.MIME_IMG_JPX);
		this.setFileExt(Extension.EXT_JPX);
		
		this.anomsPossible = null;
		// TODO: JPX may have its own anomalies ...
		// if not this will go in JPEG2000's constructor
		this.anomsPossible = new Jpeg2000Anomalies();
	}

	/**
	 * @param path
	 * @param ip
	 * @param _metadata
	 * @throws FatalException
	 */
	public Jpx(String path, InformationPackage ip, METSDocument _metadata)
			throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
     /**
	 * Sets the members for which their value depends on the value
	 * of the members that were set by parsing this file. In other
	 * words, this is the 'second phase' of setting this file's members. 
	 * The first was the parsing phase.
	 * 
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		super.evalMembers();
		
	}
    
	 /**
	 * Parses itself and sets format-specific members. For roots of distributed
	 * objects, also recognizes links to external files and creates Link
	 * objects and adds them to the links Vector using addLink(Link). 
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		String methodName = "parse()";
		
		
	}

	
	/**
     * Puts all the file's members and their values 
     * in a String.
     * 
     * @return all the members and their values
     */
    public String toString() {
        String prior = super.toString();
        StringBuffer sb = new StringBuffer("");
        sb.append(prior);
        
        // add any JPX-specific items here
        boolean isTrue;
        
        isTrue = this.hasFormatAttribute(Jpx.Attribute.HAS_CREATION_MD);
        sb.append("Has creation metadata: " +  isTrue + "\n");
    
        isTrue = this.hasFormatAttribute(Jpx.Attribute.HAS_DESCRIPTION_MD);
        sb.append("Has content description metadata: " +  isTrue + "\n");
        
        isTrue = this.hasFormatAttribute(Jpx.Attribute.HAS_HISTORY_MD);
        sb.append("Has history metadata: " +  isTrue + "\n");
        
        return sb.toString();
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
}
