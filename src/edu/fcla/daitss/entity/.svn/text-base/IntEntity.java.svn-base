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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.entity.NoSuchIEIDException;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;

/**
 * A logical object which represents a book, map, issue, ETD, etc.
 *  
 */
public class IntEntity extends ArchiveEntity {

    /**
     * Maximum supported <code>extRec</code> value.
     */
    public static final int MAX_LEN_EXT_REC = 255;

    /**
     * Maximum supported <code>extRecType</code> value.
     */
    public static final int MAX_LEN_EXT_REC_TYPE = 255;

    /**
     * Maximum supported <code>issue</code> value.
     */
    public static final int MAX_LEN_ISSUE = 3;
   
    /**
     * Maximum supported <code>title</code> value.
     */
    public static final int MAX_LEN_TITLE = 65535;

    /**
     * Maximum supported <code>vol</code> value.
     */
    public static final int MAX_LEN_VOL = 4;
    
    // NOTE: The entity type must start with "ENTITY_"

    /**
     * Entity type: aerial
     */
    public static final String TYPE_AERIAL = "aerial";

    /**
     * Entity type: artifact
     */
    public static final String TYPE_ARTIFACT = "artifact";

    /**
     * Entity type: collection
     */
    public static final String TYPE_COLLECTION = "collection";

    /**
     * Entity type: collection
     */
    public static final String TYPE_ETD = "etd";    
    
    /**
     * Entity type: map
     */
    public static final String TYPE_MAP = "map";

    /**
     * Entity type: monograph
     */
    public static final String TYPE_MONOGRAPH = "monograph";

    /**
     * Entity type: multipart
     */
    public static final String TYPE_MULTIPART = "multipart";

    /**
     * Entity type: photo
     */
    public static final String TYPE_PHOTO = "photo";

    /**
     * Entity type: postcard
     */
    public static final String TYPE_POSTCARD = "postcard";

    /**
     * Entity type: serial
     */
    public static final String TYPE_SERIAL = "serial";

    /**
     * Entity type: oral
     */
    public static final String TYPE_ORAL = "oral";

    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = IntEntity.class.getName();
    
    /**
     * Main method. Used for testing.
     * 
     * @param args
     * @throws FatalException
     * @throws PackageException
     */
    public static void main(String[] args) throws PackageException, FatalException {
        try {
            IntEntity ie = IntEntity.buildFromDB("E20060607_AAAAAX");
            System.out.println(ie.toString());
        } catch (PackageException e) {
            e.printStackTrace();
        } catch (FatalException e) {
            e.printStackTrace();
        } catch (NoSuchIEIDException e) {
            e.printStackTrace();
        }
    }

    /**
     * The OID of the file that is the SIPDescriptor for this logical object
     * (for ingest).
     * 
     */
    private String descDfid = null;
    
    /**
     * Absolute path to this intellectual entity's descriptor.
     */
    private String descPath = null;

    /**
     * A unique, pre-existing identifier for the logical object.
     */
    private String entityId = null;

    /**
     * Entity type of this logical object (monograph, serial...).
     */
    private String entityType = null;

    /**
     * <p>
     * Identifier for an external metadata record.
     * </p>
     *  
     */
    private String extRec = null;

    /**
     * <p>
     * Code indicating the system containing the extRec.
     * </p>
     *  
     */
    private String extRecType = null;

    /**
     * an IntEntity's unique ID
     */
    //private String ieid = null;
    /**
     * <p>
     * For serials and multipart, the issue number (3 digits left-padded with
     * 0s).
     * </p>
     *  
     */
    private String issue = null;

    /**
     * Package name.
     */
    private String packageName = null;
    
    /**
     * <p>
     * The title of this logical object as defined in dc.title.
     * </p>
     *  
     */
    private String title = null;

    /**
     * <p>
     * For serial or multipart logical objects, the volume in which the issue
     * appears (4 digits left-padded with 0s).
     * </p>
     *  
     */
    private String vol = null;

