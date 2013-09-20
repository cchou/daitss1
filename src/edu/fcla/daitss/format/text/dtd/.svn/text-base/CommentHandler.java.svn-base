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
 * Class to read the comment text.
 *
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class CommentHandler {
	
	/**
	 * Token identifying the end of a comment.
	 */
	private static final String COMMENT_END = "-->";

	/**
	 * Token identifying the start of a comment.
	 */
	private static final String COMMENT_START = "<!--";
	
	/**
	 * CommentReader Constructor.
	 */
	public CommentHandler() {
		super();
	}
	
	/**
	 * Either skips or writes out a comment, depending on what the
	 * parser should be doing.
	 * 
	 * @param data all of the DTD data with a ptr to the curent reading position
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 */
	public void handleComment(DTDTextParser data) 
		throws DTDSyntaxException, DTDWriterException, EOFException {
	    
		if(!seeCommentStart(data)) {
			return; 
		}
		data.skipChars(COMMENT_START.length());
		
		if (DTDParser.shouldWriteFile()) {
			writeComment(data);
		} else {
			skipComment(data);
		}
	}
	
	/**
	 * Checks if there is a comment at the 
	 * beginning of the parser reading position.
	 * 
	 * @param data all of the DTD data with a ptr to the curent reading position
	 * 
	 * @return <code>true</code> if the current location
	 * 	of the data has a comment; <code>false</code> otherwise.
	 * @throws EOFException
	 */
	public boolean seeCommentStart(DTDTextParser data) throws EOFException {
		return data.nextStringEquals(COMMENT_START);
	}
	
	/**
	 * Determines if the current parser reading position is
	 * at the start of "--". Note that the XML 1.0 spec does not
	 * allow "--" withn a comment so this must indicate that we
	 * are at the start of a comment end.
	 * 
	 * @param data all of the DTD data with a pointer at the current
	 * 	reading position
	 * @return whether the parser is at the start of the end of a comment
	 * @throws EOFException
	 */
	private boolean seeCommentEndStart(DTDTextParser data) throws EOFException {
		return(data.nextStringEquals("--"));
	}
	
	/**
	 * Determines if the current parser reading position is
	 * at the start of "-->".
	 * 
	 * @param data all of the DTD data with a pointer at the current
	 * 	reading position
	 * @return whether the parser is at the start of the end of a comment
	 * @throws EOFException
	 */
	private boolean seeCommentFullEnd(DTDTextParser data) throws EOFException {
		return data.nextStringEquals(COMMENT_END);
	}
	
	/**
	 * Skips a comment.
	 * 
	 * @param data all of the DTD data with a pointer at the current
	 * 	reading position
	 * @throws DTDSyntaxException
	 * @throws EOFException
	 */
	private void skipComment(DTDTextParser data) throws DTDSyntaxException, EOFException {
		boolean sawCommentClose = false;
		
		// iterate through each character in this comment until we see its end
		while (!data.endOfData()){
			if (seeCommentEndStart(data) && !seeCommentFullEnd(data)){
				throw new DTDSyntaxException("Encountered illegal characters within a comment");
			} else if (seeCommentFullEnd(data)){
				data.skipChars(COMMENT_END.length());
				sawCommentClose = true;
				break;
			} else {
				data.getNextChar(); // and do nothing with it
			}
		}
		
		// did we see the cmoment end properly?
		if (!sawCommentClose){
			throw new DTDSyntaxException("DTD data ended before the commment closed");
		}
	}
	
	/**
	 * Writes out a comment.
	 * 
	 * @param data all of the DTD data with a pointer at the current
	 * 	reading position
	 * @throws DTDSyntaxException
	 * @throws DTDWriterException
	 * @throws EOFException
	 */
	private void writeComment(DTDTextParser data) 
		throws DTDSyntaxException, DTDWriterException, EOFException {
		
		DTDParser.getWriter().write(COMMENT_START);
		
		boolean sawCommentClose = false;
		
		// iterate through each comment character writing it out, looking for
		// the comment end
		while (!data.endOfData()){
			if (seeCommentEndStart(data) && !seeCommentFullEnd(data)){
				throw new DTDSyntaxException(
				        "Encountered illegal characters within a comment ");
			} else if (seeCommentFullEnd(data)){
				data.skipChars(COMMENT_END.length());
				DTDParser.getWriter().write(COMMENT_END);
				sawCommentClose = true;
				break;
			} else {
				char c = data.getNextChar();
				DTDParser.getWriter().write(c);
			}
		}
		
		// did we see the end?
		if (!sawCommentClose){
			throw new DTDSyntaxException("DTD data ended before the commment closed");
		}
	}
	
}
