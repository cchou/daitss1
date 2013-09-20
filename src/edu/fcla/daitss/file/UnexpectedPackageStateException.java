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
 * Created on Feb 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.fcla.daitss.file;

import edu.fcla.daitss.exception.FatalException;

/**
 * @author vicary
 *
 */
public class UnexpectedPackageStateException extends FatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7915524697450124697L;

	/**
	 * 
	 */
	public UnexpectedPackageStateException() {
		super();
	}

	/**
	 * @param message
	 */
	public UnexpectedPackageStateException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnexpectedPackageStateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public UnexpectedPackageStateException(Throwable cause) {
		super(cause);
	}

}