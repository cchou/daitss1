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
package edu.fcla.daitss.format.image.jpeg;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.Format;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.file.TransformationFormat;
import edu.fcla.daitss.format.image.AdobeIRBs;
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.Image;
import edu.fcla.daitss.format.image.ImageMetadataConflicts;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.ExternalProgram;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.Procedure;

/**
 * JPEG represents any simple variation of a JPEG image interchange format like a
 * JFIF or Adobe JPEG file.
 * 
 * @author Andrea Goethals, FCLA
 *  
 */
public class Jpeg extends DataFile {
	
	/**
	 * Attribute represents an attribute specific to Jpeg files.
	 * Note that the length of the values of these constants have to be 
	 * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
	 * they have to be unique in the FORMAT_ATTRIBUTES database table.
	 * 
	 * The version of the file is taken dynamically from
	 * the file content so it is not coded as constants in this class.
	 * 
	 * @author Andrea Goethals, FCLA
	 *
	 */
	public static class Attribute {
		
		/**
		 * File is a JPEG written by Adobe Photoshop or Illustrator.
		 */
		public static final String IS_ADOBE_PS_3 = "IMAGE_JPEG_IS_ADOBE_PS_3";
		
		/**
		 * TODO: Not supported yet
		 */
		public static final String IS_EXIF = "IMAGE_JPEG_IS_EXIF";
		
		/**
		 * File is a JFIF file.
		 */
		public static final String IS_JFIF = "IMAGE_JPEG_IS_JFIF";
		
		/**
		 * The JPEG file variation is unknown.
		 */
		public static final String IS_UNKNOWN_VAR = "IMAGE_JPEG_IS_UNKNOWN_VAR";
	}
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.image.jpeg.Jpeg";
	
	/**
	 * Adobe variation
	 */
	public static final String VAR_ADOBE = "ADOBE";
	
	/**
	 * JFIF variation
	 */
	public static final String VAR_JFIF = "JFIF";
	
	/**
	 * Unknown variation
	 */
	public static final String VAR_UNKNOWN = "UNKNOWN";
	
	/**
	 * Determines whether or not the file is a JPEG file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a JPEG file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}

	/**
	 * Determines whether or not the file is a JPEG file when metadata about
	 * this file is available. Determines that it is some kind of JPEG file
	 * from the first two bytes and the last two bytes of the file. A JPEG
	 * must have the following header:
	 * 
	 * <TABLE>
	 * <TR><TD>Byte number</TD><TD>Marker</TD></TR>
	 * <TR><TD>0-1</TD><TD>SOI</TD></TR>
	 * <TR><TD>(last-1) - last</TD><TD>EOI</TD></TR>
	 * </TABLE>
	 * 
	 * @return a boolean with <code>true</code> if it is a jfif image,
	 *               otherwise <code>false</code>
	 * @param filePath	an absolute path to an existing file
	 * @param	_metadata metadata about 
	 * 		this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		
		String methodName = "isType(String, DataFile)";
		boolean isType = false;
		
		// check that filePath is != null and points to an existing file
		if (!FileUtil.isGoodFile(filePath)) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Illegal argument",
				"filePath: " + filePath,
				new FatalException("Not an existing, readable absolute path to a file"));
		}
		ByteReader tempReader = null;
		File f = new File(filePath);
		tempReader = new ByteReader(f, "r");

		// look for the Jpeg SOI marker in the first 2 bytes if the file
		// size is at least that long
		if (f.length() >= 2) {
			int marker1 = (int) tempReader.readBytes(2, DataFile.BYTE_ORDER_BE);
			if (marker1 == JpegMarkers.SOI) {
				// so far so good, check its last 2 bytes
				long numBytes = f.length();
				// move reader pointer to start of last 2 bytes of the file to look
				// for the EOI marker
				tempReader.seek(numBytes - 2L);
				int lastMarker =
					(int) tempReader.readBytes(2, DataFile.BYTE_ORDER_BE);
				if (lastMarker == JpegMarkers.EOI) {
					isType = true;
				}
			}
		}
		
		tempReader.close();
		tempReader = null;
		f = null;
		
		return isType;
	} 

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws PackageException
	 * @throws FatalException
	 */
	public static void main(String[] args) throws PackageException, 
		FatalException {
		String testFile = null;
		String packagePath = null;
		if (args.length == 4 && args[0].equals("-p") && args[2].equals("-f")) {
			packagePath = args[1];
			testFile = args[1] + args[3];
		}
		else {
			printUsage();
			System.exit(1);
		}
		
		if (Jpeg.isType(testFile, null)) {
			System.out.println("Is a Jpeg");
			Jpeg jFile = new Jpeg(testFile, new SIP(packagePath));
			jFile.setOid("F20040101_AAAAAA");
			jFile.extractMetadata();
			System.out.println(jFile);
		} else {
			System.out.println("Is not a Jpeg");
		}
	}
	
	 /**
	 * Print the command line usage of this class.
	 */
	private static void printUsage() {
		System.out.println("java Jpeg [-p packagePath] [-f filename]");
	}
	
	/**
	 * Whether or not to continue parsing this file (might as well stop after seeing weirdness)
	 */
	private boolean continueParsing = true;
	
	/**
	 * An image contained in this file
	 */
	private JpegImage image = null;
	
	/**
	 * Whether or not the APP Marker being parsed is the first APP marker
	 * in the file. Used to determine the file variation which is based on the first
	 * APP marker.
	 */
	private boolean isFirstAppMarker = true;

	/**
	 * All JPEG markers - not necessarily contained in this file
	 */
	private JpegMarkers jMarkers = null;
	
	/**
	 * The markers used in this file, in the order they were encountered 
	 * while parsing this file sequentially
	 */
	private Vector markersUsed = null;
	
	/**
	 * A flag to note when a SOF marker segment has been parsed
	 */
	private boolean sawSof = false;
	
