package edu.fcla.daitss.format.pro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.Informer;

public class ProFile extends DataFile {

	// Fully-qualified name for this class. To be used for 
	// Informer calls from within static methods.
	private static String CLASSNAME = 
	    "edu.fcla.daitss.format.ProFile";
	
	/**
	 * format identification string for PRO files
	 */
	private static String proID = "Version, Format, Confidence";
	
	/**
	 * Put in here because DataFileValidator requires it.
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a Text file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
	 * Determines if an existing, readable file is a PRO file or not
 	 * when metadata about this file is available.
	 * @return 	whether or not its a pro file					
	 * @param 	filePath absolute path to an existing readable file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, METSDocument)";
		
		boolean isType = false;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			// read in the first line
			String text = reader.readLine();
			// check if this is a PRO file
			if (text != null && text.contains(proID))
				isType = true;
		} catch (FileNotFoundException e) {
			Informer.getInstance().fail(CLASSNAME, methodName,
					"I/O error", "Can not find the file", e);
		} catch (IOException e) {
			Informer.getInstance().fail(CLASSNAME, methodName,
					"I/O error", "Can not read the file", e);
		} catch (Exception e) {
			// any other exception, don't fail.  It's mostly not the type.
			isType = false;
		}
		
		return isType;
	}         
	
	/**
	 * The constructor to call for an existing pro file when 
	 * metadata about this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public ProFile(String path, InformationPackage ip)
		throws FatalException {
		super(path, ip);

		this.setMediaType(MimeMediaType.MIME_APP_PRO);
		this.setFileExt("pro");
	}      
	
	/**
	 * The constructor to call for an existing pro file when  
	 * metadata about this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public ProFile(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Put in here because DataFileValidator requires it.
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		super.evalMembers();
	}
	/**
	 * Put in here because DataFileValidator requires it.
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		super.parse();
		// we need to add a bistream for PRO or the DataFile will add an anomaly for no bitstream! 
		Bitstream bs = new Bitstream(this);
		this.addBitstream(bs);
	}
}
