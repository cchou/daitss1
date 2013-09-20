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
 * Created on Jul 6, 2004
 * Bitstream representing a METS Descriptor file
 */
package edu.fcla.daitss.format.markup;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.ArchiveMessageDigest;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.Informer;
import gov.loc.mets.AmdSecType;
import gov.loc.mets.MdSecType;
import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsType;

/**
 * @author franco
 *  
 */
public class METSDocument extends XMLDocument {

    /**
     * checksum attribute constant
     */
    public static final String METS_CHECKSUM_ATTR = "CHECKSUM";
    /**
     * checksum type attribute constant
     */
    public static final String METS_CHECKSUMTYPE_ATTR = "CHECKSUMTYPE";
    
    private MetsDocument documentBean;

    private XmlOptions options;

    /**
     * @param df
     *            data file to make a mets out of
     * @throws FatalException
     */
    public METSDocument(DataFile df) throws FatalException {
        super(df);

        // Set options for xmlbeans
        options = new XmlOptions();
        options.setSavePrettyPrint();
        //options.setSaveAggresiveNamespaces();
 
    }

    /**
     * @return Element object
     */
    public Element getDocumentElement() {
        Document d = (Document) documentBean.newDomNode();
        Element e = d.getDocumentElement();
        return e;
    }

    /**
     * @param e
     * @throws FatalException
     */
    public void setDocumentElement(Element e) throws FatalException {
        Document d = e.getOwnerDocument();
        try {
            documentBean = MetsDocument.Factory.parse(d);
        } catch (XmlException e1) {
            throw new FatalException(e1);
        }
    }

    /**
     * @return total number of 'amd sections'
     */
    public int amdCount() {
        return documentBean.getMets().sizeOfAmdSecArray();
    }

    /**
     * @return total number of 'behavior sections'
     */
    public int behaviorSecCount() {
        return documentBean.getMets().sizeOfBehaviorSecArray();
    }

    /**
     * @return total number of 'dmd sections'
     */
    public int dmdCount() {
        return documentBean.getMets().sizeOfDmdSecArray();
    }

    /**
     * @return total number of files in the mets descriptor
     */
    public int fileCount() {
        XmlObject[] files = documentBean.getMets().selectPath("//METS:file");
        return files.length;
    }

