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

import java.util.Enumeration;
import java.util.Hashtable;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.text.Text;

/**
 * Represents the internal or external DTD subset.
 * 
 */
public class DTDSubset extends Text {
	
	/**
	 * Declared attributes.
	 */
	private Hashtable attributeList; 

    /**
     * Declared elements.
     */
	private Hashtable elementList;
	
	/**
	 * Declared entities.
	 */
	private Hashtable entityList;
	
	/**
	 * Declared notations.
	 */
	private Hashtable notationList;
	
	/**
	 * @param df
	 * @throws FatalException
	 */
	public DTDSubset(DataFile df) throws FatalException {
		super(df);
		reset();
		
		this.setBsTable(ArchiveDatabase.TABLE_BS_TEXT);
		this.getCompression().setCompression(Compression.COMP_NOT_APPLICABLE);
		this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		this.setLocation("0");
		this.setRole(BitstreamRole.DOC_MAIN);
	}
	
	/**
	 * Adds an attribute list.
	 * 
	 *
	 * 3.3 Attribute-List Declarations [XML Rec 1.0]
	 *
	 * When more than one AttlistDecl is provided for a
	 * given element type, the contents of all those
	 * provided are merged.
	 *
	 * @param attList The attribute list to add.
	 * @throws DTDSyntaxException
	 * @throws FatalException
	 */
	public void addAttList(AttList attList) 
		throws DTDSyntaxException, FatalException {
		String elemName = attList.getElemName();

		if (elemName == null) {
			// no element name in the attribute list
			if (DTDParser.shouldAddAnoms()){
				this.getDf().addAnomaly(
					    this.getDf().getAnomsPossible().getSevereElement(
							DTDAnomalies.DTD_ATT_FOR_NO_ELEM));
			}
			return;
		}

		if (attributeList.get(elemName) == null) {
			attributeList.put(elemName, attList);
		} else {
			// attribute list for this element already exists
			if (DTDParser.UO_MULTIPLE_ATTR_LIST) {
				if (DTDParser.shouldAddAnoms()){
					this.getDf().addAnomaly(
						    this.getDf().getAnomsPossible().getSevereElement(
								DTDAnomalies.DTD_MULT_ATTLIST));
				}
			}
			AttList oldAttList = (AttList) attributeList.get(elemName);

			
			oldAttList.merge(attList);
			attributeList.put(elemName, oldAttList);
		}

		Element elementType = getElementType(elemName);
		if (elementType == null) {
			if (DTDParser.UO_MISSING_ELEM_DECL) {
				if (DTDParser.shouldAddAnoms()){
					this.getDf().addAnomaly(
						    this.getDf().getAnomsPossible().getSevereElement(
								DTDAnomalies.DTD_ATT_FOR_MISSING_ELEM));
				}
			}
		} else {
			/*
			 * 3.3.1 Attribute Types [XML Rec 1.0]
			 *
			 * Validity constraint: No Notation on Empty Element
			 * 		For compatibility, an attribute of type NOTATION
			 * 		must not be declared on an element declared EMPTY.
			 */
			if (elementType.isEmptyContentModel()
				&& attList.hasNotationAttribute()) {
				if (DTDParser.shouldAddAnoms()){
					this.getDf().addAnomaly(
						    this.getDf().getAnomsPossible().getSevereElement(
								DTDAnomalies.DTD_NOT_ATT_EMPTY_ELEM));
				}
			}
		}
	}
	
	/**
	 * Adds an element declaration.
	 *
	 * @param elem The element declaration.
	 * @throws FatalException
	 */
	public void addElement(Element elem) throws FatalException {
		
		if (elementList.get(elem.getName()) == null) {
			elementList.put(elem.getName(), elem);
		} else {
			if (DTDParser.shouldAddAnoms()){
				this.getDf().addAnomaly(
					    this.getDf().getAnomsPossible().getSevereElement(
							DTDAnomalies.DTD_MULT_ELEM_DECL));
			}
		}
	}
	
	/**
	 * Adds an entity.
	 *
	 * @param ent The entity to add.
	 */
	public void addEntity(Entity ent) {
	    // During replace links the entity is 'readded' changing the
	    // system literal so an exception is made here during that
	    // parse activity
		if (DTDParser.getParseActivity() == DTDParser.ACTION_REP_LKS
		        || entityList.get(ent.getName()) == null)
			//the first definition always overrides the remaining.
			entityList.put(ent.getName(), ent);
	}
	
