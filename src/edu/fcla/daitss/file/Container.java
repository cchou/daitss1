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

/** Java interface "Container.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package edu.fcla.daitss.file;

import java.util.*;

/**
 * <p>
 * 
 * @author Andrea Goethals
 * </p>
 */
public interface Container {

  ///////////////////////////////////////
  // operations 

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a DataFile with ...
 * </p><p>
 * @param dataFiles ...
 * </p>
 */
    public DataFile compose(Vector dataFiles);
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Vector with ...
 * </p>
 */
    public Vector decompose();

} // end Container





