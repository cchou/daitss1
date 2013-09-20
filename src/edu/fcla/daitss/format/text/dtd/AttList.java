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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/**
 * Class to store the attribute list for an element type.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class AttList {
    
    /**
     * Everything after the element name (attDef)
     */
    private String attDef = null;

    /**
     * The collection of attributes for the Element type.
     */
    private Vector attribs = null;

    /**
     * The name of the Element type to which this attribute list is associated.
     */
    private String elemName = null;

    /**
     * Is <code>true</code> if any of the attributes is of type "ID".
     */
    private boolean hasIdAttribute = false;

    /**
     * Is <code>true</code> if any of the attributes is of type "NOTATION".
     */
    private boolean hasNotationAttribute = false;
    
    /**
     * Constructor.
     *
     */
    public AttList() {
        attribs = new Vector();
    }

    /**
     * Add an attribute to the attribute list.
     * 
     *
     * 3.3 Attribute-List Declarations [XML Rec 1.0]
     * 
     * When more than one definition is provided for the same attribute of a
     * given element type, the first declaration is binding and later
     * declarations are ignored.
     * 
     *
     * 3.3.1 Attribute Types [XML Rec 1.0]
     * 
     * Validity constraint: One ID per Element Type No element type may
     * have more than one ID attribute specified.
     *
     *
     * 3.3.1 Attribute Types [XML Rec 1.0]
     * 
     * No element type may have more than one NOTATION attribute
     * specified.
     *
     * 
     * @param a The Attribute to be added.
     * @throws DTDSyntaxException
     */
    public void addAttribute(Attribute a) throws DTDSyntaxException {

        //check if its already defined
        if (!attribs.contains(a)) {

            if (a.isIdType()) {
                if (hasIdAttribute) {
                    // attribute list has more than one ID attribute
                    throw new DTDSyntaxException("Has multiple ID attributes");
                }
                hasIdAttribute = true;
            }

            if (a.getAttType() == Attribute.TYPE_ENUM_NOTATION) {
                if (hasNotationAttribute) {
                    // attribute list has more than one notation attribute
                    throw new DTDSyntaxException("Has multiple notaion attributes");
                }
                hasNotationAttribute = true;
            }
            attribs.addElement(a);
        }
    }
    
    /**
     * @return Returns the attDef.
     */
    public String getAttDef() {
        return attDef;
    }

    /**
     * Get the list of attributes in the atribute list.
     * 
     * @return The list of attributes.
     */
    public Vector getAttribs() {
        return attribs;
    }

    /**
     * Get the name of the element to which this attribute list is associated.
     * 
     * @return The element name.
     */
    public String getElemName() {
        return elemName;
    }

    /**
     * Checks if the attribute list has an attribute of type notation.
     * 
     * @return <code>true</code> if the attribute list has an attribute of
     *         type notation; <code>false</code> otherwise.
     */
    public boolean hasNotationAttribute() {
        return hasNotationAttribute;
    }

    /**
     * Merges all the attributes in the specified attribute list into this
     * attribute list.
     * 
     * @param attList The attribute list to merge from.
     * @throws DTDSyntaxException
     */
    public void merge(AttList attList) throws DTDSyntaxException {

        //check if the element type is the same
        if (!attList.getElemName().equals(getElemName())) {
            return;
        }

        Collection newAttrs = attList.getAttribs();
        Iterator iter = newAttrs.iterator();

        while (iter.hasNext()) {
            addAttribute((Attribute) iter.next());
        }
    }
    
    /**
     * @param attDef The attDef to set.
     */
    public void setAttDef(String attDef) {
        this.attDef = attDef;
    }

    /**
     * Set the element name associated with this attribute list.
     * 
     * @param newElemName The name of the element.
     */
    public void setElemName(String newElemName) {
        this.elemName = newElemName;
    }

    /**
     * Convert this Attribute list to a DTD String.
     * 
     * @return the ATTLIST XML element
     */
    public String toString() {

        String attlist = "<!ATTLIST ";
        String defaultValue;

        attlist += getElemName() + " \n";

        Enumeration attributes = attribs.elements();

        while (attributes.hasMoreElements()) {
            Attribute currAttrib = (Attribute) attributes.nextElement();

            attlist += "\t" + currAttrib.getAttributeName() + " ";

            switch (currAttrib.getAttType()) {
            case Attribute.TYPE_STRING:
            case Attribute.TYPE_TOKENIZED:
                attlist += currAttrib.getDataType() + " ";

                String optionality = currAttrib.getOptionalityString();
                if (optionality != null)
                    attlist += optionality + " ";

                defaultValue = currAttrib.getDefaultValue();
                if (defaultValue != null ) { 
                    char quote;
                    if (defaultValue.indexOf('\"') == -1)
                        quote = '\"';
                    else
                        quote = '\'';
                    attlist += quote + defaultValue + quote;
                }
                break;

            case Attribute.TYPE_ENUM_ENUMERATION:
                attlist += "(";
                String[] enums = currAttrib.getEnumeratedValues();
                for (int i = 0; i < enums.length - 1; i++)
                    attlist += enums[i] + " | ";

                if (enums.length > 0) {
                    attlist += enums[enums.length - 1] + ") ";
                } else {
                    attlist += ") ";
                }

                optionality = currAttrib.getOptionalityString();
                if (optionality != null)
                    attlist += optionality + " ";

                defaultValue = currAttrib.getDefaultValue();
                if (defaultValue != null && defaultValue.length() > 0)
                    attlist += "\"" + defaultValue + "\"";
                break;

            case Attribute.TYPE_ENUM_NOTATION:
                break;
            }

            attlist += "\n";
        }

        attlist += ">";

        return attlist;
    }
}