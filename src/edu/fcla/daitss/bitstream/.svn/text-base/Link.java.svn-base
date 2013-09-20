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
package edu.fcla.daitss.bitstream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.fcla.da.xml.WebCache;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * A reference to an external file from within a bitstream of another file.
 * 
 * This class does not check whether or not it should retrieve a Link (
 * <code>shouldRetrieve()</code>). This is checked by
 * <code>DataFile.retrieveLinks()</code> before calling
 * <code>Link.retrieve()</code>. Whether or not links'
 * <code>shouldRetrieve</code> member is set to true is determined by the
 * values in the configPolicies configuration file in the harvesting section.
 * 
 * @author franco
 */
public class Link {

    /**
     * @author franco
     * 
     * Thrown when a link is determined broken.
     */
    public class BrokenLinkException extends Exception {

        /**
		 * 
		 */
		private static final long serialVersionUID = 8060455400823404689L;

		BrokenLinkException(Exception e) {
            super(e);
        }

        BrokenLinkException(String m) {
            super(m);
        }
    }
    /**
    * Thrown when a link is determined to be ignored.
    */
   public class IgnoredLinkException extends Exception {

	   /**
		 * 
		 */
		private static final long serialVersionUID = 2263924686733824991L;

	IgnoredLinkException(Exception e) {
           super(e);
       }

	   IgnoredLinkException(String m) {
           super(m);
       }
   }
    /**
     * attempts to obtain the linked-to resource were unsuccessful
     */
    public static final String STATUS_BROKEN = "BROKEN";

    /**
     * the linked-to resource was not pursued
     */
    public static final String STATUS_IGNORED = "IGNORED";

    /**
     * the linked-to resource was obtained successfully
     */
    public static final String STATUS_SUCCESSFUL = "SUCCESSFUL";

    /**
     * unknown link status
     */
    public static final String STATUS_UNKNOWN = "UNKNOWN";

    /**
     * an absolute path
     */
    public static final String TYPE_ABS_PATH = "ABS_PATH";

    /**
     * A URL using the http scheme
     */
    public static final String TYPE_HTTP_URL = "HTTP_URL";

    /**
     * a relative path
     */
    public static final String TYPE_REL_PATH = "REL_PATH";

    /**
     * unknown type of path
     */
    public static final String TYPE_UNKNOWN = "UNKNOWN";

	/**
	 * milliseconds till timeout
	 */
	public static final int TIMEOUT = 5 * 1000;

    /**
     * for testing, need a system test
     * 
     * @param args
     *            not used
     * @throws FatalException
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws FatalException,
            URISyntaxException {

        String packagePath = "/home/franco/dtest/UF00003061";
        String filePath = "/home/franco/dtest/UF.xml";
        //String alias = "UF00003061/palmm.xsd";
        String alias = "images/paypal.gif";
        String linksDir = ArchiveProperties.getInstance().getArchProperty("INGEST_WORKPATH") + "links";
        String parentOrigin = DataFile.ORIG_DEPOSITOR;
        String parentURI = "http://club977.com/";

        Link link = new Link(packagePath, filePath, alias, linksDir,
                parentOrigin, parentURI);

        String path = link.retrieve();
        System.out.println("path: " + path);
        System.out.println("type: " + link.getLinkType());
    }

    /**
     * The absolute path to the parent file
     */
    private String parentFilePath = null;

    /**
     * Whether or not this link is the schema for the root element.
     */
    private boolean isRootSchema = false;

    /**
     * Whether or not the link is a schema document.
     */
    private boolean isSchema = false;

    /**
     * The link name found in the file. The name is exactly as it appeared in
     * the parent file, for example: &quot;http://www.fcla.edu/index.html&quot;,
     * &quot;images/i.jpg&quot;, &quot;C:\My
     * Documents\packages\UFE0000006\smith.pdf&quot;, etc.
     */
    private String linkAlias = null;

    /**
     * Origin of the link, types from DataFile.ORIG_*
     */
    private String linkOrigin = DataFile.ORIG_UNKNOWN;

    /**
     * The absolute path to the directory to put downloaded files into. Ex:
     * &quot;/darchive/ingest/work/UFE0000001/links_20030601T093354/&quot;.
     */
    private String linksPath = null;

    /**
     * Type of link - either <code>TYPE_ABS_PATH</code>,
     * <code>TYPE_HTTP_URL</code>,<code>TYPE_REL_PATH</code>, or
     * <code>TYPE_UNKNOWN</code>
     */
    private String linkType = TYPE_UNKNOWN;

