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
package edu.fcla.daitss.format.image.tiff;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;
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
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.Image;
import edu.fcla.daitss.format.image.ImageMetadataConflicts;
import edu.fcla.daitss.format.image.Orientation;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * Represents a TIFF file. 
 * 
 * TODO: check tags against 
 * http://www.awaresystems.be/imaging/tiff/tifftags.html
 * 
 * @author Andrea Goethals, FCLA
 */
public class Tiff extends DataFile {
	
	/**
	 * Attribute represents an attribute specific to Tiff files.
	 * 
	 * @author Andrea Goethals, FCLA
	 *
	 */
	public static class Attribute {	
	}
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.image.tiff.Tiff";
	
	/**
	 * Determines whether or not the file is a Tiff file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a Tiff file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
	 * Determines whether or not the file is a Tiff file when metadata about
	 * this file is available. Determines that it is from the first
	 * few bytes of the file. 
	 * 
	 * @return	a boolean with <code>true</code> if it is a tiff image,
	 * 				otherwise false
	 * @param filePath An absolute path to an existing file.
	 * @param _metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		String methodName = "isType(String, DataFile)";
		
        boolean rCode = false;
        
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
		File tempFile = new File(filePath);
		tempReader = new ByteReader(tempFile, "r");
		
		// read 4 bytes if the file size is at least that long
		
		long fsize = tempFile.length();
		if (fsize >= 4) {
			//	read first four bytes and look for one of two tiff headers
			// depending on endian order
			int result = (int)tempReader.readBytes(4, DataFile.BYTE_ORDER_UNKNOWN);
			if (result == 0x4d4d002a || result == 0x49492a00) {
				rCode = true;                
			} 
		}
		
		tempReader.close();
		tempReader = null;
		tempFile = null;
		
		return rCode;
	} // end isType        

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 * @throws PackageException
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
		
		if (Tiff.isType(testFile, null)) {
			Tiff tFile = new Tiff(testFile, new SIP("AB12345678"));
			tFile.setOid("F20040101_AAAAAA");
			tFile.extractMetadata();
			System.out.println(tFile);
		} else {
			System.out.println("File " + testFile + " is not a tiff image");
		}
	}
	
	 /**
	 * Print the command line usage of this class.
	 */
	private static void printUsage() {
		System.out.println("java Tiff [-p packagePath] [-f filename]");
	}
	
	/**
	 * All the Tiff tags contained in this tiff file
	 */
	private TreeSet allTags = null;
	
	/**
	 * Used to determine if this tiff image is a revision 6.0 tiff. When one
	 * of the data types that were new to revision 6.0 is used in this image,
	 * this is set to <code>true</code>.
	 */
	private boolean hasSixODataType = false;
	
	/**
	 * An image contained in this Tiff File
	 */
	private TiffImage image = null;
	
	/**
	 * The number of IFDs in this Tiff File
	 */
	private int numIfds = 0; 
	
	/**
	 * The Tiff tags related to a particular image in this Tiff file
	 */
	private int[] parsedTags = null;
	
	/**
	 * All known possible Tiff tags (not necessarily found in this Tiff File)
	 */
	private TiffTags tt = null;

	/**
	 * The constructor to call for an existing Tiff file when metadata about
	 * this file is not available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public Tiff(String path, InformationPackage ip) throws FatalException {
		super(path, ip); // build data structures common to all RasterImageFile objects

		this.setMediaType(MimeMediaType.MIME_IMG_TIFF);
		this.setFileExt(Extension.EXT_TIF);
		
		this.tt = new TiffTags();
		this.anomsPossible = null;
		this.anomsPossible = new TiffAnomalies();
		
		metadataConfPossible = null;
		metadataConfPossible = new ImageMetadataConflicts();
	}      
	
	/**
	 * The constructor to call for an existing Tiff file when metadata about
	 * this file is available.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @param _metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public Tiff(String path, InformationPackage ip, METSDocument _metadata)
		throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}


	 public Document toXML() throws FatalException {
	        // Document.
	        Document doc = super.toXML();

	        // Namespace.
	        String namespace = doc.getNamespaceURI();

	        // Root element.
	        Element rootElement = doc.getDocumentElement();
	        
	        // TODO waiting for dbupdate
	        
	        return doc;
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
	
		// set the Tiff version of this file
		this.setMediaTypeVersion(TiffRevisions.calcRevision(allTags, this, hasSixODataType));
		
		// this is after we get the revision (version) because
		// DataFile.evalMembers will determine the format code
		// based on the revision
		super.evalMembers();
		
		// set the image profiles and other members for each image found 
		// in this tiff file
		Iterator bsIter = this.getBitstreams().iterator();
		while (bsIter.hasNext()) {
			TiffImage ti = (TiffImage) bsIter.next();
			ti.evalMembers();
			
			// set it
			ti.setProfiles(TiffProfiles.calcProfile(ti.getImageTags(),ti));
		}
	}
	
	/**
	 * Checks to see if the data at the offset will exceed the
	 * length of this file. The file size should already be set before
	 * calling this method. If the data type sent into this method
	 * is invalid no error will be seen.
	 * 
	 * @param offset	the byte offset into the file
	 * @param type		the tiff data type
	 * @param count	the number of values found at the offset
	 * @return	<code>true</code> if the offset data will exceed the
	 * 				file length, otherwise <code>false</code>
	 */
	private boolean exceedsFileSize(long offset, int type, long count){
		boolean exceeds = false;
		long filesize = this.getSize();
		if (filesize > 0){
			if (offset > filesize) {
				exceeds = true;
			} else {
				if (TiffDataTypes.isValidDataType(type)){
					if ((offset + (TiffDataTypes.getNumBytes(type) * count)) > filesize){
						exceeds = true;
					}
				}
			}
		}
		return exceeds;
	}

	/**
	 * Given a 4-byte offset (last 4 bytes of a Tiff field), this method returns
	 * the values contained in the offset. This method extracts 1-4 1-byte values, 
	 * or 1-2 2-byte values.
	 * 
	 * @param oldOffset	the offset value in a Tiff field
	 * @param count	the count in a Tiff field
	 * @param tiffTypeByte	the number of bytes taken by theTiff data type
	 * @return	the values contained in this offset
	 * @throws FatalException
	 */
	private int[] getOffsetVals(long oldOffset, long count, int tiffTypeByte) 
		throws FatalException {
		int[] vals = new int[(int)count];
		//System.out.println("oldOffset: " + oldOffset);
		int fOffset = (int) oldOffset;
		
		// flip the bytes back to the way they appear in the file if the data
		// is little-endian and it will need to be split. Little-endian data had 
		// been flipped earlier in parseIFDs(int) in case this was an offset to 
		// value(s) instead of the values themselves. Does nothing if the 
		// data is big-endian 
		if (this.getByteOrder() == DataFile.BYTE_ORDER_LE &&
			count == 1 && tiffTypeByte == 4) {
				// don't flip the bytes - value is the offset
		} else {
			// flip the bytes if its little-endian
			fOffset = ByteReader.flipLeBytes((int)oldOffset,4,this.getByteOrder());
		}
		
		// extract the values depending on the number wanted and the tiff
		// data type.
		if (tiffTypeByte == 4){
			// don't need to split it up (send back the whole thing as is as the first value)
			vals[0] = fOffset;
		} else {
			//System.out.println("offset: " + fOffset);
			vals = ByteReader.splitLeftPackedData((int)count,
				tiffTypeByte, fOffset, this.getByteOrder());
		}
		return vals;
	}

