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
 * Created on Mar 11, 2004
 *
 */
package edu.fcla.daitss.format.image;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * ImageAnomalies represents all the known generic image-specific anomalies.
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class ImageAnomalies extends Anomalies {
	
	/**
	 * over the data size limit for the field BS_IMAGE.BITS_PER_SAMPLE
	 */
	public static final String IMG_OVRLMT_BITS_PER_SAMPLE = 
		"A_IMG_OVRLMT_BITS_PER_SAMPLE";
		
	/**
	 * over the data size limit for the field BS_IMAGE.EXTRA_SAMPLES
	 */
	public static final String IMG_OVRLMT_EXTRA_SAMPLES = 
		"A_IMG_OVRLMT_EXTRA_SAMPLES";
		
	/**
	 * over the data size limit for the field BS_IMAGE.IMAGE_HEIGHT
	 */
	public static final String IMG_OVRLMT_IMAGE_HEIGHT = 
		"A_IMG_OVRLMT_IMAGE_HEIGHT";
		
	/**
	 * over the data size limit for the field BS_IMAGE.IMAGE_WIDTH
	 */
	public static final String IMG_OVRLMT_IMAGE_WIDTH = 
		"A_IMG_OVRLMT_IMAGE_WIDTH";

	/**
	 * over the data size limit for the field BS_IMAGE.NUM_COMPONENTS
	 */
	public static final String IMG_OVRLMT_NUM_COMPONENTS = 
		"A_IMG_OVRLMT_NUM_COMPONENTS";
		
	/**
	 * over the data size limit for the field BS_IMAGE.PIXEL_ASPECT_RATIO
	 */
	public static final String IMG_OVRLMT_PIXEL_ASPECT_RATIO = 
		"A_IMG_OVRLMT_PIXEL_ASPECT_RATIO";
	
	/**
	 * over the data size limit for the field BS_IMAGE.RES_HORZ
	 */
	public static final String IMG_OVRLMT_RES_HORZ = 
		"A_IMG_OVRLMT_RES_HORZ";
		
	/**
	 * over the data size limit for the field BS_IMAGE.RES_VERT
	 */
	public static final String IMG_OVRLMT_RES_VERT = 
		"A_IMG_OVRLMT_RES_VERT";
	
	/**
	 * unknown color space
	 */
	public static final String IMG_UNKNOWN_COLORSPACE = 
		"A_IMG_UNKNOWN_COLORSPACE";
		
	/**
	 * unknown purpose of extra samples
	 */
	public static final String IMG_UNKNOWN_EXTRASAMPLES = 
		"A_IMG_UNKNOWN_EXTRASAMPLES";
		
	/**
	 * not a known orientation value
	 */
	public static final String IMG_UNKNOWN_ORIENTATION = 
		"A_IMG_UNKNOWN_ORIENTATION";
		
	/**
	 * not a known resolution unit value
	 */
	public static final String IMG_UNKNOWN_RESUNIT = 
		"A_IMG_UNKNOWN_RESUNIT";

	/**
	 * Test driver 
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//ImageAnomalies ia = new ImageAnomalies();
	}
	
	/**
	 * Builds the list of known generic-image-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	public ImageAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known generic-image-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// over the data size limit for the field BS_IMAGE.BITS_PER_SAMPLE
		insert(IMG_OVRLMT_BITS_PER_SAMPLE,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for BITS_PER_SAMPLE");
		// over the data size limit for the field BS_IMAGE.EXTRA_SAMPLES
		insert(IMG_OVRLMT_EXTRA_SAMPLES,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for EXTRA_SAMPLES");
		// over the data size limit for the field BS_IMAGE.IMAGE_HEIGHT
		insert(IMG_OVRLMT_IMAGE_HEIGHT,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for IMAGE_HEIGHT");
		// over the data size limit for the field BS_IMAGE.IMAGE_WIDTH
		insert(IMG_OVRLMT_IMAGE_WIDTH,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for IMAGE_WIDTH");
		// over the data size limit for the field BS_IMAGE.NUM_COMPONENTS
		insert(IMG_OVRLMT_NUM_COMPONENTS,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for NUM_COMPONENTS");
		// over the data size limit for the field BS_IMAGE.PIXEL_ASPECT_RATIO
		insert(IMG_OVRLMT_PIXEL_ASPECT_RATIO,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for PIXEL_ASPECT_RATIO");
		// over the data size limit for the field BS_IMAGE.RES_HORZ
		insert(IMG_OVRLMT_RES_HORZ,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for RES_HORZ");
		// over the data size limit for the field BS_IMAGE.RES_VERT
		insert(IMG_OVRLMT_RES_VERT,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for RES_VERT");
		//	unknown color space
		 insert(IMG_UNKNOWN_COLORSPACE,
			Severity.SEVERITY_NOTE,
			"Uses an unknown color space.");
		insert(IMG_UNKNOWN_EXTRASAMPLES,
			Severity.SEVERITY_NOTE,
			"Has extra data per pixel for which the purpose is unknown.");
		// not a known orientation value
		insert(IMG_UNKNOWN_ORIENTATION,
			Severity.SEVERITY_NOTE,
			"Has an unknown orientation.");
		// not a known resolution unit value
		insert(IMG_UNKNOWN_RESUNIT,
			Severity.SEVERITY_NOTE,
			"Uses an unknown resolution unit.");
	}
}
