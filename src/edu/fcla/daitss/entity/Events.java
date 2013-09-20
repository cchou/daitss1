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

/** Java class "Events.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss.entity;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.MySqlTransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.StringUtil;


/**
 * A singleton class representing all the events that should be recorded 
 * in the archive database.
 */
public class Events {
	
	/**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = Events.class.getName();

	/**
	 * Represents the singleton instance of the Events class
	 */
	private static Events instance = null;
	
	/**
	 * A convenience method to destroy the Events object since a new 
	 * one is needed by each package.
	 */
	public static void die() {
	    if (instance != null) {
	        instance = null;
	    }	    
	}

	/**
	 * Returns an instance of Events.
	 * 
	 * @return the only instance of this Class
	 */
	public static synchronized Events getInstance() {
		if (instance == null) {
			instance = new Events();
		}
		return instance;
	}         
	
	/**
	 * For testing purposes.
	 * 
	 * @param args not used
	 * @throws PackageException 
	 * @throws FatalException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) 
		throws PackageException, FatalException, SQLException {
		System.out.println("F20040501_AAAAAA is valid key: " + OIDServer.isValidDfid("F20040501_AAAAAA"));
		Events events = new Events();
		Event e = new Event("F20040501_AAAAAA", Event.EVENT_CHANGED_PRES_DOWN, 
		        DateTimeUtil.now(), "changed pres level", Event.OUTCOME_SUCCESS, "", null);
		events.add(e);
		e = new Event("F20040501_AAAAAA", Event.EVENT_VERIFIED_CHECKSUM, 
		        DateTimeUtil.now(), "verified checksum", Event.OUTCOME_FAIL, "", null);
		events.add(e);
		e = new Event("F20040501_AAAAAA", Event.EVENT_VERIFIED_CHECKSUM, 
		        DateTimeUtil.now(), "verified checksum", 
		        Event.OUTCOME_FAIL, "", null);
		events.add(e);
		System.out.println(events.toString());
		
		MySqlTransactionConnection.getSharedConnection().startTransaction();
		events.dbUpdate(ArchiveManager.ACTION_INGEST);
		MySqlTransactionConnection.getSharedConnection().commitTransaction();
		MySqlTransactionConnection.getSharedConnection().close();
			
	}

	/**
	 * Represents storage for all Event objects
	 */
	private Vector events = null;

	/**
	 * Sets this.events = new Vector(50,25);
	 */
	private Events() {
		this.events = new Vector(50,25);
	} // end Events        

	/**
	 * Adds a single Event to the events Vector.
	 * 
	 * @param event a non-null Event
	 * @throws FatalException
	 */
	public void add(Event event) throws FatalException {
		String methodName = "addEvent(Event)";
		
		// check input
		if (event == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"event: " + event,
				new FatalException("event argument must not be null"));
		}
		this.events.add(event);
	}         

	/**
	 * Inserts all its events into the event-related table in the 
	 * archive database.
	 * @param archiveAction 
	 * @return integer
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 */
	public int dbUpdate(byte archiveAction) throws FatalException, PackageException{
		int rowsAffected = 0;
	    
		Iterator iter = this.events.iterator();
		while (iter.hasNext()) {
			Event e = (Event)iter.next();
			rowsAffected += e.dbUpdate(archiveAction);
		}				
		
		return rowsAffected;
	}         
	
	/**
	 * @param dfid
	 * @throws FatalException
	 */
	public void removeReferences(String dfid) throws FatalException {
	    String methodName = "removeReferences(String)";
	    // check input
	    if (dfid == null || dfid.equals("")) {
	        Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "dfid: " + dfid,
                    new IllegalArgumentException());
	    }
	    
	    // iterate through the relatioships and remove affected 
	    // relationship objects
	    Vector v = new Vector(this.events.size());
	    Iterator iter = this.events.iterator();	
        while (iter.hasNext()) {
            Event e = (Event) iter.next();
            if (e.getOid().equals(dfid) || (e.getRelId() != null && e.getRelId().equals(dfid))) {
                // don't add the event to the new vector
            }
            else {
                // event doesn't reference dfid, add it to new events
                v.add(e);
            }
        }
	    
        this.events = v;
        
	}	
	
	/**
	 * Returns a String representation of an Events instance.
	 * 
	 * @return the members of Event
	 */
	public String toString() {
		return (StringUtil.getString(this.events));
	}

	/**
	 * Builds an XML representation of this Events.
	 * @return Document object
	 * @throws FatalException
	 */
	public Document toXML() throws FatalException {

        // Document.
        Document doc = XPaths.newDaitssDoc();

        // Root element.
        Element rootElement = doc.getDocumentElement();
        
        
        // Get all the events
        Iterator iter = this.events.iterator();
		while (iter.hasNext()) {
			Event e = (Event)iter.next();
			Document eDoc = e.toXML();			
			// Select all the event nodes.
			NodeList ns = XPaths.selectNodeList(eDoc, XPaths.Daitss.EVENT);
            // Insert all the event nodes into the diatss document.
            for (int i = 0; i < ns.getLength(); i++) {
                Node n = doc.importNode(ns.item(i), true);
                rootElement.appendChild(n);
            }
		}
        
        return doc;
	}

} 
