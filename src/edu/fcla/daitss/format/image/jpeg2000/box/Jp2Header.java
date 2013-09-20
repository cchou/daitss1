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

/**
 * JP2 Header Box.
 * Some differences exist between the JP2 Header box
 * found in a JP2 vs a JPX, so this class is subclassed
 * for them.
 * 
 *
 * @author Andrea Goethals, FCLA
 */
public abstract class Jp2Header extends Box {
	
	/**
	 * box type value;
	 */
	public static final int TYPE = 0x6A703268;

	/**
     * Whether or not his box contains a Bits per Component box.
     */
    private boolean hasBpcBox = false;
    
    /**
     * Whether or not his box contains a Component Mapping box.
     */
    private boolean hasCmapBox = false;
    
    /**
	 * Whether or not this box needs to have a BitsPerComponentBox.
	 */
    private boolean needsBpcBox = false;
    
    /**
     * Whether or not this box needs to have a ComponentMapping box.
     * Needs to if the JP2 header box has a Palette box.
     */
    private boolean needsCmapBox = false;
	
	/**
	 * Number of color boxes in this file.
	 */
	int numColrBoxes = 0;
	
    /**
     * 
     * @param box
     */
	public Jp2Header(Box box) {
		super(box, Jp2Header.TYPE);
	}

	/**
     * @return Returns the hasBpcBox.
     */
    public boolean hasBpcBox() {
        return hasBpcBox;
    }

	/**
     * @return Returns the hasCmapBox.
     */
    public boolean hasCmapBox() {
        return hasCmapBox;
    }

    /**
	 * Return whether or not this box needs to have a BitsPerComponentBox.
	 * @return boolean
	 */
	public boolean needsBpcBox() {
		return needsBpcBox;
	}

    /**
     * @return Returns the needsCmapBox.
     */
    public boolean needsCmapBox() {
        return needsCmapBox;
    }

    /**
     * @param hasBpcBox The hasBpcBox to set.
     */
    public void setHasBpcBox(boolean hasBpcBox) {
        this.hasBpcBox = hasBpcBox;
    }

    /**
     * @param hasCmapBox The hasCmapBox to set.
     */
    public void setHasCmapBox(boolean hasCmapBox) {
        this.hasCmapBox = hasCmapBox;
    }

    /**
	 * Set whether or not this box needs to have a BitsPerComponentBox.
	 * @param needsBpcBox
	 */
	public void setNeedsBpcBox(boolean needsBpcBox) {
		this.needsBpcBox = needsBpcBox;
	}

    /**
     * @param needsCmapBox The needsCmapBox to set.
     */
    public void setNeedsCmapBox(boolean needsCmapBox) {
        this.needsCmapBox = needsCmapBox;
    }

}
