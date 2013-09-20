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
 * Created on Dec 5, 2003
 * 
 */
package edu.fcla.daitss.database;

import edu.fcla.daitss.exception.FatalException;

/**
 * @author vicary
 */
public class NullConnectionException extends FatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2288405223455298982L;

	/**
	 * 
	 */
	public NullConnectionException() {
		super();
	}

	/**
	 * @param message
	 */
	public NullConnectionException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NullConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public NullConnectionException(Throwable cause) {
		super(cause);
	}

}
