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
 * 
 */
package edu.fcla.daitss.util;
 
import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.exception.FatalException;
/**
 * <p>
 * Singleton class representing a log file.
 * </p>
 * 
 */
public class Log {

	/**
	 * <p>
	 * Represents ...
	 * </p>
	 */
	private static Log instance = null;

	/**
	 * 
	 * @return a Log
	 * </p>
	 * @throws FatalException 
	 */
	public static synchronized Log getInstance() throws FatalException {
		// this is used for unit testing purposes 
		// in production mode Log.getInstance(String archiveAction)
		if (instance == null) {
			getInstance(ArchiveManager.ACTION_UNIT_TEST);			
		}
		return instance;
	} // end getInstance        
	
	/**
	 * Log constructor to be called only by ArchiveProperties when an error is encoutered 
	 * while actually creating the ArchiveProperties object. It should only be used during an
	 * abnormal end.
	 * 
	 * @param logPath
	 * @return Log object
	 */
	public static synchronized Log getInstance(String logPath) {
		instance = new Log(logPath);
		return instance;
	}

	/**
	 * Constructs the singleton Log object and creates the log file.
	 * Currently the implemented archive actions are: Ingestor, Unit_Test
	 * @param archiveAction 
	 * @return a Log 
	 * </p>
	 * @throws FatalException 
	 */
	public static synchronized Log getInstance(byte archiveAction) throws FatalException {
		if (instance == null) {

			// calculate the ArchiveManager subclass portion of the log name
			String logPathPrefix = null;

			if (ArchiveManager.isValidAction(archiveAction)){
				logPathPrefix =
					ArchiveProperties.getInstance().getArchProperty(
						ArchiveManager.getActionText(archiveAction) + "_LOG");
				
				// note that if the desired property doesn't exist, then
				// Informer.fail will have been called and control would not 
				// have made it here, so it is not necessary to check the log
				// path prefix
				instance = new Log(logPathPrefix + ArchiveManager.getActionText(archiveAction));
			}
			else {
				// unknown archiveAction, need to fail
				Informer.getInstance().fail("Log", 
					"getInstance(byte archiveAction)",
					"Received unrecognized argument to Log.getInstance(String), unable to create log file.", 
					archiveAction + ": " + archiveAction, 
					new IllegalArgumentException("Unrecognized argument to Log.getInstance(String): " + archiveAction));
			}
		}
		return instance;
	} // end getInstance        

	/**
	 * Main function, testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Log l = Log.getInstance(ArchiveManager.ACTION_FIXITY_CHECK);
			l.add(new LogEntry("Log", "main", 10, "a fixity test"));			
		}
		catch (Exception e) {
			System.exit(1);
		}
	}

	/**
	 * <p>
	 * The absolute path to the log file.
	 * </p>
	 */
	private String logPath;

	/**
	 * <p>
	 * Constructor. Sets this.logPath to logPath
	 * </p>
	 * @param logPath 
	 */
	private Log(String logPath) {		
		// create the log file
		// calculate the timestamp portion of the log name using current time and date
		String time = DateTimeUtil.getDateTimeStamp();
		this.logPath = logPath + "_" + time + ".txt";
	} // end Log        

	/**
	 * <p>
	 * Open the file at this.logPath and add a log entry to the file. Close the file. 
	 * Needs to catch
	 * any exceptions while opening, writing, closing the file.
	 * </p>
	 * @param entry 
	 * @throws FatalException 
	 */
	public void add(LogEntry entry) throws FatalException {
		try {
			FileUtil.appendStringToFile(entry.toString(), this.logPath);
		}
		catch (Exception e) {
			// Note: cannot send Exceptions occuring here to Informer.
			// If an error occurs while writing to the Log and Informer.fail 
			// is called, Informer will create a new log entry, which will 
			// fail, resulting in a cycle. 
			throw new FatalException("Unable to write to log file: " + this.getLogPath() 
					+ "\n\t" + e.getMessage());
		}
	} // end add        

	/**
	 * @return String
	 */
	public String getLogPath() {
		return logPath;
	}
} // end Log
