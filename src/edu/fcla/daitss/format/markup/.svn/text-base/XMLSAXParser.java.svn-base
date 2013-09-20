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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;

import edu.fcla.da.xml.Checker;
import edu.fcla.da.xml.Validator;
import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.WebCacheUtils;

/**
 * XMLSAXParser is a SAX parser for XML files. It uses different SAX handlers
 * depending on what 'parse activity' it is asked to perform.
 * 
 * @author Andrea Goethals, FCLA
 *  
 */
public class XMLSAXParser {

	/**
	 * MyDeclHandler is the only DeclHandler used in this SAX parsing of XML
	 * files. The XML filters can't specify a DeclHandler to use so this handler
	 * is used by the underlying XMLReader which passes info back upstream to
	 * the filters if they need it.
	 * 
	 * @author Andrea Goethals, FCLA
	 *  
	 */
	private class MyDeclHandler implements DeclHandler {

		/**
		 * Reports an attribute type declaration in a DTD declaration in an XML
		 * document. DeclHandler interface method
		 * 
		 * @param eName
		 * @param aName
		 * @param type
		 * @param valueDefault
		 * @param value
		 * @throws SAXException
		 */
		public void attributeDecl(String eName, String aName, String type,
				String valueDefault, String value) throws SAXException {
		    switch (getParseActivity()) {
			case ACTION_REP_LKS:
				// pass attributes to the writer filter
				writerFilter.takeAttributeDecl(eName, aName, type, 
				        valueDefault, value);
				break;
			default:
			    // do nothing
		    }
		}

		/**
		 * Reports an element type declaration in a DTD declaration in an XML
		 * document or external to the XML file. Processes the internal DTD
		 * subset before the external DTD. DeclHandler interface method
		 * 
		 * @param name element type name
		 * @param model content model
		 * @throws SAXException
		 */
		public void elementDecl(String name, String model) throws SAXException {
		    switch (getParseActivity()) {
				case ACTION_REP_LKS:
					// pass attributes to the writer filter
					writerFilter.takeElementDecl(name, model);
					break;
				default:
				    // do nothing
		    }
		}

		/**
		 * Reports a parsed external entity declaration in a DTD declaration in
		 * an XML document. DeclHandler interface method.
		 * Not called for declarations in an XML file's DTD.
		 * Applies to general as well as parameter entities.
		 * When its a parameter entity the name will startt with '%'.
		 * 
		 * @param name
		 * @param publicId
		 * @param systemId
		 * @throws SAXException
		 */
		public void externalEntityDecl(String name, String publicId,
				String systemId) throws SAXException {
			switch (XMLSAXParser.getParseActivity()) {
				case XMLSAXParser.ACTION_REP_LKS:
					// pass DTD info back upstream to the link filter
					linkFilter.takeExternalEntityDecl(name, publicId, systemId);
				    break;
				case XMLSAXParser.ACTION_EXT_META:
					// pass DTD info back upstream to the metadata filter
					linkFilter.takeExternalEntityDecl(name, publicId, systemId);
					break;
			}
		}

		/**
		 * Reports an internal entity declaration in a DTD declaration in an XML
		 * document. DeclHandler interface method.
		 * Not called for declarations in an XML file's DTD.
		 * Only called when the entity is defined directly instead
		 * of pointing to an external file.
		 * 
		 * @param name
		 * @param value
		 * @throws SAXException
		 */
		public void internalEntityDecl(String name, String value)
				throws SAXException {
		    switch (getParseActivity()) {
				case ACTION_REP_LKS:
					// pass attributes to the writer filter
					writerFilter.takeInternalEntityDecl(name, value);
					break;
				default:
				    // do nothing
		    }
		}

	}

	/**
	 * MyErrorHandler handles all the recoverable and nonrecoverable errors
	 * encountered in the parsing of XML files. Used by the underlying XMLReader
	 * as well as the filters that receive the SAX-parsed info before the
	 * underlying XMLReader.
	 * 
	 * @author Andrea Goethals, FCLA
	 *  
	 */
	public class MyErrorHandler implements ErrorHandler {

