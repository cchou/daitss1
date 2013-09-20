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
package edu.fcla.daitss.format.image;

import java.util.Vector;

import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.ByteReader;

/**
 * AdobeIRBs represent Adobe's Image Resource Blocks that
 * can be found in Adobe JPEGs, Tiffs, and other formats created
 * with Adobe's software.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class AdobeIRBs {
	
	/**
	 * The signature that Photoshop always uses to begin an
	 * image resource block.
	 */
	public static final int PHOTOSHOP_SIG = 0x3842494d;  // '8BIM'

	/**
	 * Determines whether or not a number is a valid Adobe
	 * Image Resource Block ID. Taken from "Adobe Photoshop
	 * 6.0 File Formats Specification", Version 6.0 Release 1,
	 * September 2000.
	 * 
	 * @param id
	 * @return boolean primitive
	 */
	public static boolean isKnownID(int id) {
		boolean result = false;

		switch (id) {
			// let all valid Image Resource IDs fall through
			case 0x03e8 :
			case 0x03e9 :
			case 0x03eb :
			case 0x03ed :
			case 0x03ee :
			case 0x03ef :
			case 0x03f0 :
			case 0x03f1 :
			case 0x03f2 :
			case 0x03f3 :
			case 0x03f4 :
			case 0x03f5 :
			case 0x03f6 :
			case 0x03f7 :
			case 0x03f8 :
			case 0x03f9 :
			case 0x03fa :
			case 0x03fb :
			case 0x03fc :
			case 0x03fd :
			case 0x03fe :
			case 0x03ff :
			case 0x0400 :
			case 0x0401 :
			case 0x0402 :
			case 0x0403 :
			case 0x0404 :
			case 0x0405 :
			case 0x0406 :
			case 0x0408 :
			case 0x0409 :
			case 0x040a :
			case 0x040b :
			case 0x040c :
			case 0x040d :
			case 0x040e :
			case 0x040f :
			case 0x0410 :
			case 0x0411 :
			case 0x0412 :
			case 0x0413 :
			case 0x0414 :
			case 0x0415 :
			case 0x0416 :
			case 0x0417 :
			case 0x0419 :
			case 0x041a :
			case 0x041b :
			case 0x041c :
			case 0x041d :
			case 0x041e :
			case 0x0421 :
			case 0x0bb7 :
			case 0x02710 :
				result = true;
				break;
			default : // leave false
		}
		
		// include the check for it being a path information id
		if (id >= 2000 && id <= 2998){
			result = true;
		}

		return result;
	}

	/**
	 * Parses an Adobe Image Resource Block and sets any relevant
	 * information in the file and/or image.
	 * 
	 * @param df
	 * @param image
	 * @param irb
	 */
	public static void parseIrb(DataFile df, Image image, byte[] irb) {

	}

	/**
	* A reader of bytes for use in parsing this file
	*/
	private ByteReader reader = null;

	/**
	 * 
	 *
	 */
	public AdobeIRBs() {

	}

	/**
	 * Determines whether or not this Image Resource Block is
	 * valid. To be valid it must start with 0x3842 ('8BIM'), be
	 * followed by a valid Image resource ID, followed by a Pascal string,
	 * followed by 4 bytes that specify how many more bytes follow
	 * in the block.
	 * 
	 * @param irb
	 * @return boolean 
	 */
	private boolean isValidIrb(Vector irb) {
		boolean result = false;

		return result;
	}

}
