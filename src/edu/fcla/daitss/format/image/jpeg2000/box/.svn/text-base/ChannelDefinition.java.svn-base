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
 * Channel definition box.
 * Only difference between the JP2 and JPX
 * channel definition boxes is that the latter can
 * map more color space values in the ASOCi field.
 * As far as validation goes, there's no difference
 * between the component mapping boxes of JP2s
 * and JPXs.
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class ChannelDefinition extends Box {
	
	/**
     * Fixed size in bytes of each ASOCi field.
     */
    protected static final int FIXED_ASOCi_LENGTH = 2;
    
    /**
     * Fixed size in bytes of each CNi field.
     */
    protected static final int FIXED_CNi_LENGTH = 2;
    
    /**
     * Fixed size in bytes of the N field.
     */
    protected static final int FIXED_N_LENGTH = 2;
    
    /**
     * Fixed size in bytes of each TYPi field.
     */
    protected static final int FIXED_TYPi_LENGTH = 2;
    
    /**
     * Minimum value of the N field.
     */
    protected static final int MIN_N_VALUE = 0;
    
    /**
     * Maximum value of the N field.
     */
    protected static final int MAX_N_VALUE = 
        ((int)Math.pow(2,16))-1;
    
    /**
	 * box type value;
	 */
	public static final int TYPE = 0x63646566;

    /**
     * 
     * @param box
     */
	public ChannelDefinition(Box box) {
		super(box, ChannelDefinition.TYPE);
	}
    
    /**
     * 
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        
        System.out.println("\tCDEF");
        boolean keepParsingBox = true;
        
        if (contentLength < (FIXED_N_LENGTH + 
                FIXED_CNi_LENGTH + FIXED_TYPi_LENGTH +
                FIXED_ASOCi_LENGTH)){
            // content length not large enough to
            // contain a single channel description
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_CDEFBOX));
        } else {
            // number of channel descriptions
            int numChan = (int) reader.readBytes(
                    FIXED_N_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_N_LENGTH);
            
            if (numChan < MIN_N_VALUE || 
                    numChan > MAX_N_VALUE){
                df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_CDEFBOX));
            } else {
                // number of bytes left in the content
                // should be numChannels (N field value)
                // * the fixed sizes of the CNi, TYPi and
                // ASOCi fields
                if (contentLength != 
                    numChan * (FIXED_CNi_LENGTH + 
                            FIXED_TYPi_LENGTH + 
                            FIXED_ASOCi_LENGTH)){
                    df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_CDEFBOX));
                    keepParsingBox = false;
                    
                    // TODO: read these values and if the
                    // value of TYPi is 1 or 2 then add to
                    // the bitstream's extraSamples 
                    // SAMPLE_DESC_ASSOC_ALPHA
                    
                    // TODO: if the TYPi value is 2^16-1 then
                    // add to the bitstream's extraSamples
                    // SAMPLE_DESC_UNKNOWN
                    
                    
                    reader.skipBytes((int)contentLength);
                    // stop parsing file
                    df.setContinueParsing(false);
                }
            }
            
        }
        
        // parse the array of 3 fields
        while (keepParsingBox && contentLength>0){
            // we know the length of content left to read 
            // is correct or we would have stopped parsing
            // already
            
            // channel index
            int ci = (int) reader.readBytes(
                    FIXED_CNi_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_CNi_LENGTH);
            
            // channel type
            int ct = (int) reader.readBytes(
                    FIXED_TYPi_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_TYPi_LENGTH);
            // TODO: could determine if the image has transparancy 
            // from this value
            
            // channel association
            int ca = (int) reader.readBytes(
                    FIXED_ASOCi_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_ASOCi_LENGTH);
            
            // TODO: could validate these 3 fields.
            // If so add min and max constant values for each
            
        }
        
    }

}
