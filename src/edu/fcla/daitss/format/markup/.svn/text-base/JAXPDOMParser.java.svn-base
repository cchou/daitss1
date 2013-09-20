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
package edu.fcla.daitss.format.markup;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import com.sun.org.apache.xpath.internal.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal; // (xml-apis_2_6_2.jar)
import org.w3c.dom.traversal.NodeFilter; // (xml-apis_2_6_2.jar)
import org.w3c.dom.traversal.NodeIterator; // (xml-apis_2_6_2.jar)
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.WebCacheUtils;

/**
 * JAXPDOMParser is a class used for aiding FCLA's METS xml file processing. It contains
 * 2 parse methods and several find methods. The packages used are JAXP ( version 1.2, from
 * Sun: Java XML summer 02_01 package, whose default parser is Apache's Xerces ) and XPath 
 * from Apache's Xalan-J.
 *    <br> Dec.26  Modified from JAXP's sample/DOM/DOMEcho.java 
 *    <br> Dec.30  Vector find(String eltName, String attName) 
 *    <br>         Vector find(String eltName, String attName, String attValue)
 *    <br> Dec.31  Vector findNodes(String xpath) throws Exception
 *    <br> Jan.08  Vector find(String xpath) throws Exception
 *    <br> Feb.17  Parser configuraton settings are global variables. setDefaultSettings("dtd"/"schema") can change them.
 *                 dtd xml files can be parsed.
 * @version 1.0
 * @author dongqing
 *
 */
public class JAXPDOMParser {
	
	
	// Error handler to report errors and warnings
	 private static class MyErrorHandler implements ErrorHandler {
		 /** Error handler output goes here */
		 private PrintWriter out;

		 MyErrorHandler(PrintWriter out) {
			 this.out = out;
		 }
        
		 /**
		 * @param spe 
		 * @throws SAXException 
		 */
		public void error(SAXParseException spe) throws SAXException {
			 String message = "Error: " + getParseExceptionInfo(spe);
			 throw new SAXException(message);
		 }

		 /** (non-Javadoc)
		 * @param spe 
		 * @throws SAXException 
		 */
		public void fatalError(SAXParseException spe) throws SAXException {
			 String message = "Fatal Error: " + getParseExceptionInfo(spe);
			 throw new SAXException(message);
		 }

		 /**
		  * Returns a string describing parse exception details
		 * @param spe 
		 * @return String
		  */
		 private String getParseExceptionInfo(SAXParseException spe) {
			 String systemId = spe.getSystemId();
			 if (systemId == null) {
				 systemId = "null";
			 }
			 String info = "URI=" + systemId +
				 " Line=" + spe.getLineNumber() +
				 ": " + spe.getMessage();
			 return info;
		 }

		 /**
		 * @param spe 
		 * @throws SAXException 
		 */
		// The following methods are standard SAX ErrorHandler methods.
		 // See SAX documentation for more info.

		 public void warning(SAXParseException spe) throws SAXException {
			 out.println("Warning: " + getParseExceptionInfo(spe));
		 }
	 }

	 /** Constants used for JAXP 1.2 */
	 static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	 static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	 /** Mets schema file */
	 static final String METS_SCHEMA_FILE ="http://www.loc.gov/standards/mets/mets.xsd";
	/** All output will use this encoding */
	 static final String outputEncoding = "UTF-8";
	 static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	 /**
	  * Using NodeIterator (nonrecursively) to extract text of an element.
	  * Note: All subelements' text and CDATA section will be retrieved.
	 * @param node 
	 * @return String
	  */
	 public static String extractText( Node node ) {

		 if ( node == null )  return "";

		 Document doc = node.getOwnerDocument();
		 DocumentTraversal traversable = (DocumentTraversal) doc;
		 int whatToShow = NodeFilter.SHOW_TEXT | NodeFilter.SHOW_CDATA_SECTION;
		 NodeIterator iterator = traversable.createNodeIterator(node, whatToShow, null, true);

		 StringBuffer result = new StringBuffer();
		 Node current;
		 while ((current = iterator.nextNode())!= null ) {
			result.append(current.getNodeValue());
		 }

		 return result.toString();

	 }


	 /**
	  * Return the comment textual content of the element node.
	  * @param node A dom tree element node.
	  * @return Textual content of this node's comment node. null if this element doesn't contain comment node.
	  */
	 public static String getCommentTextOfThisElement( Node node ) {

	   if ( node == null ) return null;
	   int type = node.getNodeType();
	   if ( type != Node.ELEMENT_NODE ) { return null; }

	   StringBuffer comment = new StringBuffer();
	   if ( node.hasChildNodes() ) {
		   NodeList children = node.getChildNodes();
		   for ( int i=0; i< children.getLength(); i++ ) {
			  Node child = children.item(i);
			  type = child.getNodeType();
			  if ( type == Node.COMMENT_NODE ) {
				  comment.append( child.getNodeValue().trim() );
			  }
		   }
	   }

	   return comment.toString();

	 }


	 /**
	  * Using recursive method to retrieve text of descedant nodes of specified node.
	  * Descedant nodes include elements, entity references, CDATA sections and text nodes,
	  * not include comments or processing instructions nodes.
	  * @param node A Dom tree node
	  * @return All text contents in a String.
	  */
	 public static String getText(Node node) {

		 int type = node.getNodeType();

		 if ( type == Node.COMMENT_NODE || type == Node.PROCESSING_INSTRUCTION_NODE) {
			 return "";
		 }

		 StringBuffer text  = new StringBuffer();

		 String value = node.getNodeValue();
		 if ( value !=  null ) text.append(value);
		 if ( node.hasChildNodes() ) {
			 NodeList children = node.getChildNodes();
			 for ( int i=0; i< children.getLength(); i++ ) {
				 Node child = children.item(i);
				 text.append(getText(child));
			 }
		  }

		  return text.toString();
	 }

