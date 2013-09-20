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
 * Created on Feb 2, 2004
 *
 */
package edu.fcla.daitss.severe.element;

import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;

/**
 * Anomalies
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Anomalies extends SevereElements {
	
	/**
	 * bad URI syntax for a link within the file
	 */
	public static final String FILE_LINK_BAD_URI =
		"A_FILE_LINK_BAD_URI";
	
	/**
	 * over the data size limit for the field DATA_FILE.CREATOR_PROG
	 */
	public static final String FILE_OVRLMT_CREATOR_PROG =
		"A_FILE_OVRLMT_CREATOR_PROG";
		
	/**
	 * over the data size limit for the field DATA_FILE.FILE_TITLE
	 */
	public static final String FILE_OVRLMT_FILE_TITLE = 
		"A_FILE_OVRLMT_FILE_TITLE";
		
	/**
	 * over the data size limit for the field DATA_FILE.MEDIA_TYPE_VERSION
	 */
	public static final String FILE_OVRLMT_MEDIA_TYPE_VERSION =
		"A_FILE_OVRLMT_MEDIA_TYPE_VERSION";
 
	/**
	 * over the data size limit for the field DATA_FILE.NUM_BITSTREAMS
	 */
	public static final String FILE_OVRLMT_NUM_BITSTREAMS = 
		"A_FILE_OVRLMT_NUM_BITSTREAMS";
		
	/**
	 * over the data size limit for the field DATA_FILE.ORIG_URI
	 */
	public static final String FILE_OVRLMT_ORIG_URI =
		"A_FILE_OVRLMT_ORIG_URI";
		
	/**
	 * over the data size limit for the field DATA_FILE.SIZE
	 */
	public static final String FILE_OVRLMT_SIZE = "A_FILE_OVRLMT_SIZE";
	
	/**
	 * has more than the configured maximum number of anomalies
	 * (see the <code>ANOMALIES_TOO_MANY</code> property in the
	 * <code>configPolicies</code> file.
	 */
	public static final String FILE_TOO_MANY_ANOMS = 
		"A_FILE_TOO_MANY_ANOMS";

	/**
	 * unknown compression scheme
	 */
	public static final String FILE_UNKNOWN_COMPRESSION = 
		"A_FILE_UNKNOWN_COMPRESSION";
		
	/**
	 * unknown media type (file format)
	 */
	public static final String FILE_UNKNOWN_MEDIATYPE = 
		"A_FILE_UNKNOWN_MEDIATYPE";
	
	/**
	 * no valid bitstream
	 */
	public static final String FILE_NO_BITSTREAM = 
		"A_FILE_NO_BITSTREAM";
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		
		Anomalies a = new Anomalies();
		
	}

	/**
	* A list of format-specific anomalies. 
	* Contains SevereElement objects
	*/
	protected Vector elems = null;
	
	/**
	 * Create storage for the severe elements
	 * 
	 * @throws FatalException
	 */
	public Anomalies() throws FatalException {
		elems = new Vector();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known generic file anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// bad URI syntax for a link within the file
		insert(FILE_LINK_BAD_URI,
			Severity.SEVERITY_NOTE,
			"Bad URI syntax for a link within the file");
		// over the data size limit for the field DATA_FILE.CREATOR_PROG
		insert(FILE_OVRLMT_CREATOR_PROG,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for CREATOR_PROG");
		// over the data size limit for the field DATA_FILE.FILE_TITLE
		insert(FILE_OVRLMT_FILE_TITLE,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for FILE_TITLE");
		// over the data size limit for the field DATA_FILE.MEDIA_TYPE_VERSION
		insert(FILE_OVRLMT_MEDIA_TYPE_VERSION,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for MEDIA_TYPE_VERSION");
		// over the data size limit for the field DATA_FILE.NUM_BITSTREAMS
		insert(FILE_OVRLMT_NUM_BITSTREAMS,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for NUM_BITSTREAMS");
		// over the data size limit for the field DATA_FILE.ORIG_URI
		insert(FILE_OVRLMT_ORIG_URI,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for ORIG_URI");
		// over the data size limit for the field DATA_FILE.SIZE
		insert(FILE_OVRLMT_SIZE,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for SIZE");
		// has more than the configured maximum number of anomalies
		insert(FILE_TOO_MANY_ANOMS,
			Severity.SEVERITY_BIT,
			"Has too many anomalies");
		// unknown compression scheme
		insert(FILE_UNKNOWN_COMPRESSION,
			Severity.SEVERITY_NOTE,
			"Uses an unknown compression scheme.");
		// unknown compression scheme
		insert(FILE_UNKNOWN_MEDIATYPE,
			Severity.SEVERITY_NOTE,
			"Can not recognize the file format.");
		
		// no bitstream
		insert(FILE_NO_BITSTREAM, Severity.SEVERITY_NOTE,
			"Cannot find any valid bitstream.");
	}
	
	/**
	 * @return all the recognized anomalies
	 */
	public Vector getElems() {
		return this.elems;
	}
	
	/**
	 * Returns a cloned copy of an anomaly.
	 * 
	 * @param name	the anomaly code name
	 * @return	a cloned copy of the anomaly
	 * @throws FatalException
	 */
	public SevereElement getSevereElement(String name) 
		throws FatalException {
	return SevereElements.getSevereElement(name, this.elems);
	}
	
	/**
	 * Inserts an anomaly into the list of known anomalies.
	 * 
	 * @param _name	anomaly name
	 * @param _severity	severity of the anomaly
	 * @param _longDescription	description of the anomaly
	 * @throws FatalException
	 */
	protected void insert(String _name, String _severity, String _longDescription) 
		throws FatalException{
		super.insert(_name,_severity, _longDescription, elems);
	}
	
	/**
	 * Determines if this is a valid code name for an anomaly
	 * 
	 * @param elemName	the anomaly name
	 * @return	<code>true</code> if the name is valid, else <code>false</code>
	 */
	public boolean isValidSevereElement(String elemName){
		return SevereElements.isValidSevereElement(elemName, this.elems);
	}

}
