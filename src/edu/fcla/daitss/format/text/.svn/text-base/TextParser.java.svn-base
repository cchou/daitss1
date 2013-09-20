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

package edu.fcla.daitss.format.text;

import java.io.EOFException;

import edu.fcla.daitss.format.text.dtd.DTDParser;
import edu.fcla.daitss.format.text.dtd.DTDTextParser;
import edu.fcla.daitss.format.text.dtd.DTDWriterException;

/**
 * Utility class to parse a String.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class TextParser {
	
	/**
	 * Denotes end of data stream.
	 * This is used in the peekAtNextChar and getPreviousChar 
	 * methods when at the edge of the data. 
	 */
	public static final char END_OF_DATA_CHAR = Character.MAX_VALUE;

	/**
	 * Location of the first char (inclusive) from which to start reading.
	 */
	private int begin = 0;
	
	/**
	 * Current column parsing position.
	 * A value of 1 for the clm will indicate that the first
	 * char in the current row is read.
	 */
	private int clm = 0;
	
	/**
	 * Current parsing position as measured from the beginning 
	 * of the String data. 
	 * For example a value of '0' will indicate that the first char
	 * is yet to be read. '1' will indicate that the first char is 
	 * read and the second char is to be read. 
	 */
	private int currPos = 0;

	/** 
	 * String data being parsed.
	 */
	private String data = "";
	
	/**
	 * Location of the last character (inclusive) until which reading is allowed.
	 * -1 indicates the last character of the string
	 */
	private int end   = -1;
	
	/**
	 * Current row parsing position.
	 * A value of 1 for the row will indicate that the first
	 * row is being read.
	 * 
	 */
	private int row = 1;

	/**
	 * Data Constructor.
	 * 
	 * @param strData The data to be parsed.
	 */
	public TextParser(String strData) {
		
		data = strData;
		
		if(strData != null) {
			end  = strData.length() - 1;
		} else {
			row = 0;
		}
	}
	
	/**
	 * Conditional read from the data stream.
	 * Checks if the next char is the one specified.
	 * If the next char is the char specified, move
	 * ahead by one char (i.e. read the char).
	 * 
	 * @param lookupChar The char to look for.
	 * @return  Returns whether or not the advance occurred.
	 * @throws EOFException
	 */
	public boolean advanceIfNextChar(char lookupChar) throws EOFException {
	    
		if (data == null || endOfData())
			throw new EOFException("At end of file");
	
		char nextChar = peekAtNextChar();
		
		if(nextChar != lookupChar) {
			return false;
		}
		
		getNextChar(nextChar);
			
		return true;
	}
	
	/**
	 * Check if the end of the data stream is reached.
	 * 
	 * @return <code>true</code> if end of data is reached; 
	 * <code>false</code> if end of data is not reached.
	 */
	public boolean endOfData() {
	
		if (currPos > end) {
			return true;
		}
	
		return false;
	}

    /**
     * Read the next token, delimited by the specified delimiter, from the data stream.
     * 
     * @param delim The delimiter for the Token.
     * @return The next Token.
     * @throws EOFException
     */
    public String getAllSkipping(char delim) throws EOFException {
    
    	StringBuffer name = new StringBuffer("");
    	
    	while(peekAtNextChar() != delim)
    		name.append(getNextChar());
    
    	//read the delim char
    	getNextChar();
    
    	return name.toString();
    }

    /**
     * Read the next token, delimited by one of the specified 
     * delimiters, from the data stream. 
     * 
     * @param delims The list of delimiters for the Token.
     * @return The next Token.
     * @throws EOFException
     */
    public String getAllSkipping(char[] delims) throws EOFException {
    
    	char nextChar;
    	String delimsStr = new String(delims);
    	StringBuffer name = new StringBuffer("");
    	
    	while(!endOfData() && delimsStr.indexOf(peekAtNextChar()) == -1) {
    	    nextChar = getNextChar();
    	    name.append(nextChar);
    	    
    	}
    	if (!endOfData()){
    	    getNextChar();
    	}
    	
    	return name.toString();
    }

    /**
     * Read the next token, delimited by a whitespace, from the data stream.
     * 
     * @return The next token.
     * @throws EOFException
     */
    public String getAllSkippingNextWS() throws EOFException {
    
    	StringBuffer name = new StringBuffer("");
    	
    	while(!endOfData() && !DTDTextParser.isWhiteSpace(peekAtNextChar()))
    		name.append(getNextChar());
    
    	//read the space char
    	if (!endOfData()) {
    	    getNextChar();
    	}
    	
    	return name.toString();
    }
	
	/**
	 * Get the next n sequential characters.
	 * 
	 * @param numChars number of characters to retrieve
	 * @return the characters as a String
	 * @throws EOFException
	 */
	public String getChars(int numChars) throws EOFException {
	    StringBuffer sb = new StringBuffer();
	    
	    for (int i=0; i<numChars; i++) {
	        sb.append(getNextChar());
	    }
	    
	    return sb.toString();
	}

	/**
	 * Get the current reading position in the Data stream - 
	 * as measured in the number of chars from the beginning
	 * of the data.
	 * 
	 * @return The current position.
	 */
	public int getCurrentPosition() {
		return currPos;
	}
	
	/**
	 * Read the next char from the data stream.
	 * 
	 * @return The next char.
	 * @throws EOFException
	 */
	public char getNextChar() throws EOFException {
	    
	    if (data == null || endOfData())
			throw new EOFException("At end of file");
	
		char nextChar = peekAtNextChar();
		
		getNextChar(nextChar);
			
		return nextChar;
	}
	
	/**
	 * Advance file pointer ahead into the data stream.
	 * Note that users of this method must check that the next
	 * character is <code>thisChar</code>.
	 * 
	 * @param thisChar The char that was moved past.
	 * @throws EOFException
	 */
	private final void getNextChar(char thisChar) throws EOFException {
	    
	    if (data == null || endOfData())
			throw new EOFException("At end of file");
		
		currPos++;
	
		if(thisChar == '\n') {
			row++;
			clm = 0;
		} else {
			clm++;
		}
	}
	
	/**
	 * Get the unread part of the data stream.
	 * 
	 * @return The remaining data string.
	 * @throws EOFException
	 */
	public String getRemaining() throws EOFException {
		
		if (data == null || endOfData())
			throw new EOFException("At end of file");
	
		return data.substring(currPos);
	}

    /**
     * Either ignores or writes out whitespace, depending on what
     * the DTD parser is supposed to be doing.
     * 
     * @throws DTDWriterException
     * @throws EOFException
     */
    public void handleWhiteSpace() throws DTDWriterException, EOFException {
        if (!endOfData()) {
            if (DTDParser.shouldWriteFile()) {
    			printWhiteSpace();
    		} else {
    			skipWhiteSpace();
    		}
        }
    }
	
	/**
	 * Get the total length of the data stream,
	 * whether or not its been read.
	 * 
	 * @return total length of data.
	 */
	public int length() {
		return (end - begin + 1);
	}

	/**
	 * Get the row number which the parser is currently parsing. 
	 * 
	 * @return the current row number
	 */
	public int row() {
		return row;
	}
    /**
     * Check if the current location starts with the 
     * string specified.
     * 
     * @param str The string to check against.
     * @return <code>true</code> if the current location
     * 		starts with the string specified; 
     * 		<code>false</code> otherwise.
     * @throws EOFException
     */
    public boolean nextStringEquals(char[] str) throws EOFException {
    	
    	for (int i = 0; i < str.length; i++) {
    		if (peekAhead(i + 1) != str[i]) {
    			return false;
    		}
    	}
    	
    	return true;
    }

    /**
     * Check if the current location starts with the 
     * string specified.
     * 
     * @param str The string to check against.
     * @return <code>true</code> if the current location
     * 		starts with the string specified; 
     * 		<code>false</code> otherwise.
     * @throws EOFException
     */
    public boolean nextStringEquals(String str) throws EOFException {	
    	return nextStringEquals(str.toCharArray());
    }
	
	/**
	 * Peek ahead n number of chars from the data.
	 * 
	 * @param numChars Number of characters to peek ahead. 
	 * 			<code>0</code> will return current char, 
	 * 			<code>1</code> will return next char
	 * @return The peeked char.
	 * @throws EOFException
	 */
	public char peekAhead(int numChars) throws EOFException {
	    
	    if (data == null || endOfData() || (currPos + numChars - 1) > end)
			throw new EOFException("At end of file");

		if( numChars < 0 || (currPos + numChars - 1) < begin) {
		    // should not have been called
		    return 0;
		}
	
		char nextChar = data.charAt(currPos + numChars - 1);
			
		return nextChar;
	}
	
	/**
	 * Peek at the next char to be read.
	 * 
	 * @return The next unread character in the data.
	 * @throws EOFException
	 */
	public char peekAtNextChar() throws EOFException {
	
	    if (data == null || endOfData())
			throw new EOFException("At end of file");
	
		char nextChar = data.charAt(currPos);
			
		return nextChar;
	}
	
	/**
	 * Get the previous char read from the data stream.
	 * 
	 * @return The previous char.
	 */
	public char peekAtPrevChar() {
		
		if (data == null || data.length() == 0 | currPos == begin){
		    // the method shouldn't have been called for this case
		    return 0;
		}
			
		if (endOfData()) {
		    // return last char
			return data.charAt(end);
		}
		
		char prevChar = data.charAt(currPos - 1);
			
		return prevChar;
	}

    /**
     * Read from the data stream and pass to the out stream
     * while the char is a whitespace character.
     * 
     * @throws DTDWriterException
     * @throws EOFException
     */
    public void printWhiteSpace() throws DTDWriterException, EOFException {
        
        while(!endOfData() && DTDTextParser.isWhiteSpace(peekAtNextChar())){
            char ws = getNextChar();
            DTDParser.getWriter().write(ws);
        }
    }
	
	/**
	 * Reset the data for use.
	 */
	public void reset() {
		data = "";
		currPos = 0;
		row = 1;
		clm = 0;
		begin = 0;
		end = -1;
	}
	
	/**
	 * Move the current read position back to the
	 * beginning of the data stream.
	 */
	public void rewind() {
		currPos = begin;
		row = 1;
		clm = 0;
		begin = 0;
		
		if (data != null) {
			end = data.length() - 1;
		} else {
			end = -1;
			row = 0;
		}
	}

    /**
     * Read from the data stream while the char is among
     * the one specified.
     * 
     * @param delims The list of characters to be skipped.
     * @throws EOFException
     */
    public void skipChars(char[] delims) throws EOFException {
    	
    	if(delims == null || delims.length == 0) {
    		return;
    	}
    	
    	char nextChar;
    	boolean isDelim = true;
    	
    	while(isDelim) {
    		nextChar = peekAtNextChar();
    		isDelim = false;
    		
    		for(int i = 0; i < delims.length; i++) {
    			if (nextChar == delims[i]) {
    				getNextChar();
    				isDelim = true;
    				//break; //added by AG
    			}
    		}
    	}
    
    }

    /**
     * Skip the next few chars.
     * 
     * @param numChars Number of characters to skip.
     * 			<code>0</code> will not skip any characters.
     * 			<code>1</code> will read and discard one charater.
     * @throws EOFException
     */
    public void skipChars(int numChars) throws EOFException {
    	
    	if (numChars <= 0)
    		return;
    		
    	for(int i = 0; i < numChars; i++) {
    		getNextChar();
    	}
    }

    /**
     * Read from the data stream while the char is
     * a whitespace character.
     * Note that a lot of methods are relying on the fact that this
     * makes the endOfData check so they don't have to.
     * 
     * @throws EOFException
     */
    public void skipWhiteSpace() throws EOFException {
    
    	while(!endOfData() && DTDTextParser.isWhiteSpace(peekAtNextChar()))
    		getNextChar();
    }
	
	/**
	 * Return the String representation of the data stream.
	 * @return The string data.
	 */
	public String toString() {
		
		if(data == null) {
			return null;
		}
		
		if (end == (data.length() - 1) && begin == 0) {
			return data;
		}
		return data.substring(begin, end + 1);
	}
}