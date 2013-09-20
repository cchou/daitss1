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
 * Created on Jul 19, 2004
 *
 */
package edu.fcla.daitss.format.text.dtd;


import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.text.TextAnomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * DTDAnomalies represents anomalies that can be recognized
 * and recorded about DTD files.
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class DTDAnomalies extends TextAnomalies {
	
	/**
	 * ATTLIST for a non-existent element
	 */
	public static final String DTD_ATT_FOR_MISSING_ELEM = "A_DTD_ATT_FOR_MISSING_ELEM";

	/**	
	 * attribute list that doesn't specify an element type
	 */
	public static final String DTD_ATT_FOR_NO_ELEM = "A_DTD_ATT_FOR_NO_ELEM";
			
	/**
	 * badly formed character reference
	 */
	public static final String DTD_BAD_CHARREF = "A_DTD_BAD_CHARREF";
	
	/**
	 * can't read a file that the DTD references for entities
	 */
	public static final String DTD_BAD_ENT_FILE = "A_DTD_BAD_ENT_FILE";
	
	/**
	 * general parsing problem due to bad DTD syntax.
	 * not expected to be used but a catch-all just in case
	 */
	public static final String DTD_BAD_SYNTAX = "A_DTD_BAD_SYNTAX";
	
	/**
	 * can't find a file that the DTD references for entities
	 */
	public static final String DTD_MISSING_ENT_FILE = "A_DTD_MISSING_ENT_FILE";
	
	/**
	 * multiple attribute lists for the same element type
	 */
	public static final String DTD_MULT_ATTLIST = "A_DTD_MULT_ATTLIST";
	
	/**
	 * multiple declarations of the same element type
	 */
	public static final String DTD_MULT_ELEM_DECL = "A_DTD_MULT_ELEM_DECL";
	
	/**
	 * multiple ID attributes for the same element type
	 */
	public static final String DTD_MULT_ID_ATT = "A_DTD_MULT_ID_ATT";
	
	/**
	 * multiple notation attributes for the same element type
	 */
	public static final String DTD_MULT_NOT_ATT = "A_DTD_MULT_NOT_ATT";
	
	/**
	 * multiple declarations of the same notation name
	 * 
	 * Validity constraint in XML 1.0 specification:
	 * A given Name MUST NOT be declared in more than one notation declaration.
	 */
	public static final String DTD_MULT_NOT_DECL = "A_DTD_MULT_NOT_DECL";
	
	/**
	 * notation attribute on an empty element
	 * See XML 1.0 3rd edition section 3.3.1
	 */
	public static final String DTD_NOT_ATT_EMPTY_ELEM = "A_DTD_NOT_ATT_EMPTY_ELEM";
	
	/**
	 * Makes sure that all the known DTD anomalies are in the list
	 * of all anomalies.
	 * 
	 * @throws FatalException
	 */
	public static void init() throws FatalException {
		DTDAnomalies da = new DTDAnomalies();
	}
	
	/**
	 * Test driver
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		
	}

	/**
	 * Builds the list of known DTD-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public DTDAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known DTD-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		//	attribute list for a non-existent element
		insert(DTD_ATT_FOR_MISSING_ELEM,
			Severity.SEVERITY_NOTE,
			"Attribute list for a non-existent element");
		//	attribute list that doesn't specify an element type
		insert(DTD_ATT_FOR_NO_ELEM,
			Severity.SEVERITY_NOTE,
			"Attribute list that doesn't list an element type");
		//	badly formed character reference
		insert(DTD_BAD_CHARREF,
			Severity.SEVERITY_NOTE,
			"Badly formed character reference");
		//	can't read a file that the DTD references for entities
		insert(DTD_BAD_ENT_FILE,
			Severity.SEVERITY_NOTE,
			"Can't read a file that the DTD references for entities");
		// general parsing problem due to bad DTD syntax.
		// not expected to be used but a catch-all just in case
		insert(DTD_BAD_SYNTAX,
			Severity.SEVERITY_NOTE,
			"Bad syntax");
		//	can't find a file that the DTD references for entities 
		insert(DTD_MISSING_ENT_FILE,
			Severity.SEVERITY_NOTE,
			"Can't find a file that the DTD references for entities");
		//	multiple attribute lists for the same element type
		insert(DTD_MULT_ATTLIST,
			Severity.SEVERITY_NOTE,
			"Multiple attribute lists for the same element type");
		//	multiple declarations of the same element type 
		insert(DTD_MULT_ELEM_DECL,
			Severity.SEVERITY_NOTE,
			"Multiple declarations of the same element type");
		// multiple ID attributes for the same element type
		insert(DTD_MULT_ID_ATT,
			Severity.SEVERITY_NOTE,
			"Multiple ID attributes for the same element type");
		// multiple notation attributes for the same element type
		insert(DTD_MULT_NOT_ATT,
			Severity.SEVERITY_NOTE,
			"Multiple notation attributes for the same element type");
		// multiple notation attributes for the same element type
		insert(DTD_MULT_NOT_DECL,
				Severity.SEVERITY_NOTE,
				"Multiple declarations of the same notation name");
		//	notation attribute on an empty element 
		insert(DTD_NOT_ATT_EMPTY_ELEM,
			Severity.SEVERITY_NOTE,
			"Attribute of type notation declared on an empty element");
	}
}