    /**
     * Absolute path to package directory for the file that references the link
     * argument. Ex: &quot;/daitss/prod/daitss/ingest/work/UFE0000001/&quot;.
     * Include the trailing slash.
     */
    private String packagePath = null;

    /**
     * The origin of the DataFile containing this link. One of three values:
     * DataFile.ORIG_DEPOSITOR, DataFile.ORIG_ARCHIVE, or DataFile.ORIG_INTERNET
     */
    private String parentOrigin = null;

    /**
     * URI of the parent file
     */
    private URI parentUri = null;

    /**
     * Whether or not this link should be retrieved.
     */
    private boolean shouldRetrieve = false;

    /**
     * The current status of the linked-to file (has it been ignored or
     * downloaded, etc.)
     */
    private String status = STATUS_UNKNOWN;

    /**
     * The URL of the linked-to file
     */
    private URL theUrl = null;

    /* the absoulte path to the downloaded file */
    private String childPath = null;

	private WebCache webCache;

    /**
     * 
     * @param _packagePath
     *            absolute path to the package directory
     * @param _filePath
     *            absolute path to the file containing the link
     * @param _linkAlias
     *            the name of the link as written in the file
     * @param _linksPath
     *            absolute path to a directory in which to put the downloaded
     *            files
     * @param _parentOrigin
     *            the origin of the anchor
     * @param _parentURI
     *            the uri of the anchor
     * @throws URISyntaxException
     * @throws FatalException 
     */
    public Link(String _packagePath, String _filePath, String _linkAlias, String _linksPath, String _parentOrigin, String _parentURI) throws URISyntaxException, FatalException {
    	
    	File webCacheDir = new File(ArchiveProperties.getInstance().getArchProperty("WEBCACHE_DIR"));
    	    	
        try {
			webCache = new WebCache(webCacheDir);
		} catch (IOException e) {
			Informer.getInstance().fail("cannot initialize webcache", "constructing link", e);
		}
    	
        this.packagePath = _packagePath;
        this.parentFilePath = _filePath;
        this.linkAlias = _linkAlias;
        this.linksPath = _linksPath;
        this.parentOrigin = _parentOrigin;

        if (_parentURI == null || _parentURI.equals("")) {
            this.parentUri = new File(_filePath).toURI();
        } else {
            this.parentUri = new URI(_parentURI);
        }

        determineLinkType();
    }

    /**
     * Determines the type of link and assigns one of the possible types:
     * <ul>
     * <li>"UNKNOWN"</li>
     * <li>"HTTP_URL"</li>
     * <li>"REL_PATH"</li>
     * <li>"ABS_PATH"</li>
     * </ul>
     *  
     */
    public void determineLinkType() {
        String type = null;
        URI u = null;
        URI alias = null;

        // Construct a url of the link.
        String absWinPattern = "([a-zA-Z]:|\\\\)?(\\\\[\\w\\s\\.]+)+";
        String relWinPattern = "[\\w\\s\\.]+(\\\\[\\w\\s\\.]+)+";

        if (getLinkAlias().matches(absWinPattern)) {
            // Absolute windows path
            type = TYPE_ABS_PATH;
        } else if (getLinkAlias().matches(relWinPattern)) {
            // Relative windows path
            type = TYPE_REL_PATH;
        } else {
            try {
                alias = new URI(getLinkAlias());
                u = getParentUri().resolve(alias);

                if (u.getScheme() != null && u.getScheme().matches("https?")) {
                    type = TYPE_HTTP_URL;
                } else if (alias.isAbsolute()
                        && alias.getScheme().equals("file")
                        || ((alias.getPath() != null ) && alias.getPath().startsWith("/"))) {
                    // Absolute Path
                    type = TYPE_ABS_PATH;
                } else if (!alias.isAbsolute() && alias.getQuery() == null) {
                    // Relative Path
                    type = TYPE_REL_PATH;
                } else {
                    // Unknown
                    type = TYPE_UNKNOWN;
                }
            } catch (URISyntaxException e) {
                type = TYPE_UNKNOWN;
            }
        }
        setLinkType(type);
    }

    /**
     * Download a file from the Internet.
     * 
     * @param file file to write
     * @param url URL of file to copy
     * @throws IOException
     */
    public void downloadFileFromUrl(File file, URL url) throws IOException {
    	
    	// trim off the fragments
    	URL urlToGet = trimFragment(url);
    	
    	// io streams
        InputStream inputStream = webCache.get(urlToGet);
        FileOutputStream outputStream = new FileOutputStream(file);

        // transfer bytes
		int b;
		while((b = inputStream.read()) != -1) {
			outputStream.write(b);
		}

		// cleanup
		inputStream.close();
		outputStream.close();

    }

