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
package edu.fcla.daitss.entity;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * When new event type codes are added, the general convention is Verb+Object,
 * for example CHECKED_FOR_VIRUS not VIRUS_CHECKED.
 * 
 * New outcome constants must be prefixed by OUTCOME_. New event type codes must
 * be prefixed by EVENT_.
 */
public class Event {
    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = Event.class.getName();
    
    /**
     * Changed preservation level downward.
     */
    public static final String EVENT_CHANGED_PRES_DOWN = "CPD";

    /**
     * Changed preservation level upward.
     */
    public static final String EVENT_CHANGED_PRES_UP = "CPU";

    /**
     * Checked for virus
     */
    public static final String EVENT_CHECKED_FOR_VIRUS = "CV";

    /**
     * A intellectual entity or datafile was disseminated.
     */
    public static final String EVENT_DISSEMINATED = "D";
    
    /**
     * A linked-to file was downloaded from the Internet.
     */
    public static final String EVENT_DOWNLOADED_LINK = "DLK";

    /**
     * A intellectual entity or datafile was ingested.
     */
    public static final String EVENT_INGESTED = "I";
    
    /**
     * An attempt was made to localize a datafile.
     */
    public static final String EVENT_LOCALIZED_TO = "L";

    /**
     * An attempt was made to migrate a datafile.
     */
    public static final String EVENT_MIGRATED_TO = "M";

    /**
     * An attempt was made to normalize a datafile.
     */
    public static final String EVENT_NORMALIZED_TO = "N";

    /**
     * Refreshed media.
     */
    public static final String EVENT_REFRESHED_MEDIA = "RM";

    /**
     * The datafile's checksum was verified.
     */
    public static final String EVENT_VERIFIED_CHECKSUM = "VC";

    /**
     * The datafile's fixity was verified.
     */
    public static final String EVENT_VERIFIED_FIXITY = "FC";

    /**
     * The object represented by oid was withdrawn by the archive
     */
    public static final String EVENT_WITHDRAWN_BY_ARCHIVE = "WA";

    /**
     * * The object represented by oid was withdrawn by the depositor
     */
    public static final String EVENT_WITHDRAWN_BY_OWNER = "WO";

    /**
     * Convenient storage for all possible event codes.
     */
    private static Vector eventTypes = null;

    /**
     * The maximum length of the note field.
     */
    private static int MAX_NOTE_LEN = (int) Math.pow(2, 100);

    /**
     * The maximum length of the procedure field.
     */
    private static int MAX_PROCEDURE_LEN = 255;

    /**
     * The event failed.
     */
    public static final String OUTCOME_FAIL = "FAIL";

    /**
     * Outcome doesn't apply to this event.
     */
    public static final String OUTCOME_NOT_APPLICABLE = "N/A";

    /**
     * The event was not fully successful.
     */
    public static final String OUTCOME_PARTIAL_SUCCESS = "P_SUCCESS";

    /**
     * The event was completely successful
     */
    public static final String OUTCOME_SUCCESS = "SUCCESS";

    /**
     * The outcome was unknown.
     */
    public static final String OUTCOME_UNKNOWN = "UNKNOWN";

    /**
     * Convenient storage for all possible outcome types.
     */
    private static Vector outcomes = null;

    /**
     * Method to initialize static data structures.
     * 
     * @throws FatalException
     */
    private static void init() throws FatalException {
        initOutcomes();
        initEventTypes();
    }

    /**
     * Initializes the Vector of possible event type codes. The eventTypes
     * Vector is static and therefore available to all instances.
     * 
     * @throws FatalException
     */
    private static void initEventTypes() throws FatalException {
        String methodName = "initEventTypes()";
        if (eventTypes == null) {
            // build the outcome Vector used for checking outcome codes
            //Field[] members = new Event().getClass().getFields();
            Field[] members = Event.class.getFields();
            eventTypes = new Vector();
            for (int i = 0; i < members.length; i++) {
                if (members[i].getName().matches("EVENT_.*")) {
                    try {
                        eventTypes.add(members[i].get(members[i]));
                    } catch (IllegalArgumentException e) {
                        Informer.getInstance().fail(CLASSNAME, methodName,
                                "Illegal argument", members[i].getName(), e);
                    } catch (IllegalAccessException e) {
                        Informer.getInstance().fail(CLASSNAME, methodName,
                                "Illegal access", members[i].getName(), e);
                    }
                }
            }
        }
    }

