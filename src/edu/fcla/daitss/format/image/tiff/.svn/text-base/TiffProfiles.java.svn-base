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
package edu.fcla.daitss.format.image.tiff;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.Image;

/**
 * <P>TiffProfile represents all the Tiff profiles and their requirements.
 * Note that there does not seem to be any authoritative list of Tiff
 * profiles and their requirements, so the information in this class was
 * culled from a variety of sources and specifications. Any comments
 * or suggestions can be sent to the FCLA.
 * 
 * <P>Tiff has default values for some of its fields. This class
 * does not require that an image have a field that is required of a profile
 * if the default value for that field is what is required by the profile.
 * 
 * @author Andrea Goethals, FCLA
 */
public class TiffProfiles {

	/**
	 * <P>Tiff Baseline image in bilevel colorspace (aka Tiff Class B).
	 * See Part 1, Section 3: Bilevel Images of the Tiff 6.0 specification</P>
	 * 
	 * <P>Required Tiff fields:</P>
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>							<TD>Required Value</TD></TR>
	 * <TR><TD>Compression(259)</TD>	<TD>1(Compression.NONE), 2(Compression.CCITT_ID), 
	 * 																	or 32773(Compression.PACKBITS)</TD></TR>
	 * <TR><TD>ImageWidth(256)</TD>		<TD>-</TD></TR>
	 * <TR><TD>ImageLength(257)</TD>		<TD>-</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD><TD>0(ColorSpace.WHITEISZERO) or 
	 * 																	1(ColorSpace.BLACKISZERO)</TD></TR>
	 * <TR><TD>RowsPerStrip(278)</TD>	<TD>-</TD></TR>
	 * <TR><TD>StripOffsets(273)</TD>		<TD>-</TD></TR>
	 * <TR><TD>StripByteCounts(279)</TD><TD>-</TD></TR>
	 * <TR><TD>XResolution(282)</TD>		<TD>-</TD></TR>
	 * <TR><TD>YResolution(283)</TD>		<TD>-</TD></TR>
	 * <TR><TD>ResolutionUnit(296)</TD><TD>1(RasterImageFile.RES_UNIT_NONE), 
	 * 																	2(RasterImageFile.RES_UNIT_INCH or 
	 * 																	3(RasterImageFile.RES_UNIT_CM)</TD></TR>
	 * </TABLE>
	 * Also the first component (if there are more than 1 sample per pixel) must have
	 * 1 bit per sample. 
	 */
	public static final String BASELINE_BILEVEL = "TIFF_BL_BILEVEL";

