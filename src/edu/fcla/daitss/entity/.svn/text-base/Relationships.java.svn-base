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
package edu.fcla.daitss.entity;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.Informer;

import java.util.Vector;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A singleton class representing all the relationships among data files in
 * a package.

 */
public class Relationships {

	/**
	 * Represents all relationship objects for a package.
	 */
	private static Relationships instance = null;

	/**
	 * A convenience method to destroy the Relationships since a new 
	 * one is needed by each package.
	 */
	public static void die() {
	    if (instance != null) {
	        instance = null;
	    }	    
	}	

	/**
	 * Returns the single instance of Relationships.
	 * 
	 * @return the set of all relationships for this package
	 */
	public static synchronized Relationships getInstance() {
		if (instance == null){
			instance = new Relationships();
		}
		return instance;
	}   
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 */
	public static void main(String args[]){
		try{
			Relationship rel = new Relationship("F00000000_AAAAAA", 
			        "F00000000_AAAAAA", Relationship.REL_CHILD, null);
			Relationships rels = Relationships.getInstance();
			System.out.println( (rels == null));
			rels.add(rel);
			

			Relationship rel2 = new Relationship("F00000000_AAAAAA", 
			        "F00000000_AAAAAA", Relationship.REL_MIGRATED_TO, null);
			Relationships.getInstance().add(rel2);
			
			Relationships.getInstance().dbUpdate(ArchiveManager.ACTION_INGEST);
			
		} catch(FatalException fe){
			fe.printStackTrace();
		} catch(PackageException pe){
			pe.printStackTrace();
		}
		
	}

	/**
	 * A vector to store Relationship objects.
	 */
	private Vector relationships;

	/**
	 * The vector of relationships is initialized here.
	 */
	private Relationships() {
		relationships = new Vector(50, 25);
	}        

	/**
	 * Adds a relationship between 2 archive objects to the set of
	 * all relationships for this package.
	 * 
	 * @param relationship a relationship between 2 archive objects
	 * @throws FatalException
	 */
	public void add(Relationship relationship) throws FatalException {
		String methodName = "add(Relationship)";
		
		if (relationship == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Null argument",
				"arg: relationship",
				new IllegalArgumentException("relationship can't be null"));
		}
		// add the relationship if its unique
		// (has a unique combination of dfid1, dfid2, relationType) 
		// note: it appears that they are always unique anyway because
		// of the way we process the package - revisit later if this needs
		// to actually make sure they're unique
		relationships.add(relationship);
	}    

	/**
	 * Inserts all its relationship objects into the RELATIONSHIP table in the archive
	 * database. Essentially it iterates through the relationships Vector and calls
	 * dbUpdate for each stored relationship, which in turn will add the relationship 
	 * insert to the shared transaction connection. 
	 * @param archiveAction 
	 * @return integer 
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	public int dbUpdate(byte archiveAction) 
		throws FatalException, PackageException{

	    int rowsAffected = 0;
	    
		Iterator iter = this.relationships.iterator();
		while (iter.hasNext()) {
			Relationship r = (Relationship)iter.next();
			rowsAffected += r.dbUpdate(archiveAction);
		}	
		
		return rowsAffected;
	}       
	
	/**
	 * @return the vector of relationships of this object
	 */
	public Vector getRelationships() {
		return relationships;
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
	    Vector v = new Vector(this.relationships.size());
	    Iterator iter = this.relationships.iterator();	
        while (iter.hasNext()) {
            Relationship r = (Relationship) iter.next();
            if (r.getDfid1().equals(dfid) || r.getDfid2().equals(dfid)) {
                // don't add the relationship to the new Vector
            }
            else {
                // this relationship doesn't reference the deleted dfid, 
                // add it to the new Vector
                v.add(r);
            }
        }
        
	    // swap the old relationships vector for the new one
        setRelationships(v);
	}
	
	/**
	 * Set the relationships member to _relationships
	 * @param _relationships 
	 */
	public void setRelationships(Vector _relationships) {
		relationships = _relationships;
	}       

	/**
	 * Builds an XML representation of this Relationships.
	 * @return Document object
	 * @throws FatalException
	 */
	public Document toXML() throws FatalException {
	   
	    /* Document. */
        Document doc = XPaths.newDaitssDoc();

        /* Root element. */
        Element rootElement = doc.getDocumentElement();
        
        // Get all the events
        for (Iterator iterator = relationships.iterator(); iterator.hasNext();) {
            Relationship r = (Relationship) iterator.next();
			Document rDoc = r.toXML();			
			// Select all the event nodes.
            NodeList ns = XPaths.selectNodeList(rDoc, XPaths.Daitss.RELATIONSHIP);
            // Insert all the event nodes into the diatss document.
            for(int i = 0; i < ns.getLength(); i++){
                if (ns.item(i) instanceof Element) {
                    Node n = doc.importNode(ns.item(i), true);
                    rootElement.appendChild(n);
                }
            }            
        }
        
        return doc;
	}
} // end Relationships
