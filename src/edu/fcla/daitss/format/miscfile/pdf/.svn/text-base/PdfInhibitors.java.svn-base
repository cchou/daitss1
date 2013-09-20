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
package edu.fcla.daitss.format.miscfile.pdf;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Inhibitors;
import edu.fcla.daitss.severe.element.Severity;

/**
 * PdfInhibitors represents inhibitors (things that limit access to
 * the file's contents) that are specific to Pdf files.
 * Note that all inhibitor values must be less than 255 characters 
 * and need to start with "I_", inhibitor descriptions
 * must be less than 255 characters.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class PdfInhibitors extends Inhibitors {
    
	/**
	 * Bad Action Dictionary (doesn't have something that's required)
	 */
	public static final String PDF_ENCRYPTION = 
		"I_PDF_ENCRYPTION";

	/**
	 * Builds the list of inhibitors that a Pdf can have.
	 * 
	 * @throws FatalException
	 */
	public PdfInhibitors() throws FatalException{
		super();
		buildInhibs();
	}
	
	/**
	 * Builds a list of known PDF inhibitors to access
	 * (printing, copying, etc.)
	 * 
	 * @throws FatalException
	 */
	private void buildInhibs() throws FatalException{
		// encryption
		insert(
			PDF_ENCRYPTION,
			Severity.SEVERITY_BIT,
			"Encrypts Strings and Streams.");
	}

	/**
	 * Test driver
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException{
		//PdfInhibitors pis = new PdfInhibitors();
	}
}
