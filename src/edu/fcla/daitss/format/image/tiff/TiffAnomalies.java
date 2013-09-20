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

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.image.ImageAnomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * TiffAnomalies represent anomalies that are specific to Tiff files.
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class TiffAnomalies extends ImageAnomalies{
	
	/**
	 * not the correct format for a Tiff date/time
	 */
	public static final String TIFF_BAD_DATETIME = "A_TIFF_BAD_DATETIME";
	
	/**
	 * has a negative number of values (count) in a Tiff field
	 */
	public static final String TIFF_NEG_FIELD_COUNT = "A_TIFF_NEG_FIELD_COUNT";
	
	/**
	 * has a negative offset into the file
	 */
	public static final String TIFF_NEG_FILE_OFFSET = "A_TIFF_NEG_FILE_OFFSET";
	
	/**
	 * has a negative tag number
	 */
	public static final String TIFF_NEG_TAG = "A_TIFF_NEG_TAG";
	
	/**
	 * no IFD in the file
	 */
	public static final String TIFF_NO_IFD = "A_TIFF_NO_IFD";
	
	/**
	 * offset data exceeds the file size
	 */
	public static final String TIFF_OFFSET_TOO_BIG = "A_TIFF_OFFSET_TOO_BIG";
	
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.MAX_STRIP_BYTES
	 */
	public static final String TIFF_OVRLMT_MAX_STRIP_BYTES = 
		"A_TIFF_OVRLMT_MAX_STRIP_BYTES";
		
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.MAX_TILE_BYTES
	 */
	public static final String TIFF_OVRLMT_MAX_TILE_BYTES = 
		"A_TIFF_OVRLMT_MAX_TILE_BYTES";
		
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.NUM_STRIPS
	 */
	public static final String TIFF_OVRLMT_NUM_STRIPS = 
		"A_TIFF_OVRLMT_NUM_STRIPS";
		
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.NUM_TILES
	 */
	public static final String TIFF_OVRLMT_NUM_TILES = "A_TIFF_OVRLMT_NUM_TILES";
	
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.ROWS_PER_STRIP
	 */
	public static final String TIFF_OVRLMT_ROWS_PER_STRIP = 
		"A_TIFF_OVRLMT_ROWS_PER_STRIP";
		
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.TILE_LENGTH
	 */
	public static final String TIFF_OVRLMT_TILE_LENGTH = 
		"A_TIFF_OVRLMT_TILE_LENGTH";
		
	/**
	 * over the data size limit for the field BS_IMAGE_TIFF.TILE_WIDTH
	 */
	public static final String TIFF_OVRLMT_TILE_WIDTH = 
		"A_TIFF_OVRLMT_TILE_WIDTH";
		
	/**
	 * an IFD without any Tiff tags
	 */
	public static final String TIFF_TAGLESS_IFD = "A_TIFF_TAGLESS_IFD";
	
	/**
	 * not a known GrayResponseUnit value
	 */
	public static final String TIFF_UNKNOWN_GRUNIT = "A_TIFF_UNKNOWN_GRUNIT";
	
	/**
	 * not a known PlanarConfiguration value
	 */
	public static final String TIFF_UNKNOWN_PLANCONFIG = 
		"A_TIFF_UNKNOWN_PLANCONFIG";
		
	/**
	 * an unknown tiff tag number
	 */
	public static final String TIFF_UNKNOWN_TAG = "A_TIFF_UNKNOWN_TAG";
	
	/**
	 * an unexpected data type for a tag
	 */
	public static final String TIFF_UNKNOWN_TYPE = "A_TIFF_UNKNOWN_TYPE";
	
	/**
	 * not a known YCbCRPositioning value
	 */
	public static final String TIFF_UNKNOWN_YCBCRPOSITIONING = 
		"A_TIFF_UNKNOWN_YCBCRPOSITIONING";
		
	/**
	 * not a known YCbCRSubsampling
	 */
	public static final String TIFF_UNKNOWN_YCBCRSUBSAMPLING = 
		"A_TIFF_UNKNOWN_YCBCRSUBSAMPLING";
		
	/**
	 * tags are not in ascending order
	 */
	public static final String TIFF_UNSORTED_TAGS = "A_TIFF_UNSORTED_TAGS";
	
	/**
	 * an unexpected data type for a tag
	 */
	public static final String TIFF_WRONG_COUNT = "A_TIFF_WRONG_COUNT";
	
	/**
	 * an unexpected data type for a tag
	 */
	public static final String TIFF_WRONG_TYPE = "A_TIFF_WRONG_TYPE";

	/**
	 * Test driver.
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//TiffAnomalies ta = new TiffAnomalies();
	}

	/**
	 * Builds the list of known Tiff-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public TiffAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known Tiff-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// not the correct format for a Tiff date/time
		insert(TIFF_BAD_DATETIME,
			Severity.SEVERITY_NOTE,
			"Doesn't use the correct format for date/time");
		// has a negative number of values (count) in a Tiff field
		insert(TIFF_NEG_FIELD_COUNT,
			Severity.SEVERITY_NOTE,
			"Says it has a negative number of values for a field - impossible.");
		// has a negative offset into the file
		insert(TIFF_NEG_FILE_OFFSET,
			Severity.SEVERITY_NOTE,
			"Has a negative offset into the file - impossible.");
		// has a negative tag number
		insert(TIFF_NEG_TAG,
			Severity.SEVERITY_NOTE,
			"Has a negative tag number.");
		// no IFD in the file
		insert(TIFF_NO_IFD,
			Severity.SEVERITY_NOTE,
			"Does not have any of the most basic internal metadata (IFDs)");
		// offset data exceeds the file size
		insert(TIFF_OFFSET_TOO_BIG,
			Severity.SEVERITY_NOTE,
			"Offset data exceeds file size.");
		// over the data size limit for the field BS_IMAGE_TIFF.MAX_STRIP_BYTES
		insert(TIFF_OVRLMT_MAX_STRIP_BYTES,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for MAX_STRIP_BYTES");
		// over the data size limit for the field BS_IMAGE_TIFF.MAX_TILE_BYTES
		insert(TIFF_OVRLMT_MAX_TILE_BYTES,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for MAX_TILE_BYTES");
		// over the data size limit for the field BS_IMAGE_TIFF.NUM_STRIPS
		insert(TIFF_OVRLMT_NUM_STRIPS,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for NUM_STRIPS");
		// over the data size limit for the field BS_IMAGE_TIFF.NUM_TILES
		insert(TIFF_OVRLMT_NUM_TILES,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for NUM_TILES");
		// over the data size limit for the field BS_IMAGE_TIFF.ROWS_PER_STRIP
		insert(TIFF_OVRLMT_ROWS_PER_STRIP,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for ROWS_PER_STRIP");
		// over the data size limit for the field BS_IMAGE_TIFF.TILE_LENGTH
		insert(TIFF_OVRLMT_TILE_LENGTH,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for TILE_LENGTH");
		// over the data size limit for the field BS_IMAGE_TIFF.TILE_WIDTH
		insert(TIFF_OVRLMT_TILE_WIDTH,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for TILE_WIDTH");
		// an IFD without any Tiff tags
		insert(TIFF_TAGLESS_IFD,
			Severity.SEVERITY_NOTE,
			"Contains metadata sections (IFDs) without any metadata (fields).");
		// not a known GrayResponseUnit value
		insert(TIFF_UNKNOWN_GRUNIT,
			Severity.SEVERITY_NOTE,
			"Doesn't use a valid value for the GrayResponseUnit field.");
		// not a known PlanarConfiguration value
		insert(TIFF_UNKNOWN_PLANCONFIG,
			Severity.SEVERITY_NOTE,
			"Doesn't use a valid value for the PlanarConfiguration field.");
		// an unknown tiff tag number
		insert(TIFF_UNKNOWN_TAG,
			Severity.SEVERITY_NOTE,
			"Contains a metadata field (tag) not found in the tiff specification.");
		//	an unexpected data type for a tag
		insert(TIFF_UNKNOWN_TYPE,
			Severity.SEVERITY_NOTE,
			"Uses a data type that isn't part of the tiff specification.");
		// not a known YCbCRPositioning value
		insert(TIFF_UNKNOWN_YCBCRPOSITIONING,
			Severity.SEVERITY_NOTE,
			"Doesn't use a valid value for the YCbCRPositioning field.");
		// not a known YCbCRSubsampling
		insert(TIFF_UNKNOWN_YCBCRSUBSAMPLING,
			Severity.SEVERITY_NOTE,
			"Doesn't use a valid value for the YCbCRSubsampling field.");
		//	tags are not in ascending order
		 insert(TIFF_UNSORTED_TAGS, 
			 Severity.SEVERITY_NOTE,
			 "Metadata fields are not in the order they are supposed to be.");
		//	an unexpected data type for a tag
		insert(TIFF_WRONG_COUNT,
			Severity.SEVERITY_NOTE,
			"Has a metadata field (tag) with the wrong number of values.");
		// an unexpected data type for a tag
		insert(TIFF_WRONG_TYPE,
			Severity.SEVERITY_NOTE,
			"Uses the wrong data type for a metadata field (tag).");
	}
}
