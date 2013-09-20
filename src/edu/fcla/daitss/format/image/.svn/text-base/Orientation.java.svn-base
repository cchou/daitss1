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
 * Created on Sep 29, 2003
 *
 */
package edu.fcla.daitss.format.image;

/**
 * Orientation
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Orientation {

	/**
	 * The 0th row represents the visual bottom of the image, and the 0th 
	 * column represents the visual left-hand side.
	 */	
	public static final String BOTTOM_LEFT = "BL";

	/**
	 * The 0th row represents the visual bottom of the image, and the 0th 
	 * column represents the visual right-hand side.
	 */	
	public static final String BOTTOM_RIGHT = "BR";

	/**
	 * The 0th row represents the visual left-hand side of the image, and the 0th 
	 * column represents the visual bottom of the image.
	 */	
	public static final String LEFT_BOTTOM = "LB";

	/**
	 * The 0th row represents the visual left-hand side of the image, and the 0th 
	 * column represents the visual top of the image.
	 */	
	public static final String LEFT_TOP = "LT";

	/**
	 * The 0th row represents the visual right-hand side of the image, and the 0th 
	 * column represents the visual bottom of the image.
	 */	
	public static final String RIGHT_BOTTOM = "RB";

	/**
	 * The 0th row represents the visual right-hand side of the image, and the 0th 
	 * column represents the visual top of the image.
	 */	
	public static final String RIGHT_TOP = "RT";
	
	/**
	 * The 0th row represents the visual top of the image, and the 0th 
	 * column represents the visual left-hand side.
	 */
	public static final String TOP_LEFT = "TL";
	
	/**
	 * The 0th row represents the visual top of the image, and the 0th 
	 * column represents the visual right-hand side.
	 */	
	public static final String TOP_RIGHT = "TR";
	
	/**
	 * Unknown orientation.
	 */
	public static final String UNKNOWN = "UNKNOWN";
	
	/** 
	 * Valid orientations where the index into this array is the orienation's value
	 */
	private static String[] validOrientations = {UNKNOWN, TOP_LEFT,
		TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT,
		LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM,
		LEFT_BOTTOM};
	
	/**
	 * Checks that an orientation code is valid (is the value of a constant in
	 * this class). 
	 * 
	 * @param orientation	the code for an orientation
	 * @return	<code>true</code> if this is a valid code for an orientation,
	 * 				else <code>false</code>.
	 */
	public static boolean isValidOrientation(String orientation){
		for (int i=0; i<validOrientations.length; i++) {
			if (orientation.equals(validOrientations[i])){
				return true;
			}
		}
		return false;
	}
}
