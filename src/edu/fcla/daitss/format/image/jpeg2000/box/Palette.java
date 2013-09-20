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
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.util.ByteReader;

/**
 * Palette box optionally found in JP2 or JPX files.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class Palette extends Box {

	/**
     * Fixed size in bytes of each Bi field.
     */
    protected static final int FIXED_Bi_LENGTH = 1;
    
    /**
     * Fixed size in bytes of the NE field.
     */
    protected static final int FIXED_NE_LENGTH = 2;
    
    /**
     * Fixed size in bytes of the NPC field.
     */
    protected static final int FIXED_NPC_LENGTH = 1;
    
    /**
     * Maximum value of the NE field.
     */
    protected static final int MAX_NE_VALUE = 1024;
    
    /**
     * Maximum value of the NPC field.
     */
    protected static final int MAX_NPC_VALUE = 255;
    
    /**
     * Minimum value of the NE field.
     */
    protected static final int MIN_NE_VALUE = 1;
    
    /**
     * Minimum value of the NPC field.
     */
    protected static final int MIN_NPC_VALUE = 1;
    
    /**
	 * box type value;
	 */
	public static final int TYPE = 0x70636C72;
	
    /**
     * 
     * @param box
     */
	public Palette(Box box) {
		super(box, Palette.TYPE);
	}
    
    /**
     * 
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        System.out.println("\tPCLR");
        df.getCodeStream().setHasIntPalette(DataFile.TRUE);
        boolean keepParsingBox = true;
        
        if (contentLength < (FIXED_NE_LENGTH + 
                FIXED_NPC_LENGTH + FIXED_Bi_LENGTH)){
            // in reality this box needs to have at least 1 
            // component value entry so the minimum 
            // content length is slightly bigger 
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                        Jpeg2000Anomalies.JPEG2K_BAD_PCLRBOX));
            keepParsingBox = false;
            
        }
        
        if (keepParsingBox){
            // number of entries in color table
            int ne = (int) reader.readBytes(
                    FIXED_NE_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_NE_LENGTH);
            
            if (ne < MIN_NE_VALUE || ne > MAX_NE_VALUE){
                df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(
                        Jpeg2000Anomalies.JPEG2K_BAD_PCLRBOX));
                keepParsingBox = false;
            }
        }
        
        int npc=0;
        if (keepParsingBox){
            // number of expanded components per palette component
            npc = (int) reader.readBytes(
                    FIXED_NPC_LENGTH, df.getByteOrder());
            subtractFromBytesLeft(FIXED_NPC_LENGTH);
            if (npc < MIN_NPC_VALUE || npc > MAX_NPC_VALUE){
                df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_PCLRBOX));
                keepParsingBox = false;
            }
        }
        
        if (keepParsingBox){
            // bit depth of generated component i
            // number of Bi fields == NPC field value
            if (contentLength < (npc*FIXED_Bi_LENGTH)){
                df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(
                            Jpeg2000Anomalies.JPEG2K_BAD_PCLRBOX));
                keepParsingBox = false;
            } else {
                for (int i=0; i<npc; i++) {
                    int bi = (int) reader.readBytes(
                            FIXED_Bi_LENGTH, df.getByteOrder());
                    subtractFromBytesLeft(FIXED_Bi_LENGTH);
                }
            }
            
        }
        
        // TODO: parse through the Cji fields to make sure
        // they look correct. Or at least that the number of
        // bytes left are correct.
        
        // move reader to end of this box
        reader.skipBytes((int)this.contentLength);
    }
}
