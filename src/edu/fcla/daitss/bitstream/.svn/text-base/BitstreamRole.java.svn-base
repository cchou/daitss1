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
 * Created on Nov 26, 2003
 *
 */
package edu.fcla.daitss.bitstream;

/**
 * BitstreamRole represents the role of a bitstream within a file, for example
 * a bitstream in a image file could be a 'thumbnail' of another bitstream in the 
 * image file.
 */
public class BitstreamRole {
	
	/**
	 * The main  or only audio in the file
	 */
	public static final String AUDIO_MAIN = "AUDIO_MAIN";
	
	/**
	 * Audio for previe movie
	 */
	public static final String AUDIO_PREVIEW = "AUDIO_PREVIEW";
	/**
	 * Audio for movie
	 */
	public static final String AUDIO_MOVIE = "AUDIO_MOVIE";
	/**
	 * Audio for poster
	 */
	public static final String AUDIO_POSTER = "AUDIO_POSTER";
	
	/**
	 * The main  or only document in the file
	 */
	public static final String DOC_MAIN = "DOC_MAIN";
	
	/**
	 * A lower resolution image of another image in the file
	 */
	public static final String IMG_LOW_RES = "IMG_LOW_RES";
	
	/**
	 * The main (assumed highest resolution) or only image in the file
	 */
	public static final String IMG_MAIN = "IMG_MAIN";
	
	/**
	 * A single page of a multi-page image
	 */
	public static final String IMG_PAGE = "IMG_PAGE";
	
	/**
	 * A thumbnail (smaller dimension) of the main image
	 */
	public static final String IMG_THUMBNAIL = "IMG_THUMBNAIL";
	
	/**
	 * A transparancy mask
	 */
	public static final String IMG_TRANSPARENCY = "IMG_TRANSPARENCY";
	
	/**
	 * Textual metadata
	 */
	public static final String TXT_METADATA = "TXT_METADATA";
	
	/**
	 * Unknown role of the bitstream within the file
	 */
	public static final String UNKNOWN = "UNKNOWN";
	
	/**
	 * The main  or only video in the file
	 */
	public static final String VIDEO_MAIN = "VIDEO_MAIN";
	
	/**
	 * video for previe movie
	 */
	public static final String VIDEO_PREVIEW = "VIDEO_PREVIEW";
	/**
	 * video for movie
	 */
	public static final String VIDEO_MOVIE = "VIDEO_MOVIE";
	/**
	 * video for movie poster
	 */
	public static final String VIDEO_POSTER = "VIDEO_POSTER";
	
	/**
	* All the valid bitream roles
	*/
	private static String[] validRoles = {
		AUDIO_MAIN, AUDIO_PREVIEW, AUDIO_MOVIE, AUDIO_POSTER, DOC_MAIN,
		IMG_LOW_RES, IMG_MAIN, IMG_PAGE, IMG_THUMBNAIL, IMG_TRANSPARENCY, 
		TXT_METADATA, UNKNOWN,
		VIDEO_MAIN, VIDEO_PREVIEW, VIDEO_MOVIE, VIDEO_POSTER
		};
	
	/**
	 * Checks if a role code is a valid one (is a constant in this class).
	 * 
	 * @param role	the code for a role
	 * @return	<code>true</code> if the role code is valid, else
	 * 				<code>false</code>
	 */
	public static boolean isValidRole(String role){
		for (int i=0; i<validRoles.length; i++) {
			if (role.equals(validRoles[i])){
				return true;
			}
		} 
		return false;
	}

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		boolean result = BitstreamRole.isValidRole("UNKNOWNE");
		System.out.println(result);
	}
}
