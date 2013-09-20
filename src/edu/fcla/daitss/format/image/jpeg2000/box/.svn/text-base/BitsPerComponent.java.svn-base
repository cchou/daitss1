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

import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.util.ByteReader;

/**
 * The BitsPerComponent box common to the JP2 and JPX 
 * formats.
 *
 * @author Andrea Goethals, FCLA
 */
public class BitsPerComponent extends Box {

	/**
	 * box type value;
	 */
	public static final int TYPE = 0x62706363;
    
    /**
     * Fixed size in bytes of each BPC field.
     */
    protected static final int FIXED_BPCi_LENGTH = 1;
    
    /**
     * Number of compoents that should be found in this box.
     * This is the number indicated by the NC field in the
     * Image Header box.
     */
    private int numNCComponents = -1;
	
    /**
     * 
     * @param box
     * @param numComponents
     */
	public BitsPerComponent(Box box, int numComponents) {
		super(box, BitsPerComponent.TYPE);
        this.numNCComponents = numComponents;
	}

    /**
     * Parse, extract metadata, look for anomalies.
     * 
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        
        System.out.println("\tBPCC");
        
        // the size of the content in this box should be 
        // (FIXED_BPCi * numNCComponents) bytes
        if (this.contentLength != 
            (FIXED_BPCi_LENGTH * numNCComponents)){
            // doesn't have the correct number of values
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_IHDRBOX));
            df.continueParsing = false;
            
            // move reader to end of this box
            reader.skipBytes((int)this.contentLength);
        }
        
        if (df.continueParsing()){
            // parse the box
            Vector bps = new Vector();
            while (this.contentLength > 0){
                int bitsPerComp = (int) reader.readBytes(
                        FIXED_BPCi_LENGTH, df.getByteOrder());
                bps.add(new Integer(bitsPerComp+1));
                
                subtractFromBytesLeft(FIXED_BPCi_LENGTH);
            }
            df.getCodeStream().setBitsPerSample(bps);
        }
        
        // note that we know there aren't any leftover
        // unread bytes because we checked the box length
        // in the beginning of this method
 
    }

}
