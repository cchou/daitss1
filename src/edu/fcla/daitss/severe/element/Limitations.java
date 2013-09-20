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
package edu.fcla.daitss.severe.element;

import java.util.Vector;
import edu.fcla.daitss.exception.FatalException;
/**
 * Note that all limitation values must be less than 255 characters 
 * and need to start with "L_", limitations descriptions
 * must be less than 255 characters.
 * 
 * @author carol
 *
 */
public class Limitations  extends SevereElements {

	/**
	 * The file is an unknown format to the archive.
	 */
	public static final String FILE_UNKNOWN =
		"L_FILE_UNKNOWN";
	
	/**
	* A list of format-specific Limitations 
	* Contains SevereElement objects
	*/
	protected Vector elems = null;
	
	/**
	 * Number of format-specific limitations that are currently in this class'
	 * list of limitations
	 */
	protected static int numBuiltElems = 0;
	
	/**
	 * Create storage for the severe elements
	 */
	public Limitations() throws FatalException {
		elems = new Vector();
		// will store limitations here that are not format-specific
		insert(FILE_UNKNOWN,
				Severity.SEVERITY_NOTE,
				" The file is an unknown format to the archive.");
	}
	
	/**
	 * Returns a cloned copy of a limitation.
	 * 
	 * @param name	- the limitation code name
	 * @return	a cloned copy of the limitation
	 * @throws FatalException
	 */
	public SevereElement getSevereElement(String name) 
			throws FatalException {
		return SevereElements.getSevereElement(name, elems);
	}
	
	/**
	 * Determines if this is a valid code name for a limitation
	 * 
	 * @param elemName	- the limitation name
	 * @return	<code>true</code> if the name is valid, else <code>false</code>
	 */
	public boolean isValidSevereElement(String elemName){
		return SevereElements.isValidSevereElement(elemName, elems);
	}
	
	/**
	 * Inserts a limitation into the list of known limitations.
	 * 
	 * @param _name				- limitation name
	 * @param _severity			- severity of the limitation
	 * @param _longDescription	- description of the limitation
	 * @throws FatalException
	 */
	protected void insert(String _name, String _severity, String _longDescription) 
		throws FatalException{
		super.insert(_name,_severity, _longDescription, elems);
	}
}
