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
 * This class was loosely based on one from "Matra - the DTD Parser" whose initial
 * developer was Conrad S Roche <derupe at users.sourceforge.net>. 
 * The Matra code was released under either the Mozilla Public License 
 * Version 1.1 or alternatively the GNU GENERAL PUBLIC LICENSE Version 2 or 
 * later.
 */

package edu.fcla.daitss.format.text.dtd;

import java.io.EOFException;

import edu.fcla.daitss.format.text.TextParser;

/**
 * Parsing utility to read DTD names, nmtokens, conditional sections.
 * 
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class DTDTextParser extends TextParser {
    
	/**
	 * List of valid chars in a public identifier.
	 * [13]    PubidChar    ::=    #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
	 */
	private static final String VALID_PUBID_CHAR = 
		" \r\nabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-'()+,./:=?;!*#@$_%"; 
	
	/**
	 * Determines if a character is a base character.
	 * 
	 * [85]    BaseChar    ::=    [#x0041-#x005A] | [#x0061-#x007A] | [#x00C0-#x00D6] | 
	 * 			[#x00D8-#x00F6] | [#x00F8-#x00FF] | [#x0100-#x0131] | [#x0134-#x013E] | 
	 * 			[#x0141-#x0148] | [#x014A-#x017E] | [#x0180-#x01C3] | [#x01CD-#x01F0] | 
	 * 			[#x01F4-#x01F5] | [#x01FA-#x0217] | [#x0250-#x02A8] | [#x02BB-#x02C1] | 
	 * 			#x0386 | [#x0388-#x038A] | #x038C | [#x038E-#x03A1] | [#x03A3-#x03CE] | 
	 * 			[#x03D0-#x03D6] | #x03DA | #x03DC | #x03DE | #x03E0 | [#x03E2-#x03F3] | 
	 * 			[#x0401-#x040C] | [#x040E-#x044F] | [#x0451-#x045C] | [#x045E-#x0481] | 
	 * 			[#x0490-#x04C4] | [#x04C7-#x04C8] | [#x04CB-#x04CC] | [#x04D0-#x04EB] | 
	 * 			[#x04EE-#x04F5] | [#x04F8-#x04F9] | [#x0531-#x0556] | #x0559 | 
	 * 			[#x0561-#x0586] | [#x05D0-#x05EA] | [#x05F0-#x05F2] | [#x0621-#x063A] | 
	 * 			[#x0641-#x064A] | [#x0671-#x06B7] | [#x06BA-#x06BE] | [#x06C0-#x06CE] | 
	 * 			[#x06D0-#x06D3] | #x06D5 | [#x06E5-#x06E6] | [#x0905-#x0939] | #x093D | 
	 * 			[#x0958-#x0961] | [#x0985-#x098C] | [#x098F-#x0990] | [#x0993-#x09A8] | 
	 * 			[#x09AA-#x09B0] | #x09B2 | [#x09B6-#x09B9] | [#x09DC-#x09DD] | 
	 * 			[#x09DF-#x09E1] | [#x09F0-#x09F1] | [#x0A05-#x0A0A] | [#x0A0F-#x0A10] | 
	 * 			[#x0A13-#x0A28] | [#x0A2A-#x0A30] | [#x0A32-#x0A33] | [#x0A35-#x0A36] | 
	 * 			[#x0A38-#x0A39] | [#x0A59-#x0A5C] | #x0A5E | [#x0A72-#x0A74] | 
	 * 			[#x0A85-#x0A8B] | #x0A8D | [#x0A8F-#x0A91] | [#x0A93-#x0AA8] | 
	 * 			[#x0AAA-#x0AB0] | [#x0AB2-#x0AB3] | [#x0AB5-#x0AB9] | #x0ABD | #x0AE0 | 
	 * 			[#x0B05-#x0B0C] | [#x0B0F-#x0B10] | [#x0B13-#x0B28] | [#x0B2A-#x0B30] | 
	 * 			[#x0B32-#x0B33] | [#x0B36-#x0B39] | #x0B3D | [#x0B5C-#x0B5D] | 
	 * 			[#x0B5F-#x0B61] | [#x0B85-#x0B8A] | [#x0B8E-#x0B90] | [#x0B92-#x0B95] | 
	 * 			[#x0B99-#x0B9A] | #x0B9C | [#x0B9E-#x0B9F] | [#x0BA3-#x0BA4] | 
	 * 			[#x0BA8-#x0BAA] | [#x0BAE-#x0BB5] | [#x0BB7-#x0BB9] | [#x0C05-#x0C0C] | 
	 * 			[#x0C0E-#x0C10] | [#x0C12-#x0C28] | [#x0C2A-#x0C33] | [#x0C35-#x0C39] | 
	 * 			[#x0C60-#x0C61] | [#x0C85-#x0C8C] | [#x0C8E-#x0C90] | [#x0C92-#x0CA8] | 
	 * 			[#x0CAA-#x0CB3] | [#x0CB5-#x0CB9] | #x0CDE | [#x0CE0-#x0CE1] | 
	 * 			[#x0D05-#x0D0C] | [#x0D0E-#x0D10] | [#x0D12-#x0D28] | [#x0D2A-#x0D39] | 
	 * 			[#x0D60-#x0D61] | [#x0E01-#x0E2E] | #x0E30 | [#x0E32-#x0E33] | 
	 * 			[#x0E40-#x0E45] | [#x0E81-#x0E82] | #x0E84 | [#x0E87-#x0E88] | #x0E8A | 
	 * 			#x0E8D | [#x0E94-#x0E97] | [#x0E99-#x0E9F] | [#x0EA1-#x0EA3] | #x0EA5 | 
	 * 			#x0EA7 | [#x0EAA-#x0EAB] | [#x0EAD-#x0EAE] | #x0EB0 | [#x0EB2-#x0EB3] | 
	 * 			#x0EBD | [#x0EC0-#x0EC4] | [#x0F40-#x0F47] | [#x0F49-#x0F69] | 
	 * 			[#x10A0-#x10C5] | [#x10D0-#x10F6] | #x1100 | [#x1102-#x1103] | 
	 * 			[#x1105-#x1107] | #x1109 | [#x110B-#x110C] | [#x110E-#x1112] | #x113C | 
	 * 			#x113E | #x1140 | #x114C | #x114E | #x1150 | [#x1154-#x1155] | #x1159 | 
	 * 			[#x115F-#x1161] | #x1163 | #x1165 | #x1167 | #x1169 | [#x116D-#x116E] | 
	 * 			[#x1172-#x1173] | #x1175 | #x119E | #x11A8 | #x11AB | [#x11AE-#x11AF] | 
	 * 			[#x11B7-#x11B8] | #x11BA | [#x11BC-#x11C2] | #x11EB | #x11F0 | #x11F9 | 
	 * 			[#x1E00-#x1E9B] | [#x1EA0-#x1EF9] | [#x1F00-#x1F15] | [#x1F18-#x1F1D] | 
	 * 			[#x1F20-#x1F45] | [#x1F48-#x1F4D] | [#x1F50-#x1F57] | #x1F59 | #x1F5B | 
	 * 			#x1F5D | [#x1F5F-#x1F7D] | [#x1F80-#x1FB4] | [#x1FB6-#x1FBC] | #x1FBE | 
	 * 			[#x1FC2-#x1FC4] | [#x1FC6-#x1FCC] | [#x1FD0-#x1FD3] | [#x1FD6-#x1FDB] | 
	 * 			[#x1FE0-#x1FEC] | [#x1FF2-#x1FF4] | [#x1FF6-#x1FFC] | #x2126 | 
	 * 			[#x212A-#x212B] | #x212E | [#x2180-#x2182] | [#x3041-#x3094] | 
	 * 			[#x30A1-#x30FA] | [#x3105-#x312C] | [#xAC00-#xD7A3]
	 * 
	 * @param c character to test
	 * @return whether or not the character is a base character
	 */
	private static boolean isBaseChar(char c) {
	    boolean itIs = false;
	    
	    if ((c >= 0x0041 && c <= 0x005A) || (c >= 0x0061 && c <= 0x007A) || 
	            (c >= 0x00C0 && c <= 0x00D6) || (c >= 0x00D8 && c <= 0x00F6) || 
	            (c >= 0x00F8 && c <= 0x00FF) || (c >= 0x0100 && c <= 0x0131) || 
	            (c >= 0x0134 && c <= 0x013E) || (c >= 0x0141 && c <= 0x0148) || 
	            (c >= 0x014A && c <= 0x017E) || (c >= 0x0180 && c <= 0x01C3) || 
	            (c >= 0x01CD && c <= 0x01F0) || (c >= 0x01F4 && c <= 0x01F5) || 
	            (c >= 0x01FA && c <= 0x0217) || (c >= 0x0250 && c <= 0x02A8) || 
	            (c >= 0x02BB && c <= 0x02C1) || c == 0x0386 || (c >= 0x0388 && c <= 0x038A) || 
	             c == 0x038C || (c >= 0x038E && c <= 0x03A1) || (c >= 0x03A3 && c <= 0x03CE) || 
	            (c >= 0x03D0 && c <= 0x03D6) || c == 0x03DA || c == 0x03DC || c == 0x03DE || 
	             c == 0x03E0 || (c >= 0x03E2 && c <= 0x03F3) || (c >= 0x0401 && c <= 0x040C) || 
	            (c >= 0x040E && c <= 0x044F) || (c >= 0x0451 && c <= 0x045C) || 
	            (c >= 0x045E && c <= 0x0481) || (c >= 0x0490 && c <= 0x04C4) || 
	            (c >= 0x04C7 && c <= 0x04C8) || (c >= 0x04CB && c <= 0x04CC) || 
	            (c >= 0x04D0 && c <= 0x04EB) || (c >= 0x04EE && c <= 0x04F5) || 
	            (c >= 0x04F8 && c <= 0x04F9) || (c >= 0x0531 && c <= 0x0556) || c == 0x0559 || 
	            (c >= 0x0561 && c <= 0x0586) || (c >= 0x05D0 && c <= 0x05EA) || 
	            (c >= 0x05F0 && c <= 0x05F2) || (c >= 0x0621 && c <= 0x063A) || 
	            (c >= 0x0641 && c <= 0x064A) || (c >= 0x0671 && c <= 0x06B7) || 
	            (c >= 0x06BA && c <= 0x06BE) || (c >= 0x06C0 && c <= 0x06CE) || 
	            (c >= 0x06D0 && c <= 0x06D3) || c == 0x06D5 || (c >= 0x06E5 && c <= 0x06E6) || 
	            (c >= 0x0905 && c <= 0x0939) || c == 0x093D || (c >= 0x0958 && c <= 0x0961) || 
	            (c >= 0x0985 && c <= 0x098C) || (c >= 0x098F && c <= 0x0990) || 
	            (c >= 0x0993 && c <= 0x09A8) || (c >= 0x09AA && c <= 0x09B0) || c == 0x09B2 || 
	            (c >= 0x09B6 && c <= 0x09B9) || (c >= 0x09DC && c <= 0x09DD) || 
	            (c >= 0x09DF && c <= 0x09E1) || (c >= 0x09F0 && c <= 0x09F1) || 
	            (c >= 0x0A05 && c <= 0x0A0A) || (c >= 0x0A0F && c <= 0x0A10) || 
	            (c >= 0x0A13 && c <= 0x0A28) || (c >= 0x0A2A && c <= 0x0A30) || 
	            (c >= 0x0A32 && c <= 0x0A33) || (c >= 0x0A35 && c <= 0x0A36) || 
	            (c >= 0x0A38 && c <= 0x0A39) || (c >= 0x0A59 && c <= 0x0A5C) || c == 0x0A5E ||
	            (c >= 0x0A72 && c <= 0x0A74) || (c >= 0x0A85 && c <= 0x0A8B) || c == 0x0A8D || 
	            (c >= 0x0A8F && c <= 0x0A91) || (c >= 0x0A93 && c <= 0x0AA8) || 
	            (c >= 0x0AAA && c <= 0x0AB0) || (c >= 0x0AB2 && c <= 0x0AB3) || 
	            (c >= 0x0AB5 && c <= 0x0AB9) || c == 0x0ABD || c == 0x0AE0 || 
	            (c >= 0x0B05 && c <= 0x0B0C) || (c >= 0x0B0F && c <= 0x0B10) || 
	            (c >= 0x0B13 && c <= 0x0B28) || (c >= 0x0B2A && c <= 0x0B30) || 
	            (c >= 0x0B32 && c <= 0x0B33) || (c >= 0x0B36 && c <= 0x0B39) || c == 0x0B3D ||
	            (c >= 0x0B5C && c <= 0x0B5D) || (c >= 0x0B5F && c <= 0x0B61) || 
	            (c >= 0x0B85 && c <= 0x0B8A) || (c >= 0x0B8E && c <= 0x0B90) || 
	            (c >= 0x0B92 && c <= 0x0B95) || (c >= 0x0B99 && c <= 0x0B9A) || c == 0x0B9C ||
	            (c >= 0x0B9E && c <= 0x0B9F) || (c >= 0x0BA3 && c <= 0x0BA4) || 
	            (c >= 0x0BA8 && c <= 0x0BAA) || (c >= 0x0BAE && c <= 0x0BB5) || 
	            (c >= 0x0BB7 && c <= 0x0BB9) || (c >= 0x0C05 && c <= 0x0C0C) || 
	            (c >= 0x0C0E && c <= 0x0C10) || (c >= 0x0C12 && c <= 0x0C28) || 
	            (c >= 0x0C2A && c <= 0x0C33) || (c >= 0x0C35 && c <= 0x0C39) || 
	            (c >= 0x0C60 && c <= 0x0C61) || (c >= 0x0C85 && c <= 0x0C8C) || 
	            (c >= 0x0C8E && c <= 0x0C90) || (c >= 0x0C92 && c <= 0x0CA8) || 
	            (c >= 0x0CAA && c <= 0x0CB3) || (c >= 0x0CB5 && c <= 0x0CB9) || c == 0x0CDE ||
	            (c >= 0x0CE0 && c <= 0x0CE1) || (c >= 0x0D05 && c <= 0x0D0C) || 
	            (c >= 0x0D0E && c <= 0x0D10) || (c >= 0x0D12 && c <= 0x0D28) || 
	            (c >= 0x0D2A && c <= 0x0D39) || (c >= 0x0D60 && c <= 0x0D61) || 
	            (c >= 0x0E01 && c <= 0x0E2E) || c == 0x0E30 || (c >= 0x0E32 && c <= 0x0E33) || 
	            (c >= 0x0E40 && c <= 0x0E45) || (c >= 0x0E81 && c <= 0x0E82) || c == 0x0E84 ||
	            (c >= 0x0E87 && c <= 0x0E88) || c == 0x0E8A || c == 0x0E8D ||
	            (c >= 0x0E94 && c <= 0x0E97) || (c >= 0x0E99 && c <= 0x0E9F) || 
	            (c >= 0x0EA1 && c <= 0x0EA3) || c == 0x0EA5 || c == 0x0EA7 ||
	            (c >= 0x0EAA && c <= 0x0EAB) || (c >= 0x0EAD && c <= 0x0EAE) || c == 0x0EB0 ||
	            (c >= 0x0EB2 && c <= 0x0EB3) ||  c == 0x0EBD || (c >= 0x0EC0 && c <= 0x0EC4) || 
	            (c >= 0x0F40 && c <= 0x0F47) || (c >= 0x0F49 && c <= 0x0F69) || 
	            (c >= 0x10A0 && c <= 0x10C5) || (c >= 0x10D0 && c <= 0x10F6) || c == 0x1100 ||
	            (c >= 0x1102 && c <= 0x1103) || (c >= 0x1105 && c <= 0x1107) || c == 0x1109 ||
	            (c >= 0x110B && c <= 0x110C) || (c >= 0x110E && c <= 0x1112) || c == 0x113C || 
	             c == 0x113E || c == 0x1140 || c == 0x114C || c == 0x114E || c == 0x1150 ||
	            (c >= 0x1154 && c <= 0x1155 && c <= 0) || c == 0x1159 || 
	            (c >= 0x115F && c <= 0x1161) || c == 0x1163 || c == 0x1165 || c == 0x1167 ||
	             c == 0x1169 || (c >= 0x116D && c <= 0x116E) || (c >= 0x1172 && c <= 0x1173) || 
	             c == 0x1175 || c == 0x119E || c == 0x11A8 || c == 0x11AB || 
	            (c >= 0x11AE && c <= 0x11AF) || (c >= 0x11B7 && c <= 0x11B8) || c == 0x11BA ||
	            (c >= 0x11BC && c <= 0x11C2) || c == 0x11EB || c == 0x11F0 || c == 0x11F9 || 
	            (c >= 0x1E00 && c <= 0x1E9B) || (c >= 0x1EA0 && c <= 0x1EF9) || 
	            (c >= 0x1F00 && c <= 0x1F15) || (c >= 0x1F18 && c <= 0x1F1D) || 
	            (c >= 0x1F20 && c <= 0x1F45) || (c >= 0x1F48 && c <= 0x1F4D) || 
	            (c >= 0x1F50 && c <= 0x1F57) || c == 0x1F59 || c == 0x1F5B || c == 0x1F5D ||
	            (c >= 0x1F5F && c <= 0x1F7D) || (c >= 0x1F80 && c <= 0x1FB4) || 
	            (c >= 0x1FB6 && c <= 0x1FBC) || c == 0x1FBE || (c >= 0x1FC2 && c <= 0x1FC4) || 
	            (c >= 0x1FC6 && c <= 0x1FCC) || (c >= 0x1FD0 && c <= 0x1FD3) || 
	            (c >= 0x1FD6 && c <= 0x1FDB) || (c >= 0x1FE0 && c <= 0x1FEC) || 
	            (c >= 0x1FF2 && c <= 0x1FF4) || (c >= 0x1FF6 && c <= 0x1FFC) || c == 0x2126 || 
	            (c >= 0x212A && c <= 0x212B) || c == 0x212E || (c >= 0x2180 && c <= 0x2182) || 
	            (c >= 0x3041 && c <= 0x3094) || (c >= 0x30A1 && c <= 0x30FA) || 
	            (c >= 0x3105 && c <= 0x312C) || (c >= 0xAC00 && c <= 0xD7A3)) { 
	        itIs = true;
    	}
	    return itIs;
	}
	
	/**
	 * Determines if a character is a combining character.
	 * 
	 *  [87] CombiningChar    ::=    [#x0300-#x0345] | [#x0360-#x0361] | [#x0483-#x0486] | 
	 * 			[#x0591-#x05A1] | [#x05A3-#x05B9] | [#x05BB-#x05BD] | #x05BF | 
	 * 			[#x05C1-#x05C2] | #x05C4 | [#x064B-#x0652] | #x0670 | [#x06D6-#x06DC] | 
	 * 			[#x06DD-#x06DF] | [#x06E0-#x06E4] | [#x06E7-#x06E8] | [#x06EA-#x06ED] | 
	 * 			[#x0901-#x0903] | #x093C | [#x093E-#x094C] | #x094D | [#x0951-#x0954] | 
	 * 			[#x0962-#x0963] | [#x0981-#x0983] | #x09BC | #x09BE | #x09BF | 
	 * 			[#x09C0-#x09C4] | [#x09C7-#x09C8] | [#x09CB-#x09CD] | #x09D7 | 
	 * 			[#x09E2-#x09E3] | #x0A02 | #x0A3C | #x0A3E | #x0A3F | [#x0A40-#x0A42] | 
	 * 			[#x0A47-#x0A48] | [#x0A4B-#x0A4D] | [#x0A70-#x0A71] | [#x0A81-#x0A83] | 
	 * 			#x0ABC | [#x0ABE-#x0AC5] | [#x0AC7-#x0AC9] | [#x0ACB-#x0ACD] | 
	 * 			[#x0B01-#x0B03] | #x0B3C | [#x0B3E-#x0B43] | [#x0B47-#x0B48] | 
	 * 			[#x0B4B-#x0B4D] | [#x0B56-#x0B57] | [#x0B82-#x0B83] | [#x0BBE-#x0BC2] | 
	 * 			[#x0BC6-#x0BC8] | [#x0BCA-#x0BCD] | #x0BD7 | [#x0C01-#x0C03] | 
	 * 			[#x0C3E-#x0C44] | [#x0C46-#x0C48] | [#x0C4A-#x0C4D] | [#x0C55-#x0C56] | 
	 * 			[#x0C82-#x0C83] | [#x0CBE-#x0CC4] | [#x0CC6-#x0CC8] | [#x0CCA-#x0CCD] | 
	 * 			[#x0CD5-#x0CD6] | [#x0D02-#x0D03] | [#x0D3E-#x0D43] | [#x0D46-#x0D48] | 
	 * 			[#x0D4A-#x0D4D] | #x0D57 | #x0E31 | [#x0E34-#x0E3A] | [#x0E47-#x0E4E] | 
	 * 			#x0EB1 | [#x0EB4-#x0EB9] | [#x0EBB-#x0EBC] | [#x0EC8-#x0ECD] | 
	 * 			[#x0F18-#x0F19] | #x0F35 | #x0F37 | #x0F39 | #x0F3E | #x0F3F | 
	 * 			[#x0F71-#x0F84] | [#x0F86-#x0F8B] | [#x0F90-#x0F95] | #x0F97 | 
	 * 			[#x0F99-#x0FAD] | [#x0FB1-#x0FB7] | #x0FB9 | [#x20D0-#x20DC] | #x20E1 | 
	 * 			[#x302A-#x302F] | #x3099 | #x309A 
	 * 
	 * @param c character to test
	 * @return whether or not the character is an ideographic character
	 */
	private static boolean isCombiningChar(char c) {
	    boolean itIs = false;
	    
	    if ((c >= 0x0300 && c <= 0x0345) || (c >= 0x0360 && c <= 0x0361) || 
	            (c >= 0x0483 && c <= 0x0486) || (c >= 0x0591 && c <= 0x05A1) || 
	            (c >= 0x05A3 && c <= 0x05B9) || (c >= 0x05BB && c <= 0x05BD) || c == 0x05BF ||
	            (c >= 0x05C1 && c <= 0x05C2) || c == 0x05C4 || (c >= 0x064B && c <= 0x0652) || 
	             c == 0x0670 || (c >= 0x06D6 && c <= 0x06DC) || (c >= 0x06DD && c <= 0x06DF) || 
	            (c >= 0x06E0 && c <= 0x06E4) || (c >= 0x06E7 && c <= 0x06E8) || 
	            (c >= 0x06EA && c <= 0x06ED) || (c >= 0x0901 && c <= 0x0903) || c == 0x093C || 
	            (c >= 0x093E && c <= 0x094C) || c == 0x094D || (c >= 0x0951 && c <= 0x0954) || 
	            (c >= 0x0962 && c <= 0x0963) || (c >= 0x0981 && c <= 0x0983) || c == 0x09BC || 
	             c == 0x09BE || c == 0x09BF || (c >= 0x09C0 && c <= 0x09C4) || 
	            (c >= 0x09C7 && c <= 0x09C8) || (c >= 0x09CB && c <= 0x09CD) || c == 0x09D7 || 
	            (c >= 0x09E2 && c <= 0x09E3) || c == 0x0A02 || c == 0x0A3C || c == 0x0A3E || 
	             c == 0x0A3F || (c >= 0x0A40 && c <= 0x0A42) || (c >= 0x0A47 && c <= 0x0A48) || 
	            (c >= 0x0A4B && c <= 0x0A4D) || (c >= 0x0A70 && c <= 0x0A71) || 
	            (c >= 0x0A81 && c <= 0x0A83) || c == 0x0ABC || (c >= 0x0ABE && c <= 0x0AC5) || 
	            (c >= 0x0AC7 && c <= 0x0AC9) || (c >= 0x0ACB && c <= 0x0ACD) || 
	            (c >= 0x0B01 && c <= 0x0B03) || c == 0x0B3C || (c >= 0x0B3E && c <= 0x0B43) || 
	            (c >= 0x0B47 && c <= 0x0B48) || (c >= 0x0B4B && c <= 0x0B4D) || 
	            (c >= 0x0B56 && c <= 0x0B57) || (c >= 0x0B82 && c <= 0x0B83) || 
	            (c >= 0x0BBE && c <= 0x0BC2) || (c >= 0x0BC6 && c <= 0x0BC8) || 
	            (c >= 0x0BCA && c <= 0x0BCD) || c == 0x0BD7 || (c >= 0x0C01 && c <= 0x0C03) || 
	            (c >= 0x0C3E && c <= 0x0C44) || (c >= 0x0C46 && c <= 0x0C48) || 
	            (c >= 0x0C4A && c <= 0x0C4D) || (c >= 0x0C55 && c <= 0x0C56) || 
	            (c >= 0x0C82 && c <= 0x0C83) || (c >= 0x0CBE && c <= 0x0CC4) || 
	            (c >= 0x0CC6 && c <= 0x0CC8) || (c >= 0x0CCA && c <= 0x0CCD) || 
	            (c >= 0x0CD5 && c <= 0x0CD6) || (c >= 0x0D02 && c <= 0x0D03) || 
	            (c >= 0x0D3E && c <= 0x0D43) || (c >= 0x0D46 && c <= 0x0D48) || 
	            (c >= 0x0D4A && c <= 0x0D4D) || c == 0x0D57 || c == 0x0E31 || 
	            (c >= 0x0E34 && c <= 0x0E3A) || (c >= 0x0E47 && c <= 0x0E4E) || c == 0x0EB1 || 
	            (c >= 0x0EB4 && c <= 0x0EB9) || (c >= 0x0EBB && c <= 0x0EBC) || 
	            (c >= 0x0EC8 && c <= 0x0ECD) || (c >= 0x0F18 && c <= 0x0F19) || c == 0x0F35 || 
	             c == 0x0F37 || c == 0x0F39 || c == 0x0F3E || c == 0x0F3F || 
	            (c >= 0x0F71 && c <= 0x0F84) || (c >= 0x0F86 && c <= 0x0F8B) || 
	            (c >= 0x0F90 && c <= 0x0F95) || c == 0x0F97 || (c >= 0x0F99 && c <= 0x0FAD) || 
	            (c >= 0x0FB1 && c <= 0x0FB7) || c == 0x0FB9 || (c >= 0x20D0 && c <= 0x20DC) || 
	             c == 0x20E1 || (c >= 0x302A && c <= 0x302F) || c == 0x3099 || c == 0x309A) {
	        itIs = true;
	    }
	                                
	    return itIs;
	}
	
	/**
	 * Determines if a character is a digit.
	 * 
	 * [88]    Digit    ::=    [#x0030-#x0039] | [#x0660-#x0669] | [#x06F0-#x06F9] | 
	 * 			[#x0966-#x096F] | [#x09E6-#x09EF] | [#x0A66-#x0A6F] | [#x0AE6-#x0AEF] | 
	 * 			[#x0B66-#x0B6F] | [#x0BE7-#x0BEF] | [#x0C66-#x0C6F] | [#x0CE6-#x0CEF] | 
	 * 			[#x0D66-#x0D6F] | [#x0E50-#x0E59] | [#x0ED0-#x0ED9] | [#x0F20-#x0F29]
	 * 
	 * @param c character to test
	 * @return whether or not the character is a digit
	 */
	private static boolean isDigit(char c) {
	    boolean itIs = false;
	    
	    if ((c >= 0x0030 && c <= 0x0039) || (c >= 0x0660 && c <= 0x0669) || 
	            (c >= 0x06F0 && c <= 0x06F9) || (c >= 0x0966 && c <= 0x096F) || 
	            (c >= 0x09E6 && c <= 0x09EF) || (c >= 0x0A66 && c <= 0x0A6F) || 
	            (c >= 0x0AE6 && c <= 0x0AEF) || (c >= 0x0B66 && c <= 0x0B6F) || 
	            (c >= 0x0BE7 && c <= 0x0BEF) || (c >= 0x0C66 && c <= 0x0C6F) || 
	            (c >= 0x0CE6 && c <= 0x0CEF) || (c >= 0x0D66 && c <= 0x0D6F) || 
	            (c >= 0x0E50 && c <= 0x0E59) || (c >= 0x0ED0 && c <= 0x0ED9) || 
	            (c >= 0x0F20 && c <= 0x0F29)) { 
	        itIs = true;
    	}
	    return itIs;
	}
	
	/**
	 * Determines if a character is an extender character.
	 * 
	 * [89]    Extender    ::=    #x00B7 | #x02D0 | #x02D1 | #x0387 | #x0640 | #x0E46 | 
	 * 			#x0EC6 | #x3005 | [#x3031-#x3035] | [#x309D-#x309E] | [#x30FC-#x30FE]
	 * 
	 * @param c character to test
	 * @return whether or not the character is an extender character
	 */
	private static boolean isExtender(char c) {
	    boolean itIs = false;
	    
	    if (c == 0x00B7 || c == 0x02D0 || c == 0x02D1 || c == 0x0387 ||
	            c == 0x0640 || c == 0x0E46 || c == 0x0EC6 || c == 0x3005 ||
	            (c >= 0x3031 && c <= 0x3035) || (c >= 0x309D && c <= 0x309E) ||
	            (c >= 0x30FC && c <= 0x30FE)){
	        itIs = true;
	    }
	    
	    return itIs;
	}
	
	/**
	 * Determines if a character is an ideographic character.
	 * 
	 *  [86]    Ideographic    ::=    [#x4E00-#x9FA5] | #x3007 | [#x3021-#x3029] 
	 * 
	 * @param c character to test
	 * @return whether or not the character is an ideographic character
	 */
	private static boolean isIdeographic(char c) {
	    boolean itIs = false;
	    
	    if ((c >= 0x4E00 && c <= 0x9FA5) ||
	    	c == 0x3007 || 
	    	(c >= 0x3021 && c <= 0x3029)) {
	        itIs = true;
	    }
	                                
	    return itIs;
	}
	
	/**
	 * Determines if a character is a letter.
	 * 
	 * [84]    Letter    ::=    BaseChar | Ideographic
	 * 
	 * @param c character to test
	 * @return whether or not it is a letter
	 */
	private static boolean isLetter(char c) {
	    return (isBaseChar(c) || isIdeographic(c));
	}

	/**
	 * Performs checks to determine if the specified
	 * string conforms to the 'Name' construct.
	 * 
	 * [5]    Name    ::=    (Letter | '_' | ':') (NameChar)*
	 * 
	 * @param name The string to check
	 * @return whether or not the string conforms 
	 * 		to the 'Name' construct
	 */
	public static boolean isName(String name) {
	    
	    if (name == null || name.length() < 1){
	        return false;
	    }
		
	    // check first character
		char c = name.charAt(0);
		if( !isNameFirstChar(c)) {
			return false;
		}
		
		// check rest of name
		for(int i = 1; i < name.length(); i++) {
			c = name.charAt(i);
			if(!isNameChar(c)) {
				return false; 
			}
		}
		
		return true;
	}
	
	/**
	 * Determines if a character is a name character.
	 * 
	 * [4]    NameChar    ::=    Letter | Digit | '.' | '-' | '_' | ':' | 
	 * 					CombiningChar | Extender 
	 *  
	 * @param c character to test
	 * @return whether or not it is a name character
	 */	
	private static boolean isNameChar(char c) {
		return (isLetter(c) || isDigit(c) ||
			c == '.' || c == '-' || c == '_' || c == ':' ||
			isCombiningChar(c) || isExtender(c));
	}
	
	/**
	 * Checks if the specified char is a valid
	 * first char for the Name production in 
	 * the XML Specification.
	 * 
	 * [5]    Name    ::=    (Letter | '_' | ':') (NameChar)*
	 * 
	 * @param c character to check
	 * 
	 * @return whether or not this char is a 
	 * 		valid first char for a Name
	 */
	private static boolean isNameFirstChar(char c) {
		return (isLetter(c) ||  c == '_' || c == ':');
	}
	
	/**
	 * Checks if the character follows the PubidChar construct.
	 * 
	 * [13]    PubidChar    ::=    #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
	 * 
	 * @param c The character to check
	 * @return whether or not the string conforms 
	 * 		to the 'PubidChar' construct
	 */
	public static boolean isPubidChar(char c) {
		return (VALID_PUBID_CHAR.indexOf(c) != -1);
	}
	
	/**
	 * Checks if the specified string follows the PubidLiteral construct.
	 * 
	 * [12]    PubidLiteral    ::=    '"' PubidChar* '"' | "'" (PubidChar - "'")* "'" 
	 * 
	 * @param pubId The string to check
	 * @return whether or not the String is a valid PublidLiteral
	 */
	public static boolean isPubidLiteral(String pubId) {
		if(pubId == null) {
			return false;
		}
		
		for(int i = 0; i < pubId.length(); i++) {
			if(!DTDTextParser.isPubidChar(pubId.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the char specified is 
	 * a quotation mark - either single (')
	 * or double (").
	 * 
	 * @param c The character to check
	 * @return whether or not the character is a quote character
	 */
	public static boolean isQuotChar(char c) {
		return (c == '\'' || c == '\"');
	}

    /**
     * Check if the given char is a whitespace. Whitespace is 
     * defined as the space, tab, linefeed and carriage return
     * characters.
     * 
     * [3]   S    ::=    (#x20 | #x9 | #xD | #xA)+ 
     * 
     * @param c The character to be checked.
     * 
     * @return <code>true</code> if the char is a whitespace char; 
     * if not returns <code>false</code>.
     */
    public static boolean isWhiteSpace(char c) {
        boolean isWS = false;
    
    	if(c == ' ' || c == '\t' || c == '\n' || c == '\r')
    		isWS = true;
    
    	return isWS;
    }
	
	/**
	 * DTDData constructor.
	 * 
	 * @param strData The data to be parsed.
	 */
	public DTDTextParser(String strData) {
		super(strData);
	}
	
	/**
	 * Reads the Name from the current location.
	 * If no valid Name is present, it returns
	 * <code>null</code>.
	 * 
	 * @return The Name read from the current 
	 * 		location; <code>null</code> if a valid
	 * 		Name is not present at the current
	 * 		location. 
	 * @throws EOFException
	 */
	public String getNextName() throws EOFException {
		
		if (endOfData() || !isNameFirstChar(peekAtNextChar())) {
			return null; 
		}
		
		StringBuffer name = new StringBuffer("");
		
		name.append(getNextChar()); //append the first char
		getNextNmToken(name);
		
		return name.toString();
	}
	

	
	/**
	 * Reads the NmToken from the current location.
	 * If no valid nmToken is present, it returns
	 * <code>null</code>.
	 * 
	 * [7]    Nmtoken    ::=    (NameChar)+ 
	 * 
	 * @return The NmToken read from the current 
	 * 		location; <code>null</code> if a valid
	 * 		NmToken is not present at the current
	 * 		location.
	 * @throws EOFException
	 */
	public String getNextNmToken() throws EOFException {

		StringBuffer nmToken = new StringBuffer(""); 
		getNextNmToken(nmToken);
		
		if (nmToken.length() == 0) {
			return null;
		}
		
		return nmToken.toString();
	}
	
	/**
	 * Reads the NmToken at the current location
	 * and appends it to the StringBuffer. 
	 * 
	 * If there are no valid chars at the current
	 * location, then nothing is appended to the
	 * buffer.
	 * 
	 * @param buffer The StringBuffer to which all
	 * 			read chars are appended to.
	 * @throws EOFException
	 */
	private void getNextNmToken(StringBuffer buffer) throws EOFException {
		
		char nextChar;
		
		while (!endOfData()) {
			nextChar = peekAtNextChar();
			if (isNameChar(nextChar)) {
				buffer.append(nextChar);
				getNextChar(); //read it
			} else {
				break;
			}
		}
		return;
	}
	
	/**
	 * Read the conditional section from the data stream and check its syntax.
	 * This should not write out any data.
	 * 
	 * Assumption:
	 * the first two characters of the conditional section have already been read
	 * So instead of getting <![INCLUDE ... we'll get [INCLUDE...
	 * 
	 * [61] conditionalSect ::= includeSect | ignoreSect
	 * 
	 * [62] includeSect ::= '<![' S? 'INCLUDE' S? '[' extSubsetDecl ']]>'
	 * [63] ignoreSect ::= '<![' S? 'IGNORE' S? '[' ignoreSectContents* ']]>'
	 * [64] ignoreSectContents ::= Ignore ('<![' ignoreSectContents ']]>' Ignore)*
	 * [65] Ignore ::= Char* - (Char* ('<![' | ']]>') Char*)
	 * 
	 * So the string to this method will look like:
	 * '[' ('INCLUDE' | 'IGNORE') S? '['
	 * ('<!' char* )* '>' | S | conditionalSection | '%' char* ';')*
	 * ']]>'
	 * 
	 * 
	 * @param parser DTD parser
	 * @return The conditional section.
	 * @throws DTDSyntaxException
	 * @throws EOFException
	 */
	public DTDTextParser validateConditionalSect(DTDParser parser) 
		throws DTDSyntaxException, EOFException {
		
		int nesting = 1; // start within outer-most conditional statement
		boolean inCdata = false; // within a CDATA section
		boolean inComment = false; // within a comment
		boolean inString = false; // within a String
		char quoteType = 0; // surrounding quote character
		boolean inIgnore = false;
		StringBuffer sb = new StringBuffer();
		
		// [
		char nextChar = getNextChar();
		if (nextChar != '['){
		    throw new DTDSyntaxException("Expected '['");
		}
		sb.append(nextChar);
		
		// S?
		nextChar = peekAtNextChar();
		while (DTDTextParser.isWhiteSpace(nextChar)){
		    sb.append(getNextChar());
		    nextChar = peekAtNextChar();
		}
		
		// IGNORE | INCLUDE
		String condType = null;
		if (peekAtNextChar() == '%'){
		    // parameter entity
		    sb.append(getNextChar()); // %
		    String entName = getAllSkipping(';');
		    sb.append(entName + ";");
		    Entity ent = parser.getDtdSubset().getParamEntity(entName);
		    if (ent == null) {
		        throw new DTDSyntaxException("Undeclared entity");
		    }
		    condType = ent.getLiteralValue();
		    if (condType == null || 
		            (!condType.equals("IGNORE") && !condType.equals("INCLUDE"))){
		        throw new DTDSyntaxException("Expected entity to resolve to IGNORE or INCLUDE");
		    }
		    if (condType.equals("IGNORE")){
		        inIgnore = true;
		    }
		} else if (nextStringEquals("IGNORE")){
		    inIgnore = true;
		    sb.append(getChars("IGNORE".length()));
		} else if (nextStringEquals("INCLUDE")) {
		    sb.append(getChars("INCLUDE".length()));
		} else {
		    throw new DTDSyntaxException("Expected IGNORE or INCLUDE");
		}
		
		// S?
		nextChar = peekAtNextChar();
		while (DTDTextParser.isWhiteSpace(nextChar)){
		    sb.append(getNextChar());
		    nextChar = peekAtNextChar();
		}
		
		// [
		nextChar = getNextChar();
		if (nextChar != '['){
		    throw new DTDSyntaxException("Expected '['");
		}
		sb.append(nextChar);
		
		while (!endOfData()) {
		    if (inIgnore && nextStringEquals("<![CDATA[") && !inComment && !inString){
		        // starting a CDATA section
		        inCdata = true;
		        sb.append(getChars("<![CDATA[".length()));
		    } else if (nextStringEquals("]]>") && !inComment && !inString && !inCdata){
		        // ending a conditional section
		        nesting--;
		        sb.append(getChars("]]>".length()));
		        if (nesting == 0){
		            break;
		        }
		    } else if (nextStringEquals("]]>") && !inComment && !inString && inCdata){
		        // ending a CDATA section
		        inCdata = false;
		        sb.append(getChars("]]>".length()));
		    } else if (nextStringEquals("<![") && !inComment && !inString){
		        nesting++;
		        sb.append(getChars("<![".length()));
		    } else if (nextStringEquals("<!--") && !inString){
		        inComment = true;
		        sb.append(getChars("<!--".length()));
		    } else if (inComment && nextStringEquals("-->")){
		        inComment = false;
		        sb.append(getChars("-->".length()));
		    } else {
		        nextChar = getNextChar();
		        if (!inString && !inComment && (nextChar == '\"' || nextChar == '\'')){
		            inString = true;
		            quoteType = nextChar;
		        } else if (inString && 
		                ((nextChar == '\"' && quoteType == '\"') ||
		                (nextChar == '\'' && quoteType == '\'')) && 
		                peekAtPrevChar() != '\\'){
		            inString = false;
		        }
		        sb.append(nextChar);
		    }
		}
		
		if (nesting != 0){
		    throw new DTDSyntaxException("Improper nesting");
		}
		
		
		return new DTDTextParser(sb.toString());
	}
	
	/*
	public DTDData readConditionalSect() throws EOFException {
		
		String content = "";
		DTDData conditionalSect;
		char ch;
		int nesting = 1;
		
		while (!endOfData()) {
			ch = getNextChar();
			if (ch == '<') {
				content += '<';
				if (peekAtNextChar() == '!') {
					content += '!';
					getNextChar();
					if (peekAtNextChar() == '[') { //nested conditional section
						content += '[';
						getNextChar(); //read the '['
						nesting++;
					}
				} // else throw DTDException
			} else if (ch == ']') {
				content += ']';
				if (peekAtNextChar() == ']') {
					content += ']';
					getNextChar();
					if (peekAtNextChar() == '>') { //nested conditional section
						content += '>';
						getNextChar();
						nesting--;
						if (nesting == 0) {
							break;
						}
					} // else throw DTDException
				} // else throw DTDException
			} else {
			    content += ch;
			}
		}
	
		//CR: TODO: Throw DTDException if nesting != 0
		
		conditionalSect = new DTDData(content);
		
		return conditionalSect;
	}
	*/
}
