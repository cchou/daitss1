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
 * Created on Sep 1, 2004
 *
 */
package edu.fcla.daitss.format.markup;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.WebCacheUtils;

/**
 * @author franco
 * 
 * Constant xpaths for selecting data from a METS document
 */
public class XPaths {

    /**
     * @author franco
     *
     * All the XPaths associated with the daitss schema
     */
    public static class Daitss {
        
        /**
         * All children under a daitss document element.
         */
        public static final String ALL_CHILDREN = "/" + PREFIX_DAITSS + ':' + "daitss/*";
        /**
         * Selects all the bitstreams.
         */
        public static final String BITSTREAM = "//" + PREFIX_DAITSS + ':' + "BITSTREAM";
        
        /**
         * Selects all of the events.
         */
        public static final String EVENT = "//" + PREFIX_DAITSS + ':' + "EVENT";
        

        /**
         * Selects all of the events.
         */
        public static final String GLOBAL_FILE = "//" + PREFIX_DAITSS + ':' + "GLOBAL_FILE";

        /**
         * Selects all of the int entity global files
         */
        public static final String INT_ENTITY_GLOBAL_FILE = "//" + PREFIX_DAITSS + ':' + "INT_ENTITY_GLOBAL_FILE";
        
        /**
         * Selects all of the int entity ids in the file. It will only select
         * IEIDs that are part of an INT_ENTITY (i.e. it will not select IEID
         * elements belonging to other entities, such as DataFile).
         */
        public static final String INT_ENTITY_ID = "//" + PREFIX_DAITSS + ":INT_ENTITY" + "/" + PREFIX_DAITSS + ":IEID";
        
        /**
         * Selects all of the relationships.
         */
        public static final String RELATIONSHIP = "//" + PREFIX_DAITSS + ':' + "RELATIONSHIP";
                
        /**
         * Selects all the tsm storage descriptors.
         */
        public static final String TSM_STORAGE_DESC = "//" + PREFIX_DAITSS + ':' + "TSM_STORAGE_DESC";
        
        /**
         * Selects Agreement Info Accounts.
         */
        public static final String AGREEMENT_INFO_ACCOUNT = "//" + PREFIX_DAITSS + ":daitss" + "/" + PREFIX_DAITSS 
        		+ ":AGREEMENT_INFO" + "/@ACCOUNT";          
        
        /**
         * Selects Aggreement Info Projects.
         */
        public static final String AGREEMENT_INFO_PROJECT = "//" + PREFIX_DAITSS + ":daitss" + "/" + PREFIX_DAITSS 
                + ":AGREEMENT_INFO" + "/@PROJECT";          

        /**
         * Selects Aggreement Info Sub Accounts. 
         */
        public static final String AGREEMENT_INFO_SUB_ACCOUNT = "//" + PREFIX_DAITSS + ":daitss" + "/" + PREFIX_DAITSS 
				+ ":AGREEMENT_INFO" + "/@SUB_ACCOUNT"; 
                
        
    }
    
    /**
     * @author franco
     * 
     * All the XPaths associated with the mets schema.
     */
    public static class Mets {

        /**
         * a techmd bucket
         */    	
    	public static final String TECHMD_BUCKET = 
    		"/" + PREFIX_METS + ":" + "mets" +
    		"/" + PREFIX_METS + ":" + "amdSec" +
    		"/" + PREFIX_METS + ":" + "techMD" +
    		"/" + PREFIX_METS + ":" + "mdWrap" +
    		"/" + PREFIX_METS + ":" + "xmlData";
    	
        /**
         * Selects all the archivist agents.
         */
        public static final String AGENT_ARCHIVIST = '/' + PREFIX_METS + ':'
                + "mets" + '/' + PREFIX_METS + ':' + "metsHdr" + '/'
                + PREFIX_METS + ":" + "agent[@ROLE='ARCHIVIST']";

        /**
         * Selects all the custordian agents.
         */
        public static final String AGENT_CUSTODIAN = '/' + PREFIX_METS + ':'
                + "mets" + '/' + PREFIX_METS + ':' + "metsHdr" + '/'
                + PREFIX_METS + ":" + "agent[@ROLE='CUSTODIAN']";

