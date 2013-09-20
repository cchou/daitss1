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

import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * XMLLinkHandler intercepts a SAX parsing stream to
 * recognize links and take actions on these links.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class XMLLinkFilter extends XMLFilterImpl {
    
	/**
	 * Whether or not the parser is at the start of the root element
	 * (actually whether or not it has just finished parsing the start of the
	 * root element)
	 */
	private static boolean atRootElement = true;
	
	/**
	 * A filter that can write out XML.
	 */
	private static XMLWriterFilter writerFilter = null;
	
	/**
	 * Initialize the static class members.
	 */
	public static void init() {
		atRootElement = true;
		writerFilter = null;
	}
	
	/** 
	 * Locator that is used to find the location within the XML
	 * file where events occurred, and also to identify the public and/or system ID
	 * of this file. To supply a Locator is not a requirement of SAX
	 * parsers, so there is no guarantee that this Locator will be
	 * initialized in the setLocator(Locator) method.
	 */
	private Locator docLocator = null;
	
	/**
	 * Constructs a <code>Link</code> object. 
	 * This is added to the <code>DataFile</code> object representing 
	 * the XML file being parsed AFTER calling this method.
	 * Uses configuration file properties to possibly override the
	 * <code>shouldRetrieve</code> argument to reflect the archive's
	 * harvesting policies.
	 * 
	 * @param linkAlias the name of the link as it appears in the file
	 * @param isRootSchema whether or not this points to the root element schema
	 * @param isSchema whether or not this link points to a schema
	 * @param shouldRetrieve whether or not the link should be retrieved
	 * @return the Link object
	 * @throws SAXException
	 */
	private Link constructLink(String linkAlias, boolean isRootSchema, 
		boolean isSchema, String shouldRetrieve) 
		throws SAXException {
		
		Link lk = null;
		boolean retrieve;
		
		try {
			lk =
                // TODO this should probably be in the workingDir
				new Link(
					XMLSAXParser.getXFile().getIp().getWorkingPath(),
					XMLSAXParser.getXFile().getPath(),
					linkAlias,
					XMLSAXParser.getXFile().getIp().getLinksDir(),
					XMLSAXParser.getXFile().getOrigin(),
					XMLSAXParser.getXFile().getOriginalUri());
			
			// first set whether or not it should be retrieved based on the argument
			// passed in
			retrieve = (shouldRetrieve.equalsIgnoreCase("true"))? true : false;
			
			// Now possibly override the shouldRetrieve argument.
			// See if this is an XML descriptor file that is being parsed and if so
			// if it should be treated specially (retrieve all its links)
			if (ArchiveProperties.getInstance().
					getArchProperty("HARVEST_XML_DESCRIPTORS_ALL").equalsIgnoreCase("true") &&
				(XMLSAXParser.getXFile().isDescriptor())) {
				retrieve = true;
			}
		} catch (FatalException e){
			// wrap it in a SAXException
			throw new SAXException(e);
		} catch (URISyntaxException e) {
			// wrap it in a SAXException
			throw new SAXException(e);
		}
	
		lk.setShouldRetrieve(retrieve);
		lk.setIsRootSchema(isRootSchema);
		lk.setIsSchema(isSchema);
		
		return lk;
	}
	
	/**
	 * Constructs a 'normalized' path to one locally-held file x
	 * from another file y to replace the way the link is currently
	 * represented in y. The normalized path is relative to this
	 * file.
	 * 
	 * The second parameter determines which file gets linked to.
	 * There are some locations in the file where we always want to
	 * link to the localized version of a file (for example schema).
	 * There are other locations where we don't want to do this - for
	 * example in the file elements of a mets file. When <code>useLocPath</code>
	 * is set to true, the localized version of a file will be set as the
	 * link target. When it is <code>false</code>, the file itself,
	 * whether or not it is localized will be used. The localized version 
	 * is set in the DataFile member <code>localizedFilePath</code>, the file itself
	 * is set in the DataFile member <code>path</code>.
	 * 
	 * @param linkAlias link path as it looks before normalization
	 * @param useLocPath whether to use the localized path of this path
	 * @return the normalized link path
	 * @throws FatalException if the parameter is null
	 */
	private String constructRelLocalPath(String linkAlias, boolean useLocPath) 
		throws FatalException {
	    String methodName = "constructRelLocalPath(String)";
	    
	    String newPath = null;
	    boolean done = false;
	    
	    // check args
	    if (linkAlias == null){
	        Informer.getInstance().fail(this,
                    methodName,
                    "Null argument",
                    "linkAlias",
                    new FatalException("Can't be null."));
	    }
	    
	    try {
	        //System.out.println("Getting df for linkAlias: " + linkAlias);
            DataFile df = XMLSAXParser.getXFile().getDfFromLinkAlias(linkAlias);
            if (df == null) {
                // link was not successfully retrieved - return the same
                // path given to this method
                newPath = linkAlias;
                done = true;
            }
            // get the path to the linked-to file and make it relative
            if (!done) {
                // df.getLocalizedFilePath() will be df.getPath()
                // when in phase 1 of localization, but it will
                // be different in phase 2
                String linkToUse = null;
                if (useLocPath) {
                    // want the localized absolute path
                    linkToUse = df.getLocalizedFilePath();
                } else {
                    // want the absolute path
                    linkToUse = df.getPath();
                }
                                
                // make path relative to the file containing the link
                newPath = FileUtil.getRelPathFrom(XMLSAXParser.getXFile().getPath(),
                        linkToUse);
            }
            
        } catch (FatalException e) {
            // linkAlias was null - won't happen, but account for this anyway
	        Informer.getInstance().fail(this,
                    methodName,
                    "Null argument",
                    "linkAlias, df.getPath(), df.getOid(), or df.getFileExt()",
                    new FatalException("Can't be null."));
        }
	    
        //System.out.println("newPath: " + newPath);
	    return newPath;
	}

	/**
	 * Handles the end of an element.
	 * Probably don't need to override XMLFilterImpl's implementation
	 * of this method...
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws SAXException
	 */
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
		super.endElement(uri, localName, qName);
	}
	
	/**
	 * Looks for links within notation declarations in an internal DTD.
	 * 
	 * @param name notation name
	 * @param publicId notation's public identifier location
	 * @param systemId notation's system location
	 * @throws SAXException
	 */
	public void notationDecl(String name, String publicId, String systemId)
		throws SAXException {
		//System.out.println("Received a not decl. systemId: " + systemId);
		
		if (systemId != null && !systemId.equals("")){
			// found a link within a notation declaration
			if (XMLSAXParser.getParseActivity() == 
				XMLSAXParser.ACTION_EXT_META) {
			    // EXTRACTING METADATA			
				try {
					Link lk = constructLink(systemId, false, false, 
						ArchiveProperties.getInstance().getArchProperty("HARVEST_XML_NOTS"));
					XMLSAXParser.getXFile().addLink(lk);
				} catch (FatalException e) {
					// wrap it in a SAXException
					throw new SAXException(e);
				}
			} else if (XMLSAXParser.getParseActivity() == 
				XMLSAXParser.ACTION_REP_LKS) {
			    // NORMALIZING
				
			    // turn the systemId into a relative path from the Xml
			    /// file, replacing its file title with the linked-to
			    // file's DFID + '.' + normalized extension
			    String newSystemId = null;
                try {
                    
                    newSystemId = constructRelLocalPath(systemId, true);
                    
                    // switch out systemId for the next filter
    			    systemId = newSystemId;
    			    
                } catch (FatalException e) {
                    // systemId was null - won't happen
                    // but anyway, wrap it
                    throw new SAXException(e);
                }
                
			}
		}

		super.notationDecl(name, publicId, systemId);
	}
	
	/**
	 * Handles links in processing instructions.
	 * 
	 * @param target the processing instruction target
	 * @param data the text following the target
	 * @throws SAXException
	 */
	public void processingInstruction(String target, String data) 
		throws SAXException {

		super.processingInstruction(target, data);
	}
	
	/** Sets the document locator.
	 * This is called (if it's going to be called at all) before the startDocument()
	 * method. This method is only called if the parser being used implements
	 * the setting of a Locator.
	 * 
	 * @param locator the document locator
	 */
	public void setDocumentLocator(Locator locator) {
		// save it away so that we can retrieve locations in the
		// file later
		this.docLocator = locator;
		
		super.setDocumentLocator(locator);
	}
	
	/**
	 * Handles the start of an XML document.
	 * Probably don't need to override XMLFilterImpl's implementation
	 * of this method...
	 * 
	 * @throws SAXException
	 */
	public void startDocument()
		throws SAXException {
			super.startDocument();
	}

	/**
	 * Handles the links that can be found in the start of an element.
	 * This is where most of the links are detected, usually within attributes.
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
			String markupLanguage = null;
			
			// Used only used when 
			// XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS
			AttributesImpl newAttributes = new AttributesImpl();
			
				
			if (atRoot && uri != null && !uri.equals("")){
				// remember the root element schema if this file uses namespaces
				// will be used to determine if a linked-to schema is the root element schema
				markupLanguage = uri;
			}
				
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					String attrLocalName = attributes.getLocalName(i);
					String attrQName = attributes.getQName(i);
					String attrURI = attributes.getURI(i);
					String attrType = attributes.getType(i);
					String attrValue = attributes.getValue(i);
					StringBuffer newAttrValue = new StringBuffer();

					try {
						if ((attrLocalName.equals("schemaLocation")
							|| attrLocalName.equals("noNamespaceSchemaLocation"))
								&& NamespaceURI.isXmlSchemaInstanceNs(attrURI)) {
							
							// found a link (value or values of the schemaLocation attribute in
							// the XMLSchema namespace)
							if (attrLocalName.equals("schemaLocation") && 
								attrValue != null && !attrValue.equals("")){
								// attr: XML SCHEMA INSTANCE NAMESPACE:schemaLocation
								
								// this is a flag to set when the root element's
								// namespace URI is seen to know that the next attribute value
								// will be the location of the root element schema
								boolean nextIsRootSchema = false;
								
								StringTokenizer st = new StringTokenizer(attrValue);
								
								// make sure that pairs exist for the schemaLocation attribute value
								if ((st.countTokens() % 2) == 0) {
									int j = 0;
									while (st.hasMoreTokens()){
										String URI = st.nextToken();
										if ((j % 2) == 1) {
											// looking at the schema location
											if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
												//	add link to DataFile
												 Link lk = null;
												 if (nextIsRootSchema) {
													// this is the root schema
													lk = constructLink(URI, true, true,
														ArchiveProperties.getInstance().getArchProperty("HARVEST_XML_XSI_SLOC"));
												 } else {
												 	// this is a schema but not the root schema
													lk = constructLink(URI, false, true,
														ArchiveProperties.getInstance().getArchProperty("HARVEST_XML_XSI_SLOC"));
												 }
												 XMLSAXParser.getXFile().addLink(lk);
											} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
												// replace the schema location with
											    // the normalized path
											    String newUri = constructRelLocalPath(URI, true);
										        
										        // pass on the changed schema location to next filter
										        newAttrValue.append(" " + newUri);
											        
											}
											nextIsRootSchema = false;
										} else {
											// looking at the uri - lets see if its the root schema uri
											if (markupLanguage != null &&
												markupLanguage != "" && 
												URI.equals(markupLanguage)){
													nextIsRootSchema = true;
											}
											
											// pass on the namespace URI unchanged to next filter
											if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
											    // put a space between uri, schema location pairs
										        if (j != 0) newAttrValue.append(" ");
											    newAttrValue.append(URI);
											}
										}
										j++;
									}
								} else {
									//	error - this must be even because they are pairs
									if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
										XMLSAXParser.getXFile().addAnomaly(
												XMLSAXParser.getXFile().getAnomsPossible().
												getSevereElement(XMLAnomalies.XML_BAD_SCHEMALOCATION));
									} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
									    // pass it on to next filter unchanged
									    newAttrValue.append(attrValue);
									}
								}
							} else if (attrLocalName.equals("noNamespaceSchemaLocation") 
											&& attrValue != null 
											&& !attrValue.equals("")){
								// attr: XML SCHEMA INSTANCE NAMESPACE:noNamespaceSchemaLocation
								StringTokenizer st = new StringTokenizer(attrValue);
								if ((st.countTokens() != 1)) {
									// error - there should only be a single value here
									if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
										XMLSAXParser.getXFile().addAnomaly(
												XMLSAXParser.getXFile().getAnomsPossible().
												getSevereElement(XMLAnomalies.XML_BAD_SCHEMALOCATION));
									} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
									    // pass it on to next filter unchanged
									    newAttrValue.append(attrValue);
									}
								} else {
									// looking at the schema location
									String URI = st.nextToken();
									if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
										//	add link to DataFile
										Link lk = null;
										// see if its the root schema
										if (atRoot){
											// root schema
											lk = constructLink(URI, true, true,
												ArchiveProperties.getInstance().getArchProperty("HARVEST_XML_XSI_NOSLOC"));
										} else {
											// schema but not the root schema
											lk = constructLink(URI, false, true,
												ArchiveProperties.getInstance().getArchProperty("HARVEST_XML_XSI_NOSLOC"));
										}
										XMLSAXParser.getXFile().addLink(lk);
									} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
									    
										// replace schema location with normalized path
									    String newUri = constructRelLocalPath(URI, true);
								        
								        // pass on the changed schema location to next filter
								        newAttrValue.append(newUri);
									    
									}
								}
							} 
						} else if (attrLocalName.equals("href") && 
											attrValue != null && !attrValue.equals("") &&
											NamespaceURI.isXmlLinkSchemaNs(attrURI)) {
							// attr: XLINK NAMESPACE:href
							if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
								Link lk = null;
								// construct the link
								lk = constructLink(attrValue, false, false,
										ArchiveProperties.getInstance().
											getArchProperty("HARVEST_XML_XLINK_HREF"));
								XMLSAXParser.getXFile().addLink(lk);
							} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
								// replace the path with the rel path
							    String newHrefValue = constructRelLocalPath(attrValue, false);
							    
							    // pass the changed href value to next filter
							    newAttrValue.append(newHrefValue);
							}
						} else if (attrLocalName.equals("schemaLocation") && attrValue != null && 
									!attrValue.equals("") && uri != null && !uri.equals("") &&
									NamespaceURI.isXmlSchemaNs(uri) &&
									(localName.equals("import") || localName.equals("include") || 
										localName.equals("redefine"))) {
							// XMLSCHEMA NAMESPACE:import attr:schemaLocation or
							// XMLSCHEMA NAMESPACE:include attr:schemaLocation or
							// XMLSCHEMA NAMESPACE:redefine attr:schemaLocation
							if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
								String property = null, value = null;
								if (localName.equals("import")){
									property ="HARVEST_XML_IMPORT_SLOC";
								} else if (localName.equals("include")){
									property ="HARVEST_XML_INCLUDE_SLOC";
								} else if (localName.equals("redefine")){
									property = "HARVEST_XML_REDEFINE_SLOC";
								} // had to be one of these
								
								// see whether or not this link should be retrieved
								value = ArchiveProperties.getInstance().getArchProperty(property);
								
								// record the schema link 
								Link lk = constructLink(attrValue, false, true, value);
								XMLSAXParser.getXFile().addLink(lk);
							} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
								//	replace the schema location with the normalized path
							    String newLocation = constructRelLocalPath(attrValue, true);
							    
							    // pass the changed schema location value to next filter
							    newAttrValue.append(newLocation);
							}
						} else if (attrLocalName.equals("href") && attrValue != null &&
							!attrValue.equals("") && uri != null && !uri.equals("") &&
							NamespaceURI.isXslNs(uri) &&
							(localName.equals("import") || localName.equals("include"))){
								// XSL NAMESPACE:import attr:href or
								// XSL NAMESPACE:include attr:href 
								if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
									String property = null, value = null;
									if (localName.equals("import")){
										property ="HARVEST_XML_XSLT_IMPORT_HREF";
									} else if (localName.equals("include")){
										property ="HARVEST_XML_XSLT_INCLUDE_HREF";
									} // had to be one of these
									
									// see whether or not this link should be retrieved
									value = ArchiveProperties.getInstance().getArchProperty(property);
									
									// record the schema link
									Link lk = constructLink(attrValue, false, true, value);
									XMLSAXParser.getXFile().addLink(lk);
								} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
									// replace the href location with the normalized path
								    String newHrefValue = constructRelLocalPath(attrValue, true);
								    
								    // pass the changed href value to the next filter
								    newAttrValue.append(newHrefValue);
								}	
						} else if (attrLocalName.equals("select") && attrValue != null &&
							!attrValue.equals("") && uri != null && !uri.equals("") &&
							NamespaceURI.isXslNs(uri) && localName.equals("value-of")) {
								// see if its a document function
								Matcher matcher = null;
								// this pattern will match href = "any URI" - windows or *nix
								// it takes into account that another 'attribute' may come before href
								// and that the URI can have spaces
								// the URI must have single quotes (href='URI') delimitting the URI
								Pattern tempPattern = Pattern.compile("document\\(\'([a-zA-Z|\\d|/|\\\\|:|\\.|\\s]+)\'\\)");
								
								matcher = tempPattern.matcher(attrValue);
								if (matcher.lookingAt()){
									// XSL NAMESPACE:value-of attribute:select ; document function parameter
									String linkAlias = matcher.group(1);
									if (linkAlias != null && !linkAlias.equals("")){
										if (XMLSAXParser.getParseActivity() == 
													XMLSAXParser.ACTION_EXT_META) {
											Link lk = constructLink(linkAlias, false, false, 
												ArchiveProperties.getInstance().
													getArchProperty("HARVEST_XML_XSLT_DOCFUNC"));
											try {
												XMLSAXParser.getXFile().addLink(lk);
											} catch (FatalException e) {
												// wrap it in a SAXException
												throw new SAXException(e);
											}
										} else if (XMLSAXParser.getParseActivity() == 
											XMLSAXParser.ACTION_REP_LKS) {
											// switch the URI with the normalized path
										    String newUri = constructRelLocalPath(linkAlias, true);
										    
										    // pass the nromalized path to the next filter
										    newAttrValue.append(newUri);
										}
									}
								}
						} else if (attrLocalName.equals("href") && attrValue != null &&
							!attrValue.equals("") && uri != null && !uri.equals("") &&
							NamespaceURI.isXIncludeNamespace(uri) &&
							localName.equals("include")){
								// XINCLUDE NAMESPACE:include attr:href
								if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_EXT_META) {
									// record the non-schema link
									Link lk = constructLink(attrValue, false, false, 
											ArchiveProperties.getInstance().
												getArchProperty("HARVEST_XML_XINCLUDE_HREF"));
									XMLSAXParser.getXFile().addLink(lk);
								} else if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
									// switch the href value with the normalized path
								    String newHrefValue = constructRelLocalPath(attrValue, true);
								    
								    // pass on the changed path to the next filter
								    newAttrValue.append(newHrefValue);
								}	
						} else {
						    // is some attribute were not interested in
						    
						    // pass it on unchanged to the next filter if we are replacing links
						    if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS){
						        newAttrValue.append(attrValue);
						    }
						}
						
					} catch (FatalException e) {
						// wrap it in a SAXException
						throw new SAXException(e);
					}
					
					// done processing the attribute
					// if we are replacing links then add the attribute to the new attributes set
					// replacing the attribute value with the normalized attribute value
					if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
					    
					    /*
					    System.out.println("Adding attribute: attrURI: " + attrURI + " attrLocalName: " 
					    + attrLocalName + " attrQName: " + attrQName + " attrType: " 
					    + attrType + " newAttrValue: " + newAttrValue.toString());
					    */
					     
					    newAttributes.addAttribute(attrURI, attrLocalName, attrQName, 
				            attrType, newAttrValue.toString());
					}
					
				} // done for loop
			} // done (if attributes != null)
			
			// switch out the attributes if we are replacing links
			if (XMLSAXParser.getParseActivity() == XMLSAXParser.ACTION_REP_LKS) {
			    attributes = newAttributes;
			}
			
			// send on to next stream
			super.startElement(uri, localName, qname, attributes);
	}
	
	/**
	 * Receives the name and location of an external entity.
	 * Not an inherited method from XMLFilterImpl.
	 * This method takes in DTD information from a parse stream 'upriver' from it.
	 * This is a workaround to the problem that this class can't have a LexicalHandler set
	 * on it which is where the externalEntityDecl would have been. It's essentially 
	 * being caught upstream, and sent back downstream.
	 * 
	 * Applies to general as well as parameter entities.
	 * When it's a parameter entity the name will startt with '%'.
	 * 
	 * @param name
	 * @param publicId
	 * @param systemId
	 * @throws SAXException
	 */
	public void takeExternalEntityDecl(String name, String publicId,
				String systemId) throws SAXException {
	    
		// see if its a parameter or general entity
		if (name != null && !name.equals("") && systemId != null &&
				!systemId.equals("")) {
			switch (XMLSAXParser.getParseActivity()){
				case XMLSAXParser.ACTION_EXT_META:
					if (name.charAt(0) == '%'){
						// parameter entity declaration
						// add link to file
						try {
							Link lk = constructLink(systemId,
									false, false,
								ArchiveProperties.getInstance().
									getArchProperty("HARVEST_XML_PENTS"));
							XMLSAXParser.getXFile().addLink(lk);
						} catch (FatalException e) {
							// wrap it in a SAXException
							throw new SAXException(e);
						} 
					} else {
						// general entity declaration
						// add link to file
						try {
							Link lk = constructLink(systemId,
									false, false,
								ArchiveProperties.getInstance().
									getArchProperty("HARVEST_XML_GENTS"));
							XMLSAXParser.getXFile().addLink(lk);
						} catch (FatalException e) {
							// wrap it in a SAXException
							throw new SAXException(e);
						} 
					}
					break;
			}
		}	
	}
	
	/**
	 * Receives the name and location of a DTD used by the xml file.
	 * Not an inherited method from XMLFilterImpl.
	 * This method takes in DTD information from a parse stream 'upriver' from it.
	 * This is a workaround to the problem that this class can't have a LexicalHandler set
	 * on it which is where the startDTD would have been. It's essentially being caught upstream,
	 * and sent back downstream.
	 * 
	 * @param name document type name
	 * @param publicId the declared public identifier for the external DTD subset, 
	 * 	or null if none was declared
	 * @param systemId The declared system identifier for the external DTD subset, 
	 * 	or null if none was declared.
	 * @throws SAXException
	 */
	public void takeStartDtd(String name, String publicId, String systemId) 
		throws SAXException{
	    
		if (systemId != null && !systemId.equals("")){
			switch (XMLSAXParser.getParseActivity()){
				case XMLSAXParser.ACTION_EXT_META:
					// record the link to the dtd
					try {
						Link lk = constructLink(systemId, true, true,
								ArchiveProperties.getInstance().getArchProperty("HARVEST_XML_DTD"));
						XMLSAXParser.getXFile().addLink(lk);
					} catch (FatalException e) {
						// wrap it in a SAXException
						throw new SAXException(e);
					}
					break;
				case XMLSAXParser.ACTION_REP_LKS:
					// substitute the DFID for the systemID and pass it on to the writer stream
				    
				    String newSystemId = null;
					
				    try {
				        newSystemId = constructRelLocalPath(systemId, true);
				        getWriterFilter().takeStartDtd(name, publicId, newSystemId);
	                } catch (FatalException e) {
	                    // systemId was null - won't happen
	                    // but anyway, wrap it
	                    throw new SAXException(e);
	                }
					break;
			}						
		}
	}
	
    /**
     * @return Returns the writerFilter.
     */
    public XMLWriterFilter getWriterFilter() {
        return writerFilter;
    }
    
    /**
     * @param _writerFilter The writerFilter to set.
     */
    public void setWriterFilter(XMLWriterFilter _writerFilter) {
        writerFilter = _writerFilter;
    }
}
