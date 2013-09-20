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
 * Created on Mar 16, 2004
 *
 */
package edu.fcla.daitss.file;

import java.lang.reflect.Field;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.VirusFoundException;

/**
 * ArchiveMessageDigest represents a message digest algorithm.
 * The configPolicies file is where the types of message digest algorithms
 * are specified that will be calculated on every ingested file. Look
 * for the properties MD_TYPE_*. Those properties
 * must be set to the values of the message digest algorithms constants
 * listed in this class (ex: md5, sha-1, etc.).
 * 
 * Note that there are restrictions in the database for how long
 * the message digest code (64 chars) and value (255 chars) can be.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class ArchiveMessageDigest {
	
	/**
	 * The MD5 message digest algorithm, 128 bits,  as defined in RFC 1321.
	 */
	public static final String ALG_MD5 = "md5";
	
	/**
	 * The Secure Hash Algorithm, 160 bits, as defined in Secure Hash Standard, 
	 * NIST FIPS 180-1.
	 */
	public static final String ALG_SHA1 = "sha-1";
	
	/**
	 * The Secure Hash Algorithm, 256 bits, as defined in Secure Hash Standard, 
	 * NIST FIPS 180-2.
	 */
	public static final String ALG_SHA256 = "sha-256";
	
	/**
	 * The Secure Hash Algorithm, 384 bits, as defined in Secure Hash Standard, 
	 * NIST FIPS 180-2.
	 */
	public static final String ALG_SHA384 = "sha-384";
	
	/**
	 * The Secure Hash Algorithm, 512 bits, as defined in Secure Hash Standard, 
	 * NIST FIPS 180-2.
	 */
	public static final String ALG_SHA512 = "sha-512";
	
	/**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.file.util.ArchiveMessageDigest";
	
	/**
     * Determines whether or not a message digest code is valid.
     * If the argment is null or "" then false is returned
     * without throwing an exception.
     * 
     * @param code message digest code
     * @return whether or not the code is valid
     * @throws FatalException
     */
    public static boolean isValidMdCode(String code) 
    	throws FatalException {
        boolean isValid = false, foundIt = false;
        
        String methodName = "isValidMdCode(String)";
        
        if (code == null || code.equals("")){
            return isValid; // false
        }
        
        // iterate through the constant members and see if
        // the argument matches one of them.
        
        Field[] fields = new ArchiveMessageDigest().getClass().getFields();
		int i= 0;
		
		while (!foundIt && i<fields.length) {
			try {
				// only want to consider the members that start with "ALG_"
				if (fields[i].getName().startsWith("ALG_") && 
					((String)fields[i].get(fields[i])).equals(code)){
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
	 * A coded value for the type of digest algorithm used to calculate this
	 * file's message digest.
	 */
	private String code = null;

	/**
	 * The agency first performing the digest calculation. For original objects, this
	 * will indicate whether a contributed data file had a pre-existing
	 * digest that was verified on ingest (indicating the object archived was
	 * the same as sent), or whether the initial digest was calculated by the
	 * archive at time of ingest. For normalized and migrated objects, this is
	 * not meaningful and will always indicate the archive. Valid origins
	 * include <code>DataFile.ORIG_ARCHIVE</code>, 
	 * <code>DataFile.ORIG_DEPOSITOR</code>, and
	 * <code>DataFile.ORIG_UNKNOWN</code>.
	 */
	private String origin = DataFile.ORIG_UNKNOWN;
	
	/**
	 * The digest value.
	 */
	private String value = null;
	
	/**
	 * Used in the <code>isValidMdCode</code> method.
	 */
	private ArchiveMessageDigest(){}
	
	/**
	 * Constructs an ArchiveMessageDigest with a digest code
	 * and value. Assumes it was the archive that calculated this
	 * message digest.
	 * 
	 * @param _code a message digest code
	 * @param _value a message digest value
	 * @throws FatalException
	 */
	public ArchiveMessageDigest(String _code, String _value) 
		throws PackageException, FatalException {
		
		if(_code.length() == 0) 
			_code = this.checkValueForCode(_value);
		
		
		this.setCode(_code);
		this.setValue(_value);
		this.setOrigin(DataFile.ORIG_ARCHIVE);
	}
	
	/**
	 * Constructs the ArchiveMessageDigest object setting the digest algorithm code,
	 * value and the origin of the first calculation of this digest
	 * value (potentially prior to the file's arrival in the archive).
	 * 
	 * @param _code	digest algorithm code
	 * @param _value	digest value
	 * @param _origin	digest calculation origin
	 * @throws FatalException
	 */
	public ArchiveMessageDigest(String _code, String _value, String _origin) 
		throws PackageException, FatalException {
		
		if(_code.length() == 0) 
			_code = this.checkValueForCode(_value);
		
		this.setCode(_code);
		this.setValue(_value);
		this.setOrigin(_origin);
	}
	 
	/**
	 * @return the digest algorithm code name
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * @return the digest calculation's origin
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * @return the digest value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Checks the digest value in an attempt to determine the digest algorithm.
	 * 
	 * @param _value The value of the digest to check
	 * 
	 * @throws FatalException
	 */
	private String checkValueForCode(String _value) throws PackageException, FatalException
	{
		String _code = "";
		if(_value.length() == 32) 
			_code = "md5";
		else if(_value.length() == 40) 
			_code = "sha-1";
		else 
			Informer.getInstance().error(
					this, 
					"checkValueForCode", 
					"Unable to determine digest code by value",
					"_value: " + _value,
					new PackageException("Cannot determine message digest code"));
		return _code;
	}
	
	/**
	 * Sets the code for the digest algorithm used to calculate the digest
	 * value.
	 * 
	 * @param _code	a code for a digest algorithm
	 * @throws FatalException
	 */
	public void setCode(String _code) 
		throws FatalException {
		if (isValidMdCode(_code)){
			this.code = _code;
		} else {
			Informer.getInstance().fail(
				this, "setCode(_code)", "Illegal argument",
				"_code: " + _code,
				new FatalException("Not a valid message digest code"));			
		}
	}
	
	/**
	 * Sets the digest calculation's origin.
	 * 
	 * @param _origin	the agency that first calculated this
	 * 						this file's digest
	 * @throws FatalException
	 */
	public void setOrigin(String _origin) throws FatalException {
		if (_origin == null ||
			(!_origin.equals(DataFile.ORIG_ARCHIVE) &&
			!_origin.equals(DataFile.ORIG_DEPOSITOR))) {
			Informer.getInstance().fail(
				this, "setOrigin(_origin)", "Illegal argument",
				"_origin: " + _origin,
				new FatalException("Not a valid origin"));
		}
		this.origin = _origin;
	}         
	
	/**
	 * Sets the digest value for this file. Does not check that the
	 * digest is the correct length but will check that it is not null.
	 * 
	 * @param _value	the digest value
	 * @throws FatalException
	 */
	public void setValue(String _value) throws FatalException {
		if (_value != null){
			this.value = _value;
		} else {
			Informer.getInstance().fail(
				this, "setValue(String)", "Illegal argument",
				"_value: null", 
				new FatalException("ArchiveMessageDigest value must be non-null")
			);
		}	
	}
	
	/**
	 * Prints out the members of this class.
	 * 
	 * @return the members of this class
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		
		sb.append("algorithm: " + this.getCode());
		sb.append (" value: " + this.getValue());
		return sb.toString();
	}

}
