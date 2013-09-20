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
import edu.fcla.daitss.util.ByteReader;

/**
 * ComponentMapping box found in a JP2 or JPX file.
 * @author Andrea Goethals, FCLA
 */
public class ComponentMapping extends Box {

	/**        
     * Fixed size in bytes of each CMPi field.
     */
    protected static final int FIXED_CMPi_LENGTH = 2;
    
    /**        
     * Fixed size in bytes of each MTYPi field.
     */
    protected static final int FIXED_MTYPi_LENGTH = 1;
    
    /**        
     * Fixed size in bytes of each PCOLi field.
     */
    protected static final int FIXED_PCOLi_LENGTH = 1;
    
    /**
     * Maximum value per CMPi field.
     */
    protected static final int MAX_CMPi_VALUE = 16384;
    
    /**
     * Maximum value per MTYPi field.
     */
    protected static final int MAX_MTYPi_VALUE = 1;
    
    /**
     * Maximum value per PCOLi field.
     */
    protected static final int MAX_PCOLi_VALUE = 255;
    
    /**
     * Minimum value per CMPi field.
     */
    protected static final int MIN_CMPi_VALUE = 0;
    
    /**
     * Minimum value per MTYPi field.
     */
    protected static final int MIN_MTYPi_VALUE = 0;
    
    /**
     * Minimum value per PCOLi field.
     */
    protected static final int MIN_PCOLi_VALUE = 0;
    
    /**
	 * box type value;
	 */
	public static final int TYPE = 0x636D6170;
	
    /**
     * @param box
     */
	public ComponentMapping(Box box) {
		super(box, ComponentMapping.TYPE);
	}
    
    /**
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        System.out.println("\tCMAP");
        
        boolean keepParsingBox = true;
        
        // check to see that the content length can hold
        // at least 1 channel.
        if (contentLength < (FIXED_CMPi_LENGTH + 
                FIXED_MTYPi_LENGTH + FIXED_PCOLi_LENGTH)){
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                        Jpeg2000Anomalies.JPEG2K_BAD_CMAPBOX));
            keepParsingBox = false;
            df.setContinueParsing(false);
        }
        
        // parse the array of the 3 fields
        while (keepParsingBox && contentLength > 0){
            // component index mapped to this channel
            int cmpi =  (int) reader.readBytes(
                    FIXED_CMPi_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_CMPi_LENGTH);
            
            if (cmpi < MIN_CMPi_VALUE || cmpi > MAX_CMPi_VALUE){
                df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                        Jpeg2000Anomalies.JPEG2K_BAD_CMAPBOX));
            }
            
            // how the channel is generated from the actual components
            if (keepParsingBox){
                int mtypi = (int) reader.readBytes(
                        FIXED_MTYPi_LENGTH, df.getByteOrder());
                subtractFromBytesLeft(FIXED_MTYPi_LENGTH);
                
                if (mtypi < MIN_MTYPi_VALUE || mtypi > MAX_MTYPi_VALUE){
                    df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_CMAPBOX));
                }
            }
            
            // palette index component mapped to the actual component
            if (keepParsingBox){
                int pcoli = (int) reader.readBytes(
                        FIXED_PCOLi_LENGTH, df.getByteOrder());
                subtractFromBytesLeft(FIXED_PCOLi_LENGTH);
                
                if (pcoli < MIN_PCOLi_VALUE || pcoli > MAX_PCOLi_VALUE){
                    df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_CMAPBOX));
                }
            }
        }
    }
}
