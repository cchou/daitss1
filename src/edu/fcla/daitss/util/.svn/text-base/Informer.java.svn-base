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
package edu.fcla.daitss.util;

import java.io.IOException;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.exception.SilentPackageException;

/**
 * Facilitates writing log counts, informational messages and errors to the
 * log file and to STDOUT. 
 */
public class Informer {

	/**
	 * The single instance of Informer.
	 */
	private static Informer instance;

	/**
	 * Returns the single instance of Informer.
	 * 
	 * @return Informer
	 * @throws FatalException 
	 */
	public static synchronized Informer getInstance() throws FatalException {
		if (instance == null) {
			instance = new Informer();
		}
		return instance;
	}

	/**
	 * @return Informer Object
	 * @throws FatalException
	 */
	public static Informer getSafeInstance() {
		if (instance == null) {
			instance = new Informer(true, true, false);
		}
		return instance;
	}

	/**
	 * Returns the single instance of Informer.
	 * @param archiveAction 
	 * 
	 * @return Informer
	 * @throws FatalException 
	 */
	public static Informer getInstance(byte archiveAction) throws FatalException {
		if (instance == null) {
			Log.getInstance(archiveAction);
			instance = new Informer();
		}
		return instance;
	}


	/**
	 * Test driver.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {	
	    
		try {
			Informer.getInstance().fail("Informer", "main()", "test package error", "test", null);
		} 
		catch (Exception e) {
			System.out.println(e.getClass().getName());
		}		
		try {
			Informer.getInstance().fail("Informer", "main()", "test package error", null, new IOException());
		} 
		catch (Exception e) {
			System.out.println(e.getClass().getName());
		}			
		try {
			Informer.getInstance().fail("Informer", "main()", null, "test", new IOException());
		} 
		catch (Exception e) {
			System.out.println(e.getClass().getName());
		}	
		try {
			Informer.getInstance().fail("Informer", null, "test package error", "test", new IOException());
		} 
		catch (Exception e) {
			System.out.println(e.getClass().getName());
		}			
		try {
			Informer.getInstance().fail(null, "main()", "test package error", "test", new IOException());
		} 
		catch (Exception e) {
			System.out.println(e.getClass().getName());
		}	
	}

	/**
	 * Specified whether all stdout message should be suppressed
	 */
	private boolean quiet_mode;

	/**
	 * Specifies whether or not verbose messages should be printed to the log.
	 */
	private boolean verbose_log;

	/**
	 * Specifies whether or not verbose messages should be printed to STDOUT.
	 */
	private boolean verbose_stdout;

	/**
	 * Constructor. Sets the verbose_log and verbose_stdout members that were
	 * either set in the DAITSS configuration files or passed in as arguments.
	 * @throws FatalException 
	 *
	 */
	private Informer() throws FatalException {
		if (ArchiveProperties
			.getInstance()
			.getArchProperty("VERBOSE_STDOUT")
			.equals("true")) {
			this.verbose_stdout = true;
		} else {
			this.verbose_stdout = false;
		}

		if (ArchiveProperties
			.getInstance()
			.getArchProperty("VERBOSE_LOG")
			.equals("true")) {
			this.verbose_log = true;
		} else {
			this.verbose_log = false;
		}
		
		if (ArchiveProperties
			.getInstance()
			.getArchProperty("QUIET_MODE")
			.equals("true")) {
			this.quiet_mode = true;
			// quiet mode also means that verbose stdout is off
			this.verbose_stdout = false;
		} else {
			this.quiet_mode = false;
		}
		
		
	}
	
	private Informer(boolean vLog, boolean vStdout, boolean qMode) {
		this.verbose_log = vLog;
		this.verbose_stdout = vStdout;
		this.quiet_mode = qMode;
	}	
	
