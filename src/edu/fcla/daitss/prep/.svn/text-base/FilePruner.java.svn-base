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
package edu.fcla.daitss.prep;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.WebCacheUtils;

/**
 * Removes files in the package not described in the descriptor.
 * 
 * @author franco
 *
 */
public class FilePruner extends Filter {

	@Override
	public void process(File pkg) throws FatalException, PackageException {
		// construct a collection of files described
		Set<File> described = getDescribed(pkg);
		
		// construct a collection of files in package sans descriptor
		Set<File> packaged = getPackaged(pkg);
		
		// determine all the undescribed files
		Set<File> undescribed = new HashSet<File>(packaged);
		undescribed.removeAll(described);
		
		// remove all the undescribed files from the package
		for(File file : undescribed) {
		    StackTraceElement ste = new Throwable().getStackTrace()[0];
		    Informer.getInstance().info(
						ste.getClassName(), 
						ste.getMethodName(), 
						String.format("Pruning undescribed file %s", file.getPath()),
						"Pruning undescribed files", 
						false);		    
		    file.delete();
		}
	}

	private static Set<File> getPackaged(File pkg) {
		// return all files but the descriptor
		File descriptor = getDescriptor(pkg);
		Set<File> packaged = findFiles(pkg);
		packaged.remove(descriptor);
		return packaged;
	}

	private static Set<File> findFiles(File f) {
		// recursively get all files in the path f
		Set<File> files = new HashSet<File>();
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				files.addAll(findFiles(child));
			}
		} else {
			files.add(f);	
		}
		return files;
	}

	private static Set<File> getDescribed(File pkg) throws FatalException {
		Document doc = null;
		File descriptor = getDescriptor(pkg);
		try {
			// DOM overhead
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
        	WebCacheResolver resolver = WebCacheUtils.getResolver();
			builder.setEntityResolver(resolver);

			// parse the descriptor	
			doc = builder.parse(descriptor);
		} catch (ParserConfigurationException e) {
			StackTraceElement ste = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(
					ste.getClassName(), 
					ste.getMethodName(), 
					"cannot create document builder", 
					"Pruning undescribed files", 
					e);
		} catch (SAXException e) {
			StackTraceElement ste = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(
					ste.getClassName(), 
					ste.getMethodName(), 
					"Cannot parse descriptor: " + descriptor.getAbsolutePath(), 
					"Pruning undescribed files", 
					e);
		} catch (IOException e) {
			StackTraceElement ste = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(
					ste.getClassName(), 
					ste.getMethodName(), 
					"Cannot parse descriptor: " + descriptor.getAbsolutePath(), 
					"Pruning undescribed files", 
					e);
		}
		
		// select all xlink:href from files
		String xpath = "/mets:mets/mets:fileSec//mets:file/mets:FLocat/@xlink:href";
		NodeList nodes = XPaths.selectNodeList(doc, xpath);
		
		// build a file set
		Set<File> files = new HashSet<File>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Attr attr = (Attr) nodes.item(i);
			String path = attr.getValue();			
			File file;
			if(new File(path).isAbsolute()) {
				file = new File(path);
			} else {
				file = new File(pkg, attr.getNodeValue());
			}
			files.add(file);
		}
		return files;
	}
}