    /**
     * Loads a METS Document's root bitsream 
     * @param f
     * @throws PackageException
     * @throws FatalException
     */
    public void parseFile(File f) throws PackageException, FatalException {
        String methodName = "parseFile()";
        try {
            XmlOptions ops = new XmlOptions();
            ops.setLoadStripWhitespace();
            ops.setLoadStripComments();
            ops.setLoadStripProcinsts();
            documentBean = MetsDocument.Factory.parse(f, ops);
        } catch (XmlException e) {
            Informer.getInstance().error(
					this,
                    methodName,
                    "could not parse: " + f.getPath(),
                    "xml validation",
                    e);
        } catch (IOException e) {
            Informer.getInstance().fail(this,
                    methodName,
                    "",
                    "io error",
                    e);
        }
    }
    
    
    /**
     * Returns INT_ENTITY/IEIDs from the daitss namespace within the file.
     * 
     * @return Vector object
     * @throws FatalException
     */
    public Vector <String> getIntEntityIDs() throws FatalException {
        Vector <String> ieids = new Vector<String>();        
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Daitss.INT_ENTITY_ID;        
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            ieids.add(node.getFirstChild().getNodeValue());           
        }                
        return ieids;
    }
    
    /**
     * @return xml bean MetsDocument
     */
    public MetsDocument getDocument() {
        return documentBean;
    }

    /**
     * @return all the file IDs in the 'file section'
     * @throws FatalException
     */
    public String[] getFileIds() throws FatalException {
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Mets.FILE + "/@ID";        
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String[] ids = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            ids[i] = node.getNodeValue();
        }
        return ids;
    }

    /**
     * @param fileID
     * @throws FatalException
     * @return String object
     */
    public String getFilePathById(String fileID) throws FatalException {
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Mets.FILE + "[@ID='" + fileID + "']/mets:FLocat/@xlink:href";     
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String id = (node != null) ? node.getNodeValue() : null;
        return id;
    }

    /**
     * @throws FatalException
     * @return an array of file paths in the mets document
     */
    public String[] getFilePaths() throws FatalException {
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Mets.FLOCAT_HREF;        
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String[] paths = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            paths[i] = node.getNodeValue();
        }
        return paths;
    }

    /**
     * @throws FatalException
     * @return the names of the issues
     */
    public String[] getIssues() throws FatalException {
        Node contextNode = getDocumentElement();
        String xpath = XPaths.Mets.DIV_ISSUE + "/@ORDERLABEL";
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String[] issues = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            issues[i] = node.getNodeValue();
        }
        return issues;
    }

    /**
     * @throws FatalException
     * @return an array of the dmdSec//MdRef IDs
     */
    public String[] getMdRefIds() throws FatalException {
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Mets.DMD_MDREFS + "/@ID";
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);        
        String[] refIds = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            refIds[i] = node.getNodeValue();
        }
        return refIds;
    }

    /**
     * @param mdRefID
     *            the ID of a meta data reference
     * @throws FatalException
     * @return the type of the meta data reference
     */
    public String getMdRefMdType(String mdRefID) throws FatalException {
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Mets.DMD_MDREFS + "[@ID='" + mdRefID + "']/@MDTYPE";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String type = (node != null) ? node.getNodeValue() : null;
        return type;
    }

    /**
     * Returns all message digests included in an XMLDocument.
     * 
     * @return Vector object
     * @throws FatalException 
     */
    public Vector<ArchiveMessageDigest> getMessageDigests() 
            throws FatalException{
        
        Vector<ArchiveMessageDigest> digests = new Vector<ArchiveMessageDigest>();
        
        // message digest values and types are stored in the file
        // element. First find files, then look for checksum attributes.
        String xpath = XPaths.Mets.FILE;
        NodeList nodes = XPaths.selectNodeList(getDocumentElement(), xpath);
        
        // examine all file nodes
        for (int i = 0; i < nodes.getLength(); i++) {
        	try {
	            Element node = (Element)nodes.item(i);
	            // check to see if there's a checksum and checksum type
	            // need to get both the checksum value and type, each is 
	            // meaningless without the other
	            String checksumType = node.getAttribute(METS_CHECKSUMTYPE_ATTR).toLowerCase(); 
	            String checksum = node.getAttribute(METS_CHECKSUM_ATTR);
	                       
	            if (checksum != null && !"".equals(checksum.trim()) && checksumType != null) {              
	                digests.add(new ArchiveMessageDigest(
	                        checksumType,
	                        checksum));
	            }
        	} catch (PackageException e)
        	{
        		throw new FatalException(e.getMessage());
        	}
        	
        }        
        
        return digests;
    }
    
    /**
     * @return the namespace of the mets document
     */
    public String getNamespace() {
        return getDocumentElement().getNamespaceURI();
    }

    /**
     * @return the object id of the mets document
     */
    public String getObjId() {
        return documentBean.getMets().getOBJID();
    }

    /**
     * @throws FatalException
     * @return array of owners' names from daitss:AGREEMENT_INFO/@ACCOUNT
     */
    public String[] getAccounts() throws FatalException {
        Node contextNode = getDocumentElement();
        //String xpath = XPaths.Mets.AGENTS_ORGANIZATION_NAME + "/text()";
        //String xpath = XPaths.Daitss.AGREEMENT_INFO_ACCOUNT + "/text()";
        String xpath = XPaths.Daitss.AGREEMENT_INFO_ACCOUNT;
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String[] names = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            names[i] = node.getNodeValue();
        }        
        return names;
    }

    
    
    /**
     * @return String array
     * @throws FatalException
     */
    public String[] getProjects()  throws FatalException {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.AGENT_NOTES + "[starts-with(text(),'projects=')]/text()";    
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String[] projectArray;
        if(node != null) {
            projectArray = node.getNodeValue().replaceFirst("projects=", "").split(",");
        } else {
            projectArray = new String[0];
        }
        return projectArray;
    }

    /**
     * @throws FatalException
     * @return String array
     */
    public String[] getSubAccounts() throws FatalException {
        Node contextNode  = getDocumentElement();
        //String xpath = XPaths.Mets.AGENT_IPOWNER_NAME + "/text()";
        String xpath = XPaths.Daitss.AGREEMENT_INFO_SUB_ACCOUNT;
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String[] subAccounts = new String[nodes.getLength()];
        for(int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            subAccounts[i] = node.getNodeValue(); 
        }
        return subAccounts;        
    }


    
    /**
     * @return the agreement info subaccount
     * @throws FatalException
     */
    public String getSubAccount() throws FatalException {
        Node contextNode  = getDocumentElement();
        String xpath = XPaths.Daitss.AGREEMENT_INFO_SUB_ACCOUNT;
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String subAccount;
        if (node != null) {
            subAccount = node.getNodeValue();    
        } else {
            subAccount = "";
        }
        
        return subAccount;
    }
    
    /**
     * @return the first occurance of dc:title in the document.
     * @throws FatalException
     */
    public String getTitle() throws FatalException {       
        String methodName = "getTitles()";
        Element contextNode = getDocumentElement();
        String xpath = XPaths.SimpleDublinCore.TITLE + "/text()" + "|" + XPaths.Mods.TITLE + "/text()";
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String title;
        if (nodes.getLength() >= 1) {
            // extract the title
            title = nodes.item(0).getNodeValue();
        } else {
            // return an empty string
            String message = xpath + " does not exist.";
            String subject = this.getDf().getPath();
            Informer.getInstance().warning(this, methodName, message, subject);
            title = "";
        }
        
        if(nodes.getLength() > 1) {
            String subject = xpath + " occurs " + nodes.getLength() + " times" ;
            Informer.getInstance().info(this, 
                    methodName, 
                    "Too many titles", 
                    subject,
                    false);
        }                
        return title;
    }

    /**
     * @return the type of the mets document
     */
    public String getType() {
        return documentBean.getMets().getTYPE();
    }

    /**
     * @throws FatalException
     * @return the names of the volumes
     */
    public String[] getVolumes() throws FatalException {
        Node contextNode = getDocumentElement();
        String xpath = XPaths.Mets.DIV_VOLUME + "/@ORDERLABEL";
        NodeList nodes = XPaths.selectNodeList(contextNode, xpath);
        String[] volumes = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            volumes[i] = node.getNodeValue();
        }
        return volumes;
    }

    /**
     * Returns the first mimetype of a file found. This is only meant to be used
     * in a reduced METSDocument.
     * 
     * @throws
     *         FatalException
     * @return String object
     */
    public String getFirstMimeType() throws FatalException {
        Element contextNode = getDocumentElement();
        String xpath = XPaths.Mets.FILE + "/@MIMETYPE";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String type = (node != null) ? node.getNodeValue() : null;
        return type;
    }
    
    /**
     * @return true if the mets document has a 'file section'
     */
    public boolean hasFileSec() {
        return documentBean.getMets().isSetFileSec();
    }

    /**
     * @return true if the mets document has a 'mets header'
     */
    public boolean hasMetsHdr() {
        return documentBean.getMets().isSetMetsHdr();
    }

    /**
     * @param arg0
     * @param arg1
     * @throws org.xml.sax.SAXException
     */
    public void save(ContentHandler arg0, LexicalHandler arg1)
            throws SAXException {
        documentBean.save(arg0, arg1, options);
    }

    /**
     * @param arg0
     * @throws java.io.IOException
     */
    public void save(File arg0) throws IOException {
        documentBean.save(arg0, options);
    }

    /**
     * @param arg0
     * @throws java.io.IOException
     */
    public void save(OutputStream arg0) throws IOException {
        documentBean.save(arg0, options);
    }

    /**
     * @param arg0
     * @throws java.io.IOException
     */
    public void save(Writer arg0) throws IOException {
        documentBean.save(arg0, options);
    }

    /**
     * @param d
     */
    public void setDocument(MetsDocument d) {
        documentBean = d;
    }

    /**
     * @return true if the mets document has a 'structure link'
     */
    public boolean hasStructLink() {
        return documentBean.getMets().isSetStructLink();
    }

    /**
     * @return the total number of 'structure maps' in the mets document
     */
    public int structMapCount() {
        return documentBean.getMets().sizeOfStructMapArray();
    }

    /**
     * Merge the meta data from m
     * 
     * @param m
     */
    public void mergeMetaData(METSDocument m) {

        // Merge IDs

        // Get files
        MetsType.FileSec.FileGrp.File oldFile;
		oldFile = documentBean.getMets().getFileSec().getFileGrpArray()[0].getFileArray()[0];
        MetsType.FileSec.FileGrp.File newFile;
		newFile = m.getDocument().getMets().getFileSec().getFileGrpArray()[0].getFileArray()[0];

        //DMDID
        //get old dmdids
        String[] oldDmdIDs = (oldFile.isSetDMDID()) ? (String[]) oldFile.getDMDID().toArray(new String[0]) : new String[0];

        //get new dmdids
        String[] newDmdIDs = (newFile.isSetDMDID()) ? (String[]) newFile.getDMDID().toArray(new String[0]) : new String[0];

        //set old to of old union new
        String[] mergedDmdIDs = (String[]) union(oldDmdIDs, newDmdIDs);
        if (mergedDmdIDs.length > 0) {
            oldFile.setDMDID(Arrays.asList(mergedDmdIDs));
        }

        //ADMID
        //get old amdids
        String[] oldAmdIDs = (oldFile.isSetADMID()) ? (String[]) oldFile.getADMID().toArray(new String[0]) : new String[0];

        //get new amds
        String[] newAmdIDs = (newFile.isSetADMID()) ? (String[]) newFile.getADMID().toArray(new String[0]) : new String[0];

        //set old to of old union new
        String[] mergedAmdIDs = (String[]) union(oldAmdIDs, newAmdIDs);
        if (mergedAmdIDs.length > 0) {
            oldFile.setADMID(Arrays.asList(mergedAmdIDs));
        }
        // Descriptive meta data

        // Merge dmds
        MdSecType[] dmds = (documentBean.getMets().sizeOfDmdSecArray() > 0) ? documentBean
                .getMets().getDmdSecArray()
                : new MdSecType[0];
        MdSecType[] m_dmds = (m.getDocument().getMets().sizeOfDmdSecArray() > 0) ? m
                .getDocument().getMets().getDmdSecArray()
                : new MdSecType[0];
        MdSecType[] mergedDmds = (MdSecType[]) union(dmds, m_dmds);
        documentBean.getMets().setDmdSecArray(mergedDmds);

        // Merge adminitrative meta data

        // Old default amdSec
        AmdSecType[] amds = documentBean.getMets().getAmdSecArray();
        Vector oldAmds = new Vector(Arrays.asList(amds));
        AmdSecType oldDefaultAmd = null;
        for (Iterator iter = oldAmds.iterator(); iter.hasNext();) {
            AmdSecType element = (AmdSecType) iter.next();
            if (element.getID().equals("DEFAULT")) {
                oldDefaultAmd = element;
                iter.remove();
            }
        }
        amds = (oldAmds.size() > 0) ? (AmdSecType[]) oldAmds
                .toArray(new AmdSecType[0]) : new AmdSecType[0];

        // New default amdSec
        AmdSecType[] m_amds = m.getDocument().getMets().getAmdSecArray();
        Vector newAmds = new Vector(Arrays.asList(m_amds));
        AmdSecType newDefaultAmd = null;
        for (Iterator iter = newAmds.iterator(); iter.hasNext();) {
            AmdSecType element = (AmdSecType) iter.next();
            if (element.getID().equals("DEFAULT")) {
                newDefaultAmd = element;
                iter.remove();
            }
        }
        m_amds = (newAmds.size() > 0) ? (AmdSecType[]) newAmds
                .toArray(new AmdSecType[0]) : new AmdSecType[0];

        // Merge non-default amdSec
        AmdSecType[] mergedAmds = (AmdSecType[]) union(amds, m_amds);

        // Merge default amdSec
        if (oldDefaultAmd != null && newDefaultAmd != null) {
            // Merge techs
            MdSecType[] oldTechs = (oldDefaultAmd.sizeOfTechMDArray() > 0) ? oldDefaultAmd
                    .getTechMDArray()
                    : new MdSecType[0];
            MdSecType[] newTechs = (newDefaultAmd.sizeOfTechMDArray() > 0) ? newDefaultAmd
                    .getTechMDArray()
                    : new MdSecType[0];
            MdSecType[] mergedTechs = (MdSecType[]) union(oldTechs, newTechs);
            oldDefaultAmd.setTechMDArray(mergedTechs);

            // Merge rights
            MdSecType[] oldRights = (oldDefaultAmd.sizeOfRightsMDArray() > 0) ? oldDefaultAmd
                    .getRightsMDArray()
                    : new MdSecType[0];
            MdSecType[] newRights = (newDefaultAmd.sizeOfRightsMDArray() > 0) ? newDefaultAmd
                    .getRightsMDArray()
                    : new MdSecType[0];
            MdSecType[] mergedRights = (MdSecType[]) union(oldRights, newRights);
            oldDefaultAmd.setRightsMDArray(mergedRights);

            // sources
            MdSecType[] oldSources = (oldDefaultAmd.sizeOfSourceMDArray() > 0) ? oldDefaultAmd
                    .getSourceMDArray()
                    : new MdSecType[0];
            MdSecType[] newSources = (newDefaultAmd.sizeOfSourceMDArray() > 0) ? newDefaultAmd
                    .getSourceMDArray()
                    : new MdSecType[0];
            MdSecType[] mergedSources = (MdSecType[]) union(oldSources,
                    newSources);
            oldDefaultAmd.setSourceMDArray(mergedSources);

            // digiprovs
            MdSecType[] oldDigiprovs = (oldDefaultAmd.sizeOfDigiprovMDArray() > 0) ? oldDefaultAmd
                    .getDigiprovMDArray()
                    : new MdSecType[0];
            MdSecType[] newDigiprovs = (newDefaultAmd.sizeOfDigiprovMDArray() > 0) ? newDefaultAmd
                    .getDigiprovMDArray()
                    : new MdSecType[0];
            MdSecType[] mergedDigiprovs = (MdSecType[]) union(oldDigiprovs,
                    newDigiprovs);
            oldDefaultAmd.setDigiprovMDArray(mergedDigiprovs);
        }
        amds = documentBean.getMets().getAmdSecArray();
        mergedAmds = (AmdSecType[]) union(amds, mergedAmds);
        if (mergedAmds.length > 0) {
            documentBean.getMets().setAmdSecArray(mergedAmds);
        }

        // Merge the structure map sections

    }

    private static Object[] union(Object[] a, Object[] b) {
        HashSet set = new HashSet(Arrays.asList(a));
        set.addAll(Arrays.asList(b));
        Object[] u = set.toArray(a);
        return u;
    }

    /**
     * @return the project in the agreement info
     * @throws FatalException
     */
    public String getProject() throws FatalException {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Daitss.AGREEMENT_INFO_PROJECT;
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String project;
        if(node != null) {
            project = node.getNodeValue();
        } else {
            project = "";
        }
        return project;
    }
    
    /**
     * @return the account in the agreement info
     * @throws FatalException
     */
    public String getAccount() throws FatalException {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Daitss.AGREEMENT_INFO_ACCOUNT;
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String account;
        if(node != null) {
            account = node.getNodeValue();
        } else {
            account = "";
        }
        return account;
    }
    
    /**
     * Get the Creator metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the creator program 
     * @throws FatalException
     */
    public String getCreator() throws FatalException {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.TechMd.CREATION_METHOD + "/@SOFTWARE";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String creator = null;
        if(node != null && node.getNodeValue() != null) {
        	creator = new String(node.getNodeValue());
        } 
        return creator;
    }
    
    /**
     * Get the Create Date metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getCreateDate() throws FatalException {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.FILE + "/@CREATEDATE";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String createDate = null;
        if(node != null && node.getNodeValue() != null) {
        	createDate = new String(node.getNodeValue());
        } 
        return createDate;
    }
    
    /**
     * Get the File Size metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getFileSize() throws FatalException {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.FILE + "/@SIZE";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String fileSize = null;
        if(node != null && node.getNodeValue() != null) {
        	fileSize = new String(node.getNodeValue());
        } 
        return fileSize;
    }
    
    /**
     * Get the compression metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getCompressionName() throws FatalException    {   
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.TECHMD_BUCKET + 
        	XPaths.TechMd.COMPRESSION.substring(1) + "/@NAME";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String compression = null;
        if(node != null && node.getNodeValue() != null) {
        	compression = new String(node.getNodeValue());
        } 
        return compression;
    }
    
    /**
     * Get the image colorspace metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getImageColorspace() throws FatalException    {
        Element contextNode  = getDocumentElement();
        String xpath =  XPaths.Mets.TECHMD_BUCKET + 
        	XPaths.TechMd.IMAGE_COLOR_SPACE.substring(1) + "/text()"; 
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String colorspace = null;
        if(node != null && node.getNodeValue() != null) {
        	colorspace = new String(node.getNodeValue());
        } 
        return colorspace;
    }
    
    /**
     * Get the image orientation metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getImageOrientation() throws FatalException    {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.TECHMD_BUCKET + 
        	XPaths.TechMd.IMAGE_ORIENTATION.substring(1) + "/text()";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String orientation = null;
        if(node != null && node.getNodeValue() != null) {
        	orientation = new String(node.getNodeValue());
        } 
        return orientation;
    }
    
    /**
     * Get the image depth metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getImageDepth() throws FatalException    {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.TECHMD_BUCKET + 
    		XPaths.TechMd.IMAGE_BIT_DEPTH.substring(1) + "/text()";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String depth = null;
        if(node != null && node.getNodeValue() != null) {
        	depth = new String(node.getNodeValue());
        } 
        return depth;
    }
    
    /**
     * Get the image X dimension metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getImageDimensionX() throws FatalException    {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.TECHMD_BUCKET + 
			XPaths.TechMd.IMAGE_DIMENSIONS.substring(1) + "/@X";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String dimX = null;
        if(node != null && node.getNodeValue() != null) {
        	dimX = new String(node.getNodeValue());
        } 
        return dimX;
    }
    
    /**
     * Get the image Y dimension metadata described in the MetDocument.  This is only meant to be used
     * in a reduced METSDocument.
     * @return the create date
     * @throws FatalException
     */
    public String getImageDimensionY() throws FatalException    {
        Element contextNode  = getDocumentElement();
        String xpath = XPaths.Mets.TECHMD_BUCKET + 
			XPaths.TechMd.IMAGE_DIMENSIONS.substring(1) + "/@Y";
        Node node = XPaths.selectSingleNode(contextNode, xpath);
        String dimY = null;
        if(node != null && node.getNodeValue() != null) {
        	dimY = new String(node.getNodeValue());
        } 
        return dimY;
    }
}