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
package edu.fcla.daitss.format.multimedia.quicktime;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

public class QuickTimeAnomalies extends Anomalies {

	// missing top-level MOOV atom
	public static final String QUICKTIME_NO_MOOV = "A_QUICKTIME_NO_MOOV";

	// missing top-level MDAT atom
	public static final String QUICKTIME_NO_MDAT = "A_QUICKTIME_NO_MDAT";
	
	// malform MOOV atom
	public static final String QUICKTIME_BAD_MOOV = "A_QUICKTIME_BAD_MOOV";
	
	// malform Track atom
	public static final String QUICKTIME_BAD_TRAK = "A_QUICKTIME_BAD_TRAK";
	
	// malform Media atom
	public static final String QUICKTIME_BAD_MDIA = "A_QUICKTIME_BAD_MDIA";
	
	// malform Media Information atom
	public static final String QUICKTIME_BAD_MINF = "A_QUICKTIME_BAD_MINF";
	
	// insufficient atom size
	public static final String QUICKTIME_ATOM_TOO_SMALL = "A_QUICKTIME_ATOM_TOO_SMALL";
	
	public QuickTimeAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known QuickTime anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		
		insert(QUICKTIME_NO_MOOV, Severity.SEVERITY_NOTE, "missing the required Movie atom");
		insert(QUICKTIME_NO_MDAT, Severity.SEVERITY_NOTE, "missing the required Movie Data atom");
		insert(QUICKTIME_BAD_MOOV, Severity.SEVERITY_NOTE, "malform Movie atom");
		insert(QUICKTIME_BAD_TRAK, Severity.SEVERITY_NOTE, "malform Track atom");
		insert(QUICKTIME_BAD_MDIA, Severity.SEVERITY_NOTE, "malform Media atom");
		insert(QUICKTIME_BAD_MINF, Severity.SEVERITY_NOTE, "malform Media Information atom");
		insert(QUICKTIME_ATOM_TOO_SMALL, Severity.SEVERITY_NOTE, "insufficient atom size");
	}
}
