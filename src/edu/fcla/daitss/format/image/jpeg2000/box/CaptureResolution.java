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
 * Capture resolution box.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class CaptureResolution extends Box {

	/**
     * Fixed size in bytes of the box content.
     */
    public static final int FIXED_CONTENT_LENGTH = 10;
    
    /**
	 * box type value;
	 */
	public static final int TYPE = 0x72657363;
	
    /**
     * 
     * @param box
     */
	public CaptureResolution(Box box) {
		super(box, CaptureResolution.TYPE);
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
        
        reader.skipBytes((int)contentLength);
    }
}
