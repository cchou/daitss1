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
package edu.fcla.daitss.format.image.jpeg2000;

import java.io.File;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.entity.AIP;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.SIP;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.DataFileFactory;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.file.Extension;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.format.image.jpeg2000.box.Box;
import edu.fcla.daitss.format.image.jpeg2000.box.ContigCodestream_jp2;
import edu.fcla.daitss.format.image.jpeg2000.box.FileType;
import edu.fcla.daitss.format.image.jpeg2000.box.Ipr;
import edu.fcla.daitss.format.image.jpeg2000.box.Jp2Header_jp2;
import edu.fcla.daitss.format.image.jpeg2000.box.Jpeg2000Signature;
import edu.fcla.daitss.format.image.jpeg2000.box.Uuid;
import edu.fcla.daitss.format.image.jpeg2000.box.UuidInfo;
import edu.fcla.daitss.format.image.jpeg2000.box.XML;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.Informer;

/**
 * Jp2 represents the JP2 file format that was specified
 * in part 1, Annex I, of the JPEG 2000 version 1 specification.
 * 
 * @author Andrea Goethals, FCLA
 */
public class Jp2 extends Jpeg2000 {
    
    /**
     * Attribute represents an attribute specific to Jp2 files.
     * Note that the length of the values of these constants have to be 
     * less than or equal to DataFile.MAX_FORMATATTRIBUTE_LENGTH and
     * they have to be unique in the FORMAT_ATTRIBUTES database table.
     * 
     * @author Andrea Goethals, FCLA
     *
     */
    public static class Attribute { 
    }

	/**
	 * Equivalent to a major version number of the JP2 format.
	 * This value appears in the Brand field of a File Type box
	 * for formats that are completely defined by the JP2 spec.
	 */
	public static final int BRAND_JP2 = 0x6A703220;
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.format.image.jpeg2000.Jp2";
	
    /**
     * JP2 version 1.0
     */
    public static final String VERSION_1_0 = "1.0";
    
	/**
	 * Determines whether or not the file is a JP2 file when metadata about
	 * this file is not available.
	 * 
	 * @param filePath absolute path to an existing readable file
	 * @return 	whether or not its a <FORMAT> file
	 * @throws FatalException
	 */
	public static boolean isType(String filePath) throws FatalException {
			return isType(filePath, null);
	}
	
	/**
	 * Determines whether or not the file is a JP2 file when metadata about
	 * this file is available. Determines that it is a JP2 file
	 * by:
	 * First seeing if it's a JPEG 2000 format. All JPEG 2000
	 * file formats start with the following 12 bytes:
	 * 0X0000 0X000C 0X6A50 0X2020 0X0D0A 0X870A 
	 * 
	 * If the file has that then we see if its a JP2 file. In a
	 * JP2 file, the JPEG 2000 signature is followed by the File
	 * Type box. The value of the Brand field in this box is
	 * 'jp2\040'. Note that we are seeing if this file is entirely
	 * described by the JP2 spec. It is possible that this file has
	 * a value other than 'jp2\040' but lists 'jp2\040' in the
	 * Compatibility field in the File Type box. For now we will
	 * not consider those files JP2 files.
	 * 
	 * @return <code>true</code> if it is a JP2,
	 *               otherwise <code>false</code>
	 * @param filePath	an absolute path to an existing file
	 * @param	_metadata metadata about this DataFile
	 * @throws FatalException
	 */
	public static boolean isType(String filePath, METSDocument _metadata) 
		throws FatalException {
		//System.out.println("In JP2 isType");
		
		// first see if its a JPEG 2000 file format
		boolean isType = Jpeg2000.isType(filePath, _metadata);
		
		// see if we should continue on with this
		if (isType) {
			isType = false;
			
			// now see if its a JP2
			ByteReader tempReader = null;
			File f = new File(filePath);
			tempReader = new ByteReader(f, "r");
			
			// skip past the JPEG2000 Signature box 
			// because we already checked this
			tempReader.skipBytes(Jpeg2000Signature.FIXED_BOX_LENGTH);
			
			// look for the File Type Box
			Box nextBox = readBoxHeader(tempReader);
			if (nextBox.getType() == FileType.TYPE){
				FileType fileTypeBox = new FileType(nextBox);
				
				// read just the brand field
				fileTypeBox.extractMetadata(tempReader, 
						true, // read just the Brand field
						false, // don't add anomalies to the file
						null); // don't need to send in the file
				
				// look at brand
				int brand = fileTypeBox.getBrand();
				if (brand == FileType.BRAND_JP2){
					isType = true;
				}

			}
			
			// clean up
			tempReader.close();
			tempReader = null;
			f = null;
		}
		
		return isType;
	}

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws PackageException
	 * @throws FatalException
	 */
	public static void main(String[] args) throws PackageException, FatalException {
	    
	    String testFile = "/daitss/dev/data/ingest/in/UFE0002242/E20060817_AAAAAB/migrated_20060817155132/F20060817_AAAAAA_JPEG_JP2_1/mig.jp2";
		String packagePath = "/daitss/dev/data/ingest/in/UFE0002242";
		String initDesc = "/daitss/dev/data/ingest/in/UFE0002242/E20060817_AAAAAB/UFE0002242.xml";
			
		if (Jp2.isType(testFile)) {
			System.out.println("Is a Jp2");
			
			AIP sip = new AIP(packagePath);
			DataFileFactory.getInstance(sip);
			
			sip.setMigratedDir(packagePath + "migrateDir/");
			sip.setNormalizedDir(packagePath + "normDir/");
			sip.setLinksDir(packagePath + "link/");
			
			METSDescriptor md = new METSDescriptor(initDesc, sip);
			sip.setInitialDescriptor(md);
			sip.setPackageArchived(true);
			Jp2 dFile = new Jp2(testFile, sip);
			//DataFile dFile = DataFileFactory.getInstance(sip).createDataFile(null,testFile, false, null);
	        //dFile.extractMetadata();
			dFile.setOid("F20060817_AAAAAC");
			System.out.print("Has migrated:" + dFile.hasMigrated());
		} else {
			System.out.println("Is not a Jp2");
		}
	}
	
