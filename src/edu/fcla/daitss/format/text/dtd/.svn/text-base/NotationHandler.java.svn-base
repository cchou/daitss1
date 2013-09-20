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

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.text.dtd.DTDWriterException;
import edu.fcla.daitss.util.ArchiveProperties;


/**
 * Class to read a Notation declaration.
 *
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class NotationHandler {
	
	/**
	 * Token identifying a notation declaration.
	 */
	private static final String NOTATION_START = "<!NOTATION";

	/**
	 * Parse a notation declaration.
	 * 
	 * @param data All DTD content with a pointer at the notation declaration
	 * @param parser DTD parser
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 */
	public void buildNotation(DTDTextParser data, DTDParser parser) 
		throws DTDSyntaxException, DTDWriterException, EOFException, FatalException {
	    
	    if(!seeNotationStart(data)) {
			throw new DTDSyntaxException("Not a notation declaration start");
		}
	    
	    data.getAllSkippingNextWS(); // '<!NOTATION'
		data.handleWhiteSpace(); // S
		
		String rest = data.getAllSkipping('>');
		if (rest == null || rest.length() < 1){
		    throw new DTDSyntaxException("Notation declaration without content");
		}
	    
		// see if we need to do something with the notation other
		// than skip over it
		if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META ||
		        DTDParser.buildLinkSet() ) {
		    
			DTDTextParser notData = new DTDTextParser(rest);
			
			// read the notation
			// S
			notData.skipWhiteSpace();
			
			// Name
			String name = notData.getNextName();
			if (name == null || name.length() < 1){
			    throw new DTDSyntaxException("Notation missing name");
			}
			Notation not = new Notation(name);
			// ADD NOTATION TO DTD BITSTREAM
			if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META) {
			    parser.getDtdSubset().addNotation(not);
			}
			
			// S
			notData.skipWhiteSpace();
			
			// (ExternalID | PublicID)
			String nextKey = notData.getAllSkippingNextWS(); // 'SYSTEM' S | 'PUBLIC' S
			if (nextKey == null || (!nextKey.equals("PUBLIC") && (!nextKey.equals("SYSTEM")))){
			    throw new DTDSyntaxException("Expected SYSTEM or PUBLIC here");
			}
			
			char quoteType;
			String content;
			if (nextKey.equals("SYSTEM")){
			    // read SystemLiteral
			    content = readSystemLiteral(notData); // move past end quote
			    if (content == null || content.length() < 1){
			        throw new DTDSyntaxException("Empty system literal");
			    }
			    not.setSystemLiteral(content);
			} else { // PUBLIC
			    // read PubidLiteral
				notData.skipWhiteSpace();
			    quoteType = notData.getNextChar(); // move past start quote
			    if (!DTDTextParser.isQuotChar(quoteType)){
			        throw new DTDSyntaxException("Expected a quote character here");
			    }
			    content = notData.getAllSkipping(quoteType); // move past end quote
			    if (!DTDTextParser.isPubidLiteral(content)){
			        throw new DTDSyntaxException("Invalid PubidLiteral");
			    }
			    not.setPubidLiteral(content);
			    
			    // S
			    notData.skipWhiteSpace();
			    
			    // check for SystemLiteral
			    if (!notData.endOfData()){
			        content = readSystemLiteral(notData); // move past end quote
			        if (content == null || content.length() < 1){
				        throw new DTDSyntaxException("Empty system literal");
				    }
				    not.setSystemLiteral(content);
			    }
			}
			
			// look for the link
			// see if there's a link and if it should be recorded
			if (DTDParser.buildLinkSet() && not.getSystemLiteral() != null
					&& !not.getSystemLiteral().equals("")) {
			    if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META){
					// add link to file
					Link lk = parser.constructLink(not.getSystemLiteral(),
						ArchiveProperties.getInstance().
							getArchProperty("HARVEST_DTD_NOTATIONS"));
					parser.getDFile().addLink(lk);
			    } else if (DTDParser.getParseActivity() == DTDParser.ACTION_REP_LKS){
					String newURI = 
						parser.constructRelLocalPath(not.getSystemLiteral(), true);
					not.setSystemLiteral(newURI);
			    }

			}
		}
	}
	
	/**
	 * 
	 * [82]    NotationDecl ::= '<!NOTATION' S Name S (ExternalID | PublicID) S? '>'
	 * [83]    PublicID    ::=    'PUBLIC' S PubidLiteral  
	 * [75]    ExternalID    ::=    'SYSTEM' S SystemLiteral | 
	 * 				'PUBLIC' S PubidLiteral S SystemLiteral   
	 * 
	 * PublicID and ExternalID are better combined as:
	 * 'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral (S SystemLiteral)?
	 * 
	 * @param data the notation declaration content after NOTATION S
	 * @param parser DTD parser
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 * @throws FatalException
	 */
	public void handleNotation(String data, DTDParser parser) 
		throws DTDSyntaxException, DTDWriterException, EOFException, FatalException {

		// write it if necessary
	    if (DTDParser.shouldWriteFile()){
	        
	        if (data == null || data.length() < 1) {
				throw new DTDSyntaxException("Notation declaration without content");
			}
	        
	        DTDTextParser notData = new DTDTextParser(data);
			
			// read the notation
			// S
			notData.skipWhiteSpace();
			
			// Name
			String name = notData.getNextName();
			if (name == null || name.length() < 1){
			    throw new DTDSyntaxException("Notation missing name");
			}
			Notation not = parser.getDtdSubset().getNotation(name);
	    
	        writeNotation(not, parser);
	        
	    }
	}
	
	/**
	 * Parses a 'SystemLiteral' construct.
	 * 
	 * [11]    SystemLiteral    ::=    ('"' [^"]* '"') | ("'" [^']* "'")
	 * 
	 * @param notData
	 * @return the portion between the quotes
	 * @throws DTDSyntaxException
	 * @throws EOFException
	 */
	private String readSystemLiteral(DTDTextParser notData)
		throws DTDSyntaxException, EOFException {
	    // read SystemLiteral
	    char quoteType = notData.getNextChar(); // move past start quote
	    if (!DTDTextParser.isQuotChar(quoteType)){
	        throw new DTDSyntaxException("Expected a quote character here");
	    }
	    String content = notData.getAllSkipping(quoteType); // move past end quote
	    
	    return content;
	}

	/**
	 * Checks if there is a notation declaration at the 
	 * beginning of the data.
	 * 
	 * @param data The data to be parsed.
	 * 
	 * @return <code>true</code> if the current location
	 * 	of the data has a notation declaration; 
	 * 	<code>false</code> otherwise.
	 * @throws EOFException
	 */
	public boolean seeNotationStart(DTDTextParser data) throws EOFException {
		return data.nextStringEquals(NOTATION_START + " ");
	}
	
	/**
	 * Writes out a notation declaration.
	 * 
	 * @param not Notation object
	 * @param parser DTD parser
	 * @throws DTDWriterException
	 * @throws FatalException
	 */
	private void writeNotation(Notation not, DTDParser parser) 
		throws DTDWriterException, FatalException {
	    DTDParser.getWriter().write("NOTATION "  + not.getName() + " ");
        String sysLit = not.getSystemLiteral();
        String pubLit = not.getPubidLiteral();
        if (DTDParser.getParseActivity() == DTDParser.ACTION_REP_LKS &&
                sysLit != null){
             sysLit = parser.constructRelLocalPath(sysLit, true);
        }
       
        if (pubLit != null){
            // 'PUBLIC' S PubidLiteral (S SystemLiteral)?
			DTDParser.getWriter().write("PUBLIC \"" + pubLit + "\"");
			if (sysLit != null){
			    DTDParser.getWriter().write(" \"" + sysLit + "\"");
			}
		} else if (sysLit != null) {
		    // 'SYSTEM' S SystemLiteral
		    DTDParser.getWriter().write("SYSTEM \"" + sysLit + "\"");
		}
        
        // '>'
        DTDParser.getWriter().write(">");
	}

}