	/**
	 * A thumbnail image that may or may not be in this file (and may or may
	 * not be JPEG-compressed)
	 */
	private Image thumbnail = null;

	/**
	 * The constructor to call for an existing JPEG file when metadata about
	 * this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public Jpeg(String path, InformationPackage ip) throws FatalException {
		super(path, ip); // build data structures common to all ImageFile objects
		
		// set default member values
		// set byte order
		this.setByteOrder(DataFile.BYTE_ORDER_BE);
		// set MIME media type
		this.setMediaType(MimeMediaType.MIME_IMG_JPEG);
		// set extension this file will get on long-term storage
		this.setFileExt(Extension.EXT_JPG);
		// default to unknown
		this.setFormatVariation(VAR_UNKNOWN);
		// build needed data structures
		this.jMarkers = new JpegMarkers();
		this.markersUsed = new Vector();
		this.anomsPossible = null;
		this.anomsPossible = new JpegAnomalies();
		metadataConfPossible = null;
		metadataConfPossible = new ImageMetadataConflicts();
	}
	
	/**
	 * The constructor to call for an existing JPEG file when metadata about
	 * this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata a lite DataFile containing metadata about this DataFile
	 * @throws FatalException
	 */
	public Jpeg(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * Determines and assigns this file's format code, using its
	 * media type and format variation.
	 * This implementation of calcFormatCode ignores the
	 * file's variation because that hasn't found to be consistent
	 * and it is taken dynamically from the file contents instead
	 * of stored as constants.
	 * 
	 * @throws FatalException
	 */
	public void calcFormatCode() 
		throws FatalException {
	    String methodName = "calcFormatCode()";
	    
	    // media type
	    String mType = this.getMediaType();
	    
	    // format variation
	    String variation = this.getFormatVariation();
	    if (variation == null) {
	        variation = "";
	    }
	    
	    String code = Format.getFormatCode(mType,
	            "", variation);
	    if (code == null || code.equals("") ||
	            code.length() > MAX_FORMATCODE_LENGTH) {
	        Informer.getInstance().fail(this,
                    methodName,
                    "Invalid format code",
                    "format code: " + code + " file: " + this.getPath(),
                    new FatalException("Format code doesn't meet constraints."));
	    }
	    
	    this.setFormatCode(code);
	    
	}
	
	/**
	 * Sets the members for which their value depends on the value
	 * of the members that were set by parsing this file. In other
	 * words, this is the 'second phase' of setting this file's members. 
	 * The first was the parsing phase.
	 * 
	 * @throws FatalException
	 */
	protected void evalMembers() throws FatalException {
		// set the Jpeg variation of this file
		super.evalMembers();
		
		// set the image profiles and other members for each image found 
		// in this tiff file
		Iterator bsIter = this.getBitstreams().iterator();
		while (bsIter.hasNext()) {
			JpegImage ti = (JpegImage) bsIter.next();
			ti.evalMembers();
		}
	}
	
	/**
	 * @return a collection of all the markers contained in this file
	 */
	public Vector getMarkersUsed() {
		return this.markersUsed;
	}

	/**
	 * @return the thumbnail image contained in this file
	 */
	public Image getThumbnail() {
		return thumbnail;
	}

	/**
	 * Reads the file in one pass and sets any members that it can. Assumes that
	 * this is the main image in the file and not a thumbnail in the file. 
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		
		// assume the jpeg variation is unknown
		this.addFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR);
		
		//	BEGIN MAIN IMAGE
		image = new JpegImage(this);
		image.setRole(BitstreamRole.IMG_MAIN);
		image.setLocation("0"); // main image starts at the SOI marker (1st byte)
		this.addBitstream(image);
		
		// skip SOI - because we already know this is a Jpeg file, the
		// SOI must exist
		reader.skipBytes(2);
		
		// Determine Jpeg file variation
		// look for the APPn byte
		int APPn = (int)reader.readBytes(2, this.getByteOrder());
		//System.out.println("APPN: " + Integer.toHexString(APPn));
		
		//	add the marker to the list found in this file
		String APPnStr = JpegMarkers.markerToString(APPn);
		if (APPnStr != null){
			this.markersUsed.add(APPnStr);
		}
		
		// parse the first APP marker		 
		switch(APPn) {
			case JpegMarkers.APP0: this.parseApp0(); break; // JFIF
			case JpegMarkers.APP1: break; // Exif
			case JpegMarkers.APP2: break; // embedded ICC profile
			case JpegMarkers.APP3: break;
			case JpegMarkers.APP4: break;
			case JpegMarkers.APP5: break;
			case JpegMarkers.APP6: break;
			case JpegMarkers.APP7: break;
			case JpegMarkers.APP8: break;
			case JpegMarkers.APP9: break;
			case JpegMarkers.APP10: break;
			case JpegMarkers.APP11: break;
			case JpegMarkers.APP12: break;
			case JpegMarkers.APP13: this.parseApp13(); break;
			case JpegMarkers.APP14: break; // Adobe
			case JpegMarkers.APP15: break;
			default:
				// UNKNOWN Jpeg file format
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_UNKNOWN_VARIATION));
				continueParsing = false;
				this.addFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR);
		}
		
		// record the fact that we have already seen the first APP marker. Any APP
		// markers that follow may be used for their metadata but not to determine the
		// file variation
		this.isFirstAppMarker = false;
		
		// parse rest of file looking for relevant technical metadata
		while (continueParsing) {
			// read the marker
			int marker = (int) this.reader.readBytes(2, this.getByteOrder());
			
			// add the marker to the list found in this file
			String markerStr = JpegMarkers.markerToString(marker);
			if (markerStr != null) {
				this.markersUsed.add(markerStr);
			}
			
			// parse the marker segment
			switch(marker) {
				case JpegMarkers.APP0: this.skipMarker(); break;
				case JpegMarkers.APP1: this.skipMarker(); break;
				case JpegMarkers.APP2: this.skipMarker(); break;
				case JpegMarkers.APP3: this.skipMarker(); break;
				case JpegMarkers.APP4: this.skipMarker(); break;
				case JpegMarkers.APP5: this.skipMarker(); break;
				case JpegMarkers.APP6: this.skipMarker(); break;
				case JpegMarkers.APP7: this.skipMarker(); break;
				case JpegMarkers.APP8: this.skipMarker(); break;
				case JpegMarkers.APP9: this.skipMarker(); break;
				case JpegMarkers.APP10: this.skipMarker(); break;
				case JpegMarkers.APP11: this.skipMarker(); break;
				case JpegMarkers.APP12: this.skipMarker(); break;
				case JpegMarkers.APP13: this.parseApp13(); break;
				case JpegMarkers.APP14: this.skipMarker(); break;
				case JpegMarkers.APP15: this.skipMarker(); break;
				case JpegMarkers.COM:  this.skipMarker(); break;
				case JpegMarkers.DAC: this.skipMarker(); break;
				case JpegMarkers.DHP: this.skipMarker(); break;
				case JpegMarkers.DHT: this.parseDht(); break;
				case JpegMarkers.DNL: this.skipMarker(); break;
				case JpegMarkers.DQT: this.parseDqt(); break;
				case JpegMarkers.DRI: this.parseDri(); break;
				case JpegMarkers.EOI: 
					// end of image - stop parsing
					continueParsing = false;
					break;
				case JpegMarkers.EXP: this.skipMarker(); break;
				case JpegMarkers.JPG: this.skipMarker();break;
				case JpegMarkers.JPG0: this.skipMarker();break;
				case JpegMarkers.JPG1: this.skipMarker();break;
				case JpegMarkers.JPG2: this.skipMarker();break;
				case JpegMarkers.JPG3: this.skipMarker();break;
				case JpegMarkers.JPG4: this.skipMarker();break;
				case JpegMarkers.JPG5: this.skipMarker();break;
				case JpegMarkers.JPG6: this.skipMarker();break;
				case JpegMarkers.JPG7: this.skipMarker();break;
				case JpegMarkers.JPG8: this.skipMarker();break;
				case JpegMarkers.JPG9: this.skipMarker();break;
				case JpegMarkers.JPG10: this.skipMarker();break;
				case JpegMarkers.JPG11: this.skipMarker();break;
				case JpegMarkers.JPG12: this.skipMarker();break;
				case JpegMarkers.JPG13: this.skipMarker();break;
				case JpegMarkers.RST0: break;  // stand-alone
				case JpegMarkers.RST1: break;  // stand-alone
				case JpegMarkers.RST2: break;  // stand-alone
				case JpegMarkers.RST3: break;  // stand-alone
				case JpegMarkers.RST4: break;  // stand-alone
				case JpegMarkers.RST5: break;  // stand-alone
				case JpegMarkers.RST6: break;  // stand-alone
				case JpegMarkers.RST7: break;  // stand-alone
				case JpegMarkers.SOF0: 
					this.image.setProcess(JpegProcess.BASELINE);
					this.parseSof(); break;
				case JpegMarkers.SOF1:
					this.image.setProcess(JpegProcess.EXT_SEQ);
					this.parseSof(); break;
				case JpegMarkers.SOF2: 
					this.image.setProcess(JpegProcess.PROG);
					this.parseSof(); break;
				case JpegMarkers.SOF3:
					this.image.setProcess(JpegProcess.LOSSLESS);
					this.parseSof(); break;
				case JpegMarkers.SOF5:
					this.image.setProcess(JpegProcess.DIFF_SEQ);
					this.parseSof(); break;
				case JpegMarkers.SOF6:
					this.image.setProcess(JpegProcess.DIFF_PROG);
					this.parseSof(); break;
				case JpegMarkers.SOF7: 
					this.image.setProcess(JpegProcess.DIFF_LOSSLESS);
					this.parseSof(); break;
				case JpegMarkers.SOF9: 
					this.image.setProcess(JpegProcess.EXT_SEQ_ARITH);
					this.parseSof(); break;
				case JpegMarkers.SOF10: 
					this.image.setProcess(JpegProcess.PROG_ARITH);
					this.parseSof(); break;
				case JpegMarkers.SOF11: 
					this.image.setProcess(JpegProcess.LOSSLESS_ARITH);
					this.parseSof(); break;
				case JpegMarkers.SOF13: 
					this.image.setProcess(JpegProcess.DIFF_SEQ_ARITH);
					this.parseSof(); break;
				case JpegMarkers.SOF14: 
					this.image.setProcess(JpegProcess.DIFF_PROG_ARITH);
					this.parseSof(); break;
				case JpegMarkers.SOF15: 
					this.image.setProcess(JpegProcess.DIFF_LOSSLESS_ARITH);
					this.parseSof(); break;
				case JpegMarkers.SOI: break;
				case JpegMarkers.SOS: this.parseSos(); break;
				case JpegMarkers.TEM: break;  // stand-alone
				default: 
					// unknown marker - have to stop parsing (can't interpret it)
					this.addAnomaly(
							this.getAnomsPossible().
								getSevereElement(JpegAnomalies.JPEG_UNKNOWN_MARKER));
					continueParsing = false;
					break;
			}
		}
	}
	
	/**
	 * Parses the APP0 marker (hex FF E0) segment used by JFIF files and
	 * sets members based on values found in the segment
	 * 
	 * @throws FatalException
	 */
	private void parseApp0() throws FatalException {
		int data;
		
		long finalPosition; // pointer to start of marker after this one
		
		// used to determine if the Xdensity and
		// Ydensity refer to resolution or pixel aspect ratio
		boolean isPixelAspectRatio = false; 
		
		// check the APP0 field length (which is the field length minus the APP0 marker itself)
		// (in 2 bytes)
		int length = (int) reader.readBytes(2, this.getByteOrder());
		if (length < 16 && this.hasFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR)) {
			// this should be >= 16
			this.addAnomaly(
					this.getAnomsPossible().
					getSevereElement(JpegAnomalies.JPEG_INCOMPLETE_APP0));
			// keep parsing it anyway
		}
		
		// remember the position to seek to at the end 
		// (to skip over the thumbnail image data)
		// subtracting 2 to get back the position before reading the length
		finalPosition = this.reader.getFilePointer() + length - 2;
		
		// look for the null terminated 'JFIF' if the file type is still unknown
		if (this.hasFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR) &&
				reader.readBytes(4, this.getByteOrder()) == 0x4a464946 &&
				reader.readBytes(1, this.getByteOrder()) == 0x00) {
			this.addFormatAttribute(Jpeg.Attribute.IS_JFIF);
			this.setFormatVariation(VAR_JFIF);
			this.removeFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR);
			this.setMediaTypeVersion(reader.readBytes(1, this.getByteOrder()) + 
				".0" + reader.readBytes(1, this.getByteOrder()));
			//	Units for X and Y densities (resolutions)
			data = (int) reader.readBytes(1, this.getByteOrder());
			switch (data) {
				// JFIF uses different values for this than Tiff, so mapping them here
				case 0:
					isPixelAspectRatio = true;
					break;
				case 1: 
					this.image.setResUnit(Image.RES_UNIT_INCH);
					break;
				case 2:
					this.image.setResUnit(Image.RES_UNIT_CM);
					break;
				default:
					// not a valid value - note the anomaly
					this.addAnomaly(
							this.getAnomsPossible().
							getSevereElement(JpegAnomalies.IMG_UNKNOWN_RESUNIT));
			}
			// Xdensity
			float xRatio = 1.0f; // horizontal pixel aspect
			int xDensity = (int) reader.readBytes(2, this.getByteOrder());
			if (!isPixelAspectRatio) {
				this.image.setXResolution(xDensity);
			} else {
				xRatio = xDensity;
			}
			
