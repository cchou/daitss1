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
 * This class was loosely based on one from "Matra - the DTD Parser" whose initial
 * developer was Conrad S Roche <derupe at users.sourceforge.net>. 
 * The Matra code was released under either the Mozilla Public License 
 * Version 1.1 or alternatively the GNU GENERAL PUBLIC LICENSE Version 2 or 
 * later.
 */
package edu.fcla.daitss.format.text.dtd;

import java.io.EOFException;

import edu.fcla.daitss.format.text.dtd.DTDWriterException;


/**
 * Class to read the processing instruction text.
 *
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class PIHandler {
	
	/**
	 * Token identifying a PI end.
	 */
	private static final String PI_END = "?>";

	/**
	 * Token identifying a PI start.
	 */
	private static final String PI_START = "<?";
	
	/**
	 * Skips or writes a processing instruction to a file, depending on
	 * what the DTD parser is supposed to do.
	 * 
	 * @param data Dtd contents with a pointer at current reading position
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 */
	public void handlePi(DTDTextParser data) 
		throws DTDSyntaxException, DTDWriterException, EOFException {
		if(!seePiStart(data)) {
			return; 
		}
		data.skipChars(PI_START.length());
		
		if (DTDParser.shouldWriteFile()) {
			writePi(data);
		} else {
			skipPi(data);
		}
	}
	
	/**
	 * Checks if there is a processing instruction end at the 
	 * current location of the data.
	 * 
	 * @param data The data to be parsed.
	 * @return <code>true</code> if the current location
	 * 		of the data has a processing instruction; 
	 * 		<code>false</code> otherwise.
	 * @throws EOFException
	 */
	public boolean seePiEnd(DTDTextParser data) throws EOFException {
		
		return data.nextStringEquals(PI_END);
	}

	/**
	 * Checks if there is a processing instruction start at the 
	 * current location of the data.
	 * 
	 * @param data The data to be parsed.
	 * @return <code>true</code> if the current location
	 * 		of the data has a processing instruction; 
	 * 		<code>false</code> otherwise.
	 * @throws EOFException
	 */
	public boolean seePiStart(DTDTextParser data) throws EOFException {
		
		return data.nextStringEquals(PI_START);
	}
	
	/**
	 * Skip over a processing instruciton.
	 * 
	 * @param data Dtd contents with a pointer at current reading position
	 * @throws DTDSyntaxException
	 * @throws EOFException
	 */
	public void skipPi(DTDTextParser data) throws DTDSyntaxException, EOFException {
		boolean sawPiClose = false;
		
		while (!data.endOfData()){
			if (seePiEnd(data)){
				data.skipChars(PI_END.length());
				sawPiClose = true;
				break;
			} 
				
			data.getNextChar(); // and do nothing with it
			
		}
		
		if (!sawPiClose){
			throw new DTDSyntaxException(
			        "DTD data ended before the processing instruction closed");
		}
	}
	
	/**
	 * Write a processing instruction to a file.
	 * 
	 * @param data Dtd contents with a pointer at current reading position
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 */
	public void writePi(DTDTextParser data) 
		throws DTDSyntaxException, DTDWriterException, EOFException {
		DTDParser.getWriter().write(PI_START);
		
		boolean sawPiClose = false;
		
		while (!data.endOfData()){
			if (seePiEnd(data)) {
				data.skipChars(PI_END.length());
				DTDParser.getWriter().write(PI_END);
				sawPiClose = true;
				break;
			} 
			
			char c = data.getNextChar();
			DTDParser.getWriter().write(c);
			
		}
		
		if (!sawPiClose){
			throw new DTDSyntaxException(
			        "DTD data ended before the processing instruction closed");
		}
	}
}

