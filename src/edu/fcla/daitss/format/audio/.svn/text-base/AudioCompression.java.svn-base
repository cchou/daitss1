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
package edu.fcla.daitss.format.audio;

import java.lang.reflect.Field;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

public class AudioCompression extends Compression {
	
	// MPEG 1/2, Layer 3 (MP3)
	public static final String COMP_MP3 = "MP3";
	public static final String COMP_QDESIGN = "QDESIGN";
	
	private static String CLASSNAME = 
		"edu.fcla.daitss.format.audio.AudioCompression";
	
	public AudioCompression() {
	}
	
	/**
	 * Checks if a compression code is a valid one. 
	 * 
	 * @param compression	the code for a compression scheme
	 * @return	<code>true</code> if the compression code is valid, else
	 * 			<code>false</code>
	 * @throws FatalException
	 */
	public boolean isValidCompression(String compression)
		throws FatalException {
		
		String methodName = "isValidCompression(String)";
		boolean isValid = false, foundIt = false;
		
		// check that the compression value is compatible with
		// the database data constraints
		if (compression == null || compression.length() > MAX_COMP_LENGTH) {
			isValid = false;
			foundIt = true;
		}
		
		// dynamically make a list of all the compression types listed
		// in this class
		Field[] fields = new AudioCompression().getClass().getFields();
		int i= 0;
		while (!foundIt && i<fields.length) {
			try {
				// only want to consider the members that start with "COMP_"
				if (fields[i].getName().startsWith("COMP_") && 
					((String)fields[i].get(fields[i])).equals(compression.toUpperCase())){
					foundIt = true;
					isValid = true;
				}
				i++;
			} catch (IllegalArgumentException e) {
				Informer.getInstance().fail(CLASSNAME, methodName,
						"Illegal argument", "compression: " + compression,
						new FatalException("Not a valid compression type"));
			} catch (IllegalAccessException e) {
				Informer.getInstance().fail(CLASSNAME, methodName,
						"Illegal access", "field",
						new FatalException(e.getMessage()));
			}
		}
		
		return isValid;
	}
}