	 /**
	  * Return the text that an element node contains. This method: <ul>
	  *  <li>Ignores comments and processing instructions.
	  *  <li>Concatenated TEXT nodes, CDATA nodes. 
	  *  <li>Ignores any element nodes in the sublist.
	  *  <li>Ignore EntityRef nodes, since the parser is validating.
	  * @param node A dom tree element node.
	  * @return A String representing its contents. Null if node is not an element node.
	  *         
	  */
	 public static String getTextOfThisElementOnly(Node node) {

		 if ( node == null ) return null;
		 int type = node.getNodeType();
		 if ( type != Node.ELEMENT_NODE ) { return null; }

		 StringBuffer text = new StringBuffer();
		 if ( node.hasChildNodes() ) {   // element node does have child node
			NodeList children = node.getChildNodes();
			for ( int i=0; i< children.getLength(); i++ ) {
			   Node child = children.item(i);
			   type = child.getNodeType();
			   if ( type == Node.TEXT_NODE ) {
				   //Trim the value to get rid of whitespace(newline,space,etc.) from both ends
				   text.append( child.getNodeValue().trim() );
			   }
			   else if ( type == Node.CDATA_SECTION_NODE ) {
				   text.append( child.getNodeValue()); // maybe keep whitespace??
			   }
			   else if ( type == Node.ENTITY_REFERENCE_NODE ) {
				   NodeList ref_children = child.getChildNodes();    
				   for ( int j=0; j< ref_children.getLength(); j++ ) {
					   Node ref_child = ref_children.item(j);
					   type = ref_child.getNodeType();
                      
					   if ( type == Node.TEXT_NODE ) {  //assume it only contains Text node
						   text.append( ref_child.getNodeValue().trim() );
					   }
					   //ignore comment, PI, CDATA, element, entityRef

				   }
			   }
			}
		 }
		 return text.toString();       

	 }

	 static boolean isAttributeNode( Node n ) {
		 if ( n == null ) return false;
		 short type = n.getNodeType();
		 return type == Node.ATTRIBUTE_NODE;
	 }

	 static boolean isElementNode( Node n ) {
		 if ( n == null ) return false;
		 short type = n.getNodeType();
		 return type == Node.ELEMENT_NODE;
	 }


	 static boolean isTextNode( Node n ) {
		 if ( n ==  null ) return false;
		 short type = n.getNodeType();
		 return type == Node.CDATA_SECTION_NODE || type == Node.TEXT_NODE;
	 }

	/**
	 * testing routine
	 * @param args
	 */
	public static void main(String[] args) {
		//String filename = "C:\\Program Files\\eclipse\\workspace\\daitss\\ingest\\in\\UF00003061\\UF00003061.xml";
		
		// uses a DTD
		//String filename = "/apps/xerces-2_6_2/data/personal.xml";
		
		// uses W3C schema
		//String filename = "/apps/xerces-2_6_2/data/personal-schema.xml";
		String filename = "C:\\XML Stuff\\personal_extdtd_andnsschema.xml";
		
		JAXPDOMParser parser = new JAXPDOMParser();
		
		try {
			// use for xml with W3C schema
			parser.parseXmlFile(filename, false, true, null, true,true, true, false);				
			// use for XML files with DTDs
			//parser.parseXMLFile(filename,true, false, null, true,true, true, false);
			System.out.println(filename + " is valid");
		} catch (Exception e) {
			// it's OK to print this out in the main
			e.printStackTrace();
			System.out.println(filename + " is not valid");
		}
		
		/*
		//default setting: schema validation=true, using Mets.xsd.
		String filename = null;
		boolean dtdValidate = false;
		boolean xsdValidate = true;
		String schemaSource = null;

		boolean ignoreWhitespace = true;
		boolean ignoreComments = true;
		boolean putCdataIntoText = true;
		boolean createEntityRefs = false;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dtd")) {
				dtdValidate = true;
				xsdValidate = false;
			} else if (args[i].equals("-xsd")) {
				xsdValidate = true;
			} else if (args[i].equals("-xsdss")) {
				if (i == args.length - 1) {
					usage();
				}
				xsdValidate = true;
				schemaSource = args[++i];
			} else if (args[i].equals("-ws")) {
				ignoreWhitespace = true;
			} else if (args[i].startsWith("-co")) {
				ignoreComments = true;
			} else if (args[i].startsWith("-cd")) {
				putCdataIntoText = true;
			} else if (args[i].startsWith("-e")) {
				createEntityRefs = true;
			} else if (args[i].equals("-usage")) {
				usage();
			} else if (args[i].equals("-help")) {
				usage();
			} else {
				filename = args[i];

				// Must be last arg
				if (i != args.length - 1) {
					usage();
				}
              
			}
		}
		if (filename == null) {
			//usage();

			//Use default xml file
			filename = new String("brk00010.00000004.mets.xml");
		}

		OutputStreamWriter outWriter = new OutputStreamWriter( System.out, outputEncoding );
		JAXPDOMParser parser = new JAXPDOMParser( new PrintWriter(outWriter, true) );
		parser.setDebug( true );
     
		Document doc;
		try {

		   //1. Test longer parse method
		   //doc = parser.parseXMLFile(filename, dtdValidate, xsdValidate, schemaSource, 
											 ignoreWhitespace, ignoreComments,
											 putCdataIntoText, createEntityRefs);


		   //2. Test shorter parse method
		   if ( dtdValidate ) { 
			  parser.setDefaultSettings( "dtd" );
		   }
		   else {
			  parser.setDefaultSettings( "schema" );
		   }
		   doc = parser.parseXMLFile( filename );

		}
		catch (Exception e ) {
		   e.printStackTrace();
		   System.exit(-1);
		}

		//3. Test find method on brk00010.00000004.mets.xml
		if ( filename.equals("brk00010.00000004.mets.xml") ) {
		   System.out.println("\n**************************");
		   System.out.println("Test find('mets:dmdSec', 'ID'):");
		   parser.find("mets:dmdSec", "ID");

		   System.out.println("\n**************************");
		   System.out.println("Test find('mets:dmdSec', ''):");
		   parser.find("mets:dmdSec", "");

		   System.out.println("\n**************************");
		   System.out.println("Test find('moa2:Title', ''):");
		   parser.find("moa2:Title", "");

		   System.out.println("\n**************************");
		   System.out.println("Test find('moa2:coreDate', 'primaryDate','1915'):");
		   parser.find("moa2:coreDate", "primaryDate", "1915");

		   System.out.println("\n**************************");
		   System.out.println("Test findNodes('//mets:dmdSec[@ID='DM1']//moa2:SOType'):");
		   parser.findNodes("//mets:dmdSec[@ID='DM1']//moa2:SOType");
		}

		//4. Test find method on etd.mets.xml
		else if ( filename.equals("etd.mets.xml") ) {
		   System.out.println("\n***************************");
		   System.out.println("Test find('//METS:dmdSec[@ID='DMD1']//dc:subject')");
		   parser.find("//METS:dmdSec[@ID='DMD1']//dc:subject");

		   System.out.println("\n***************************");
		   System.out.println("Test findNodes('//METS:amdSec//METS:mdWrap/@OTHERMDTYPE')");
		   parser.findNodes("//METS:amdSec//METS:mdWrap/@OTHERMDTYPE");
		}

		else {
		   System.out.println( filename + " is parsed. ");
		}

		// Print out the DOM tree
		//parser.echo(doc);
		*/
	}

