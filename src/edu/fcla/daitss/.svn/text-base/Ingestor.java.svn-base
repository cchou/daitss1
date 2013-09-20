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
/** Java class "Ingestor.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss;

import edu.fcla.daitss.entity.SIPList;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.JhoveEngine;

/**
 * <p>
 * Performs all actions needed to ingest digital objects into the archive.
 * </p>
 * 
 */
public class Ingestor extends ArchiveWorker {

    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */

	/**
	 * Initializes objects that are used across packages.
	 */
	private void init() throws FatalException {
		String methodName = "init()";
		
		Informer.getInstance().info(
			this,
			methodName,
			"Initializing Ingestor",
			"Ingestor",
			false);
				
		// the primary function of this line is simply to initialize the 
		// ArchiveProperties singleton
		ArchiveProperties.getInstance();

		// set the current archive action
		ArchiveProperties.getInstance().setArchProperty(
				"ARCHIVE_ACTION",
			ArchiveManager.getActionText(ArchiveManager.ACTION_INGEST));

		// initialize the Informer singleton (and therefore the Log singleton)
		Informer.getInstance(ArchiveManager.ACTION_INGEST);
		
		// initialize the JhoveEngine singleton
		JhoveEngine.getInstance();
	}

	/**
	 * Checks that valid arguments were given to the Ingestor:main method.
	 * Valid arguments: -p <space-delimited name=value list>
	 * No spaces allowed between the name and the '=' and between
	 * the value and the '='.
	 * Will not check that the name is a valid property or that the
	 * value is a legal value for that property.
	 */
	private static void checkArgs(String[] args) {
		// doesn't have to have any arguments
		if (args.length == 0) {
			return;
		}
		if (!args[0].equals("-p")) {
			// throw an IllegalArgumentException
		}
		// parse the rest of the arguments looking for valid-looking name=value pairs

	} // end checkArgs        

	/**
	 * Creates a new instance of Ingestor ing; Calls ing.process
	 * 
	 * @param args ...
	 * </p>
	 * @throws FatalException 
	 */
	public static void main(String[] args) throws FatalException {
		checkArgs(args);
		Ingestor ing = new Ingestor();
		ing.process();
	} // end main         

	private SIPList list = null;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * @throws FatalException 
	 * 
	 */
	public Ingestor() throws FatalException {
        ArchiveManager.getInstance().register(ArchiveManager.ACTION_INGEST, this);
        setTempDir();
	}

	/** 
	 * Method to handle Ingestor error (when a PackageException
	 * is encountered). 
	 */
	public void error() {
		// destroy any objects/sinlgetons that should not remain
		// across packages.
	}

	/* 
	 * Method to handle Ingestor program failure (when a 
	 * FatalException is encountered).
	 */
	public void fail() {	    
		// stop everything
		System.exit(1);
	}

	/**
	 * init(); Creates a SIPList ipList; sets this.list = ipList, calls ipList.process() (in a try/catch
	 * block).
	 * 
	 * Catch exceptions in this order:
	 * 
	 * First catch a FatalException, if so, call
	 * Log.getInstance().add(exception.getLogEntry()) and then
	 * System.exit(exception.getLogEntry().getErrorCode());
	 * 
	 * Next try to catch an Exception, if one is caught, create a
	 * LogEntry(null, LogEntry.IND_LOG, LogEntry.FATAL, exception.getMessage(),
	 * &quot;UNKNOWN&quot;); call Log.getInstance().add(logEntry); System.exit(logEntry.getErrorCode()).
	 * 
	 */
	public void process() {

		try {
		    // this will cause the log to be initialized, among other things
		    init();
		    
		    Informer.getInstance().info("Begin Ingestor processing", "Ingestor");
			
			// initialize the SIPList
			list = new SIPList();
			// build the SIPList
			list.build();
			// start working 
			list.process();
		} catch (FatalException fe) {
			// fail
		    fe.printStackTrace();
		    fail();			
			
		} catch (Exception e) {			
		    e.printStackTrace();
			fail();
		}
	} // end process        

} // end Ingestor
