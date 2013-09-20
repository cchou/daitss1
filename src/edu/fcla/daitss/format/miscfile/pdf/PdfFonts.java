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
package edu.fcla.daitss.format.miscfile.pdf;

import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * PdfFonts represents fonts used in Pdfs.  Only record the non standard fonts.
 */
	
public class PdfFonts {
	// an array of all non standard fonts found
	private Vector<PdfFont> nonstandardFonts = new Vector<PdfFont>();  
	
	// Maximum supported length of the fonts list
	private final int MAX_FONTS_LENGTH = 65535; // = 2^16 -1
	
	public void addFont(String _fontName, boolean isEmbedded) {
		// first, check if this is non standard font
		if (!isStandardFont(_fontName)) {
			PdfFont font = null;
			boolean exist = false;
			Iterator iter = nonstandardFonts.iterator();
			// check if we already seen this font
			while (iter.hasNext()){						
				font = (PdfFont) iter.next();
				if (font.getFontName().equals(_fontName)) {
					exist = true;
					break;
				}
			}	
			
			if (!exist) {
				// if this font is not recorded yet, add it to the list
				font = new PdfFont(_fontName, isEmbedded);
				nonstandardFonts.add(font);
			} else if (!font.isEmbedded()) {
				// otherwise, set the embedded flag if it was previous set to unembedded.
				font.setEmbedded(isEmbedded);
			}
		}
	}
	
	/**
	 * Determines if the font is one of the 14 Type 1 fonts known as the
	 * standard fonts. These are the ones that every Pdf reader is
	 * assumed to have access to. Non case-sensitive.
	 * 
	 * @return whether or not the font is one of the standard 14 Type 1 fonts.
	 */
	private boolean isStandardFont(String _fontName){
		String font = _fontName;
		boolean isStandardFont = false;	// true if this is standard PDF font, false, otherwise
		
		font = font.toLowerCase();
		
		if ( // standard font name
			font.equals("times-roman") || font.equals("times-bold") ||
			font.equals("times-italic") || font.equals("times-bolditalic") ||
			font.equals("helvetica") || font.equals("helvetica-bold") ||
			font.equals("helvetica-oblique") || font.equals("helvetica-boldoblique") ||
			font.equals("courier") || font.equals("courier-bold") || 
			font.equals("courier-oblique") || font.equals("courier-boldoblique") ||
			font.equals("symbol") || font.equals("zapfdingbats") ||
			// the alternative font names from p. 1015 of pdf 1.6 spec.
			font.equals("couriernew") || font.equals("couriernew,italic") || 
			font.equals("couriernew,bold") || font.equals("couriernew,bolditalic") ||
			font.equals("arial") || font.equals("arial,italic") ||
			font.equals("arial,bold") || font.equals("arial,bolditalic") ||
			font.equals("timesnewroman") || font.equals("timesnewroman,italic") ||
			font.equals("timesnewroman,bold") || font.equals("timesnewroman,bolditalic"))
		{
		
			isStandardFont = true;
		}
		return isStandardFont;
	}
	
	/**
	 * return a comma delimited string which contains all the nonstandard unembedded fonts used in the PDF
	 * @return String
	 */
	public String getNonstandardUnEmbedded() throws FatalException  {
		String fontList = null;
		PdfFont font = null;
		
		Iterator iter = nonstandardFonts.iterator();
		while (iter.hasNext()){			
			font = (PdfFont) iter.next();
			if (!font.isEmbedded()) {
				if (fontList == null)
					fontList = font.getFontName();
				else
					fontList = fontList + ", " +  font.getFontName();
			}
		}
		// make sure it is less than the maximum allowed length
		if (fontList != null && fontList.length() > MAX_FONTS_LENGTH) {
            Informer.getInstance().warning(this,"getNonstandardUnEmbedded",
            	"the list of nonstandard unembedded fonts exceeds the maximum", 
            	"Max lenght = "+ MAX_FONTS_LENGTH);
			fontList = fontList.substring(0, MAX_FONTS_LENGTH-1);	
		}
		return fontList;
	}
	
	/**
	 * return a comma delimited string which contains all the nonstandard embedded fonts used in the PDF
	 * @return String
	 */
	public String getNonstandardEmbedded() throws FatalException {
		String fontList = null;
		PdfFont font = null;
		
		Iterator iter = nonstandardFonts.iterator();
		while (iter.hasNext()){			
			font = (PdfFont) iter.next();
			if (font.isEmbedded()) {
				if (fontList == null)
					fontList = font.getFontName();
				else
					fontList = fontList + ", " +  font.getFontName();
			}
		}
		// make sure it is less than the maximum allowed length
		if (fontList != null && fontList.length() > MAX_FONTS_LENGTH) {
	        Informer.getInstance().warning(this,"getNonstandardEmbedded",
	           "the list of nonstandard embedded fonts exceeds the maximum", 
	           "Max lenght = "+ MAX_FONTS_LENGTH);
			fontList = fontList.substring(0, MAX_FONTS_LENGTH-1);
		}
		return fontList;
	}
}
