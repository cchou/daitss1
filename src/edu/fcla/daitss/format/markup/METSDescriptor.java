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
 * Created on Jun 23, 2004
 *
 */

package edu.fcla.daitss.format.markup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.fcla.da.xml.Checker;
import edu.fcla.da.xml.Validator;
import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.entity.AIP;
import edu.fcla.daitss.entity.Agreements;
import edu.fcla.daitss.entity.Events;
import edu.fcla.daitss.entity.GlobalFilePackage;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.IntEntity;
import edu.fcla.daitss.entity.Relationships;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.ArchiveMessageDigest;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MessageDigestCalculator;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.WebCacheUtils;
import gov.loc.mets.DivType;
import gov.loc.mets.FileGrpType;
import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsType;
import gov.loc.mets.StructMapType;
import gov.loc.mets.MetsDocument.Mets;
import gov.loc.mets.MetsType.FileSec.FileGrp;

/**
 * @author franco
 */
public class METSDescriptor extends XML implements Descriptor {

    /**
     * @author franco
     *
     * Provides functionality to generate mets descriptors
     */
    public static final class Factory {

        /**
         * Used to specify absolution to the filesystem.
         */
        public static final short ABSOLUTE = 2;

        private static MetsDocument metsDocument;

        private static MetsType metsRoot;

        private static File packageDir;

        /**
         * Used to specify relativity to the highest degree.
         */
        public static final short RELATIVE = 0;

        /**
         * Used to specify relativity to the package.
         */
        public static final short RELATIVE_TO_PACKAGE = 1;

        private static File[] concatenate(File[] a, File[] b) {
            File[] temp = new File[a.length + b.length];

            for (int i = 0; i < temp.length; i++) {
                if (i < a.length) {
                    temp[i] = a[i];
                } else {
                    temp[i] = b[i - a.length];
                }
            }
            return temp;
        }

        /**
         * Creates a mets descriptor for a document consisting of a collection
         * of files. Sequence order in the document is determined by
         * alphabeticaly.
         *
         * @param dirPath
         *            The path to the directory containing the distributed
         *            files.
         * @param fileName
         *            The name of mets descriptor file.
         * @param comparator
         *            Determines which order to sort the files in the directory.
         * @throws FatalException
         *             when it cannot create a new mets docuemt.
         * @return Returns the path of the descriptor.
         */
        public static String createDirectoryDescriptor(String dirPath, String fileName, Comparator<File> comparator)
                throws FatalException {
            File dir = new File(dirPath);

            // Check constraints.
            if (!dir.exists()) {
                throw new FatalException(dirPath + " does not exist.");
            }
            if (!dir.isDirectory()) {
                throw new FatalException(dirPath + " is not a valid directory.");
            }
            if (dir.listFiles().length < 1) {
                throw new FatalException(dirPath + " is an empty directory.");
            }

            // Get a sorted list of files.
            File[] files = dir.listFiles();
            Arrays.sort(files, comparator);

            // Construct the mets.
            MetsDocument distDoc = METSDescriptor.Factory.newIdentity();
            FileGrp fileGrp = distDoc.getMets().addNewFileSec().addNewFileGrp();
            StructMapType sMap = distDoc.getMets().getStructMapArray(0);
            DivType rootDiv = sMap.getDiv();
            for (int i = 0; i < files.length; i++) {
                File sysFile = files[i];
                gov.loc.mets.FileGrpType.File metsFile = fileGrp.addNewFile();

                // Descriptive file meta data.

                // File Id.
                String metsId = "FID" + Integer.toString(i);
                metsFile.setID(metsId);

                // Sequence.
                metsFile.setSEQ(i + 1);

                // Structure meta data
                DivType div = rootDiv.addNewDiv();
                div.setID("div" + (i + 1));
                div.setORDER(BigInteger.valueOf(i + 1));
                div.setLABEL("Page " + (i + 1));
                div.setTYPE("page");
                div.addNewFptr().setFILEID(metsId);

                // OS specific file meta data.
                describeSystemFile(sysFile, metsFile, FileGrpType.File.FLocat.LOCTYPE.OTHER, "DatabaseID", RELATIVE, false);
            }

            // Write mets descriptor to disk.
            XmlOptions options = new XmlOptions();
            options.setSavePrettyPrint();
            options.setCharacterEncoding("UTF-8");
            File outFile = new File(dir, fileName);
            try {
                distDoc.save(outFile, options);
            } catch (IOException e) {
                throw new FatalException(e);
            }

            return outFile.getAbsolutePath();
        }

        /**
         * Generates a mets descriptor file describing a package
         *
         * @param packagePath
         *            absolute path to the package directory
         * @param fileName
         *            the file to write the description to
         * @param account
         *            to whom the package belongs
         * @param subAccount
         *            from where the package came from
         * @param project
         *            to what project the package belongs
         *
         * @throws FatalException
         *             when the path of package does not exist or is not a
         *             directory.
         *
         * @return path to the generated descriptor
         */
        public static String createSipDescriptor(String packagePath, String fileName, String account)
                throws FatalException {

            packageDir = new File(packagePath);

            // must exist and must be a dir
            if (!packageDir.exists()) {
                throw new FatalException(packagePath + " does not exist");
            } else if (!packageDir.isDirectory()) {
                throw new FatalException(packagePath + " is not a directory");
            }

            // Options
            XmlOptions ops = generateOptions();

            // Make Document Object
            //metsDocument = MetsDocument.Factory.newInstance(ops);
            metsDocument = newIdentity();

            // Make mets object
            metsRoot = metsDocument.getMets();

            // Set the object id
            metsRoot.setOBJID(packageDir.getName());

            // Scan packageDir for all the files
            File[] files = listFilesRecursive(packageDir);

            // Generate the mets header
            generateMetsHeader(account);

            // Generate the file section
            generateFileSection(files);

            // Generate the stucture map
            generateStructureMap();

            File outputFile = new File(packageDir, fileName);
            try {
                metsDocument.save(outputFile, ops);
            } catch (IOException e) {
                throw new FatalException(e);
            }
            return outputFile.getAbsolutePath();
        }

        private static void describeSystemFile(File sysFile, FileGrpType.File metsFile, FileGrpType.File.FLocat.LOCTYPE.Enum locType, String otherLocType, short relativity, boolean setDate)
                throws FatalException {

            // Checksum.
            String algorithm = ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1");

            try {
            	String sum = MessageDigestCalculator.getMessageDigest(sysFile.getAbsolutePath(), algorithm, false).getValue();
            	metsFile.setCHECKSUM(sum);
            } catch (PackageException e)
            {
            	throw new FatalException(e.getMessage());
            }

            if (ArchiveMessageDigest.ALG_MD5.equals(algorithm)) {
                metsFile.setCHECKSUMTYPE(FileGrpType.File.CHECKSUMTYPE.MD_5);
            } else if (ArchiveMessageDigest.ALG_SHA1.equals(algorithm)) {
                metsFile.setCHECKSUMTYPE(FileGrpType.File.CHECKSUMTYPE.SHA_1);
            } else if (ArchiveMessageDigest.ALG_SHA256.equals(algorithm)) {
                metsFile.setCHECKSUMTYPE(FileGrpType.File.CHECKSUMTYPE.SHA_256);
            } else if (ArchiveMessageDigest.ALG_SHA384.equals(algorithm)) {
                metsFile.setCHECKSUMTYPE(FileGrpType.File.CHECKSUMTYPE.SHA_384);
            } else if (ArchiveMessageDigest.ALG_SHA512.equals(algorithm)) {
                metsFile.setCHECKSUMTYPE(FileGrpType.File.CHECKSUMTYPE.SHA_512);
            }

            // Mimetype.
            int extStart = sysFile.getName().lastIndexOf('.') + 1;
            String extension = sysFile.getName().substring(extStart);
            //Extension.init();
            String[] mimeTypes = (String[]) Extension.getMimeLookup().get(extension);
            String mimeType = (mimeTypes != null) ? mimeTypes[0]
                    : MimeMediaType.MIME_APP_UNK;
            metsFile.setMIMETYPE(mimeType);

            // File Size.
            metsFile.setSIZE(sysFile.length());

            // Created Date.
            if (setDate) {
                metsFile.setCREATED(Calendar.getInstance());
            }
            // Location.
            FileGrpType.File.FLocat fLocat = metsFile.addNewFLocat();
            fLocat.setHref(sysFile.getName());
            fLocat.setType("simple");
            fLocat.setLOCTYPE(locType);
            if (otherLocType != null) {
                fLocat.setOTHERLOCTYPE(otherLocType);
            }
            switch (relativity) {
            case RELATIVE_TO_PACKAGE:
                fLocat.setHref(relativePath(sysFile));
                break;
            case RELATIVE:
                fLocat.setHref(sysFile.getName());
                break;
            case ABSOLUTE:
                fLocat.setHref(sysFile.getAbsolutePath());
                break;
            }
        }


        private static void generateFileSection(File sysFiles[])
                throws FatalException {
            Arrays.sort(sysFiles);
            MetsType.FileSec fileSec = metsRoot.addNewFileSec();
            MetsType.FileSec.FileGrp fileGrp = fileSec.addNewFileGrp();

            for (int i = 0; i < sysFiles.length; i++) {
                String fileID = "FID" + (i + 1);
                File sysFile = sysFiles[i];
                if (!sysFile.isDirectory()) {
                    // Make a new file
                    FileGrpType.File file = fileGrp.addNewFile();
                    file.setID(fileID);

                    // Set Element Attributes
                    describeSystemFile(sysFile, file, FileGrpType.File.FLocat.LOCTYPE.OTHER, "SYSTEM", RELATIVE_TO_PACKAGE, true);
                }
            }
        }

