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
 * Created on Nov 5, 2003
 *
 */
package edu.fcla.daitss.format.image.jpeg;

/**
 * JPEGMarkers represents the markers and marker  segments described 
 * in the CCITT Recommendation T.81: "Information Technology - Digital 
 * Compression and Coding of Continuous-Tone Still Images - 
 * Requirements and Guidelines", September 1992.
 * 
 * These markers are two-byte codes in which the first byte is hexadecimal
 * FF (X'FF') and the second byte is a value between 1 and hexadecimal
 * FE (X'FE').
 * 
 * Some markers are the start of 'marker segments'. This means that
 * parameters follow the marker.
 * 
 * 
 * Defined markers according to ISO/IEC 15444-1:2000(E):
 * (doesn't include JFIF-defined markers)
 * 
 * Marker code range					Standard definition
 * ---------------------------------------------------------
 * 0xFF00,0xFF01,0xFFFE,0xFFC0-0xFFDF	ITU-T Rec. T.81|ISO/IEC 10918-1
 * 0xFFF0-0xFFF6						ITU-T Rec. T.84|ISO/IEC 14495-1
 * 0xFFF7-0xFFF8						ITU-T Rec. T.87|ISO/IEC 14495-1
 * 
 * 
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class JpegMarkers {
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP0 = 0xffe0;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP1 = 0xffe1;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP10 = 0xffea;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP11 = 0xffeb;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP12 = 0xffec;
	
	/**
	 * Reserved for application segments
	 * Photoshop Information Block
	 */
	public static final int APP13 = 0xffed;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP14 = 0xffee;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP15 = 0xffef;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP2 = 0xffe2;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP3 = 0xffe3;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP4 = 0xffe4;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP5 = 0xffe5;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP6 = 0xffe6;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP7 = 0xffe7;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP8 = 0xffe8;
	
	/**
	 * Reserved for application segments
	 */
	public static final int APP9 = 0xffe9;
	
	/**
	 * Comment
	 */
	public static final int COM = 0xfffe;
	
	/**
	 * Define arithmetic coding conditioning(s)
	 */
	public static final int DAC = 0xffcc;
	
	/**
	 * Define hierarchical progression
	 */
	public static final int DHP = 0xffde;
	
	/**
	 * Define Huffman table(s)
	 */
	public static final int DHT = 0xffc4;
	
	/**
	 * Define number of lines
	 */
	public static final int DNL = 0xffdc;
	
	/**
	 * Define quantization table(s)
	 */
	public static final int DQT = 0xffdb;
	
	/**
	 * Define restart interval
	 */
	public static final int DRI = 0xffdd;
	
	/**
	 * End of image
	 */
	public static final int EOI = 0xffd9;
	
	/**
	 * Expand reference component(s)
	 */
	public static final int EXP = 0xffdf;
	
	/**
	 * Start of Frame marker, non-differential, arithmetic coding:
	 * Reserved for JPEG extensions
	 */
	public static final int JPG = 0xffc8;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG0 = 0xfff0;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG1 = 0xfff1;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG10 = 0xfffa;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG11 = 0xfffb;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG12 = 0xfffc;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG13 = 0xfffd;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG2 = 0xfff2;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG3 = 0xfff3;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG4 = 0xfff4;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG5 = 0xfff5;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG6 = 0xfff6;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG7 = 0xfff7;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG8 = 0xfff8;
	
	/**
	 * Reserved for JPEG extensions
	 */
	public static final int JPG9 = 0xfff9;
	
	/**
	 * Restart interval termination 0
	 */
	public static final int RST0 = 0xffd0;
	
	/**
	 * Restart interval termination 1
	 */
	public static final int RST1 = 0xffd1;
	
	/**
	 * Restart interval termination 2
	 */
	public static final int RST2 = 0xffd2;
	
	/**
	 * Restart interval termination 3
	 */
	public static final int RST3 = 0xffd3;
	
	/**
	 * Restart interval termination 4
	 */
	public static final int RST4 = 0xffd4;
	
	/**
	 * Restart interval termination 5
	 */
	public static final int RST5 = 0xffd5;
	
	/**
	 * Restart interval termination 6
	 */
	public static final int RST6 = 0xffd6;
	
	/**
	 * Restart interval termination 7
	 */
	public static final int RST7 = 0xffd7;
	
	/**
	 * Start of Frame marker, non-differential, Huffman coding: Baseline
	 * DCT
	 */
	public static final int SOF0 = 0xffc0;
	
	/**
	* Start of Frame marker, non-differential, Huffman coding: Extended
	* Sequential DCT
	*/
	public static final int SOF1 = 0xffc1;
	
	/**
	 * Start of Frame marker, non-differential, arithmetic coding:
	 * Progressive DCT
	 */
	public static final int SOF10 = 0xffca;
	
	/**
	 * Start of Frame marker, non-differential, arithmetic coding:
	 * Lossless (sequential)
	 */
	public static final int SOF11 = 0xffcb;
	
	/**
	 * Start of Frame marker, differential, arithmetic coding:
	 * Differential sequential DCT
	 */
	public static final int SOF13 = 0xffcd;
	
	/**
	 * Start of Frame marker, differential, arithmetic coding:
	 * Differential progressive DCT
	 */
	public static final int SOF14 = 0xffce;
	
	/**
	 * Start of Frame marker, differential, arithmetic coding:
	 * Differential lossless (sequential)
	 */
	public static final int SOF15 = 0xffcf;
	
	/**
	 * Start of Frame marker, non-differential, Huffman coding: Progressive
	 * DCT
	 */
	public static final int SOF2 = 0xffc2;
	
	/**
	 * Start of Frame marker, non-differential, Huffman coding: Lossless
	 * (sequential)
	 */
	public static final int SOF3 = 0xffc3;
	
	/**
	 * Start of Frame marker, differential, Huffman coding: Differential
	 * sequential DCT
	 */
	public static final int SOF5 = 0xffc5;
	
	/**
	 * Start of Frame marker, differential, Huffman coding: Differential
	 * progressive DCT
	 */
	public static final int SOF6 = 0xffc6;
	
	/**
	 * Start of Frame marker, differential, Huffman coding: Differential
	 * lossless (sequential)
	 */
	public static final int SOF7 = 0xffc7;
	
	/**
	 * Start of Frame marker, non-differential, arithmetic coding:
	 * Extended sequential DCT
	 */
	public static final int SOF9 = 0xffc9;
	
	/**
	 * Start of image
	 */
	public static final int SOI = 0xffd8;
	
	/**
	 * Start of scan
	 */
	public static final int SOS = 0xffda;
	
	/**
	 * Temporary private use in arithmetic coding
	 */
	public static final int TEM = 0xff01;

	
	/**
	 * Returns the string version (more human-readable) of a JPEG marker
	 * 
	 * @param marker a JPEG marker (ex: 0xFF01)
	 * @return the string version of the marker (ex: TEM)
	 */
	public static String markerToString(int marker){
		String stringVersion = null;
		switch(marker){
			case APP0: stringVersion = "APP0"; break;
			case APP1: stringVersion = "APP1"; break;
			case APP2: stringVersion = "APP2"; break;
			case APP3: stringVersion = "APP3"; break;
			case APP4: stringVersion = "APP4"; break;
			case APP5: stringVersion = "APP5"; break;
			case APP6: stringVersion = "APP6"; break;
			case APP7: stringVersion = "APP7"; break;
			case APP8: stringVersion = "APP8"; break;
			case APP9: stringVersion = "APP9"; break;
			case APP10: stringVersion = "APP10"; break;
			case APP11: stringVersion = "APP11"; break;
			case APP12: stringVersion = "APP12"; break;
			case APP13: stringVersion = "APP13"; break;
			case APP14: stringVersion = "APP14"; break;
			case APP15: stringVersion = "APP15"; break;
			case COM: stringVersion = "COM"; break;
			case DAC: stringVersion = "DAC"; break;
			case DHP: stringVersion = "DHP"; break;
			case DHT: stringVersion = "DHT"; break;
			case DNL: stringVersion = "DNL"; break;
			case DQT: stringVersion = "DQT"; break;
			case DRI: stringVersion = "DRI"; break;
			case EOI: stringVersion = "EOI"; break;
			case EXP: stringVersion = "EXP"; break;
			case JPG: stringVersion = "JPG"; break;
			case JPG0: stringVersion = "JPG0"; break;
			case JPG1: stringVersion = "JPG1"; break;
			case JPG2: stringVersion = "JPG2"; break;
			case JPG3: stringVersion = "JPG3"; break;
			case JPG4: stringVersion = "JPG4"; break;
			case JPG5: stringVersion = "JPG5"; break;
			case JPG6: stringVersion = "JPG6"; break;
			case JPG7: stringVersion = "JPG7"; break;
			case JPG8: stringVersion = "JPG8"; break;
			case JPG9: stringVersion = "JPG9"; break;
			case JPG10: stringVersion = "JPG10"; break;
			case JPG11: stringVersion = "JPG11"; break;
			case JPG12: stringVersion = "JPG12"; break;
			case JPG13: stringVersion = "JPG13"; break;
			case RST0: stringVersion = "RST0"; break;
			case RST1: stringVersion = "RST1"; break;
			case RST2: stringVersion = "RST2"; break;
			case RST3: stringVersion = "RST3"; break;
			case RST4: stringVersion = "RST4"; break;
			case RST5: stringVersion = "RST5"; break;
			case RST6: stringVersion = "RST6"; break;
			case RST7: stringVersion = "RST7"; break;
			case SOF0: stringVersion = "SOF0"; break;
			case SOF1: stringVersion = "SOF1"; break;
			case SOF2: stringVersion = "SOF2"; break;
			case SOF3: stringVersion = "SOF3"; break;
			case SOF5: stringVersion = "SOF5"; break;
			case SOF6: stringVersion = "SOF6"; break;
			case SOF7: stringVersion = "SOF7"; break;
			case SOF9: stringVersion = "SOF9"; break;
			case SOF10: stringVersion = "SOF10"; break;
			case SOF11: stringVersion = "SOF11"; break;
			case SOF13: stringVersion = "SOF13"; break;
			case SOF14: stringVersion = "SOF14"; break;
			case SOF15: stringVersion = "SOF15"; break;
			case SOI: stringVersion = "SOI"; break;
			case SOS: stringVersion = "SOS"; break;
			case TEM: stringVersion = "TEM"; break;
			default: // keep it qual to null (the unknown marker will be noticed
				// elsewhere (during the parse)
		}
		return stringVersion;
	}
}
