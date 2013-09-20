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
 * Created on Mar 30, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

/**
 * PdfProfiles
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class PdfProfiles {
	
	/**
	 * Tagged PDF.
	 * 
	 * (See Section 9.7 of PDF 1.4 spec.)
	 * To be recognized as such:
	 * The Catalog Dictionary must have a MarkInfo Entry holding a 
	 * mark information dictionary which has a single entry, Marked, 
	 * containing the boolean true.
	 * 
	 */
	public static final String PDF_TAGGED = "PDF_TAGGED";

}
