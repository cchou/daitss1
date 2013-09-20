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
package edu.fcla.daitss.format.miscfile.pdf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.etymon.pjx.PdfArray;
import com.etymon.pjx.PdfBoolean;
import com.etymon.pjx.PdfDictionary;
import com.etymon.pjx.PdfFormatException;
import com.etymon.pjx.PdfInputFile;
import com.etymon.pjx.PdfManager;
import com.etymon.pjx.PdfName;
import com.etymon.pjx.PdfObject;
import com.etymon.pjx.PdfReader;
import com.etymon.pjx.PdfWriter;
import com.etymon.pjx.PdfReference;
import com.etymon.pjx.PdfStream;
import com.etymon.pjx.PdfString;
import com.etymon.pjx.util.PdfPageObjects;
import com.etymon.pjx.util.PdfPageTree;

import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.file.TransformationFormat;
import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.file.distributed.Distributed;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.format.markup.METSDescriptor.Factory;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.AlphaComparator;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.ExternalProgram;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.JhoveEngine;
import edu.fcla.daitss.util.Procedure;
import edu.harvard.hul.ois.jhove.RepInfo;
import edu.harvard.hul.ois.jhove.module.PdfModule;

/**
 * Pdf represents a file in the Adobe Portable Document File (PDF) Format.
 * Note that the length of the values of these constants have to be 
 * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
 * they have to be unique in the FORMAT_ATTRIBUTES database table.
 * 
 * Requirements of a Tagged PDF
 */
public class Pdf extends DataFile implements Distributed {

    /**
     * Attribute represents an attribute specific to Pdf files. 
     */
    public static class Attribute {
        /** Fully conformant with PDF/A-1a specification. */
        public static final String PDFA_1A_CONF = "APP_PDF_PDFA_1A_CONF";

        /** Has the recommended binary characters in its header. */
        public static final String HAS_BIN_HDR = "APP_PDF_HAS_BIN_HDR";

        /** is linearized */
        public static final String IS_LIN = "APP_PDF_IS_LIN";	

        /** conformant with PDF/A-1b specification. */
        public static final String PDFA_1B_CONF = "APP_PDF_PDFA_1B_CONF";
    }

    /* Fully-qualified name for this class. To be used for Informer calls from
     * within static methods. */
    private static String CLASSNAME = "edu.fcla.daitss.format.miscfile.pdf.Pdf";
    
    /**
     * class constant
     */
    public static final String VERSION_1_0 = "1.0"; //  PDF version 1.0

    /**
     * class constant
     */    
    public static final String VERSION_1_1 = "1.1"; // PDF version 1.1

    /**
     * class constant
     */    
    public static final String VERSION_1_2 = "1.2";	// PDF version 1.2

    /**
     * class constant
     */    
    public static final String VERSION_1_3 = "1.3";	// PDF version 1.3

    /**
     * class constant
     */    
    public static final String VERSION_1_4 = "1.4"; // PDF version 1.4

    /**
     * class constant
     */    
    public static final String VERSION_1_5 = "1.5"; // PDF version 1.5

    /**
     * class constant
     */    
    public static final String VERSION_1_6 = "1.6"; // PDF version 1.6

    /**
     * class constant
     */    
    public static final String VERSION_1_7 = "1.7"; // PDF version 1.7
    
    /**
     * class constant
     */    
    public static final String VERSION_UNKNOWN = "UNKNOWN"; //Unknown PDF version (the default)

    /**
     * Determines whether or not the file is a PDF file when metadata about this
     * file is not available.
     * 
     * @param filePath
     *            absolute path to an existing readable file
     * @return whether or not its a PDF file
     * @throws FatalException
     */
    public static boolean isType(String filePath) throws FatalException {
        return isType(filePath, null);
    }

    /**
     * Determines if this file is a PDF when metadata about this file is
     * available.
     * 
     * Determines this by looking for the PDF header anywhere within the first
     * 1024 bytes. Can be any of: <code>%PDF-1.0</code>,
     * <code>%PDF-1.1</code>,<code>%PDF-1.2</code>,<code>%PDF-1.3</code>,
     * <code>%PDF-1.4</code>,<code>%PDF-1.5</code>. Normally the header
     * is the first line of the PDF but section 3.4.1 of the PDF 1.4 spec says
     * that the header can be anywhere within the first 1024 bytes. It also says
     * that the header <code>%!PS-Adobe-N.n PDF-M.m</code> is acceptable, so
     * this is looked for too.
     * 
     * @return whether or not its a Pdf file
     * @param filePath 	- absolute path to an existing readable file
     * @param _metadata - metadata about this DataFile
     * @throws FatalException
     */
    public static boolean isType(String filePath, METSDocument _metadata)
            throws FatalException {

        String methodName = "isType(String, DataFile)";

        boolean isType = false;

        Pdf tempPdf = new Pdf();

        // check that filePath is != null and points to an existing file
        if (!FileUtil.isGoodFile(filePath)) {
            Informer.getInstance().fail(CLASSNAME, methodName,
                "Illegal argument", "filePath: " + filePath,
                new FatalException("Not an existing, readable absolute path to a file"));
        }

        ByteReader tempReader = null;
        File theFile = new File(filePath);
        // get the file size just in case there aren't 1024 bytes in the file to
        // look for the
        // header in
        tempPdf.setSize(theFile.length());
        tempReader = new ByteReader(theFile, "r");

        // look for the header
        if (tempPdf.getSize() > 0 && tempPdf.foundHeader(tempReader, false)) {
            isType = true;
        }

        tempPdf = null;
        tempReader.close();
        theFile = null;

        return isType;
    }

    /**
     * Test driver
     * 
     * @param args
     *            not used
     * @throws FatalException
     * @throws PackageException
     */
    public static void main(String[] args) throws PackageException,
            FatalException {
		// check for command line options	
		String testFile = null;
		String packagePath = null;
		String initDesc = null;
		if (args.length == 6 && args[0].equals("-p") && args[2].equals("-f") & args[4].equals("-d")) {
			packagePath = args[1];
			testFile = args[1] + args[3];
			initDesc = args[1] + args[5];
		}
		else {
			printUsage();
			System.exit(1);
		}
		
		// isType, parsing, validation test code
		if (Pdf.isType(testFile, null)) {            
			SIP sip = new SIP(packagePath);
			DataFileFactory.getInstance(sip);
			
			sip.setMigratedDir(packagePath + "migrateDir/");
			sip.setNormalizedDir(packagePath + "normDir/");
			sip.setLinksDir(packagePath + "link/");
			
 			METSDescriptor md = new METSDescriptor(initDesc, sip);
 			sip.setInitialDescriptor(md);
 			DataFile pdfFile = DataFileFactory.getInstance(sip).createDataFile(null,testFile, false, null);
            
            pdfFile.localize(); // phase 1
            pdfFile.localize();	// phase 2
            
            System.out.println(pdfFile.toString());
		} else {
			// not a PDF file
			System.out.println("File " + testFile + " is not an Pdf file");
		}
    }

    /**
	 * Print the command line usage of this class.
	 */
	private static void printUsage() {
		System.out.println("java Pdf [-p packagePath] [-f filename] [-d metsDesc]");
	}
	
    /**
     * The byte offset to the byte after the header.
     */
    private long afterHeaderPos = -1;

    /**
     * The byte offset to the byte after the linearization dictionary. This
     * points to the byte after the "endobj"
     */
    private long afterLinDictPos = -1;

    /**
     * The PDF document (single bitstream) contained in the PDF file
     */
    private PdfDocument doc = null;

    /**
     * The PDF header. This only includes the part up until the end of the
     * version string. It does not include the binary characters after the version.
     */
    private String header = null;

    /**
     * A list of known Pdf-specific inhibitors, not necessarily found in this PDF
     */
    private PdfInhibitors inhibs = null;

    /**
     * A list of known limitations in relate to processing PDF file.
     */
    private PdfLimitations limitations = null;
    
    /**
     * Whether or not this PDF is encrypted (affects the Strings and Streams).
     */
    private String isEncrypted = UNKNOWN;

    /**
     * The number of images contained in this PDF.
     */
    private int numImages = 0;
    
    /**
     * A parsing flag to set when a feature is encountered
     * which makes it not a fully conforming PDF/A document.
     */
    private String isPDFAFullConf = UNKNOWN;
    
    /**
     * A parsing flag to set when a feature is encountered
     * which makes it not a minimally conforming PDF/A document.
     */
    private String isPDFAMinConf = UNKNOWN;

    /**
     * The offset to the start of the trailer (trailer keyword)
     */
    private long offsetEof = -1L;
  
    /**
     * a hash table that retain all external references (original) with 
     * the associated pdf object number.  
     */
    private Map m_originalLinks = null;
    
    /**
     * base URI to be used by the relative URI action in the document.
     */
    private String baseURI = null;
    /**
     * Not for public use. Only used to access instance methods during
     * <code>isType(String)</code>.
     */
    
    /**
     * PdfManager, it is needed for all pjx calls.
     */
    private PdfManager m_pm = null;
    
    /* The JHOVE's PdfModule */
    private final String jhovePdfModuleName = "pdf-hul";
    private final String pdfA1aProfile = "ISO PDF/A-1, Level A";
    private final String pdfA1bProfile = "ISO PDF/A-1, Level B";
    
    private Pdf() {
    }

    /**
     * The constructor to call for an existing Pdf file when metadata about this
     * file is not available.
     * 
     * @param path
     *            the absolute path to an existing readable file
     * @param ip
     *            the Information Package that this file is part of
     * @throws FatalException
     */
    public Pdf(String path, InformationPackage ip) throws FatalException {
        super(path, ip);

        this.setByteOrder(DataFile.BYTE_ORDER_NA);
        this.setMediaType(MimeMediaType.MIME_APP_PDF);
        this.setFileExt(Extension.EXT_PDF);
        this.setMediaTypeVersion(VERSION_UNKNOWN);

        // build lists of potential anomalies, inhibitors, etc.
        this.anomsPossible = null;
        this.anomsPossible = new PdfAnomalies();
        this.inhibs = new PdfInhibitors();
        this.limitationsPossible = null;
        this.limitationsPossible = new PdfLimitations();
        this.m_originalLinks = new HashMap();
    }

