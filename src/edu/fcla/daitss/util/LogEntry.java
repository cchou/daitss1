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
/** 
 * Will have the copyright statement.
 */
package edu.fcla.daitss.util;

/**
 * <p>
 * Represents an informational or error entry in a log file.
 * </p>
 */
public class LogEntry {

	/**
	 * <p>
	 * For use with data errors causing a package to be rejected 
	 * ('package errors').
	 * </p>
	 */
	public static final short ERROR = 16;	

	/**
	 * <p>
	 * For use with DAITSS application errors ('fatal errors').
	 * </p>
	 */
	public static final short FATAL = 16384;

	/**
	 * <p>
	 * Code for log entries that are counts.
	 * </p>
	 * 
	 */
	public static final char IND_COUNT = 'C';

	/**
	 * <p>
	 * Code for Log entries that are not counts.
	 * </p>
	 * 
	 */
	public static final char IND_LOG = 'L';

	/**
	 * <p>
	 * For use with informational log entries not related to errors.
	 * </p>
	 */
	public static final short INFO = 0;

	/**
	 * <p>
	 * For use with data errors that will are not severe enough to cause a 
	 * package to be rejected.
	 * </p>
	 */
	public static final short WARNING = 8;
	
	private static void checkArgs(String[] args) throws IllegalArgumentException {
		
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			checkArgs(args);
		}
		catch (IllegalArgumentException iae){
			System.err.println(iae);
			System.exit(1);
		}
		LogEntry le = new LogEntry("LogEntry", "method name", LogEntry.ERROR, "message", "subject");
		System.out.println(le.toString());
	}

	/**
	 * A count used for log entries referring to a total number of something.
	 */
	private int count = -1;

	/**
	 * <p>
	 * The type of error. Exs: LogEntry.INFO, LogEntry.WARNING, 
	 * LogEntry.ERROR, LogEntry.UNKNOWN
	 * </p>
	 * 
	 */
	private short errorCode = 0;

	/**
	 * <p>
	 * Type of log entry. 'L' for errors, warnings and information; 'C' for
	 * counts.
	 * </p>
	 * 
	 */
	private char indicator = '\u0000';

	/**
	 * <p>
	 * Human-readable description for log entry.
	 * </p>
	 * 
	 */
	private String message = "";

	/**
	 * <p>
	 * The method that created this log entry.
	 * </p>
	 * 
	 */
	private String method = "";

	///////////////////////////////////////
	// attributes

	/**
	 * <p>
	 * The object that created this log entry.
	 * </p>
	 * 
	 */
	private String origin = null;


	/**
	 * <p>
	 * The subject of this log entry. If the entry is about a file, make this
	 * the absolute path to the file. The subject should be non-generic and
	 * give specific and helpful information.
	 * </p>
	 * 
	 */
	private String subject = "";

	/**
	 * To be used for count entries in the log file.
	 * 
	 * @param origin The object this count entry originates from
	 * @param method 
	 * @param count Number of items counted
	 * @param message A human-readable description of the entry
	 */
	public LogEntry(String origin, String method, int count, String message) {
		this.indicator = IND_COUNT;
		this.origin = origin;
		this.method = method;
		this.count = count;
		this.message = message;
	} // end LogEntry 

	/**
	 * <p>
	 * Constructor to use when there is not a filePath to log.
	 * </p>
	 * 
	 * @param origin The object this info or error message originates from
	 * @param method 
	 * @param errorCode Severity of log entry
	 * @param message A possibly generic error or info message
	 */
	public LogEntry(String origin, String method, short errorCode, String message) {
		this.indicator = IND_LOG;
		this.origin = origin;
		this.method = method;
		this.errorCode = errorCode;
		this.message = message;
	} // end LogEntry        

	/**
	 * <p>
	 * Constructor to use when there is a filePath to log.
	 * If subject == null, call the the constructor without filePath.
	 * </p>
	 * 
	 * @param origin The object this info or error message originates from
	 * @param method 
	 * @param errorCode Severity of log entry
	 * @param message A possibly generic error or info message
	 * @param subject The non-generic subject of this log entry
	 */
	public LogEntry(String origin, String method, short errorCode, String message, String subject) {
		this(origin, method, errorCode, message);
		this.subject = subject;
	} // end LogEntry        

	/**
	 * <p>
	 * Returns the severity level of the entry or null if this is a count log entry.
	 * </p>
	 * @return short primitive
	 */
	public short getErrorCode() {
		return errorCode;
	} // end getErrorCode        

	///////////////////////////////////////
	// operations

	/**
	 * <p>
	 * Returns the type of log entry, <code>'L'</code> for log entries or
	 * <code>'C'</code>for count entries.
	 * </p>
	 * @return char primitive
	 */
	public char getIndicator() {
		return indicator;
	} // end getIndicator        

	/**
	 * <p>
	 * Returns the human-readable description of the info/error or count.
	 * </p>
	 * @return String object
	 */
	public String getMessage() {
		return message;
	} // end getMessage        

	/**
	 * @return String object
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * <p>
	 * Returns the object that created this log entry.
	 * </p>
	 * @return origin
	 */
	public Object getOrigin() {
		return origin;
	} // end getOrigin        

	/**
	 * <p>
	 * Returns the subject of this error or informational message, 
	 * or null if it is a count entry.
	 * </p>
	 * @return String object
	 */
	public String getSubject() {
		return subject;
	} // end getSubject        

	/**
	 * <p>
	 * Set the severity of the log entry. Does not apply to count entries, only 
	 * log entries (error and informational entries).
	 * </p>
	 * @param _errorCode 
	 */
	public void setErrorCode(short _errorCode) {
		errorCode = _errorCode;
	} // end setErrorCode        

	/**
	 * <p>
	 * Sets the type of log entry, <code>'C'</code> for count entries or
	 * <code>'L'</code> for error or informational log entries.
	 * </p>
	 * @param _indicator 
	 */
	public void setIndicator(char _indicator) {
		indicator = _indicator;
	} // end setIndicator        

	/**
	 * <p>
	 * Sets the human-readable description of the error/info or count log entry.
	 * </p>
	 * @param _message 
	 */
	public void setMessage(String _message) {
		message = _message;
	} // end setMessage        

	/**
	 * @param string
	 */
	public void setMethod(String string) {
		method = string;
	}

	/**
	 * <p>
	 * Sets the object that created this log entry.
	 * </p>
	 * @param _origin 
	 */
	public void setOrigin(String _origin) {
		origin = _origin;
	} // end setOrigin        

	/**
	 * <p>
	 * Sets the specific subject of this log entry. Does not apply to count entries.
	 * If the log entry is about a particular file, this should be the absolute path
	 * to the file.
	 * </p>
	 * @param _subject 
	 */
	public void setSubject(String _subject) {
		subject = _subject;
	} // end setSubject      
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(DateTimeUtil.getDateTimeStamp());
		sb.append("\t" + this.indicator);
		sb.append("\t" + this.errorCode);
		sb.append("\t" + this.origin);
		sb.append("\t" + this.method);			
		sb.append("\t" + this.subject);
		sb.append("\t" + this.message);
		return sb.toString();
	}

} // end LogEntry
