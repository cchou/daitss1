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
import java.util.Hashtable;


/**
 * Class to hold an entity declaration.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class Entity extends ExternalID {

    /**
     * Has no external id.
     * There was an EntityValue instead of an ExternalID.
     */
    public static final int EXT_ID_NONE = 0;

    /**
     * Has a public id and a system ID.
     */
    public static final int EXT_ID_PUBLIC = 2;

    /**
     * Has a system id.
     */
    public static final int EXT_ID_SYSTEM = 1;

    /**
     * general entity.
     */
    public static final int GENERAL_ENTITY = 1;

    /**
     * parameter entity.
     */
    public static final int PARAMETER_ENTITY = 0;

    /**
     * external ID type
     */
    private int externalIdType = Entity.EXT_ID_NONE;

    /**
     * The entity value (EntityValue)
     */
    private String literalValue;

    /**
     * entity name
     */
    private String name = null;
    
    /**
     * The Name in an NDataDecl in an EntityDef in a GEDecl
     */
    private String nDataName = null;

    /**
     * entity type (parameter or general)
     */
    private int type;
    
    /**
     * 
     *
     */
    public Entity() {
        
    }

    /**
     * Entity constructor.
     * 
     * @param name The name of the entity.
     * @param value The value of the enetity.
     * @param type an int specifying the entity type.
     */
    public Entity(String name, String value, int type) {
        this.name = name;
        this.literalValue = value;
        this.type = type;
    }

    /**
     * Returns the external ID type for this entity.
     * 
     * @return the external ID type for this entity.
     */
    public int getExternalIdType() {
        return externalIdType;
    }

    /**
     * Returns the literal value of this entity.
     * 
     * @return The literal value
     */
    public String getLiteralValue() {
        return literalValue;
    }

    /**
     * Returns the Entity Name.
     * 
     * @return The Entity Name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Returns the nDataName.
     */
    public String getNDataName() {
        return nDataName;
    }

    /**
     * Returns the resolved value for this entity.
     * 
     * @param EntityList All the declared entities in this DTD.
     * @return The resolved value.
     * @throws EOFException
     */
    public String getResolvedValue(Hashtable EntityList)
            throws EOFException {
        return resolveEntities(new DTDTextParser(literalValue), EntityList)
                .toString();
    }

    /**
     * Returns the entity type (general, parameter).
     * 
     * @return The entity type.
     */
    public int getType() {
        return type;
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
        char[] entityDelim = {'&', '%'};

        newContent += content.getAllSkipping(entityDelim);

        while (!content.endOfData()) {
            boolean pEntity;
            pEntity = (content.peekAtPrevChar() == '%' ? true : false);
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

    /**
     * Sets the external ID type for this entity.
     * 
     * @param newExternalIDType The external ID type for this entity.
     */
    public void setExternalIdType(int newExternalIDType) {
        this.externalIdType = newExternalIDType;
    }

    /**
     * Sets the literal value of this entity.
     * 
     * @param newValue The literal value of this entity.
     */
    public void setLiteralValue(String newValue) {
        this.literalValue = newValue;
    }

    /**
     * Sets the Entity Name.
     * 
     * @param newName the Entity Name.
     */
    public void setName(String newName) {
        this.name = newName;
    }
    
    /**
     * @param dataName The nDataName to set.
     */
    public void setNDataName(String dataName) {
        this.nDataName = dataName;
    }

    /**
     * Sets the entity type.
     * 
     * @param newType the entity type.
     */
    public void setType(int newType) {
        this.type = newType;
    }

    /**
     * Returns a DTD String representation for this entity.
     * 
     * @return The DTD string representation.
     */
    public String toString() {

        String entityDecl;

        entityDecl = "<!ENTITY "; //'<!ENTITY' S

        if (getType() == PARAMETER_ENTITY)
            entityDecl += "% "; //'%' S

        entityDecl += getName() + " "; //Name S

        int extIDType = getExternalIdType();

        if (extIDType == EXT_ID_NONE) { //EntityValue
            entityDecl += "\"" + getLiteralValue() + "\"";
        } else if (extIDType == EXT_ID_SYSTEM) {
            entityDecl += "SYSTEM \"" + getSystemLiteral() + "\""; //'SYSTEM' S
                                                             // SystemLiteral
        } else if (extIDType == EXT_ID_PUBLIC) {
            entityDecl += "PUBLIC \"" + getPubidLiteral() + " "
                    + getSystemLiteral() + "\""; //'PUBLIC' S PubidLiteral S
                                             // SystemLiteral
        }

        entityDecl += ">"; //'>'

        return entityDecl;
    }
}

