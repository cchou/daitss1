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
 */
package edu.fcla.daitss.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * A singleton class that has access to the preservation agreements between
 * the archive and the data owners.
 * 
 */
public class Agreements { 

    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = Agreements.class.getName();

	/**
	 * Default AP (Account/Project) id assigned to objects until the real
	 * AP id is known.
	 */
	public static final int DEFAULT_AP_ID = 0;

	/**
	 * Default Subaccount id assigned to objects until the real
	 * Subaccount id is known.
	 */
	public static final int DEFAULT_SA_ID = 0;
	
	
	/**
	 * Maximum valid AP (account/project) id
	 */
	public static final long MAX_AP_ID = 4294967293L;
	
	/**
	 * Maximum valid subaccount (account/project) id
	 */
	public static final long MAX_SUBACCOUNT_ID = 99999999L;	
	
	/**
	 * Minimum valid AP (account/project) id
	 */
	public static final long MIN_AP_ID = 0;

	/**
	 * Minimum valid subaccount id
	 */
	public static final long MIN_SUBACCOUNT_ID = 0;
	
    /**
     * Bit-level preservation
     */
    public static final String PRES_LEV_BIT = "BIT";

    /**
     * Full preservation
     */
    public static final String PRES_LEV_FULL = "FULL";

    /** 
     * No preservation
     */
    public static final String PRES_LEV_NONE = "NONE";

    /** 
     * Unknown preservation level
     */
    public static final String PRES_LEV_UNKNOWN = "UNKNOWN";
	
	/** 
	 * Looks up the AP id for a file. 
	 * An AP id is a unique id per account and project.
	 * Can cause a package exception when there is no AP code for a
	 * given account and project combination. Can cause a fatal 
	 * exception when an AP idretrieved from the database is not
	 * valid or when multiple APs ids map to the same account and
	 * project combination in the database, or when there is an
	 * error connecting to the database.
	 * 
	 * @param account : object owner
	 * @param project : primary project of object
	 * @return OPS code for the archival object
	 * @throws FatalException 
	 * @throws PackageException 
	 */
	public static int getApId(String account, String project) 
		throws PackageException, FatalException{ 
		
		String methodName = "getAPCode(String, String)";
		int id = DEFAULT_AP_ID;

		String tbl = "";
		Vector columns = new Vector();
		String where = "";
		try{	
			DBConnection conn = DBConnection.getConnection();
			
			tbl = ArchiveDatabase.TABLE_ACCOUNT_PROJECT;
			columns.addElement(ArchiveDatabase.COL_ACCOUNT_PROJECT_ID);
			
			where = " " + ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT + "= " + SqlQuote.escapeString(account) +
					" AND " + ArchiveDatabase.COL_ACCOUNT_PROJECT_PROJECT + "= " + SqlQuote.escapeString(project);
			
			ResultSet rs = conn.retrieve(columns, tbl, where);
			
            			
			// move the resultset pointer to the last row in the result so we can use it
			// to count the number of rows returned
			rs.last();
			
			int size = rs.getRow();
			if (size == 0) {
			    // no matching ap code in database
			    // could be that their metadata was wrong
			    // so we are treating this as a package exception instead
			    // of a fatal exception
				Informer.getInstance().error(
					CLASSNAME, methodName,
					"No AP id exists for this account and project combination", 
					"account: " + account + " project: " + project, 
					new PackageException("No AP id"));
			} else if (size > 1) {
			    // multiple ap codes
			    // error in database table
				Informer.getInstance().fail(
					CLASSNAME, methodName,
					"Multiple AP codes for this account and project combination", 
					"account: " + account + " project: " + project, 
					new FatalException("Multiple AP ids"));
											
			} else {
			    //size == 1
				id = rs.getInt(ArchiveDatabase.COL_ACCOUNT_PROJECT_ID);
				
				// check the validity of the ap code
				if (id < Agreements.MIN_AP_ID || id > Agreements.MAX_AP_ID) {
				    // error in database
					Informer.getInstance().fail(
						CLASSNAME, methodName,
						"Invalid AP id", 
						"ap: " + id + " account: " + account + 
						" project: " + project, 
						new FatalException("AP id not valid"));
				}
			}
            
            try {
            	rs.close();
                conn.close();
            } catch (SQLException e) {
                Informer.getInstance().warning(CLASSNAME,
                        methodName,
                        "Error closing database connection: " + e.getMessage(),
                        conn.toString());
            } finally {
                conn = null;  
            }
            
            
		} catch (SQLException se){
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Error executing SQL query",
				"SQLStatement:" + "retrieve "+columns+" from "+tbl+" where "+where,
				se);
		}
		
