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
 * This class was loosely based on one from "Matra - the DTD Parser" whose initial
 * developer was Conrad S Roche <derupe at users.sourceforge.net>. 
 * The Matra code was released under either the Mozilla Public License 
 * Version 1.1 or alternatively the GNU GENERAL PUBLIC LICENSE Version 2 or 
 * later.
 */

package edu.fcla.daitss.format.text.dtd;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Enumeration;

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.text.dtd.DTDWriterException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * Class to parse a DTD resource. Adapted from Matra.
 *
 * 
 */
public class DTDParser {
    
	/**
	 * Check that external files exist and add anomalies
	 * if they don't.
	 */
	public static final byte ACTION_CHECK_EXT_FILES = 1;

	/**
	 * Extract metadata
	 */
	public static final byte ACTION_EXT_META = 2;

	/**
	 * Determine if this DTD is well-formed.
	 */
	public static final byte ACTION_IS_WELLFORMED = 3;

	/**
	 * Replace the links with DFIDs.
	 */
	public static final byte ACTION_REP_LKS = 4;
	
	/**
	 * Whether or not the parser dhould be adding anomalies
	 * to the file metadata when it sees them.
	 */
	private static boolean addAnoms = false;
	
	/**
	 * Whether or not the parser should be recognizing links
	 * and adding them to the set of links for the parsed file
	 */
	private static boolean buildLinkSet = false;
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
		"edu.fcla.daitss.format.text.dtd.util.DTDParser";	
	
	/**
	 * Whether or not the parser should be loading external
	 * files when building its set of parsed entities
	 */
	private static boolean loadExternalFiles = false;

	/**
	 * The specific activity (extract metadata, etc.) requested by the object 
	 * that called for a parse.
	 */
	private static byte parseActivity = 0;
	 
	/**
	 * 3.3 Attribute-List Declarations [XML Rec 1.0]
	 * 
	 * At user option, an XML processor may issue a warning if attributes 
	 * are declared for an element type not itself declared, but this is 
	 * not an error.
	 */
	public static boolean UO_ATTR_UNKNOWN_ELEM = true;
	
	/**
	 * 3.2 Element Type Declarations [XML Rec 1.0]
	 * 
	 * At user option, an XML processor may issue a warning when a 
	 * declaration mentions an element type for which no declaration 
	 * is provided, but this is not an error.
	 */
	public static boolean UO_MISSING_ELEM_DECL = true;
	
	/**
	 * 3.3 Attribute-List Declarations [XML Rec 1.0]
	 * 
	 * For interoperability, an XML processor may at user option issue a 
	 * warning when more than one attribute-list declaration is provided 
	 * for a given element type, or more than one attribute definition is 
	 * provided for a given attribute, but this is not an error.
	 */
	public static boolean UO_MULTIPLE_ATTR_LIST = true;
	
	/**
	 * Whether to write out to a file as its parsed
	 */
	private static boolean writeFile = false;
	
	/**
	 * Used to output a DTD to a file.
	 */
	private static DTDWriter writer = null;

	/**
	 * @return whether or not the parser should be recognizing links
	 * and adding them to the set of links for the parsed file
	 */
	public static boolean buildLinkSet() {
		return buildLinkSet;
	}
	
	/**
	 * @return the requested parse activity
	 */
	public static byte getParseActivity() {
		return parseActivity;
	}
    /**
     * @return Returns the writer.
     */
    public static DTDWriter getWriter() {
        return writer;
    }
	
	/**
	 * Determine whether or not a parse activity is
	 * valid for this parser.
	 * 
	 * @param activity a parse activity
	 * @return whether or not a parse activity is
	 * 	valid for this parser
	 */
	private static boolean isValidParseActivity(byte activity) {
		boolean isValid = false;		
		if (activity == ACTION_EXT_META ||
		    activity == ACTION_CHECK_EXT_FILES ||
			activity == ACTION_IS_WELLFORMED ||
			activity == ACTION_REP_LKS){
				isValid = true;
		}
		
		return isValid;
	}
	
	/**
	 * @return whether or not external files should be loaded
	 * 	by the parser
	 */
	public static boolean loadExternalFiles() {
		return loadExternalFiles;
	}
	
	/**
	 * @param addAnoms whether or not the parser should be
	 * 	storing a list of the anomalies seen
	 */
	public static void setAddAnoms(boolean addAnoms) {
		DTDParser.addAnoms = addAnoms;
	}

	/**
	 * @param _buildLinkSet whether or not the parser should be recognizing links
	 * and adding them to the set of links for the parsed file
	 * 
	 */
	public static void setBuildLinkSet(boolean _buildLinkSet) {
		buildLinkSet = _buildLinkSet;
	}

	/**
	 * @param _loadExternalFiles whether or not external files 
	 * 	should be loaded by the parser
	 */
	public static void setLoadExternalFiles(boolean _loadExternalFiles) {
		loadExternalFiles = _loadExternalFiles;
	}
	