	/**
	 * Checks that the list of tags is in ascending order.
	 * 
	 * @param tags the tag numbers contained in a single image
	 * @return <code>true</code> if the tags are ordered from lowest to
	 * 	highest, else <code>false</code>
	 */
	private boolean isAscendingOrder(int[] tags){
		boolean isSorted = true;
		int lastVal = -1;
		int i=0;
		boolean sawError = false;
		while (i<tags.length && !sawError){
			if (tags[i]<= lastVal){
				sawError = true;
				isSorted = false;
			}
			lastVal = tags[i];
			i++;
		}
		//System.out.println("Is sorted: " + isSorted);
		return isSorted;
	}
	
	/**
	 * Determines whether or not a value or values can fit within the IFD field's
	 * 2 byte slot for values. If it can't fit then this is an offset to the values. 
	 * Whether or not it can fit is determined by the data type and count of the 
	 * values.
	 * 
	 * @param type		the tiff data type
	 * @param count	the number of values for the tiff field
	 * @return				<code>true</code> if this is an offset to the values instead
	 * 							of the values themselves, otherwise <code>false</code>
	 */
	private boolean isOffset(int type, long count) {
		boolean isOffset = false;
		switch (type) {
			// 1 byte per
			case TiffDataTypes.ASCII:			// 1 byte per
				// fall through
			case TiffDataTypes.BYTE:  			// 1 byte per
				// fall through
			case TiffDataTypes.UNDEFINED: // 1 byte per
				// fall through
			case TiffDataTypes.SBYTE:			// 1 byte per
				if (count > 4){
					isOffset = true;
				}
				break;
			// 2 bytes per
			case TiffDataTypes.SHORT: 		// 2 bytes per
				// fall through
			case TiffDataTypes.SSHORT: 		// 2 bytes per
				if (count > 2) {
					isOffset = true;
				}
				break;
			// 4 bytes per
			case TiffDataTypes.FLOAT: 			// 4 bytes per
				// fall through
			case TiffDataTypes.LONG: 			// 4 bytes per
				// fall through
			case TiffDataTypes.SLONG: 		// 4 bytes per
				if (count > 1){
					isOffset = true;
				}
			// 8 bytes per
			case TiffDataTypes.RATIONAL: 	// 8 bytes per
				// fall through
			case TiffDataTypes.SRATIONAL: // 8 bytes per
				// fall through
			case TiffDataTypes.DOUBLE: 		// 8 bytes per
				// offset stays equal to false
				break;
			default :
				// unknown Tiff data type - won't be able to interpret the value(s)
				/*
				Informer.getInstance().info(this,
					"TiffFile.isOffset(byte, int) was given an invalid Tiff data type",
					"data type: " + type + " found in file " + this.getFilePath(),
					true);*/
				break;
		}
		
		return isOffset;
	}

