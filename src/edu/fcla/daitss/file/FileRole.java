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
package edu.fcla.daitss.file;

import java.lang.reflect.Field;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * Represents the primary role of a file in a package or its role 
 * related to other files in the package.
 * 
 * @author Andrea Goethals, FCLA
 */
public class FileRole {
	
	/**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.file.FileRole";

    /**
     * The role for generic files contained in a package (that is files that 
     * are not descriptors or schmemas.
     */
    public static final String ROLE_CONTENT_FILE = "CONTENT_FILE";
    
	/**
	 * An AIP (Archive Information Package) descriptor (the descriptor that was
	 * created by the archive at the time of ingest to describe the package being 
	 * archived.)
	 */
	public static final String ROLE_DESCRIPTOR_AIP = "DESCRIPTOR_AIP";
	
	/**
	 * A DIP (Disseminated Information Package) descriptor (the descriptor that was
	 * created by the archive at the time of dissemination to describe the package being 
	 * disseminated.)
	 */
	public static final String ROLE_DESCRIPTOR_DIP = "DESCRIPTOR_DIP";

	/**
	 * A GFP (Global File Package) descriptor (the descriptor that was
	 * created by the archive at the time of ingest to describe the package 
	 * containing all new GlobalFile objects being 
	 * archived.)
	 */
	public static final String ROLE_DESCRIPTOR_GFP = "DESCRIPTOR_GFP";	
	
	/**
	 * A SIP descriptor (the descriptor that was submitted in the original package
	 * or that was created by the archive at the time of ingest to describe what was 
	 * submitted in the original package.)
	 */
	public static final String ROLE_DESCRIPTOR_SIP = "DESCRIPTOR_SIP";

	/**
	 * A Schema Document
	 */
	public static final String ROLE_SCHEMA = "SCHEMA";

	/**
	 * Unknown role of the file within the package
	 */
	public static final String ROLE_UNKNOWN = "UNKNOWN";
	
	/**
     * Determines whether or not a file role code is valid.
     * If the argment is null or "" then false is returned
     * without throwing an exception.
     * 
     * @param role role code
     * @return whether or not the role code is valid
     * @throws FatalException
     */
    public static boolean isValidRole(String role) 
    	throws FatalException {
        boolean isValid = false, foundIt = false;
        
        String methodName = "isValidRole(String)";
        
        if (role == null || role.equals("")){
            return isValid; // false
        }
        
        // iterate through the constant members and see if
        // the argument matches one of them.
        
        Field[] fields = new FileRole().getClass().getFields();
		int i= 0;
		
		while (!foundIt && i<fields.length) {
			try {
				// only want to consider the members that start with "ROLE_"
				if (fields[i].getName().startsWith("ROLE_") && 
					((String)fields[i].get(fields[i])).equals(role)){
					foundIt = true;
					isValid = true;
				}
				i++;
			} catch (IllegalAccessException e) {
				Informer.getInstance().fail(CLASSNAME,
						methodName,
						"Illegal access",
						"field",
						new FatalException(e.getMessage()));
			}
		}
        
        return isValid;
    }
    
    /**
     * Test driver.
     * 
     * @param args not used
     * @throws FatalException
     */
    public static void main(String[] args) throws FatalException {
        System.out.println("Is valid? SCHEMA: " + isValidRole(ROLE_SCHEMA));
        System.out.println("Is valid? X: " + isValidRole("X"));
        System.out.println("Is valid? null: " + isValidRole(null));
        System.out.println("Is valid? \"\": " + isValidRole(""));
    }

} 
