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
 * Created on Jun 2, 2005
 */
package edu.fcla.daitss.format.miscfile.pdf;

/**
 * @author carol
 *
 * PdfExternalReference represents all the information needed for processing
 * external references in a PDF file.
 */
public class PdfExternalReference {
	/**
     * map entry to the PdfDictionary that hold the external reference.
     * could be "URI", "F"
     */
	private String	m_entry;    
	
	/**
     * map value in the PdfDictionary that represent the external reference
     * could be file path or actual value of URI
     */
	private String 	m_value;
	
	/**
     * constructor
     */
	public PdfExternalReference(String entry, String value) {
		m_entry = entry;
		m_value = value;
	}
	
	public String getEntry() {
		return m_entry;
	}
	
	public String getValue() {
		return m_value;
	}
}