    /**
     * The constructor to call for an existing Pdf file when when metadata about
     * this file is available.
     * 
     * @param path
     *            the absolute path to an existing readable file
     * @param ip
     *            the Information Package that this file is part of
     * @param _metadata
     *            a lite DataFile containing metadata about this DataFile
     * @throws FatalException
     */
    public Pdf(String path, InformationPackage ip, METSDocument _metadata)
            throws FatalException {
        this(path, ip);
        // allow for the DataFileFactory to send a null METSDocument in
        if (_metadata != null) {
            this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
        }
    }

    /**
     * The destructor
     * This method shall be called by garbage collector whenever this object is destroyed.  
     * This is suppose the place to perform housekeeping tasks such as freeing up system 
     * resource.  However, all members show up to be null during debugging.  This could be due 
     * to a bug in java.  Therefore, we need to call cleanup method at other places to make sure  
     * resources is free (see localized and parse method).
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
    	cleanup();
     	super.finalize();
	    }
    
    /**
     * self clean up.  Free up all system resources used in this object.
     */
    private void cleanup() {
        // close the file handle used by the PdfManager
		if (m_pm != null) {
		    if (m_pm.getReader().getInput() instanceof PdfInputFile) {
		    	try {
		    	((PdfInputFile) m_pm.getReader().getInput()).close();
		    	} catch (IOException e) {
		    	}
		    }
		    m_pm = null;
		}
    }
 
    /**
     * Sets the members for which their value depends on the value of the
     * members that were set by parsing this file. In other words, this is the
     * 'second phase' of setting this file's members. The first was the parsing
     * phase.
     * 
     * @throws FatalException
     */
    protected void evalMembers() throws FatalException {
        super.evalMembers();

        // evaluate the members for each pdf stream found
        // in this pdf
        Iterator bsIter = this.getBitstreams().iterator();
        while (bsIter.hasNext()) {
            PdfDocument ps = (PdfDocument) bsIter.next();
            ps.evalMembers();
        }
        // figure out which (if any) profiles this file complies with

    }

    /**
     * Locates the last EOF marker in the last 1028 bytes of the file. If it is
     * found it sets the offset to the %%EOF in <code>offsetEof</code>.
     * 
     * @return whether or not the EOF marker was found
     * @throws FatalException
     */
    private boolean foundEof() throws FatalException {
        String methodName = "foundEof()";

        boolean foundTrailer = false;

        // try the obvious first - see if the %%EOF is in the last 6 bytes of
        // the file
        this.reader.seek(this.getSize() - 6);
        byte[] last6Bytes = new byte[6];
        int numRead = this.reader.read(last6Bytes);
        if (numRead != 6) {
            Informer.getInstance().fail(this, methodName, "IO problem",
                    "file: " + this.getPath(),
                    new FatalException("Could not read last 6 bytes"));
        }
        int lastRead = 4;
        while (!foundTrailer && lastRead < 6) {
            if (last6Bytes[lastRead - 4] == 0x25
                    && last6Bytes[lastRead - 3] == 0x25
                    && last6Bytes[lastRead - 2] == 0x45
                    && last6Bytes[lastRead - 1] == 0x4f
                    && last6Bytes[lastRead - 0] == 0x46) {
                foundTrailer = true;
                this.offsetEof = (this.getSize() - 6) + (lastRead - 4);
            }
            lastRead++;
        }
        this.reader.goBack();

        if (!foundTrailer) {
            //	seek to the last %%EOF comment in the last 1024 bytes of the file
            long lastKBytesOffset = this.getSize() - 1024;
            if (lastKBytesOffset < 0) {
                lastKBytesOffset = 0;
            }
            this.reader.seek(lastKBytesOffset);
            byte[] lastKBytes = new byte[1024];
            numRead = this.reader.read(lastKBytes);
            lastRead = 4;
            // find the LAST instance of %%EOF
            while (lastRead < numRead) {
                if (lastKBytes[lastRead - 4] == 0x25
                        && lastKBytes[lastRead - 3] == 0x25
                        && lastKBytes[lastRead - 2] == 0x45
                        && lastKBytes[lastRead - 1] == 0x4f
                        && lastKBytes[lastRead - 0] == 0x46) {
                    foundTrailer = true;
                    this.offsetEof = (this.getSize() - 1024) + (lastRead - 4);
                }
                lastRead++;
            }

            this.reader.goBack();
        }
        //System.out.println("Found trailer: " + foundTrailer);
        return foundTrailer;
    }

    /**
     * Attempts to find the PDF header. It looks for it anywhere in the first
     * 1024 bytes of the file (because the PDF spec allows it). The more common
     * header is 8 bytes and looks like <code>%PDF-1.x</code> where x can be
     * 0-5 inclusive. The less common header (though allowed by the PDF spec)
     * looks like <code>%!PS-Adobe-N.n PDF-M.m</code>.
     * 
     * @param theReader 	- a reader already reading this file
     * @param checkBinary 	- whether or not to check if it has the binary 
     * 						characters after the version number
     * @return <code>true</code> if the header was found, else
     *         <code>false</code>
     * @throws FatalException
     */
    private boolean foundHeader(ByteReader theReader, boolean checkBinary)
            throws FatalException {

        String methodName = "foundHeader(ByteReader, boolean)";

        boolean foundHeader = false;
        // read 1024 bytes or less if the file size is less than that
        byte[] firstBytes = new byte[(int) Math.min(1024, this.getSize())];

        theReader.seek(0);
        int numRead = theReader.read(firstBytes);

        ByteBuffer bb = ByteBuffer.allocate(firstBytes.length);
        bb = bb.put(firstBytes);
        bb.position(0);
        CharBuffer cb = null;
        try {
            cb = Charset.forName("ISO-8859-1").newDecoder().decode(bb);
        } catch (CharacterCodingException e) {
            Informer.getInstance().fail(this, methodName,
                    "Can't map character to Unicode",
                    "In header of " + this.getPath(), e);
        }

        Pattern pdfHeader = Pattern
                .compile("%(!PS-Adobe-\\d\\.\\d )?PDF-\\d\\.\\d");
        Matcher m = pdfHeader.matcher(cb);
        if (m.find()) {
            this.header = m.group(); // save header to extact version
            foundHeader = true;
        }

        // look to see if it has the binary data after the version (reqd by PDF/A)
        // and useful to know where to start looking for the linearization dictionary
        if (foundHeader & checkBinary) {
            boolean isBinary = true;

            // move the file pointer to the character after the version number
            theReader.seek(m.end());
            char c = (char) theReader.readBytes(1, DataFile.BYTE_ORDER_NA);
            // get past any white space chars
            while (PdfObjectCaster.isWhiteSpace(c)) {
                c = (char) theReader.readBytes(1, DataFile.BYTE_ORDER_NA);
            }
            if (c == '%') {
                // read the next 4 chars to see if they are 'binary'
                long[] biChars = new long[4];
                for (int i = 0; i < biChars.length; i++) {
                    biChars[i] = theReader.readBytes(1, DataFile.BYTE_ORDER_NA);
                    if (biChars[i] < 128 || biChars[i] > 255) {
                        isBinary = false;
                    }
                }

                // record the file pointer - we want to know where the actual
                // header ends
                // so that we can look for the linearization dictionary after
                // this position.
                this.afterHeaderPos = theReader.getFilePointer();
                //System.out.println("_afterHeaderPos: " + _afterHeaderPos);

                if (isBinary) {
                    this.addFormatAttribute(Pdf.Attribute.HAS_BIN_HDR);
                } else {
                    this.removeFormatAttribute(Pdf.Attribute.HAS_BIN_HDR);
                    ;
                }
            }
        }

        return foundHeader;
    }

    /**
     * Extracts the PDF file version from the PDF header.
     * 
     * @return whether or not the PDF version was found
     * @throws FatalException
     */
    private boolean foundVersion() throws FatalException {
        boolean foundVersion = false;

        //	would already have the header now - get the version from it
        if (this.header != null
                && (this.header.length() == 8 || this.header.length() == 22)
                && this.header.charAt(this.header.length() - 2) == '.'
                && Character.isDigit(this.header.charAt(this.header.length() - 3))
                && Character.isDigit(this.header.charAt(this.header.length() - 1))) {
            foundVersion = true;
            setMediaTypeVersion(this.header.substring(this.header.length() - 3,
                    this.header.length()));
        }

        return foundVersion;
    }

    /**
     * @return whether or not the file is encrypted
     */
    public String isEncrypted() {
        return isEncrypted;
    }

