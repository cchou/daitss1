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
 * Created on May 14, 2004
 *
 */
package edu.fcla.daitss.format.markup;


import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;

/**
 * XMLDocument represents the document within an XML file.
 * TODO: Not sure if I will keep this class because everything
 * needed by this is in its superclss.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class XMLDocument extends MLDocument {


	/**
	 * 
	 * @param df the file containing this XML bitstream
	 * @throws FatalException
	 */
	public XMLDocument(DataFile df) throws FatalException {
		super(df);		
	}

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
	}
}
