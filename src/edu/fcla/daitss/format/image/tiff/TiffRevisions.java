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
 * Created on Oct 10, 2003
 *
 */
package edu.fcla.daitss.format.image.tiff;

import java.util.Iterator;
import java.util.TreeSet;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.ColorSpace;

/**
 * TiffRevisions represents the information needed to distinguish 
 * between different Tiff Revisions (4.0, 5.0 and 6.0). All Tiffs have
 * version 42, but each are based on one of the Tiff revisions.
 * The revision number is used as the version number for Tiff
 * images in this application because it is more informative than 
 * giving all Tiff images version 42.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class TiffRevisions {
	/**
	 * Tiff revision 4.0
	 * 
	 * See Tag Image File Format  Rev 4.0, April 31, 1987
	 */
	public static final String REVISION_4 = "4.0";
	
	/**
	 * Tiff revision 5.0
	 * 
	 *See "An Aldus/Microsoft Technical Memorandum:  8/8/88"
          
	 * 
	 * Indicated by the presence of tags that were new to revision 5.0
	 * (see the list in <code>five_oTags</code> or at least one of the
	 * following:
	 * PhotometricInterpretation(262) = 	3(ColorSpace.PALETTE) or
	 * 														4(ColorSpace.TRANSP)
	 * Compression(259) = 5(Compression.LZW)
	 */
	public static final String REVISION_5 = "5.0";
	
	/**
	 * Tiff revision 6.0
	 * 
	 * Indicated by the presence of tags that were new to revision 6.0
	 * (see the list in <code>six_oTags</code> or at least one of the
	 * following:
	 * PhotometricInterpretation(262) = 	5(ColorSpace.CMYK),
	 * 														6(ColorSpace.YCBCR), or
	 * 														8(ColorSpace.CIELAB)
	 * Compression(259) = 6(Compression.JPEG)
	 * Any data types of 6(TiffDataType.SBYTE), 7(TiffDataType.UNDEFINED), 
	 * 8((TiffDataType.SSHORT), 9((TiffDataType.SLONG), 
	 * 10(TiffDataType.SRATIONAL), 11(TiffDataType.FLOAT), or 
	 * 12(TiffDataType.DOUBLE)
	 */
	public static final String REVISION_6 = "6.0";
	
	/**
	 * Tags introdiuced in the Tiff 5.0 spec.
	 */
	private static final int[] five_oTags = {
		254,		// NewSubFileType
		305,		// Software
		306,		// DateTime
		315,		// Artist
		316,		// HostComputer
		317,		// Predictor
		318,		// WhitePoint
		319,		// PrimaryChromaticities
		320 		// ColorMap
	};
	
	/**
	 * Tags introduced in the Tiff 6.0 spec.
	 */
	private static final int[] six_oTags = {
		321,		// HalftoneHints
		322, 		// TileWidth
		323, 		// TileLength
		324,		// TileOffsets
		325, 		// TileByteCounts
		332,		// InkSet
		333,		// InkNames
		334,		// NumberOfInks
		336,		// DotRange
		337, 		// TargetPrinter
		338,		// ExtraSamples
		339,		// SampleFormat
		340, 		// SMinSampleValue
		341, 		// SMaxSampleValue
		342,		// TransferRange
		512,		// JPEGProc
		513,		// JPEGInterchangeFormat
		514,		// JPEGInterchangeFormatLength
		515, 		// JPEGRestartInterval
		517,		// JPEGLosslessPredictors
		518, 		// JPEGPointTransforms
		519, 		// JPEGQTables
		520, 		// JPEGDCTables
		521,		// JPEGACTables
		529,		// YCbCrCoefficients
		530, 		// YCbCrSubSampling
		531,		// YCbCrPositioning
		532,		// ReferenceBlackWhite
		33442 		// Copyright
	};
	
	/**
	 * Determines the file format version of this tiff image.
	 * 
	 * @param tags	the tag numbers found in the file
	 * @param tf	the TiffFile representing the image
	 * @param hasSixOTypes	whether or not the image uses tiff 6.0 data types
	 * @return	the tiff revision number
	 */
	public static String calcRevision(TreeSet tags, Tiff tf, boolean hasSixOTypes){
		String revision = REVISION_4;
		boolean stopLooking = false, is_six = false, is_five = false;
		Iterator tagsIter = null;
		
		// if it used data types new to revision 6.0 - call it 6.0 and
		// stop looking at its other characteristics
		if (hasSixOTypes) {
			//System.out.println("Has 6.0 data type");
			is_six = true;
			revision = REVISION_6;
			stopLooking = true;
		}
		
		// see if its a revision 6.0 tiff
		// check for tags that were new to revision 6.0
		int i = 0;
		tagsIter = tags.iterator();
		while(i<six_oTags.length && !stopLooking){
			while(tagsIter.hasNext() && !stopLooking) {
				int theTag = ((Integer) tagsIter.next()).intValue();
				if (six_oTags[i] == theTag){
					//System.out.println("Has 6.0 tag: " + theTag);
					is_six = true;
					revision = REVISION_6;
					stopLooking = true;
				}
			}
			i++;
		}
		
		if (!is_six) {
			// check the values of tags to be sure
			Iterator bsIter = tf.getBitstreams().iterator();
			// for each image in the Tiff:
			while (bsIter.hasNext()) {
				TiffImage ti = (TiffImage) bsIter.next();
				
				// check its compression for 6.0 compression
				String comp = ti.getCompression().toString();
				if (comp.equals(Compression.COMP_JPEG)){
					is_six = true;
					revision = REVISION_6;
				}
				
				if (!is_six) {
					// check the image's color space for 6.0 color spaces
					String cs = ti.getColorSpace();
					if (cs.equals(ColorSpace.CS_CMYK) ||
						cs.equals(ColorSpace.CS_YCBCR) ||
						cs.equals(ColorSpace.CS_CIELAB)){
							is_six = true;
							revision = REVISION_6;
					} // end if
				} // end if
			} // end while
		} // end if
		
		// now see if its a revision 5.0 tiff
		// check for tags that were new to revision 5.0
		if (!is_six) {
			i = 0;
			tagsIter = tags.iterator();
			while(i<five_oTags.length && !stopLooking){
				while(tagsIter.hasNext() && !stopLooking) {
					int theTag = ((Integer) tagsIter.next()).intValue();
					if (five_oTags[i] == theTag){
						//System.out.println("Has 5.0 tag: " + theTag);
						is_five = true;
						revision = REVISION_5;
						stopLooking = true;
					} // end if
				} // end while
				i++;
			} // end while
		} // end if
		
		if (!is_six && !is_five) {
			// check the value of its tags to be sure its not a 5.0
			Iterator bsIter = tf.getBitstreams().iterator();
			// for each image in the Tiff:
			while (bsIter.hasNext()) {
				TiffImage ti = (TiffImage) bsIter.next();
							
				//	check its Color Space (PhotometricInterpretation)
				String cs = ti.getColorSpace();
				if (ti.hasIntPalette().equals(DataFile.TRUE) ||
					cs.equals(ColorSpace.CS_TRANSP)){
					 is_five = true;
					 revision = REVISION_5;
				 } // end if
			 } // end while
		} // end if
		
		if (!is_six && !is_five) {
			Iterator bsIter = tf.getBitstreams().iterator();
			// for each image in the Tiff:
			while (bsIter.hasNext()) {
				TiffImage ti = (TiffImage) bsIter.next();
				//	check its compression
				String comp = ti.getCompression().toString();
				if (comp.equals(Compression.COMP_LZW)){
					is_five = true;
					revision = REVISION_5;
				}
			}
		}
		return revision;
	}

}
