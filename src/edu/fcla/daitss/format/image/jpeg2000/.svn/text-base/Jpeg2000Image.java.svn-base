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

import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.Image;
import edu.fcla.daitss.format.image.ImageAnomalies;
import edu.fcla.daitss.format.image.Orientation;
import edu.fcla.daitss.severe.element.SevereElement;

/**
 * The codestream of a JPEG 2000 file.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class Jpeg2000Image extends Image {

    /**
     * Maximium length of the progression order after
     * its converted to a comma-delimited String. So that
     * it will fit in the database.
     */
    public static final int MAX_PROG_ORDER_LENGTH=255;
    
    /**
     * Maximium length of the transformation type after
     * its converted to a comma-delimited String. So that
     * it will fit in the database.
     */
    public static final int MAX_TRANSFORM_TYPE_LENGTH=255;

    /**
     * A particular filter pair used in the wavelet transformation.
     * This reversible pair has 5 taps in the low-pass and 3 taps in
     * the high-pass. (lossless)
     */
    public static final String TRANS_5_3 = "5-3_REVERSIBLE";

    /**
     * A particular filter pair used in the wavelet transformation.
     * This irreversible pair has 9 taps in the low-pass and 7 taps in
     * the high-pass. (lossy)
     */
    public static final String TRANS_9_7 = "9-7_IRREVERSIBLE";
    
    /**
     * Code-block height
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private int codeBlkHeight = -1;
    
    /**
     * Whether or not the image has the code-block style of
     * predictable termination.
     * (see table A-19 of ISO/IEC 15444-1:2000(E)).
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private String codeBlkPredTerm = UNKNOWN;
    
    /**
     * Whether or not the image has the code-block style of
     * reset context probabilities on coding pass boundaries.
     * (see table A-19 of ISO/IEC 15444-1:2000(E)).
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private String codeBlkResetProbs = UNKNOWN;
    
    /**
     * Whether or not the image has the code-block style of
     * segmentation symbols are used.
     * (see table A-19 of ISO/IEC 15444-1:2000(E)).
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private String codeBlkSegSyms = UNKNOWN;
    
    /**
     * Whether or not the image has the code-block style of
     * selective arithmethic coding bypass.
     * (see table A-19 of ISO/IEC 15444-1:2000(E)).
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private String codeBlkSelArith = UNKNOWN;
    
    /**
     * Whether or not the image has the code-block style of
     * termination on each coding pass.
     * (see table A-19 of ISO/IEC 15444-1:2000(E)).
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private String codeBlkTerm = UNKNOWN;
    
    /**
     * Whether or not the image has the code-block style of
     * vertically causal context.
     * (see table A-19 of ISO/IEC 15444-1:2000(E)).
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private String codeBlkVertCC = UNKNOWN;
    
    /**
     * Code-block width
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private int codeBlkWidth = -1;
    
    /**
     * Color specification method.
     * Taken from the Color Specification box.
     */
    private String colorSpecMethod = UNKNOWN;
    
    /**
     * Specifies if the actual color space of the image data
     * is unknown.
     */
    private String colorUnknown = UNKNOWN;
    
    /**
     * The horizontal separation of a sample of ith component
     * with respect to the reference grid. There is one separation
     * value for each image component.
     * Contains Integer objects.
     */
    protected Vector compHorSep = null;
    
    /**
     * The vertical separation of a sample of ith component
     * with respect to the reference grid. There is one separation
     * value for each image component.
     * Contains Integer objects.
     */
    protected Vector compVertSep = null;
    
    /**
     * Stores the precinct sizes found in the COD marker
     * segment of the codestream main header. Used when the
     * Scod parameter's LSB is set to indicate that the default
     * precinct sizes are defined in the SPcod marker
     * segment (See tables A-13 and A-15 of ISO/IEC 15444-1:2000(E)).
     */
    protected Vector defPrecinctSizes = null;
    
    /**
     * Indicates what is found in the LSB of the Scod parameter
     * of the COD marker segment. When this is true, by default
     * the codestream precincts sizes are PPx=15 and
     * PPy = 15. (see table A-13 of ISO/IEC 15444-1:2000(E)).
     */
    private String has15x15DefPrecSizes = UNKNOWN;
    
    /**
     * Whether or not it has a Region of Interest.
     * Determined by the presence of the RGN marker
     * in the Contiguous Codestream box.
     */
    private String hasRegionOfInterest = UNKNOWN;
    
    /**
     * Whether or not the image's components are signed values.
     */
    private String hasSignedComps = UNKNOWN;
    
    /**
     * Horizontal offset from the origin of the reference grid to
     * the left side of the image area
     */
    private int imageHorOffset = -1;
    
    /**
     * Vertical offset from the origin of the reference grid to
     * the top side of the image area
     */
    private int imageVertOffset = -1;
    
    /**
     * Multiple component transformation.
     * Determined by the least significant byte of the SGcod
     * parameter of the COD marker segment. 
     * (see table A-17 of ISO/IEC 15444-1:2000(E)).
     * This is an array of length 1 because JpxImage, a
     * subclass of this class can have multiple values
     * for this member.
     */
    protected String[] multCompTrans = null;
    
    /**
     * Indicates whether or not an EPH marker segment can be used.
     * Determined by bit 2 of the
     * Scod parameter in the COD marker segment. 
     * (see table A-13 of ISO/IEC 15444-1:2000(E)).
     */
    private boolean noEPH = false;
    
    /**
     * Indicates whether or not an SOP marker segment can be used.
     * Determined by bit 1 of the
     * Scod parameter in the COD marker segment. 
     * (see table A-13 of ISO/IEC 15444-1:2000(E)). 
     */
    private boolean noSOP = false;
    
    /**
     * Number of decomposition levels
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private int numDecLevs = -1;
    
    /**
     * Number of guard bits.
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private int numGuardBits = -1;
    
    /**
     * Number of layers
     * TODO: this should be a Vector - 
     * tiles and tile-parts can change this value
     */
    private int numLayers = -1;
    
    /**
     * Number of tiles.
     */
    private int numTiles = 0;
    
    /**
     * Progression order (see Table A-16 in 15444-1)
     * 
     */
    private Vector progOrder = null;
    
    /**
     * Default quantization style.
     */
    private String quantStyle = "";
    
    /**
     * Reference grid height
     */
    private int refGridHeight = -1;
    
    /**
     * Reference grid width
     */
    private int refGridWidth = -1;
    
    /**
     * Height of one reference tile with respect to the 
     * reference grid.
     */
    private int tileHeight = 0;
    
    /**
     * Horizontal offset from the origin of the reference grid
     * to the left side of the first tile.
     */
    private int tileHorOffset = -1;
    
    /**
     * Vertical offset from the origin of the reference grid
     * to the top side of the first tile.
     */
    private int tileVertOffset = -1;
    
    /**
     * Width of one reference tile with respect to the 
     * reference grid.
     */
    private int tileWidth = 0;

    /**
     * Transformation.
     * (see table A-20 of ISO/IEC 15444-1:2000(E)). 
     */
    private Vector transformType = null;
    
    /**
     * 
     * @param df
     * @throws FatalException
     */
    public Jpeg2000Image(DataFile df) throws FatalException {
        super(df);
        
		this.setBsImageTable(ArchiveDatabase.TABLE_BS_IMAGE_JPEG2000);
		
		// default to TOP_LEFT, see p57 ISO?IEC 15441-1:2000(E) Annex B
		this.setOrientation(Orientation.TOP_LEFT);
		
        // this.setResUnit();
        this.compHorSep = new Vector();
        this.compVertSep = new Vector();
        this.defPrecinctSizes = new Vector();
        
        // wavelet transformation type
        this.transformType = new Vector();
        transformType.add("UNKNOWN");
        
        // progression order
        this.progOrder = new Vector();
        progOrder.add("UNKNOWN");
        
        // location will be to the first byte
        // of the codestream data.
        // for a JP2 this will be to the first
        // byte of the Contiguous Codestream box.
        // TODO: for JPX this may need to be to
        // the fragment table or ...
        this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
        
        // orientation
        // can't tell the display resolution of a JPEG2000 image.
        // leave set to unknown
        
        // resolution
        // there is a default display resolution box
        // which has fields that can be used to calculate
        // the resolution in samples per meter
        this.setResUnit(Image.RES_UNIT_METER);
        // leave the XResolution and YResolution set
        // to 0.0 (unknown) unless we see the default
        // display resolution box.
    }

    /**
     * Add a wavelet transformation type to the list
     * of ones used in this image.
     * 
     * @param type
     * @throws FatalException
     */
    public void addWaveletTransformType(String type) throws FatalException {
        if (transformType.size() == 1 && 
                transformType.elementAt(0).equals(UNKNOWN)){
            transformType.remove(0);
        }
        
        // add it if its unique
        if (!transformType.contains(type)){
            transformType.add(type);
        }
        
        // check that the length of this list will not be too long for the database
        // when it is converted to a comma-delimited list
        int numBits = 0;
        Iterator iter = transformType.iterator(); // we know it's non-null...
        boolean tooLong = false;
        while (!tooLong && iter.hasNext()) {
            String bits = (String) iter.next();
            //  add the number of characters in the bits for this component
            numBits += bits.length(); 
            if (iter.hasNext()) {
                numBits++; // add one for the comma
            }
            if (numBits > MAX_TRANSFORM_TYPE_LENGTH){
                // exceeds the database field for types - note this
                tooLong = true;
                SevereElement ta = 
                this.getDf().getAnomsPossible().getSevereElement(
                        Jpeg2000Anomalies.JPEG2K_OVRLMT_WAVELET_TRANF_TYPE);
                this.getDf().addAnomaly(ta);
            }
        }
        
    }
    
    /**
     * Add a progression order to the list
     * of ones used in this image.
     * 
     * @param order
     * @throws FatalException
     */
    public void addProgressionOrder(String order) throws FatalException {
        if (progOrder.size() == 1 && 
                progOrder.elementAt(0).equals(UNKNOWN)){
            progOrder.remove(0);
        }
        
        // add it if its unique
        if (!progOrder.contains(order)){
            progOrder.add(order);
        }
        
        // check that the length of this list will not be too long for the database
        // when it is converted to a comma-delimited list
        int numBits = 0;
        Iterator iter = progOrder.iterator(); // we know it's non-null...
        boolean tooLong = false;
        while (!tooLong && iter.hasNext()) {
            String bits = (String) iter.next();
            //  add the number of characters in the bits for this component
            numBits += bits.length(); 
            if (iter.hasNext()) {
                numBits++; // add one for the comma
            }
            if (numBits > MAX_PROG_ORDER_LENGTH){
                // exceeds the database field for progression order - note this
                tooLong = true;
                SevereElement ta = 
                this.getDf().getAnomsPossible().getSevereElement(
                        Jpeg2000Anomalies.JPEG2K_OVRLMT_PROG_ORDER);
                this.getDf().addAnomaly(ta);
            }
        }
        
    }

    /**
     * @return Returns the codeBlockHeight.
     */
    public int getCodeBlkHeight() {
        return codeBlkHeight;
    }

    /**
     * @return Returns the codeBlkPredTerm.
     */
    public String getCodeBlkPredTerm() {
       return codeBlkPredTerm;
    }

    /**
     * @return Returns the codeBlkResetContProbs.
     */
    public String getCodeBlkResetProbs() {
        return codeBlkResetProbs;
    }

    /**
     * @return Returns the codeBlkSegSyms.
     */
    public String getCodeBlkSegSyms() {
        return codeBlkSegSyms;
    }

    /**
     * @return Returns the codeBlkArithCodBypass.
     */
    public String getCodeBlkSelArith() {
        return codeBlkSelArith;
    }

    /**
     * @return Returns the codeBlkTermOnPass.
     */
    public String getCodeBlkTerm() {
        return codeBlkTerm;
    }

    /**
     * @return Returns the codeBlkVertCausCont.
     */
    public String getCodeBlkVertCC() {
        return codeBlkVertCC;
    }

    /**
     * @return Returns the codeBlockWidth.
     */
    public int getCodeBlkWidth() {
        return codeBlkWidth;
    }

    /**
     * @return Returns the colorSpecMethod.
     */
    public String getColorSpecMethod() {
        return colorSpecMethod;
    }

    /**
     * @return Returns the componentHorSep.
     */
    public Vector getCompHorSep() {
        return compHorSep;
    }

    /**
     * @return Returns the componentVertSep.
     */
    public Vector getCompVertSep() {
        return compVertSep;
    }

    /**
     * @return Returns the defPrecinctSizes.
     */
    public Vector getDefPrecinctSizes() {
        return defPrecinctSizes;
    }

    /**
     * @return Returns the horOffset.
     */
    public int getImageHorOffset() {
        return imageHorOffset;
    }

    /**
     * @return Returns the vertOffset.
     */
    public int getImageVertOffset() {
        return imageVertOffset;
    }

    /**
     * @return Returns the multCompTrans.
     */
    public String[] getMultCompTrans() {
        return this.multCompTrans;
    }

    /**
     * @return Returns the numDecompLevels.
     */
    public int getNumDecLevs() {
        return numDecLevs;
    }

    /**
     * @return Returns the numGuardBits.
     */
    public int getNumGuardBits() {
        return numGuardBits;
    }

    /**
     * @return Returns the numLayers.
     */
    public int getNumLayers() {
        return numLayers;
    }

    /**
     * @return Returns the numTiles.
     */
    public int getNumTiles() {
        return numTiles;
    }

    /**
     * @return Returns the progressionOrder.
     */
    public Vector getProgOrder() {
        return progOrder;
    }

    /**
     * @return Returns the quantStyle.
     */
    public String getQuantStyle() {
        return quantStyle;
    }

    /**
     * @return Returns the refGridHeight.
     */
    public int getRefGridHeight() {
        return refGridHeight;
    }

    /**
     * @return Returns the refGridWidth.
     */
    public int getRefGridWidth() {
        return refGridWidth;
    }

    /**
     * @return Returns the tileHeight.
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * @return Returns the tileHorOffset.
     */
    public int getTileHorOffset() {
        return tileHorOffset;
    }

    /**
     * @return Returns the tileVertOffset.
     */
    public int getTileVertOffset() {
        return tileVertOffset;
    }

    /**
     * @return Returns the tileWidth.
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * @return Returns the transformType.
     */
    public Vector getTransformType() {
        return transformType;
    }

    /**
     * 
     * @return whether or not it uses the default precinct
     * 	sizes.
     */
    public String has15x15DefPrecSizes() {
        return this.has15x15DefPrecSizes;
    }

    /**
     * @return Returns the hasRegionOfInterest.
     */
    public String hasRegionOfInterest() {
        return hasRegionOfInterest;
    }

    /**
     * @return Returns whether or not the image's components 
     * 	are signed values.
     */
    public String hasSignedComponents() {
    	return hasSignedComps;
    }

    /**
     * @return Returns whether the actual color space of the image data
     * is unknown.
     */
    public String isColorUnknown() {
    	return colorUnknown;
    }

    /**
     * @return Returns the noEPH.
     */
    public boolean isNoEPH() {
        return noEPH;
    }

    /**
     * @return Returns the noSOP.
     */
    public boolean isNoSOP() {
        return noSOP;
    }

    /**
     * @param codeBlockHeight The codeBlockHeight to set.
     */
    public void setCodeBlkHeight(int codeBlockHeight) {
        this.codeBlkHeight = codeBlockHeight;
    }

    /**
     * @param codeBlkPredTerm The codeBlkPredTerm to set.
     */
    public void setCodeBlkPredTerm(String codeBlkPredTerm) {
        this.codeBlkPredTerm = codeBlkPredTerm;
    }

    /**
     * @param codeBlkResetContProbs The codeBlkResetContProbs to set.
     */
    public void setCodeBlkResetProbs(String codeBlkResetContProbs) {
        this.codeBlkResetProbs = codeBlkResetContProbs;
    }

    /**
     * @param codeBlkSegSyms The codeBlkSegSyms to set.
     */
    public void setCodeBlkSegSyms(String codeBlkSegSyms) {
        this.codeBlkSegSyms = codeBlkSegSyms;
    }

    /**
     * @param codeBlkArithCodBypass The codeBlkArithCodBypass to set.
     */
    public void setCodeBlkSelArith(String codeBlkArithCodBypass) {
        this.codeBlkSelArith = codeBlkArithCodBypass;
    }

    /**
     * @param codeBlkTermOnPass The codeBlkTermOnPass to set.
     */
    public void setCodeBlkTerm(String codeBlkTermOnPass) {
        this.codeBlkTerm = codeBlkTermOnPass;
    }

    /**
     * @param codeBlkVertCausCont The codeBlkVertCausCont to set.
     */
    public void setCodeBlkVertCC(String codeBlkVertCausCont) {
        this.codeBlkVertCC = codeBlkVertCausCont;
    }

    /**
     * @param codeBlockWidth The codeBlockWidth to set.
     */
    public void setCodeBlkWidth(int codeBlockWidth) {
        this.codeBlkWidth = codeBlockWidth;
    }

    /**
     * @param colorSpecMethod The colorSpecMethod to set.
     */
    public void setColorSpecMethod(String colorSpecMethod) {
        this.colorSpecMethod = colorSpecMethod;
    }

    /**
     * @param colorUnknown Specifies if the actual color 
     * space of the image data is unknown.
     */
    public void setColorUnknown(String colorUnknown) {
    	this.colorUnknown = colorUnknown;
    }

    /**
     * @param componentHorSep The componentHorSep to set.
     */
    public void setCompHorSep(Vector componentHorSep) {
        this.compHorSep = componentHorSep;
    }

    /**
     * @param componentVertSep The componentVertSep to set.
     */
    public void setCompVertSep(Vector componentVertSep) {
        this.compVertSep = componentVertSep;
    }

    /**
     * @param defPrecinctSizes The defPrecinctSizes to set.
     */
    public void setDefPrecinctSizes(Vector defPrecinctSizes) {
        this.defPrecinctSizes = defPrecinctSizes;
    }

    /**
     * @param has15x15DefPrecSizes The has15x15DefPrecSizes to set.
     */
    public void setHas15x15DefPrecSizes(String has15x15DefPrecSizes) {
        this.has15x15DefPrecSizes = has15x15DefPrecSizes;
    }

    /**
     * @param hasRegionOfInterest The hasRegionOfInterest to set.
     */
    public void setHasRegionOfInterest(String hasRegionOfInterest) {
        this.hasRegionOfInterest = hasRegionOfInterest;
    }

    /**
     * Sets whether or not the image's components are signed values.
     * 
     * @param hasSignedComponents The hasSignedComponents to set.
     */
    public void setHasSignedComps(String hasSignedComponents) {
    	this.hasSignedComps = hasSignedComponents;
    }

    /**
     * @param horOffset The horOffset to set.
     */
    public void setImageHorOffset(int horOffset) {
        this.imageHorOffset = horOffset;
    }

    /**
     * @param vertOffset The vertOffset to set.
     */
    public void setImageVertOffset(int vertOffset) {
        this.imageVertOffset = vertOffset;
    }

    /**
     * @param multCompTrans The multCompTrans to set.
     */
    public void setMultCompTrans(String[] multCompTrans) {
       this.multCompTrans = multCompTrans;
    }

    /**
     * @param noEPH The noEPH to set.
     */
    public void setNoEPH(boolean noEPH) {
        this.noEPH = noEPH;
    }

    /**
     * @param noSOP The noSOP to set.
     */
    public void setNoSOP(boolean noSOP) {
        this.noSOP = noSOP;
    }

    /**
     * @param numDecompLevels The numDecompLevels to set.
     */
    public void setNumDecLevs(int numDecompLevels) {
        this.numDecLevs = numDecompLevels;
    }

    /**
     * @param numGuardBits The numGuardBits to set.
     */
    public void setNumGuardBits(int numGuardBits) {
        this.numGuardBits = numGuardBits;
    }

    /**
     * @param numLayers The numLayers to set.
     */
    public void setNumLayers(int numLayers) {
        this.numLayers = numLayers;
    }

    /**
     * @param numTiles The numTiles to set.
     */
    public void setNumTiles(int numTiles) {
        this.numTiles = numTiles;
    }

    /**
     * @param quantStyle The quantStyle to set.
     */
    public void setQuantStyle(String quantStyle) {
        this.quantStyle = quantStyle;
    }
    
    /**
     * @param refGridHeight The refGridHeight to set.
     */
    public void setRefGridHeight(int refGridHeight) {
        this.refGridHeight = refGridHeight;
    }

    /**
     * @param refGridWidth The refGridWidth to set.
     */
    public void setRefGridWidth(int refGridWidth) {
        this.refGridWidth = refGridWidth;
    }

    /**
     * @param tileHeight The tileHeight to set.
     */
    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    /**
     * @param tileHorOffset The tileHorOffset to set.
     */
    public void setTileHorOffset(int tileHorOffset) {
        this.tileHorOffset = tileHorOffset;
    }

    /**
     * @param tileVertOffset The tileVertOffset to set.
     */
    public void setTileVertOffset(int tileVertOffset) {
        this.tileVertOffset = tileVertOffset;
    }

    /**
     * @param tileWidth The tileWidth to set.
     */
    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    /**
     * Puts all the Jpeg2000 image's members and their 
     * values in a String.
     * 
     * @return all the members and their values
     */
    public String toString() {
        String prior = super.toString();
        StringBuffer sb = new StringBuffer("");
        sb.append(prior);
        
        Iterator iter = null;
        
        sb.append("\n");
        //sb.append("\tCan have no EPH marker: " + isNoEPH() + "\n");
        //sb.append("\tCan have no SOP marker: " + isNoSOP() + "\n");
        sb.append("\tCode-block height: " + getCodeBlkHeight() + "\n");
        sb.append("\tCode-block width: " + getCodeBlkWidth() + "\n");
        sb.append("\tCode-block style (arith coding bypass): " + 
                getCodeBlkSelArith() + "\n");
        sb.append("\tCode-block style (pred. termination): " + 
                getCodeBlkPredTerm() + "\n");
        sb.append("\tCode-block style (reset context probs.): " + 
                getCodeBlkResetProbs() + "\n");
        sb.append("\tCode-block style (Seg. symbols): " + 
                getCodeBlkSegSyms() + "\n");
        sb.append("\tCode-block style (term. on coding pass): " + 
                getCodeBlkTerm() + "\n");
        sb.append("\tCode-block style (vert. causal context): " + 
                getCodeBlkVertCC() + "\n");
        sb.append("\tColor space is unknown: " +
                this.isColorUnknown() + "\n");
        sb.append("\tColor specification method: " +
                this.getColorSpecMethod() + "\n");
        sb.append("\tComponent separation (horizontal): ");
        iter = this.getCompHorSep().iterator();
        while (iter.hasNext()) {
            sb.append(iter.next() + " ");
        }
        sb.append("\n");
        sb.append("\tComponent separation (vertical): ");
        iter = this.getCompVertSep().iterator();
        while (iter.hasNext()) {
            sb.append(iter.next() + " ");
        }
        sb.append("\n");
        sb.append("\tHas default precinct sizes PPx=15, PPy=15: " + 
                has15x15DefPrecSizes() + "\n");
        sb.append("\tHas region of interest: " + 
                this.hasRegionOfInterest() + "\n");
        sb.append("\tHas signed component values: " + 
                this.hasSignedComponents() + "\n");
        sb.append("\tImage horizontal offset: " + getImageHorOffset() + "\n");
        sb.append("\tImage vertical offset: " + getImageVertOffset() + "\n");
        sb.append("\tMultiple component transformation: ");
        String[] mcts = getMultCompTrans();
        if (mcts != null){
            sb.append(mcts[0]);
        }
        sb.append("\n");
        sb.append("\tNumber of decomposition levels: " + getNumDecLevs() + "\n");
        sb.append("\tNumber of guard bits: " + getNumGuardBits() + "\n");
        sb.append("\tNumber of layers: " + getNumLayers() + "\n");
        sb.append("\tNumber of tiles: " + getNumTiles() + "\n");
        sb.append("\tProgression order: ");
        iter = this.getProgOrder().iterator();
        while (iter.hasNext()) {
            sb.append(iter.next() + " ");
        }
        sb.append("\n");
        sb.append("\tQuantization style: " + getQuantStyle() + "\n");
        sb.append("\tReference grid height: " + getRefGridHeight() + "\n");
        sb.append("\tReference grid width: " + getRefGridWidth() + "\n");
        sb.append("\tTile height: " + getTileHeight() + "\n");
        sb.append("\tTile horizontal offset: " + getTileHorOffset() + "\n");
        sb.append("\tTile vertical offset: " + getTileVertOffset() + "\n");
        sb.append("\tTile width: " + getTileWidth() + "\n");
        sb.append("\tWavelet Transformation type: ");
        iter = this.getTransformType().iterator();
        while (iter.hasNext()) {
            sb.append(iter.next() + " ");
        }
        sb.append("\n");
        
        return sb.toString();
    }

}
