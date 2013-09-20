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
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * AviAnomalies represent anomalies that can appear in AVI files.
 */
public class AviAnomalies extends Anomalies {

	/**
	 * the AVI header does not start with "avih"
	 */
	public static final String AVI_BAD_MAIN_HEADER = "A_AVI_BAD_MAIN_HEADER";
	
	/**
	 * the AVIH header does not has sufficient size. It needs to be at least 56 bytes
	 */
	public static final String AVI_MAIN_HEADER_TOO_SMALL = "A_AVI_MAIN_HEADER_TOO_SMALL";
	
	/**
	 * the AVI stream header does not start with "strh"
	 */
	public static final String AVI_BAD_STREAM_HEADER = "A_AVI_BAD_STREAM_HEADER";
	
	/**
	 * the STRH header does not has sufficient size. It needs to be at least 56 bytes
	 */
	public static final String AVI_STRH_TOO_SMALL = "A_AVI_STRH_TOO_SMALL";
	
	/**
	 * missing the required STRH header.
	 */
	public static final String AVI_STRH_MISSING = "A_AVI_STRH_MISSING";
	
	/**
	 * unidentifiable stream header.
	 */
	public static final String AVI_UNIDENTIFIABLE_STREAM_HEADER = "A_AVI_UNIDENTIFIABLE_STREAM_HEADER";
	
	/**
	 * No "hdrl" chunk before "movi" chunk.  A  "hdrl" chunk must appear before "movi" chunk.
	 */
	public static final String AVI_NO_HDRL_BEFORE_MOVI = "A_AVI_NO_HDRL_BEFORE_MOVI";
	
	/**
	 * missing the required "hdrl" chunk
	 */
	public static final String AVI_HDRL_MISSING = "A_AVI_HDRL_MISSING";
	
	/**
	 * missing the required "movi" chunk
	 */
	public static final String AVI_MOVI_MISSING = "A_AVI_MOVI_MISSING";
	
	public AviAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known AVI anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// bad AVI header
		insert(AVI_BAD_MAIN_HEADER, Severity.SEVERITY_NOTE, "Bad AVI Main Header.");
		
		// the AVIH header chunk is too small
		insert(AVI_MAIN_HEADER_TOO_SMALL, Severity.SEVERITY_NOTE, "insuficient avih header size.");
		
		// bad AVI STREAM Header 
		insert(AVI_BAD_STREAM_HEADER, Severity.SEVERITY_NOTE, "bad AVI stream header.");

		// insufficent stream header chunk 
		insert(AVI_STRH_TOO_SMALL, Severity.SEVERITY_NOTE, "insufficient stream header chunk.");

		// insufficent stream header chunk 
		insert(AVI_STRH_MISSING, Severity.SEVERITY_NOTE, "missing the required STRH header.");
		
		// unidentifiable stream header 
		insert(AVI_UNIDENTIFIABLE_STREAM_HEADER, Severity.SEVERITY_NOTE, "unidentifiable stream header");
		
		//  No "hdrl" chunk before "movi" chunk.
		insert(AVI_NO_HDRL_BEFORE_MOVI, Severity.SEVERITY_NOTE, "No hdrl chunk before movi chunk");
		
		//  missing the required "hdrl" chunk
		insert(AVI_HDRL_MISSING, Severity.SEVERITY_NOTE, "missing the required hdrl chunk");
		
		// missing the required "movi" chunk
		insert(AVI_MOVI_MISSING, Severity.SEVERITY_NOTE, "missing the required movi chunk");
	}

}