	 private static void usage() {
		 System.err.println("Usage: FCLA_JAXPDOMParser [-options] <file.xml>");
		 System.err.println("       -dtd = DTD validation");
		 System.err.println("       -xsd | -xsdss <file.xsd> = W3C XML Schema validation using xsi: hints");
		 System.err.println("           in instance document or schema source <file.xsd>");
		 System.err.println("       -ws = do not create element content whitespace nodes");
		 System.err.println("       -co[mments] = do not create comment nodes");
		 System.err.println("       -cd[ata] = put CDATA into Text nodes");
		 System.err.println("       -e[ntity-ref] = create EntityReference nodes");
		 System.err.println("       -usage or -help = this message");
		 System.exit(1);
	 }

	 /** Indentation will be in multiples of basicIndent  */
	 private final String basicIndent = "  ";
	 boolean createEntityRefs ;        // wheather produce entity references nodes
	 private Document document;        //save parsed Document

	 /** parser configuration settings */
	 boolean dtdValidate ;             // wheather againest dtd
	 boolean ignoreComments ;          // wheather produce comment nodes
	 boolean ignoreWhitespace ;        // wheather ignore white spaces

	 /** Indent level */
	 private int indent = 0;
	 Hashtable namespaces;

	 /** Output goes here */
	 private PrintWriter out;

	 /** Global variables */
	 boolean printFlag;
	 boolean putCdataIntoText ;        // wheather produce CDATA section
	 String schemaSource ;             // schema source specified by program
	 boolean xsdValidate ;             // wheather againest schema


	 /**
	  * Constructor using System.out as default output place, using Java virtual machine
	  * default character encoding as encoding type and set global parser configuration settings.
	  * Default parser configuration settings are set for parsing xml with schema.
	  * They are:  dtdValidate = false; xsdValidate = true;   schemaSource = null;   ignoreWhitespace = true; 
	  *            ignoreComments = true;  putCdataIntoText = true; createEntityRefs = false.
	  */
	 public JAXPDOMParser() {
		 OutputStreamWriter outWriter = new OutputStreamWriter( System.out );
		 PrintWriter pw =  new PrintWriter(outWriter, true) ;
		 this.out = pw; 
		 this.printFlag = false;
		 this.namespaces = new Hashtable();

		 //default settings is for xml use schema
		 try {
			setDefaultSettings("schema");
		 } 
		 catch (Exception e) { };
	 }

	 /**
	  * Constructor with specified PrintWriter, which can specify encoding type. 
	  * Default parser configuration settings are set for parsing xml with schema.
	  * They are:  dtdValidate = false; xsdValidate = true;   schemaSource = null;   ignoreWhitespace = true; 
	  *            ignoreComments = true;  putCdataIntoText = true; createEntityRefs = false.
	  * @param out Where to output.
	  */
	 public JAXPDOMParser(PrintWriter out) {
		 this.out = out;
		 this.printFlag = false;
		 this.namespaces = new Hashtable();

		 //default settings is for xml use schema
		 try {
			setDefaultSettings("schema");
		 } catch (Exception e) {};
	 }

