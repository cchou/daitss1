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

/**
 * Class to hold an Attribute declaration.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class Attribute {

    /**
     * The type of the attribute. This can take any of the four values -
     * <code>TYPE_STRING</code>,<code>TYPE_TOKENIZED</code>,
     * <code>TYPE_NOTATION</code> or <code>TYPE_ENUMERATION</code>.
     */
    private int attType;

    /**
     * The data type for this attribute.
     */
    private String dataType;

    /**
     * The default value for this attribute.
     */
    private String defaultValue;

    /**
     * The enumerated list of values for the attribute. This will be populated
     * only if the type of the attribute is of type
     * <code>TYPE_ENUMERATION</code> or of type <code>TYPE_NOTATION</code>.
     */
    private String[] enumeration;

    /**
     * The name of the attribute.
     */
    private String name;

    /**
     * The "optionality" of this attribute.
     */
    private int optionality;

    /**
     * Identifies an Attribute defined as a String 
     * type. 
     * This is identified by having a value of
     * "CDATA" for the AttType. 
     */
    public static final short TYPE_STRING = 1;

    /**
     * Identifies an Attribute defined as a Tokenized 
     * type. 
     * This is identified by AttType having one of 
     * the following values -
     * "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", 
     * "NMTOKEN" or "NMTOKENS". 
     */
    public static final short TYPE_TOKENIZED = 2;

    /**
     * Identifies an Attribute defined as an Enumeration.
     */
    public static final short TYPE_ENUM_ENUMERATION = 4;

    /**
     * Identifies an Attribute defined as a Notation.
     */
    public static final short TYPE_ENUM_NOTATION = 5;

    /**
     * Identifies a Required attribute. This is recognized in
     * the attribute definition by the use of the "#REQUIRED"
     * clause in the DefaultDecl section.
     */
    public static final short OPTIONALITY_REQUIRED = 1;

    /**
     * Identifies a Implied attribute. This is recognized in
     * the attribute definition by the use of the "#IMPLIED"
     * clause in the DefaultDecl section.
     */
    public static final short OPTIONALITY_IMPLIED  = 2;

    /**
     * Identifies a Fixed attribute. This is recognized in
     * the attribute definition by the use of the "#FIXED"
     * clause in the DefaultDecl section.
     */
    public static final short OPTIONALITY_FIXED    = 3;

    /**
     * Identifies a Required attribute. This is recognized in
     * the attribute definition by the use of none of the clauses
     * in the DefaultDecl section, other than the default value.
     */
    public static final short OPTIONALITY_DEFAULT  = 4;

    /**
     * Get the name of the attribute.
     * 
     * @return The attribute name.
     */
    public String getAttributeName() {
        return name;
    }

    /**
     * Return the type of the Attribute.
     * 
     * @return The attribute type.
     */
    public int getAttType() {
        return attType;
    }

    /**
     * Get the data type of the Attribute.
     * 
     * @return The attribute datatype.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Get the Default declaration of the Attribute.
     * 
     * @return The attribute default declaration.
     */
    public String getDefaultValue() {

        if (getOptionality() == Attribute.OPTIONALITY_DEFAULT) {
            return defaultValue;
        }

        return null;
    }

    /**
     * If the Attribute is of Enumerated type, get the list of permitted values.
     * 
     * @return The list of permintted values.
     */
    public String[] getEnumeratedValues() {
        return enumeration;
    }

    /**
     * Get the optionality of the attribute.
     * 
     * @return The attribute optionality.
     */
    public int getOptionality() {
        return optionality;
    }

    /**
     * Get the optionality of the attribute.
     * 
     * @return The attribute optionality.
     */
    public String getOptionalityString() {

        String opt = "";

        if (optionality == Attribute.OPTIONALITY_REQUIRED) {
            opt = "#REQUIRED";
        } else if (optionality == Attribute.OPTIONALITY_IMPLIED) {
            opt = "#IMPLIED";
        } else if (optionality == Attribute.OPTIONALITY_FIXED) {
            opt = "#FIXED";
        }

        return opt;
    }

    /**
     * Checks if the attribute is of type enumeration.
     * 
     * @return <code>true</code> the attribute is of type enumeration;
     *         <code>false</code> otherwise.
     */
    public boolean isEnumeration() {
        return (getAttType() == Attribute.TYPE_ENUM_ENUMERATION);
    }

    /**
     * Returns true if this attribute is of type ID.
     * 
     * @return <code>true</code> if the attribute is of type ID;
     *         <code>false</code> otherwise.
     */
    public boolean isIdType() {
        return (getAttType() == Attribute.TYPE_TOKENIZED
                && getDataType().equals("ID"));
    }

    /**
     * Check if the Attribute is of NOTATION type.
     * 
     * @return <code>true</code> if the attribute is a Notation type.
     *         <code>false</code> if the attribute is not.
     */
    public boolean isNotationType() {
        return (getAttType() == Attribute.TYPE_ENUM_NOTATION);
    }

    /**
     * Set the name of the attribute.
     * 
     * @param newName attribute name
     */
    public void setAttributeName(String newName) {
        if (newName != null) {
            name = newName;
        }
    }

    /**
     * Sets the attribute type.
     * 
     * @param newType The attribute type.
     */
    public void setAttrType(int newType) {
        attType = newType;
    }

    /**
     * Set the data type of the attribute.
     * 
     * @param newDataType attribute data type
     */
    public void setDataType(String newDataType) {
        dataType = newDataType;
    }

    /**
     * Set the default declaration of the attribute.
     * 
     * @param newDefaultDecl default value
     */
    public void setDefaultValue(String newDefaultDecl) {
        defaultValue = newDefaultDecl;
    }

    /**
     * Set the permitted values for the attribute (only used for attributes of
     * the Enumerated type).
     * 
     * @param newEnumeration Enumerated values for the attribute.
     */
    public void setEnumeration(String[] newEnumeration) {
        enumeration = newEnumeration;
    }
    
    /**
     * Set the optionality of the attribute.
     * 
     * @param _optionality
     */
    public void setOptionality(int _optionality) {
        this.optionality = _optionality;
    }

    /**
     * Set the optionality of the attribute.
     * 
     * @param optionalityStr optionality of the attribute
     */
    public void setOptionality(String optionalityStr) {

        if (optionalityStr == null) {
            return;
        }
        
        if (optionalityStr.equals("#REQUIRED")) {
            optionality = Attribute.OPTIONALITY_REQUIRED;
        } else if (optionalityStr.equals("#IMPLIED")) {
            optionality = Attribute.OPTIONALITY_IMPLIED;
        } else if (optionalityStr.equals("#FIXED")) {
            optionality = Attribute.OPTIONALITY_FIXED;
        }
    }
}