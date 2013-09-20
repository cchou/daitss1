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
 * Created on Jan 14, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * 
 * PdfAnomalies represents anomalies that are specific to PDF files.
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 */
public class PdfAnomalies extends Anomalies {
	
	/**
	 * Bad Action Dictionary (doesn't have something that's required)
	 */
	public static final String PDF_BAD_ACTION_DICT = 
		"A_PDF_BAD_ACTION_DICT";
		
	/**
	 * Bad Catalog Dictionary (doesn't have something that's required)
	 */
	public static final String PDF_BAD_CATALOG_DICT = 
		"A_PDF_BAD_CATALOG_DICT";
		
	/**
	 * Bad Pdf date format
	 */
	public static final String PDF_BAD_DATE_OBJECT = 
		"A_PDF_BAD_DATE_OBJECT";
		
	/**
	 * Unknown format exception (cover all of pjx's PdfFormatException)
	 */
	public static final String PDF_BAD_FORMAT = "A_PDF_BAD_FORMAT";
	
	/**
	 * no pdf page
	 */
	public static final String PDF_NO_PAGE = "A_PDF_NO_PAGE";
	/**
	 * Bad mark information dictionary
	 */
	public static final String PDF_BAD_MARKINFO_DICT = 
		"A_PDF_BAD_MARKINFO_DICT";
		
	/**
	 * Illegal PDF name object
	 */
	public static final String PDF_BAD_NAME_OBJECT = "A_PDF_BAD_NAME_OBJECT";
	
	/**
	 * Bad page layout value
	 */
	public static final String PDF_BAD_PAGELAYOUT = 
		"A_PDF_BAD_PAGELAYOUT";
		
	/**
	 * Bad page mode value
	 */
	public static final String PDF_BAD_PAGEMODE = 
		"A_PDF_BAD_PAGEMODE";
		
	/**
	 * Bad page mode value
	 */
	public static final String PDF_BAD_PAGETREE = "A_PDF_BAD_PAGETREE";
	
	/**
	 * invalid Prev value in trailer dictionary
	 */
	public static final String PDF_BAD_PREV_VALUE = 
		"A_PDF_BAD_PREV_VALUE";
		
	/**
	 * invalid Size value in trailer dictionary
	 */
	public static final String PDF_BAD_SIZE_VALUE = "A_PDF_BAD_SIZE_VALUE";
	
	/**
	 * cross-reference and/or trailer dictionary not found
	 */
	public static final String PDF_BAD_STREAM_LENGTH = 
		"A_PDF_BAD_STREAM_LENGTH";
		
	/**
	 * Illegal PDF string object
	 */
	public static final String PDF_BAD_STRING_OBJECT = 
		"A_PDF_BAD_STRING_OBJECT";
		
	/**
	 * Object not found at an offset where it's supposed to be
	 */
	public static final String PDF_NO_OBJECT = "A_PDF_NO_OBJECT";
	
	/**
	 * No Root keyword in the trailer dictionary (it's required)
	 */
	public static final String PDF_NO_ROOT = "A_PDF_NO_ROOT";
	
	/**
	 * no startxref keyword
	 */
	public static final String PDF_NO_STARTXREF = "A_PDF_NO_STARTXREF";
	
	/**
	 * no trailer in last 1024 bytes
	 */
	public static final String PDF_NO_TRAILER = "A_PDF_NO_TRAILER";
	
	/**
	 * cross-reference dictionary in wrong location
	 */
	public static final String PDF_NO_XREF = "A_PDF_NO_XREF";
	
	/**
	 * cross-reference and/or trailer dictionary not found
	 */
	public static final String PDF_NO_XREF_OR_TRAILER = 
		"A_PDF_NO_XREF_OR_TRAILER";
		
	/**
	 * cross-reference dictionary in wrong location
	 */
	public static final String PDF_NO_XREF_SUBSECTION = 
		"A_PDF_NO_XREF_SUBSECTION";
		
	/**
	 * too large a number of pages for database
	 */
	public static final String PDF_OVRLMT_NUM_PAGES = 
		"A_PDF_OVRLMT_NUM_PAGES";
		
	/**
	 * Unrecognized action type
	 */
	public static final String PDF_UNKNOWN_ACTION = 
		"A_PDF_UNKNOWN_ACTION";
	
