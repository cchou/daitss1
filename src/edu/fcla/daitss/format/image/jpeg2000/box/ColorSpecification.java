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

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.ImageAnomalies;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.util.ByteReader;

/**
 * The Color Specification box.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public abstract class ColorSpecification extends Box {
    
    /**
     * Fixed size in bytes of the Approx field.
     */
    protected static final int FIXED_APPROX_LENGTH = 1;
    
    /**
     * Fixed size in bytes of the METH field.
     */
    protected static final int FIXED_METH_LENGTH = 1;
    
    /**
     * Fixed size in bytes of the Precedence field.
     */
    protected static final int FIXED_PREC_LENGTH = 1;
    
    /**
     * Fixed size in bytes of the EnumCS field.
     */
    protected static final int FIXED_ENUMCS_LENGTH = 4;
    
    /**
     * Any ICC profile method.
     */
    public static final String METH_ANYICC = "anyICC";
    
    /**
     * Enumerated color method.
     */
    public static final String METH_ENUM = "enumerated";

	/**
     * Restricted ICC profile method.
     */
    public static final String METH_RESTICC = "restrictedICC";
    
    /**
     * Vendor color method.
     */
    public static final String METH_VENDOR = "vendor";
    
    /**
	 * box type value;
	 */
	public static final int TYPE = 0x636F6C72;

    /**
     * 
     * @param box
     */
	public ColorSpecification(Box box) {
		super(box, ColorSpecification.TYPE);
	}
    
    /**
     * Parse the color specification box.
     * 
     * @param reader
     * @param df
     * @throws FatalException
     */
    public void extractMetadata(ByteReader reader, Jpeg2000 df) 
        throws FatalException {
        System.out.println("\tCOLR");
    
        boolean keepParsingBox = true;
        //boolean isICC = false;
        
        
        // specification method
        int meth = (int) reader.readBytes(FIXED_METH_LENGTH, 
                df.getByteOrder());
        subtractFromBytesLeft(FIXED_METH_LENGTH);
        
        if (!isValidMethType(meth)){
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_UNK_CS_METH));
                // stop parsing this box but not the entire file
                keepParsingBox = false;
        } else {
            // valid color specification method
            String colorMeth = mapMethValue(meth);
            if (colorMeth != null){
                df.getCodeStream().setColorSpecMethod(colorMeth);
            }
            
            if (df.getCodeStream().getColorSpecMethod().
                    equals(METH_RESTICC) ||
                    df.getCodeStream().getColorSpecMethod().
                    equals(METH_ANYICC)){
                // has ICC profile
                df.getCodeStream().setHasIccProfile(Bitstream.TRUE);
                // TODO: could add a not-applicable color space
                df.getCodeStream().setColorSpace(ColorSpace.CS_UNKNOWN);
            } else {
                // no ICC profile
                df.getCodeStream().setHasIccProfile(Bitstream.FALSE);
            }
        }
        
        // precedence
        if (keepParsingBox){
            // parse it differently for JP2/JPX
            parsePrecField(reader);
            subtractFromBytesLeft(FIXED_PREC_LENGTH);
        }
        
        // colorspace approximation
        if (keepParsingBox){
            // reserved ISO field - skip it
            parseApproxField(reader, df);
            subtractFromBytesLeft(FIXED_APPROX_LENGTH);
        }
        
        // se the color space if the color space
        // specification method is enumerated colorspace
        if (keepParsingBox && 
                df.getCodeStream().
                getColorSpecMethod().equals(METH_ENUM)){
            int csCode = (int) reader.readBytes(FIXED_ENUMCS_LENGTH, 
                    df.getByteOrder());
            subtractFromBytesLeft(FIXED_ENUMCS_LENGTH);
            
            if (!isValidEnumCS(csCode)){
                //System.out.println("Unknown enum color: " + csCode);
                // unknown color space
                df.addAnomaly(df.getAnomsPossible().
                        getSevereElement(
                                ImageAnomalies.IMG_UNKNOWN_COLORSPACE));
            } else {
                String cs = mapEnumCsValue(csCode);
                if (cs != null) {
                    df.getCodeStream().setColorSpace(cs);
                }
            }
            
        } 
        
        reader.skipBytes((int)contentLength);
    }
    
    /**
     * Determine whether the METH field is valid for 
     * the file.
     * 
     * @param type meth type
     * @return boolean
     */
    protected abstract boolean isValidMethType(int type);
    
    /**
     * Determine whether the EnumCS field is valid for 
     * the file.
     * 
     * @param type enumerated color space
     * @return boolean
     */
    protected abstract boolean isValidEnumCS(int type);
    
    /**
     * Map the color specification method value to a 
     * type known by the archive.
     * 
     * @param type color specification method value
     * @return String object
     */
    protected abstract String mapMethValue(int type);
    
    /**
     * Map the enumerated color space value to a 
     * type known by the archive.
     * 
     * @param type color space value
     * @return String object
     */
    protected abstract String mapEnumCsValue(int type);
    
    /**
     * Parse the APPROX field.
     * 
     * @param reader
     * @param df 
     * @throws FatalException 
     */
    protected abstract void parseApproxField(ByteReader reader, Jpeg2000 df) 
        throws FatalException;   

    /**
     * Parse the PREC field.
     * 
     * @param reader
     * @throws FatalException 
     */
    protected abstract void parsePrecField(ByteReader reader) 
        throws FatalException;   
}
