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
 * Created on Sep 1, 2004
 *
 */
package edu.fcla.daitss.format.image.jpeg2000;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Severity;

/**
 * Jp2Anomalies
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Jp2Anomalies extends Jpeg2000Anomalies {
    
    /**
	 * codestream has decode requirements not described 
	 * in the ISO Jp2 spec
	 */
	public static final String JP2_EXTRA_DECODE_REQS =
		"A_JP2_EXTRA_DECODE_REQS";

	/**
	 * no contiguous codestream box
	 */
	public static final String JP2_NO_JP2CBOX = 
	    "A_JP2_NO_JP2CBOX";
	
	/**
	 * no JP2 Header box
	 */
	public static final String JP2_NO_JP2HBOX = 
	    "A_JP2_NO_JP2HBOX";
	
	/**
	 * file not compatible with the ISO Jp2 spec
	 */
	public static final String JP2_NONCOMPAT_JP2 =
		"A_JP2_NONCOMPAT_JP2";
	
	/**
	 * Non-contiguous color specification boxes in the 
	 * JP2 Header box
	 */
	public static final String JP2_NONCONTIG_COLRBOX = 
	    "JP2_NONCONTIG_COLRBOX";
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//Jp2Anomalies ja = new Jp2Anomalies();
	}
	
	/**
	 * Builds the list of known JP2-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public Jp2Anomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known Text file-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
	    // codestream has decode requirements not described in the ISO Jp2 spec
	    insert(JP2_EXTRA_DECODE_REQS,
				Severity.SEVERITY_NOTE,
				"Codestream has decode requirements not described in ISO Jp2 v1 spec.");
	    // no contiguous codestream box
		insert(JP2_NO_JP2CBOX,
			Severity.SEVERITY_NOTE,
			"No image data (contiguous codestream box)");
	    // no JP2 Header box
		insert(JP2_NO_JP2HBOX,
			Severity.SEVERITY_NOTE,
			"No image metadata (JP2 Header box)");
	    // file not compatible with the ISO Jp2 spec
		insert(JP2_NONCOMPAT_JP2,
			Severity.SEVERITY_NOTE,
			"File not compatible with ISO Jp2 v1 spec.");
		// Non-contiguous color specification boxes in the JP2 Header box
		insert(JP2_NONCONTIG_COLRBOX,
				Severity.SEVERITY_NOTE,
				"Color specification boxes are not contiguous in the JP2 Header box");
	}

}