	/**
	 * The single codestream (image) contained by a JP2 file.
	 */
    public Jp2Image getCodeStream(){
        return (Jp2Image) codeStreams.elementAt(0);
    }
    	
	/**
	 * Construct a JP2 file.
	 * 
	 * @param path the absolute path to an existing readable file
	 * @param ip the Information Package that this file is part of
	 * @throws FatalException
	 */
	public Jp2(String path, InformationPackage ip) throws FatalException {
		super(path, ip);
		
		this.setMediaType(MimeMediaType.MIME_IMG_JP2);
		this.setFileExt(Extension.EXT_JP2);
		
		// only one version so far
		this.setMediaTypeVersion(VERSION_1_0);
		
		this.anomsPossible = null;
		this.anomsPossible = new Jp2Anomalies();
	}

	/**
	 * The constructor to call for an existing JP2 file when metadata about
	 * this file is available.
	 * 
	 * @param path
	 * @param ip
	 * @param _metadata
	 * @throws FatalException
	 */
	public Jp2(String path, InformationPackage ip, METSDocument _metadata)
			throws FatalException {
		this(path, ip);
		// allow for the DataFileFactory to send a null METSDocument in
		if (_metadata != null) {
			this.addXmlMetadata(Descriptor.TYPE_SIP_DEPOSITOR, _metadata);
		}
	}
	
	/**
	 * @param path
	 * @param metadata
	 * @return whether or not this is a JP2
	 * @throws FatalException
	 */
	protected boolean checkIsType(String path, METSDocument metadata)
			throws FatalException {
		return isType(path, metadata);
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
		super.evalMembers();
		
		Iterator bsIter = this.getBitstreams().iterator();
		while (bsIter.hasNext()) {
			Jp2Image ji = (Jp2Image) bsIter.next();
			ji.evalMembers();
		}
	}
	
	/**
	 * Parses itself and sets format-specific members. For roots of distributed
	 * objects, also recognizes links to external files and creates Link
	 * objects and adds them to the links Vector using addLink(Link). 
	 * Note: this must not be called on files other than JP2 files - make sure
	 * isType was called before this to verify that it its a JP2 file. 
	 * @throws FatalException
	 */
	protected void parse() throws FatalException {
		
		// begin collecting technical metadata
		//codeStream = new Jp2Image(this);
        this.codeStreams.add(new Jp2Image(this));
		this.addBitstream(this.getCodeStream());
		
		// skip past the JPEG 2000 signature box
		reader.skipBytes(Jpeg2000Signature.FIXED_BOX_LENGTH);
		
		// parse the File Type Box
		// which is required to come next.
		Box nextBox = readBoxHeader(reader);
		if (nextBox.getType() == FileType.TYPE){
			FileType fileTypeBox = new FileType(nextBox);
			
			// read just the brand field
			fileTypeBox.extractMetadata(reader, 
					false, // don't just read the Brand field
					true, // add anomalies to the file
					this); // send in the file

		} else {
			// error - FileType box must be next
			SevereElement ja = 
				this.getAnomsPossible().
				getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_LOC_BOX);
			this.addAnomaly(ja);
			
			// STOP PARSING
			continueParsing = false;
		}
		
		// the number of codestream boxes is stored because
		// a JP2 can have more than one but a conforming JP2
		// reader ignores all of them except the first.
		int numContigCodestreamBoxes = 0;
		
