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
package edu.fcla.daitss.format.multimedia.avi;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Quirks;
import edu.fcla.daitss.severe.element.Severity;


/**
 * AviQuirks represent quirks that were introduced in normalized/localized/migrated avi file that
 * were introduced by processing AVI file
 */
public class AviQuirks extends Quirks {
	/**
	 * the data quality is reset to -1 after normalization, Q_ stands for Quirk
	 */
	public static final String NORM_DATA_QUALITY_RESET = "Q_NORM_DATA_QUALITY_RESET";
	
	public AviQuirks() throws FatalException {
		super();
		buildList();
	}
	
	/**
	 * Builds the list of known AVI quirks.
	 * 
	 * @throws FatalException
	 */
	private void buildList() throws FatalException {
		// data quality reset.  This quirk is introduced by the normalization program "ffmpeg"
		insert(NORM_DATA_QUALITY_RESET, Severity.SEVERITY_NOTE, 
			"data quality metadata is reset to -1 by normalization program");
	}
}
