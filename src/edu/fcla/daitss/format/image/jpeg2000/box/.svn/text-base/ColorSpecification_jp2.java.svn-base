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
import edu.fcla.daitss.util.ByteReader;

/**
 * JP2-specific elements of the Color Specification box.
 * 
 * 
 * 
 * @author Andrea Goethals, FCLA
 */
public class ColorSpecification_jp2 extends ColorSpecification {

    /**
     * 
     * @param box
     */
    public ColorSpecification_jp2(Box box) {
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
        return (type == 16 || type == 17);
    }
    
    /**
     * Determine whether the METH field is valid for the file.
     * 
     * @param type meth type
     * @return boolean
     */
    public boolean isValidMethType(int type) {
        return (type == 1 || type == 2);
    }

    /**
     * Map the enumerated color space value to a 
     * type known by the archive.
     * 
     * @param type color space value
     * @return String
     */
    protected String mapEnumCsValue(int type){
        String cs = null;
        
        switch (type) {
            
            case 16: cs = ColorSpace.CS_SRGB;
                break;
            case 17: cs = ColorSpace.CS_GREYSCALE;
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
     * @return String
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
        }
        return meth;
    }
    
    /**
     * Parse the APPROX field.
     * This field is reserved for ISO use in JP2 files.
     * @param reader 
     * @param df 
     * @throws FatalException 
     */
    protected void parseApproxField(ByteReader reader, Jpeg2000 df) 
        throws FatalException{
        reader.skipBytes(FIXED_APPROX_LENGTH);
    }

    /**
     * Parse the PREC field.
     * This field is reserved for ISO use in JP2 files.
     * @param reader 
     * @throws FatalException 
     */
    protected void parsePrecField(ByteReader reader) throws FatalException{
        reader.skipBytes(FIXED_PREC_LENGTH);
    }

}
