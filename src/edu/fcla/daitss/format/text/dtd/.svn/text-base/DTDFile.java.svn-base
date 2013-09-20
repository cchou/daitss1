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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.fcla.daitss.util.FileUtil;


/**
 * Class to do the io operations on a File.
 *
 * @author Conrad Roche
 * @author Andrea Goethals, FCLA
 */
public class DTDFile extends DTDSource {

	/**
	 * The folder in which the dtd file is located.
	 */
	private String dir;
	
	/**
	 * The name of the dtd file.
	 */
	private String fileTitle;
	
	/**
	 * The path separator.
	 * This will be '/' for unix files
	 * and '\' for windows files.
	 */
	private String separator = "";
	
	/**
	 * DTDFile constructor.
	 * 
	 * @param filename The location of the DTD file.
	 * @throws FileNotFoundException
	 */
	public DTDFile(String filename) throws FileNotFoundException {
		
		if (filename == null)
			throw new FileNotFoundException("File is null");
		
		File f = new File(filename);
		
		if (!f.exists()){
			throw new FileNotFoundException("File " + filename + " doesn't exist.");
		}
		separator = File.separator;
		fileTitle = f.getName();
		dir = f.getParent();
	}

	/**
	 * Return the absolute path of the source
	 * using the path relative to the file being parsed.
	 *
	 *
	 * @param childRelPath The relative path of the source w.r.t. 
	 * 			the file being parsed.
	 * @param parentPath absolute path to parent file
	 * @return The absolute path of the source.
	 * @throws FileNotFoundException
	 */
	public static String getAbsolute(String childRelPath, String parentPath) 
		throws FileNotFoundException {
	    
	    String absPath = null;
	    
	    // check args
	    // parentPath must be absolute and exist
	    if (!FileUtil.isGoodFile(parentPath)){
	        throw new FileNotFoundException("Parent file doesn't exist: " + parentPath);
	    }
	    
	    // childRelPath is a relative path from parentPath
	    absPath = FileUtil.getAbsPathFromRelPath(parentPath, childRelPath);
	    
	    // make sure the absolute path exists
    	File childFile = new File(absPath);
    	if (!childFile.exists() || !childFile.isAbsolute()){
    	    throw new FileNotFoundException("Child file doesn't exist: " + absPath);
    	}
	    
	    return absPath;
	}

	/**
	 * Method to perform the read operation on this file source.
	 * 
	 * @return The DTDData object containing all the read data
	 * 			from the current source.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public DTDTextParser read() throws FileNotFoundException, IOException {
	
		if(fileTitle == null || fileTitle.equals(""))
			throw new FileNotFoundException();
	
		String location = "";
		
		if(dir != null) {
			location = dir + separator + fileTitle; 
		}
		try {
			// read the input file
			BufferedReader in = new BufferedReader( new InputStreamReader( new BufferedInputStream( 
									new FileInputStream(location) ) ) );
			
			String line = null;
			String dtdStr = "";
			
			while((line = in.readLine())!= null)
				dtdStr += line + "\n";
				
			in.close();
			
			return new DTDTextParser(dtdStr);
		}
		catch (FileNotFoundException e) {
			throw new FileNotFoundException("Specified DTD file (" + location + ") not found.");
		}
		catch (IOException e) {
			throw new IOException("IO Exception reading file " + location + ".");
		}
	}
}

