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

import java.util.Vector;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.jpeg2000.Jp2;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;

/**
 * @author ?
 *
 */
public abstract class ImageHeader extends Box {
	
	/**
	 * 15444-1:2000(E) The length of the Image Header box shall
	 * be 22 bytes, including the box length and type fields.
	 */
	protected static final int FIXED_BOX_LENGTH = 22;
	
	/**
	 * Fixed size in bytes of the BPC field.
	 */
	protected static final int FIXED_BPC_LENGTH = 1;
	
	/**
	 * Fixed size in bytes of the C field.
	 */
	protected static final int FIXED_C_LENGTH = 1;
	
	/**
	 * Fixed size in bytes of the HEIGHT field.
	 */
	protected static final int FIXED_HEIGHT_LENGTH = 4;
	
	/**
	 * Fixed size in bytes of the IPR field.
	 */
	protected static final int FIXED_IPR_LENGTH = 1;
	
	/**
	 * Fixed size in bytes of the NC field.
	 */
	protected static final int FIXED_NC_LENGTH = 2;
	
	/**
	 * Fixed size in bytes of the UNK field.
	 */
	protected static final int FIXED_UNK_LENGTH = 1;
	
	/**
	 * Fixed size in bytes of the WIDTH field.
	 */
	protected static final int FIXED_WIDTH_LENGTH = 4;
	
	/**
	 * Maximum value for the HEIGHT field.
	 */
	protected static final int MAX_HEIGHT_VALUE = 
		((int)Math.pow(2,32))-1;
	
	/**
	 * Maximum value for the IPR field.
	 */
	protected static final int MAX_IPR_VALUE = 1;
	
	/**
	 * Maximum value for the NC field.
	 */
	protected static final int MAX_NC_VALUE = 16384;
	
	/**
	 * Maximum value for the UNK field.
	 */
	protected static final int MAX_UNK_VALUE = 1;
	
	/**
	 * Maximum value for the WIDTH field.
	 */
	protected static final int MAX_WIDTH_VALUE = 
		((int)Math.pow(2,32))-1;
	
	/**
	 * Minimum value for the HEIGHT field.
	 */
	protected static final int MIN_HEIGHT_VALUE = 1;
	
	/**
	 * Minimum value for the IPR field.
	 */
	protected static final int MIN_IPR_VALUE = 0;
	
	/**
	 * Minimum value for the NC field.
	 */
	protected static final int MIN_NC_VALUE = 1;
	
	/**
	 * Minimum value for the UNK field.
	 */
	protected static final int MIN_UNK_VALUE = 0;
	
	/**
	 * Minimum value for the WIDTH field.
	 */
	protected static final int MIN_WIDTH_VALUE = 1;
	
	/**
	 * box type value;
	 */
	public static final int TYPE = 0x69686472;

	/**
	 * @param box
	 */
	public ImageHeader(Box box) {
		super(box, ImageHeader.TYPE);
	}
	
	/**
	 * Determine whether the compression type is valid for 
     * the file.
	 * 
	 * @param type compression type
	 * @return boolean
	 */
	public abstract boolean isValidCompressionType(int type);
	
	/**
	 * Map the compression value to a type known by the archive.
	 * 
	 * @param type compression value
	 * @return String object
	 */
	public abstract String mapCompressionValue(int type);
	
