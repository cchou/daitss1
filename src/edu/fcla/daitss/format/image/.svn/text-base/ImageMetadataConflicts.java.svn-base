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
package edu.fcla.daitss.format.image;

import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.MetadataConflicts;
import edu.fcla.daitss.severe.element.Severity;

public class ImageMetadataConflicts extends MetadataConflicts {
	/**
	 * ImageMetadataConflicts represents all the known generic image-specific conflicts in metadata.
	 * Note that all metdata-conflict values must be less than 255 characters and need to start
	 * with "M_", metdata-conflict descriptions must be less than 255 characters.
	 */
	public static final String COLORSPACE_MISMATCH = "M_COLORSPACE_MISMATCH";
	public static final String ORIENTATION_MISMATCH = "M_ORIENTATION_MISMATCH";
	public static final String DEPTH_MISMATCH = "M_DEPTH_MISMATCH";
	public static final String XDIMENSION_MISMATCH = "M_XDIMENSION_MISMATCH";
	public static final String YDIMENSION_MISMATCH = "M_YDIMENSION_MISMATCH";
	
	// A list of format-specific conflicts. Contains SevereElement objects
	protected static Vector elems = null;
	
	// Number of format-specific conflicts that are currently in this class' list of conflicts
	protected static int numBuiltElems = 0;
	
	/**
	 * Builds the list of known generic-image-specific metadata conflicts.
	 * 
	 * @throws FatalException
	 */
	public ImageMetadataConflicts() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known generic-image-specific metadata conflicts.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// conflict in file size metadata
		insert(COLORSPACE_MISMATCH, Severity.SEVERITY_NOTE, "metadata conflict in image colorspace");
		insert(ORIENTATION_MISMATCH, Severity.SEVERITY_NOTE, "metadata conflict in image orientation");
		insert(DEPTH_MISMATCH, Severity.SEVERITY_NOTE, "metadata conflict in image depth");
		insert(XDIMENSION_MISMATCH, Severity.SEVERITY_NOTE, "metadata conflict in image X dimension");
		insert(YDIMENSION_MISMATCH, Severity.SEVERITY_NOTE, "metadata conflict in image Y dimension");
	}
}