	/**
	 * Unrecognized annotation type
	 */
	public static final String PDF_UNKNOWN_ANNOTATION = 
		"A_PDF_UNKNOWN_ANNOTATION";
	
	/**
	 * Unrecognized filter type
	 */
	public static final String PDF_UNKNOWN_FILTER = 
		"A_PDF_UNKNOWN_FILTER";
		
	/**
	 * Unrecognized PDF Object
	 */
	public static final String PDF_UNKNOWN_OBJECT = 
		"A_PDF_UNKNOWN_OBJECT";
		
	/**
	 * Wrong Pdf data type
	 */
	public static final String PDF_WRONG_TYPE = "A_PDF_WRONG_TYPE";

	/**
	 * Test driver
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//PdfAnomalies ta = new PdfAnomalies();
	}

	/**
	 * Builds the list of known Pdf-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public PdfAnomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known Pdf-specific anomalies.
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
		//	Bad Action Dictionary (doesn't have something that's required)
		insert(PDF_BAD_ACTION_DICT,
			Severity.SEVERITY_NOTE,
			"Badly formed action dictionary");
		//	Bad Catalog Dictionary (doesn't have something that's required)
		insert(PDF_BAD_CATALOG_DICT,
			Severity.SEVERITY_NOTE,
			"Badly formed catalog dictionary.");
		//	Bad Pdf date format
		insert(PDF_BAD_DATE_OBJECT,
			Severity.SEVERITY_NOTE,
			"Illegal date object.");
		//	Unknown format exception (cover all of pjx's PdfFormatException)
		insert(PDF_BAD_FORMAT,
			Severity.SEVERITY_NOTE,
			"Badly formatted.");
		//	Bad mark information dictionary
		insert(PDF_BAD_MARKINFO_DICT,
			Severity.SEVERITY_NOTE,
			"Badly formed mark info dictionary.");
		//	Illegal PDF name object
		insert(PDF_BAD_NAME_OBJECT,
			Severity.SEVERITY_NOTE,
			"Illegal name object.");
		//	Bad page layout value
		insert(PDF_BAD_PAGELAYOUT,
			Severity.SEVERITY_NOTE,
			"Not a valid page layout option.");
		//	Bad page mode value
		insert(PDF_BAD_PAGEMODE,
			Severity.SEVERITY_NOTE,
			"Not a valid page mode option.");
		//	Bad page tree 
		insert(PDF_BAD_PAGETREE,
			Severity.SEVERITY_NOTE,
			"Malformed or cyclical page tree.");
		//	invalid Prev value in trailer dictionary
		insert(PDF_BAD_PREV_VALUE,
			Severity.SEVERITY_NOTE,
			"Invalid Prev value in the trailer dictionary.");
		//	invalid Size value in trailer dictionary
		insert(PDF_BAD_SIZE_VALUE,
			Severity.SEVERITY_NOTE,
			"Invalid Size value in trailer dictionary.");
		//	cross-reference and/or trailer dictionary not found
		insert(PDF_BAD_STREAM_LENGTH,
			Severity.SEVERITY_NOTE,
			"Invalid Length value in stream dictionary.");
		//	Illegal PDF string object
		insert(PDF_BAD_STRING_OBJECT,
			Severity.SEVERITY_NOTE,
			"Illegal string object.");
		//	Object not found at an offset where it's supposed to be
		insert(PDF_NO_OBJECT,
			Severity.SEVERITY_NOTE,
			"An object not found.");
		//	No PDF page
		insert(PDF_NO_PAGE,
			Severity.SEVERITY_NOTE,
			"There is no pdf page in this PDF file.");
		//	No Root keyword in the trailer dictionary (it's required)
		insert(PDF_NO_ROOT,
			Severity.SEVERITY_NOTE,
			"No Root keyword in trailer dictionary.");
		//	no startxref keyword
		insert(PDF_NO_STARTXREF,
			Severity.SEVERITY_NOTE,
			"No startxref keyword.");
		// no trailer in last 1024 bytes
		insert(PDF_NO_TRAILER,
			Severity.SEVERITY_NOTE,
			"No trailer in last 1024 bytes.");
		//	cross-reference dictionary in wrong location
		insert(PDF_NO_XREF,
			Severity.SEVERITY_NOTE,
			"Cross-reference dictionary not found.");
		//	cross-reference and/or trailer dictionary not found
		insert(PDF_NO_XREF_OR_TRAILER,
			Severity.SEVERITY_NOTE,
			"The cross-reference and/or trailer dictionary not found.");
		//	cross-reference dictionary in wrong location
		insert(PDF_NO_XREF_SUBSECTION,
			Severity.SEVERITY_NOTE,
			"A cross-reference sub-section not found.");
		//	too large a number of pages for database
		insert(PDF_OVRLMT_NUM_PAGES,
			Severity.SEVERITY_NOTE,
			"Exceeds data size limit for NUM_PAGES.");
		// Unrecognized action type
		insert(PDF_UNKNOWN_ACTION,
			Severity.SEVERITY_NOTE,
			"Lists an unknown action type");
		// Unrecognized annotation type
		insert(PDF_UNKNOWN_ANNOTATION,
			Severity.SEVERITY_NOTE,
			"Lists an unknown annotation type");
		// Unrecognized filter type
		insert(PDF_UNKNOWN_FILTER,
			Severity.SEVERITY_NOTE,
			"Lists an unknown filter type");
		//	Unrecognized PDF Object
		insert(PDF_UNKNOWN_OBJECT,
			Severity.SEVERITY_NOTE,
			"Not a known PDF object type.");
		//	Wrong Pdf data type
		insert(PDF_WRONG_TYPE,
			Severity.SEVERITY_NOTE,
			"Not the right data type for that key/object.");
	}
	
	/**
	 * Given the message from a com.etymon.pjx.PdfFormatEception
	 * the corresponding daitts PDF anomaly name is returned. Always
	 * returns a valid PDF anomaly name.
	 * 
	 * @param mesg the exception message
	 * @return	the name of the PDF anomaly
	 */
	public static String interpretPdfFormatExceptionMessage(String mesg){
		String anomName = null;
		
		if (mesg == null) { // just in case - can't control pjx code
			anomName = PdfAnomalies.PDF_BAD_FORMAT; 
		}else if (mesg.equals("PDF startxref not found.")){
			anomName = PdfAnomalies.PDF_NO_STARTXREF;
		} else if (mesg.equals("Cross-reference table or trailer not found at correct position.")){
			anomName = PdfAnomalies.PDF_NO_XREF_OR_TRAILER;
		} else if (mesg.equals("Trailer dictionary not found.")){
			anomName = PdfAnomalies.PDF_NO_TRAILER;
		} else if (mesg.equals("Valid Prev value not found in trailer dictionary.")){
			anomName = PdfAnomalies.PDF_BAD_PREV_VALUE;
		} else if (mesg.equals("Valid xref size not found in trailer dictionary.")){
			anomName = PdfAnomalies.PDF_BAD_SIZE_VALUE;
		} else if (mesg.equals("Cross-reference table (xref) not found at correct position.")){
			anomName = PdfAnomalies.PDF_NO_XREF;
		} else if (mesg.equals("Cross-reference table (subsection) not found.")){
			anomName = PdfAnomalies.PDF_NO_XREF_SUBSECTION;
		} else if (mesg.equals("Object not found.")){
			anomName = PdfAnomalies.PDF_NO_OBJECT;
		} else if (mesg.equals("Valid Length value not found in stream dictionary.")){
			anomName = PdfAnomalies.PDF_BAD_STREAM_LENGTH;
		} else if (mesg.equals("Object not recognized.")){
			anomName = PdfAnomalies.PDF_UNKNOWN_OBJECT;
		} else if (mesg.equals("Page tree root (Pages) is not a dictionary.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page tree (Pages) is not a dictionary.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page count is not an integer or reference.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page count is not an integer.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Catalog is not a dictionary.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page tree root (Pages) is not an indirect reference.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Kids object is not an array or reference.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Kids object is not an array.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Kids element is not a reference.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Kids element is not a dictionary.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page node type is not a name or reference.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page node type is not a name.")){
			anomName = PdfAnomalies.PDF_WRONG_TYPE;
		} else if (mesg.equals("Page tree contains a cycle (must be acyclic).")){
			anomName = PdfAnomalies.PDF_BAD_PAGETREE;
		} else if (mesg.equals("Requested page not found.")){
			anomName = PdfAnomalies.PDF_BAD_PAGETREE;
		} else { // just in case we missed a reason why pjx would throw a
			// PdfFormatException
			anomName = PdfAnomalies.PDF_BAD_FORMAT;
		}
		return anomName;
	}
}
