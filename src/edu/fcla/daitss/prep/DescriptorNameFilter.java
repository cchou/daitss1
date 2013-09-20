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
import edu.fcla.daitss.util.Informer;

/**
 * Asserts a package's descriptor has a specific name
 * @author franco
 *
 */
public class DescriptorNameFilter extends Filter {

    final String origName;
    
    /**
     * Create a Filter that changes the name of a descriptor from <code>nane</code> to the proper convention.
     * @param name
     */
    public DescriptorNameFilter(String name) {
        origName = name;
    }
    
    public void process(File pkg) throws FatalException {
        File oldDescriptor = new File(pkg, origName);
        File newDescriptor = new File(pkg, pkg.getName() + ".xml");
        if (oldDescriptor.exists()) {
            oldDescriptor.renameTo(newDescriptor);
            StackTraceElement ste = new Throwable().getStackTrace()[0];
            Informer.getInstance().info(this, 
            		ste.getMethodName(), 
            		"Renaming descriptor: " + oldDescriptor.getPath() + " is now " + newDescriptor.getPath(), 
            		"Asserting Descriptor Names", 
            		true);
        }
    }
}