	 /**
	  * Recursive routine to print out DOM tree nodes
	 * @param n 
	  */
	 private void echo(Node n) {
		 // Indent to the current level before printing anything
		 outputIndentation();

		 int type = n.getNodeType();
		 switch (type) {
		 case Node.ATTRIBUTE_NODE:
			 out.print("ATTR:");
			 printlnCommon(n);
			 break;
		 case Node.CDATA_SECTION_NODE:
			 out.print("CDATA:");
			 printlnCommon(n);
			 break;
		 case Node.COMMENT_NODE:
			 out.print("COMM:");
			 printlnCommon(n);
			 break;
		 case Node.DOCUMENT_FRAGMENT_NODE:
			 out.print("DOC_FRAG:");
			 printlnCommon(n);
			 break;
		 case Node.DOCUMENT_NODE:
			 out.print("DOC:");
			 printlnCommon(n);
			 break;
		 case Node.DOCUMENT_TYPE_NODE:
			 out.print("DOC_TYPE:");
			 printlnCommon(n);

			 // Print entities if any
			 NamedNodeMap nodeMap = ((DocumentType)n).getEntities();
			 indent += 2;
			 for (int i = 0; i < nodeMap.getLength(); i++) {
				 Entity entity = (Entity)nodeMap.item(i);
				 echo(entity);
			 }
			 indent -= 2;
			 break;
		 case Node.ELEMENT_NODE:
			 out.print("ELEM:");
			 printlnCommon(n);

			 // Print attributes if any.  Note: element attributes are not
			 // children of ELEMENT_NODEs but are properties of their
			 // associated ELEMENT_NODE.  For this reason, they are printed
			 // with 2x the indent level to indicate this.
			 NamedNodeMap atts = n.getAttributes();
			 indent += 2;
			 for (int i = 0; i < atts.getLength(); i++) {
				 Node att = atts.item(i);
				 echo(att);
			 }
			 indent -= 2;
			 break;
		 case Node.ENTITY_NODE:
			 out.print("ENT:");
			 printlnCommon(n);
			 break;
		 case Node.ENTITY_REFERENCE_NODE:
			 out.print("ENT_REF:");
			 printlnCommon(n);
			 break;
		 case Node.NOTATION_NODE:
			 out.print("NOTATION:");
			 printlnCommon(n);
			 break;
		 case Node.PROCESSING_INSTRUCTION_NODE:
			 out.print("PROC_INST:");
			 printlnCommon(n);
			 break;
		 case Node.TEXT_NODE:
			 out.print("TEXT:");
			 printlnCommon(n);
			 break;
		 default:
			 out.print("UNSUPPORTED NODE: " + type);
			 printlnCommon(n);
			 break;
		 }

		 // Print children if any
		 indent++;
		 for (Node child = n.getFirstChild(); child != null;
			  child = child.getNextSibling()) {
			 echo(child);
		 }
		 indent--;
	 }

	 /**
	  * A find method that using xpath as input parameter.
	  * @param xpath A String format of xpath.
	  * @return a Vector of String value of those Nodes that satiesfy the xpath query.
	  * @exception Exception
	  */
	 public Vector find(String xpath) throws Exception {

		 Vector v = new Vector();
		 Vector nodes;

		 try {
			nodes = findNodes(xpath);
		 }
		 catch (Exception e) {
			throw e;
		 }

		 if ( nodes.size() > 0 ) {
			for ( int i=0; i< nodes.size(); i++ ) {
			   Node node = (Node) nodes.elementAt(i);
			   if ( isTextNode(node) ) {
				  StringBuffer sb = new StringBuffer(node.getNodeValue());
				  for (Node nextNode = node.getNextSibling(); isTextNode(nextNode); nextNode=node.getNextSibling()) {
					  sb.append( nextNode.getNodeValue() );
				  }
				  v.addElement( sb);
			   }
			   else if ( isAttributeNode(node) ){
				  v.addElement( node.getNodeValue() );
			   }
			   else if ( isElementNode(node) ) {
				  v.addElement( getTextOfThisElementOnly( node ) );
			   }
			}
		 }

		 return v;

	 }


	 /** 
	  * A find method that using element name and attribute name as input parameter.
	  * @param eltName The name of the element that we will search. If the element is in a namespace, then
	  *        eltName should be in this format: namespace prefix:element localname. Ie, "mets:dmdSec". 
	  * @param attName The name of the attribute that we will search. Use empty string if we don't care attribute.
	  * @return A vector of Strings. If the attName is empty, the result is the value of eltName's Text Node 
	  *         (no descedant text nodes). If the attName is not empty, the result is the attribute value
	  *          of eltName's attName attribute. null is returned if the DOM tree is empty or eltName is empty.
	  */
	 public Vector find(String eltName, String attName) {

		 Vector v = new Vector();
		 Document doc = this.document;

		 if ( doc == null ) {
			String msg = "Error: 'document' is empty. Call parseXMLFile method before calling find method.";
			if ( printFlag ) {
				out.println( msg );
			}
			return v;
		 }
           
		 //check input
		 if ( eltName.equals("")) {
			  return v;
		 }

		 NodeList nodelist;

		 if ( eltName.indexOf(":") != -1 ) {  // namespace is used
			  String prefix = eltName.substring(0, eltName.indexOf(":"));
			  String localName = eltName.substring( eltName.indexOf(":")+ 1);
			  String namespaceuri = retrieveUri( prefix );
             
			  nodelist = doc.getElementsByTagNameNS( namespaceuri, localName );
		 }
		 else {  // no namespace is used
			  nodelist = doc.getElementsByTagNameNS( "*", eltName );
		 }

		 if( printFlag ) { System.out.println("Found result number=" + nodelist.getLength()); }

		 for ( int i =0; i< nodelist.getLength(); i++ ) {
			  org.w3c.dom.Element ele = (org.w3c.dom.Element)nodelist.item(i);

			  if ( attName.equals("") ) { // we want element value
					String eleText = getTextOfThisElementOnly( ele );
					v.addElement( eleText );      
					if ( printFlag ) {
						 echo( ele );
						 out.println("Result " + i + ": '" + eleText + "'" );
					}
			  }
			  else {  // we want attribute value
					String attValue = ele.getAttribute( attName );
					if ( attValue != null ) {
						v.addElement( attValue );
						if ( printFlag ) {
							//echo( ele );
							out.println("Result " + i + ": '" + attValue );
						}
					}
			  }
		 }
                 
		 return v;

	 }


