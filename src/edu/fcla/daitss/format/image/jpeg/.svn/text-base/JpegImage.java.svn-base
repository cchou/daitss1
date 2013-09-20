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

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.ColorSpace;
import edu.fcla.daitss.format.image.Image;
import edu.fcla.daitss.format.image.ImageAnomalies;
import edu.fcla.daitss.format.image.Orientation;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.Informer;

/**
 * JPEGImage represents a JPEG-compressed image within some
 * variation of a JPEG file (JFIF, Adobe JPEG, etc.).
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class JpegImage extends Image{
	
	/**
	 * Maximum supported pixel aspect ratio.
	 */
	private static final double MAX_PAR = 3.402823466e+38;
	
	/**
	 * Minimum supported pixel aspect ratio.
	 * Means unknown.
	 */
	private static final double MIN_PAR = 0;

	/**
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
	}
	
	/**
	 * Identifiers of the components in this image.
	 * Contains Integer objects.
	 */
	private Vector componentId = null;
	
	/**
	 * Identifiers of Huffman AC tables contained in this image.
	 * Contains Integer objects.
	 */
	private Vector huffmanAcTableId = null;
	
	/**
	 * Identifiers of Huffman DC tables contained in this image.
	 * Contains Integer objects.
	 */
	private Vector huffmanDcTableId = null;
	
	/**
	 * A fraction of horizontal (x) pixel size divided by (y) vertical pixel size. 
	 * The pixel aspect ratio for square pixels is 1/1 or 1.0. Also known as
	 * Sample Aspect Ration (SAR). 
	 */
	private double pixelAspectRatio = 0;
	
	/**
	 * A code for the JPEG compresssion processes used on this image
	 */
	private String process = JpegProcess.UNKNOWN;
	
	/**
	 * Identifiers of Quantization tables contained in this image.
	 * Contains Integer objects.
	 */
	private Vector quantValTableId = null;
	
	/**
	 * 
	 * @param df the file containing this JPEG bitstream
	 * @throws FatalException
	 */
	public JpegImage(DataFile df) throws FatalException {
		super(df);
		
		this.setBsImageTable(ArchiveDatabase.TABLE_BS_IMAGE_JPEG);
		this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		compression.setCompression(Compression.COMP_JPEG);
		//	pixel aspect ratio - assume square pixels unless see otherwise
		this.setPixelAspectRatio(1.0);
		this.setOrientation(Orientation.TOP_LEFT);
		
		// build data members
		this.componentId = new Vector();
		this.huffmanAcTableId = new Vector();
		this.huffmanDcTableId = new Vector();
		this.quantValTableId = new Vector();
	}
	
	/**
	 * Adds an identifier for a component to a collection
	 * of all component identifiers used in this image
	 * 
	 * @param ID an identifier for a component in this image
	 */
	public void addComponentID(int ID){
		this.componentId.add(new Integer(ID));
	}
	
	/**
	 * Adds an identifier for a Huffman AC Table to a collection
	 * of all Huffman AC Table identifiers used in this image if
	 * its ID is unique.
	 * 
	 * @param ID an identifier for a Huffman AC Table used in this image
	 */
	public void addHuffmanAcTableId(int ID){
		if (!this.huffmanAcTableId.contains(new Integer(ID))){
			this.huffmanAcTableId.add(new Integer(ID));
		}
	}
	
	/**
	 * Adds an identifier for a Huffman DC Table  to a collection
	 * of all Huffman DC Table identifiers used in this image if its ID
	 * is unique.
	 * 
	 * @param ID an identifier for a Huffman DC Table used in this image
	 */
	public void addHuffmanDcTableId(int ID){
		if (!this.huffmanDcTableId.contains(new Integer(ID))){
			this.huffmanDcTableId.add(new Integer(ID));
		}
	}
	
	/**
	 * Adds an identifier for a Quantization Table  to a collection
	 * of all Quantization Table identifiers used in this image if the
	 * ID is unique.
	 * 
	 * @param ID an identifier for a Quantization Table used in this image
	 */
	public void addQuantValTableID(int ID){
		if (!this.quantValTableId.contains(new Integer(ID))){
			this.quantValTableId.add(new Integer(ID));
		}
	}
	
	/**
	 * 
	 * @throws FatalException
	 */
	private void calcHasIccProfile() throws FatalException {
		// TODO: can JPEGs ever have an ICC profile?
		this.setHasIccProfile(Bitstream.FALSE);
	}

	/**
	 * Determines whether or not this image has a palette stored in it.
	 * If the table had been encountered when parsing the image,
	 * then <code>hasIntPalette</code> would be TRUE. If this is
	 * not the case then this value should be FALSE.
	 * 
	 * @throws FatalException
	 */
	private void calcHasIntPalette() throws FatalException {
		// maybe i just see if the hasIntPalette has not been set to true
		// or maybe check the color space
		String cs = this.getColorSpace();
		if (cs.equals(ColorSpace.CS_YCBCR) || cs.equals(ColorSpace.CS_Y)){
			this.setHasIntPalette(Bitstream.FALSE);
		}
	}
	
	/**
	 * fill in the database column-value pairs for this image
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();

		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG_JPEG_PROCESS);
		values.add(this.getProcess());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG_PIXEL_ASPECT_RATIO);
		values.add(new Double(this.getPixelAspectRatio()));
	}
		
	/**
	 * Insert this image into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_IMAGE_JPEG, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this image
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		super.dbUpdate();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BS_IMAGE_JPEG_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_IMAGE_JPEG, columns, colValues, whereClause);
	}
	

	public Document toXML() throws FatalException {
	    /* Daitss Document */
	    Document doc = super.toXML();
	    
	    /* Daitss Element */
	    Element rootElement = doc.getDocumentElement();
	    
	    /* Daitss Namespace */
	    String namespace = rootElement.getNamespaceURI();
	    
	    /* Bs Image Jpeg */
	    Element bsImageJpegElement = doc.createElementNS(namespace, "BS_IMAGE_JPEG");
	    rootElement.appendChild(bsImageJpegElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsImageJpegElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsImageJpegElement.appendChild(bsidElement);

	    /* Jpeg Process */
	    Element jpegProcessElement = doc.createElementNS(namespace, "JPEG_PROCESS");
	    String jpegProcessValue = (this.getProcess() != null ? this.getProcess() : "" );
	    Text jpegProcessText = doc.createTextNode(jpegProcessValue);
	    jpegProcessElement.appendChild(jpegProcessText);
	    bsImageJpegElement.appendChild(jpegProcessElement);

	    /* Pixel Aspect Ratio */
		Element pAspectRatioElement = doc.createElementNS(namespace, "PIXEL_ASPECT_RATIO");
        String pAspectRatioValue = Double.toString(this.getPixelAspectRatio());
        Text pAspectRatioText = doc.createTextNode(pAspectRatioValue);
        pAspectRatioElement.appendChild(pAspectRatioText);
        bsImageJpegElement.appendChild(pAspectRatioElement);
	    
	    return doc;
	}
	
	/**
	 * 
	 * @throws FatalException
	 */
	public void evalMembers() throws FatalException {
	    //System.out.println("In JpegImage's evalMembers");
	
		this.calcHasIccProfile();
		this.calcHasIntPalette();
	}

	/**
	 * @return all component identifiers used in this image
	 */
	public Vector getComponentId() {
		return this.componentId;
	}

	/**
	 * @return all Huffman AC Table identifiers used in this image
	 */
	public Vector getHuffmanAcTableId() {
		return this.huffmanAcTableId;
	}

	/**
	 * @return all Huffman DC Table identifiers used in this image
	 */
	public Vector getHuffmanDcTableId() {
		return this.huffmanDcTableId;
	}
	
	/**
	 * @return the pixel aspect ratio
	 */
	public double getPixelAspectRatio() {
		return this.pixelAspectRatio;
	}

	/**
	 * @return the JPEG compression process used on this image
	 */
	public String getProcess() {
		return this.process;
	}

	/**
	 * @return all Quantization Table identifiers used in this image
	 */
	public Vector getQuantValTableId() {
		return this.quantValTableId;
	}
	
	/**
	 * @param _pixelAspectRatio the pixel aspect ratio
	 * @throws FatalException
	 */
	public void setPixelAspectRatio(double _pixelAspectRatio) 
		throws FatalException {
			String methodName = "setPixelAspectRatio(double)";
			
		if (_pixelAspectRatio < MIN_PAR) {
			Informer.getInstance().fail(
				this, methodName,
				"Illegal argument", "_pixelAspectRatio: " + _pixelAspectRatio,
				new FatalException("Not a valid pixel aspect ratio"));
		}
		if (_pixelAspectRatio > MAX_PAR){
			// we can't support it's large pixel aspect ratio. note the anomaly, log
			// a warning and reduce the ratio to the max recordable
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(ImageAnomalies.IMG_OVRLMT_PIXEL_ASPECT_RATIO);
			this.getDf().addAnomaly(ta);
			Informer.getInstance().warning(
				this,
				methodName,
				"Overlimit: Reducing pixel aspect ratio (too large)",
				"file: " + this.getDf().getPath());
			_pixelAspectRatio = MAX_PAR;
		}
		this.pixelAspectRatio = _pixelAspectRatio;
	}

	/**
	 * Sets the JPEG compression process used on this image.
	 * 
	 * @param _process the JPEG compression process used on this image
	 * @throws FatalException
	 */
	public void setProcess(String _process) throws FatalException {
		if (JpegProcess.isValidProcess(_process)){
			this.process = _process;
		} else {
			Informer.getInstance().fail(
				this, "setProcess(String)",
				"Illegal argument", "_process: " + _process,
				new FatalException("Not a valid JPEG process"));
		}
	}

	/**
	 * @return	a String composed of all the members in this class
	 */
	public String toString(){
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		Iterator iter = null;

		sb.append("\tComponent IDs: ");
		iter = this.getComponentId().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next() + " ");
		}
		sb.append("\n");	
		sb.append("\tHuffman AC Table IDs: ");
		iter = this.getHuffmanAcTableId().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next() + " ");
		}
		sb.append("\n");		
		sb.append("\tHuffman DC Table IDs: ");
		iter = this.getHuffmanDcTableId().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next() + " ");
		}
		sb.append("\n");
		sb.append("\tJPEGProcess: " + this.getProcess() + "\n");
		sb.append("\tPixel Aspect Ratio: " + getPixelAspectRatio() + "\n");
		sb.append("\tQuantization Table IDs: ");
		iter = this.getQuantValTableId().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next() + " ");
		}
		sb.append("\n");
		
		return sb.toString();
	}

}
