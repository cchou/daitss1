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

package edu.fcla.daitss.format.markup;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * Filter to write an XML document from a SAX event stream.
 * This class is derived from David Megginson's
 * org.xml.sax.XMLFilter which is in the public domain.
 * 
 * David Megginson's class documentation follows:
 * 
 * <p>
 * This class can be used by itself or as part of a SAX event stream: it takes
 * as input a series of SAX2 ContentHandler events and uses the information in
 * those events to write an XML document. Since this class is a filter, it can
 * also pass the events on down a filter chain for further processing (you can
 * use the XMLWriter to take a snapshot of the current state at any point in a
 * filter chain), and it can be used directly as a ContentHandler for a SAX2
 * XMLReader.
 * </p>
 * 
 * <p>
 * The client creates a document by invoking the methods for standard SAX2
 * events, always beginning with the {@link #startDocument startDocument}method
 * and ending with the {@link #endDocument endDocument}method. There are
 * convenience methods provided so that clients to not have to create empty
 * attribute lists or provide empty strings as parameters; for example, the
 * method invocation
 * </p>
 * 
 * <pre>
 * w.startElement(&quot;foo&quot;);
 * </pre>
 * 
 * <p>
 * is equivalent to the regular SAX2 ContentHandler method
 * </p>
 * 
 * <pre>
 * w.startElement(&quot;&quot;, &quot;foo&quot;, &quot;&quot;, new AttributesImpl());
 * </pre>
 * 
 * <p>
 * Except that it is more efficient because it does not allocate a new empty
 * attribute list each time. The following code will send a simple XML document
 * to standard output:
 * </p>
 * 
 * <pre>
 * XMLWriter w = new XMLWriter();
 * 
 * w.startDocument();
 * w.startElement(&quot;greeting&quot;);
 * w.characters(&quot;Hello, world!&quot;);
 * w.endElement(&quot;greeting&quot;);
 * w.endDocument();
 * </pre>
 * 
 * <p>
 * The resulting document will look like this:
 * </p>
 * 
 * <pre>
 * 
 *  &lt;?xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;
 * 
 *  &lt;greeting&gt;Hello, world!&lt;/greeting&gt;
 *  
 * </pre>
 * 
 * <p>
 * In fact, there is an even simpler convenience method, <var>dataElement
 * </var>, designed for writing elements that contain only character data, so
 * the code to generate the document could be shortened to
 * </p>
 * 
 * <pre>
 * XMLWriter w = new XMLWriter();
 * 
 * w.startDocument();
 * w.dataElement(&quot;greeting&quot;, &quot;Hello, world!&quot;);
 * w.endDocument();
 * </pre>
 * 
 * <h2>Whitespace</h2>
 * 
 * <p>
 * According to the XML Recommendation, <em>all</em> whitespace in an XML
 * document is potentially significant to an application, so this class never
 * adds newlines or indentation. If you insert three elements in a row, as in
 * </p>
 * 
 * <pre>
 * w.dataElement(&quot;item&quot;, &quot;1&quot;);
 * w.dataElement(&quot;item&quot;, &quot;2&quot;);
 * w.dataElement(&quot;item&quot;, &quot;3&quot;);
 * </pre>
 * 
 * <p>
 * you will end up with
 * </p>
 * 
 * <pre>
 * 
 *  &lt;item&gt;1&lt;/item&gt;&lt;item&gt;3&lt;/item&gt;&lt;item&gt;3&lt;/item&gt;
 *  
 * </pre>
 * 
 * <p>
 * You need to invoke one of the <var>characters </var> methods explicitly to
 * add newlines or indentation. Alternatively, you can use
 * com.megginson.sax.DataWriter, which is derived from this
 * class -- it is optimized for writing purely data-oriented (or field-oriented)
 * XML, and does automatic linebreaks and indentation (but does not support
 * mixed content properly).
 * </p>
 * 
 * 
 * <h2>Namespace Support</h2>
 * 
 * <p>
 * The writer contains extensive support for XML Namespaces, so that a client
 * application does not have to keep track of prefixes and supply <var>xmlns
 * </var> attributes. By default, the XML writer will generate Namespace
 * declarations in the form _NS1, _NS2, etc., wherever they are needed, as in
 * the following example:
 * </p>
 * 
 * <pre>
 * w.startDocument();
 * w.emptyElement(&quot;http://www.foo.com/ns/&quot;, &quot;foo&quot;);
 * w.endDocument();
 * </pre>
 * 
 * <p>
 * The resulting document will look like this:
 * </p>
 * 
 * <pre>
 * 
 *  &lt;?xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;
 * 
 *  &lt;_NS1:foo xmlns:_NS1=&quot;http://www.foo.com/ns/&quot;/&gt;
 *  
 * </pre>
 * 
 * <p>
 * In many cases, document authors will prefer to choose their own prefixes
 * rather than using the (ugly) default names. The XML writer allows two methods
 * for selecting prefixes:
 * </p>
 * 
 * <ol>
 * <li>the qualified name</li>
 * <li>the {@link #setPrefix setPrefix}method.</li>
 * </ol>
 * 
 * <p>
 * Whenever the XML writer finds a new Namespace URI, it checks to see if a
 * qualified (prefixed) name is also available; if so it attempts to use the
 * name's prefix (as long as the prefix is not already in use for another
 * Namespace URI).
 * </p>
 * 
 * <p>
 * Before writing a document, the client can also pre-map a prefix to a
 * Namespace URI with the setPrefix method:
 * </p>
 * 
 * <pre>
 * w.setPrefix(&quot;http://www.foo.com/ns/&quot;, &quot;foo&quot;);
 * w.startDocument();
 * w.emptyElement(&quot;http://www.foo.com/ns/&quot;, &quot;foo&quot;);
 * w.endDocument();
 * </pre>
 * 
 * <p>
 * The resulting document will look like this:
 * </p>
 * 
 * <pre>
 * 
 *  &lt;?xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;
 * 
 *  &lt;foo:foo xmlns:foo=&quot;http://www.foo.com/ns/&quot;/&gt;
 *  
 * </pre>
 * 
 * <p>
 * The default Namespace simply uses an empty string as the prefix:
 * </p>
 * 
 * <pre>
 * w.setPrefix(&quot;http://www.foo.com/ns/&quot;, &quot;&quot;);
 * w.startDocument();
 * w.emptyElement(&quot;http://www.foo.com/ns/&quot;, &quot;foo&quot;);
 * w.endDocument();
 * </pre>
 * 
 * <p>
 * The resulting document will look like this:
 * </p>
 * 
 * <pre>
 * 
 *  &lt;?xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;
 * 
 *  &lt;foo xmlns=&quot;http://www.foo.com/ns/&quot;/&gt;
 *  
 * </pre>
 * 
 * <p>
 * By default, the XML writer will not declare a Namespace until it is actually
 * used. Sometimes, this approach will create a large number of Namespace
 * declarations, as in the following example:
 * </p>
 * 
 * <pre>
 * 
 *  &lt;xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;
 * 
 *  &lt;rdf:RDF xmlns:rdf=&quot;http://www.w3.org/1999/02/22-rdf-syntax-ns#&quot;&gt;
 *   &lt;rdf:Description about=&quot;http://www.foo.com/ids/books/12345&quot;&gt;
 *    &lt;dc:title xmlns:dc=&quot;http://www.purl.org/dc/&quot;&gt;A Dark Night&lt;/dc:title&gt;
 *    &lt;dc:creator xmlns:dc=&quot;http://www.purl.org/dc/&quot;&gt;Jane Smith&lt;/dc:title&gt;
 *    &lt;dc:date xmlns:dc=&quot;http://www.purl.org/dc/&quot;&gt;2000-09-09&lt;/dc:title&gt;
 *   &lt;/rdf:Description&gt;
 *  &lt;/rdf:RDF&gt;
 *  
 * </pre>
 * 
 * <p>
 * The "rdf" prefix is declared only once, because the RDF Namespace is used by
 * the root element and can be inherited by all of its descendants; the "dc"
 * prefix, on the other hand, is declared three times, because no higher element
 * uses the Namespace. To solve this problem, you can instruct the XML writer to
 * predeclare Namespaces on the root element even if they are not used there:
 * </p>
 * 
 * <pre>
 * w.forceNSDecl(&quot;http://www.purl.org/dc/&quot;);
 * </pre>
 * 
 * <p>
 * Now, the "dc" prefix will be declared on the root element even though it's
 * not needed there, and can be inherited by its descendants:
 * </p>
 * 
 * <pre>
 * 
 *  &lt;xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;
 * 
 *  &lt;rdf:RDF xmlns:rdf=&quot;http://www.w3.org/1999/02/22-rdf-syntax-ns#&quot;
 *              xmlns:dc=&quot;http://www.purl.org/dc/&quot;&gt;
 *   &lt;rdf:Description about=&quot;http://www.foo.com/ids/books/12345&quot;&gt;
 *    &lt;dc:title&gt;A Dark Night&lt;/dc:title&gt;
 *    &lt;dc:creator&gt;Jane Smith&lt;/dc:title&gt;
 *    &lt;dc:date&gt;2000-09-09&lt;/dc:title&gt;
 *   &lt;/rdf:Description&gt;
 *  &lt;/rdf:RDF&gt;
 *  
 * </pre>
 * 
 * <p>
 * This approach is also useful for declaring Namespace prefixes that be used by
 * qualified names appearing in attribute values or character data.
 * </p>
 * 
 * @author David Megginson, david@megginson.com
 * @version 0.2
 * @see org.xml.sax.XMLFilter
 * @see org.xml.sax.ContentHandler
 */
public class XMLWriterFilter extends XMLFilterImpl {
    
	/** 
	 * Locator that is used to find the location within the XML
	 * file where events occurred, and also to identify the public and/or system ID
	 * of this file. To supply a Locator is not a requirement of SAX
	 * parsers, so there is no guarantee that this Locator will be
	 * initialized in the setLocator(Locator) method.
	 */
	private Locator docLocator = null;

    /**
     * A mapping of namespace URI to namespace prefixes.
     */
    private Hashtable doneDeclTable;

    /**
     * The element nesting depth.
     */
    private int elementLevel = 0;

    /**
     * An empty attribute list.
     */
    private final Attributes EMPTY_ATTS = new AttributesImpl();

    /**
     * A mapping of namespace URIS to whether or not 
     * namespaces are declared for them.
     */
    private Hashtable forcedDeclTable;
    
    /**
     * Whether the file has a Dtd Declaration.
     */
    private static boolean hasInternalDtd = false;
    
    /**
     * Whether the filter ahs already started outputting declarations
     * of an internal DTD. Useful to know when to print out the '[' character.
     */
    private static boolean startedInternalDtd = false;

    /**
     * A context-aware mapping of namespace URI to namespace prefixes.
     */
    private NamespaceSupport nsSupport;

    /**
     * XML output.
     */
    private Writer output;

    /**
     * Number of namespace prefixes seen.
     */
    private int prefixCounter = 0;

    /**
     * A mapping of namespace URIs to its preferred prefix.
     */
    private Hashtable prefixTable;

    /**
     * Create a new XML writer.
     * Write to standard output.
     */
    public XMLWriterFilter() {
        init(null);
    }

    /**
     * Create a new XML writer.
     * Write to the writer provided.
     * 
     * @param writer output destination, or null to use standard output.
     */
    public XMLWriterFilter(Writer writer) {
        init(writer);
    }

    /**
     * Create a new XML writer.
     * Use the specified XML reader as the parent.
     * 
     * @param xmlreader The parent in the filter chain, or null for no parent.
     */
    public XMLWriterFilter(XMLReader xmlreader) {
        super(xmlreader);
        init(null);
    }

    /**
     * Create a new XML writer.
     * Use the specified XML reader as the parent, and write to the specified
     * writer.
     * @param xmlreader The parent in the filter chain, or null for no parent.
     * @param writer The output destination, or null to use standard output.
     */
    public XMLWriterFilter(XMLReader xmlreader, Writer writer) {
        super(xmlreader);
        init(writer);
    }

    /**
     * Write character data.
     * 
     * Pass the event on down the filter chain for further processing.
     * 
     * @param ch The array of characters to write.
     * @param start The starting position in the array.
     * @param len The number of characters to write.
     * @exception org.xml.sax.SAXException
     *         If there is an error writing the characters, or if a
     *         handler further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters(char ch[], int start, int len) throws SAXException {
        writeEsc(ch, start, len, false);
        super.characters(ch, start, len);
    }

    /**
     * Write a string of character data, with XML escaping.
     * 
     * This is a convenience method that takes an XML String, converts it to a
     * character array, then invokes {@link #characters(char[], int, int)}.
     * 
     * @param data The character data.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the string, or if a handler
     *        further down the filter chain raises an exception.
     * @see #characters(char[], int, int)
     */
    public void characters(String data) throws SAXException {
        char ch[] = data.toCharArray();
        characters(ch, 0, ch.length);
    }

    /**
     * Write an element with character data content but no attributes or
     * Namespace URI.
     * 
     * This is a convenience method to write a complete element with character
     * data content, including the start tag and end tag. The method provides an
     * empty string for the Namespace URI, and empty string for the qualified
     * name, and an empty attribute list.
     * 
     * This method invokes
     * {@link #startElement(String, String, String, Attributes)}, followed by
     * {@link #characters(String)}, followed by
     * {@link #endElement(String, String, String)}.
     * 
     * @param localName The element's local name.
     * @param content The character data content.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the empty tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #startElement(String, String, String, Attributes)
     * @see #characters(String)
     * @see #endElement(String, String, String)
     */
    public void dataElement(String localName, String content)
            throws SAXException {
        dataElement("", localName, "", EMPTY_ATTS, content);
    }

    /**
     * Write an element with character data content but no attributes.
     * 
     * This is a convenience method to write a complete element with character
     * data content, including the start tag and end tag. This method provides
     * an empty string for the qname and an empty attribute list.
     * 
     * This method invokes
     * {@link #startElement(String, String, String, Attributes)}, followed by
     * {@link #characters(String)}, followed by
     * {@link #endElement(String, String, String)}.
     * 
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param content The character data content.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the empty tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #startElement(String, String, String, Attributes)
     * @see #characters(String)
     * @see #endElement(String, String, String)
     */
    public void dataElement(String uri, String localName, String content)
            throws SAXException {
        dataElement(uri, localName, "", EMPTY_ATTS, content);
    }

    /**
     * Write an element with character data content.
     * 
     * This is a convenience method to write a complete element with character
     * data content, including the start tag and end tag.
     * 
     * This method invokes
     * {@link #startElement(String, String, String, Attributes)}, followed by
     * {@link #characters(String)}, followed by
     * {@link #endElement(String, String, String)}.
     * 
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's default qualified name.
     * @param atts The element's attributes.
     * @param content The character data content.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the empty tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #startElement(String, String, String, Attributes)
     * @see #characters(String)
     * @see #endElement(String, String, String)
     */
    public void dataElement(String uri, String localName, String qName,
            Attributes atts, String content) throws SAXException {
        startElement(uri, localName, qName, atts);
        characters(content);
        endElement(uri, localName, qName);
    }

    /**
     * Determine the prefix for an element or attribute name.
     * 
     * TODO: this method probably needs some cleanup.
     * 
     * @param uri The Namespace URI.
     * @param qName
     *        The qualified name (optional); this will be used to indicate
     *        the preferred prefix if none is currently bound.
     * @param isElement
     *        true if this is an element name, false if it is an attribute
     *        name (which cannot use the default Namespace).
     * @return the prefix
     */
    private String doPrefix(String uri, String qName, boolean isElement) {
        String defaultNS = nsSupport.getURI("");
        if ("".equals(uri)) {
            if (isElement && defaultNS != null)
                nsSupport.declarePrefix("", "");
            return null;
        }
        String prefix;
        if (isElement && defaultNS != null && uri.equals(defaultNS)) {
            prefix = "";
        } else {
            prefix = nsSupport.getPrefix(uri);
        }
        if (prefix != null) {
            return prefix;
        }
        prefix = (String) doneDeclTable.get(uri);
        if (prefix != null
                && ((!isElement || defaultNS != null) && "".equals(prefix) || nsSupport
                        .getURI(prefix) != null)) {
            prefix = null;
        }
        if (prefix == null) {
            prefix = (String) prefixTable.get(uri);
            if (prefix != null
                    && ((!isElement || defaultNS != null) && "".equals(prefix) || nsSupport
                            .getURI(prefix) != null)) {
                prefix = null;
            }
        }
        if (prefix == null && qName != null && !"".equals(qName)) {
            int i = qName.indexOf(':');
            if (i == -1) {
                if (isElement && defaultNS == null) {
                    prefix = "";
                }
            } else {
                prefix = qName.substring(0, i);
            }
        }
        
        for (; 
        	prefix == null || nsSupport.getURI(prefix) != null; 
        	prefix = "__NS" + ++prefixCounter);
        
        nsSupport.declarePrefix(prefix, uri);
        doneDeclTable.put(uri, prefix);
        return prefix;
    }

    /**
     * Add an empty element without a Namespace URI, qname or attributes.
     * 
     * This method will supply an empty string for the qname, and empty string
     * for the Namespace URI, and an empty attribute list. It invokes
     * {@link #emptyElement(String, String, String, Attributes)}directly.
     * 
     * @param localName The element's local name.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the empty tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #emptyElement(String, String, String, Attributes)
     */
    public void emptyElement(String localName) throws SAXException {
        emptyElement("", localName, "", EMPTY_ATTS);
    }

    /**
     * Add an empty element without a qname or attributes.
     * 
     * This method will supply an empty string for the qname and an empty
     * attribute list. It invokes
     * {@link #emptyElement(String, String, String, Attributes)}directly.
     * 
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the empty tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #emptyElement(String, String, String, Attributes)
     */
    public void emptyElement(String uri, String localName) throws SAXException {
        emptyElement(uri, localName, "", EMPTY_ATTS);
    }

    /**
     * Write an empty element.
     * 
     * This method writes an empty element tag rather than a start tag followed
     * by an end tag. Both a {@link #startElement(String, String, String, Attributes) 
     * startElement} and an
     * {@link #endElement(String, String, String) endElement}event will be 
     * passed on down the filter chain.
     * 
     * @param uri
     *      The element's Namespace URI, or the empty string if the
     *      element has no Namespace or if Namespace processing is not
     *      being performed.
     * @param localName
     *      The element's local name (without prefix). This parameter must
     *      be provided.
     * @param qName
     *      The element's qualified name (with prefix), or the empty
     *      string if none is available. This parameter is strictly
     *      advisory: the writer may or may not use the prefix attached.
     * @param atts
     *      The element's attribute list.
     * @exception org.xml.sax.SAXException
     *      If there is an error writing the empty tag, or if a
     *      handler further down the filter chain raises an exception.
     */
    public void emptyElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        nsSupport.pushContext();
        write('<');
        writeName(uri, localName, qName, true);
        writeAttributes(atts);
        if (elementLevel == 1) {
            forceNSDecls();
        }
        writeNSDecls();
        write("/>");
        super.startElement(uri, localName, qName, atts);
        super.endElement(uri, localName, qName);
    }

    /**
     * Write a newline at the end of the document.
     * 
     * Pass the event on down the filter chain for further processing.
     * 
     * @exception org.xml.sax.SAXException
     *       If there is an error writing the newline, or if a handler
     *       further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument() throws SAXException {
        write('\n');
        super.endDocument();
        try {
            flush();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * End an element without a Namespace URI or qname.
     * 
     * This method will supply an empty string for the qName and an empty string
     * for the Namespace URI. It invokes
     * {@link #endElement(String, String, String)}directly.
     * 
     * @param localName The element's local name.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the end tag, or if a handler
     *        further down the filter chain raises an exception.
     * @see #endElement(String, String, String)
     */
    public void endElement(String localName) throws SAXException {
        endElement("", localName, "");
    }

    /**
     * End an element without a qname.
     * 
     * This method will supply an empty string for the qName. It invokes
     * {@link #endElement(String, String, String)}directly.
     * 
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the end tag, or if a handler
     *        further down the filter chain raises an exception.
     * @see #endElement(String, String, String)
     */
    public void endElement(String uri, String localName) throws SAXException {
        endElement(uri, localName, "");
    }

    /**
     * Write an end tag.
     * 
     * Pass the event on down the filter chain for further processing.
     * 
     * @param uri The Namespace URI, or the empty string if none is available.
     * @param localName The element's local (unprefixed) name (required).
     * @param qName
     *      The element's qualified (prefixed) name, or the empty string
     *      is none is available. This method will use the qName as a
     *      template for generating a prefix if necessary, but it is not
     *      guaranteed to use the same qName.
     * @exception org.xml.sax.SAXException
     *      If there is an error writing the end tag, or if a handler
     *      further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        write("</");
        writeName(uri, localName, qName, true);
        write('>');
        if (elementLevel == 1) {
            write('\n');
        }
        super.endElement(uri, localName, qName);
        nsSupport.popContext();
        elementLevel--;
    }

    /**
     * Flush the output.
     * This method flushes the output stream. It is especially useful when you
     * need to make certain that the entire document has been written to output
     * but do not want to close the output stream.
     * 
     * This method is invoked automatically by the
     * {@link #endDocument endDocument}method after writing a document.
     * 
     * @see #reset
     * @throws IOException
     */
    public void flush() throws IOException {
        //output.flush();
        
        // flush and then close stream
        output.close();
    }

    /**
     * Force a Namespace to be declared on the root element.
     * 
     * By default, the XMLWriter will declare only the Namespaces needed for an
     * element; as a result, a Namespace may be declared many places in a
     * document if it is not used on the root element.
     * 
     * This method forces a Namespace to be declared on the root element even if
     * it is not used there, and reduces the number of xmlns attributes in the
     * document.
     * 
     * @param uri The Namespace URI to declare.
     * @see #forceNSDecl(java.lang.String,java.lang.String)
     * @see #setPrefix
     */
    public void forceNSDecl(String uri) {
        forcedDeclTable.put(uri, Boolean.TRUE);
    }

    /**
     * Force a Namespace declaration with a preferred prefix.
     * 
     * This is a convenience method that invokes {@link #setPrefix setPrefix}
     * then {@link #forceNSDecl(String) forceNSDecl}.
     * 
     * @param uri The Namespace URI to declare on the root element.
     * @param prefix The preferred prefix for the Namespace, or "" for 
     * 	the default Namespace.
     * @see #setPrefix
     * @see #forceNSDecl(java.lang.String)
     */
    public void forceNSDecl(String uri, String prefix) {
        setPrefix(uri, prefix);
        forceNSDecl(uri);
    }

    /**
     * Force all Namespaces to be declared.
     * 
     * This method is used on the root element to ensure that the predeclared
     * Namespaces all appear.
     */
    private void forceNSDecls() {
        Enumeration prefixes = forcedDeclTable.keys();
        while (prefixes.hasMoreElements()) {
            String prefix = (String) prefixes.nextElement();
            doPrefix(prefix, null, true);
        }
    }

    /**
     * Get the current or preferred prefix for a Namespace URI.
     * 
     * @param uri The Namespace URI.
     * @return The preferred prefix, or "" for the default Namespace.
     * @see #setPrefix
     */
    public String getPrefix(String uri) {
        return (String) prefixTable.get(uri);
    }

    /**
     * Write ignorable whitespace.
     * 
     * Pass the event on down the filter chain for further processing.
     * 
     * @param ch The array of characters to write.
     * @param start The starting position in the array.
     * @param length The number of characters to write.
     * @exception org.xml.sax.SAXException
     *       If there is an error writing the whitespace, or if a
     *       handler further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
        writeEsc(ch, start, length, false);
        super.ignorableWhitespace(ch, start, length);
    }

    /**
     * Internal initialization method.
     * All of the public constructors invoke this method.
     * 
     * @param writer The output destination, or null to use standard output.
     */
    private void init(Writer writer) {
        setOutput(writer);
        nsSupport = null;
        nsSupport = new NamespaceSupport();
        prefixTable = new Hashtable();
        forcedDeclTable = new Hashtable();
        doneDeclTable = new Hashtable();
        hasInternalDtd = false;
        startedInternalDtd = false;
    }
    
    /**
     * 
     * @param name
     * @param publicId
     * @param systemId
     * @throws SAXException
     */
	public void notationDecl(String name, String publicId, String systemId)
		throws SAXException {
        hasInternalDtd = true;
        
        try {
            if (!startedInternalDtd){
                output.write("[\n");
                startedInternalDtd = true;
            }
            output.write("\n<!ELEMENT " + name);
            if (publicId != null) {
                output.write(" " + publicId);
            }
            if (systemId != null) {
                output.write(" " + systemId);
            }
            output.write(">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
	}

    /**
     * Write a processing instruction.
     * 
     * Pass the event on down the filter chain for further processing.
     * 
     * @param target The PI target.
     * @param data The PI data.
     * @exception org.xml.sax.SAXException
     *       If there is an error writing the PI, or if a handler
     *       further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#processingInstruction
     */
    public void processingInstruction(String target, String data)
            throws SAXException {
        write("<?");
        write(target);
        write(' ');
        write(data);
        write("?>");
        if (elementLevel < 1) {
            write('\n');
        }
        super.processingInstruction(target, data);
    }

    /**
     * Reset the writer.
     * This method is especially useful if the writer throws an exception before
     * it is finished, and you want to reuse the writer for a new document. It
     * is usually a good idea to invoke {@link #flush flush}before resetting
     * the writer, to make sure that no output is lost.
     * 
     * This method is invoked automatically by the
     * {@link #startDocument startDocument}method before writing a new
     * document.
     * 
     * Note: this method will not clear the prefix
     * or URI information in the writer or the selected output writer.
     * 
     * @see #flush
     */
    public void reset() {
        elementLevel = 0;
        prefixCounter = 0;
        nsSupport.reset();
    }
    
	/** 
	 * Sets the document locator.
	 * This is called (if it's going to be called at all) before the startDocument()
	 * method. This method is only called if the parser being used implements
	 * the setting of a Locator.
	 * 
	 * @param locator document locator
	 */
	public void setDocumentLocator(Locator locator) {
		// save it away so that we can retrieve locations in the
		// file later
		this.docLocator = locator;
		
		super.setDocumentLocator(locator);
	}

    /**
     * Set a new output destination for the document.
     * 
     * @param writer The output destination, or null to use standard output.
     * @see #flush
     */
    public void setOutput(Writer writer) {
        if (writer == null) {
            output = new OutputStreamWriter(System.out);
        } else {
            output = writer;
        }
    }

    /**
     * Specify a preferred prefix for a Namespace URI.
     * 
     * Note that this method does not actually force the Namespace to be
     * declared; to do that, use the {@link #forceNSDecl(java.lang.String)
     * forceNSDecl} method as well.
     * 
     * @param uri The Namespace URI.
     * @param prefix The preferred prefix, or "" to select the default Namespace.
     * @see #getPrefix
     * @see #forceNSDecl(java.lang.String)
     * @see #forceNSDecl(java.lang.String,java.lang.String)
     */
    public void setPrefix(String uri, String prefix) {
        prefixTable.put(uri, prefix);
        
        // help? no - doesn't put it at the root
        //forceNSDecl(uri, prefix);
        
        // help? no - doesn't put it at the root
        //forceNSDecl(uri);
    }

    /**
     * Write the XML declaration at the beginning of the document.
     * 
     * Pass the event on down the filter chain for further processing.
     * Note that if the character encoding set in <code>XMLSAXParser</code>
     * changes, then the value hard-coded into this method must change too.
     * 
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the XML declaration, or if a
     *        handler further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument() throws SAXException {
        reset();
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        super.startDocument();
    }

    /**
     * Start a new element without a qname, attributes or a Namespace URI.
     * 
     * This method will provide an empty string for the Namespace URI, and empty
     * string for the qualified name, and a default empty attribute list. It
     * invokes #startElement(String, String, String, Attributes)} directly.
     * 
     * @param localName The element's local name.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the start tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #startElement(String, String, String, Attributes)
     */
    public void startElement(String localName) throws SAXException {
        startElement("", localName, "", EMPTY_ATTS);
    }

    /**
     * Start a new element without a qname or attributes.
     * 
     * This method will provide a default empty attribute list and an empty
     * string for the qualified name. It invokes {@link #startElement(String,
     * String, String, Attributes)} directly.
     * 
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the start tag, or if a
     *        handler further down the filter chain raises an exception.
     * @see #startElement(String, String, String, Attributes)
     */
    public void startElement(String uri, String localName) throws SAXException {
        startElement(uri, localName, "", EMPTY_ATTS);
    }

    /**
     * Write a start tag.
     * 
     * Pass the event on down the filter chain for further processing.
     * 
     * @param uri The Namespace URI, or the empty string if none is available.
     * @param localName The element's local (unprefixed) name (required).
     * @param qName
     *      The element's qualified (prefixed) name, or the empty string
     *      is none is available. This method will use the qName as a
     *      template for generating a prefix if necessary, but it is not
     *      guaranteed to use the same qName.
     * @param atts The element's attribute list (must not be null).
     * @exception org.xml.sax.SAXException
     *      If there is an error writing the start tag, or if a
     *      handler further down the filter chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        elementLevel++;
        nsSupport.pushContext();

        write('<');
        writeName(uri, localName, qName, true);
        writeAttributes(atts);
        
        // writing namespaces out like attributes because it
        // was more foolproof in testing
        
        
        if (elementLevel == 1 &&
                !XMLSAXParser.getXFile().hasFormatAttribute(XML.Attribute.HAS_DTD)) {
            //forceNSDecls();
            writeNonAttributeNSDecls(atts);
        }
        
        write('>');
        super.startElement(uri, localName, qName, atts);
    }
    
    /**
     * 
     * @param eName name of the associated element
     * @param aName name of the attribute
     * @param type attribute type
     * @param valueDefault attribute default ("#IMPLIED,
     *   "#REQUIRED", or "#FIXED") or null if none of these applies
     * @param value attribute's default value, or null if there is none
     * @throws SAXException
     */
    public void takeAttributeDecl(String eName, String aName, String type,
            String valueDefault, String value) throws SAXException {
        
        hasInternalDtd = true;
        
        try {
            if (!startedInternalDtd){
                output.write("[");
                startedInternalDtd = true;
            }
            output.write("\n<!ATTLIST ");
            output.write(eName + " " + aName + " " + type);
            if (valueDefault != null) {
                output.write(" " + valueDefault);
            }
            if (value != null) {
                output.write(" " + value);
            }
            output.write(">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
        
    }
    
    /**
     * 
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    public void takeComment(char[] ch, int start, int length) 
		throws SAXException{
        try {
            
       	 if (hasInternalDtd && !startedInternalDtd){
                output.write("\n[");
                startedInternalDtd = true;
            }
           
            output.write("\n<!--");
            output.write(ch, start, length);
            output.write("-->");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    /**
     * 
     * @param name element type name
     * @param model content model
     * @throws SAXException
     */
    public void takeElementDecl(String name, String model) 
    	throws SAXException {
        hasInternalDtd = true;
        
        try {
            if (!startedInternalDtd){
                output.write("[\n");
                startedInternalDtd = true;
            }
            output.write("\n<!ELEMENT " + name + " " + model + ">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    /**
     * 
     * @throws SAXException
     */
    public void takeEndDtd() throws SAXException {
		switch (XMLSAXParser.getParseActivity()){
			case XMLSAXParser.ACTION_REP_LKS:
				try {
				    if (startedInternalDtd){
				        output.write("\n]");
				    }
	                output.write(" >\n");
			        // reset those flags
			        hasInternalDtd = false;
			        startedInternalDtd = false;
	            } catch (IOException e) {
	                throw new SAXException(e);
	            }
				break;
		}
    }
    
    /**
     * 
     * @param name
     * @param publicId
     * @param systemId
     * @throws SAXException
     */
	public void takeExternalEntityDecl(String name, String publicId,
			String systemId) throws SAXException {
        hasInternalDtd = true;
        
        try {
            if (!startedInternalDtd){
                output.write("[\n");
                startedInternalDtd = true;
            }
            output.write("\n<!ENTITY " + name);
            if (publicId != null) {
                output.write(" " + publicId);
            }
            if (systemId != null) {
                output.write(" " + systemId);
            }
            output.write(">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
	}
	
	/**
	 * 
	 * @param name entity name
	 * @param value replacement text of the entity
	 * @throws SAXException
	 */
	public void takeInternalEntityDecl(String name, String value)
		throws SAXException {
        hasInternalDtd = true;
        
        try {
            if (!startedInternalDtd){
                output.write("[\n");
                startedInternalDtd = true;
            }
            output.write("\n<!ENTITY ");
            if (name.startsWith("%")){
                // parameter entity
                output.write("% ");
                name = name.substring(1);
            }
            output.write(name + " \"" + value + "\">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
	}
	
	/**
	 * Receives the name and location of a DTD used by the xml file.
	 * Not an inherited method from XMLFilterImpl.
	 * This method takes in DTD information from a parse stream 'upriver' from it.
	 * This is a workaround to the problem that this class can't have a LexicalHandler set
	 * on it which is where the startDTD would have been. It's essentially being caught upstream,
	 * and sent back downstream.
	 * 
	 * @param name document type name
	 * @param publicId the declared public identifier for the external DTD subset, 
	 * 	or null if none was declared
	 * @param systemId The declared system identifier for the external DTD subset, 
	 * 	or null if none was declared.
	 * @throws SAXException
	 */
	public void takeStartDtd(String name, String publicId, String systemId) 
		throws SAXException{

		switch (XMLSAXParser.getParseActivity()){
			case XMLSAXParser.ACTION_REP_LKS:
			    hasInternalDtd = true;
			
				try {
                    output.write("\n<!DOCTYPE ");
                    output.write(name + " ");
                    // if publicId exist, write PUBLIC keyword and then appended with 
                    // both public Id and system Id
                    if (publicId != null && !publicId.equals("")){
                        output.write("PUBLIC \"" + publicId + "\" ");
                        if (systemId != null && !systemId.equals(""))
                        	output.write("\"" + systemId + "\"");
                    } else {  
                    	// only write SYSTEM keyword if no PUBLIC Id exist.
                    	if (systemId != null && !systemId.equals(""))
                        	output.write("SYSTEM \"" + systemId + "\"");
                    }
                } catch (IOException e) {
                    throw new SAXException(e);
                }
				break;
		}						
	}

    /**
     * Write a raw character.
     * 
     * @param c The character to write.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the character, this method
     *        will throw an IOException wrapped in a SAXException.
     */
    private void write(char c) throws SAXException {
        try {
            output.write(c);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * Write a raw string.
     * 
     * @param s
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the string, this method will
     *        throw an IOException wrapped in a SAXException
     */
    private void write(String s) throws SAXException {
        try {
            output.write(s);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * Write out an attribute list, escaping values.
     * 
     * The names will have prefixes added to them.
     * 
     * @param atts The attribute list to write.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the attribute list, this
     *        method will throw an IOException wrapped in a
     *        SAXException.
     */
    private void writeAttributes(Attributes atts) throws SAXException {
        for (int i = 0; i < atts.getLength(); i++) {
            char ch[] = atts.getValue(i).toCharArray();
            write(' ');
            writeName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i),
                    false);
            write("=\"");
            writeEsc(ch, 0, ch.length, true);
            write('"');
        }
    }

    /**
     * Write an array of data characters with escaping.
     * 
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @param isAttVal true if this is an attribute value literal.
     * @exception org.xml.sax.SAXException
     *        If there is an error writing the characters, this method
     *        will throw an IOException wrapped in a SAXException.
     */
    private void writeEsc(char ch[], int start, int length, boolean isAttVal)
            throws SAXException {
        for (int i = start; i < start + length; i++) {
            switch (ch[i]) {
            case '&':
                write("&amp;");
                break;
            case '<':
                write("&lt;");
                break;
            case '>':
                write("&gt;");
                break;
            case '\"':
                if (isAttVal) {
                    write("&quot;");
                } else {
                    write('\"');
                }
                break;
            default:
                if (ch[i] > '\u007f') {
                    write("&#");
                    write(Integer.toString(ch[i]));
                    write(';');
                } else {
                    write(ch[i]);
                }
            }
        }
    }

    /**
     * Write an element or attribute name.
     * 
     * @param uri The Namespace URI.
     * @param localName The local name.
     * @param qName The prefixed name, if available, or the empty string.
     * @param isElement
     *        true if this is an element name, false if it is an attribute
     *        name.
     * @exception org.xml.sax.SAXException
     *        This method will throw an IOException wrapped in a
     *        SAXException if there is an error writing the name.
     */
    private void writeName(String uri, String localName, String qName,
            boolean isElement) throws SAXException {
        String prefix = doPrefix(uri, qName, isElement);
        if (prefix != null && !"".equals(prefix)) {
            write(prefix);
            write(':');
        }
        
        // it was found that when writing namespace declarations
        // as attributes, their localName was set to "" but their
        // qualified name was correct
        if (localName != null && !"".equals(localName)){
            write(localName);
        } else {
            // namespace declaration
            write(qName);
        }
        
    }

    /**
     * Write out the list of Namespace declarations.
     * 
     * @exception org.xml.sax.SAXException
     *        This method will throw an IOException wrapped in a
     *        SAXException if there is an error writing the Namespace
     *        declarations.
     */
    private void writeNSDecls() throws SAXException {
        Enumeration prefixes = nsSupport.getDeclaredPrefixes();
        
        while (prefixes.hasMoreElements()) {
            String prefix = (String) prefixes.nextElement();
            
            String uri = nsSupport.getURI(prefix);
            
            if (uri == null) {
                uri = "";
            }
            char ch[] = uri.toCharArray();
            write(' ');
            if ("".equals(prefix)) {
                write("xmlns=\"");
            } else {
                write("xmlns:");
                write(prefix);
                write("=\"");
            }
            writeEsc(ch, 0, ch.length, true);
            write('\"');
        }
    }
    
    /**
     * 
     * @param atts
     * @throws SAXException
     */
    private void writeNonAttributeNSDecls(Attributes atts)
    	throws SAXException {
        // create a Vector of qualified namespace prefixes in scope right now
        Vector allNs = new Vector();
        Enumeration prefixes = nsSupport.getDeclaredPrefixes();
        while (prefixes.hasMoreElements()) {
            String prefix = (String) prefixes.nextElement();
            String qNsName;
            if (prefix.equals("")){
                qNsName = "xmlns";
            } else {
                qNsName = "xmlns:" + prefix;
            }
            allNs.add(qNsName);
        }
        
        // remove the prefixes that are also attributes of this element
        for (int i = 0; i < atts.getLength(); i++) {
            String lName, qName, nameToUse;
            lName = atts.getLocalName(i);
            qName = atts.getQName(i);
            if (lName != null && !"".equals(lName)){
                nameToUse = lName;
            } else {
                nameToUse = qName;
            }
            if (allNs.contains(nameToUse)){
                allNs.remove(nameToUse);
                //System.out.println("Removed ns: " + nameToUse 
                //        + " from " + XMLSAXParser.getXFile().getPath());
            }
        }
        
        // strip xmlns: from namespaces
        Vector minusXmlns = new Vector();
        for (Iterator iter = allNs.iterator(); iter.hasNext();){
            String prefix = (String) iter.next();
            minusXmlns.add(prefix.substring(6));
        }
        
        // now write out leftover namespaces
        for (Iterator iter = minusXmlns.iterator(); iter.hasNext();){
            String prefix = (String) iter.next();
            
            String uri = nsSupport.getURI(prefix);
            
            if (uri == null) {
                uri = "";
            }
            char ch[] = uri.toCharArray();
            write(' ');
            if ("".equals(prefix)) {
                write("xmlns=\"");
            } else {
                write("xmlns:");
                write(prefix);
                write("=\"");
            }
            writeEsc(ch, 0, ch.length, true);
            write('\"');   
            
        }    
    }

}