	/**
	 * @param o
	 * @param method
	 * @param message
	 * @param subject
	 * @param e
	 * @throws FatalException
	 */
	public void safeFail(		
		Object o,
		String method,
		String message,
		String subject,
		Exception e) throws FatalException{	
				
		safeFail(
			o.getClass().getName(),
			method,
			message,
			subject,
			e);
	}
	
	
	/**
	 * @param objectClassName
	 * @param method
	 * @param message
	 * @param subject
	 * @param e
	 * @throws FatalException
	 */
	public void safeFail(		
		String objectClassName,
		String method,
		String message,
		String subject,
		Exception e) throws FatalException{		
		
		String eMsg = (e.getMessage()!=null?e.getMessage():e.getClass().getName());
		
		System.out.println(
			"Fatal Error: " + 
				"\n\tOrigin: " + objectClassName +
				"\n\tMethod: " + method +
				"\n\tSubject: " + subject +
				"\n\tMessage: " + message + 					 
				"\n\tException: " + eMsg);
				
		try {
			Log.getInstance("ABEND").add(new LogEntry(
				this.getClass().getName(),
				method,
				LogEntry.FATAL,
				message));
		} catch (FatalException e1) {
			throw e1;
		}		

		if (e instanceof FatalException) {
			throw (FatalException) e;
		}
		throw new FatalException(e.getMessage());		
	}
	
	/**
	 * Used to write a count entry to the log file or to STDOUT.
	 * 
	 * @param o Object writing the count entry.
	 * @param method The method (within the Object) writing the count entry.
	 * @param message Description of the count entry.
	 * @param count The count associated with the message.
	 * @throws FatalException 
	 */
	public void count(Object o, String method, String message, int count) 
		throws FatalException{
		count(o.getClass().getName(), method, message, count);
	}

	/**
	 * Used to write a count entry to the log file or to STDOUT.
	 * 
	 * @param objectClassName Class name of object writing the count entry.
	 * @param method The method (within the Object) writing the count entry.
	 * @param message Description of the count entry.
	 * @param count The count associated with the message.
	 * @throws FatalException 
	 */
	public void count(String objectClassName, String method, String message, int count) 
		throws FatalException{
		if (isVerboseStdout()) {
			System.out.println("Count: " + count + "\tDescription: " + message);
		}
		// log the count entry
		Log.getInstance().add(new LogEntry(objectClassName, method, count, message));
	}

	/**
	 * Used for Package exceptions that cause a package to be rejected.
	 * 
	 * @param o Object that encountered this error.
	 * @param method The method (within the Object) writing the error entry.
	 * @param message Description of the error.
	 * @param subject Subject (file, etc.) of this error.
	 * @param e The PackageException
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	public void error(
		Object o,
		String method,
		String message,
		String subject,
		Exception e) throws FatalException, PackageException {
			
		error(o.getClass().getName(), method, message, subject, e);
	}

	/**
	 * Used for Package exceptions that cause a package to be rejected.
	 * 
	 * @param objectClassName Class name of object that encountered this error.
	 * @param method The method (within the Object) writing the error entry.
	 * @param message Description of the error.
	 * @param subject Subject (file, etc.) of this error.
	 * @param e The Exception
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	public void error(
		String objectClassName,
		String method,
		String message,
		String subject,
		Exception e) throws FatalException, PackageException{
		
	    String methodName = "error(String, String, String, String, Exception)";

	    // check input
	    if (objectClassName == null) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: [unknown class]" + "." + (method == null?"[unknown method]":method), 
	                "objectClassName argument is null", 
	                "String objectClassName argument", 
	                new NullPointerException());	        	        
	    }
	    else if(method == null ) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "[unknown method]", 
	                "method argument is null", 
	                "String method argument", 
	                new NullPointerException());	        	        
	    }
	    else if(message == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "message argument is null", 
	                "String message argument", 
	                new NullPointerException());	        	        
	    }
	    else if(subject == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "subject argument is null", 
	                "String subject argument", 
	                new NullPointerException());	        
	    }
	    else if(e == null) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "e argument is null", 
	                "Exception e argument", 
	                new NullPointerException());
	    }
	    
		String eMsg = (e.getMessage()!=null?e.getMessage():e.getClass().getName());
		
		if (!isQuiet_mode()) {
			System.out.println(
			"Package Error: " + 
				"\n\tOrigin: " + objectClassName + 
				"\n\tMethod: " + method +
				"\n\tSubject: " + subject +
				"\n\tMessage: " + message + 				 
				"\n\tException: " + eMsg);

		}
		// log the package error
		Log.getInstance().add(
			new LogEntry(
				objectClassName,
				method,
				LogEntry.ERROR,
				message + ": " + eMsg,
				subject));
				
		if (e instanceof SilentPackageException) {
			SilentPackageException spe;
			spe = (SilentPackageException) e;
			spe.setLocalMessage(message);
			spe.setLocalSubject(subject);
			throw spe;
		}
		else {
			PackageException pe;
			if (e instanceof PackageException) {
				pe = (PackageException) e;
				pe.setLocalMessage(message);
				pe.setLocalSubject(subject);
			} else {
				pe = new PackageException(e.getMessage());
				pe.setLocalMessage(message);
				pe.setLocalSubject(subject);
			}
			throw pe;
		}
	}

	/**
	 * Used for DAITSS fatal errors (errors not related to particular data but to the 
	 * program itself).
	 * 
	 * @param o Object that encountered this error.
	 * @param method The method (within the Object) writing the failure entry.
	 * @param message Description of the error.
	 * @param subject Subject (file, etc.) of this error.
	 * @param e The FatalException.
	 * @throws FatalException 
	 */
	public void fail(
		Object o,
		String method,
		String message,
		String subject,
		Exception e) throws FatalException {
		fail(o.getClass().getName(), method, message, subject, e);
	}