	/**
	 * Parses itself and sets format-specific members. Opens the file
	 * in read-only mode. Assumes that this is a tiff file. <code>DataFile.filePath</code> 
	 * must be already set for this file.
	 * 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		String methodName = "parse()";
		
		// figure out endian-ness
		int result = (int)reader.readBytes(4, BYTE_ORDER_UNKNOWN);
		if (result == 0x4d4d002a) {
			 this.setByteOrder(DataFile.BYTE_ORDER_BE); // big-endian	
		} else if (result == 0x49492a00) {
			this.setByteOrder(DataFile.BYTE_ORDER_LE); // little-endian
		} else {
			// should not get here - it's not a Tiff image
			Informer.getInstance().fail(this, methodName,
				"Not a tiff file",
				"file: " + this.getPath(),
				new FatalException("Parsing a non-tiff"));
		}
		
		// get offset to first IFD
		int firstOffset = (int)reader.readBytes(4, this.getByteOrder());
		//System.out.println("Offset to first IFD: " + firstOffset);
		
		// start parsing the IFDs
		this.parseIfds(firstOffset);
	} // end parse

	/**
	 * Evaluates a single IFD field and sets members with it if applicable.
	 * Performs a number of validity checks on the field like:
	 * - has a known tag number
	 * - has a valid data type for that tag
	 * - has the right number of values for that tag
	 * 
	 * @param tag		a tiff tag number
	 * @param type		a tiff data type
	 * @param count	number of values for this field
	 * @param offset	value(s) for the field if it will fit in 2 bytes, otherwise the offset
	 * 						to the value(s) from the beginning of the file
	 * @throws FatalException
	 */
	private void parseField(int tag, int type, long count, long offset) 
		throws FatalException {
		String methodName = "parseField(int, int, long, long)";
		
		boolean bailOnTag = false; // whether or not to continue with tag
		boolean isOffset = false; // whether this is an offset to a value
		Vector tempV = null;
		char[] chars = null; // used for ascii type data
		int num, denom; // used for Rational data types
		int[] offsetVals = null; // values contained in the Value/Offset part of a Tiff field

		// check that the data type used in the file is legal for that tag
		boolean goodType = tt.isRightType(tag, type);
		
		if (!goodType) {
			SevereElement ta = 
				this.getAnomsPossible().
				getSevereElement(TiffAnomalies.TIFF_WRONG_TYPE);
			this.addAnomaly(ta);
			Informer.getInstance().info(this, methodName,
				"Wrong type",
				"file: " + this.getPath() + "\tfield: " + tag + "\ttype: " + type,
				false);
			bailOnTag = true; // wouldn't be able to interpret the value(s)
		}
		
		// check that the number of values is legal for that field
		boolean goodCount = tt.isGoodCount(tag, count);
		
		if (!goodCount) {
			System.out.println("Not a good count.");
			SevereElement ta = 
				this.getAnomsPossible().
				getSevereElement(TiffAnomalies.TIFF_WRONG_COUNT);
			this.addAnomaly(ta);
			System.out.println("Calling Informer.");
			Informer.getInstance().info(this, methodName,
				"Wrong number of values",
				"file: " + this.getPath() + "\tfield: " + tag + "\tcount: " + count,
				false);
			//System.out.println("After calling Informer.");
			bailOnTag = true; // wouldn't be able to interpret the value(s)
		}

		if (!bailOnTag) {
			switch (tag) {
				case 254 : // NewSubFileType 
					if (numIfds > 1){
						// this is a new image in the file
					}
					// set the bitstream role - note that the tiff 6.0 spec did not make
					// the setting of these bits as mutually-exclusive (for example
					// allowing for a low resolution transparancy and other nonsense.
					// Here only recognizing 4 possible roles based on:
					// 1 (only bit 0 set): low resolution image
					// 2 (only bit 1 set): single page of multi-page image
					// 4 (only bit 2 set): transparancy mask
					// 0 (no bits set) : main image
					offsetVals = getOffsetVals(offset, count, 
										TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0];
					//System.out.println("NewSubfileType: " + Integer.toHexString((int)offset));
					switch ((int)offset) {
						case 0: this.image.setRole(BitstreamRole.IMG_MAIN);
							break;
						case 1: this.image.setRole(BitstreamRole.IMG_LOW_RES);
							break;
						case 2: this.image.setRole(BitstreamRole.IMG_PAGE);
							break;
						case 4:  this.image.setRole(BitstreamRole.IMG_TRANSPARENCY);
							break;
						default: this.image.setRole(BitstreamRole.UNKNOWN);
							break;
					}
					break; 
				case 255 : // SubFileType - ignore
					break; 
				case 256 : // ImageWidth
					this.image.setPixelsHorizontal(offset);
					break;
				case 257 : // ImageHeight
					this.image.setPixelsVertical(offset);
					break;
				case 258 : // BitsPerSample
					tempV = new Vector((int)count);
					isOffset = isOffset(type, count);
					if (isOffset) {
						reader.seek(offset);	// go to offset
						for (int i = 0; i < count; i++) {
							int value = (int)reader.readBytes(TiffDataTypes.getNumBytes(type), 
								this.getByteOrder());
							tempV.add(new Integer(value));
						}
						reader.goBack(); // come back from offset
					} else { // value(s) in the offset
						offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
						for (int i=0; i<offsetVals.length; i++) {
							tempV.add(new Integer(offsetVals[i]));
						}
					}
					this.image.setBitsPerSample(tempV);
					break;
				case 259 : // Compression
					offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					String compCode = translateCompression((int) offset);
					if (this.image.getCompression().isValidCompression(compCode)){
						this.image.getCompression().setCompression(compCode);
					} else {
						// don't know what kind of compression this has
						SevereElement ta = 
							this.getAnomsPossible().
							getSevereElement(TiffAnomalies.FILE_UNKNOWN_COMPRESSION);
						//ta.setValue("Compression: " + Long.toString(offset));
						this.addAnomaly(ta);
						this.image.getCompression().setCompression(Compression.COMP_UNKNOWN);
					}
					break;
				case 262 : // PhotometricInterpretation
					offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					// account for palette (photometricInterpretation code = 3)
					if (offset == 3){
						// the color space is RGB for palette-mapped Tiffs
						// according to the Tiff spec.
						this.image.setColorSpace(ColorSpace.CS_RGB);
						// the fact that it has a palette will be set
						// when it actually sees the ColorMap tag (tag 320)
					} else {
						// find out what the tiff color space is
						String csCode = translateColorSpace((int)offset);
						if (ColorSpace.isValidColorSpace(csCode)){
							this.image.setColorSpace(csCode);
						} else {
							// don't know what kind of color space this is
							SevereElement ta = 
								this.getAnomsPossible().
								getSevereElement(TiffAnomalies.IMG_UNKNOWN_COLORSPACE);
							this.addAnomaly(ta);
							this.image.setColorSpace(ColorSpace.CS_UNKNOWN);
						}
					}
					break;
				case 263 : // Threshholding - ignore
					break;
				case 264 : // CellWidth - ignore
					break;
				case 265 : // CellLength - ignore
					break;
				case 266 : // FillOrder - ignore
					break;
				case 269 : // DocumentName - ignore
					break;
				case 270 : // ImageDescription - ignore
					break;
				case 271 : // Make - ignore
					break;
				case 272 : // Model - ignore
					break;
				case 273 : // StripOffsets
					this.image.setStorageSegment(TiffImage.STORAGE_STRIP);	// StorageSegment
					this.image.setNumStrips(count);
					tempV = new Vector((int)count);
					isOffset = isOffset(type, count);
					if (isOffset) { // value(s) are stored at an offset
						reader.seek(offset);	// go to offset
						for (int i=0; i<count; i++) {
							tempV.add(i, new Long(reader.readBytes(TiffDataTypes.getNumBytes(type), this.getByteOrder())));
						}
						reader.goBack(); // come back from offset
					} else { // value(s) stored in the offset instead of at an offset
						if (type == TiffDataTypes.LONG){
							offsetVals = getOffsetVals(offset, count, 
									TiffDataTypes.getNumBytes(type));
							offset = offsetVals[0]; 
							tempV.add(new Long(offset)); // one strip, no offset to value, offset is value
						} else if (type == TiffDataTypes.SHORT){
							offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
							for (int i=0; i<offsetVals.length; i++) {
								tempV.add(new Integer(offsetVals[i]));
							}
						}
					}
					this.image.setStripOffsets(tempV);
					break;
				case 274 : // Orientation
					offsetVals = getOffsetVals(offset, count, 
						TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					if (offset > 0 && offset < 9) {
						String or = tt.mapOrientation((int)offset);
						this.image.setOrientation(or);
					} else {
						SevereElement ta = 
							this.getAnomsPossible().
							getSevereElement(TiffAnomalies.IMG_UNKNOWN_ORIENTATION);
						this.addAnomaly(ta);
						this.image.setOrientation(Orientation.UNKNOWN);
					}
					break;
				case 277 : // SamplesPerPixel
					offsetVals = getOffsetVals(offset, count, 
						TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					this.image.setNumComponents((int)offset);
					StringBuffer samp = new StringBuffer("");
					for (int i=0; i<(int)offset; i++){
						// all tiff images are assumed to not do any downsampling.
						// in other words, unless the color space is YCbCr, there is
						// data for each component for every pixel. this will be overridden
						// for YCbCr images later
						samp.append("1");
						if (i != (int)offset-1) {
							samp.append(":");
						}
					}
					this.image.setSamplingHor(samp.toString());
					this.image.setSamplingVer(samp.toString());
					break;
				case 278 : // RowsPerStrip
					offsetVals = getOffsetVals(offset, count, 
							TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0];
					this.image.setRowsPerStrip(offset);
					break;
				case 279 : // StripByteCounts
					this.image.setNumStrips(count); // should have already been set in case 273
					tempV = new Vector((int)count);
					if (isOffset(type,count)) { // value(s) are stored at an offset
						reader.seek(offset);	// go to offset
						for (int i=0; i<count; i++) {
							tempV.add(i, new Long(reader.readBytes(TiffDataTypes.getNumBytes(type),
								 this.getByteOrder())));
						}
						reader.goBack(); // come back from offset
					} else { // value(s) stored in the offset instead of at an offset
						if (type == TiffDataTypes.LONG){
							offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
							offset = offsetVals[0];
							tempV.add(new Long(offset)); // one strip, no offset to value, offset is value
						} else if (type == TiffDataTypes.SHORT){
							offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
							for (int i=0; i<offsetVals.length; i++) {
								tempV.add(new Long(offsetVals[i]));
							}
						}
					}
					this.image.setStripByteCounts(tempV);
					//this.image.calcMaxStripByteCount();
					break;
				case 280 : // MinSampleValue - ignore
					break;
				case 281 : // MaxSampleValue - ignore
					break;
				case 282 : // XResolution
					reader.seek(offset);	// go to offset
					// Don't change the type parameter to getNumBytes to 'type' - want to use longs here because
					// rationals are 2 longs
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setXResolution(tiffRationalPrimitive(num,denom));
					reader.goBack();
					break;
				case 283 : // YResolution
					reader.seek(offset);	// go to offset
					// Don't change the type parameter to getNumBytes to 'type' - want to use longs here because
					// rationals are 2 longs
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setYResolution(tiffRationalPrimitive(num,denom));
					reader.goBack();
					break;
				case 284 : // PlanarConfiguration
					offsetVals = getOffsetVals(offset, count, 
						TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					if (offset == 1 || offset ==2){
						String pc = tt.mapPlanarConfig((int)offset);
						this.image.setPlanarConfig(pc);
					} else {
						SevereElement ta = this.getAnomsPossible().
							getSevereElement(TiffAnomalies.TIFF_UNKNOWN_PLANCONFIG);
						this.addAnomaly(ta);
						this.image.setPlanarConfig(TiffImage.PLANAR_CONFIG_UNKNOWN);
					}
					break;
				case 285 : // PageName - ignore
					break;
				case 286 : // XPosition - ignore
					break;
				case 287 : // YPosition - ignore
					break;
				case 288 : // FreeOffsets - ignore
					break;
				case 289 : // FreeByteCounts - ignore
					break;
				case 290 : // GrayResponseUnit
					offsetVals = getOffsetVals(offset, count, 
						TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					if (offset <1 || offset > 5) {
						SevereElement ta = this.getAnomsPossible().
							getSevereElement(TiffAnomalies.TIFF_UNKNOWN_GRUNIT);
						this.addAnomaly(ta);
						this.image.setGrayResponseUnit(TiffImage.PREC_UNKNOWN);
					} else {
						String gru = tt.mapGrayResponseUnit((int)offset);
						this.image.setGrayResponseUnit(gru);
					}
					break;
				case 291 : // GrayResponseCurve
					// the count should be == 2^bitsPerSample (for one of the values in bitsPerSample)
					// Do we want to check this??
					tempV = new Vector((int)count);
					if (isOffset(type,count)){ // value(s) are stored at an offset
						reader.seek(offset);	// go to offset
						for (int i=0; i<count; i++) {
								tempV.add(i, new Integer((int)(reader.readBytes(TiffDataTypes.getNumBytes(type), this.getByteOrder()))));
						}
						reader.goBack(); // come back from offset
					} else { // value(s) stored in the offset instead of at an offset
						offsetVals = getOffsetVals(offset,count,TiffDataTypes.getNumBytes(type));
						for (int i=0; i<offsetVals.length; i++) {
							//int value = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.SHORT), 
							//	this.getByteOrder());
							tempV.add(new Integer(offsetVals[i]));
						}
					}
					this.image.setGrayResponseCurve(tempV);
					break;
				case 292 : // T4Options - ignore
					break;
				case 293 : // T6Options - ignore
					break;
				case 296 : // ResolutionUnit
					offsetVals = getOffsetVals(offset, count, TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0]; 
					switch ((int)offset){
						case 1: this.image.setResUnit(Image.RES_UNIT_NONE);
							break;
						case 2: this.image.setResUnit(Image.RES_UNIT_INCH);
							break;
						case 3: this.image.setResUnit(Image.RES_UNIT_CM);
							break;
						default:
							//	unknown resolution unit value
							this.addAnomaly(this.getAnomsPossible().
									getSevereElement(TiffAnomalies.IMG_UNKNOWN_RESUNIT));
					}
					break;
				case 297 : // PageNumber - ignore
					break;
				case 300: // Tiff 4.0's ColorResponseUnit - ignore
					break;
				case 301 : // TransferFunction - ignore
					break;
				case 305 : // Software
					// Note that we are stripping off the NUL at the end
					chars = new char[(int)count-1];
					if (isOffset(type,count)) { // value(s) are stored at an offset
						reader.seek(offset);	// go to offset
						for (int i=0; i<count-1; i++) {
							chars[i] = (char)reader.readBytes(TiffDataTypes.getNumBytes(type), 
								this.getByteOrder());
						}
						this.setCreatorProg(new String(chars));
						reader.goBack();
					} else { // value stored in the offset
						// NOT TESTED YET. Would there ever be a 3 letter software?
						offsetVals = getOffsetVals(offset,count-1,TiffDataTypes.getNumBytes(type));
						for (int i=0; i<offsetVals.length; i++) {
							chars[i] = (char)offsetVals[i];
						}
						this.setCreatorProg(new String(chars));
					}
					break;
				case 306 :
					// the tiff format for this is YYYY:MM:DD HH:MM:SS using a
					// 24-hour clock; the length of this string is 20 bytes
					if (count != 20) {
						SevereElement ta = this.getAnomsPossible().
							getSevereElement(TiffAnomalies.TIFF_BAD_DATETIME);
						this.addAnomaly(ta);
						bailOnTag = true; // wouldn't be able to interpret the value(s)
					} else {
						// the data has to be at an offset - 19 bytes can't fit in 4 bytes
						reader.seek(offset);	// go to offset
						chars = new char[(int)count-1]; // STRIP OFF NUL AT END
						for (int i=0; i<count-1; i++) {
							chars[i] = (char)reader.readBytes(TiffDataTypes.getNumBytes(type), 
												this.getByteOrder());
						}
						try {
							String formattedDate = DateTimeUtil.convertDateTiff2Arch(new String(chars));
							this.setCreateDate(formattedDate);
						} catch (IllegalArgumentException e) { 
							// bad tiff date/time, need to record an anomaly
							SevereElement ta = this.getAnomsPossible().
								getSevereElement(TiffAnomalies.TIFF_BAD_DATETIME);
							this.addAnomaly(ta);
						} 
						reader.goBack();
					}
					break;
				case 315 : // Artist - ignore
					break;
				case 316 : // HostComputer - ignore
					break;
				case 317 : // Predictor - ignore
					break;
				case 318 : // WhitePoint
					this.image.setHasChromaticities(DataFile.TRUE);
					
					// the ordering of this tag is white[x], white[y]
					reader.seek(offset);	// go to offset
					// Don't change the type param to getNumBytes to 'type' - must stay as long
					// because this is one half of  a rational
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesWhitePointX(tiffRationalPrimitive(num,denom));
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesWhitePointY(tiffRationalPrimitive(num,denom));
					reader.goBack();
					break;
				case 319 : // PrimaryChromaticities
					this.image.setHasChromaticities(DataFile.TRUE);
					
					// the ordering of this tag is red[x],red[y],green[x],green[y],blue[x],blue[y]
					reader.seek(offset);	// go to offset
					//	Don't change the type param to getNumBytes to 'type' - must stay as long
					// because this is one half of  a rational
					// red[x]
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesPrimaryRedX(tiffRationalPrimitive(num,denom));
					// red[y]
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesPrimaryRedY(tiffRationalPrimitive(num,denom));
					// green[x]
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesPrimaryGreenX(tiffRationalPrimitive(num,denom));
					// green[y]
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesPrimaryGreenY(tiffRationalPrimitive(num,denom));
					// blue[x]
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesPrimaryBlueX(tiffRationalPrimitive(num,denom));
					// blue[y]
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					this.image.setChromaticitiesPrimaryBlueY(tiffRationalPrimitive(num,denom));
					reader.goBack();
					break;
				case 320 : // ColorMap
					// has a palette
					this.image.setHasIntPalette(DataFile.TRUE);
					
					reader.seek(offset);
					tempV = new Vector((int)count);
					for (int i=0; i<count; i++) {
						int colorVal = (int)reader.readBytes(TiffDataTypes.getNumBytes(type), this.getByteOrder());
						tempV.add(new Integer(colorVal));
					}
					reader.goBack();
					this.image.setColorMap(tempV);
					break;
				case 321 : // HalftoneHints - ignore
					break;
				case 322 : // TileWidth
					this.image.setTileWidth((int)offset);
					break;
				case 323 : // TileLength
					this.image.setTileLength(offset);
					break;
				case 324 : // TileOffsets
					this.image.setStorageSegment(TiffImage.STORAGE_TILE);	// StorageSegment
					this.image.setNumTiles(count);
					tempV = new Vector((int)count);
					if (isOffset(type,count)){
						reader.seek(offset);
						for (int i=0; i<count; i++) {
							long theTileOffset = reader.readBytes(TiffDataTypes.getNumBytes(type), 
								this.getByteOrder());
							tempV.add(new Long(theTileOffset));
						}
						reader.goBack();
					} else { // has only 1 tile
						offsetVals = getOffsetVals(offset,count,TiffDataTypes.getNumBytes(type));
						tempV.add(new Long(offsetVals[0]));
					}
					this.image.setTileOffsets(tempV);
					break;
				case 325 : // TileByteCounts
					tempV = new Vector((int)count);
					if (isOffset(type,count)){
						reader.seek(offset);
						for (int i=0; i<count; i++) {
							long tbc = reader.readBytes(TiffDataTypes.getNumBytes(type), this.getByteOrder());
							tempV.add(new Long(tbc));
						}
						reader.goBack();
					} else { // value(s) in the offset
						offsetVals = getOffsetVals(offset,count,TiffDataTypes.getNumBytes(type));
						for (int i=0; i<offsetVals.length; i++) {
							tempV.add(new Long(offsetVals[i]));
						}
					}
					this.image.setTileByteCounts(tempV);
					//this.image.calcMaxTileByteCount();
					break;
				case 326: // Class F's BadFaxLines - ignore
					break;
				case 327: // Class F's CleanFaxData - ignore
					break;
				case 328: // Class F's ConsecutiveBadFaxLines - ignore
					break;
				case 330: // Photoshop 6.0 / PageMaker SubIFDs - ignore
					break;
				case 332 : // InkSet - ignore
					break;
				case 333 : // InkNames - ignore
					break;
				case 334 : // NumberOfInks - ignore
					break;
				case 336 : // DotRange - ignore
					break;
				case 337 : // TargetPrinter - ignore
					break;
				case 338 : // ExtraSamples
					String es = null;
					if (isOffset(type,count)){
						reader.seek(offset);
						for (int i=0; i<count; i++) {
							int value = (int)reader.readBytes(TiffDataTypes.getNumBytes(type),
								this.getByteOrder());
							es = tt.mapExtraSamples(value);
							if (this.image.isValidExtraSample(es)){
								this.image.addExtraSample(es);
							} else {
								SevereElement ta = 
									this.getAnomsPossible().
									getSevereElement(TiffAnomalies.IMG_UNKNOWN_EXTRASAMPLES);
								this.addAnomaly(ta);
								bailOnTag = true; // wouldn't be able to interpret the value(s)
							}	
						}
						reader.goBack();
					} else { // value(s) stored in the offset
						offsetVals = getOffsetVals(offset,count,TiffDataTypes.getNumBytes(type));
						for (int i=0; i<offsetVals.length; i++) {
							es =  tt.mapExtraSamples(offsetVals[i]);
							if (this.image.isValidExtraSample(es)) {
								this.image.addExtraSample(es);
							} else {
								SevereElement ta = this.getAnomsPossible().
									getSevereElement(TiffAnomalies.IMG_UNKNOWN_EXTRASAMPLES);
								this.addAnomaly(ta);
								bailOnTag = true; // wouldn't be able to interpret the value(s)
							}
						}
					}
					break;
				case 339 : // SampleFormat - ignore
					break;
				case 340 : // SMinSampleValue - ignore
					break;
				case 341 : // SMaxSampleValue - ignore
					break;
				case 342 : // TransferRange - ignore
					break;
				case 343: // PageMaker's ClipPath - ignore
					break;
				case 344: // PageMaker's XClipPathUnits - ignore
					break;
				case 345: // PageMaker's YClipPathUnits - ignore
					break;
				case 346: // PageMaker's Indexed - ignore
					break;
				case 347: // Photoshop 6.0 JPEGTables
					break;
				case 351: // PageMakers's OPIProxy - ignore
					break;
				case 512 : // JPEGProc - ignore
					break;
				case 513 : // JPEGInterchangeFormat - ignore
					break;
				case 514 : // JPEGInterchangeFormatLngth - ignore
					break;
				case 515 : // JPEGRestartInterval - ignore
					break;
				case 517 : // JPEGLosslessPredictors - ignore
					break;
				case 518 : // JPEGPointTransforms - ignore
					break;
				case 519 : // JPEGQTables - ignore
					break;
				case 520 : // JPEGDCTables - ignore
					break;
				case 521 : // JPEGACTables - ignore
					break;
				case 529 : // YCbCrCoefficients
					tempV = new Vector((int)count);
					reader.seek(offset);
					// Don't change the type param to 'type' here - leave it as long because
					// that's equivalent to half a rational
					// LumaRed
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					tempV.add(tiffRationalObject(num, denom));
					// LumaGreen
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					tempV.add(tiffRationalObject(num, denom));
					// LumaBlue
					num = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					denom = (int)reader.readBytes(TiffDataTypes.getNumBytes(TiffDataTypes.LONG), this.getByteOrder());
					tempV.add(tiffRationalObject(num, denom));
					reader.goBack();
					this.image.setYcbcrCoefficients(tempV);
					break;
				case 530 : // YCbCrSubSampling
					tempV = new Vector((int)count);
					String yss = null;
					// the value should be in the offset
					offsetVals = getOffsetVals(offset,count,TiffDataTypes.getNumBytes(type));
					yss = tt.mapYcbcrSubsampling(offsetVals[0]);
					if (!yss.equals("")){
						tempV.add(new Integer(yss)); // YCbCrSubsampleHoriz
					}
					yss = tt.mapYcbcrSubsampling(offsetVals[1]);
					if (!yss.equals("")){
						tempV.add(new Integer(yss)); //	YCbCrSubsampleVert
					}
					if (this.image.isValidYcbcrSubsampling(tempV)){
						this.image.setYcbcrSubsampling(tempV);
						// set the subsampling values
						switch (offsetVals[0]){
							// we already know its a valid value here - don't need a default
							case 1: this.image.setSamplingHor("1:1:1"); break;
							case 2: this.image.setSamplingHor("2:1:1"); break;
							case 4: this.image.setSamplingHor("4:1:1"); break;
						}
						switch (offsetVals[1]){
							// we already know its a valid value here - don't need a default
							case 1: this.image.setSamplingVer("1:1:1"); break;
							case 2: this.image.setSamplingVer("2:1:1"); break;
							case 4: this.image.setSamplingVer("4:1:1"); break;
						}
					} else {
						SevereElement ta = this.getAnomsPossible().
							getSevereElement(TiffAnomalies.TIFF_UNKNOWN_YCBCRSUBSAMPLING);
						this.addAnomaly(ta);
						bailOnTag = true; // wouldn't be able to interpret the value(s)						
					}
					break;
				case 531 : // YCbCrPositioning
					offsetVals = getOffsetVals(offset,count,TiffDataTypes.getNumBytes(type));
					offset = offsetVals[0];
					if (offset != 1 && offset != 2){
						SevereElement ta = this.getAnomsPossible().
							getSevereElement(TiffAnomalies.TIFF_UNKNOWN_YCBCRPOSITIONING);
						this.addAnomaly(ta);
						bailOnTag = true; // wouldn't be able to interpret the value(s)					
					} else {
						String yp = tt.mapYcbcrPositioning((int)offset);
						this.image.setYcbcrPositioning(yp);
					}
					break;
				case 532 : // ReferenceBlackWhite
					// Seems like it would always be at the offset pointer ....
					tempV = new Vector((int)count);
					reader.seek(offset);
					for (int i=0; i<count; i++) {
						tempV.add(new Long(reader.readBytes(TiffDataTypes.getNumBytes(type), 
							this.getByteOrder())));
					}
					reader.goBack();
					this.image.setReferenceBlackWhite(tempV);					
					break;
				case 700: // XMLPackets - ignore
					break;
				case 32781: // PageMaker's ImageID - ignore
					break;
				case 33421: // TIFF/EP's CFARepeatPatternDim - ignore
					break;
				case 33422: // TIFF/EP's CFAPattern - ignore
					break;
				case 33423: // TIFF/EP's BatteryLevel - ignore
					break;
				case 33432 : // Copyright - ignore
					break;
				case 33434: // TIFF/EP's ExposureTime - ignore
					break;
				case 33437: // TIFF/EP's Fnumber - ignore
					break;
				case 33550: // GeoTiff's ModelPixelScaleTag - ignore
					break;
				case 33589: // Advent's Scale - ignore
					break;
				case 33590: // Advent's Revision - ignore
					break;
				case 33723: // TIFF/EP's IPTC/NAA - ignore
					break;
				// Intergraph uses tag 33920 for something (?)
				//case 33920: // GeoTiff's IntergraphMatrixTag (deprecated) - ignore
				//	break;
				case 33922: // GeoTiff's ModelTiepointTag - ignore
					break;
				case 34016: // TIFF/IT's Site - ignore
					break;
				case 34017: // TIFF/IT's ColorSequence - ignore
					break;
				case 34018: // TIFF/IT's IT8Header - ignore
					break;
				case 34019: // TIFF/IT's RasterPadding - ignore
					break;
				case 34020: // TIFF/IT's BitsPerRunLength - ignore
					break;
				case 34021: // TIFF/IT's BitsPerExtendedRunLength - ignore
					break;
				case 34022: // TIFF/IT's ColorTable - ignore
					this.image.setHasIntPalette(DataFile.TRUE);
					break;
				case 34023: // TIFF/IT's ImageColorIndicator - ignore
					break;
				case 34024: // TIFF/IT's BackgroundColorIndicator - ignore
					break;
				case 34025: // TIFF/IT's ImageColorValue - ignore
					break;
				case 34026: // TIFF/IT's BackgroundColorValue - ignore
					break;
				case 34027: // TIFF/IT's PixelIntensityRange - ignore
					break;
				case 34028: // TIFF/IT's TransparencyIndicator - ignore
					break;
				case 34029: // TIFF/IT's ColorCharacterization - ignore
					break;
				case 34030: // TIFF/IT's HCUsage - ignore
					break;
				case 34152: // RichTiff's Kodak IPTC - ignore
					break;
				case 34264: // GeoTiff's ModelTransformationTag - ignore
					break;
				case 34377: // Photoshop 3.0+ Image Resources - ignore
					break;
				case 34665: // EXIF's ExifIFD - ignore
					// offset to another IFD
					break;
				case 34675: // Photoshop 5.0+, TIFF/EP  InterColourProfile (ICC Profile)
					this.image.setHasIccProfile(DataFile.TRUE);
					break;
				case 34735: // GeoTiff's GeoKeyDirectoryTag - ignore
					break;
				case 34736: // GeoTiff's GeoDoubleParamsTag - ignore
					break;
				case 34737: // GeoTiff's GeoAsciiParamsTag - ignore
					break;
				case 34850: // TIFF/EP's ExposureProgram - ignore
					break;
				case 34852: // TIFF/EP's SpectralSensitivity - ignore
					break;
				case 34853: // TIFF/EP's GPSInfo - ignore
					break;
				case 34855: // TIFF/EP's ISOSpeedRatings - ignore
					break;
				case 34856: // TIFF/EP's OECF - ignore
					break;
				case 34857: // TIFF/EP's Interlace - ignore
					break;
				case 34858: // TIFF/EP's TimeZoneOffset - ignore
					break;
				case 34859: // TIFF/EP's SelfTimerMode - ignore
					break;
				case 36867: // TIFF/EP's DateTimeOriginal - ignore
					break;
				case 37122: // TIFF/EP's CompressedBitsPerPixel - ignore
					break;
				case 37377: // TIFF/EP's ShutterSpeedValue - ignore;
					break;
				case 37378: // TIFF/EP's ApertureValue - ignore;
					break;
				case 37379: // TIFF/EP's BrightnessValue - ignore;
					break;
				case 37380: // TIFF/EP's ExposureBiasValue - ignore;
					break;
				case 37381: // TIFF/EP's MaxApertureValue - ignore;
					break;
				case 37382: // TIFF/EP's SubjectDistance - ignore;
					break;
				case 37383: // TIFF/EP's MeteringMode - ignore;
					break;
				case 37384: // TIFF/EP's LightSource - ignore;
					break;
				case 37385: // TIFF/EP's Flash - ignore;
					break;
				case 37386: // TIFF/EP's FocalLength - ignore;
					break;
				case 37387: // TIFF/EP's FlashEnergy - ignore;
					break;
				case 37388: // TIFF/EP's SpatialFrequencyResponse - ignore;
					break;
				case 37389: // TIFF/EP's Noise - ignore;
					break;
				case 37390: // TIFF/EP's FocalPlaneXResolution - ignore;
					break;
				case 37391: // TIFF/EP's FocalPlaneYResolution - ignore;
					break;
				case 37392: // TIFF/EP's FocalPlaneResolutionUnit - ignore;
					break;
				case 37393: // TIFF/EP's ImageNumber - ignore;
					break;
				case 37394: // TIFF/EP's SecurityClassification - ignore;
					break;
				case 37395: // TIFF/EP's ImageHistory - ignore;
					break;
				case 37396: // TIFF/EP's SubjectLocation - ignore;
					break;
				case 37397: // TIFF/EP's ExposureIndex - ignore;
					break;
				case 37398: // TIFF/EP's TIFF/EPStandardID - ignore;
					break;
				case 37399: // TIFF/EP's SensingMethod - ignore;
					break;
				case 37724:  // Photoshop 6.0 ImageSourceData - ignore
					break;
				case 40965: // Exif's InteroperabilityIFD - ignore
					break;
				case 50255: // Photoshop 6.0 Annotations - ignore
					break;
				default :
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_UNKNOWN_TAG);
					this.addAnomaly(ta);
					bailOnTag = true;
			}
		}
	}
	
	/**
	 * Starts parsing the IFDs from the first one, continuing on to
	 * any additional IFDs until they are all read. Sets the file's members
	 * as it parses.
	 * 
	 * @param offset	the byte offset from the beginning of the
	 * 							file to the first IFD
	 * @throws FatalException
	 */
	private void parseIfds(int offset) throws FatalException {
		//	assume that there is at least one image in the file
		boolean hasMoreIFDs = true;
		
		boolean keepParsingField = true;
		boolean keepParsingFile = true;
		
		int numFields = -1; // number of fields in this IFD
		int fieldNum = 0; // number of fields read (including IFDs already read)
		
		while (keepParsingFile && hasMoreIFDs){
			// start reading and recording info about an image in this file
			image = new TiffImage(this);
			
			//	set the offset to the image's IFD - only way to identify multiple images
			// in the tiff file
			image.setLocation(Integer.toString(offset));
			this.addBitstream(image);
			
			// go to the image's IFD start	 
			this.reader.seek(offset);
			
			//	read the number of directory entries in this IFD (stored in the first 2 bytes of the IFD)
			numFields = (int)reader.readBytes(2, this.getByteOrder());
			// begin contstructing a list of the image's tags
			image.initTags(numFields);
			//System.out.println("Number of fields in this image: " + numFields);
			
			// see if there are no fields in this IFD
			if (numFields <= 0){
				// record the anomaly
				SevereElement ta = this.getAnomsPossible().
					getSevereElement(TiffAnomalies.TIFF_TAGLESS_IFD);
				this.addAnomaly(ta);
				/*Informer.getInstance().info(this,
					"Tiff.parseIFDs(int) found a tiff without an IFD",
					"file: " + this.getPath(),
					true);*/
				// STOP PARSING TIFF:
				hasMoreIFDs = false;
				continue;
			}
			numIfds++; // saw another IFD (or one if this is the first time through)

			// create a list of tags so we can check that they are sorted
			// empty out tags after each IFD
			parsedTags = null;
			parsedTags = new int[numFields];
				
			int currentField = 0; // field sequence within an IFD
			
			// read each 12-byte directory entry (field) in this IFD
			while (keepParsingFile && currentField < numFields) {
				keepParsingField = true;
					
				// read the tag number (first 2 bytes of the field)
				int tag = (int)reader.readBytes(2, this.getByteOrder());
				parsedTags[currentField] = tag; // to check sort order
				//System.out.print("Field " + (fieldNum+1) + "\tTag: " + tag);
				// check that the tag is non-negative
				if (tag < 0){
					// record the anomaly
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_NEG_TAG);
					this.addAnomaly(ta);
					// no need to continue with this field
					keepParsingField = false;
					/*Informer.getInstance().info(this,
						"Tiff.parseIFDs(int) found a tiff with a negative tag",
						"file: " + this.getPath() + "\ttag: " + tag,
						false);*/
				}
				// read the field type (next 2 bytes)
				int fieldType = (int) reader.readBytes(2, this.getByteOrder());
				//System.out.print("\tField type: " + fieldType);
				// check if the data type was new to tiff spec revision 6.0 -
				// is used to determine file format version
				if (TiffDataTypes.isSixODataType(fieldType)){
					hasSixODataType = true;
				}
				// check that the type is valid
				if (!TiffDataTypes.isValidDataType(fieldType)){
					// record the anomaly
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_UNKNOWN_TYPE);
					this.addAnomaly(ta);
					// no need to continue with this field
					keepParsingField = false;
					/*Informer.getInstance().info(this,
						"Tiff.parseIFDs(int) found a tiff with an unknown field type",
						"file: " + this.getPath() + "\ttag: " + tag + "\ttype: " + fieldType,
						false);*/
				}
				// read the number of values (next 4 bytes)
				long numValues = reader.readBytes(4, this.getByteOrder());
				//System.out.print("\tNumber of values: " + numValues);
				// check that the number of values is non-negative
				if (numValues < 0){
					// record the anomaly
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_NEG_FIELD_COUNT);
					this.addAnomaly(ta);
					// no need to continue with this field
					keepParsingField = false;
					/*Informer.getInstance().info(this,
						"Tiff.parseIFDs(int) found a tiff with a negative field count",
						"file: " + this.getPath() + "\ttag: " + tag,
						false);*/
				}
				// read the value offset
				long valueOffset = reader.readBytes(4, this.getByteOrder());
				//System.out.println("\tValue offset: " + valueOffset);
				// check that if this is an offset, it's non-negative
				if (isOffset(fieldType,numValues) && valueOffset < 0){
					//System.out.println("It's an offset");
					// record the anomaly
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_NEG_FILE_OFFSET);
					this.addAnomaly(ta);
					// no need to continue with this field
					keepParsingField = false;
				} else if (isOffset(fieldType,numValues) && exceedsFileSize(valueOffset, fieldType, numValues)){
					// falls off the end of the file
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_OFFSET_TOO_BIG);
					this.addAnomaly(ta);
					keepParsingField = false;
				}
				if (keepParsingField){
					parseField(tag,fieldType,numValues,valueOffset);
				}
				currentField++; // field sequence within an IFD
				fieldNum++; // field sequence of all fields in all IFDs in this file
				
				if (this.isHopelessFile()){
					keepParsingFile = false;
				}
			}
			
