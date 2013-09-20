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

import java.lang.reflect.Field;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * BoxType represents the known types of boxes that can 
 * be found in a JPEG 2000 file, and how they are recognized.
 * Note: all the members of this class that are box
 * types must start with 'TYPE_' for the benefit of
 * the <code>isValidType</code> method.
 *
 * @author Andrea Goethals, FCLA
 */
public class BoxType {
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 * Can be present in: JP2, JPX
	 */
	private static String CLASSNAME = 
		"edu.fcla.daitss.format.image.jpeg2000.BoxType";
	
	/**
	 * Association box.
	 * This box allows several other boxes (i.e., boxes
	 * containing metadata) to be grouped together and referenced
	 * as a single entity.
	 * Can be present in: JPX
	 */
	public static final int TYPE_ASOC = 0x61736F63;
	
	/**
	 * Binary Filter box.
	 * This box contains data that has been transformed as 
	 * part of the storage process (such as compressed or
	 * encrypted).
	 * Can be present in: JPX
	 */
	public static final int TYPE_BFIL = 0x6266696C;
	
	/**
	 * Bits Per Component box.
	 * This box specifies the bit depth of the components in the
	 * file in cases where the bit depth is not consistent across
	 * all components.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_BPCC = 0x62706363;
	
	/**
	 * Channel Definition box.
	 * This box specifies the type and ordering of the
	 * components within the codestream, as well as those
	 * created by the application of a palette.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_CDEF = 0x63646566;
	
	/**
	 * Colour Group box.
	 * This box groups a sequence of Colour Specification
	 * boxes that specify the different ways that a colourspace
	 * of a layer can be processed.
	 * Can be present in: JPX
	 */
	public static final int TYPE_CGRP = 0x63677270;
	
	/**
	 * Digital Signature box.
	 * This box contains a checksum or digital signature for
	 * a poriotn of the JPX file.
	 * Can be present in: JPX
	 */
	public static final int TYPE_CHCK = 0x6368636B;
	
	/**
	 * Component Mapping box.
	 * This box specifies the mapping between a palette
	 * and codestream components.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_CMAP = 0x636D6170;
	
	/**
	 * Colour Specification box.
	 * This box specifies one way in which the colourspace
	 * of an image can be processed.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_COLR = 0x636F6C72;
	
	/**
	 * Composition box.
	 * This box specifies how a set of compositing layers
	 * shall be combined to create the rendered result.
	 * Can be present in: JPX
	 */
	public static final int TYPE_COMP = 0x636F6D70;
	
	/**
	 * Composition Options box.
	 * This box specifies generic options for the
	 * composition of multiple compositing layers.
	 * Can be present in: JPX
	 */
	public static final int TYPE_COPT = 0x636F7074;
	
	/**
	 * Cross-Reference box.
	 * This box specifies that a box found in another
	 * location (either within the JPX file or within another
	 * file) should be considered as if it was directly 
	 * contained at this location in the JPX file.
	 * Can be present in: JPX
	 */
	public static final int TYPE_CREF = 0x63726566;
	
	/**
	 * Codestream Registration box.
	 * This box specifies the alignment between the set of
	 * codestreams that make up one compositing layer.
	 * Can be present in: JPX
	 */
	public static final int TYPE_CREG = 0x63726567;
	
	/**
	 * Desired Reproductions box.
	 * This box specifies a set of transformations that
	 * must be applied to the image to guarantee a specific
	 * desired reproduction on a set of specific output
	 * devices.
	 * Can be present in: JPX
	 */
	public static final int TYPE_DREP = 0x64726570;
	
	/**
	 * Data Reference box.
	 * This box contains a set of pointers to other files or
	 * data streams not contained within the JPX file itself
	 * Can be present in: JPX
	 */
	public static final int TYPE_DTBL = 0x6474626C;
	
	/**
	 * Fragment List box.
	 * This box specifies a list of fragments that make up one
	 * particular codestream within this JPX file.
	 * Can be present in: JPX
	 */
	public static final int TYPE_FLST = 0x666C7374;
	
	/**
	 * Free box.
	 * This box contains data that is no longer used and may
	 * be overwritten when the fiel is updated.
	 * Can be present in: JPX
	 */
	public static final int TYPE_FREE = 0x66726565;
	
	/**
	 * Fragment Table box.
	 * This box specifies how one particular codestream
	 * has been fragmented and stored within this JPX file or
	 * in other streams.
	 * Can be present in: JPX
	 */
	public static final int TYPE_FTBL = 0x6674626C;
	
	/**
	 * File Type box.
	 * This box specifies file type, version and compatibility 
	 * information, including specifying if this file is a conforming
	 * JP2 file or if it can be read by a conforming JP2 reader.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_FTYP = 0x66747970;
	
	/**
	 * Graphics Technology Standard Output box.
	 * This box specifies the desired reproduction of the
	 * rendered result for commercial printing and proofing
	 * systems.
	 * Can be present in: JPX
	 */
	public static final int TYPE_GTSO = 0x6774736F;
	
	/**
	 * Image Header box.
	 * This box contains the size of the image and other related fields.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_IHDR = 0x69686472;
	
	/**
	 * Instruction Set box.
	 * This box specifies the specific instructions for
	 * combining multiple compositing layers to create
	 * the rendered result.
	 * Can be present in: JPX
	 */
	public static final int TYPE_INST = 0x696E7374;
	
