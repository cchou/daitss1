/**
 * 
 */
package edu.fcla.daitss.format.text.csv;

import java.util.HashSet;
import java.util.Iterator;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.text.Text;
import edu.fcla.daitss.format.text.TextAnomalies;
import edu.fcla.daitss.format.text.TextFile;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * @author carol
 */
public class CSVFile extends TextFile {
	// constants
	private final char COMMA = ',';
	public final static char LF = '\n';
	public final static char CR = '\r';
	public final static String CRLF = "\r\n";
	private final char DOUBLEQUOTE = '"';
	private final static String CSV = "csv";
	
	// member variables
	private char columnDelimiter; 	// column delimiter
	private char textQualifier;		// text qualifier
	private int  numOfRows;			// total number of rows in CSV
	private boolean endedWithComma = false;
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
	    "edu.fcla.daitss.format.text.csv.CSVFile";
	
	/**
	 * Determines whether or not the file is a CSV when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a Text file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
	 * Determines if an existing, readable file is a csv text file or not
 	 * when metadata about this file is available.
	 * @return 	whether or not its a text file					
	 * @param 	filePath absolute path to an existing readable file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, DataFile)";
		
		// check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(CLASSNAME,
				methodName, "Illegal argument", "filePath: " + filePath,
				new FatalException("Not an existing, readable absolute path to a file"));
		}
		
		// check the file extension to determine if it's CSV file
		boolean isType = true;
		String fileExt = FileUtil.getExtension(filePath);
		if (fileExt != null && fileExt.equalsIgnoreCase(CSV))
			isType = true;
		else	
			isType = false;
		
		return isType;
	}         
	
	/**
	 * The constructor to call for an existing csv file when 
	 * metadata about this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public CSVFile(String path, InformationPackage ip)
		throws FatalException {
		super(path, ip);

		this.setMediaType(MimeMediaType.MIME_TXT_CSV);
		this.setFileExt(CSV);
        
		this.anomsPossible = null;
		this.anomsPossible = new CSVAnomalies();
		
		this.columnDelimiter = COMMA;
		this.textQualifier = DOUBLEQUOTE;
		this.numOfRows = 0;
		endedWithComma = false;
	}      

	/**
	 * The constructor to call for an existing csv file when  
	 * metadata about this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public CSVFile(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Evaluate the members set by parsing the file to set other members.
	 * 
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		super.evalMembers();
	}
	
	/**
	 * Parses itself and sets format-specific members.
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		super.textStream = new CSV(this);
		this.addBitstream(textStream);
		
		// detect the text encoding used in the csv file 
		String theCharSet = super.detectCharSet();
		if (!theCharSet.equals(DataFile.UNKNOWN)){
			textStream.setCharSet(theCharSet);
			textStream.setCharSetOrigin(Bitstream.ORIG_ARCHIVE);
		} else {
			this.addAnomaly(anomsPossible.getSevereElement(TextAnomalies.TXT_UNKNOWN_CHARENC));
		}
		
		// we could really like to use super.parse() method but it needs some cleanup
		detectLinebreak();		
		validate();
	}

	/**
	 * detect the line breaks used in the csv file
	 */
	void detectLinebreak() throws FatalException {
		HashSet <String> linebreaks = new HashSet <String> ();
		char currentChar = 0;
		char nextChar = 0;
		int numOfLine = 0;
		
		if (this.unicodeChars == null)
			return;
		
		// looking for linebreak used
		for (int i=0; i<this.unicodeChars.length+1; i++) {
			if (i < unicodeChars.length)
				currentChar = unicodeChars[i];
			
			if (i< unicodeChars.length-1)
				nextChar = unicodeChars[i+1];
			
			if (currentChar == CR) {
				numOfLine++;
				// saw CRLF
				if (nextChar == LF) {  
					linebreaks.add(Text.LB_CRLF);
					i++; // skip the LF since we already process it
				} else   
					// saw CR
					linebreaks.add(Text.LB_CR);
			}
			if (currentChar == LF) { 
				//saw LF 
				linebreaks.add(Text.LB_LF);
				numOfLine++;
			}
		}
		
		// add those found linebreaks to bitstream
		Iterator iter = linebreaks.iterator ();
		while (iter.hasNext()) {
			String linebr = (String) iter.next();
			super.textStream.addLineBreak(linebr);
		}
		
		// uses multiple line ending characters
		if (linebreaks.size() > 1)
			this.addAnomaly(anomsPossible.getSevereElement(TextAnomalies.TXT_MULT_LINEEND));
		
		// the number of lines CSV parser found.  
		if (super.textStream != null)
			super.textStream.setNumLines(numOfLine);
	}
	/**
	 * validate the CSV file for RFC 4180 conformance
	 * @throws FatalException
	 */
	protected void validate() throws FatalException 
	{
		LineReader linereader= new LineReader(this.getPath());
		
		String lineData;
		HashSet <Integer> columns = new HashSet <Integer> ();
		endedWithComma = false;
		while ((lineData = linereader.readNext()) != null) {
			int numOfColumns = parseRow(lineData, linereader);
			// ignore empty line
			if (numOfColumns > 0)
				columns.add(numOfColumns);
			numOfRows++;
		}

		// check for variable columns, rule 3 & 4 in RFC 4180
		if (columns.size() > 1)
			this.addAnomaly(anomsPossible.getSevereElement(CSVAnomalies.CSV_VARIABLE_COLUMNS));
		
		// rule 4 in RFC 4180: the last column in the row must not be followed by a comma
		if (endedWithComma)
			this.addAnomaly(anomsPossible.getSevereElement(
				CSVAnomalies.CSV_ROW_ENDED_WITH_COMMA));
		
		// the number of rows and columns CSV parser found.  
		if (super.textStream != null) {
			((CSV) super.textStream).setNumOfRows(numOfRows);
			((CSV) super.textStream).setNumOfColumns(columns.toString());
		}
	}
	
