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
 */
package edu.fcla.daitss.exception;

/**
 * @author vicary
 *
 */
public class PackageException extends DaitssException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1631407254769417807L;

	String localMessage;

	String localSubject;
	
	/**
	 * 
	 */
	public PackageException() {
		super();
	}

	/**
	 * @param message
	 */
	public PackageException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PackageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public PackageException(Throwable cause) {
		super(cause);
	}

	/**
	 * Hack for accessing the informed message in the report
	 * @return the message
	 */
	public String getLocalMessage() {
		return localMessage;
	}

	/**
	 * Hack for setting the informed message in the report
	 * @param localMessage
	 */
	public void setLocalMessage(String localMessage) {
		this.localMessage = localMessage;
	}

	/**
	 * Hack for accessing the informed subject in the report
	 * @return the subject
	 */
	public String getLocalSubject() {
		return localSubject;
	}

	/**
	 * Hack for setting the informed message in the report
	 * @param localSubject
	 */
	public void setLocalSubject(String localSubject) {
		this.localSubject = localSubject;
	}
}