	 /** 
	  * A find method that looks for text content of an element where its attribute value matchs specified value.
	  * @param eltName The name of the element that we will search. If the element is in a namespace, then
	  *        eltName should be in this format: namespace prefix:element localname. Ie, "mets:dmdSec". 
	  * @param attName The name of the attribute that we will search. Use empty string if we don't care attribute.
	  * @param attValue String of attribute value.
	  * @return A vector of String of those elements' text context (not include its sub element's text context)
	  *          whose attribute 'attName' has the specified attribute value 'attValue'. 
	  *          null is returned if the DOM tree is empty or eltName is empty or attName is empty.
	  */
	 public Vector find(String eltName, String attName, String attValue) {

		 Vector v = new Vector();
		 Document doc = this.document;
    
		 if ( doc == null ) {
			String msg = "Error: 'document' is empty. Call parseXMLFile method before calling find method.";
			if ( printFlag ) {
				out.println( msg );
			}       
			return v;
		 }       
       
		 //check input
		 if ( eltName.equals("")) { return v; }       
		 if ( attName.equals("")) { return v; }
    
		 NodeList nodelist;
    
		 if ( eltName.indexOf(":") != -1 ) {  // namespace is used 
			  String prefix = eltName.substring(0, eltName.indexOf(":"));
			  String localName = eltName.substring( eltName.indexOf(":")+ 1);
			  String namespaceuri = retrieveUri( prefix );
       
			  nodelist = doc.getElementsByTagNameNS( namespaceuri, localName );
		 }       
		 else {  // no namespace is used 
			  nodelist = doc.getElementsByTagNameNS( "*", eltName );
		 }       

		 for ( int i =0; i< nodelist.getLength(); i++ ) {
			  org.w3c.dom.Element ele = (org.w3c.dom.Element)nodelist.item(i);

			  String attributeValue = ele.getAttribute( attName );
			  if ( attributeValue.equals( attValue ) ) {
					String eleText = getTextOfThisElementOnly ( ele );
					v.addElement( eleText );
					if ( printFlag ) {
						 //echo( ele );
						 out.println("Result : '" + eleText + "'" );
					}
			  }
                
		 }

		 return v;
	 }



	 /** 
	  * A find method that only looks for subtree of an element node.
	  * @param element_node The element node whose descedant will be searched.
	  * @param eltName The name of the element that we are searching. If the element is in a namespace, then
	  *        eltName should be in this format: namespace prefix:element localname. Ie, "mets:dmdSec". 
	  * @param attName The name of the attribute that we will search. Use empty string if we don't care attribute.
	  * @return A vector of Strings. If the attName is empty, the result is the value of eltName's Text Node 
	  *         (no descedant text nodes). If the attName is not empty, the result is the attribute value
	  *          of eltName's attName attribute. null is returned if the DOM tree is empty or eltName is empty.
	  */
	 public Vector findInSubtree(Node element_node, String eltName, String attName) {

		 Vector v = new Vector();
		 Document doc = this.document;

		 if ( doc == null ) {
			String msg = "Error: 'document' is empty. Call parseXMLFile method before calling find method.";
			if ( printFlag ) {
				out.println( msg );
			}
			return v;
		 }
           
		 //check input
		 Element element;
		 if ( element_node.getNodeType() == Node.ELEMENT_NODE ) {
			 element = (Element) element_node;
		 }
		 else {
			  return v;
		 }

		 if ( eltName.equals("")) {
			  return v;
		 }

		 NodeList nodelist;

		 if ( eltName.indexOf(":") != -1 ) {  // namespace is used
			  String prefix = eltName.substring(0, eltName.indexOf(":"));
			  String localName = eltName.substring( eltName.indexOf(":")+ 1);
			  String namespaceuri = retrieveUri( prefix );
             
			  nodelist = element.getElementsByTagNameNS( namespaceuri, localName );
		 }
		 else {  // no namespace is used
			  nodelist = element.getElementsByTagNameNS( "*", eltName );
		 }

		 if( printFlag ) { System.out.println("Found result number=" + nodelist.getLength()); }

		 for ( int i =0; i< nodelist.getLength(); i++ ) {
			  org.w3c.dom.Element ele = (org.w3c.dom.Element)nodelist.item(i);

			  if ( attName.equals("") ) { // we want element value
					String eleText = getTextOfThisElementOnly( ele );
					v.addElement( eleText );      
					if ( printFlag ) {
						 //echo( ele );
						 out.println("Result " + i + ": '" + eleText + "'" );
					}
			  }
			  else {  // we want attribute value
					String attValue = ele.getAttribute( attName );
					if ( attValue != null ) {
						v.addElement( attValue );
						if ( printFlag ) {
							//echo( ele );
							out.println("Result " + i + ": '" + attValue );
						}
					}
			  }
		 }
                 
		 return v;

	 }


