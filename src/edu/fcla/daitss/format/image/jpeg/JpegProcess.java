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
/*
 * Created on Nov 5, 2003
 *
 */
package edu.fcla.daitss.format.image.jpeg;

/**
 * JPEGProcess represents the processes used in JPEG compresssion.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class JpegProcess {
	
	/**
	 * Baseline DCT, non-differential, Huffman coding
	 */
	public static final String BASELINE = "BASELINE";
	
	/**
	 * Differential lossless (sequential), Huffman coding
	 */
	public static final String DIFF_LOSSLESS = "DIFF_LOSSLESS";
	
	/**
	 * Differential lossless (sequential), arithmetic coding
	 */
	public static final String DIFF_LOSSLESS_ARITH = "DIFF_LOSSLESS_ARITH";
	
	/**
	 * Differential progressive DCT, Huffman coding
	 */
	public static final String DIFF_PROG = "DIFF_PROG";
	
	/**
	 * Differential progressive DCT, arithmetic coding
	 */
	public static final String DIFF_PROG_ARITH = "DIFF_PROG_ARITH";
	
	/**
	 * Differential sequential DCT, Huffman coding
	 */
	public static final String DIFF_SEQ = "DIFF_SEQ";
	
	/**
	 * Differential sequential DCT, arithmetic coding
	 */
	public static final String DIFF_SEQ_ARITH = "DIFF_SEQ_ARITH";
	
	/**
	 * Extended sequential DCT, non-differential, Huffman coding
	 */
	public static final String EXT_SEQ = "EXT_SEQ";
	
	/**
	 * Extended sequential DCT, non-differential, arithmetic coding
	 */
	public static final String EXT_SEQ_ARITH = "EXT_SEQ_ARITH";
	
	/**
	 * Lossless (sequential), non-differential, Huffman coding
	 */
	public static final String LOSSLESS = "LOSSLESS";
	
	/**
	 * Lossless (sequential), non-differential, arithmetic coding
	 */
	public static final String LOSSLESS_ARITH = "LOSSLESS_ARITH";
	
	/**
	 * Progressive DCT, non-differential, Huffman coding
	 */
	public static final String PROG = "PROG";
	
	/**
	 * Progressive DCT, non-differential, arithmetic coding
	 */
	public static final String PROG_ARITH = "PROG_ARITH";
	
	/**
	 * Unknown encoding process
	 */
	public static final String UNKNOWN = "UNKNOWN";
	
	/**
	 * Determines whether or not a JPEG Process code name is a valid one
	 * in the archive.
	 * 
	 * @param process a JPEG Process code name
	 * @return <code>true</code> if the name is valid, else
	 * 	<code>false</code>
	 */
	public static boolean isValidProcess(String process) {
		if (process != null &&
			(process.equals(BASELINE) || process.equals(DIFF_LOSSLESS) ||
			process.equals(DIFF_LOSSLESS_ARITH) || process.equals(DIFF_PROG) ||
			process.equals(DIFF_PROG_ARITH) || process.equals(DIFF_SEQ) ||
			process.equals(DIFF_SEQ_ARITH) || process.equals(EXT_SEQ) ||
			process.equals(EXT_SEQ_ARITH) || process.equals(LOSSLESS) ||
			process.equals(LOSSLESS_ARITH) || process.equals(PROG) ||
			process.equals(PROG_ARITH) || process.equals(UNKNOWN))) {
				return true;
		}
		return false;
	}
	
}
