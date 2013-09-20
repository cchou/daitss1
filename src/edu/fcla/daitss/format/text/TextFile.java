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
package edu.fcla.daitss.format.text;


import java.io.File;
import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * TextFile represents a plain text file.
 * This is text without any formatting, font specification, etc.
 * 
 *  @author Andrea Goethals, FCLA
 */
public class TextFile extends DataFile {
    
	/**
	 * Attribute represents an attribute specific to text files.
	 * Note that the length of the values of these constants have to be 
	 * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
	 * they have to be unique in the FORMAT_ATTRIBUTES database table.
	 * 
	 * @author Andrea Goethals, FCLA
	 *
	 */
	public static class Attribute {
		
		/**
		 * File begins with the BOM (byte order mark).
		 * This looks like 0xfeff for big-endian data and 
		 * 0xfffe for little endian data.
		 */
		public static final String HAS_BOM = "TEXT_PLAIN_HAS_BOM";
		
	}
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
	    "edu.fcla.daitss.format.text.TextFile";
	
	/**
	 * The maximum number of bytes in the file read to determine if 
	 * this is text
	 */
	private static final long HOWMANY = 65536;
	
	/**
	 * Determines whether or not the file is a Text file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a Text file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
		
	/**
	 * Determines if an existing, readable file is a plain text file or not
 	 * when metadata about this file is available.
 	 * 
	 * If the file has at least one byte and the bytes comply with some 
	 * character set than it is said to be a text file. 
	 * 
	 * NOTE: Make sure that you have tried to see if the file was of all 
	 * other types before calling this to cut down on false positives!
	 * 
	 * @return 	whether or not its a text file					
	 * @param 	filePath absolute path to an existing readable file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, DataFile)";
		
		boolean isType = false;
		
		// check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(CLASSNAME,
				methodName,
				"Illegal argument",
				"filePath: " + filePath,
				new FatalException("Not an existing, readable absolute path to a file"));
		}
		
		TextFile tFile = null;

		// Needs at least 1 byte
		if (new File(filePath).length() >=1) {
		    tFile = new TextFile(filePath);
			
		    String theCharSet = null;            
		    try {
                theCharSet = tFile.detectCharSet();   
            } catch (FatalException e) {
                isType = false;
                theCharSet = DataFile.UNKNOWN;
            } catch (Exception e) {
                isType = false;
                theCharSet = DataFile.UNKNOWN;
            }

            if (theCharSet != DataFile.UNKNOWN){
				isType = true;
			}
		}
		
		tFile = null;
		
		return isType;
	}         
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 * @throws PackageException
	 */
	public static void main(String[] args) throws PackageException,
		FatalException {
		String testFile = "/apps/testPackages/WF00000001/full_citation.txt";
		
		if (TextFile.isType(testFile, null)) {
			TextFile textFile = new TextFile(testFile, new SIP("/daitss/test/AB12345678"));
			textFile.setOid("F20041001_AAAAAA");
			textFile.extractMetadata();
			System.out.println(textFile);
		} else {
			System.out.println("File " + testFile + " is not a text file");
		}
	}
	
	/**
	 * File characters converted from EBCDIC
	 */
	short[] fromEbcdic = null;
	
	/**
	 * The single text bitstream in this file.
	 */
	protected Text textStream = null;
	
	/**
	 * File's characters converted to Unicode
	 */
	protected char[] unicodeChars = null;
	
	/**
	 * Only for use by isType methods!
	 * 
	 * @param path absolute path to the text file
	 * @throws FatalException
	 */
	protected TextFile(String path) throws FatalException {
		this.setPath(path);
	}

	/**
	 * The constructor to call for an existing text file when 
	 * metadata about this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public TextFile(String path, InformationPackage ip)
		throws FatalException {
		super(path, ip);

		this.setMediaType(MimeMediaType.MIME_TXT_PLAIN);
		this.setFileExt("txt");
        
		this.anomsPossible = null;
		this.anomsPossible = new TextAnomalies();
	}      

	/**
	 * The constructor to call for an existing text file when  
	 * metadata about this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public TextFile(String path, InformationPackage ip, METSDocument _metadata)
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
	 * Attempts to detect (and set) the character set used in a text file.
	 * 
	 * @return the character set (in upper case)
	 * @throws FatalException
	 */
	protected String detectCharSet() throws FatalException{
		String theCharSet = DataFile.UNKNOWN;
		
		// GNU file's ascmagic.c port, sort-of
		File theFile = new File(this.getPath());
		ByteReader tempReader = new ByteReader(theFile, "r");
		long numToRead = Math.min(theFile.length(), HOWMANY);
		short[] theBytes = new short[(int)numToRead];
			
		// we're going to go ahead and create the Unicode version of the characters
		// in this file even though it's not needed to determine if it's a text file. We're
		// doing this because this method uses the same character set encoding
		// methods that will be used when we know it's a text file and want to get
		// info on it like the correct character set encoding, line endings, etc.
		
		this.unicodeChars = new char[(int)numToRead];
			
		for (int i=0; i<numToRead; i++){
			theBytes[i] = (short) tempReader.readBytes(1, DataFile.BYTE_ORDER_NA);
		}
		if (CharacterSet.looksPrintableAscii(theBytes, this)) {
			theCharSet = "US-ASCII";
			this.setByteOrder(DataFile.BYTE_ORDER_NA);
		} else if (CharacterSet.looksPrintableUtf8(theBytes, this)) {
			theCharSet = "UTF-8";
			this.setByteOrder(DataFile.BYTE_ORDER_BE);
		} else {
			byte x = CharacterSet.looksPrintableUtf16(theBytes, this);
			if  (x == 1) {
				theCharSet = "UTF16-LE";
				this.setByteOrder(DataFile.BYTE_ORDER_LE);
			} else if (x == 2) {
				theCharSet = "UTF-16BE";
				this.setByteOrder(DataFile.BYTE_ORDER_BE);
			} else if  (CharacterSet.looksPrintableIso8859(theBytes, this)) {
				theCharSet = "ISO-8859-x";
				this.setByteOrder(DataFile.BYTE_ORDER_NA);
			} else if  (CharacterSet.looksPrintableExtendedAscii(theBytes, this)) {
				theCharSet = "Extended Ascii";
				this.setByteOrder(DataFile.BYTE_ORDER_NA);
			} else {
				// pretend its EBCDIC and see what happens
				this.fromEbcdic = new short[theBytes.length];
				CharacterSet.ebcdicToAscii(theBytes, this);
				if (CharacterSet.looksPrintableAscii(this.fromEbcdic, this)) {
					theCharSet = "EBCDIC";
					this.setByteOrder(DataFile.BYTE_ORDER_NA);
				} else if (CharacterSet.looksPrintableIso8859(this.fromEbcdic, this)){
					theCharSet = "EBCDIC";
					this.setByteOrder(DataFile.BYTE_ORDER_NA);
				}
			}
		}
		
		tempReader.close();
		
		return theCharSet;
	}

