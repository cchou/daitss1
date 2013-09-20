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
 * This class was loosely based on one from "Matra - the DTD Parser" whose initial
 * developer was Conrad S Roche <derupe at users.sourceforge.net>. 
 * The Matra code was released under either the Mozilla Public License 
 * Version 1.1 or alternatively the GNU GENERAL PUBLIC LICENSE Version 2 or 
 * later.
 * 
 */
package edu.fcla.daitss.format.text.dtd;


/**
 * Exception denoting a syntax error (well-formedness error) in the input DTD.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class DTDSyntaxException extends Exception {
	private int row = -1;

	/**
	 * DTDSyntaxException constructor.
	 * 
	 * @param s Description of the syntax error.
	 */
	public DTDSyntaxException(String s) {
		super(s);
	}
	/**
	 * DTDSyntaxException constructor.
	 * 
	 * @param s Description of the syntax error.
	 */
	public DTDSyntaxException(String s, int rowNo) {
		super(s);
		row = rowNo;
	}
	
	public int getRow() {
		return row;
	}
}
