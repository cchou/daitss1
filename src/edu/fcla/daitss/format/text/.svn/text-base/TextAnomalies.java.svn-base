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
 * Created on Mar 15, 2004
 *
 */
package edu.fcla.daitss.format.text;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * TextAnomalies
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class TextAnomalies extends Anomalies {
	
	/**
	 * uses multiple types of line ending characters
	 */
	public static final String TXT_MULT_LINEEND = 
		"A_TXT_MULT_LINEEND";
		
	/**
	 * too long a string length for charset name
	 */
	public static final String TXT_OVRLMT_CHARSET = 
		"A_TXT_OVRLMT_CHARSET";
		
	/**
	 * too large a number of lines for database
	 */
	public static final String TXT_OVRLMT_NUM_LINES = 
		"A_TXT_OVRLMT_NUM_LINES";
		
	/**
	 * unknown character encoding
	 */
	public static final String TXT_UNKNOWN_CHARENC = 
		"A_TXT_UNKNOWN_CHARENC";

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//TextAnomalies ta = new TextAnomalies();
	}
	
	/**
	 * Builds the list of known Pdf-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public TextAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known Text file-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		//	uses multiple types of line ending characters
		insert(TXT_MULT_LINEEND,
			Severity.SEVERITY_NOTE,
			"Uses multiple types of line ending characters.");
		//	too long a string length for charset name
		insert(TXT_OVRLMT_CHARSET,
			Severity.SEVERITY_NOTE,
			"Exceeds string length for CHARSET.");
		//	too large a number of lines for database
		insert(TXT_OVRLMT_NUM_LINES,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for NUM_LINES.");
		//	too large a number of lines for database
		insert(TXT_UNKNOWN_CHARENC,
			Severity.SEVERITY_NOTE,
			"Can not determine the character encoding.");
	}
}