	/**
	 * Parse the Image Header Box, extracting metadata and 
     * noting
	 * any anomalies.
	 * 
	 * @param reader
	 * @param df
	 * @param superbox
	 * @throws FatalException
	 */
	public void extractMetadata(ByteReader reader, Jpeg2000 df, 
			Jp2Header superbox) 
		throws FatalException {
		
		System.out.println("\tIHDR");
		
		if (this.getBoxLength() != ImageHeader.FIXED_BOX_LENGTH){
	        df.addAnomaly(df.getAnomsPossible().
					getSevereElement(
							Jpeg2000Anomalies.JPEG2K_BAD_IHDRBOX));
	        df.continueParsing = false;
	    }
		
		// HEIGHT
		int imgHeight = (int) reader.readBytes(
				ImageHeader.FIXED_HEIGHT_LENGTH, df.getByteOrder());
		//System.out.println("Image height: " + imgHeight);
		df.getCodeStream().setPixelsVertical(imgHeight);
		
		// WIDTH
		int imgWidth = (int) reader.readBytes(
				ImageHeader.FIXED_WIDTH_LENGTH, df.getByteOrder());
		//System.out.println("Image width: " + imgWidth);
		df.getCodeStream().setPixelsHorizontal(imgWidth);
		
		// NC
		int numComps = (int) reader.readBytes(
				ImageHeader.FIXED_NC_LENGTH, df.getByteOrder());
		//System.out.println("Number of components: " + numComps);
		df.getCodeStream().setNumComponents(numComps);
		
		// BPC
		int bitsPerComp = (int) reader.readBytes(
				ImageHeader.FIXED_BPC_LENGTH, df.getByteOrder());
		if (bitsPerComp > 127 && bitsPerComp < 166) {
			bitsPerComp = bitsPerComp - 128;
			df.getCodeStream().setHasSignedComps(DataFile.TRUE);
		} else {
			df.getCodeStream().setHasSignedComps(DataFile.FALSE);
		}
		if (bitsPerComp > -1 && bitsPerComp < 38) {
            // means that the components do not vary in bit depth
            // and the bit depth is specified here.
			Vector bps = new Vector();
			for (int i=0; i<numComps; i++) {
				bps.add(new Integer(bitsPerComp+1));
			}
			df.getCodeStream().setBitsPerSample(bps);
		} else if (bitsPerComp == 255){
            // means that the components vary in bit depth
			// remember the fact that this JP2 Header box will
			// need to have a Bits Per Component box
			superbox.setNeedsBpcBox(true);
		} else {
		    // illegal bpcc 
		    SevereElement ja = 
				df.getAnomsPossible().
				getSevereElement(
						Jpeg2000Anomalies.JPEG2K_BAD_IHDRBOX);
			df.addAnomaly(ja);
		}
		
		// C
		int compresType = (int) reader.readBytes(
				ImageHeader.FIXED_C_LENGTH, df.getByteOrder());
		//System.out.println("Compression type: " + compresType);
		if (!isValidCompressionType(compresType)) {
			SevereElement ja = 
				df.getAnomsPossible().
				getSevereElement(
						Jpeg2000Anomalies.FILE_UNKNOWN_COMPRESSION);
			df.addAnomaly(ja);
			// keep parsing file
		} else {
            // map the compression code to a type known
            // by the archive
			String comp = mapCompressionValue(compresType);
			if (comp != null && !comp.equals("")){
				df.getCodeStream().getCompression().setCompression(comp);
			} // else leave it set to unknown
			
		}
		
		// UNK
		int colorUnk = (int) reader.readBytes(
				ImageHeader.FIXED_UNK_LENGTH, df.getByteOrder());
		//System.out.println("Colorspace unknown: " + colorUnk);
		if (colorUnk == 0) {
			df.getCodeStream().setColorUnknown(DataFile.FALSE);
		} else if (colorUnk == 1){
			df.getCodeStream().setColorUnknown(DataFile.TRUE);
		} else {
			SevereElement ja = 
				df.getAnomsPossible().
				getSevereElement(Jpeg2000Anomalies.JPEG2K_UNK_UNKC);
			df.addAnomaly(ja);
			// keep parsing file
		}
		
		// IPR
		int hasIpr = (int) reader.readBytes(
				ImageHeader.FIXED_IPR_LENGTH, df.getByteOrder());
		//System.out.println("Has IPR metadata: " + hasIpr);
		if (hasIpr == 0) {
		    df.removeFormatAttribute(Jpeg2000.Attribute.HAS_IPR_MD);
		} else if (hasIpr == 1) {
		    df.addFormatAttribute(Jpeg2000.Attribute.HAS_IPR_MD);
		}
        
        // Note that we know there aren't any leftover unread 
        // bytes in the box because we checked the box
        // length in the beginning of this method
	}

}