			// Done parsing this IFD
			
			// check that the tags are in ascending order
			boolean isSorted = this.isAscendingOrder(parsedTags);
			if (!isSorted){
				// anomaly - not in ascending order
				SevereElement ta = this.getAnomsPossible().
					getSevereElement(TiffAnomalies.TIFF_UNSORTED_TAGS);
				this.addAnomaly(ta);
				/*Informer.getInstance().info(this,
					"Tiff.parseIFDs(int) found a file with unsorted tags",
					"file: " + this.getPath(),
					true);*/
			}
			// see if there's another IFD to read (stored in next 4 bytes)
			int nextIFDOffset = (int)reader.readBytes(4, this.getByteOrder());
			//System.out.println("Next offset: " + nextIFDOffset);
			if (nextIFDOffset == 0) {
				// there are no more IFDs to parse
				hasMoreIFDs = false;
				
			} else {
				// there's another IFD to parse
				offset = nextIFDOffset;
				// check that the offset is not negative
				if (offset<0){
					SevereElement ta = this.getAnomsPossible().
						getSevereElement(TiffAnomalies.TIFF_NEG_FILE_OFFSET);
					this.addAnomaly(ta);
					/*Informer.getInstance().info(this,
						"Tiff.parseIFDs(int) found a file with a negative offset into the file",
						"file: " + this.getPath(),
						true);*/
					// STOP PROCESSING IFDs
					hasMoreIFDs = false;
				}
			}
			//	copy the IFD's tags to the image's list of IFD tags
			image.setImageTags(parsedTags);
		}
		
		// Done parsing all IFDs in this file. Copy each image's IFD to the file's
		// master list of tags so that the file's version can be determined.
		this.allTags = new TreeSet();
		Iterator bsIter = this.getBitstreams().iterator();
		while (bsIter.hasNext()) {
			TiffImage ti = (TiffImage) bsIter.next();
			int[] theImageTags = ti.getImageTags();
			int i=0;
			while (theImageTags != null && i< theImageTags.length) {
				// add the tag if it hasn't already been added
				allTags.add(new Integer(theImageTags[i]));
				i++;
			}
		}
	}

	/**
	 * Converts an integer numerator and integer denominator into a
	 * floating point number.
	 * 
	 * @param num integer numerator
	 * @param denom integer denominator
	 * @return the numerator divided by the denominator
	 */
	private Float tiffRationalObject(int num, int denom) {
		if (denom == 0) {
			return new Float(0);
		}
		return new Float((double)num/denom);
	}
	
	/**
	 * Converts an integer numerator and integer denominator into a
	 * floating point number.
	 * 
	 * @param num integer numerator
	 * @param denom integer denominator
	 * @return the numerator divided by the denominator
	 */
	private float tiffRationalPrimitive(int num, int denom) {
		if (denom == 0) {
			return 0.0f;
		}
		return (float)num/denom;
	}
	
	/**
	 * Puts all the Tiff file's members and their values in a String.
	 * 
	 * @return all the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);

		return sb.toString();
	}
	
	/**
	 * Maps a Tiff color space code to the color space code used by
	 * the archive.
	 * Note that the Tiff color space code 3 (Palette) is not treated
	 * as a color space in the archive. This is because a palette is a
	 * way to map one component into multiple components and does not
	 * imply anything about the color space of the multiple components.
	 * The tiff specification says that the result of mapping the 
	 * palette will be the RGB color space, but other formats handle
	 * this differently and so the color space and existence of a
	 * palette are handled separately in the archive.
	 * 
	 * @param intCs color space value from a Tiff Photometric tag
	 * @return the archive's code for the color space
	 */
	private String translateColorSpace(int intCs){
		String stringCs  = null;
		
		switch (intCs) {
			case 0: stringCs = ColorSpace.CS_WHITEISZERO; break;
			case 1: stringCs = ColorSpace.CS_BLACKISZERO; break;
			case 2: stringCs = ColorSpace.CS_RGB; break;
			case 4: stringCs = ColorSpace.CS_TRANSP; break;
			case 5: stringCs = ColorSpace.CS_CMYK; break;
			case 6: stringCs = ColorSpace.CS_YCBCR; break;
			case 8: stringCs = ColorSpace.CS_CIELAB; break;
			// no default - send out the null
		}
		
		return stringCs;
	}
	
	/**
	 * Maps a Tiff compression code to the compression code used by
	 * the archive.
	 * 
	 * @param intCompression compression value from a Tiff compression tag
	 * @return the archive's code for the compression
	 */
	private String translateCompression(int intCompression){
		String stringCompression = null;
		
		switch (intCompression) {
			case 1: stringCompression = Compression.COMP_NONE; break;
			case 2: stringCompression = Compression.COMP_CCITT_ID; break;
			case 3: stringCompression = Compression.COMP_GROUP_3_FAX; break;
			case 4: stringCompression = Compression.COMP_GROUP_4_FAX; break;
			case 5: stringCompression = Compression.COMP_LZW; break;
			case 6: stringCompression = Compression.COMP_JPEG; break;
			case 7: stringCompression = Compression.COMP_JPEG_NEW; break;
			case 8: stringCompression = Compression.COMP_DEFLATE; break;
			case 103: stringCompression = Compression.COMP_PEGASUS_IMJ; break;
			case 32766: stringCompression = Compression.COMP_NEXT; break;
			case 32771: stringCompression = Compression.COMP_NONE_WORD_ALIGNED; break;
			case 32773: stringCompression = Compression.COMP_PACKBITS; break;
			case 32809: stringCompression = Compression.COMP_THUNDERSCAN; break;
			case 32895: stringCompression = Compression.COMP_IT8_CT_PADDING; break;
			case 32896: stringCompression = Compression.COMP_IT8_LINEWORK; break;
			case 32897: stringCompression = Compression.COMP_IT8_MONOCHROME; break;
			case 32898: stringCompression = Compression.COMP_IT8_BINARY_LINE; break;
			case 32908: stringCompression = Compression.COMP_PIXAR10; break;
			case 32909: stringCompression = Compression.COMP_PIXAR11; break;
			case 32946: stringCompression = Compression.COMP_DEFLATE_UNOFFICIAL; break;
			case 32947: stringCompression = Compression.COMP_KODAK_DCS; break;
			case 34661: stringCompression = Compression.COMP_JBIG; break;
			case 34676: stringCompression = Compression.COMP_SGI_LOG_RLE; break;
			case 34677: stringCompression = Compression.COMP_SGI_LOG_24_PACKED; break;
			case 34715: stringCompression = Compression.COMP_JBIG2; break;
			// no default - send out the null
		}
		
		return stringCompression;
	}

} 