	/**
	 * Add a notation.
	 * 
	 * @param not The notation to add
	 * @throws FatalException
	 */
	public void addNotation(Notation not) throws FatalException {
	    // During replace links the notation is 'readded' changing the
	    // system literal so an exception is made here during that
	    // parse activity
		if (DTDParser.getParseActivity() == DTDParser.ACTION_REP_LKS ||
		        notationList.get(not.getName()) == null) {
			notationList.put(not.getName(), not);
		} else {
		    // notation has already been declared with the same name
		    // validity constraint in xml 1.0 spec.
		    if (DTDParser.shouldAddAnoms()){
				this.getDf().addAnomaly(
					    this.getDf().getAnomsPossible().getSevereElement(
							DTDAnomalies.DTD_MULT_NOT_DECL));
			}
		}
	}
	
	/**
	 * Adds a parameter entity.
	 *
	 * @param ent The parameter entity to add.
	 */
	public void addParamEntity(Entity ent) {

		if (entityList.get(ent.getName()) == null)
			//the first definition always overrides the remaining.
			entityList.put(ent.getName(), ent);
	}
	
	/**
	 * Returns a Hashtable of all attribute lists.
	 *
	 * @return A Hashtable of all attribute lists.
	 */
	public Hashtable getAllAttributes() {
		return attributeList;
	}
	
	/**
	 * Returns all the attributes for the specified
	 * Element Type.
	 *
	 * @param elementName  The name of the Element Type.
	 *
	 * @return java.util.Enumeration
	 */
	public AttList getAttributeList(String elementName) {
		return (AttList) attributeList.get(elementName);
	}
	
	/**
	 * Returns a Hashtable of all Element types.
	 *
	 * @return A Hashtable of all Element types.
	 */
	public Hashtable getElementList() {
		return this.elementList;
	}
	
	/**
	 * Returns the element names for
	 * all the element types.
	 *
	 * @return An enumeration of element names.
	 */
	public Enumeration getElementNames() {
		return elementList.keys();
	}

	/**
	 * Returns the Element Type declaration for
	 * the specified element name.
	 *
	 * @return The Element Type declaration.
	 *
	 * @param elementName The name of the Element Type.
	 */
	public Element getElementType(String elementName) {
		return (Element) elementList.get(elementName);
	}
	
	/**
	 * Returns an entity with the specified name.
	 *
	 * @param entityName The name of the entity.
	 * @return An entity with the specified name.
	 */
	public Entity getEntity(String entityName) {
		return (Entity) entityList.get(entityName);
	}
	
	/**
	 * Returns all the entity definitions.
	 *
	 * @return All the entities.
	 */
	public Hashtable getEntityList() {
		return entityList;
	}
	
	/**
	 * Returns a notation with the specified name.
	 * 
	 * @param notationName name of the notation
	 * @return the notation with the specified name
	 */
	public Notation getNotation(String notationName) {
	    return (Notation) notationList.get(notationName);
	}
	
	/**
	 * Returns all the declared notations.
	 * 
	 * @return notations
	 */
	public Hashtable getNotationList() {
	    return this.notationList;
	}

	/**
	 * Returns the parameter entity with
	 * the specified name.
	 *
	 * @param pEntityName The name of the
	 * 			parameter entity.
	 *
	 * @return The parameter entity.
	 */
	public Entity getParamEntity(String pEntityName) {
		return (Entity) entityList.get(pEntityName);
	}

	/**
	 * Resets this DocType object.
	 */
	private void reset() {
		elementList = new Hashtable();
		attributeList = new Hashtable();
		entityList = new Hashtable();
		notationList = new Hashtable();
	}

	/**
	 * Returns the values of all its members.
	 * 
	 * @return the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		/*
		sb.append("Elements:\n");
		Enumeration elems = this.getElementList().elements();
		Element eType = null;
		AttList aList = null;
		
		int count = 1;
		while (elems.hasMoreElements()) {
			eType = (Element) elems.nextElement();
			sb.append("\telement " + count + ": " + eType.toString() + "\n");
			//get the attribute list for this element
			aList = (AttList) attributeList.get(eType.getName());
			if (aList != null) {
				sb.append("\t" + aList.toString() + "\n\n");
			} else {
				sb.append("\n");
			}
			count++;
		}
		
		count = 1;
		sb.append("Entities:\n");
		Enumeration ents = this.getEntityList().elements();
		Entity ent = null;
		while (ents.hasMoreElements()){
			ent = (Entity) ents.nextElement();
			sb.append("\tentity " + count + ": " + ent.toString() + "\n");
			count++;
		}
		
		count = 1;
		sb.append("Notations:\n");
		Enumeration nots = this.getNotationList().elements();
		Notation not = null;
		while (nots.hasMoreElements()){
			not = (Notation) nots.nextElement();
			sb.append("\tnotation " + count + ": " + not.toString() + "\n");
			count++;
		}
		*/
		

		return sb.toString();
	}

}