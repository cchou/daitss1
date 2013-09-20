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

import java.util.Enumeration;
import java.util.Hashtable;

import edu.fcla.daitss.format.image.Image;
import edu.fcla.daitss.format.image.Orientation;


/**
 * TiffTags represents all the known tags of a Tiff file.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class TiffTags {

	private class Tag {
		/**
		 * Name for this Tiff field
		 */
		String tagName;
		
		/** 
		 * Tiff Tag number
		 */
		int tagNumber;
		
		/**
		 * Valid number of values for this field. If this is
		 * equal to <code>DEPENDS</code> it means that this number
		 * varies depending on other fields. If this is equal to
		 * <code>INFINITE</code> it means that this member does not
		 * apply to this Tiff field (there's no limit).
		 */
		long validNumValues;
		
		/**
		 * List of valid Tiff data types for this Tiff field
		 */
		int[] validTypes;
		
		Tag(String name, int num, int[] types, long numVals){
			this.tagName = name;
			this.tagNumber = num;
			this.validTypes = types;
			this.validNumValues = numVals;
		}
	}
	
	private static int DEPENDS = -1;
	
	private static int INFINITE = -2;
	
	/**
	 * Number of possible fields in a Tiff image
	 */
	private static final int numTags = 74;

	/**
	 * Test driver
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		//TiffTags tt = new TiffTags();
	}
	
	/**
	 * Number of Tiff fields added to the tags array so far
	 */
	private int numBuiltTags = 0;

	/**
	 * The Tiff Fields
	 */
	private Hashtable tags = null;

	/**
	 * 
	 */
	public TiffTags() {
		tags = new Hashtable(numTags);
		buildTags();
	}
	
	/**
	 * Inserts all the Tiff fields into the tags array
	 */
	private void buildTags() {
		insert("NewSubfileType",254,"LONG",1);
		insert("SubfileType",255,"SHORT",1);
		insert("ImageWidth",256,"SHORT,LONG",1);
		insert("ImageLength",257,"SHORT,LONG",1);
		insert("BitsPerSample",258,"SHORT",DEPENDS);
		insert("Compression",259,"SHORT",1);
		insert("PhotometricInterpretation",262,"SHORT",1);
		insert("Threshholding",263,"SHORT",1);
		insert("CellWidth",264,"SHORT",1);
		insert("CellLength",265,"SHORT",1);
		insert("FillOrder",266,"SHORT",1);
		insert("DocumentName",269,"ASCII",INFINITE);
		insert("ImageDescription",270,"ASCII",INFINITE);
		insert("Make",271,"ASCII",INFINITE);
		insert("Model",272,"ASCII",INFINITE);
		insert("StripOffsets",273,"SHORT,LONG",DEPENDS);
		insert("Orientation",274,"SHORT",1);
		insert("SamplesPerPixel",277,"SHORT",1);
		insert("RowsPerStrip",278,"SHORT,LONG",1);
		insert("StripByteCounts",279,"SHORT,LONG",DEPENDS);
		insert("MinSampleValue",280,"SHORT",DEPENDS);
		insert("MaxSampleValue",281,"SHORT",DEPENDS);
		insert("XResolution",282,"RATIONAL",1);
		insert("YResolution",283,"RATIONAL",1);
		insert("PlanarConfiguration",284,"SHORT",1);
		insert("PageName",285,"ASCII",INFINITE);
		insert("XPosition",286,"RATIONAL",1);
		insert("YPosition",287,"RATIONAL",1);
		insert("FreeOffsets",288,"LONG",INFINITE);
		insert("FreeByteCounts",289,"LONG",INFINITE);
		insert("GrayResponseUnit",290,"SHORT",1);
		insert("GrayResponseCurve",291,"SHORT",DEPENDS);
		insert("T4Options",292,"LONG",1);
		insert("T6Options",293,"LONG",1);
		insert("ResolutionUnit",296,"SHORT",1);
		insert("PageNumber",297,"SHORT",2);
		insert("ColorResponseUnit",300,"SHORT",1); // Tiff 4.0
		insert("TransferFunction",301,"SHORT",DEPENDS);
		insert("Software",305,"ASCII",INFINITE);
		insert("DateTime",306,"ASCII",20);
		insert("Artist",315,"ASCII",INFINITE);
		insert("HostComputer",316,"ASCII",INFINITE);
		insert("Predictor",317,"SHORT",1);
		insert("WhitePoint",318,"RATIONAL",2);
		insert("PrimaryChromaticities",319,"RATIONAL",6);
		insert("ColorMap",320,"SHORT",DEPENDS);
		insert("HalftoneHints",321,"SHORT",2);
		insert("TileWidth",322,"SHORT,LONG",1);
		insert("TileLength",323,"SHORT,LONG",1);
		insert("TileOffsets",324,"LONG",DEPENDS);
		insert("TileByteCounts",325,"SHORT,LONG",DEPENDS);
		insert("BadFaxLines",326,"LONG",1); // Class F
		insert("CleanFaxData",327,"SHORT",1); // Class F
		insert("ConsecutiveBadFaxLines",328,"LONG",1); // Class F
		insert("SubIFDs",330,"LONG,IFD",INFINITE);
		insert("InkSet",332,"SHORT",1);
		insert("InkNames",333,"ASCII",INFINITE);
		insert("NumberOfInks",334,"SHORT",1);
		insert("DotRange",336,"BYTE,SHORT",DEPENDS);
		insert("TargetPrinter",337,"ASCII",INFINITE);
		insert("ExtraSamples",338,"BYTE",DEPENDS);
		insert("SampleFormat",339,"SHORT",DEPENDS);
		insert("SMinSampleValue",340,"ANY",DEPENDS);
		insert("SMaxSampleValue",341,"ANY",DEPENDS);
		insert("TransferRange",342,"SHORT",6);
		insert("ClipPath",343,"BYTE",INFINITE); // PageMaker
		insert("XClipPathUnits",344,"LONG",1); // PageMaker
		insert("YClipPathUnits",345,"LONG",1); // PageMaker
		insert("Indexed",346,"SHORT",1); // PageMaker
		insert("JPEGTables",347,"UNDEFINED",INFINITE); // Photoshop
		insert("OPIProxy",351,"SHORT",1); // PageMaker
		insert("JPEGProc",512,"SHORT",1);
		insert("JPEGInterchangeFormat",513,"LONG",1);
		insert("JPEGInterchangeFormatLngth",514,"LONG",1);
		insert("JPEGRestartInterval",515,"SHORT",1);
		insert("JPEGLosslessPredictors",517,"SHORT",DEPENDS);
		insert("JPEGPointTransforms",518,"SHORT",DEPENDS);
		insert("JPEGQTables",519,"LONG",DEPENDS);
		insert("JPEGDCTables",520,"LONG",DEPENDS);
		insert("JPEGACTables",521,"LONG",DEPENDS);
		insert("YCbCrCoefficients",529,"RATIONAL",3);
		insert("YCbCrSubSampling",530,"SHORT",2);
		insert("YCbCrPositioning",531,"SHORT",1);
		insert("ReferenceBlackWhite",532,"LONG",6);
		insert("ImageID",32781,"ASCII",INFINITE); // PageMaker
		insert("CFARepeatPatternDim",33421,"SHORT",2); // TIFF/EP
		insert("CFAPattern",33422,"BYTE",INFINITE); // TIFF/EP
		insert("BatteryLevel",33423,"RATIONAL,ASCII",1); // TIFF/EP	
		insert("Copyright",33432,"ASCII",INFINITE);
		insert("ExposureTime",33434,"RATIONAL",1); // TIFF/EP
		insert("Fnumber",33437,"RATIONAL",INFINITE); // TIFF/EP
		insert("ModelPixelScaleTag",33550,"DOUBLE",3); // GeoTIFF
		insert("Associated Press IPTC",33723,"LONG,ASCII",INFINITE); // TIFF/EP, RichTIFF
		//insert("IntergraphMatrixTag",33920,"DOUBLE",17); // GeoTIFF (Revision 0.2 and earlier, now deprecated)
		// Intergraph uses tag 33920 for something (?)
		insert("ModelTiepointTag",33922,"DOUBLE",DEPENDS); // GeoTIFF
		insert("Site",34016,"ASCII",INFINITE); // TIFF/IT
		insert("ColorSequence",34017,"ASCII",INFINITE); // TIFF/IT
		insert("IT8Header",34018,"ASCII",INFINITE); // TIFF/IT
		insert("RasterPadding",34019,"SHORT",1); // TIFF/IT
		insert("BitsPerRunLength",34020,"SHORT",1); // TIFF/IT
		insert("BitsPerExtendedRunLength",34021,"SHORT",1); // TIFF/IT
		insert("ColorTable",34022,"BYTE",INFINITE); // TIFF/IT
		insert("ImageColorIndicator",34023,"BYTE",1); // TIFF/IT
		insert("BackgroundColorIndicator",34024,"BYTE",1); // TIFF/IT
		insert("ImageColorValue",34025,"BYTE",1); // TIFF/IT
		insert("BackgroundColorValue",34026,"BYTE",1); // TIFF/IT
		insert("PixelIntensityRange",34027,"BYTE",2); // TIFF/IT
		insert("TransparencyIndicator",34028,"BYTE",1); // TIFF/IT
		insert("ColorCharacterization",34029,"ASCII",INFINITE); // TIFF/IT
		insert("HCUsage",34030,"LONG",1); // TIFF/IT
		insert("Kodak IPTC",34152,"BYTE",INFINITE); // RichTIFF
		insert("ModelTransformationTag",34264,"DOUBLE",16); // GeoTIFF
		insert("ExifIFD",34665,"LONG",1); // Exif
		insert("InterColourProfile",34675,"UNDEFINED",INFINITE); // TIFF/EP (ICC Profile)
		insert("GeoKeyDirectoryTag",34735,"SHORT",INFINITE); // GeoTIFF
		insert("GeoDoubleParamsTag",34736,"DOUBLE",INFINITE); // GeoTIFF
		insert("GeoAsciiParamsTag",34737,"ASCII", INFINITE); // GeoTIFF
		insert("ExposureProgram",34850,"SHORT",1); // TIFF/EP
		insert("SpectralSensitivity",34852,"ASCII",INFINITE);  // TIFF/EP
		insert("GPSInfo",34853,"LONG",1);  // TIFF/EP
		insert("ISOSpeedRatings",34855,"SHORT",3);  // TIFF/EP
		insert("OECF",34856,"UNDEFINED",INFINITE);  // TIFF/EP
		insert("Interlace",34857,"SHORT",1);  // TIFF/EP
		insert("TimeZoneOffset",34858,"SSHORT",INFINITE);  // TIFF/EP
		insert("SelfTimerMode",34859,"SHORT",1);  // TIFF/EP
		insert("DateTimeOriginal",36867,"ASCII",20); // TIFF/EP
		insert("CompressedBitsPerPixel",37122,"RATIONAL",1); // TIFF/EP
		insert("ShutterSpeedValue",37377,"RATIONAL",1); // TIFF/EP
		insert("ApertureValue",37378,"RATIONAL",1); // TIFF/EP
		insert("BrightnessValue",37379,"SRATIONAL",INFINITE); // TIFF/EP
		insert("ExposureBiasValue",37380,"SRATIONAL",INFINITE); // TIFF/EP
		insert("MaxApertureValue",37381,"RATIONAL",1); // TIFF/EP
		insert("SubjectDistance",37382,"SRATIONAL",INFINITE); // TIFF/EP
		insert("MeteringMode",37383,"SHORT",1); // TIFF/EP
		insert("LightSource",37384,"SHORT",1); // TIFF/EP
		insert("Flash",37385,"SHORT",1); // TIFF/EP
		insert("FocalLength",37386,"RATIONAL",INFINITE); // TIFF/EP
		insert("FlashEnergy",37387,"RATIONAL",INFINITE); // TIFF/EP
		insert("SpatialFrequencyResponse",37388,"UNDEFINED",INFINITE); // TIFF/EP
		insert("Noise",37389,"UNDEFINED",INFINITE); // TIFF/EP
		insert("FocalPlaneXResolution",37390,"RATIONAL",1); // TIFF/EP
		insert("FocalPlaneYResolution",37391,"RATIONAL",1); // TIFF/EP
		insert("FocalPlaneResolutionUnit",37392,"SHORT",1); // TIFF/EP
		insert("ImageNumber",37393,"LONG",1); // TIFF/EP
		insert("SecurityClassification",37394,"ASCII",INFINITE); // TIFF/EP
		insert("ImageHistory",37395,"ASCII",INFINITE); // TIFF/EP
		insert("SubjectLocation",37396,"SHORT",INFINITE); // TIFF/EP
		insert("ExposureIndex",37397,"RATIONAL",INFINITE); // TIFF/EP
		insert("TIFF/EPStandardID",37398,"BYTE",4); // TIFF/EP
		insert("SensingMethod",37399,"SHORT",1); // TIFF/EP
		insert("ImageSourceData",37724,"UNDEFINED",INFINITE); // Photoshop
		insert("InteroperabilityIFD",40965,"LONG",1); // Exif
	}
	
	/**
	 * Returns all the Tiff members.
	 * 
	 * @return all the members of this class
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		for (Enumeration e = tags.keys(); e.hasMoreElements();) {
			Tag tag = (Tag) tags.get(e.nextElement());
			buffer.append("Tag " + tag.tagNumber + 
				"\t" + tag.tagName + "\tType(s): ");
			buffer.append(TiffDataTypes.getStringValue(tag.validTypes[0]));
			for (int j=1; j<tag.validTypes.length; j++){
				buffer.append("," + TiffDataTypes.getStringValue(tag.validTypes[j]));
			}
			buffer.append("\tcount: " + tag.validNumValues + "\n");
		}
		return buffer.toString();
	}
	
	/**
	 * Checks that a Tiff tag can have a particular data type (according to
	 * the Tiff specification)
	 * 
	 * @param tag	 a Tiff tag number
	 * @param type	a Tiff data type
	 * @return	<code>true</code> if the tag can have that type, else
	 * 				<code>false</code>
	 */
	public boolean isRightType(int tag, int type){
		boolean isRight = false;
		Integer theInt = new Integer(tag);
		if (tags.containsKey(theInt)){
			Tag t = (Tag) tags.get(theInt);
			for (int i=0; i<t.validTypes.length; i++){
				if (type == t.validTypes[i] || t.validTypes[i] == TiffDataTypes.ANY){
					isRight = true;
				}
			}
		} else {
			// the fact that this tag is unknown will be caught in TiffFile.recordField
			// for now just return true
			isRight = true;
		}
		return isRight;
	}
	
	/**
	 * Checks that the number of values a tiff image has for a field
	 * is valid for that field. This is a 'lazy' check. It does not cross-reference
	 * other tiff fields for the tags which have a valid count that is
	 * dependent on other tags. The only tags that really get checked are
	 * those that always have a consistent number of values.
	 * 
	 * @param tag	a tiff tag number
	 * @param count	number of values for a tiff field
	 * @return	<code>true</code> if the number of values is valid for
	 * 				that field, otherwise <code>false</code>
	 */
	public boolean isGoodCount(int tag, long count) {
		boolean isRight = false;
		Integer theInt = new Integer(tag);
		if (tags.containsKey(theInt)) {
			Tag t = (Tag) tags.get(theInt);
			if (t.validNumValues == INFINITE ||  // can have any number of values
				t.validNumValues == DEPENDS ||	// depend on other fields
				t.validNumValues == count) {
					isRight = true;
				}
		} else {
			// the fact that this tag is unknown will be caught in TiffFile.recordField
			// for now just return true
			isRight = true;
		}
		return isRight;
	}

	/**
	 * Inserts a single Tiff field into the Hashtable of tags
	 * 
	 * @param name	name of the field
	 * @param num		field tag number
	 * @param types	legal types for this field
	 * @param numVals	number of values for this field
	 */
	private void insert(String name, int num, String types, long numVals) {
		String[] stringTypes = types.split(",");
		int[] intTypes = new int[stringTypes.length];
		for (int i=0; i<stringTypes.length; i++){
			intTypes[i] = TiffDataTypes.getValue(stringTypes[i]);
		}
		tags.put(new Integer(num), new Tag(name,num,intTypes,numVals));
		numBuiltTags++;
	}
	
	/**
	 * Returns the ExtraSamples description value used in the archive given
	 * the value found in the Tiff ExtraSamples field.
	 * 
	 * @param value	the value found in the Tiff ExtraSamples field
	 * @return the ExtraSamples description value used in the archive
	 */
	public String mapExtraSamples(int value) {
		String result = "";
		switch (value) {
			case 0 :
				result = Image.SAMPLE_DESC_UNKNOWN;
				break;
			case 1 :
				result = Image.SAMPLE_DESC_ASSOC_ALPHA;
				break;
			case 2 :
				result = Image.SAMPLE_DESC_UNASSOC_ALPHA;
				break;
		}
		return result;
	}
	
	/**
	 * Returns the GrayResponseUnit value used in the archive given
	 * the value found in the Tiff GrayResponseUnit field.
	 * 
	 * @param value	the value found in the Tiff GrayResponseUnit field
	 * @return the GrayResponseUnit value used in the archive
	 */
	public String mapGrayResponseUnit(int value) {
		String result = "";
		switch (value) {
			case 1 :
				result = TiffImage.PREC_TEN;
				break;
			case 2:
				result = TiffImage.PREC_HUND;
				break;
			case 3:
				result = TiffImage.PREC_THOU;
				break;
			case 4:
				result = TiffImage.PREC_TEN_THOU;
				break;
			case 5:
				result = TiffImage.PREC_HUND_THOU;
				break;
		}
		return result;
	}
	
	/**
	 * Returns the Orientation value used in the archive given
	 * the value found in the Tiff Orientation field.
	 * 
	 * @param value	the value found in the Tiff Orientation field
	 * @return the Orientation value used in the archive
	 */
	public String mapOrientation(int value) {
		String result = "";
		switch (value) {
			case 1 :
				result = Orientation.TOP_LEFT;
				break;
			case 2:
				result = Orientation.TOP_RIGHT;
				break;
			case 3:
				result = Orientation.BOTTOM_RIGHT;
				break;
			case 4:
				result = Orientation.BOTTOM_LEFT;
				break;
			case 5:
				result = Orientation.LEFT_TOP;
				break;
			case 6:
				result = Orientation.RIGHT_TOP;
				break;
			case 7:
				result = Orientation.RIGHT_BOTTOM;
				break;
			case 8:
				result = Orientation.LEFT_BOTTOM;
				break;
		}
		return result;
	}	
	
	/**
	 * Returns the PlanarConfig value used in the archive given
	 * the value found in the Tiff PlanarConfiguration field.
	 * 
	 * @param value	the value found in the Tiff PlanarConfiguration field
	 * @return the PlanarConfig value used in the archive
	 */
	public String mapPlanarConfig(int value) {
		String result = "";
		switch (value) {
			case 1 :
				result = TiffImage.PLANAR_CONFIG_CHUNKY;
				break;
			case 2:
				result = TiffImage.PLANAR_CONFIG_PLANAR;
				break;
		}
		return result;
	}	
	
	/**
	 * Returns the YcbcrPositioning value used in the archive given
	 * the value found in the Tiff YcbcrPositioning field.
	 * 
	 * @param value	the value found in the Tiff YcbcrPositioning field
	 * @return the YcbcrPositioning value used in the archive
	 */
	public String mapYcbcrPositioning(int value) {
		String result = "";
		switch (value) {
			case 1 :
				result = TiffImage.YCBCR_POSITION_CENTER;
				break;
			case 2:
				result = TiffImage.YCBCR_POSITION_COSITE;
				break;
		}
		return result;
	}
	
	/**
	 * Returns the YcbcrSubsampling value used in the archive given
	 * the value found in the Tiff YcbcrSubsampling field.
	 * 
	 * @param value	the value found in the Tiff YcbcrSubsampling field
	 * @return the YcbcrSubsampling value used in the archive
	 */
	public String mapYcbcrSubsampling(int value) {
		String result = "";
		switch (value) {
			case 1 :
				result = TiffImage.YCBCR_SUBSAMPLE_EQUAL;
				break;
			case 2:
				result = TiffImage.YCBCR_SUBSAMPLE_HALF;
				break;
			case 4:
				result = TiffImage.YCBCR_SUBSAMPLE_FOURTH;
				break;
		}
		return result;
	}
}