        /**
         * Selects all the disseminator agents.
         */
        public static final String AGENT_DISSEMINATOR = '/' + PREFIX_METS + ':'
                + "mets" + '/' + PREFIX_METS + ':' + "metsHdr" + '/'
                + PREFIX_METS + ":" + "agent[@ROLE='DISSEMINATOR']";

        /**
         * Selects all the editor agents.
         */
        public static final String AGENT_EDITOR = '/' + PREFIX_METS + ':'
                + "mets" + '/' + PREFIX_METS + ':' + "metsHdr" + '/'
                + PREFIX_METS + ":" + "agent[@ROLE='EDITOR']";

        /**
         * Selects all the ipowner agents.
         */
        public static final String AGENT_IPOWNER = '/' + PREFIX_METS + ':' + "mets" + 
                '/' + PREFIX_METS + ':' + "metsHdr" +
                '/' + PREFIX_METS + ":" + "agent[@ROLE='IPOWNER']";

        /**
         * Selects all the ipowner agents.
         */
        public static final String AGENT_IPOWNER_NAME = '/' + PREFIX_METS + ':' + "mets" + 
                '/' + PREFIX_METS + ':' + "metsHdr" + 
                '/' + PREFIX_METS + ":" + "agent[@ROLE='IPOWNER']" +
                '/' + PREFIX_METS + ":" + "name";

        /**
         * Selects all the notes.
         */
        public static final String AGENT_NOTES = "/" + PREFIX_METS + ":" + "mets"
                + "/" + PREFIX_METS + ":" + "metsHdr" + "/" + PREFIX_METS + ":"
                + "agent" + "/" + PREFIX_METS + ":" + "note";
        
        
        
        /**
         * Selects all the other agents.
         */
        public static final String AGENT_OTHER = '/' + PREFIX_METS + ':'
                + "mets" + '/' + PREFIX_METS + ':' + "metsHdr" + '/'
                + PREFIX_METS + ":" + "agent[@ROLE='OTHER']";

        /**
         * Selects all the preservation agents.
         */
        public static final String AGENT_PRESERVATION = '/' + PREFIX_METS + ':'
                + "mets" + '/' + PREFIX_METS + ':' + "metsHdr" + '/'
                + PREFIX_METS + ":" + "agent[@ROLE='PRESERVATION']";
        
        /**
         * Selects all the agents.
         */
        public static final String AGENTS = "/" + PREFIX_METS + ":" + "mets"
                + "/" + PREFIX_METS + ":" + "metsHdr" + "/" + PREFIX_METS + ":"
                + "agent";

        /**
         * Selects all the names of organization agents.
         */
        public static final String AGENTS_ORGANIZATION_NAME = "/" + PREFIX_METS + ":" + "mets"
                + "/" + PREFIX_METS + ":" + "metsHdr" + "/" + PREFIX_METS + ":"
                + "agent[@TYPE='ORGANIZATION']" + "/" + PREFIX_METS + ":" + "name";
                
        /**
         * Select all the div elements. 
         */
        public static final String DIV = "//" + PREFIX_METS + ':' + "div";
        
        /**
         * Select all the issue divs. 
         */
        public static final String DIV_ISSUE = "//" + PREFIX_METS + ':' + "div[@TYPE='issue']";

        /**
         * Select all the volume divs. 
         */
        public static final String DIV_VOLUME = "//" + PREFIX_METS + ':' + "div[@TYPE='volume']";

        /**
         * Selects all the dmdSec mdRefs.
         */
        public static final String DMD_MDREFS = "/" + PREFIX_METS + ":" + "mets"
        		+ "/" + PREFIX_METS + ":" + "dmdSec" + "//" + PREFIX_METS + ":"
        		+ "mdRef";
        
        
        /**
         * Selects all the files.
         */
        public static final String FILE = "/"
        	+ PREFIX_METS + ":mets/"
        	+ PREFIX_METS + ":fileSec//"
        	+ PREFIX_METS + ":fileGrp/"
        	+ PREFIX_METS + ":file";