	/**
	 * parse the current row (linedata)
	 * @param linedata
	 * @param linereader
	 * @return number of columns in the row
	 * @throws FatalException
	 */
	int parseRow(String linedata, LineReader linereader) throws FatalException {
		int index = 0;
		int numOfColumn = 0;
		boolean inColumn = false;
		int numOfQuotes = 0;
		
		// parsing through every character in the line
		while ((linedata != null) && index < linedata.length()) {
			char ch = linedata.charAt(index);
			
			// see a text qualifier (double quote)
			if (ch == textQualifier) {
				numOfQuotes += 1;
				// indicate the start of a new column
				inColumn = true;
				// read till reaching the end of the column
				while (inColumn && linedata != null) {
					index++;
					// if the column was breaking up into multiple lines, read next line 
					if (index >= linedata.length()) {
						linedata = linereader.readNext();
						index = 0;
					}
					
					// make sure we have not reached the end of file
					if (linedata != null) {
						ch = linedata.charAt(index);
						// see anoter text qualifier (double quote).  This could means either
						// the end of the column or it's an embedded text qualifier
						if (ch == textQualifier) {
							numOfQuotes += 1;
							// normally, this should indicate the end of column,
							inColumn = false;
							// however, we need to handle escaped double quote	
							if (index+1 < linedata.length() && 
									linedata.charAt(index+1) == textQualifier) {
								inColumn = true;
								numOfQuotes += 1;
								// skip the next double quote since we process it already
								index ++; 
							}
						} 
					}
				}
			}
			
			if (ch == columnDelimiter) {
				numOfColumn ++;
			} 
			
			index++;
			// check if this is the end of the line 
			if (linedata != null && index == linedata.length()) {
				numOfColumn++;
				// rule 4 in RFC 4180: the last column in the row must not be followed by a comma
				if (ch == columnDelimiter)
					endedWithComma = true;
			}
		}
		
		// check if text qualifiers are pairred up.  If not, it's likely that
		// double quotes in quoted column are not escaped with another double quote.
		// rule 7 in RFC 4180
		if ((numOfQuotes % 2) != 0)
			this.addAnomaly(anomsPossible.getSevereElement(
					CSVAnomalies.CSV_BAD_ESCAPE_CHAR));
		
		return numOfColumn;
	}
}
