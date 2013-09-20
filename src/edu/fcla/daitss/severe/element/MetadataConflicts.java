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
 * MetadataConflicts
 * Note that all metadata conflict values must be less than 255 characters 
 * and need to start with "M_", metadata conflict descriptions
 * must be less than 255 characters.
 * 
 * Will store constants of metadata conflicts that can occur regardless of file format
 */
public class MetadataConflicts extends SevereElements {
	// mixmatch of the file size metadata between the descriptor vs. retrieved by parser. 
	public static final String FILE_SIZE_MISMATCH = "M_FILE_SIZE_MISMATCH";

	// mixmatch of the mime type metadata between the descriptor vs. retrieved by parser. 
	public static final String MIME_TYPE_MISMATCH = "M_MIME_TYPE_MISMATCH";

	// mixmatch of the creator program metadata between the descriptor vs. retrieved by parser. 	
	public static final String CREATOR_PROGRAM_MISMATCH = "M_CREATOR_PROGRAM_MISMATCH";
	
	// mixmatch of the compression metadata between the descriptor vs. retrieved by parser. 	
	public static final String COMPRESSION_MISMATCH = "M_COMPRESSION_MISMATCH";
	
	// A list of format-specific conflicts.  Contains SevereElement objects 
	protected Vector elems = null;

	// Number of format-specific conflicts that are currently in this class' list of conflicts
	protected static int numBuiltElems = 0;
	
	/**
	 * Create storage for the severe elements
	 */
	public MetadataConflicts() throws FatalException {
		elems = new Vector();
		buildAnoms();
	}
	
	/**
	 * Returns a cloned copy of a metadata conflict.
	 * 
	 * @param name	the metadata conflict code name
	 * @return	a cloned copy of the metadata conflict
	 * @throws FatalException
	 */
	public SevereElement getSevereElement(String name) 
			throws FatalException {
		return SevereElements.getSevereElement(name, elems);
	}
	
	/**
	 * Determines if this is a valid code name for a metadata conflict
	 * 
	 * @param elemName	the metadata conflict name
	 * @return	<code>true</code> if the name is valid, else <code>false</code>
	 */
	public boolean isValidSevereElement(String elemName){
		return SevereElements.isValidSevereElement(elemName, elems);
	}
	
	/**
	 * Inserts a metadata conflict into the list of known metadata conflicts.
	 * 
	 * @param _name	conflict name
	 * @param _severity	severity of the conflict
	 * @param _longDescription	description of the conflict
	 * @throws FatalException
	 */
	protected void insert(String _name, String _severity, String _longDescription) 
		throws FatalException{
		super.insert(_name,_severity, _longDescription, elems);
	}
	
	/**
	 * Builds the list of known generic file metadata conflict.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// conflict in file size metadata
		insert(FILE_SIZE_MISMATCH, Severity.SEVERITY_NOTE,
			"conflict in file size metadata");
		// conflict in mime type metadata
		insert(MIME_TYPE_MISMATCH, Severity.SEVERITY_NOTE,
			"conflict in mime type metadata");
		// conflict in creator program metadata
		insert(CREATOR_PROGRAM_MISMATCH, Severity.SEVERITY_NOTE,
			"conflict in creator program metadata");
		// conflict in compression metadata
		insert(COMPRESSION_MISMATCH, Severity.SEVERITY_NOTE,
			"conflict in compression metadata");
	}
}
