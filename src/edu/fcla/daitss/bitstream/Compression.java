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
package edu.fcla.daitss.bitstream;

import java.lang.reflect.Field;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;


/**
 * Compression algorithms that are used on data within DataFile objects.
 * 
 * Note that the values of theses strings must not be changed for
 * database consistency.
 */
public class Compression {
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
		"edu.fcla.daitss.bitstream.util.Compression";
	
	/**
	 * 1-dimensional modified Huffman run length encoding.
	 * (Section 10: Modified Huffman Compression of Tiff 6.0 spec).
	 * A method for compressing bilevel data based on the CCITT Group 3  ID
	 * facsimile compression scheme.
	 * 
	 * Also known as ITU-T Rec. T.4. (MH) Modified Huffman
	 * See JPEG2000 JPX (ISO/IEC 15444-2).
	 * 
	 * Used for bi-level images.
	 */
	public static final String COMP_CCITT_ID = "CCITT_ID";
	
	/**
	 * Deflate compression uses the zlib compressed data format. 
	 * This compression can be used in Advanced Tiffs (which can be written
	 * by Adobe Photoshop and Adobe Pagemaker). See "Adobe Photoshop
	 * TIFF Technical Notes", March 22, 2002.
	 */
	public static final String COMP_DEFLATE = "DEFLATE";
	
	/**
	 * PKZIP-style Deflate encoding (experimental) 
	 * (SubAccount: JHOVE, JIU documentation).
	 */
	public static final String COMP_DEFLATE_UNOFFICIAL = "DEFLATE_UNOFFICIAL";
	
	/**
	 * T4-encoding: CCITT T.4 bi-level encoding.
	 * Specified in section 4, Coding, of
	 * CCITT Recommendation T.4: "Standardization of Group 3 Facsimile
	 * apparatus for document transmission." International Telephone and
	 * Telegraph Consultative Committee (CCITT, Geneva: 1988).
	 */
	public static final String COMP_GROUP_3_FAX = "GROUP_3_FAX";
	
	/**
	 * T6-encoding: CCITT T.6 bi-level encoding.
	 * Specified in section 2, Coding, of
	 * CCITT Recommendation T.6: "Facsimile coding schemes and coding
	 * control functions for Group 4 facsimile apparatus." International Telephone and
	 * Telegraph Consultative Committee (CCITT, Geneva: 1988).
	 */
	public static final String COMP_GROUP_4_FAX = "GROUP_4_FAX";
	
	/**
 	* RLE for binary line art (SubAccount: JHOVE, JIU documentation).
	*/
	public static final String COMP_IT8_BINARY_LINE = "IT8_BINARY_LINE";
	
	/**
	 * RasterPaddding in CT (continuous tone) or 
	 * MP (SubAccount: JHOVE, JIU documentation).
	 */
	public static final String COMP_IT8_CT_PADDING = "IT8_CT_PADDING";
	
	/**
	* RLE for LW (linework) (SubAccount: JHOVE, JIU documentation).
	*/
	public static final String COMP_IT8_LINEWORK = "IT8_LINEWORK";
	
	/**
	* RLE for Monochrome images (SubAccount: JHOVE, JIU documentation).
	*/
	public static final String COMP_IT8_MONOCHROME = "IT8_MONOCHROME";
	
	/**
	 * JBIG (SubAccount: JIU documentation).
	 * 
	 * Also known as ITU-T Rec. T.82|ISO/IEC 11544.
	 * See JPEG2000 JPX 15444-2.
	 */
	public static final String COMP_JBIG = "JBIG";
	
	/**
	 * JBIG2 (SubAccount: JIU documentation).
	 */
	public static final String COMP_JBIG2 = "JBIG2";
	
	/**
	 * JPEG (Joint Photographic Experts Group) compression. 
	 * See Section 22: JPEG Compression in the Tiff 6.0 spec. 
	 * Applicable to Tiff 6.0 and later.
	 * 
	 * Also known as CCITT Rec. T.81|ISO/IEC 10918-1 or
	 * ITU-T Rec. T.84|ISO/IEC 10918-3.
	 * See JPEG2000 JPX 15444-2.
	 */
	public static final String COMP_JPEG = "JPEG";
	
	/**
	 * JPEG-LS.
	 * See JPEG2000 JPX 15444-2.
	 */
	public static final String COMP_JPEG_LS = "JPEG_LS";
	
	/**
	 * JPEG compression as described in Adobe Photoshop
	 * TIFF Technical Notes", March 22, 2002.
	 * Not sure if it is any different than <code>JPEG</code>.
	 */
	public static final String COMP_JPEG_NEW = "JPEG_NEW";
	
	/**
	 * JPEG 2000 compression as defined by ISO/IEC 15444-1.
	 * Encoded as a 1-byte unsigned integer.
	 */
	public static final String COMP_JPEG2000 = "JPEG2000";
	
	/**
	 * Kodak DCS (SubAccount: JIU documentation).
	 */
	public static final String COMP_KODAK_DCS = "KODAK_DCS";
	
	/**
	 * LZW (Lempel-Ziv & Welsh) compression.
	 * An adaptive compression sceme 
	 * for raster images. See Section 13: LZW Compression in the Tiff 6.0 spec.
	 * Applicable to Tiff 5.0 and later.
	 */
	public static final String COMP_LZW = "LZW";
	
	/**
	 * CCITT Rec. T.6 (MMR) Modified Modified Read.
	 * Used for bi-level images.
	 */
	public static final String COMP_MOD_MOD_READ = "MODMODREAD";
	
	/**
	 * ITU-T Rec. T.4 Modified Read.
	 * 2-dimensional coding.
	 * Used for bi-level images.
	 */
	public static final String COMP_MOD_READ = "MODREAD";
	
