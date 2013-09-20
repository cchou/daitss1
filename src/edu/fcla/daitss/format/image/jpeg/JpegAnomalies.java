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
 * Created on Nov 12, 2003
 *
 */
package edu.fcla.daitss.format.image.jpeg;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.image.ImageAnomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * JPEGAnomalies represent all the anomalies that can be found
 * in a JPEG image
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class JpegAnomalies extends ImageAnomalies {
	
	/**
	 * malformed DHT marker segment
	 */
	public static final String JPEG_BAD_DHT = "A_JPEG_BAD_DHT";
		
	/**
	 * malformed DQT marker segment
	 */
	public static final String JPEG_BAD_DQT = "A_JPEG_BAD_DQT";
		
	/**
	 * malformed DRI marker segment
	 */
	public static final String JPEG_BAD_DRI = "A_JPEG_BAD_DRI";
		
	/**
	 * malformed SOF marker segment
	 */
	public static final String JPEG_BAD_SOF = "A_JPEG_BAD_SOF";
		
	/**
	 * malformed SOS marker segment
	 */
	public static final String JPEG_BAD_SOS = "A_JPEG_BAD_SOS";
		
	/**
	 * incomplete JFIF APP0 marker segment (less than 16 bytes)
	 */
	public static final String JPEG_INCOMPLETE_APP0 = 
		"A_JPEG_INCOMPLETE_APP0";
		
	/**
	 * A SOF marker segment refers to a DQT that was not
	 * described prior to this SOF
	 */
	public static final String JPEG_MISSING_DQT = "A_JPEG_MISSING_DQT";
		
	/**
	 * unknown JFIF Extension
	 */
	public static final String JPEG_UNKNOWN_JFIFEXT = 
		"A_JPEG_UNKNOWN_JFIFEXT";
		
	/**
	 * unknown marker
	 */
	public static final String JPEG_UNKNOWN_MARKER = 
		"A_JPEG_UNKNOWN_MARKER";
		
	/**
	 * unknown JPEG variation
	 */
	public static final String JPEG_UNKNOWN_VARIATION = 
		"A_JPEG_UNKNOWN_VARIATION";

	/**
	 * Test driver
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//JpegAnomalies ja = new JpegAnomalies();
	}

	/**
	 * Builds the list of known JPEG-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public JpegAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known JPEG-specific anomalies. 
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// malformed DHT marker segment
		insert(JPEG_BAD_DHT,
			Severity.SEVERITY_NOTE,
			"DHT marker segment is malformed");
		// malformed DQT marker segment
		insert(JPEG_BAD_DQT,
			Severity.SEVERITY_NOTE,
			"DQT marker segment is malformed");
		// malformed DRI marker segment
		insert(JPEG_BAD_DRI,
			Severity.SEVERITY_NOTE,
			"DRI marker segment is malformed");
		// malformed SOF marker segment
		insert(JPEG_BAD_SOF,
			Severity.SEVERITY_NOTE,
			"SOF marker segment is malformed");
		// malformed SOS marker segment
		insert(JPEG_BAD_SOS,
			Severity.SEVERITY_NOTE,
			"SOS marker segment is malformed");
		// incomplete JFIF APP0 marker segment (less than 16 bytes)
		insert(JPEG_INCOMPLETE_APP0,
			Severity.SEVERITY_NOTE,
			"The JFIF APP0 marker segment is incomplete");
		// A SOF marker segment refers to a DQT that was not
		// described prior to this SOF
		insert(JPEG_MISSING_DQT,
			Severity.SEVERITY_NOTE,
			"SOF marker segment refers to an unknown DQT");
		// unknown JFIF Extension
		insert(JPEG_UNKNOWN_JFIFEXT,
			Severity.SEVERITY_NOTE,
			"Uses an unknown JFIF extension");
		// unknown marker
		insert(JPEG_UNKNOWN_MARKER,
			Severity.SEVERITY_NOTE,
			"Contains an unknown marker - can't interpret file");
		// unknown JPEG variation
		insert(JPEG_UNKNOWN_VARIATION,
			Severity.SEVERITY_NOTE,
			"Do not know this JPEG variation.");
	}

}