	/**
	 * <P>Tiff Baseline image in grayscale colorspace (aka Tiff Class G).
	 * See Part 1, Section 4: Grayscale Images of the Tiff 6.0 specification</P>
	 * 
	 * <P>Required Tiff fields:</P>
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>						<TD>Required Value</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>		<TD>4 or 8</TD></TR>
	 * <TR><TD>Compression(259)</TD>		<TD>1(Compression.NONE) or 
	 * 																		32773(Compression.PACKBITS)</TD></TR>
	 * <TR><TD>ImageWidth(256)</TD>			<TD>-</TD></TR>
	 * <TR><TD>ImageLength(257)</TD>		<TD>-</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD><TD>0(ColorSpace.WHITEISZERO) or 
	 * 																		1(ColorSpace.WHITEISZERO)</TD></TR>
	 * <TR><TD>RowsPerStrip(278)</TD>		<TD>-</TD></TR>
	 * <TR><TD>StripOffsets(273)</TD>		<TD>-</TD></TR>
	 * <TR><TD>StripByteCounts(279)</TD>	<TD>-</TD></TR>
	 * <TR><TD>XResolution(282)</TD>		<TD>-</TD></TR>
	 * <TR><TD>YResolution(283)</TD>		<TD>-</TD></TR>
	 * <TR><TD>ResolutionUnit(296)</TD>		<TD>1(RasterImageFile.RES_UNIT_NONE), 
	 * 																			2(RasterImageFile.RES_UNIT_INCH or 
	 * 																			3(RasterImageFile.RES_UNIT_CM)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_GRAYSCALE = "TIFF_BL_GRAYSCALE";

	/**
	 * <P>Tiff Baseline image in palette colorspace (aka Tiff Class P).
	 * See Part 1, Section 5: Palette-color Images of the Tiff 6.0 specification</P>
	 * 
	 * <P>Required Tiff fields:</P>
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>								<TD>Required Value</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>		<TD>4 or 8</TD></TR>
	 * <TR><TD>ColorMap(320)</TD>				<TD>-</TD></TR>
	 * <TR><TD>Compression(259)</TD>		<TD>1(Compression.NONE) or 
	 * 																		32773(Compression.PACKBITS)</TD></TR>
	 * <TR><TD>ImageWidth(256)</TD>			<TD>-</TD></TR>
	 * <TR><TD>ImageLength(257)</TD>			<TD>-</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD><TD>3(ColorSpace.PALETTE)</TD></TR>
	 * <TR><TD>RowsPerStrip(278)</TD>		<TD>-</TD></TR>
	 * <TR><TD>StripOffsets(273)</TD>			<TD>-</TD></TR>
	 * <TR><TD>StripByteCounts(279)</TD>	<TD>-</TD></TR>
	 * <TR><TD>XResolution(282)</TD>			<TD>-</TD></TR>
	 * <TR><TD>YResolution(283)</TD>			<TD>-</TD></TR>
	 * <TR><TD>ResolutionUnit(296)</TD>		<TD>1(RasterImageFile.RES_UNIT_NONE), 
	 * 																		2(RasterImageFile.RES_UNIT_INCH or 
	 * 																		3(RasterImageFile.RES_UNIT_CM)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_PALETTE = "TIFF_BL_PALETTE";

	/**
	 * <P>Tiff Baseline image in RGB colorspace (aka Tiff Class R).
	 * See Part 1, Section 6: RGB Full Color Images of the Tiff 6.0 specification</P>
	 * 
	 * <P>Required Tiff fields:</P>
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>								<TD>Required Value</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>		<TD>8,8,8</TD></TR>
	 * <TR><TD>Compression(259)</TD>		<TD>1(Compression.NONE) or 
	 * 																		32773(Compression.PACKBITS)</TD></TR>
	 * <TR><TD>ImageWidth(256)</TD>			<TD>-</TD></TR>
	 * <TR><TD>ImageLength(257)</TD>			<TD>-</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD><TD>2(ColorSpace.RGB)</TD></TR>
	 * <TR><TD>RowsPerStrip(278)</TD>		<TD>-</TD></TR>
	 * <TR><TD>SamplesPerPixel(277)</TD>	<TD>3 or more</TD></TR>
	 * <TR><TD>StripOffsets(273)</TD>			<TD>-</TD></TR>
	 * <TR><TD>StripByteCounts(279)</TD>	<TD>-</TD></TR>
	 * <TR><TD>XResolution(282)</TD>			<TD>-</TD></TR>
	 * <TR><TD>YResolution(283)</TD>			<TD>-</TD></TR>
	 * <TR><TD>ResolutionUnit(296)</TD>		<TD>1(RasterImageFile.RES_UNIT_NONE), 
	 * 																		2(RasterImageFile.RES_UNIT_INCH or 
	 * 																		3(RasterImageFile.RES_UNIT_CM)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_RGB = "TIFF_BL_RGB";

	/**
	 * <P>A subclass of BASELINE_BILEVEL for fax images.</P>
	 * 
	 * <P>SubAccount:  
	 * Cygnet Technologies, Inc., TIFF-F Revised Specifications: The Spirit of 
	 * TIFF Class F, April 29, 1990</P>
	 * 
	 * <P>Required Tiff fields:</P>
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>								<TD>Required Value</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>		<TD>1</TD></TR>
	 * <TR><TD>Compression(259)</TD>		<TD>3(Compression.GROUP_3_FAX)</TD></TR>
	 * <TR><TD>FillOrder(266)</TD>				<TD>1 or 2 (these are the only choices anyway)</TD></TR>
	 * <TR><TD>-Group3Options(T4Options)(292)</TD><TD>4 or 5</TD></TR>
	 * <TR><TD>ImageWidth(256)</TD>			<TD>1728, 2048, 2482</TD></TR>
	 * <TR><TD>-NewSubFileType(254)</TD>	<TD>2</TD></TR>
	 * <TR><TD>PageNumber(297)</TD>		<TD>-</TD></TR>
	 * <TR><TD>ResolutionUnit(296)</TD>		<TD>2 or 3</TD></TR>
	 * <TR><TD>SamplesPerPixel(277)</TD>	<TD>1</TD></TR>
	 * <TR><TD>XResolution(282)</TD>			<TD>204 (inches)</TD></TR>
	 * <TR><TD>YResolution(283)</TD>			<TD>98, 196 (inches)</TD></TR>
	 * <TR><TD COLSPAN=2>(And meets the requirements for BASELINE_BILEVEL)</TD></TR>
	 * </TABLE>
	 */
	public static final String CLASS_F = "TIFF_CLASS_F";

