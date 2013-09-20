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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import edu.fcla.daitss.bitstream.Link;

/**
 * Base class for the various classes performing io operations.
 * 
 */
public abstract class DTDSource {

    /**
     * Method to perform the read operation on this source.
     * 
     * @return The DTDData object containing all the read data from the current
     *         source.
     * 
     * @throws FileNotFoundException When the dtd source could not be found.
     * @throws IOException When an IO Exception happens while reading the source
     */
    public abstract DTDTextParser read() throws FileNotFoundException, IOException;

    /**
     * Determines the link type.
     * 
     * @param linkAlias link as it appears in the parent file
     * @param parentPath absolute path to parent file
     * @return the link type
     * @throws URISyntaxException
     */
    public static String determineLinkType(String linkAlias, String parentPath) 
    	throws URISyntaxException {
        String type = null;
        URI u = null;
        URI alias = null;
        URI parentUri = new URI(parentPath);

        // Construct a url of the link.
        String absWinPattern = "([a-zA-Z]:|\\\\)?(\\\\[\\w\\s\\.]+)+";
        String relWinPattern = "[\\w\\s\\.]+(\\\\[\\w\\s\\.]+)+";

        if (linkAlias.matches(absWinPattern)) {
            // Absolute windows path
            type = Link.TYPE_ABS_PATH;
        } else if (linkAlias.matches(relWinPattern)) {
            // Relative windows path
            type = Link.TYPE_REL_PATH;
        } else {
            try {
                alias = new URI(linkAlias);
                u = parentUri.resolve(alias);

                if (u.getScheme() != null && u.getScheme().matches("https?")) {
                    type = Link.TYPE_HTTP_URL;
                } else if (alias.isAbsolute()
                        && alias.getScheme().equals("file")
                        || alias.getPath().startsWith("/")) {
                    // Absolute Path
                    type = Link.TYPE_ABS_PATH;
                } else if (!alias.isAbsolute() && alias.getQuery() == null) {
                    // Relative Path
                    type = Link.TYPE_REL_PATH;
                } else {
                    // Unknown
                    type = Link.TYPE_UNKNOWN;
                }
            } catch (URISyntaxException e) {
                type = Link.TYPE_UNKNOWN;
            }
        }
        
        return type;
    }
}

