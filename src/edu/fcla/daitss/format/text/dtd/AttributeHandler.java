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
package edu.fcla.daitss.format.text.dtd;

import java.io.EOFException;
import java.util.ArrayList;

import edu.fcla.daitss.exception.FatalException;

/**
 * AttributeHandler handles (parses, writes) DTD attributes.
 * 
 * @author Andrea Goethals, FCLA
 */
public class AttributeHandler {
    
    /**
     * List of valid tokenized types.
     */
    private static final String[] tokenizedTypes = { "ID", "IDREF", "IDREFS",
            "ENTITY", "ENTITIES", "NMTOKEN", "NMTOKENS" };
    
    /**
     * Checks the data type for this attribute and determines its attribute
     * type.
     * 
     * @param at attribute being parsed
     * @param token The data type for this attribute.
     */
    private void checkAttType(String token, Attribute at) {

        if (token.equals("CDATA")) {
            at.setDataType(token);
            at.setAttrType(Attribute.TYPE_STRING);
        } else if (checkIsTokenizedType(token)) {
            at.setDataType(token);
            at.setAttrType(Attribute.TYPE_TOKENIZED);
        } else if (token.equals("NOTATION")) {
            at.setAttrType(Attribute.TYPE_ENUM_NOTATION);
        } else {
            at.setAttrType(Attribute.TYPE_ENUM_ENUMERATION);
        }
    }
    