	/**
	 * <P>GeoTiff Revision 1.0 - a tiff having geographic information.</P>
	 * <P>See "GeoTiff Format Specification: GeoTiff Revision 1.0", 
	 * Specification Version: 1.8.1, October 31, 1995.</P>
	 * 
	 * <P>Adds the following tags to Tiff 6.0. </P>
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>											<TD>Required Value</TD></TR>
	 * <TR><TD>ModelPixelScaleTag(33550)</TD>		<TD>-</TD></TR>
	 * <TR><TD>ModelTiepointTag(33922)</TD>			<TD>-</TD></TR>
	 * <TR><TD>ModelTransformationTag(34264)</TD>		<TD>-</TD></TR>
	 * <TR><TD>GeoKeyDirectoryTag(34735)</TD>	<TD>-</TD></TR>
	 * <TR><TD>GeoDoubleParamsTag(34736)</TD><TD>-</TD></TR>
	 * <TR><TD>GeoAsciiParamsTag(34737)</TD>	<TD>-</TD></TR>
	 * </TABLE>
	 */
	public static final String GEOTIFF = "TIFF_GEOTIFF";

	/**
	 * <P>Tiff/EP.</P>
	 */
	public static final String TIFF_EP = "TIFF_EP";

	/**
	 * <P>Tiff/IT (Transport Independent File Format for Image Technology).</P>
	 */
	public static final String TIFF_IT = "TIFF_IT";

	/**
	 * <P>Exif (Exchangeable Image File Format).
	 * See "Exchangeable Image File Format for digital still cameras:
	 * Exif Version 2.2", JEITA CP-3451, April 2002.</P>
	 */
	public static final String EXIF = "TIFF_EXIF";