	/**
	 * Sets the activity that the SAX parser is performing.
	 * 
	 * @param _parseActivity the requested parse activity
	 * @throws FatalException
	 */
	public static void setParseActivity(byte _parseActivity) 
		throws FatalException {
		String methodName = "setParseActivity(byte)";
		
		if (isValidParseActivity(_parseActivity)) {
			parseActivity = _parseActivity;
		} else {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Invalid argument",
				"_parseActivity: " + _parseActivity,
				new FatalException("Not a valid parse activity"));
		}
	}
	
	/**
	 * Set whether or not to write out to a file as its parsed.
	 * 
	 * @param _writeFile whether or not to write out to a file 
	 * 	as its parsed
	 */
	public static void setWriteFile(boolean _writeFile) {
	    writeFile = _writeFile;
	}
    /**
     * @param writer The writer to set.
     */
    public static void setWriter(DTDWriter writer) {
        DTDParser.writer = writer;
    }
	
	/**
	 * @return Returns whether or not the parser should be
	 * 	storing a list of the anomalies seen
	 */
	public static boolean shouldAddAnoms() {
		return addAnoms;
	}
	
    /**
     * @return Returns whether or not the parser should write out to 
     * a file as it parses.
     */
    public static boolean shouldWriteFile() {
        return writeFile;
    }
	
	/**
	 * attribute list reader and writer
	 */
	private AttributeHandler attributeHandler = new AttributeHandler();

	/**
	 * comment reader and writer
	 */
	private CommentHandler commentHandler = new CommentHandler();
	
	/**
	 * The file containing the DTDSubset
	 */
	private DTD dFile = null;

	/**
	 * The DTD data to read (sometimes a subset of the DTD,
	 * sometimes a DTD that this DTD links to)
	 */
	private DTDTextParser dtd;
	
	/**
	 * Will get rid of
	 */
	private DTDSource dtdSrc;
	
	/**
	 * The single bitstream - the DTD's declarations, entities, comments, etc.
	 */
	private DTDSubset dtdSubset = null;
	
	/**
	 * element reader and writer
	 */
	private ElementHandler elementHandler = new ElementHandler();

    /**
     * General and paramter entity declaration reader and writer
     */
	private EntityHandler entityHandler = new EntityHandler();

	/**
	 * Create an instance of an NotationReader
	 * to read the Notation declaration.
	 */
	private NotationHandler notationReader = new NotationHandler();

	/**
	 * Create an instance of an PIReader
	 * to read the PI.
	 */
	private PIHandler piReader = new PIHandler();

	/**
	 * Construct the DTDParser, all known DTDAnomalies,
	 * and reset some parser flags.
	 * 
	 */
	public DTDParser() {
		dtd = new DTDTextParser("");
		reset();
	}
	
	/**
	 * Builds the set of parameter/general entities that can be resolved for the
	 * DTD being parsed. Only loads external files if a flag is set 
	 * in <code>DTDParser</code>.
	 *
	 * @param unresolvedDTD the contents of this DTD or an 
	 * 	external DTD used to populate a parameter reference
	 *
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void buildEntityList(DTDTextParser unresolvedDTD)
		throws DTDSyntaxException, DTDWriterException, 
		FileNotFoundException, FatalException, EOFException {
	    
	    String methodName = "buildEntityList(DTDData)";

		if (unresolvedDTD == null)
			throw new IllegalArgumentException();

		unresolvedDTD.rewind();
		unresolvedDTD.handleWhiteSpace();

		// skip over non-entities and take action on entities
		while (!unresolvedDTD.endOfData()) {

			unresolvedDTD.handleWhiteSpace();

			if (unresolvedDTD.endOfData())
				return;

			// look for "<!--"
			if (commentHandler.seeCommentStart(unresolvedDTD)) {
				// skip over the comment (not catching returned
				// Comment here)
				commentHandler.handleComment(unresolvedDTD);
				unresolvedDTD.handleWhiteSpace();
				continue;
			}

			// look for "<!NOTATION"
			if (notationReader.seeNotationStart(unresolvedDTD)) {
				notationReader.buildNotation(unresolvedDTD, this);
				unresolvedDTD.handleWhiteSpace();
				continue;
			}

			// look for "<?"
			if (piReader.seePiStart(unresolvedDTD)) {
				// not catching returned PI here
				piReader.handlePi(unresolvedDTD);
				unresolvedDTD.handleWhiteSpace();
				continue;
			}

			char ch = unresolvedDTD.getNextChar();

			if (ch == '%') {
				// parameter entity
				String peEntityName = unresolvedDTD.getAllSkipping(';');

				Entity ent = this.getDtdSubset().getParamEntity(peEntityName);
				if (ent == null) {
					throw new DTDSyntaxException("Unresolved parameter entity " + peEntityName);
					/*
					Informer.getInstance().fail(this,
                            methodName,
                            "Unresolved parameter entity",
                            "Entity name: " + peEntityName + 
                            " File: " + this.getDFile().getPath(),
                            new FatalException("This parameter entity should have been resolved"));
                    */
				}
				if (ent.getExternalIdType() == Entity.EXT_ID_SYSTEM
					|| ent.getExternalIdType() == Entity.EXT_ID_PUBLIC) {
						// check if we should be loading external files
						if (DTDParser.loadExternalFiles() &&
							ent.getSystemLiteral() != null && 
							!ent.getSystemLiteral().equals("")) {
							//String newURI = constructRelLocalPath(ent.getSystemLiteral(), true);
							//loadDtdEntities(newURI);
							loadDtdEntities(ent.getSystemLiteral());
						} 
				}
				unresolvedDTD.handleWhiteSpace();
				continue;
			}
			
			if (ch != '<') {
				throw new DTDSyntaxException(
					"Was expecting '<'.");
			}

			if (unresolvedDTD.getNextChar() != '!') {
				throw new DTDSyntaxException(
					"Was expecting '!'.");
			}

			if (unresolvedDTD.peekAtNextChar() == '[') {
				// check syntax of conditional section
				DTDTextParser condSect = unresolvedDTD.validateConditionalSect(this);
				condSect.getNextChar(); //'['
				String sectType = condSect.getAllSkipping('[').trim();
				if (sectType.charAt(0) == '%') {
				    // parameter entity
					String entName =
						sectType.substring(1, sectType.indexOf(";"));
					Entity ent = this.getDtdSubset().getParamEntity(entName);
					if (ent == null) {
					    throw new DTDSyntaxException("Undeclared entity");
					}
					sectType = ent.getLiteralValue();
				}

				//Ignore the ignored sections
				if (sectType.equals("INCLUDE")) {
					if ((condSect.length() - condSect.getCurrentPosition())
						< "]]>".length()) {
						throw new DTDSyntaxException(
							"Invalid conditional section");
					}

					// keep calling this method recursively for any nested
					// conditional statements
					// remove the ('[' 'INCLUDE' | 'IGNORE' & ']]>') before 
					// passing it to buildEntityList()
					buildEntityList(
						new DTDTextParser(
							condSect.toString().substring(
								condSect.getCurrentPosition(),
								condSect.length() - "]]>".length())));
					unresolvedDTD.handleWhiteSpace();
				} // sectType validity was already checked in readConditionalSect
				continue;
			}

			// suck in the keyword: ELEMENT, ATTLIST, ENTITY, etc.
			String name = unresolvedDTD.getAllSkippingNextWS();

			String content = "";

			while (!unresolvedDTD.endOfData()
				&& unresolvedDTD.peekAtNextChar() != '>')
				content += unresolvedDTD.getNextChar();

			//read the '>' char
			if (unresolvedDTD.getNextChar() != '>')
				throw new DTDSyntaxException("Was expecting '>'.");

			// don't resolve entity references during localization (REP_LKS) 
			if (getParseActivity() != DTDParser.ACTION_REP_LKS){
				content = resolveEntities(new DTDTextParser(content)).toString();
			}
			
			if (name.equals("ENTITY")) {
			    entityHandler.buildEntity(content, this);
			}
			unresolvedDTD.skipWhiteSpace();
			
		} // end while
	}
	
	/**
	 * Write out a character if the parser should be doing this.
	 * 
	 * @param c character to write
	 * @throws DTDWriterException
	 */
	public void conditionalWrite(char c) 
		throws DTDWriterException {
	    if (shouldWriteFile()) {
			DTDParser.getWriter().write(c);
		}
	}
	
	/**
	 * Write out a string if the parser should be doing this and the
	 * string is not null.
	 * 
	 * @param data
	 * @throws DTDWriterException
	 */
	public void conditionalWrite(String data) 
		throws DTDWriterException {
	    if (shouldWriteFile() && data != null) {
			DTDParser.getWriter().write(data);
		}
	}
	
	/**
	 * Constructs a <code>Link</code> object that can be
	 * added to the DataFile object after calling this method.
	 * 
	 * @param linkAlias how the link is written within the dtd
	 * @param shouldRetrieve whether this link is important enough to retrieve
	 * @return the Link object
	 * @throws FatalException
	 */
	public Link constructLink(String linkAlias, String shouldRetrieve) 
		throws FatalException {
		
		Link lk = null;
		boolean retrieve = false;
		
		try {
			lk = new Link(
                    // TODO this should probably be in the workingDir
					getDFile().getIp().getWorkingPath(),
					getDFile().getPath(),
					linkAlias,
					getDFile().getIp().getLinksDir(),
					getDFile().getOrigin(),
					getDFile().getOriginalUri());
			
			// first set whether or not it should be retrieved based on the argument
			// passed in
			retrieve = (shouldRetrieve.equalsIgnoreCase("true"))? true : false;
			
			
		} catch (URISyntaxException e) {
			// badly formed URI
			if (shouldAddAnoms()) {
				// note the anomaly
				this.getDFile().addAnomaly(
						this.getDFile().getAnomsPossible().getSevereElement(
							Anomalies.FILE_LINK_BAD_URI));
			}
		}
	
		lk.setShouldRetrieve(retrieve);
		
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
	public String constructRelLocalPath(String linkAlias, boolean useLocPath) 
		throws FatalException {
	    String methodName = "constructRelLocalPath(String)";
	    
	    String newPath = null;
	    
	    // check args
	    if (linkAlias == null){
	        Informer.getInstance().fail(this,
                    methodName,
                    "Null argument",
                    "linkAlias",
                    new FatalException("Can't be null."));
	    }
	    
	    DataFile df = null; // local file the link resolves to
	    
	    try {
            df = getDFile().getDfFromLinkAlias(linkAlias);
            if (df == null) {
                // link was not successfully retrieved - return the same
                // path given to this method
                newPath = linkAlias;
            } else {
                // get the path to the linked-to file and make it relative
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
                newPath = FileUtil.getRelPathFrom(getDFile().getPath(),
                        linkToUse);
            }
            
        } catch (FatalException e) {
            // linkAlias was null - won't happen, but account for this anyway
	        Informer.getInstance().fail(this,
                    methodName,
                    "Null argument",
                    "linkAlias: " + linkAlias + 
					" df.getPath(): " + df.getPath() +
					" df.getOid(): " + df.getOid() +
					" df.getFileExt(): " + df.getFileExt(),
                    new FatalException("None of these can be null."));
        }
	    
	    return newPath;
	}
	
	/**
	 * @return the file being parsed (DTD object).
	 */
	public DTD getDFile() {
		return this.dFile;
	}

	/**
	 * @return Returns the dtdSubset (DTD file's bitstream).
	 */
	public DTDSubset getDtdSubset() {
		return this.dtdSubset;
	}
	
	/**
	 * Load a DTD linked to from this DTD, parses it and
	 * adds its declarations to this DTD.
	 *
	 * @param fileName the linked-to DTD's file name
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void importLinkedToDtdDecls(String fileName) 
		throws DTDSyntaxException, DTDWriterException, EOFException, 
		FatalException, FileNotFoundException {

		DTDParser linkedToFileParser = new DTDParser();
		// this parser has the same parseActivity because its a class member
		
		DTDSource newDtdSrc = null;
		String parentPath = this.getDFile().getPath();
		boolean cantResolve = false;

		String linkType = null;
		try {
            linkType = DTDSource.determineLinkType(fileName, parentPath);
            
            if (linkType == null){
                cantResolve = true;
            } else if (linkType.equals(Link.TYPE_ABS_PATH)){
                newDtdSrc = new DTDFile(fileName);
            } else if (linkType.equals(Link.TYPE_HTTP_URL)){
                newDtdSrc = new DTDUrl(fileName);
            } else if (linkType.equals(Link.TYPE_REL_PATH)){
                String absPath = DTDFile.getAbsolute(fileName, parentPath);
                newDtdSrc = new DTDFile(absPath);
            } else if (linkType.equals(Link.TYPE_UNKNOWN)){
                cantResolve = true;
            }
		} catch (MalformedURLException e) {
		    cantResolve = true;
        } catch (URISyntaxException e) {
            cantResolve = true;
        } catch (FileNotFoundException e) {
            cantResolve = true;
        }
        
        if (cantResolve && shouldAddAnoms()){
            this.getDFile().addAnomaly(
					this.getDFile().getAnomsPossible().getSevereElement(
					DTDAnomalies.DTD_MISSING_ENT_FILE));
            return;
        }
        
        // Saving the real parse activity so that we can revert to it
        // after parsing this linked-to DTD
        byte realParserActivity = getParseActivity();
        // setting the parse activity to extraction of metadata for this
        // linked-to DTD. Need to do this because the handlers (AttributeHandler, etc.)
        // will only parse the declarations if the parse activity is
        // extract metadata. Note that we can not change the handlers to also
        // parse during parse activity check external files because then
        // it would parse the declarations for the parent DTD twice. 
        // This leads to validation errors (DTDs can't declare the same element
        // name multiple times, etc.)
        setParseActivity(ACTION_EXT_META);
        
        // parse the linked-to DTD
		linkedToFileParser.parse(newDtdSrc, this.getDFile());
		
		// restore parse activity
		setParseActivity(realParserActivity);
		
		// now transferring this external DTD's declarations
		// to the one that linked to it

		Enumeration keys;

		//load the attribute list
		keys = linkedToFileParser.getDtdSubset().getAllAttributes().keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			this.getDtdSubset().addAttList(linkedToFileParser.getDtdSubset().getAttributeList(key));
		}
		
		//load the element list
		keys = linkedToFileParser.getDtdSubset().getElementNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			this.getDtdSubset().addElement(linkedToFileParser.getDtdSubset().getElementType(key));
		}

		//load the entity list
		keys = linkedToFileParser.getDtdSubset().getEntityList().keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			this.getDtdSubset().addEntity(linkedToFileParser.getDtdSubset().getEntity(key));
		}
		
		// load the notation list
		keys = linkedToFileParser.getDtdSubset().getNotationList().keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			this.getDtdSubset().addNotation(linkedToFileParser.getDtdSubset().getNotation(key));
		}

	}
	
	/**
	 * Parse and load the conditional section.
	 *
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void loadConditionalSect() 
		throws DTDSyntaxException, DTDWriterException, 
		EOFException, FileNotFoundException, FatalException {

	    // check the syntax
		DTDTextParser conditionalSect = dtd.validateConditionalSect(this);
        
        // import conditional section declarations if it should
        // be included; write it out if supposed to
		loadConditionalSect(conditionalSect);

	}
	
	/**
	 * Loads the DTD's conditional section.
	 * At this point the syntax of the conditional section has
	 * already been checked. This will write out the contents
	 * if the parser is supposed to.
	 *
	 * @param conditionalSect  a DTDData object
	 *
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void loadConditionalSect(DTDTextParser conditionalSect)
		throws DTDSyntaxException, DTDWriterException, EOFException,
		FileNotFoundException, FatalException {
	    
	    char nextChar;

	    // '['
		if (conditionalSect.getNextChar() != '[') //'['
		    throw new DTDSyntaxException("Expected '['");
		conditionalWrite('[');
		
		// S?
		nextChar = conditionalSect.peekAtNextChar();
		while (DTDTextParser.isWhiteSpace(nextChar)){
		    conditionalWrite(conditionalSect.getNextChar());
		    nextChar = conditionalSect.peekAtNextChar();
		}
		
		// IGNORE | INCLUDE
		String condType = null;
		if (conditionalSect.peekAtNextChar() == '%'){
		    // parameter entity
		    conditionalSect.getNextChar(); // '%'
		    String entName = conditionalSect.getAllSkipping(';');
		    conditionalWrite('%' + entName + ";");
		    Entity ent = getDtdSubset().getParamEntity(entName);
		    if (ent == null) {
		        throw new DTDSyntaxException("Undeclared entity");
		    }
		    condType = ent.getLiteralValue();
		    if (condType == null || 
		            (!condType.equals("IGNORE") && !condType.equals("INCLUDE"))){
		        throw new DTDSyntaxException(
		                "Expected entity to resolve to IGNORE or INCLUDE");
		    }
		    if (condType.equals("IGNORE")){
		        condType = "IGNORE";
		    } else if (condType.equals("INCLUDE")){
		        condType = "INCLUDE";
		    }
		} else if (conditionalSect.nextStringEquals("IGNORE")){
		    condType = "IGNORE";
		    conditionalWrite(conditionalSect.getChars("IGNORE".length()));
		} else if (conditionalSect.nextStringEquals("INCLUDE")) {
		    condType = "INCLUDE";
		    conditionalWrite(conditionalSect.getChars("INCLUDE".length()));
		} else {
		    throw new DTDSyntaxException("Expected IGNORE or INCLUDE");
		}
		
		// S?
		nextChar = conditionalSect.peekAtNextChar();
		while (DTDTextParser.isWhiteSpace(nextChar)){
		    conditionalWrite(conditionalSect.getNextChar());
		    nextChar = conditionalSect.peekAtNextChar();
		}
		
		// [
		nextChar = conditionalSect.getNextChar();
		if (nextChar != '['){
		    throw new DTDSyntaxException("Expected '['");
		}
		conditionalWrite(nextChar);

		if (condType.equals("INCLUDE")) {
			
			//extSubsetDecl
			while (!conditionalSect.endOfData()) {
			    // S?
				conditionalSect.handleWhiteSpace();
				
				// "]]>"
				if (conditionalSect.nextStringEquals("]]>")) {
				    conditionalSect.getChars("]]>".length());
				    conditionalWrite("]]>");
				    continue;
				}
				
        		// COMMENT
        		if (commentHandler.seeCommentStart(conditionalSect)) {
        			commentHandler.handleComment(conditionalSect);
        			conditionalSect.handleWhiteSpace();
        			continue;
        		}
        		
				// PI
				if (piReader.seePiStart(conditionalSect)) {
					piReader.handlePi(conditionalSect);
					conditionalSect.handleWhiteSpace();
					continue;
				}
				
				// PARAMETER REFERENCE
				try {
					if (conditionalSect.advanceIfNextChar('%')) {
						// parameter entity reference
						String entName = conditionalSect.getAllSkipping(';');
						conditionalWrite('%' + entName + ';');
						
						// resolve it
						Entity ent = this.getDtdSubset().getParamEntity(entName);
						if (ent.getExternalIdType() == Entity.EXT_ID_SYSTEM
							|| ent.getExternalIdType() == Entity.EXT_ID_PUBLIC) {
							if (DTDParser.loadExternalFiles() ) {
								//String newURI = constructRelLocalPath(ent.getSystemLiteral(), true);
								importLinkedToDtdDecls(ent.getSystemLiteral());
							} 
						} 
						conditionalSect.handleWhiteSpace();
						continue;
					}
				} catch (EOFException e1) {
		            // incomplete or malformed conditional section
		            throw new DTDSyntaxException("Conditional section ended prematurely");
		        }
				
				// START OF CONDITIONAL SECTION / ELEMENT / ATTLIST / 
				// NOTATION / ENTITY DECLARATIONS

				if (!conditionalSect.nextStringEquals("<!")){
				    throw new DTDSyntaxException("Expected <! here.");
				}
				conditionalSect.skipChars(2); // skip <!
				conditionalWrite("<!");

				if (conditionalSect.peekAtNextChar() == '[') {
				    //conditionalSect
					loadConditionalSect(conditionalSect);
					// continue?
				} else { 
					// declaration keyword ('ELEMENT', etc.)
					String sectName = conditionalSect.getAllSkippingNextWS();

					// slurp up the rest of the declaration not including the final '>'
					String content = conditionalSect.getAllSkipping('>');
					if (content == null || content.length() < 1){
					    throw new DTDSyntaxException("Declaration without content");
					}

			        // check that the last char is '>'
			        if (conditionalSect.peekAtPrevChar() != '>') {
			            throw new DTDSyntaxException("Expected > here.");
			        }
			        	
					// resolve any entity references in the declaration
					content = resolveEntities(new DTDTextParser(content)).toString();
					
					// create the appropriate object
					if (sectName.equals("ELEMENT")) {
						elementHandler.handleElement(content, this);
					} else if (sectName.equals("ATTLIST")) {
					    attributeHandler.handleAttribute(content, this);
					} else if (sectName.equals("ENTITY")) {
						entityHandler.handleEntity(content, this);
					} else if (sectName.equals("NOTATION")) {
					    notationReader.handleNotation(content, this);
					} else {
						throw new DTDSyntaxException(
							"Invalid section encountered with name '"
								+ sectName
								+ "' with content - \""
								+ content
								+ "\".");
					}
				}
			}
		} else if (condType.equals("IGNORE")) {
			//ignore it as far as importing declarations
		    // append its contents as is to the write buffer		    
		    String rest = conditionalSect.getRemaining();
		    conditionalWrite(rest);
		} 
		
	}
	
	/**
	 * Load an external file used to populate a parameter
	 * reference in the DTD.
	 *
	 * @param fileName an external file whose contents are
	 * 	needed for a parameter entity in the DTD
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws FatalException
	 */
	private void loadDtdEntities(String fileName) 
		throws DTDSyntaxException, DTDWriterException, FatalException {
		
		String methodName = "loadDtdEntities(String)";

		DTDSource newDtdSrc = null;
		
		String parentPath = this.getDFile().getPath();
		boolean cantResolve = false;

		String linkType = null;
		try {
            linkType = DTDSource.determineLinkType(fileName, parentPath);
            
            if (linkType == null){
                cantResolve = true;
            } else if (linkType.equals(Link.TYPE_ABS_PATH)){
                newDtdSrc = new DTDFile(fileName);
            } else if (linkType.equals(Link.TYPE_HTTP_URL)){
                newDtdSrc = new DTDUrl(fileName);
            } else if (linkType.equals(Link.TYPE_REL_PATH)){
                String absPath = DTDFile.getAbsolute(fileName, parentPath);
                newDtdSrc = new DTDFile(absPath);
            } else if (linkType.equals(Link.TYPE_UNKNOWN)){
                cantResolve = true;
            }
		} catch (MalformedURLException e) {
		    cantResolve = true;
        } catch (URISyntaxException e) {
            cantResolve = true;
        } catch (FileNotFoundException e) {
            cantResolve = true;
        }
        
        if (cantResolve && shouldAddAnoms()){
            this.getDFile().addAnomaly(
					this.getDFile().getAnomsPossible().getSevereElement(
					DTDAnomalies.DTD_MISSING_ENT_FILE));
            return;
        }

		try {
			buildEntityList(newDtdSrc.read());
		} catch (FileNotFoundException e) {
		    if (DTDParser.getParseActivity() == ACTION_CHECK_EXT_FILES){
				Informer.getInstance().warning(this, methodName, "File not found",
					"file: " + fileName);
				if (shouldAddAnoms()) {
				    this.getDFile().addAnomaly(this.getDFile().getAnomsPossible().
				    	getSevereElement(DTDAnomalies.DTD_MISSING_ENT_FILE));
				}
				
		    }
		    // continue parsing??

		} catch (IOException e) {
		    if (DTDParser.getParseActivity() == ACTION_CHECK_EXT_FILES){
				Informer.getInstance().warning(this, 
					methodName, "IO Exception","file: " + newDtdSrc);
				if (shouldAddAnoms()){
					this.getDFile().addAnomaly(
					this.getDFile().getAnomsPossible().getSevereElement(
					DTDAnomalies.DTD_BAD_ENT_FILE));
				}
			 }
		}
	}
	
	/**
	 * Loads all the predefined entities, plus any defined parameter
	 * and/or general entity declarations in this DTD.
	 * 
	 * 4.6 Predefined Entities [XML Rec 1.0]
	 *
	 * <!ENTITY lt "&#38;#60;">
	 * <!ENTITY gt "&#62;">
	 * <!ENTITY amp "&#38;#38;">
	 * <!ENTITY apos "&#39;">
	 * <!ENTITY quot "&#34;">
	 * 
	 * @param unresolvedDTD the contents of this DTD
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void loadEntities(DTDTextParser unresolvedDTD)
		throws DTDSyntaxException, DTDWriterException, EOFException,
			FileNotFoundException, FatalException {

		if (unresolvedDTD == null)
			throw new FileNotFoundException();
		 
		 this.getDtdSubset().addEntity(new Entity("lt", "&#38;#60;", Entity.GENERAL_ENTITY));

		 this.getDtdSubset().addEntity(new Entity("gt", "&#62;", Entity.GENERAL_ENTITY));

		 this.getDtdSubset().addEntity(new Entity("amp", "&#38;#38;", Entity.GENERAL_ENTITY));

		 this.getDtdSubset().addEntity(new Entity("apos", "&#39;", Entity.GENERAL_ENTITY));

		 this.getDtdSubset().addEntity(new Entity("quot", "&#34;", Entity.GENERAL_ENTITY));

		// load any defined parameter and/or general entity declarations
		buildEntityList(unresolvedDTD);
		
	}
	
	/**
	 * Parses a DTD performing a particular activity (seeing if
	 * its a DTD, extracting metadata, recognizing and storing links,
	 * etc.)
	 * 
	 * @param _dFile the file being parsed
	 * @param _parseActivity the activity the parser is performing
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void parse(DTD _dFile, byte _parseActivity) 
			throws FileNotFoundException, IOException, FatalException, 
			DTDSyntaxException, DTDWriterException {
	    parse(_dFile, _parseActivity, null);
	}
	
	/**
	 * Parses a DTD performing a particular activity (seeing if
	 * its a DTD, extracting metadata, recognizing and storing links,
	 * etc.)
	 * 
	 * @param _dFile the file being parsed
	 * @param _parseActivity the activity the parser is performing
	 * @param newFilePath absolute path of new file to create
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void parse(DTD _dFile, byte _parseActivity, String newFilePath) 
			throws FileNotFoundException, IOException, FatalException, 
			DTDSyntaxException, DTDWriterException {
				
			String methodName = "parse(DTD, byte, String)";
			
			if (_dFile == null) {
				Informer.getInstance().fail(
					this,
					methodName,
					"Null argument",
					"DTD _dFile: " + _dFile,
					new FatalException("Must be a non-null DTD"));
			}
		
			if (!isValidParseActivity(_parseActivity)){
				Informer.getInstance().fail(
					this,
					methodName,
					"Invalid parse activity",
					"parseActivity: " + _parseActivity,
					new FatalException("Not a valid parser activity"));
			}
			
			setParseActivity(_parseActivity);
			
			reset();
		
			// store it for helper utils so they can add links,
			// anomalies, etc. to it.
			this.dFile = _dFile;
			this.dtdSubset = dFile.getDSubset();
			
			// get absolute system path to file to parse
			// it's existence, readability already been checked
			String filePath = dFile.getPath();
			
			switch (_parseActivity) {
				case ACTION_IS_WELLFORMED:
				// see if it's well-formed DTD
				    // don't add anomalies to the file
				    setAddAnoms(false);
				    
					// don't recognize and store external links
					setBuildLinkSet(false);
					
					// don't load external entity files
					setLoadExternalFiles(false);
					
					// don't write contents out to a file
					setWriteFile(false);
				
					this.dtdSrc = new DTDFile(filePath);
					
					try {
						// read the file in and store its data
						dtd = dtdSrc.read();
						
						// build list of predefined entities plus any defined
						// parameter/general entities
						loadEntities(dtd);
						
						// move cursor back to beginning of file
						dtd.rewind();

						dtd.handleWhiteSpace();

						// parse again using the parsed entities
						while (!dtd.endOfData()) {
							parseNextSection();
						}
					} catch (DTDSyntaxException e) {
						// rethrow so we'll know its not well-formed
						throw e;
					} catch (DTDWriterException e) {
						throw e;
					}
					break;
				case ACTION_EXT_META:
					
					// don't record the anomalies yet, do it in ACTION_CHECK_EXT_FILES
					setAddAnoms(false);
					
					// will recognize links
					setBuildLinkSet(true);
					
					// don't load external entity files, those external files will not exist until retrieveLink.
					setLoadExternalFiles(false);  
					
					// don't write contents out to a file
					setWriteFile(false);
					
					this.dtdSrc = new DTDFile(filePath);
				
					try {
						// read the file in and store its data
						dtd = dtdSrc.read();
						
						// build list of predefined entities plus any defined
						// parameter/general entities
						loadEntities(dtd);
						
						// move cursor back to beginning of file
						dtd.rewind();

						dtd.handleWhiteSpace();

						// parse again using the parsed entities
						while (!dtd.endOfData()) {
							parseNextSection();
						}
					} catch (DTDSyntaxException e) {
						// ignore it, the ACTION_CHECK_EXT_FILES will report it
						// throw e;
					} 
					break;
			    case ACTION_CHECK_EXT_FILES:
					// should record the anomalies seen while parsing
					setAddAnoms(true);
					
					// will recognize links and add them to the datafile
					setBuildLinkSet(false);
					
					// do load external entity files
					setLoadExternalFiles(true);
					
					// don't write contents out to a file
					setWriteFile(false);
					
					this.dtdSrc = new DTDFile(filePath);
				
					try {
						// read the file in and store its data
						dtd = dtdSrc.read();
						
						// build list of predefined entities plus any defined
						// parameter/general entities
						loadEntities(dtd);
						
						// move cursor back to beginning of file
						dtd.rewind();

						dtd.handleWhiteSpace();

						// parse again using the parsed entities
						while (!dtd.endOfData()) {
							parseNextSection();
						}
					} catch (DTDSyntaxException e) {
						// rethrow so we'll know ...
						throw e;
					} 
					break;
				case ACTION_REP_LKS:
				    // don't add anomalies to the file
				    setAddAnoms(false); 
				    
				    // recognize links
				    setBuildLinkSet(true);
				    
				    // don't load external files
				    setLoadExternalFiles(false);
				    
					// don't write contents out to a file
					setWriteFile(false);
				    
				    writer = new DTDWriter(newFilePath, this.getDFile());
				    this.dtdSrc = new DTDFile(filePath);
				    
				    try { 
						// read the file in and store its data
						dtd = dtdSrc.read();
						
						// build list of predefined entities 
						loadEntities(dtd);
						
						// move cursor back to beginning of file
						dtd.rewind();
						
						// do write contents out to a file
						setWriteFile(true);
						
						dtd.handleWhiteSpace();
						
					    while (!dtd.endOfData()) {
							parseNextSection();
						}
					    
					    writer.cleanUp();
				    }
				    catch (IOException e) {
						// rethrow so we'll know ...
						throw e;
					} 
				    break;
			}
	}
	
	/**
	 * Parses the specified DTD. Used within conditional sections
	 * of a DTD, or to parse the contents of a DTD containing
	 * entity and other declarations.
	 *
	 * @param _dtdSrc The location of the DTD.
	 * @param _dFile the file that contained a link to this file
	 * 	now being parsed.
	 *
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void parse(DTDSource _dtdSrc, DTD _dFile) 
		throws DTDSyntaxException, DTDWriterException, EOFException, 
		FileNotFoundException, FatalException {
			
		String methodName = "parse(DTDSource)";
		
		boolean keepParsingThisFile = true;
		
		// setting this for the anomaly-adding
		this.dFile = _dFile;
		
		// setting this to a new DTDSubset instead of to the
		// dFile's DTDSubset prevents repeated element additions
		this.dtdSubset = new DTDSubset(_dFile);
		
		if (_dtdSrc == null) {
			Informer.getInstance().fail(
				this,
				methodName,
				"Illegal argument",
				"null DTDSource argument",
				new FatalException("DTDSource can't be null."));
		}

		reset();

		// changing where to get the dtd data from - this new file
		this.dtdSrc = _dtdSrc;
		
		try {
			// read the contents of this external DTD
			dtd = _dtdSrc.read();
		} catch (FileNotFoundException e) {
			// can't find this file
			if (shouldAddAnoms() && getParseActivity() == ACTION_CHECK_EXT_FILES) {
				this.getDFile().addAnomaly(
						this.getDFile().getAnomsPossible().getSevereElement(
						DTDAnomalies.DTD_MISSING_ENT_FILE));
			}
			keepParsingThisFile = false;
		} catch (IOException e) {
			// can't read this file
			if (shouldAddAnoms()){
				this.getDFile().addAnomaly(
						this.getDFile().getAnomsPossible().getSevereElement(
							DTDAnomalies.DTD_BAD_ENT_FILE));
			}

			keepParsingThisFile = false;
		}
		
		// build list of predefined entities plus any defined
		// parameter/general entities
		if (keepParsingThisFile) {
			loadEntities(dtd);
		
			// move cursor back to beginning of file
			dtd.rewind();
	
			dtd.handleWhiteSpace();

			while (!dtd.endOfData() && keepParsingThisFile) {
				parseNextSection();
			}
		}
	} 
	
	/**
	 * Parses a section of the DTD.
	 *
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 * @throws FileNotFoundException
	 */
	private void parseNextSection()
		throws DTDSyntaxException, DTDWriterException, EOFException,
		FileNotFoundException, FatalException {

		dtd.handleWhiteSpace(); // shouldn't this be gone already?

		if (dtd.endOfData())
			return;

		// COMMENT
		if (commentHandler.seeCommentStart(dtd)) {
			commentHandler.handleComment(dtd);
			dtd.handleWhiteSpace();
			return;
		}

		// PI
		if (piReader.seePiStart(dtd)) {
			piReader.handlePi(dtd);
			dtd.handleWhiteSpace();
			return;
		}

		// PARAMETER REFERENCE
		try {
			if (dtd.advanceIfNextChar('%')) {
				// parameter entity reference
				String entName = dtd.getAllSkipping(';');
				
				if (shouldWriteFile() && entName != null) {
					DTDParser.getWriter().write('%' + entName + ';');
				}
				
				Entity ent = this.getDtdSubset().getParamEntity(entName);
				if (ent.getExternalIdType() == Entity.EXT_ID_SYSTEM
					|| ent.getExternalIdType() == Entity.EXT_ID_PUBLIC) {
					if (DTDParser.loadExternalFiles() ) {
						//String newURI = constructRelLocalPath(ent.getSystemLiteral(), true);
						importLinkedToDtdDecls(ent.getSystemLiteral());
					} 
				} 
				dtd.handleWhiteSpace();
				return;
			}
		} catch (EOFException e1) {
            // incomplete or malformed conditional section
            throw new DTDSyntaxException("DTD ended prematurely");
        }

		// START OF CONDITIONAL SECTION / ELEMENT / ATTLIST / 
		// NOTATION / ENTITY DECLARATIONS

		if (!dtd.nextStringEquals("<!")){
		    throw new DTDSyntaxException("Expected <! here.", dtd.row());
		}
		dtd.skipChars(2); // skip <!
		if (shouldWriteFile()){
		    DTDParser.getWriter().write("<!");
		}
			
		// CONDITIONAL SECTION
		if (dtd.peekAtNextChar() == '[') { 
			loadConditionalSect();
			//dtd.handleWhiteSpace(); // probably not necessary
			return;
		}

		// declaration keyword ('ELEMENT', etc.)
		String sectName = dtd.getAllSkippingNextWS();

		// slurp up the rest of the declaration not including the final '>'
		String content = dtd.getAllSkipping('>');
		if (content == null || content.length() < 1){
		    throw new DTDSyntaxException("Declaration without content", dtd.row());
		}

        // check that the last char is '>'
        if (dtd.peekAtPrevChar() != '>') {
            throw new DTDSyntaxException("Expected > here.", dtd.row());
        }
        	
		// resolve any entity references in the declaration except during localization
        if (getParseActivity() != DTDParser.ACTION_REP_LKS){
        	content = resolveEntities(new DTDTextParser(content)).toString();
        }
        
		// create the appropriate object
		if (sectName.equals("ELEMENT")) {
			elementHandler.handleElement(content, this);
			return;
		} else if (sectName.equals("ATTLIST")) {
		    attributeHandler.handleAttribute(content, this);
		    return;
		} else if (sectName.equals("ENTITY")) {
			entityHandler.handleEntity(content, this);
			return;
		} else if (sectName.equals("NOTATION")) {
		    notationReader.handleNotation(content, this);
		    return;
		} else {
			throw new DTDSyntaxException(
				"Invalid section encountered with name '" + sectName + 
				"' with content - \"" + content + "\".", dtd.row());
		}

	}
	
	/**
	 * Resets parser flags.
	 */
	private void reset() {
		dtd.reset();
		writer = null;
	}
	
	/**
	 * Resolves parameter, general and character entity references.
	 * 
	 * Entities will be in the form -
	 *   &entityName;  for Global Entities
	 *   %entityName; for Parameter Entities
	 *
	 *   &#n; for char reference (where n is a decimal number)
	 *   &#xn; for char reference (where n is a hex number)
	 *
	 * @param content the content with entities to resolve
	 * @return the data with its entities resolved
	 * @throws DTDSyntaxException
	 * @throws EOFException
	 * @throws FatalException
	 */
	private DTDTextParser resolveEntities(DTDTextParser content) 
		throws DTDSyntaxException, EOFException, FatalException {
	    
	    boolean continueResolve = true;

		if (content == null)
			throw new IllegalArgumentException();

		String newContent = ""; // the content preceding the & or %
		char[] entityDelim = {'&', '%'};

		// move past the % or & saving what came before it
		newContent += content.getAllSkipping(entityDelim);
		// PTR: after delimiter

		while (!content.endOfData()) {
			boolean isParamEntity = (content.peekAtPrevChar() == '%' ? true : false);
            
            if (content.toString().indexOf(';') == -1) {
                continueResolve = false;
                //entName = content.toString();
            }
            
            String entName = null;
            
            if (continueResolve) {
                try {
                    entName = content.getAllSkipping(';'); // PTR: after first ';'
                    // In case the retrieved entity name contains another parameter,
                    // retrieve the last parameter.
                    int delIndex =  entName.indexOf('%');
                    if (delIndex != -1) {
                    	newContent += entName.substring(0, delIndex);
                    	entName = entName.substring(delIndex+1);
                    }
                    
                } catch (EOFException e1) {
                    // won't happen because we know it has a ';'
                    continueResolve = false;
                }
            }
            
            // see if this entity name was declared
            Entity entity = null;
            if (continueResolve) {
                entity = this.getDtdSubset().getEntity(entName);
            }
            
			if (entity == null && continueResolve) {
			    // entity not declared - check if its a character reference
			    // TODO: probably want to add some name length checking
				if (entName.charAt(0) == '#') { //CharRef
					int index = 10;

					String charValue;

					if (entName.charAt(1) == 'x') { //hex char
						index = 16;
						charValue = entName.substring(2, entName.length());
					} else {
						charValue = entName.substring(1, entName.length());
					}

					try {
						//CR: FIXME: Will this work for non-ascii chars
						char ch = (char) Integer.parseInt(charValue, index);
						newContent += ch;
					} catch (NumberFormatException ne) {
						throw new DTDSyntaxException("Bad character reference");
					}

				} else
				    // entity wasn't declared yet
					newContent += (isParamEntity ? '%' : '&') + entName + ';';
			} else if (continueResolve){
			    // entity was declared already - resolve it
				 String entValue = entity.getResolvedValue(this.getDtdSubset().getEntityList());
				newContent += entValue;
			} else {
			    char prevChar = content.peekAtPrevChar();
			    if (prevChar == '%' || prevChar == '&') {
			        newContent += prevChar;
			    }
			}
			// add to the resolved content the next entity reference
			// in the content not yet resolved
			newContent += content.getAllSkipping(entityDelim);
			// PTR: after next delimiter
		}

		return new DTDTextParser(newContent);
	}
}