		return id;
	}         
	
	/**
	 * Queries the database to find a subaccount ID given a subaccount code
	 * and an account code.
	 * 
	 * @param subaccount a subaccount code 
	 * @param account an account code
	 * @return integer
	 * @throws PackageException thrown when an id is not found, or if there is an error closing the connection
	 * @throws FatalException thrown when multiple ids are found, or if the id value is out of range
	 */
	public static int getSubaccountId(String subaccount, String account) 
		throws PackageException, FatalException {
	    
	    String methodName = "getSubaccountId(String, String)";
	    int subaccountId = DEFAULT_SA_ID;
	    
	    String query = "SELECT " + ArchiveDatabase.COL_SUB_ACCOUNT_ID + 
	    	" FROM " + ArchiveDatabase.TABLE_SUB_ACCOUNT + 
	    	" WHERE " + ArchiveDatabase.COL_SUB_ACCOUNT_CODE +
	    	" = " + SqlQuote.escapeString(subaccount) + " AND " + ArchiveDatabase.COL_SUB_ACCOUNT_ACCOUNT +
	    	" = " + SqlQuote.escapeString(account);
	    
	    DBConnection conn = DBConnection.getConnection();
	    try {
            ResultSet rs = conn.executeQuery(query);
            rs.last();
            int size = rs.getRow();
            
            if (size == 0) {
                Informer.getInstance().error(CLASSNAME, 
                        methodName, 
                        "No subaccount ID exists for this combination of account code and subaccount code",
                        "subaccount: " + subaccount + " account: " + account,
                        new PackageException("No subaccount ID"));
            }
            else if (size > 1) {
                // mulitple subaccount ids found. this should never happen
                // because of database constraints, but just in case the 
                // database schema changes ...
                Informer.getInstance().fail(CLASSNAME,
                        methodName,
                        "Multiple subaccount IDs found",
                        "subaccount: " + subaccount + " account: " + account,
                        new FatalException("Multiple subaccount IDs found"));
                
            }
            else {
                // exactly one id was found
                subaccountId = rs.getInt(ArchiveDatabase.COL_SUB_ACCOUNT_ID);
            	
                // check the validity of the subaccount code
                // NOTE: given current database schema, this check is not 
                // strictly necessary
            	if (subaccountId < Agreements.MIN_SUBACCOUNT_ID || subaccountId > Agreements.MAX_SUBACCOUNT_ID) {
            	    // error in database
            		Informer.getInstance().fail(
            			CLASSNAME, methodName,
            			"Subaccount ID is out of range", 
            			"id: " + subaccountId + " subaccount: " + subaccount + 
            			" account: " + account, 
            			new FatalException("Subaccount id not valid"));
            	}	        
            }
        } catch (SQLException e) {
            Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "Error communicating with database",
                    e.toString(),
                    e);
            
        } finally {
            try {
                conn.close();
            } catch (SQLException e1) {
                Informer.getInstance().warning(CLASSNAME,
                        methodName,
                        "Error closing database connection",
                        e1.toString());
            }        
            conn = null;
        }
	    	    
	    return subaccountId;
	}
	
	/** 
	 * Looks up the preservation level for a file.
	 * Can cause a FatalException when the mime type is not one
	 * recognized by the archive, or there are errors in the database
	 * (no default preservation level for an AP, multiple preservation
	 * levels for a format for an AP, invalid preservation levels), or
	 * there was a problem connecting to the database.
	 * 
	 * @param apId : code representing the file's account and project
	 * @param mimeMediaType : file format
	 * @return preservation level 
	 * @throws FatalException
	 * @throws PackageException
	 */
	public static String getPreservationLevel(int apId, String mimeMediaType) 
		throws PackageException, FatalException {
		
		String methodName = "getPreservationLevel(int,String)";
		
		String presLevel = Agreements.PRES_LEV_UNKNOWN;	
			
		// check input
		if(mimeMediaType == null || 
			!MimeMediaType.isValidType(mimeMediaType)) {
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Illegal argument",
				"mimeMediaType: " + mimeMediaType,
				new FatalException("Invalid MimeType"));
		}

		Vector columns = new Vector();
		String tbl = "";
		String where = "";
		
		try{
			DBConnection conn = DBConnection.getConnection();
			
			tbl = ArchiveDatabase.TABLE_ARCHIVE_LOGIC;
			
			columns.addElement(ArchiveDatabase.COL_ARCHIVE_LOGIC_PRES_LEVEL);
			
			String nowDate = DateTimeUtil.now(); 
			
			// first see if there is an agreement for that specific media type
			where =   
				SqlQuote.escapeString(nowDate) + "  >= " + ArchiveDatabase.COL_ARCHIVE_LOGIC_START_DATE +
				" and (" + 
				SqlQuote.escapeString(nowDate) + " < " + ArchiveDatabase.COL_ARCHIVE_LOGIC_END_DATE + 
				" or " +
				ArchiveDatabase.COL_ARCHIVE_LOGIC_END_DATE + " IS NULL) " +
				" and " + 
				ArchiveDatabase.COL_ARCHIVE_LOGIC_ACCOUNT_PROJECT + " =  " + SqlQuote.escapeInt(apId)	+ 
				" and " + 
				ArchiveDatabase.COL_ARCHIVE_LOGIC_MEDIA_TYPE + " = " +  SqlQuote.escapeString(mimeMediaType);	
			
			ResultSet rs = conn.retrieve(columns, tbl, where);
			
			// move the resultset pointer to the last row in the result so we can use it
			// to count the number of rows returned
			rs.last();
			int rowNum = rs.getRow();

			if (rowNum == 0){
			    // no agreement for that mime type
			    // see what preservation level they want to use for all
			    // mime types they didn't specify individually
				where =   
					SqlQuote.escapeString(nowDate) + "  >= " + ArchiveDatabase.COL_ARCHIVE_LOGIC_START_DATE +
					" and (" + 
					SqlQuote.escapeString(nowDate) + " < " + ArchiveDatabase.COL_ARCHIVE_LOGIC_END_DATE + 
					" or " +
					ArchiveDatabase.COL_ARCHIVE_LOGIC_END_DATE + " IS NULL) " 
					+ " and " + 
					ArchiveDatabase.COL_ARCHIVE_LOGIC_ACCOUNT_PROJECT + " =  " + SqlQuote.escapeInt(apId)
					+ " and " +
					ArchiveDatabase.COL_ARCHIVE_LOGIC_MEDIA_TYPE + " = " + SqlQuote.escapeString("default");
				
				rs = conn.retrieve(columns, tbl, where);
				rs.last();
				rowNum = rs.getRow();
			}
						
			if (rowNum == 0){
			    // they didn't specify a default preservation - this should
			    // have been caught in the adminstrative module. Throw a package error.
				Informer.getInstance().error(
					CLASSNAME,
					methodName,
					"No preservation level code exists for this mimeType by this AP.",
					"mime type: " + mimeMediaType + " ap: " + apId,
					new FatalException("No default preservation level"));
			} else if (rowNum > 1){
			    // more than 1 preservation level specified for this media type - 
			    // should have been caught in the administrative module - throw a fatal.
				Informer.getInstance().error(
					CLASSNAME,
					methodName,
					"More than 1 preservation level exist for this mimeType by this AP.",
					"mime type: " + mimeMediaType + " ap: " + apId,
					new FatalException("Multiple preservation levels"));					
			} else {
			    //size == 1
				rs.first();
				presLevel = rs.getString(ArchiveDatabase.COL_ARCHIVE_LOGIC_PRES_LEVEL);
				if	(!isValidArchivePresLevel(presLevel)){
				    // an invalid preservation level exists in the
				    // database for this mime type and ap - should have been
				    // caught in the administrative module - throw a fatal
					Informer.getInstance().error(
						CLASSNAME,
						methodName,
						"Invalid preservation level code in database",
						"preservation level: " + presLevel +
						" mime type: " + mimeMediaType +
						" ap: " + apId,
						new FatalException("Invalid preservation level"));
				}	
			}		
            
            try {
            	rs.close();
                conn.close();
            } catch (SQLException e) {
                Informer.getInstance().warning(CLASSNAME,
                        methodName,
                        "Error closing database connection: " + e.getMessage(),
                        conn.toString());
            } finally {            
                conn = null;
            }
            
		} catch(SQLException se){
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Error Executing SQL query",
				"SQLStatement:" + "retrieve "+columns+" from "+tbl+" where "+where,
				se);
		}
		
		return presLevel;
	}         

    /**
     * Valid preservation values in the archive are
     * <code>PRES_LEV_NONE</code> which means do not archive, 
     * <code>PRES_LEV_BIT</code> which means bit-level preservation, 
     * <code>PRES_LEV_FULL</code> which means full preservation.
     * These ae the preservation levels that a contributor
     * can specify in an agreement.
     * 
     * @param _presLevel the preservation level
     * @return whether or not the preservation level is a valid one
     */
    public static boolean isValidArchivePresLevel(String _presLevel) {
    	boolean isValid = false;
    	
    	if (_presLevel != null
    		&& (_presLevel.equals(PRES_LEV_BIT)
    			|| _presLevel.equals(PRES_LEV_FULL)
    			|| _presLevel.equals(PRES_LEV_NONE))) {
    		isValid = true;
    	} 
    		
    	return isValid;
    	
    }

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 * @throws FatalException
	 * @throws PackageException
	 */
	public static void main(String args[]) throws PackageException, FatalException {
		
		int apId = Agreements.getApId("FDA","FDA");
		System.out.println("apId :" + apId);
		System.out.println("presLevel: "
		+ Agreements.getPreservationLevel(apId, "text/plain"/*"application/octet-stream"*/)
		+ "," + Agreements.getPreservationLevel(apId, "application/octet-stream")
		+ "," + Agreements.getPreservationLevel(apId, "image/tiff"));
	}
} 