        private static void generateMetsHeader(String account) {

            // Make the metsHdr Element
            MetsType.MetsHdr metsHdr = metsRoot.addNewMetsHdr();

            // Set Element Attributes
            // ID
            String packageID = packageDir.getName();
            metsHdr.setID(packageID);

            // Create date
            metsHdr.setCREATEDATE(Calendar.getInstance());

            // Generate Children Elements
            // Make Agents
            MetsType.MetsHdr.Agent agent1 = metsHdr.addNewAgent();
            agent1.setROLE(MetsType.MetsHdr.Agent.ROLE.CREATOR);
            agent1.setTYPE(MetsType.MetsHdr.Agent.TYPE.ORGANIZATION);
            // 1st Agent name
            agent1.setName("FDA");

            MetsType.MetsHdr.Agent agent2 = metsHdr.addNewAgent();
            agent2.setROLE(MetsType.MetsHdr.Agent.ROLE.CREATOR);
            agent2.setTYPE(MetsType.MetsHdr.Agent.TYPE.OTHER);
            agent2.setOTHERTYPE("SOFTWARE");
            // 2nd Agent name
            agent2.setName("edu.fcla.daitss.format.markup.METSDescriptor.Factory");

            if (account != null) {
                    MetsType.MetsHdr.Agent agentAccount = metsHdr.addNewAgent();
                    agentAccount.setROLE(MetsType.MetsHdr.Agent.ROLE.IPOWNER);
                    agentAccount.setTYPE(MetsType.MetsHdr.Agent.TYPE.OTHER);
                    agentAccount.setName(account);
            }
        }

        /**
         * @return options for saving and loading mets
         */
        public static XmlOptions generateOptions() {
            XmlOptions ops = new XmlOptions();
            Hashtable<String, String> nameSpaces = new Hashtable<String, String>();

            ops.setCharacterEncoding("ISO-8859-1");

            ops.setCompileDownloadUrls();

            // hardcoded, but internal use only
            nameSpaces.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            nameSpaces.put("http://www.w3.org/1999/xlink", "xlink");
            nameSpaces.put("http://www.loc.gov/METS/", "METS");
            nameSpaces.put("http://purl.org/dc/elements/1.1/", "dc");
            nameSpaces.put("http://www.fcla.edu/dls/md/techmd/", "techmd");
            nameSpaces.put("http://www.fcla.edu/dls/md/palmm/", "palmm");
            nameSpaces.put("http://www.fcla.edu/dls/md/rightsmd/", "rightsmd");

            ops.setLoadAdditionalNamespaces(nameSpaces);
            ops.setLoadLineNumbers();

            ops.setSaveSuggestedPrefixes(nameSpaces);
            ops.setSaveAggresiveNamespaces();
            ops.setSaveNamespacesFirst();
            ops.setSavePrettyPrint();
            ops.setSavePrettyPrintIndent(4);
            ops.setCharacterEncoding("UTF-8");

            return ops;
        }

        private static void generateStructureMap() {
            //StructMapType structMap = metsRoot.addNewStructMap();
            StructMapType structMap = metsRoot.getStructMapArray(0);
            //DivType div = structMap.addNewDiv();
            DivType div = structMap.getDiv();
            DivType.Fptr fPtr;

            FileGrpType.File[] files = metsRoot.getFileSec().getFileGrpArray()[0].getFileArray();

            // Make a new file pointer for each file in the file section
            for (int i = 0; i < files.length; i++) {
                FileGrpType.File file = files[i];
                String fileID = file.getID();
                fPtr = div.addNewFptr();
                fPtr.setFILEID(fileID);
            }
        }

