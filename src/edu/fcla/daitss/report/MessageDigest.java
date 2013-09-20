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
package edu.fcla.daitss.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author franco
 *
 */
public class MessageDigest implements Xmlable {
	String algorithm;
	String value;
	/**
	 * @param value
	 * @param algorithm
	 */
	public MessageDigest(String value, String algorithm) {
		this.value = value;
		this.algorithm = algorithm;
	}
	
	/**
	 * @return Returns the algorithm.
	 */
	public String getAlgorithm() {
		return algorithm;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see edu.fcla.daitss.report.Xmlable#toXML(org.w3c.dom.Document)
	 */
	public Node toXML(Document doc) {
		Element messageDigest = doc.createElement("MESSAGE_DIGEST");
		messageDigest.setAttribute("ALGORITHM", this.algorithm.toUpperCase());
		messageDigest.appendChild(doc.createTextNode(this.value));
		
		return messageDigest;
	}
}