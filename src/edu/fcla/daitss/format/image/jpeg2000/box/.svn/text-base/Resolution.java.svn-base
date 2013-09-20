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
import edu.fcla.daitss.format.image.jpeg2000.BoxType;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;

/**
 * Resolution box.
 * Contains either a Capture resolution box,
 * display resolution box or both.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class Resolution extends Box {

	/**
	 * box type value;
	 */
	public static final int TYPE = 0x72657320;
	
    /**
     * 
     * @param box
     */
	public Resolution(Box box) {
		super(box, Resolution.TYPE);
	}
    
    /**
     * 
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        
        System.out.println("/tRES0");
        boolean sawResCap = false;
        boolean sawResDis = false;
        
        while (df.continueParsing && contentLength > 0){
            Box nextBox = Jpeg2000.readBoxHeader(reader);
            if (nextBox.getType() == BoxType.TYPE_RESC){
                // capture resolution box
                if (sawResCap){
                    // already saw one of these - error
                    SevereElement ja = 
                        df.getAnomsPossible().
                        getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_RES0BOX);
                    df.addAnomaly(ja);
                    df.setContinueParsing(false);
                }
                sawResCap = true;
                CaptureResolution crb = new CaptureResolution(nextBox);
                crb.extractMetadata(reader, df);
                subtractFromBytesLeft(crb.boxLength);
            } else if (nextBox.getType() == BoxType.TYPE_RESD){
                // default display resolution box
                if (sawResDis){
                    // already saw one of these - error
                    SevereElement ja = 
                        df.getAnomsPossible().
                        getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_RES0BOX);
                    df.addAnomaly(ja);
                    df.setContinueParsing(false);
                }
                sawResDis = true;
                DefaultDisplayResolution ddrb = 
                    new DefaultDisplayResolution(nextBox);
                ddrb.extractMetadata(reader, df);
                subtractFromBytesLeft(ddrb.boxLength);
            } else {
                // error
                SevereElement ja = 
                    df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_RES0BOX);
                df.addAnomaly(ja);
                df.setContinueParsing(false);
            }
        }
        
        // make sure that this superbox contained at least 1
        // of the required boxes
        if (!sawResCap && !sawResDis){
            // didn't contain either box - error
            SevereElement ja = 
                df.getAnomsPossible().
                getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_RES0BOX);
            df.addAnomaly(ja);
            df.setContinueParsing(false);
        }
        
        
    }
}
