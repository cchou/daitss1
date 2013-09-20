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
package edu.fcla.daitss.format.video;

import java.lang.reflect.Field;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

public class VideoCompression  extends Compression {
	// Cinepak (video)	 
	public static final String COMP_CVID = "CVID";
	
	// AVI: Indeo Video 3.2 (video) 
	public static final String COMP_IV32 = "IV32";
	
	// AVI: Indeo Video 5.0 (video) 
	public static final String COMP_IV50 = "IV50";
	
	// Motion JPEG (video)
	public static final String COMP_MJPG = "MJPG";
	
	// QuickTime: SMC (Graphics)
	public static final String COMP_SMC = "SMC";
	
	// AVC1 (H.264)
	public static final String COMP_AVC1 = "AVC1";

	private static String CLASSNAME = 
		"edu.fcla.daitss.format.video.VideoCompression";
	
	public VideoCompression() {
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
		Field[] fields = new VideoCompression().getClass().getFields();
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