    /**
     * Creates a file or group of files that is the normalized version of this
     * file.
     * 
     * @throws PackageException
     * @throws FatalException
     */
    public void normalize() throws PackageException, FatalException {
    	String methodName = "normalize()";
        
        boolean keepNormalizing = normalizeSetup();
     
        // exit early if we already normalized or this file
        // should not be normalized.
        if (!keepNormalizing){
            return;
        }
         
        String newFilePath = null; // new file to create (container file)
        String normForm = null; // normalization format
        String specProc = null; // specific transformation procedure for event reporting
        String genProc = Procedure.TRANSFORM_NORM; // general transformation procedure
        
        //      put in separate norm dir
        keepNormalizing = transformStart(genProc,   this.getFormatCode(), true); 

        // get the normalization form from the configuration files
        if (keepNormalizing){
            String formProp = "NORM_" + this.getFormatCode() + "_FORMAT";
            normForm = 
                ArchiveProperties.getInstance().getArchProperty(formProp);
        }

        if (keepNormalizing && normForm.equals(TransformationFormat.PDF_NORM_1)) {
            // set the event procedure
            specProc = Procedure.PROC_PDF_NORM_1;
            
            //quick normalization if there is no PDF page.  No need to through a warning since
            // it is already checked at the end of parsing.
            if (keepNormalizing && (this.getBitstreams() == null
                || this.getBitstreams().size() != 1
                || !(this.getBitstreams().elementAt(0) instanceof PdfDocument)
                || ((PdfDocument) this.getBitstreams().elementAt(0)).getNumPages() < 1)) {
                // done normalizing; exit
                keepNormalizing = false;
            }
            
            // this normalization procedure is only currently supported on linux
            if (keepNormalizing) {
                
                // create a tiff image for every pdf page
                ExternalProgram prog = new ExternalProgram();

                // construct the ideal dir name
                String dirName = this.getOid() + "_" + normForm;

                File normDirPath = new File(this.getIp().getNormalizedDir(),dirName);
                
                // make sure directory is unique and doesn't already exist
                int i = 1;
                while (normDirPath.exists()){
                    dirName = this.getOid() + "_" + normForm + "_" + i;
                    normDirPath = new File(this.getIp().getNormalizedDir(), dirName);
                    i++;
                }
                
                // make directory within normalization directory
                normDirPath.mkdirs();
                File outputFile = new File(normDirPath, "page%d.tiff");

                String outputPath = outputFile.getAbsolutePath();
                // construct the file name to be normalized
                //String fileToNorm = FileUtil.replaceString(this.getPath(), " ", "\\ ");
                String fileToNorm = this.getPath();
                
                // tell DataFileFactory to treat these files as created by the archive
                long numPages = ((PdfDocument) this.getBitstreams().elementAt(0)).getNumPages();
                for (long j=1; j<=numPages; j++){
                    // inform dff of each future tiff name
                    String tiffPath = normDirPath + File.separator + "page" + j + ".tiff";
                    DataFileFactory.getInstance().addFutureOriginArchivePath(tiffPath);
                }
                
                // construct command to create tiffs from each pdf page
                String cmdProperty = ArchiveProperties.getInstance().getArchProperty("PDF_TO_TIFF_CMD");
 
                String[] command = cmdProperty.split("\\s");
                // In order to handle space in file name, we need to replace the 
                // following arguments after the command has been split.
                for (i = 0; i < command.length; i++) {
        			command[i] = command[i].replaceFirst("%INPUT_FILE%", fileToNorm);
        			command[i] = command[i].replaceFirst("%OUTPUT_FILE%", outputPath);
        		}           
               
                // create the tiffs
                prog.executeCommand(command);

                if (prog.getExitValue() == 0) // success, no error
                {
	                // create the METS-described XML wrapper
	                Comparator alpha = new AlphaComparator();
	                String parentDirPath = normDirPath.getAbsolutePath();
	                String fileName = this.getOid() + "_NORM." + Extension.EXT_XML;
	                newFilePath = Factory.createDirectoryDescriptor(parentDirPath, fileName, alpha);
	                
	                // create DataFile, add events and relationships
	                transformEnd(genProc, newFilePath, specProc);
                } else { // encounter error
                	/* ghostscript does not give us different error code/message for system 
                	 * errors or error in PDF files.  thus we need to find out what kind of 
                	 * error by looking at the ghostscript command output*/
                	String cmdOutput = prog.getOutput();
                	/* if the command output contains "/undefinedresource", 
                	 * we will record a limitation as we cannot normalize pdf file 
                	 * whose resource (such as font) is not defined or not embedded. */
                	if (cmdOutput != null && ( cmdOutput.contains("/undefinedresource") || cmdOutput.contains("/undefined in findresource"))) {
            			// unsupported resource, report as a limitation
            	    	super.addLimitation(getLimitationsPossible().getSevereElement(
            				PdfLimitations.PDF_UNSUPPORTED_RESOURCE));	
		        } else {
                		/* otherwise, this error is likely related to system (such as out of space) 
                		 * or command line error (such as bad file name).  In this case, we want 
                		 * to reject the package create a nice print-out ready String of the command */
	            		StringBuffer theCommand = new StringBuffer();
	            		for (i = 0; i < command.length; i++) {
	            			theCommand.append(" " + command[i]);
	            		}
	            		
	                   	Informer.getInstance().error(this, methodName,
	                        "Encounter problems during " + theCommand,
	                        " Error Output: " + cmdOutput + " File:" + this.getPath(),                                
	                        new PackageException("Normalization Problem"));
	                	}
                }
                // clean up file references
                normDirPath = null;
                outputFile = null;
                
            } else if (keepNormalizing) {
                // unsupported platform
                Informer.getInstance().warning(this,
                        methodName,
                        "Normalization problem: unsupported OS",
                        "OS: " + ArchiveProperties.getOs() + 
                        " File: " + this.getPath());
                // end of normalization for this file
                Informer.getInstance().info(this,methodName, "Done " + genProc 
                		+ " at " + DateTimeUtil.now() + 
                        " (UNSUPPORTED OS - NO NORMALIZATION)",
                        "File: " + this.getPath(), false);
		    }
        }  else {
            // put other normalization procedures here  
        }
    }

    /**
     * Parses itself and sets format-specific members. For roots of distributed
     * objects, also recognizes links to external files and creates Link objects
     * and adds them to the links Vector using addLink(Link). MUST NOT BE CALLED
     * ON ANY FILE BUT A PDF OR A FATAL EXCEPTION WILL BE THROWN. Always call
     * <code>isType(String)</code> on a file before calling this method.
     * Specifically it parses the trailer dictionary, catalog dictionary and
     * document information dictionary.
     * 
     * @throws FatalException
     *  
     */
    protected void parse() throws FatalException {

        String methodName = "parse()";

        boolean keepParsingFile = true;

        // get the header and check to see if it has the recommended
        // binary characters
        if (!foundHeader(this.reader, true)) {
            // not a PDF! this parse should never have called on this file
            Informer.getInstance().fail(this, methodName, "Not a PDF",
                    "file: " + this.getPath(),
                    new FatalException("Pdf.parse() can only be used on PDFs"));
            keepParsingFile = false;
        }

        // set the PDF version
        if (keepParsingFile && !foundVersion()) {
            // this is an exception - not an anomaly. there should have been a
            // version in this header if we recognized it as a header
            Informer.getInstance().fail(this, methodName, "Bad file header",
                    "file: " + this.getPath(),
                    new FatalException("Not a PDF header: " + this.header));
        }

        // start building the PDF document
        this.doc = new PdfDocument(this);
        this.doc.setRole(BitstreamRole.DOC_MAIN);
        this.addBitstream(doc);

        // see if the file is linearized
        if (keepParsingFile) {
            parseForLinearization();
        }

        // locate the EOF marker
        if (keepParsingFile && !foundEof()) {
            // error with file - stop parsing it
            SevereElement pa = this.getAnomsPossible().getSevereElement(
                    PdfAnomalies.PDF_NO_TRAILER);
            this.addAnomaly(pa);
            keepParsingFile = false;
        }

        // prepare to read the PDF's trailer and cross-ref. dictionaries
        try {
            m_pm = new PdfManager(new PdfReader(new PdfInputFile(new File(this
                    .getPath()))));
        } catch (PdfFormatException e1) {
            // found an anomaly - find out which one
            String mesg = e1.getMessage();
            String anomName = PdfAnomalies.interpretPdfFormatExceptionMessage(mesg);
            SevereElement pa = this.getAnomsPossible().getSevereElement(anomName);
            this.addAnomaly(pa);
            keepParsingFile = false;
        } catch (IOException e) {
            // shouldn't happen - but anyway:
            Informer.getInstance().fail(this,methodName,"I/O Error",
                "file: " + this.getPath(),
                new FatalException("Can't open file, parse it or get its size"));
        }

        if (keepParsingFile) {
            // retrieve the required trailer dictionary
            PdfDictionary tDict = m_pm.getTrailerDictionary();
            Map tDictMap = tDict.getMap();
            if (tDictMap.containsKey(new PdfName("Encrypt"))) {
            	Map tNewMap = new HashMap(tDictMap);
            	tNewMap.remove(new PdfName("Encrypt"));
                // The file has an encryption dictionary
                this.addInhibitor(PdfInhibitors
                        .getSevereElement(PdfInhibitors.PDF_ENCRYPTION));
                this.setIsEncrypted(DataFile.TRUE);
            } else {
                this.setIsEncrypted(DataFile.FALSE);
            }

            // see if there's the required catalog dictionary
            PdfReference rootRef = (PdfReference) tDictMap.get(new PdfName(
                    "Root"));
            if (rootRef == null) {
                // no catalog dictionary!
                SevereElement pa = this.getAnomsPossible().getSevereElement(
                        PdfAnomalies.PDF_NO_ROOT);
                this.addAnomaly(pa);
                keepParsingFile = false;
            } else {
                //	retrieve the required catalog dictionary and record any
                //  info from the catalog dictionary that's relevant
                boolean wasSuccess = parseCatDict(rootRef);
                if (!wasSuccess) {
                    keepParsingFile = false;
                }
            }

            // retrieve the optional document information dictionary if included
            // and record any relevant info
            PdfReference infoRef = (PdfReference) tDictMap.get(new PdfName("Info"));
            if (infoRef != null) {
                boolean wasSuccess = parseDocInfoDict(infoRef);
                // Actually problems with the document information dictionary
                // shouldn't affect the ability to parse the rest of the PDF, 
                // so we don't care about the return value here.
            }

            // parse the PDF document's pages
            if (keepParsingFile) {
                parsePages();
            }
        }
        
        // make sure that there are PDF pages to normalize
        if ((this.getBitstreams() == null || this.getBitstreams().size() != 1
                || !(this.getBitstreams().elementAt(0) instanceof PdfDocument)
                || ((PdfDocument) this.getBitstreams().elementAt(0)).getNumPages() < 1)) {
            
            Informer.getInstance().warning(this, methodName,
				"No PDF page in this PDF file.","Pdf file: " + this.getPath());
        	this.addAnomaly(this.getAnomsPossible().getSevereElement(PdfAnomalies.PDF_NO_PAGE));
        }
        // If this is already localized file, perform a self clean up to free up
        // system resources since we don't have to localize this file.
        if (this.getPath().indexOf("_LOC") != -1)
        	cleanup();
        /* check for possible PDF/A-1b conformance */
        checkPDFAConformance();
    }

