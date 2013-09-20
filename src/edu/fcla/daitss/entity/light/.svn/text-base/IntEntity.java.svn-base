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
package edu.fcla.daitss.entity.light;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.storage.StorageHelper;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.Silo;

/**
 * Light weight class for 
 * @author franco
 *
 */
public class IntEntity {

	List<DataFile> dataFiles = new Vector<DataFile>();
	private String ieid;
	private String packageName;
	List<DataFile> globalFiles = new Vector<DataFile>();
    private List<Copy> copies;
	
	/**
	 * @param ieid
	 * @param useGlobals if true global files will be loaded
	 * @throws FatalException 
	 */
	public IntEntity(String ieid) throws FatalException {
		this.ieid = ieid;
		loadData();
		loadDataFiles();
		loadGlobalFiles();
		loadCopies();
	}
	
	private void loadCopies() throws FatalException {
		copies = StorageHelper.copies(ieid);		
    }

    public List<String> getGFPIDs() {
	    return null;
	}
	
	private void loadData() throws FatalException {
		DBConnection conn = DBConnection.getConnection();		
		String sql = String.format("SELECT %s FROM %s WHERE %s = %s",
				ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME,
				ArchiveDatabase.TABLE_INT_ENTITY,
				ArchiveDatabase.COL_INT_ENTITY_IEID, SqlQuote.escapeString(ieid));
		try {
			ResultSet results = conn.executeQuery(sql);
			if(results.first()) {
				this.packageName = results.getString(ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME);
			} else {
				Informer.getInstance().fail(
						this,
						new Throwable().getStackTrace()[0].getMethodName(), 
						"Cannot select package name for int entity: " + ieid,
						"Loading IntEntity",
						new FatalException());
			}
			results.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(
					this,
					new Throwable().getStackTrace()[0].getMethodName(), 
					"Cannot select data files for int entity: " + ieid,
					"Loading IntEntity",
					e);
		}
	}

	private void loadDataFiles() throws FatalException {
		DBConnection conn = DBConnection.getConnection();		
		// retrieve all data files in the ieid which are not obsolete
		String sql = String.format("SELECT %s FROM %s WHERE %s = %s AND %s = 'FALSE'",
				ArchiveDatabase.COL_DATA_FILE_DFID,
				ArchiveDatabase.TABLE_DATA_FILE,
				ArchiveDatabase.COL_DATA_FILE_IEID,
				SqlQuote.escapeString(ieid),
				ArchiveDatabase.COL_DATA_FILE_IS_OBSOLETE
				);
		try {
			ResultSet results = conn.executeQuery(sql);
			while(results.next()) {
				String dfid = results.getString(ArchiveDatabase.COL_DATA_FILE_DFID);
				dataFiles.add(new DataFile(dfid));
			}			
			results.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(
					this,
					new Throwable().getStackTrace()[0].getMethodName(), 
					"cannot select data files for int entity: " + ieid,
					"Loading IntEntity",
					e);
		}
	}
	
	private void loadGlobalFiles() throws FatalException {
		DBConnection conn = DBConnection.getConnection();
		String sql = String.format("SELECT %s FROM %s WHERE %s = %s",
				ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_DFID,
				ArchiveDatabase.TABLE_INT_ENTITY_GLOBAL_FILE,
				ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_IEID,
				SqlQuote.escapeString(ieid));
		try {
			ResultSet results = conn.executeQuery(sql);
			while(results.next()) {
				String dfid = results.getString(ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_DFID);
				globalFiles.add(new DataFile(dfid));
			}			
			results.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(
					this,
					new Throwable().getStackTrace()[0].getMethodName(), 
					"cannot select global files for int entity: " + ieid,
					"Loading IntEntity",
					e);
		}
	}

	/**
	 * Removes all the DataFile specific data from the database
	 * @param scon
	 * @throws FatalException 
	 */
	public void deleteMetaDataFiles(TransactionConnection scon) throws FatalException {
		for(DataFile df: dataFiles) {
			df.deleteMetaData(scon);
		}
	}

	/**
	 * @return the ieid of this intellectual entity
	 */
	public String getIeid() {
		return ieid;
	}

	/**
	 * @return the data files in this int entity
	 */
	public List<DataFile> getDataFiles() {
		return dataFiles;
	}

	/**
	 * @return the global files in this int entity
	 */
	public List<DataFile> getGlobalFiles() {
		return globalFiles;
	}

	
	/**
	 * @return the package name of this int entity
	 */
	public String getPackageName() {
		return packageName;
		
	}

	/**
	 * @param scon
	 * @throws FatalException
	 */
	public void deleteGlobalFileReferences(TransactionConnection scon) throws FatalException {
		String where = String.format("%s = %s", ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_IEID, SqlQuote.escapeString(ieid));
		scon.delete(ArchiveDatabase.TABLE_INT_ENTITY_GLOBAL_FILE, where);
	}
	

    public List<Copy> copies() {        
        return copies;
    }

	public void deleteCopies(TransactionConnection scon) throws FatalException {
		String where = String.format("%s = %s", ArchiveDatabase.COL_COPY_IEID, SqlQuote.escapeString(ieid));
		scon.delete(ArchiveDatabase.TABLE_COPY, where);
	}
}