	/**
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	private URL trimFragment(URL url) throws MalformedURLException {
		URL urlToGet;
		if(url.getRef() != null) {
    		String TrimmedUrlString = url.toString().replaceFirst("#.*$", "");
    		urlToGet = new URL(TrimmedUrlString);
    	} else {
    		urlToGet = url;
    	}
		return urlToGet;
	}

    /**
     * Encodes illegal URI characters so that they can be included
     * in a URI.
     * 
     * @param c character to encode
     * @return String encoded character
     */
    private String encodeUriChar(char c) {
        return "%" + Integer.toHexString(c);
    }

    /**
     * Looks up a datafile by URI and determines if it is already in the system
     * if the link alias was an HTTP URL.
     * 
     * @return DataFile representing the target of this link
     * @throws FatalException if we can't get an instance of the DataFileFactory
     */
    public DataFile getDataFile() throws FatalException {
        DataFile oldDf = null;
        if (getLinkType().equals(TYPE_UNKNOWN)) {
            // link type is unknown
            oldDf = null;  		
        } else if (getLinkType().equals(TYPE_ABS_PATH)) {
			// link type is absolute
			oldDf = null;
        } else if (getLinkType().equals(TYPE_REL_PATH)) {
			// link type is relative	
			
			try {
				String uri = this.getUri().toString();
				oldDf = DataFileFactory.getInstance().getDfByUri(uri);
			} catch (URISyntaxException e) {
				oldDf = null;
			}
        } else {
            // http URL
            try {
                // see if the DataFileFactory already saw this URL for
                // this package
                oldDf = 
                    DataFileFactory.getInstance().getDfByUri(this.getUri().toString());
            } catch (URISyntaxException e) {
                oldDf = null;
            }
        } 
        return oldDf;
    }

    /**
     * Returns the path to the file to which this link is anchored.
     * In other words, the absolute path to the parent file.
     * 
     * @return the path to the parent file.
     */
    public String getParentFilePath() {
        return this.parentFilePath;
    }

    /**
     * The link name found in the file. The name is exactly as it appeared in
     * the file, for example: &quot;http://www.fcla.edu/index.html&quot;,
     * &quot;images/i.jpg&quot;, &quot;C:\My
     * Documents\packages\UFE0000006\smith.pdf&quot;, etc.
     * 
     * @return the link name found in the file
     */
    public String getLinkAlias() {
        return this.linkAlias;
    }

    /**
     * @return the link origin
     */
    public String getLinkOrigin() {
        return linkOrigin;
    }

    /**
     * Returns the path to the directory where links should be stored.
     * 
     * @return the path to the directory where links should be stored
     */
    public String getLinksPath() {
        return this.linksPath;
    }

    /**
     * Returns the type of link one of:
     * <ul>
     * <li>"UNKNOWN"</li>
     * <li>"HTTP_URL"</li>
     * <li>"REL_PATH"</li>
     * <li>"ABS_PATH"</li>
     * </ul>
     * 
     * @return String representing the type of Link
     */
    public String getLinkType() {
        // your code here
        return this.linkType;
    }

    /**
     * @return the path of the package that the parent file belongs.
     */
    public String getPackagePath() {
        return this.packagePath;
    }

    /**
     * @return the origin of the file that contains this link
     */
    public String getParentOrigin() {
        return parentOrigin;
    }

    /**
     * @return the URI of the file that contains this link
     */
    public URI getParentUri() {
        return parentUri;
    }

    /**
     * Gets the status of the link.
     * 
     * @return String status of the link
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the URL of the downloaded file.
     */
    public URL getTheUrl() {
        return theUrl;
    }

    /**
     * Determines the link's URI taking into account the
     * parent (file containing the link)'s URI if the parent
     * file was downloaded.
     * 
     * @return String representing the URI of the link reference
     * @throws URISyntaxException
     */
    public String getUri() throws URISyntaxException {
        URI path = null;
        URI alias = null;

        // Make the alias a URI
        alias = new URI(getLinkAlias());

        // Resolve the alias against the parent's URI
        path = parentUri.resolve(alias);
        return path.toString();
    }

    /**
     * Whether or not the link is to a root element schema.
     * 
     * @return whether or not the link is to a root element schema
     */
    public boolean isRootSchema() {
        return this.isRootSchema;
    }

