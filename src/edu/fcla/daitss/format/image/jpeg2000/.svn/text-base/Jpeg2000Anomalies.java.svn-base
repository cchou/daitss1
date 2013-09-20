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
 * Created on Aug 3, 2004
 *
 */
package edu.fcla.daitss.format.image.jpeg2000;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.image.ImageAnomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * JPEG2000Anomalies represent all the anomalies that can be found
 * in a JPEG2000 file (including all variations).
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 *
 * @author Andrea Goethals, FCLA
 */
public class Jpeg2000Anomalies extends ImageAnomalies {
    
    /**
     * malformed bits per component box
     */
    public static final String JPEG2K_BAD_BPCBOX =
        "A_JPEG2K_BAD_BPCBOX";
	
	/**
     * malformed channel definition box
     */
    public static final String JPEG2K_BAD_CDEFBOX =
        "A_JPEG2K_BAD_CDEFBOX";
	
    /**
     * malformed component mapping box
     */
    public static final String JPEG2K_BAD_CMAPBOX =
        "A_JPEG2K_BAD_CMAPBOX";
    
	/**
	 * malformed file type box
	 */
	public static final String JPEG2K_BAD_FTYPBOX =
		"A_JPEG2K_BAD_FTYPBOX";
    
    /**
	 * malformed image header box
	 */
	public static final String JPEG2K_BAD_IHDRBOX =
		"A_JPEG2K_BAD_IHDRBOX";
    
    /**
	 * malformed codestream
	 */
	public static final String JPEG2K_BAD_JP2CBOX =
		"A_JPEG2K_BAD_JP2CBOX";
	
	/**
     * malformed JP2 header box
     */
    public static final String JPEG2K_BAD_JP2HBOX =
        "A_JPEG2K_BAD_JP2HBOX";
    
    /**
	 * invalid box length value
	 */
	public static final String JPEG2K_BAD_LBOX = 
		"A_JPEG2K_BAD_LBOX";
    
    /**
	 * A box is not in the correct location within 
     * the file.
	 */
	public static final String JPEG2K_BAD_LOC_BOX = 
		"A_JPEG2K_BAD_LOC_BOX";
	
	/**
	 * JP2 Header box is not in the correct location 
     * within the file.
	 */
	public static final String JPEG2K_BAD_LOC_JP2H = 
		"A_JPEG2K_BAD_LOC_JP2H";
	
	/**
     * malformed palette box
     */
    public static final String JPEG2K_BAD_PCLRBOX =
        "A_JPEG2K_BAD_PCLRBOX";
	
	/**
     * malformed resolution box
     */
    public static final String JPEG2K_BAD_RES0BOX =
        "A_JPEG2K_BAD_RES0BOX";
    
    /**
	 * invalid extended box length value
	 */
	public static final String JPEG2K_BAD_XLBOX = 
		"A_JPEG2K_BAD_XLBOX";
	
	/**
	 * end of box reached before specified box length reached;
	 * or box length smaller than the minimum length
	 */
	public static final String JPEG2K_EARLY_BOX_END = 
		"A_JPEG2K_EARLY_BOX_END";

	/**
     * no bits per component box
     */
    public static final String JPEG2K_NO_BPCBOX = 
        "A_JPEG2K_NO_BPCBOX";
	
	/**
     * no component mapping box
     */
    public static final String JPEG2K_NO_CMAPBOX = 
        "A_JPEG2K_NO_CMAPBOX";
    
    /**
	 * no color specification box
	 */
	public static final String JPEG2K_NO_COLRBOX = 
	    "A_JPEG2K_NO_COLRBOX";
    
    /**
     * over the data size limit for the field 
     * BS_IMAGE_JPEG2000.PROG_ORDER
     */
    public static final String JPEG2K_OVRLMT_PROG_ORDER = 
        "A_JPEG2K_OVRLMT_PROG_ORDER";
    
    /**
     * over the data size limit for the field 
     * BS_IMAGE_JPEG2000.WAVELET_TRANF_TYPE
     */
    public static final String JPEG2K_OVRLMT_WAVELET_TRANF_TYPE = 
        "A_JPEG2K_OVRLMT_WAVELET_TRANF_TYPE";
	
	/**
	 * unrecognized (unknown) box type.
	 */
	public static final String JPEG2K_UNK_BOXTYPE = 
		"A_JPEG2K_UNK_BOXTYPE";
	
	/**
	 * Unknown color specification method (METH field) in the
	 * Colour Specification box.
	 */
	public static final String JPEG2K_UNK_CS_METH =
		"A_JPEG2K_UNK_CS_METH";
	
	/**
	 * Unknown multiple component transformation in the
	 * COD box.
	 */
	public static final String JPEG2K_UNK_MCT =
		"A_JPEG2K_UNK_MCT";
	
	/**
	 * unrecognized (unknown) File Type box, MinV field value.
	 */
	public static final String JPEG2K_UNK_MIN_VERSION = 
		"A_JPEG2K_UNK_MIN_VERSION";
	
	/**
	 * Unrecognized progression order.
	 */
	public static final String JPEG2K_UNK_PROG_ORDER =
	    "A_JPEG2K_UNK_PROG_ORDER";
	
	/**
	 * unrecognized (unknown) box type value
	 */
	public static final String JPEG2K_UNK_TBOX = 
		"A_JPEG2K_UNK_TBOX";	
	
