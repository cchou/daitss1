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
package edu.fcla.daitss.format.audio.wave;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Limitations;
import edu.fcla.daitss.severe.element.Severity;

/**
 * * WaveLimitations represent the list of software limitations in processing WAVE files.
 * 
 * @author carol
 */
public class WaveLimitations extends Limitations {
	/**
	 * unsupported wave audio encoding
	 */
	public static final String WAVE_UNSUPPORTED_ENCODING = "L_WAVE_UNSUPPORTED_ENCODING";
	
	public WaveLimitations() throws FatalException {
		super();
		buildList();
	}
	
	/**
	 * Builds the list of known WAVE limitations.
	 * 
	 * @throws FatalException
	 */
	private void buildList() throws FatalException {
		// unsupported audio encoding in the WAVE file, need to downgrade to bit level
		insert(WAVE_UNSUPPORTED_ENCODING, Severity.SEVERITY_BIT, 
				"unsupported wave audio encoding.");
	}
}