        private static File[] listFilesRecursive(File dir) {
            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    // Get list of subfiles recursively
                    File[] subFiles = listFilesRecursive(file);
                    // Concatenate files and subs
                    files = concatenate(files, subFiles);
                }
            }
            return files;
        }

        /**
         * @return Document class
         * @throws FatalException
         */
        public static Document newAipBase() throws FatalException {
            Document doc = XPaths.newDocument();

            /* Mets namespace */
            String namespace = ArchiveProperties.getInstance().getArchProperty("NS_METS");

            /* Mets Element */
            doc.appendChild(doc.createComment("AIP Descriptor"));
            Element root = (Element) doc.appendChild(doc.createElementNS(namespace, "mets"));

            /* metsHdr */
            root.appendChild(doc.createComment("mets header"));
            Element metsHdr = (Element) root.appendChild(doc.createElementNS(namespace, "metsHdr"));
            metsHdr.setAttribute("CREATEDATE", DateTimeUtil.now(DateTimeUtil.SCHEMA_DATE_PATTERN));
            Element agent = (Element) metsHdr.appendChild(doc.createElementNS(namespace, "agent"));
            agent.setAttribute("ROLE", "CREATOR");
            agent.setAttribute("TYPE", "OTHER");
            agent.setAttribute("OTHERTYPE", "SOFTWARE");
            Element name = (Element) agent.appendChild(doc.createElementNS(namespace, "name"));
            name.appendChild(doc.createTextNode("FDA"));

            /* amdSec for AIP meta data */
            root.appendChild(doc.createComment("AIP administrative meta data concerning daitss"));
            Element aipCoreAmdSec = (Element) root.appendChild(doc.createElementNS(namespace, "amdSec"));
            aipCoreAmdSec.setAttribute("ID", "AMD-AIP-CORE");

            /* amdSec for AIP file meta data */
            root.appendChild(doc.createComment("AIP administrative meta data concerning files"));
            Element aipFileAmdSec = (Element) root.appendChild(doc.createElementNS(namespace, "amdSec"));
            aipFileAmdSec.setAttribute("ID", "AMD-AIP-FILE");

            /* amdSec for SIP file meta data */
            root.appendChild(doc.createComment("SIP administrative meta data"));
            Element sipAmdSec = (Element) root.appendChild(doc.createElementNS(namespace, "amdSec"));
            sipAmdSec.setAttribute("ID", "AMD-SIP-MD");

            /* fileSec */
            root.appendChild(doc.createComment("file section"));
            Element fileSec = (Element) root.appendChild(doc.createElementNS(namespace, "fileSec"));
            fileSec.appendChild(doc.createComment("files in the package"));
            Element fileGrp = doc.createElementNS(namespace, "fileGrp");
            fileSec.appendChild(fileGrp);

            /* structMap */
            root.appendChild(doc.createComment("structure map"));
            Element structMap = (Element) root.appendChild(doc.createElementNS(namespace, "structMap"));
            Element div = doc.createElementNS(namespace, "div");
            structMap.appendChild(div);

            return doc;
        }

        /**
         * @return a minimal MetsDocument for use in daitss
         * @throws FatalException
         */
        public static MetsDocument newIdentity() {
            MetsDocument mDoc = MetsDocument.Factory.newInstance();
            Mets mets = mDoc.addNewMets();
            StructMapType structMap = mets.addNewStructMap();
            structMap.addNewDiv();
            return mDoc;
        }

        private static String relativePath(File file) {
            String relative;
            if (file.getAbsolutePath().startsWith(packageDir.getAbsolutePath())) {
                relative = file.getAbsolutePath().substring(packageDir.getAbsolutePath().length() + 1);
            } else {
                relative = file.getAbsolutePath();
            }
            return relative;
        }
    }

    /**
     * @author franco
     *
     * Resolves meta data dependancies
     */
    public static final class MetaDataResolver {

        /**
         * @param cursor
         * @param id
         * @return meta data ID references associated with this file ID
         */
        private static String[] getFileMdById(XmlCursor cursor, String id) {
            cursor.toStartDoc();
            // get all the IDs
            cursor.selectPath("$this//@ID");

            // goto the id that matches id
            while (cursor.hasNextSelection()) {
                cursor.toNextSelection();
                String currentID = cursor.getTextValue();
                if (currentID.equals(id)) {
                    // goto the element that the attribute belongs to
                    cursor.toParent();
                    break;
                }
            }

            String[] metaData = getMetaDataIds(cursor);
            return metaData;
        }

        /**
         * @param cursor
         * @return the meta data ID references for element that the cursor is at
         */
        private static String[] getMetaDataIds(XmlCursor cursor) {
            // get all the amdids
            QName amdQName = new QName("ADMID");
            String amdid = cursor.getAttributeText(amdQName);

            // get all the dmdids
            QName dmdQName = new QName("DMDID");
            String dmdid = cursor.getAttributeText(dmdQName);

            String[] amdidArray = (amdid != null) ? amdid.split(" ")
                    : new String[0];

            String[] dmdidArray = (dmdid != null) ? dmdid.split(" ")
                    : new String[0];

            String[] metaData = (String[]) union(amdidArray, dmdidArray);
            // Return an array of all the meta data ids
            return metaData;
        }

        /**
         * @param cursor
         * @param id
         * @return structural meta data ID references associated with this file
         *         ID
         */
        private static String[] getStructureMdById(XmlCursor cursor, String id) {

            String[] structureMD;

            // if we are in a div & we are a div
            QName myQName = cursor.getName();
            String myName = myQName.getLocalPart();
            cursor.selectPath("$this//@FILEID");
            if (myName.equals("div") && cursor.hasNextSelection()) {
                structureMD = new String[0];
                // get my md
                cursor.push();
                String[] myMetaDataIDs = getMetaDataIds(cursor);
                cursor.pop();

                // union my md with structure md
                structureMD = (String[]) union(structureMD, myMetaDataIDs);

                // get childrens ms
                cursor.push();
                if (cursor.toFirstChild()) {
                    do {
                        String[] childMetaData;
                        childMetaData = getStructureMdById(cursor, id);
                        // union this with structuremd
                        structureMD = (String[]) union(structureMD, childMetaData);
                    } while (cursor.toNextSibling());
                }
                cursor.pop();
            } else {
                structureMD = new String[0];
            }

            return structureMD;
        }

        /**
         * @param doc
         * @param id
         * @return String array
         */
        public static String[] resolve(MetsDocument doc, String id) {
            String[] ids = resolveMetaDataIds(doc, id);
            return ids;
        }

        private static String[] resolveMetaDataIds(MetsDocument doc, String id) {

            XmlCursor cursor = doc.newCursor();
            String[] base = new String[1];
            base[0] = id;

            // get file meta data
            String[] fileMetaDataArray = getFileMdById(cursor, id);
            fileMetaDataArray = (String[]) union(fileMetaDataArray, base);

            // get structure meta data
            StructMapType[] structMapArray = doc.getMets().getStructMapArray();
            String[] structureMetaDataArray = new String[0];
            for (int i = 0; i < structMapArray.length; i++) {
                StructMapType map = structMapArray[i];
                DivType div = map.getDiv();
                XmlCursor divCursor = div.newCursor();
                String[] divMetaDataArray = getStructureMdById(divCursor, id);
                structureMetaDataArray = (String[]) union(structureMetaDataArray, divMetaDataArray);
            }

            // union file md with structure md
            String[] resolvedMD = (String[]) union(fileMetaDataArray, structureMetaDataArray);

            return resolvedMD;
        }

        private static Object[] union(Object[] a, Object[] b) {
            HashSet<Object> set = new HashSet<Object>(Arrays.asList(a));
            set.addAll(Arrays.asList(b));
            Object[] u = set.toArray(a);
            return u;
        }

    }

    /**
     * Fully-qualified name for this class. To be used for
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = METSDescriptor.class.getName();

    /**
     * @param aip
     * @throws FatalException
     */
    private static void attachSchemaLocations(Document aip) throws FatalException {
        ArchiveProperties p = ArchiveProperties.getInstance();
        aip.getDocumentElement().setAttribute(p.getArchProperty("DES_SCHEMAINSTANCE_NS"), p.getArchProperty("NS_XMLSCHEMA_INSTANCE"));
        String schemaMap = p.getArchProperty("DES_METS_SCHEMA")
                + " "
                + p.getArchProperty("DES_DC_SCHEMA")
                + " "
                + p.getArchProperty("DES_TECHMD_SCHEMA")
                + " "
                + p.getArchProperty("DES_PALMM_SCHEMA")
                + " "
                + p.getArchProperty("DES_RIGHTSMD_SCHEMA")
                + " "
                + p.getArchProperty("DES_DAITSS_SCHEMA");

        aip.getDocumentElement().setAttribute(p.getArchProperty("DES_SCHEMA_LOCATION"), schemaMap);

    }

    /**
     * @param fileName
     * @param ip
     * @param oldAipDescFile the old aip descriptor file
     * @return METSDescriptor object
     * @throws PackageException
     * @throws FatalException
     */
    public static METSDescriptor createAipDescriptorForReIngest(String fileName, InformationPackage ip, File oldAipDescFile) throws PackageException, FatalException {

        // make the mets descriptor
        METSDescriptor descriptor = createAipDescriptor(fileName, ip);

        /*
         * Auto-localize
         * Attach the old schema so we don't have to re-localize this
         */

        // get the xml file
        File newAipDescFile = new File(descriptor.getPath());

        try {

                // xml parsing overhead
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();

                WebCacheResolver resolver = WebCacheUtils.getResolver();
                builder.setEntityResolver(resolver);

                // parse the old and new documents
                Document newDoc = builder.parse(newAipDescFile);
                Document oldDoc = builder.parse(oldAipDescFile);

                // transfer the schema location from the old doc to the new doc to have it pre-localized
                String schemaLocationAttrName = ArchiveProperties.getInstance().getArchProperty("DES_SCHEMA_LOCATION");
                String schemaLocation = oldDoc.getDocumentElement().getAttribute(schemaLocationAttrName);
                newDoc.getDocumentElement().setAttribute(schemaLocationAttrName, schemaLocation);

                // save the document to the file
                newAipDescFile.delete();
                FileOutputStream fos = new FileOutputStream(newAipDescFile);
                serializeXml(fos, newDoc);
                fos.close();
        } catch (IOException e) {
                StackTraceElement element = new Throwable().getStackTrace()[0];
            Informer.getInstance().fail(
                        element.getClassName(),
                        element.getMethodName(),
                    "Cannot update file: " + fileName,
                    "Updating aip descriptor for reingest (autolocalizing)",
                    e);
        } catch (ParserConfigurationException e) {
                StackTraceElement element = new Throwable().getStackTrace()[0];
            Informer.getInstance().fail(
                        element.getClassName(),
                        element.getMethodName(),
                    "Cannot create xml parser",
                    "Updating aip descriptor for reingest (autolocalizing)",
                    e);
                } catch (SAXException e) {
                        StackTraceElement element = new Throwable().getStackTrace()[0];
            Informer.getInstance().fail(
                        element.getClassName(),
                        element.getMethodName(),
                    "Cannot parse xml: " + fileName,
                    "Updating aip descriptor for reingest (autolocalizing)",
                    e);
                }

        return descriptor;
    }

    /**
     * @param fileName
     * @param ip
     * @return METSDescriptor object
     * @throws FatalException
     * @throws PackageException
     */
    public static METSDescriptor createAipDescriptor(String fileName, InformationPackage ip)
            throws PackageException, FatalException {
        String methodName = "createAipDescriptor(String)";

        /* Get a base AIP descriptor. */
        Document aip = Factory.newAipBase();

        /* Get the data files associated by thier absolute paths. */
        Hashtable<String, DataFile> dfl = DataFileFactory.getInstance().getDflAbsPath();

        /*
         * Merge the AIP core meta data: (Global Files, Events, Relationships,
         * IntEntity).
         */
        mergeAipCoreMD(aip, ip);

        if (ip instanceof SIP || ip instanceof AIP) {

            /* Get only the local files */
            Hashtable<String, DataFile> temp = new Hashtable<String, DataFile>();
            for (String path : dfl.keySet()) {
                DataFile df = dfl.get(path);
                if (!df.isGlobal()) {
                        temp.put(path, df);
                }
            }
            dfl = temp;

            /*
             * Make a mapping to store associations of element ids between the
             * SIP desctiptor and AIP descriptor.
             */
            Map<String, String> idMap = new Hashtable<String, String>();

            /*
             * Get the SIP descriptor.
             * This is the original submitted descriptor
             */
            Document sip = loadSipDoc(ip);

            /*
             * Merge the namespace locations
             * from the sip into the aip
             */
            mergeNamespacLoactions(aip, sip);

            /*
             * Merge descriptive and administrative meta data from SIP
             * descriptor.
             */
            mergeSipMd(aip, sip, idMap);

            /*
             * Merge file meta data from
             * the data files and the SIP descriptor.
             */
            mergeFiles(aip, sip, dfl, idMap);

            /*
             * Merge structure map from the sip descriptor.
             * This will create r0 and rC
             */
            String sipDescPath = ip.getInitialDescriptor().getPath();
            mergeStructMap(aip, sip, idMap, sipDescPath);

            /* Resolve meta data id references. */
            updateRefs(aip, idMap);

            /* Remove unreferenced meta data */
            stripUnrefMd(aip);

        } else if (ip instanceof GlobalFilePackage) {
            // type is gfp
            /* Get only the global files */
            Hashtable<String, DataFile> temp = new Hashtable<String, DataFile>();
            for(String key : dfl.keySet()) {
                DataFile df = dfl.get(key);
                if (df.isGlobal()) {
                    temp.put(key, df);
                }
            }
            dfl = temp;

            /* amdSec for daitss xml */
            Node amdSec = XPaths.selectSingleNode(aip, "/mets:mets/mets:amdSec[@ID='AMD-AIP-FILE']");

            /* File section */
            Node fileGrp = XPaths.selectSingleNode(aip, "/mets:mets/mets:fileSec/mets:fileGrp");

            /* Structure map div */
            Node div = XPaths.selectSingleNode(aip, "/mets:mets/mets:structMap/mets:div");

            // for all the data files
            Vector<DataFile> dfVec = new Vector<DataFile>(dfl.values());

            // table for all the ids
            Map<String, String> idMap = new Hashtable<String, String>();

            // Merge the xml namespace information into the aip descriptor
            attachSchemaLocations(aip);


            for (Iterator iter = dfVec.iterator(); iter.hasNext();) {
                DataFile df = (DataFile) iter.next();

                /* FID & TMDID */
                String mid = nextID("TMD", idMap);
                idMap.put(mid, mid);

                String fid = nextID("FID", idMap);
                idMap.put(fid, fid);

                Element daitssXML = df.toXML().getDocumentElement();
                daitssXML = (Element) aip.importNode(daitssXML, true);

                /* XML */
                makeDfMd(amdSec, mid, daitssXML);

                /* File */
                makeDfFile(fileGrp, fid, mid, df);

                /* Structure */
                makeDfStruct(div, fid);

            }
        }

        /* Save the AIP descriptor xml to a file */
        File file = null;
        try {
            file = new File(fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            serializeXml(fos, aip);
            try {
                fos.close();
            } catch (IOException e) {
                Informer.getInstance().warning(CLASSNAME,
                        methodName,
                        "Unable to close FileOutputStream",
                        file.getAbsolutePath());
            }
        } catch (IOException e) {
            Informer.getInstance().fail(CLASSNAME, methodName,
                    "Cannot create File",
                    "file: " + file.getAbsolutePath(), e);
        }

        /* Return an Ingest Descriptor of the AIP descriptor */
        return new METSDescriptor(file.getAbsolutePath(), ip);
    }

        /**
     * @param filePath
     *            absolute path to an existing readable file
     * @return whether or not its a PDF file
     * @throws FatalException
     */
    public static boolean isType(String filePath) throws FatalException {
        return isType(filePath, null);
    }

    /**
     * @param ip
     * @return  a DOM Document of the sip descriptor
     * @throws FatalException
     */
    private static Document loadSipDoc(InformationPackage ip)
            throws FatalException {
        String methodName = "loadSipDoc(InformationPackage)";
        String sipFilePath = ip.getIntEnt().getDescPath();
        File file = new File(sipFilePath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db;
        Document sip = null;
        try {
            db = dbf.newDocumentBuilder();
                WebCacheResolver resolver = WebCacheUtils.getResolver();
            db.setEntityResolver(resolver);
            sip = db.parse(file);
        } catch (ParserConfigurationException e) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Could not fabricate a new Document Builder", "loading sip xml", e);
        } catch (SAXException e) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Could not parse sip xml", "loading sip xml", e);
        } catch (IOException e) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Could not load sip xml file", "loading sip xml", e);
        }

        return sip;
    }

    /**
     * Construct a mets:file element based on a DataFile object
     *
     * @param fileGrp
     *            the parent of the new file element
     * @param df
     *            the souorce of the file elements attributes.
     * @param fid
     *            the value for the ID attribute of the file
     * @param mid
     *            the value for the ADMID attribue of the file.
     * @throws FatalException
     */
    private static void makeDfFile(Node fileGrp, String fid, String mid, DataFile df)
            throws FatalException {
        Document doc = fileGrp.getOwnerDocument();
        String namespace = doc.getDocumentElement().getNamespaceURI();

        /* Generate a new file element from the datafile and the id */

        // File element
        Element newFile = (Element) fileGrp.appendChild(doc.createElementNS(namespace, "file"));

        // ID
        newFile.setAttribute("ID", fid);

        // Mimetype
        newFile.setAttribute("MIMETYPE", df.getMediaType());

        // Size
        newFile.setAttribute("SIZE", Long.toString(df.getSize()));

        // Creation Date
        if (!df.getCreateDate().equals("0000-00-00 00:00:00")) {
            newFile.setAttribute("CREATED", df.getCreateDate().replaceFirst(" ", "T"));
        }

        // Checksum Type
        String mdType = ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1");
        newFile.setAttribute("CHECKSUMTYPE", mdType.toUpperCase());

        // Checksum
        newFile.setAttribute("CHECKSUM", df.getMesgDigestValue(mdType));

        // Administrative meta data
        newFile.setAttribute("ADMID", mid);

        // File location
        Element fLocat = (Element) newFile.appendChild(doc.createElementNS(namespace, "FLocat"));
        fLocat.setAttribute("LOCTYPE", "URL");
        String xlinkNS = ArchiveProperties.getInstance().getArchProperty("DES_XLINK_NS_URI");
        fLocat.setAttributeNS(xlinkNS, "href", df.getPackagePath());
    }

    /**
     * Build a techMD section around a meta data element.
     *
     * @param amdSec
     * @param mid
     * @param daitss
     */
    private static void makeDfMd(Node amdSec, String mid, Element daitss) {

        // Document to add the mdSec too
        Document doc = amdSec.getOwnerDocument();

        // Mets namespace
        String namespace = amdSec.getNamespaceURI();

        // Make a techMD section
        Element techMD = (Element) amdSec.appendChild(doc.createElementNS(namespace, "techMD"));
        techMD.setAttribute("ID", mid);

        // Make a mdWrap
        Element mdWrap = (Element) techMD.appendChild(doc.createElementNS(namespace, "mdWrap"));
        mdWrap.setAttribute("MDTYPE", "OTHER");

        // Make a xmlData
        Element xmlData = (Element) mdWrap.appendChild(doc.createElementNS(namespace, "xmlData"));
        xmlData.appendChild(daitss);
    }

    /**
     * @param div
     * @param fid
     */
    private static void makeDfStruct(Node div, String fid) {
        /* add an ftpr to the div pointing to the id */
        String namespace = div.getNamespaceURI();
        Document d = div.getOwnerDocument();
        Element fptr = (Element) div.appendChild(d.createElementNS(namespace, "fptr"));
        fptr.setAttribute("FILEID", fid);
    }

    /**
     * Merge the core AIP meta data into the aip descriptor. Three techMD
     * sections will be created using ID's: TMD1, TMD2, TMD3.
     *
     * @param aip
     * @param ip
     * @throws FatalException
     */
    private static void mergeAipCoreMD(Document aip, InformationPackage ip)
            throws FatalException {

        String namespace = aip.getDocumentElement().getNamespaceURI();

        /* Intellectual Entity */
        IntEntity ie = ip.getIntEnt();

        /* Core amdSec */
        Element amdSec = (Element) XPaths.selectSingleNode(aip, "/mets:mets/mets:amdSec[@ID='AMD-AIP-CORE']");

        Element techMD;
        Element dpMD;
        Element mdWrap;
        Element xmlData;

        /* Relationships */
        amdSec.appendChild(aip.createComment("Relationships"));
        Element relXML = Relationships.getInstance().toXML().getDocumentElement();
        techMD = (Element) amdSec.appendChild(aip.createElementNS(namespace, "techMD"));
        techMD.setAttribute("ID", "TMD-CORE-RELATIONSHIPS");
        mdWrap = (Element) techMD.appendChild(aip.createElementNS(namespace, "mdWrap"));
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("OTHERMDTYPE", "DAITSS");
        xmlData = (Element) mdWrap.appendChild(aip.createElementNS(namespace, "xmlData"));
        xmlData.appendChild(aip.importNode(relXML, true));

        /* Global files */
        Element de = ip.toXML().getDocumentElement();
        NodeList childNodes = de.getChildNodes();
        boolean hasGlobalFiles = false;
        for(int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                        hasGlobalFiles = true;
                }
        }
        if (hasGlobalFiles) {
                amdSec.appendChild(aip.createComment("Global Files"));
                Element globalXML = de;
                techMD = (Element) amdSec.appendChild(aip.createElementNS(namespace, "techMD"));
                techMD.setAttribute("ID", "TMD-CORE-GLOBALFIES");
                mdWrap = (Element) techMD.appendChild(aip.createElementNS(namespace, "mdWrap"));
                mdWrap.setAttribute("MDTYPE", "OTHER");
                mdWrap.setAttribute("OTHERMDTYPE", "DAITSS");
                xmlData = (Element) mdWrap.appendChild(aip.createElementNS(namespace, "xmlData"));
                xmlData.appendChild(aip.importNode(globalXML, true));
        }
        /* Intellectual Entity */
        amdSec.appendChild(aip.createComment("Intellectual Entity"));
        Element ieXML = ie.toXML().getDocumentElement();
        techMD = (Element) amdSec.appendChild(aip.createElementNS(namespace, "techMD"));
        techMD.setAttribute("ID", "TMD-CORE-INTELLECTUAL-ENTITY");
        mdWrap = (Element) techMD.appendChild(aip.createElementNS(namespace, "mdWrap"));
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("OTHERMDTYPE", "DAITSS");
        xmlData = (Element) mdWrap.appendChild(aip.createElementNS(namespace, "xmlData"));
        xmlData.appendChild(aip.importNode(ieXML, true));

        /* Events */
        amdSec.appendChild(aip.createComment("Events"));
        Element eventXML = Events.getInstance().toXML().getDocumentElement();
        dpMD = (Element) amdSec.appendChild(aip.createElementNS(namespace, "digiprovMD"));
        dpMD.setAttribute("ID", "DP-CORE-EVENTS");
        mdWrap = (Element) dpMD.appendChild(aip.createElementNS(namespace, "mdWrap"));
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("OTHERMDTYPE", "DAITSS");
        xmlData = (Element) mdWrap.appendChild(aip.createElementNS(namespace, "xmlData"));
        xmlData.appendChild(aip.importNode(eventXML, true));
    }

    /**
     * @param aip
     * @param sip
     * @param dfl
     * @param idMap
     * @throws FatalException
     */
    private static void mergeFiles(Document aip, Document sip, Hashtable<String, DataFile> dfl, Map<String, String> idMap)
            throws FatalException {

        // namespace
        String namespace = aip.getDocumentElement().getNamespaceURI();

        /*
         * Copy over the entire file section and replace each file with a file
         * based on the associated DataFile.
         */
        // File Section to copy.
        Node sipFileSec = XPaths.selectSingleNode(sip, "/mets:mets/mets:fileSec");
        // File Section to replace.
        Node tempFileSec = XPaths.selectSingleNode(aip, "/mets:mets/mets:fileSec");
        // Parent of the file section to replace.
        Node fsParent = tempFileSec.getParentNode();
        // Import the File Section
        Node fileSec = aip.importNode(sipFileSec, true);
        // Replace the temp file section with the sip's file section
        fsParent.replaceChild(fileSec, tempFileSec);

        /*
         * Construct an mapping from package relative paths to absolute paths
         */
        Map<String, String> pathMap = new Hashtable<String, String>();
        for(DataFile df : dfl.values()) {

            // Absolute path to the aip file
            String absFilePath = df.getPath();

            // Package path
            String packagePath = df.getIp().getWorkingPath();

            // resovle the path
            int index = packagePath.length();
            String relFilePath = absFilePath.substring(index);
            pathMap.put(relFilePath, absFilePath);
        }

        // Where to put the new techMD.
        Element aipFileAmdSec = (Element) XPaths.selectSingleNode(aip, "//mets:amdSec[@ID='AMD-AIP-FILE']");

        /*
         * Construct a file element based on the respective data file for each
         * file in sip descriptor.
         */
        // sip file elements to aip file elements
        Hashtable<Element, Element> fileMap = new Hashtable<Element, Element>();
        // list of all the .toXML() documents.
        Vector<Element> mdVec = new Vector<Element>();
        // list of all the file elements to remove
        Vector<Element> killVec = new Vector<Element>();
        // list of all the datafiles that were used
        Vector<DataFile> usedVec = new Vector<DataFile>();

        NodeList files = XPaths.selectNodeList(fileSec, "//mets:file");
        for (int i = 0; i < files.getLength(); i++) {
            Element oldFile = (Element) files.item(i);

            /* Get an associated data file */
            Node hrefNode = XPaths.selectSingleNode(oldFile, "mets:FLocat/@xlink:href");
            String oldFilePath = hrefNode.getNodeValue();

            String newFilePath = pathMap.get(oldFilePath);
            if (newFilePath != null) {
                /* Get the data file by the absolue path */
                DataFile dataFile = dfl.get(newFilePath);

                /* Add the daitss xml for this file to a new techMD. */
                Document daitssDoc = dataFile.toXML();
                Element daitssXML = (Element) aip.importNode(daitssDoc.getDocumentElement(), true);

                /* Get an id association for the techMD section. */
                String tmdID = nextID("TMD", idMap);
                idMap.put(tmdID, tmdID);

                /* Build the techMD section. */
                Element techMD = aip.createElementNS(namespace, "techMD");
                techMD.setAttribute("ID", tmdID);
                Element mdWrap = (Element) techMD.appendChild(aip.createElementNS(namespace, "mdWrap"));
                mdWrap.setAttribute("MDTYPE", "OTHER");
                Element xmlData = (Element) mdWrap.appendChild(aip.createElementNS(namespace, "xmlData"));
                xmlData.appendChild(daitssXML);

                /* Add the daitss xml into the md list */
                mdVec.add(techMD);

                /* Get a new id association for the file */
                String oldID = oldFile.getAttribute("ID");
                String newID = nextID("FID", idMap);
                idMap.put(oldID, newID);

                /* Generate a new file element from the datafile and the id */
                Element newFile = aip.createElementNS(namespace, "file");
                newFile.setAttribute("ID", newID);
                newFile.setAttribute("MIMETYPE", dataFile.getMediaType());
                newFile.setAttribute("SIZE", Long.toString(dataFile.getSize()));
                if (!dataFile.getCreateDate().equals("0000-00-00 00:00:00")) {
                    newFile.setAttribute("CREATED", dataFile.getCreateDate().replaceFirst(" ", "T"));
                }
                String mdType = ArchiveProperties.getInstance().getArchProperty("MD_TYPE_1");
                newFile.setAttribute("CHECKSUMTYPE", mdType.toUpperCase());
                newFile.setAttribute("CHECKSUM", dataFile.getMesgDigestValue(mdType));
                if (oldFile.getAttribute("ADMID") != null && !oldFile.getAttribute("ADMID").trim().equals("")) {
                    newFile.setAttribute("ADMID", oldFile.getAttribute("ADMID") + " " + tmdID);
                } else {
                    newFile.setAttribute("ADMID", tmdID);
                }
                if (oldFile.getAttribute("DMDID") != null
                        && !oldFile.getAttribute("DMDID").trim().equals("")) {
                    newFile.setAttribute("DMDID", oldFile.getAttribute("DMDID"));
                }

                Element fLocat = (Element) newFile.appendChild(aip.createElementNS(namespace, "FLocat"));
                fLocat.setAttribute("LOCTYPE", "URL");
                String xlinkNS = ArchiveProperties.getInstance().getArchProperty("DES_XLINK_NS_URI");
                fLocat.setAttributeNS(xlinkNS, "href", dataFile.getPackagePath());

                /* Associate the old file element with the new file element */
                fileMap.put(oldFile, newFile);
                usedVec.add(dataFile);
            } else {
                /* Put the file element on a removal list */
                killVec.add(oldFile);
            }
        }

        /* Insert all the techmd Elements made from .toXML */
        for (Iterator iter = mdVec.iterator(); iter.hasNext();) {
            Element techMD = (Element) iter.next();
            aipFileAmdSec.appendChild(techMD);
        }

        /* Replace all the sip file elements with the new generated ones */
        for (Iterator iter = fileMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            Element oldFile = (Element) entry.getKey();
            Element newFile = (Element) entry.getValue();
            Node fParent = oldFile.getParentNode();
            fParent.replaceChild(newFile, oldFile);
        }

        /* Remove all the file elements that don't belong in the AIP */
        for (Iterator iter = killVec.iterator(); iter.hasNext();) {
            Node kFile = (Node) iter.next();
            Node kParent = kFile.getParentNode();
            kParent.removeChild(kFile);
        }

        /*
         * Add files that didnt make it in a new fileGrp. eg the descriptor and
         * any normalized files.
         */

        /* Get a list of the orphan files */

        // all the data files.
        Vector<DataFile> allFiles = new Vector<DataFile>(dfl.values());

        // find the set difference, all files that are not used
        Vector<DataFile> orphanFiles = new Vector<DataFile>();
        for(DataFile df : allFiles) {
            if (!usedVec.contains(df)) {
                orphanFiles.add(df);
            }
        }

        /* Make a second fileGrp for orphan files */
        fileSec.appendChild(aip.createComment("file group for files not described by the sip descriptor"));
        Element orphanGrp = aip.createElementNS(namespace, "fileGrp");
        orphanGrp.setAttribute("ID", "UNDESCRIBED-FILES");
        fileSec.appendChild(orphanGrp);

        for (Iterator iter = orphanFiles.iterator(); iter.hasNext();) {
            DataFile dataFile = (DataFile) iter.next();
            /* Add the meta data to the amd section */

            // Get the daitss xml for this file to a new techMD.
            Document daitssDoc = dataFile.toXML();
            Element daitssXML = (Element) aip.importNode(daitssDoc.getDocumentElement(), true);

            // Make a new id association for the techMD section.
            String tmdID = nextID("TMD", idMap);
            idMap.put(tmdID, tmdID);

            // Build the techMD section.
            makeDfMd(aipFileAmdSec, tmdID, daitssXML);

            // Make a new id association for the techMD section.
            String fID = nextID("FID", idMap);
            idMap.put("n" + fID, fID);

            // build a file element based on a data file
            makeDfFile(orphanGrp, fID, tmdID, dataFile);

        }
    }

        private static void mergeNamespacLoactions(Document aip, Document sip) throws FatalException {
                Element sDocElement = sip.getDocumentElement();
                Element aDocElement = aip.getDocumentElement();


                // get the system schema locations
                ArchiveProperties p = ArchiveProperties.getInstance();

                Map<String, String> systemLocs = new Hashtable<String, String>();
                systemLocs.putAll(parseSchemaLocation(p.getProperty("DES_DAITSS_SCHEMA")));
                systemLocs.putAll(parseSchemaLocation(p.getProperty("DES_RIGHTSMD_SCHEMA")));
                systemLocs.putAll(parseSchemaLocation(p.getProperty("DES_METS_SCHEMA")));
                systemLocs.putAll(parseSchemaLocation(p.getProperty("DES_DC_SCHEMA")));
                systemLocs.putAll(parseSchemaLocation(p.getProperty("DES_TECHMD_SCHEMA")));
                systemLocs.putAll(parseSchemaLocation(p.getProperty("DES_PALMM_SCHEMA")));

                // get the aip's schema locations
                Map<String, String> aipLocs = parseSchemaLocation(aDocElement.getAttribute("xsi:schemaLocation"));

                // get the sip's schema locations
                Map<String, String> sipLocs = parseSchemaLocation(sDocElement.getAttribute("xsi:schemaLocation"));

                // merge them into one big map
                aipLocs.putAll(sipLocs);
                aipLocs.putAll(systemLocs);

                // merge the namespace prefixes into the aip
                aDocElement.setAttribute("xmlns:xsi", p.getArchProperty("DES_SCHEMAINSTANCE_NS_URI"));

                // merge schemalocations into the aip
                aDocElement.setAttribute("xsi:schemaLocation", renderSchemaLocation(aipLocs));
        }

    /**
     * @param aip
     * @param sip
     * @param idMap
     * @throws FatalException
     */
    private static void mergeSipMd(Document aip, Document sip, Map<String, String> idMap)
            throws FatalException {
        /* Merge //dmdSec */

        // Select all the dmdSec from the sip
        NodeList dmdSecs = XPaths.selectNodeList(sip, "//mets:dmdSec");

        // Select the place to put the dmdSections
        //Node firstAmdSec = XPaths.selectSingleNode(aip, "//mets:amdSec");
        Node target = XPaths.selectSingleNode(aip, "/mets:mets/mets:metsHdr").getNextSibling();

        // for each dmdSec:
        for (int i = 0; i < dmdSecs.getLength(); i++) {
            Element dmdSec = (Element) dmdSecs.item(i);

            String oldID = dmdSec.getAttribute("ID");

            // Get a new @ID for this dmdSec
            String newID = nextID("DMD", idMap);

            // associate the old @ID with the new @ID
            idMap.put(oldID, newID);

            // insert the dmdSec into the aip descriptor
            Node parent = target.getParentNode();
            parent.insertBefore(aip.createComment("descriptive meta data from the SIP descriptor"), target);
            dmdSec = (Element) parent.insertBefore(aip.importNode(dmdSec, true), target);

            // change the @ID to the new @ID
            dmdSec.setAttribute("ID", newID);
        }

        /* Merge //amdSec/* */

        // Select all the place to put the mdSectypes
        Node amdAipMd = XPaths.selectSingleNode(aip, "/mets:mets/mets:amdSec[@ID='AMD-SIP-MD']");

        // Select all the mdSecTypes from the sip
        NodeList mdSecs = XPaths.selectNodeList(sip, "//mets:amdSec/mets:*");
        for (int i = 0; i < mdSecs.getLength(); i++) {
            if (mdSecs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element mdSec = (Element) mdSecs.item(i);

                // Get the old @ID
                String oldID = mdSec.getAttribute("ID");

                // Get a new @ID for this mdSec
                String newID;
                if (oldID.startsWith("TMD")) {
                    newID = nextID("TMD", idMap);
                } else if (oldID.startsWith("SMD")) {
                    newID = nextID("SMD", idMap);
                } else if (oldID.startsWith("RMD")) {
                    newID = nextID("RMD", idMap);
                } else if (oldID.startsWith("DP")) {
                        newID = nextID("DP", idMap);
                } else {
                    newID = nextID("MD", idMap);
                }

                // associate the old @ID with the new @ID
                idMap.put(oldID, newID);

                // insert the mdSec into the aip descriptor
                mdSec = (Element) aip.importNode(mdSec, true);
                amdAipMd.appendChild(mdSec);
            }
        }

        /* Merge //amdSec */
        // Get all amdSecs
        NodeList amdSecs = XPaths.selectNodeList(sip, "//mets:amdSec");
        for (int i = 0; i < amdSecs.getLength(); i++) {
            Element amdSec = (Element) amdSecs.item(i);

            // Get the ID
            String oldID = amdSec.getAttribute("ID");
            if (oldID.trim().length() > 0) {
                    // Get the IDs of the children, and resolve to theier new IDs
                    String childrenID = "";
                    NodeList idNodes = XPaths.selectNodeList(amdSec, "./*/@ID");
                    for (int j = 0; j < idNodes.getLength(); j++) {
                        Node oldChildIdNode = idNodes.item(j);
                        String oldChildID = oldChildIdNode.getNodeValue();
                        String newChildID = idMap.get(oldChildID);
                        childrenID += newChildID + " ";
                    }
                    // associate the old @ID with the new @IDs
                    idMap.put(oldID, childrenID.trim());
            }
        }
    }

    /**
     * @param aip
     * @param sip
     * @param idMap
     * @throws FatalException
     */
    private static void mergeStructMap(Document aip, Document sip, Map<String, String> idMap, String sipDescPath) throws FatalException {
        // make r0
        makeR0StructMap(aip, sip, idMap, sipDescPath);

        // make rC
        Map <String, String> successionMap = makeSuccessionMap(aip);
        if (successionMap.size() > 0) {
                makeRCStructMap(aip, successionMap);
        }

    }

        /**
         * @return      a mapping of file ids to file ids that reference migrated versions of the files referenced in domain.
         * @throws FatalException
         */
        private static Map<String, String> makeSuccessionMap(Document aip) throws FatalException {

                Element mets = aip.getDocumentElement();

                // map dfid -> fid for all files
                Map<String, String> daitssToMetsId = new Hashtable<String, String>();

                // get all mets:files with an @ID
                NodeList files = XPaths.selectNodeList(mets, "/mets:mets/mets:fileSec//mets:file[@ID]");
                for (int i = 0; i < files.getLength(); i++) {
                        Element file = (Element) files.item(i);

                        String fileId = file.getAttribute("ID");
                        String dfid = null;

                        // get the tmd section
                        String tmdId = null;
                        for(String id : file.getAttribute("ADMID").split("\\s+") ) {
                                if (id.matches("TMD\\d+")) {
                                        tmdId = id;
                                }
                        }
                        // get the DFID of the file based on the daitss metadata
                        String xpath = String.format("//mets:amdSec[@ID='AMD-AIP-FILE']/mets:techMD[@ID='%s']//daitss:DATA_FILE/daitss:DFID/text()", tmdId);
                        Text textNode = (Text) XPaths.selectSingleNode(mets, xpath);
                        dfid = textNode.getData();
                        daitssToMetsId.put(dfid, fileId);
                }

                // get (DFID_1, DFID_2) for all migrations
                NodeList rels = XPaths.selectNodeList(mets, "//daitss:RELATIONSHIP[daitss:REL_TYPE/text()='MIGRATED_TO']");
                Map<String, String> migration = new Hashtable<String, String>(rels.getLength());
                for (int i = 0; i < rels.getLength(); i++) {
                        Element rel = (Element) rels.item(i);

                        Text dfid1Text = (Text) XPaths.selectSingleNode(rel, "daitss:DFID_1/text()");
                        String dfidFrom = dfid1Text.getData();

                        Text dfid2Text = (Text) XPaths.selectSingleNode(rel, "daitss:DFID_2/text()");
                        String dfidTo = dfid2Text.getData();


                        migration.put(dfidFrom, dfidTo);
                }


                // resolve a terminal mapping dfid_1 -> dfid_2
                Map<String, String> terminalMigration = new Hashtable<String, String>();

                // get intersection of successors and predicessors
                Set<String> predicessors = migration.keySet();
                Collection<String> successors = migration.values();
                Set<String> intermediates = new HashSet<String>();
                intermediates.addAll(predicessors);
                boolean terminal = !intermediates.retainAll(successors);

                // terminate it
                if (terminal) {
                        terminalMigration = migration;
                } else {
                        Hashtable<String, String> tempMigration = new Hashtable<String, String>(migration);
                        for (String i : intermediates) {

                                // get the key for this, and remove mapping: k -> i
                                String from = null;
                                for (String f : tempMigration.keySet()) {
                                        if (tempMigration.get(f).equals(i)) {
                                                from = tempMigration.remove(f);
                                                break;
                                        }
                                }

                                // get the value for this and remove mapping: i -> v
                                String to = tempMigration.remove(i);

                                // map from key to value: k -> v
                                tempMigration.put(from, to);
                        }

                        terminalMigration = tempMigration;
                }


                // map dfids to fids
                Map<String, String> metsMigration = new Hashtable<String, String>();
                for (String dfidFrom : terminalMigration.keySet()) {
                        String dfidTo = terminalMigration.get(dfidFrom);

                        String fidFrom = daitssToMetsId.get(dfidFrom);
                        String fidTo = daitssToMetsId.get(dfidTo);

                        metsMigration.put(fidFrom, fidTo);
                }

                return metsMigration;
        }

        /**
         * Initial structMap (R0)
         *
         * this describes the structure of the submitted package
         * exactly how it was submitted, but with the first file
         * being the sip descriptor.
         *
         * @param aip
         * @throws FatalException
         */
        private static void makeR0StructMap(Document aip, Document sip, Map<String, String> idMap, String sipDescPath) throws FatalException {
                Element mets = aip.getDocumentElement();
                String namespace = mets.getNamespaceURI();

        // Copy the sip's structMap/div over into the structMap
                Element r0 = (Element) XPaths.selectSingleNode(aip, "/mets:mets/mets:structMap");
                r0.setAttribute("ID", "SMR0");
        Element sipDiv = (Element) XPaths.selectSingleNode(sip, "/mets:mets/mets:structMap/mets:div");
        Element aipDiv = (Element) aip.importNode(sipDiv, true);
        Element tempDiv = (Element) XPaths.selectSingleNode(aip, "/mets:mets/mets:structMap/mets:div");
        r0.replaceChild(aipDiv, tempDiv);

        // Update the fileids from the sip the aip
        NodeList fidNodes = XPaths.selectNodeList(aipDiv, "//mets:fptr/@FILEID");
        for (int i = 0; i < fidNodes.getLength(); i++) {
                Attr fileId = (Attr) fidNodes.item(i);
                String oldID = fileId.getValue();
            String newID = idMap.get(oldID);
            if (newID == null) {
                fileId.getOwnerElement().removeAttributeNode(fileId);
            } else {
                fileId.setValue(newID);
            }
        }

                // add the sip descriptor as the first element
        Attr id = (Attr) XPaths.selectSingleNode(aip, "/mets:mets/mets:fileSec//mets:file[contains('" + sipDescPath + "',mets:FLocat/@xlink:href)]/@ID");
        Element sipDescFptr = aip.createElementNS(namespace, "fptr");
        sipDescFptr.setAttribute("FILEID", id.getValue());
        aipDiv.insertBefore(sipDescFptr, aipDiv.getFirstChild());
        }

        /**
         * Current structMap (RC)
         *
         * If migrations of any files in R0 exist they replace thier predecessor, otherwise identical to R0.
         * If there are no migrations RC will not exist
         *
         * @param namespace
         * @param r0    the structMap that represents r0
         * @throws FatalException
         */
        private static void makeRCStructMap(Document aip, Map<String, String>  successionMap) throws FatalException {

                Element mets = aip.getDocumentElement();

        // make a new struct map for RC, initially the same as r0
        Element r0 = (Element) XPaths.selectSingleNode(mets, "/mets:mets/mets:structMap[@ID='SMR0']");
        Element rC = (Element) r0.cloneNode(true);
        rC.setAttribute("ID", "SMRC");

        // append it after r0
        Element parent = (Element) r0.getParentNode();
        Node nextSibling = r0.getNextSibling();
        parent.insertBefore(rC, nextSibling);

        // map file ids over migration successions
        NodeList fileIds = XPaths.selectNodeList(mets, "/mets:mets/mets:structMap[@ID='SMRC']//mets:fptr/@FILEID");
        for(int i = 0; i < fileIds.getLength(); i++) {
                Attr fileId = (Attr) fileIds.item(i);
                String successorId = successionMap.get(fileId.getValue());
                if(successorId != null) {
                        fileId.setValue(successorId);
                }
        }

        }

    private static String nextID(String base, Map idMap) {
        int num = 1;
        String newID;
        do {
            newID = base + Integer.toString(num++);
        } while (idMap.containsValue(newID));
        return newID;
    }

        private static Map<String, String> parseSchemaLocation(String schemaLocation) {

                StringTokenizer st = new StringTokenizer(schemaLocation.trim());
                List<String> tokens = new Vector<String>();
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken().trim());
            }

                Map<String, String> locationTable = new Hashtable<String, String>();
                for(int i = 0; i < tokens.size(); i += 2) {
                        String key = tokens.get(i);
                        String value = tokens.get(i + 1);
                        locationTable.put(key, value);
                }
                return locationTable;
        }

        private static String renderSchemaLocation(Map schemaMap) {
                String data = "";
                Set keys = schemaMap.keySet();
                for (Iterator iter = keys.iterator(); iter.hasNext();) {
                        String key = (String) iter.next();
                        String value = (String) schemaMap.get(key);
                        data += key + " " + value + " ";
                }

                data = data.trim();

                return data;
        }

    /**
     * @param os
     * @param doc
     * @throws IOException
     * @throws FatalException
     */
    public static void serializeXml(OutputStream os, Document doc) throws IOException, FatalException {

        XmlObject xo;
	try {
	    xo = XmlObject.Factory.parse(doc);
	    XmlOptions ops = new XmlOptions();
	    ops.setSavePrettyPrint();
	    Map<String, String> prefixMap = new Hashtable<String, String>();
	    ArchiveProperties p = ArchiveProperties.getInstance();

	    // XML
	    prefixMap.put(p.getArchProperty("NS_XINCLUDE"), XPaths.PREFIX_XINCLUDE);
	    prefixMap.put(p.getArchProperty("NS_XMLSCHEMA"), XPaths.PREFIX_XMLSCHEMA);
	    prefixMap.put(p.getArchProperty("NS_XSL"), XPaths.PREFIX_XMLSTYLESHEET);
	    prefixMap.put(p.getArchProperty("NS_XLINK"), XPaths.PREFIX_XLINK);

	    // METS
	    prefixMap.put(p.getArchProperty("NS_METS"), XPaths.PREFIX_METS);
	    prefixMap.put(p.getArchProperty("DES_TECHMD_NS_URI"), XPaths.PREFIX_TECHMD);
	    prefixMap.put(p.getArchProperty("DES_RIGHTSMD_NS_URI"), XPaths.PREFIX_RIGHTSMD);
	    prefixMap.put(p.getArchProperty("DES_PALMM_NS_URI"), XPaths.PREFIX_PALMM);
	    prefixMap.put(p.getArchProperty("DES_DC_NS_URI"), XPaths.PREFIX_DUBLINCORE);
	    prefixMap.put(p.getArchProperty("NS_DAITSS"), XPaths.PREFIX_DAITSS);

	    ops.setSaveSuggestedPrefixes(prefixMap);
	    ops.setSaveAggresiveNamespaces();
	    ops.setSaveNamespacesFirst();

	    xo.save(os, ops);
	} catch (XmlException e) {
	    StackTraceElement ste = new Throwable().getStackTrace()[0];
	    Informer.getInstance().fail(ste.getClassName(), ste.getMethodName(), "cannot serialize xml", "serializing xml", e);
	}

    }

    /**
     * @param aip
     * @throws FatalException
     */
    private static void stripUnrefMd(Document aip) throws FatalException {
        Vector<Element> killVec;

        /* List of all meta data ID references */

        /*
         * elements with dmd references:
         * mets:file
         * mets:div
         */
        String dmdPath =
                "/mets:mets/mets:fileSec//mets:fileGrp/mets:file/@DMDID|" +
                "/mets:mets/mets:structMap//mets:div/@DMDID";

        /*
         * elements with amd references:
         * mets:file
         * mets:div
         * mets:fileGrp
         * mets:area
         * mets:behavior
         * mets:dmdSec
         * mets:techMD
         * mets:rightsMD
         * mets:sourceMD
         * mets:digiprovMD
         */
        String amdPath =
                "/mets:mets/mets:fileSec//mets:fileGrp/mets:file/@ADMID|" +
                "/mets:mets/mets:structMap//mets:div/@ADMID|" +
                "/mets:mets/mets:fileSec//mets:fileGrp/@ADMID|" +
                "/mets:mets/mets:structMap/mets:div/mets:ftpr//mets:area/@ADMID|" +
                "/mets:mets/mets:behaviorSec/mets:behavior/@ADMID|" +
                "/mets:mets/mets:dmdSec/@ADMID|" +
                "/mets:mets/mets:amdSec/mets:techMD/@ADMID|" +
                "/mets:mets/mets:amdSec/mets:rightsMD/@ADMID|" +
                "/mets:mets/mets:amdSec/mets:sourceMD/@ADMID|" +
                "/mets:mets/mets:amdSec/mets:digiprovMD/@ADMID";

        NodeList ids = XPaths.selectNodeList(aip, amdPath + "|" + dmdPath);
        Vector<String> idVec = new Vector<String>();
        for (int i = 0; i < ids.getLength(); i++) {
            String id = ids.item(i).getNodeValue().trim();
            idVec.addAll(Arrays.asList(id.split("\\s+")));
        }

        /* sip md section */
        Node amdAipMd = XPaths.selectSingleNode(aip, "/mets:mets/mets:amdSec[@ID='AMD-SIP-MD']");

        /* Identify all the unreferenced meta data sections */
        NodeList mdSecs = XPaths.selectNodeList(amdAipMd, "./mets:techMD|" +
                        "./mets:rightsMD|" +
                        "./mets:sourceMD|" +
                        "./mets:digiprovMD");
        killVec = new Vector<Element>();
        for (int i = 0; i < mdSecs.getLength(); i++) {
            Element mdSec = (Element) mdSecs.item(i);
            String id = mdSec.getAttribute("ID");
            if (!idVec.contains(id)) {
                killVec.add(mdSec);
            }
        }
        for (Iterator iter = killVec.iterator(); iter.hasNext();) {
            Element mdSec = (Element) iter.next();
            Node parent = mdSec.getParentNode();
            parent.removeChild(mdSec);
        }

        /* Strip all fptrs that point to non existant files */

        /* All file IDs */
        NodeList fids = XPaths.selectNodeList(aip, "/mets:mets/mets:fileSec/mets:fileGrp[position() != last()]//mets:file/@ID");
        Vector<String> fidVec = new Vector<String>();
        for (int i = 0; i < fids.getLength(); i++) {
            String fid = fids.item(i).getNodeValue().trim();
            fidVec.addAll(Arrays.asList(fid.split("\\s+")));
        }

        /* Strip all divs with no children */

        /* Select all empty divs */
        killVec = new Vector<Element>();
        NodeList nodeList = XPaths.selectNodeList(aip, "/mets:mets/mets:structMap//mets:div[count(*)=0]");
        for (int i = 0; i < nodeList.getLength(); i++) {
                Element k = (Element) nodeList.item(i);
                killVec.add(k);
        }
        for (Iterator iter = killVec.iterator(); iter.hasNext();) {
                        Node node = (Node) iter.next();
            Node parent = node.getParentNode();
            parent.removeChild(node);
                }


        /* Remove all the irrelevant Relationships and Events */
        // Get all the DFIDs
        NodeList dfids = XPaths.selectNodeList(aip, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData//daitss:DATA_FILE/daitss:DFID/text()|" +
                        "/mets:mets/mets:amdSec/*/mets:mdWrap/mets:xmlData//daitss:DATA_FILE/daitss:DFID/text()");
        String namePath = "";
        for (int i = 0; i < dfids.getLength(); i++) {
            String dfid = dfids.item(i).getNodeValue();
            namePath += " and text()!='" + dfid + "'";

        }

        String xpath = "//daitss:RELATIONSHIP/*[(local-name()='DFID_1' or local-name()='DFID_2')" + namePath + "]/parent::*|"
                + "//daitss:EVENT/*[(local-name()='OID' or local-name()='REL_OID') " + namePath + " ]/parent::*";
        killVec = new Vector<Element>();
        nodeList = XPaths.selectNodeList(aip, xpath);
        for (int i = 0; i < nodeList.getLength(); i++) {
                Element k = (Element) nodeList.item(i);
                killVec.add(k);
        }
        for (Iterator iter = killVec.iterator(); iter.hasNext();) {
                        Node node = (Node) iter.next();
            Node parent = node.getParentNode();
            parent.removeChild(node);
                }

        /* Remove all the empty fileGrps */
        killVec = new Vector<Element>();
        nodeList = XPaths.selectNodeList(aip, "/mets:mets/mets:fileSec//mets:fileGrp[count(*)=0]");
        for (int i = 0; i < nodeList.getLength(); i++) {
                Element k = (Element) nodeList.item(i);
                killVec.add(k);
        }
        for (Iterator iter = killVec.iterator(); iter.hasNext();) {
                        Node node = (Node) iter.next();
            Node parent = node.getParentNode();
            parent.removeChild(node);
        }

        /* Strip empty events, relationships */
        /* core amdSec */
        Element amdAipCore = (Element) XPaths.selectSingleNode(aip, "/mets:mets/mets:amdSec[@ID='AMD-AIP-CORE']");

        /* events */
        Element ev = (Element) XPaths.selectSingleNode(amdAipCore, "./mets:digiprovMD[@ID='DP-CORE-EVENTS']");
        int eventCount = XPaths.selectNodeList(ev, "mets:mdWrap/mets:xmlData/daitss:daitss/*").getLength();
        if (eventCount == 0) {
            amdAipCore.removeChild(ev);
        }
        /* relationships */
        Element rl = (Element) XPaths.selectSingleNode(amdAipCore, "./mets:techMD[@ID='TMD-CORE-RELATIONSHIPS']");
        int relCount = XPaths.selectNodeList(rl, "mets:mdWrap/mets:xmlData/daitss:daitss/*").getLength();
        if (relCount == 0) {
            amdAipCore.removeChild(rl);
        }
    }

    /**
     * @param aip
     * @param idMap
     * @throws FatalException
     */
    private static void updateRefs(Document aip, Map idMap)
            throws FatalException {
        /* Update each mdID ref with the values in the idMap */
        NodeList idNodes = XPaths.selectNodeList(aip, "//@ADMID|"
                + "//@DMDID|"
                + "/mets:mets/mets:fileSec/mets:fileGrp[position() != last()]//mets:fptr/@FILEID");
        for (int i = 0; i < idNodes.getLength(); i++) {
            Node idNode = idNodes.item(i);

            // List of ids.
            String oldIDs = idNode.getNodeValue();

            // List of resolved ids.
            String newIDs = "";

            // Resolve the ids.
            String[] idArray = oldIDs.split("\\s+");
            for (int j = 0; j < idArray.length; j++) {
                String oldId = idArray[j];
                String newId = (String) (idMap.get(oldId) != null ? idMap.get(oldId)
                        : idMap.get("n" + oldId));
                newIDs += newId + " ";
            }

            // Replace the old id with the new id.
            idNode.setNodeValue(newIDs.trim());
        }
    }

    /**
     * @param d
     * @throws FatalException
     * @throws PackageException
     * @return boolean primitive
     */
    public static boolean validate(Document d) throws FatalException, PackageException {


        ByteArrayOutputStream os = new ByteArrayOutputStream();

        XMLSerializer ser = new XMLSerializer();
        ser.setOutputByteStream(os);
        ser.setNamespaces(true);
        try {
            ser.serialize(d);
        } catch (IOException e1) {
            Informer.getInstance().fail(e1.getMessage(), "xml serialization", e1);
        }

        byte[] xmlBytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(xmlBytes);

        // validator
        Validator validator;
        Checker checker;
        try {
                WebCacheResolver resolver = WebCacheUtils.getResolver();
            validator = new Validator(resolver);
            checker = validator.validate(is);
        } catch (ParserConfigurationException e) {
            throw new FatalException(e);
        } catch (IOException e) {
            throw new FatalException(e);
        } catch (SAXException e) {
            throw new FatalException(e);
        }

        for (Iterator iter = checker.getFatals().iterator(); iter.hasNext();) {
            SAXParseException error = (SAXParseException) iter.next();
            Informer.getInstance().error(error.getMessage(), "XML Validation Fatal Error", error);
        }

        for (Iterator iter = checker.getErrors().iterator(); iter.hasNext();) {
            SAXParseException error = (SAXParseException) iter.next();
            Informer.getInstance().error(error.getMessage(), "XML Validation Error", error);
        }

        for (Iterator iter = checker.getWarnings().iterator(); iter.hasNext();) {
            SAXParseException warning = (SAXParseException) iter.next();
            Informer.getInstance().warning(warning.getMessage(), "XML Validation Warning");
        }

        boolean valid = checker.getErrors().isEmpty() && checker.getFatals().isEmpty();

        return valid;
    }

    /**
     * A hashtable of partially-populated DataFile objects (the 'values') which
     * is keyed by absolute paths to files. Each of these objects contains the
     * information from a METS file that was relevant to a particular file.
     */
    private Hashtable dflLite;

    /**
     * The intellectual entity described in this descriptor file.
     */
    private IntEntity intEnt = null;

    /**
     * The top level METS Bitstream in the specified file.
     */
    private METSDocument rootMETSDocument;

    /**
     * Creates a METSDesc from an existing descriptor file
     *
     * @param filePath
     *            path to the descriptor file.
     * @param ip
     *            the information package that the descriptor file belongs to.
     * @throws FatalException
     * @throws PackageException
     *             when the descriptor is not valid.
     */
    public METSDescriptor(String filePath, InformationPackage ip)
            throws FatalException, PackageException {

        // Call superclass contructor
        super(filePath, ip);

        String methodName = "METSDescriptor(String, InformationPackage)";

        // Check if filePath is in the path of packagePath
        if (!filePath.startsWith(ip.getPackagePath())) {
            Informer.getInstance().fail(this, methodName, filePath
                    + " is not in the path of " + ip.getPackagePath(), "File: "
                    + this.getPath() + " IP path: " + ip.getPackagePath(), new FatalException(
                    "File must be in info. package's path"));
        }

        // Load the root bitstream
        rootMETSDocument = new METSDocument(this);
        File metsFile = new File(filePath);
        rootMETSDocument.parseFile(metsFile);

        // Validate
        if (isValid() != MLDocument.VALID_TRUE && (ip instanceof SIP) ) {
            Informer.getInstance().error(this, methodName, "could not validate "
                    + filePath, "File: " + this.getPath(), new PackageException(
                    "Descriptors must be valid"));
        }
    }

    /**
     * @throws FatalException
     * @throws PackageException
     */
    public void extractContent() throws FatalException, PackageException {
                String methodName = "extractContent()";

                Informer.getInstance().info(this,
                                methodName,
                                "Extracting descriptor content",
                                this.getPath(),
                                false);

        // Initialize the lite-DataFile hashtable
        setDflLite(new Hashtable());

        // Get file paths
        int fileCount = rootMETSDocument.getFileIds().length;
        String fileIDs[] = rootMETSDocument.getFileIds();
        METSDocument[] fileMetaDatas = new METSDocument[fileCount];

        // Absolute parent path of files' relative paths
        File parentFile = new File(getPath()).getParentFile();
        for (int i = 0; i < fileIDs.length; i++) {

            // Current file id
            String fileID = fileIDs[i];

            // Path of the file represented by the file id
            String path = rootMETSDocument.getFilePathById(fileID);

                        Informer.getInstance().info(this,
                                        methodName,
                                        "Searching for file listed in descriptor",
                                        path,
                                        false);

            // Physical file represented by the path
            File sysFile = new File(parentFile, path);

            // Only process if the physical file exists
            if (sysFile.exists()) {

                                Informer.getInstance().info(this,
                                                methodName,
                                                "File found",
                                                path,
                                                false);

                // Extract the meta data associated with the file id
                METSDocument m = extractFileMetaData(fileID);
                fileMetaDatas[i] = m;
                String absPath = sysFile.getAbsolutePath();

                // If the path is already in the dfl
                if (dflLite.containsKey(absPath)) {
                    // get the old mets descriptor
                    METSDocument n = (METSDocument) dflLite.get(absPath);

                    // get the fileID(s) of the old file
                    String[] oldIDs = n.getFileIds();
                    // get the fileID of the new file
                    String[] newIDs = m.getFileIds();

                    // merge the file IDs
                    HashSet<String> idSet = new HashSet<String>();
                    idSet.addAll(Arrays.asList(oldIDs));
                    idSet.addAll(Arrays.asList(newIDs));
                    String[] mergedIDs = idSet.toArray(new String[0]);

                    // Generate a merged descriptor
                    METSDocument nm = resolveFileMetaDataConflicts(mergedIDs);

                    // Update it in the dlf Lite
                    dflLite.put(absPath, nm);

                } else {
                    // Insert it into the dfl Lite
                    dflLite.put(absPath, m);
                }
            } else {
                                Informer.getInstance().info(this,
                                                methodName,
                                                "File not found",
                                                path,
                                                false);
            }
        }
    }

    private METSDocument extractFileMetaData(String fileID)
            throws FatalException, PackageException {
        String methodName = "extractFileMetaData(String)";

        // Get meta data IDs
        MetsDocument root = (MetsDocument) rootMETSDocument.getDocument().copy();
        String[] ids = MetaDataResolver.resolve(root, fileID);

        // Get a DOM representation of the InitialDescriptor
        Document domDoc = (Document) rootMETSDocument.getDocument().newDomNode();
        Element domRoot = domDoc.getDocumentElement();

        // Reduce, but on on reingest
        if (!hasArchived) {
                reduce(domRoot, ids);
        }

        // Change MIMETYPE of mdWrap to application/xml
        NodeList wraps;
        wraps = XPaths.selectNodeList(domRoot, "//mets:mdWrap[@MIMETYPE='text/xml']");
        for (int i = 0; i < wraps.getLength(); i++) {
            Element mdWrap = (Element) wraps.item(i);
            mdWrap.setAttribute("MIMETYPE", "application/xml");
        }

        // Make a bitsream to return
        METSDocument METSDoc = new METSDocument(this);
        METSDoc.setDocumentElement(domRoot);
        return METSDoc;
    }

    /**
     * @return Hashtale object
     */
    public Hashtable getDflLite() {
        return dflLite;
    }

    /**
     * @return
     */
