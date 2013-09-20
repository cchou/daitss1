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

import edu.fcla.daitss.bitstream.Compression;

/**
 * represents: 
 *
 */
public class ImageHeader_jpx extends ImageHeader {

	/**
	 * @param box
	 */
	public ImageHeader_jpx(Box box) {
		super(box);
	}

	/**
	 * Determines if the compression type is valid
	 * for the Image Header Box of a JP2 file.
	 * 
	 * @param type	compression type
	 * @return boolean
	 */
	public boolean isValidCompressionType(int type){
		return type > -1 && type < 10;
	}
	
	/**
	 * Map a compression value to the compression
	 * type known by the archive.
	 * 
	 * Note that values 4 and 9 both map to JBIG.
	 * This is not a mistake on the part of this
	 * method - this is what 15444-2 says (mistake
	 * on their part?)
	 * @param type 
	 * @return String
	 */
	public String mapCompressionValue(int type){
		String comp = null;
		
		switch(type){
			case 0: 
				// no compression
				comp = Compression.COMP_NONE;
				break;
			case 1: 
				// modified huffman
				comp = Compression.COMP_CCITT_ID;
				break;
			case 2: 
				// modified read
				comp = Compression.COMP_MOD_READ;
				break;
			case 3: 
				// modified modified read
				comp = Compression.COMP_MOD_MOD_READ;
				break;
			case 4: 
				// JBIG
				comp = Compression.COMP_JBIG;
				break;
			case 5: 
				// JPEG
				comp = Compression.COMP_JPEG;
				break;
			case 6: 
				// JPEG-LS
				comp = Compression.COMP_JPEG_LS;
				break;
			case 7: 
				// JPEG2000
				comp = Compression.COMP_JPEG2000;
				break;
			case 8: 
				// JBIG2
				comp = Compression.COMP_JBIG2;
				break;
			case 9: 
				// JBIG
				comp = Compression.COMP_JBIG;
				break;
		}
		
		return comp;
	}
}
