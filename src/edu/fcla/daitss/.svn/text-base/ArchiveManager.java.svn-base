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
 */
package edu.fcla.daitss;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Log;

/** 
 * <p>
 * The main Class for the entire digital archive application.
 * a second comment
 * </p>
 * @author vicary
 */
public class ArchiveManager {

	/**
	 * @author vicary
	 *
	 * Inner class primarily used as a data structure to associate 
	 * archiveActions with their String representations.
	 */
 	static class archiveAction {
		private byte action;
		private String actionText;
		
		archiveAction (byte action, String actionText){	
			setAction(action);
			setActionText(actionText);
		}
	
		/**
		 * @return a byte representing a valid archiveAction
		 */
		public byte getAction() {
			return action;
		}

		/**
		 * @return a String represnting the textual form of an archiveAction
		 */
		public String getActionText() {
			return actionText;
		}

		/**
		 * @param b an archiveAction
		 */
		public void setAction(byte b) {
			action = b;
		}

		/**
		 * @param string representation of the archiveAction
		 */
		public void setActionText(String string) {
			actionText = string;
		}

	}
 	
 	// ARCHIVE ACTIONS
	// NOTE: WHEN A NEW ACTION IS CREATED:
	// 0) Archive action values must be unique
	// 1) IT MUST BE ADDED TO THE archiveAction array (note: order is not important)
	// 2) Log paths for the new action must be added to the right config file (configSystem)
	// 3) The action must be added to the ARCHIVE_ACTION "authority" list in configGeneral
	/**
	 * <p>Constant for the archiveAction of Disseminate</p> 
	 */	
	public static final byte ACTION_DISSEMINATE=0;

	/**
	 * <p>Constant for the archiveAction of integrity test</p> 
	 */	
	public static final byte ACTION_FIXITY_CHECK=1;		
	
	/**
	 * <p>Constant for the archiveAction of Ingest</p> 
	 */	
	public static final byte ACTION_INGEST=3;

	
	/**
	 * Constant for the archiveAction of KeyServer.
	 */
	public static final byte  ACTION_KEYSERVER = 5;
	
	/**
	 * <p>Constant for the archiveAction of Migrate</p> 
	 */	
	public static final byte ACTION_MIGRATE=6;
	/**
	 * <p>Constant for the archiveAction of none. In other words, archiveAction is unitialized.</p> 
	 */	
	public static final byte ACTION_NONE=7;
	/**
	 * <p>Constant for the archiveAction of Normalize</p> 
	 */	
	public static final byte ACTION_NORMALIZE=8;
	/**
	 * <p>Constant for the archiveAction of PreProcessor</p> 
	 */	
	public static final byte ACTION_PREPROCESS=9;	
	/**
	 * <p>Constant for the archiveAction of unit test</p> 
	 */	
	public static final byte ACTION_UNIT_TEST=10;

	/**
	 * <p>Constant for the archiveAction of withdrawal</p> 
	 */	
	public static final byte ACTION_WITHDRAWAL=11;	
	
	/**
	 * Array storage for all valid ArchiveActions. Primarily used to
	 * determine if a specific action is a valid archiveAction and for
	 * String->byte and byte->String translation. The order in which
	 * actions are added to this array is unimportant.
	 */
	private static final archiveAction[] archiveActions = {
		new archiveAction(ACTION_NONE, "NONE"),
		new archiveAction(ACTION_DISSEMINATE, "DISSEMINATION"),
		new archiveAction(ACTION_INGEST, "INGEST"),
		new archiveAction(ACTION_KEYSERVER, "KEYSERVER"),
		new archiveAction(ACTION_MIGRATE, "MIGRATE"),
		new archiveAction(ACTION_NORMALIZE, "NORMALIZE"),
		new archiveAction(ACTION_PREPROCESS, "PREPROCESSOR"),
		new archiveAction(ACTION_UNIT_TEST, "UNIT_TEST"),
		new archiveAction(ACTION_FIXITY_CHECK, "FIXITY_CHECK"),
		new archiveAction(ACTION_WITHDRAWAL, "WITHDRAWAL")
	};

	/**
	 * The only instance of the singleton ArchiveManager class
	 */
	private static ArchiveManager instance = null;

	/**
	 * Stores the current action being performed by the current archiverWorker.
	 */
	private byte archiveAction = ACTION_NONE;

	/**
	 * Points to the subclass of ArchiveWorker that is executing. Used to
	 * clean up after DAITSS fatal errors and package exceptions.
	 */
	private ArchiveWorker archiveWorker = null;

	/** 
	 * Protected constructor. Although this class is a singleton,
	 * the visibility of the contstructor needs to be protected so 
	 * it can be used by subclasses
	 */
	private ArchiveManager() {
	}

	/**
	 * Used to clean up after package exceptions
	 */
	public void error() {
		if (archiveWorker != null){
			archiveWorker.error();
		}			
	}

	/**
	 * @return The current archiveAction being performed
	 */
	public byte getArchiveAction() {
		return archiveAction;
	}

	/**
	 * @return Archive Worker object
	 */
	public ArchiveWorker getArchiveWorker() {
		return archiveWorker;
	}

