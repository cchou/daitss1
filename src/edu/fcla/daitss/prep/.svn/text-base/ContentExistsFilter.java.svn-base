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
import java.io.FileFilter;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.Informer;

/**
 * Signaled when a package has no content files.
 * @author franco
 *
 */
public class ContentExistsFilter extends Filter {

    public void process(final File pkg) throws PackageException, FatalException {        
        int contentFileCount = pkg.listFiles(new FileFilter() {            
            public boolean accept(File file) {
                File descriptor = getDescriptor(pkg);
                return ! descriptor.equals(file);
            }            
        }).length;
        
        if(contentFileCount == 0) {
            Informer.getInstance().error(
            		this,
            		new Throwable().getStackTrace()[0].getMethodName(),
            		"No content found.",
            		"Checking if packages have content.",
            		new PackageException());
        }
    }
}
