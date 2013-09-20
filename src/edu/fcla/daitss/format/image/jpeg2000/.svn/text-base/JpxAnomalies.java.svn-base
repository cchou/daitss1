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
 * Jpx-specific Anomalies
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class JpxAnomalies extends Jpeg2000Anomalies {

	/**
	 * file not compatible with the ISO Jp2 v1 spec
	 */
	public static final String JPX_NONCOMPAT_JP2 =
		"JPX_NONCOMPAT_JP2";
    
    /**
     * Unknown color space approximation value (APPROX field) in the
     * Colour Specification box.
     */
    public static final String JPX_UNK_APPROX =
        "A_JPX_UNK_APPROX";
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//JpxAnomalies ja = new JpxAnomalies();
	}
	
	/**
	 * Builds the list of known JPX-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public JpxAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known JPX file-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// unknown value for Color Specification box, APPROX field
		insert(JPX_UNK_APPROX,
			Severity.SEVERITY_NOTE,
			"Unknown value for color space approximation.");
        // file not compatible with the ISO Jp2 spec
        insert(JPX_NONCOMPAT_JP2,
            Severity.SEVERITY_NOTE,
            "File not compatible with ISO Jp2 v1 spec.");
	}
}
