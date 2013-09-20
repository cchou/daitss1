package edu.fcla.daitss.format.spreadsheet.biff8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

public class BIFF8 extends DataFile {
	private static String CLASSNAME = "edu.fcla.daitss.format.spreadsheet.biff";
	   
	boolean isDoubleStream;
	boolean IRMprotected = false;
	
	/**
	 * Determines whether or not the file is a BIFF8 file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a Text file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
     * Determines if this file is a BIFF file when metadata about this file is
     * available.
     * 
     * @return whether or not its a BIFF file
     * @param _filePath - absolute path to an existing readable file
     * @param _metadata - metadata about this DataFile
     * @throws FatalException
     */
	public static boolean isType(String _filePath, METSDocument _metadata) throws FatalException {
		boolean isType = true;
	    String methodName = "isType(String, METSDocument)";
	    
		// check that filePath is != null and points to an existing file
        if (!FileUtil.isGoodFile(_filePath)) {
            Informer.getInstance().fail(CLASSNAME, methodName, "Illegal argument", 
            	"filePath: " + _filePath, new FatalException("invalid absolute path to a file"));
        }

        File theFile = new File(_filePath);
        ByteReader breader = new ByteReader(theFile, "r");
        
        // read in the first 512 bytes, the OLE compound document header
        // read in the fist 8 bytes.
        byte[] docHeader= {(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1};
        byte[] header = new byte[8];
        
        breader.read(header);
        
        if (!Arrays.equals(docHeader, header))
        	isType = false;
        
        //skip the rest of the compound document header.
        breader.close();
        breader = null;
        theFile = null;
		
		return isType;
	}
	
	/**
     * The constructor to call for an existing BIFF8 file when metadata about this
     * file is not available.
     * 
     * @param path 	- the absolute path to an existing readable file
     * @param ip	- the Information Package that this file is part of
     * @throws FatalException
     */
    public BIFF8(String path, InformationPackage ip) throws FatalException {
        super(path, ip);

        this.setByteOrder(DataFile.BYTE_ORDER_LE);
        //this.setMediaType(MimeMediaType.MIME_);
        //this.setMediaTypeVersion(VERSION_NA);
        //this.setFileExt(Extension.);
    }
    
	/**
	 * The constructor to call for an existing text file when  
	 * metadata about this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public BIFF8(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Read the file and retrieve the attributes of BIFF files
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		SummaryInformation si = null;
		try {
			FileInputStream fin = new FileInputStream(super.getPath());
			POIFSFileSystem poifs = new POIFSFileSystem(fin);
			// Create the workbook 
			Workbook wb = new Workbook(poifs);
			DocumentInputStream bookStream = poifs.createDocumentInputStream("Book");
			if (bookStream != null) {
				isDoubleStream = true;
				bookStream.close();
			}
			
			DocumentInputStream summaryStream = poifs.createDocumentInputStream("\005SummaryInformation");
			if (summaryStream == null) {
				//TODO: record anomaly
				System.out.print("Cannot find required Summary Information stream");
			} else {
				try {
					si = (SummaryInformation) PropertySetFactory.create(summaryStream);
					// retrieve the create date and creating application information
					super.setCreateDate(si.getCreateDateTime().toString());
					super.setCreatorProg(si.getApplicationName());
					wb.setSummary(si);
				} catch (Exception ex) {
					//TODO: record anomaly instead
					System.out.print("Cannot retrieve prperties from the Summery Information stream " + ex);
				}
				summaryStream.close();
			}

			// check if the BIFF file is IRM protected
			DocumentInputStream drmStream = poifs.createDocumentInputStream("\006DataSpaces");
			if (drmStream != null) {
				IRMprotected = true;
				bookStream.close();
				DocumentInputStream drmContentStream = poifs.createDocumentInputStream("\011DRMContent");
				if (drmContentStream == null)
					//TODO record anomaly
					System.out.print("missing required DRM Content stream for IRM protected document");
			}
			 
			// close our workbook stream and file stream
			fin.close();

		} catch (IOException e) {
			
		} 
	}
	
	/**
	 * Evaluate the members set by parsing the file to set other members.
	 * 
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		super.evalMembers();
	}
	
	/**
	 * testing routine
	 * @param args 
	 * @throws PackageException 
	 * @throws FatalException 
	 */
	public static void main(String[] args) throws PackageException, FatalException {
		String testFile = "/Users/Carol/Desktop/work/testdata/excel/object1.xls";
		if (BIFF8.isType(testFile, null)) {
			System.out.println(testFile + " is a BIFF8 file");
			BIFF8 file = new BIFF8(testFile, new SIP("/var/daitss/AA00000000"));
			file.setOid("F20090101_AAAAAA");
			file.extractMetadata();
			System.out.println(file);
		} else {
			System.out.println("Is not a BIFF8");
		}
	}
}
