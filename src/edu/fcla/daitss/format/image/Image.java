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
 * Image.java 	Created on Oct 30, 2003 
 */
package edu.fcla.daitss.format.image;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.severe.element.MetadataConflicts;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;

/**
 * RasterImage represents two-dimensional bitmap image data.
 */
public class Image extends Bitstream {
	
	/**
	 * The maximum number of characters (including the commas)
	 * that can exist in a comma-delimited list of the image's bits per sample.
	 * Only used to ensure that the list is compatible with the data
	 * type of the corresponding field in the archive database.
	 */
	private static final int MAX_BPS_LENGTH = 255;
	
	/**
	 * The maximum length of a Bitstream table name.
	 */
	private static final int MAX_BSTABLE_LENGTH = 64;
	
	/**
	 * The maximum number of characters (including the commas)
	 * that can exist in a comma-delimited list of the image's extra
	 * samples descriptions.
	 * Only used to ensure that the list is compatible with the data
	 * type of the corresponding field in the archive database.
	 */
	private static final int MAX_ES_LENGTH = 255;
	
	/**
	 * Maximum supported image height (vertical pixels).
	 */
	private static final long MAX_HEIGHT = 4294967295L;
	
	/**
	 * Maximum supported number of components per pixel.
	 */
	private static final int MAX_NUM_COMPS = 32767;
	
	/**
	 * Maximum supported horizontal (X) resolution.
	 */
	private static final double MAX_RES_X = 3.402823466e+38;
	
	/**
	 * Maximum supported vertical (Y) resolution.
	 */
	private static final double MAX_RES_Y = 3.402823466e+38;
	
	/**
	 * Maximum supported image width (horizontal pixels).
	 */
	private static final long MAX_WIDTH = 4294967295L;
	
	/**
	 * Minimum supported number of components per pixel.
	 * (means Not Applicable)
	 */
	private static final int MIN_NUM_COMPS = -1;
	
	/**
	 * Minimum supported horizontal (X) resolution.
	 * Means not applicable
	 */
	private static final double MIN_RES_X = -1;
	
	/**
	 * Minimum supported vertical (Y) resolution.
	 * Means not applicable
	 */
	private static final double MIN_RES_Y = -1;
	
	/**
	 * Measurement in centimeters
	 */
	public static final String RES_UNIT_CM = "CM";
	
	/**
	 * Measurement in inches
	 */
	public static final String RES_UNIT_INCH = "IN";
    
    /**
     * Measurement in meters
     */
    public static final String RES_UNIT_METER = "M";
	
	/**
	 * No absolute unit of measurement
	 */
	public static final String RES_UNIT_NONE = "NONE";

	/**
	 * Unknown absolute unit of measurement
	 */
	public static final String RES_UNIT_UNKNOWN = "UNKNOWN";
	
	/**
	 * Existence of a pixel sample used to store alpha data used for 
	 * transparancy.
	 */
	public static final String SAMPLE_DESC_ASSOC_ALPHA = "AA";
	
	/**
	 * Existence of a pixel sample used to store alpha data used for 
	 * transparancy.
	 */
	public static final String SAMPLE_DESC_UNASSOC_ALPHA = "UA";
	
	/**
	 * Existence of a non-color-related pixel sample that has an unknown 
	 * purpose.
	 */
	public static final String SAMPLE_DESC_UNKNOWN = "UNK";
	
	public static final long DEFAULT_DIMENSION = 0L;

	/**
	 * Test driver
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		Image img = 
			new Image(new DataFile("/apps/xerces-2_6_2/data/personal_nons_schema_sstarget.xml", 
			new SIP("/daitss/test/AB12345678")));
		Vector v = new Vector();
		for (int i = 0; i < 200; i++) {
			v.add(new Integer("8"));
		}
			
		img.setBitsPerSample(v);
	}
	
	/**
	 * A list of all known generic image anomalies, not necessarily found
	 * in this file
	 */
	private ImageAnomalies anomsPossible = null;
	
	/**
	 * List of the number of bits (bit depth) per component (could be 
	 * different values for each of the <code>numComponents</code> values). 
	 * Ex: for an image with <code>numComponents</code> = 3, 
	 * <code>bitsPerSample</code> could be {8,8,8}. Contains
	 * Integer objects.
	 */
	private Vector bitsPerSample = null;
	
	/**
	 * The name of a database table containing information specific to
	 * this image's type.
	 */
	private String bsImageTable = null;



	/**
	 * A red-green-blue color lookup table, only applicable if this is an indexed 
	 * (palette) image. In a palette-color image, a pixel value is used to index
	 * into an RGB lookup table. So a palette-color pixel having a value of 0
	 * would be displayed according to the 0th red, green and blue values.
	 * 
	 * Should be interpreted as the 0th through last red components, followed 
	 * by the 0th through last green components, followed by the 0th through 
	 * last blue components. The number of values in this array are 
	 * 3*(2^bitsPerSample), where bitsPerSample is one of the values in the 
	 * list of the <code>bitsPerSample</code> member, and these values are 
	 * all equal to each other. For example if this is an indexed image with a 
	 * <code>bitsPerSample</code> of {8,8,8}, then there are 
	 * 3*(2^8), or 768 values in this list. Contains Integer objects.
	 */
	private Vector colorMap = null;

	/**
	 * The color space of the image data. Valid values are contained in
	 * the ColorSpace class.
	 */
	private String colorSpace = ColorSpace.CS_UNKNOWN;

	/**
	 * A description of any non-color samples that exist for each pixel of the 
	 * image. Valid values include 
	 * <code>SAMPLE_DESC_ASSOC_ALPHA</code>, 
	 * <code>SAMPLE_DESC_UNASSOC_ALPHA</code> or 
	 * <code>SAMPLE_DESC_UNKNOWN</code>.
	 * 
	 * When this member is not equal to null, it means that the 
	 * <code>numComponents</code> member will have  a value larger than 
	 * if this member were equal to null. For example, if this were an RGB
	 * image that had <code>numComponents</code> equal to 4 (instead 
	 * of the typical 3), this member would not be equal to null, instead it would 
	 * describe the purpose of the one extra sample per pixel. 
	 * Contains String objects.
	 */
	private Vector extraSamples = null;
	