		// prepare to record if we saw the boxes that
		// are required after the File Type box 
		boolean sawContigCodestreamBox = false;
		boolean sawJp2HeaderBox = false;
		
		// read the rest of the top-level boxes.
		// if any top-level box contains another box
		// those will be read by the outer box.
		while (continueParsing && reader.bytesLeft() > 0){
			
			// see if there are enough bytes left to make a box
			if (reader.bytesLeft() < Box.MIN_BOX_LENGTH) {
				SevereElement ja = 
					this.getAnomsPossible().
					getSevereElement(
							Jpeg2000Anomalies.JPEG2K_EARLY_BOX_END);
				this.addAnomaly(ja);
				
				// STOP PARSING
				continueParsing = false;
				break;
			}
			
			// get the next box
			nextBox = readBoxHeader(reader);
			
			// figure out what type of box it is
			// and then get the box to parse itself,
			// extracting metadata and looking for anomalies
			int type = nextBox.getType();
			
			switch (type){
				// Contiguous Codestream box
				case ContigCodestream_jp2.TYPE: 
					sawContigCodestreamBox = true;
					
					// only parse the first Codestream box
					if (numContigCodestreamBoxes++ == 0){
                        
                        // mark the byte offset location.
                        // this will be recorded as the location
                        // of the bitstream
                        this.getCodeStream().setLocation(
                                Long.toString(nextBox.getFileOffset()));
                        
						ContigCodestream_jp2 codestreamBox = 
							new ContigCodestream_jp2(nextBox);
						
						codestreamBox.extractMetadata(reader, this);
					}
					break;
				// JP2 Header box
				case Jp2Header_jp2.TYPE: 
					sawJp2HeaderBox = true;
					
					if (numContigCodestreamBoxes > 0){
						// saw the Contiguous Codestream box before the
						// JP2 Header box - goes against a JP2 requirement
						SevereElement ja = 
							this.getAnomsPossible().
							getSevereElement(
									Jpeg2000Anomalies.JPEG2K_BAD_LOC_JP2H);
						this.addAnomaly(ja);
						
						// STOP PARSING
						continueParsing = false;
						break;
					}
					
					Jp2Header_jp2 jp2HeaderBox = 
						new Jp2Header_jp2(nextBox);
					// this is a superbox.
					// parse it and the boxes it contains.
					jp2HeaderBox.extractMetadata(reader, this);
					break;
				// IPR 
				case Ipr.TYPE: 
					Ipr iprBox = new Ipr(nextBox);
					iprBox.extractMetadata(reader, this);
					break;
				// XML
				case XML.TYPE: 
					XML xmlBox = new XML(nextBox);
					xmlBox.extractMetadata(reader, this);
					break;
				// UUID
				case Uuid.TYPE: 
					Uuid uuidBox = new Uuid(nextBox);
					uuidBox.extractMetadata(reader, this);
					break;
				// UUIDInfo
				case UuidInfo.TYPE: 
					UuidInfo uuidInfoBox = new UuidInfo(nextBox);
					// this is a superbox.
					// parse it and the boxes it contains.
					uuidInfoBox.extractMetadata(reader, this);
					break;
				default:
					// see if we recognize the type
					if (!BoxType.isValidType(type)){
						// box type not described in spec.
						// this is OK, but note it
						SevereElement ja = 
							this.getAnomsPossible().
							getSevereElement(
								Jpeg2000Anomalies.JPEG2K_UNK_BOXTYPE);
						this.addAnomaly(ja);
					} else {
						// box type is described in spec but 
						// this box shouldn't be at the top-level
						SevereElement ja = 
							this.getAnomsPossible().
							getSevereElement(
								Jpeg2000Anomalies.JPEG2K_BAD_LOC_BOX);
						this.addAnomaly(ja);
					}
					reader.skipBytes((int)nextBox.getContentLength());
			}
			
			if (this.isHopelessFile()){
				continueParsing = false;
			}
		}
		
		// make sure that we saw the boxes required after the 
		// File Type box 
		if (!sawContigCodestreamBox){
			// no Contiguous Codestream box
			SevereElement ja = 
				this.getAnomsPossible().
				getSevereElement(Jp2Anomalies.JP2_NO_JP2CBOX);
			this.addAnomaly(ja);
		}
		if (!sawJp2HeaderBox){
			// no JP2 Header box
			SevereElement ja = 
				this.getAnomsPossible().
				getSevereElement(Jp2Anomalies.JP2_NO_JP2HBOX);
			this.addAnomaly(ja);
		}
		
	}
	
	 /**
	 * Puts all the file's members and their values in a String.
	 * 
	 * @return all the members and their values
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		// add any JP2-specific items here
		return sb.toString();
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
}
