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
package edu.fcla.daitss.format.image.jpeg2000.box;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.image.jpeg2000.Jp2Anomalies;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;

/**
 * File Type box.
 * This box specifies file type, version and compatibility 
 * information, including specifying if this file is a conforming
 * JP2 file or if it can be read by a conforming JP2 reader.
 * Can be present in: JP2, JPX
 * 
 * @author andrea
 *
 */
public class FileType extends Box {
	
	/**
	 * Equivalent to a major version number of the JP2 format.
	 * This value appears in the Brand field of a File Type box
	 * for formats that are completely defined by the JP2 spec.
	 */
	public static final int BRAND_JP2 = 0x6A703220;
	
	/**
	 * The number by which the content length is divisible
	 * without any remainder.
	 */
	private static final int MODULUS_CONTENT_LENGTH = 4;
	
	/**
	 * box type value;
	 */
	public static final int TYPE = 0x66747970;
	
	/**
	 * Brand field.
	 */
	private int brand = -1;
	
	/**
	 * Compatibility list.
	 * Should have at least 1 value in the list
	 * and one of these values should be jp2\040.
	 */		
	private int[] cl;
	
	/**
	 * length in bytes of the brand field.
	 */
	private int FIXED_BRAND_LENGTH = 4;
	
	/**
	 * Fixed Length in bytes of the MinV field.
	 * 
	 */
	private int FIXED_MINV_LENGTH = 4;
	
	/**
	 * Compatibility List field.
	 *  
	 */
	private int FIXED_PER_CL_LENGTH = 4;
	
	/**
	 * MinV field.
	 * Should be 0 but readers shouldn't care if its not.
	 */
	private int minV = -1;
	
	/*public boolean readContent(ByteReader reader) throws FatalException {
		boolean readOK = true;
		long contentLeft = this.getContentLength();
		
		// always read the brand
		if (reader.bytesLeft() >= FIXED_BRAND_LENGTH){
			this.brand = 
				(int) reader.readBytes(FIXED_BRAND_LENGTH, 
						DataFile.BYTE_ORDER_BE);
			this.contentLength -= FIXED_BRAND_LENGTH;
		} else {
			readOK = false;	
		}
		
		return readOK;
	}*/
	
	/**
	 * @param _box
	 */
	public FileType(Box _box) {
		super(_box, FileType.TYPE);
	}

	/*public FileType(int _length, int _type) {
		super(_length, _type);
	}*/

	/*public FileType(int _length, int _type, long _extLength) {
		super(_length, _type, _extLength);
	}*/
	
	/**
	 * Return the brand field.
	 * 
	 * @return int primitive
	 */
	public int getBrand() {
		return brand;
	}

	/**
	 * Read its content and set its members.
	 * If <code>justBrand</code> == true,
	 * then just read its Brand field.
	 * 
	 * @param reader open reader on a file
	 * @param justBrand whether or not to just read the Brand field
	 * @param addingAnomalies whether or not to add anomalies
	 * @param df the file to set values in if applicable
	 * @throws FatalException
	 */
	public void extractMetadata(ByteReader reader, boolean justBrand, 
			boolean addingAnomalies, Jpeg2000 df) 
		throws FatalException {
		boolean keepParsing = true;
		//long contentLeft = this.getContentLength();
		
		// always read the brand
		if (getContentLength() >= FIXED_BRAND_LENGTH){
			this.brand = 
				(int) reader.readBytes(FIXED_BRAND_LENGTH, 
						DataFile.BYTE_ORDER_BE);
			// adjust the number of bytes left to read
			subtractFromBytesLeft(FIXED_BRAND_LENGTH);
		} else {
			// box bytes left not big enough to hold Brand
			keepParsing = false;
			
			if (addingAnomalies){
				SevereElement ja = 
					df.getAnomsPossible().
					getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_FTYPBOX);
				df.addAnomaly(ja);
			}
		}
		
		if (justBrand) {
			return;
		}
		
		// check that the box is of minimum length and
		// divisible by 4
		if (keepParsing && this.getContentLength() < FIXED_MINV_LENGTH
				|| this.getContentLength() % MODULUS_CONTENT_LENGTH != 0){
			keepParsing = false;
			if (addingAnomalies){
				SevereElement ja = 
					df.getAnomsPossible().
					getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_FTYPBOX);
				df.addAnomaly(ja);
			}
			
		}
	
		if (keepParsing){
			// read the MinV field
			this.minV = 
				(int) reader.readBytes(FIXED_MINV_LENGTH, 
						DataFile.BYTE_ORDER_BE);
			subtractFromBytesLeft(FIXED_MINV_LENGTH);
			
			if (this.minV != 0 && addingAnomalies){
				// unrecognized minor version
				SevereElement ja = 
					df.getAnomsPossible().getSevereElement(
						Jpeg2000Anomalies.JPEG2K_UNK_MIN_VERSION);
				df.addAnomaly(ja);
				df.setMediaTypeVersion("1." + 
						String.valueOf(this.minV));
				
				// keep parsing
			} else {
				df.setMediaTypeVersion("1.0");
			}
			
			// already know contentLeft is a multiple of 4 so this should
			// end correctly
			boolean sawJp2Compat = false;
			
			long numClEntries = this.getContentLength()/MODULUS_CONTENT_LENGTH;
			cl = new int[(int)numClEntries];
			
			//System.out.println("cl length: " + cl.length);
			int counter = 0;
			while (this.getContentLength() != 0) {
				int profile = (int) reader.readBytes(FIXED_PER_CL_LENGTH, 
						DataFile.BYTE_ORDER_BE);
				subtractFromBytesLeft(FIXED_PER_CL_LENGTH);
				cl[counter] = profile;
				if (profile == BRAND_JP2){
					// is compatible with the JP2 spec
					sawJp2Compat = true;
				}
				counter++;
				//System.out.println("profile: " + Integer.toHexString(profile));
			}
			
			// Add an anomaly if this is a JP2 and the compatibility
			// list does not contain the JP2 brand.
			if (!sawJp2Compat && addingAnomalies &&
					df.getMediaType().equals(MimeMediaType.MIME_IMG_JP2)){
				SevereElement ja = 
					df.getAnomsPossible().
					getSevereElement(Jp2Anomalies.JP2_NONCOMPAT_JP2);
				df.addAnomaly(ja);
				// might as well stop parsing this JP2 if its not
				// compatible with the ISO Jp2 v1 spec
				df.setContinueParsing(false);
			}
		} 
	}

}
