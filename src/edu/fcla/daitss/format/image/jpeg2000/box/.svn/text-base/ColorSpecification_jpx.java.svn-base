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
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.format.image.jpeg2000.Jpx;
import edu.fcla.daitss.format.image.jpeg2000.JpxAnomalies;
import edu.fcla.daitss.format.image.jpeg2000.JpxImage;
import edu.fcla.daitss.util.ByteReader;

/**
 * JPX-specific elements of the Color Specification
 * box.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class ColorSpecification_jpx extends 
	ColorSpecification {
    
    /**
     * accurate approximation.
     */
    public static final String APPROX_ACCURATE = "accurate";
    
    /**
     * exceptional approximation.
     */
    public static final String APPROX_EXC = "veryClose";
    
    /**
     * poor approximation.
     */
    public static final String APPROX_POOR = "poor";
    
    /**
     * reasonable approximation.
     */
    public static final String APPROX_REAS = "somewhatClose";
    
    
    
    /**
     * 
     * @param box
     */
	public ColorSpecification_jpx(Box box) {
		super(box);
	}
    
    /**
     * Determine whether the EnumCS field is valid for 
     * the file.
     * 
     * @param type enumerated color space
     * @return boolean
     */
    protected boolean isValidEnumCS(int type){
        return (type == 0 || type == 1 ||
                type == 3 || type == 4 ||
                type == 9 || type == 11 ||
                type == 12 || type == 13 ||
                type == 14 || type == 15 ||
                type == 16 || type == 17 || 
                type == 18 || type == 19 ||
                type == 20 || type == 21 ||
                type == 22 || type == 23 ||
                type == 24 
                );
    }
    
    /**
     * Determine whether the METH field is valid for 
     * the file.
     * 
     * @param type meth type
     * @return boolean
     */
    public boolean isValidMethType(int type){
        return (type == 1 || type == 2 ||
                type == 3 || type == 4);
    }
    
    /**
     * Map the enumerated color space value to a 
     * type known by the archive.
     * 
     * @param type color space value
     * @return String object
     */
    protected String mapEnumCsValue(int type){
        String cs = null;
        
        switch (type) {
            case 0: cs = ColorSpace.CS_BLACKISZERO;
                break;
            case 1: cs = ColorSpace.CS_YCBCR_1;
                break;
            case 3: cs = ColorSpace.CS_YCBCR_2;
                break;
            case 4: cs = ColorSpace.CS_YCBCR_3;
                break;
            case 9: cs = ColorSpace.CS_PHOTO_YCC;
                break;
            case 11: cs = ColorSpace.CS_CMY;
                break;
            case 12: cs = ColorSpace.CS_CMYK;
                break;
            case 13: cs = ColorSpace.CS_YCCK;
                break;
            case 14: cs = ColorSpace.CS_CIELAB;
                break;
            case 15: cs = ColorSpace.CS_WHITEISZERO;
                break;
            case 16: cs = ColorSpace.CS_SRGB;
                break;
            case 17: cs = ColorSpace.CS_GREYSCALE;
                break;
            case 18: cs = ColorSpace.CS_SYCC;
                break;
            case 19: cs = ColorSpace.CS_CIEJAB;
                break;
            case 20: cs = ColorSpace.CS_ESRGB;
                break;
            case 21: cs = ColorSpace.CS_ROMMRGB;
                break;
            case 22: cs = ColorSpace.CS_YPBPR_1125_60;
                break;
            case 23: cs = ColorSpace.CS_YPBPR_1250_50;
                break;
            case 24: cs = ColorSpace.CS_ESYCC;
                break;
            // no default - send back null
        }
        return cs;
    }
    
    /**
     * Map the color specification method value to a 
     * type known by the archive.
     * 
     * @param type color specification method value
     * @return String object
     */
    public String mapMethValue(int type) {
        String meth = null;

        switch (type) {
            case 1:
                meth = METH_ENUM;
                break;
            case 2:
                meth = METH_RESTICC;
                break;
            case 3:
                meth = METH_ANYICC;
                break;
            case 4:
                meth = METH_VENDOR;
                break;
        }
        return meth;
    }

    /**
     * Parse the APPROX field.
     * See Table M.23 in 15444-2.
     * @param reader 
     * @param df 
     * @throws FatalException 
     */
    protected void parseApproxField(ByteReader reader, Jpeg2000 df) 
        throws FatalException{
        int apx = 
            (int) reader.readBytes(FIXED_APPROX_LENGTH, df.getByteOrder());
        JpxImage ji = (JpxImage) df.getCodeStream();
        switch (apx) {
            case 1:
                // accurate approximation
                ji.setColorApprox(APPROX_ACCURATE);
                break;
            case 2:
                // exceptional approximation
                ji.setColorApprox(APPROX_EXC);
                break;
            case 3:
                // reasonable approximation
                ji.setColorApprox(APPROX_REAS);
                break;
            case 4:
                // poor approximation
                ji.setColorApprox(APPROX_POOR);
                break;
            default:
                // invalid value
                df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(JpxAnomalies.JPX_UNK_APPROX));
                // keep parsing file
                break;
        }
    }
    
    /**
     * Parse the PREC field.
     * JPX files can have multiple Color Specification boxes.
     * This field could be used to note which color specification
     * field takes precedence.
     * @param reader 
     * @throws FatalException 
     */
    protected void parsePrecField(ByteReader reader) throws FatalException{
        reader.skipBytes(FIXED_PREC_LENGTH);
    }

}
