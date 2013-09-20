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

import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;


/**
 * Quirks
 * Note that all quirk values must be less than 255 characters 
 * and need to start with "Q_", quirk descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Quirks extends SevereElements {

	/**
	* A list of format-specific quirks 
	* Contains SevereElement objects
	*/
	protected static Vector elems = null;
	
	/**
	 * Number of format-specific quirks that are currently in this class'
	 * list of quirks
	 */
	protected static int numBuiltElems = 0;
	
	/**
	 * Create storage for the severe elements
	 */
	public Quirks() {
		elems = new Vector();
	}
	
	/**
	 * Returns a cloned copy of a quirk.
	 * 
	 * @param name	the quirk code name
	 * @return	a cloned copy of the quirk
	 * @throws FatalException
	 */
	public static SevereElement getSevereElement(String name) 
			throws FatalException {
		return SevereElements.getSevereElement(name, elems);
	}
	
	/**
	 * Determines if this is a valid code name for a quirk
	 * 
	 * @param elemName	the quirk name
	 * @return	<code>true</code> if the name is valid, else <code>false</code>
	 */
	public static boolean isValidSevereElement(String elemName){
		return SevereElements.isValidSevereElement(elemName, elems);
	}
	
	/**
	 * Inserts a quirk into the list of known quirks.
	 * 
	 * @param _name	quirk name
	 * @param _severity	severity of the quirk
	 * @param _longDescription	description of the quirk
	 * @throws FatalException
	 */
	protected void insert(String _name, String _severity, String _longDescription) 
		throws FatalException{
		super.insert(_name,_severity, _longDescription, elems);
	}
	
	// will store quirks here that are not format-specific
	
}