    /**
     * Initializes the possible outcome codes. The outcomes Vector is static and
     * therefore available to all instances.
     * 
     * @throws FatalException
     */
    private static void initOutcomes() throws FatalException {
        String methodName = "initOutcomes()";

        if (outcomes == null) {
            // build the outcome Vector used for checking outome codes
            Field[] members = Event.class.getFields();
            outcomes = new Vector();
            for (int i = 0; i < members.length; i++) {
                if (members[i].getName().matches("OUTCOME_.*")) {
                    try {
                        outcomes.add(members[i].get(members[i]));
                    } catch (IllegalArgumentException e) {
                        Informer.getInstance().fail(CLASSNAME, methodName,
                                "Illegal argument", members[i].getName(), e);
                    } catch (IllegalAccessException e) {
                        Informer.getInstance().fail(CLASSNAME, methodName,
                                "Illegal access", members[i].getName(), e);
                    }
                }
            }
        }

    }

    /**
     * Static method to determine the validity of an outcome.
     * 
     * @param outcome
     * @return true if outcome is a recognized outcome, else false
     * @throws FatalException
     */
    private static boolean isValidOutcome(String outcome) throws FatalException {
        initOutcomes();
        boolean isValid = false;

        Iterator iter = outcomes.iterator();
        while (iter.hasNext()) {
            String validOutcome = (String) iter.next();
            if (outcome.equals(validOutcome)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    /**
     * Static method to check the validity of a type code.
     * 
     * @param typeCode
     * @return true if typeCode is a recognized type code, else false
     * @throws FatalException
     */
    private static boolean isValidTypeCode(String typeCode)
            throws FatalException {

        initEventTypes();
        boolean isValid = false;

        Iterator iter = eventTypes.iterator();
        while (iter.hasNext()) {
            String eventType = (String) iter.next();
            if (typeCode.equals(eventType)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    /**
     * Test driver.
     * 
     * @param args not used
     * @throws PackageException
     * @throws FatalException
     */
    public static void main(String[] args) throws PackageException,
            FatalException {				
        
        Event e = new Event("F12345678_AAAAAA", Event.EVENT_VERIFIED_CHECKSUM,
                DateTimeUtil.now(),
                "verified checksum", Event.OUTCOME_FAIL, "", null);
        System.out.println(e);
    }

    /**
     * The date and time when the event occurred. Format YYYY-MM-DD HH:MM:SS.
     */
    private String dateTime = null;

    /**
     * For Event objects created from db query. Stores the EVENT.id.
     */
    private long id;

    /**
     * Any additional information relevant to the event, for example, an
     * unexpected outcome.
     */
    private String note = null;

    /**
     * The unique ID of the ArchiveEntity to which this event applies.
     */
    private String oid = null;

    /**
     * The event outcome. Must be present in the outcomes Vector.
     */
    private String outcome = null;

    /**
     * A textual description of the procedure leading to the outcome of this
     * event.
     */
    private String procedure = null;

    /**
     * A related id for the event. For example, if the event type is
     * MIGRATED_TO, relId will be the OID of the source, and oid will be the
     * OID of the resulting object.
     */
    private String relId = null;

    /**
     * A coded value indicating the type of event. Must be a constant declared
     * in this class (prefixed with EVENT_).
     */
    private String typeCode = null;

    /**
     * Constructor used when retrieving info from the database.
     * 
     * @param ID event ID
     * @param oid object ID
     * @param typeCode event type
     * @param dateTime event timestamp
     * @param procedure event procedure description
     * @param outcome event outcome
     * @param note event notes
     * @param relId related object ID
     * @throws FatalException
     */
    public Event(long ID, String oid, String typeCode, String dateTime,
            String procedure, String outcome, String note, String relId)
            throws FatalException {

        init();

        setId(ID);
        setOid(oid);
        setTypeCode(typeCode);
        setDateTime(dateTime);
        setProcedure(procedure);
        setOutcome(outcome);
        setNote(note);
        setRelId(relId); // allows null
    }

    /**
     * Constructor used when inserting info into the database.
     * 
     * @param oid object ID
     * @param typeCode event type
     * @param dateTime event timestamp
     * @param procedure event procedure description
     * @param outcome event outcome
     * @param note event notes (can be empty string, not null)
     * @param relId related object ID (can be null)
     * @throws FatalException
     */
    public Event(String oid, String typeCode, String dateTime,
            String procedure, String outcome, String note, String relId)
            throws FatalException {

        this(OIDServer.getNewEventId(), oid, typeCode, dateTime, procedure, outcome,
                note, relId);

    } 

    /**
     * Constructor used when inserting info into the database.
     * 
     * @param oid object ID
     * @param typeCode event type
     * @param procedure event procedure description
     * @param outcome event outcome
     * @param note event notes (can be empty string, not null)
     * @param relId related object ID (can be null)
     * @throws FatalException
     */
    public Event(String oid, String typeCode, String procedure, String outcome, String note)
            throws FatalException {

        this(OIDServer.getNewEventId(), oid, typeCode, DateTimeUtil.now(), procedure, outcome,
                note, null);

    } 

    
	/**
	 * Write the Event to the shared transaction connection.
	 * 
	 * @param archiveAction the current function of the archive
	 * @return the number of database rows inserted
	 * @throws FatalException
	 */
	public int dbUpdate(byte archiveAction) throws FatalException {
		
		String methodName = "dbUpdate(byte)";
		int affected = 0;
		
		// do a different database update depending on the archive action
		switch (archiveAction) {
		case ArchiveManager.ACTION_FIXITY_CHECK:
		case ArchiveManager.ACTION_INGEST:
			// add Event members to the database
			String table = ArchiveDatabase.TABLE_EVENT;
			DBConnection.NameValueVector values = new DBConnection.NameValueVector();

			values.addElement(ArchiveDatabase.COL_EVENT_DATE_TIME,
					getDateTime());
			values.addElement(ArchiveDatabase.COL_EVENT_ID, new Long(getId()));
			values.addElement(ArchiveDatabase.COL_EVENT_EVENT_PROCEDURE,
					getProcedure());
			values.addElement(ArchiveDatabase.COL_EVENT_EVENT_TYPE,
					getTypeCode());
			values.addElement(ArchiveDatabase.COL_EVENT_NOTE, getNote());
			values.addElement(ArchiveDatabase.COL_EVENT_OID, getOid());
			values.addElement(ArchiveDatabase.COL_EVENT_OUTCOME, getOutcome());
			if (getRelId() != null) {
				values
						.addElement(ArchiveDatabase.COL_EVENT_REL_OID,
								getRelId());
			}

			TransactionConnection tcnn = DBConnection.getSharedConnection();
			affected = tcnn.insert(table, values);
			break;
		case ArchiveManager.ACTION_MIGRATE:
			break;
		case ArchiveManager.ACTION_NORMALIZE:
			break;
		case ArchiveManager.ACTION_DISSEMINATE:
			break;
		default:
			// unknown archive action - fatal error
			Informer.getInstance().fail(this, methodName, "Illegal argument",
					"archiveAction: " + archiveAction,
					new FatalException("Not a valid archive action"));
		}
		
		return affected;
	}
	
    /**
	 * @return the event timestamp
	 */
    public String getDateTime() {
        return dateTime;
    } 

    /**
     * @return EVENT_ID: A key uniquely identifying the event
     */
    public long getId() {
        return id;
    }

    /**
     * @return event note 
     */
    public String getNote() {
        return note;
    } 

    /**
     * @return object ID
     */
    public String getOid() {
        return oid;
    } 

    /**
     * @return event outcome
     */
    public String getOutcome() {
        return outcome;
    } 

    /**
     * @return event procedure description
     */
    public String getProcedure() {
        return procedure;
    } 

    /**
     * @return related object ID
     */
    public String getRelId() {
        return relId;
    }

    /**
     * @return event type
     */
    public String getTypeCode() {
        return typeCode;
    } 

    /**
     * Sets the event timestamp.
     * Must be in the archive's date/time format.
     * 
     * @param _dateTime event timestamp
     * @see edu.fcla.daitss.util.DateTimeUtil
     * @throws FatalException
     */
    public void setDateTime(String _dateTime) throws FatalException {
        String methodName = "setDateTime(String)";

        if (_dateTime == null || 
                !DateTimeUtil.isInArchiveDateFormat(_dateTime)) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_dateTime: " + _dateTime,
                    new IllegalArgumentException(
                            "Date is not in archive format"));
        }
        dateTime = _dateTime;
    } 

    /**
     * Sets the event ID.
     * Note that the event ID is automatically assigned by
     * the database. This method is not for assigning a new event ID,
     * just storing an existing event ID in the java object.
     *  
     * @param _id A key uniquely identifying the event
     * @throws FatalException if the event ID is not a valid pattern
     */
    public void setId(long _id) throws FatalException{
        String methodName = "setId(long)";
        if (OIDServer.isValidEventId(_id)) {	            
            id = _id;
        }
        else {
            Informer.getInstance().fail(
	            this,
	            methodName,
	            "Illegal argument",
	            "_id: " + _id,
	            new IllegalArgumentException());
        }
    }

    /**
     * Sets the event note.
     * Can be an empty string.
     * The note length can not exceed <code>MAX_NOTE_LEN</code>
     * or a FatalException will be thrown.
     * 
     * @param _note event note.
     * @throws FatalException
     */
    public void setNote(String _note) throws FatalException {
        String methodName = "setNote(String)";
        if (_note == null) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "_note: " + _note,
                    new IllegalArgumentException("Note is null"));
        }
        if (_note.length() > MAX_NOTE_LEN) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "_note.length: " + _note.length(),
                    new IllegalArgumentException("Note length exceeds database limit"));
        }

        note = _note;
    } 

