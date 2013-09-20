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
 * A JP2 Image Header Box.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class ImageHeader_jp2 extends ImageHeader {
	
	/**
	 * Fixed value for the C Field.
	 */
	private static final int FIXED_C_VALUE = 7;

	/**
	 * @param box
	 */
	public ImageHeader_jp2(Box box) {
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
		return type == ImageHeader_jp2.FIXED_C_VALUE;
	}
	
    /**
     * Map a compression type known to the archive
     * to a JPEG2000 compression code.
     * @param type 
     * @return String
     */
	public String mapCompressionValue(int type){
		String comp = null;
		
		if (type == ImageHeader_jp2.FIXED_C_VALUE){
			comp = Compression.COMP_JPEG2000;
		}
		
		return comp;
	}
}