			//	Ydensity
			float yRatio = 1.0f; // vertical pixel aspect
			int yDensity = (int) reader.readBytes(2, this.getByteOrder());
			if (!isPixelAspectRatio) {
				this.image.setYResolution(yDensity);
			} else {
				yRatio = yDensity;
			}
			
			// set the pixel aspect ratio if the xDensity and yDensity values were
			// used to specify a pixel aspect ratio other than 1:1 (square)
			if (isPixelAspectRatio) {
				this.image.setPixelAspectRatio(xRatio/yRatio);
				//System.out.println("Set aspect ratio to " + this.image.getPixelAspectRatio());
			}
			
			// see if there's a thumbnail in the APP0 marker segment
			int xThumbnail = (int) reader.readBytes(1, this.getByteOrder());
			int yThumbnail = (int) reader.readBytes(1, this.getByteOrder());
			if (xThumbnail != 0 && yThumbnail != 0){
				//System.out.println("Thumbnail in APP0");
				thumbnail = new Image(this);
				thumbnail.setRole(BitstreamRole.IMG_THUMBNAIL);
				// set the thumbnail's position to the start of the XThumbnail (15th byte of APP0)
				thumbnail.setLocation(Long.toString(this.reader.getFilePointer()-2));
				// a thumbnail here would always be RGB
				thumbnail.setColorSpace(ColorSpace.CS_RGB); // thumbnail color space
				thumbnail.getCompression().setCompression(Compression.COMP_NONE);
				thumbnail.setPixelsHorizontal(xThumbnail); // thumbnail pixel width
				thumbnail.setPixelsVertical(yThumbnail); // thumbnail pixel height
				this.addBitstream(thumbnail);
			} else {
				//System.out.println("No thumbnail in APP0");
			}
			
		} else if (this.hasFormatAttribute(Jpeg.Attribute.IS_JFIF) &&
			reader.readBytes(4, this.getByteOrder()) == 0x4a465858 && // 'JFXX'
			reader.readBytes(1, this.getByteOrder()) == 0x00){
			// a JFIF extension - get the extension code
			int extCode = (int)this.reader.readBytes(1, this.getByteOrder());
			switch (extCode) {
				case 0x10: // thumbnail coded using Jpeg
					thumbnail = new JpegImage(this);
					thumbnail.setRole(BitstreamRole.IMG_THUMBNAIL);
					// set location to start of SOI
					thumbnail.setLocation(Long.toString(this.reader.getFilePointer()));
					// TODO: seek to its SOF, get the sample Precision, numComponents, 
					// seek to its SOS and get the Jpeg process
					this.addBitstream(thumbnail);
					break;
				case 0x11: // thumbnail stored using 1 byte per pixel
					thumbnail = new Image(this);
					thumbnail.setRole(BitstreamRole.IMG_THUMBNAIL);
					// set location to start of Xthumbnail
					thumbnail.setLocation(Long.toString(this.reader.getFilePointer()));
					// TODO: what should this color space be?
					//thumbnail.setColorSpace(ColorSpace.CS_PALETTE);
					thumbnail.setHasIntPalette(DataFile.TRUE);
					thumbnail.setNumComponents(1);
					thumbnail.getCompression().setCompression(Compression.COMP_NONE);
					// XThumbnail
					long width11 = this.reader.readBytes(1, this.getByteOrder());
					thumbnail.setPixelsHorizontal(width11);
					// YThumbnail
					long height11 = this.reader.readBytes(1, this.getByteOrder());
					thumbnail.setPixelsHorizontal(height11);
				    this.addBitstream(thumbnail);
					break;
				case 0x13: // thumbnail stored using 3 bytes per pixel
					thumbnail = new Image(this);
					thumbnail.setRole(BitstreamRole.IMG_THUMBNAIL);
					//	set location to start of Xthumbnail
					thumbnail.setLocation(Long.toString(this.reader.getFilePointer()));
					thumbnail.setColorSpace(ColorSpace.CS_RGB);
					thumbnail.setNumComponents(3);
					thumbnail.getCompression().setCompression(Compression.COMP_NONE);
					// XThumbnail
					long width13 = this.reader.readBytes(1, this.getByteOrder());
					thumbnail.setPixelsHorizontal(width13);
					// YThumbnail
					long height13 = this.reader.readBytes(1, this.getByteOrder());
					thumbnail.setPixelsHorizontal(height13);
				    this.addBitstream(thumbnail);
					break;
				default: // unknown JFIF extension
					this.addAnomaly(
							this.getAnomsPossible().
							getSevereElement(JpegAnomalies.JPEG_UNKNOWN_JFIFEXT));
			}
		} else {
			// Some variation other than JFIF also using the APP0 marker
			this.addAnomaly(
					this.getAnomsPossible().
					getSevereElement(JpegAnomalies.JPEG_UNKNOWN_VARIATION));
			this.addFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR);
		}
		this.reader.seek(finalPosition);
	}
	
	/**
	 * Parses the APP13 marker segment used by Adobe Photoshop 3.0
	 * 
	 * @throws FatalException
	 */
	private void parseApp13() throws FatalException {
		//	skip length (2 bytes)
		int length = (int) this.reader.readBytes(2, this.getByteOrder());
		
		// remember how many bytes in this marker segment still need to be parsed
		int unparsed = length-2;
		
		boolean continueParsing = true; // used to stop parsing this segment when
		// an error is found
		
		// remember the position to seek to at the end
		// subtracting 2 to get back the position before reading the length
		long finalPosition = this.reader.getFilePointer() + length - 2;
		
		// read next 14 bytes looking for the null-terminated 'PHOTOSHOP 3.0'
		if (length >= 14 &&
			this.reader.readBytes(4, this.getByteOrder()) == 0x50686f74 && // 'PHOT'
			this.reader.readBytes(4, this.getByteOrder()) == 0x6f73686f && // 'OSHO'
			this.reader.readBytes(4, this.getByteOrder()) == 0x7020332e && // 'P 3.'
			this.reader.readBytes(2, this.getByteOrder()) == 0x3000) { // O 0x00
			 //	Adobe Photoshop 3.0
			 if (this.isFirstAppMarker) {
			 	// set the file variation to Adobe Jpeg
			 	this.addFormatAttribute(Jpeg.Attribute.IS_ADOBE_PS_3);
			 	this.setFormatVariation(VAR_ADOBE);
			 	this.removeFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR);
			 }
			 if (this.getCreatorProg() == null) {
			 	this.setCreatorProg("Photoshop 3.0");
			 }
			 unparsed = unparsed-14;
		} else {
			// what?
		}
			
		// parse its image resource blocks looking for relevant metadata
		while (continueParsing && unparsed > 12) {
			//System.out.println("Beginning loop at byte: " + this.reader.getFilePointer());
			//System.out.println("Bytes left to parse: " + unparsed);
			// look for the Photoshop signature
			int sig = (int) this.reader.readBytes(4, this.getByteOrder());
			unparsed-=4;
			if (sig == AdobeIRBs.PHOTOSHOP_SIG){
				// get the IRB id
				int irbid = (int) this.reader.readBytes(2, this.getByteOrder());
				unparsed-=2;
				if (AdobeIRBs.isKnownID(irbid)){
					// skip the pascal string
					int pstring = (int) this.reader.readBytes(1, this.getByteOrder());
					unparsed-=1;
					// pad if necessary to make pstring+1 even
					if ((pstring%2) == 0) {
						pstring+=1;
					}
					this.reader.skipBytes(pstring);
					unparsed = unparsed - pstring;
					//System.out.println("After switch at byte: " + this.reader.getFilePointer());
					// get the number of bytes in the rest of this image resource block
					int resourceLength = (int) this.reader.readBytes(4, this.getByteOrder());
					//System.out.println("Resource length: " + resourceLength);
					// pad if necessary to make size even
					if ((resourceLength%2) == 1) {
						resourceLength+=1;
						//System.out.println("Padding resource length");
					}
					unparsed = unparsed - resourceLength - 4;
					// pad if necessary to make even
					if ((resourceLength %2) == 1) {
						resourceLength+=1;
					}
					this.reader.skipBytes(resourceLength);
				} else {
					//System.out.println("Do not know that ID: " + Integer.toHexString(irbid));
				}
			} else {
				//System.out.println("Did not have PS sig: " + Integer.toHexString(sig));
				continueParsing = false;
			}
		}

		
		// move to next marker
		this.reader.seek(finalPosition);		
	}
	
	/**
	 * Parses the DHT marker, looking for anomalies. This marker can 
	 * store multiple tables (baseline mode is limited to 2 of each type; progressive 
	 * and sequential are limited to 4 of each type).
	 * 
	 * @throws FatalException
	 */
	private void parseDht() throws FatalException {
		// get the length of this segment
		int length = (int) reader.readBytes(2, this.getByteOrder());
		
		// remember the position to seek to at the end
		// subtracting 2 to get back the position before reading the length
		long finalPosition = this.reader.getFilePointer() + length - 2;
		
		int unparsed = length -2;
		
		// parse the table(s) noting the table ids for later use when parsing the
		// SOS marker
		while (unparsed != 0) {
			// record the table identifier
			byte tableInfo = (byte) reader.readBytes(1, this.getByteOrder());
			unparsed--;
			byte[] infos = ByteReader.splitByteInTwo(tableInfo);
			if (infos[0] == 0) {
				this.image.addHuffmanDcTableId(infos[1]);
			} else if (infos[0] == 1) {
				this.image.addHuffmanAcTableId(infos[1]);
			} else {
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_DHT));
				// move to next marker	
				this.reader.seek(finalPosition);
			}
			// make sure there are still 16 Huffman codes to parse
			if (unparsed < 16) {
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_DHT));
				// move to next marker	
				this.reader.seek(finalPosition);
			}
			// add up the number of symbols
			int numHuffmanCodes = 0;
			for (int i=1; i<17; i++) { // add up the count of huffman codes of length 1 to 16
				numHuffmanCodes = numHuffmanCodes + (int) reader.readBytes(1, this.getByteOrder());
			}
			unparsed = unparsed-16;
			// make sure that there are still numHuffmanCodes to parse
			if (unparsed < numHuffmanCodes) {
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_DHT));
				// move to next marker	
				this.reader.seek(finalPosition);
			}
			// skip over the symbols
			this.reader.skipBytes(numHuffmanCodes);
			unparsed = unparsed - numHuffmanCodes;
		}
		
		// move to next marker	
		this.reader.seek(finalPosition);
	}
	
	/**
	 * Parses the DQT marker segment used to define or redefine the quantization
	 * tables used in an image. Checks for valid values in the number of quantization
	 * values and in the information byte.
	 * 
	 * @throws FatalException
	 */
	private void parseDqt() throws FatalException {
		// get the length of this segment
		int length = (int) reader.readBytes(2, this.getByteOrder());
		
		// remember the position to seek to at the end
		// subtracting 2 to get back the position before reading the length
		long finalPosition = this.reader.getFilePointer() + length - 2;

		int unparsed = length - 2;
		
		while (unparsed != 0) {
			// read the 1-byte info, get the quantization value size and table id
			byte dqtInfoList = (byte) reader.readBytes(1, this.getByteOrder());
			unparsed--;
			byte[] dqtInfo = ByteReader.splitByteInTwo(dqtInfoList);
			// get the quantization value size (1 or 2 bytes) to know how many bytes to skip
			byte valueSize = 0;
			if (dqtInfo[0] == 0) {
				valueSize = 1;
			} else if (dqtInfo[0] == 1) {
				valueSize = 2;
			} else {
				//	found an unexpected value here
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_DQT));
				// Skip to next marker
				this.reader.seek(finalPosition);
			}
			
			// record the quantization table id
			this.image.addQuantValTableID(dqtInfo[1]);
			// skip the quantization values
			int skipBytes = 0;
			if (valueSize == 1) {
				skipBytes = 64;
			} else if (valueSize == 2) {
				skipBytes = 128;
			}
			// make sure there are still skipBytes left to skip
			if (unparsed < skipBytes) {
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_DQT));
				// Skip to next marker
				this.reader.seek(finalPosition);
			}
			this.reader.skipBytes(skipBytes);
			unparsed = unparsed - skipBytes;
			// loop back to process next quantization table if it is present
		}

		// move to next marker	
		this.reader.seek(finalPosition);
	}
	
	/**
	 * Parse the DRI marker segment noting any anomalies
	 * 
	 * @throws FatalException
	 */
	private void parseDri() throws FatalException {
		// get the length of this segment
		int length = (int) reader.readBytes(2, this.getByteOrder());
		
		// check the validity of the length - should always be 4
		if (length != 4) {
			//	found an unexpected value here
			this.addAnomaly(
					this.getAnomsPossible().
					getSevereElement(JpegAnomalies.JPEG_BAD_DRI));
		}
		//	move to next marker	
		this.reader.skipBytes(length-2);
	}
	
	/**
	 * Parses a SOF marker segment, storing all relevant metadata (like
	 * image height and width) and noting any anomalies.
	 * See B.2.2 Frame header syntax (p. 35 CCITT Rec. T.81 (1992 E))
	 * 
	 * Parameter									Size (bits)
	 * -----------------------------------------------------
	 * Lf (Frame header length)							16
	 * P (Sample precision)								8
	 * Y (No. of lines / height)						16
	 * X (No. of samples per line / width)				16
	 * Nf (No. of image components in frame)			8
	 * 
	 * (and for each image component):
	 * Ci (Component identifer)							8
	 * Hi (Horizontal sampling factor)					4
	 * Vi (Vertical sampling factor)					4
	 * Tqi (Quantization table destination selector) 	8
	 * 
	 * @throws FatalException
	 */
	private void parseSof()  throws FatalException {		
		// get the length of this segment (16 bits)
		int length = (int) reader.readBytes(2, this.getByteOrder());
		
		// determine sample precision (can be 8 or 12 bits)
		// TODO: can this really be other bit lengths?
		byte precision = (byte) reader.readBytes(1, this.getByteOrder());
		if (precision != 8 && precision != 12) {
			this.addAnomaly(
					this.getAnomsPossible().
					getSevereElement(JpegAnomalies.JPEG_BAD_SOF));
		} 
		
		// get image height
		this.image.setPixelsVertical((int) reader.readBytes(2, this.getByteOrder()));
		// get image width
		this.image.setPixelsHorizontal((int) reader.readBytes(2, this.getByteOrder()));
		// get the number of components in the image
		int numComps = (int) reader.readBytes(1, this.getByteOrder());
		this.image.setNumComponents(numComps);
		// set the bits per component precision for each component
		Vector bps = new Vector();
		for (int i=0; i<numComps; i++) {
			bps.add(new Integer(precision));
		}
		this.image.setBitsPerSample(bps);
		// set color space
		if (this.hasFormatAttribute(Jpeg.Attribute.IS_JFIF)) {
			switch (numComps) {
				case 1: this.image.setColorSpace(ColorSpace.CS_Y); break;
				case 3: this.image.setColorSpace(ColorSpace.CS_YCBCR); break;
				default: 
					this.addAnomaly(
							this.getAnomsPossible().
							getSevereElement(JpegAnomalies.IMG_UNKNOWN_COLORSPACE));
			}
		} else {
			
		}
		
		int [] xSamps = new int[numComps]; // will store horizontal sampling
		int [] ySamps = new int[numComps]; // will store vertical sampling
		int i=0;
		// do some checking for each component
		while (i<numComps && continueParsing) {
			// add component identifier
			this.image.addComponentID((int) reader.readBytes(1, this.getByteOrder()));
			
			// check horizontal and vertical sampling
			byte samplingList = (byte) reader.readBytes(1, this.getByteOrder());
			byte[] samples = ByteReader.splitByteInTwo(samplingList);
			if ((samples[0] != 1 && samples[0] != 2 && samples[0] != 3 && samples[0] != 4) ||
				(samples[1] != 1 && samples[1] != 2 && samples[1] != 3 && samples[1] != 4)) {
					// bad sampling values
					this.addAnomaly(
							this.getAnomsPossible().
							getSevereElement(JpegAnomalies.JPEG_BAD_SOF));
			} else {
				xSamps[i] = samples[0];
				ySamps[i] = samples[1];
				//System.out.println("Hor Sampling: " + samples[0] + "\tVert Sampling: " + samples[1]);
			}
			
			// check that the quantization table has been defined
			boolean sawQTable = false;
			int thisQTableID = (int) reader.readBytes(1, this.getByteOrder());
			//System.out.println("thisQTableID: " + thisQTableID);
			Iterator qtIter = this.image.getQuantValTableId().iterator();
			while (qtIter.hasNext()) {
				int storedQTableID = ((Integer) qtIter.next()).intValue();
				if (thisQTableID == storedQTableID) {
					sawQTable = true;
				}
			}
			if (!sawQTable) {
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_MISSING_DQT));
			}
			i++;
		}
		
		// record the horizontal and vertical sampling
		StringBuffer xSampsStr = new StringBuffer("");
		for (int j=0; j<xSamps.length; j++) {	
			xSampsStr.append(Integer.toString(xSamps[j]) );
			if (j != xSamps.length-1) {
				xSampsStr.append(":");
			}
		}
		this.image.setSamplingHor(xSampsStr.toString());
		StringBuffer ySampsStr = new StringBuffer("");
		for (int j=0; j<ySamps.length; j++) {	
			ySampsStr.append(Integer.toString(ySamps[j]) );
			if (j != ySamps.length-1) {
				ySampsStr.append(":");
			}
		}
		this.image.setSamplingVer(ySampsStr.toString());
 
		// move to next marker	
		this.reader.skipBytes(length-(8+(3*numComps)));
	}
	
	
	/**
	 * Parses a SOS marker segment noting any anomalies. Also reads
	 * the data immediately following the SOS marker until the next marker is
	 * found
	 * 
	 * @throws FatalException
	 */
	private void parseSos() throws FatalException {
		boolean skipComponents = false; // used to skip to start of data if error is found
		
		// get the length of this segment
		int length = (int) reader.readBytes(2, this.getByteOrder());
		
		// remember the position to go to if an error is found before the data
		long afterComponents = this.reader.getFilePointer() + length -2;

		// number of components in scan - must be >= 1 and <= 4
		int numComps = (int) reader.readBytes(1, this.getByteOrder());
					
		// check that the length is 6 + (2*number of components in scan)
		if (length != 6 + (2*numComps)){
			// wrong length
			this.addAnomaly(
					this.getAnomsPossible().
					getSevereElement(JpegAnomalies.JPEG_BAD_SOS));
			//System.out.println("Wrong SOS length: " + length);
			skipComponents = true;
		}
		
		if (numComps<1 || numComps > 4) {
			//	wrong number of components
			this.addAnomaly(
					this.getAnomsPossible().
					getSevereElement(JpegAnomalies.JPEG_BAD_SOS));
			//System.out.println("Wrong SOS number of components: " + numComps);
			skipComponents = true;
		}
		
		// the components
		int i=0;
		while (i<numComps && !skipComponents) {
			// check that the identifier was declared in the SOF
			int id = (int) reader.readBytes(1, this.getByteOrder());
			//System.out.println("Component ID in SOS: " + id);
			if (!this.image.getComponentId().contains(new Integer(id))){
				//	id was not declared earlier in the SOF marker
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_SOS));
				//System.out.println("Component ID not declared earlier: " + id);
				skipComponents = true;
			}
			// check that the Huffman tables were declared in DHT markers
			byte tableList = (byte) reader.readBytes(1, this.getByteOrder());
			byte[] tables = ByteReader.splitByteInTwo(tableList);
			if (!this.image.getHuffmanDcTableId().contains(new Integer(tables[0])) ||
				!this.image.getHuffmanAcTableId().contains(new Integer(tables[1]))){
				//	table id was not declared earlier in the SOF marker
				this.addAnomaly(
						this.getAnomsPossible().
						getSevereElement(JpegAnomalies.JPEG_BAD_SOS));
				//System.out.println("Huffman table ID not declared earlier: " + tables[0] + " or " + tables[1]);
				skipComponents = true;
				/*
				System.out.println("Huffman tables that have been defined:");
				System.out.print("\tHuffman AC Table IDs: ");
				Iterator iter = this.image.getHuffmanACTableID().iterator();
				while (iter.hasNext()) {
					System.out.print((Integer)iter.next() + " ");
				}
				System.out.print("\n\tHuffman DC Table IDs: ");
				iter = this.image.getHuffmanDCTableID().iterator();
				while (iter.hasNext()) {
					System.out.print((Integer)iter.next() + " ");
				}
				System.out.println();*/
			}
			i++;
		}
		
		if (!skipComponents) {
			this.reader.skipBytes(length-(6+(2*numComps)));
		} else {
			this.reader.seek(afterComponents);
		}
		
		//System.out.println("After SOS info at offset: " + this.reader.getFilePointer());
		
		// now at the image data - scan forward until the next marker is found
		// have to watch out for 'false markers' (any 0xFF or 0x00 bytes following a
		// 0xFF is not a marker - assume its false until we see the next marker).
		// Also skip the restart markers which can occur inb the middle of the data
		boolean falseMarker = true;
		int thisByte = (int) this.reader.readBytes(1, this.getByteOrder());
		int nextByte = (int) this.reader.readBytes(1, this.getByteOrder());
		
		while (falseMarker || thisByte != 0xFF) {
			// read the next byte. note that this should not produce an endless
			// loop because we know that that this file has an EOI marker - otherwise
			// it never would have passed the 'isType(String)' test
			thisByte = nextByte;
			nextByte = (int) this.reader.readBytes(1, this.getByteOrder());
			if (thisByte == 0xFF && nextByte != 0xFF && nextByte != 0x00 &&
				nextByte != 0xd0 && nextByte != 0xd1 && nextByte != 0xd2 && nextByte != 0xd3 &&
				nextByte != 0xd4 && nextByte != 0xd5 && nextByte != 0xd6 && nextByte != 0xd7) {
				falseMarker = false;
			}
		}
		// now pointing at a marker - walk back to the beginnning of the marker
		this.reader.seek(this.reader.getFilePointer()-2);
	}

	/**
	 * Creates a file that is the migrated version of this file. 
	 * Note:  The following migration code are only for testing of re/ingest, 
	 * it has not determined to be a permanent migration for JPEG file.
	 * 
	 * @throws PackageException
     * @throws FatalException
	 */
	public void migrate() throws PackageException, FatalException{
	   String methodName = "migrate()";
		
       String migForm = null; // migration format
       String procedure = Procedure.TRANSFORM_MIG; // event procedure
       boolean shouldContinue = migrateSetup();
        
        // exit early if we already migrated or this file
        // should not be migrated.
        if (!shouldContinue){
            return;
        }

        // put in separate dir
        shouldContinue = transformStart(procedure, this.getFormatCode(), true); 

        // get the migration format from the configuration files
        if (shouldContinue){
            String formProp = "MIG_" + this.getFormatCode() + "_FORMAT";
            migForm = ArchiveProperties.getInstance().getArchProperty(formProp);
        }
 
	    if (shouldContinue && migForm.equals(TransformationFormat.JPEG_JP2_1)) {
	        // create a J2k file converted from jpeg
	        ExternalProgram prog = new ExternalProgram();
	
	        // construct the ideal dir name
	        String dirName = this.getOid() + "_" + migForm;
	
	        File migDirPath = new File(this.getIp().getMigratedDir(),dirName);
	        
	        // find an unique directory name that is not exist.
	        for (int i=1;migDirPath.exists();i++){
	            dirName = this.getOid() + "_" + migForm + "_" + i;
	            migDirPath = new File(this.getIp().getMigratedDir(), dirName);
	        }
	        
	        // make directory within migration directory
	        migDirPath.mkdirs();
	        File outputFile = new File(migDirPath, "mig.jp2");
	        String outputPath = outputFile.getAbsolutePath();
	        String fromFile = this.getPath();
	        
	        // tell DataFileFactory to treat these files as created by the archive
	        DataFileFactory.getInstance().addFutureOriginArchivePath(outputFile.getAbsolutePath());
	
	        // construct command to convert jpeg file to jp2 file
	        String cmdProperty = ArchiveProperties.getInstance().getArchProperty("JPEG_MIG_CMD");
	        cmdProperty = cmdProperty.replaceFirst("%INPUT_FILE%", fromFile);
	        cmdProperty = cmdProperty.replaceFirst("%OUTPUT_FILE%", outputPath);
	        String[] command = cmdProperty.split("\\s");                
	
	        // migrate jpeg to jp2
	        prog.executeCommand(command);
	        
	        if (prog.getExitValue() == 0) // success, no error
	        {
	            // create DataFile, add events and relationships
	            transformEnd(procedure, outputPath, Procedure.PROC_JPEG_MIG_1);
	        } else { // encounter error
	            Informer.getInstance().error(this, methodName,
	                "Encounter problems during " + cmdProperty,
	                " Error Code: " + prog.getExitValue() + " File:" + this.getPath(),                                
	                new PackageException("Migration Problem"));
	        }
	        // clean up file references
	        migDirPath = null;
	        outputFile = null;
	        
	    } else if (shouldContinue) {
	        // end of normalization for this file
	        Informer.getInstance().info(this, methodName, "Done " + procedure 
	        	+ " at " + DateTimeUtil.now(), "File: " + this.getPath(), false);
	    }
    
	}
	
	/**
	 * Reads and discards an arbitrary number of bytes from this file
	 * 
	 * @throws FatalException
	 */
	private void skipMarker() throws FatalException {
		int length = (int) reader.readBytes(2, this.getByteOrder());
		this.reader.skipBytes(length-2);
	}
	
	/**
	 * Puts all the JPEG file's members and their values in a String.
	 * 
	 * @return all the members and their values
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		Iterator iter = null;
		boolean isTrue;
		
		sb.append("\nJpeg markers found: ");
		if (this.getMarkersUsed() == null) {
			sb.append(this.getMarkersUsed() + "\n");
		} else {
			iter = this.getMarkersUsed().iterator();
			while (iter.hasNext()) {
				sb.append(iter.next() + " ");
			}
		}
		sb.append("\n");
		
		isTrue = this.hasFormatAttribute(Jpeg.Attribute.IS_ADOBE_PS_3);
		sb.append("Is Adobe PS 3 variation: " +  isTrue + "\n");
	
	
		isTrue = this.hasFormatAttribute(Jpeg.Attribute.IS_EXIF);
		sb.append("Is Exif variation: " +  isTrue + "\n");
	
		isTrue = this.hasFormatAttribute(Jpeg.Attribute.IS_JFIF);
		sb.append("Is JFIF variation: " +  isTrue + "\n");
	
		isTrue = this.hasFormatAttribute(Jpeg.Attribute.IS_UNKNOWN_VAR);
		sb.append("Is unknown variation: " +  isTrue + "\n");
		
		
		return sb.toString();
	}
	
    public Document toXML() throws FatalException {
        // Document.
        Document doc = super.toXML();

        // Namespace.
        String namespace = doc.getNamespaceURI();

        // Root element.
        Element rootElement = doc.getDocumentElement();
        
        return doc;
    }
}
