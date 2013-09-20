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
import java.util.Hashtable;

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;

/**
 * Handles (reads, writes, resolves) entity declarations.
 *
 * @author Andrea Goethals, FCLA
 */
public class EntityHandler {
    
	/**
	 * Token identifying an entity declaration.
	 */
	private static final String ENTITY_DECL_START = "<!ENTITY";
	
	/**
	 * Checks if there is an entity declaration
	 * at the current location of the data.
	 * 
	 * @param data The data to be parsed.
	 * 
	 * @return <code>true</code> if the current location
	 * 	of the data has an entity declaration; 
	 * 	<code>false</code> otherwise.
	 * @throws EOFException
	 */
	public boolean isEntityDeclStart(DTDTextParser data) throws EOFException {
		return data.nextStringEquals(ENTITY_DECL_START + " ");
	}
	
	/**
	 * 
	 * [70] EntityDecl ::= GEDecl | PEDecl 
	 * [71] GEDecl ::= '<!ENTITY' S Name S EntityDef S? '>' 
     * [72] PEDecl ::= ' <!ENTITY' S '%' S Name S PEDef S? '>' 
     * [73] EntityDef ::= EntityValue | (ExternalID NDataDecl?) 
     * [74] PEDef ::= EntityValue | ExternalID
     * [75] ExternalID ::= 'SYSTEM' S SystemLiteral | 'PUBLIC' S 
     * 		PubidLiteral S SystemLiteral 
     * [76] NDataDecl ::= S 'NDATA' S Name
     * 
     * [9] EntityValue ::= '"' ([^%&"] | PEReference | Reference)* '"' | "'"
     * 		([^%&'] | PEReference | Reference)* "'" 
     * [11] SystemLiteral ::= ('"' [^"]* '"') | ("'" [^']* "'") 
     * [12] PubidLiteral ::= '"' PubidChar* '"' | "'" (PubidChar - "'")* "'" 
     * [13] PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
     * 
	 * @param content
	 * @param parser
	 * @throws DTDSyntaxException
	 * @throws EOFException
	 * @throws FatalException
	 */
	public void buildEntity(String content, DTDParser parser) 
		throws DTDSyntaxException, EOFException, FatalException {
    
	    if (content == null || parser == null){
	        return;
	    }
	        
        // BUILD ENTITY
        Entity ent = new Entity();
        DTDTextParser entData = new DTDTextParser(content);
        
        // leading white space (S)
        // (writing it if desired here)
        entData.skipWhiteSpace();
        
        // entity type
        if (entData.advanceIfNextChar('%')){
            // parameter entity
            ent.setType(Entity.PARAMETER_ENTITY);
            
            // S
            entData.skipWhiteSpace();
            
            // Name S
            String entName = entData.getAllSkippingNextWS();
            if (entName.length() < 1){
                throw new DTDSyntaxException("No entity name in entity declaration");
            }
            ent.setName(entName);
            
            // PEDef
            // EntityValue | ExternalID
            String entityValue = readEntityValue(entData);
            if (entityValue == null){
                // ExternalID
                readExtId(entData, ent); // will set ent members
            } else {
                // EntityValue
                //Carol Chou 7/21/05 entityValue = resolveEntities(new DTDTextParser(entityValue), 
                 //       parser.getDtdSubset().getEntityList()).toString();
                ent.setLiteralValue(entityValue);
            }
            
            // S?
            entData.skipWhiteSpace();
            
        } else {
            // general entity
            ent.setType(Entity.GENERAL_ENTITY);
            
            // S
            entData.skipWhiteSpace();
            
            // Name S
            String entName = entData.getAllSkippingNextWS();
            if (entName.length() < 1){
                throw new DTDSyntaxException("No entity name in entity declaration");
            }
            ent.setName(entName);
            
            // EntityDef
            // EntityValue | (ExternalID NDataDecl?)
            String entityValue = readEntityValue(entData);
            if (entityValue == null){
                // (ExternalID NDataDecl?)
                readExtId(entData, ent); // will set ent members
                
                // NDataDecl?
                // S
                entData.skipWhiteSpace();
                if (!entData.endOfData()){
                    // NDataDecl
                    // 'NDATA'
                    String nextWd = entData.getAllSkippingNextWS();
                    if (!nextWd.equals("NDATA")){
                        throw new DTDSyntaxException(
                                "Expected NDATA here in this entity declaration");
                    }
                    // S
                    entData.skipWhiteSpace();
                    // Name
                    nextWd = entData.getAllSkippingNextWS();
                    if (nextWd == null || nextWd.length() < 1){
                        throw new DTDSyntaxException(
                                "Missing NDataDecl name in entity declaration");
                    }
                    ent.setNDataName(nextWd);
                }
            } else {
                // EntityValue
                ent.setLiteralValue(entityValue);
            }
        }
    
        // RECORD ANY EXTERNAL LINKS
        if (DTDParser.buildLinkSet() && ent.getSystemLiteral() != null &&
				!ent.getSystemLiteral().equals("")) {
            String configProperty = null;
            if (ent.getType() == Entity.PARAMETER_ENTITY){
                configProperty = "HARVEST_DTD_P_ENTITIES";
            } else if (ent.getType() == Entity.GENERAL_ENTITY) {
                configProperty = "HARVEST_DTD_G_ENTITIES";
            }
            if (DTDParser.getParseActivity() == DTDParser.ACTION_EXT_META){
				// add parameter entity link to file
				Link lk = parser.constructLink(ent.getSystemLiteral(),
					ArchiveProperties.getInstance().
						getArchProperty(configProperty));
				parser.getDFile().addLink(lk);
            } else if (DTDParser.getParseActivity() == DTDParser.ACTION_REP_LKS){
				String newURI = 
					parser.constructRelLocalPath(ent.getSystemLiteral(), true);
				ent.setSystemLiteral(newURI);
            }
        }
        
        // ADD THE ENTITY TO THE DTD BITSTREAM
        parser.getDtdSubset().addEntity(ent);
            
	}
	