	/**
	 * <p>
	 * 
	 * Initialize singleton objects that are shared across packages. In other
	 * words, these objects do not lose scope after a package is complete.
	 * </p>
	 * @param archiveAction 
	 * @param archiveWorker 
	 * @throws FatalException 
	 * 
	 */
	public void register(byte archiveAction, ArchiveWorker archiveWorker) throws FatalException {
		// check input
		if (!isValidAction(archiveAction)) {
			Informer.getInstance().fail(this, 
				"ArchiveManager.init(byte,ArchiveManager)",
				"Invalid archive action argument", 
				"archiveAction: " + archiveAction,
				new IllegalArgumentException("Unrecognized archiveAction"));
		}
		// generate log file
		Log.getInstance(archiveAction);
		// set the archive action
		setArchiveAction(archiveAction);
		// set the archive object
		setArchiveWorker(archiveWorker);		
	} // end init        

	/**
	 * Sets the current archiveAction to action. The action argument
	 * is expected to be a valid archive action based on the archiveAction 
	 * constants. If it is not a valid archiveAction, ArchiveManager will fail.
	 * @param archiveAction
	 * @throws FatalException 
	 */
	protected void setArchiveAction(byte archiveAction) throws FatalException {
		if (this.archiveAction != ACTION_NONE) {
			// archiveAction has already been set, there
			// should be no attempt to reset it. Programmer error. 
			Informer.getInstance().fail(this, 
				"ArchiveManager.setArchiveAction(byte)",
				"Illegal attempt to reset archiveAction",
				"archiveAction: " + archiveAction,
				new IllegalAccessException("Request to set archiveAction denied because archiveAction has already been set"));
		}
		
		if (!isValidAction(archiveAction)) {
			// unrecognized archiveAction
			Informer.getInstance().fail(this, 
				"ArchiveManager.setArchiveAction(byte)",
				"Unrecognized archiveAction argument",
				"archiveAction: " + archiveAction,
				new IllegalArgumentException("Unrecognized archiveAction"));
			
		}
		// if control made it here, then archiveAction is valid and
		// this.archiveAction has not already been set
		this.archiveAction = archiveAction;
	}		

	/**
	 * Set the current archiveWorker performing the current archiveAction. 
	 * The archiveWorker can only be set once per instance of ArchiveManager. An
	 * attempt to reset the object will result in failure.
	 * @param archiveWorker an ArchiveManager subclass that is about to begin processing
	 * @throws FatalException 
	 */
	public void setArchiveWorker(ArchiveWorker archiveWorker) throws FatalException {
		if (this.archiveWorker != null) {
			// archiveWorker has already been set, there
			// should be no attempt to reset it. Programmer error. 
			Informer.getInstance().fail(this, 
				"ArchiveManager.setArchiveWorker(ArchiveWorker)",
				"Illegal attempt to reset archiveWorker",
				"archiveWorker: " + archiveWorker.getClass().getName(),
				new IllegalAccessException("Request to set archiveWorker denied because archiveWorker has already been set"));
		}
		this.archiveWorker = archiveWorker;
	}

	/**
	 * <p>
	 * Checks that valid arguments were given to the ArchiveManager:main method.
	 * </p>
	 * @param args 
	 */
	private static void checkArgs(String[] args) {
	}
	
	/**
	 * Returns the byte version the archiveAction represented by the 
	 * String argument.
	 * @param archiveAction
	 * @return byte representation of the archiveAction
	 * @throws FatalException 
	 */
	public static byte getActionByte(String archiveAction) throws FatalException {
		for (int i=0;i<archiveActions.length;i++) {
			if (archiveActions[i].getActionText().equalsIgnoreCase(archiveAction)) {
				return archiveActions[i].getAction();
			}
		}
		// if control made it here, then the actionText wasn't found, 
		// implying programmer error
		Informer.getInstance().fail("edu.fcla.daitss.core.ArchiveManager",
			"ArchiveManager.getActionByte(String)", 
			"Invalid archiveAction argument", 
			"archiveAction: " + archiveAction,
			new IllegalArgumentException("Unrecognized archiveAction"));

		// this will never be executed, but it's needed for the compiler
		return ACTION_NONE;
	}



	/**
	 * Returns the string version of the archiveAction
	 * @param archiveAction
	 * @return String representation of the archiveAction
	 * @throws FatalException 
	 */
	public static String getActionText(byte archiveAction) throws FatalException {
		for (int i=0;i<archiveActions.length;i++) {
			if (archiveActions[i].getAction() == archiveAction) {
				return archiveActions[i].getActionText();
			}
		}		
		// if control made it here, then the actionText wasn't found, 
		// implying programmer error
		// invalid input
		Informer.getInstance().fail("ArchiveManager", 
			"ArchiveManager.getActionText(byte)",
			"Invalid archiveAction argument", 
			archiveAction + "",
			new IllegalArgumentException("Unrecognized archiveAction"));
		// this will never be executed, but it's needed for the compiler
		return "NONE";		
	}
	
    /**
     * 
     * @return Archive Manager
     */
	public static synchronized ArchiveManager getInstance(){
		if (instance == null) {
			instance = new ArchiveManager();
		}
		return instance;
	}

	/**
	 * Determines if the archiveAction is a recognized archiveAction
	 * @param archiveAction
	 * @return true if the archiveAction is recognized, false otherwise
	 */
	public static boolean isValidAction(byte archiveAction) { 
		// search for the archiveAction in the archiveActions array
		for (int i=0;i<archiveActions.length;i++) {
			if (archiveActions[i].getAction() == archiveAction) {
				return true;
			}
		}
		// if control made it here, then archiveAction was not found
		return false;
	}
} // end ArchiveManager
