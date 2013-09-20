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
 * Class to hold the external ID(s) identified for the 
 * entity or notation declaration.
 *
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class ExternalID {

    /**
	 * Public ID identifier
	 * 
	 * [12]    PubidLiteral ::= '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
	 */
	private String pubId = null;

    /**
     * System literal
     * 
     * [11]    SystemLiteral ::= ('"' [^"]* '"') | ("'" [^']* "'")
     */
	private String sysId = null;
    
    /**
	 * Returns the PubidLiteral.
	 * 
	 * @return The PubidLiteral
	 */
	public String getPubidLiteral() {
		return this.pubId;
	}

	/**
	 * Returns the SystemLiteral.
	 * 
	 * @return The SystemLiteral
	 */
	public String getSystemLiteral() {
		return this.sysId;
	}
    
    /**
	 * Sets the PubidLiteral.
	 * 
	 * @param publicId The PubidLiteral
	 */
	public void setPubidLiteral(String publicId) {
		this.pubId = publicId;
	}

	/**
	 * Sets the SystemLiteral.
	 * 
	 * @param systemId The SystemLiteral
	 */
	public void setSystemLiteral(String systemId) {
		this.sysId = systemId;
	}
}