		/**
		 * Catch and throw recoverable parser errors. This method will be called
		 * when the parser is trying to validate an invalid XML file.
		 * 
		 * (DefaultHandler does nothing with errors but print them out.)
		 * 
		 * @param e
		 * @throws SAXException
		 */
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}

		/**
		 * 
		 * @param e
		 * @throws SAXException
		 */
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		/**
		 * Receives notification of a warning. ErrorHandler interface method
		 * 
		 * @param e
		 * @throws SAXException
		 */
		public void warning(SAXParseException e) throws SAXException {
		    throw e;
		}
	}

	/**
	 * MyLexicalHandler is the only LexicalHandler used by the SAX parsing of
	 * the XML file. The XML filters can't specify a LexicalHandler to use so
	 * this handler is used by the underlying XMLReader which passes info back
	 * upstream to the filters if they need it. Currently it's only purpose is
	 * to give DTD info to the filters because LexicalHandlers are the only SAX
	 * handlers to get this information. Most of the methods in this class do
	 * nothing but are needed to implement LexicalHandler.
	 * 
	 * @author Andrea Goethals, FCLA
	 *  
	 */
	private class MyLexicalHandler implements LexicalHandler {

		/**
		 * Reports an XML comment anywhere in the XML document. LexicalHandler
		 * interface method
		 * 
		 * @param ch
		 * @param start
		 * @param length
		 * @throws SAXException
		 */
		public void comment(char[] ch, int start, int length)
				throws SAXException {
		    switch (getParseActivity()) {
				case ACTION_REP_LKS:
					// pass comment to the writer filter
					writerFilter.takeComment(ch, start, length);
					break;
				default:
				// do nothing
			}
		}

		/**
		 * Reports the end of a CDATA section. LexicalHandler interface method
		 * 
		 * @throws SAXException
		 */
		public void endCDATA() throws SAXException {
			// do nothing
		}

		/**
		 * Reports the end of DTD declarations. LexicalHandler interface method
		 * 
		 * @throws SAXException
		 */
		public void endDTD() throws SAXException {
		    switch (getParseActivity()) {
				case ACTION_REP_LKS:
					// pass dtd end to the writer filter
					writerFilter.takeEndDtd();
					break;
				default:
				    // do nothing
		    }
		}

		/**
		 * Reports the end of an entity. LexicalHandler interface method
		 * 
		 * @param name
		 * @throws SAXException
		 */
		public void endEntity(String name) throws SAXException {
			// do nothing
		}

		/**
		 * 
		 *  
		 */
		public void startCDATA() {
			// do nothing
		}

		/**
		 * Reports the start of DTD declarations (the beginning of a DOCTYPE
		 * declaration). LexicalHandler interface method
		 * 
		 * @param name
		 * @param publicId
		 * @param systemId
		 * @throws SAXException
		 */
		public void startDTD(String name, String publicId, String systemId)
				throws SAXException {
			
			switch (getParseActivity()) {
				case ACTION_EXT_META:
					// pass DTD info back upstream to the link filter
					linkFilter.takeStartDtd(name, publicId, systemId);
					metaFilter.takeStartDtd(name, publicId, systemId);
				    break;
				case ACTION_REP_LKS:
					// pass DTD info back upstream to the metadata filter
					linkFilter.takeStartDtd(name, publicId, systemId);
					break;
				default:
				// do nothing
			}

		}

		/**
		 * Reports the beginning of some internal or external XML entities in
		 * the XML document. Useful to know which of the declared entities were
		 * actually used. LexicalHandler interface method
		 * 
		 * @param name
		 * @throws SAXException
		 */
		public void startEntity(String name) throws SAXException {

			//usedEntities.add(name);
		}
	}

	/**
	 * Extract metadata, determine if a DTD/schema was used, extract links, etc.
	 */
	public static final byte ACTION_EXT_META = 1;

	/**
	 * Determine if this XML file is well-formed.
	 */
	public static final byte ACTION_IS_WELLFORMED = 3;

	/**
	 * Replace the links with DFIDs.
	 */
	public static final byte ACTION_REP_LKS = 10;

	/**
	 * Validate using an internal and/or external DTD.
	 */
	public static final byte ACTION_VALIDATE = 2;

	/**
	 * Fully-qualified name for this class. To be used for Informer calls from
	 * within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.markup.util.XMLSAXParser";

	/**
	 * The parser will validate the document only if a grammar is specified.
	 */
	private static final String FEATURE_ID_DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";

	/**
	 * Load the external DTD. Always on when validation is on.
	 */
	private static final String FEATURE_ID_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/**
	 * Namespace prefixes feature id
	 * (http://xml.org/sax/features/namespace-prefixes). Controls the reporting
	 * of qNames and Namespace declarations (xmlns* attributes). Unless the
	 * value of this feature flag is changed to true (from its default of
	 * false), qNames may optionally (!) be reported as empty strings for
	 * elements and attributes that have an associated namespace URI, and xmlns*
	 * attributes will not be reported. When set to true, that information will
	 * always be available. Note: leaving this true keeps this
	 * parser-independent!
	 */
	private static final String FEATURE_ID_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

	/**
	 * Namespaces feature id (http://xml.org/sax/features/namespaces). When this
	 * feature is true (the default), any applicable Namespace URIs and
	 * localNames (for elements in namespaces) must be available through the
	 * startElement and endElement callbacks in the ContentHandler interface,
	 * and through the various methods in the Attributes interface, and
	 * start/endPrefixMapping events must be reported. For elements and
	 * attributes outside of namespaces, the associated namespace URIs will be
	 * empty strings and the qName parameter is guaranteed to be provided as a
	 * non-empty string. NOTE: this must stay true.
	 */
	private static final String FEATURE_ID_NAMESPACES = "http://xml.org/sax/features/namespaces";

	/**
	 * Enable full schema grammar constraint checking, including checking which
	 * may be time-consuming or memory intensive. Currently, particle unique
	 * attribution constraint checking and particle derivation resriction
	 * checking are controlled by this option.
	 */
	private static final String FEATURE_ID_SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";

	/**
	 * Turn on XML Schema validation by inserting XML Schema validator in the
	 * pipeline.
	 */
	private static final String FEATURE_ID_SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";

	/**
	 * Validate the document and report validity errors. If this feature is set
	 * to true, the document must specify a grammar. By default, validation will
	 * occur against DTD. If this feature is set to false, and document
	 * specifies a grammar that grammar might be parsed but no validation of the
	 * document contents will be performed.
	 */
	private static final String FEATURE_ID_VALIDATION = "http://xml.org/sax/features/validation";

	/**
	 * The specific activity (extract metadata, etc.) requested by the object
	 * that called for a parse.
	 */
	private static byte parseActivity = 0;

	/** Default parser name. */
	private static final String PARSER_CLASS_NAME = SAXParser.class.getName();
	

	/**
	 * Lexical handler property id
	 * (http://xml.org/sax/properties/lexical-handler).
	 */
	private static final String PROP_ID_DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";

	/**
	 * Lexical handler property id
	 * (http://xml.org/sax/properties/lexical-handler).
	 */
	private static final String PROP_ID_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

	/**
	 * Represents the XML file that this parser is parsing. Need to store it
	 * statically here so that XMLLinkFilter can access it to store Link objects
	 * in it.
	 */
	private static XML xFile = null;

	/**
	 * @return the requested parse activity
	 */
	public static byte getParseActivity() {
		return parseActivity;
	}

	/**
	 * @return the XML object being parsed by this parser
	 */
	public static XML getXFile() {
		return xFile;
	}

	/**
	 * Determine whether or not a parse activity is valid for this parser.
	 * 
	 * @param activity
	 *            a parse activity
	 * @return whether or not a parse activity is valid for this parser
	 */
	private static boolean isValidParseActivity(byte activity) {
		boolean isValid = false;
		if (activity == ACTION_EXT_META || activity == ACTION_IS_WELLFORMED
				|| activity == ACTION_REP_LKS 
				|| activity == ACTION_VALIDATE) {
			isValid = true;
		}

		return isValid;
	}

	/**
	 * Sets the activity that the SAX parser is performing.
	 * 
	 * @param _parseActivity
	 *            the requested parse activity
	 * @throws FatalException
	 */
	public static void setParseActivity(byte _parseActivity)
			throws FatalException {
		String methodName = "setParseActivity(byte)";

		if (isValidParseActivity(_parseActivity)) {
			parseActivity = _parseActivity;
		} else {
			Informer.getInstance().fail(CLASSNAME, methodName,
					"Invalid argument", "_parseActivity: " + _parseActivity,
					new FatalException("Not a valid parse activity"));
		}
	}

	/**
	 * parser that can recognize links
	 */
	private XMLLinkFilter linkFilter = null;
	
	/**
	 * parser that can output XML
	 */
	private XMLWriterFilter writerFilter = null;

	/**
	 * parser that can recognize metadata
	 */
	private XMLMetadataFilter metaFilter = null;

	/**
	 * The underlying parser
	 */
	private XMLReader parser = null;