	/**
	 * Whether or not this image specifies an ICC profile internally.
	 */
	private String hasIccProfile = Bitstream.UNKNOWN;
	
	/**
	 * Whether or not this image specifies an internal palette.
	 */
	private String hasIntPalette = Bitstream.UNKNOWN;

	/**
	 * The maximum number of components per pixel. For example, an RGB Tiff image
	 * with no extra samples per pixel would have <code>numComponents</code>
	 * equal to 3. Some images, like images in YCbCr color space do not contain 
	 * data for each component for every pixel. Commonly, the Y component 
	 * exists for every pixel, but the Cb and Cr components exist for every
	 * other pixel or every fourth pixel. See <code>samplingHor</code> and 
	 * <code>samplingVer</code> for more about 'subsampling' (not sampling on
	 * every pixel for every component).
	 * The value -1 means that the number of components is unknown. The minimum
	 * value is -1, the maximum value is <code>MAX_NUM_COMPS</code>.
	 */
	private int numComponents = 0;

	/**
	 * The orientation of the image with respect to the rows and columns.
	 * Valid values are stored in the Orientation class.
	 * 
	 * @see edu.fcla.daitss.format.image.Orientation
	 */
	private String orientation = Orientation.UNKNOWN;

	/**
	 * The width of the image in pixels.
	 * Minimum width can be 0, which means that the width is unknown.
	 * The maximum supported width is <code>MAX_WIDTH</code> pixels.
	 */
	private long pixelsHorizontal = DEFAULT_DIMENSION;

	/**
	 * The height of the image (number of scanlines or rows).
	 * Minimum height can be 0, which means that the height is unknown.
	 * The maximum supported height is <code>MAX_HEIGHT</code> pixels.
	 */
	private long pixelsVertical = DEFAULT_DIMENSION;

	/**
	 * The unit of measurement for <code>xResolution</code>
	 * and <code>yResolution</code>. Valid values are 
	 * <code>RES_UNIT_NONE</code>,
	 * <code>RES_UNIT_INCH</code>,
	 * <code>RES_UNIT_CM</code>, or
	 * <code>RES_UNIT_UNKNOWN</code> (when this isn't set yet).
	 */
	private String resUnit = RES_UNIT_UNKNOWN;
	
	/**
	 * The sampling format used in terms of the components 
	 * (usually luminance and chrominance). 
	 * E.g. 4:2:0, 4:2:2, 2:4:4, etc in the horizontal (X) dimension.
	 */
	protected String samplingHor = Bitstream.UNKNOWN;

	/**
	 * The sampling format used in terms of the components 
	 * (usually luminance and chrominance). 
	 * E.g. 4:2:0, 4:2:2, 2:4:4, etc in the vertical (Y) dimension.
	 */
	protected String samplingVer = Bitstream.UNKNOWN;

	/**
	 * Number of pixels per <code>resUnit</code> in each row
	 * Minimum value: -1 (mean N/A).
	 * 0 means that it is unknown.
	 * Maximum value is <code>MAX_RES_X</code>.
	 */
	private double xResolution = 0; // start as 'unknown'

	/**
	 * Number of pixels per <code>resUnit</code> in each column
	 * Minimum value: -1 (mean N/A).
	 * 0 means that it is unknown.
	 * Maximum value is <code>MAX_RES_Y</code>.
	 */
	private double yResolution = 0; // start as 'unknown'
	
	/**
	 * Construct a Image, set some members, build some data structures.
	 * 
	 * @param df the file containing this image bitstream
	 * @throws FatalException
	 */
	public Image(DataFile df) throws FatalException {
		super(df);
		this.setBsTable(ArchiveDatabase.TABLE_BS_IMAGE);
		
		this.bitsPerSample = new Vector(); // not needed by tiffs
		this.colorMap = new Vector(); // not needed by tiffs
		this.extraSamples = new Vector(); // NEEDED by tiffs
		
		// build the list of possible image anomalies
		this.anomsPossible = new ImageAnomalies();
	}