        /**
         * Selects all the file groups.
         */
        public static final String FILE_GRP = "/"
        	+ PREFIX_METS + ":mets/"
        	+ PREFIX_METS + ":fileSec//"
        	+ PREFIX_METS + ":fileGrp";
        	

        /**
         * Selects all the FLocats.
         */
        public static final String FLOCAT = "//" + PREFIX_METS + ':' + "FLocat";

        /**
         * Selects all the DOI FLocats.
         */
        public static final String FLOCAT_DOI = "//" + PREFIX_METS + ':'
                + "FLocat[@LOCTYPE='DOI']";

        /**
         * Selects all the HANDLE FLocats.
         */
        public static final String FLOCAT_HANDLE = "//" + PREFIX_METS + ':'
                + "FLocat[@LOCTYPE='HANDLE']";

        /**
         * Selects all the FLocats' hrefs.
         */
        public static final String FLOCAT_HREF = "//" + PREFIX_METS + ':'
        	+ "FLocat/@" + PREFIX_XLINK + ':' + "href";

        /**
         * Selects all the OTHER FLocats.
         */
        public static final String FLOCAT_OTHER = "//" + PREFIX_METS + ':'
                + "FLocat[@LOCTYPE='OTHER']";

        /**
         * Selects all the PURL FLocats.
         */
        public static final String FLOCAT_PURL = "//" + PREFIX_METS + ':'
                + "FLocat[@LOCTYPE='PURL']";

        /**
         * Selects all the URL FLocats.
         */
        public static final String FLOCAT_URL = "//" + PREFIX_METS + ':'
                + "FLocat[@LOCTYPE='URL']";

        /**
         * Selects all the URN FLocats.
         */
        public static final String FLOCAT_URN = "//" + PREFIX_METS + ':'
                + "FLocat[@LOCTYPE='URN']";
    
    }    

    /**
     * @author franco
     * 
     * All the XPaths associated with the palmm schema
     */
    public static class Palmm {

        /**
         * Selects all the contributor elements.
         */
        public static final String CONTRIBUTOR = "//" + PREFIX_PALMM + ':'
                + "contributor";

        /**
         * Selects all the creator elements.
         */
        public static final String CREATOR = "//" + PREFIX_PALMM + ':'
                + "creator";

        /**
         * Selects all the dc contributor role elements.
         */
        public static final String DC_CONTRIBUTOR_ROLE = "//" + PREFIX_PALMM
                + ':' + "dc.contributor.role";

        /**
         * Selects all the dc coverage spatial elements.
         */
        public static final String DC_COVERAGE_SPATIAL = "//" + PREFIX_PALMM
                + ':' + "dc.coverage.spatial";

        /**
         * Selects all the dc coverage temporal elements.
         */
        public static final String DC_COVERAGE_TEMPORAL = "//" + PREFIX_PALMM
                + ':' + "dc.coverage.temporal";

        /**
         * Selects all the dc date created elements.
         */
        public static final String DC_DATE_CREATED = "//" + PREFIX_PALMM + ':'
                + "dc.date.created";

        /**
         * Selects all the dc date issued elements.
         */
        public static final String DC_DATE_ISSUED = "//" + PREFIX_PALMM + ':'
                + "dc.date.issued";

        /**
         * Selects all the dc date modified elements.
         */
        public static final String DC_DATE_MODIFIED = "//" + PREFIX_PALMM + ':'
                + "dc.date.modified";

        /**
         * Selects all the dc description abstract elements.
         */
        public static final String DC_DESCRIPTION_ABSTRACT = "//"
                + PREFIX_PALMM + ':' + "dc.description.abstract";

        /**
         * Selects all the dc description release elements.
         */
        public static final String DC_DESCRIPTION_RELEASE = "//" + PREFIX_PALMM
                + ':' + "dc.description.release";

        /**
         * Selects all the dc description table Of Contents elements.
         */
        public static final String DC_DESCRIPTION_TABLE_OF_CONTENTS = "//"
                + PREFIX_PALMM + ':' + "dc.description.tableOfContents";

