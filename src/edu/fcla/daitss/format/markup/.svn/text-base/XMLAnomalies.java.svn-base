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
 * Created on May 5, 2004
 *
 */
package edu.fcla.daitss.format.markup;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Severity;

/**
 * XMLAnomalies represent anomalies that can appear in XML files.
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class XMLAnomalies extends MLAnomalies {
	
	/**
	 * Unknown format exception (parse error)
	 */
	public static final String XML_BAD_FORMAT = "A_XML_BAD_FORMAT";
	
	/**
	 * badly formatted schema location value
	 * 
	 * TODO: check that this anomaly isn't being added 
	 * when it also has a dtd
	 */
	public static final String XML_BAD_SCHEMALOCATION = "A_XML_BAD_SCHEMALOCATION";
	
	/**
	 * uses more than one schema type
	 */
	public static final String XML_MULT_SCHEMA_TYPES = "A_XML_MULT_SCHEMA_TYPES";
	
	/**
	 * Unknown XML version
	 */
	public static final String XML_UNKNOWN_VERSION = "A_XML_UNKNOWN_VERSION";
	
	/**
	 * Test driver
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		XMLAnomalies xa = new XMLAnomalies();
	}

	/**
	 * Builds the list of known XML-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public XMLAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known XML-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		//	Unknown format exception (parse error)
		insert(XML_BAD_FORMAT,
			Severity.SEVERITY_NOTE,
			"Unknown format exception (parse error)");
		// badly formatted schema location value
		insert(XML_BAD_SCHEMALOCATION,
			Severity.SEVERITY_NOTE,
			"Badly formatted schema location value");
		//	uses more then one schema type (ex: DTD and W3C XML schema)
		insert(XML_MULT_SCHEMA_TYPES,
			Severity.SEVERITY_NOTE,
			"Uses multiple schema types");
		//	Unknown XML version
		insert(XML_UNKNOWN_VERSION,
			Severity.SEVERITY_NOTE,
			"Unknown XML version");
	}
}