	/**
	 * NeXT 2-bit encoding (SubAccount: JHOVE, JIU documentation).
	 */
	public static final String COMP_NEXT = "NEXT";
	
	/**
	 * No compression.
	 * For tiffs this means pack data into bytes as tightly as possible leaving
	 * no unused bits except at the end of the row. This is 'no compression'
	 * for Tiff images. For details see the Compression section on page 30 of
	 * the Tiff 6.0 spec. This is the default for Tiff images.
	 * 
	 * For JPX this means:
	 * Picture data is stored in component interleaved format, encoded at the
	 * bit depth. All components are encoded at the same bit depth. When the 
	 * bit depth of each component is not 8, sample values shall be packed
	 * into bytes so that no bytes are unused between samples. Each sample 
	 * shall begin on a byte boundary and padding bits having value zero
	 * shall be inserted after the last sample of a scan line as necessary
	 * to fill out the last byte of the scan line. When multiple sample
	 * values are packed into a byte, the first sample shall appear in the 
	 * most significant bits of the byte. When a sample is larger than a 
	 * byte, its most significant bit shall appear in earlier bytes. 
	 * (Source Table M.19 ISO/IEC 15444-2)
	 */
	public static final String COMP_NONE = "NONE";
	
	/**
	 * No compression, word-aligned (SubAccount: JIU documentation).
	 */
	public static final String COMP_NONE_WORD_ALIGNED = "NONE_WORD_ALIGNED";
	
	/**
	 * Not applicable.
	 */
	public static final String COMP_NOT_APPLICABLE = "NOT_APPLICABLE";

	/**
	 * Packbits compression.
	 * A simple byte-oriented run length scheme.
	 * See Section 9: PackBits Compression of the Tiff 6.0 spec for details.
	 */
	public static final String COMP_PACKBITS = "PACKBITS";
	
	/**
	 * Pegasus IMJ (SubAccount: JIU documentation).
	 */
	public static final String COMP_PEGASUS_IMJ = "PEGASUS_IMJ";
	
	/**
	 * Pixar compounded 10-bit ZIP encoding (SubAccount: JIU documentation).
	 */
	public static final String COMP_PIXAR10 = "PIXAR10";
	
	/**
	 * Pixar compounded 11-bit ZIP encoding.
	 */
	public static final String COMP_PIXAR11 = "PIXAR11";
	
	/**
	 * SGI 24-bit Log Luminance encoding (experimental) 
	 * (SubAccount: JHOVE, JIU documentation).
	 */
	public static final String COMP_SGI_LOG_24_PACKED = "SGI_LOG_24_PACKED";
	
	/**
	 * SGI 32-bit Log Luminance encoding (experimental) 
	 * (SubAccount: JHOVE, JIU documentation).
	 */
	public static final String COMP_SGI_LOG_RLE = "SGI_LOG_RLE";
	
	/**
	 * ThunderScan 4-bit encoding (SubAccount: JHOVE, JIU documentation).
	 */
	public static final String COMP_THUNDERSCAN = "THUNDERSCAN";
	

	/**
	 * Not known if there is any compression.
	 */
	public static final String COMP_UNKNOWN = "UNKNOWN";
	
	/**
	 * Maximum valid compression code name length
	 */
	public static final int MAX_COMP_LENGTH = 64;
	
	// the compression sheme used.
	private String 	compressonScheme;
	
	/** 
	 * constructor
	 */
	public Compression() {
		compressonScheme = COMP_UNKNOWN;
	}
	
	public Compression(String _compression) throws FatalException{
		setCompression(_compression);
	}
	
	/**
	 * Checks if a compression code is a valid one. 
	 * 
	 * @param compression	the code for a compression scheme
	 * @return	<code>true</code> if the compression code is valid, else
	 * 			<code>false</code>
	 * @throws FatalException
	 */
	public boolean isValidCompression(String compression)
		throws FatalException {
		
		String methodName = "isValidCompression(String)";
		boolean isValid = false, foundIt = false;
		
		// check that the compression value is compatible with
		// the database data constraints
		if (compression == null || compression.length() > MAX_COMP_LENGTH) {
			isValid = false;
			foundIt = true;
		}
		
		// dynamically make a list of all the compression types listed
		// in this class
		Field[] fields = new Compression().getClass().getFields();
		int i= 0;
		while (!foundIt && i<fields.length) {
			try {
				// only want to consider the members that start with "COMP_"
				if (fields[i].getName().startsWith("COMP_") && 
					((String)fields[i].get(fields[i])).equals(compression.toUpperCase())){
					foundIt = true;
					isValid = true;
				}
				i++;
			} catch (IllegalArgumentException e) {
				Informer.getInstance().fail(CLASSNAME, methodName,
						"Illegal argument", "compression: " + compression,
						new FatalException("Not a valid compression type"));
			} catch (IllegalAccessException e) {
				Informer.getInstance().fail(CLASSNAME, methodName,
						"Illegal access", "field",
						new FatalException(e.getMessage()));
			}
		}
		
		return isValid;
	}
	/**
	 * Sets the compression scheme
	 * The compression algorithm must be defined this class or its super class.
	 * 
	 * @param comp	a compression algorithm
	 * @see	edu.fcla.daitss.bitstream.Compression
	 * @throws FatalException
	 */
	public void setCompression(String comp) throws FatalException {
		if (isValidCompression(comp)){
			this.compressonScheme = comp;
		} else {
			throw new FatalException("Not a valid compression");
		}
	}
	
	/**
	 * return a string representing the compression algorithm
	 */
	public String toString() {
		return compressonScheme.toString();
	}
}
