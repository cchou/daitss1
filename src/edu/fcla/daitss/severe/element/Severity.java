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
 * Created on Nov 12, 2003
 *
 */
package edu.fcla.daitss.severe.element;

/**
 * Severity
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class Severity {
	
	/**
	 * Anomaly will be noted in the database and reported to the
	 * file's owner. The file will be archived. Preservation 
	 * level will be 'bit-level' for this file no matter what level
	 * of preservation was desired for the file.
	 */
	public static final String SEVERITY_BIT = "BIT";
	
	/**
	 * Anomaly will be noted in the database but not reported.
	 * The file will be archived. Desired preservation level will
	 * not be deprecated.
	 */
	public static final String SEVERITY_NOTE = "NOTE";
	
	/** 
	 * Package will be rejected because of this anomaly.
	 * Nothing will be noted in the database. Nothing in
	 * this package will be archived. The owner will be
	 * notified.
	 */
	public static final String SEVERITY_REJECT = "REJECT";
	
	/**
	 * Anomaly will be noted in the database and reported to the
	 * file's owner. The file will be archived. Desired preservation 
	 * level will not be deprecated.
	 */
	public static final String SEVERITY_REPORT = "REPORT";
	
	/**
	 * Unknown severity - only used for the initial value; is always overridden later.
	 */
	public static final String SEVERITY_UNKNOWN = "UNKNOWN";
	
	/**
	 * Determines whether a severity code name is valid (non-null and one of the severity
	 * code name constants stored in this class). Includes the "UNKNOWN" severity for
	 * better or for worse.
	 * 
	 * @param _severity	severity code name
	 * @return	<code>true</code> if the severity code name is valid,
	 * 	else <code>false</code>
	 */
	public static boolean isValidSeverity(String _severity){
		boolean result = false;
		if (_severity != null && (
				_severity.equals("BIT") || _severity.equals("NOTE") || _severity.equals("REJECT")
				|| _severity.equals("REPORT") || _severity.equals("UNKNOWN"))){
					result = true;
		}
		return result;
	}

}