        /**
         * Selects all the dc format extent elements.
         */
        public static final String DC_FORMAT_EXTENT = "//" + PREFIX_PALMM + ':'
                + "dc.format.extent";

        /**
         * Selects all the dc format medium elements.
         */
        public static final String DC_FORMAT_MEDIUM = "//" + PREFIX_PALMM + ':'
                + "dc.format.medium";

        /**
         * Selects all the dc relation has Format elements.
         */
        public static final String DC_RELATION_HAS_FORMAT = "//" + PREFIX_PALMM
                + ':' + "dc.relation.hasFormat";

        /**
         * Selects all the dc relation has Part elements.
         */
        public static final String DC_RELATION_HAS_PART = "//" + PREFIX_PALMM
                + ':' + "dc.relation.hasPart";

        /**
         * Selects all the dc relation has Version elements.
         */
        public static final String DC_RELATION_HAS_VERSION = "//"
                + PREFIX_PALMM + ':' + "dc.relation.hasVersion";

        /**
         * Selects all the dc relation is Format Of elements.
         */
        public static final String DC_RELATION_IS_FORMAT_OF = "//"
                + PREFIX_PALMM + ':' + "dc.relation.isFormatOf";

        /**
         * Selects all the dc relation is Part Of elements.
         */
        public static final String DC_RELATION_IS_PART_OF = "//" + PREFIX_PALMM
                + ':' + "dc.relation.isPartOf";

        /**
         * Selects all the dc relation is Version Of elements.
         */
        public static final String DC_RELATION_IS_VERSION_OF = "//"
                + PREFIX_PALMM + ':' + "dc.relation.isVersionOf";

        /**
         * Selects all the dc title alternative elements.
         */
        public static final String DC_TITLE_ALTERNATIVE = "//" + PREFIX_PALMM
                + ':' + "dc.title.alternative";

        /**
         * Selects all the display elements.
         */
        public static final String DISPLAY = "//" + PREFIX_PALMM + ':'
                + "display";

        /**
         * Selects all the entity Desc elements.
         */
        public static final String ENTITY_DESC = "//" + PREFIX_PALMM + ':'
                + "entityDesc";

        /**
         * Selects all the has Format elements.
         */
        public static final String HAS_FORMAT = "//" + PREFIX_PALMM + ':'
                + "hasFormat";

        /**
         * Selects all the is Format Of elements.
         */
        public static final String IS_FORMAT_OF = "//" + PREFIX_PALMM + ':'
                + "isFormatOf";

        /**
         * Selects all the location elements.
         */
        public static final String LOCATION = "//" + PREFIX_PALMM + ':'
                + "location";

        /**
         * Selects all the notes elements.
         */
        public static final String NOTES = "//" + PREFIX_PALMM + ':' + "notes";

        /**
         * Selects all the pages elements.
         */
        public static final String PAGES = "//" + PREFIX_PALMM + ':' + "pages";

        /**
         * Selects all the scale elements.
         */
        public static final String SCALE = "//" + PREFIX_PALMM + ':' + "scale";

        /**
         * Selects all the spatial elements.
         */
        public static final String SPATIAL = "//" + PREFIX_PALMM + ':'
                + "spatial";

        /**
         * Selects all the thesis elements.
         */
        public static final String THESIS = "//" + PREFIX_PALMM + ':'
                + "thesis";

    }

    /**
     * @author franco
     * 
     * All the XPaths associated with the rightsmd schema.
     */
    public static class RightsMd {

        /**
         * Selects all the access code elements.
         */
        public static final String ACCESS_CODE = "//" + PREFIX_RIGHTSMD + ':'
                + "accessCode";

        /**
         * Selects all the copyright statement elements.
         */
        public static final String COPYRIGHT_STATEMENT = "//" + PREFIX_RIGHTSMD
                + ':' + "copyrightStatement";

        /**
         * Selects all the embargo end elements.
         */
        public static final String EMBARGO_END = "//" + PREFIX_RIGHTSMD + ':'
                + "embargoEnd";

        /**
         * Selects all the version statement elements.
         */
        public static final String VERSION_STATEMENT = "//" + PREFIX_RIGHTSMD
                + ':' + "versionStatement";

    }

