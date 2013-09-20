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
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;

/**
 * Default display resolution box.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class DefaultDisplayResolution extends Box {

	/**
     * Fixed size in bytes of the box content.
     */
    public static final int FIXED_CONTENT_LENGTH = 10;
    
    /**
     * Fixed size in bytes of the HRdD field.
     */
    public static final int FIXED_HRdD_LENGTH = 2;
    
    /**
     * Fixed size in bytes of the HRdE field.
     */
    public static final int FIXED_HRdE_LENGTH = 1;
    
    /**
     * Fixed size in bytes of the HRdN field.
     */
    public static final int FIXED_HRdN_LENGTH = 2;
    
    /**
     * Fixed size in bytes of the VRdD field.
     */
    public static final int FIXED_VRdD_LENGTH = 2;
    
    /**
     * Fixed size in bytes of the VRdE field.
     */
    public static final int FIXED_VRdE_LENGTH = 1;
    
    /**
     * Fixed size in bytes of the VRdN field.
     */
    public static final int FIXED_VRdN_LENGTH = 2;
    
    /**
	 * box type value;
	 */
	public static final int TYPE = 0x72657364;
	
    /**
     * 
     * @param box
     */
	public DefaultDisplayResolution(Box box) {
		super(box, DefaultDisplayResolution.TYPE);
	}
    
    /**
     * 
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        
        if (contentLength != FIXED_CONTENT_LENGTH){
            SevereElement ja = 
                df.getAnomsPossible().
                getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_RES0BOX);
            df.addAnomaly(ja);
            df.setContinueParsing(false);
        }
        
        // if it gets here we know that the box has the correct number
        // of bytes and we don't need to check it again
        
        // vertical display grid resolution numerator
        int vrdn =  (int) reader.readBytes(
                FIXED_VRdN_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_VRdN_LENGTH);
        
        // vertical display grid resolution denominator
        int vrdd =  (int) reader.readBytes(
                FIXED_VRdD_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_VRdD_LENGTH);

        // horizontal display grid resolution numerator
        int hrdn =  (int) reader.readBytes(
                FIXED_HRdN_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_HRdN_LENGTH);

        // horizontal display grid resolution denominator
        int hrdd =  (int) reader.readBytes(
                FIXED_HRdD_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_HRdD_LENGTH);
 
        // vertical display grid resolution exponent
        int vrde = (int) reader.readBytes(
                FIXED_VRdE_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_VRdE_LENGTH);
        
        // horizontal display grid resolution exponent
        int hrde = (int) reader.readBytes(
                FIXED_HRdE_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_HRdE_LENGTH);       
        
        // Using the formula on page 173 of 15444-1
        // vertical display grid resolution
        double vrd = ((double) vrdn/ (float) vrdd) * ((int) Math.pow(10, vrde));
        System.out.println("Def. display res (vertical):" + vrd);
        df.getCodeStream().setYResolution(vrd);
        
        // horizontal display grid resolution
        double hrd = ((double) hrdn/(float) hrdd) * ((int) Math.pow(10, hrde));
        System.out.println("Def. display res (horizontal):" + hrd);
        df.getCodeStream().setXResolution(hrd);
    }
}
