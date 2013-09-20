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
 * Created on Feb 12, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

/**
 * PdfFilter
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class PdfFilter {
	
	/**
	 * Decodes data encoded in an ASCII base-85 representation,
	 * reproducing the original binary data.
	 */
	public static final String ASCII_85_DECODE = "ASCII85Decode";
	
	/**
	 * Decodes data encoded in an ASCII hexadecimal representation,
	 * reproducing the original binary data.
	 */
	public static final String ASCII_HEX_DECODE = "ASCIIHexDecode";
	
	/**
	 * Decodes data encoded using the CCITT facsimile standard, 
	 * reproducing the original data (typically monochrome image data
	 * at 1 bit per pixel).
	 */
	public static final String CCITT_FAX_DECODE = "CCITTFaxDecode";
	
	/**
	 * Decodes data encoded using a DCT (discrete cosine transform), 
	 * technique based on the JPEG standard, reproducing the image
	 * sample data that approximates the original data.
	 */
	public static final String DCT_DECODE = "DCTDecode";
	
	/**
	 * Decodes data encoded using the zlib/deflate
	 * compression method, reproducing the original text or binary data.
	 */
	public static final String FLATE_DECODE = "FlateDecode";
	
	/**
	 * Decodes data encoded using the JBIG2 standard, 
	 * reproducing the original monochrome (1 bit per pixel) image data 
	 * (or an approximation of that data).
	 */
	public static final String JBIG2_DECODE = "JBIG2Decode";
	
	/**
	 * Decodes data encoded using the LZW (Lempel-Ziv-Welch) adaptive
	 * compression method, reproducing the original text or binary data.
	 */
	public static final String LZW_DECODE = "LZWDecode";
	
	/**
	 * Decodes data encoded using a byte-oriented run-length encoding
	 * algorithm, reproducing the original text or binary data (typically
	 * monochrome image data, or any data that contains frequent long
	 * runs of a single-byte value).
	 */
	public static final String RUN_LENGTH_DECODE = "RunLengthDecode";
	
	/**
	 * Decodes data encoded using the wavelet-based JPEG 2000 standard, 
	 * reproducing the original image data
	 */
	public static final String JPX_DECODE = "JPXDecode";
	
	/**
	 * Decodes data encrypted by a security handler, reproducing 
	 * the original data as it was before encryption.
	 */
	public static final String CRYPT_DECODE = "Crypt";
		
	/**
	 * Determines whether or not a filter type is considered to be
	 * a valid one (specified in the PDF 1.4 spec.).
	 * 
	 * @param type	the filter type 
	 * @return	<code>true</code> if the type is valid, else
	 * 	<code>false</code>.
	 */
	public static boolean isValidType (String type){
		boolean isValid = false;
		
		if (type != null && 
			(type.equals(ASCII_85_DECODE)) 
				|| type.equals(ASCII_HEX_DECODE)
				|| type.equals(CCITT_FAX_DECODE) 
				|| type.equals(DCT_DECODE)
				|| type.equals(FLATE_DECODE) 
				|| type.equals(JBIG2_DECODE)
				|| type.equals(LZW_DECODE) 
				|| type.equals(RUN_LENGTH_DECODE)
				|| type.equals(JPX_DECODE)
				|| type.equals(CRYPT_DECODE)
			){
				isValid = true;
			}
		
		return isValid;
	}

}