	/**
	 * unrecognized (unknown) Image Header box, UnkC field value
	 */
	public static final String JPEG2K_UNK_UNKC = 
		"A_JPEG2K_UNK_UNKC";

	/**
	 * Test driver
	 * 
	 * @param args	not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		//Jpeg2000Anomalies ja = new Jpeg2000Anomalies();
	}

	/**
	 * Builds the list of known JPEG 2000-specific anomalies
	 * 
	 * @throws FatalException
	 */
	public Jpeg2000Anomalies() throws FatalException {
		super();
		buildAnoms();
	}
	
	/**
	 * Builds the list of known JPEG 2000-specific anomalies. 
	 * 
	 * @throws FatalException
	 */
	private void buildAnoms() throws FatalException {
        // malformed bits per component box
        insert(JPEG2K_BAD_BPCBOX, Severity.SEVERITY_NOTE,
                "Bits Per Component box not formatted correctly");
        // malformed channel definition box
        insert(JPEG2K_BAD_CDEFBOX,Severity.SEVERITY_NOTE,
                "Channel definition box not formatted correctly");
        // malformed component mapping box
        insert(JPEG2K_BAD_CMAPBOX, Severity.SEVERITY_NOTE,
                "Component mapping box not formatted correctly");
		// malformed file type box
		insert(JPEG2K_BAD_FTYPBOX, Severity.SEVERITY_NOTE,
			"End of file type box reached prematurely");
		// malformed image header box
		insert(JPEG2K_BAD_IHDRBOX, Severity.SEVERITY_NOTE,
				"Image header box not formatted correctly");
		// malformed codestream
		insert(JPEG2K_BAD_JP2CBOX, Severity.SEVERITY_NOTE,
				"Codestream box not formatted correctly");
        // malformed JP2 header box
        insert(JPEG2K_BAD_JP2HBOX, Severity.SEVERITY_NOTE,
                "JP2 header box not formatted correctly");
		// invalid box length value
		insert(JPEG2K_BAD_LBOX, Severity.SEVERITY_NOTE,
			"Invalid box length value");
		// A box is not in the correct location within the file.
		insert(JPEG2K_BAD_LOC_BOX, Severity.SEVERITY_NOTE,
			"Incorrect location of a box.");
		// JP2 Header box is not in the correct location within the file.
		insert(JPEG2K_BAD_LOC_JP2H, Severity.SEVERITY_NOTE,
			"Incorrect location of JP2 Header box.");
        // Malformed palette box
        insert(JPEG2K_BAD_PCLRBOX, Severity.SEVERITY_NOTE,
            "Palette box not formatted correctly.");
        // Malformed resolution box
        insert(JPEG2K_BAD_RES0BOX, Severity.SEVERITY_NOTE,
            "Resolution box not formatted correctly.");
		// invalid extended box length value
		insert(JPEG2K_BAD_XLBOX, Severity.SEVERITY_NOTE,
			"Invalid extended box length value");
		// end of box reached before specified box length reached
		insert(JPEG2K_EARLY_BOX_END, Severity.SEVERITY_NOTE,
			"End of box reached prematurely");
        // no bits per component box
        insert(JPEG2K_NO_BPCBOX,  Severity.SEVERITY_NOTE,
            "No bits per component info");
        // no component mapping box
        insert(JPEG2K_NO_CMAPBOX, Severity.SEVERITY_NOTE,
            "No component mapping info");
		// no color specification box
		insert(JPEG2K_NO_COLRBOX, Severity.SEVERITY_NOTE,
			"No color specification info");
        // over the data size limit for the field 
        // BS_IMAGE_JPEG2000.PROG_ORDER
        insert(JPEG2K_OVRLMT_PROG_ORDER,  Severity.SEVERITY_NOTE,
            "Exceeds data size limit for PROG_ORDER");
        // over the data size limit for the field 
        // BS_IMAGE_JPEG2000.WAVELET_TRANF_TYPE
        insert(JPEG2K_OVRLMT_WAVELET_TRANF_TYPE, Severity.SEVERITY_NOTE,
            "Exceeds data size limit for WAVELET_TRANF_TYPE");
		// unrecognized (unknown) box type. spec says it's OK - just note it
		insert(JPEG2K_UNK_BOXTYPE, Severity.SEVERITY_NOTE,
			"Unrecognized box type");
		// Unknown color specification method (METH field) in the
		// Colour Specification box.
		insert(JPEG2K_UNK_CS_METH, Severity.SEVERITY_NOTE,
			"Unrecognized color specification method");
		// Unknown multiple component transformation in the
		// COD box.
		insert(JPEG2K_UNK_MCT, Severity.SEVERITY_NOTE,
				"Unrecognized multiple component transformation");
		// Unrecognized progression order.
		insert(JPEG2K_UNK_PROG_ORDER, Severity.SEVERITY_NOTE,
				"Unrecognized progression order");
		// unrecognized (unknown) File Type box, MinV field value.
		insert(JPEG2K_UNK_MIN_VERSION, Severity.SEVERITY_NOTE,
			"Unrecognized minor version");
		// unrecognized (unknown) box type value
		insert(JPEG2K_UNK_TBOX, Severity.SEVERITY_NOTE,
			"Unrecognized box type");
		// unrecognized (unknown) Image Header box, UnkC field value
		insert(JPEG2K_UNK_UNKC, Severity.SEVERITY_NOTE,
			"Unrecognized color unknown value");
	}

}
