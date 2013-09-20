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
 */
package edu.fcla.daitss.file;


/**
 * Interface for all Descriptor-type objects. The interface currently
 * consists of constants for Xpaths and element/attribute names. These
 * constants are METS-specific, yet they are not contained in the METSDescriptor.
 * The rational behind this is that if the metadata language is changed 
 * (say from METS to some other XML-based format), then only this file needs 
 * to be changed to address new XPaths and names. If the constants were
 * in the METSDescriptor, then the contants would still need to be changed, 
 * but all references to those constants would also need to be changed
 * (say from METSDescriptor.XPATH_MEDIATYPE to NewFormat.XPATH_MEDIATYPE). So
 * the primary reason to store the constants here is for flexibility and 
 * maintainability. 
 * 
 */
public interface Descriptor {
	
	/**
	 * The name of the mime media type metadata attribute
	 */
	public static final String ATT_NAME_MEDIATYPE = "MIMETYPE";	
	
	// Types of descriptors.
	// The value of each type must be unique as they represent
	// positions within the <code>xmlMetadata</code> vector in the 
	// DataFile class.
	
	/**
	 * A SIP descriptor created by the archive for a package to be ingested.
	 */
	public static final int TYPE_SIP_ARCHIVE = 1;
	
	/**
	 * A SIP descriptor, not created by the archive, submitted with 
	 * a package to be ingested.
	 */
	public static final int TYPE_SIP_DEPOSITOR = 0;
	
	/**
	 * The XPath location path to get the media (MIME) type attribute of the 
	 * file element in a METS descriptor
	 */
	public static final String XPATH_MEDIATYPE = "//METS:file/@" + ATT_NAME_MEDIATYPE;
	

	public abstract String getPath();
	
} 





