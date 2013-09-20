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

/**
 * SevereElement represents an element contained in a file that
 * has a severity (represented by the <code>Severity</code>
 * class). For example, an inhibitor, an anomaly, a quirk and a 
 * metadata conflict are examples of severe elements.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class SevereElement implements Cloneable {

	/**
	 * A very human-understandable description of this element that
	 * would likely be used for reporting purposes.
	 * 
	 */
	private String longDescription = null;
	
	/**
	 * A unique name for this element.
	 */
	private String name = null;
	
	/**
	 * The relative severity of this element. For example, is it
	 * important enough to report this to the file's creator?
	 */
	private String severity = null;
	
	/**
	 * @param _name
	 * @param _severity
	 * @param _longDescription
	 */
	public SevereElement (
		String _name,
		String _severity,
		String _longDescription) {
		this.name = _name;
		this.severity = _severity;
		this.longDescription = _longDescription;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return a copy of this element
	 * @throws CloneNotSupportedException
	 */
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * @return a human-readable description of this element
	 */
	public String getLongDescription() {
		return this.longDescription;
	}

	/**
	 * @return the code name for this element
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the severity code for this element
	 */
	public String getSeverity() {
		return this.severity;
	}

	
}