	/**
	 * Adds a description of a non-color sample per pixel to the 
	 * <code>extraSamples</code> Vector.
	 * 
	 * @param sample	a description of an extra sample
	 * @throws FatalException
	 */
	public void addExtraSample(String sample) throws FatalException {
		if (!isValidExtraSample(sample)) {
				Informer.getInstance().fail(this, "addExtraSample(String)",
					"Illegal argument",
					"sample: " + sample,
					new FatalException("Not a valid extraSample value"));
		}
		// We are constructing this Vector here instead of in the constructor
		// because we want to keep the Vector null if it does not have extra
		// samples
		if (this.extraSamples == null) {
			this.extraSamples = new Vector();
		}
		
		this.extraSamples.add(new Integer(sample));
		
		// check that the length of this list will not be too long for the database
		// when it is converted to a comma-delimited list
		int numBits = 0;
		Iterator iter = extraSamples.iterator(); // we know it's non-null...
		boolean tooLong = false;
		while (!tooLong && iter.hasNext()) {
			String bits = (String) iter.next();
			//	add the number of characters in the bits for this component
			numBits += bits.length(); 
			if (iter.hasNext()) {
				numBits++; // add one for the comma
			}
			if (numBits > MAX_ES_LENGTH){
				// exceeds the database field for extra samples - note this
				tooLong = true;
				SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_EXTRA_SAMPLES);
				this.getDf().addAnomaly(ta);
			}
		}
		//System.out.println("Num characters for the es: " + numBits);
	}
	
	/**
	 * Compare the depth of the image with the value described in the descriptor
	 * @param _depth
	 * @throws FatalException
	 */
	protected void compareDepth(String _depth) throws FatalException{
		if (_depth != null) {
			// compare bitsPerSample (Depth)
			String[] _depths = _depth.split(",");
			Vector _depthsVec = new Vector();
	        for (int i = 0; i < _depths.length; i++) {
	        	_depthsVec.add((Integer) Integer.parseInt(_depths[i]));
       		}           
			//Integer _depthInt = Integer.parseInt(_depth);
			String bps = bitsPerSample.toString();
			String depths = _depthsVec.toString();
			if (!bps.equals(depths)) { 
	    		// record the type of metadata conflict
	    		getDf().addMetadataConflict(getDf().getMetadataConflictsPossible().getSevereElement(
	    			ImageMetadataConflicts.DEPTH_MISMATCH));
	            Informer.getInstance().warning(this,"compareDepth",
	                "image depth extracted from  " + getDf().getFileTitle() + " does not match submitted value. ",
	                getDf().getPath() + "\n\t\textracted value: " + bitsPerSample + "\n\t\tdescriptor value: " + _depth);
			}
		}
	}
	
	/**
	 * compare the colorspace of the image with the value described in the descriptor
	 * @param _colorspace
	 * @throws FatalException
	 */
	protected void compareColorspace(String _colorspace) throws FatalException{
		if (_colorspace != null) {
			if (colorSpace.equals(ColorSpace.CS_UNKNOWN)) {
				colorSpace = _colorspace;
			} else if (!colorSpace.toLowerCase().equals(_colorspace.toLowerCase())) {
	    		// record the type of metadata conflict
	    		getDf().addMetadataConflict(getDf().getMetadataConflictsPossible().getSevereElement(
	    			ImageMetadataConflicts.COLORSPACE_MISMATCH));
	            Informer.getInstance().warning(this,"compareColorspace",
	                "image colorspace extracted from  " + getDf().getFileTitle() + " does not match submitted value. ",
	                getDf().getPath() + "\n\t\textracted value: " + colorSpace + "\n\t\tdescriptor value: " + _colorspace);
			}
		}
	}
	
	/**
	 * compare the horizontal dimension of the image with the value described in the descriptor
	 * @param _dimX
	 * @throws FatalException
	 */
	protected void compareHorDimension(String _dimX) throws FatalException{
		if (_dimX != null) {
			Long dimXLong = new Long(_dimX);
			if (pixelsHorizontal == DEFAULT_DIMENSION) {
				pixelsHorizontal = dimXLong.longValue();
			} else if (pixelsHorizontal != dimXLong) {
	    		// record the type of metadata conflict
	    		getDf().addMetadataConflict(getDf().getMetadataConflictsPossible().getSevereElement(
	    			ImageMetadataConflicts.XDIMENSION_MISMATCH));
	            Informer.getInstance().warning(this,"compareHorDimension",
	                "image dimension extracted from  " + getDf().getFileTitle() + " does not match submitted value. ",
	                getDf().getPath() + "\n\t\textracted value: " + pixelsHorizontal + "\n\t\tdescriptor value: " + _dimX);
			}
		}
	}
	
	/**
	 * compare the vertical dimension of the image with the value described in the descriptor
	 * @param _dimY
	 * @throws FatalException
	 */
	protected void compareVertDimension(String _dimY) throws FatalException{
		if (_dimY != null) {
			Long dimYLong = new Long(_dimY);
			if (pixelsVertical == DEFAULT_DIMENSION) {
				pixelsVertical = dimYLong.longValue();
			} else if (pixelsVertical != dimYLong) {
	    		// record the type of metadata conflict
	    		getDf().addMetadataConflict(getDf().getMetadataConflictsPossible().getSevereElement(
	    			ImageMetadataConflicts.YDIMENSION_MISMATCH));
	            Informer.getInstance().warning(this,"compareHorDimension",
	                "image dimension extracted from  " + getDf().getFileTitle() + " does not match submitted value. ",
	                getDf().getPath() + "\n\t\textracted value: " + pixelsVertical + "\n\t\tdescriptor value: " + _dimY);
			}
		}
	}
	
	/**
	 * compare the image orientation with the value described in the descriptor
	 * @param _orientation
	 * @throws FatalException
	 */
	protected void compareOrientation(String _orientation) throws FatalException{
		if (_orientation != null) {
			if (orientation.equals(Orientation.UNKNOWN)) {
				orientation = _orientation;
			} else if (!orientation.toLowerCase().equals(_orientation.toLowerCase())) {
	    		// record the type of metadata conflict
	    		getDf().addMetadataConflict(getDf().getMetadataConflictsPossible().getSevereElement(
	    			ImageMetadataConflicts.ORIENTATION_MISMATCH));
	            Informer.getInstance().warning(this,"compareOrientation",
	                "image orientation extracted from  " + getDf().getFileTitle() + " does not match submitted value. ",
	                getDf().getPath() + "\n\t\textracted value: " + orientation + "\n\t\tdescriptor value: " + _orientation);
			}
		}
	}
	
	public void compareMetadata(METSDocument sipMetadata) throws FatalException{
        super.compareMetadata(sipMetadata);
                                                                                                                                                                                                                                                                          		
		// compare image depth
		compareDepth(sipMetadata.getImageDepth());
		
		// compare color space
		compareColorspace(sipMetadata.getImageColorspace());
		
		// compare horizontal dimension
		compareHorDimension(sipMetadata.getImageDimensionX());
		
		// compare vertical dimension
		compareVertDimension(sipMetadata.getImageDimensionY());
		
		// compare orientation
		compareOrientation(sipMetadata.getImageOrientation());
	}
	
	/**
	 * fill in the database column-value pairs for this image
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
	
		Vector bps;
		columns.add(ArchiveDatabase.COL_BS_IMAGE_BITS_PER_SAMPLE);
		values.add((bps = this.getBitsPerSample()) == null? null : bps.toString());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_BS_TABLE);
		values.add(this.getBsImageTable());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_COLOR_SPACE);
		values.add(this.getColorSpace());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_DFID);
		values.add(this.getDfid());
		Vector esmpls;
		columns.add(ArchiveDatabase.COL_BS_IMAGE_EXTRA_SAMPLES);
		values.add((esmpls = this.getExtraSamples())==null ? null : esmpls.toString());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_HAS_ICC_PROFILE);
		values.add(this.hasIccProfile());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_HAS_INTERNAL_CLUT);
		values.add(this.hasIntPalette());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_IMAGE_LENGTH);
		values.add(new Long(this.getPixelsVertical()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_IMAGE_WIDTH);
		values.add(new Long(this.getPixelsHorizontal()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_NUM_COMPONENTS);
		values.add(new Integer(this.getNumComponents()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_ORIENTATION);
		values.add(this.getOrientation());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_RES_HORZ);
		values.add(new Double(this.getXResolution()));	
		columns.add(ArchiveDatabase.COL_BS_IMAGE_RES_UNIT);
		values.add(this.getResUnit());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_RES_VERT);
		values.add(new Double(this.getYResolution()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_SAMPLING_HOR);
		values.add(this.getSamplingHor());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_SAMPLING_VER);
		values.add(this.getSamplingVer());
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
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_IMAGE, columns, colValues);
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
				ArchiveDatabase.COL_BS_IMAGE_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_IMAGE, columns, colValues, whereClause);
	}
	

	public Document toXML()throws FatalException {
	    
	    Document doc =  super.toXML();
	    Element rootElement = doc.getDocumentElement();
	    String namespace = rootElement.getNamespaceURI();
	    
	    /* Bs Image */
	    Element bsImageElement = doc.createElementNS(namespace, "BS_IMAGE");
	    rootElement.appendChild(bsImageElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsImageElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsImageElement.appendChild(bsidElement);

	    /* Bits Per Sample */
	    Element bitsPerSampleElement = doc.createElementNS(namespace, "BITS_PER_SAMPLE");
	    String bitsPerSampleValue = (this.getBitsPerSample() != null ? this.getBitsPerSample().toString() : "" );
	    bitsPerSampleValue = bitsPerSampleValue.replaceAll("^\\[|\\]$", "");
	    Text bitsPerSampleText = doc.createTextNode(bitsPerSampleValue);
	    bitsPerSampleElement.appendChild(bitsPerSampleText);
	    bsImageElement.appendChild(bitsPerSampleElement);

//	    /* Has Chromaticities */
//	    Element hasChromaticitiesElement = doc.createElementNS(namespace, "HAS_CHROMATICITIES");
//	    String hasChromaticitiesValue = (this.getHasChromaticities() != null ? this.getHasChromaticities() : "" );
//	    Text hasChromaticitiesText = doc.createTextNode(hasChromaticitiesValue);
//	    hasChromaticitiesElement.appendChild(hasChromaticitiesText);
//	    bsImageElement.appendChild(hasChromaticitiesElement);

	    /* Has Internal Clut */
	    Element hasInternalClutElement = doc.createElementNS(namespace, "HAS_INTERNAL_CLUT");
	    String hasInternalClutValue = (this.hasIntPalette() != null ? this.hasIntPalette() : "" );
	    Text hasInternalClutText = doc.createTextNode(hasInternalClutValue);
	    hasInternalClutElement.appendChild(hasInternalClutText);
	    bsImageElement.appendChild(hasInternalClutElement);

	    /* Extra Samples */
	    Element extraSamplesElement = doc.createElementNS(namespace, "EXTRA_SAMPLES");
	    String extraSamplesValue = (this.getExtraSamples() != null ? this.getExtraSamples().toString() : "" );
	    extraSamplesValue = extraSamplesValue.replaceAll("^\\[|\\]$", "");
	    Text extraSamplesText = doc.createTextNode(extraSamplesValue);
	    extraSamplesElement.appendChild(extraSamplesText);
	    bsImageElement.appendChild(extraSamplesElement);

	    /* Orientation */
	    Element orientationElement = doc.createElementNS(namespace, "ORIENTATION");
	    String orientationValue = (this.getOrientation() != null ? this.getOrientation() : "" );
	    Text orientationText = doc.createTextNode(orientationValue);
	    orientationElement.appendChild(orientationText);
	    bsImageElement.appendChild(orientationElement);

	    /* Color Space */
	    Element colorSpaceElement = doc.createElementNS(namespace, "COLOR_SPACE");
	    String colorSpaceValue = (this.getColorSpace() != null ? this.getColorSpace() : "" );
	    Text colorSpaceText = doc.createTextNode(colorSpaceValue);
	    colorSpaceElement.appendChild(colorSpaceText);
	    bsImageElement.appendChild(colorSpaceElement);

	    /* Image Width */
	    Element imageWidthElement = doc.createElementNS(namespace, "IMAGE_WIDTH");
	    String imageWidthValue = Long.toString(this.getPixelsVertical());
	    Text imageWidthText = doc.createTextNode(imageWidthValue);
	    imageWidthElement.appendChild(imageWidthText);
	    bsImageElement.appendChild(imageWidthElement);

	    /* Image Length */
	    Element imageLengthElement = doc.createElementNS(namespace, "IMAGE_LENGTH");
	    String imageLengthValue = Long.toString(this.getPixelsVertical());
	    Text imageLengthText = doc.createTextNode(imageLengthValue);
	    imageLengthElement.appendChild(imageLengthText);
	    bsImageElement.appendChild(imageLengthElement);

	    /* Has Icc Profile */
	    Element hasIccProfileElement = doc.createElementNS(namespace, "HAS_ICC_PROFILE");
	    String hasIccProfileValue = (this.hasIccProfile() != null ? this.hasIccProfile() : "" );
	    Text hasIccProfileText = doc.createTextNode(hasIccProfileValue);
	    hasIccProfileElement.appendChild(hasIccProfileText);
	    bsImageElement.appendChild(hasIccProfileElement);

	    /* Num Components */
	    Element numComponentsElement = doc.createElementNS(namespace, "NUM_COMPONENTS");
	    String numComponentsValue = Integer.toString(this.getNumComponents());
	    Text numComponentsText = doc.createTextNode(numComponentsValue);
	    numComponentsElement.appendChild(numComponentsText);
	    bsImageElement.appendChild(numComponentsElement);

	    /* Res Unit */
	    Element resUnitElement = doc.createElementNS(namespace, "RES_UNIT");
	    String resUnitValue = (this.getResUnit() != null ? this.getResUnit() : "" );
	    Text resUnitText = doc.createTextNode(resUnitValue);
	    resUnitElement.appendChild(resUnitText);
	    bsImageElement.appendChild(resUnitElement);

	    /* Res Horz */
	    Element resHorzElement = doc.createElementNS(namespace, "RES_HORZ");
	    String resHorzValue = Long.toString(this.pixelsHorizontal);
	    Text resHorzText = doc.createTextNode(resHorzValue);
	    resHorzElement.appendChild(resHorzText);
	    bsImageElement.appendChild(resHorzElement);
	    
	    /* Res Vert */
	    Element resVertElement = doc.createElementNS(namespace, "RES_VERT");
	    String resVertValue = Long.toString(this.pixelsVertical);
	    Text resVertText = doc.createTextNode(resVertValue);
	    resVertElement.appendChild(resVertText);
	    bsImageElement.appendChild(resVertElement);
	    
	    /* Sampling Hor */
	    Element samplingHorElement = doc.createElementNS(namespace, "SAMPLING_HOR");
	    String samplingHorValue = (this.getSamplingHor() != null ? this.getSamplingHor() : "" );
	    Text samplingHorText = doc.createTextNode(samplingHorValue);
	    samplingHorElement.appendChild(samplingHorText);
	    bsImageElement.appendChild(samplingHorElement);

	    /* Sampling Ver */
	    Element samplingVerElement = doc.createElementNS(namespace, "SAMPLING_VER");
	    String samplingVerValue = (this.getSamplingVer() != null ? this.getSamplingVer() : "" );
	    Text samplingVerText = doc.createTextNode(samplingVerValue);
	    samplingVerElement.appendChild(samplingVerText);
	    bsImageElement.appendChild(samplingVerElement);

	    /* Bs Table */
	    Element bsTableElement = doc.createElementNS(namespace, "BS_TABLE");
	    String bsTableValue = (this.getBsImageTable() != null ? this.getBsImageTable() : "" );
	    Text bsTableText = doc.createTextNode(bsTableValue);
	    bsTableElement.appendChild(bsTableText);
	    bsImageElement.appendChild(bsTableElement);

	    return doc;
	}
	
	/**
	 * Returns a list of the number of bits (bit depth) per component . 
	 * 
	 * @return	a list of the number of bits (bit depth) per component
	 */
	public Vector getBitsPerSample() {
		return this.bitsPerSample;
	}        

	/**
	 * Returns the name of a database table containing information specific to
	 * this image's type.
	 * 
	 * @return the name of a database table containing information specific to
	 * this image's type
	 */
	public String getBsImageTable() {
		return this.bsImageTable;
	}      

	/**
	 * Returns a red-green-blue color lookup table, only applicable if this is an 
	 * indexed (palette) image.
	 * 
	 * @return	the color lookup table, or an empty (not null) array if not available or not 
	 * 				applicable
	 */
	public Vector getColorMap() {
		return this.colorMap;
	}         

	/**
	 * Returns the color space of the image data.
	 * 
	 * @return	the color space of the image data
	 */
	public String getColorSpace() {
		return this.colorSpace;
	}         

	/**
	 * Returns a description of any non-color samples that exist for each 
	 * pixel of the  image.
	 * 
	 * @return	a description of any non-color samples that exist for each
	 * 				pixel in the image, or -1 if none exist.
	 */
	public Vector getExtraSamples() {
		return this.extraSamples;
	}              

	/**
	 * Returns the maximum number of components per pixel.
	 * 
	 * @return	the maximum number of components per pixel
	 */
	public int getNumComponents() {
		return this.numComponents;
	}         

	/**
	 * Returns the orientation of the image with respect to the rows and columns.
	 * 
	 * @return	the orientation of the image
	 */
	public String getOrientation() {
		return this.orientation;
	}        

	/**
	 * Returns the width of the image in pixels (number of columns).
	 * 
	 * @return	the image's number of columns (pixels)
	 */
	public long getPixelsHorizontal() {
		return this.pixelsHorizontal;
	}         

	/**
	 * Returns the length of the image (number of scanlines or rows).
	 * 
	 * @return	the image's number of scanlines (rows)
	 */
	public long getPixelsVertical() {
		return this.pixelsVertical;
	}               

	/**
	 * Returns the unit of measurement for <code>xResolution</code>
	 * and <code>yResolution</code>.
	 * 
	 * @return	the unit of measurement for <code>xResolution</code>
	 * 				and <code>yResolution</code>
	 * @see Image#xResolution
	 * @see Image#yResolution
	 */
	public String getResUnit() {
		return this.resUnit;
	}        
	
	/**
	 * @return the sampling format used in terms of luminance and chrominance, 
	 * e.g. 4:2:0, 4:2:2, 2:4:4, etc in the horizontal (X) dimension
	 */
	public String getSamplingHor() {
		return this.samplingHor;
	}

	/**
	 * @return the sampling format used in terms of luminance and chrominance, 
	 * e.g. 4:2:0, 4:2:2, 2:4:4, etc in the vertical (Y) dimension
	 */
	public String getSamplingVer() {
		return this.samplingVer;
	}



	/**
	 * Returns the number of pixels per <code>resUnit</code> in each row
	 * 
	 * @return the number of pixels per <code>resUnit</code> in each row
	 */
	public double getXResolution() {
		return this.xResolution;
	}

      

	/**
	 * Returns the number of pixels per <code>resUnit</code> in each column
	 * 
	 * @return the number of pixels per <code>resUnit</code> in each column
	 */
	public double getYResolution() {
		return this.yResolution;
	}

	/**
	 * Returns whether or not this image specifies an ICC profile internally.
	 * 
	 * @return whether or not this image specifies an ICC profile internally
	 */
	public String hasIccProfile() {
		return this.hasIccProfile;
	}

	/**
	 * Returns whether or not this image specifies an internal palette.
	 * 
	 * @return whether or not this image specifies an internal palette
	 */
	public String hasIntPalette() {
		return this.hasIntPalette;
	}
	
	/**
	 * Determines whether or not an extraSamples value is valid.
	 * 
	 * @param sample	an extraSamples value
	 * @return	<code>true</code> if its valid, else <code>false</code>
	 */
	public boolean isValidExtraSample(String sample){
		if (sample.equals(SAMPLE_DESC_ASSOC_ALPHA) ||
			sample.equals(SAMPLE_DESC_UNASSOC_ALPHA) ||
			sample.equals(SAMPLE_DESC_UNKNOWN)){
				return true;
		}
		return false;
	}

	/**
	 * Sets a list of the number of bits (bit depth) per component. 
	 * Ex: for an image with <code>numComponents</code> = 3, 
	 * <code>bitsPerSample</code> could be {8,8,8}.
	 * Note that there is a limit to the number of values that can exist in
	 * this list because it has to be compatible with the archive database.
	 * The values in the vector will be converted to a comma-delimited
	 * list of numbers; and this list can not exceed 255 characters including
	 * the commas. So in effect, this list can have 128 single-digit numbers,
	 * or 85 2-digit numbers, or 51 3-digit numbers. Images are not known 
	 * to carry that many components per pixel so we should not run up to 
	 * this limit. If it does go over the limit the anomaly will be stored in the
	 * DataFile, an info record will be logged, and the list will be truncated.
	 * 
	 * @param _bitsPerSample	the list of bit depths per component per pixel
	 * @throws FatalException
	 */
	public void setBitsPerSample(Vector _bitsPerSample) 
		throws FatalException {
		String methodName = "setBitsPerSample(Vector)";
		
		// check that the argument is not null
		this.checkForNullObjectArg(
			"setBitsPerSample(String _bitsPerSample)",
			"_bitsPerSample",
			_bitsPerSample, this.getClass().getName());
			
		// check that the length of this list will not be too long for the database
		// when it is converted to a comma-delimited list
		int numBits = 0;
		Vector shorterList = new Vector(); // in case we need to use a shorter list
		Iterator iter = _bitsPerSample.iterator();
		boolean tooLong = false;
		while (!tooLong && iter.hasNext()) {
			Integer theValue = (Integer) iter.next();
			shorterList.add(theValue);
			String bits = theValue.toString();
			//	add the number of characters in the bits for this component
			numBits += bits.length(); 
			if (iter.hasNext()) {
				numBits++; // add one for the comma
			}
			if (numBits > MAX_BPS_LENGTH){
				// exceeds the data limits for the bits per sample field - note this
				tooLong = true;
				SevereElement ta = 
					this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_BITS_PER_SAMPLE);
				this.getDf().addAnomaly(ta);
				
				// switch over to using the shorter list
				// remove the last value that put it over the edge
				shorterList.remove(theValue);
				
				Informer.getInstance().warning(
					this,
					methodName,
					"Overlimit: Truncating list of bitsPerSample (too long)",
					"file: " + this.getDf().getPath());		
					
				_bitsPerSample = shorterList;	
			}
		}
		//System.out.println("Num characters for the bps: " + numBits);
		this.bitsPerSample = _bitsPerSample;
		//System.out.println("Storing size: " + this.bitsPerSample.size());
	}         

	/**
	 * Sets the name of a database table containing information specific to
	 * this image's type.
	 * 
	 * @param _bsTable the name of a database table containing information 
	 * specific to this image's type
	 * @throws FatalException
	 */
	public void setBsImageTable(String _bsTable) throws FatalException {
		//	check that the argument is not null
		if (_bsTable == null || 
			_bsTable.length() > MAX_BSTABLE_LENGTH ||
			!ArchiveDatabase.isValidImageBitstreamTable(_bsTable)){
				// illegal bitstream image table name
				Informer.getInstance().fail(this, "setBsImageTable(String)",
				"Illegal argument",
				"_bsTable: " + _bsTable,
				new FatalException("Not a valid bitstream image table name"));			
		}		
		bsImageTable = _bsTable;
	}

     

	/**
	 * Sets the red-green-blue color lookup table, only applicable if this is an 
	 * indexed (palette) image.
	 * 
	 * Should be  interpreted as the 0th through last red components, followed 
	 * by the 0th through last green components, followed by the 0th through 
	 * last blue components. The number of values in this list are 
	 * 3*(2^bitsPerSample), where bitsPerSample is one of the values in 
	 * the list of the <code>bitsPerSample</code> member, and these values 
	 * are all equal to each other. For example if this is an indexed image with a 
	 * <code>bitsPerSample</code> of {8,8,8}, then there are 
	 * 3*(2^8), or 768 values in this list.
	 * 
	 * @param _colorMap	the RGB color table (reds, then greens, then blues)
	 * @throws FatalException
	 */
	public void setColorMap(Vector _colorMap) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setColorMap(String _colorMap)", "_colorMap", _colorMap, this.getClass().getName());
		this.colorMap = _colorMap;
	}         

	/**
	 * Sets the color space of the image data. 
	 * 
	 * @param _colorSpace	the image data's color space
	 * @throws FatalException
	 */
	public void setColorSpace(String _colorSpace) throws FatalException {
		if (!ColorSpace.isValidColorSpace(_colorSpace)) {
			Informer.getInstance().fail(
				this, "setColorSpace(String)",
				"Illegal argument",
				"_colorSpace: " + _colorSpace,
				new FatalException("Not a valid color space"));
		}
		this.colorSpace = _colorSpace;
	}          

	/**
	 * Sets whether or not this image specifies an ICC profile internally.
	 * 
	 * @param _hasICCProfile <code>TRUE</code> if this image specifies an ICC
	 * 	profile internally, <code>FALSE</code> if it does not, or 
	 * 	<code>UNKNOWN</code> if it is not known if it does or not.
	 * @throws FatalException
	 */
	public void setHasIccProfile(String _hasICCProfile) throws FatalException {
		if (_hasICCProfile != null
			&& (_hasICCProfile.equals(Bitstream.FALSE)
				|| _hasICCProfile.equals(Bitstream.TRUE)
				|| _hasICCProfile.equals(Bitstream.UNKNOWN))) {
			this.hasIccProfile = _hasICCProfile;
		} else {
			Informer.getInstance().fail(
				this,
				"setHasICCProfile(String)",
				"Illegal argument",
				"_hasICCProfile: " + _hasICCProfile,
				new FatalException("Not a valid hasIccProfile"));
		}
	}

	/**
	 * Sets whether or not this image specifies an internal palette
	 * 
	 * @param _hasIntPal <code>TRUE</code> if this image specifies an internal palette, 
	 * <code>FALSE</code> if it does not, or <code>UNKNOWN</code>
	 * 	if it is unknown.
	 * @throws FatalException
	 */
	public void setHasIntPalette(String _hasIntPal) 
		throws FatalException {
		String methodName = "setHasIntPalette(String)";
		
		if (_hasIntPal != null && 
			(_hasIntPal.equals(Bitstream.FALSE) || _hasIntPal.equals(Bitstream.TRUE) ||
					_hasIntPal.equals(Bitstream.UNKNOWN))) {
				this.hasIntPalette = _hasIntPal;
		} else {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument",
				"_hasIntPal: " + _hasIntPal,
				new FatalException("Not a valid _hasIntPal"));
		}
		hasIntPalette = _hasIntPal;
	}

	/**
	 * Sets the number of components per pixel. For example, an RGB image
	 * with no extra samples per pixel would have <code>numComponents</code>
	 * equal to 3. There is a maximum number of components the storage
	 * can support. When this method is called with a number over that limit,
	 * an anomaly is added to the DataFile, a warning is logged, and the
	 * value is set to the maximum supported value.
	 * 
	 * @param	_samplesPerPixel	the number of components per pixel
	 * @throws FatalException
	 */
	public void setNumComponents(int _samplesPerPixel) throws FatalException {
		String methodName = "setNumComponents(int)";
		
		if (_samplesPerPixel < MIN_NUM_COMPS){
			Informer.getInstance().fail(
				this, 
				methodName,
				"Illegal argument",
				"_samplesPerPixel: " + _samplesPerPixel,
				new FatalException("Not a valid samples per pixel value"));		
		}
		if (_samplesPerPixel > MAX_NUM_COMPS) {
			// can't support its large number of components - note this anomaly
			// log a warning and reduce the number of components to the max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_NUM_COMPONENTS);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing number of components (too many)",
				"file: " + this.getDf().getPath());
			_samplesPerPixel = MAX_NUM_COMPS;
		}
		this.numComponents = _samplesPerPixel;
	}         

	/**
	 * Sets the orientation of the image with respect to the rows and columns.
	 * Valid values are stored in the <code>Orientation</code> class.
	 * 
	 * @param _orientation the image orientation
	 * @throws FatalException
	 */
	public void setOrientation(String _orientation) throws FatalException {
		if (!Orientation.isValidOrientation(_orientation)){
				Informer.getInstance().fail(
					this, "setOrientation(String)",
					"Illegal argument",
					"_orientation: " + _orientation,
					new FatalException("Not a valid orientation"));
		}
		this.orientation = _orientation;
	}         



	/**
	 * Sets the width of the image in pixels (number of columns).
	 * 
	 * @param _pixelsHorizontal	the image's number of columns
	 * @throws FatalException
	 */
	public void setPixelsHorizontal(long _pixelsHorizontal) throws FatalException {
		String methodName = "setPixelsHorizontal(long)";
		
		if (_pixelsHorizontal < 0) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument",
				"_pixelsHorizontal: " + _pixelsHorizontal,
				new FatalException("Image width can't be less than 0"));
		}
		if (_pixelsHorizontal > MAX_WIDTH){
			// we can't support it's large width. note the anomaly, log a warning
			// and reduce the value to the max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_IMAGE_WIDTH);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing hor pixels (too large)",
				"file: " + this.getDf().getPath());
			_pixelsHorizontal = MAX_WIDTH;	
		}
		this.pixelsHorizontal = _pixelsHorizontal;
	}         

	/**
	 * Sets the height of the image (number of scanlines or rows).
	 * 
	 * @param _pixelsVertical	the image's number of scanlines
	 * @throws FatalException
	 */
	public void setPixelsVertical(long _pixelsVertical) throws FatalException {
		String methodName = "setPixelsVertical(long)";
		
		if (_pixelsVertical < 0) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument",
				"_pixelsVertical: " + _pixelsVertical,
				new FatalException("Image height can't be less than 0"));
		}
		if (_pixelsVertical > MAX_HEIGHT){
			// we can't support it's large height. note the anomaly, log a warning
			// and reduce the value to the max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_IMAGE_HEIGHT);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing vert pixels (too large)",
				"file: " + this.getDf().getPath());
			_pixelsVertical = MAX_HEIGHT;
		}
		this.pixelsVertical = _pixelsVertical;
	}        

	/**
	 * Sets the unit of measurement for <code>xResolution</code>
	 * and <code>yResolution</code>. Valid values are 
	 * <code>RES_UNIT_NONE</code>,
	 * <code>RES_UNIT_INCH</code>, 
	 * <code>RES_UNIT_CM</code> or
	 * <code>RES_UNIT_UNKNOWN</code.
	 * 
	 * @param _resUnit	the unit of measurement for <code>xResolution</code>
	 * 												and <code>yResolution</code>
	 * @see Image#xResolution
	 * @see Image#yResolution
	 * @throws FatalException
	 */
	public void setResUnit(String _resUnit) throws FatalException {
		if (_resUnit == null || 
				(!_resUnit.equals(RES_UNIT_CM) &&
				!_resUnit.equals(RES_UNIT_INCH) &&
                !_resUnit.equals(RES_UNIT_METER) &&
				!_resUnit.equals(RES_UNIT_NONE) &&
				!_resUnit.equals(RES_UNIT_UNKNOWN))) {
				Informer.getInstance().fail(
					this, "setResUnit(String)",
					"Illegal argument",
					"_resUnit: " + _resUnit,
					new FatalException("Not a valid resolution unit value"));				
			}
		this.resUnit = _resUnit;
	}
	
	/**
	 * @param _samplingHor the sampling format used in terms of luminance and chrominance, 
	 * e.g. 4:2:0, 4:2:2, 2:4:4, etc in the horizontal (X) dimension
	 * @throws FatalException
	 */
	public void setSamplingHor(String _samplingHor) 
		throws FatalException{
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setSamplingHor(String _samplingHor)", "_samplingHor", 
			_samplingHor, this.getClass().getName());
		this.samplingHor = _samplingHor;
	}

	/**
	 * @param _samplingVer the sampling format used in terms of luminance and chrominance, 
	 * e.g. 4:2:0, 4:2:2, 2:4:4, etc in the vertical (Y) dimension
	 * @throws FatalException
	 */
	public void setSamplingVer(String _samplingVer) throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg(
			"setSamplingVer(String _samplingVer)", "_samplingVer", 
		_samplingVer, this.getClass().getName());
		this.samplingVer = _samplingVer;
	}

	/**
	 * Sets the number of pixels per <code>resUnit</code> in each row,
	 * which must be greater or equal to 0.
	 * 
	 * @param _xResolution	the number of pixels per 
	 * 						<code>resUnit</code> in each row
	 * @throws FatalException
	 */
	public void setXResolution(double _xResolution) throws FatalException {
		String methodName = "setXResolution(double)";
		
		if (_xResolution < MIN_RES_X) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument",
				"_xResolution: " + _xResolution,
				new FatalException("Not a valid X resolution"));			
		}
		if (_xResolution > MAX_RES_X){
			// can't support this high a resolution - note the anomaly, log a
			// warning and reduce value to max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_RES_HORZ);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing x resolution (too large)",
				"file: " + this.getDf().getPath());
			_xResolution = MAX_RES_X;			
		}
		this.xResolution = _xResolution;
	}

        

	/**
	 * Sets the number of pixels per <code>resUnit</code> in each column.
	 * 
	 * @param _yResolution	the number of pixels per 
	 * 						<code>resUnit</code> in each column
	 * @throws FatalException
	 */
	public void setYResolution(double _yResolution) throws FatalException {
		String methodName = "setYResolution(double)";
		
		if (_yResolution < MIN_RES_Y) {
			Informer.getInstance().fail(
				this, "setYResolution(double)",
				"Illegal argument",
				"_yResolution: " + _yResolution,
				new FatalException("Not a valid Y resolution"));			
		}
		if (_yResolution > MAX_RES_Y){
			// can't support this high a resolution - note the anomaly, log a
			// warning and reduce value to max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_RES_VERT);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing y resolution (too large)",
				"file: " + this.getDf().getPath());
			_yResolution = MAX_RES_Y;
		}
		this.yResolution = _yResolution;
	}
	
	/**
	 * Returns the members of this class.
	 * 
	 * @return the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		Iterator iter = null;
		int count = 0;
			
		sb.append("\tBitsPerSample: " );
		if (this.bitsPerSample != null && bitsPerSample.size() > 0){
			sb.append("\n");
			iter = bitsPerSample.iterator();
			count = 0;
			while (iter.hasNext()){
				sb.append("\t\tSample " + count + ": " + iter.next() + "\n");
				count++;
			}
		} else {
			sb.append(getBitsPerSample() + "\n");
		}
		sb.append("\tBitstream Image Table: " + getBsImageTable() + "\n");
		/*sb.append("\tColorMap: ");
		if (this.colorMap != null && colorMap.size() > 0){
			sb.append("\n");
			iter = colorMap.iterator();
			count = 0;
			while(iter.hasNext()){
				sb.append("\t\tColor " + count + ": " + (Integer)iter.next() + "\n");
				count++;
			}
		} else {
			sb.append(getColorMap() + "\n");
		}*/
		sb.append("\tColorSpace: " + getColorSpace() + "\n");
		sb.append("\tExtraSamples: ");
		if (extraSamples != null && extraSamples.size() > 0){
			sb.append("\n");
			iter = extraSamples.iterator();
			count = 0;
			while(iter.hasNext()){
				sb.append("\t\tExtraSample Description " + count + ": " + iter.next() + "\n");
				count++;
			}
		}else {
			sb.append(getExtraSamples() + "\n");
		}
		sb.append("\tHasIntPalette: " + hasIntPalette() + "\n");
		sb.append("\tHasICCProfile: " + hasIccProfile() + "\n");
		sb.append("\tNumComponents: " + getNumComponents() + "\n");
		sb.append("\tOrientation: " + getOrientation() + "\n");
		sb.append("\tPixelsHorizontal: " + getPixelsHorizontal() + "\n");
		sb.append("\tPixelsVertical: " + getPixelsVertical() + "\n");
		sb.append("\tResUnit: " + getResUnit() + "\n");
		sb.append("\tSampling horizontal: " + this.getSamplingHor() + "\n");
		sb.append("\tSampling vertical: " + this.getSamplingVer() + "\n");
		sb.append("\tXResolution: " + getXResolution() + "\n");		
		sb.append("\tYResolution: " + getYResolution() + "\n");
		return sb.toString();
	}
}