	/**
	 * Used for DAITSS fatal errors (errors not related to particular data but to the 
	 * program itself).
	 * 
	 * @param objectClassName Class name of the object that encountered this error.
	 * @param method The method (within the Object) writing the failure entry.
	 * @param message Description of the error.
	 * @param subject Subject (file, etc.) of this error.
	 * @param e The FatalException.
	 * @throws FatalException 
	 */
	public void fail(
		String objectClassName,
		String method,
		String message,
		String subject,
		Exception e) throws FatalException {

	    String methodName = "fail(String, String, String, String, Exception)";

	    // check input
	    if (objectClassName == null) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: [unknown class]" + "." + (method == null?"[unknown method]":method), 
	                "objectClassName argument is null", 
	                "String objectClassName argument", 
	                new NullPointerException());	        	        
	    }
	    else if(method == null ) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "[unknown method]", 
	                "method argument is null", 
	                "String method argument", 
	                new NullPointerException());	        	        
	    }
	    else if(message == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "message argument is null", 
	                "String message argument", 
	                new NullPointerException());	        	        
	    }
	    else if(subject == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "subject argument is null", 
	                "String subject argument", 
	                new NullPointerException());	        
	    }
	    else if(e == null) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "e argument is null", 
	                "Exception e argument", 
	                new NullPointerException());
	    }
	    
	    // This makes life nice, the rest of this is garbage
	    System.err.println();
	    System.err.println("-- STACKTRACE BEGIN --");
	    e.printStackTrace();
	    System.err.println("-- STACKTRACE END --");
	    System.err.println();

	    if (objectClassName == null || method == null 
	            || message == null || subject == null
	            || e == null) {
			// print out the Fatal Error
	        if (!isQuiet_mode()) {
				System.out.println(
				"Fatal Error: NULL argument" + 
					"\n\tOrigin: " + objectClassName + 
					"\n\tCalled method: " + methodName + " Caller Method: " + method +
					"\n\tSubject: " + subject +
					"\n\tMessage: " + message + 				 
					"\n\tException: " + e);

			}
			// log the fatal error
			Log.getInstance().add(
				new LogEntry(
				    "Origin: " + objectClassName,
				    "Called method: " + methodName + " Caller Method: " + method,
				    LogEntry.FATAL,
					message + ": " + e,
					subject));	       
			
			throw new FatalException("Nulll argmuent passed to Informer.fail(...)");
	    }
	    
	    
		String eMsg = (e.getMessage()!=null?e.getMessage():e.getClass().getName());
			
		if (!isQuiet_mode()) {
			System.out.println(
				"Fatal Error: " + 
					"\n\tOrigin: " + objectClassName +
					"\n\tMethod: " + method +
					"\n\tSubject: " + subject +
					"\n\tMessage: " + message + 					 
					"\n\tException: " + eMsg);
		}
		// log the fatal error
		// the only problem here is what if this fail is coming from the 
		// log class because an unknown archiveAction is used as an argument to 
		// getInstance(String)? A log will never have been created and the following
		// call will create a Unit_Test log. Although the creation of the Unit_Test 
		// log should not fail, we will have to remember to look there when logs
		// aren't where they're expected to be after a fail. Maybe the Log class
		// should directly call ArchiveManager.fail() rather than telling Informer.
		Log.getInstance().add(
			new LogEntry(
				objectClassName,
				method,
				LogEntry.FATAL,
				message + ": " + eMsg,
				subject));
		// cast e to a FatalException and throw it
		if (new FatalException().getClass().isInstance(e)) {
			throw (FatalException)e;
		}
		throw new FatalException(e.getMessage());
	}
    
	
	/**
	 * @param message
	 * @param subject
	 * @throws FatalException
	 */
	public void info(String message, String subject) throws FatalException {
		StackTraceElement element = new Throwable().getStackTrace()[1];
		info(element.getClassName(), element.getMethodName(), message, subject, false);
	}

	/**
	 * @param message
	 * @param subject
	 * @throws FatalException
	 */
	public void warning(String message, String subject) throws FatalException {
		StackTraceElement element = new Throwable().getStackTrace()[1];
		warning(element.getClassName(), element.getMethodName(), message, subject);
	}
	
	/**
	 * @param message
	 * @param subject
	 * @param e 
	 * @throws FatalException
	 * @throws PackageException 
	 */
	public void error(String message, String subject, Exception e) throws FatalException, PackageException {
		StackTraceElement element = new Throwable().getStackTrace()[1];
		error(element.getClassName(), element.getMethodName(), message, subject, e);
	}

	/**
	 * @param message
	 * @param subject
	 * @param e
	 * @throws FatalException
	 */
	public void fail(String message, String subject, Exception e) throws FatalException {
		StackTraceElement element = new Throwable().getStackTrace()[1];
		fail(element.getClassName(), element.getMethodName(), message, subject, e);
	}
	
	
	public void fail(String message, String subject) throws FatalException {
		fail(message, subject, new FatalException());
	}
	
	/**
     * Used for informational (non-error) messages.
     * 
     * @param o
     *            Object that is communicating this information.
     * @param method
     *            The method (within the Object) writing the info entry.
     * @param message
     *            Description of the information.
     * @param subject
     *            Subject (file, etc.) of this informational entry.
     * @param alwaysWrite
     *            Whether this entry should always be written to the log file
     *            despite the verbosity setting
	 * @throws FatalException 
     */
	public void info(
		Object o,
		String method, 
		String message,
		String subject,
		boolean alwaysWrite) throws FatalException {
		info(o.getClass().getName(), method, message, subject, alwaysWrite);
	}

	/** 
	 * Used for informational (non-error) messages. 
	 * 
	 * @param objectClassName 	Class name of the object that is communicating this information.
	 * @param method 			The method (within the Object) writing the info entry.
	 * @param message 			Description of the information.
	 * @param subject 			Subject (file, etc.) of this informational entry.
	 * @param alwaysWrite 		Whether this entry should always be written to
	 * 							the log file despite the verbosity setting
	 * @throws FatalException 
	 */
	public void info(
		String objectClassName,
		String method,
		String message,
		String subject,
		boolean alwaysWrite) throws FatalException {	    
	    
	    String methodName = "info(String,String,String,String,boolean)";
	    
	    // check input
	    if (objectClassName == null) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: [unknown class]" + "." + (method == null?"[unknown method]":method), 
	                "objectClassName argument is null", 
	                "String objectClassName argument", 
	                new NullPointerException());	        	        
	    }
	    else if(method == null ) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "[unknown method]", 
	                "method argument is null", 
	                "String method argument", 
	                new NullPointerException());	        	        
	    }
	    else if(message == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "message argument is null", 
	                "String message argument", 
	                new NullPointerException());	        	        
	    }
	    else if(subject == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "subject argument is null", 
	                "String subject argument", 
	                new NullPointerException());	        
	    }
	    	    
		if (isVerboseStdout()) {
			System.out.println(
			"Info: " + 
				"\n\tOrigin: " + objectClassName +
				"\n\tMethod: " + method +
				"\n\tSubject: " + subject + 
				"\n\tMessage: " + message);
		}
		if (isVerboseLog() || alwaysWrite) {
			// log the info message
			Log.getInstance().add(
				new LogEntry(objectClassName, method, LogEntry.INFO, message, subject));
		}
	}


	/**
	 * Returns whether or not the log should be written in verbose mode. In verbose
	 * mode extra information is output to STDOUT and/or to a log file. The 
	 * verbose mode can be set in the DAITSS configuration files.
	 * 
	 * @return	<code>true</code> if the log is being written in verbose mode,
	 * 				else <code>false</code>.
	 */
	private boolean isVerboseLog() {
		return this.verbose_log;
	}

	/**
	 * Returns whether or not STDOUT should be written to at all. In verbose
	 * mode extra information is output to STDOUT and/or to a log file. The 
	 * verbose mode can be set in the DAITSS configuration files.
	 * 
	 * @return	<code>true</code> if STDOUT is being written to,
	 * 				else <code>false</code>.
	 */
	private boolean isVerboseStdout() {
		return this.verbose_stdout;
	}

	/**
	 * Used for Package warnings that are not severe enough for the package to 
	 * be rejected.
	 * 
	 * @param o Object that encountered this warning.
	 * @param method The method (within the Object) writing the warning entry.
	 * @param message Description of the warning.
	 * @param subject Subject (file, etc.) of the warning.
	 * @throws FatalException 
	 */
	public void warning(
		Object o,
		String method, 
		String message,
		String subject) throws FatalException {
		warning(o.getClass().getName(), method, message, subject);
	}
	
	/**
	 * Used for Package warnings that are not severe enough for the package to 
	 * be rejected.
	 * 
	 * @param objectClassName Class name of the object that encountered this warning.
	 * @param method The method (within the Object) writing the warning entry.
	 * @param message Description of the warning.
	 * @param subject Subject (file, etc.) of the warning.
	 * @throws FatalException 
	 */
	public void warning(
		String objectClassName,
		String method,
		String message,
		String subject) throws FatalException {
			
	    String methodName = "warning(String,String,String,String)";
	    
	    // check input
	    if (objectClassName == null) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: [unknown class]" + "." + (method == null?"[unknown method]":method), 
	                "objectClassName argument is null", 
	                "String objectClassName argument", 
	                new NullPointerException());	        	        
	    }
	    else if(method == null ) {
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "[unknown method]", 
	                "method argument is null", 
	                "String method argument", 
	                new NullPointerException());	        	        
	    }
	    else if(message == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "message argument is null", 
	                "String message argument", 
	                new NullPointerException());	        	        
	    }
	    else if(subject == null){
	        fail(getClass().getName(), 
	                "Called method: " + methodName + " Caller Method: " + objectClassName + "." + method, 
	                "subject argument is null", 
	                "String subject argument", 
	                new NullPointerException());	        
	    }
		
		if (!isQuiet_mode()) {
			System.out.println(
				"Warning: " + 
					"\n\tOrigin: " + objectClassName + 
                    "\n\tMethod: " + method +
					"\n\tSubject: " + subject +
					"\n\tMessage: " + message);
		}
		// log the warning
		Log.getInstance().add(
			new LogEntry(
				objectClassName,
				method,
				LogEntry.WARNING,
				message,
				subject));
	}	
	/**
	 * @return boolean
	 */
	public boolean isQuiet_mode() {
		return quiet_mode;
	}

    public void info2(String message, String subject) throws FatalException {
        StackTraceElement element = new Throwable().getStackTrace()[1];
        info(element.getClassName(), element.getMethodName(), message, subject, true);
        
    }

}
