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
 * Created on Sep 10, 2004
 *
 */
package edu.fcla.daitss.format.markup;

import java.util.Hashtable;
import java.util.Map;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author franco
 * 
 * Resolve prefixes used in the xpaths in daitss
 */
public class SimplePrefixResolver implements PrefixResolver {

    private Map nsTable;

    /**
     * Default constructor.
     */
    public SimplePrefixResolver() {
        nsTable = new Hashtable();
    }

    /**
     * 
     * @param nsMap
     */
    public SimplePrefixResolver(Map nsMap) {
        if (nsMap != null) {
            nsTable = new Hashtable(nsMap);
        } else {
            nsTable = new Hashtable();
        }
    }

    /**
     * Insert a prefix into the resolver.
     * 
     * @param prefix
     * @param namespace
     */
    public void addPrefix(String prefix, String namespace) {
        nsTable.put(prefix, namespace);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#handlesNullPrefixes()
     */
    public boolean handlesNullPrefixes() {
        // Until further notice.
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#getBaseIdentifier()
     */
    public String getBaseIdentifier() {
        // Base identifier is unknown at this time.
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String)
     */
    public String getNamespaceForPrefix(String prefix) {
        String ns = (String) nsTable.get(prefix);
        return ns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String,
     *      org.w3c.dom.Node)
     */
    public String getNamespaceForPrefix(String prefix, Node context) {

        // Temporary namespace table
        Hashtable cNsTable = new Hashtable();

        // Get all the xmlns:prefix="namespace-uri" attributes.
        NamedNodeMap attributes = context.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr a = (Attr) attributes.item(i);
            if (a.getName().startsWith("xmlns")) {
                // Get the context's prefix.
                String name = a.getName();
                int prefixIndex = name.indexOf(':') + 1;
                String cPrefix = name.substring(prefixIndex);

                // Get the namespace
                String cNamespace = a.getValue();

                // Insert the pait into a nstable
                cNsTable.put(cPrefix, cNamespace);
            }
        }

        // Lookup the namespace in the generated table
        String namespace = (String) cNsTable.get(prefix);
        return namespace;
    }
}