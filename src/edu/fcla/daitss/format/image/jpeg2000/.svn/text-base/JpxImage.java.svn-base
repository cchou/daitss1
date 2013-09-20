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
 * Created on Oct 18, 2004
 *
 */
package edu.fcla.daitss.format.image.jpeg2000;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;

/**
 * JpxImage represents a JPEG 2000 image as specified in 
 * ISO/IEC 15444-2:2004(E) (the same specification that 
 * describes JPX).
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class JpxImage extends Jpeg2000Image {
    
    /**
     * A particular filter used in the wavelet transformation.
     * See 15444-2 last row of table A-10.
     */
    public static final String ARB_TRANSFORM = "ARBITRARY";
    
    /**
     * Array-based multiple component transformation is used.
     * May be combined with wavelet-based multiple component 
     * transformation.
     */       
    public static final String MCT_ARRAY = "ARRAY";
    
    /**
     * Wavelet-based multiple component transformation is used.
     * May be combined with array-based multiple component 
     * transformation.
     */       
    public static final String MCT_WAVELET = "WAVELET";
    
    /**
     * Degree to which the color specification method approximates
     * the color space of the image.
     * See Table M.23 of 15444-2.
     */
    private String colorApprox = UNKNOWN;

    /**
     * @param df
     * @throws FatalException
     */
    public JpxImage(DataFile df) throws FatalException {
        super(df);
        
        // 
        this.multCompTrans = new String[2];
    }

    /**
     * Puts all the JPX image's members and their values in a String.
     * 
     * @return all the members and their values
     */
    public String toString() {
        String prior = super.toString();
        StringBuffer sb = new StringBuffer("");
        sb.append(prior);
        
        // append any JPX-specific things here
        sb.append("\n");
        sb.append("\tColor space approximation: " + getColorApprox() + "\n");
        
        return sb.toString();
    }

    /**
     * @return Returns the colorApprox.
     */
    public String getColorApprox() {
        return colorApprox;
    }

    /**
     * @param colorApprox The colorApprox to set.
     */
    public void setColorApprox(String colorApprox) {
        this.colorApprox = colorApprox;
    }
}
