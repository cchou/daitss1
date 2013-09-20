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
 * Created on Apr 12, 2004
 *
 */
package edu.fcla.daitss.format.text;

import edu.fcla.daitss.exception.FatalException;

/**
 * CharacterSet represents the chacter sets and encodings that a text file
 * can be encoded in. This class includes a Java port of ascmagic.c which is
 * a part of the GNU file utility.
 * 
 * The official names for character sets that can be used in the Internet are
 * documented at http://www.iana.org/assignments/character-sets
 * 
 * DO NOT FORMAT THIS SOURCE CODE IN ECLIPSE!
 * It makes the character tables unreadable.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class CharacterSet {
	
	/**
	 * The character set detected by the <code>detectCharSet</code>
	 * method. 
	 */
	//public static String detectedCharset = "";
	
	/**
	 * A mapping of EBCDIC charcaters to extended ASCII characters.
	 * This is the mapping used by the GNU file utility which in turn was gotten
	 * from the mapping used by the GNU  dd utility.
	 */
	private static char[] ebcdicToAscii = {
		// 00  	01  	02  	03  	04 	05  	06  	07  	08  	09  	0a  	0b  	0c  	0d  	0e  	0f
			0,		1, 	2, 	3,   156, 	 9,   134, 127, 	151, 141, 142, 11, 	12, 	13, 	14, 	15,
		// 10		11	12	13	14	15	16	17	18	19	1a	1b	1c	1d	1e	1f
			16,	17,	18,	19,	157, 133, 8,		135, 24,	25,	146,	143,	28,	29,	30,	31,
		// 20		21	22	23	24	25	26	27	28	29	2a	2b	2c	2d	2e	2f	
			128, 129, 130, 131, 132, 10,	23,	27,	136, 137, 138, 139, 140, 5,		6,		7,
		//	30	31	32	33	34	35	36	37	38	39	3a	3b	3c	3d	3e	3f
			144, 145, 22,	147, 148, 149, 150, 4,		152, 153, 154, 155, 20,	21,	158, 26,
		//	40	41	42	43	44	45	46	47	48	49	4a	4b	4c	4d	4e	4f
			' ', 	160, 161, 162, 163, 164, 165, 166, 167, 168, 213,  '.', 	'<', 	'(', 	'+', 	'|',
		//	50	51	52	53	54	55	56	57	58	59	5a	5b	5c	5d	5e	5f
			'&', 	169, 170, 171, 172, 173, 174, 175, 176, 177,  '!', 	'$', 	'*', 	')', 	';', 	'~',
		//	60	61	62	63	64	65	66	67	68	69	6a	6b	6c	6d	6e	6f
			'-', 	'/', 	178, 179, 180, 181, 182, 183, 184, 185, 203,  ',', 	'%', 	'_', 	'>', 	'?',
		//	70	71	72	73	74	75	76	77	78	79	7a	7b	7c	7d	7e	7f
			186, 187, 188, 189, 190, 191, 192, 193, 194,  '`', 	':', 	'#', 	'@',  '\'',	'=', 	'"',
		//	80	81	82	83	84	85	86	87	88	89	8a	8b	8c	8d	8e	8f
			195, 'a', 	'b', 	'c', 	'd', 	'e', 	'f', 	'g', 	'h', 	'i', 	196, 197, 198, 199, 200, 201,
		//	90	91	92	93	94	95	96	97	98	99	9a	9b	9c	9d	9e	9f
			202,  'j', 	'k', 	'l', 	'm', 	'n', 	'o', 	'p', 	'q', 	'r', 	'^', 	204, 205, 206, 207, 208,
		//	a0	a1	a2	a3	a4	a5	a6	a7	a8	a9	aa	ab	ac	ad	ae	af
			209, 229, 's', 	't', 	'u', 	'v', 	'w', 	'x', 	'y', 	'z', 	210, 211, 212, '[', 	214, 215,
		//	b0	b1	b2	b3	b4	b5	b6	b7	b8	b9	ba	bb	bc	bd	be	bf
			216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, ']', 	230, 231,
		//	c0	c1	c2	c3	c4	c5	c6	c7	c8	c9	ca	cb	cc	cd	ce	cf
			'{', 	'A', 	'B', 	'C', 	'D', 	'E', 	'F', 	'G', 	'H', 	'I', 	232, 233, 234, 235, 236, 237,
		//	d0	d1	d2	d3	d4	d5	d6	d7	d8	d9	da	db	dc	dd	de	df
			'}', 	'J', 	'K', 	'L', 	'M', 	'N', 	'O', 	'P', 	'Q', 	'R', 	238, 239, 240, 241, 242, 243,
		//	e0	e1	e2	e3	e4	e5	e6	e7	e8	e9	ea	eb	ec	ed	ee	ef
			'\\',	159, 'S', 	'T', 	'U', 	'V', 	'W', 	'X', 	'Y', 	'Z', 	244, 245, 246, 247, 248, 249,
		//	f0		f1		f2		f3		f4		f5		f6		f7		f8		f9		fa		fb		fc		fd		fe		ff		
			'0', 	'1', 	'2', 	'3', 	'4', 	'5', 	'6', 	'7', 	'8', 	'9', 	250, 251, 252, 253, 254, 255
	};
	
	/**
	 * Whether or not the <code>detectCharSet</code> method has
	 * determined the character set yet.
	 */
	//public static boolean found = false;
	
	/**
	 * Characters with code points [0..255] coded according to:
	 * 'F': not a printable character
	 * 'T': an ASCII printable character
	 * 'I': an ISO-8859-x printable character
	 * 'X': a non-ISO extended ASCII printable character (Mac, IBM PC)
	 */
	private static final char[] textChars = {
		// 00  01  02  03  04  05  06  07  08  09  0a  0b  0c  0d  0e  0f
		   'F','F','F','F','F','F','F','T','T','T','T','T','T','T','F','F',
		// 10  11  12  13  14  15  16  17  18  19  1a  1b  1c  1d  1e  1f
		   'F','F','F','F','F','F','F','F','F','F','F','T','F','F','F','F',
		// 20  21  22  23  24  25	26	27	28	29	2a	2b	2c	2d	2e	2f
		   'T','T','T','T','T','T',	'T','T','T','T','T','T','T','T','T','T',	
		// 30	31	32	33	34	35	36	37	38	39	3a	3b	3c	3d	3e	3f
		   'T',	'T','T','T','T','T','T','T','T','T','T','T','T','T','T','T',
		// 40	41	42	43	44	45	46	47	48	49	4a	4b	4c	4d	4e	4f
		   'T',	'T','T','T','T','T','T','T','T','T','T','T','T','T','T','T',
		// 50	51	52	53	54	55	56	57	58	59	5a	5b	5c	5d	5e	5f
		   'T',	'T','T','T','T','T','T','T','T','T','T','T','T','T','T','T',
		// 60	61	62	63	64	65	66	67	68	69	6a	6b	6c	6d	6e	6f
		   'T',	'T','T','T','T','T','T','T','T','T','T','T','T','T','T','T',
		// 70	71	72	73	74	75	76	77	78	79	7a	7b	7c	7d	7e	7f
		   'T',	'T','T','T','T','T','T','T','T','T','T','T','T','T','T','F',
		// 80	81	82	83	84	85	86	87	88	89	8a	8b	8c	8d	8e	8f
		   'X',	'X','X','X','X','T','X','X','X','X','X','X','X','X','X','X',	
		// 90	91	92	93	94	95	96	97	98	99	9a	9b	9c	9d	9e	9f
		   'X',	'X','X','X','X','X','X','X','X','X','X','X','X','X','X','X',
		// a0	a1	a2	a3	a4	a5	a6	a7	a8	a9	aa	ab	ac	ad	ae	af
		   'I',	'I','I','I','I','I','I','I','I','I','I','I','I','I','I','I',
		// b0	b1	b2	b3	b4	b5	b6	b7	b8	b9	ba	bb	bc	bd	be	bf
		   'I',	'I','I','I','I','I','I','I','I','I','I','I','I','I','I','I',
		// c0	c1	c2	c3	c4	c5	c6	c7	c8	c9	ca	cb	cc	cd	ce	cf
		   'I',	'I','I','I','I','I','I','I','I','I','I','I','I','I','I','I',
		// d0	d1	d2	d3	d4	d5	d6	d7	d8	d9	da	db	dc	dd	de	df
		   'I',	'I','I','I','I','I','I','I','I','I','I','I','I','I','I','I',
		// e0	e1	e2	e3	e4	e5	e6	e7	e8	e9	ea	eb	ec	ed	ee	ef
		   'I',	'I','I','I','I','I','I','I','I','I','I','I','I','I','I','I',
		// f0	f1	f2	f3	f4	f5	f6	f7	f8	f9	fa	fb	fc	fd	fe	ff
		   'I',	'I','I','I','I','I','I','I','I','I','I','I','I','I','I','I'
	};
	
	/**
	 * Converts EBCDIC characters to US-ASCII characters.
	 * Uses an EBCDIC-to-ASCII mapping used by the GNU tools
	 * file and dd. See <code>ebcdicToAscii</code> for the
	 * mapping.
	 * 
	 * @param ebcdicBytes	the EBCDIC characters
	 * @param tFile	the file containing the EBCDIC characters
	 */
	public static void ebcdicToAscii(short[] ebcdicBytes, TextFile tFile){
		for (int i=0; i<ebcdicBytes.length; i++) {
			tFile.fromEbcdic[i] = (short) ebcdicToAscii[ebcdicBytes[i]];
			//System.out.print(" " + ((char) (short)ebcdicToAscii[ebcdicBytes[i]]));
			//System.out.print(" " + ebcdicBytes[i] + "->" + ((char) (short)ebcdicToAscii[ebcdicBytes[i]]));
			if ((i % 10) == 0){
				//System.out.println();
			}
		}
	}

	/**
	 * Determines if the characters are all 'printable' ascii characters.
	 * Stops as soon as it finds one that is not.
	 * 
	 * The official names for ASCII are:
	 * Name: ANSI_X3.4-1968                                   [RFC1345,KXS2]
	 * MIBenum: 3
	 * SubAccount: ECMA registry
	 * Alias: iso-ir-6
	 * Alias: ANSI_X3.4-1986
	 * Alias: ISO_646.irv:1991
	 * Alias: ASCII
	 * Alias: ISO646-US
	 * Alias: US-ASCII (preferred MIME name)
	 * Alias: us
	 * Alias: IBM367
	 * Alias: cp367
	 * Alias: csASCII
	 * 
	 * @param theBytes	the possibly ascii characters to examine
	 * @param tFile	the text file containing the characters
	 * @return whether all the characters are printable ascii
	 */
	public static boolean looksPrintableAscii(short[] theBytes, TextFile tFile){
		for (int i=0; i<theBytes.length; i++) {
			char c = textChars[theBytes[i]];
			
			if (c != 'T'){
				return false;
			}
			
			tFile.unicodeChars[i] = (char)theBytes[i];
		}
		
		return true;
	}
	
	/**
	 * Determines if all the characters in the file are non-ISO
	 * extended ASCII characters. This is not a specific character set but
	 * represents all the non-ISO character set that have code points
	 * in the range [0..255]. The characters in the range [0x0..0x7f] are
	 * US-ASCII characters. Extended ASCII character sets can include
	 * printable charactes in the range [0x80..0x9f] unlike the ISO8859-x
	 * character sets.
	 * 
	 * @param theBytes the potentially extended ASCII bytes
	 * @param tFile the file containing the bytes
	 * @return whether or not the bytes look like extended ASCII
	 */
	public static boolean looksPrintableExtendedAscii(short[] theBytes, TextFile tFile){

		for (int i = 0; i < theBytes.length; i++) {
			char c = textChars[theBytes[i]];
			if (c != 'T' && c!= 'I' && c != 'X'){
				return false;
			}
			
			tFile.unicodeChars[i] = (char) theBytes[i];
		}
				
		return true;
	}
	
	/**
	 * Determines if all the characters in the file are valid printable characters in 
	 * ISO 8859-x. This is not a particular character set but represents the
	 * ISO 8859 family of character sets. These character set are 8-bit and 
	 * omit characters in the [0x80..0x9f] range. They have US-ASCII characters 
	 * in the [0x0..0x7f] range. Each ISO8859 character set differs in
	 * the [0xa1..0xff] range. 
	 * 
	 * @param theBytes	the potentially ISO8859-x bytes
	 * @param tFile the bytes translated to Unicode code points
	 * @return whether or not it looks like ISO8859-x
	 */
	public static boolean looksPrintableIso8859(short[] theBytes, TextFile tFile){
		
		for (int i = 0; i < theBytes.length; i++) {
			char c = textChars[theBytes[i]];
			if (c != 'T' && c!= 'I'){
				return false;
			}
			
			tFile.unicodeChars[i] = (char) theBytes[i];
		}
		return true;
	}
	
	/**
	 * Determines if this looks like UTF-16. Note that this requires the presence of
	 * the BOM in the first 2 bytes of the file. 
	 * 
	 * Official names foe UTF-16:
	 * 
	 * Name: UTF-16BE                           [RFC2781]
	 * MIBenum: 1013
	 * SubAccount: RFC 2781
	 * Alias: None
	 * 
	 * Name: UTF-16LE                           [RFC2781]
	 * MIBenum: 1014
	 * SubAccount: RFC 2781
	 * Alias: None
	 * 
	 * Name: UTF-16                             [RFC2781]
	 * MIBenum: 1015
	 * SubAccount: RFC 2781
	 * Alias: None
	 * 
	 * @param theBytes	the potentially UTF16 bytes
	 * @param tFile	the text file containing the characters
	 * @return <code>0</code> if its not UTF16, <code>1</code> if its UTF16-LE 
	 * 	and <code>2</code> if its UTF16-BE
	 */
	public static byte looksPrintableUtf16(short[] theBytes, TextFile tFile){
		boolean isBigEndian = false;
		
		// there must be at least 2 bytes for this to be UTF-16
		if (theBytes.length < 2) {
			return 0;
		}
		
		// assume it is UTF-16 for the moment
		// check for the BOM
		if (theBytes[0] == 0xff && theBytes[1] == 0xfe){
		    // has big-endian BOM
			isBigEndian = false;
			try {
                tFile.addFormatAttribute(TextFile.Attribute.HAS_BOM);
            } catch (FatalException e) {
                // won't really fail - using a constant
            }
			//tFile.setByteOrder(DataFile.BYTE_ORDER_LE);
		} else if (theBytes[0] == 0xfe && theBytes[1] == 0xff){
		    // has little-endian BOM
			isBigEndian = true;
			try {
                tFile.addFormatAttribute(TextFile.Attribute.HAS_BOM);
            } catch (FatalException e) {
                // won't really fail - using a constant
            }
		} else {
		    // doesn't have BOM
            tFile.removeFormatAttribute(TextFile.Attribute.HAS_BOM);
            
			return 0;
		}
		
		// shuffle through each UTF-16 character (2 bytes)
		int j = 0;
		for (int i=2; i<theBytes.length; i+=2){
			if (isBigEndian) {
				tFile.unicodeChars[j++] = (char) (theBytes[i+1] + (256*theBytes[i]));
			} else {
				// little-endian
				tFile.unicodeChars[j++] = (char) (theBytes[i] + (256*theBytes[i+1]));
			}
			
			// disallow the BOM elsewhere in the file
			if (tFile.unicodeChars[j-1] == 0xfffe) {
				return 0;
			}
			
			// disallow non-printable characters
			//System.out.println("tFile.unicodeChars[j-1]: " + tFile.unicodeChars[j-1]);
			if (tFile.unicodeChars[j-1] < 128 && textChars[tFile.unicodeChars[j-1]] != 'T'){
					return 0;
			}
		}
		
		if (isBigEndian) {
			return 2;
		} 
		
		// little endian
		return 1;
		
	}
	
	/**
	 * Determines if the characters in a file look like UTF-8. Note that this will 
	 * return false if the characters are all printable ascii even though technically 
	 * these characters are the same in UTF-8. For this reason always check if
	 * the characters are ascii first before calling this method.
	 * 
	 * The official names for UTF-8:
	 * 
	 * Name: UTF-8                               [RFC3629]
	 * MIBenum: 106
	 * SubAccount: RFC 3629
	 * Alias: None 
	 * 
	 * @param theBytes	the possibly UTF-8 characters to examine
	 * @param tFile	teh text file containing the characters
	 * @return whether or not it looks like UTF-8
	 */
	public static boolean looksPrintableUtf8(short[] theBytes, TextFile tFile){
		char unicodeChar;
		boolean gotOne = false;
		boolean done = false;
		
		int i = 0;
		
		// this loop always starts again at the beginning of a 1 or multi-byte character
		while (!done &&  i<theBytes.length) {
			if ((theBytes[i] & 0x80) == 0) {
				// 0xxx xxxx - 7th bit is not set
				// it's ascii - but make sure its printable ascii
				if (textChars[theBytes[i]] != 'T'){
					return false;
				}
			} else if ((theBytes[i] & 0x40) == 0) {
				// 10xx xxxx - 7 bit set, 6th bit not set
				// this will never be the case for the 1st byte of UTF8
				return false;
			} else {
				int numBytesToFollow = 0;
				
				if ((theBytes[i] & 0x20) == 0) {
					// 110x xxxx
					unicodeChar = (char) (theBytes[i] & 0x1f); // add 31
					numBytesToFollow = 1;
				} else if ((theBytes[i] & 0x10) == 0) {
					// 1110 xxxx
					unicodeChar = (char) (theBytes[i] & 0x0f); // add 15
					numBytesToFollow = 2;
				} else  if ((theBytes[i] & 0x08) == 0){
					// 1111 0xxx
					unicodeChar = (char) (theBytes[i] & 0x07);
					numBytesToFollow = 3;
				} else  if ((theBytes[i] & 0x04) == 0){
					// 1111 10xx
					unicodeChar = (char) (theBytes[i] & 0x03);
					numBytesToFollow = 4;
				} else  if ((theBytes[i] & 0x02) == 0){
					// 1111 110x
					unicodeChar = (char) (theBytes[i] & 0x01);
					numBytesToFollow = 5;
				} else {
					return false;
				}
				
				// check the rest of the bytes in this utf-8 multi-byte character
				for (int j=0; j < numBytesToFollow; j++) {
					i++;
					if (i >= theBytes.length) {
						done = true;
						break;
					}
					if ((theBytes[i] & 0x80) == 0 || (theBytes[i] & 0x40) != 0) {
						// following bytes didn't start as 10xx xxxx - illegal for UTF8
						return false;
					}
					unicodeChar = (char) ((unicodeChar << 6) + (theBytes[i] & 0x3f));
				} // end for
				
				if (i < theBytes.length)
					tFile.unicodeChars[i] = unicodeChar;
				gotOne = true; // found a non-ASCII UTF-8 character
				
			} // end else
			i++;
		} // end while
		return gotOne;
	}
	
}
