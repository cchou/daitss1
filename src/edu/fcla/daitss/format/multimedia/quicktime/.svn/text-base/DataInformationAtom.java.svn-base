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
package edu.fcla.daitss.format.multimedia.quicktime;

import java.io.RandomAccessFile;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;

public class DataInformationAtom extends Atom {
	public static final int TYPE = 0x64696E66; 	// "dinf"

	/** 
	 * construct a Data Information Atom using the information in the basic atom object.
	 * @param _atom
	 */
	DataInformationAtom(Atom _atom) {
		super(_atom);
	}
	
	/**
	 * parse and extract Data Information metadata
	 * @param _reader
	 * @param qtFile
	 * @return
	 */
	void parse(ByteDecoder _reader, QuickTime qtFile) throws FatalException {
	long bytesRead = headerSize;
		
		// make sure there is enough room to read in the header of the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			if (childAtom.isType(DataReferenceAtoms.TYPE)) {
				DataReferenceAtoms drefAtom = new DataReferenceAtoms(childAtom);
				drefAtom.parse(_reader, qtFile);
			}  else {
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
		}
	}

	/**
	 * localize the content of the data information atom.
	 * @param _reader
	 * @param qtFile
	 * @throws FatalException
	 */
	public void localize(ByteReader _reader, QuickTime qtFile) throws FatalException {
		long bytesRead = headerSize;
		
		int newSize = headerSize;
		
		// make sure there is enough room to read in the header of the next child atom
		while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
			// prefetch the next atom.
			Atom childAtom = new Atom(_reader);
			if (childAtom.isType(DataReferenceAtoms.TYPE)) {
				DataReferenceAtoms drefAtom = new DataReferenceAtoms(childAtom);
				drefAtom.localize(_reader, qtFile);
			}  else {
				childAtom.skip(_reader);
			}
			bytesRead += childAtom.size;
			newSize += childAtom.size;
		}
		
		// recalculate the atom size after the child atoms have been localized.
		size = newSize;
	}
	
	/**
	 * write the information in the data information atom to a file
	 * @param _reader
	 * @param _writer
	 * @throws FatalException
	 */
	public void write(ByteReader _reader, RandomAccessFile _writer) 
	throws FatalException {
	long bytesRead = headerSize;
		
	super.writeHeader(_writer);
	
	// make sure there is enough room to read in the header of the next child atom
	while ((size - bytesRead) > Atom.MIN_HEADER_SIZE) {
		// prefetch the next atom.
		Atom childAtom = new Atom(_reader);
		if (childAtom.isType(DataReferenceAtoms.TYPE)) {
			DataReferenceAtoms drefAtom = new DataReferenceAtoms(childAtom);
			drefAtom.write(_writer);
		}  else {
			childAtom.copy(_reader, _writer);
		}
		bytesRead += childAtom.size;
	}
}
}