	 /** 
	  * A find method that searchs subtree of an element node to find matching attribute name with matching 
	  * attribute value in a element with matching name.
	  * @param element_node The element node whose subtree will be searched.
	  * @param eltName The name of the element that we are searching. If the element is in a namespace, then
	  *        eltName should be in this format: namespace prefix:element localname. Ie, "mets:dmdSec". 
	  * @param attName The name of the attribute that we will search. Use empty string if we don't care attribute.
	  * @param attValue String of attribute value.
	  * @return A vector of String of those elements' text context (not include its sub element's text context)
	  *          whose attribute 'attName' has the specified attribute value 'attValue'. 
	  *          null is returned if the DOM tree is empty or eltName is empty or attName is empty.
	  */
	 public Vector findInSubtree(Node element_node, String eltName, String attName, String attValue) {

		 Vector v = new Vector();
		 Document doc = this.document;
    
		 if ( doc == null ) {
			String msg = "Error: 'document' is empty. Call parseXMLFile method before calling find method.";
			if ( printFlag ) {
				out.println( msg );
			}       
			return v;
		 }       
       
		 //check input
		 Element element;
		 if ( element_node.getNodeType() == Node.ELEMENT_NODE ) {
			 element = (Element) element_node;
		 }
		 else {   return v;   }

		 if ( eltName.equals("")) { return v; }       
		 if ( attName.equals("")) { return v; }
    
		 NodeList nodelist;
    
		 if ( eltName.indexOf(":") != -1 ) {  // namespace is used 
			  String prefix = eltName.substring(0, eltName.indexOf(":"));
			  String localName = eltName.substring( eltName.indexOf(":")+ 1);
			  String namespaceuri = retrieveUri( prefix );
       
			  nodelist = element.getElementsByTagNameNS( namespaceuri, localName );
		 }       
		 else {  // no namespace is used 
			  nodelist = element.getElementsByTagNameNS( "*", eltName );
		 }       

		 for ( int i =0; i< nodelist.getLength(); i++ ) {
			  org.w3c.dom.Element ele = (org.w3c.dom.Element)nodelist.item(i);

			  String attributeValue = ele.getAttribute( attName );
			  if ( attributeValue.equals( attValue ) ) {
					String eleText = getTextOfThisElementOnly ( ele );
					v.addElement( eleText );
					if ( printFlag ) {
						 //echo( ele );
						 out.println("Result : '" + eleText + "'" );
					}
			  }
                
		 }

		 return v;
	 }


	 /**
	  * A find method that using xpath as input parameter.
	  * @param xpath A String format of xpath.
	  * @return a Vector of Nodes that satiesfy the xpath query.
	  * @exception Exception
	  */
	 public Vector findNodes(String xpath) throws Exception {

		  Vector v = new Vector();
		  Document doc = this.document;
		  NodeIterator ni;

		  try {
			  Transformer serializer = TransformerFactory.newInstance().newTransformer();
			  serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			  ni = XPathAPI.selectNodeIterator(doc, xpath);
		  }
		  catch (Exception e) {
			  throw e;
		  }

		  Node n;
		  while ( (n= ni.nextNode()) != null ) {
			  v.addElement( n );

			  if ( printFlag ) {
				 if ( isTextNode( n ) ) {
					 StringBuffer sb = new StringBuffer( n.getNodeValue());
					 for ( Node nextNode = n.getNextSibling(); isTextNode(nextNode); nextNode = n.getNextSibling()) {
						 sb.append( nextNode.getNodeValue() );
					 }
					 out.println("Result: '" + sb + "'");
				 }
				 else if( isElementNode(n)) {
					 //serializer.transform(new DOMSource(n), new StreamResult(System.out));
					 out.println( "Result: '" + getTextOfThisElementOnly( n ) + "'");
				 }
				 else if (isAttributeNode(n) ) {
					 out.println( "Result: '" + n.getNodeValue() + "'");
				 }
			  }
		  }
         
		  return v;
	 }


	 /**
	  * A find method that using xpath as input parameter.
	 * @param startnode 
	  * @param xpath A String format of xpath.
	  * @return a Vector of Nodes that satiesfy the xpath query.
	  * @exception Exception
	  */
	 public Vector findNodesInSubtree(Node startnode, String xpath) throws Exception {

		  Vector v = new Vector();
		  NodeIterator ni;

		  try {
			  Transformer serializer = TransformerFactory.newInstance().newTransformer();
			  serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			  ni = XPathAPI.selectNodeIterator(startnode, xpath);
		  }
		  catch (Exception e) {
			  throw e;
		  }

		  Node n;
		  while ( (n= ni.nextNode()) != null ) {
			  v.addElement( n );

			  if ( printFlag ) {
				 if ( isTextNode( n ) ) {
					 StringBuffer sb = new StringBuffer( n.getNodeValue());
					 for ( Node nextNode = n.getNextSibling(); isTextNode(nextNode); nextNode = n.getNextSibling()) {
						 sb.append( nextNode.getNodeValue() );
					 }
					 out.println("Result: '" + sb + "'");
				 }
				 else if( isElementNode(n)) {
					 //serializer.transform(new DOMSource(n), new StreamResult(System.out));
					 out.println( "Result: '" + getTextOfThisElementOnly( n ) + "'");
				 }
				 else if (isAttributeNode(n) ) {
					 out.println( "Result: '" + n.getNodeValue() + "'");
				 }
			  }
		  }
         
		  return v;
	 }
	 
