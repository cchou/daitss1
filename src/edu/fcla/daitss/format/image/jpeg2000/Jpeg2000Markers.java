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
 * Created on Oct 8, 2004
 *
 */
package edu.fcla.daitss.format.image.jpeg2000;

/**
 * Jpeg2000Markers represents the marker types
 * described in ISO/IEC 15444-1:2000 Appendix A.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Jpeg2000Markers {
	
	/**
	 * Length in bytes of a marker.
	 */
	public static final int FIXED_MARKER_LENGTH = 2;
    
    /**
     * Coding style component.
     * Functional marker segment, optional in
     * main header, optional in tile-part header.
     */
    public static final int COC = 0xff53;
    
    /**
     * Coding style default.
     * Functional marker segment, required in
     * main header, optional in tile-part header.
     */
    public static final int COD = 0xff52;
    
    /**
     * Comment.
     * Informational marker segments, optional in
     * main header, optional in tile-part header.
     */
    public static final int COM = 0xff64;
    
    /**
     * Component registration.
     * Informational marker segments, optional in
     * main header, not allowed in tile-part header.
     */
    public static final int CRG = 0xff66;
    
    
    /**
     * End of codestream.
     * Delimiting marker/marker segment, not allowed in
     * main header, not allowed in tile-part header.
     */
    public static final int EOC = 0xffd9;
    
    /**
     * End of packet header.
     * In bit stream marker/marker segments, optional in
     * PPM, optional in PPT or bit stream.
     */
    public static final int EPH = 0xff92;
    
    /**
     * Packet length, main header.
     * Pointer marker segments, optional in
     * main header, not allowed in tile-part header.
     */
    public static final int PLM = 0xff57;
    
    /**
     * Packet length, tile-part header.
     * Pointer marker segments, not allowed in
     * main header, optional in tile-part header.
     */
    public static final int PLT = 0xff58;
    
    /**
     * Progression order change.
     * Functional marker segment, optional in
     * main header, optional in tile-part header.
     * (Required in both if there are progression
     * order changes)
     */
    public static final int POC = 0xff5f;
    
    /**
     * Packed packet headers, main header.
     * Pointer marker segments, optional in
     * main header, not allowed in tile-part header.
     */
    public static final int PPM = 0xff60;
    
    /**
     * Packed packet headers, tile-part header.
     * Pointer marker segments, not allowed in
     * main header, optional in tile-part header.
     */
    public static final int PPT = 0xff61;
    
    /**
     * Quantization component.
     * Functional marker segment, required in
     * main header, optional in tile-part header.
     */
    public static final int QCC = 0xff5d;
    
    /**
     * Quantization default.
     * Functional marker segment, required in
     * main header, optional in tile-part header.
     */
    public static final int QCD = 0xff5c;
    
    /**
     * Region-of-interest.
     * Functional marker segment, optional in
     * main header, optional in tile-part header.
     */
    public static final int RGN = 0xff5e;
    
    /**
     * Image and tile size.
     * Fixed information marker segment, required in
     * main header, not allowed in tile-part header.
     */
    public static final int SIZ = 0xff51;

    /**
     * Start of codestream.
     * Delimiting marker/marker segment, required in
     * main header, not allowed in tile-part header.
     */
    public static final int SOC = 0xff4f;
    
    /**
     * Start of data.
     * Delimiting marker/marker segment, not allowed in
     * main header, last marker of tile-part header.
     */
    public static final int SOD = 0xff93;
    
    /**
     * Start of packet.
     * In bit stream marker/marker segments, not allowed in
     * main header, not allowed in tile-part header,
     * optional in bit stream.
     */
    public static final int SOP = 0xff91;
    
    /**
     * Start of tile-part.
     * Delimiting marker/marker segment, not allowed in
     * main header, required in tile-part header.
     */
    public static final int SOT = 0xff90;
    
    /**
     * Tile-part lengths.
     * Pointer marker segments, optional in
     * main header, not allowed in tile-part header.
     */
    public static final int TLM = 0xff55;
    
}
