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
 * Created on Sep 23, 2003
 *
 */
package edu.fcla.daitss.format.image.tiff;


/**
 * <P>TiffDataTypes represents all the data types used in tiff files.</P>
 * 
 *  <P>Tiff images' data types are reconciled with Java's data types like so:</P>
 * <TABLE BORDER=1>
 * <TR><TD>Tiff data type</TD>										<TD>Java data type</TD></TR>
 * <TR><TD>BYTE (8bit unsigned)</TD>						<TD>short</TD></TR>
 * <TR><TD>ASCII (8bit unsigned, 7 usable)</TD>		<TD>byte</TD></TR>
 * <TR><TD>SHORT (16bit unsigned int)</TD>				<TD>int</TD></TR>
 * <TR><TD>LONG (32bit unsigned int)</TD>				<TD>long</TD></TR>
 * <TR><TD>RATIONAL (2 longs, 32bit/32bit)</TD>		<TD>float</TD></TR>
 * <TR><TD>SBYTE (8bit signed, 2's complement)</TD><TD>byte</TD></TR>
 * <TR><TD>UNDEFINED (8bit)</TD>							<TD>char[]?</TD></TR>
 * <TR><TD>SSHORT (16bit signed, 2's complement)</TD><TD>short</TD></TR>
 * <TR><TD>SLONG (32bit signed, 2's complement)</TD><TD>int</TD></TR>
 * <TR><TD>SRATIONAL (2 slongs, 32bit/32bit)</TD>	<TD>float</TD></TR>
 * <TR><TD>FLOAT (32bit single-precision IEEE)</TD><TD>float</TD></TR>
 * <TR><TD>DOUBLE (64bit double-precision IEEE)</TD><TD>double</TD></TR>
 * <TR><TD>IFD (32bit unsigned int)</TD>						<TD>long		(PageMaker)</TD></TR>
 * </TABLE>
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class TiffDataTypes {

	/**
	 * Any type.
	 */
	public static final int ANY = 0;

	/**
	 * 8-bit byte that contains a 7-bit ASCII code; 
	 * the last byte must NUL (binary zero)
	 */
	public static final int ASCII = 2;

	/**
	 * 8-bit unsigned integer.
	 */
	public static final int BYTE = 1;

	/**
	 * double precision (8-byte) IEEE format.
	 */
	public static final int DOUBLE = 12;

	/**
	 * single precision (4-byte) IEEE format.
	 */
	public static final int FLOAT = 11;
	
	/**
	 * identical to longs but points to IFDs, PageMaker
	 */
	public static final int IFD = 13;

	/**
	 * 32-bit (4-byte) unsigned integer.
	 */
	public static final int LONG = 4;

	/** 
	 * 2 LONGs; the first represents the numerator of a fraction;
	 * the second represents the denominator.
	 */
	public static final int RATIONAL = 5;

	/**
	 * an 8-bit signed (twos-complement) integer
	 */
	public static final int SBYTE = 6;

	/**
	 * 16-bit (2-byte) unsigned integer.
	 */
	public static final int SHORT = 3;

	/**
	 * an 32-bit (4-byte) signed (twos-complement) integer
	 */
	public static final int SLONG = 9;

	/** 
	 * 2 SLONGs; the first represents the numerator of a fraction;
	 * the second represents the denominator.
	 */
	public static final int SRATIONAL = 10;

	/**
	 * an 16-bit (2-byte) signed (twos-complement) integer
	 */
	public static final int SSHORT = 8;
	
	/**
	 * undefined type.
	 */
	public static final int UNDEFINED = 7;
	
	/**
	 * The Tiff data types as Strings (the index of the type is its code)
	 */	
	private static String[] stringTypes = {"ANY", "BYTE", "ASCII", "SHORT", "LONG", "RATIONAL",
			"SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "FLOAT",
			"DOUBLE", "IFD"};
	
	/**
	 * The codes for the Tiff data types (the index of the type is its code)
	 */
	private static int[] types = {ANY, BYTE, ASCII, SHORT, LONG, RATIONAL,
		SBYTE, UNDEFINED, SSHORT, SLONG, SRATIONAL, FLOAT,
		DOUBLE, IFD};
	
	/**
	 * Returns the number of bytes this tiff data type is stored in.
	 * The <code>type</code> parameter must be the value of
	 * one of the static data type members of this class.
	 * 
	 * @param type	a tiff data types
	 * @return	the number of bytes
	 */
	public static int getNumBytes(int type) {
		int numBytes = 0;
		switch (type) {
			case ASCII : numBytes = 1; break;
			case BYTE: numBytes = 1; break; 
			case DOUBLE: numBytes = 8; break; 
			case IFD: numBytes = 4; break;
			case FLOAT: numBytes = 4; break; 
			case LONG: numBytes = 4; break; 
			case RATIONAL: numBytes = 8; break; 
			case SBYTE: numBytes = 1; break; 
			case SHORT: numBytes = 2; break; 
			case SLONG: numBytes = 4; break; 
			case SRATIONAL: numBytes = 8; break; 
			case SSHORT: numBytes = 2; break; 
			case UNDEFINED: numBytes = 1; break; 
			/*default :
				Informer.getInstance().fail(
					"edu.fcla.daitss.format.image.TiffDataTypes", 
					"TiffDataTypes.getNumBytes(int) given an illegal argument",
					"data type: " + type,
					new IllegalArgumentException("Illegal tiff data type"));
				break;*/
		}
		
		return numBytes;
	}
	
	/**
	 * Returns the name of a Tiff data type given its code
	 * 
	 * @param code the code for the Tiff data type
	 * @return the name of the Tiff data type
	 */
	public static String getStringValue(int code){
		return stringTypes[code];
	}

	/**
	 * Returns the code for a Tiff data type
	 * 
	 * @param value the name of the Tiff data type
	 * @return the code
	 */
	public static int getValue(String value) {
		int result = -1;
		boolean foundIt = false;
		int i=0;
		while (i<stringTypes.length && ! foundIt) {
			if (stringTypes[i].equals(value)) {
				foundIt = true;
				result = types[i];
			}
			i++;
		}
		return result;
	}
	
	/**
	 * Determines if a Tiff data type was new to Tiff spec.
	 * revision 6.0.
	 * 
	 * @param code	a tiff data type
	 * @return	<code>true</code> if this data type was new
	 * 	to tiff spec revision 6.0, else <code>false</code>
	 */
	public static boolean isSixODataType(int code){
		if (	code == SBYTE 		|| code == UNDEFINED ||
				code == SSHORT 	|| code == SLONG ||
				code == SRATIONAL || code == FLOAT ||
				code == DOUBLE){
					return true;
		}
			return false;
	}
	
	/**
	 * Determines whether or not a type code is a valid tiff 
	 * type code.
	 * 
	 * @param code the type code
	 * @return  whether or not a type code is a valid tiff 
	 * 			type code.
	 */
	public static boolean isValidDataType(int code){
		for(int i=0; i<types.length; i++) {
			if (types[i] == code) {
				return true;
			}
		}
		return false;
	}

}
