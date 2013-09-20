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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.Iterator;

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.util.ByteDecoder;
import edu.fcla.daitss.util.ByteReader;
import edu.fcla.daitss.util.FileUtil;

public class DataReference {
	public static final int DATA_REFERENCE_HEADER_SIZE = 12;
	
	int 		dfSize;
	String 		dfType;
	byte[]		version = new byte[1];
	byte[] 		flags = new byte[3];
	String 		dfData;
	
	/**
	 * construct the data reference object by reading the information from a file
	 * @param _reader
	 * @throws FatalException
	 */
	DataReference( ByteReader _reader) 	throws FatalException {
	    byte[] buffer = new byte[4];
	    
		// read in the size of the data reference
		dfSize = (int) _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		
		// read in the type of the data reference
		_reader.read(buffer);
		dfType = new String(buffer);
		
		// read in version(1), flags(3)
		_reader.read(version);
		_reader.read(flags);
		
		// deduct the dfSize(4), dfType(4), version(1) and flag(3) from the dfSize
	    byte[] data = new byte[(int) dfSize-DATA_REFERENCE_HEADER_SIZE];
	    _reader.read(data);
	    dfData = new String(data);
	}
	
	/**
	 * construct a data reference object by decoding the information from a byte-buffer.
	 * @param _reader
	 * @throws FatalException
	 */
	DataReference( ByteDecoder _reader) 	throws FatalException {
	    byte[] buffer = new byte[4];
	    
		// read in the size of the data reference
		dfSize = (int) _reader.readBytes(4, DataFile.BYTE_ORDER_BE);
		
		// read in the type of the data reference
		_reader.read(buffer);
		dfType = new String(buffer);
		
		// read in version(1), flags(3)
		_reader.read(version);
		_reader.read(flags);
		
		// deduct the dfSize(4), dfType(4), version(1) and flag(3) from the dfSize
	    byte[] data = new byte[(int) dfSize-DATA_REFERENCE_HEADER_SIZE];
	    _reader.read(data);
	    dfData = new String(data);
	}
	
	/**
	 * process the URL information in the data reference.  
	 * @param qtFile
	 * @throws FatalException
	 */
	void processURL(QuickTime qtFile) 	throws FatalException {
	    if (dfType.equals("url ") && dfData.length() != 0) {
	    	try {
	            /* testing only
	    		if (dfData.length() == 0)
	    			dfData = "HFSup.mov";
	    		*/
	    		// create and add the link to data file
	    		Link lk = new Link(qtFile.getIp().getWorkingPath(),qtFile.getPath(), dfData, 
	    			qtFile.getIp().getLinksDir(), qtFile.getOrigin(), qtFile.getOriginalUri());
				qtFile.addLink(lk);
				// only the relative path link will be retrieved.  Other types of links will
				// ignored as downloading those links (such as HTTP) could leads to copyright issue.
				if (lk.getLinkType() == Link.TYPE_REL_PATH)
					lk.setShouldRetrieve(true);
				else 
					lk.setShouldRetrieve(false);
				
	    	} catch (FatalException e) {
	       		throw e;
			} catch (URISyntaxException e) {
	       		throw new FatalException(e);
			}
	    }
	}
	
	/**
	 * localize the information in the data referenc
	 * @param _writer
	 * @param _reader
	 * @param qtFile
	 */
	void localize(QuickTime qtFile) 	throws FatalException {
	    if (dfType.equals("url ") && dfData.length() != 0) {
	    	/* testing only 
	    	 if (dfData.length() == 0)
	    		dfData = "HFSup.mov";
	    	*/
	    	// find the localized name (URI or filename) for the original URI/filename
	        for (Iterator iter = qtFile.getLinks().iterator(); iter.hasNext();) {
	            Link lk = (Link) iter.next();
	            // find the link
	            if (lk.getStatus().equals(Link.STATUS_SUCCESSFUL) && 
	            	lk.getLinkAlias().equals(dfData)) {	
        			// retrieve the datafile object for the associated link
            		DataFile df = qtFile.getDfFromLinkAlias(lk.getLinkAlias());
            		// df could be null, double check.    
            		if (df != null) {   
            			// get the name of the localized file/download URI
            			if (df.getLocalizedFilePath() != null)
            				dfData = FileUtil.getRelPathFrom(qtFile.getPath(), df.getLocalizedFilePath()); 
            			else
            				dfData = FileUtil.getRelPathFrom(qtFile.getPath(), df.getPath()); 
	                }
            		dfSize = DATA_REFERENCE_HEADER_SIZE + dfData.length();
            		break;
	            }
	        }
	    }
	}
	
	/**
	 * write the information in the data reference object to a file
	 * @param _writer
	 * @throws FatalException
	 */
	void write( RandomAccessFile _writer) 	throws FatalException {
		try {
		    _writer.writeInt((int) dfSize);
		    _writer.writeChars(dfType);
		    _writer.write(version);
		    _writer.write(flags);
		    _writer.writeChars(dfData);
		}  catch (IOException e) {throw new FatalException(e); }
	}
}