/*    public IntEntity getIntEnt() throws FatalException, PackageException{
        if (this.intEnt == null) {
            populateIntEntity();
        }
        return intEnt;
    }*/

    /**
     * @param apath
     *            an absolute path of a file
     * @return METSDocument relating to the file that apath represents
     */
    public METSDocument getMetsDoc(String apath) {
        METSDocument m = null;
        m = (METSDocument) dflLite.get(apath);
        return m;
    }

    /**
     * @return METSDocument object
     */
    public METSDocument getMetsDocument() {
        return rootMETSDocument;
    }

    /**
     * Creates a file or group of files that is the migrated version of this
     * file.
     *
     * @throws PackageException
     * @throws FatalException
     */
    public void migrate() throws PackageException, FatalException {
        // your code here
    }

    /**
     * Creates a file or group of files that is the normalized version of this
     * file.
     *
     * @throws PackageException
     * @throws FatalException
     */
    public void normalize() throws PackageException, FatalException {
        // your code here
    }

    /**
     * Takes an IntEntity and sets its members based on metadata contained in the
     * descriptor.
     *
     * @param intEnt
     * @throws FatalException
     * @throws PackageException
     */
    public void populateIntEntity(IntEntity intEnt) throws FatalException, PackageException {
        String methodName = "populateIntEntity(IntEntity)";

                //intEnt = new IntEntity();

                // Set packageName
                intEnt.setPackageName(this.getIp().getPackageName());

        /* Set account */
        String account = rootMETSDocument.getAccount();
        if(!account.equals("")) {
            intEnt.setAcccount(account);
        } else {
            Informer.getInstance().error(this,
                    methodName,
                    "Account not provided in descriptor",
                    "Descriptor file: " + getPath(),
                    new PackageException());
        }

        /* Set project with assertion */
        String project = rootMETSDocument.getProject();
        if (!project.equals("")) {
            intEnt.setProject(project);
        } else {
            Informer.getInstance().error(this,
                    methodName,
                    "Project not provided in descriptor",
                    "Descriptor file: " + getPath(),
                    new PackageException());
        }

        /* Set subAccountId, can be 0 or many */
        String subAccount = rootMETSDocument.getSubAccount();
                if (!subAccount.equals("")) {
                        intEnt.setSubAccountId(new Long(Agreements.getSubaccountId(subAccount, intEnt.getAcccount())));
                }


        /* Record */
        String mdRefIDs[] = rootMETSDocument.getMdRefIds();

        /* ExRec */
        String mdRef;
        if (mdRefIDs.length > 0) {
            mdRef = mdRefIDs[0];
        } else {
            mdRef = null;
        }

                if (mdRef != null) {
                        intEnt.setExtRec(mdRef);
                }

        /* ExRecType */
        String mdRefMDType = rootMETSDocument.getMdRefMdType(mdRef);

                if (mdRefMDType != null) {
                        intEnt.setExtRecType(mdRefMDType);
                }

        /* Title */
        String title = rootMETSDocument.getTitle().equals("") ? null
                : rootMETSDocument.getTitle();

                if (title != null) {
                        intEnt.setTitle(title);
                }

        /* Issue */
        String[] issues = rootMETSDocument.getIssues();
        String issue = (issues.length == 0) ? null : issues[0];

                if (issue != null) {
                        intEnt.setIssue(issue);
                }

        /* Volume */
        String[] volumes = rootMETSDocument.getVolumes();
        String volume = (volumes.length == 0) ? null : volumes[0];

                if (volume != null) {
                        intEnt.setVol(volume);
                }

        /* Set entity id */
        String objID = rootMETSDocument.getObjId();

                if (objID != null) {
                        intEnt.setEntityId(objID);
                }

        /* Create a new IntEntity */
        //intEnt = new IntEntity(this.getIp().getPackageName(), mdRef,
          //      mdRefMDType, objID, volume, issue, title);

        /* Set the descriptor dfid */
        intEnt.setDescPath(getPath());

        /* Set type */
        String type = rootMETSDocument.getType();
        if (type != null) {
            intEnt.setEntityType(type);
        }

        // this is only for non-global package intellectual entities
        intEnt.setAp(Agreements.getApId(intEnt.getAcccount(), intEnt.getProject()));
    }

    private void reduce(Element e, String[] ids) throws FatalException {
        XMLReducer r = new XMLReducer(ids);
        r.reduce(e);
        r.purge();
    }

    private METSDocument resolveFileMetaDataConflicts(String[] fileIDs)
            throws FatalException, PackageException {

        String methodName = "resolveFileMetaDataConflicts(String[])";

        // Get meta data IDs
        MetsDocument root = (MetsDocument) rootMETSDocument.getDocument().copy();
        Vector idVec = new Vector();
        for (int i = 0; i < fileIDs.length; i++) {
            String fileID = fileIDs[i];
            String[] ids = MetaDataResolver.resolve(root, fileID);
            idVec.addAll(Arrays.asList(ids));
        }
        String[] ids = (!idVec.isEmpty()) ? (String[]) idVec.toArray(new String[0])
                : new String[0];

        // Get a DOM representation of the InitialDescriptor
        Document domDoc = (Document) rootMETSDocument.getDocument().newDomNode();
        Element domRoot = domDoc.getDocumentElement();

        // Reduce the tree specific to only the fileID
        reduce(domRoot, ids);

        // Make a bitsream to return
        METSDocument METSDoc = new METSDocument(this);
        METSDoc.setDocumentElement(domRoot);
        return METSDoc;
    }

    /**
     * @param hashtable
     */
    public void setDflLite(Hashtable hashtable) {
        dflLite = hashtable;
    }

    /**
     * @param entity
     */
    public void setIntEnt(IntEntity entity) {
        intEnt = entity;
    }

    /**
     * @param document
     */
    public void setMetsDocument(METSDocument document) {
        rootMETSDocument = document;
    }

    /**
     * @see edu.fcla.daitss.format.markup.XML#toXML()
     */
    public Document toXML() throws FatalException {

        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        return doc;
    }

        /**
         * @return the intEnt
         */
        public IntEntity getIntEnt() {
                return intEnt;
        }
}
