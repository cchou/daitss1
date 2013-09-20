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
 * Java interface "Distributed.java" generated from Poseidon for UML.
 */
package edu.fcla.daitss.file.distributed;

import java.util.Hashtable;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;

/**
 * 
 * Distributed encapsulates the methods needed for 'distributed objects'.
 * Files within distributed objects can contain links to other files in the same
 * distributed object.
 * 
 * @author Andrea Goethals, FCLA
 * @author Chris Vicary, FCLA
 *
 */
public interface Distributed {

	/**
	 * Returns a DataFile object representing the Link with alias = <code>alias</code>
	 * 
	 * @param alias The alias of a Link stored in the Distributed object
	 * @return A DataFile object representing the Link with alias = <code>alias</code>,
	 * or null if no DataFile object has been created for the Link
	 * @throws FatalException
	 */
	public DataFile getDfFromLinkAlias(String alias) throws FatalException;
	

	public Hashtable getLinkAliasRelPathTable();
	
	/**
	 * Returns the mappings this file has between it's link aliases
	 * and the associated DataFiles
	 * @return A Hashtable with link alias as the key and DataFile as the
	 * value.
	 */
	public Hashtable getLinkAliasTable();
	
	/**
	 * Uses the DataFile.linkAlias Hashtable to change the links in the file
	 * represented by this DataFile to the DFIDs of the files that the
	 * links refer to. This is done when this file is the root of a distributed
	 * object and this file was created by a migration or normalization
	 * of a different file. This would never be used by a DataFile, only
	 * subclasses of DataFiles.
	 */
	//public void updateLinks();
	
	/**
	 * Returns a Vector of Link objects as collected by retrieveLinks() 
	 */
	public Vector getLinks();
	
	/**
	 * Returns the preservation level associated with the root of 
	 * a Distributed object.
	 * 
	 * @return the preservation level
	 */
	public String getPresLevel();	

	/**
	 * <p>
	 * Iterates through the links Vector of Link objects and calls resultString
	 * = Link.retrieve(). An exception may be thrown
	 * (java.io.FileNotFoundException, MultipleFilesFoundException,
	 * SchemaNotFoundException, java.io.IOException?).
	 * </p>
	 * <p>
	 * If an exception is thrown, act differently depending on the type of
	 * exception: If a java.io.FileNotFoundException is thrown, remove the Link
	 * from the links vector and add it to the brokenLinks Vector. If any
	 * PackageException (or its subclasses) are thrown, then this method throws
	 * the PackageException.
	 * </p>
	 * <p>
	 * If an exception isn't thrown it will return the absolute path to the
	 * linked file (&quot;filePath&quot;). Calls Link.setFilePath(filePath); Calls newDf
	 * = this.cof.createDataFile(filePath, this, &quot;child&quot;); Set current Link.oid
	 * to newDF.oid. If Link.isRootSchema == true, call
	 * this.setMarkupLanguage(newDf.getOid()). Continue with next link in links
	 * Vector.
	 * </p>
	 * 
	 * @throws FatalException
	 * @throws PackageException
	 * 
	 */
	public void retrieveLinks() throws PackageException, FatalException; 
} 