	/**
	 * Used by subclases of TextFile (variations of plain text files, ex. XML DTD files)
	 * to get the text bitstream after its been parsed so that its member's values can be 
	 * copied to Text subclasses before further parsing.
	 * 
	 * @return the text bitstream from this file
	 */
	protected Text getTextStream() {
		return this.textStream;
	}

	/**
	 * Parses itself and sets format-specific members. 
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		this.textStream = new Text(this);
		this.addBitstream(textStream);
		
		String theCharSet = this.detectCharSet();
		if (!theCharSet.equals(DataFile.UNKNOWN)){
			textStream.setCharSet(theCharSet);
			textStream.setCharSetOrigin(Bitstream.ORIG_ARCHIVE);
			
			// count the lines and record the line endings
			long numLines = 0;
			char prevChar = 0;
			char prevPrevChar = 0;
			boolean justSawLineEnd = false; 
			long numCR = 0, numLF = 0, numCRLF = 0, numLFCR = 0;
			int numChars = 0;
			
			// this crazy loop goes one character past the length
			// of the array on purpose so that it can catch the case
			// where the file ends in a carraige return ('\r')
			for (int i=0; i<this.unicodeChars.length+1; i++) {
				char thisChar = 0;
				char nextChar = 0;
				if (i < unicodeChars.length) {
					thisChar = unicodeChars[i];
				}
				if (i<unicodeChars.length-1){
					nextChar = unicodeChars[i+1];
				}
				
				// stop at first line ending character
				if (prevChar == '\r' && thisChar == '\n'){
					// CRLF
					numLines++;
					numCRLF++;
					justSawLineEnd = true;
					numChars = 0;
				} else if (prevChar != '\r' && thisChar == '\n' && nextChar != '\r') {
					// LF
					numLines++;
					numLF++;
					justSawLineEnd = true;
					numChars = 0;
				} else if (prevPrevChar != '\n' && prevChar == '\r' && thisChar != '\n'){
					// CR
					numLines++;
					numCR++;
					justSawLineEnd = true;
					numChars = 0;
				} else if (prevPrevChar != '\r' && prevChar == '\n' && thisChar == '\r' 
								&& nextChar != '\n') {
					// LFCR
					numLines++;
					numLFCR++;
					justSawLineEnd = true;
					numChars = 0;
				} else {
					// not at a line ending yet
					justSawLineEnd = false;
					numChars++;
				}
				prevPrevChar = prevChar;
				prevChar = thisChar;
			}
			
			// add an extra line if there was at least one character after the last
			// line ending - an extra 'fake' character is always present in the file
			// so that is why we check for the presence of at least 2 characters after
			// the last line ending
			if (!justSawLineEnd && numChars > 1){
				numLines++;
			}
			
			this.textStream.setNumLines(numLines);
			
			int numEndingTypes = 0;
			if (numCR > 0) {
				this.textStream.addLineBreak(Text.LB_CR);
				numEndingTypes++;
			}
			if (numCRLF > 0) {
				this.textStream.addLineBreak(Text.LB_CRLF);
				numEndingTypes++;
			}
			if (numLF > 0) {
				this.textStream.addLineBreak(Text.LB_LF);
				numEndingTypes++;
			}
			if (numLFCR > 0) {
				this.textStream.addLineBreak(Text.LB_LFCR);
				numEndingTypes++;
			}
			
			if (numEndingTypes > 1){
				// uses multiple line ending characters - note this
				this.addAnomaly(anomsPossible.getSevereElement(TextAnomalies.TXT_MULT_LINEEND));
			}
	
			// account for when there are no line breaks
			if (numLines == 1 && this.textStream.getLineBreak().equals(UNKNOWN)){
				this.textStream.addLineBreak(Text.LB_NA);
			}
		} else {
			this.addAnomaly(anomsPossible.getSevereElement(TextAnomalies.TXT_UNKNOWN_CHARENC));
		}
	}            
	
	/**
	 * Returns the values of all its members.
	 * 
	 * @return the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		return sb.toString();
	}

}
