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
 * Created on Feb 12, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

/**
 * PdfAnnotation represents all the annotation types specified
 * in the PDF 1.4 specification (p. 499).
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class PdfAnnotation {
	
	/**
	 * Circle annotation (PDF 1.3)
	 */
	public static final String CIRCLE = "Circle";
	
	/**
	 * File attachment annotation (PDF 1.3)
	 */
	public static final String FILE_ATTACHMENT = "FileAttachment";
	
	/**
	 * Free text annotation (PDF 1.3)
	 */
	public static final String FREE_TEXT = "FreeText";
	
	/**
	 * Highlight annotation (PDF 1.3)
	 */
	public static final String HIGHLIGHT = "Highlight";
	
	/**
	 * Ink annotation (PDF 1.3)
	 */
	public static final String INK = "Ink";
	
	/**
	 * Line annotation (PDF 1.3)
	 */
	public static final String LINE = "Line";
	
	/**
	 * Link annotation
	 */
	public static final String LINK = "Link";
	
	/**
	 * Movie annotation (PDF 1.2)
	 */
	public static final String MOVIE = "Movie";
	
	/**
	 * Popup annotation (PDF 1.3)
	 */
	public static final String POPUP = "Popup";
	
	/**
	 * Printer mark annotation (PDF 1.4)
	 */
	public static final String PRINTER_MARK = "PrinterMark";
	
	/**
	 * Sound annotation (PDF 1.2)
	 */
	public static final String SOUND = "Sound";
	
	/**
	 * Square annotation (PDF 1.3)
	 */
	public static final String SQUARE = "Square";
	
	/**
	 * Squiggly annotation (PDF 1.4)
	 */
	public static final String SQUIGGLY = "Squiggly";
	
	/**
	 * Stamp annotation (PDF 1.3)
	 */
	public static final String STAMP = "Stamp";
	
	/**
	 * Strike out annotation (PDF 1.3)
	 */
	public static final String STRIKE_OUT = "StrikeOut";
	
	/**
	 * Text annotation
	 */
	public static final String TEXT = "Text";
	
	/**
	 * Trap net annotation (PDF 1.3)
	 */
	public static final String TRAP_NET = "TrapNet";
	
	/**
	 * Underline annotation (PDF 1.3)
	 */
	public static final String UNDERLINE = "Underline";
	
	/**
	 * Widget annotation (PDF 1.2)
	 */
	public static final String WIDGET = "Widget";
	
	/**
	 * Polygon annotation (PDF 1.5)
	 */
	public static final String POLYGON = "Polygon";
	
	/**
	 * Polyline annotation (PDF 1.5)
	 */
	public static final String POLYLINE = "PolyLine";
	
	/**
	 * Screen annotation (PDF 1.5)
	 */
	public static final String SCREEN = "Screen";
	
	/**
	 * Caret annotation (PDF 1.5)
	 */
	public static final String CARET = "Caret";
	
	/**
	 * Watermark annotation (PDF 1.6)
	 */
	public static final String WATERMARK = "Watermark";
	
	/**
	 * 3D annotation (PDF 1.6)
	 */
	public static final String THREE_D = "3D";
	
	/**
	 * Determines whether or not an annotation type is considered to be
	 * a valid one (specified in the PDF 1.6 spec.).
	 * 
	 * @param type	the annotation type 
	 * @return	<code>true</code> if the type is valid, else
	 * 	<code>false</code>.
	 */
	public static boolean isValidType (String type){
		boolean isValid = false;
		
		if (type != null && 
			(type.equals(CIRCLE)) || type.equals(FILE_ATTACHMENT)
				|| type.equals(FREE_TEXT) || type.equals(HIGHLIGHT)
				|| type.equals(INK) || type.equals(LINE)
				|| type.equals(LINK) || type.equals(MOVIE)
				|| type.equals(POPUP) || type.equals(PRINTER_MARK)
				|| type.equals(SOUND) || type.equals(SQUARE)
				|| type.equals(SQUIGGLY) || type.equals(STAMP)
				|| type.equals(STRIKE_OUT) || type.equals(TEXT)
				|| type.equals(TRAP_NET) || type.equals(UNDERLINE)
				|| type.equals(WIDGET) || type.equals(POLYGON)
				|| type.equals(POLYLINE) || type.equals(CARET)
				|| type.equals(SCREEN) || type.equals(WATERMARK)
				|| type.equals(THREE_D)
			){
				isValid = true;
			}
		
		return isValid;
	}

}
