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
package edu.fcla.daitss.format.image.jpeg2000.box;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.image.jpeg2000.Jp2;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.util.ByteReader;

public class Ipr extends Box {

	/**
	 * box type value;
	 */
	public static final int TYPE = 0x6A703269;
	
	public Ipr(Box box) {
		super(box, Ipr.TYPE);
	}

	/**		
	 * 
	 * @param reader
	 * @param df
	 * @throws FatalException
	 */
	public void extractMetadata(ByteReader reader, Jpeg2000 df) 
		throws FatalException {
		boolean keepParsing = true;
		
		System.out.println("JP2I");
        
        df.addFormatAttribute(Jpeg2000.Attribute.HAS_IPR_MD);
        
		reader.skipBytes((int)this.getContentLength());
	}
}
