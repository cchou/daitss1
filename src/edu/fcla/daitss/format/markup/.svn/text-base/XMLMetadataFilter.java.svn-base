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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.exception.FatalException;

/**
 * XMLMetadataHandler intercepts a SAX parsing stream to record
 * metadata about an XML file.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class XMLMetadataFilter extends XMLFilterImpl{

	/**
	 * Whether or not the parser is at the start of the root element
	 */
	private static boolean atRootElement = true;
	
	/**
	 * Initialize the static class members.
	 */
	public static void init() {
		atRootElement = true;
	}

	/** 
	 * Locator that is used to find the location within the XML
	 * file where events occurred, and also to identify the public and/or system ID
	 * of this file. To supply a Locator is not a requirement of SAX
	 * parsers, so there is no guarantee that this Locator will be
	 * initialized in the setLocator(Locator) method.
	 */
	private Locator docLocator = null;

	/** Characters. 
	 * This is where the '&' and '\<' characters would need to be substituted
	 * with &amp, and &gt; if these characters were going to be printed out.
	 * 
	 * @param ch
	 * @param length
	 * @param offset
	 * @throws SAXException
	 */
	public void characters(char[] ch, int offset, int length)
		throws SAXException {
		// do nothing
	}

	/** 
	 * Ends document. 
	 * Figures out the character encoding and the xml version.
	 * 
	 * @throws SAXException
	 */
	public void endDocument() throws SAXException {
		// get the encoding before we finish
		String encoding = getEncoding();
		if (encoding != null) {
			try {
				XMLSAXParser.getXFile().getDoc().setCharSet(encoding);
				XMLSAXParser.getXFile().getDoc().setCharSetOrigin(Bitstream.ORIG_ARCHIVE);
			} catch (FatalException e) {
				// wrap it in a SAXException
				// this should never happen
				throw new SAXException(e);
			}
		}
		String version = getXmlVersion();
		if (!version.equals("1.0")) {
			// document in an unknown version - note this anomaly
			try {
				XMLSAXParser.getXFile().addAnomaly(
						XMLSAXParser.getXFile().getAnomsPossible().
						getSevereElement(
						XMLAnomalies.XML_UNKNOWN_VERSION));
			} catch (FatalException e) {
				// wrap it in a SAXException
				throw new SAXException(e);
			}
		}
	}

	/**
	 * Ends element.
	 * 
	 * @param uri
	 * @param localName
	 * @param qname
	 */
	public void endElement(String uri, String localName, String qname) {
		// do nothing
	}

	/** Ends prefix mapping. 
	 * 
	 * @param prefix
	 * @throws SAXException
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
		// do nothing
	}

	/**
	 * Determines the character encoding used by the XML file.
	 * 
	 * @return the character encoding used by the XML file.
	 * @throws SAXException
	 */
	private String getEncoding() throws SAXException {
		String encoding = null;
		Method getEncoding = null;
		try {
			getEncoding =
				docLocator.getClass().getMethod("getEncoding", new Class[] {
			});
			if (getEncoding != null) {
				try {
					encoding = (String) getEncoding.invoke(docLocator);
				} catch (IllegalArgumentException e1) {
					// wrap it in a SAXException
					throw new SAXException(e1);
				} catch (IllegalAccessException e1) {
					// wrap it in a SAXException
					throw new SAXException(e1);
				} catch (InvocationTargetException e1) {
					// wrap it in a SAXException
					throw new SAXException(e1);
				}
			}
		} catch (SecurityException e) {
			// wrap it in a SAXException
			throw new SAXException(e);
		} catch (NoSuchMethodException e) {
			// wrap it in a SAXException
			throw new SAXException(e);
		}
		return encoding;
	}

	/**
	 * Determines the version of XML the XML file complies with.
	 * 
	 * @return the version of XML the XML file complies with.
	 * @throws SAXException
	 */
	private String getXmlVersion() throws SAXException {
		String version = null;
		Method getXMLVersion = null;
		try {
			getXMLVersion =
				docLocator.getClass().getMethod("getXMLVersion", new Class[] {
			});
			if (getXMLVersion != null) {
				try {
					version = (String) getXMLVersion.invoke(docLocator);
				} catch (IllegalArgumentException e1) {
					// wrap it in a SAXException
					throw new SAXException(e1);
				} catch (IllegalAccessException e1) {
					// wrap it in a SAXException
					throw new SAXException(e1);
				} catch (InvocationTargetException e1) {
					// wrap it in a SAXException
					throw new SAXException(e1);
				}
			}
		} catch (SecurityException e) {
			// wrap it in a SAXException
			throw new SAXException(e);
		} catch (NoSuchMethodException e) {
			// wrap it in a SAXException
			throw new SAXException(e);
		}
		return version;
	}

	/** 
	 * Processes a processing instruction. 
	 * The format for a processing instruction is <?target data?>.
	 * 
	 * @param data
	 * @param target
	 * @throws SAXException
	 */
	public void processingInstruction(String target, String data)
		throws SAXException {
		// do nothing
	}

	/** 
	 * Sets the document locator.
	 * This is called (if it's going to be called at all) before the startDocument()
	 * method. This method is only called if the parser being used implements
	 * the setting of a Locator.
	 * 
	 * @param locator document locator
	 */
	public void setDocumentLocator(Locator locator) {
		// save it away so that we can retrieve locations in the
		// file later
		this.docLocator = locator;
		
		super.setDocumentLocator(locator);
	}

	/** 
	 * Starts the document. 
	 * 
	 * @throws SAXException
	 */
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	
	/**
	 * Starts the element.
	 * This is relying on the parser to be namespace-aware. If the parser isn't
	 * namespace-aware then the localName could be the empty String and in that
	 * case the qname would contain the element name.
	 * 
	 * @param uri
	 * @param localName
	 * @param qname
	 * @param attributes
	 * @throws SAXException
	 */
	public void startElement(
		String uri,
		String localName,
		String qname,
		Attributes attributes)
		throws SAXException {

		boolean atRoot = atRootElement;
		atRootElement = false;
		
		switch (XMLSAXParser.getParseActivity()){
			case XMLSAXParser.ACTION_EXT_META:
			
				// record the root element schema if namespaces are used
				if (atRoot && uri != null && !uri.equals("")) {
					// this is the root schema
					try {
						XMLSAXParser.getXFile().getDoc().setMarkupLanguage(uri);
					} catch (FatalException e) {
						// wrap it in a SAXException
						throw new SAXException(e);
					}
				}
				 
				// examine the attributes
				//if (attributes != null) {
				// Changed this to only applying to the root element because
				// of cases where the document was not schema-described but it
				// did point to a schema later in the document
				if (atRoot && attributes != null) {
					for (int i = 0; i < attributes.getLength(); i++) {
						String attrLocalName = attributes.getLocalName(i);
						//String attrQName = attributes.getQName(i);
						String attrURI = attributes.getURI(i);
						//String attrType = attributes.getType(i);
						String attrValue = attributes.getValue(i);

						try {
							if ((attrLocalName.equals("schemaLocation")
								|| attrLocalName.equals("noNamespaceSchemaLocation"))
									&& NamespaceURI.isXmlSchemaInstanceNs(attrURI)) {
									
								// record that it uses a W3D XML schema
								XMLSAXParser.getXFile().addFormatAttribute(XML.Attribute.HAS_W3D_SCHEMA);
								XMLSAXParser.getXFile().getDoc().setSchemaType(
															MLDocument.ST_W3C_XML_SCHEMA);
								
								// set the root element schema if this file doesn't use namespaces
								// (uses the noNamespaceSchemaLocation attribute)
								if (atRoot && 
									attrLocalName.equals("noNamespaceSchemaLocation") &&
									attrValue != null){
										XMLSAXParser.getXFile().getDoc().setMarkupLanguage(attrValue);
								}
							}
						} catch (FatalException e) {
							// wrap it in a SAXException
							throw new SAXException(e);
						}
					}
				}
				break;
			default : // do nothing
		}
		
	}
	
	/**
	 * Starts prefix mapping.
	 * @param prefix
	 * @param uri
	 * @throws SAXException
	 */
	public void startPrefixMapping(String prefix, String uri)
		throws SAXException {
		// do nothing
	}
	
	/**
	 * Receives the name and location of a DTD used by this file.
	 * Not an inherited method from XMLFilterImpl.
	 * This method takes in DTD information from a parse stream 'upriver' from it.
	 * This is a workaround to the problem that this class can't have a LexicalHandler set
	 * on it which is where the startDTD would have been. It's essentially being caught upstream,
	 * and sent back downstream.
	 * 
	 * @param name
	 * @param publicId
	 * @param systemId
	 * @throws SAXException
	 */
	public void takeStartDtd(String name, String publicId, String systemId) 
		throws SAXException{
	    
		switch (XMLSAXParser.getParseActivity()){
			case XMLSAXParser.ACTION_EXT_META:
				try {
					XMLSAXParser.getXFile().addFormatAttribute(XML.Attribute.HAS_DTD);
					XMLSAXParser.getXFile().getDoc().setSchemaType(MLDocument.ST_DTD);
				} catch (FatalException e1) {
					// wrap it in a SAXException
					// won't happen because were passing in a known constant
					throw new SAXException(e1);
				}
				if (name != null) {
					try {
						XMLSAXParser.getXFile().getDoc().setMarkupLanguage(name);
					} catch (FatalException e) {
						// wrap it in a SAXException
						// shouldn't happen - we're checking that it's not null
						throw new SAXException(e);
					}
				}
				break;
			default :
				// do nothing
		}
	}

}
