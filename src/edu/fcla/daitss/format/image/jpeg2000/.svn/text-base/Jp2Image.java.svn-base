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
 * Created on Aug 17, 2004
 *
 */
package edu.fcla.daitss.format.image.jpeg2000;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.BitstreamRole;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;

/**
 * Jp2Image represents a JPEG 2000 image as specified in 
 * ISO/IEC 15444-1:2000(E) (the same specification that 
 * describes JP2).
 */
public class Jp2Image extends Jpeg2000Image {
    
    /**
     * Component transformation used on components 0, 1, 2
     * for coding efficiency. Irreversible component transformation
     * used with the 9-7 irreversible filter. Reversible
     * transformation used with the 5-3 reversible filter.
     */
    public static final String MCT_COMP012 = "COMP012";
    
    /**
     * No multiple component transformation specified.
     */
    public static final String MCT_NONE = "NONE";
    
    /**
     * Progression order of
     * Component-Position-Resolution level-layer progression.
     */
    public static final String PROG_ORDER_CPRL = "CPRL";
    
    /**
     * Progression order of
     * Layer-resolution level-component-position progression.
     */
    public static final String PROG_ORDER_LRCP = "LRCP";
    
    /**
     * Progression order of
     * Position-component-Resolution level-layer progression.
     */
    public static final String PROG_ORDER_PCRL = "PCRL";
    
    /**
     * Progression order of
     * Resolution level-layer-component-position progression.
     */
    public static final String PROG_ORDER_RLCP = "RLCP";
    
    /**
     * Progression order of
     * Resolution level-position-component-layer progression.
     */
    public static final String PROG_ORDER_RPCL = "RPCL";
    
    /**
     * No quantization.
     */
    public static final String QUANT_NONE = "NONE";
    
    /**
     * Scalar derived quantization (values signalled for NlLL
     * subband only). Uses Equation E.5 of ISO/IEC 15444-1:2000(E).
     */
    public static final String QUANT_SCALAR_DER = "SCALAR_DER";
    
    /**
     * Scalar expounded quantization (values signalled for each
     * subband). There are as many step sizes signalled as
     * there are subbabnds.
     */
    public static final String QUANT_SCALAR_EXP = "SCALAR_EXP";

    /**
	 * @param df
	 * @throws FatalException
	 */
	public Jp2Image(DataFile df) throws FatalException {
		super(df);
		
		// JP2 can only have 1 value
		this.multCompTrans = new String[1];
        
        // only one image in a JP2
        this.setRole(BitstreamRole.IMG_MAIN);
        
    	this.setBsImageTable(ArchiveDatabase.TABLE_BS_IMAGE_JPEG2000);     
	}
	
	/**
	 * Determines whether or not this image has a palette stored in it.
	 * If the palette box had been encountered when parsing the image,
	 * then <code>hasIntPalette</code> would be TRUE. If this is
	 * not the case then this value should be FALSE.
	 * 
	 * @throws FatalException
	 */
	private void calcHasIntPalette() throws FatalException {
		if (!this.hasIntPalette().equals(Bitstream.TRUE)){
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

		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_HAS_ROI);
		values.add(this.hasRegionOfInterest());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_PROG_ORDER);
		values.add(this.getProgOrder().toString());
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_NUM_TILES);
		values.add(new Long(this.getNumTiles()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_TILE_LENGTH);
		values.add(new Long(this.getTileHeight()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_TILE_WIDTH);
		values.add(new Integer(this.getTileWidth()));
		columns.add(ArchiveDatabase.COL_BS_IMAGE_JPEG2000_WAVELET_TRANF_TYPE);
		values.add(this.getTransformType().toString());
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
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_IMAGE_JPEG2000, columns, colValues);
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
				ArchiveDatabase.COL_BS_IMAGE_JPEG2000_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_IMAGE_JPEG2000, columns, colValues, whereClause);
	}
	
	public Document toXML() throws FatalException {

        // Document.
        Document doc = super.toXML();

        // Root element.
        Element rootElement = doc.getDocumentElement();

        // Namespace. 
        String namespace = rootElement.getNamespaceURI();
        
	    // Bs Image Jpeg 2000
	    Element bsImageJ2kElement = doc.createElementNS(namespace, "BS_IMAGE_JPEG2000");
	    rootElement.appendChild(bsImageJ2kElement);

	    // Dfid
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsImageJ2kElement.appendChild(dfidElement);

	    // Bsid
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsImageJ2kElement.appendChild(bsidElement);
	    
	    // HAS_ROID
	    Element hasROIElement = doc.createElementNS(namespace, "HAS_ROI");
	    String hasROIValue = (this.hasRegionOfInterest() != null ? this.hasRegionOfInterest() : "" );
	    hasROIElement.appendChild(doc.createTextNode(hasROIValue));
	    bsImageJ2kElement.appendChild(hasROIElement);

	    // PROG_ORDER
	    Element hasProgOrderElement = doc.createElementNS(namespace, "PROG_ORDER");
	    String hasProgOrderValue = this.getProgOrder().toString();
	    hasProgOrderElement.appendChild( doc.createTextNode(hasProgOrderValue));
	    bsImageJ2kElement.appendChild(hasProgOrderElement);
	    
	    // NUM_TILES
	    Element hasNumTilesElement = doc.createElementNS(namespace, "NUM_TILES");
	    Text hasNumTilesValue = doc.createTextNode(Long.toString(this.getNumTiles()));
	    hasNumTilesElement.appendChild( hasNumTilesValue);
	    bsImageJ2kElement.appendChild(hasNumTilesElement);
	    
	    // TILE_LENGTH
	    Element hasTileLengthElement = doc.createElementNS(namespace, "TILE_LENGTH");
	    Text hasTileLengthValue = doc.createTextNode(Long.toString(this.getTileHeight()));
	    hasTileLengthElement.appendChild( hasTileLengthValue);
	    bsImageJ2kElement.appendChild(hasTileLengthElement);
	    
	    // TILE_WIDTH
	    Element hasTileWidthElement = doc.createElementNS(namespace, "TILE_WIDTH");
	    Text hasTileWidthValue = doc.createTextNode(Long.toString(this.getTileHeight()));
	    hasTileWidthElement.appendChild( hasTileWidthValue);
	    bsImageJ2kElement.appendChild(hasTileWidthElement);
	    
	    // WAVELET_TRANF_TYPE
	    Element hasTranfTypeElement = doc.createElementNS(namespace, "WAVELET_TRANF_TYPE");
	    String hasTranfTypeValue = this.getTransformType().toString();
	    hasTranfTypeElement.appendChild( doc.createTextNode(hasTranfTypeValue));
	    bsImageJ2kElement.appendChild(hasTranfTypeElement);
	    
        return doc;
	}
	
	/**
	 * Evaluates its members to set other members
	 * based on those values.
	 * 
	 * @throws FatalException
	 */
	public void evalMembers() throws FatalException {	    
		//this.calcHasIccProfile();
        
        // think this is already being set but
		this.calcHasIntPalette();
        
        // if we didn't see the RGN marker than this
        // will still be set to UNKNOWN
        if (this.hasRegionOfInterest().equals(UNKNOWN)){
            this.setHasRegionOfInterest(FALSE);
        }
	}
    
    /**
	 * Puts all the JP2 image's members and their values in a String.
	 * 
	 * @return all the members and their values
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		// append any JP2-specific things here
		
		
		return sb.toString();
	}
}
