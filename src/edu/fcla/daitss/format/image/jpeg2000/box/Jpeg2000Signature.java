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

/**
 * JPEG 2000 Signature box
 * This box uniquely identifies the file as a JPEG 2000 file.
 * Can be present in: JP2, JPX
 * 
 * @author andrea
 *
 */
public class Jpeg2000Signature extends Box{
	
	/**
	 * total length in bytes of this box.
	 */
	public static final int FIXED_BOX_LENGTH = 12;
	
	/**
	 * fixed content of this box.
	 */
	public static final int[] FIXED_CONTENT = 
		{0x0000000C, 0x6A502020, 0x0D0A870A};
	
	/**
	 * box type value.
	 */
	public static final int TYPE = 0x6A502020;
	
	public Jpeg2000Signature(int _length, int _type) {
		super(_length, _type);
	}

	public Jpeg2000Signature(int _length, int _type, long _extLength) {
		super(_length, _type, _extLength);
	}

}