    /**
     * @author franco
     * 
     * All the XPaths associated with simple dublin core schema.
     */
    public class SimpleDublinCore {

        /**
         * Selects all the contributor elements.
         */
        public static final String CONTRIBUTOR = "//" + PREFIX_DUBLINCORE + ':'
                + "contributor";

        /**
         * Selects all the coverage elements.
         */
        public static final String COVERAGE = "//" + PREFIX_DUBLINCORE + ':'
                + "coverage";

        /**
         * Selects all the creator elements.
         */
        public static final String CREATOR = "//" + PREFIX_DUBLINCORE + ':'
                + "creator";

        /**
         * Selects all the date elements.
         */
        public static final String DATE = "//" + PREFIX_DUBLINCORE + ':'
                + "date";

        /**
         * Selects all the description elements.
         */
        public static final String DESCRIPTION = "//" + PREFIX_DUBLINCORE + ':'
                + "description";

        /**
         * Selects all the format elements.
         */
        public static final String FORMAT = "//" + PREFIX_DUBLINCORE + ':'
                + "format";

        /**
         * Selects all the identifier elements.
         */
        public static final String IDENTIFIER = "//" + PREFIX_DUBLINCORE + ':'
                + "identifier";

        /**
         * Selects all the language elements.
         */
        public static final String LANGUAGE = "//" + PREFIX_DUBLINCORE + ':'
                + "language";

        /**
         * Selects all the publisher elements.
         */
        public static final String PUBLISHER = "//" + PREFIX_DUBLINCORE + ':'
                + "publisher";

        /**
         * Selects all the relation elements.
         */
        public static final String RELATION = "//" + PREFIX_DUBLINCORE + ':'
                + "relation";

        /**
         * Selects all the rights elements.
         */
        public static final String RIGHTS = "//" + PREFIX_DUBLINCORE + ':'
                + "rights";

        /**
         * Selects all the source elements.
         */
        public static final String SOURCE = "//" + PREFIX_DUBLINCORE + ':'
                + "source";

        /**
         * Selects all the subject elements.
         */
        public static final String SUBJECT = "//" + PREFIX_DUBLINCORE + ':'
                + "subject";

        /**
         * Selects all the title elements.
         */
        public static final String TITLE = "//" + PREFIX_DUBLINCORE + ':'
                + "title";

        /**
         * Selects all the type elements.
         */
        public static final String TYPE = "//" + PREFIX_DUBLINCORE + ':'
                + "type";
    }

    
    /**
     * @author franco
     *
     *All the XPaths associated with mods schema.
     */
    public class Mods {
        /**
         * Selects all the title elements.
         */
        public static final String TITLE = "//" + PREFIX_MODS + ':' + "title";
    }
    
    /**
     * @author franco
     * 
     * All the XPaths associated with the techmd schema.
     */
    public static class TechMd {

        /**
         * Selects all captureInfo elements.
         */
        public static final String CAPTURE_INFO = "//" + PREFIX_TECHMD + ':'
                + "captureInfo";

        /**
         * Selects all the camera settings elements.
         */
        public static final String CAPTURE_INFO_CAMERA_SETTINGS = "//"
                + PREFIX_TECHMD + ':' + "captureInfo" + "/" + PREFIX_TECHMD
                + ':' + "cameraSettings";

        /**
         * Selects all capture elements.
         */
        public static final String CAPTURE_INFO_CAPTURE = "//" + PREFIX_TECHMD
                + ':' + "captureInfo" + "/" + PREFIX_TECHMD + ':' + "capture";

        /**
         * Selects all the device elements.
         */
        public static final String CAPTURE_INFO_DEVICE = "//" + PREFIX_TECHMD
                + ':' + "captureInfo" + "/" + PREFIX_TECHMD + ':' + "device";

        /**
         * Selects all the light elements.
         */
        public static final String CAPTURE_INFO_LIGHT = CAPTURE_INFO + "/"
                + PREFIX_TECHMD + ':' + "light";

        /**
         * Selects all the scanner settings elements.
         */
        public static final String CAPTURE_INFO_SCANNER_SETTINGS = "//"
                + PREFIX_TECHMD + ':' + "captureInfo" + "/" + PREFIX_TECHMD
                + ':' + "scannerSettings";