	/**
	 * @return the parsed XML file as a DOM tree
	 */
	public Document getDocument() {
		return document;
	}

	 /** Retrive an element's attribute whose name is alt_name.
	  * @param node The element node whose attributes are searched.
	  * @param alt_name The name of the attribute we are searching.
	  * @return String value of the attribute if it is found. Null if node is not element node or
	  *          the given attribute is not found.
	  */
	 public String getElementAttribute( Node node, String alt_name ) {
		 String alt_value = new String();

		 if ( node.getNodeType() != Node.ELEMENT_NODE ) {
			return null;
		 }

		 NamedNodeMap all_atts = node.getAttributes();
		 Node attribute = all_atts.getNamedItem( alt_name );
		 if ( attribute != null ) {      //attribute could be ""
			alt_value = attribute.getNodeValue();
			return alt_value;
		 }
		 else {
			return null;
		 }

	 }

	 private Hashtable getNamespaces() throws Exception {
		  Hashtable nameURIs = new Hashtable(); 
		  Document doc = this.document;
		  org.w3c.dom.Element rootEle = doc.getDocumentElement();

		  NamedNodeMap nnm = rootEle.getAttributes();
		  if ( nnm.getLength() == 0 ) {
			  String msg = "Error: Empty Document Element. No namespace can be retrieved. ";
			  if ( printFlag ) {  out.println( msg );  };
			  throw new Exception (msg);
		  }

		  for ( int i=0; i< nnm.getLength(); i++ ) {
			  Node attrNode = (Node) nnm.item(i);
			  String prefix = attrNode.getPrefix();  //ie, xmlns
			  String local = attrNode.getLocalName(); //ie, mets
			  if ( prefix == null ) continue;
			  if ( prefix.equals("xmlns") ) {
				  String uri = attrNode.getNodeValue();  //ie, http://www.loc.gov/METS/
				  nameURIs.put( local, uri );
			  }
		  }

		  return nameURIs;
	 }

	 /**
	  * Indent to the current level in multiples of basicIndent
	  */
	 private void outputIndentation() {
		 for (int i = 0; i < indent; i++) {
			 out.print(basicIndent);
		 }
	 }


	 /**
	  * Parse a xml file using global parser configuration settings. Default configuration settings are set for
	  * parsing xml files with schema. Method 'setDefaultSettings(String type) can be used to change global parser
	  * configuration settings. For example, to parse a MXF xml file (using dtd), call 'setDefaultSettings("dtd")'
	  * first, then call parseXMLFile(xmlfile). 
	  * @param xmlfile 
	  * @return Document
	  * @throws Exception 
	  */
	 public Document parseXmlFile(String xmlfile) throws Exception {

	 /*
		 // Default settings
		 boolean dtdValidate = false;  //assume all xml files we are going to process do not use DTD
		 boolean xsdValidate = true;   //assume all xml files are using schema
		 String schemaSource = null;   //assume all xml files contain schema source
		 boolean ignoreWhitespace = true; //document must hava a DTD and should be valid or nearly so
		 boolean ignoreComments = true;  //comment nodes will not be produced
		 boolean putCdataIntoText = true; //CDATA section will not be produced, since we care reading not writing
		 boolean createEntityRefs = false;//entity references will not be created, but be expanded inline
	  */

		 try {

			 Document doc = parseXmlFile( xmlfile, dtdValidate, xsdValidate, schemaSource, ignoreWhitespace,
									  ignoreComments, putCdataIntoText, createEntityRefs );

			 this.document = doc;
			 return doc;
		 }
		 catch (Exception e ) {
			 throw e;
		 }

	 }