	/**
	 * <P>See Section 11: CCITT Bilevel Encodings (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>							<TD>Required Value</TD></TR>
	 * <TR><TD>Compression(259)</TD>	<TD>3(Compression.GROUP_3_FAX) or
	 * 																	4(Compression.GROUP_4_FAX)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_G3G4 = "TIFF_BL_EXT_G3G4";

	/**
	 * <P>See Section 13: LZW Compression (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>							<TD>Required Value</TD></TR>
	 * <TR><TD>Compression(259)</TD>	<TD>5(Compression.LZW)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_LZW = "TIFF_BL_EXT_LZW";

	/**
	 * <P>See Section 15: Tiled Images (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>							<TD>Required Value</TD></TR>
	 * <TR><TD>TileWidth(322)</TD>			<TD>-</TD></TR>
	 * <TR><TD>TileLength(323)</TD>			<TD>-</TD></TR>
	 * <TR><TD>TileOffsets(324)</TD>		<TD>-</TD></TR>
	 * <TR><TD>TileByteCounts(325)</TD>	<TD>-</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_TILED = "TIFF_BL_EXT_TILED";

	/**
	 * <P>See Section 16: CMYK Images (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>										<TD>Required Value</TD></TR>
	 * <TR><TD>SamplesPerPixel(277)</TD>			<TD>4</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>				<TD>{8,8,8,8}</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD><TD>5(ColorSpace.CMYK)</TD></TR>
	 * <TR><TD COLSPAN=2>(And all the Baseline tags)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_CMYK = "TIFF_BL_EXT_CMYK";

	/**
	 * <P>See Section 21: YCbCr Images (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>												<TD>Required Value</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD>		<TD>6(ColorSpace.YCBCR)</TD></TR>
	 * <TR><TD>ReferenceBlackWhite(532)</TD>			<TD>-</TD></TR>
	 * <TR><TD>SamplesPerPixel(277)</TD>					<TD>3</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>						<TD>{8,8,8}</TD></TR>
	 * <TR><TD>Compression(259)</TD>						<TD>1(Compression.NONE),
	 * 																						5(Compression.LZW), or
	 * 																						6(Compression.JPEG)</TD></TR>
	 * <TR><TD COLSPAN=2>(And all the Baseline tags)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_YCBCR = "TIFF_BL_EXT_YCBCR";

	/**
	 * <P>See Section 22: JPEG Compression (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>												<TD>Required Value</TD></TR>
	 * <TR><TD>-Compression(259)</TD>						<TD>6(Compression.JPEG)</TD></TR>
	 * <TR><TD>-NewSubFileType(254)</TD>					<TD>(Single image)</TD></TR>
	 * <TR><TD>-ImageWidth(256)</TD>							<TD>-</TD></TR>
	 * <TR><TD>-ImageLength(257)</TD>						<TD>-</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>						<TD>{8},{8,8,8} or {8,8,8,8}</TD></TR>
	 * <TR><TD>-PhotometricInterpretation(262)</TD>	<TD>0(ColorSpace.WHITEISZERO),
	 * 																						1(ColorSpace.BLACKISZERO),
	 * 																						2(ColorSpace.RGB),
	 * 																						5(ColorSpace.CMYK), or
	 * 																						6(ColorSpace.YCBCR)</TD></TR>
	 * <TR><TD>-SamplesPerPixel(277)</TD>					<TD>1, 3, or 4</TD></TR>
	 * <TR><TD>-XResolution(282)</TD>							<TD>-</TD></TR>
	 * <TR><TD>-YResolution(283)</TD>							<TD>-</TD></TR>
	 * <TR><TD>-PlanarConfiguration(284)</TD>				<TD>1 or 2 (all the valid choices anyway)</TD></TR>
	 * <TR><TD>-ResolutionUnit(296)</TD>						<TD>-</TD></TR>
	 * <TR><TD>-JPEGProc(512)</TD>							<TD>-</TD></TR>
	 * <TR><TD>-JPEGDCTables(520)</TD>					<TD>-</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_JPEG = "TIFF_BL_EXT_JPEG";

	/**
	 * <P>See Section 23: CIE L*a*b* Images (Tiff 6.0 spec).</P>
	 * 
	 * <TABLE BORDER=1>
	 * <TR><TD>Tag</TD>												<TD>Required Value</TD></TR>
	 * <TR><TD>PhotometricInterpretation(262)</TD>		<TD>8 (ColorSpace.CIELAB)</TD></TR>
	 * <TR><TD>BitsPerSample(258)</TD>						<TD>{8...} (list size = SamplesPerPixel)</TD></TR>
	 * </TABLE>
	 */
	public static final String BASELINE_EXT_CIELAB = "TIFF_BL_EXT_CIELAB";

