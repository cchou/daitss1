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
package edu.fcla.daitss.util;

import java.util.Iterator;
import java.util.Vector;

/**
 * A convenience class for String manipulation and coversion.
 * @author vicary
 */
public class StringUtil {

	/**
	 * Constructor.
	 */
	public StringUtil() {
	}

	/**
	 * Converts the contents of a Vector to a String.
	 * Essentially, toString() is called on each element of the
	 * Vector. Each element gets a new line.
	 *  
	 * @param v
	 * @return String object
	 */
	public static String getString(Vector v) {
		StringBuffer sb = new StringBuffer("");
		Iterator iter = v.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next().toString() + "\n");
		}		
		return sb.toString();
	}

	/**
	 * Converts the contents of an array to a String.
	 * Essentially, toString() is called on each element of the
	 * array. Each element gets a new line.
	 *  
	 * @param array
	 * @return String object
	 */
	public static String getString(Object[] array) {
		StringBuffer sb = new StringBuffer("");
		for (int i=0;i<array.length;i++) {
			sb.append(array[i].toString() + "\n");
		}
		return sb.toString();
	}


}
