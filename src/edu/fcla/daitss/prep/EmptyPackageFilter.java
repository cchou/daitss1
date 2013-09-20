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
 * Asserts a package is not empty.
 * @author franco
 *
 */
public class EmptyPackageFilter extends Filter {

    public void process(File pkg) throws PackageException, FatalException {
        if (pkg.listFiles().length == 0) {
            Informer.getInstance().error(
            		this,
            		new Throwable().getStackTrace()[0].getMethodName(),
            		"Package is empty.",
            		"Checking if packages are empty,",
            		new PackageException());
        }
    }

}