    /**
     * Sets the object's unique ID.
     * Must be either a valid BSID (bitstream ID), 
     * DFID (Datafile ID) or IEID (intellectual object ID).
     * 
     * @param _oid unique object ID
     * @throws FatalException
     */
    public void setOid(String _oid) throws FatalException {
        String methodName = "setOid(String)";
        // check input
        if (_oid == null || _oid.trim().length() == 0) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "_oid: " + _oid,
                    new IllegalArgumentException("oid is null or empty"));
        }
        if (!(	OIDServer.isValidDfid(_oid) || 
                OIDServer.isValidIeid(_oid) || 
                OIDServer.isValidBsid(_oid))) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_oid: " + _oid,
                    new FatalException("oid is not in expected format"));
        }

        oid = _oid;
    } 

    /**
     * Sets the event outcome.
     * Does not allow null or empty string outcomes.
     * Must use an outcome constant specified in this class
     * (with an OUTCOME_ prefix).
     * 
     * @param _outcome event outcome
     * @throws FatalException
     */
    public void setOutcome(String _outcome) throws FatalException {
        String methodName = "setOutcome(String)";
        // check input
        if (_outcome == null || _outcome.trim().equals("")) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "_outcome: " + _outcome,
                    new IllegalArgumentException("_outcome is null or empty"));
        }
        if (!isValidOutcome(_outcome)) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_outcome: " + _outcome,
                    new IllegalArgumentException(
                            "_outcome is not a valid event outcome"));
        }
        outcome = _outcome;
    } 

    /**
     * Sets the event procedure description.
     * Allows an empty string.
     * 
     * @param _procedure event procedure description
     * @throws FatalException
     */
    public void setProcedure(String _procedure) throws FatalException {
        String methodName = "setProcedure(String)";
        // check input
        if (_procedure == null) {
            Informer.getInstance()
                    .fail(
                            this,
                            methodName,
                            "Illegal argument",
                            "_procedure: " + _procedure,
                            new IllegalArgumentException(
                                    "_procedure is null"));
        }
        if (_procedure.length() > MAX_PROCEDURE_LEN) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_procedure length exceeds maximum allowable length for that field. length: "
                            + _procedure.length(),
                    new IllegalArgumentException("_procedure length exceeds "
                            + MAX_PROCEDURE_LEN));
        }

        procedure = _procedure;
    } 

    /**
     * Sets the related Object ID.
     * Allows a null ID.
     * 
     * @param _oid related ID
     * @throws FatalException
     */
    public void setRelId(String _oid) throws FatalException {
        String methodName = "setRelId(String)";
        // check input if it's not null
        if (_oid != null && !_oid.equals("") &&
                (!(		OIDServer.isValidDfid(_oid) || 
                        OIDServer.isValidIeid(_oid) || 
                        OIDServer.isValidBsid(_oid)))) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_oid: " + _oid,
                    new IllegalArgumentException(
                            "oid is not in expected format"));
        }

        relId = _oid;
    }

    /**
     * Sets the event type.
     * Must be a valid event type specified in this 
     * class as a constant (prefixed with EVENT_).
     * 
     * @param _typeCode event type
     * @throws FatalException
     */
    public void setTypeCode(String _typeCode) throws FatalException {
        String methodName = "setTypeCode(String)";
        
        if (_typeCode == null || _typeCode.trim().length() == 0) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "_typeCode: " + _typeCode,
                    new IllegalArgumentException("_typeCode is null or empty"));
        }
        if (!isValidTypeCode(_typeCode)) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_typeCode: " + _typeCode,
                    new IllegalArgumentException(
                            "_typeCode is not a valid event type"));
        }
        typeCode = _typeCode;
    } 

    /**
     * Method to return a string representation of the event.
     * 
     * @return the members of this class
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Event\n");
        sb.append("\tID: " + getId() + "\n");
        sb.append("\tOID: " + getOid() + "\n");
        sb.append("\ttypeCode: " + getTypeCode() + "\n");
        sb.append("\tdateTime: " + getDateTime() + "\n");
        sb.append("\tprocedure: " + getProcedure() + "\n");
        sb.append("\toutcome: " + getOutcome() + "\n");
        sb.append("\tnote: " + getNote() + "\n");
        sb.append("\trelId: " + getRelId() + "\n");

        return sb.toString();
    }
        
	/**
	 * Builds an XML representation of this Event.
	 * @return Document object
	 * @throws FatalException
	 */
	public Document toXML() throws FatalException {

	
        // Document.
        Document doc = XPaths.newDaitssDoc();

        // Root element.
        Element rootElement = doc.getDocumentElement();
        
        // Namespace.
        String namespace =  rootElement.getNamespaceURI();
        
        /* Event */
        Element eventElement = doc.createElementNS(namespace, "EVENT");
        rootElement.appendChild(eventElement);

        /* Id */
        Element idElement = doc.createElementNS(namespace, "ID");
        String idValue = Long.toString(this.id);
        Text idText = doc.createTextNode(idValue);
        idElement.appendChild(idText);
        eventElement.appendChild(idElement);

        /* Oid */
        Element oidElement = doc.createElementNS(namespace, "OID");
        String oidValue = (this.oid != null ? this.oid : "" );
        Text oidText = doc.createTextNode(oidValue);
        oidElement.appendChild(oidText);
        eventElement.appendChild(oidElement);

        /* Event Type */
        Element eventTypeElement = doc.createElementNS(namespace, "EVENT_TYPE");
        String eventTypeValue = (this.getTypeCode() != null ? this.getTypeCode() : "" );
        Text eventTypeText = doc.createTextNode(eventTypeValue);
        eventTypeElement.appendChild(eventTypeText);
        eventElement.appendChild(eventTypeElement);

        /* Date Time */
        Element dateTimeElement = doc.createElementNS(namespace, "DATE_TIME");
        String dateTimeValue = (this.getDateTime() != null ? this.getDateTime() : "" );
        Text dateTimeText = doc.createTextNode(dateTimeValue);
        dateTimeElement.appendChild(dateTimeText);
        eventElement.appendChild(dateTimeElement);

        /* Event Procedure */
        Element eventProcedureElement = doc.createElementNS(namespace, "EVENT_PROCEDURE");
        String eventProcedureValue = (this.getProcedure() != null ? this.getProcedure() : "" );
        Text eventProcedureText = doc.createTextNode(eventProcedureValue);
        eventProcedureElement.appendChild(eventProcedureText);
        eventElement.appendChild(eventProcedureElement);

        /* Outcome */
        Element outcomeElement = doc.createElementNS(namespace, "OUTCOME");
        String outcomeValue = (this.getOutcome() != null ? this.getOutcome() : "" );
        Text outcomeText = doc.createTextNode(outcomeValue);
        outcomeElement.appendChild(outcomeText);
        eventElement.appendChild(outcomeElement);

        /* Note */
        Element noteElement = doc.createElementNS(namespace, "NOTE");
        String noteValue = (this.getNote() != null ? this.getNote() : "" );
        Text noteText = doc.createTextNode(noteValue);
        noteElement.appendChild(noteText);
        eventElement.appendChild(noteElement);

        /* Rel Oid */
        if (this.getRelId() != null && !this.getRelId().trim().equals("")) {
        Element relOidElement = doc.createElementNS(namespace, "REL_OID");
            String relOidValue = this.getRelId();
        Text relOidText = doc.createTextNode(relOidValue);
        relOidElement.appendChild(relOidText);
        eventElement.appendChild(relOidElement);
        }
        return doc;
	}

} // end Event
