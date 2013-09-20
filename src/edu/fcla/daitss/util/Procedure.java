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

/**
 * Procedure represents any procedures performed by the code
 * and any outcomes.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Procedure {
    
    /**
     * Procedure was called already.
     */
    public static final int CALLED = 1;
	
	/**
	 * Procedure was called but failed.
	 */
	public static final int FAILED = 3;
    
    /**
     * Procedure was not called yet.
     */
    public static final int NOT_CALLED = 0;
 
    /**
     * Description of downloading a link during <code>retrieveLinks</code>.
     * Used in an <code>Event</code> constructor.
     */
    public static final String PROC_DOWNLOADED_LINK = 
        "Downloaded the file from the Internet";
    
    /**
     * Description of migration to the JPEG_MIG_1 format.
     * Used in an <code>Event</code> constructor.
     */
    public static final String PROC_JPEG_MIG_1 = 
        "Create a jp2 file migrated from a jpeg file";
    
    /**
     * Description of normalization the bitstream in AVI files
     * Used in an <code>Event</code> constructor.
     */
    public static final String PROC_AVI_NORM_1 = 
        "Normalized the video and audio streams in the AVI file and wrapped those streams in an AVI file";
    
    /**
     * Description of normalization to the PDF_NORM_1 format.
     * Used in an <code>Event</code> constructor.
     */
    public static final String PROC_PDF_NORM_1 = 
        "Created a tiff file for each Pdf page and wrapped the tiffs in a METS file";
    
    /**
     * Description of normalization the bitstream in AVI files
     * Used in an <code>Event</code> constructor.
     */
    public static final String PROC_QUICKTIME_NORM_1 = 
        "Normalized the video and audio streams in the QUICKTIME file and wrapped those streams in a QuickTime file";
    
    /**
     * Description of normalization to the WAVE_NORM_1_1 format.
     * Used in an <code>Event</code> constructor.
     */
    public static final String PROC_WAVE_NORM_1 = 
        "Normalize the compressed audio stream in the WAVE file into PCM uncompressed format";
    
    /**
     * Description of localization to the XML_LOC_1 format.
     * Use in an <code>Event</code> constructor.
     */
    public static final String PROC_XML_LOC_1 = 
        "Made a copy of this XML file with relative links to files with relative links";
	
    /**
     * Description of localization to the XMLDTD_LOC_1 format.
     * Use in an <code>Event</code> constructor.
     */
    public static final String PROC_XMLDTD_LOC_1 = 
        "Made a copy of this XML DTD file with relative links to files with relative links";
    
    /**
     * Description of localization to the PDF_LOC_1 format.
     * Use in an <code>Event</code> constructor.
     */
    public static final String PROC_PDF_LOC_1 = 
        "Made a copy of this PDF file with relative links to files with relative links";
	
    /**
     * Description of localization to the QUICKTIME_LOC_1_1 format.
     * Use in an <code>Event</code> constructor.
     */
    public static final String PROC_QUICKTIME_LOC_1 = 
    	"Made a copy of this QuickTime file with relative links to files with relative links";
	/**
	 * Procedure was called and succeeded.
	 */
	public static final int SUCCEEDED = 4;

    /**
     * Localization transformation part 1.
     */
    public static final String TRANSFORM_LOC_1 = "LOCALIZE_1";
    
    /**
     * Localization transformation part 2.
     */
    public static final String TRANSFORM_LOC_2 = "LOCALIZE_2";
    
    /**
     * Migration transformation
     */
    public static final String TRANSFORM_MIG = "MIGRATE";
    
    /**
     * Normalization transformation
     */
    public static final String TRANSFORM_NORM = "NORMALIZE";
    
    /**
     * Returns the string version of an int procedure call
     * state. Useful for printing out the call state in a 
     * human-readable form.
     * 
     * @param state call state as an int
     * @return call state as a String
     */
    public static String getCallStateAsString(int state){
        String stateStr = null;
        
        switch(state){
        	case CALLED: stateStr = "CALLED"; break;
           	case FAILED: stateStr = "FAILED"; break;
           	case NOT_CALLED: stateStr = "NOT CALLED"; break;
           	case SUCCEEDED: stateStr = "SUCCEEDED"; break;
        }
        
        return stateStr;
    }
    
    /**
     * Determines whether or not a general transformation type
     * is valid for the archive. These types are constants in 
     * this class that begin with "TRANSFORM_".
     * 
     * 
     * @param type transformation type
     * @return whether the transform type is valid
     */
    public static boolean isValidTransformType(String type) {
        boolean isValid = false;
        
        if (type != null && 
                (type.equals(TRANSFORM_LOC_1) ||
                        type.equals(TRANSFORM_LOC_2) ||
                        type.equals(TRANSFORM_MIG) ||
                        type.equals(TRANSFORM_NORM))){
            isValid = true;
        }
        
        return isValid;
    }

}
