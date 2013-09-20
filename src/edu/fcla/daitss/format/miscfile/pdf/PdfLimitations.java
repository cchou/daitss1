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
package edu.fcla.daitss.format.miscfile.pdf;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Limitations;
import edu.fcla.daitss.severe.element.Severity;

public class PdfLimitations extends Limitations {
	/**
	 * unsupported video encoding
	 */
	public static final String PDF_UNSUPPORTED_RESOURCE = "L_PDF_UNSUPPORTED_RESOURCE";
	

	public PdfLimitations() throws FatalException {
		super();
		buildList();
	}
	
	/**
	 * Builds the list of known AVI limitations.
	 * 
	 * @throws FatalException
	 */
	private void buildList() throws FatalException {
		// unsupported video encoding in the AVI file, need to downgrade to bit level
		insert(PDF_UNSUPPORTED_RESOURCE, Severity.SEVERITY_BIT, 
				"unsupported resource in the PDF file.");
		
	}

}
