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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * Calculates message digests on individual files.
 * 
 * @author Andrea Goethals, FCLA
 */
public class MessageDigestCalculator {
	
	/**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = MessageDigestCalculator.class.getName();
	
	/**
	 * All the message digests calculated for this package.
	 */
	private static Hashtable digests = null;
	
	/**
	 * Calculates a message digest on a file.
	 * 
	 * @param absPath	absolute path to a file
	 * @param algorithm message digest algorithm
	 * @return the message digest object
	 * @throws FatalException
	 */
	private static ArchiveMessageDigest calcMessageDigest(String absPath, String algorithm) 
		throws FatalException, PackageException {
		String md = "";
		
		// get the class name to use in Informer calls
		String methodName = "calcMessageDigest(String, String)";
		
		if (absPath == null ||
			!ArchiveMessageDigest.isValidMdCode(algorithm)){
	    	Informer.getInstance().fail(
	    	        CLASSNAME, methodName,
	       		"Illegal argument", "filePath: " + absPath + " algorithm: " + algorithm,
	       		new FatalException("Bad arguments"));
		}
		
		Informer.getInstance().info(
		        CLASSNAME, methodName,
				"Beginning to calculate a " + algorithm + " message digest: " 
					+ DateTimeUtil.now(),
				"File: " + absPath,
				false);
	      
		try {
			MessageDigest msgDig = MessageDigest.getInstance(algorithm);
            InputStream is = new FileInputStream(absPath);                        
            
/*            DigestInputStream dis = new DigestInputStream(new FileInputStream(absPath),
					  msgDig);
			dis.on(true);
			
			// read in the file sending the bytes to the update() method
			while(dis.read() != -1);
*/			 
            byte[] buf = new byte[8 * 1024];        
            int read = 0;
            while ((read = is.read (buf)) > 0) {
                msgDig.update(buf, 0, read);
            }                        
            
			// calculate the message digest
			byte[] fileMD = msgDig.digest();
			 
			// convert the bytes to a hexadecimal String
			md = toHexString(fileMD);
			// cleanup
			is.close();
			is = null;
			 
		} catch (NoSuchAlgorithmException e) {
			Informer.getInstance().fail(
			        CLASSNAME, methodName,
				"Illegal argument", " algorithm: " + algorithm,
				new FatalException("Not a valid digest algorithm"));
		} catch (FileNotFoundException e) {
			Informer.getInstance().fail(
			        CLASSNAME, methodName,
				"File not found", " filePath: " + absPath,
				new FatalException("Not an existing file"));
		} catch (IOException e) {
			Informer.getInstance().fail(
			        CLASSNAME, methodName,
				"IO Exception", " filePath: " + absPath,
				new FatalException("Can't read file"));
		}						
		
		// Setting here that the archive is the origin of this message digest calculation
		ArchiveMessageDigest amd = 
			new ArchiveMessageDigest(algorithm, md, DataFile.ORIG_ARCHIVE);

		Informer.getInstance().info(
		        CLASSNAME, methodName,
				"Done calculating " + algorithm + " message digest: " 
					+ DateTimeUtil.now(),
				"File: " + absPath,
				false);
					
		return amd;
	}
	

	public static String md5(InputStream stream) throws FatalException {
		return digestStream("MD5", stream);
	}
	
	public static String sha1(InputStream stream) throws FatalException {
		return digestStream("SHA1", stream);
	}	
	
	public static String digestStream(String algorithm, InputStream stream) throws FatalException {
		String hex = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] buffer = new byte[8 * 1024];
			
			int read = 0;
	        while ((read = stream.read (buffer)) > 0) {
	            digest.update(buffer, 0, read);
	        }
	        
	        hex = toHexString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			Informer.getInstance().fail("Bad message digest alorithm", algorithm, e);
		} catch (IOException e) {
			Informer.getInstance().fail("Cannot calculate message digest", "calculating " + algorithm + " for stream", e);
		}
		return hex;
	}
	
    
	/**
	 * Delete digests stored for the last package.
	 *
	 */
	public static void die() {
	    digests = null;
	}
	
	/**
	 * Gives a message digest for a file.
	 * Calculates it if it hasn't already done so for this package.
	 * 
	 * @param absPath absolute path to file
	 * @param algorithm message digest algorithm
	 * @param forceNewDigest if true, calculate a new message digest
	 * even if one has already been calculated
	 * @return digest value
	 * @throws FatalException
	 */
	public static ArchiveMessageDigest getMessageDigest(String absPath, 
	        String algorithm, boolean forceNewDigest) throws FatalException, PackageException {
	    
	    if (absPath.endsWith("daitss_LOC.xsd") || absPath.endsWith("daitss.xsd")) {
	        System.out.println("Calculating MD for: " + absPath);
	    }
	    
	    String methodName = "getMessageDigest(String, String)";
	    
	    ArchiveMessageDigest amd = null;
	    
	    // check args
	    if (absPath == null ||
			!ArchiveMessageDigest.isValidMdCode(algorithm)){
	    	Informer.getInstance().fail(
	    	        CLASSNAME, methodName,
	       		"Illegal argument", 
	       		"filePath: " + absPath + " algorithm: " + algorithm,
	       		new FatalException("Bad arguments"));
		}
	    
	    // account for someone calling die on this class
	    if (digests == null) {
	        digests = new Hashtable();
	    }
	    
	    Hashtable digestSet = (Hashtable) digests.get(absPath);
	    
	    // see if we have the path in digests
		// if forceNewDigest == true, skip this and go to the else clause
		// (generate and store a new checksum)
	    if (digestSet != null && !forceNewDigest){
	        // see if it has that alg. type already
	        amd = (ArchiveMessageDigest) digestSet.get(algorithm);
	        
	        if (amd == null){
	            // calculate it
	            amd = calcMessageDigest(absPath, algorithm);
	            digestSet.put(algorithm, amd);
	        } else {
	            // already had calculated this
	            Informer.getInstance().info(
	                    CLASSNAME,
                        methodName,
                        "Message digest return (PREVIOUSLY CALCULATED)",
                        "File: " + absPath + " algorithm: " + algorithm,
                        false);
	        }
	    } else {
	        // calculate the digest
	        digestSet = new Hashtable();
	        amd = calcMessageDigest(absPath, algorithm);
	        digestSet.put(algorithm, amd);
	        digests.put(absPath, digestSet);
	    }
	    
	    return amd;
	}

	/**
	 * Test driver.
	 * 
	 * @param args Not used.
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException, PackageException {
		// test file for linux
		//String file = "/apps/mycode/testmd5/MD5Test.class";
	    //String file = "/daitss/dev/data/ingest/work/UF00003061/E20050114_AAAAAF/links_20050114144900/www.fcla.edu/dls/md/daitss/daitss_LOC.xsd";
	    String file = "/daitss/dev/data/global/E20050114_AAAAAF/links_20050114144900/www.fcla.edu/dls/md/daitss/daitss_LOC.xsd";
		
		String val = getMessageDigest(file, ArchiveMessageDigest.ALG_SHA1, false).getValue();
		System.out.println("Message digest: " + 
		        val + "\nlength: " + val.length());
	}
	
	/**
	 * Converts an array of bytes to a hexadecimal String.
	 * 
	 * @param b	an array of bytes
	 * @return	a hexadecimal String
	 */
	private static String toHexString(byte[] b){
		char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		StringBuffer sb = new StringBuffer(b.length * 2);
		
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChars[(b[i] & 0xf0) >>> 4]); // high nibble
			sb.append(hexChars[(b[i] & 0x0f)]); // low nibble
		}
		
		return sb.toString();
	}

} 