	/**
	 * JPEG 2000 Signature box
	 * This box uniquely identifies the file as a JPEG 2000 file.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_JP00 = 0x6A502020;
	
	/**
	 * Contiguous Codestream box.
	 * This box contains the codestream as defined by the
	 * JPEG 2000 spec (part 1, annex A), stored contiguously
	 * in a single box.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_JP2C = 0x6A703263;
	
	/**
	 * JP2 Header box.
	 * This box contains a series of boxes that contain header-type
	 * information.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_JP2H = 0x6A703268;
	
	/**
	 * Intellectual Property box.
	 * This box contains intellectual property rights information
	 * about the image.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_JP2I = 0x6A703269;
	
	/**
	 * Codestream header box.
	 * This box specifies general information, such as
	 * bit depth, height and width about one specific
	 * codestream in the file.
	 * Can be present in: JPX
	 */
	public static final int TYPE_JPCH = 0x6A706368;
	
	/**
	 * Compositing Layer Header box.
	 * This box specifies general information, such as
	 * colourspace and resolution about one specific
	 * composoting layer in the file.
	 * Can be present in: JPX
	 */
	public static final int TYPE_JPLH = 0x6A706C68;
	
	/**
	 * Label box.
	 * This box specifies a textual label for either a
	 * Codestream Header, Compositing Layer Header, or
	 * Association box.
	 * Can be present in: JPX
	 */
	public static final int TYPE_LBL0 = 0x6C626C20;
	
	/**
	 * Media Data box.
	 * This box contains generic media data, which is 
	 * referenced through the Fragment Table box.
	 * Can be present in: JPX
	 */
	public static final int TYPE_MDAT = 0x6D646174;
	
	/**
	 * MPEG-7 Binary box.
	 * This box contains metadata in MPEG-7 binary format 
	 * (BiM) as defined by ISO/IEC 15938.
	 * Can be present in: JPX
	 */
	public static final int TYPE_MP7B = 0x6D703762;
	
	/**
	 * Number List box.
	 * This box specifies what entities are associated
	 * with the data contained within an Association box.
	 * Can be present in: JPX
	 */
	public static final int TYPE_NLST = 0x6E6C7374;
	
	/**
	 * Opacity box.
	 * This box specifies how opacity information is
	 * contained within a set of channels.
	 * Can be present in: JPX
	 */
	public static final int TYPE_OPCT = 0x6F706374;
	
	/**
	 * Palette box.
	 * This box specifies the palette which maps a single component
	 * in index space to a multiple component image.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_PCLR = 0x70636C72;
	
	/**
	 * Resolution box.
	 * This box specifies the resolution of the image.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_RES0 = 0x72657320;
	
	/**
	 * Capture Resolution box.
	 * This box specifies the resolution at which the image
	 * was captured.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_RESC = 0x72657363;
	
	/**
	 * Default Display Resolution box.
	 * This box specifies the default resolution at which the image
	 * should be displayed.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_RESD = 0x72657364;
	
	/**
	 * ROI Description box.
	 * This box specifies information about specific regions
	 * of interest in the image.
	 * Can be present in: JPX
	 */
	public static final int TYPE_ROID = 0x726F6964;
	
	/**
	 * Reader Requirements box type.
	 * This box specifies the different modes in which this
	 * file may be processed.
	 * Can be present in: JPX
	 */
	public static final int TYPE_RREQ = 0x72726571;
	
	/**
	 * UUID Info box.
	 * This box provides a tool by which a vendor may provide
	 * access to additional information associated with a UUID.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_UINF = 0x75696E66;
	
	/**
	 * UUID List box.
	 * This box specifies a list of UUIDs.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_ULST = 0x75637374;
	
	/**
	 * URL box.
	 * This box specifies a URL.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_URL0 = 0x75726C20;
	
	/**
	 * UUID box.
	 * This box provides a tool by which vendors can add additional
	 * information to a file without risking conflict with other 
	 * vendors.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_UUID = 0x75756964;
	
	/**
	 * XML box.
	 * This box provides a tool by which vendors can add XML
	 * formatted information.
	 * Can be present in: JP2, JPX
	 */
	public static final int TYPE_XML0 = 0x786D6C20;
	
	/**
	 * Determines whether or not a box type is described
	 * in the JPEG 2000 spec.
	 * 
	 * @param _type box type
	 * @return whether or not a box type is described
	 * 		in the JPEG 2000 spec
	 * @throws FatalException
	 */
	public static boolean isValidType(int _type) throws FatalException {
		boolean isType = false, foundIt = false;
		
		String methodName = "isValidType(int)";
		
		// dynamically make a list of all the types listed
		// in this class
		Field[] fields = new BoxType().getClass().getFields();
		int i= 0;
		while (!foundIt && i<fields.length) {
			try {
				// only want to consider the members that start with "TYPE_"
				if (fields[i].getName().startsWith("TYPE_") && 
					fields[i].getInt(fields[i]) == _type){
					foundIt = true;
					isType = true;
				}
				i++;
			} catch (IllegalAccessException e) {
				Informer.getInstance().fail(CLASSNAME,
						methodName,
						"Illegal access",
						"field",
						new FatalException(e.getMessage()));
			}
		}
		
		return isType;
	}
	
	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		System.out.println("Is valid type: " + isValidType(BoxType.TYPE_JP2H));
	}

}
