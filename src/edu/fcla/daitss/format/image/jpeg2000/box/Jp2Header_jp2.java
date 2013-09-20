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
import edu.fcla.daitss.format.image.jpeg2000.Jp2;
import edu.fcla.daitss.format.image.jpeg2000.Jp2Anomalies;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;

/**
 * The JP2 Header box found in a JP2 file.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class Jp2Header_jp2 extends Jp2Header {
	
    /**
     * 
     * @param box
     */
	public Jp2Header_jp2(Box box) {
		super(box);
	}

	/**		
	 * Parse a Jp2 Header Box. 
	 * Sets members based on the internal metadata found in
	 * this box (image height, image width, number of components,
	 * bits per component, compression, whether the color space
	 * is known, whether the file contains intellectual property
	 * rights information).
	 * 
	 * @param reader
	 * @param df
	 * @throws FatalException
	 */
	public void extractMetadata(ByteReader reader, Jp2 df) 
		throws FatalException {
		
		System.out.println("JP2H");
		
		boolean justSawColrBox = false;
		
		//System.out.println("contentLength of JP2 header: " + contentLength);
		Box nextBox = Jpeg2000.readBoxHeader(reader);
		if (nextBox.getType() != BoxType.TYPE_IHDR){
			// does not meet the requirement of having the Image Header
			// box first in the JP2 Header box's DBox
			SevereElement ja = 
				df.getAnomsPossible().
				getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_LOC_BOX);
			df.addAnomaly(ja);
			df.setContinueParsing(false);
		} else {
            // parse Image Header box
			ImageHeader ihdr = new ImageHeader_jp2(nextBox);
			ihdr.extractMetadata(reader, df, this);
			
			// reduce the number of bytes left in this box to
			// read
			subtractFromBytesLeft(ImageHeader.FIXED_BOX_LENGTH);
		}
		
		// parse the rest of the boxes in the JP2 Header box
		while (df.continueParsing && contentLength > 0){
		    nextBox = Jpeg2000.readBoxHeader(reader);
            
            // whether or not to parse the contents of the box
            // (only interested in certain boxes)
		    boolean doSkipBoxContent = true;
            
		    switch (nextBox.getType()){
		    	case BoxType.TYPE_BPCC:
                    setHasBpcBox(true);
                    
		    	    // parse the BitsPerComponent box
                    BitsPerComponent bpcb = 
                        new BitsPerComponent(nextBox, 
                                df.getCodeStream().getNumComponents());
                    bpcb.extractMetadata(reader, df);
                    
                    doSkipBoxContent = false;
		    		justSawColrBox = false;
		    		break;
		    	case BoxType.TYPE_COLR:
		    	    
		    		if (numColrBoxes > 0 && !justSawColrBox) {
		    		    // noncontiguous color specification boxes
		    		    df.addAnomaly(df.getAnomsPossible().
		    				getSevereElement(Jp2Anomalies.JP2_NONCONTIG_COLRBOX));
		    		    // doesn't seem serious enough to stop parsing it
		    		}
		    		numColrBoxes++;
		    		justSawColrBox = true;
		    		// only interested in the first color specification box
		    		if (numColrBoxes == 1) {
                        ColorSpecification csb = 
                            new ColorSpecification_jp2(nextBox);
                        csb.extractMetadata(reader, df);
                        
		    			doSkipBoxContent = false;
		    		}
		    		break;
		    	case BoxType.TYPE_PCLR:
                    // needs a Component Mapping box
                    setNeedsCmapBox(true);
                    
		    	    Palette pb = new Palette(nextBox);
                    pb.extractMetadata(reader, df);
                    doSkipBoxContent = false;
                    
	    			justSawColrBox = false; 
	    			break;
		    	case BoxType.TYPE_CMAP:
                    setHasCmapBox(true);
                    
		    	    ComponentMapping cmb = 
                        new ComponentMapping(nextBox);
                    cmb.extractMetadata(reader, df);
                    doSkipBoxContent = false;
                    
	    			justSawColrBox = false;
	    			break;
		    	case BoxType.TYPE_CDEF:
		    	    ChannelDefinition cdb = new ChannelDefinition(nextBox);
                    cdb.extractMetadata(reader, df);
                    doSkipBoxContent = false;
                    
	    			justSawColrBox = false;
	    			break;
		    	case BoxType.TYPE_RES0:
		    	    Resolution rb = new Resolution(nextBox);
                    rb.extractMetadata(reader, df);
                    doSkipBoxContent = false;
                    
		    		justSawColrBox = false;
		    		break;
		    	default:
		    		// unknown box type
					// this is OK, but note it
					df.addAnomaly(df.getAnomsPossible().
							getSevereElement(Jpeg2000Anomalies.JPEG2K_UNK_BOXTYPE));
		    		justSawColrBox = false;
		    }
		    if (doSkipBoxContent){
		    	reader.skipBytes((int)nextBox.getContentLength());
		    }
            
            // reduce the number of bytes left in the
            // JP2 Header box by the size of the box within
            // it that we just read
            subtractFromBytesLeft(nextBox.getBoxLength());
		}
		
		// check that there is at least one color specification box
		if (numColrBoxes == 0){
		    // the JP2 format must have at least one color specification
		    // box in the JP2 Header box
		    df.addAnomaly(df.getAnomsPossible().
					getSevereElement(Jpeg2000Anomalies.JPEG2K_NO_COLRBOX));
	        df.continueParsing = false;
		}
		
		// check that there was a Bpcc box if it needs one
		if (needsBpcBox() && !hasBpcBox()){
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_NO_BPCBOX));
            df.continueParsing = false;
        }
        
        // check that there was a component mapping box if it needs one
        if (needsCmapBox() && !hasCmapBox()){
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_NO_CMAPBOX));
            df.continueParsing = false;
        }
		
        // should have parsed the whole box
        if (contentLength != 0){
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_JP2HBOX));
            df.continueParsing = false;
        }
	}
}