    /**
     * Whether or not the link is to a schema.
     * 
     * @return whether or not the link is to a schema
     */
    public boolean isSchema() {
        return this.isSchema;
    }

    /**
     * Format a URI.
     * 
     * @param s URI to format
     * @return the URI with all illegal URI characters encoded
     * 	and windows path separator characters switched to *nix path separator
     * 	characters
     */
    private String normalizeUri(String s) {
        // slashify
        s = s.replace('\\', '/');

        // normalize all other characters
        // 0-9 a-z A-Z $-_.+!*'();/?:@=&, are to be left alone, 
        // other chars are to be encoded
        Pattern p = Pattern
                .compile("[^\\w\\d\\.\\-\\+\\!\\*\\'\\(\\)\\$\\;\\/\\:\\@\\=\\&]");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String encodedChar = encodeUriChar(m.group().charAt(0));
            m.appendReplacement(sb, encodedChar);
        }
        return sb.toString();
    }

    /**
     * Retrieve files from the web or the local file system.
     * Only attempts to resolve links if the link is written either
     * as a relative system path or as a URL.
     * 
     * @return String representing the absolute path of the file referenced by
     *         the link
     * @throws FatalException
     */
    public String retrieve() throws FatalException {

        Informer.getInstance().info("Attempting to resolve link at " + DateTimeUtil.now(), "link: " + this.getLinkAlias() + " (file containing link: " + this.getParentFilePath() + ")");

        String returnPath = null;

        try {
            File linkFile = null;
            
            // linkType was set in the Link constructor
            if (linkType.equals(TYPE_REL_PATH)) {
                linkFile = retrieveRelativePath();
            } else if (linkType.equals(TYPE_HTTP_URL)) {
                linkFile = retrieveHttpLink();
                setLinkOrigin(DataFile.ORIG_INTERNET);
            } else if (linkType.equals(TYPE_ABS_PATH)) {
                linkFile = null;
                //setLinkOrigin(DataFile.ORIG_UNKNOWN);
                throw new BrokenLinkException("Ignoring absolute filesystem path " + getLinkAlias());
                
            } else if (linkType.equals(Link.TYPE_UNKNOWN)) {
                linkFile = null;
                //setLinkOrigin(DataFile.ORIG_UNKNOWN);
                throw new IgnoredLinkException("Unknown link type for "
                        + getLinkAlias());
            }

            // get the absolute path of the local or downloaded file
            // returnPath = linkFile.getCanonicalPath();
            returnPath = linkFile.getAbsolutePath();
            
            // clean up
            linkFile = null;
        } catch (BrokenLinkException e) {
            returnPath = null;
        } catch (IgnoredLinkException e) {
                returnPath = null;
        } /*catch (IOException e) {
            returnPath = null;
        }*/
        
        // figure out what the retrieval result should be for print outs
        String result = null;
        if (linkType.equals(TYPE_REL_PATH)){
            result = 
                (returnPath == null)? " (NOT RESOLVED)" : " (RESOLVED TO LOCAL FILE)";
        } else if (linkType.equals(TYPE_HTTP_URL)){
            result = 
                (returnPath == null)? " (NOT ABLE TO DOWNLOAD)" : " (DOWNLOADED)";
        } else {
            // link alias was an absolute path or the link type is unknown
            result = " (LINK IGNORED)";
        }

        Informer.getInstance().info("Link retrieval complete at " + DateTimeUtil.now() + " " + result, "alias: " + this.linkAlias + " local path: " + returnPath);
        
        // set the child file's absolute path
        this.childPath = returnPath;
        
        return returnPath;
    }

    /**
     * Retrives the referenced file.
     * 
     * @return File object that is associated with the linked data
     * @throws BrokenLinkException
     */
    private File retrieveHttpLink() throws BrokenLinkException {

        // Get the identifier and locator of the data

        URL url;
        try {
            URI uri = new URI(getUri());
            url = uri.toURL();            
            setTheUrl(url);
        } catch (URISyntaxException e) {
            throw new BrokenLinkException(e);
        } catch (MalformedURLException e) {
            throw new BrokenLinkException(e);
        }

        // Create the link file object
        // construct the directory path that will go in the links directory
        String path = url.getAuthority() + url.getPath();
        File linkFile = new File(new File(getLinksPath()), path);
        
        // Create the directories within the links directory in which it will be downloaded
        // if not already done
        linkFile.getParentFile().mkdirs();

        // Download the file only if it does not already exist 
        // (meaning it was downloaded already).
        // don't think we need to check this because if we already
        // downloaded this URL we would have short-circuited
        // this in DataFile.retrieveLinks
        if (!linkFile.exists()) {
            try {
                downloadFileFromUrl(linkFile, url);
            } catch (IOException e) {
                throw new BrokenLinkException("could not download file, IO error: " + e);
            }
        } 
        return linkFile;
    }

    /**
     * Retrieves the file referenced by a relative path
     * 
     * @return File object that is associated with the linked file
     * @throws BrokenLinkException
     */
    private File retrieveRelativePath() throws BrokenLinkException {
    	
    	
        // Construct the linked-to file path as a file: URI
        URI parentUri = new File(getParentFilePath()).toURI();

        // Resolve the target path
        URI target;
        try {
            if (getLinkAlias().matches("[\\w\\s\\.]+(\\\\[\\w\\s\\.]+)+")) {
                // format the URI properly
                String targetPath = normalizeUri(getLinkAlias());
                target = new URI(targetPath);
            } else {
                target = new URI(getLinkAlias());
            }
        } catch (URISyntaxException e) {
            throw new BrokenLinkException(e);
        }
        URI abs = parentUri.resolve(target);
        
        URI cleaned;
		try {
			cleaned = trimFragment(abs.toURL()).toURI();
		} catch (MalformedURLException e) {
			throw new BrokenLinkException(e);
		} catch (URISyntaxException e) {
			throw new BrokenLinkException(e);
		}
    
        File file = new File(cleaned);

        // Check if the file exists
        if (!file.exists() || !file.isFile()) {
            throw new BrokenLinkException("file " + file.getAbsolutePath()
                    + " not found");
        }

        return file;
    }

    /**
     * Setter for the file parsers to use.
     * 
     * @param _isRootSchema
     */
    public void setIsRootSchema(boolean _isRootSchema) {
        isRootSchema = _isRootSchema;
    }

    /**
     * Setter for the file parsers to use.
     * 
     * @param _isSchema
     */
    public void setIsSchema(boolean _isSchema) {
        isSchema = _isSchema;
    }

    /**
     * @param string
     */
    public void setLinkOrigin(String string) {
        linkOrigin = string;
    }

    /**
     * Setter called ultimately from the Constructor.
     * 
     * @param _linkType
     *            the link type
     */
    public void setLinkType(String _linkType) {
        linkType = _linkType;
    }

    /**
     * Setter for the file parsers to use.
     * 
     * @param b whether or not this link should be retrieved
     */
    public void setShouldRetrieve(boolean b) {
        shouldRetrieve = b;
    }

    /**
     * Sets the resolution status of the link.
     * 
     * @param string desired status
     */
    public void setStatus(String string) {
        if (string.equals(STATUS_BROKEN) || string.equals(STATUS_IGNORED)
                || string.equals(STATUS_SUCCESSFUL)
                || string.equals(STATUS_UNKNOWN)) {
            status = string;
        } else {
            status = STATUS_UNKNOWN;
        }
    }
    
    /**
     * Set by the http retrieval.
     * 
     * @param theUrl
     */
    public void setTheUrl(URL theUrl) {
        this.theUrl = theUrl;
    }

    /**
     * @return whether or not this link should be retrieved
     */
    public boolean shouldRetrieve() {
        return shouldRetrieve;
    }

    /**
     * @return the members of this class
     * @see java.lang.Object#toString()
     */
    public String toString() {
        // make a string of all the members
        String buffer = "";
        buffer += "\n\tfilePath: " + parentFilePath + "\n";
        buffer += "\tisRootSchema: " + isRootSchema + "\n";
        buffer += "\tisSchema: " + isSchema + "\n";
        buffer += "\tlinkAlias: " + linkAlias + "\n";
        buffer += "\tlinkOrigin: " + linkOrigin + "\n";
        buffer += "\tlinksPath: " + linksPath + "\n";
        buffer += "\tlinkType: " + linkType + "\n";
        buffer += "\tpackagePath: " + packagePath + "\n";
        buffer += "\tparentOrigin: " + parentOrigin + "\n";
        buffer += "\tshouldRetrieve: " + shouldRetrieve + "\n";
        buffer += "\tstatus: " + status + "\n";
        buffer += "\ttheUrl: " + theUrl + "\n";
        return buffer;
    }

    /**
     * @return Returns the absolute path to the downloaded child file. 
     */
    public String getChildPath() {
        return childPath;
    }
}