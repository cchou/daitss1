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
 * 
 */
package edu.fcla.daitss.format.text.dtd;

import java.io.EOFException;
import java.util.Hashtable;

import edu.fcla.daitss.exception.FatalException;

/**
 * ElementHandler handles all DTD elements.
 *
 * @author Andrea Goethals, FCLA
 */
public class ElementHandler {
    
    /**
     * Performs tasks such as adding the element to the dtdSubset,
     * writing it to a file or ignoring it, depending on the task
     * the DTD parser is performing.
     * 
     * [45] elementdecl ::= ' <!ELEMENT' S Name S contentspec S? '>' [VC: Unique
     * Element Type Declaration] 
     * [46] contentspec ::= 'EMPTY' | 'ANY' | Mixed | children
     * [47] children ::= (choice | seq) ('?' | '*' | '+')? 
     * [48] cp ::= (Name | choice | seq) ('?' | '*' | '+')? 
     * [49] choice ::= '(' S? cp ( S? '|' S? cp )* S? ')' [VC: Proper Group/PE Nesting] 
     * [50] seq ::= '(' S? cp ( S? ',' S? cp )* S? ')' [VC: Proper Group/PE Nesting]
     * [51] Mixed ::= '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*' | '(' S?
     * '#PCDATA' S? ')' [VC: Proper Group/PE Nesting] [VC: No Duplicate Types]
     * 
     * @param content
     * @param parser DTD parser
     * @throws DTDSyntaxException
     * @throws DTDWriterException
     * @throws EOFException
     * @throws FatalException
     */
    public void handleElement(String content, DTDParser parser) 
    	throws DTDSyntaxException, DTDWriterException, EOFException, FatalException {
        
        if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META ||
                DTDParser.shouldWriteFile()) {
            
            boolean doneBuilding = false; // flag to know when parsing the element is done.
            
            // BUILD ELEMENT OBJECT
            Element elem = new Element();
            DTDTextParser elemData = new DTDTextParser(content);
            
            // leading white space (S)
            // (writing it out here if desired)
            elemData.handleWhiteSpace();
            
            // Name and S
            String elemName = elemData.getAllSkippingNextWS();
            if (elemName.length() < 1) {
                throw new DTDSyntaxException("Element name missing");
            }
            elem.setName(elemName);
            
            // ADD ELEMENT OBJECT
            if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META){
                parser.getDtdSubset().addElement(elem);
            }
            
            // contentspec
            String contentSpec = elemData.getRemaining().trim();
            if (contentSpec.length() < 1) {
                throw new DTDSyntaxException("Element contentspec missing");
            }
            elem.setContentSpec(contentSpec);
            
            if (contentSpec.equals("EMPTY")){
                elem.setHasEmptyContentSpec(true);
                doneBuilding = true;
                //return;
            } else if (contentSpec.equals("ANY")){
                doneBuilding = true;
                //return;
            }
            
            if (!doneBuilding){
                elemData.skipWhiteSpace();
                
                // mixed or children contentspec
                // '('
                if (elemData.getNextChar() != '('){
                    throw new DTDSyntaxException("Expected a ( character in element declaration");
                }
                // S
                elemData.skipWhiteSpace();
                
                char[] delimiters = { ' ', '\t', '\r', '\n', '|', ',', '(', ')' };
                String child; // element child
                Hashtable theChildren = new Hashtable();
                
                elemData.skipChars(delimiters);
                while (!elemData.endOfData()){
                    child = elemData.getAllSkipping(delimiters); // ADVANCE
                    if (child == null || child.trim().length() == 0
                            || child.equals("?") || child.equals("*")
                            || child.equals("+")){
                        continue; 
                    }
                    
                    // optionality
                    char lastChar = child.charAt(child.length()-1);
                    if (lastChar == '?' || lastChar == '*' || lastChar == '+'){
                        // remove last char
                        child = child.substring(0, child.length()-1);
                        theChildren.put(child, new Character(lastChar));
                    }
                    
                }
            }

            // WRITE ELEMENT OBJECT
            if (DTDParser.shouldWriteFile()){
                DTDParser.getWriter().write("ELEMENT " + elem.getName() +
                        " " + elem.getContentSpec() + ">");
            }
            
        } // otherwise ignore it
    }

}