    /**
     * Constructor for new IntEntity objects - Gets a new ieid and assign it to
     * the new object
     * 
     * @throws FatalException
     */
    public IntEntity() throws FatalException {
        // set the IEID
        getNewIeid();
    } 

    
    /**
     * Constructor for IntEntity objects that already have IEIDs assigned.
     * 
     * @param ieid IEID
     * @throws FatalException
     */
    public IntEntity(String ieid) throws FatalException {
        // set the IEID
        this.setOid(ieid);
    } 
    
    /**
     * Constructor for new IntEntity objects - Gets a new ieid and assigns it to
     * the new object.
     * 
     * @param packageName package name
     * @param extRec external record
     * @param extRecType external record type
     * @param entityId entity ID
     * @param vol volume
     * @param issue issue
     * @param title title
     * @throws FatalException
     * @throws PackageException
     */
    public IntEntity(String packageName, String extRec, 
            String extRecType, String entityId, 
            String vol, String issue, String title) 
    		throws PackageException, FatalException {
        
        String methodName = 
            "IntEntity(String, String, String, String, String, String, String)";
        if (packageName == null) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "packageName: " + packageName,
                    new IllegalArgumentException());
        }
        else {
            setPackageName(packageName);
        }
        
        // set the IEID
        getNewIeid();        
        
        if (extRec != null) {
            setExtRec(extRec);
        }
        if (extRecType != null) {
            setExtRecType(extRecType);
        }
        if (entityId != null) {
            setEntityId(entityId);
        }
        if (vol != null) {
            setVol(vol);
        }
        if (issue != null) {
            setIssue(issue);
        }
        if (title != null) {
            setTitle(title);
        }                  
    } // end IntEntity

    /**
     * Constructor for new IntEntity objects. IEID is parameterized in order to 
     * allow re-creation of IntEntity objects stored in the database.
     * 
     * @param ieid the ieid to assign to this IntEntity
     * @param packageName package name
     * @param extRec external record
     * @param extRecType external record type
     * @param entityId entity ID
     * @param vol volume
     * @param issue issue
     * @param title title
     * @throws FatalException
     * @throws PackageException
     */
    public IntEntity(String ieid, String packageName, String extRec, 
            String extRecType, String entityId, 
            String vol, String issue, String title) 
            throws PackageException, FatalException {
        
        String methodName = 
            "IntEntity(String, String, String, String, String, String, String)";
        if (packageName == null) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "packageName: " + packageName,
                    new IllegalArgumentException());
        }
        else {
            setPackageName(packageName);
        }
        
        // set the IEID
        if (ieid == null) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "ieid: " + ieid,
                    new IllegalArgumentException());            
        } else {
            setOid(ieid);
        }     
        
        if (extRec != null) {
            setExtRec(extRec);
        }
        if (extRecType != null) {
            setExtRecType(extRecType);
        }
        if (entityId != null) {
            setEntityId(entityId);
        }
        if (vol != null) {
            setVol(vol);
        }
        if (issue != null) {
            setIssue(issue);
        }
        if (title != null) {
            setTitle(title);
        }                  
    } // end IntEntity
    
    /**
     * Queries the database and (re)creates an IntEntity object based on an existing 
     * ieid. If the ieid does not exist in the database, a NoSuchIEIDException is
     * thrown. 
     *  
     * @param ieid
     * @return An IntEntity with members populated based with values from db
     * @throws PackageException
     * @throws FatalException
     * @throws NoSuchIEIDException
     */
    public static IntEntity buildFromDB(String ieid) 
            throws PackageException, FatalException, NoSuchIEIDException {
        
        String methodName = "buildFromDB(String)";
        IntEntity ie = null;        
        DBConnection conn = DBConnection.getConnection();
        ResultSet rs = conn.retrieve("*", ArchiveDatabase.TABLE_INT_ENTITY, 
                ArchiveDatabase.COL_INT_ENTITY_IEID + " = " + SqlQuote.escapeString(ieid));       
         
        try {
            if (rs.first()) {
                ie = new IntEntity(rs.getString(ArchiveDatabase.COL_INT_ENTITY_IEID),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_EXT_REC),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_EXT_REC_TYPE),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_ENTITY_ID),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_VOL),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_ISSUE),
                        rs.getString(ArchiveDatabase.COL_INT_ENTITY_TITLE));
            }
            else {
                throw new NoSuchIEIDException("The id \'" + ieid + "\' does not exist in the table \'"  + ArchiveDatabase.TABLE_INT_ENTITY + "\'");
        
            }
        
            // get the ap code from the admin table
            rs.close();
            rs = conn.retrieve("*", ArchiveDatabase.TABLE_ADMIN, 
                    ArchiveDatabase.COL_ADMIN_OID + " = " + SqlQuote.escapeString(ieid));
            int apCode = 0;
            if (rs.first()) {
                apCode = rs.getInt(ArchiveDatabase.COL_ADMIN_ACCOUNT_PROJECT);
                ie.setAp(apCode);
            }
            else {
                throw new NoSuchIEIDException("The id \'" + ieid + "\' does not exist in the table \'"  + ArchiveDatabase.TABLE_ADMIN + "\'");        
            }
            
            // set the individual account and project codes
            rs.close();
            rs = conn.retrieve("*", ArchiveDatabase.TABLE_ACCOUNT_PROJECT, 
                    ArchiveDatabase.COL_ACCOUNT_PROJECT_ID + " = " + SqlQuote.escapeInt(apCode));
            if (rs.first()) {
                ie.setAcccount(rs.getString(ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT));
                ie.setProject(rs.getString(ArchiveDatabase.COL_ACCOUNT_PROJECT_PROJECT));                  
            }
            else {
                throw new NoSuchIEIDException("The AP code \'" + apCode + "\' does not exist in the table \'"  + ArchiveDatabase.TABLE_ACCOUNT_PROJECT + "\'");        
            }
            
        } catch (SQLException e) {
            Informer.getInstance().error(CLASSNAME,
                    methodName,
                    e.getMessage(),
                    "Error accessing result set",
                    e);
        } finally {
            try {
            	rs.close();
                conn.close();
            } catch (SQLException e) {
            }
        }
                
        return ie;
    }
    
    
    /**
     * Updates database records depending on the archive action
     * being performed (ingest, etc.).
     * 
     * @param archiveAction general archive function
     * @return number of affected database rows
     * @throws FatalException
     */
    public int dbUpdate(byte archiveAction) throws FatalException {
        String methodName = "dbUpdate(String)";

        int counterAll = 0; // total number of rows affected
		int counterIE = 0; // total number of Int. Entity rows affected
		int counterGF = 0; // number of Int. Entity Global File rows affected

        super.dbUpdate(archiveAction);

        switch (archiveAction) {
        case (ArchiveManager.ACTION_INGEST):
        
            TransactionConnection tcon = DBConnection.getSharedConnection();
        	DBConnection.NameValueVector values = 
        	    new DBConnection.NameValueVector();
        	
        	// update the int. entity table
        	String table = ArchiveDatabase.TABLE_INT_ENTITY;

        	values.addElement(ArchiveDatabase.COL_INT_ENTITY_IEID, 
        	        this.getOid());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_EXT_REC, 
                    this.getExtRec());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_EXT_REC_TYPE,
                    this.getExtRecType());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_ENTITY_ID,
                    this.getEntityId());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME,
                    this.getPackageName());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_VOL,
                    this.getVol());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_ISSUE,
                    this.getIssue());
            values.addElement(ArchiveDatabase.COL_INT_ENTITY_TITLE,
                    this.getTitle());
            
			// insert the records and get back the number of rows inserted
			counterIE = tcon.insert(table, values);
            
            counterAll = counterGF + counterIE;
            
            break;

        case (ArchiveManager.ACTION_DISSEMINATE):
            // fall through
        case (ArchiveManager.ACTION_NORMALIZE):
            // fall through
        case (ArchiveManager.ACTION_MIGRATE):
            Informer.getInstance().fail(this, methodName,
                    "Unimplemented archiveAction",
                    "archiveAction: " + archiveAction,
                    new FatalException("Unimplemented archiveAction"));
            break;
        default:
            Informer.getInstance().fail(this, methodName, "Invalid argument",
                    "archiveAction: " + archiveAction,
                    new FatalException("Not a valid archive action"));
        }
        return counterAll;
    } // end dbUpdate

    /**
     * @return the dfid of the descriptor associated with this IntEntity
     */
    public String getDescDfid() {
        return descDfid;
    } // end getDescDFID

    /**
     * @return Returns the descPath.
     */
    public String getDescPath() {
        return descPath;
    }

    /**
     * A unique, pre-existing identifier for the logical object
     * 
     * @return entity ID
     */
    public String getEntityId() {
        return entityId;
    } 

    /**
     * The entity type of this logical object (monograph, serial...)
     * 
     * @return entity type
     */
    public String getEntityType() {
        return entityType;
    } 

    /**
     * Identifier for an external metadata record.
     * 
     * @return external record
     */
    public String getExtRec() {
        return extRec;
    } 

    /**
     * Code indicating the system containing the extRec.
     * 
     * @return external record type
     */
    public String getExtRecType() {
        return extRecType;
    } 

    /**
     * For serials and multipart, the issue number (3 digits left-padded with
     * 0s)
     * 
     * @return issue
     */
    public String getIssue() {
        return issue;
    } 

    /**
     * Gets a new IEID from the OIDServer and sets its IEID to this.
     * 
     * @throws FatalException
     */
    private void getNewIeid() throws FatalException {
        setOid(OIDServer.getNewIeid());
    }

    /**
     * 
     * @return package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * The title of this logical object as defined in dc.title
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    } 

    /**
     * For serial or multipart logical objects, the volume in which the issue
     * appears (4 digits left-padded with 0s)
     * 
     * @return the volume
     */
    public String getVol() {
        return vol;
    } 

    /**
     * Determines if an entity type is recognized as a valid entity type.
     * 
     * @param entityType
     * @return whether the entity type is valid
     * @throws FatalException
     */
    private boolean isValidEntityType(String entityType) 
    	throws FatalException {
        String methodName = "isValidEntityType(String)";

        boolean isValid = false, foundIt = false;

        if (entityType != null && !entityType.equals("")) {
            
            // allow the argument to be upper case
            entityType = entityType.toLowerCase();
            
            // dynamically make a list of all the fields listed
            // in this class
            Field[] fields = IntEntity.class.getFields();            
            
            int i = 0;
            while (!foundIt && i < fields.length) {
                try {
                    // only want to consider the members that start with
                    // "TYPE_"
                            
                    if (fields[i].getName().startsWith("TYPE_")
                            && ((String) fields[i].get(fields[i]))
                                    .equals(entityType)) {
                        foundIt = true;
                        isValid = true;
                    }
                    i++;
                } catch (IllegalAccessException e) {
                    Informer.getInstance().fail(this, methodName,
                            "Illegal access", "field",
                            new FatalException(e.getMessage()));
                }
            }

        }

        return isValid;
    }
    
    /**
     * @param descPath The descPath to set.
     */
    public void setDescPath(String descPath) {
        this.descPath = descPath;
    }

    /**
     * A unique, pre-existing identifier for the logical object
     * 
     * @param entityID entity ID
     * @throws FatalException
     * @throws PackageException
     */
    public void setEntityId(String entityID) throws PackageException, FatalException {
        String methodName = "setEntityID(String)";

        if (entityID == null) {
            Informer.getInstance().fail(this, methodName, 
                    "IllegalArgument",
                    "entityID: " + entityID, 
                    new IllegalArgumentException("Entity ID can't be null"));
        }
        else if (!OIDServer.isValidEntityid(entityID)){
			Informer.getInstance().error(
					this,
					methodName,
					"Illegal argument",
					"entityID: " + entityID,
					new IllegalArgumentException("EntityID is not valid"));
		}
        this.entityId = entityID;
    } 

    /**
     * The entity type of this logical object (monograph, serial...).
     * 
     * @param entityType entity type
     * @throws FatalException
     */
    public void setEntityType(String entityType) throws FatalException {
        String methodName = "setEntityType(String)";

        if (entityType == null || !this.isValidEntityType(entityType)) {
            Informer.getInstance()
                    .fail(this, methodName, "Illegal argument",
                            "entityType: " + entityType,
                            new IllegalArgumentException());
        }
        this.entityType = entityType;
    } 

    /**
     * Identifier for an external metadata record.
     * 
     * @param extRec external record ID
     * @throws FatalException
     */
    public void setExtRec(String extRec) throws FatalException {
        String methodName = "setExtRec(String)";
        if (extRec == null || extRec.length() > MAX_LEN_EXT_REC) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "extRec: " + extRec, new IllegalArgumentException());
        }
        this.extRec = extRec;
    } 

    /**
     * Code indicating the system containing the extRec.
     * 
     * @param extRecType external record type
     * @throws FatalException
     */
    public void setExtRecType(String extRecType) throws FatalException {
        String methodName = "setExtRecType(String)";
        if (extRecType == null || extRecType.length() > MAX_LEN_EXT_REC_TYPE) {
            Informer.getInstance()
                    .fail(this, methodName, "Illegal argument",
                            "extRecType: " + extRecType,
                            new IllegalArgumentException());
        }
        this.extRecType = extRecType;
    } 

    /**
     * For serials and multipart, the issue number (3 digits left-padded with
     * 0s)
     * 
     * @param  issue issue
     * @throws FatalException
     */
    public void setIssue(String issue) throws FatalException {
        String methodName = "setIssue(String)";
        // not all intellectual entities have issues, removing pattern match
        //if (issue == null || !issue.matches("\\d{1,"+MAX_LEN_ISSUE+"}")) {

        if (issue == null) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "issue: " + issue, new IllegalArgumentException());
        }
        String value = issue;
        // pad the issue string if necessary
        for (int i = 0; i < MAX_LEN_ISSUE - issue.length(); i++) {
            value = "0" + value;
        }
        this.issue = value;
    } 

    /**
     * Sets the package name.
     * 
     * @param packageName package name
     * @throws FatalException
     */
    public void setPackageName(String packageName) 
    	throws FatalException {
        String methodName = "setPackageName(String)";
        
        
        if (packageName == null) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "packageName: " + packageName,
                    new IllegalArgumentException());
        }       
        
        if (packageName.length() == 0) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "package name: " + packageName, 
                    new FatalException("Package name is empty"));
        }        
        if (packageName.length() > InformationPackage.MAX_LEN_PACKAGE_NAME) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "package name: " + packageName, 
                    new FatalException("Package name length exceeds max length"));
        }
        
        this.packageName = packageName;
    }

    /**
     * Sets the title of this logical object as defined in dc.title
     * 
     * @param title title
     * @throws FatalException
     */
    public void setTitle(String title) throws FatalException {
        String methodName = "setTitle(String)";
        if (title != null && title.length() > MAX_LEN_TITLE) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "title: " + title, 
                    new FatalException("Title length exceeds max length"));
        }
        this.title = title;
    } 

    /**
     * For serial or multipart logical objects, the volume in which the issue
     * appears (4 digits left-padded with 0s)
     * 
     * @param vol volume
     * @throws FatalException
     */
    public void setVol(String vol) throws FatalException {
        String methodName = "setVol(String)";
        // All intellectual entities do not have volumes, getting rid of pattern
        // match
        //if (vol == null || !vol.matches("\\d{1,"+MAX_LEN_VOL+"}")) {

        if (vol == null) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "vol: " + vol, new IllegalArgumentException());
        }
        String value = vol;
        // pad the issue string if necessary
        for (int i = 0; i < MAX_LEN_VOL - vol.length(); i++) {
            value = "0" + value;
        }
        this.vol = value;
    }

    /**
     * Returns a String representing the IntEntity's state
     * 
     * @return String the state of the IntEntity
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("IntEntity:");
        sb.append("\n\tieid: " + getOid());
        sb.append("\n\tpackageName: " + getPackageName());
        sb.append("\n\textRec: " + getExtRec());
        sb.append("\n\textRecType: " + getExtRecType());
        sb.append("\n\tentityID: " + getEntityId());
        sb.append("\n\tvol: " + getVol());
        sb.append("\n\tissue: " + getIssue());
        sb.append("\n\ttitle: " + getTitle());               
        return sb.toString();
    }
    
    /**
     * Returns the members in the form of XML.
     * 
     * @return the members in the form of XML
     * @throws FatalException
     */
    public Document toXML() throws FatalException {
        
        /* Document */
        Document doc = super.toXML();

        /* Root element */
        Element rootElement = doc.getDocumentElement();

        /* Namespace */
        String namespace = rootElement.getNamespaceURI();

        /* INT_ENTITY */
        Element intEntityElement = doc.createElementNS(namespace, "INT_ENTITY");
        rootElement.appendChild(intEntityElement);

        /* IEID */
        Element ieidElement = doc.createElementNS(namespace, "IEID");
        String ieidValue = (this.getOid() != null ? this.getOid() : "");
        Text ieidText = doc.createTextNode(ieidValue);
        ieidElement.appendChild(ieidText);
        intEntityElement.appendChild(ieidElement);

        /* PACKAGE_NAME */
        if (this.getPackageName() != null && !this.getPackageName().trim().equals("")) {
	        Element packageNameElement = doc.createElementNS(namespace,"PACKAGE_NAME");
	        String packageNameValue = this.getPackageName();
	        Text packageNameText = doc.createTextNode(packageNameValue);
	        packageNameElement.appendChild(packageNameText);
	        intEntityElement.appendChild(packageNameElement);
        }
        
        /* EXT_REC */
        if (this.getExtRec() != null && !this.getExtRec().trim().equals("")) {
            Element extRecElement = doc.createElementNS(namespace, "EXT_REC");
            String extRecValue = this.getExtRec();
            Text extRecText = doc.createTextNode(extRecValue);
            extRecElement.appendChild(extRecText);
            intEntityElement.appendChild(extRecElement);
        }

        /* EXT_REC_TYPE */
        if (this.getExtRecType() != null && !this.getExtRecType().trim().equals("")) {
            Element extRecTypeElement = doc.createElementNS(namespace, "EXT_REC_TYPE");
            String extRecTypeValue = this.getExtRecType();
            Text extRecTypeText = doc.createTextNode(extRecTypeValue);
            extRecTypeElement.appendChild(extRecTypeText);
            intEntityElement.appendChild(extRecTypeElement);
        }

        /* ENTITY_ID */
        if (this.getEntityId() != null && !this.getEntityId().trim().equals("")) {
            Element entityIdElement = doc.createElementNS(namespace, "ENTITY_ID");
            String entityIdValue = this.getEntityId();
            Text entityIdText = doc.createTextNode(entityIdValue);
            entityIdElement.appendChild(entityIdText);
            intEntityElement.appendChild(entityIdElement);
        }

        /* VOL */
        if (this.getVol() != null && !this.getVol().trim().equals("")) {
            Element volElement = doc.createElementNS(namespace, "VOL");
            String volValue = this.getVol();
            Text volText = doc.createTextNode(volValue);
            volElement.appendChild(volText);
            intEntityElement.appendChild(volElement);
        }

        /* ISSUE */
        if (this.getIssue() != null && !this.getIssue().trim().equals("")) {
            Element issueElement = doc.createElementNS(namespace, "ISSUE");
            String issueValue = (this.getIssue() != null ? this.getIssue() : "");
            Text issueText = doc.createTextNode(issueValue);
            issueElement.appendChild(issueText);
            intEntityElement.appendChild(issueElement);
        }

        /* TITLE */
        if (this.getTitle() != null && !this.getTitle().trim().equals("")) {
            Element titleElement = doc.createElementNS(namespace, "TITLE");
            String titleValue = (this.getTitle() != null ? this.getTitle() : "");
            Text titleText = doc.createTextNode(titleValue);
            titleElement.appendChild(titleText);
            intEntityElement.appendChild(titleElement);
        }

		/* AgreementInfo */		
		Element aInfo = (Element) rootElement.appendChild(doc.createElementNS(namespace, "AGREEMENT_INFO"));
		aInfo.setAttribute("ACCOUNT", this.getAcccount());
		if (this.getSubAccountId() != null) { 
			aInfo.setAttribute("SUB_ACCOUNT", this.getSubAccountId().toString()); 
		}
		aInfo.setAttribute("PROJECT", this.getProject());

        return doc;
    }
} 
