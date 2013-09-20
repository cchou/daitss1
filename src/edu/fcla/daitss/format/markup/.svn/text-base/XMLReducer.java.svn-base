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
package edu.fcla.daitss.format.markup;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.fcla.daitss.exception.FatalException;

/**
 * @author franco
 * 
 * Reduces a mets document to be specific for file metadata. Example: to
 * reduce an <code>Element e</code> based on a set of IDs: <code>
 * 	Reducer r = new Reducer(new String[] { "ID1", "ID2", "ID3" });
 * 	r.reduce(e);
 * 	r.purge();
 * </code>
 */
public class XMLReducer {

	// idList contains objects of type String
	private List idList = new Vector();
	
	// purgeList contains objects of type Element
	private List purgeList = new Vector();

	private String[] ids;
	
	/**
	 * Default constructor.
	 * 
	 * @param metaDataIds
	 *            IDs to specify the metadata to base the reduction.
	 */
	public XMLReducer(String[] metaDataIds) {
		ids = metaDataIds;
	
		for (int i=0; i<metaDataIds.length; i++) {
			String id = metaDataIds[i];
			idList.add(id);
		}
		
		/*
		for(String id:metaDataIds) {
			idList.add(id);
		}*/
	}

	/**
	 * Removes all the elements that dont relate to the metadata.
	 */
	public void purge() {
		Iterator i = purgeList.iterator();
		
		while (i.hasNext()) {
			Element e = (Element)i.next();
			e.getParentNode().removeChild(e);
		}
		
		/*
		for(Element element:purgeList) {
			element.getParentNode().removeChild(element);
		}
		*/
	}

	/**
	 * Determines which Elements to remove with respect to the metadata
	 * 
	 * @param element
	 * @throws FatalException
	 */
	public void oldReduce(Element element) throws FatalException {

		// Determine if this is me
		boolean isMe = false;
		{
			String id = element.getAttribute("ID");
			String fileID = element.getAttribute("FILEID");
			Vector idVec = new Vector(ids.length);
			idVec.addAll(Arrays.asList(ids));
			isMe = idVec.contains(id) || idVec.contains(fileID);
		}

		// Determine if this element has the IDs under it
		int total = 0;
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			String idPath = ".//*[@ID='" + id + "']";
			String fidPath = ".//*[@FILEID='" + id + "']";
			String xPath = idPath + "|" + fidPath;
			NodeList nodes = XPaths.selectNodeList(element, xPath);
			total += nodes.getLength();
			
			// slight optimizaion
			if (total > 0) {
				break;
			}
		}
		boolean underMe = (total > 0);

		// Check every child or remove this element
		if (isMe) {
			// Dont recurse any deeper on this branch
		} else if (underMe) {
			// Reduce all children below
			NodeList childNodes = element.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i) instanceof Element) {
					Element child = (Element) childNodes.item(i);
					oldReduce(child);
				}
			}
		} else {
			// Set this element to be purged
			purgeList.add(element);
		}
	}

	public boolean reduce(Element element) {
		
		String id;
		if (element.getLocalName().equals("fptr")) {
			id = element.getAttribute("FILEID");	
		} else {
			id = element.getAttribute("ID");	
		}
		
		boolean isMe = idList.contains(id);
		
		if (isMe) {
			return true;
		}
		
		boolean underMe = false;
		
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				Element child = (Element) childNodes.item(i);
				boolean inChild = reduce(child);
				if (inChild) {
					underMe = underMe || inChild;
				} else {
					purgeList.add(child);
				}
			}
		}
		
		return underMe;
	}
}