        /**
         * Selects all the tracking ID elements
         */
        public static final String CAPTURE_INFO_TRACKING_ID = "//"
                + PREFIX_TECHMD + ':' + "captureInfo" + "/" + PREFIX_TECHMD
                + ':' + "trackingID";

        /**
         * Select all compression elements.
         */
        public static final String COMPRESSION = "//" + PREFIX_TECHMD + ':'
                + "compression";

        /**
         * Select all creationMethod elements.
         */
        public static final String CREATION_METHOD = "//" + PREFIX_TECHMD + ':'
                + "creationMethod";

        /**
         * Selects all the creator elements.
         */
        public static final String CREATOR = "//" + PREFIX_TECHMD + ':'
                + "creator";

        /**
         * Select all the individual elements.
         */
        public static final String CREATOR_INDIVIDUAL = "//" + PREFIX_TECHMD
                + ':' + "creator" + "/" + PREFIX_TECHMD + ':' + "individual";

        /**
         * Select all the institution elements.
         */
        public static final String CREATOR_INSTITUTION = "//" + PREFIX_TECHMD
                + ':' + "creator" + "/" + PREFIX_TECHMD + ':' + "institution";

        /**
         * Selects all the image elements.
         */
        public static final String IMAGE = "//" + PREFIX_TECHMD + ':' + "image";

        /**
         * Selects all the bit depth elements.
         */
        public static final String IMAGE_BIT_DEPTH = "//" + PREFIX_TECHMD + ':'
                + "image" + "/" + PREFIX_TECHMD + ':' + "bitDepth";

        /**
         * Selects all the clut elements.
         */
        //	clut
        public static final String IMAGE_CLUT = "//" + PREFIX_TECHMD + ':'
                + "image" + "/" + PREFIX_TECHMD + ':' + "clut";

        /**
         * Selects all the color management elements.
         */
        public static final String IMAGE_COLOR_MANAGEMENT = "//"
                + PREFIX_TECHMD + ':' + "image" + "/" + PREFIX_TECHMD + ':'
                + "colorManagement";

        /**
         * Selects all the color space elements.
         */
        public static final String IMAGE_COLOR_SPACE = "//" + PREFIX_TECHMD
                + ':' + "image" + "/" + PREFIX_TECHMD + ':' + "colorSpace";

        /**
         * Selects all the descreening elements.
         */
        public static final String IMAGE_DESCREENING = "//" + PREFIX_TECHMD
                + ':' + "image" + "/" + PREFIX_TECHMD + ':' + "descreening";

        /**
         * Selects all the dimensions elements.
         */
        public static final String IMAGE_DIMENSIONS = "//" + PREFIX_TECHMD
                + ':' + "image" + "/" + PREFIX_TECHMD + ':' + "dimensions";

        /**
         * Selects all the orientation elements.
         */
        public static final String IMAGE_ORIENTATION = "//" + PREFIX_TECHMD
                + ':' + "image" + "/" + PREFIX_TECHMD + ':' + "orientation";

        /**
         * Selects all the resolution elements.
         */
        public static final String IMAGE_RESOLUTION = "//" + PREFIX_TECHMD
                + ':' + "image" + "/" + PREFIX_TECHMD + ':' + "resolution";

        /**
         * Selects all the sampling frequency elements.
         */
        public static final String IMAGE_SAMPLING_FREQUENCY = "//"
                + PREFIX_TECHMD + ':' + "image" + "/" + PREFIX_TECHMD + ':'
                + "samplingFrequency";

        /**
         * Selects all the storage elements.
         */
        public static final String IMAGE_STORAGE = "//" + PREFIX_TECHMD + ':'
                + "image" + "/" + PREFIX_TECHMD + ':' + "storage";

        /**
         * Selects all the target elements.
         */
        //	target
        public static final String IMAGE_TARGET = "//" + PREFIX_TECHMD + ':'
                + "image" + "/" + PREFIX_TECHMD + ':' + "target";

