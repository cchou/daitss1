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
 * Created on Oct 30, 2003
 *
 */
package edu.fcla.daitss.format.image.tiff;


import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.Image;
import edu.fcla.daitss.format.image.Orientation;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.Informer;

/**
 * TiffImage represents an image within a Tiff file.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class TiffImage extends Image {
	
	/**
	 * The maximum supported number of bytes per strip
	 */
	private static final long MAX_NUM_BPS = 4294967295L;
	
	/**
	 * The maximum supported number of bytes per tile
	 */
	private static final long MAX_NUM_BPT = 4294967295L;
	
	/**
	 * The maximum supported number of strips in an image
	 */
	private static final long MAX_NUM_STRIPS = 4294967295L;
	
	/**
	 * The maximum supported number of tiles in an image
	 */
	private static final long MAX_NUM_TILES = 4294967295L;
	
	/**
	 * The maximum supported number of rows per strip in an image
	 */
	private static final long MAX_ROWS_PER_STRIP = 4294967295L;
	
	/**
	 * The maximum supported tile length (height) in an image
	 */
	private static final long MAX_TILE_LENGTH = 4294967295L;
	
	/**
	 * The maximum supported tile width in an image
	 */
	private static final long MAX_TILE_WIDTH = 4294967295L;
	
	/**
	 * The minimum supported number of bytes per strip
	 */
	private static final long MIN_NUM_BPS = 0;
	
	/**
	 * The minimum supported number of bytes per tile
	 */
	private static final long MIN_NUM_BPT = 0;
	
	/**
	 * The minimum supported number of strips in an image
	 */
	private static final long MIN_NUM_STRIPS = 0;
	
	/**
	 * The minimum supported number of tiles in an image
	 */
	private static final long MIN_NUM_TILES = 0;
	
	/**
	 * The minimum supported number of rows per strip in an image
	 */
	private static final long MIN_ROWS_PER_STRIP = 0;
	
	/**
	 * The minimum supported tile length (height) in an image
	 */
	private static final long MIN_TILE_LENGTH = 0;
	
	/**
	 * The minimum supported tile width in an image
	 */
	private static final long MIN_TILE_WIDTH = 0;
	
	/**
	 * All the components of a single pixel are grouped together, followed
	 * by all the components of another pixel, and so on.
	 */
	public static final String PLANAR_CONFIG_CHUNKY = "CHUNKY";
	
	/**
	 * Each type of pixel component is stored together in separate 'component planes'.
	 * For example an RGB image using this storage would store all the red values
	 * together and all the green values together, and so on.
	 */
	public static final String PLANAR_CONFIG_PLANAR = "PLANAR";

	/**
	* Unknown planar configuration
	*/
	public static final String PLANAR_CONFIG_UNKNOWN = "UNKNOWN";
	
	/**
	 * Hundredths of a unit precision
	 * the default for tiffs
	 */
	public static final String PREC_HUND = "HUND";
	
	/**
	 * Hundred Thousandths of a unit precision
	 */
	public static final String PREC_HUND_THOU = "HUND_THOU";
	
	/**
	 * Precision not applicable
	 */
	public static final String PREC_NA = "NA";
	
	/**
	 * Tenths of a unit precision
	 */
	public static final String PREC_TEN = "TEN";
	
	/**
	 * Ten Thousandths of a unit precision
	 */
	public static final String PREC_TEN_THOU = "TEN_THOU";
	
	/**
	 * Thousandths of a unit precision
	 */
	public static final String PREC_THOU = "THOU";
	
	/**
	 * Unknown precision
	 */
	public static final String PREC_UNKNOWN = "UNKNOWN";
		
	/**
	 * Data storage method is in strips
	 */
	public static final String STORAGE_STRIP = "STRIP";

	/**
	 * Data storage method is in tiles
	 */
	public static final String STORAGE_TILE = "TILE";
	
	/**
	 * Data storage method is unknown
	 */
	public static final String STORAGE_UNKNOWN = "UNKNOWN";
	
	/**
	 * First chrominance sample uses the following x, y offsets:
	 * Xoffset[0,0] = (ChromaSubsampleHoriz / 2) - 0.5
	 * Yoffset[0,0] = (ChromaSubsampleVert / 2) - 0.5
	 * This is the default for tiffs.
	 */
	public static final String YCBCR_POSITION_CENTER = "CENTER";
	
	/**
	 * First chrominance sample uses the following x, y offsets:
	 * Xoffset[0,0] = 0
	 * Yoffset[0,0] = 0
	 */
	public static final String YCBCR_POSITION_COSITE = "COSITE";
	
	/**
	 * First chrominance sample uses the following x, y offsets:
	 * Xoffset[0,0] = 0
	 * Yoffset[0,0] = 0
	 */
	public static final String YCBCR_POSITION_NA = "NA";
	
	/**
	 * First chrominance sample uses the following x, y offsets:
	 * Xoffset[0,0] = 0
	 * Yoffset[0,0] = 0
	 */
	public static final String YCBCR_POSITION_UNKNOWN = "UNKNOWN";
	
	/**
	 * <code>pixelsHorizontal</code> (or <code>pixelsVertical</code>) is equal 
	 * to the <code>pixelsHorizontal</code> (or <code>pixelsVertical</code>)
	 * of the associated luma image.
	 */
	public static final String YCBCR_SUBSAMPLE_EQUAL = "EQUAL";
	
	/**
	 * <code>pixelsHorizontal</code> (or <code>pixelsVertical</code>) is one fourth 
	 * of the <code>pixelsHorizontal</code> (or <code>pixelsVertical</code>)
	 * of the associated luma image.
	 */
	public static final String YCBCR_SUBSAMPLE_FOURTH = "FOURTH";
	
	/**
	 * <code>pixelsHorizontal</code> (or <code>pixelsVertical</code>) is half 
	 * the <code>pixelsHorizontal</code> (or <code>pixelsVertical</code>)
	 * of the associated luma image.
	 */
	public static final String YCBCR_SUBSAMPLE_HALF = "HALF";
	
	/**
	 * The blue [x] value for the chromaticities of the primary colors of the imaging process. 
	 * Based on the 1931 CIE xy chromaticity diagram, used for device-independent color 
	 * calibration. No default for tiffs.
	 */
	private float chromaticitiesPrimaryBlueX = -1.0f;

	/**
	 * The blue [y] value for the chromaticities of the primary colors of the imaging process. 
	 * Based on the 1931 CIE xy chromaticity diagram, used for device-independent color 
	 * calibration. No default for tiffs.
	 */
	private float chromaticitiesPrimaryBlueY = -1.0f;

	/**
	 * The green [x] value for the chromaticities of the primary colors of the imaging process. 
	 * Based on the 1931 CIE xy chromaticity diagram, used for device-independent color 
	 * calibration. No default for tiffs.
	 */
	private float chromaticitiesPrimaryGreenX = -1.0f;

	/**
	 * The green [y] value for the chromaticities of the primary colors of the imaging process. 
	 * Based on the 1931 CIE xy chromaticity diagram, used for device-independent color 
	 * calibration. No default for tiffs.
	 */
	private float chromaticitiesPrimaryGreenY = -1.0f;

	/**
	 * The red [x] value for the chromaticities of the primary colors of the imaging process. 
	 * Based on the 1931 CIE xy chromaticity diagram, used for device-independent color 
	 * calibration. No default for tiffs.
	 */
	private float chromaticitiesPrimaryRedX = -1.0f;

	/**
	 * The red [y] value for the chromaticities of the primary colors of the imaging process. 
	 * Based on the 1931 CIE xy chromaticity diagram, used for device-independent color 
	 * calibration. No default for tiffs.
	 */
	private float chromaticitiesPrimaryRedY = -1.0f;

	/**
	 * The x value of the white point chromaticity, representing the effective illumination 
	 * source. Based on the 1931 CIE xy chromaticity diagram. This value can correspond 
	 * to the chromaticity of the alignment white of a monitor, the filter set and the light 
	 * source combination of a scanner or the imaging model of a rendering package. 
	 * No default for tiffs.
	 */
	private float chromaticitiesWhitePointX = -1.0f;

	/**
	 * The y value of the white point chromaticity, representing the effective illumination 
	 * source. Based on the 1931 CIE xy chromaticity diagram. This value can correspond 
	 * to the chromaticity of the alignment white of a monitor, the filter set and the light 
	 * source combination of a scanner or the imaging model of a rendering package.
	 * No default for tiffs.
	 */
	private float chromaticitiesWhitePointY = -1.0f;
	
	/**
	 * The optical density of each possible pixel value. Only applicable to 
	 * grayscale images. The number of elements in this list is equal to
	 * 2^bitsPerSample, where bitsPerSample is one of the numbers in the
	 * <code>bitsPerSample</code> member, where its assumed that 
	 * all the numbers are the same (ex: {8,8,8}). The 0th value in this
	 * member corresponds to the optical density of a pixel having the 
	 * value 0, and so on. The values can not be correctly interpreted
	 * without the <code>grayResponseUnit</code> member.
	 * Contains Integer objects.
	 */
	private Vector grayResponseCurve = null;

	/**
	 * The precision of the information in the <code>grayResponseCurve</code> 
	 * member. For example, if this member is set to 4 and the 5th 
	 * <code>grayResponseCurve</code> number is 3455, then the resulting actual number
	 * is .3455
	 * 
	 * Valid values are <code>PREC_HUND</code>,  
	 * <code>PREC_HUND_THOU</code>, <code>PREC_TEN</code>,
	 * <code>PREC_TEN_THOU</code>, <code>PREC_THOU</code>,
	 * <code>PREC_UNKNOWN</code>, or
	 * <code>PREC_NA</code> (when this isn't set yet or isn't applicable).
	 */
	private String grayResponseUnit = PREC_NA;
	
	/**
	 * Whether or not this image specifies chromaticities.
	 */
	private String hasChromaticities = Bitstream.UNKNOWN;
	
	/**
	 * The Tiff tags contained in this image
	 */
	private int[] imageTags = null;
	
	/**
	 * Maximum number of bytes in a strip in this image.
	 * If this is -1 it means that it is not known (convert to null in the database).
	 * The maximum value is <code>MAX_NUM_BPS</code>.
	 */
	private long maxStripBytes = -1;
	
	/**
	 * Maximum number of bytes in a tile in this image
	 * If this is -1 it means that it is not known (convert to null in the database).
	 * The maximum value is <code>MAX_NUM_BPT</code>.
	 */
	private long maxTileBytes = -1;
	
	/**
	 * The number of strips in the image, if this is a stripped image. Not
	 * a tiff field, must be inferred.
	 * If this is -1 it means that it is not known (convert to null in the database).
	 * The maximum value is <code>MAX_NUM_STRIPS</code>.
	 */
	private long numStrips = -1;

	/**
	 * The number of tiles in the tiled image. Not a Tiff field, must be inferred
	 * from other fields like so:
	 * tilesAcross = (pixelsHorizontal + tileWidth - 1) / tileWidth
	 * tilesDown = (pixelsVertical + tileLength - 1) / tileLength
	 * numTiles = tilesAcross * tilesDown
	 * 
	 * -1 means it is not known how many tiles there are (convert to null in the database)
	 * The maximum value is <code>MAX_NUM_TILES</code>.
	 */
	private long numTiles = -1;
	
	/**
	 * How the pixel components are ordered in the image. Valid values
	 * are <code>PLANAR_CONFIG_CHUNKY</code>, 
	 * <code>PLANAR_CONFIG_PLANAR</code> and 
	 * <code>PLANAR_CONFIG_UNKNOWN</code> 
	 * (when this hasn't been set yet).
	 */
	private String planarConfig = PLANAR_CONFIG_UNKNOWN;
    
	/**
	 * A pair of 'headroom' and 'footroom' image data values for each pixel
	 * component. The first component code within a pair is associated with 
	 * black, the second with white. The ordering of the pairs is the same
	 * as with the components of the image's <code>colorSpace</code>
	 * type. This member is applicable to images with <code>colorSpace</code>
	 * of <code>COLOR_SPACE_RGB</code> or 
	 * <code>COLOR_SPACE_YCBCR</code>. Contains Long objects.
	 */
	private Vector referenceBlackWhite = null;
	
	/**
	 * Number of rows per strip (except possibly the last strip which could have less).
	 * Tag 278 in a tiff image.
	 * If this is -1 it means that it is not known (convert to null in the database).
	 * The maximum value is <code>MAX_ROWS_PER_STRIP</code>.
	 */
	private long rowsPerStrip = -1;
	
	/**
	 * Specifies whether the image is stored in tiles or strips.
	 * Valid values include <code>STORAGE_STRIP</code>,
	 * <code>STORAGE_TILE</code>, and
	 * <code>STORAGE_UNKNOWN</code>.
	 * Not an explicit Tiff tag - must be inferred.
	 */
	private String storageSegment = STORAGE_UNKNOWN;

	/**
	 * For each strip, the number of bytes in the strip, after any compression.
	 * The length of the array should be <code>numStrips</code>.
	 * Tag 279 in a tiff image. Contains Long objects.
	 */
	private Vector stripByteCounts = null;

	/**
	 * For each strip, the byte offset to the strip. Tag 273 in a tiff image.
	 * The length of the array should be <code>numStrips</code>.
	 * Contatins Long objects.
	 */
	private Vector stripOffsets = null;

	/**
	 * For each tile, the number of (compressed) bytes in that tile. The 
	 * length of the array should be <code>numTiles</code>.
	 * Tag 325 in a tiff image. Holds Long objects.
	 */
	private Vector tileByteCounts = null;
	
	/**
	 * Tile length (height) in pixels. This is the number of rows in each tile.
	 * The bottom-most tile(s) may be padded. Tile length must be a multiple
	 * of 16 for compatibility with compression scemes like JPEG. Replaces
	 * stripRows in tiled tiff images. Tile height can be larger than 
	 * <code>pixelsVertical</code>. Tag 323 in a tiff image. 
	 * If this is -1 it means that it is not known (convert to null in the database).
	 * The maximum value is <code>MAX_TILE_LENGTH</code>.
	 */
	private long tileLength = -1;

	/**
	 * For each tile, the byte offset of that tile, relative to the beginning of the
	 * file. Each tile has a location in the file independent of the other tiles.
	 * Offsets are ordered left-to-right and top-to-bottom. For
	 * <code>planarConfig</code> = <code>PLANAR_CONFIG_PLANAR</code>,
	 * the offsets for the first component plane are stored first, followed by
	 * all the offsets of the second component plane, and so on. The 
	 * length of the array should be <code>numTiles</code>.
	 * Tag 324 in a tiff image. Contains Long objects.
	 */
	private Vector tileOffsets = null;

	/**
	 * The tile width in pixels (number of columns). All tiles have the same width. 
	 * The right-most tile may have padding. Tile width must be a multiple of 16.
	 * Tile width can be larger than <code>pixelsHorizontal</code>.
	 * Tag 322 in a tiff image. No default for tiffs.
	 * If this is -1 it means that it is not known (convert to null in the database).
	 * The maximum value is <code>MAX_TILE_WIDTH</code>.
	 */
	private long tileWidth = -1;
	
	/**
	 * Coefficients needed to transform the image data from the YCbCr color
	 * space to the RGB color space. This is a list of three elements: LumaRed,
	 * LumaGreen, and LumaBlue (in that order). They are the proportions of
	 * red, green and blue, respectively, in Y (the luminance component).
	 * 
	 * Y, Cb and Cr may be computed from these coefficients as follows:
	 * Y = (LumaRed*R + LumaGreen*G + LumaBlue*B)
	 * Cb = (B-Y) / (2-(2*LumaBlue))
	 * Cr = (R-Y) / (2-(2*LumaRed))
	 * 
	 * R, G, and B can be computed from YCbCr as follows:
	 * R = Cr*(2-(2*LumaRed)) +Y
	 * G = (Y-(LumaBlue*B)-(LumaRed*R)) / LumaGreen
	 * B = Cb*(2-(2*LumaBlue)) + Y
	 * 
	 * For tiff images in YCbCr color space, the default values for these coefficients
	 * are defined by CCIR Recommendation 601-1: 299/1000, 587/1000, 114/1000,
	 * for LumaRed, LumaGreen and LumaBlue, respectively.
	 * Contains Float objects.
	 */
	private Vector ycbcrCoefficients = null;

	/**
	 * The positioning of subsampled chrominance components relative to luminance
	 * samples. Because components must be sampled orthogonally (along rows and
	 * columns), the spatial position of the samples in a given subsampled component
	 * may be determined by specifying the horizontal and vertical offsets of the first
	 * sample (i.e. the sample in the upper-left corner) with respect to the luminance
	 * component. The horizontal and vertical offsets of the first chrominance sample
	 * are denoted Xoffset[0,0] and Yoffset[0,0] respectively. Xoffset[0,0] and Yoffset[0,0]
	 * are defined in terms of the number of samples in the luminance component.
	 * The valid values for this field are <code>YCBCR_POSITION_CENTER</code>,
	 * <code>YCBCR_POSITION_COSITE</code>, or <code>0</code> (when
	 * this is not set yet or not applicable).
	 * 
	 * For tiff images in the YCbCr color space the default for this is 
	 * <code>YCBCR_POSITION_CENTER</code>.
	 * 
	 * NOTE: If the default value for this member (0) ever changes, changes will 
	 * need to be made in subclasses of this class. For example, TiffFile.calcYCbCrFields(TiffFile)
	 * checks this value to see if this member has already been set.
	 * 
	 */
	private String ycbcrPositioning = YCBCR_POSITION_NA;

	/**
	 * Specifies the subsampling factors used for the chrominance components
	 * of an image in YCbCr color space. The two elements in this list, 
	 * YCbCrSubsampleHoriz and YCbCrSubsampleVert, specify the horizontal
	 * and vertical subsampling factors, respectively. Valid values for
	 * YCbCrSubsampleHoriz and YCbCrSubsampleVert are 
	 * <code>YCBCR_SUBSAMPLE_EQUAL</code>, 
	 * <code>YCBCR_SUBSAMPLE_HALF</code>, or
	 * <code>YCBCR_SUBSAMPLE_FOURTH</code>.
	 *  
	 * Both Cb and Cr have the same subsampling ratio. YCbCrSubsampleVert
	 * must always be less than or equal to YCbCrSubsampleHoriz. 
	 * <code>pixelsHorizontal</code> and <code>pixelsVertical</code> are
	 * constrained to be integer multiples of YCbCrSubsampleHoriz and
	 * YCbCrSubsampleVert. Same with tileWidth and tileHeight. rowsPerStrip
	 * must be an integer multiple of YCbCrSubsampleVert.
	 * 
	 * The default values of this field for tiff images in YCbCr color space are
	 * {<code>YCBCR_SUBSAMPLE_HALF</code>, 
	 * <code>YCBCR_SUBSAMPLE_HALF</code>}
	 * Contains String objects.
	 */
	private Vector ycbcrSubsampling;

	/**
	 * 
	 * @param df the file containing this Tiff bitstream
	 * @throws FatalException
	 */
	public TiffImage(DataFile df) throws FatalException {
		super(df);
		this.setBsImageTable(ArchiveDatabase.TABLE_BS_IMAGE_TIFF);
		this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		
		// construct the required tiff-specific data structures
		
		this.grayResponseCurve = new Vector(); // not needed by tiffs
		this.referenceBlackWhite = new Vector(); // not needed by tiffs
		this.stripByteCounts = new Vector();
		this.stripOffsets = new Vector();
		this.tileByteCounts = new Vector();
		this.tileOffsets = new Vector();
		this.ycbcrCoefficients = new Vector(); // not needed by tiffs
		this.ycbcrSubsampling = new Vector(); // not needed by tiffs
		
		// set Tiff image defaults
		Vector bps = new Vector(1);
		bps.add(new Integer(1));
		this.setBitsPerSample(bps);
		// Compression - default is {Compression.NONE}
		compression.setCompression(Compression.COMP_NONE);
		// GrayResponseUnit - default is RasterImageFile.PREC_HUND
		this.setGrayResponseUnit(TiffImage.PREC_HUND);
		// Orientation - default is 1(Orientation.TOP_LEFT)
		this.setOrientation(Orientation.TOP_LEFT);
		// PlanarConfiguration - default is 1(RasterImageFile.PLANAR_CONFIG_CHUNKY)
		this.setPlanarConfig(PLANAR_CONFIG_CHUNKY);
		// ResolutionUnit - default is 2(RasterImageFile.RES_UNIT_INCH)
		this.setResUnit(Image.RES_UNIT_INCH);
		// SamplesPerPixel - default is 1
		this.setNumComponents(1); 
	}
	
	/**
	 * Determines whether or not this image has chromaticities stored in it.
	 * If the chromaticiticities had been encountered when parsing the tiff,
	 * then <code>hasChromaticities</code> would be TRUE. If this is
	 * not the case then this value should be FALSE.
	 * 
	 * @throws FatalException
	 */
	private void calcHasChromaticities() throws FatalException {
		if (!this.hasChromaticities().equals(Bitstream.TRUE)){
			this.setHasChromaticities(Bitstream.FALSE);
		}
	}
	
	/**
	 * Determines whether or not this image has an ICC Profile stored in it.
	 * If the ICC Profile had been encountered when parsing the tiff,
	 * then <code>hasICCProfile</code> would be TRUE. If this is
	 * not the case then this value should be FALSE.
	 * 
	 * @throws FatalException
	 */
	private void calcHasIccProfile() throws FatalException {
		if (!this.hasIccProfile().equals(Bitstream.TRUE)){
			this.setHasIccProfile(Bitstream.FALSE);
		}
	}
	
	/**
	 * Determines whether or not this image has a palette stored in it.
	 * If the table had been encountered when parsing the tiff,
	 * then <code>hasIntPalette</code> would be TRUE. If this is
	 * not the case then this value should be FALSE.
	 * 
	 * @throws FatalException
	 */
	private void calcHasIntPalette() throws FatalException {
		if (!this.hasIntPalette().equals(Bitstream.TRUE)){
				this.setHasIntPalette(Bitstream.FALSE);
		}
	}
	
	/**
	 * Examines this image's list of StripByteCounts and sets the
	 * <code>maxStripBytes</code> to the maximum in the list.
	 * If there are no values in the list this method does nothing.
	 * 
	 * @throws FatalException
	 */
	private void calcMaxStripByteCount() throws FatalException {
		Vector counts = this.getStripByteCounts();
		
		// start the max at whatever the default for 
		// <code>maxStripBytes</code> is
		long maxCount = this.getMaxStripBytes();
		
		if (counts.size() > 0) {
			Iterator countIter = counts.iterator();
			while (countIter.hasNext()){
				long value = ((Long) countIter.next()).longValue();
				if (value > maxCount) {
					maxCount = value;
				}
			}
			this.setMaxStripBytes(maxCount);
		}
		
	}
	
	/**
	 * Examines this image's list of TileByteCounts and sets the
	 * <code>maxTileBytes</code> to the maximum in the list.
	 * If there are no values in the list this method does nothing.
	 * 
	 * @throws FatalException
	 */
	private void calcMaxTileByteCount() throws FatalException {
		Vector counts = this.getTileByteCounts();
		
		// start the max at whatever the default for 
		// <code>maxTileBytes</code> is
		long maxCount = this.getMaxTileBytes();
		
		if (counts.size() > 0) {
			Iterator countIter = counts.iterator();
			while (countIter.hasNext()){
				long value = ((Long) countIter.next()).longValue();
				if (value > maxCount) {
					maxCount = value;
				}
			}
			this.setMaxTileBytes(maxCount);
		}
	}
	
	/**
	 * Calculates and sets the default ReferenceBlackWhite value
	 * for this image, if it applies to this image, and if it hasn't already been 
	 * set
	 * 
	 * @throws FatalException
	 */
	private void calcReferenceBlackWhite() throws FatalException {
		// the Tiff ReferenceBlackWhite field is only relevant to tiff
		// images with PhotometricInterpretation (aka color space)
		// of RGB or YCbCr
		if (this.getColorSpace() == ColorSpace.CS_RGB ||
			this.getColorSpace() == ColorSpace.CS_YCBCR){
				if (this.getReferenceBlackWhite().size() == 0){
					// the image relied on the default value instead of setting this
					if (this.getBitsPerSample().size() > 0) {
						int bpsVal = ((Integer)this.getBitsPerSample().elementAt(0)).intValue();
						long nv = ((long) Math.pow(2, bpsVal)) -1L;
						Vector tempV = new Vector(6);
						for (int i=0; i<3; i++) {
							tempV.add(new Long(0));
							tempV.add(new Long(nv));
						}
						this.setReferenceBlackWhite(tempV);
					} else {
						// can't set this
					}
				}
			}
	}
	
	/**
	 * Calculates and sets the YCbCr-related fields to their default values 
	 * if this image is in the YCbCr color space and these fields are not set 
	 * in the image. See Section 21: YCbCr Images (Tiff 6.0 spec)
	 * 
	 * @throws FatalException
	 * 
	 */
	private void calcYcbcrFields() throws FatalException {
		if (this.getColorSpace() == ColorSpace.CS_YCBCR){
			// YCbCrCoefficients
			if (this.getYcbcrCoefficients().size() != 3){
				Vector tempV = new Vector(3);
				tempV.add(new Float(299.0f/1000.0f));
				tempV.add(new Float(587.0f/1000.0f));
				tempV.add(new Float(114.0f/1000.0f));
				this.setYcbcrCoefficients(tempV);
			}
			// YCbCrSubSampling
			if (this.getYcbcrSubsampling().size() != 2){
				// use the default values
				Vector tempV = new Vector(2);
				tempV.add(new Integer(TiffImage.YCBCR_SUBSAMPLE_HALF));
				tempV.add(new Integer(TiffImage.YCBCR_SUBSAMPLE_HALF));
				this.setYcbcrSubsampling(tempV);
				this.setSamplingHor("2:1:1"); 
				this.setSamplingVer("2:1:1");
			} 
			// YCbCrPositioning
			if (this.getYcbcrPositioning().equals(TiffImage.YCBCR_POSITION_NA)){
				this.setYcbcrPositioning(TiffImage.YCBCR_POSITION_CENTER);
			}
		}
	}
	
	/**
	 * fill in the database column-value pairs for this image
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();

		columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_HAS_CHROMS);
		values.add(this.hasChromaticities());	
		columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_PLANAR_CONFIG);
		values.add(this.getPlanarConfig());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_STOR_SEGMENT);
		values.add(this.getStorageSegment());
		
	    // the following data are all default to -1 in code and thus shall not be inserted into the 
		// data if it's missing.
		
		if (this.getMaxStripBytes() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_MAX_STRIP_BYTES);
			values.add(new Long(this.getMaxStripBytes()));
		}
	
		if (this.getRowsPerStrip() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_ROWS_PER_STRIP);
			values.add(new Long(this.getRowsPerStrip()));
		}
		if (this.getNumStrips() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_NUM_STRIPS);
			values.add(new Long(this.getNumStrips()));
		}
		
		if (this.getMaxTileBytes() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_MAX_TILE_BYTES);
			values.add(new Long(this.getMaxTileBytes()));
		}
		
		if (this.getNumTiles() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_NUM_TILES);
			values.add(new Long(this.getNumTiles()));
		}
		if (this.getTileLength() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_TILE_LENGTH);
			values.add(new Long(this.getTileLength()));
		}
		
		if (this.getTileWidth() >= 0) {
			columns.add(ArchiveDatabase.COL_BS_IMAGE_TIFF_TILE_WIDTH);
			values.add(new Long(this.getTileWidth()));
		}
	}
		
	/**
	 * Insert this image into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_IMAGE_TIFF, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this image
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		super.dbUpdate();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BS_IMAGE_TIFF_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_IMAGE_TIFF, columns, colValues, whereClause);
	}
	
	public Document toXML() throws FatalException {
	    /* Daitss Document */
	    Document doc = super.toXML();
	    
	    /* Daitss Element */
	    Element rootElement = doc.getDocumentElement();
	    
	    /* Daitss Namespace */
	    String namespace = rootElement.getNamespaceURI();
	    
	    /* Bs Image Tiff */
	    Element bsImageTiffElement = doc.createElementNS(namespace, "BS_IMAGE_TIFF");
	    rootElement.appendChild(bsImageTiffElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsImageTiffElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsImageTiffElement.appendChild(bsidElement);

	    /* Has Chromaticities */
	    Element hasChromaticitiesElement = doc.createElementNS(namespace, "HAS_CHROMATICITIES");
	    String hasChromaticitiesValue = this.hasChromaticities();
	    Text hasChromaticitiesText = doc.createTextNode(hasChromaticitiesValue);
	    hasChromaticitiesElement.appendChild(hasChromaticitiesText);
	    bsImageTiffElement.appendChild(hasChromaticitiesElement);
	    
	    // the following data are all default to -1 in code and thus shall not be inserted into the XML if
	    // it's missing.
	    /* Num Strips */
	    if (this.getNumStrips() >= 0) {
		    Element numStripsElement = doc.createElementNS(namespace, "NUM_STRIPS");
		    String numStripsValue = Long.toString(this.getNumStrips());
		    Text numStripsText = doc.createTextNode(numStripsValue);
		    numStripsElement.appendChild(numStripsText);
		    bsImageTiffElement.appendChild(numStripsElement);
	    }
	    
	    /* Num Tiles */
	    if (this.getNumTiles() >= 0) {
		    Element numTilesElement = doc.createElementNS(namespace, "NUM_TILES");
		    String numTilesValue = Long.toString(this.getNumTiles());
		    Text numTilesText = doc.createTextNode(numTilesValue);
		    numTilesElement.appendChild(numTilesText);
		    bsImageTiffElement.appendChild(numTilesElement);
	    }
	    
	    /* Planar Config */
	    Element planarConfigElement = doc.createElementNS(namespace, "PLANAR_CONFIG");
	    String planarConfigValue = (this.getPlanarConfig() != null ? this.getPlanarConfig() : "" );
	    Text planarConfigText = doc.createTextNode(planarConfigValue);
	    planarConfigElement.appendChild(planarConfigText);
	    bsImageTiffElement.appendChild(planarConfigElement);

	    /* Storage Segment */
	    Element storageSegmentElement = doc.createElementNS(namespace, "STORAGE_SEGMENT");
	    String storageSegmentValue = (this.getStorageSegment() != null ? this.getStorageSegment() : "" );
	    Text storageSegmentText = doc.createTextNode(storageSegmentValue);
	    storageSegmentElement.appendChild(storageSegmentText);
	    bsImageTiffElement.appendChild(storageSegmentElement);


	    /* Max Strip Bytes */
	    if (this.getMaxStripBytes() >= 0) {
		    Element maxStripBytesElement = doc.createElementNS(namespace, "MAX_STRIP_BYTES");
		    String maxStripBytesValue = Long.toString(this.getMaxStripBytes());
		    Text maxStripBytesText = doc.createTextNode(maxStripBytesValue);
		    maxStripBytesElement.appendChild(maxStripBytesText);
		    bsImageTiffElement.appendChild(maxStripBytesElement);
	    }
	    
	    /* Rows Per Strip */
	    if (this.getRowsPerStrip() >=0) {
		    Element rowsPerStripElement = doc.createElementNS(namespace, "ROWS_PER_STRIP");
		    String rowsPerStripValue = Long.toString(this.getRowsPerStrip());
		    Text rowsPerStripText = doc.createTextNode(rowsPerStripValue);
		    rowsPerStripElement.appendChild(rowsPerStripText);
		    bsImageTiffElement.appendChild(rowsPerStripElement);
	    }
	    
	    /* Max Tile Bytes */
	    if (this.getMaxTileBytes() >= 0) {
		    Element maxTileBytesElement = doc.createElementNS(namespace, "MAX_TILE_BYTES");
		    String maxTileBytesValue = Long.toString(this.getMaxTileBytes());
		    Text maxTileBytesText = doc.createTextNode(maxTileBytesValue);
		    maxTileBytesElement.appendChild(maxTileBytesText);
		    bsImageTiffElement.appendChild(maxTileBytesElement);
	    }
	    
	    /* Tile Length */
	    if (this.getTileLength() >= 0) {
		    Element tileLengthElement = doc.createElementNS(namespace, "TILE_LENGTH");
		    String tileLengthValue = Long.toString(this.getTileLength());
		    Text tileLengthText = doc.createTextNode(tileLengthValue);
		    tileLengthElement.appendChild(tileLengthText);
		    bsImageTiffElement.appendChild(tileLengthElement);
	    }
	    
	    /* Tile Width */
	    if (this.getTileWidth() >= 0) { 
		    Element tileWidthElement = doc.createElementNS(namespace, "TILE_WIDTH");
		    String tileWidthValue = Long.toString(this.getTileWidth());
		    Text tileWidthText = doc.createTextNode(tileWidthValue);
		    tileWidthElement.appendChild(tileWidthText);
		    bsImageTiffElement.appendChild(tileWidthElement);
	    }
	    
	    return doc;
	}
	
	/**
	 * Sets any instance members that can only be set after the parsing of the
	 * file. For instance the value of some members are not explicitly found in
	 * the file by parsing - they have to be inferred or calculated.
	 * 
	 * @throws FatalException
	 */
	public void evalMembers() throws FatalException {
		this.calcHasChromaticities();
		this.calcHasIccProfile();
		this.calcHasIntPalette();
		if (this.getStorageSegment().equals(STORAGE_STRIP)){
			this.calcMaxStripByteCount();
		} else if (this.getStorageSegment().equals(STORAGE_TILE)){
			this.calcMaxTileByteCount();
		}
		this.calcReferenceBlackWhite();
		this.calcYcbcrFields();
	}
	
	/**
	 * Returns the blue [x] value for the chromaticities of the primary colors of the imaging process 
	 * 
	 * @return	the blue [x] value for the chromaticities of the primary colors of the imaging process
	 */
	public float getChromaticitiesPrimaryBlueX() {
		return this.chromaticitiesPrimaryBlueX;
	}         

	/**
	 * Returns the blue [y] value for the chromaticities of the primary colors of the imaging process 
	 * 
	 * @return	the blue [y] value for the chromaticities of the primary colors of the imaging process
	 */
	public float getChromaticitiesPrimaryBlueY() {
		return this.chromaticitiesPrimaryBlueY;
	}        

	/**
	 * Returns the green [x] value for the chromaticities of the primary colors of the imaging process 
	 * 
	 * @return	the green [x] value for the chromaticities of the primary colors of the imaging process
	 */
	public float getChromaticitiesPrimaryGreenX() {
		return this.chromaticitiesPrimaryGreenX;
	}        

	/**
	 * Returns the green [y] value for the chromaticities of the primary colors of the imaging process 
	 * 
	 * @return	the green [y] value for the chromaticities of the primary colors of the imaging process
	 */
	public float getChromaticitiesPrimaryGreenY() {
		return this.chromaticitiesPrimaryGreenY;
	}         

	/**
	 * Returns the red [x] value for the chromaticities of the primary colors of the imaging process 
	 * 
	 * @return	the red [x] value for the chromaticities of the primary colors of the imaging process
	 */
	public float getChromaticitiesPrimaryRedX() {
		return this.chromaticitiesPrimaryRedX;
	}        

	/**
	 * Returns the red [y] value for the chromaticities of the primary colors of the imaging process 
	 * 
	 * @return	the red [y] value for the chromaticities of the primary colors of the imaging process
	 */
	public float getChromaticitiesPrimaryRedY() {
		return this.chromaticitiesPrimaryRedY;
	}         

	/**
	 * Returns the x value of the white point chromaticity. 
	 * 
	 * @return	the x value of the white point chromaticity
	 */
	public float getChromaticitiesWhitePointX() {
		return this.chromaticitiesWhitePointX;
	}        

	/**
	 * Returns the y value of the white point chromaticity.
	 * 
	 * @return	the y value of the white point chromaticity
	 */
	public float getChromaticitiesWhitePointY() {
		return this.chromaticitiesWhitePointY;
	} 
	
	/**
	 * Returns the optical density of each possible pixel value. Only applicable to 
	 * grayscale images. 
	 * 
	 * @return	the list of optical densities of each possible pixel value
	 */
	public Vector getGrayResponseCurve() {
		return this.grayResponseCurve;
	} // end getGrayResponseCurve        

	/**
	 * Returns the precision of the information in the 
	 * <code>grayResponseCurve</code> member.
	 * 
	 * @return	the precision of the information in the 
	 * 				<code>grayResponseCurve</code> member.
	 * @see TiffImage#grayResponseCurve
	 */
	public String getGrayResponseUnit() {
		return this.grayResponseUnit;
	}   

	/**
	 * @return all the tag numbers used in this image
	 */
	public int[] getImageTags() {
		return this.imageTags;
	}

	/**
	 * @return the max number of bytes in a strip in this image
	 */
	public long getMaxStripBytes() {
		return this.maxStripBytes;
	}

	/**
	 * @return the max number of bytes in a tile in this image
	 */
	public long getMaxTileBytes() {
		return this.maxTileBytes;
	}

	/**
	 * Returns the number of strips in the image
	 * 
	 * @return	the number of strips in the image
	 */
	public long getNumStrips() {
		return this.numStrips;
	}

	/**
	 * Returns the number of tiles in the tiled image.
	 * 
	 * @return the number of tiles in the tiled image
	 */
	public long getNumTiles() {
		return this.numTiles;
	}

	/**
	 * Returns how the pixel components are ordered in the file.
	 * 
	 * @return	how the pixel components are ordered in the file
	 */
	public String getPlanarConfig() {
		return this.planarConfig;
	}
	
	/**
	 * Returns a pair of 'headroom' and 'footroom' image data values for each pixel
	 * component.
	 * 
	 * @return	a pair of 'headroom' and 'footroom' image data values for each pixel
	 * 				component
	 */
	public Vector getReferenceBlackWhite() {
		return this.referenceBlackWhite;
	}  

	/**
	 * Returns the number of rows per strip 
	 * (except possibly the last strip which could have less).
	 * 
	 * @return	the number of rows per strip (except possibly the 
	 * 				last strip which could have less)
	 */
	public long getRowsPerStrip() {
		return this.rowsPerStrip;
	}         
	
	/**
	 * Specifies whether the image is stored in tiles or strips.
	 * 
	 * @return whether the image is stored in tiles or strips
	 */
	public String getStorageSegment() {
		return this.storageSegment;
	}        

	/**
	 * Returns for each strip, the number of bytes in the strip, after any compression.
	 * 
	 * @return	the number of bytes in the strip, for each strip
	 */
	public Vector getStripByteCounts() {
		return this.stripByteCounts;
	}        

	/**
	 * Returns for each strip, the byte offset to the strip.
	 * 
	 * @return	the byte offset to the strip, for each strip
	 */
	public Vector getStripOffsets() {
		return this.stripOffsets;
	}        

	/**
	 * Returns the number of (compressed) bytes in that tile, for each tile.
	 * 
	 * @return the number of bytes in that tile, for each tile
	 */
	public Vector getTileByteCounts() {
		return this.tileByteCounts;
	}         

	/**
	 * Returns the tile length (height) in pixels.
	 * 
	 * @return the tile length (height) in pixels
	 */
	public long getTileLength() {
		return this.tileLength;
	}         

	/**
	 * Returns the byte offset of that tile, for each tile, relative to the beginning of the
	 * file.
	 * 
	 * @return the byte offset of each tile relative to the beginning of the file
	 */
	public Vector getTileOffsets() {
		return this.tileOffsets;
	}       

	/**
	 * Returns the tile width in pixels (number of columns).
	 * 
	 * @return the tile width in pixels
	 */
	public long getTileWidth() {
		return this.tileWidth;
	}         
	
	/**
	 * Returns the coefficients needed to transform the image data from the YCbCr color
	 * space to the RGB color space.
	 * 
	 * @return	the coefficients needed to transform the image data from YCbCr to
	 * 				RGB color space
	 */
	public Vector getYcbcrCoefficients() {
		return this.ycbcrCoefficients;
	}         

	/**
	 * Returns the positioning of subsampled chrominance components relative to luminance
	 * samples.
	 * 
	 * @return	the positioning of subsampled chrominance components relative to luminance
	 * 				samples
	 */
	public String getYcbcrPositioning() {
		return this.ycbcrPositioning;
	}        

	/**
	 * Returns the subsampling factors used for the chrominance components
	 * of an image in YCbCr color space.
	 * 
	 * @return	the subsampling factors used for the chrominance components
	 * 				of an image in YCbCr color space
	 */
	public Vector getYcbcrSubsampling() {
		return this.ycbcrSubsampling;
	}   
	
	/**
	 * Returns whether or not this image specifies chromaticities.
	 * 
	 * @return whether or not this image specifies chromaticities
	 */
	public String hasChromaticities() {
		return this.hasChromaticities;
	}
	
	/**
	 * 
	 * @param count
	 */
	public void initTags(int count) {
		if (count > 0) {
			this.imageTags = new int[count];
		}
	}
	
	/**
	 * Determines whether or not a YcbcrPositioning value is valid.
	 * 
	 * @param position	a YcbcrPositioning value
	 * @return	<code>true</code> if its valid, else <code>false</code>
	 */
	public boolean isValidYcbcrPositioning(String position){
		if (position.equals(YCBCR_POSITION_CENTER) ||
			position.equals(YCBCR_POSITION_COSITE) ||
			position.equals(YCBCR_POSITION_NA) ||
			position.equals(YCBCR_POSITION_UNKNOWN)){
				return true;
		}
		return false;
	}
	
	/**
	 * Determines whether or not a list of YcbcrSubsampling values is valid.
	 * 
	 * TODO: add restriction that the horiz (1st value) must be >= the vert (2nd value) for tiffs
	 * @param samples	a list of YcbcrSubsampling values
	 * @return	<code>true</code> if its valid, else <code>false</code>
	 */
	public boolean isValidYcbcrSubsampling(Vector samples){
		if (samples == null ||
			samples.size() != 2 || 
			//((Integer)samples.elementAt(1)).intValue() >((Integer) samples.elementAt(0)).intValue() ||
			(!samples.elementAt(0).equals(YCBCR_SUBSAMPLE_EQUAL) &&
						!samples.elementAt(0).equals(YCBCR_SUBSAMPLE_FOURTH) &&
						!samples.elementAt(0).equals(YCBCR_SUBSAMPLE_HALF)) ||
			(!samples.elementAt(1).equals(YCBCR_SUBSAMPLE_EQUAL) &&
						!samples.elementAt(1).equals(YCBCR_SUBSAMPLE_FOURTH) &&
						!samples.elementAt(1).equals(YCBCR_SUBSAMPLE_HALF))
			){
				return false;
			}
			return true;
	}
	
	/**
	 * Sets the blue [x] value for the chromaticities of the primary colors of 
	 * the imaging process
	 * 
	 * @param _chromaticitiesPrimaryBlueX	the blue [x] value for the
	 * 																chromaticities of the primary colors
	 */
	public void setChromaticitiesPrimaryBlueX(float _chromaticitiesPrimaryBlueX) {
		this.chromaticitiesPrimaryBlueX = _chromaticitiesPrimaryBlueX;
	}         

	/**
	 * Sets the blue [y] value for the chromaticities of the primary colors of 
	 * the imaging process
	 * 
	 * @param _chromaticitiesPrimaryBlueY	the blue [y] value for the
	 * 																chromaticities of the primary colors
	 */
	public void setChromaticitiesPrimaryBlueY(float _chromaticitiesPrimaryBlueY) {
		this.chromaticitiesPrimaryBlueY = _chromaticitiesPrimaryBlueY;
	}        

	/**
	 * Sets the green [x] value for the chromaticities of the primary colors of 
	 * the imaging process
	 * 
	 * @param _chromaticitiesPrimaryGreenX	the green [x] value for the
	 * 																	chromaticities of the primary colors
	 */
	public void setChromaticitiesPrimaryGreenX(float _chromaticitiesPrimaryGreenX) {
		this.chromaticitiesPrimaryGreenX = _chromaticitiesPrimaryGreenX;
	}         

	/**
	 * Sets the green [y] value for the chromaticities of the primary colors of 
	 * the imaging process
	 * 
	 * @param _chromaticitiesPrimaryGreenY	the green [y] value for the
	 * 																	chromaticities of the primary colors
	 */
	public void setChromaticitiesPrimaryGreenY(float _chromaticitiesPrimaryGreenY) {
		this.chromaticitiesPrimaryGreenY = _chromaticitiesPrimaryGreenY;
	}         

	/**
	 * Sets the red [x] value for the chromaticities of the primary colors of 
	 * the imaging process
	 * 
	 * @param _chromaticitiesPrimaryRedX	the red [x] value for the
	 * 																chromaticities of the primary colors
	 */
	public void setChromaticitiesPrimaryRedX(float _chromaticitiesPrimaryRedX) {
		this.chromaticitiesPrimaryRedX = _chromaticitiesPrimaryRedX;
	}        

	/**
	 * Sets the red [y] value for the chromaticities of the primary colors of 
	 * the imaging process
	 * 
	 * @param _chromaticitiesPrimaryRedY	the red [y] value for the
	 * 																chromaticities of the primary colors
	 */
	public void setChromaticitiesPrimaryRedY(float _chromaticitiesPrimaryRedY) {
		this.chromaticitiesPrimaryRedY = _chromaticitiesPrimaryRedY;
	}         

	/**
	 * Sets the x value of the white point chromaticity, representing the effective illumination 
	 * source.
	 * 
	 * @param _chromaticitiesWhitePointX	the x value of the white point
	 * 																chromaticity
	 */
	public void setChromaticitiesWhitePointX(float _chromaticitiesWhitePointX) {
		this.chromaticitiesWhitePointX = _chromaticitiesWhitePointX;
	}         

	/**
	 * Sets the y value of the white point chromaticity, representing the effective illumination 
	 * source.
	 * 
	 * @param _chromaticitiesWhitePointY	the y value of the white point
	 * 																chromaticity
	 */
	public void setChromaticitiesWhitePointY(float _chromaticitiesWhitePointY) {
		this.chromaticitiesWhitePointY = _chromaticitiesWhitePointY;
	}
	
	/**
	 * Sets the optical density of each possible pixel value. Only applicable to 
	 * grayscale images. The number of elements in this list is equal to
	 * 2^bitsPerSample, where bitsPerSample is one of the numbers in the
	 * <code>bitsPerSample</code> member, where its assumed that 
	 * all the numbers are the same (ex: {8,8,8}). The 0th value in this
	 * member corresponds to the optical density of a pixel having the 
	 * value 0, and so on. The values can not be correctly interpreted
	 * without the <code>grayResponseUnit</code> member.
	 * 
	 * @param _grayResponseCurve	the list of optical densities of each
	 * 								possible pixel value
	 * @throws FatalException
	 */
	public void setGrayResponseCurve(Vector _grayResponseCurve) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setGrayResponseCurve(Vector _grayResponseCurve)",
			"_grayResponseCurve",
			_grayResponseCurve, this.getClass().getName());
		this.grayResponseCurve = _grayResponseCurve;
	}         

	/**
	 * Sets the precision of the information in the 
	 * <code>grayResponseCurve</code> member. For example, if this 
	 * <code>grayResponseUnit</code> member is set to 4 and the 5th 
	 * <code>grayResponseCurve</code> number is 3455, 
	 * then the resulting actual <code>grayResponseCurve</code> number 
	 * is .3455
	 * 
	 * Valid values are <code>PREC_HUND</code>,  
	 * <code>PREC_HUND_THOU</code>, <code>PREC_TEN</code>,
	 * <code>PREC_TEN_THOU</code>, <code>PREC_THOU</code>,
	 * <code>PREC_UNKNOWN</code>, or <code>PREC_NA</code>.
	 * 
	 * @param _grayResponseUnit	the precision of the information in the 
	 * 													<code>grayResponseCurve</code> member.
	 * @see TiffImage#grayResponseCurve
	 * @throws FatalException
	 */
	public void setGrayResponseUnit(String _grayResponseUnit) 
		throws FatalException {
		if (!_grayResponseUnit.equals(PREC_UNKNOWN) &&
			!_grayResponseUnit.equals(PREC_NA) &&
			!_grayResponseUnit.equals(PREC_TEN) &&
			!_grayResponseUnit.equals(PREC_HUND) &&
			!_grayResponseUnit.equals(PREC_THOU) &&
			!_grayResponseUnit.equals(PREC_TEN_THOU) &&
			!_grayResponseUnit.equals(PREC_HUND_THOU)) {
				Informer.getInstance().fail(
					this, "setGrayResponseUnit(int)",
					"Illegal argument",
					"_grayResponseUnit: " + _grayResponseUnit,
					new FatalException("Not a valid grayResponseUnit value"));
			}
		this.grayResponseUnit = _grayResponseUnit;
	}
	
	/**
	 * Sets whether or not this image specifies chromaticities.
	 * 
	 * @param hasChroms <code>TRUE</code> if this image specifies
	 * 	chromaticities, <code>FALSE</code> if it does not,
	 * 	or <code>UNKNOWN</code> if it is not known 
	 * @throws FatalException
	 */
	public void setHasChromaticities(String hasChroms) 
		throws FatalException {
		if (hasChroms != null && 
		        (hasChroms.equals(Bitstream.FALSE) || 
		                hasChroms.equals(Bitstream.TRUE) ||
		                hasChroms.equals(Bitstream.UNKNOWN))) {
				this.hasChromaticities =hasChroms;
		} else {
			Informer.getInstance().fail(
				this, "setHasChromaticities(String)",
				"Illegal argument",
				"hasChroms: " + hasChroms,
				new FatalException("Not a valid hasChroms"));
		}
	}

	/**
	 * @param is the tag numbers used to describe this image
	 */
	public void setImageTags(int[] is) {
		this.imageTags = is;
	}

	/**
	 * Sets the maximum number of Bytes per strip in this image.
	 * Note that you can not use this method to set the number to
	 * the unknown value (-1)!
	 * 
	 * @param _maxStripByteCount the max number of bytes in a 
	 * 					strip in a strip in this image
	 * @throws FatalException
	 */
	public void setMaxStripBytes(long _maxStripByteCount) 
		throws FatalException {
			String methodName = "setMaxStripBytes(long)";
			
		if (_maxStripByteCount < MIN_NUM_BPS) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument", "_maxStripByteCount: " + _maxStripByteCount,
				new FatalException("Not a valid number of bytes")
			);
		}
		if (_maxStripByteCount > MAX_NUM_BPS){
			// can't support this large a number of bytes - record the anomaly,
			// log a warning and reduce the value to the max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_MAX_STRIP_BYTES);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing max number of strip bytes (too many)",
				"file: " + this.getDf().getPath());
			_maxStripByteCount = MAX_NUM_BPS;
		}
		this.maxStripBytes = _maxStripByteCount;
	}

	/**
	 * Sets the maximum number of Bytes per tile in this image.
	 * Note that you can not use this method to set the number to
	 * the unknown value (-1)!
	 * 
	 * @param _maxTileBytes the max number of bytes in a tile in a strip
	 *  in this image
	 * @throws FatalException
	 */
	public void setMaxTileBytes(long _maxTileBytes) 
		throws FatalException {
		String methodName = "setMaxTileBytes(long)";
		
		if (_maxTileBytes < MIN_NUM_BPT) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument", "_maxTileBytes: " + _maxTileBytes,
				new FatalException("Not a valid number of bytes")
			);
		}
		if (_maxTileBytes > MAX_NUM_BPT){
			// can't support this large a number of bytes - record the anomaly
			// and then try to store the value
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_MAX_TILE_BYTES);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing max number of tile bytes (too many)",
				"file: " + this.getDf().getPath());
			_maxTileBytes = MAX_NUM_BPT;
			
		}
		this.maxTileBytes = _maxTileBytes;
	}

	/**
	 * Sets the number of strips in the image
	 * Note that you can't set the number of strips to the unknown
	 * value (-1) in this method!
	 * 
	 * @param _numStrips	the number of strips in the image
	 * @throws FatalException
	 */
	public void setNumStrips(long _numStrips) throws FatalException {
		String methodName = "setNumStrips(long)";
		
		if (_numStrips < MIN_NUM_STRIPS) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument", "_numStrips: " + _numStrips,
				new FatalException("Not a valid number of strips")
			);
		}
		if (_numStrips > MAX_NUM_STRIPS){
			// can't support this large a number of strips - record the anomaly
			// and then try to store the value
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_NUM_STRIPS);
			this.getDf().addAnomaly(ta);
			
		}
		this.numStrips = _numStrips;
	}

	/**
	 * Sets the number of tiles in the tiled image.
	 * Note that you can't use this method to set the number
	 * of tiles to the unknown value (-1)!
	 * 
	 * @param	_numTiles	the number of tiles in the tiled image
	 * @throws FatalException
	 */
	public void setNumTiles(long _numTiles) throws FatalException{
		if (_numTiles < MIN_NUM_TILES) {
			Informer.getInstance().fail(
				this, "setNumTiles(long)",
				"Illegal argument", "_numTiles: " + _numTiles,
				new FatalException("Not a valid number of tiles")
			);
		}
		if (_numTiles > MAX_NUM_TILES){
			// can't support this large a number of tiles - record the anomaly
			// and then try to store the value
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_NUM_TILES);
			this.getDf().addAnomaly(ta);
		}
		this.numTiles = _numTiles;
	}

	/**
	 * Sets how the pixel components are ordered in the image. Valid values
	 * are <code>PLANAR_CONFIG_CHUNKY</code>,
	 * <code>PLANAR_CONFIG_PLANAR</code>, or
	 * <code>PLANAR_CONFIG_UNKNOWN</code>.
	 * 
	 * @param	_planarConfig how the pixels components are ordered 
	 * 	in the image
	 * @throws FatalException
	 */
	public void setPlanarConfig(String _planarConfig) 
		throws FatalException {
		if (_planarConfig == null ||
			(!_planarConfig.equals(PLANAR_CONFIG_CHUNKY) &&
			!_planarConfig.equals(PLANAR_CONFIG_UNKNOWN) &&
			!_planarConfig.equals(PLANAR_CONFIG_PLANAR))) {
				Informer.getInstance().fail(
					this, "setPlanarConfig(String)",
					"Ilegal argument",
					"_planarConfig: " + _planarConfig,
					new FatalException("Not a valid planar configuration"));				
			}
		this.planarConfig = _planarConfig;
	}
	
	/**
	 * Sets a pair of 'headroom' and 'footroom' image data values for each pixel
	 * component. Only applicable to RGB and LCbCr color space images.
	 * 
	 * @param	_referenceBlackWhite	a pair of 'headroom' and 'footroom' image 
	 * 									data values for each pixel component
	 * @throws FatalException
	 */
	public void setReferenceBlackWhite(Vector _referenceBlackWhite) throws FatalException {
		// check that the argument is not null
		this.checkForNullObjectArg(
			"setReferenceBlackWhite(float[] _referenceBlackWhite)",
			"_referenceBlackWhite",
			_referenceBlackWhite, this.getClass().getName());
		this.referenceBlackWhite = _referenceBlackWhite;
	} 

	/**
	 * Sets the number of rows per strip 
	 * (except possibly the last strip which could have less).
	 * Note that you can not use this method to set the number
	 * to the unknown value (-1)!
	 * 
	 * @param	_rows	the number of rows per strip 
	 * 			(except possibly the last strip)
	 * @throws FatalException
	 */
	public void setRowsPerStrip(long _rows) throws FatalException {
		if (_rows < MIN_ROWS_PER_STRIP) {
			Informer.getInstance().fail(
				this, "setRowsPerStrip(long)",
				"Illegal argument",
				"_rows: " + _rows,
				new FatalException("Not a valid number of rows per strip"));
		}
		if (_rows > MAX_ROWS_PER_STRIP){
			// can't support this large a number of rows - record the anomaly
			// and then try to store the value
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_ROWS_PER_STRIP);
			this.getDf().addAnomaly(ta);
		}
		this.rowsPerStrip = _rows;
	}           
	
	/**
	 * Specifies whether the image is stored in tiles or strips.
	 * Valid values include <code>STORAGE_STRIP</code> or
	 * <code>STORAGE_TILE</code>.
	 * 
	 * @param _storageSegment	specifies whether the image is stored in
	 * 								tiles or strips.
	 * @throws FatalException
	 */
	public void setStorageSegment(String _storageSegment) 
		throws FatalException {
		if (_storageSegment == null ||
				(!_storageSegment.equals(STORAGE_STRIP) &&
				!_storageSegment.equals(STORAGE_TILE) &&
				!_storageSegment.equals(STORAGE_UNKNOWN))) {
			Informer.getInstance().fail(
				this, "setStorageSegment(String)",
				"Illegal argument",
				"_storageSegment: " + _storageSegment,
				new FatalException("Not a valid storage segment value"));
		}
		
		// now that the storage segment type is known, set the correct 
		// values for the storage segment-related members
		if (_storageSegment.equals(STORAGE_STRIP)){
			this.setMaxTileBytes(0);
			this.setNumTiles(0);
			this.setTileLength(0);
			this.setTileWidth(0);
		} else if (_storageSegment.equals(STORAGE_TILE)){
			this.setMaxStripBytes(0);
			this.setNumStrips(0);
			this.setRowsPerStrip(0);
		}
		
		this.storageSegment = _storageSegment;
	}        

	/**
	 * Sets for each strip, the number of bytes in the strip, after any compression.
	 * 
	 * @param _stripByteCounts	the number of bytes in the strip, 
	 * 	for each strip
	 * @throws FatalException
	 */
	public void setStripByteCounts(Vector _stripByteCounts) 
		throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setStripByteCounts(long[] _stripByteCounts)",
			"_stripByteCounts",
			_stripByteCounts, this.getClass().getName());
		this.stripByteCounts = _stripByteCounts;
	}         

	/**
	 * Sets for each strip, the byte offset to the strip.
	 * 
	 * @param	_stripOffsets	the byte offset to the strip, 
	 * 		for each strip
	 * @throws FatalException
	 */
	public void setStripOffsets(Vector _stripOffsets) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setStripOffsets(long[] _stripOffsets)",
			"_stripOffsets",
			_stripOffsets, this.getClass().getName());
		this.stripOffsets = _stripOffsets;
	} // end setStripOffsets        

	/**
	 * Sets the number of (compressed) bytes in that tile, for each tile.
	 * 
	 * @param	_tileByteCounts	the number of bytes in that tile, 
	 * 	for each tile
	 */
	public void setTileByteCounts(Vector _tileByteCounts) {
		this.tileByteCounts = _tileByteCounts;
	} // end setTileByteCounts        

	/**
	 * Sets the tile length (height) in pixels.
	 * Note that you can not use this method to set the number
	 * to the unknown value (-1)!
	 * 
	 * @param _length	the tile length in pixels
	 * @throws FatalException
	 */
	public void setTileLength(long _length) throws FatalException {
		if (_length < MIN_TILE_LENGTH) {
			Informer.getInstance().fail(
				this, "setTileLength(long)",
				"Illegal argument",
				"_length: " + _length,
				new FatalException("Not a valid tile length"));
		}
		if (_length > MAX_TILE_LENGTH){
			// can't support this large a tile length - record the anomaly
			// and then try to store the value
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_TILE_LENGTH);
			this.getDf().addAnomaly(ta);
		}
		this.tileLength = _length;
	}        

	/**
	 * Sets the byte offset of that tile, for each tile, relative to the beginning of the
	 * file.
	 * 
	 * @param _tileOffsets	the tile offsets relative to the beginning of the file
	 */
	public void setTileOffsets(Vector _tileOffsets) {
		this.tileOffsets = _tileOffsets;
	} // end setTileOffsets        

	/**
	 * Sets the tile width in pixels (number of columns).
	 * Note that you can not use this method to set the number
	 * to the unknown value (-1)!
	 * 
	 * @param	_width	the tile width in pixels
	 * @throws FatalException
	 */
	public void setTileWidth(long _width) throws FatalException {
		if (_width < MIN_TILE_WIDTH) {
			Informer.getInstance().fail(
				this, "setTileWidth(long)",
				"Illegal argument",
				"_width: " + _width,
				new FatalException("Not a valid tile width"));
		}
		if (_width > MAX_TILE_WIDTH){
			// can't support this large a tile width - record the anomaly
			// and then try to store the value
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TiffAnomalies.TIFF_OVRLMT_TILE_WIDTH);
			this.getDf().addAnomaly(ta);
		}
		this.tileWidth = _width;
	}
	
	/**
	 * Sets the coefficients needed to transform the image data from the YCbCr color
	 * space to the RGB color space. This is a list of three elements: LumaRed,
	 * LumaGreen, and LumaBlue (in that order). 
	 * 
	 * @param	_ycbcrCoefficients	the coefficients needed to transform the image data 
	 * 								from YCbCr to RGB color space
	 * @throws FatalException
	 */
	public void setYcbcrCoefficients(Vector _ycbcrCoefficients) throws FatalException {
		// check that the argument is not null and that it contains the 3 coefficients
		if (_ycbcrCoefficients == null || _ycbcrCoefficients.size() != 3){
			Informer.getInstance().fail(
				this, "setYcbcrCoefficients(Vector)",
				"Illegal argument",
				"_ycbcrCoefficients: " + _ycbcrCoefficients.toString(),
				new FatalException("Not valid values for ycbcrCoefficients"));			
		}
		this.ycbcrCoefficients = _ycbcrCoefficients;
	} // end setYcbcrCoefficients        

	/**
	 * Sets the positioning of subsampled chrominance components relative to luminance
	 * samples. The valid values are <code>YCBCR_POSITION_CENTER</code> or
	 * <code>YCBCR_POSITION_COSITE</code>.
	 * 
	 * @param _ycbcrPositioning	the positioning of subsampled chrominance components 
	 * 				relative to luminance samples
	 * @throws FatalException
	 */
	public void setYcbcrPositioning(String _ycbcrPositioning) 
		throws FatalException {
		if (!isValidYcbcrPositioning(_ycbcrPositioning)){
			Informer.getInstance().fail(
				this, "setYcbcrPositioning(int)",
				"Illegal argument",
				"_ycbcrPositioning: " + _ycbcrPositioning,
				new FatalException("Not a valid ycbcrPositioning value"));			
			}
		this.ycbcrPositioning = _ycbcrPositioning;
	} // end setYcbcrPositioning        

	/**
	 * Sets the subsampling factors used for the chrominance components
	 * of an image in YCbCr color space. Must meet the requirements specified
	 * for the <code>ycbcrSubsampling</code> member.
	 * 
	 * @param _ycbcrSubsampling	the subsampling factors used for the 
	 * 		chrominance components of an image in YCbCr color space
	 * @see TiffImage#ycbcrSubsampling
	 * @throws FatalException
	 */
	public void setYcbcrSubsampling(Vector _ycbcrSubsampling) 
		throws FatalException {
		// check that the argument is valid
		if (!isValidYcbcrSubsampling(_ycbcrSubsampling)) {
			Informer.getInstance().fail(
				this, "setYcbcrSubsampling(Vector)",
				"Illegal argument",
				"_ycbcrSubsampling: " + _ycbcrSubsampling.toString(),
				new FatalException("Not valid values for ycbcrSubsampling"));				
			}
		this.ycbcrSubsampling = _ycbcrSubsampling;
	} // end setYcbcrSubsampling

	/**
	 * @return the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		sb.append("\tImage tags: ");
		int [] imTags = this.getImageTags();
		for (int i=0; i<imTags.length; i++) {
			sb.append(imTags[i] + " ");
		}
		sb.append("\n");
		sb.append("\tHasChromaticities: " + hasChromaticities() + "\n");
		sb.append("\tMaxStripByteCount: " + getMaxStripBytes() + "\n");
		sb.append("\tMaxTileByteCount: " + getMaxTileBytes() + "\n");
		sb.append("\tPlanarConfig: " + getPlanarConfig() + "\n");
		sb.append("\tStorageSegment: " + this.getStorageSegment() + "\n");
		sb.append("\tTileHeight: " + getTileLength() + "\n");
		sb.append("\tTilesPerImage: " + getNumTiles() + "\n");
		sb.append("\tTileWidth: " + getTileWidth() + "\n\n");
		return sb.toString();
	}

}
