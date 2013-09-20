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
 * Created on Apr 1, 2004
 */
package edu.fcla.daitss.file;

import edu.fcla.daitss.exception.PackageException;

/**
 * Exception to be thrown when a file is encountered that does 
 * not meet the requirements to be valid for any known DataFile
 * sublcass or for the DataFile class itself. In theory this
 * should never happen since every physical file should be valid 
 * as a general DataFile, but in case this does occur, we need to 
 * be notified.
 * 
 * @author vicary
 */
public class UnknownFileTypeException extends PackageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5815231909799789039L;

	/**
	 * 
	 */
	public UnknownFileTypeException() {
		super();
	}

	/**
	 * @param message
	 */
	public UnknownFileTypeException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnknownFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public UnknownFileTypeException(Throwable cause) {
		super(cause);
	}

}
