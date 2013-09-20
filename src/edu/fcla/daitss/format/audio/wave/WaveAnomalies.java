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
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * WaveAnomalies represent anomalies that can appear in Wave files.
 * @author carol
 *
 */
public class WaveAnomalies extends Anomalies {
	/**
	 * Insufficient format chunk size
	 */
	public static final String WAVE_FMT_CHUNK_TOO_SMALL = "A_WAVE_FMT_CHUNK_TOO_SMALL";
	
	/**
	 * lack of format chunk before data chunk
	 */
	public static final String WAVE_NO_FMT_BEFORE_DATA = "A_WAVE_NO_FMT_BEFORE_DATA";
	
	/**
	 * lack of fact chunk for compressed data
	 */
	public static final String WAVE_NO_FACT_FOR_COMPRESSED_DATA = "A_WAVE_NO_FACT_FOR_COMPRESSED_DATA";
	
	/**
	 * bad associated data list chunk
	 */
	public static final String WAVE_BAD_ASSOCIATED_DATA_CHUNK = "A_WAVE_BAD_ASSOCIATED_DATA_CHUNK";
	
	/**
	 * undefined cue point name is used in the associated data list
	 */
	public static final String WAVE_UNDEFINED_CUE_POINT_IN_ASSOCIATED_DATA_LIST = "A_WAVE_UNDEFINED_CUE_POINT_IN_ASSOCIATED_DATA_LIST";
	/**
	 * bad subchunk in the associated data list chunk
	 */
	public static final String WAVE_BAD_SUBCHUNK_IN_ASSOCIATED_DATA_LIST = "A_WAVE_BAD_SUBCHUNK_IN_ASSOCIATED_DATA_LIST";
	/**
	 * non-unique cue point id.
	 */
	public static final String WAVE_NON_UNIQUE_CUE_POINT_ID = "A_WAVE_NON_UNIQUE_CUE_POINT_ID";
	
	/**
	 * multiple cue-points chunks are seen in this wave file
	 */
	public static final String WAVE_MULTIPLE_CUE_POINTS_CHUNK = "A_WAVE_MULTIPLE_CUE_POINTS_CHUNK";
	
	/**
	 * undefined cue point name is used in the play list chunk
	 */
	public static final String WAVE_UNDEFINED_CUE_POINT_IN_PLAYLIST = "A_WAVE_UNDEFINED_CUE_POINT_IN_PLAYLIST";
	
	/**
	 * use of unrecognizable speaker position in the channel mask
	 */
	public static final String WAVE_UNRECOGNIZABLE_SPEAKER_POSITION = "A_WAVE_UNRECOGNIZABLE_SPEAKER_POSITION";
	
	public WaveAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known Wave anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		// Insufficient format chunk size, downgrade to bit level since
		// we won't be able to parse it.
		insert(WAVE_FMT_CHUNK_TOO_SMALL, Severity.SEVERITY_NOTE, "Insufficient format chunk size");
		
		// lack of format chunk before data chunk, downgrade to bit level 
		insert(WAVE_NO_FMT_BEFORE_DATA, Severity.SEVERITY_NOTE, "lack of format chunk before data chunk");
		
		// lack of fact chunk for compressed data, downgrade to bit level 
		insert(WAVE_NO_FACT_FOR_COMPRESSED_DATA, Severity.SEVERITY_NOTE, "lack of fact chunk for compressed data");
		
		// non-unique cue point id.  The cue point id in the wave file need to be unique
		insert(WAVE_NON_UNIQUE_CUE_POINT_ID, Severity.SEVERITY_NOTE, "non-unique cue point id");
		
		// multiple cue-points chunks are seen in this wave file.  Only one is allowed.
		insert(WAVE_MULTIPLE_CUE_POINTS_CHUNK, Severity.SEVERITY_NOTE, "there are multiple cue-points chunks in this wave file");
		
		// undefined cue point name are used in the play list chunk
		insert(WAVE_UNDEFINED_CUE_POINT_IN_PLAYLIST, Severity.SEVERITY_NOTE, "undefined cue point name are used in the play list chunk");
		
		// use of unrecognizable speaker position in the channel mask
		insert(WAVE_UNRECOGNIZABLE_SPEAKER_POSITION, Severity.SEVERITY_NOTE, "use of unrecognizable speaker position in the channel mask");
		
		// bad associated data list chunk
		insert(WAVE_BAD_ASSOCIATED_DATA_CHUNK, Severity.SEVERITY_NOTE, "bad associated data list chunk");
		
		// undefined cue point name is used in the associated data list
		insert(WAVE_UNDEFINED_CUE_POINT_IN_ASSOCIATED_DATA_LIST, Severity.SEVERITY_NOTE, "undefined cue point name is used in the associated data list");
		
		// bad subchunk in the associated data list chunk
		insert(WAVE_BAD_SUBCHUNK_IN_ASSOCIATED_DATA_LIST, Severity.SEVERITY_NOTE, "bad subchunk in the associated data list chunk");
	}
}
