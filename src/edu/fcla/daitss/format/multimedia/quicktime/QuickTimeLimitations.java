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
import edu.fcla.daitss.severe.element.Limitations;
import edu.fcla.daitss.severe.element.Severity;

public class QuickTimeLimitations  extends Limitations {
	/**
	 * unsupported video encoding
	 */
	public static final String QUICKTIME_UNSUPPORTED_VIDEO_ENCODING = "L_QUICKTIME_UNSUPPORTED_VIDEO_ENCODING";
	
	/**
	 * unsupported audio encoding
	 */
	public static final String QUICKTIME_UNSUPPORTED_AUDIO_ENCODING = "L_QUICKTIME_UNSUPPORTED_AUDIO_ENCODING";

	/**
	 * unsupported compressed header
	 */
	public static final String QUICKTIME_UNSUPPORTED_COMPRESSED_HEADER = "L_QUICKTIME_UNSUPPORTED_COMPRESSED_HEADER";
	
	/** constructor
	 * @throws FatalException
	 */
	public QuickTimeLimitations() throws FatalException {
		super();
		buildList();
	}
	
	/**
	 * Builds the list of known QuickTime limitations.
	 * 
	 * @throws FatalException
	 */
	private void buildList() throws FatalException {
		// unsupported video encoding in the QuickTime file, need to downgrade to bit level
		insert(QUICKTIME_UNSUPPORTED_VIDEO_ENCODING, Severity.SEVERITY_BIT, 
				"unsupported video encoding in the QuickTime file.");
		
		// unsupported audio encoding in the QuickTime file, need to downgrade to bit level
		insert(QUICKTIME_UNSUPPORTED_AUDIO_ENCODING, Severity.SEVERITY_BIT, 
				"unsupported audio encoding in the QuickTime file.");
		
		// unsupported compressed header in the QuickTime file, need to downgrade to bit level
		insert(QUICKTIME_UNSUPPORTED_COMPRESSED_HEADER, Severity.SEVERITY_BIT, 
				"unsupported comrpessed header in the QuickTime file.");
	}

}
