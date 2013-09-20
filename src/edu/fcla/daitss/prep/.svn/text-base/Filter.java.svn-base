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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.WebCacheUtils;

/**
 * Provides a superclass for pre-processor filters used by the preprocessor.
 * @author franco
 *
 */
public abstract class Filter {
	
    /**
     * Assert that a package has a certain property defined by the subclass.
     * @param pkg 
     * @throws FatalException 
     * @throws PackageException if the package does not have the property defined by the subclass. 
     */	
    public abstract void process(File pkg) throws FatalException, PackageException;

    
	/**
	 * Locate a file in a directory hierarchy
	 * @param dir the directory to search
	 * @param name the descriptor's filename
	 * @return the descriptor
	 */
	public static File findDescriptor(File dir, String name) {
		
		File descriptor = new File(dir, name);
		
		if (descriptor.isFile()) {
			
			// normal place for descriptors
			descriptor = new File(dir, name);
			
		} else {
			
			// recursively search of the descriptor in all subdirs
			for (File file : dir.listFiles(new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory();
				}				
			})) {
				File p = findDescriptor(file, name);
				if (p.isFile()) {
					descriptor = p;
					break;
				}
			}			
		}
		
		return descriptor;
	}
    
    protected static File getDescriptor(File pkg) {    	
    	String name = pkg.getName() + ".xml";
    	return new File(pkg, name);
    }
    
    protected Document parseXML(File xmlFile, boolean dumb) throws FatalException, PackageException, SAXParseException {
    
        String methodName = new Throwable().getStackTrace()[0].getMethodName();
    
        Document document = null;
        DocumentBuilder df = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            if(!dumb) {
            	dbf.setNamespaceAware(true);
            } else {
            	dbf.setValidating(false);
            	dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);            	
            }
            
            df = dbf.newDocumentBuilder();
        	WebCacheResolver resolver = WebCacheUtils.getResolver();
            df.setEntityResolver(resolver);
            dbf.setExpandEntityReferences(false);
        } catch (ParserConfigurationException e) {
            Informer.getInstance().fail(this, methodName,
                    "Cannot configure parser", "Parsing package descriptor", e);
        }
        
        try {
			document = df.parse(xmlFile);
		} catch (Exception e) {
			if (e instanceof SAXParseException) {
				throw (SAXParseException) e;
			}
            Informer.getInstance().error(this, methodName,
                    "Cannot parse file " + xmlFile.getAbsolutePath(),
                    "Parsing package descriptor",
                    e);
		}
        return document;
    }

    /**
     * @param pkg
     * @param doc
     * @throws FatalException 
     */
    protected void saveDescriptorXml(File pkg, Document doc) throws FatalException {
    	
        // find file to write data to
        File descriptor = getDescriptor(pkg);


    
        // save the dom to file
        try {
            OutputStream fos = new FileOutputStream(descriptor);
            METSDescriptor.serializeXml(fos, doc);
        } catch (FileNotFoundException e) {
            Informer.getInstance().fail(
            		this,
                    new Throwable().getStackTrace()[0].getMethodName(),
                    "cannot locate file " + descriptor, 
                    "Preprocessing", 
                    e);
		} catch (IOException e) {
			Informer.getInstance().fail(
					this,
                    new Throwable().getStackTrace()[0].getMethodName(),
                    "cannot stream to file " + descriptor, 
                    "Preprocessing", 
                    e);
		}
    }

    /**
     * Asserts that a package has a descriptor of the name
     * @param pkg
     * @throws PackageException 
     * @throws FatalException 
     */
    public void assertDescriptorExists(File pkg) throws PackageException, FatalException {
        /* make sure we have a descriptor to work on */
        File descriptor = getDescriptor(pkg);
        if (!descriptor.exists()) {
        	Informer.getInstance().error(
            		this,
            		new Throwable().getStackTrace()[0].getMethodName(),
            		"Descriptor does not exist: " + descriptor.getAbsolutePath(),
            		"Asserting that packages have descriptors.",
            		new PackageException());
        }
    }

    /**
     * Create a new mets descriptor to describe this directory
     * 
     * @param pkg
     * @throws FatalException
     */
    protected void createDescriptor(File pkg) throws FatalException {
    
        Informer.getInstance().info(
        		this,
        		new Throwable().getStackTrace()[0].getMethodName(),
        		"Creating METS descriptor for package: " + pkg.getName(),
        		"Filtering packages in pre processor.",
        		false);

    	
        /* paths */
        String packagePath = pkg.getAbsolutePath();
        String descriptorName = getDescriptor(pkg).getName();
       
        /* descriptor */
        METSDescriptor.Factory.createSipDescriptor(packagePath, descriptorName, null);
    }


	/**
	 * @param file
	 * @return	the DOM document parsed from the file
	 * @throws FatalException 
	 */
	protected Document parseXml(File file) throws FatalException {
	
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
        	WebCacheResolver resolver = WebCacheUtils.getResolver();
			builder.setEntityResolver(resolver);
			document = builder.parse(file);
		} catch (Exception e) {
			StackTraceElement element = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(this, element.getMethodName(), "Cannot parse descriptor: " + file.getPath(), "Ensuring @CHECKSUMTYPE='MD5'", e);
		}
		return document;
	}
}
