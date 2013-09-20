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
 * Created on Oct 15, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.fcla.daitss.exception;

/**
 * @author vicary
 *
 */
public class FatalException extends DaitssException {

	private static final long serialVersionUID = 6082110791730445385L;

	/**
	 * 
	 */
	public FatalException() {
		super();
	}

	/**
	 * @param message
	 */
	public FatalException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FatalException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public FatalException(Throwable cause) {
		super(cause);
	}
}