        /**
         * Select all the sourceInfo elements.
         */
        public static final String SOURCE_INFO = "//" + PREFIX_TECHMD + ':'
                + "sourceInfo";

        /**
         * Select all the dimensions elements.
         */
        public static final String SOURCE_INFO_DIMENSIONS = "//"
                + PREFIX_TECHMD + ':' + "sourceInfo" + "/" + PREFIX_TECHMD
                + ':' + "dimsensions";

        /**
         * Select all the source elements
         */
        public static final String SOURCE_INFO_SOURCE = "//" + PREFIX_TECHMD
                + ':' + "sourceInfo" + "/" + PREFIX_TECHMD + ':' + "source";
    }

    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.format.markup.XPaths";

	private static DocumentBuilder builder;
	
    /**
     * Namespace prefix used for daitss schema in xpaths.
     */
    public static final String PREFIX_DAITSS = "daitss";

    /**
     * Namespace prefix used for dublin core in xpaths.
     */
    public static final String PREFIX_DUBLINCORE = "dc";

    /**
     * Namespace prefix used for mets in xpaths.
     */
    public static final String PREFIX_METS = "mets";

    /**
     * Namespace prefix used for dublin core in xpaths.
     */
    public static final String PREFIX_MODS = "mods";

    /**
     * Namespace prefix used for palmm in xpaths.
     */
    public static final String PREFIX_PALMM = "palmm";

    /**
     * Namespace prefix used for rightsmd in xpaths.
     */
    public static final String PREFIX_RIGHTSMD = "rightsmd";

    /**
     * Namespace prefix used for techmd in xpaths.
     */
    public static final String PREFIX_TECHMD = "techmd";

    /**
     * Namespace prefix used for xinclude in xpaths.
     */
    public static final String PREFIX_XINCLUDE = "xinclude";

    /**
     * Namespace prefix used for xlink in xpaths.
     */
    public static final String PREFIX_XLINK = "xlink";

    /**
     * Namespace prefix used for xml schema in xpaths.
     */
    public static final String PREFIX_XMLSCHEMA = "xs";

    /**
     * Namespace prefix used for schema instance in xpaths.
     */
    public static final String PREFIX_XMLSCHEMAINSTANCE = "xsi";

    /**
     * Namespace prefix used for xml style sheets in xpaths.
     */
    public static final String PREFIX_XMLSTYLESHEET = "xsl";
    
    private static DocumentBuilder getBuilder() throws FatalException {
        String methodName = new Throwable().getStackTrace()[0].getMethodName();
    	if (builder == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = null;
            try {
                builder = factory.newDocumentBuilder();
                
            	WebCacheResolver resolver = WebCacheUtils.getResolver();
                builder.setEntityResolver(resolver);
            } catch (ParserConfigurationException e) {
                Informer.getInstance().fail(CLASSNAME, 
                        methodName, 
                        "Cannot create DocumentBuilder", 
                        "creating of new daitss document",
                        e);
            }    		
    	} 
    	return builder;
    }
    
    /**
     * Creates a new daitss document.
     * @return a new daitss document
     * @throws FatalException
     */
    public static Document newDaitssDoc() throws FatalException {
        
        /* Create the Document Node */
        Document doc = getBuilder().newDocument();
        String namespace = ArchiveProperties.getInstance().getArchProperty("NS_DAITSS");
        
        /* Create the Document Element */
        doc.appendChild(doc.createElementNS(namespace, "daitss"));
        
        return doc;
    }
    
    /**
     * Creates a new Document document.
     * @return a new document
     * @throws FatalException
     */
    public static Document newDocument() throws FatalException {
        
        /* Create the Document Node */
        Document doc = getBuilder().newDocument();
        
        return doc;
    }
    
