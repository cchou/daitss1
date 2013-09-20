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
package edu.fcla.daitss.prep;

import java.io.File;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.Informer;

/**
 * Creates a package descriptor in mets format if archive propery <code>GENERATE_DESCRIPTORS</code> is true and if no descriptor exists.
 * @author franco
 *
 */
public class DescriptorExistenceFilter extends Filter {

	private final boolean shouldCreateDescriptor;

    /**
     * @throws FatalException
     */
    public DescriptorExistenceFilter() {        
        shouldCreateDescriptor = Boolean.valueOf(System.getProperty("prep.make.descriptor")).booleanValue();
    }
   
    public void process(File pkg) throws PackageException, FatalException {

    	File descriptor = getDescriptor(pkg);
    	
    	if (!descriptor.exists()) {
			if (shouldCreateDescriptor) {
				createDescriptor(pkg);
			} else {
				Informer.getInstance().error(this,
						new Throwable().getStackTrace()[0].getMethodName(),
						"No package descriptor found.",
						"Checking if packages have a descriptor.",
						new PackageException());
			}
		}    	
    }
}