	 /** 
	  * Use user defined settings to parse xml file againest either DTD or schema.
	  * Global parser configuration settings won't take effect.
	  * @param xmlfile The file name of the xml file that will be parsed.
	  * @param dtdValidateFlag Set to true if xmlfile is with a DTD. Set to false if it uses schema.
	  * @param xsdValidateFlag 
	  * @aram xsdValidateFlag Set to true if xmlfile is with schemas. Set to false if it uses DTD.
	  * @param schemaSourceString To specify schema locations other than schemas included in xmlfile.
	  * @param ignoreWhitespaceFlag If set to true, text nodes will not include ignorable white spaces.
	  * @param ignoreCommentsFlag 
	  * @param putCDATAIntoTextFlag If set to false, results will contain CDATA. In most situation it 
	  *        should be set to true, especially if we just reading the document and are not going to write it back again.
	  * @param createEntityRefsFlag If set to false, results will contain entity reference nodes.
	  *        Since validation feature overrides the expand-entity-references feature,
	  *        which means if a parser is validating, then it will expand entity references, even
	  *        this feature is set to false, we will always get expanded entity reference nodes inline !!
	  * @return Parsed Docuement
	 * @throws Exception 
	  */
	 public Document parseXmlFile(String xmlfile, 
								  boolean dtdValidateFlag, 
								  boolean xsdValidateFlag,
								  String schemaSourceString, 
								  boolean ignoreWhitespaceFlag, 
								  boolean ignoreCommentsFlag,
								  boolean putCDATAIntoTextFlag, 
								  boolean createEntityRefsFlag 
								  ) throws Exception {

		 //Note: Input parameters overwrite global default settings

		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		  //Configuring DocumentBuilderFactory

		  //Default is false. But we should always set it to true.
		  dbf.setNamespaceAware(true);

		  //Validate the document against DTD or schema or not.
		  dbf.setValidating( dtdValidateFlag || xsdValidateFlag );
		  if ( xsdValidateFlag ) {
			 try {
				 dbf.setAttribute( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
			 }
			 catch ( IllegalArgumentException x ) {
				 // This can happen if the parser does not support JAXP 1.2
				 String msg = new String( "Error: JAXP DocumentBuilderFactory attribute not recognized: " 
											 + JAXP_SCHEMA_LANGUAGE
											 +  ".\nCheck to see if parser conforms to JAXP 1.2 spec." );
				 if ( printFlag ) {
					   out.println( msg );
				 }
				 throw new Exception( msg );
			 }
		  }


		  //Actually, schemas are included in mets xml files, so we don't specify schema source here.
		  if ( schemaSourceString != null ) {
			 dbf.setAttribute(JAXP_SCHEMA_SOURCE, new File(schemaSourceString)); 
		  }

		  //Default value = false, means comment nodes will be produced. We should set it to true
		  //   because we don't care about comment nodes.
		  dbf.setIgnoringComments( ignoreCommentsFlag );

		  //Default is false, means include text nodes for ignorable white space.
		  //   To set it to true, document must have a DTD and should be valid or nearly so.
		  dbf.setIgnoringElementContentWhitespace( ignoreWhitespaceFlag );

		  //Default is false, means result will contain CDATA nodes. 
		  //   But in most situation it should be set to true, especially if we just reading the document
		  //   and are not going to write it back again.
		  dbf.setCoalescing( putCDATAIntoTextFlag );


		  //The default is true. But validation feature overrides the expand-entity-references feature,
		  //   which means if a parser is validating, then it will expand entity references, even
		  //   if this feature is set to false. Since we always validate the document, we will always
		  //   get expanded entity reference nodes inline !!
		  dbf.setExpandEntityReferences( !createEntityRefsFlag );

		  DocumentBuilder db = dbf.newDocumentBuilder();
      	  WebCacheResolver resolver = WebCacheUtils.getResolver();
		  db.setEntityResolver(resolver);
 /*
		  // Check for the traversal module
		  DOMImplementation impl = db.getDOMImplementation();
		  if (!impl.hasFeature("traversal", "2.0")) {
			 System.out.println( "A DOM implementation that supports traversal is required.");  
		  }
      
		  //Check DOM Level3 XPath
		  if (!impl.hasFeature("XPath", "3.0")) {
			 System.err.println("This DOM implementation does not support XPath");
		  }
 */

		  //Error handler is set to receive validity errors
		  db.setErrorHandler ( new MyErrorHandler ( out ));

		  // parse the input file
		  Document doc = db.parse( new File(xmlfile) );
		  doc.normalize();  // Merges all adjacent text nodes and deletes empty text nodes.
							// But it does not merge CDATA section

		  //Set global variable so other methods can use it
		  this.document = doc;
		  if ( xsdValidateFlag ) {
			 this.namespaces= getNamespaces();
		  }
         
		  return doc;
	 }

	 /**
	  * Echo common attributes of a DOM2 Node and terminate output with an
	  * EOL character.
	  * @param n 
	  */
	 private void printlnCommon(Node n) {
		 out.print(" nodeName=\"" + n.getNodeName() + "\"");

		 String val = n.getNamespaceURI();
		 if (val != null) {
			 out.print(" uri=\"" + val + "\"");
		 }

		 val = n.getPrefix();
		 if (val != null) {
			 out.print(" pre=\"" + val + "\"");
		 }

		 val = n.getLocalName();
		 if (val != null) {
			 out.print(" local=\"" + val + "\"");
		 }

		 val = n.getNodeValue();
		 if (val != null) {
			 out.print(" nodeValue=");
			 if (val.trim().equals("")) {
				 // Whitespace
				 out.print("[WS]");
			 } else {
				 out.print("\"" + n.getNodeValue() + "\"");
			 }
		 }
		 out.println();
	 }

	 private String retrieveUri( String name ) {
		  String value = (String)namespaces.get( name );
		  return value;
	 }

	 /**
	  * Control print message or not
	  * @param flag Set to true if want see print out. Set to false if want quite execution.
	  */
	 public void setDebug( boolean flag ) {
		 this.printFlag = flag;
	 }

	 /** 
	  * Change global parser configuration settings for either dtd type or schema type of xml file. 
	  * @param type If set to "dtd", global variable dtdValidate will be set to true and xsdValidate will be
	  *        set to false. If type is set to "schema", dtdValidate will be set to false and xsdValidate will
	  *        be set to true. Other values are invalid.
	  * @exception Exception will be thrown if invalid input parameter is used.
	  */
	 public void setDefaultSettings( String type ) throws Exception {

		 if ( type.equalsIgnoreCase("dtd") ) {
			  dtdValidate = true;             //dtd validation
			  xsdValidate = false;   
			  schemaSource = null;   
			  ignoreWhitespace = true; 
			  ignoreComments = true;  
			  putCdataIntoText = true; 
			  createEntityRefs = false;

		 }
		 else if ( type.equalsIgnoreCase("schema") ) {
			  dtdValidate = false;  
			  xsdValidate = true;             //schema validation 
			  schemaSource = null;            //assume all xml files contain schema source
			  ignoreWhitespace = true;        //document must hava a DTD and should be valid or nearly so
			  ignoreComments = true;          //comment nodes will not be produced
			  putCdataIntoText = true;        //CDATA section will not be produced, since we care reading not writing
			  createEntityRefs = false;       //entity references will not be created, but be expanded inline

		 }
		 else {
			  throw new Exception("Invalid input parameter of method 'setDefaultSettings'. Use only 'dtd' or 'schema'.");
		 }
	 }

}
