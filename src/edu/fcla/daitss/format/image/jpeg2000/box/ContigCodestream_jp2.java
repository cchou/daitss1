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
package edu.fcla.daitss.format.image.jpeg2000.box;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.image.jpeg2000.Jp2;
import edu.fcla.daitss.format.image.jpeg2000.Jp2Anomalies;
import edu.fcla.daitss.format.image.jpeg2000.Jp2Image;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Anomalies;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Image;
import edu.fcla.daitss.format.image.jpeg2000.Jpeg2000Markers;
import edu.fcla.daitss.util.ByteReader;

/**
 * Contiguous Codestream box of a JP2.
 * 
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public class ContigCodestream_jp2 extends ContiguousCodestream {
	
	/**
	 * Size in bytes of the Csiz field in the SIZ marker segment.
	 */
	private static final int FIXED_CSIZ_LENGTH = 2;
	
	/**
     * Size in bytes of the Isot field in the SOT marker segment.
     */
    private static final int FIXED_ISOT_LENGTH = 2;
    
    /**
	 * Size in bytes of the Lsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_LSIZ_LENGTH = 2;
    
    /**
     * Size in bytes of the Lsot field in the SOT marker segment.
     */
    private static final int FIXED_LSOT_LENGTH = 2;
    
    /**
     * Size in bytes of the Psot field in the SOT marker segment.
     */
    private static final int FIXED_PSOT_LENGTH = 4;
    
    /**
	 * Size in bytes of the Rsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_RSIZ_LENGTH = 2;
    
    /**
     * Size in bytes of the TNsot field in the SOT marker segment.
     */
    private static final int FIXED_TNSOT_LENGTH = 1;
	
	/**
     * Size in bytes of the TPsot field in the SOT marker segment.
     */
    private static final int FIXED_TPSOT_LENGTH = 1;
	
	/**
	 * Size in bytes of the XOsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_XOSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the Xsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_XSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the XTOsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_XTOSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the XTsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_XTSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the YOsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_YOSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the Ysiz field in the SIZ marker segment.
	 */
	private static final int FIXED_YSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the YTOsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_YTOSIZ_LENGTH = 4;
	
	/**
	 * Size in bytes of the YTsiz field in the SIZ marker segment.
	 */
	private static final int FIXED_YTSIZ_LENGTH = 4;
	
	/**
	 * In the main header.
	 */
	private static final String LOC_MAIN_HEADER = "main_header";
	
	/**
	 * In a tile-part header.
	 */
	private static final String LOC_TILE_PART_HEADER = "tilepart_header";
	
	/**
	 * Maximum value for the Csiz field in the SIZ marker segment.
	 */
	private static final int MAX_CSIZ_VALUE = 16384;
	
	/**
	 * Maximum value for the XOsiz field in the SIZ marker segment.
	 */
	private static final int MAX_XOSIZ_VALUE = ((int)Math.pow(2,32))-2;
	
	/**
	 * Maximum value for the Xsiz field in the SIZ marker segment.
	 */
	private static final int MAX_XSIZ_VALUE = ((int)Math.pow(2,32))-1;
	
	/**
	 * Maximum value for the XTOsiz field in the SIZ marker segment.
	 */
	private static final int MAX_XTOSIZ_VALUE = ((int)Math.pow(2,32))-2;
	
	/**
	 * Maximum value for the XTsiz field in the SIZ marker segment.
	 */
	private static final int MAX_XTSIZ_VALUE = ((int)Math.pow(2,32))-1;
	
	/**
	 * Maximum value for the YOsiz field in the SIZ marker segment.
	 */
	private static final int MAX_YOSIZ_VALUE = ((int)Math.pow(2,32))-2;
	
	/**
	 * Maximum value for the Ysiz field in the SIZ marker segment.
	 */
	private static final int MAX_YSIZ_VALUE = ((int)Math.pow(2,32))-1;
	
	/**
	 * Maximum value for the YTOsiz field in the SIZ marker segment.
	 */
	private static final int MAX_YTOSIZ_VALUE = ((int)Math.pow(2,32))-2;
	
	/**
	 * Maximum value for the YTsiz field in the SIZ marker segment.
	 */
	private static final int MAX_YTSIZ_VALUE = ((int)Math.pow(2,32))-1;
	
	/**
	 * Minimum value for the Csiz field in the SIZ marker segment.
	 */
	private static final int MIN_CSIZ_VALUE = 1;
	
	/**
	 * Minimum value for the XOsiz field in the SIZ marker segment.
	 */
	private static final int MIN_XOSIZ_VALUE = 0;
	
	/**
	 * Minimum value for the Xsiz field in the SIZ marker segment.
	 */
	private static final int MIN_XSIZ_VALUE = 1;
	
	/**
	 * Minimum value for the XTOsiz field in the SIZ marker segment.
	 */
	private static final int MIN_XTOSIZ_VALUE = 0;
	
	/**
	 * Minimum value for the XTsiz field in the SIZ marker segment.
	 */
	private static final int MIN_XTSIZ_VALUE = 1;
	
	/**
	 * Minimum value for the YOsiz field in the SIZ marker segment.
	 */
	private static final int MIN_YOSIZ_VALUE = 0;
	
	/**
	 * Minimum value for the Ysiz field in the SIZ marker segment.
	 */
	private static final int MIN_YSIZ_VALUE = 1;
	
	/**
	 * Minimum value for the YTOsiz field in the SIZ marker segment.
	 */
	private static final int MIN_YTOSIZ_VALUE = 0;
	
	/**
	 * Minimum value for the YTsiz field in the SIZ marker segment.
	 */
	private static final int MIN_YTSIZ_VALUE = 1;
    
    /**
     * End of a tile part marker segment.
     * Used to jump over bitstream data to get to the next tile header.
     */
    long nextTileJumpPosition = 0;
	
	/**
	 * Parse location within the box.
	 */
	private String parseLocation = null;
	
	/**
	 * A parsing flag to set when the POC marker is seen.
	 * This is a required marker when there are progression order
	 * changes but it can be in either the codestream main header or a
	 * tile-part header.
	 */
	private boolean sawPOC = false;
	
	/**
	 * A parsing flag to set when the PPM marker is seen.
	 * If this is present in the codestream main header, then
	 * the PPT marker is disallowed in the tile-part header(s)
	 * and codestream packet headers are disallowed.
	 */
	private boolean sawPPM = false;
    
    /**
     * A parsing flag to set when the RGN marker has been seen.
     * Used to determine whether the image has a region of interest.
     */
    private boolean sawRGN = false;
	
	/**
	 * @param box
	 */
	public ContigCodestream_jp2(Box box) {
		super(box);
	}
    
    
	
	/**
	 * 
	 * @param reader
	 * @param df
	 * @throws FatalException
	 */
	public void extractMetadata(ByteReader reader, Jp2 df) 
		throws FatalException {
		boolean keepParsing = true;
		
		System.out.println("JP2C");
        
        // whether or not to print the codestream marker
        // out to STDOUT. Useful for testing.
        boolean printMarker = true;
		
		int nextMarker, nextParameter;
	    
	    try {
	        // look for the required SOC marker
		    nextMarker = (int) reader.readBytes(
		    		Jpeg2000Markers.FIXED_MARKER_LENGTH, df.getByteOrder());
		    subtractFromBytesLeft(Jpeg2000Markers.FIXED_MARKER_LENGTH);
		    
		    if (nextMarker != Jpeg2000Markers.SOC){
		        //System.out.println("Missing SOC");
		        throw new ParseException("Missing SOC",
		                (int)reader.getFilePointer());
		    }
            if (printMarker){
                System.out.println("\tSOC");
            }

		    parseLocation = LOC_MAIN_HEADER;
		    
		    // look for the required SIZ marker
	        // can only have one SIZ per codestream
		    nextMarker = (int) reader.readBytes(
		    		Jpeg2000Markers.FIXED_MARKER_LENGTH, df.getByteOrder());
		    subtractFromBytesLeft(Jpeg2000Markers.FIXED_MARKER_LENGTH);
		    
		    if (nextMarker != Jpeg2000Markers.SIZ){
		        //System.out.println("Missing SIZ");
		        throw new ParseException("Missing SIZ",
		                (int)reader.getFilePointer());
		    } 
            
            if (printMarker){
                System.out.println("\tSIZ");
            }
		    // parse SIZ marker segment
		    contentLength = parseSIZ(reader, df, contentLength);
		    
		    // parse rest of main header
		    //
		    // in this part only 2 markers are required: COD and QCD
		    //
		    // POC is required if and only if there are any progression order changes
		    // and even if it is required it can appear in the tile-part header
		    // instead of here. So we have to put the flag that POC was seen
		    // outside of this method so that it can be set as well by
		    // the method that parses the tile-part header.
		    //
		    // these can be in any order
		    boolean sawCOD = false, // have to have one and only one COD
		    	sawCRG = false, // can have 0 or 1 CRG (See A.9.1 in 15444-1)
		    	sawQCD = false,  // have to have one and only one QCD
		    	mainHeaderDone = false;
		    int numCOC = 0, // can have 0 or 1 COC per component
		    	numQCC = 0, // can have 0 or 1 QCC per component
		    	numRGN = 0; // can have 0 or 1 QCC per component
		    
		    while (!mainHeaderDone) {
		        nextMarker = (int) reader.readBytes(2, df.getByteOrder());
			    contentLength -= 2;
			    
			    // SOT
			    if (nextMarker == Jpeg2000Markers.SOT){
			        // The Tile-part header ends the main header
			        mainHeaderDone = true;
                    parseLocation = LOC_TILE_PART_HEADER;
                    if (printMarker){
                        System.out.println("\tSOT");
                    }
			    // COD
			    } else if (nextMarker == Jpeg2000Markers.COD){
                    if (printMarker){
                        System.out.println("\tCOD");
                    }
			        if (sawCOD) {
			            // error - can only be one COD in the codestream main header
			            throw new ParseException("Multiple COD",
				                (int)reader.getFilePointer());
			        }
			        sawCOD = true;
			        contentLength = parseCOD(reader, df, contentLength);
			    // COC
			    } else if (nextMarker == Jpeg2000Markers.COC){
                    if (printMarker){
                        System.out.println("\tCOC");
                    }
			        if (numCOC == df.getCodeStream().getNumComponents()) {
			            // error - can only be one COC in the codestream main header
			            // per component
			            throw new ParseException("Too many COC",
				                (int)reader.getFilePointer());
			        }
			        numCOC++;
			        contentLength = parseCOC(reader, df, contentLength);
			        
			    // QCD    
			    } else if (nextMarker == Jpeg2000Markers.QCD){
                    if (printMarker){
                        System.out.println("\tQCD");
                    }
			        if (sawQCD) {
			            // error - can only be one QCD in the codestream main header
			            throw new ParseException("Multiple QCD",
				                (int)reader.getFilePointer());
			        }
			        sawQCD = true;
			        contentLength = parseQCD(reader, df, contentLength);
			    // QCC
			    } else if (nextMarker == Jpeg2000Markers.QCC){
                    if (printMarker){
                        System.out.println("\tQCC");
                    }
			        if (numQCC == df.getCodeStream().getNumComponents()) {
			            // error - can only be one QCC in the codestream main header
			            // per component
			            throw new ParseException("Too many QCC",
				                (int)reader.getFilePointer());
			        }
			        numQCC++;
			        // Lqcc
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 5 || nextParameter > 199) {
				        // invalid value - see Table A-31 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid QCC Lqcc value",
				                (int)reader.getFilePointer());
				    }
                    // skip rest of QCC
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // RGN
			    } else if (nextMarker == Jpeg2000Markers.RGN){
                    if (printMarker){
                        System.out.println("\tRGN");
                    }
                    
                    // has region of interest
                    df.getCodeStream().setHasRegionOfInterest(DataFile.TRUE);
                    
			        if (numRGN == df.getCodeStream().getNumComponents()) {
			            // error - can only be one RGN in the codestream main 
			            // header per component
			            throw new ParseException("Too many RGN",
				                (int)reader.getFilePointer());
			        }
			        numRGN++;
			        // Lrgn
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 5 || nextParameter > 6) {
				        // invalid value - see Table A-24 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid RGN Lrgn value",
				                (int)reader.getFilePointer());
				    }
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // POC
			    } else if (nextMarker == Jpeg2000Markers.POC){
                    if (printMarker){
                        System.out.println("\tPOC");
                    }
			        if (sawPOC) {
			            // error - can only be one POC in the codestream main 
			            // header
			            throw new ParseException("Multiple QCD",
				                (int)reader.getFilePointer());
			        }
			        sawPOC = true;
			        // Lpoc
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 9 || nextParameter > 65535) {
				        // invalid value - see Table A-32 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid POC Lpoc value",
				                (int)reader.getFilePointer());
				    }
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // PPM
			    } else if (nextMarker == Jpeg2000Markers.PPM){
                    if (printMarker){
                        System.out.println("\tPPM");
                    }
			        if (sawPPM) {
			            // error - can only be one PPM in the codestream main 
			            // header
			            throw new ParseException("Multiple PPM",
				                (int)reader.getFilePointer());
			        }
			        sawPPM = true;
			        // Lppm
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 7 || nextParameter > 65535) {
				        // invalid value - see Table A-38 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid PPM Lppm value",
				                (int)reader.getFilePointer());
				    }
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // TLM
			    } else if (nextMarker == Jpeg2000Markers.TLM){
                    if (printMarker){
                        System.out.println("\tTLM");
                    }
			        // there may be multiple TLM markers in the main
			        // header (presumably when there are multiple tiles)
			        // Ltlm
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 6 || nextParameter > 65535) {
				        // invalid value - see Table A-33 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid TLM Ltlm value",
				                (int)reader.getFilePointer());
				    }
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // PLM
			    } else if (nextMarker == Jpeg2000Markers.PLM){
                    if (printMarker){
                        System.out.println("\tPLM");
                    }
			        // there may be multiple PLM markers in the main
			        // header
			        // Lplm
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 4 || nextParameter > 65535) {
				        // invalid value - see Table A-33 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid PLM Lplm value",
				                (int)reader.getFilePointer());
				    }
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // CRG
			    } else if (nextMarker == Jpeg2000Markers.CRG){
                    if (printMarker){
                        System.out.println("\tCRG");
                    }
			        if (sawCRG) {
			            // error - can only be one CRG in the codestream main 
			            // header
			            throw new ParseException("Multiple CRG",
				                (int)reader.getFilePointer());
			        }
			        sawCRG = true;
			        // Lcrg
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
				    contentLength -= 2;
				    if (nextParameter < 6 || nextParameter > 65535) {
				        // invalid value - see Table A-42 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid CRG Lcrg value",
				                (int)reader.getFilePointer());
				    }
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    // COM
			    } else if (nextMarker == Jpeg2000Markers.COM){
                    if (printMarker){
                        System.out.println("\tCOM");
                    }
			        // there may be multiple COM markers in the main
			        // header
			        // Lcom
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
			        contentLength -= 2;
				    if (nextParameter < 5 || nextParameter > 65535) {
				        // invalid value - see Table A-43 in ISO/IEC 15444-1:2000(E)
				        throw new ParseException("Invalid COM Lcom value",
				                (int)reader.getFilePointer());
				    }
				    //System.out.println("Com length: " + nextParameter);
				    reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    } else {
                    if (printMarker){
                        System.out.println("\tUnknown marker: " + nextMarker +
			                " at byte " + reader.getFilePointer());
                    }
			        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
			        reader.skipBytes(nextParameter-2);
				    contentLength -= nextParameter-2;
			    }
		    }
		   
	    } catch (ParseException pe) {
	        df.addAnomaly(df.getAnomsPossible().
					getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_JP2CBOX));
	        System.out.println("\tGot this parse exception: " + pe.getMessage() +
	                " at position: " + pe.getErrorOffset());
	        df.setContinueParsing(false);
	    }
	    
        // skipping the rest of the data starting from the start of tile
        // marker (SOT)
        
        // TODO: !!!!!!!!!!! need to keep parsing the rest of this box.
        // that way we can determine if there's a region of interest,
        // and add overriding info from tile-part COD and COCs
        
        try {
            boolean sawCodestreamEnd = false;
            int parsedTileSegments = 0;
            
            while (!sawCodestreamEnd){
                if (parsedTileSegments < df.getCodeStream().getNumTiles()){
                    // parse the tile part marker segment til just after the SOD marker
                    contentLength = parseTile(reader, df, contentLength);
                    parsedTileSegments++;
                } else {
                    sawCodestreamEnd = true;
                    continue;
                }
                
                //System.out.println("num tiles: " + 
                //        df.getCodeStream().getNumTiles());
                //System.out.println("parsed tiles: " + parsedTileSegments);
                
                if (nextTileJumpPosition != 0){
                    // see if we can read the marker
                    // TODO: check that this byte we're jumping to 
                    // is in the file; catch exceptions
                    reader.seek(nextTileJumpPosition);
                    // reset jump position
                    nextTileJumpPosition = 0;
                    
                    // read in the marker type
                    nextMarker = (int) reader.readBytes(
                            Jpeg2000Markers.FIXED_MARKER_LENGTH, df.getByteOrder());
                    subtractFromBytesLeft(Jpeg2000Markers.FIXED_MARKER_LENGTH);
                    
                    //System.out.println("Next Marker after jump: " + 
                    //        Integer.toHexString(nextMarker));
                    
                    // we should either be at a SOT or EOC marker
                    if (nextMarker == Jpeg2000Markers.EOC){
                        sawCodestreamEnd = true;
                    } else if (nextMarker == Jpeg2000Markers.SOT){
                        // parse next tile-part segment
                        continue;
                    } else {
                        throw new ParseException("Invalid marker after tile part: " + 
                                Integer.toHexString(nextMarker),
                                (int)reader.getFilePointer());
                    }
                } else {
                    // we should have just read the last tile
                }
            }
            
        } catch (ParseException e) {
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_JP2CBOX));
            System.out.println("\tGot this parse exception: " + e.getMessage() +
                    " at position: " + e.getErrorOffset());
            df.setContinueParsing(false);
        }
        
        // skip over any remaining bytes in this box
	    reader.skipBytes((int)contentLength);
		
	}
	
	/**
	 * Parse the COC marker segment.
	 * @param reader 
	 * @param df 
	 * @param contentLength
	 * @return long
	 * @throws ParseException
	 * @throws FatalException
	 */
	protected long parseCOC(ByteReader reader, Jpeg2000 df, long contentLength) 
		throws ParseException, FatalException {
	    
	    int nextParameter;
	    
	    // Lcoc
        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
	    contentLength -= 2;
	    if (nextParameter < 9 || nextParameter > 43) {
	        // invalid value - see Table A-22 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid COC Lcoc value",
	                (int)reader.getFilePointer());
	    }
	    reader.skipBytes(nextParameter-2);
	    contentLength -= nextParameter-2;
	    
	    return contentLength;
	}
	
	/**
	 * Parse the COD marker segment.
     * TODO: change setters in this method to add values instead of set
     * values since this is called from main and tile headers.
	 * @param reader 
	 * @param df 
	 * @param contentLength remaining number of bytes in the codestream box (JP2C)
	 * @return the number of bytes left to read after this marker segment
	 * @throws ParseException
	 * @throws FatalException
	 */
	protected long parseCOD(ByteReader reader, Jp2 df, 
			long contentLength) 
		throws ParseException, FatalException {
	    
	    int nextParameter;
	    
	    // Lcod
        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
	    contentLength -= 2;
	    if (nextParameter < 12 || nextParameter > 45) {
	        // invalid value - see Table A-12 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid COD Lcod value",
	                (int)reader.getFilePointer());
	    }
	    //System.out.println("COD length: " + nextParameter);
	    int Lcod = nextParameter;
	    Lcod -= 2; // remove Lcod length
	    
	    // Scod (default coding style for all components)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove Scod length
	    // see which Scod bits are set
	    if ((nextParameter & 1) == 0){
	        // entropy coder, precincts with PPx = 15, PPy = 15
	    	df.getCodeStream().setHas15x15DefPrecSizes(DataFile.TRUE);
	    } else {
	    	df.getCodeStream().setHas15x15DefPrecSizes(DataFile.FALSE);
	    }
	    if ((nextParameter & 2) == 0){ // 10 binary
	        // no SOP marker segments used
	    	df.getCodeStream().setNoSOP(true);
	    }
	    if ((nextParameter & 4) == 0){ // 100 binary
	        // no EPH marker used
	    	df.getCodeStream().setNoEPH(true);
	    }
	    //System.out.println("Scod: " + nextParameter);
	    
	    // SGcod sub-parts
	    // SGcod A (progression order)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SGCod A length
	    //System.out.println("SGcod A: " + nextParameter);
	    switch (nextParameter){
	    	case 0: 
	    		df.getCodeStream().addProgressionOrder(Jp2Image.PROG_ORDER_LRCP);
	    	    break;
	    	case 1:
	    		df.getCodeStream().addProgressionOrder(Jp2Image.PROG_ORDER_RLCP);
	    	    break;
	    	case 2:
	    		df.getCodeStream().addProgressionOrder(Jp2Image.PROG_ORDER_RPCL);
	    	    break;
	    	case 3:
	    		df.getCodeStream().addProgressionOrder(Jp2Image.PROG_ORDER_PCRL);
	    	    break;
	    	case 4:
	    		df.getCodeStream().addProgressionOrder(Jp2Image.PROG_ORDER_CPRL);
	    	    break;
	    	default:
	    	    // unknown progression order - record the anomaly
	    	    df.addAnomaly(df.getAnomsPossible().
						getSevereElement(Jpeg2000Anomalies.JPEG2K_UNK_PROG_ORDER));
	    		// keep parsing? if we throw a ParseException here
	    		// another anomaly will be recorded that this box is bad
	    }
	    
	    // SGcod B (Number of layers)
	    nextParameter = (int) reader.readBytes(2, df.getByteOrder());
	    contentLength -= 2;
	    Lcod -= 2; // remove SGCod B length
	    df.getCodeStream().setNumLayers(nextParameter);
	    //System.out.println("SGcod B: " + nextParameter);
	    
	    // SGcod C (multiple component transformation)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SGCod C length
	    String[] mcts = new String[1];
	    switch (nextParameter) {
	    	case 0:
	    	    mcts[0] = Jp2Image.MCT_NONE;
	    	    df.getCodeStream().setMultCompTrans(mcts);
	    	    break;
	    	case 1:
	    	    mcts[0] = Jp2Image.MCT_COMP012;
	    	    df.getCodeStream().setMultCompTrans(mcts);
	    	    break;
	    	default:
	    	    // unknown multiple component transformation type
	    	    df.addAnomaly(df.getAnomsPossible().
					getSevereElement(Jpeg2000Anomalies.JPEG2K_UNK_MCT));
	    		// keep parsing
	    }
	    //System.out.println("SGcod C: " + nextParameter);
	    //System.out.println("SPcod length must be: " + Lcod);
	    
	    // SPcod D (no. of decomposition levels)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SPCod D length
	    if (nextParameter < 0 || nextParameter > 32) {
	        // invalid value - see Table A-15 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid COD SPcod D value",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setNumDecLevs(nextParameter);
	    
	    // SPcod E (code-block width)
        nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SPCod E length
	    if (nextParameter < 0 || nextParameter > 8) {
	        // invalid value - see Table A-18 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid COD SPcod E value",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setCodeBlkWidth((int)Math.pow(2, nextParameter+2));
	    //System.out.println("code block width: " + nextParameter);
        
        // SPcod F (code-block height)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SPCod F length
	    if (nextParameter < 0 || nextParameter > 8) {
	        // invalid value - see Table A-18 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid COD SPcod F value",
	                (int) reader.getFilePointer());
	    }
	    df.getCodeStream().setCodeBlkHeight((int)Math.pow(2, nextParameter+2));
	    //System.out.println("code block height: " + nextParameter);
	    
	    // SPcod G (coding block style)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SPCod G length
	    //bit 0: sel. arith. coding bypass	    
	    if ((nextParameter & 1) == 0){
	    	df.getCodeStream().setCodeBlkSelArith(DataFile.FALSE);
	    } else {
	    	df.getCodeStream().setCodeBlkSelArith(DataFile.TRUE);
	    }
	    //bit 1: reset context probs.	    
	    if ((nextParameter & 2) == 0){
	    	df.getCodeStream().setCodeBlkResetProbs(DataFile.FALSE);
	    } else {
	    	df.getCodeStream().setCodeBlkResetProbs(DataFile.TRUE);
	    }
	    //bit 2: termination on each coding pass	    
	    if ((nextParameter & 4) == 0){
	    	df.getCodeStream().setCodeBlkTerm(DataFile.FALSE);
	    } else {
	    	df.getCodeStream().setCodeBlkTerm(DataFile.TRUE);
	    }
	    //bit 3: vertically causal context	    
	    if ((nextParameter & 8) == 0){
	        df.getCodeStream().setCodeBlkVertCC(DataFile.FALSE);
	    } else {
	    	df.getCodeStream().setCodeBlkVertCC(DataFile.TRUE);
	    }
	    //bit 4: predictable termination	    
	    if ((nextParameter & 16) == 0){
	    	df.getCodeStream().setCodeBlkPredTerm(DataFile.FALSE);
	    } else {
	    	df.getCodeStream().setCodeBlkPredTerm(DataFile.TRUE);
	    }
	    //bit 5: segmentation symbols	    
	    if ((nextParameter & 32) == 0){
	    	df.getCodeStream().setCodeBlkSegSyms(DataFile.FALSE);
	    } else {
	    	df.getCodeStream().setCodeBlkSegSyms(DataFile.TRUE);
	    }
	    
	    // SPcod H (transformation type)
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lcod -= 1; // remove SPCod H length
	    if ((nextParameter & 1) == 0){
	    	df.getCodeStream().addWaveletTransformType(Jpeg2000Image.TRANS_9_7);
	    } else {
	    	df.getCodeStream().addWaveletTransformType(Jpeg2000Image.TRANS_5_3);
	    }
	    
	    // SPcod I
	    if (Lcod != 0) {
	        if (df.getCodeStream().has15x15DefPrecSizes().equals(DataFile.TRUE)){
		        // shouldn't be defining precinct sizes here because
		        // the Scod parameter gave the default precinct sizes.
	            throw new ParseException("Redefining precinct sizes",
		                (int)reader.getFilePointer());
	        }
	        // TODO: could check that the precinct sizes are valid
	    }
	    // skip the precinct sizes if they are present
	    reader.skipBytes(Lcod);
        contentLength -= Lcod;
        
	    return contentLength;
	}
    
    /**
     * 
     * @param reader
     * @param df
     * @param contentLength
     * @return long
     * @throws ParseException
     * @throws FatalException
     */
    protected long parseTile(ByteReader reader, Jp2 df, long contentLength) 
        throws ParseException, FatalException {
        
        // TODO: could use this value in the next section to see if its using markers 
        // are only valid in the first tile-part (COD, COC, QCD, QCC, RGN)
        boolean isFirstTilePart = true;
        
        int nextMarker, nextParameter;
        boolean inTilePart = true;
        
        boolean printMarker = true;
        
        try {
            while (inTilePart) {
                // parse SOT marker segment
                contentLength = parseSOT(reader, df, contentLength);
                
                // parse rest of markers until you see the SOD marker
                // which indicates the end of the tile-part header
                // and the beginning of the bitstream data for the
                // current tile-part
                nextMarker = (int) reader.readBytes(
                        Jpeg2000Markers.FIXED_MARKER_LENGTH, df.getByteOrder());
                subtractFromBytesLeft(Jpeg2000Markers.FIXED_MARKER_LENGTH);
                
                // System.out.println("Next Marker: " + Integer.toHexString(nextMarker) );
                
                // SOD
                if (nextMarker == Jpeg2000Markers.SOD){
                    // The Tile-part header ends the main header
                    inTilePart = false;
                    //parseLocation = LOC_TILE_PART_HEADER;
                    if (printMarker){
                        System.out.println("\tSOD");
                    }
                // COD
                } else if (nextMarker == Jpeg2000Markers.COD){
                    if (printMarker){
                        System.out.println("\tCOD");
                    }
                    contentLength = parseCOD(reader, df, contentLength);
                // COC
                } else if (nextMarker == Jpeg2000Markers.COC){
                    if (printMarker){
                        System.out.println("\tCOC");
                    }
                    contentLength = parseCOD(reader, df, contentLength);
                // QCD
                } else if (nextMarker == Jpeg2000Markers.QCD){
                    if (printMarker){
                        System.out.println("\tQCD");
                    }
                    contentLength = parseQCD(reader, df, contentLength);
                // QCC
                } else if (nextMarker == Jpeg2000Markers.QCC){
                    if (printMarker){
                        System.out.println("\tQCC");
                    }
                    contentLength = parseQCC(reader, df, contentLength);
                // RGN
                } else if (nextMarker == Jpeg2000Markers.RGN){
                    if (printMarker){
                        System.out.println("\tRGN");
                    }
                    contentLength = parseRGN(reader, df, contentLength);
                // POC
                } else if (nextMarker == Jpeg2000Markers.POC){
                    if (printMarker){
                        System.out.println("\tPOC");
                    }
                    
                    nextParameter = (int) reader.readBytes(2, df.getByteOrder());
                    reader.skipBytes(nextParameter-2);
                    contentLength -= nextParameter-2;
                // PPT
                } else if (nextMarker == Jpeg2000Markers.PPT){
                    if (printMarker){
                        System.out.println("\tPPT");
                    }
                    
                    nextParameter = (int) reader.readBytes(2, df.getByteOrder());
                    reader.skipBytes(nextParameter-2);
                    contentLength -= nextParameter-2;
                // PLT
                } else if (nextMarker == Jpeg2000Markers.PLT){
                    if (printMarker){
                        System.out.println("\tPLT");
                    }
                    
                    nextParameter = (int) reader.readBytes(2, df.getByteOrder());
                    reader.skipBytes(nextParameter-2);
                    contentLength -= nextParameter-2;
                // COM
                } else if (nextMarker == Jpeg2000Markers.COM){
                    if (printMarker){
                        System.out.println("\tCOM");
                    }
                    
                    nextParameter = (int) reader.readBytes(2, df.getByteOrder());
                    reader.skipBytes(nextParameter-2);
                    contentLength -= nextParameter-2;
                // UNKNOWN
                } else {
                    // unknown marker - OK but note
                    if (printMarker){
                        System.out.println("\tUnknown");
                    }
                    nextParameter = (int) reader.readBytes(2, df.getByteOrder());
                    reader.skipBytes(nextParameter-2);
                    contentLength -= nextParameter-2;
                    inTilePart = false;
                }
                
            }
        } catch (ParseException e) {
            df.addAnomaly(df.getAnomsPossible().
                    getSevereElement(Jpeg2000Anomalies.JPEG2K_BAD_JP2CBOX));
            System.out.println("\tGot this parse exception: " + e.getMessage() +
                    " at position: " + e.getErrorOffset());
            df.setContinueParsing(false);
        }
        
        return contentLength;
    }
    
    /**
     * 
     * @param reader
     * @param df
     * @param contentLength
     * @return long
     * @throws ParseException
     * @throws FatalException
     */
    protected long parseQCC(ByteReader reader, Jp2 df, long contentLength) 
        throws ParseException, FatalException {
        int nextParameter;
        
        // Lqcc
        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
        contentLength -= 2;
        if (nextParameter < 5 || nextParameter > 199) {
            // invalid value - see Table A-31 in ISO/IEC 15444-1:2000(E)
            throw new ParseException("Invalid QCC Lqcc value",
                    (int)reader.getFilePointer());
        }
        // skip rest of QCC
        reader.skipBytes(nextParameter-2);
        contentLength -= nextParameter-2;
        
        return contentLength;
    }
    
    /**
     * 
     * @param reader
     * @param df
     * @param contentLength
     * @return long
     * @throws ParseException
     * @throws FatalException
     */
    protected long parseRGN(ByteReader reader, Jp2 df, long contentLength) 
        throws ParseException, FatalException {
        
        int nextParameter;
        
        // has region of interest
        df.getCodeStream().setHasRegionOfInterest(DataFile.TRUE);
  
        // Lrgn
        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
        contentLength -= 2;
        if (nextParameter < 5 || nextParameter > 6) {
            // invalid value - see Table A-24 in ISO/IEC 15444-1:2000(E)
            throw new ParseException("Invalid RGN Lrgn value",
                    (int)reader.getFilePointer());
        }
        reader.skipBytes(nextParameter-2);
        contentLength -= nextParameter-2;
        
        return contentLength;
    }
	
	/**
	 * Parse the QCD marker segment.
     * TODO: change setters in this method to add values instead of set
     * values since this is called from main and tile headers.
	 * @param reader 
	 * @param df 
	 * 
	 * @param contentLength
	 * @return long
	 * @throws ParseException
	 * @throws FatalException
	 */
	protected long parseQCD(ByteReader reader, Jp2 df, long contentLength) 
		throws ParseException, FatalException {
	    
	    int nextParameter;
	    
        // Lqcd
        nextParameter = (int) reader.readBytes(2, df.getByteOrder());
	    contentLength -= 2;
	    if (nextParameter < 4 || nextParameter > 197) {
	        // invalid value - see Table A-27 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid QCD Lqcd value: " + nextParameter,
	                (int)reader.getFilePointer());
	    }
	    int Lqcd = nextParameter;
	    Lqcd -= 2; // remove Lqcd length
	    
	    // Sqcd
	    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
	    contentLength -= 1;
	    Lqcd -= 1; // remove Sqcd length
	    //System.out.println("\t\tSqcd: " + nextParameter);
	    //System.out.println("\t\t64 & 31: " + (64 & 31));
	    if ((nextParameter & 31) == 0){ // 0 0000 & 1 1111 (bin)
	        df.getCodeStream().setQuantStyle(Jp2Image.QUANT_NONE);
	    } else if ((nextParameter & 30) == 0){ // 0 0001 & 1 1110 (bin)
	    	df.getCodeStream().setQuantStyle(Jp2Image.QUANT_SCALAR_DER);
	    } else if ((nextParameter & 29) == 0){ // 0 0010 & 1 1101 (bin)
	    	df.getCodeStream().setQuantStyle(Jp2Image.QUANT_SCALAR_EXP);
	    } else {
	        // unknown quantization style
	        // CHOICE: could record this anomaly as an unknown quantization
	        // style value rather than as a bad JP2C box
	        throw new ParseException("Invalid QCD Sqcd value: " + nextParameter,
	                (int)reader.getFilePointer());
	    }
	    
	    // number of guard bits (bits 7-5 of Sqcd)
	    int numGB = nextParameter >>> 5;
	    if (numGB < 0 || numGB > 7){
	        throw new ParseException("Invalid guard bit value: " + numGB,
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setNumGuardBits(numGB);
	    
	    // SPqcd
	    // CHOICE: Do we care about the quantization step values?
	    // skipping it here.
	    
	    reader.skipBytes(Lqcd);
	    contentLength -= Lqcd;
	    
	    return contentLength;
	}
    
    /**
	 * Parse the SIZ marker segment.
     * Image and file size information.
     * @param reader 
     * @param df 
	 * 
	 * @param contentLength content length remaining in the codestream box (JP2C box).
	 * @return content length remaining in the codestream box (JP2C box)
	 * 	not including the length of the SIZ marker segment
	 * @throws ParseException
	 * @throws FatalException
	 */
	protected long parseSIZ(ByteReader reader, Jp2 df, long contentLength)
		throws ParseException, FatalException {
	    
	    int nextParameter;
	    
	    // Lsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_LSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_LSIZ_LENGTH);
	    
	    //System.out.println("\t\tLsiz: " + nextParameter);
	    //System.out.println("contentLength: " + contentLength);
	    if (nextParameter < (38 + (3 * MIN_CSIZ_VALUE)) 
	    		|| nextParameter > (38 + (3 * MAX_CSIZ_VALUE))){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid Lsiz field (marker segment SIZ)",
	                (int)reader.getFilePointer());
	    }
	    
	    // Rsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_RSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_RSIZ_LENGTH);
	    
	    //the Rsiz value indicates the profile.  See amendment 1 of ISO/IEC15444-1
	    // 0 for profile 0, 1 for profile-1,  2 for no restriction...etc
	    // see http://www.digitalpreservation.gov/formats/fdd/fdd000138.shtml#notes
	    if (nextParameter < 0) {
	        // codestream has decode requirements not described in JP2 v1 spec.
	        df.addAnomaly(df.getAnomsPossible().
					getSevereElement(Jp2Anomalies.JP2_EXTRA_DECODE_REQS));
	    }
	    
	    // Xsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_XSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_XSIZ_LENGTH);
	    
	    if (nextParameter < MIN_XSIZ_VALUE || nextParameter > MAX_XSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid Xsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setRefGridWidth(nextParameter);
	    
	    // Ysiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_YSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_YSIZ_LENGTH);
	    
	    if (nextParameter < MIN_YSIZ_VALUE || nextParameter > MAX_YSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid Ysiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setRefGridHeight(nextParameter);
	    
	    // XOsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_XOSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_XOSIZ_LENGTH);
	    if (nextParameter < MIN_XOSIZ_VALUE || nextParameter > MAX_XOSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid XOsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setImageHorOffset(nextParameter);
	    
	    // YOsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_YOSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_YOSIZ_LENGTH);
	    if (nextParameter < MIN_YOSIZ_VALUE || nextParameter > MAX_YOSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid YOsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setImageVertOffset(nextParameter);
	    
	    // XTsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_XTSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_XTSIZ_LENGTH);
	    if (nextParameter < MIN_XTSIZ_VALUE || nextParameter > MAX_XTSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid XTsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setTileWidth(nextParameter);
	    
	    // YTsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_YTSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_YTSIZ_LENGTH);
	    if (nextParameter < MIN_YTSIZ_VALUE || nextParameter > MAX_YTSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid YTsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setTileHeight(nextParameter);
	    
	    // XTOsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_XTOSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_XTOSIZ_LENGTH);
	    if (nextParameter < MIN_XTOSIZ_VALUE || nextParameter > MAX_XTOSIZ_VALUE){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid XTOsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setTileHorOffset(nextParameter);
	    
	    // YTOsiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_YTOSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_YTOSIZ_LENGTH);
	    if (nextParameter < MIN_YTOSIZ_VALUE || nextParameter > MAX_YTOSIZ_VALUE ){
	        // invalid value - see Table A-9 in ISO/IEC 15444-1:2000(E)
	        throw new ParseException("Invalid YTOsiz",
	                (int)reader.getFilePointer());
	    }
	    df.getCodeStream().setTileVertOffset(nextParameter);
        
        // now we can calculate the number of tiles according to 
        // formula 15444-1 B-5
        int numXTiles = 
            (df.getCodeStream().getRefGridWidth() - 
            df.getCodeStream().getTileHorOffset()) / 
            df.getCodeStream().getTileWidth();
        
        int numYTiles = 
            (df.getCodeStream().getRefGridHeight() - 
            df.getCodeStream().getTileVertOffset()) / 
            df.getCodeStream().getTileHeight();
        
        // Number of tiles
        df.getCodeStream().setNumTiles(numXTiles * numYTiles);
	    
	    // Csiz
	    nextParameter = (int) reader.readBytes(
	    		FIXED_CSIZ_LENGTH, df.getByteOrder());
	    subtractFromBytesLeft(FIXED_CSIZ_LENGTH);
	    // already saw the number of components in the Image
	    // Header box - see that they are the same
	    if (nextParameter != df.getCodeStream().getNumComponents() ||
	            nextParameter < MIN_CSIZ_VALUE || nextParameter > MAX_CSIZ_VALUE){
	        // conflict or out of limit
	        throw new ParseException("Invalid Csiz",
	                (int)reader.getFilePointer());
	    }
	    //System.out.println("\t\tNum components: " + nextParameter);
	    
	    int numComps = nextParameter; // save it cause it changes in the loop
	    Vector XRsizs = new Vector(numComps);
	    Vector YRsizs = new Vector(numComps);
	    
	    // loop through the per component metadata
	    for (int i=0; i<numComps; i++){
	        // Ssiz^i
	        nextParameter = (int) reader.readBytes(1, df.getByteOrder());
		    contentLength -= 1;
		    if (nextParameter > 127 && nextParameter < 166) {
		        nextParameter = nextParameter - 128;
		        // has signed components
		        if (df.getCodeStream().hasSignedComponents().
		        		equals(DataFile.FALSE)){
		            // conflict with what was found in
		            // ImageHeaderBox (or Jp2HeaderBox?)
		            throw new ParseException("Conflict Ssiz",
			                (int)reader.getFilePointer());
		        }
			}
		    if (nextParameter < 0 || nextParameter > 37) {
		        throw new ParseException("Invalid Ssiz",
		                (int)reader.getFilePointer());
			} 
		    
			// TODO: do we want to check against the stored bpcc list?
			// would have to sort the list and ... (phase 2 ;)
			
	        // XRsiz^i
		    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
		    contentLength -= 1;
		    if (nextParameter < 1 || nextParameter > 255) {
		        throw new ParseException("Invalid XRsiz",
		                (int)reader.getFilePointer());
		    }
		    XRsizs.add(new Integer(nextParameter));
            
	        // YRsiz^i
		    nextParameter = (int) reader.readBytes(1, df.getByteOrder());
		    contentLength -= 1;
		    if (nextParameter < 1 || nextParameter > 255) {
		        throw new ParseException("Invalid XRsiz",
		                (int)reader.getFilePointer());
		    }
		    YRsizs.add(new Integer(nextParameter));
	    }
	    df.getCodeStream().setCompHorSep(XRsizs);
	    df.getCodeStream().setCompVertSep(YRsizs);
        
        // convert to the sampling horizontal/vertical format
        if (XRsizs != null && !XRsizs.isEmpty()){
            StringBuffer sb = new StringBuffer();
            for (Iterator iter = XRsizs.iterator(); iter.hasNext();){
                sb.append(((Integer)iter.next()).toString() + ":");
            }
            // remove last ":"
            sb.deleteCharAt(sb.length()-1);
            df.getCodeStream().setSamplingHor(sb.toString());
        }
        if (YRsizs != null && !YRsizs.isEmpty()){
            StringBuffer sb = new StringBuffer();
            for (Iterator iter = YRsizs.iterator(); iter.hasNext();){
                sb.append(((Integer)iter.next()).toString() + ":");
            }
            // remove last ":"
            sb.deleteCharAt(sb.length()-1);
            df.getCodeStream().setSamplingVer(sb.toString());
        }
	    
	    return contentLength;
	}
	
	/**
     * Parse the SOT marker (tile).
     * 
     * @param reader
     * @param df
     * @param contentLength
     * @return long integer
     * @throws FatalException
     */
    protected long parseSOT(ByteReader reader, Jp2 df, long contentLength)
        throws FatalException {
    
        int nextParameter;
        boolean printOut = false;
        
        // the start of this marker segment was just before the SOT marker
        // that we already read in
        long startSot = reader.getFilePointer() - Jpeg2000Markers.FIXED_MARKER_LENGTH;
        
        // Lsot
        nextParameter = (int) reader.readBytes(
                FIXED_LSOT_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_LSOT_LENGTH);
        if (printOut) System.out.println("marker length: " + nextParameter);
        
        // Isot
        nextParameter = (int) reader.readBytes(
                FIXED_ISOT_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_ISOT_LENGTH);
        if (printOut) System.out.println("tile index: " + nextParameter);
        
        // Psot (see 15444-1 Figure 
        nextParameter = (int) reader.readBytes(
                FIXED_PSOT_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_PSOT_LENGTH);
        if (printOut) System.out.println("length of tile-part marker segment: " + 
                nextParameter);
        if (nextParameter != 0){
            nextTileJumpPosition = startSot + nextParameter;
            if (printOut) System.out.println("end of tile-part marker segment: " + 
                    nextTileJumpPosition);
        } else {
            // this segment should contain all data til the EOC marker
            // so there wouldn't be anymore tile headers to parse for metadata
        }
        
        // TPsot
        nextParameter = (int) reader.readBytes(
                FIXED_TPSOT_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_TPSOT_LENGTH);
        if (printOut) System.out.println("tile-part index: " + nextParameter);
        
        // TNsot
        nextParameter = (int) reader.readBytes(
                FIXED_TNSOT_LENGTH, df.getByteOrder());
        subtractFromBytesLeft(FIXED_TNSOT_LENGTH);
        if (printOut) System.out.println("Number of tile-parts: " + nextParameter);
    
        return contentLength;
    }
}
