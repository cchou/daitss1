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

import java.util.Hashtable;


/**
 * Class to hold an Element Type declaration.
 */
public class Element {
    
    /**
     * Element children names -> optionality character
     */
    private Hashtable children = null;

    /**
     * contentspec potion of the element declaration
     */
    private String contentSpec;

    /**
     * Whether or not the element declaration has an empty contentspec
     */
    private boolean hasEmptyContentSpec = false;

    /**
     * Element name
     */
    private String name;

    /**
     * @return Returns the children.
     */
    public Hashtable getChildren() {
        return children;
    }

    /**
     * Returns the content spec for this element type.
     * 
     * @return The content spec.
     */
    public String getContentSpec() {
        return contentSpec;
    }

    /**
     * Returns the name of this element type.
     * 
     * @return The element type name.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the content model of this element type is empty.
     * 
     * @return <code>true</code> if the content model is empty;
     *         <code>false</code> otherwise.
     */
    public boolean isEmptyContentModel() {
        return hasEmptyContentSpec;
    }
    /**
     * @param children The children to set.
     */
    public void setChildren(Hashtable children) {
        this.children = children;
    }

    /**
     * Sets the content spec for this element type.
     * 
     * @param newContentSpec The content spec.
     */
    public void setContentSpec(String newContentSpec) {
        contentSpec = newContentSpec;
    }
    
    /**
     * @param _hasEmptyContentSpec The hasEmptyContentSpec to set.
     */
    public void setHasEmptyContentSpec(boolean _hasEmptyContentSpec) {
        this.hasEmptyContentSpec = _hasEmptyContentSpec;
    }

    /**
     * Sets the name for this element type.
     * 
     * @param newName The element name.
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Returns a DTD String representation for this element type.
     * 
     * @return DTD String for this element type.
     */
    public String toString() {

        StringBuffer str = new StringBuffer("<!ELEMENT " + getName() + " ");

        str.append(getContentSpec());
        str.append(">");

        return str.toString();
    }
}