	/**
	 * Write the entity out if the parser should be doing that.
	 * This is called after entities have already been 'built'.
	 * 
	 * @param content entity declaration content
	 * @param parser dtd parser
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 */
	public void handleEntity(String content, DTDParser parser) 
		throws DTDSyntaxException, DTDWriterException, EOFException{
	    if (DTDParser.shouldWriteFile()){
	        DTDParser.getWriter().write("ENTITY " );
	        
	        DTDTextParser entContent = new DTDTextParser(content);
	        
	        // S
	        entContent.handleWhiteSpace();
	        
	        // %?
	        if (entContent.advanceIfNextChar('%')){
	            DTDParser.getWriter().write('%');
	            entContent.handleWhiteSpace();
	        }
	        
	        // Name S
	        String entName = entContent.getAllSkippingNextWS();
	        
	        Entity ent = parser.getDtdSubset().getEntity(entName);
	        if (ent == null) {
	            // somehow did not process this entity earlier
	            throw new DTDSyntaxException("Could not proces this entity name: " + entName);
	        }
	        
	        DTDParser.getWriter().write(entName + " "); // potentially compressing white space
	        
	        int extIDType = ent.getExternalIdType();

	        if (extIDType == Entity.EXT_ID_NONE) {
	            // EntityValue
	            DTDParser.getWriter().write("\"" + ent.getLiteralValue() + "\"");
	        } else if (extIDType == Entity.EXT_ID_SYSTEM) {
	            // 'SYSTEM' S SystemLiteral
	            DTDParser.getWriter().write("SYSTEM \"" + ent.getSystemLiteral() + "\"");
	        } else if (extIDType == Entity.EXT_ID_PUBLIC) {
	            // 'PUBLIC' S PubidLiteral S SystemLiteral
	            DTDParser.getWriter().write("PUBLIC \"" + ent.getPubidLiteral() + "\" \""
	                    + ent.getSystemLiteral() + "\"");
	        }
	        
	        // NDataDecl
	        if (ent.getType() == Entity.GENERAL_ENTITY &&
	                ent.getNDataName() != null){
	            DTDParser.getWriter().write("NDATA " + ent.getNDataName());
	        }

	        // '>'
	        DTDParser.getWriter().write(">");
	    }
	}
	
	/**
     * Returns the resolved value for this entity.
     * 
     * @param EntityList All the declared entities in this DTD.
     * @param ent The entity to resolve
     * @return The resolved value.
     * @throws EOFException
     */
    public String getResolvedValue(Hashtable EntityList, Entity ent)
            throws EOFException {
        return resolveEntities(new DTDTextParser(ent.getLiteralValue()), EntityList)
                .toString();
    }
	
	/**
     * Parses the entity value from the string.
     * The entity value is delimited by the ' or " character.
     * 
     * @return The entity value.
     * @param dtdFrag The dtd stream.
     * @throws EOFException
     */
    private String readEntityValue(DTDTextParser dtdFrag) throws EOFException {

        String value = null;
        
        dtdFrag.skipWhiteSpace();
        
        char delim = dtdFrag.peekAtNextChar();
        if ((delim == '\'') || (delim == '\"')) { //EntityValue
            dtdFrag.getNextChar(); // move past ' or "
            String entityVal = dtdFrag.getAllSkipping(delim);
            value = entityVal;
        }

        return value;
    }
    
