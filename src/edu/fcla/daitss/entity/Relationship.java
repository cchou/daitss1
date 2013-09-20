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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.util.Informer;

/**
 * A relationship between two datafiles.
 */
public class Relationship {
	
	/**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.util.Relationship";
    
	/**
	 * The minimal possible event id.
	 */
	public static final int MIN_EVENT_ID = 1;

	// All relationship types and only relationship types must start with "REL_"
	/**
	 * A parent relationship.
	 */
	public static final String REL_CHILD = "CHILD_OF";
	
	/**
	 * A localization relationship.
	 */
	public static final String REL_LOCALIZED_TO = "LOCALIZED_TO";
	
	/**
	 * A migration relationship.
	 */
	public static final String REL_MIGRATED_TO = "MIGRATED_TO";

	/**
	 * A normalization relationship.
	 */
	public static final String REL_NORM_TO = "NORM_TO";

	/**
	 * Unknown relationship (the default)
	 */
	public static final String REL_UNKNOWN = "UNKNOWN";
	
	/**
	 * All possible event types
	 */
	private static Vector relTypes = new Vector(12,20);
	
	/**
	 * Initialization of relType, which contains all possible event types 
	 * @throws FatalException
	 */
	private static void initRelTypes() throws FatalException{
		String methodName = "initRelTypes()";
		
		//Quit if relTypes has been initilized
		if (relTypes.size() >0)
			return;
			
		Field[] fields = Relationship.class.getFields();
		
		int len = fields.length;
		for ( int i = 0; i<len; i++){
			String member = fields[i].getName();
			if (member.matches("REL_.*")){
				try{
					Relationship.relTypes.add(fields[i].get(null));
				} catch(IllegalArgumentException iae){
					Informer.getInstance().fail(
						CLASSNAME,
						methodName,
						"Illegal argument",
						member,
						iae);						
				} catch (IllegalAccessException iace) {
					Informer.getInstance().fail(
						CLASSNAME,
						methodName,
						"Illegal access",
						member,
						iace);						
				}	
			}
		}
	}
	