    /**
     * Check if the Attribute is a Tokenized type.
     * 
     *
     * [56] TokenizedType ::= 'ID' | 'IDREF' | 'IDREFS' | 'ENTITY' |
     * 'ENTITIES' | 'NMTOKEN' | 'NMTOKENS'
     *
     * 
     * @return <code>true</code> if this attribute is of tokenized type;
     *         <code>false</code> otherwise.
     * @param token The data type for this attribute.
     */
    private boolean checkIsTokenizedType(String token) {

        for (int i = 0; i < tokenizedTypes.length; i++) {
            if (token.equals(tokenizedTypes[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Performs tasks such as adding the attribute list to the dtdSubset,
     * writing it to a file or ignoring it, depending on the task
     * the DTD parser is performing.
     * 
     * [52] AttlistDecl ::= '<!ATTLIST' S Name AttDef* S? '>'
     * [53] AttDef ::= S Name S AttType S DefaultDecl
     * 
     * @param content
     * @param parser
     * @throws DTDSyntaxException
     * @throws DTDWriterException
     * @throws EOFException
     * @throws FatalException
     */
    public void handleAttribute(String content, DTDParser parser) 
		throws DTDSyntaxException, DTDWriterException, EOFException, FatalException {
        
        if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META ||
                DTDParser.shouldWriteFile()) {
            
            // BUILD ATTRIBUTE LIST
            AttList aList = new AttList();
            DTDTextParser aListData = new DTDTextParser(content.trim());
            
            // leading white space (S)
            // (writing it if desired here)
            aListData.handleWhiteSpace();
            
            // Name S
           
            String elemName = aListData.getAllSkippingNextWS();
            if (elemName.length() < 1) {
                throw new DTDSyntaxException("Element name missing from attrbute declaration");
            }
            aList.setElemName(elemName);
           
            // ADD ATTRIBUTE LIST OBJECT
            if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META){
                parser.getDtdSubset().addAttList(aList);
            }
            boolean sawAnAttribute = false;
             Attribute anAtt = null;
             
            try {
                String attDef = aListData.getRemaining();
                if (attDef.length() < 1) {
                    throw new DTDSyntaxException("Attribute definition missing from attrbute declaration");
                }
                aList.setAttDef(attDef);
                
                sawAnAttribute = true;
                
                // parse and add attribute except during localization
           	 	if (DTDParser.getParseActivity() != DTDParser.ACTION_REP_LKS){
           	 		while (!aListData.endOfData()) {
                		 anAtt = readNextAttribute(aListData);
                         aList.addAttribute(anAtt);         
                         aListData.skipWhiteSpace();
                	 }    
                }
            } catch (EOFException e) {
                // at end of attribute content - make sure there was at least 1 attribute
              if (!sawAnAttribute){
               		//throw new DTDSyntaxException("Attribute list without attributes");
                }
            } 
            
            if (DTDParser.shouldWriteFile()) {
            	if (sawAnAttribute)
            		DTDParser.getWriter().write("ATTLIST " + aList.getElemName() + " " +
                        aList.getAttDef() + ">");
            	else
            		DTDParser.getWriter().write("ATTLIST " + aList.getElemName() + " " + ">");
            }
        }
    }
    
    /**
     * Reads the default declaration for the attribute.
     * 
     * [60] DefaultDecl ::= '#REQUIRED' | '#IMPLIED' | (('#FIXED' S)? AttValue)
     * [10] AttValue ::= '"' ([^ <&"] | Reference)* '"' | "'" ([^ <&'] |
     * Reference)* "'"
     * 
     * [67] Reference ::= EntityRef | CharRef [68] EntityRef ::= '&' Name ';'
     * [66] CharRef ::= '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';'
     * 
     * @param attDef The data stream to read from.
     * @param at attribute being parsed
     * @throws EOFException
     */
    private void readDefaultDecl(DTDTextParser attDef, Attribute at) 
    	throws EOFException {

        String defaultDecl = null;

        //get the DefaultDecl
        attDef.skipWhiteSpace();

        char nextChar = attDef.peekAtNextChar();

        if ((nextChar == '\'') || (nextChar == '\"')) {

            //set the optionality
            at.setOptionality(Attribute.OPTIONALITY_DEFAULT);

            char[] defaultDeclDelim = { attDef.getNextChar() }; // this will be
                                                                // either ' or "

            defaultDecl = attDef.getAllSkipping(defaultDeclDelim);
            at.setDefaultValue(defaultDecl);

        } else if (nextChar == '#') {
            String _optionality = attDef.getAllSkippingNextWS(); //'#REQUIRED'
                                                                 // |
                                                                 // '#IMPLIED'|
                                                                 // '#FIXED'
            at.setOptionality(_optionality);

            if (_optionality.equals("#FIXED")) {
                attDef.skipWhiteSpace();
                char[] defaultDeclDelim = { attDef.getNextChar() }; // this
                                                                    // should be
                                                                    // either '
                                                                    // or "
                defaultDecl = attDef.getAllSkipping(defaultDeclDelim);

                at.setDefaultValue(defaultDecl);
            }
        }
    }
    
    /**
     * Read the permitted values for the Enumerated type. <p/>
     * 
     * Parses the data passed based on the following grammar -
     * [59] Enumeration ::= '(' S? Nmtoken (S? '|' S? Nmtoken)* S? ')'
     * 
     * @param enums DTDData containing the permitted values in dtd syntax.
     * @return A String array containing the list of permitted values.
     * @throws DTDSyntaxException
     * @throws EOFException
     */
    private String[] readEnums(DTDTextParser enums) 
    	throws DTDSyntaxException, EOFException {

        ArrayList _enumeration = new ArrayList();

        enums.skipWhiteSpace();
        if (!enums.advanceIfNextChar('(')) {
            throw new DTDSyntaxException("Bad attribute enumeration");
        }
        enums.skipWhiteSpace();

        String nmToken = enums.getNextNmToken();

        if (nmToken == null) {
            throw new DTDSyntaxException("An enumeration without any values");
        }

        while (nmToken != null) {
            _enumeration.add(nmToken);
            enums.skipWhiteSpace();

            if (enums.advanceIfNextChar(')')) {
                return (String[]) _enumeration.toArray(new String[0]);
            } else if (enums.advanceIfNextChar('|')) {
                enums.skipWhiteSpace();
                nmToken = enums.getNextNmToken();
            } else {
                //invalid char!
                throw new DTDSyntaxException("Bad enumeration in attribute declaration");
            }
        }

        return (String[]) _enumeration.toArray(new String[0]);
    }
    
    /**
     * Read the attribute details from the given data.
     * 
     * [53] AttDef ::= S Name S AttType S DefaultDecl
     * 
     * [54] AttType ::= StringType | TokenizedType | EnumeratedType 
     * [55] StringType ::= 'CDATA' 
     * [56] TokenizedType ::= 'ID' | 'IDREF' |
     * 		'IDREFS' | 'ENTITY' | 'ENTITIES' | 'NMTOKEN' | 'NMTOKENS'
     * [57] EnumeratedType ::= NotationType | Enumeration 
     * [58] NotationType ::= 'NOTATION' S '(' S? Name (S? '|' S? Name)* S? ')' 
     * [59] Enumeration ::= '(' S? Nmtoken (S? '|' S? Nmtoken)* S? ')'
     * 
     * @param attDef attribute definition
     * @return the parsed attribute
     * @throws DTDSyntaxException
     * @throws EOFException
     */
    public Attribute readNextAttribute(DTDTextParser attDef) 
    	throws DTDSyntaxException, EOFException {
        
        Attribute at = new Attribute();
        
        // S
        attDef.skipWhiteSpace();
        
        // Name S
        String attName = attDef.getAllSkippingNextWS();
        if (attName.length() < 1) {
            throw new DTDSyntaxException("Attribute name missing");
        }
        at.setAttributeName(attName);
        
        // AttType
        attDef.skipWhiteSpace();
        String typeKey = attDef.getAllSkippingNextWS();
        checkAttType(typeKey, at);
        char nextChar;
        
        if (at.isNotationType()) { 
            // NotationType
            // S
            attDef.skipWhiteSpace();
            
            // '('
            nextChar = attDef.getNextChar();
            if (nextChar != '('){
                throw new DTDSyntaxException("Bad notation attribute declaration");
            }
            
            // S?
            attDef.skipWhiteSpace();
            
            // Name S
            attDef.getAllSkippingNextWS();
            
            // (S? '|' S? Name)*
            attDef.skipWhiteSpace();
            
            boolean seeMoreNames = true;
            while (seeMoreNames) {
                nextChar = attDef.peekAtNextChar();
                if (nextChar != '|'){
                    seeMoreNames = false;
                    break;
                }
                // '|"
                attDef.getNextChar();
                // S?
                attDef.skipWhiteSpace();
                // Name S?
                attDef.getAllSkippingNextWS();
            }
            
            // S?
            attDef.skipWhiteSpace();
            
            // ')'
            nextChar = attDef.getNextChar();
            if (nextChar != ')'){
                throw new DTDSyntaxException("Bad notation attribute declaration");
            }
        } else if (at.isEnumeration()) { //Enumeration
            at.setAttrType(Attribute.TYPE_ENUM_ENUMERATION);
            
            if (typeKey.length() == 0 || typeKey.charAt(typeKey.length() - 1) != ')') {
                //read until you've gotten all the enums
                while (!attDef.endOfData()
                        && (nextChar = attDef.getNextChar()) != ')')
                    typeKey += nextChar;
                typeKey += ')';
            }

            DTDTextParser enumData = new DTDTextParser(typeKey);
            at.setEnumeration(readEnums(enumData));
        }
        
        readDefaultDecl(attDef, at);
        
        return at;
    }

}
