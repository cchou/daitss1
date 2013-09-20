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
/**
 * 
 */
package edu.fcla.daitss.format.markup;

import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.distributed.Distributed;

/**
 * Represents a file in a markup language format.
 *  
 */
public abstract class MLFile extends DataFile implements Distributed {
	
	/**
	 * 
	 * @param path absolute path to the file
	 * @throws FatalException
	 */
	protected MLFile(String path) throws FatalException {
		this.setPath(path);
	}

	/**
	 * 
	 * @param path absolute path to the file
	 * @param ip the file's information package
	 * @throws FatalException
	 */
	public MLFile(String path, InformationPackage ip) throws FatalException {
		super(path, ip); 
		
	}
	
	/**
	 * 
	 * @param path absolute path to the file
	 * @param ip the file's information package
	 * @param _liteDataFile metadata about this file
	 * @throws FatalException
	 */
	public MLFile(String path, InformationPackage ip, DataFile _liteDataFile) 
		throws FatalException {
		super(path, ip); 
		
	}

} 