	/**
	 * Check the validity of a relationship type
	 * UNKNOWN is not allowed
	 * @param type The relationship type to be checked
	 * @return true, if type is valid; false otherwise
	 */
	public static boolean isValidRelType(String type){
			if (type ==null || type.equals(REL_UNKNOWN))
				return false;
		    Iterator iter = relTypes.iterator();
		    while(iter.hasNext()){
		    	if (((String)iter.next()).equals(type)){
		    		return true;    
		    	}
		    }
		    return false;
	}
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String args[]) throws FatalException {
	    DBConnection conn = DBConnection.getConnection();
		Relationship rel = new Relationship("F20060810_AAAAAF", conn, Relationship.REL_CHILD);
		try {
		conn.close();
		} catch (SQLException e) {
		}
	}

	/**
	 * The DFID of the datafile that is the "subject" of the relationship.
	 * 
	 */
	private String dfid1 = null;

	/**
	 * The DFID of a datafile that is the "object" of the relationship.
	 */
	private String dfid2 = null;
	
	/**
	 * The event that creates this relationship, if applicable
	 */
	private Event event = null;

	/**
	 * The relationship type of dfid1 with respect to dfid2. Must be a constant
	 * defined in this class ("REL_*").
	 */
	private String relationType = REL_UNKNOWN;


	/**
	 * Sets the relationType, dfid1 and dfid2 members.
	 * 
	 * @param _dfid1 Id of the subject of the relationship
	 * @param _dfid2 Id of the object of the relationship
	 * @param _relationType type of relationship
	 * @throws FatalException
	 */
	public Relationship(String _dfid1, String _dfid2, String _relationType)
		throws FatalException {

	    initRelTypes();
		setDfid1(_dfid1);
		setDfid2(_dfid2);
		setRelationType(_relationType);
	}    

	/**
     * Constructor to use when there is an associated event.
     * 
     * @param _dfid1 Id of the first data file in the relationship
     * @param _dfid2 Id of the second data file in the relationship
     * @param _relationType Type of the relationship
     * @param _event the event (if any) that resulted in the relationship 
     * @throws FatalException
     */
    public Relationship(String _dfid1, String _dfid2, String _relationType,
            Event _event) throws FatalException {

        this(_dfid1, _dfid2, _relationType);
        setEvent(_event);
    }
		
    /**
     * build a relationship object from the database
     * @param _dfid1
     * @param dbConn
     * @throws FatalException
     */
    public Relationship(String _dfid1, DBConnection dbConn, String relType) throws FatalException{
    	String sql = String.format(
    			"select * from %s where %s = %s and %s = %s",
    			ArchiveDatabase.TABLE_RELATIONSHIP, 
    			ArchiveDatabase.COL_RELATIONSHIP_DFID_1,
    			SqlQuote.escapeString(_dfid1),
				ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE,
				SqlQuote.escapeString(relType));       
         
        try {
        	ResultSet results = dbConn.executeQuery(sql);
            if (results.first()) {
        	    initRelTypes();
        		setDfid1(_dfid1);
        		setDfid2(results.getString(ArchiveDatabase.COL_RELATIONSHIP_DFID_2));
        		setRelationType(results.getString(ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE));
            }
            results.close();
        } catch (SQLException e) {
			throw new RuntimeException(e);
        }
    }
	/**
	 * 
	 * Write the Relationship to the shared transaction connection.
	 * 
	 * @param archiveAction the current function of the archive
	 * @return the number of rows affected by the query
	 * @throws FatalException
	 */
	public int dbUpdate(byte archiveAction) throws FatalException{
		
		String methodName = "dbUpdate(byte)";
		int affected = 0;
		
		//	do a different database update depending on the archive action
		switch (archiveAction) {
			case ArchiveManager.ACTION_INGEST :
				// add all Relationship members to the database
				String table = ArchiveDatabase.TABLE_RELATIONSHIP;
				DBConnection.NameValueVector values = new DBConnection.NameValueVector();
			
				values.addElement(ArchiveDatabase.COL_RELATIONSHIP_DFID_1,getDfid1());
				values.addElement(ArchiveDatabase.COL_RELATIONSHIP_DFID_2,getDfid2());
				if (getEvent() != null) {
					values.addElement(ArchiveDatabase.COL_RELATIONSHIP_EVENT_ID,new Long(getEvent().getId()));
				}
				values.addElement(ArchiveDatabase.COL_RELATIONSHIP_REL_TYPE,getRelationType());
								
				TransactionConnection tcnn = DBConnection.getSharedConnection();
				
				affected = tcnn.insert(table, values);				
				break;
			case ArchiveManager.ACTION_MIGRATE :
			    break;
			case ArchiveManager.ACTION_NORMALIZE :
				break;
			case ArchiveManager.ACTION_DISSEMINATE :
				break;
			default :
				// unknown archive action - fatal error
				Informer.getInstance().fail(
					this, methodName,
					"Illegal argument",
					"archiveAction: " + archiveAction,
					new FatalException("Not a valid archive action"));
		}
		
		return affected;
	}
	
	/**
	 * Get the first dfid on the relationship
	 * @return Id of the first data file in this relationship
	 */
	public String getDfid1() {
		return dfid1;
	}         

	/**
	 * Get the second dfid on the relationship
	 * @return Id of the second data file in this relationship
	 */
	public String getDfid2() {
		return dfid2;
	}  

	/**
	 * Get the event that results in the relationship, 
	 * or null if there is no associated event
	 * 
	 * @return the event that results in the relationship
	 */
	public Event getEvent() {
		return event;
	}         
	       
	/**
	 * Get the type of relationship
	 * @return relathipType of this relationship
	 */
	public String getRelationType() {
		return relationType;
	}         
	
	/**
	 * Set the ID of the "subject" of this relationship. 
	 * May not be null
	 * @param _dfid1 The unique ID of the subject of the relationship
	 * @throws FatalException
	 */
	public void setDfid1(String _dfid1) throws FatalException {

		String methodName = "setDfid1(String)";

		if (_dfid1 == null || !OIDServer.isValidDfid(_dfid1)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_dfid1 " + _dfid1,
				new IllegalArgumentException(
					"DFID must be valid"));
		}
		dfid1 = _dfid1;
	}    

	/**
	 * Set the ID of the "object" of this relationship.
	 * May not be null
	 * @param _dfid2 : The unique ID of the object of the relationship
	 * @throws FatalException
	 */
	public void setDfid2(String _dfid2) throws FatalException  {
		String methodName = "setDfid2(String)";

		if (_dfid2 == null || !OIDServer.isValidDfid(_dfid2)) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_dfid2 " + _dfid2,
				new IllegalArgumentException(
					"DFID must be valid"));
		}		
		dfid2 = _dfid2;
	}
	
	/**
	 * Set the unique ID for the associated event.
	 * 
	 * @param _event event ID
	 * @throws FatalException
	 */
	public void setEvent(Event _event) throws FatalException{		
		this.event = _event;
	}

	/**
	 * Sets the relationship type. 
	 * Must be a constant declared in this class (prefix REL_).
	 * 
	 * @param _relationType The relation type
	 * @throws FatalException
	 */
	public void setRelationType(String _relationType) throws FatalException {

		String methodName = "seRelationtype(String)";

		if (!isValidRelType(_relationType)) {

			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"_relationType: " + _relationType,
				new IllegalArgumentException("Must be a valid relationship type"));
		}

		relationType = _relationType;
	}      

	/**
	 * Builds an xml representation of this Relationship.
	 * @return Document object
	 * @throws FatalException
	 */
	public Document toXML() throws FatalException {

        /* Document. */
        Document doc = XPaths.newDaitssDoc();

        /* Root element. */
        Element rootElement = doc.getDocumentElement();
        
        /* Namespace. */
        String namespace = rootElement.getNamespaceURI();        
        
        /* Relationship */
        Element relElement = doc.createElementNS(namespace, "RELATIONSHIP");
        rootElement.appendChild(relElement);

        /* Dfid 1 */
        Element dfid1Element = doc.createElementNS(namespace, "DFID_1");
        String dfid1Value = (this.getDfid1() != null ? this.getDfid1() : "" );
        Text dfid1Text = doc.createTextNode(dfid1Value);
        dfid1Element.appendChild(dfid1Text);
        relElement.appendChild(dfid1Element);

        /* Rel Type */
        Element relTypeElement = doc.createElementNS(namespace, "REL_TYPE");
        String relTypeValue = (this.getRelationType() != null ? this.getRelationType() : "" );
        Text relTypeText = doc.createTextNode(relTypeValue);
        relTypeElement.appendChild(relTypeText);
        relElement.appendChild(relTypeElement);

        /* Dfid 2 */
        Element dfid2Element = doc.createElementNS(namespace, "DFID_2");
        String dfid2Value = (this.getDfid2() != null ? this.getDfid2() : "" );
        Text dfid2Text = doc.createTextNode(dfid2Value);
        dfid2Element.appendChild(dfid2Text);
        relElement.appendChild(dfid2Element);

        if(this.event != null) {
            /* Event Id */
            Element eventIdElement = doc.createElementNS(namespace, "EVENT_ID");
            String eventIdValue = Long.toString(this.event.getId());
            Text eventIdText = doc.createTextNode(eventIdValue);
            eventIdElement.appendChild(eventIdText);
            relElement.appendChild(eventIdElement);            
        }
        
        return doc;
    }
} 