    /**
     * Reads the external ID portion of the entity declaration.
     * 
     * [75] ExternalID ::= 'SYSTEM' S SystemLiteral | 'PUBLIC' S
     * PubidLiteral S SystemLiteral
     * 
     * [11] SystemLiteral ::= ('"' [^"]* '"') | ("'" [^']* "'") [12]
     * PubidLiteral ::= '"' PubidChar* '"' | "'" (PubidChar - "'")* "'" [13]
     * PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
     * 
     * @param dtdFrag external ID
     * @param ent Entity to set external ID-related members
     * @throws DTDSyntaxException
     * @throws EOFException
     */
    private void readExtId(DTDTextParser dtdFrag, Entity ent) 
    	throws DTDSyntaxException, EOFException {

        char delim;
        String identifier = dtdFrag.getAllSkippingNextWS();
        
        if (identifier.equals("SYSTEM")) {
            // S SystemLiteral
            ent.setExternalIdType(Entity.EXT_ID_SYSTEM);
            
            // S
            dtdFrag.skipWhiteSpace();
            
            //SystemLiteral
            delim = dtdFrag.peekAtNextChar(); //this has to be ' or "
            if ((delim != '\'') && (delim != '\"')) {
                throw new DTDSyntaxException("Was expecting '\'' or '\"'.");
            }
            char[] sysLiteralDelim = { dtdFrag.getNextChar() }; // ' or "
            String SystemLiteral = dtdFrag
                    .getAllSkipping(sysLiteralDelim);
            ent.setSystemLiteral(SystemLiteral);
        } else if (identifier.equals("PUBLIC")) {
            // S PubidLiteral S SystemLiteral
            ent.setExternalIdType(Entity.EXT_ID_PUBLIC);
            
            // S
            dtdFrag.skipWhiteSpace();
            
            //PubidLiteral
            delim = dtdFrag.peekAtNextChar(); //this has to be ' or "
            if ((delim != '\'') && (delim != '\"')) {
                throw new DTDSyntaxException("Was expecting '\'' or '\"'.");
            }
            char[] pubidLiteralDelim = { dtdFrag.getNextChar() }; // ' or "
            String PubidLiteral = dtdFrag.getAllSkipping(pubidLiteralDelim);
            ent.setPubidLiteral(PubidLiteral);

            // S
            dtdFrag.skipWhiteSpace();
            
            //SystemLiteral
            delim = dtdFrag.peekAtNextChar(); //this has to be ' or "
            if ((delim != '\'') && (delim != '\"')) {
                throw new DTDSyntaxException("Was expecting '\'' or '\"'.");
            }
            char[] sysLiteralDelim = { dtdFrag.getNextChar() }; // ' or "
            String SystemLiteral = dtdFrag.getAllSkipping(sysLiteralDelim);
            ent.setSystemLiteral(SystemLiteral);
        } else {
            throw new DTDSyntaxException("Was expecting \"PUBLIC\" or \"SYSTEM\"");
        }
        
    }
    
    /**
     * Resolves entities.
     * 
     * @param content the content containing entities to resolve
     * @param EntityList the parsed entities to resolve against
     * @return the content with the entities resolved
     * @throws EOFException
     */
    private DTDTextParser resolveEntities(DTDTextParser content, Hashtable EntityList)
            throws EOFException {

        String newContent = "";
        char[] entityDelim = { '&', '%' };

        newContent += content.getAllSkipping(entityDelim);

        while (!content.endOfData()) {
            boolean pEntity = (content.peekAtPrevChar() == '%' ? true : false);
            String entName = content.getAllSkipping(';');
            Entity entity = (Entity) EntityList.get(entName);

            if (entity == null) {
                if (entName.charAt(0) == '#') { //CharRef
                    int index = 10;

                    if (entName.charAt(2) == 'x') { //hex char
                        index = 16;
                    }
                    String charValue = entName.substring(2, entName.length());
                    char ch = (char) Integer.parseInt(charValue, index);

                    newContent += ch;
                } else
                    newContent += (pEntity ? '%' : '&') + entName + ';';
            } else {
                String entValue = entity.getLiteralValue();
                newContent += entValue;
            }
            newContent += content.getAllSkipping(entityDelim);
        }

        return new DTDTextParser(newContent);
    }

}
