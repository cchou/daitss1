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
 * Created on Sep 26, 2003
 *
 */
package edu.fcla.daitss.format.image;

import java.lang.reflect.Field;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * Color space.
 * Note that when images specify their color by an ICC profile,
 * they are given the color space value CS_UNKNOWN.
 * 
 * RGB Color spaces to consider adding (source:
 * "Color Management", Jonathan Sachs, 1999-2000)
 * - Chrome 2000 D65 & Chrome 2000 D50
 * - SMPTE-240M [aka Adobe RGB (1998)] & Bruce RGB
 * - SMPTE-C, PAL-SECAM & NTSC (1953)
 * - Wide Gamut RGB
 * - Universal RGB
 * - CIE RGB
 * -------------------------------------------------
 * - ProPhoto RGB 
 * (http://www.kodak.com/global/en/service/software/colorflow/rgbProfileDownload.blind)
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class ColorSpace {
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
		"edu.fcla.daitss.bitstream.image.util.ColorSpace";
    
    /**
	 * Applicable to bilevel and grayscale images. 
	 * 0 is imaged as black, white is imaged as 2^(bitsPerSample-1), 
     * assuming that each of
	 * the samplesPerPixel have the same number of bitsPerPixel.
     * 
     * Called Bi-level for JPEG 2000 JPX format. Note that for
     * JPX this is worded explicitly that 0 is black, 1 is white.
	 */
	public static final String CS_BLACKISZERO = "BLACKISZERO";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * As defined by CIE Color Appearance Model 97s, CIE
     * Publication 131. (Note for JPX: For this colorspace
     * additional enumerated parameters are specified in the 
     * EP field as specified in M.11.7.4.2.)
     * Added for JPX 10/29/05.
     */
    public static final String CS_CIEJAB = "CIEJAB";
	
	/**
	 * CIE L*a*b* color space is colormetric with separate lightness and chroma
	 * channels.
     * 
     * From the JPEG2000 15444-2 JPX spec:
     * A color space defined by the CIE (Commission Internationale de
     * l'Eclairage), having approximately equal visually preceptible
     * differences between equally spaced points throughout the
     * space. The 3 components are L* or lightness, and a* and b* in
     * chrominance. (Note for JPX: For this colorspace additional enumerated
     * parameters are specified in the EP field as specified in
     * M.11.7.4.1.)
	 */
	public static final String CS_CIELAB = "CIELAB";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * The encoded data consists of samples of Cyan, Magenta and
     * Yellow samples, directly suitable for printing on typical 
     * CMY devices. A value of 0 shall indicate 0% ink coverages,
     * whereas a value of 2^bps-1 shall indicate 100% ink coverage
     * for a given component sample.
     * Added for JPX 10/29/05.
     */
    public static final String CS_CMY = "CMY";
	
	/**
	 * An image in which each pixel specifies the amount of cyan, magenta, 
	 * yellow and black process ink that is to be used at that location.
     * 
     * From the JPEG2000 15444-2 JPX spec:
     * A value of 0 shall indicate 0% ink coverages,
     * whereas a value of 2^bps-1 shall indicate 100% ink coverage
     * for a given component sample.
	 */
	public static final String CS_CMYK = "CMYK";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * e-sRGB as defined by PIMA 7667.
     * Added for JPX 10/29/05.
     */
    public static final String CS_ESRGB = "ESRGB";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * e-sYCC - e-sRGB based YCC colorspace as defined by PIMA 
     * 7667 Annex B.
     * Added for JPX 10/29/05.
     */
    public static final String CS_ESYCC = "ESYCC";
	
	/**
	 * Taken from ISO/IEC 15444-1:2000 (JPEG2000 Part 1) -
	 * A greyscale space where luminance is related to code values
	 * using the sRGB non-linearity given in Eqs. (2) through (4) of
	 * IEC 61966-2-1 (sRGB) specification:
	 * Y' = Y8bit / 255
	 * for (Y' <= 0.04045), Ylin = Y' / 12.92
	 * for (Y' > 0.04045), Ylin = ((Y' + 0.055) / 1.055)^2.4
	 * where Ylin is the linear image luminance value in the range 0.0 to 1.0 
	 * The image luminance values should be interpreted relative to the
	 * reference conditions in Section 2 of IEC 61966-2-1.
	 */
	public static final String CS_GREYSCALE = "GRAYSCALE";
	
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * This is the color encoding method used in the Photo CD system.
     * The colorspace is based on ITU-R Rec. BT.709 reference primaries.
     * ITU-R Rec. BT.709 linear RGB image signals are transformed
     * to nonlinear R'G'B' values to YCC corresponding to ITU-R Rec. BT.601-5.
     * Details of this encoding method can be found in Kodak Photo
     * CD products. "A Planning Guide for Developers", Eastman Kodak
     * Company, Part No. DC1200R and also in Kodak Photo CD Information
     * Bulletin PCD045.
     * Added for JPX 10/29/05.
     */
    public static final String CS_PHOTO_YCC = "PHOTO_YCC";
    
	/**
	 * An image composed of red, green and blue color components.
	 */
	public static final String CS_RGB = "RGB";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * ROMM-RGB as defined by PIMA 7666.
     * Added for JPX 10/29/05.
     */
    public static final String CS_ROMMRGB = "ROMMRGB";
	
	/**
	 * Taken from ISO/IEC 15444-1:2000 (JPEG2000 Part 1) -
	 * sRGB as defined by IEC61966-2-1.
	 * (Do not know if sRGB is treated differently from RGB by
	 * image readers).
	 * 
	 * This is the default Windows color space, based on the 
	 * colors that can be displayed on a video monitor. It is
	 * very similar to both PAL-SECAM and SMPTE-C. (source 
	 * "Color Management", Jonathan Sachs, 1999-2000)
	 */
	public static final String CS_SRGB = "SRGB";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * sYCC as defined by IEC 61966-2-1, Amd. 1.
     * See ITU-T Rec. T.800 | ISO/IEC 15444-1, J.15 for
     * guidelines on handling YCC codestreams.
     * Added for JPX 10/29/05.
     */
    public static final String CS_SYCC = "SYCC";
	
	/**
	 * An image that is used as a mask to describe an irregularly-shaped region 
	 * of another image that should be transparent.
	 */
	public static final String CS_TRANSP = "TRANSP";
	
	/** 
	 * Unknown color space.
     * When an image specifies its color by an ICC profile,
     * this value is used.
	 */
	public static final String CS_UNKNOWN = "UNKNOWN";

	/**
	 * Applicable to bilevel and grayscale images. <code>0</code> is imaged 
	 * as white, black is imaged as 2^(bitsPerSample-1), assuming that each of
	 * the <code>samplesPerPixel</code> have the same number of 
	 * <code>bitsPerPixel</code>.
     * 
     * Called Bi-level(2) for JPEG 2000 JPX format. Note that for
     * JPX this is worded explicitly that 0 is white, 1 is black.
	 */
	public static final String CS_WHITEISZERO = "WHITEISZERO";
	
	/**
	 * Uses only the Y component of YCbCr
	 */
	public static final String CS_Y = "Y";

	/**
	 * YCbCr images, also called class Y images, subsample the chrominance
	 * components for greater compression than RGB images. Based on
	 * CCIR Recommendation 601-1 "Encoding Parameters of Digital 
	 * Television for Studios". Y is the luminance component, Cb and Cr are
	 * the two chrominance components. RGB images are converted to and from 
	 * YCrCb color space for storage and display.
	 */
	public static final String CS_YCBCR = "YCBCR";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * This is a format often used for data that originated from a video signal.
     * The colorspace is based on ITU-R Rec. BT.709-4. The valid ranges of
     * the YCbCr components in this space is limited to less than the full
     * range that could be represented given an 8-bit representation.
     * ITU-R Rec. BT.601-5 specifies these ranges as well as defines a 
     * 3 x 3 matrix transformation that can be used to convert these
     * samples into RGB.
     * Added for JPX 10/29/05.
     */
    public static final String CS_YCBCR_1 = "YCBCR_1";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * This is the most commonly used format for image data that 
     * was originally captured in RGB (uncalibrated format).
     * The colorspace is based on ITU-R Rec. BT.601-5. The valid ranges of
     * the YCbCr components in this space is [0,255] for Y, and
     * [-128,127] for Cb and Cr (stored with an offset of 128 to 
     * convert the range to [0,255]. These ranges are different from
     * the ones defined in ITU-R Rec. BT.601-5. ITU-R Rec. BT.601-5
     * specifies a 3 x 3 matrix transformation that can be used to convert these
     * samples into RGB.
     * Added for JPX 10/29/05.
     */
    public static final String CS_YCBCR_2 = "YCBCR_2";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * This is a format often used for data that originated from a video signal.
     * The colorspace is based on ITU-R Rec. BT.601-5. The valid ranges of
     * the YCbCr components in this space is limited to less than the full
     * range that could be represented given an 8-bit representation.
     * ITU-R Rec. BT.601-5 specifies these ranges as well as defines a 
     * 3 x 3 matrix transformation that can be used to convert these
     * samples into RGB.
     * Added for JPX 10/29/05.
     */
    public static final String CS_YCBCR_3 = "YCBCR_3";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * This is the result of transforming original CMYK type data
     * by computing R = (2^bps-1) - C, G = (2^bps-1) - M, and 
     * B = (2^bps-1) - Y, applying the RGB to YCC transformation 
     * specified for [YCBCR_2], and then recombining the result
     * with the unmodified K-sample. This transformation is
     * intended to be the same as that specified in Adobe
     * Postscript.
     * Added for JPX 10/29/05.
     */
    public static final String CS_YCCK = "YCCK";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * YPbPr(1125/60)
     * This is the well-known colorspace and value definition for
     * the HDTV (1125/60/2:1) system for production and
     * international program exchange specified by ITU-R Rec. BT.709-3.
     * The Recommendation specifies the color space conversion matrix
     * from RGB to YPbPr(1125/60) and the range of values of each 
     * component. The matrix is different from the 1250/50 system.
     * In the 8-bit/component case, the range of values of each
     * component is [1,254], the black level of Y is 16, the
     * achromatic value of Pb/Pr is 128, the nominal peak of Y
     * is 235, and the nominal extremes of Pb/Pr are 16 and 240.
     * In the 10-bit case, these values are defined in a similar manner.
     * Added for JPX 10/29/05.
     */
    public static final String CS_YPBPR_1125_60 = "YPBPR_1125_60";
    
    /**
     * From the JPEG2000 15444-2 JPX spec: 
     * YPbPr(1250/50)
     * This is the well-known color space and value definition for
     * the HDTV (1250/50/2:1) system for production and
     * international program exchange specified by ITU-R Rec. BT.709-3.
     * The Recommendation specifies the color space conversion matrix
     * from RGB to YPbPr(1250/50) and the range of values of each 
     * component. The matrix is different from the 1125/60 system.
     * In the 8-bit/component case, the range of values of each
     * component is [1,254], the black level of Y is 16, the
     * achromatic value of Pb/Pr is 128, the nominal peak of Y
     * is 235, and the nominal extremes of Pb/Pr are 16 and 240.
     * In the 10-bit case, these values are defined in a similar manner.
     * Added for JPX 10/29/05.
     */
    public static final String CS_YPBPR_1250_50 = "YPBPR_1250_50";
	
	/**
	 * Maximum valid color space code name length.
	 */
	public static final int MAX_CS_LENGTH = 64;
	
	/**
	 * Checks that a color space code is valid (is the value of a constant in
	 * this class). 
	 * 
	 * @param colorSpace	the code for a color space
	 * @return	<code>true</code> if this is a valid code for a color space,
	 * 				else <code>false</code>.
	 * @throws FatalException
	 */
	public static boolean isValidColorSpace(String colorSpace)
		throws FatalException{

		boolean isType = false, foundIt = false;
		
		// dynamically make a list of all the color space types listed
		// in this class
		Field[] fields = new ColorSpace().getClass().getFields();
		int i= 0;
		while (!foundIt && i<fields.length) {
			try {
				// only want to consider the members that start with "CS_"
				if (fields[i].getName().startsWith("CS_") && 
					((String)fields[i].get(fields[i])).equals(colorSpace)){
					foundIt = true;
					isType = true;
				}
				i++;
			} catch (IllegalArgumentException e) {
				isType = false;
			} catch (IllegalAccessException e) {
				isType = false;
			}
		}
		
		return isType;
	}
	
	
}
