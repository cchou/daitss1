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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import edu.fcla.da.xml.WebCache;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;


/**
 * Class to do the io operations for a URL.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class DTDUrl extends DTDSource {

	/**
	 * The URL which is the source of the data.
	 */
	private URL url;
	private WebCache webCache;
	
	/**
	 * DTDUrl Constructor.
	 * 
	 * @param url The location of the dtd.
	 * @throws MalformedURLException
	 * @throws FatalException 
	 */
	public DTDUrl(String url) throws MalformedURLException, FatalException {
		
		this.url = new URL(url);
		
    	File webCacheDir = new File(ArchiveProperties.getInstance().getArchProperty("WEBCACHE_DIR"));
        try {
			webCache = new WebCache(webCacheDir);
		} catch (IOException e) {
			Informer.getInstance().fail("cannot initialize webcache", "constructing DTDUrl", e);
		}

	}

	/**
	 * Method to perform the read operation on this url source.
	 * 
	 * @return The DTDData object containing all the read data
	 * 			from the current source.
	 * 
	 * @throws IOException If the url resource could not be read.
	 */
	public DTDTextParser read() throws IOException {
		
		try {
			//BufferedReader in = new BufferedReader( new InputStreamReader(webCache.get(url)));
			
			URLConnection connection = url.openConnection(); 
            
			BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream())); 
			String inputLine;
			StringBuffer dtdStr = new StringBuffer(""); 
	
			while ((inputLine = in.readLine()) != null) { 
				dtdStr.append(inputLine); 
			}
			in.close(); 
			
			return new DTDTextParser(dtdStr.toString());
			
		} catch(IOException ie) {
			throw new IOException("Error reading url - " + url.toString());
		}
	}
}
