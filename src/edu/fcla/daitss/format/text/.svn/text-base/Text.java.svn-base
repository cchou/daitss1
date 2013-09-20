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
 * Created on Nov 12, 2003
 *
 */
package edu.fcla.daitss.format.text;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.Informer;

/**
 * Text represents a sequential continuous bitstream of human-readable
 * text. 
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Text extends Bitstream {
	
	/**
	 * Line breaks represented as CR (Carraige return 0x0d)
	 */
	public static final String LB_CR = "CR";
	
	/**
	 * Line breaks represented as CR,LF (Carraige return, Line Feed 0x0d 0x0a)
	 */
	public static final String LB_CRLF = "CRLF";
	
	/**
	 * Line breaks represented as LF (Line feed 0x0a)
	 */
	public static final String LB_LF = "LF";
	
	/**
	 * Line breaks represented as LF (Line feed, Carraige return 0x0a 0x0d)
	 */
	public static final String LB_LFCR = "LFCR";
	
	/**
	 * Line breaks not applicable (for example has 1 line of text)
	 */
	public static final String LB_NA = "N/A";
	
	/**
	 * Unknown representation of line breaks
	 */
	private static final String LB_UNKNOWN = "UNKNOWN";
	
	/**
	 * Maximum supported length of a character set name
	 */
	private static final int MAX_CHARSET_LENGTH = 255;
	
	/**
	 * The maximum supported length of a natural language String.
	 */
	private static final int MAX_NATL_LANG_LENGTH = 128;
	
	/**
	 * Maximum supported number of lines
	 */
	private static final long MAX_NUM_LINES = 9223372036854775807L;
	
	/**
	 * Minimum supported number of lines (means unknown)
	 */
	private static final long MIN_NUM_LINES = -1;

	/**
	 * Test driver
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
	}
	
	/**
	 * Character set
	 */
	private String charSet = Bitstream.UNKNOWN;
	
	/**
	 * Assume by default that the archive determined the character
	 * set/encoding of the text.
	 */
	private String charSetOrigin = Bitstream.ORIG_NA;
	
	/**
	 * Line break representation
	 */
	private String lineBreak = LB_UNKNOWN;
	
	/**
	 * Natural language
	 */
	private String natlLang = Bitstream.UNKNOWN;
	
	/**
	 * Number of lines.
	 * -1 means unknown number of pages
	 * min value: <code>MIN_NUM_LINES</code>
	 * max value: <code>MAX_NUM_LINES</code>
	 * 
	 */
	private long numLines = -1;

	/**
	 * 
	 * @param df the file containing this text bitstream
	 * @throws FatalException
	 */
	public Text(DataFile df) throws FatalException {
		super(df);
		
		this.setBsTable(ArchiveDatabase.TABLE_BS_TEXT);
		this.getCompression().setCompression(Compression.COMP_NOT_APPLICABLE);
		this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		this.setLocation("0");
	}
	
	/**
	 * Adds a line break code to the end of a comma-delimited list of character
	 * codes found in this file. If the line break characters used in 
	 * this file were unknown prior to calling this, the character
	 * code replaces the unknown code instead of appending to it.
	 * 
	 * @param _lineBreak a line break character code
	 * @throws FatalException
	 */
	public void addLineBreak(String _lineBreak)
		throws FatalException {
		if (_lineBreak != null && 
		(_lineBreak.equals(LB_CR) || _lineBreak.equals(LB_CRLF) ||
			_lineBreak.equals(LB_LF) || _lineBreak.equals(LB_LFCR))) {
				StringBuffer sb = new StringBuffer(this.getLineBreak());
				if (sb.toString().equals(LB_UNKNOWN)){
					sb.delete(0,sb.length());
				} else {
					sb.append(',');
				}
				sb.append(_lineBreak);
				this.setLineBreak(sb.toString());
		} else if(_lineBreak != null && _lineBreak.equals(LB_NA)) {
			this.setLineBreak(_lineBreak);
		} else {
			Informer.getInstance().fail(
				this, "setLineBreak(String)",
				"Illegal argument", "_lineBreak: " + lineBreak,
				new FatalException("Not a valid line break code")
			);
		}		
	}
	
	/**
	 * fill in the database column-value pairs for this document
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();

		columns.add(ArchiveDatabase.COL_BS_TEXT_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_TEXT_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_TEXT_CHARSET);
		values.add(this.getCharSet());
		columns.add(ArchiveDatabase.COL_BS_TEXT_CHARSET_ORIGIN);
		values.add(this.getCharSetOrigin());
		columns.add(ArchiveDatabase.COL_BS_TEXT_LINE_BREAK);
		values.add(this.getLineBreak());
		columns.add(ArchiveDatabase.COL_BS_TEXT_NATL_LANG);
		values.add(this.getNatlLang());

		columns.add(ArchiveDatabase.COL_BS_TEXT_NUM_LINES);
		values.add(new Long(this.getNumLines()));
	}
		
	/**
	 * Insert this document into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_TEXT, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this document
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		super.dbUpdate();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BS_TEXT_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_TEXT, columns, colValues, whereClause);
	}
	
	public Document toXML() throws FatalException {
	    /* Daitss Document */
	    Document doc = super.toXML();
	    
	    /* Daitss Element */
	    Element rootElement = doc.getDocumentElement();
	    
	    /* Daitss Namespace */
	    String namespace = rootElement.getNamespaceURI();	    
	    
	    /* Bs Text */
	    Element bsTextElement = doc.createElementNS(namespace, "BS_TEXT");
	    rootElement.appendChild(bsTextElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    org.w3c.dom.Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsTextElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    org.w3c.dom.Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsTextElement.appendChild(bsidElement);

	    /* Charset */
	    Element charsetElement = doc.createElementNS(namespace, "CHARSET");
	    String charsetValue = (this.getCharSet() != null ? this.getCharSet() : "" );
	    org.w3c.dom.Text charsetText = doc.createTextNode(charsetValue);
	    charsetElement.appendChild(charsetText);
	    bsTextElement.appendChild(charsetElement);

	    /* Charset Origin */
	    Element charsetOriginElement = doc.createElementNS(namespace, "CHARSET_ORIGIN");
	    String charsetOriginValue = (this.getCharSetOrigin() != null ? this.getCharSetOrigin() : "" );
	    org.w3c.dom.Text charsetOriginText = doc.createTextNode(charsetOriginValue);
	    charsetOriginElement.appendChild(charsetOriginText);
	    bsTextElement.appendChild(charsetOriginElement);

	    /* Line Break */
	    Element lineBreakElement = doc.createElementNS(namespace, "LINE_BREAK");
	    String lineBreakValue = (this.getLineBreak() != null ? this.getLineBreak() : "" );
	    org.w3c.dom.Text lineBreakText = doc.createTextNode(lineBreakValue);
	    lineBreakElement.appendChild(lineBreakText);
	    bsTextElement.appendChild(lineBreakElement);

	    /* Natl Lang */
	    Element natlLangElement = doc.createElementNS(namespace, "NATL_LANG");
	    String natlLangValue = (this.getNatlLang() != null ? this.getNatlLang() : "" );
	    org.w3c.dom.Text natlLangText = doc.createTextNode(natlLangValue);
	    natlLangElement.appendChild(natlLangText);
	    bsTextElement.appendChild(natlLangElement);

	    /* Num Lines */
	    Element numLinesElement = doc.createElementNS(namespace, "NUM_LINES");
	    String numLinesValue = Long.toString(this.getNumLines());
	    org.w3c.dom.Text numLinesText = doc.createTextNode(numLinesValue);
	    numLinesElement.appendChild(numLinesText);
	    bsTextElement.appendChild(numLinesElement);

	    return doc;
	}
	
	/**
	 * @return character set
	 */
	public String getCharSet() {
		return this.charSet;
	}

	/**
	 * @return the origin of the character
	 * set information
	 */
	public String getCharSetOrigin() {
		return charSetOrigin;
	}

	/**
	 * @return line break representation
	 */
	public String getLineBreak() {
		return this.lineBreak;
	}

	/**
	 * @return natural language
	 */
	public String getNatlLang() {
		return this.natlLang;
	}

	/**
	 * @return number of lines
	 */
	public long getNumLines() {
		return this.numLines;
	}

	/**
	 * @param _charSet character set
	 * @throws FatalException
	 */
	public void setCharSet(String _charSet) 
		throws FatalException {
		if (_charSet == null ){
			Informer.getInstance().fail(
				this, "setCharSet(String)",
				"Illegal argument", "_charSet: " + _charSet,
				new FatalException("Character set name can't be null")
			);
		} else if (_charSet.length() > MAX_CHARSET_LENGTH){
			// too long of a string to store - log this and try
			// to store the value anyway
			//SevereElement ta = 
				//TextAnomalies.getSevereElement(TextAnomalies.TXT_OVRLMT_CHARSET);
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TextAnomalies.TXT_OVRLMT_CHARSET);
			this.getDf().addAnomaly(ta);
		}
		this.charSet = _charSet;
	}

	/**
	 * @param _charSetOrigin the origin of the character set information 
	 * @throws FatalException
	 */
	public void setCharSetOrigin(String _charSetOrigin) throws FatalException {
		if (_charSetOrigin == null || 
			(!_charSetOrigin.equals(Bitstream.ORIG_ARCHIVE) &&
			 !_charSetOrigin.equals(Bitstream.ORIG_DEPOSITOR) &&
			 !_charSetOrigin.equals(Bitstream.ORIG_NA))) {
				Informer.getInstance().fail(
					this,
					"setCharSetOrigin(String)",
					"Invalid argument",
					"_charSetOrigin: " + _charSetOrigin,
					new FatalException("Not a valid character set origin"));
			}
		charSetOrigin = _charSetOrigin;
	}

	/**
	 * Only used by subclasses of TextFile to set their line break member
	 * to what it was found to be after parsing its content as plain text.
	 * When it was parsed as plain text the line breaks were set incrementally
	 * using <code>addLineBreak</code>.
	 * 
	 * @param _lineBreak line break representation
	 */
	public void setLineBreak(String _lineBreak) {
		this.lineBreak = _lineBreak;
	}

	/**
	 * @param _natlLang natural language
	 * @throws FatalException
	 */
	public void setNatlLang(String _natlLang) 
		throws FatalException {
		if (_natlLang != null && _natlLang.length() <= MAX_NATL_LANG_LENGTH){
			this.natlLang = _natlLang;
		} else {
			Informer.getInstance().fail(
				this, "setNatlLang(String)",
				"Illegal argument", "_natlLang: " + _natlLang,
				new FatalException("Not a valid natural language name"));
		}
		
	}

	/**
	 * @param _numLines number of lines
	 * @throws FatalException
	 */
	public void setNumLines(long _numLines) 
		throws FatalException {
		if (_numLines < MIN_NUM_LINES){
			Informer.getInstance().fail(
				this, "setNumLines(String)",
				"Illegal argument", "_numLines: " + _numLines,
				new FatalException("Not a valid number of lines"));
		}
		if (_numLines > MAX_NUM_LINES) {
			// more lines than we can support - log this and try
			// to store the value anyway
			//SevereElement ta = 
			//	TextAnomalies.getSevereElement(TextAnomalies.TXT_OVRLMT_NUM_LINES);
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(TextAnomalies.TXT_OVRLMT_NUM_LINES);
			this.getDf().addAnomaly(ta);
		}
		this.numLines = _numLines;
	}
	
	/**
	 * Returns the values of all its members.
	 * 
	 * @return the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		sb.append("\tCharacter set: " + this.getCharSet() + "\n");
		sb.append("\tCharacter set origin: " + this.getCharSetOrigin() + "\n");
		sb.append("\tLanguage: " + this.getNatlLang() + "\n");
		sb.append("\tLine break: " + this.getLineBreak() + "\n");
		sb.append("\tNumber of lines: " + this.getNumLines() + "\n");
		
		return sb.toString();
	}
}
