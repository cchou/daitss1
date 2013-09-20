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

/**
 * PdfFonts represents a font in PDF.  It contains font attributes that will be recorded.
 */
public class PdfFont {
	private String 	fontName;
	private boolean embedded;
	
	public PdfFont(String _fontName, boolean _embedded) {
		fontName = _fontName;
		embedded = _embedded;
	}
	
	/**
	 * get the name of the font
	 * @return fontName
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * Whether the font is embedded.
	 * @return True - embedded, False - unembedded
	 */
	public boolean isEmbedded() {
		if (embedded)
			return true;
		else
			return false;
	}
	
	/**
	 * Set the embedded flag of the font
	 * @param _embedded
	 */
	public void setEmbedded(boolean _embedded) {
		embedded = _embedded;
	}
}
