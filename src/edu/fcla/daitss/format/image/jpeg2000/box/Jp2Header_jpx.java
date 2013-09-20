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
import edu.fcla.daitss.format.image.jpeg2000.Jp2;
import edu.fcla.daitss.format.image.jpeg2000.Jpx;
import edu.fcla.daitss.util.ByteReader;


/**
 * The JPX version of the JP2 Header Box.
 * 
 * Notes:
 * It is unclear whether or not a Label Box is valid
 * in a JPX's JP2 Header Box. Figure M.6 of 15444-2:2004(E)
 * shows a Label Box as the first box in a JP2 Header Box 
 * (note that it is even before the Image Header Box). 
 * Section M.11.5. describes the JP2 Header box but makes
 * no mention of the Label box. In fact it says that it
 * is syntactically the same as a JP2's JP2 Header Box
 * (which can not have a Label Box). In section M.11.13,
 * which describes the Label Box, it makes no mention of
 * this box appearing in the JP2 Header Box. The parsing
 * methods in this class err on the side of allowing the 
 * Label Box anywhere in the JPX's JP2 Header Box but 
 * adding an anomaly to the file noting this.
 *  
 * @author andrea
 *
 */
public class Jp2Header_jpx extends Jp2Header {

	public Jp2Header_jpx(Box box) {
		super(box);
	}

	/**		
	 * Parse a Jpx Header Box. 
	 * Sets members based on the internal metadata found in
	 * this box (image height, image width, number of components,
	 * bits per component, compression, whether the color space
	 * is known, whether the file contains intellectual property
	 * rights information).
	 * 
	 * @param reader
	 * @param df
	 * @throws FatalException
	 */
	public void extractMetadata(ByteReader reader, Jpx df) 
		throws FatalException {
		
		
	}

}