	/**
	 * A collection of all the known Tiff profiles.
	 * HAVE TO KEEP UNKNOWN FIRST IN THIS LIST!
	 */
	private static String[] validTiffProfiles =
		{
			BASELINE_BILEVEL,
			BASELINE_GRAYSCALE,
			BASELINE_PALETTE,
			BASELINE_RGB,
			CLASS_F,
			GEOTIFF,
			TIFF_EP,
			TIFF_IT,
			EXIF,
			BASELINE_EXT_G3G4,
			BASELINE_EXT_LZW,
			BASELINE_EXT_TILED,
			BASELINE_EXT_CMYK,
			BASELINE_EXT_YCBCR,
			BASELINE_EXT_JPEG,
			BASELINE_EXT_CIELAB };

	/**
	 * Returns a list of all the known profiles that this image complies
	 * with.
	 * 
	 * @param tags	the Tiff tags found in this image
	 * @param ti	an image in the Tiff file
	 * @return	a list of all the profiles this image complies with
	 */
	public static Vector calcProfile(int[] tags, TiffImage ti) {
		Vector ps = null;
		ps = new Vector();
		// starts from the second profile in the list to skip over the
		// UNKNOWN profile
		for (int i = 1; i < validTiffProfiles.length; i++) {
			if (isProfile(validTiffProfiles[i], tags, ti)) {
				ps.add(validTiffProfiles[i]);
			}
		}

		return ps;
	}

	/**
	 * Checks that an image has the minimum required tags found in a 
	 * Tiff Baseline image. See Part 1 (Tiff 6.0 spec).
	 * 
	 * @param tags	the tags found in this tiff image
	 * @param ti	the TiffFile object representing this image
	 * @param checkStorage	whether or not to check for the strip-related tags
	 * @return	<code>true</code> if the image has these tags, otherwise
	 * 				<code>false</code> 
	 */
	@SuppressWarnings("unchecked")
	private static boolean hasBaselineTags(
		int[] tags,
		TiffImage ti,
		boolean checkStorage) {
		//	convert the array of tags to a Vector
		Vector vTags = new Vector<Integer>();
		vTags.addAll(Arrays.asList(tags));
		boolean isBaseline = false; // what will be returned

		if (vTags.contains(new Integer(256)) && // ImageWidth
		vTags.contains(new Integer(257)) && // ImageLength
		vTags.contains(new Integer(259)) && // Compression
		vTags.contains(new Integer(262)) && // PhotometricInterpretation
		vTags.contains(new Integer(282)) && // XResolution
		vTags.contains(new Integer(283)) && // YResolution
		vTags.contains(
				new Integer(296))) { // ResolutionUnit
			if (checkStorage) {
				if (vTags.contains(new Integer(273)) && // StripOffsets
				vTags.contains(new Integer(278)) && // RowsPerStrip
				vTags.contains(new Integer(279))) { // StripByteCounts
					isBaseline = true;
				}
			} else {
				isBaseline = true;
			}
		}

		return isBaseline;
	}

	/**
	 * Determines if this image meetes the requirements for a
	 * Tiff Baseline image in bilevel colorspace (aka Tiff Class B).
	 * See Part 1, Section 3: Bilevel Images of the Tiff 6.0 specification.
	 * 
	 * @param tags	the tag numbers found in this image
	 * @param ti	the TiffFile object reresenting the image
	 * @param checkCompression	whether or not to check its compression value
	 * @return	<code>true</code> if it meets the requirements of this profile,
	 * 				otherwise <code>false</code>
	 */
	private static boolean isBaselineBilevel(
		int[] tags,
		TiffImage ti,
		boolean checkCompression) {
		//	convert the array of tags to a Vector
		Vector vTags = new Vector<Integer>();
		vTags.addAll(Arrays.asList(tags));
		boolean isProfile = false; // what will be returned

		// becomes true when the image has something this profile can't have
		boolean notProfile = false;

		// becomes true after having all required tags and easy values to check
		boolean couldBeProfile = false;

		Iterator iter = null;

		// check that the first component has 1 bit per pixel. Usually bitonal images
		// only have 1 component per pixel but it could have extra samples for
		// transparancy, etc.
		if (ti.getBitsPerSample() != null) {
			iter = ti.getBitsPerSample().iterator();
			if (iter != null && ((Integer) iter.next()).intValue() == 1) {
				couldBeProfile = true;
			}
		}

		// check each image's characteristics
		if (couldBeProfile
			&& hasBaselineTags(tags, ti, true)
			&& (ti.getColorSpace() == ColorSpace.CS_WHITEISZERO
				|| ti.getColorSpace() == ColorSpace.CS_BLACKISZERO)
			&& (ti.getResUnit() == Image.RES_UNIT_CM
				|| ti.getResUnit() == Image.RES_UNIT_INCH
				|| ti.getResUnit() == Image.RES_UNIT_NONE)) {
			couldBeProfile = true;
		}
		if (couldBeProfile && checkCompression) {
			// check for the legal compression schemes
			if (ti.getCompression().toString() != Compression.COMP_NONE
				&& ti.getCompression().toString() != Compression.COMP_CCITT_ID
				&& ti.getCompression().toString() != Compression.COMP_PACKBITS) {
				notProfile = true;
			}
		}
		if (couldBeProfile && !notProfile) {
			isProfile = true;
		}
		return isProfile;
	}

