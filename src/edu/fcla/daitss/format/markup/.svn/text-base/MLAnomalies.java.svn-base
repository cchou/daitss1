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
 * Created on Jun 1, 2004
 *
 */
package edu.fcla.daitss.format.markup;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * MarkupAnomalies
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class MLAnomalies extends Anomalies {
	
	/**
	 * too long a string length for charset name
	 */
	public static final String ML_OVRLMT_CHARSET = 
		"A_ML_OVRLMT_CHARSET";
	
	/**
	 * too long a string for the root element namespace uri for database
	 */
	public static final String ML_OVRLMT_MARKUP_LANGUAGE = 
		"A_ML_OVRLMT_MARKUP_LANGUAGE";

	/**
	 * Builds the list of known Markup-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public MLAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//MLAnomalies ma = new MLAnomalies();
	}
	
	/**
	 * Builds the list of known Markup-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		//	too long a string length for charset name
		insert(ML_OVRLMT_CHARSET,
			Severity.SEVERITY_NOTE,
			"Exceeds string length for CHARSET.");
		//	too long a string for the root element namespace uri for database
		insert(ML_OVRLMT_MARKUP_LANGUAGE,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for MARKUP_LANGUAGE");
	}
}