    /**
     * Reads the PDF's Catalog Dictionary and records any relevant
     * information/anomalies in the PDF object. The only required entries in the
     * catalog dictionary are <code>Type</code> and <code>Pages</code>, all
     * the others are optional. See section 3.6.1 of the Pdf 1.4 spec.
     * @param rootRef 
     *
     * @return whether or not the catalog dict. was successfully read
     * @throws FatalException
     */
    private boolean parseCatDict(PdfReference rootRef)
            throws FatalException {

        String methodName = "parseCatDict(PdfManager, PdfReference)";

        boolean readSuccessfully = true;

        PdfDictionary catDict = null;
        try {
            // retrieve the Catalog Dictionary object
            catDict = (PdfDictionary) m_pm.getObjectIndirect(rootRef);
        } catch (PdfFormatException e2) {
            //	found an anomaly - find out which one
            String mesg = e2.getMessage();
            String anomName = PdfAnomalies
                    .interpretPdfFormatExceptionMessage(mesg);
            SevereElement pa = this.getAnomsPossible().getSevereElement(
                    anomName);
            this.addAnomaly(pa);
            readSuccessfully = false;
        } catch (IOException e2) {
            Informer.getInstance().fail(this, methodName, "I/O Exception",
                    "file: " + this.getPath(), e2);
        }

        if (catDict != null && readSuccessfully) {
            // record relevant catalog dictionary elements
            // don't care about some of the elements

            // keep track of which required entries have been seen
            boolean sawType = false;
            boolean sawPages = false;
            boolean sawOutline = false; // not a reqd field but used to set a member

            // objects to reuse
            PdfName nameValue = null;
            PdfObject pObj = null;
            String stringValue = null;

            Map catDictMap = catDict.getMap();
            Set catDictEntries = catDictMap.keySet();
            Iterator catDictEIter = catDictEntries.iterator();

            try {
	            while (catDictEIter.hasNext()) {
	                PdfName pName = (PdfName) catDictEIter.next();
	                String catDictEntry = pName.getString();
	//                System.out.println("\tCat Dict Entry: " + catDictEntry);
	
	                if (catDictEntry.equals("Type")) {
	                    sawType = true;
	                    nameValue = (PdfName) (catDictMap.get(pName));
	                    if (!nameValue.getString().equals("Catalog")) {
	                        // this is required to be of type Catalog
	                        this.addAnomaly(this.getAnomsPossible()
	                           .getSevereElement(PdfAnomalies.PDF_BAD_CATALOG_DICT));
	                    }
	                } else if (catDictEntry.equals("Version")) {
	                    nameValue = (PdfName) (catDictMap.get(pName));
	                    stringValue = nameValue.getString();
	                    if (stringValue != null) {
	                        // make sure the string length isn't too long
	                        if (stringValue.length() > DataFile.MAX_MTYPEVERS_LENGTH) {
	                            stringValue = stringValue.substring(0,MAX_MTYPEVERS_LENGTH);
	                        }
	                        System.out.println("Is really version: " + stringValue);
	                        this.setMediaTypeVersion(stringValue);
	                    }
	                } else if (catDictEntry.equals("Pages")) {
	                    sawPages = true;
	                } else if (catDictEntry.equals("PageLayout")) {
	                    nameValue = (PdfName) (catDictMap.get(pName));
	                    stringValue = nameValue.getString();
	                    if (stringValue.equals("SinglePage")
	                            || stringValue.equals("OneColumn")
	                            || stringValue.equals("TwoColumnLeft")
	                            || stringValue.equals("TwoColumnRight")) {
	                        // NOT STORING THIS INFO
	                        //this.doc.setPageLayout(stringValue);
	                    } else {
	                        // illegal page layout value
	                        SevereElement pa = this.getAnomsPossible().getSevereElement(
	                                        PdfAnomalies.PDF_BAD_PAGELAYOUT);
	                        this.addAnomaly(pa);
	                    }
	                } else if (catDictEntry.equals("PageMode")) {
	                    nameValue = (PdfName) (catDictMap.get(pName));
	                    stringValue = nameValue.getString();
	                    if (stringValue.equals("UseNone")
	                            || stringValue.equals("UseOutlines")
	                            || stringValue.equals("UseThumbs")
	                            || stringValue.equals("FullScreen")) {
	                        // NOT STORING THIS INFO
	                        //this.doc.setPageMode(stringValue);
	                    } else {
	                        // illegal page layout value
	                        SevereElement pa = this
	                                .getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_BAD_PAGEMODE);
	                        this.addAnomaly(pa);
	                    }
	                } else if (catDictEntry.equals("Outlines")) {
	                    this.doc.setHasOutline(TRUE);
	                } else if (catDictEntry.equals("OpenAction")) {
	                    pObj = (PdfObject) catDictMap.get(pName);
	                    if (pObj instanceof PdfArray) {
	                        //	its an array containing a Destination
	                        this.doc.addAction(PdfAction.TYPE_ACTIVATION, PdfAction.A_GOTO);
	                    } else if (pObj instanceof PdfDictionary) {
	                        // actually haven't seen this used and can't see how you can
	                        // get this used using Adobe Acrobat. Anyway, the spec
	                        // says it's possible.
	                        Map openActionDictMap = ((PdfDictionary) pObj).getMap();
	                        Set openActionDictEntries = openActionDictMap.keySet();
	                        Iterator entryIter = openActionDictEntries.iterator();
	                        while (entryIter.hasNext()) {
	                            PdfName aName = (PdfName) entryIter.next();
	                            String aDictEntry = aName.getString();
	                            if (PdfAction.isValidActionType(aDictEntry)) {
	                                this.doc.addAction(PdfAction.TYPE_ACTIVATION, aDictEntry);
	                            } else {
	                                // unknown document action type
	                                this.addAnomaly(this.getAnomsPossible()
	                                    .getSevereElement(
	                                    PdfAnomalies.PDF_UNKNOWN_ACTION));
	                            }
	                        }
	                    } else {
	                        // wrong type
	                        this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                    }
	                } else if (catDictEntry.equals("AA")) {
	                    pObj = (PdfObject) catDictMap.get(pName);
	                    if (pObj instanceof PdfReference) {
	                    	pObj = m_pm.getObjectIndirect((PdfReference) pObj);
	                    }
	                    if (!(pObj instanceof PdfDictionary)) {
	                        // wrong type
	                        this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                    }
	                    Map openActionDictMap = ((PdfDictionary) pObj).getMap();
	                    Set openActionDictEntries = openActionDictMap.keySet();
	                    Iterator entryIter = openActionDictEntries.iterator();
	
	                    while (entryIter.hasNext()) {
	                        PdfName aName = (PdfName) entryIter.next();
	                        String aDictEntry = aName.getString();
	                        if (PdfAction.isValidDocCatAa(aDictEntry)) {
	                            this.doc.addAction(PdfAction.TYPE_DOCUMENT, aDictEntry);
	                        } else {
	                            // unknown document action type
	                            this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_UNKNOWN_ACTION));
	                        }
	                    }
	                } // found an URI dictionary.  p116 and p625 of PDF 1.6 spec.
	                else if (catDictEntry.equals("URI")) {
	                    pObj = (PdfObject) catDictMap.get(pName);
	                    if (pObj instanceof PdfReference) {
	                    	pObj = m_pm.getObjectIndirect((PdfReference) pObj);
	                    }
	                    if (!(pObj instanceof PdfDictionary)) {
	                        //	wrong type
	                        this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                    } else {
	                        // safe to cast it to a PdfDictionary
	                        Map uriDictMap = ((PdfDictionary) pObj).getMap();
	                        if (uriDictMap.containsKey(new PdfName("Base"))) {
	                            // record the baseURI
	                            baseURI = ((PdfString) uriDictMap.get(new PdfName("Base"))).getString();
	                        }
	                    }
	                }
	                else if (catDictEntry.equals("MarkInfo")) {
	                    // might be a tagged PDF
	                    pObj = (PdfObject) catDictMap.get(pName);
	                    if (pObj instanceof PdfReference) {
	                    	pObj = m_pm.getObjectIndirect((PdfReference) pObj);
	                    }
	                    if (!(pObj instanceof PdfDictionary)) {
	                        //	wrong type
	                        this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                    } else {
	                        // safe to cast it to a PdfDictionary
	                        Map markInfoDictMap = ((PdfDictionary) pObj).getMap();
	                        if (markInfoDictMap.containsKey(new PdfName("Marked"))) {
	                            // check the value of the Marked field
	                            PdfBoolean markedValue = (PdfBoolean) markInfoDictMap
	                                    .get(new PdfName("Marked"));
	                            if (markedValue.getBoolean() == true) {
	                                this.doc.addProfile(PdfProfiles.PDF_TAGGED);
	                            }
	                        } else {
	                            //	bad mark information dictionary
	                            this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_BAD_MARKINFO_DICT));
	                        }
	                    }
	                } else if (catDictEntry.equals("Lang")) {
	                    pObj = (PdfObject) catDictMap.get(pName);
	                    if (!(pObj instanceof PdfString)) {
	                        //	wrong type
	                        this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                    } else {
	                        stringValue = ((PdfString) pObj).getString();
	                        if (stringValue != null) {
	                            // shorten the String length if necessary (to make
	                            // it database-compatible)
	                            if (stringValue.length() > PdfDocument.MAX_NATL_LANG_LENGTH) {
	                                stringValue = stringValue.substring(0, PdfDocument.MAX_NATL_LANG_LENGTH);
	                            }
	                            this.doc.setNatlLang(stringValue);
	                        }
	                    }
	                } else if (catDictEntry.equals("Metadata")) {
	                	// contains XML metadata stream
	                    pObj = (PdfObject) catDictMap.get(pName);
	                    if (pObj instanceof PdfReference) {
	                    	pObj = m_pm.getObjectIndirect((PdfReference) pObj);
	                    }
	                    if (pObj instanceof PdfStream) {
	                    	this.doc.setHasMetadata(TRUE);
	                    }
	                    else {
	                    	this.doc.setHasMetadata(FALSE);
	                        //	wrong type
	                        this.addAnomaly(this.getAnomsPossible()
	                            .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                    } 
	                }
	            }
        } catch (IOException e) {
        }
        	catch (PdfFormatException e) {
        } // done with try/catch
            // check for the required entries in the catalog dictionary
            if (!sawType) {
                this.addAnomaly(this.getAnomsPossible().getSevereElement(
                        PdfAnomalies.PDF_BAD_CATALOG_DICT));
                readSuccessfully = false;
            }
            if (!sawPages) {
                this.addAnomaly(this.getAnomsPossible().getSevereElement(
                        PdfAnomalies.PDF_BAD_CATALOG_DICT));
                //	if we can't follow the Pages field we can't parse any pages!
                readSuccessfully = false;
            }
            if (!sawOutline) {
                this.doc.setHasOutline(FALSE);
            }
        }

        //System.out.println("Read cat dictionary OK: " + readSuccessfully);

        return readSuccessfully;
    }

    /**
     * Reads the optional document information dictionary, recording any
     * relevant metadata and anomalies. All entries in the document information
     * dictionary are optional. See section 9.2.1 of the Pdf 1.4 spec. We are
     * just reading this to get the creation program and date.
     * 
     *
     *            the PDFManager (a convenience class for parsing PDFs)
     * @param infoRef
     *            a reference to the Info object (start of the doc info dict.)
     * @return whether or not the doc info dict. was successfully read
     * @throws FatalException
     */
    private boolean parseDocInfoDict(PdfReference infoRef)
            throws FatalException {
        boolean readSuccessfully = true;

        String methodName = "parseDocInfoDict(PdfManager, PdfReference)";

        PdfDictionary infoDict = null;
        try {
            infoDict = (PdfDictionary) m_pm.getObjectIndirect(infoRef);
        } catch (PdfFormatException e2) {
            //	found an anomaly - find out which one
            String mesg = e2.getMessage();
            String anomName = PdfAnomalies
                    .interpretPdfFormatExceptionMessage(mesg);
            this.addAnomaly(this.getAnomsPossible().getSevereElement(anomName));
            readSuccessfully = false;
        } catch (IOException e2) {
            Informer.getInstance().fail(this, methodName, "I/O Exception",
                    "file: " + this.getPath(), e2);
        }

        if (infoDict != null && readSuccessfully) {
            Map infoDictMap = infoDict.getMap();

            //	PRODUCER (not useful if Pdf is encrypted)
            // the software that created the PDF
            if (!this.isEncrypted().equals(TRUE)
                    && infoDictMap.containsKey(new PdfName("Producer"))) {
                PdfString producerValue = (PdfString) infoDictMap
                    .get(new PdfName("Producer"));
                String producerString = producerValue.getString();
                if (producerString != null) {
                    // make sure this value length will fit in the database
                    if (producerString.length() > DataFile.MAX_CPROG_LENGTH) {
                        producerString = producerString.substring(0,
                                DataFile.MAX_CPROG_LENGTH);
                    }
                    this.setCreatorProg(producerString);
                }
            }

            boolean haveDate = false; // flag for if we find a last modified or
            // creation date
            String dateString = null;

            // MOD DATE (last modification date)
            if (!this.isEncrypted().equals(TRUE)
                    && infoDictMap.containsKey(new PdfName("ModDate"))) {
                PdfObject pObj = (PdfObject) infoDictMap.get(new PdfName(
                        "ModDate"));
                if (pObj instanceof PdfString) {
                    dateString = ((PdfString) pObj).getString();
                    haveDate = true;
                } else {
                    // wrong type
                    this.addAnomaly(this.getAnomsPossible().getSevereElement(
                            PdfAnomalies.PDF_WRONG_TYPE));
                }

            } else {
                // see if the creation date is there - hey it's better than no
                // date
                if (!this.isEncrypted().equals(TRUE)
                        && infoDictMap.containsKey(new PdfName("CreationDate"))) {
                    PdfObject pObj = (PdfObject) infoDictMap.get(new PdfName(
                            "CreationDate"));
                    if (pObj instanceof PdfString) {
                        dateString = ((PdfString) pObj).getString();
                        haveDate = true;
                    }
                } 
            }

            if (haveDate && dateString != null) {
                // format the date in the way the archive wants it
                if (!DateTimeUtil.isBadPdfDate(dateString)) {
                    try {
                        String dateFormatted = DateTimeUtil
                                .convertDatePdf2Arch(dateString);
                        if (dateFormatted != null) {
                            this.setCreateDate(dateFormatted);
                        }
                    } catch (IllegalArgumentException e1) {
                        this.addAnomaly(this.getAnomsPossible()
                            .getSevereElement(PdfAnomalies.PDF_BAD_DATE_OBJECT));
                    }
                } else {
                    this.addAnomaly(this.getAnomsPossible().getSevereElement(
                        PdfAnomalies.PDF_BAD_DATE_OBJECT));
                }
            }
        }

        //System.out.println("Read doc info dictionary OK: " +
        // readSuccessfully);

        return readSuccessfully;
    }

    /**
     * 
     * @throws FatalException
     */
    private void parseForLinearization() throws FatalException {

        String methodName = "parseForLinearization()";

        if (this.afterHeaderPos >= 8) {
            this.reader.seek(this.afterHeaderPos);

            // read (1024-_afterHeaderPos) bytes or less if the file size is
            // less than that
            byte[] theBytes = new byte[(int) Math.min((1024 - afterHeaderPos),
                    this.getSize())];

            int numRead = reader.read(theBytes);

            ByteBuffer bb = null;
            bb = ByteBuffer.allocate(theBytes.length);
            bb = bb.put(theBytes);
            bb.position(0);
            CharBuffer cb = null;
            try {
                cb = Charset.forName("ISO-8859-1").newDecoder().decode(bb);
            } catch (CharacterCodingException e) {
                Informer.getInstance().fail(this, methodName,
                        "Can't map character to Unicode",
                        "In header of " + this.getPath(), e);
            }

            // possible whitespace, object number, one or whitespace, generation
            // number of 0, one or more whitespace, "obj", possible whitespace,
            // "<<", possible whitespace, possible any characters, "/Linearized"
            Pattern linDictStart = Pattern
                    .compile("\\s*\\d+\\s+0\\s+obj\\s*<<\\s*.*/Linearized[^>]*>>\\s*endobj");
            Matcher m = linDictStart.matcher(cb);
            if (m.find()) {
                this.header = m.group(); // save header to extact version
                this.addFormatAttribute(Pdf.Attribute.IS_LIN);
                //System.out.println("What was found: " + m.group());
                this.afterLinDictPos = (m.end() + afterHeaderPos);
            } else {
                this.removeFormatAttribute(Pdf.Attribute.IS_LIN);
            }
        }
    }

    /**
     * Parse each page looking for things we want to record (if it has images,
     * any filters, actions, annotations the document uses).
     * 
     * 
     *            a PdfManager
     * @return whether or not the pages were successfully parsed.
     * @throws FatalException
     */
    private boolean parsePages() 
    throws FatalException {

        boolean readSuccessfully = true;

        PdfPageTree pageTree = new PdfPageTree(m_pm);
        
        if (pageTree == null) {
        	readSuccessfully = false;
        	return readSuccessfully;
        }
        
        // get the number of pages
        int numPages = -1;
        try {
            numPages = pageTree.getNumberOfPages();
        } catch (PdfFormatException e3) {
            String anomName = PdfAnomalies
                    .interpretPdfFormatExceptionMessage(e3.getMessage());
            this.addAnomaly(this.getAnomsPossible().getSevereElement(anomName));
            readSuccessfully = false;
        } catch (IOException e3) {
            this.addAnomaly(this.getAnomsPossible().getSevereElement(
                    PdfAnomalies.PDF_BAD_PAGETREE));
            readSuccessfully = false;
        }

        if (readSuccessfully) {
            this.doc.setNumPages(numPages);
        }

        // this stores the number of images used per page for all pages in
        // the document. If more than one page uses the same picture, the 
        // picture is counted multiple times. this info is not currently 
        // stored in the database but if it ever is this method can be 
        // revised to only count the number of unique images in the document.
        numImages = 0;

        // parse each page
        boolean finished = false;
        int count = 0; // (page number -1)
        while (!finished && readSuccessfully && count < numPages) {
            // System.out.println("Parsing page " + (count+1));
            try {
                PdfReference thePage = (PdfReference) pageTree.getPage(count);
                // thePage must be a PdfDictionay or PdfPageTree.getPage(int) 
                // would have thrown a PdfFormatException
                Object obj = m_pm.getObjectIndirect(thePage);
                if (!(obj instanceof PdfDictionary)) {
                    this.addAnomaly(this.getAnomsPossible()
                    	.getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
                    readSuccessfully = false;
                    break;
                }

                // reuseable objects
                Object pageDictEntry = null;

                // PAGE DICTIONARY
                PdfDictionary pageDict = (PdfDictionary) obj;
                Map pageDictMap = pageDict.getMap();
                Set pageDictMapkeys = pageDictMap.keySet();
                Iterator pageDictMapkeysIter = pageDictMapkeys.iterator();
                while (pageDictMapkeysIter.hasNext()) {
                    pageDictEntry = pageDictMapkeysIter.next();
                    if (pageDictEntry instanceof PdfReference) {
                        pageDictEntry = m_pm.getObjectIndirect((PdfReference) pageDictEntry);
                    }
                    if (pageDictEntry instanceof PdfName) {
                        String pageDictEString = ((PdfName) pageDictEntry).getString();
         
                        // ANNOTATIONS
                        if (pageDictEString.equals("Annots")) {
                            	readSuccessfully = parsePageAnnotation(pageDictMap, pageDictEntry);
                        }
                        else if (pageDictEString.equals("AA")) {
                            // has page additional actions
                            PdfObject po = (PdfObject) pageDictMap.get(pageDictEntry);
                            if (po instanceof PdfReference) {
                                po = m_pm.getObjectIndirect((PdfReference) po);
                            }
                            if (po instanceof PdfDictionary) {
                                Map aaMap = ((PdfDictionary) po).getMap();
                                Iterator aaIter = aaMap.keySet().iterator();
                                while (aaIter.hasNext()) {
                                    po = (PdfObject) aaIter.next();
                                    if (po instanceof PdfReference) {
                                        po = m_pm.getObjectIndirect((PdfReference) po);
                                    }
                                    if (po instanceof PdfName) {
                                        String aaString = ((PdfName) po).getString();
                                        if (PdfAction.isValidPageAa(aaString)) {
                                        	System.out.print(aaString);
                                            this.doc.addAction(PdfAction.TYPE_PAGE, aaString);
                                        } else {
                                            // unknown page add. action type
                                            this.addAnomaly(this.getAnomsPossible()
                                                .getSevereElement(
                                                PdfAnomalies.PDF_UNKNOWN_ACTION));
                                        }
                                    } else {
                                        // wrong type
                                        this.addAnomaly(this.getAnomsPossible()
                                            .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
                                    }
                                }
                            } else {
                                System.out.println("Not a dict");
                                // wrong type
                                this.addAnomaly(this.getAnomsPossible()
                                    .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
                            }
                            //	done with page dictionary AA
                        } else if (pageDictEString.equals("Thumb")) {
                            // has thumbnail image for this page
                            this.doc.setHasThumbnails(TRUE);
                        }
                    } // done with the page dictionary entry as a PdfName
                } // done iterating through all entries in the page dictionary

                // we now know whether or not the PDF has thumbnails - take
                // away the unknown value
                if (!this.doc.hasThumbnails().equals(TRUE)) {
                    this.doc.setHasThumbnails(FALSE);
                }

                // add the inherited resources to this page dictionary?
                // PdfDictionary allAttributes =
                // pageTree.inheritAttributes((PdfDictionary)obj);

                PdfPageObjects pageObjects = new PdfPageObjects(m_pm);

                // get a HashSet of all the resources (PdfObjects) that this
                // page uses
                Set resources = pageObjects.getReferenced((PdfDictionary) obj);

                // System.out.println("Num resources on page " + (count+1) + ": " + resources.size());
                if (resources != null) {
                	parsePageResources(resources);
                } // done checking that the resources is not null
            } catch (PdfFormatException e) {
                String anomName = PdfAnomalies
                        .interpretPdfFormatExceptionMessage(e.getMessage());
                SevereElement pa = this.getAnomsPossible()
                        .getSevereElement(anomName);
                this.addAnomaly(pa);
                readSuccessfully = false;
            } catch (IOException e) {
                readSuccessfully = false;
            } // done with try/catch
            count++;
            // loop back up to read next page
        } // done reading all the pages

        if (numImages == 0) {
            this.doc.setHasImages(FALSE);
        }

        return readSuccessfully;
    }

    /**
     * Parse the annotation in the  page and record information about
     * annotations, annotation action, annotation additional actions and
     * external URI.
     * @param pageDictMap	- page dictionary map
     * @param pageDictEntry - page dictionary entry.
     * @return whether or not the pages were successfully parsed.
     * @throws FatalException
     */
    private boolean parsePageAnnotation(Map pageDictMap,Object pageDictEntry) 
    throws FatalException {
        boolean readSuccessfully = true;
        int annotRef = 0;
        
        // get the array
        try {
	        PdfObject po = (PdfObject) pageDictMap.get(pageDictEntry);
	        if (po instanceof PdfReference) {
	            po = m_pm.getObjectIndirect((PdfReference) po);
	        }
	        if (po instanceof PdfArray) {
	            PdfArray pa = (PdfArray) po;
	            Iterator paListIter = pa.getList().iterator();
	            while (paListIter.hasNext()) {
	                Object listItem = paListIter.next();
	                if (listItem instanceof PdfReference) {
	                	annotRef = ((PdfReference) listItem).getObjectNumber();
	                    listItem = m_pm.getObjectIndirect((PdfReference) listItem);
	                }
	                if (listItem instanceof PdfDictionary) {
	                    Map annotMap = ((PdfDictionary) listItem).getMap();
	                    Set annotSet = annotMap.keySet();
	                    // PdfReference annotARef = null; // object reference for annotation map
	                    int annotARefNum = 0; // object number for annotation map
	                    if (annotMap.containsKey(new PdfName("Subtype"))) {
	                        PdfObject annotStype = (PdfObject) annotMap
	                            .get(new PdfName("Subtype"));
	                        if (annotStype instanceof PdfName) {
	                            String annotSString = ((PdfName) annotStype).getString();
	                            if (PdfAnnotation.isValidType(annotSString)) {
	                                // record the annotation if it is unique
	                                this.doc.addAnnotation(((PdfName) annotStype).getString());
	                            } else {
	                                // unknown annotation type
	                                this.addAnomaly(this.getAnomsPossible()
	                                    .getSevereElement(PdfAnomalies.PDF_UNKNOWN_ANNOTATION));
	                            }
	                        } else {  // wrong type
	                            this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                        } // done with Subtype as a PdfName
	                    } // done with Subtype within an Annotation dictionary within
	                    // the Annotation array within a page dictionary
	                    
	                    // ANNOTATION ACTIONS (p. 492 pdf 1.4 spec)
	                    // TODO: this is not allowed if the Dest entry is present
	                    if (annotMap.containsKey(new PdfName("A"))) {
	                        PdfObject annotA = (PdfObject) annotMap.get(new PdfName("A"));

	                        if (annotA instanceof PdfReference) {
	                            annotARefNum = ((PdfReference) annotA).getObjectNumber();
	                            annotA = m_pm.getObjectIndirect((PdfReference) annotA);
	                        } else { 
	                        	// if this is no reference for it (direct object), 
	                        	// convert it to indirect object.
                        	  	Map newAnnotMap = new HashMap(annotMap);
                        	  	annotARefNum = m_pm.addObject(annotA);
                        		PdfReference annotARef = new PdfReference(annotARefNum, 0);
                        		newAnnotMap.put(new PdfName("A"), annotARef);
                        		m_pm.setObject(new PdfDictionary(newAnnotMap), annotRef);	                        	
	                        }
	                        if (annotA instanceof PdfDictionary) {
	                            Map annotAMap = ((PdfDictionary) annotA).getMap();	                           
	                            if (annotAMap.containsKey(new PdfName("S"))) {
	                                PdfObject actionS = (PdfObject) annotAMap
	                                    .get(new PdfName( "S"));
	                                if (actionS instanceof PdfName) {
	                                    String actionSString = ((PdfName) actionS).getString();
	                                    if (PdfAction.isValidActionType(actionSString)) {
	                                        this.doc.addAction(PdfAction.TYPE_ACTIVATION, actionSString);
	                                    }
	                                    
	                                    // found an URI, p624 of PDF 1.6
	                                    if (actionSString.equals("URI")) {                              	
	                                    	// retrieve the URI from the annotation map
		                                    PdfString URIstring = (PdfString) 
												annotAMap.get(new PdfName("URI"));                                  
	                                    	// add this URI to our link collection		
		                                    if (URIstring != null)
		                                    	handleExternalReference("URI", URIstring.getString(), annotARefNum);
	                                    }
	                                } else { // wrong type
	                                    this.addAnomaly(this.getAnomsPossible()
	                                        .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                                } // done with S as a PdfName
	                            } else {
	                                // bad - doesn't have a required actions
	                                // dictionary entry
	                                this.addAnomaly(this.getAnomsPossible()
	                                    .getSevereElement(PdfAnomalies.PDF_BAD_ACTION_DICT));
	                            } // done with S as a PdfName
	                        } else {
	                            // wrong type
	                            this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                        } // done with A as a PdfDictionary
	                    } // done with A within an Annotation dictionary within
	                    // the Annotation array within a page dictionary
	                    //	ANNOTATION ADD. ACTIONS (p. 492 pdf 1.4 spec)
	                    if (annotMap.containsKey(new PdfName("AA"))) {
	                        PdfObject annotAA = (PdfObject) annotMap.get(new PdfName("AA"));
	                        if (annotAA instanceof PdfReference) {
	                            annotAA = m_pm.getObjectIndirect((PdfReference) annotAA);
	                        }
	                        if (annotAA instanceof PdfDictionary) {
	                            Map annotAAMap = ((PdfDictionary) annotAA).getMap();
	                            Iterator annotAAIter = annotAAMap.keySet().iterator();
	                            while (annotAAIter.hasNext()) {
	                                PdfObject annotAAEntry = (PdfObject) annotAAIter
	                                        .next();
	                                if (annotAAEntry instanceof PdfName) {
	                                    String annotAAEString = ((PdfName) annotAAEntry)
	                                            .getString();
	                                    if (PdfAction.isValidAnnotAa(annotAAEString)) {
	                                        this.doc.addAction( PdfAction.TYPE_ANNOTATION, 
	                                        		((PdfName) annotAAEntry).getString());
	                                    } else { // unknown annotation add. action
	                                        this.addAnomaly(this.getAnomsPossible()
	                                            .getSevereElement(
	                                            PdfAnomalies.PDF_UNKNOWN_ACTION));
	                                    }
	                                } else { 
	                                	// wrong type
	                                    this.addAnomaly(this.getAnomsPossible()
	                                        .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                                }
	                            } // done iterating through the entries in the AA dictionary
	                        } else {
	                            // wrong type
	                            this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                        } // done with AA as a PdfDictionary
	                    } // done with AA within an Annotation dictionary within
	                    // the Annotation array within a page dictionary
	                } else { // wrong type
	                    this.addAnomaly(this.getAnomsPossible()
	                        .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                } // done with an entry in the Annots array as a PdfDictionary
	            } // done iterating through the annotation dictionaries in the Annots array
	        } else {
	        	// wrong type
	            this.addAnomaly(this.getAnomsPossible()
	                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	        } // done with Annots as an array 
	          // done with Page dictionary Annots PAGE ADD. ACTIONS
        }
        catch (PdfFormatException e) {
            String anomName = PdfAnomalies
            	.interpretPdfFormatExceptionMessage(e.getMessage());
		    SevereElement pa = this.getAnomsPossible()
		        .getSevereElement(anomName);
		    this.addAnomaly(pa);
		    readSuccessfully = false;
			} 
        catch (IOException e) {
			readSuccessfully = false;
		} // done with try/catch
        return readSuccessfully;
    }

    /**
     * Parse the resource of the page and look for things we want to record 
     * (font used and if it has images, any filters).
     * steps for font parsing: Look for when /Type is equal to /Font.  
     *  Then get the /Subtype value (for example /TrueType) and the
     *  /BaseFont (ex:/DKMJAL+Arial,Bold). To know whether or not this font
     * is embedded in the Pdf, find the corresponding Font Descriptor. 
     *  This will also be a dictionary, with the /Type equal to /FontDescriptor. 
     *  Look for it having the same /FontName value as the Font's /BaseFont value.
     *  Look for it having one of the following entries to know
     *  that the font is embedded: /FontFile, /FontFile2, or /FontFile3.
     * 
     * @param resources - the resources used in the page
     * @throws FatalException
     */
    private void parsePageResources(Set resources) throws FatalException {        
        try {
	    Iterator objIter = resources.iterator();
	    while (objIter.hasNext()) {
	        PdfObject pr = (PdfObject) objIter.next();
	        pr = m_pm.getObjectIndirect(pr);
	        if (pr instanceof PdfName) {
	        } else if (pr instanceof PdfDictionary) {
	            // Record the fonts used.
	            boolean isFontDescriptor = false;
	            boolean isFont = false;
	            String fontName = null;
	            boolean isEmbeddedFont = false;
	            boolean isExternalFile = false;
	            //PdfReference objRef = null;
	            int pdEntryObjNum = 0;
	            
	            PdfDictionary pd = (PdfDictionary) pr;
	            Map pdMap = pd.getMap();
	            Map newPdMap = new HashMap(pdMap);
	            Set pdMapkeys = pdMap.keySet();
	            Iterator pdMapkeysIter = pdMapkeys.iterator();
	            while (pdMapkeysIter.hasNext()) {
	                Object pdEntry = pdMapkeysIter.next();
	                if (pdEntry instanceof PdfReference) {
	                	pdEntryObjNum = ((PdfReference) pdEntry).getObjectNumber();
	                    pdEntry = m_pm.getObjectIndirect((PdfReference) pdEntry);
	                } else { 
                    	// if this is no reference for it (direct object), 
                    	// convert it to indirect object.
                	  	pdEntryObjNum = m_pm.addObject((PdfObject) pdEntry);
                	  	PdfReference objRef = new PdfReference(pdEntryObjNum, 0);   
                		newPdMap.put(pdEntry, objRef);   
                    }
	                if (pdEntry instanceof PdfName) {
	                    String stringEntry = ((PdfName) pdEntry).getString();
	                    if (stringEntry.equals("Type")) {
	                        // see if it's a FontDescriptor
	                        PdfObject po = (PdfObject) (pdMap.get(pdEntry));
	                        if (po instanceof PdfName && ((PdfName) po)
	                            .getString().equals("FontDescriptor")) {
	                            isFontDescriptor = true;
	                            isFont = true;  // for embedded font
	                        }
	                        if (po instanceof PdfName && ((PdfName) po)
		                            .getString().equals("Font")) {
		                            isFont = true; 
		                        }
	                        if (po instanceof PdfName && ((PdfName) po)
	                            .getString().equals("Filespec")) {
	                        	// found a file specification dictionary (p155 in PDF 1.6)
	                        	isExternalFile = true;
	                        }
	                    } else if (isFontDescriptor && !isEmbeddedFont
	                        && (stringEntry.equals("FontFile")
	                        || stringEntry.equals("FontFile2") 
							|| stringEntry.equals("FontFile3"))) {
	                        isEmbeddedFont = true;
	                    } else if (isFont && (stringEntry.equals("BaseFont") 
	                    		|| stringEntry.equals("FontName"))) {
	                    	Object objFont = pdMap.get(new PdfName(stringEntry));
	                    	if (objFont instanceof PdfName) {
	                    		PdfName pdfFontName = (PdfName) objFont;
	                    		fontName = pdfFontName.getString();
	                    	}
	                    } else if (isExternalFile && stringEntry.equals("F")) {
	                    	// extract the external file/URI reference from the 
	                    	// file specification dictionary
	                    	PdfString extReference = (PdfString) pdMap.get(new PdfName(stringEntry));
	                    	handleExternalReference(stringEntry, extReference.getString(), pdEntryObjNum);
	                    }
	                   	//System.out.print(" " + ((PdfName)pdEntry).getString() +
	                    //     "=" + pdMap.get(new PdfName(((PdfName)pdEntry).getString())).toString());
	                }
	            }
	            if (isFontDescriptor && !isEmbeddedFont) {
	                this.doc.setUsesUnembeddedFonts(TRUE);
	            }
	            
	            if (fontName != null)
	            	this.doc.addFont(fontName, isEmbeddedFont); 
	            //System.out.println();
	
	        } else if (pr instanceof PdfStream) {
	            // look for images and filters, get the stream's dictionary
	            // PdfReference objRef = null;
	            int pdEntryObjNum = 0;
	            PdfDictionary pd = ((PdfStream) pr).getDictionary();
	            Map pdMap = pd.getMap();
	            Map newPdMap = new HashMap(pdMap);
	            Set pdMapkeys = pdMap.keySet();
	            Iterator pdMapkeysIter = pdMapkeys.iterator();
	            while (pdMapkeysIter.hasNext()) {
	                Object pdEntry = pdMapkeysIter.next();
	                if (pdEntry instanceof PdfReference) {
	                	pdEntryObjNum = ((PdfReference) pdEntry).getObjectNumber();
	                    pdEntry = m_pm.getObjectIndirect((PdfReference) pdEntry);
	                }
	                else {
                	// if this is no reference for it (direct object), 
                	// convert it to indirect object.
            	  	pdEntryObjNum = m_pm.addObject((PdfObject) pdEntry);
            	  	PdfReference objRef = new PdfReference(pdEntryObjNum, 0);
            		newPdMap.put(pdEntry, objRef);   
	                }
	                if (pdEntry instanceof PdfName) {
	                	String stringEntry = ((PdfName) pdEntry).getString();
	                    if (stringEntry.equals("Subtype")) {
	                        // this may be an image XObject
	                        PdfName pn = (PdfName) pdMap.get(pdEntry);
	                        if (pn.getString().equals("Image")) {
	                            this.doc.setHasImages(TRUE);
	                            numImages++;
	                        }
	                    } else if (stringEntry.equals("Filter")) {
	                        // record the filter, it can be names or arrays
	                        // (p. 58 of pdf 1.4 spec).
	                        PdfObject po = (PdfObject) pdMap.get(pdEntry);
	                        if (po instanceof PdfName) {
	                            PdfName pn = (PdfName) po;
	                            String ps = pn.getString();
	                            if (PdfFilter.isValidType(ps)) {                         	
	                            		this.doc.addFilter(ps);
	                            } else {
	                                // unknown filter type
	                                this.addAnomaly(this.getAnomsPossible()
	                                    .getSevereElement(PdfAnomalies.PDF_UNKNOWN_FILTER));
	                            }
	                        } else if (po instanceof PdfArray) {
	                            PdfArray pa = (PdfArray) po;
	                            Iterator filterIter = pa.getList().iterator();
	                            while (filterIter.hasNext()) {
	                                Object filterItem = filterIter.next();
	                                if (filterItem instanceof PdfReference) {
	                                    filterItem = m_pm
	                                       .getObjectIndirect((PdfReference) filterItem);
	                                }
	                                if (filterItem instanceof PdfName) {
	                                    String ps = ((PdfName) filterItem)
	                                            .getString();
	                                    if (PdfFilter.isValidType(ps)) {
	                                    	this.doc.addFilter(ps);
	                                    } else {
	                                        // unknown filter type
	                                        this.addAnomaly(this.getAnomsPossible()
	                                            .getSevereElement(
	                                            PdfAnomalies.PDF_UNKNOWN_FILTER));
	                                    }
	                                }
	                            }
	                        } else {
	                            // wrong type
	                            this.addAnomaly(this.getAnomsPossible()
	                                .getSevereElement(PdfAnomalies.PDF_WRONG_TYPE));
	                        }
	                        
	                    // Is linking to an external file? add the link
	                    } else if (stringEntry.equals("F")) {
	                    	// extract the external file reference from the PDF Map
	                    	PdfString fileName = (PdfString) pdMap.get(new PdfName(stringEntry));
	                    	handleExternalReference(stringEntry, fileName.getString(), pdEntryObjNum);
	                    }
	                } // done with checking that the entry was a name
	            } // done looping through the entries of a stream's dictionary
		    } else if (pr instanceof PdfArray) {
		    }
	    } // done looping through all the resources needed for a page
        }
        catch (PdfFormatException e) {
            String anomName = PdfAnomalies
            .interpretPdfFormatExceptionMessage(e.getMessage());
		    SevereElement pa = this.getAnomsPossible()
		            .getSevereElement(anomName);
		    this.addAnomaly(pa);
			} 
        catch (IOException e) {
		} // done with try/catch
    }
    
    /**
     * replace the original link (originalName) with the localized file (localName).  
     * This method will use the relative path of the localized file to replace the link.
     * If there is no localized file (for non distributed datafile or localization error),
     * use the absolute path of the datafile itself.
     * @param map - a map object in the PDF file
     * @param entry - the map entry
     * @param originalName - original file/URI stored in the map
     * @param mapRef - the object reference number for the map object.
     * @throws FatalException 
     */
    private void replaceLink(Map map, String entry, String originalName, int mapRef)
	throws FatalException {
	String localName;   //localized name for the link.  It should be relative path 
	// int objNumber = mapRef.getObjectNumber();
	
	// need to make a copy of the map since it was an unmodifiable map.
	Map newMap = new HashMap(map);
	
	// find the localized name (URI or filename) for the original URI/filename
    for (Iterator iter = this.getLinks().iterator(); iter.hasNext();) {
        Link lk = (Link) iter.next();
        if (lk.getStatus().equals(Link.STATUS_SUCCESSFUL)) {
        	// find the link 
        	if (lk.getLinkAlias().equals(originalName)) {
        		try {
        			// retrieve the datafile object for the associated link
            		DataFile df = super.getDfFromLinkAlias(lk.getLinkAlias());
            		// df could be null, double check.    
            		if (df != null) {         			
            			if (df.getLocalizedFilePath() != null)
            				localName = FileUtil.getRelPathFrom(this.getPath(), 
                					df.getLocalizedFilePath()); 
            			else
            				localName = FileUtil.getRelPathFrom(this.getPath(), 
            					df.getPath()); 
            	    	// modified the link in the PDF to point to the 
            			// localized file/downloaded URI
            			newMap.put(new PdfName(entry), new PdfString(localName));
            			m_pm.setObject(new PdfDictionary(newMap), mapRef);
            			break;
            		}
            	} catch (FatalException e) {
                  	throw e;
            	}
            }
        }
    }
    }
    
    /**
     * Replace all the external file references in the PDF file to
     * the localized (or downloaded) file.
     * This method only go through the external references that we stored earlier
     * during the parse and replace them with the relative path of either the localized 
     * file (phase 2) or the downloaded file (phase 1).  
     * 
     * @param filePath - the output file of the this PDF file. This will contain
     * the same content of this PDF file except with the external references being
     * replaced with either localized or downloaded link.
     * @return boolean variable
     * @throws FatalException
     */
    private boolean parseLinks(String filePath) throws FatalException {
    	boolean parseSuccessfully = true;
	    //quit right away if there is no PdfManager
	    if (m_pm == null)
	    {
	    	parseSuccessfully = false;
	    	return parseSuccessfully;
	    }
	    
        Set linkKeys = m_originalLinks.keySet();
        Iterator linkKeysIter = linkKeys.iterator();
        try {
        	// iterate through the m_originalLinks map to retrieve
        	// all the external references that were found during parsing.
	        while (linkKeysIter.hasNext()) {
	            Object objNum = linkKeysIter.next();
	            if (objNum instanceof Integer) {
	            	int pjxObjNum = ((Integer) objNum).intValue();
	            	PdfExternalReference externalReference = (PdfExternalReference) m_originalLinks.get(objNum);
	            	// get the PdfDictionary that is associated with the object number.
	            	PdfObject obj = m_pm.getObject(pjxObjNum);
	            	if (obj instanceof PdfDictionary) {
		                Map map = ((PdfDictionary) obj).getMap();
		                // replace the external reference
		                replaceLink(map, externalReference.getEntry(), 
		                		externalReference.getValue(), pjxObjNum);
	            	}	              
	            }
	        }
        } catch (PdfFormatException e) {
            String anomName = PdfAnomalies
            .interpretPdfFormatExceptionMessage(e.getMessage());
            SevereElement pa = this.getAnomsPossible().getSevereElement(anomName);
            this.addAnomaly(pa);
        	}
        catch (IOException e) {
             
        } // done with try/catch
        

	    // after modified the PDF manager with the new links, we can flush it to
	    // a new pdf file.
	    try {
	    	PdfWriter writer = new PdfWriter(new File(filePath));
	    	m_pm.writeDocument(writer);
	        writer.close();
	    } catch (PdfFormatException e1) {
	        // found an anomaly - find out which one
	        String mesg = e1.getMessage();
	        String anomName = PdfAnomalies.interpretPdfFormatExceptionMessage(mesg);
	        SevereElement pa = this.getAnomsPossible().getSevereElement(anomName);
	        this.addAnomaly(pa);
	        parseSuccessfully = false;
	    }
	    catch (IOException e) {
	        Informer.getInstance().fail(this,"parseLinks","I/O Error",
	            "file: " + this.getPath(),
	            new FatalException("Can't open file for writing"));
	        parseSuccessfully = false;
	    }

	    return parseSuccessfully;
    }
    
    /**
     * handle external file or URI.  Create a link for the external file/URI and 
     * added into the external link collection in the datafile to be used by 
     * retrieveLinks method
     * @param entry - file path or URI. 
     * @param externalReference 
     * @param objectNumber 
     * @throws FatalException 
     */
    public void handleExternalReference(String entry, String externalReference, int objectNumber) throws FatalException {
		Link lk = null;
    	try {
			lk = new Link(this.getIp().getWorkingPath(),this.getPath(),
					externalReference, this.getIp().getLinksDir(),
					super.getOrigin(), super.getOriginalUri());
	    	super.addLink(lk);
	    	// only retrieve the link if it's a relative path to the system
	      	if (lk.getLinkType() == Link.TYPE_REL_PATH)
	       		lk.setShouldRetrieve(true);
	      	else
	      		lk.setShouldRetrieve(false);

	    	// store the external reference to the m_originalLinks map 
	    	// such that we can use it later during localization
	    	PdfExternalReference externalRef = new PdfExternalReference(entry, externalReference);
	    	m_originalLinks.put(new Integer(objectNumber), externalRef);
		} 
     	catch (FatalException e) {
       		throw e;
		}
     	
       	catch (URISyntaxException e) {
       		throw new FatalException(e);
		}
    }

    /**
     * @param string
     *            whether or not its encrypted
     */
    public void setIsEncrypted(String string) {
        isEncrypted = string;
    }

    /**
     * Puts all the PDF file's members and their values in a String.
     * 
     * @return the members of this class as a String
     */
    public String toString() {
        String prior = super.toString();
        StringBuffer sb = new StringBuffer("");
        sb.append(prior);

        boolean isTrue;

        sb.append("\n");
        
        isTrue = this.hasFormatAttribute(Pdf.Attribute.HAS_BIN_HDR);
        sb.append("Has binary header: " + isTrue + "\n");
    
        isTrue = this.hasFormatAttribute(Pdf.Attribute.IS_LIN);
        sb.append("Is linearized: " + isTrue + "\n");
    
        isTrue = this.hasFormatAttribute(Pdf.Attribute.PDFA_1B_CONF);
        sb.append("Is min PDF/A compliant: " + isTrue + "\n");
    
        isTrue = this.hasFormatAttribute(Pdf.Attribute.PDFA_1A_CONF);
        sb.append("Is full PDF/A compliant: " + isTrue + "\n");
        
        return sb.toString();
    }

	/**
	 * @see edu.fcla.daitss.file.DataFile#toXML()
	 */
	public Document toXML() throws FatalException {

        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        // Namespace. p.getArchProperty("NS_METS")
        String namespace = rootElement.getNamespaceURI();
        
        return doc;
	}
	/**
	 * Perform localization if the PDF file contains links to external file 
	 * reference and at least of of the link was successfully resolved.  
	 * Phase 1 or 2 is performed, depending on the <code>locPhase</code>
	 * of the DataFile.  No localized file is generated for PDF localization
	 * (SRS 6.1.5)
	 *  
	 * @throws FatalException
	 * @throws PackageException
	 */
	public void localize() throws FatalException, PackageException { 
	    // exit early if this file should not be localized
	    if (!localizeSetup()){
	        return;
	    }

	    // cleanup all resources at the end of phase 2 localization
        // Since most PDF files from ETD are huge, to avoid parsing multiple times, we 
        // keep the PDFManager around after parse method such that we can reuse it during 
        // localization to write localized file with replaced links.
        if (this.getStateLoc2() != Procedure.NOT_CALLED)
        	cleanup();
	}
	
	/**
	 * Check if this file conforms to PDF/A-1b standard (Draft)
	 * @throws FatalException
	 */
	protected void checkPDFAConformance() throws FatalException {
		// get a handle to the JHOVE's PDF module
		PdfModule jhovePdfModule = (PdfModule) JhoveEngine.getInstance().
			getModule(jhovePdfModuleName);
		
		if (jhovePdfModule != null) {
	        File file = new File(super.getPath());
	        try {
			    RepInfo info = new RepInfo (file.getCanonicalPath ());
		        JhoveEngine.getInstance().validateFile(jhovePdfModule, file, info); 
		        // check if this pdf matches either one of the pdfa profile and 
		        // record the related format attribute if it conforms.
		        if (info.getProfile().contains(pdfA1aProfile))
		        	this.addFormatAttribute(Pdf.Attribute.PDFA_1A_CONF);
		        else if (info.getProfile().contains(pdfA1bProfile))
		        	this.addFormatAttribute(Pdf.Attribute.PDFA_1B_CONF);
	        } catch (Exception e) {
	        	throw new FatalException(e);
	        }
		} else {
			Informer.getInstance().warning(this, "checkPDFAConformance", 
					"Cannot Find JHOVE's PDF module", "PDF-A validation");
		}
	}
}