	/**
	 * Given the code for a Tiff profile, the tags it had and the tiff itself,
	 * determines if this tiff image meets the requirements for the Tiff
	 * profile.
	 * 
	 * @param profile	the code for a known Tiff profile
	 * @param tags	the tag numbers found in the Tiff
	 * @param ti	the TiffFile representing the tiff image
	 * @return	<code>true</code> if this image meets the requirements
	 * 				for the profile, otherwise <code>false</code>
	 */
	private static boolean isProfile(
		String profile,
		int[] tags,
		TiffImage ti) {
		// convert the array of tags to a Vector
		Vector vTags = new Vector<Integer>();
		vTags.addAll(Arrays.asList(tags));
		boolean isProfile = false; // what will be returned

		// becomes true when it has something this profile can't have
		boolean notProfile = false;

		// becomes true after having all required tags and easy values to check
		boolean couldBeProfile = false;
		Iterator iter = null;

		if (profile.equals(BASELINE_BILEVEL)) {
			if (isBaselineBilevel(tags, ti, true)) {
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_GRAYSCALE)) {
			// check for required tags and the easy values to check
			if (hasBaselineTags(tags, ti, true)
				&& vTags.contains(new Integer(258))
				&& // BitsPerSample
				 (ti.getColorSpace() == ColorSpace.CS_WHITEISZERO
						|| ti.getColorSpace() == ColorSpace.CS_BLACKISZERO)) {
				couldBeProfile = true;
			}
			if (couldBeProfile) {
				// check for legal bitsPerSample values
				iter = ti.getBitsPerSample().iterator();
				while (iter.hasNext()) {
					int val = ((Integer) iter.next()).intValue();
					if (val != 4 && val != 8) {
						notProfile = true;
					}
				}
				// check for legal compression
				String val = ti.getCompression().toString();
				if (!val.equals(Compression.COMP_NONE) && 
					!val.equals(Compression.COMP_PACKBITS)) {
					notProfile = true;
				}

				if (couldBeProfile && !notProfile) {
					isProfile = true;
				}
			}
		} else if (profile.equals(BASELINE_PALETTE)) {
			// check for required tags and the easy values to check
			if (hasBaselineTags(tags, ti, true) && 
				vTags.contains(new Integer(258)) && // BitsPerSample
				vTags.contains(new Integer(320)) && // ColorMap
				ti.getColorSpace() == ColorSpace.CS_RGB) {
					couldBeProfile = true;
			}
			if (couldBeProfile) {
				// check for legal bitsPerSample values
				iter = ti.getBitsPerSample().iterator();
				while (iter.hasNext()) {
					int val = ((Integer) iter.next()).intValue();
					if (val != 4 && val != 8) {
						notProfile = true;
					}
				}
				// check for legal compression
				String val = ti.getCompression().toString();
				if (!val.equals(Compression.COMP_NONE) 
					&& !val.equals(Compression.COMP_PACKBITS)) {
					notProfile = true;
				}

				if (couldBeProfile && !notProfile) {
					isProfile = true;
				}
			}
		} else if (profile.equals(BASELINE_RGB)) {
			// check for required tags and the easy values to check
			if (hasBaselineTags(tags, ti, true)
				&& vTags.contains(new Integer(258)) && // BitsPerSample
				ti.getNumComponents() >= 3 && ti.getColorSpace() == ColorSpace.CS_RGB) {
					couldBeProfile = true;
			}
			if (couldBeProfile) {
				// check for legal bitsPerSample values
				iter = ti.getBitsPerSample().iterator();
				while (iter.hasNext()) {
					int val = ((Integer) iter.next()).intValue();
					if (val != 8) {
						notProfile = true;
					}
				}
				// check for legal compression
				String val = ti.getCompression().toString();
				if (!val.equals(Compression.COMP_NONE) 
					&& !val.equals(Compression.COMP_PACKBITS)) {
					notProfile = true;
				}

				if (couldBeProfile && !notProfile) {
					isProfile = true;
				}
			}
		} else if (profile.equals(CLASS_F)) {
			if (isBaselineBilevel(tags, ti, false)
				&& vTags.contains(new Integer(266)) && // FillOrder
				vTags.contains(new Integer(297)) && // PageNumber
				vTags.contains(new Integer(254)) && // NewSubFileType (NOT CHECKING VALUE!)
				vTags.contains(new Integer(292)) && // Group3Options (NOT CHECKING VALUE!)
			 	( ti.getPixelsHorizontal() == 1728
						|| ti.getPixelsHorizontal() == 2048
						|| ti.getPixelsHorizontal() == 2482)
				&& (ti.getResUnit() == Image.RES_UNIT_CM
					|| ti.getResUnit() == Image.RES_UNIT_INCH)
				&& ti.getNumComponents() == 1
				&& ti.getCompression().toString() == Compression.COMP_GROUP_3_FAX
				&& ti.getBitsPerSample().size() == 1
				&& ((Integer) ti.getBitsPerSample().elementAt(0)).intValue() == 1) {
					couldBeProfile = true;
			}

			if (couldBeProfile
				&& ti.getResUnit() == Image.RES_UNIT_INCH) {
				if (ti.getXResolution() == 204
					&& (ti.getYResolution() == 98
						|| ti.getYResolution() == 196)) {
					isProfile = true;
				}
			} else if (
				couldBeProfile
					&& ti.getResUnit() == Image.RES_UNIT_CM) {
				// 204 in = 518.16 cm, 98 in = 248.92 cm, 196 in = 497.84 cm
				int truncXRes = (int) ti.getXResolution();
				int truncYRes = (int) ti.getYResolution();
				if (truncXRes == 518
					&& (truncYRes == 248 || truncYRes == 497)) {
					isProfile = true;
				}
			} // else isProfile remains false
		} else if (profile.equals(GEOTIFF)) {
			//	note that these tags just indicate that this is a geotiff,
			// not that it meets the requirements for it!
			if (vTags.contains(new Integer(33550)) || // ModelPixelScaleTag
			vTags.contains(new Integer(33922)) || // ModelTiepointTag
			vTags.contains(new Integer(34264)) || // ModelTransformationTag
			vTags.contains(new Integer(34735)) || // GeoKeyDirectoryTag
			vTags.contains(new Integer(34736)) || // GeoDoubleParamsTag
			vTags.contains(new Integer(34737))) { // GeoAsciiParamsTag
				isProfile = true;
			}
		} else if (profile.equals(TIFF_EP)) {
			// TBD
		} else if (profile.equals(TIFF_IT)) {
			// TBD
		} else if (profile.equals(EXIF)) {
			//	note that these tags just indicate that this is an exif,
			// not that it meets the requirements for it!
			// NOT SURE ABOUT THIS ONE
			if (vTags.contains(new Integer(34665)) || // ExifIFD
			vTags.contains(new Integer(40965))) { // InteroperabilityIFD
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_G3G4)) {
			if (ti.getCompression().toString() == Compression.COMP_GROUP_3_FAX
				|| ti.getCompression().toString() == Compression.COMP_GROUP_4_FAX) {
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_LZW)) {
			if (ti.getCompression().toString() == Compression.COMP_LZW) {
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_TILED)) {
			if (vTags.contains(new Integer(322)) && // TileWidth
			vTags.contains(new Integer(323)) && // TileLength
			vTags.contains(new Integer(324)) && // TileOffsets
			vTags.contains(new Integer(325))) { // TileByteCounts
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_CMYK)) {
			if (hasBaselineTags(tags, ti, true)
				&& ti.getColorSpace() == ColorSpace.CS_CMYK
				&& ti.getNumComponents() == 4
				&& ti.getBitsPerSample().size() == 4) {
				couldBeProfile = true;
			}

			if (couldBeProfile) {
				for (int i = 0; i < 4; i++) {
					if (((Integer) ti.getBitsPerSample().elementAt(i)).intValue() != 8) {
						notProfile = true;
					}
				}
			}

			if (couldBeProfile && !notProfile) {
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_YCBCR)) {
			if (hasBaselineTags(tags, ti, true)
				&& vTags.contains(new Integer(532)) && // ReferenceBlackWhite
			ti.getColorSpace() == ColorSpace.CS_YCBCR
				&& ti.getNumComponents() == 3
				&& ti.getBitsPerSample().size() == 3
				&& (ti.getCompression().toString() == Compression.COMP_NONE
					|| ti.getCompression().toString() == Compression.COMP_LZW
					|| ti.getCompression().toString() == Compression.COMP_JPEG)) {
				couldBeProfile = true;
			}

			if (couldBeProfile) {
				for (int i = 0; i < 3; i++) {
					if (((Integer) ti.getBitsPerSample().elementAt(i)).intValue() != 8) {
						notProfile = true;
					}
				}
			}

			if (couldBeProfile && !notProfile) {
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_JPEG)) {
			if (hasBaselineTags(tags, ti, false)
				&& vTags.contains(new Integer(512)) && // JPEGProc
				vTags.contains(new Integer(520)) && // JPEGDCTables
				// check PhotometricInterpretation
				 (ti.getColorSpace() == ColorSpace.CS_WHITEISZERO
						|| ti.getColorSpace() == ColorSpace.CS_BLACKISZERO
						|| ti.getColorSpace() == ColorSpace.CS_RGB
						|| ti.getColorSpace() == ColorSpace.CS_CMYK
						|| ti.getColorSpace() == ColorSpace.CS_YCBCR)
				&& // check SamplesPerPixel
				 (ti.getNumComponents() == 1
						|| ti.getNumComponents() == 3
						|| ti.getNumComponents() == 4)
				&& // check number of BitsPerSample values
				 (ti.getBitsPerSample().size() == 1
						|| ti.getBitsPerSample().size() == 3
						|| ti.getBitsPerSample().size() == 4)
				&& // check Compression
				ti.getCompression().toString() == Compression.COMP_JPEG) {
					couldBeProfile = true;
			}

			if (couldBeProfile) {
				// check BitsPerSample values
				for (int i = 0; i < ti.getBitsPerSample().size(); i++) {
					int val = ((Integer) ti.getBitsPerSample().elementAt(i)).intValue();
					if (val != 8) {
						notProfile = true;
					}
				}
			}

			if (couldBeProfile && !notProfile) {
				isProfile = true;
			}
		} else if (profile.equals(BASELINE_EXT_CIELAB)) {
			if (ti.getColorSpace() == ColorSpace.CS_CIELAB) {
				couldBeProfile = true;
				for (int i = 0; i < ti.getBitsPerSample().size(); i++) {
					if (((Integer) ti.getBitsPerSample().elementAt(i)).intValue() != 8) {
						notProfile = true;
					}
				}
			}

			if (couldBeProfile && !notProfile) {
				isProfile = true;
			}
		} else {
			// leave result as false
		}
		return isProfile;
	}

}
