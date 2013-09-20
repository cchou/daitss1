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
package edu.fcla.daitss.format.image.jpeg2000.box;

/**
 * The basic building block of a JPEG 2000 file.
 * 
 * @author Andrea Goethals, FCLA
 */
public class Box {
	
	/**
	 * Fixed Length in bytes of the LBox field.
	 * 
	 */
	public static final int FIXED_LBOX_LENGTH = 4;
	
	/**
	 * Fixed Length in bytes of the TBox field.
	 * 
	 */
	public static final int FIXED_TBOX_LENGTH = 4;
	
	/**
	 * Fixed length in bytes of the XLBox field when its present.
	 */
	public static final int FIXED_XLBOX_LENGTH = 8;
	
	/**
	 * Minimum length in bytes of a box.
	 * Calculated by FIXED_LBOX_LENGTH + FIXED_TBOX_LENGTH.
	 * The only way a box could be 8 bytes is the DBox was empty. 
	 */
	public static final int MIN_BOX_LENGTH = 8;
	
	/**
	 * The size of the length (LBox), type (TBox) and 
	 * extended length (XLBox) fields
	 */
	protected int boxHeaderLength;

	/**
	 * The size in bytes of the entire box.
	 * (boxHeaderlength + contentLength)
	 */
	protected long boxLength;
	
	/**
	 * Whether or not the box length was known when the 
	 * file was written. If the box length was not known 
	 * (indicated by a LBox value of 0) then the length of the
	 * box is the length of the rest of the file.
	 */
	public boolean boxLengthKnown = false;
	
	/**
	 * Length of the DBox field (the field containing the content).
	 * Equal to:
	 * Box length - (LBox size + TBox size + XLBox size)
	 */
	protected long contentLength;
    
    /**
     * Byte offset from the beginning of the file to 
     * the start of this box.
     */
    protected long fileOffset = 0;
	
	/**
	 * box length.
	 * value is 0,1, 8-(2^32 - 1)
	 * (values 2-7 are reserved)
	 * 
	 * if the value is 1 then the <code>extLength</code>
	 * contains the actual length
	 */
	protected int lboxValue;
	
	/**
	 * box type.
	 */
	protected int tboxValue;
	
	/**
	 * box length if the length was too big to fit
	 * in the <code>length</code>.
	 * value is 0,16-(2^64 - 1).
	 * when the <code>length</code> != 0
	 * then this value is 0;
	 * when the <code>length</code> == 1
	 * then this value != 0.
	 */
	protected long xlboxValue;
	
	/**
	 * Construct a box that inherits all the values
	 * of another box.
	 * 
	 * @param box
	 * @param type
	 */		
	public Box(Box box, int type) {
		this.boxHeaderLength = box.boxHeaderLength;
		this.boxLength = box.boxLength;
		this.boxLengthKnown = box.boxLengthKnown;
		this.contentLength = box.contentLength;
		this.xlboxValue = box.xlboxValue;
		this.lboxValue = box.lboxValue;
		this.tboxValue = type;
	}
	
	/**
	 * The constructor to use when the box length is 
	 * specified in the length field (LBox).
	 * In other words the XLBbox field is not present.
	 * 
	 * @param _lboxValue box length
	 * @param _tboxValue box type
	 */
	public Box(int _lboxValue, int _tboxValue){
		this.lboxValue = _lboxValue;
		this.tboxValue = _tboxValue;
		this.boxLengthKnown = true;
		
		this.boxHeaderLength = 
			FIXED_LBOX_LENGTH + FIXED_TBOX_LENGTH + 
			0;
		
		// subtract the size in bytes of the LBox,
		// TBox, and XLBox fields to get the DBox size
		this.contentLength = _lboxValue - this.boxHeaderLength;
		
		// total box length
		this.boxLength = this.boxHeaderLength + this.contentLength;
		
		//System.out.println("Setting content length to : " + this.contentLength);
	}
	
	/**
	 * The constructor to use when the box length is 
	 * specified in the extra length field (XLBox).
	 * The LBox value == 1.
	 * 
	 * @param _lboxValue box length
	 * @param _tboxValue box type
	 * @param _xlboxValue box extended length
	 */
	public Box(int _lboxValue, int _tboxValue, long _xlboxValue){
		this.lboxValue = _lboxValue;
		this.tboxValue = _tboxValue;
		this.xlboxValue = _xlboxValue;
		this.boxLengthKnown = true;
		
		this.boxHeaderLength = 
			FIXED_LBOX_LENGTH + FIXED_TBOX_LENGTH + 
			FIXED_XLBOX_LENGTH;
		
		// subtract the size in bytes for the LBox,
		// TBox, and XLBox fields to get the DBox size
		this.contentLength = _xlboxValue - this.boxHeaderLength;
		
		// total box length
		this.boxLength = this.boxHeaderLength + this.contentLength;
		
		//System.out.println("Setting content length to : " + this.contentLength);
	}
	
	/**
	 * Constructor to use when this LBox value == 0.
	 * This indicates that the length of the box was not known
	 * when the file was written.
	 * The length of the box is the rest of the file.
	 *  
	 * @param _restFileLength
	 * @param _lboxValue
	 * @param _tboxValue
	 */
	public Box(long _restFileLength, int _lboxValue, int _tboxValue){
		this.lboxValue = _lboxValue;
		this.tboxValue = _tboxValue;
		this.boxLengthKnown = false;
		
		// box header length
		this.boxHeaderLength = FIXED_LBOX_LENGTH + FIXED_TBOX_LENGTH +
			0;
		
		// length of the content
		this.contentLength = _restFileLength;
		
		// total box length
		this.boxLength = this.boxHeaderLength + this.contentLength;
	}
	
	/**
	 * @return Returns the boxHeaderLength.
	 */
	public int getBoxHeaderLength() {
		return boxHeaderLength;
	}
	
	/**
	 * @return Returns the boxLength.
	 */
	public long getBoxLength() {
		return boxLength;
	}
	
	/**
	 * @return Returns the contentLength.
	 */
	public long getContentLength() {
		return contentLength;
	}
	
	/**
	 * @return Returns the extLength.
	 */
	public long getExtLength() {
		return xlboxValue;
	}
	
	/**
     * @return Returns the fileOffset.
     */
    public long getFileOffset() {
        return fileOffset;
    }
	
	/**
	 * @return Returns the length.
	 */
	public int getLength() {
		return lboxValue;
	}
	
	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return tboxValue;
	}

    /**
     * @param fileOffset The fileOffset to set.
     */
    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }

    /**
	 * Decrement the bytes left to read.
	 * 
	 * @param bytesRead
	 */
	public void subtractFromBytesLeft(long bytesRead){
		this.contentLength -= bytesRead;
	}
}
