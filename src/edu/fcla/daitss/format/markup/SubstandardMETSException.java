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
 * Created on Jan 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.fcla.daitss.format.markup;

import edu.fcla.daitss.exception.PackageException;

/**
 * @author vicary
 * 
 *	Exception to be thrown when METS files are missing 
 *	data components required for correct processing. The
 *  METS file may technically be valid, but without certain
 *  metadata, METS files cannot be successfully processed.
 */
public class SubstandardMETSException extends PackageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5694289478588454349L;

	/**
	 * 
	 */
	public SubstandardMETSException() {
		super();
	}

	/**
	 * @param message
	 */
	public SubstandardMETSException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SubstandardMETSException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SubstandardMETSException(Throwable cause) {
		super(cause);
	}

}