    /**
     * @return an Element with namespace prefixes resolving to Daitss specific
     *         namepaces.
     * @throws FatalException
     */
    public static Node newNamespaceNode() throws FatalException {

        ArchiveProperties p = ArchiveProperties.getInstance();

        Element nsNode = newDocument().createElement("nsnode");

        /*
         * http://www.w3.org/2000/xmlns/ is not a real namespace but is used in
         * dom beause xmlns is not a real prefix.
         */
        String xmlnsUri = "http://www.w3.org/2000/xmlns/";

        // XML
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_XINCLUDE, p.getArchProperty("NS_XINCLUDE"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_XMLSCHEMA, p.getArchProperty("NS_XMLSCHEMA"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_XMLSTYLESHEET, p.getArchProperty("NS_XSL"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_XLINK, p.getArchProperty("NS_XLINK"));
        
        // METS
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_METS, p.getArchProperty("NS_METS"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_TECHMD, p.getArchProperty("DES_TECHMD_NS_URI"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_RIGHTSMD, p.getArchProperty("DES_RIGHTSMD_NS_URI"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_PALMM, p.getArchProperty("DES_PALMM_NS_URI"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_DUBLINCORE, p.getArchProperty("DES_DC_NS_URI"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_MODS, p.getArchProperty("DES_MODS_NS_URI"));
        nsNode.setAttributeNS(xmlnsUri, "xmlns:" + PREFIX_DAITSS, p.getArchProperty("NS_DAITSS"));

        return nsNode;
    }

    /**
     * @return a PrefixResolver for resolving Daitss specific namepaces.
     * @throws FatalException
     */
    public static PrefixResolver newPrefixResolver() throws FatalException {
        ArchiveProperties ap = ArchiveProperties.getInstance();
        Map nsmap = new Hashtable();

        // XML
        nsmap.put(PREFIX_XINCLUDE, ap.getArchProperty("NS_XINCLUDE"));
        nsmap.put(PREFIX_XMLSCHEMA, ap.getArchProperty("NS_XMLSCHEMA"));
        nsmap.put(PREFIX_XMLSCHEMAINSTANCE, ap.getArchProperty("NS_XMLSCHEMA_INSTANCE"));
        nsmap.put(PREFIX_XMLSTYLESHEET, ap.getArchProperty("NS_XSL"));
        nsmap.put(PREFIX_XLINK, ap.getArchProperty("NS_XLINK"));

        // METS
        nsmap.put(PREFIX_METS, ap.getArchProperty("NS_METS"));
        nsmap.put(PREFIX_TECHMD, ap.getArchProperty("DES_TECHMD_NS_URI"));
        nsmap.put(PREFIX_RIGHTSMD, ap.getArchProperty("DES_RIGHTSMD_NS_URI"));
        nsmap.put(PREFIX_PALMM, ap.getArchProperty("DES_PALMM_NS_URI"));
        nsmap.put(PREFIX_DUBLINCORE, ap.getArchProperty("DES_DC_NS_URI"));
        nsmap.put(PREFIX_MODS, ap.getArchProperty("DES_MODS_NS_URI"));
        nsmap.put(PREFIX_DAITSS, ap.getArchProperty("NS_DAITSS"));
        PrefixResolver resolver = new SimplePrefixResolver(nsmap);
        return resolver;
    }
    
    /**
     * Selects nodes described by an XPath.
     * @param contextNode
     * @param xpath
     * @return the nodes selected by the xpath
     * @throws FatalException
     */
    public static NodeList selectNodeList(Node contextNode, String xpath) throws FatalException {
        String methodName = "selectSingleNode(Node, String)";
        Node nsNode = newNamespaceNode();
        NodeList results = null;
        try {
            results = XPathAPI.selectNodeList(contextNode, xpath, nsNode);
        } catch (TransformerException e) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Cannot perform selection", "Selecting nodes from xpath: " + xpath, e);
        }
        return results;
    }
    
    /**
     * Selects nodes described by an XPath.
     * @param contextNode
     * @param xpath
     * @return The first node of the resulting nodeset
     * @throws FatalException
     */
    public static Node selectSingleNode(Node contextNode, String xpath) throws FatalException {
        String methodName = "selectSingleNode(Node, String)";
        Node nsNode = newNamespaceNode();
        Node result = null;
        try {
            result = XPathAPI.selectSingleNode(contextNode, xpath, nsNode);
        } catch (TransformerException e) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Cannot perform selection", "Selecting node from xpath: " + xpath, e);
        }
        return result;
    }

}