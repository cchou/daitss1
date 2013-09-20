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
/**
 * 
 */
package edu.fcla.daitss.prep;

import java.io.File;
import java.io.FileOutputStream;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.Informer;

/**
 * @author franco
 *
 */
public class MessageDigestTypeFilter extends Filter {

	private static final String PREFERED_CHECKSUMTYPE = "MD5";

	/* (non-Javadoc)
	 * @see edu.fcla.daitss.prep.filter.Filter#process(java.io.File)
	 */
	@Override
	public void process(File pkg) throws FatalException, PackageException {
		
		// get the descriptor
		File descriptor = getDescriptor(pkg);
		
		// parse the descriptor
		Document document = parseXml(descriptor);
		String ns = document.getNamespaceURI();
		
		// select the file elements with a CHECKSUM attribute
		NodeList list = XPaths.selectNodeList(document, "//mets:file[@CHECKSUM]");
		for (int i = 0; i < list.getLength(); i++) {
			Element fileElement = (Element) list.item(i);
			String attributeNS = fileElement.getAttributeNS(ns, "CHECKSUMTYPE");
			if (attributeNS == null || attributeNS.trim().length() == 0) {

                            StackTraceElement element = new Throwable().getStackTrace()[0];
                            Informer.getInstance().info(element.getClassName(), element.getMethodName(),
                                                        "setting CHECKSUMTYPE=\"MD5\" for file: " + fileElement.getAttribute("ID"),
                                                        "Ensuring @CHECKSUMTYPE=\"MD5\"",
                                                        true);
                            
                            fileElement.setAttributeNS(ns, "CHECKSUMTYPE", PREFERED_CHECKSUMTYPE);
			}
		}
		
		// serialize the xml back to the file        
		try {
			FileOutputStream fos = new FileOutputStream(descriptor);
	        METSDescriptor.serializeXml(fos, document);
	        fos.close();
		} catch (Exception e) {
			StackTraceElement element = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(this, element.getMethodName(), "Cannot serialize descriptor: " + descriptor.getPath(), "Ensuring @CHECKSUMTYPE='MD5'", e);	
		}
	}

}
