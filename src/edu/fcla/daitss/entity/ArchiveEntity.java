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
/*
 * Created on Mar 18, 2004
 */
package edu.fcla.daitss.entity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.MySqlConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * ArchiveEntity
 * 
 * @author Chris Vicary, FCLA
 *  
 */
public abstract class ArchiveEntity {

    // origins of the metadata - if a new one is added you have to
    // add it to the isValidMetaOrigin method

    /**
     * Metadata came from the archive database
     */
    public static final String META_ORIGIN_DATABASE = "DATABASE";

    /**
     * Metadata came from the SIP descriptor
     */
    public static final String META_ORIGIN_SIP_DESC = "SIP_DESC";

    /**
     * Unknown source of the metadata
     */
    public static final String META_ORIGIN_UNKNOWN = "UNKNOWN";

    /**
     * Specifies whether or not a metadata origon code is a valid one in the
     * archive.
     * 
     * @param origin
     *            metadata origin
     * @return whether or not the metadata origon code is valid
     */
    public static boolean isValidMetaOrigin(String origin) {
        boolean isValid = false;
        if (origin != null
                && (origin.equals(META_ORIGIN_DATABASE) || origin
                        .equals(META_ORIGIN_SIP_DESC))) {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Test driver.
     * 
     * Even though this method can't throw a PackageException, it's claimed to
     * make the method signature compatible with its subclasses.
     * 
     * @param args
     *            not used
     * @throws PackageException
     * @throws FatalException
     */
    public static void main(String[] args) throws PackageException,
            FatalException {
        System.out.println(OIDServer.getNewDfid());
        System.out.println(OIDServer.getNewDfid());
        System.out.println(OIDServer.getNewDfid());
        System.out.println(OIDServer.getNewDfid());
    }

    /**
     * Metadata from the submitted descriptor about this archive entity.
     */
    private Document descriptorMetadata = null;

    /**
     * The origin of the metadata used to create the archive entity. Ex: for
     * DataFiles this is used to know the metadata source for the lite DataFile
     * (if one exists) contained by this DataFile. One use for this origin is to
     * resolve conflicts when differences are found between the parsed DataFile
     * and the DataFile's metadata.
     */
    private String metaOrigin = META_ORIGIN_UNKNOWN;

    /**
     * The object id for this ArchiveEntity. If the entity is a DataFile, then
     * oid is a DFID. If the entity is an IntEntity, then the oid is a IEID.
     */
    private String oid = null;

    /**
     * A code representing the combination of account and project of this
     * ContentObject
     */
    protected long ap = Agreements.DEFAULT_AP_ID;
    
    /**
     * The code representing the sub account
     */
    protected Long subAccountId = null;

	/**
	 * The acccount of the archive entity
	 */
	private String acccount = null;

	/**
	 * The project of the archive entity. Multiple projects may exist, as a
	 * matter of convention, daitss will use the first project in the list.
	 */
	private String project = null;

	private String ingestTime = null;

    /**
     * Checks that an Object argument is not equal to null. If the argument is
     * equal to null the application's fail routine is invoked.
     * 
     * @param method
     *            a method name with its parameters
     * @param argName
     *            the name of the parameter variable
     * @param arg
     *            the Object that is the value of the parameter variable
     * @param caller
     *            the class calling this method
     * @throws FatalException
     */
    protected void checkForNullObjectArg(String method, String argName,
            Object arg, String caller) throws FatalException {
        if (arg == null) {
            Informer
                    .getInstance()
                    .fail(
                            this,
                            "checkForNullObjectArg(String, String, Object, String)",
                            "Null argument",
                            "Argument " + argName + " (given to " + caller
                                    + "." + method + ")",
                            new FatalException("Null argument"));
        }
    }

	/**
	 * Insert this archive entity  into the database.
	 * @return number of records inserted
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException {
        MySqlConnection.NameValueVector values = new MySqlConnection.NameValueVector();

        values.addElement(ArchiveDatabase.COL_ADMIN_OID, this.getOid());
        values.addElement(ArchiveDatabase.COL_ADMIN_INGEST_TIME, getIngestTime());
        values.addElement(ArchiveDatabase.COL_ADMIN_ACCOUNT_PROJECT, new Long(this.getAp()));
        values.addElement(ArchiveDatabase.COL_ADMIN_SUB_ACCOUNT, this.getSubAccountId());

        //values.addElement(ArchiveDatabase.COL_ADMIN_SUB_ACCOUNT);
        TransactionConnection tcon = DBConnection.getSharedConnection();
        return tcon.insert(ArchiveDatabase.TABLE_ADMIN, values);
	}
    /**
     * Used to add database statements to an existing open database transaction.
     * 
     * @param archiveAction
     *            general function DAITSS is performing
     * @return number of affected rows
     * @throws FatalException
     */
    public int dbUpdate(byte archiveAction) throws FatalException {
    	    	
        String methodName = "dbUpdate(archiveAction)";

        int counter = 0;
        if (!ArchiveManager.isValidAction(archiveAction))
            Informer.getInstance().fail(this, methodName,
                    "Invalid archiveAction", "archiveAction: " + archiveAction,
                    new FatalException("Invalid archiveAction"));

        switch (archiveAction) {
        case ArchiveManager.ACTION_INGEST:
        		counter = dbInsert();
            break;
        default:
            Informer.getInstance().fail(this, methodName,
                    "Unimplemented archive action",
                    "archiveAction: " + archiveAction,
                    new FatalException("Unimplemented archive action"));
            break;
        }
        return counter;
    }

    /**
     * @return Document object
     */
    public Document getDescriptorMetadata() {
        return descriptorMetadata;
    }

    /**
     * @return this object's metadata origin
     */
    public String getMetaOrigin() {
        return metaOrigin;
    }

    /**
     * 
     * @return this object's unique ID
     */
    public String getOid() {
        return oid;
    }

    /**
     * @return the AP (id for the combination of account and project)
     *         of this object
     */
    public long getAp() {
        return this.ap;
    }

    /**
     * @param document
     *            an XML DOM tree representing the descriptor metadata for this
     *            archive entity
     * @throws FatalException
     */
    public void setDescriptorMetadata(Document document) throws FatalException {
        //	check that the argument is not null
        this.checkForNullObjectArg("setDescriptorMetadata(Document document)",
                "document", document, this.getClass().getName());

        this.descriptorMetadata = document;
    }

    /**
     * @param string
     *            metadata origin code
     */
    public void setMetaOrigin(String string) {
        metaOrigin = string;
    }

    /**
     * Sets the Object's unique ID.
     * 
     * @param _oid
     *            unique object ID
     * @throws FatalException
     */
    public void setOid(String _oid) throws FatalException {
        String methodName = "setOid(String)";
        if (_oid == null) {
            Informer.getInstance().fail(this, methodName, "Illegal argument",
                    "_oid: " + _oid,
                    new IllegalArgumentException("_oid argument is null"));
        }
        if (!(OIDServer.isValidDfid(_oid) || OIDServer.isValidIeid(_oid))) {
            Informer.getInstance().fail(
                    this,
                    methodName,
                    "Illegal argument",
                    "_oid: " + _oid,
                    new IllegalArgumentException(
                            "_oid argument is not a valid object id"));
        }
        this.oid = _oid;
    }

    /**
     * Sets the database code representing the unique combination of account
     * and primary project that this object belongs to.
     * 
     * @param _ap
     *            the Account/Project id
     * @throws FatalException
     */
    public void setAp(int _ap) throws FatalException {
        if (_ap < Agreements.MIN_AP_ID || _ap > Agreements.MAX_AP_ID) {
            Informer.getInstance().fail(this, "setAp(int)",
                    "Illegal Argument", "_ap: " + _ap,
                    new FatalException("Not a valid AP id"));
        }
        this.ap = _ap;
    }

    /**
     * Builds an XML representation of this archive entity.
     * @return Document object
     * 
     * @throws FatalException
     */
    public Document toXML() throws FatalException {
        /*
         * Since this is base class we do not have a recursive step
         * (super.toXML()), instead a base step is invoked: create a new
         * <daitss/> element.
         *  
         */

        // Document.
        Document doc = XPaths.newDaitssDoc();
        
        // Root element.
        Element rootElement = doc.getDocumentElement();
        
        // Namespace.
        String namespace = rootElement.getNamespaceURI();


        // ADMIN.
        Element adminElement = doc.createElementNS(namespace, "ADMIN");
        rootElement.appendChild(adminElement);
        
        // OID.
        Element oidElement = doc.createElementNS(namespace, "OID");
        String oidValue = (this.getOid() != null ? this.getOid() : "");
        Text oidText = doc.createTextNode(oidValue);
        oidElement.appendChild(oidText);
        adminElement.appendChild(oidElement);

        // INGEST_TIME.
        Element ingestTimeElement = doc.createElementNS(namespace, "INGEST_TIME");
        String ingestTimeValue = getIngestTime();
        Text ingestTimeText = doc.createTextNode(ingestTimeValue);
        ingestTimeElement.appendChild(ingestTimeText);
        adminElement.appendChild(ingestTimeElement);

        // ACCOUNT_PROJECT.
        Element apElement = doc.createElementNS(namespace, "ACCOUNT_PROJECT");
        String apValue = Long.toString(this.getAp());
        Text apText = doc.createTextNode(apValue);
        apElement.appendChild(apText);
        adminElement.appendChild(apElement);

		// SUB_ACCOUNT.
		if (this.getSubAccountId() != null) {
	        Element saElement = doc.createElementNS(namespace, "SUB_ACCOUNT");
			String saValue = this.getSubAccountId().toString();
	        Text saText = doc.createTextNode(saValue);
	        saElement.appendChild(saText);
	        adminElement.appendChild(saElement);
		}
		
        return doc;
    }
	
    /**
     * @return Returns the subAccountId.
     */
    public Long getSubAccountId() {
        return subAccountId;
    }
    /**
     * @param subAccount The subAccountId to set.
     * 
     */
    public void setSubAccountId(Long subAccount) {
        this.subAccountId = subAccount;
    }

	/**
	 * @return acccount code
	 */
	public String getAcccount() {
	    return acccount;
	}

	/**
	 * @return project code
	 */
	public String getProject() {
	    return project;
	}

	/**
	 * @param _owner acccount code
	 */
	public void setAcccount(String _owner) {
	    acccount = _owner;
	}

	/**
	 * @param _project project code
	 */
	public void setProject(String _project) {
	    project = _project;
	}
	
	private String getIngestTime() {
		if (ingestTime == null) {
			ingestTime = DateTimeUtil.now();
		}
		return ingestTime;
	}
}