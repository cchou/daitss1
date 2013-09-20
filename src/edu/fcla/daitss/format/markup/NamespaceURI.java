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
 * Created on May 10, 2004
 *
 */
package edu.fcla.daitss.format.markup;

import java.util.StringTokenizer;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;

/**
 * NamespaceURI stores common namespace URIs used in XML files.
 * It is used to recognize attributes accompanying elements in 
 * particular namespaces whose values are URIs pointing to 
 * external resources.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class NamespaceURI {
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME =
		"edu.fcla.daitss.format.markup.util.NamespaceURI";
	
	/**
	 * Determines if a URI is listed as a valid value for a namespace.
	 * For this to be true, the URI must be found in a '|'-delimited list
	 * of values for a particular property in the configXml configuration
	 * file.
	 * 
	 * @param property	a property in the configXml configuration file
	 * @param URI a URI
	 * @return whether or not the URI is a value of the property
	 * @throws FatalException
	 */
	private static boolean isNs(String property, String URI) 
		throws FatalException {
		
		if (property == null || URI == null ||
			property.equals("") || property.equals("")) {
			Informer.getInstance().fail(
				CLASSNAME,
				"isNS(String,String)",
				"Invalid argument",
				"property: " + property + " URI: " + URI,
				new FatalException("Not valid arguments."));
		}
		
		boolean reply = false;
		String propValue = 
			ArchiveProperties.getInstance().getArchProperty(property);
		StringTokenizer st = new StringTokenizer(propValue, "|");
		boolean foundIt = false;
		while (!foundIt && st.hasMoreTokens()){
			String validNS = st.nextToken();
			if (URI.equals(validNS)){
				foundIt = true;
				reply = true;
			}
		}
		return reply;
	}
	
	/**
	 * Determines if the URI is a known URI that can be bound to the METS 
	 * namespace.
	 * 
	 * @param URI a URI
	 * @return whether or not the URI can be bound to the METS namespace
	 * @throws FatalException
	 */	
	public static boolean isMetsNamespace(String URI) 
		throws FatalException {
		return isNs("NS_METS", URI);
	}	
	
	/**
	 * Determines if the URI is a known URI that can be bound to the XInclude 
	 * namespace.
	 * 
	 * @param URI a URI
	 * @return whether or not the URI can be bound to the XInclude namespace
	 * @throws FatalException
	 */	
	public static boolean isXIncludeNamespace(String URI) 
		throws FatalException {
		return isNs("NS_XINCLUDE", URI);
	}
	
	/**
	 * Determines if the URI is a known URI that can be bound to the XMLLink 
	 * namespace.
	 * 
	 * @param URI a URI
	 * @return whether or not the URI can be bound to the XMLLink namespace
	 * @throws FatalException
	 */	
	public static boolean isXmlLinkSchemaNs(String URI) 
		throws FatalException {
		return isNs("NS_XLINK", URI);
	}
	
	/**
	 * Determines if the URI is a known URI that can be bound to the XMLSchema 
	 * Instance namespace.
	 * 
	 * @param URI a URI
	 * @return whether or not the URI can be bound to the XMLSchema Instance namespace
	 * @throws FatalException
	 */
	public static boolean isXmlSchemaInstanceNs(String URI) 
		throws FatalException {
		return isNs("NS_XMLSCHEMA_INSTANCE", URI);
	}
	
	/**
	 * Determines if the URI is a known URI that can be bound to the XMLSchema 
	 * namespace.
	 * 
	 * @param URI a URI
	 * @return whether or not the URI can be bound to the XMLSchema namespace
	 * @throws FatalException
	 */
	public static boolean isXmlSchemaNs(String URI) 
		throws FatalException {
		return isNs("NS_XMLSCHEMA", URI);
	}
	
	/**
	 * Determines if the URI is a known URI that can be bound to the XSL 
	 * Instance namespace.
	 * 
	 * @param URI a URI
	 * @return whether or not the URI can be bound to the XSL namespace
	 * @throws FatalException
	 */
	public static boolean isXslNs(String URI) throws FatalException {
		return isNs("NS_XSL", URI);
	}

	/**
	 * Test driver
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		System.out.println("Is XMLSchema URI: http://www.w3.org/2001/XMLSchema-instance - " +
			isXmlSchemaInstanceNs("http://www.w3.org/2001/XMLSchema-instance"));
	}
}