//	/**
//	 * The parser that can DTD- and XML Schema-validate
//	 */
//	private XMLReader validateParser = null;

	private Validator validator;

	/**
	 * Constructs an XMLSAXParser object and resets
	 * the object's resources.
	 */
	public XMLSAXParser() {
		destroyClassResources();
	}

	/**
	 * Reset static class members.
	 */
	private void destroyClassResources() {
		xFile = null;
		parseActivity = 0;
	}

	/**
	 * Initialize the parser that knows what links look like in an XML file.
	 * This is to be used as a filter on the SAX stream being passed to the
	 * parser that knows how to extract XML file metadata.
	 *  
	 */
	private void initLinkFilter() {

		XMLLinkFilter.init();
		this.linkFilter = null;
		this.linkFilter = new XMLLinkFilter();

		this.linkFilter.setErrorHandler(new XMLSAXParser.MyErrorHandler());

	}

	/**
	 * Initialize the parser that knows how to extract metadata from an XML
	 * file.
	 * 
	 */
	private void initMetaFilter() {

		XMLMetadataFilter.init();
		this.metaFilter = null;
		this.metaFilter = new XMLMetadataFilter();
		this.metaFilter.setErrorHandler(new XMLSAXParser.MyErrorHandler());
	}

	/**
	 * Initialize the underlying XMLReader parser. Specifically, set all the
	 * default features and properties. This parser receives the parsing stream
	 * after all other filters have processed it.
	 * 
	 * @throws FatalException
	 */
	private void initParser() throws FatalException {
		String methodName = "initParser()";
		
		try {
		    this.parser = null;
			this.parser = XMLReaderFactory.createXMLReader(PARSER_CLASS_NAME);
		} catch (SAXException e) {
			Informer.getInstance().fail(this, methodName,
					"Can't find or load parser",
					"parser: " + PARSER_CLASS_NAME, e);
		}

		String feature = null;
		String property = null;

		// set default parser features
		try {
			feature = FEATURE_ID_NAMESPACES;
			parser.setFeature(feature, true);
			
			feature = FEATURE_ID_NAMESPACE_PREFIXES;
			parser.setFeature(feature, true);
			//parser.setFeature(feature, false);
			
			feature = FEATURE_ID_VALIDATION;
			parser.setFeature(feature, false);

			feature = FEATURE_ID_LOAD_EXTERNAL_DTD;
			parser.setFeature(feature, false);

			feature = FEATURE_ID_SCHEMA_VALIDATION;
			parser.setFeature(feature, false);
			feature = FEATURE_ID_SCHEMA_FULL_CHECKING;
			parser.setFeature(feature, false);
			feature = FEATURE_ID_DYNAMIC_VALIDATION;
			parser.setFeature(feature, false);
		} catch (SAXNotRecognizedException e) {
			Informer.getInstance().fail(this, methodName,
					"Parser does recognize feature name",
					"feature: " + feature, e);
		} catch (SAXException e) {
			Informer.getInstance()
					.fail(this, methodName, "Parser does not support feature",
							"feature: " + feature, e);
		}

		// set the custom error handler
		ErrorHandler eh = new MyErrorHandler();
		parser.setErrorHandler(eh);

		try {
			property = PROP_ID_DECLARATION_HANDLER;
			parser.setProperty(property, new MyDeclHandler());
			property = PROP_ID_LEXICAL_HANDLER;
			parser.setProperty(property, new MyLexicalHandler());
		} catch (SAXNotRecognizedException e1) {
			Informer.getInstance().fail(this, methodName,
					"Parser does recognize property name",
					"property: " + property, e1);
		} catch (SAXNotSupportedException e1) {
			Informer.getInstance().fail(this, methodName,
					"Parser does not support property",
					"property: " + property, e1);
		}
	}

	/**
	 * Initializes the parser so that it can validate a file.
	 * 
	 * @throws FatalException
	 */
	private void initValidator() throws FatalException {
		

		// try using the other validator
		try {
        	WebCacheResolver resolver = WebCacheUtils.getResolver();
			validator = new Validator(resolver);
			//validator = new Validator();
		} catch (ParserConfigurationException e) {
			Informer.getInstance().fail("Can't load validator",
					"parser: " + validator.getClass().getName(), e);
		}

		
//		String methodName = "initValidator()";
//		this.validateParser = null;
//
//		try {
//			this.validateParser = XMLReaderFactory.createXMLReader(PARSER_CLASS_NAME);
//		} catch (SAXException e) {
//			Informer.getInstance().fail(this, methodName,
//					"Can't find or load parser",
//					"validateParser: " + PARSER_CLASS_NAME, e);
//		}
//
//		String feature = null;
//
//		// set default parser features
//		try {
//			// set the validator to use the webcache resolver.
//			//LocalCacheResolver resolver = new LocalCacheResolver();
//			//this.validateParser.setEntityResolver(resolver);
//			
//			feature = FEATURE_ID_NAMESPACES;
//			validateParser.setFeature(feature, true);
//			feature = FEATURE_ID_NAMESPACE_PREFIXES;
//			validateParser.setFeature(feature, false);
//			feature = FEATURE_ID_VALIDATION;
//			validateParser.setFeature(feature, true);
//			feature = FEATURE_ID_LOAD_EXTERNAL_DTD;
//			validateParser.setFeature(feature, true);
//			feature = FEATURE_ID_SCHEMA_VALIDATION;
//			validateParser.setFeature(feature, true);
//			feature = FEATURE_ID_SCHEMA_FULL_CHECKING;
//			validateParser.setFeature(feature, true);
//			
//			// this prevented schema validation when 
//			// a schema was specified but not found!
//			//feature = FEATURE_ID_DYNAMIC_VALIDATION;
//			//validateParser.setFeature(feature, true);
//			
//		} catch (SAXNotRecognizedException e) {
//			Informer.getInstance().fail(this, methodName,
//					"Parser does recognize feature name",
//					"feature: " + feature, e);
//		} catch (SAXException e) {
//			Informer.getInstance()
//					.fail(this, methodName, "Parser does not support feature",
//							"feature: " + feature, e);
//		}
//
//		// set the custom error handler
//		ErrorHandler eh = new MyErrorHandler();
//		validateParser.setErrorHandler(eh);
	}

	/**
	 * Initialize the underlying XMLReader parser. Specifically, set all the
	 * default features and properties. This parser does not load any external
	 * entities (dtds, etc.). It is meant to be used when you only want to see
	 * if the XML is well-formed.
	 * 
	 * @throws FatalException
	 */
	private void initWellformedParser() throws FatalException {
		String methodName = "initParser()";

		this.parser = null;
		try {
			this.parser = XMLReaderFactory.createXMLReader(PARSER_CLASS_NAME);
		} catch (SAXException e) {
			Informer.getInstance().fail(this, methodName,
					"Can't find or load parser",
					"parser: " + PARSER_CLASS_NAME, e);
		}

		String feature = null;
		String property = null;

		// set default parser features
		try {
			feature = FEATURE_ID_NAMESPACES;
			parser.setFeature(feature, false);
			feature = FEATURE_ID_NAMESPACE_PREFIXES;
			parser.setFeature(feature, false);
			feature = FEATURE_ID_VALIDATION;
			parser.setFeature(feature, false);

			// The key to making this not try to load DTDs that don't exist:
			feature = FEATURE_ID_LOAD_EXTERNAL_DTD;
			parser.setFeature(feature, false);

			feature = FEATURE_ID_SCHEMA_VALIDATION;
			parser.setFeature(feature, false);
			feature = FEATURE_ID_SCHEMA_FULL_CHECKING;
			parser.setFeature(feature, false);
			feature = FEATURE_ID_DYNAMIC_VALIDATION;
			parser.setFeature(feature, false);
		} catch (SAXNotRecognizedException e) {
			Informer.getInstance().fail(this, methodName,
					"Parser does recognize feature name",
					"feature: " + feature, e);
		} catch (SAXException e) {
			Informer.getInstance()
					.fail(this, methodName, "Parser does not support feature",
							"feature: " + feature, e);
		}

		// set the custom error handler
		ErrorHandler eh = new MyErrorHandler();
		parser.setErrorHandler(eh);

		try {
			property = PROP_ID_DECLARATION_HANDLER;
			parser.setProperty(property, new MyDeclHandler());
			property = PROP_ID_LEXICAL_HANDLER;
			parser.setProperty(property, new MyLexicalHandler());
		} catch (SAXNotRecognizedException e1) {
			Informer.getInstance().fail(this, methodName,
					"Parser does recognize property name",
					"property: " + property, e1);
		} catch (SAXNotSupportedException e1) {
			Informer.getInstance().fail(this, methodName,
					"Parser does not support property",
					"property: " + property, e1);
		}
	}
	
	/**
	 * Parse the XML file based on the parse activity that was requested.
	 * Specifically this can extract metadata, recognize links, verify that an
	 * XML file was described by a particular schema, replace links, and DTD/W3C
	 * Schema validate.
	 * 
	 * @param _xFile A non-null XML file to parse
	 * @param parseActivity the activity the parser should perform
	 * @throws FatalException
	 * @throws SAXException
	 * @throws SAXParseException
	 */
	public void parse(XML _xFile, byte parseActivity) throws FatalException,
		SAXException, SAXParseException {
	    parse(_xFile, parseActivity, null);
	}

	/**
	 * Parse the XML file based on the parse activity that was requested.
	 * Specifically this can extract metadata, recognize links, verify that an
	 * XML file was described by a particular schema, replace links, and DTD/W3C
	 * Schema validate.
	 * 
	 * @param _xFile A non-null XML file to parse
	 * @param parseActivity the activity the parser should perform
	 * @param newFilePath absolute path of a file to create or null if not applicable
	 * @throws FatalException
	 * @throws SAXException
	 * @throws SAXParseException
	 */
	public void parse(XML _xFile, byte parseActivity, String newFilePath) 
		throws FatalException, SAXException, SAXParseException {
		String methodName = "parse(XML, byte, String)";

		if (_xFile == null) {
			Informer.getInstance().fail(this, methodName, "Null argument",
					"XML _xFile: " + _xFile,
					new FatalException("Must be a non-null XML"));
		}

		if (!isValidParseActivity(parseActivity)) {
			Informer.getInstance().fail(this, methodName,
					"Invalid parse activity",
					"parseActivity: " + parseActivity,
					new FatalException("Not a valid parser activity"));
		}

		// clue the handlers in to what their supposed to be doing
		// (extract metadata, see if it uses METS, etc.)
		setParseActivity(parseActivity);

		// store it for filters so they can add links,
		// anomalies, etc. to it.
		xFile = _xFile;

		String filePath = xFile.getPath();

		// intialize the needed parser(s) and string of filters
		switch (parseActivity) {
			case ACTION_IS_WELLFORMED:
				// see if it's well-formed XML
				initWellformedParser();
				try {
					this.parser.parse(filePath);
				} catch (SAXException e1) {
					// will be caught by XML.parse()
					throw e1;
				} catch (IOException e1) {
					// wrap it for XML.parse()
					throw new SAXException(e1);
				}
				break;
			case ACTION_EXT_META:
				// extract metadata and recognize links
				// set up last parser to receive parsing stream
				initParser();
				// set up the metadata extracting filter
				initMetaFilter();
				// set up the link recognition stream
				initLinkFilter();
	
				// set up the relationships between the streams
				this.linkFilter.setParent(this.parser);
				this.metaFilter.setParent(this.linkFilter);
	
				// start the link filter parsing - 
				// this filter will pass the stream
				// along to the metadata extractor filter
				try {
					this.metaFilter.parse(filePath);
				} catch (SAXParseException e) {
					// problem parsing the file - log this
					// could have been thrown by SAXHandler's fatalError() or
					// error()
					// methods so it may or may not be recoverable
					// rethrow it so that the XML.parse() will know a problem
					// occurred.
					throw e;
				} catch (SAXException e) {
					// will be caught by XML.parse()
					throw e;
				} catch (IOException e) {
					// wrap it for XML.parse()
					throw new SAXException(e);
				}
				break;
			case ACTION_VALIDATE:
				// validate
				initValidator();
	
				// any exceptions that happen trying to validate the
				// file will be translated into the file not being valid.
				try {
					Checker checker = validator.validate(new File(filePath));
					List<SAXParseException> errors = new Vector<SAXParseException>();
					errors.addAll(checker.getFatals());
					errors.addAll(checker.getErrors());
					if(errors.size() > 0) throw errors.get(0);
				} catch (SAXParseException e) {
					// not valid
					throw e;
				} catch (SAXException e) {
					// not valid
					throw e;
				} catch (IOException e) {
					// not valid
					throw new SAXException(e);
				}
				break;
			case ACTION_REP_LKS:
				// localize links
				
				// set up last parser to receive parsing stream
				initParser();
                
				// set up the link recognition stream
				initLinkFilter();
	
				// set up the relationships between the streams
				this.linkFilter.setParent(this.parser);
				try {
                    this.writerFilter = new XMLWriterFilter(this.linkFilter, 
                            new OutputStreamWriter(new FileOutputStream(newFilePath), "UTF-8"));
				} catch (UnsupportedEncodingException ue) {
				    // doesn't support UTF-8 - not going to happen
				    throw new SAXException(ue);
                } catch (FileNotFoundException e3) {
                    // the file exists but is a directory rather than a 
                    // regular file, does not exist but cannot be created, 
                    // or cannot be opened for any other reason
                    throw new SAXException(e3);
                }
                
                // kludge so that link filter can send the writer filter
                // the dtd info
                this.linkFilter.setWriterFilter(this.writerFilter);
	
				// start the writer filter parsing -  it wil pass control to its parent (link filter)
				// which will pass control to its parent (SAX parser) and then the stream will pass from 
				// the SAX parser to the link filter to the output filter.
				try {
                    FileReader fr = new FileReader(filePath);
                    this.writerFilter.parse(new InputSource(fr));
                    fr.close();
				} catch (NullPointerException ne) {
				    throw new SAXException("Null pointer exception when normalizing " + 
				            getXFile().getPath());
				} catch (SAXException e2) {
					throw e2;
				} catch (IOException e2) {
					// can't parse the file
				    throw new SAXException(e2);
				}
				break;
		}
	}

}