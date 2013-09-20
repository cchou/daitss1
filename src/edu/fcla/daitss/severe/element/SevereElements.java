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
 * Created on Jan 28, 2004
 *
 */
package edu.fcla.daitss.severe.element;

import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * SevereElements represents a list of known elements that can be found in a
 * file and that have corresponding severity levels associated with them. 
 * Examples of these 'severe elements' include anomalies, inhibitors, metadata
 * conflicts and quirks.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class SevereElements {
	
	/**
	 * Maximum supported severe element description length
	 */
	private static final int MAX_SE_DESC_LENGTH = 255;
    
	/**
	 * Maximum supported severe element value length
	 */
	private static final int MAX_SE_VALUE_LENGTH = 255;
	
	/**
	 * Produces a comma-delimited list of SevereElement names given
	 * a Vector of SevereElement objects. If the list is empty it returns an
	 * empty String. Can be used by any subclasses of SevereElements
	 * (Anomalies, Inhibitors, Quirks, MetadataConflict) to produce a 
	 * database-ready comma-delimited list of names. 
	 * 
	 * @param elems a Vector of SevereElement objects
	 * @return a comma-delimited list of the names of the SevereElement
	 * 	objects
	 */
	public static String getElemsAsString(Vector elems) {
		StringBuffer sb = new StringBuffer("");
		if (elems != null) {
			Iterator iter = elems.iterator();
			while (iter.hasNext()) {
				sb.append(((SevereElement) iter.next()).getName());
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}

	/**
	* Returns a copy of an <code>SevereElement</code> object given 
	* the name of the element. Given an element name that isn't a known 
	* element, the program's fail routines are called.
	* 
	* @param name	the name of a format-specific element
	* @param elems	the list to get the severe element from
	* @return	an object representing the element
	* @throws FatalException
	*/
	public static SevereElement getSevereElement(String name, Vector elems) 
			throws FatalException {
		SevereElement elem = null;
		SevereElement elemReturned = null;
		Iterator iter = null;
		boolean found = false;
		
		if (name == null || elems == null) {
			Informer.getInstance().fail(
				"SevereElements", "getSevereElement(String)",
				"Illegal argument",
				"name: " + name,
				new IllegalArgumentException("Not a  known Element name"));
		}
		
		iter = elems.iterator();
		while (!found && iter.hasNext()) {
			elem = (SevereElement) iter.next();
			if (elem.getName().equals(name)){
				found = true;
			}
		}
		
		if (!found) {
			Informer.getInstance().fail(
				"SevereElements", "getSevereElement(String)",
				"Illegal argument",
				"name: " + name,
				new IllegalArgumentException("Not a  known Element name"));
		} else {
			// return a copy instead of a reference so that the elements
			// can have different values
			try {
				elemReturned = (SevereElement)elem.clone();
			} catch (CloneNotSupportedException e) {
				// rethrow it as a DaitssException
				Informer.getInstance().fail(
					"SevereElements", "getSevereElement(String)",
					"Can't clone",
					"Called SevereElement.clone()",
					e);
			}
		}
		return elemReturned;
	}
	
	/**
	 * Checks if a severe element code is a valid one. Does this by searching
	 * in the list given to it as a parameter and seeing if the code exists in the list.
	 * 
	 * @param elemName	the code for a severe element
	 *  @param elems	the list to check against
	 * @return	<code>true</code> if the severe element code is valid, else
	 * 				<code>false</code>
	 */
	public static boolean isValidSevereElement(String elemName, Vector elems){
		SevereElement elem = null;
		boolean found = false;
		Iterator iter = null;
		
		if (elemName != null) {
			iter = elems.iterator();
			while (!found && iter.hasNext()) {
				elem = (SevereElement) iter.next();
				if (elem.getName().equals(elemName)){
					found = true;
				}
			}
		}
		return found;
	}

	/**
	* Inserts a single severe element (<code>SevereElement</code>)
	* into the list of known severe elements. Enforces the following database data 
	* requirements for the name, severity code and description:
	* name: 0-32 characters, default is ''
	* severity: must be one of: 'BIT', 'NOTE', 'REJECT', 'REPORT', 'UNKNOWN'; default is 'UNKNOWN'
	* description: 0-255 characters, default is 'UNKNOWN'
	* 
	* This method enforces the database data requirements for the following database
	* tables:
	* ANOMALY, INHIBITOR, METADATA_CONFLICT, PRES_LEVEL_CONFLICT, QUIRKS
	* 
	* @param _name	name of the severe element
	* @param _severity	severity of the severe element
	* @param _longDescription	human-readable description of the severe element
	* @param elems	the list to insert the severe element into
	* @throws FatalException
	*/
	protected void insert(String _name, String _severity, String _longDescription, Vector elems) 
		throws FatalException {
			
		String methodName = "insert(String, String, String, Vector)";
		
		// validate name
		if (_name == null || _name.length() > MAX_SE_VALUE_LENGTH){
			Informer.getInstance().fail(this, methodName,
			"Illegal argument",
			"_name: " + _name,
			new IllegalArgumentException("Not a valid name"));
		}
		// validate severity
		if (!Severity.isValidSeverity(_severity)){
			Informer.getInstance().fail(this, methodName,
			"Illegal argument",
			"_severity: " + _severity,
			new IllegalArgumentException("Not a valid severity code name"));
		}
		// validate description
		if (_longDescription == null || 
		        _longDescription.length() > MAX_SE_DESC_LENGTH){
			Informer.getInstance().fail(this, methodName,
			"Illegal argument",
			"_longDescription: " + _longDescription,
			new IllegalArgumentException("Not a  valid description"));
		}
		// validate Vector
		if (elems == null){
			Informer.getInstance().fail(this, methodName,
			"Null argument",
			"argument: elems",
			new IllegalArgumentException("elems must be a non-null Vector"));
		}
		elems.add(new SevereElement( _name, _severity, _longDescription));
	}
	
}
