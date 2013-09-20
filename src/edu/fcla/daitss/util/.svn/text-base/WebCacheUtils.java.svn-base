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
/**
 * 
 */
package edu.fcla.daitss.util;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.Regexp;

import edu.fcla.da.xml.WebCache;
import edu.fcla.da.xml.WebCacheResolver;
import edu.fcla.daitss.exception.FatalException;

/**
 * @author franco
 *
 */
public class WebCacheUtils {

	/**
	 * @return
	 * @throws FatalException
	 */
	public static File getWebCacheDir() throws FatalException {
		String path = ArchiveProperties.getInstance().getArchProperty("WEBCACHE_DIR");
		return new File(path);
	}
	
	
	
	/**
	 * @return
	 * @throws FatalException 
	 */
	public static Map<String, String> getLocationsMap() throws FatalException {

    	// "name" "value", "name" "value"
    	String property = ArchiveProperties.getInstance().getArchProperty("RESOLVER_LOCATIONS");
    	Map<String, String> locations = new Hashtable<String, String>();
    	
    	Pattern mapPattern = Pattern.compile("^\"(.*)\"\\s+\"(.*)\"$");
    	
    	for (String pair : property.trim().split("(?<=\")\\s*,\\s*(?=\")")) {
    		
    		Matcher matcher = mapPattern.matcher(pair);
    		if (matcher.matches()) {
        		String publicId = matcher.group(1);
        		String systemId = matcher.group(2);
        		locations.put(publicId, systemId);    			
    		} else {
    			Informer.getInstance().fail(
    					"proper format: \"name\" \"value\", \"name\" \"value\", entry is: " + pair, 
    					"Archive Property RESOLVER_LOCATIONS is not properly formed", 
    					new FatalException());
    		}
    		
    	}
		
		return locations;
	}



	/**
	 * @return
	 * @throws FatalException 
	 */
	public static WebCacheResolver getResolver() throws FatalException {

		WebCacheResolver resolver = null;
        try {
			resolver = new WebCacheResolver(getWebCacheDir(), getLocationsMap());
		} catch (IOException e) {
			Informer.getInstance().fail("cannot initialize webcache", "getting webcache", e);
		}
		return resolver;
	}

}
