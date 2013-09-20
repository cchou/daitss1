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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * @author franco
 *
 */
public class DataFile {

	private Map<String, String> messageDigests = new Hashtable<String, String>();
	private String dfid;
	private String path;
	private String size;
	private String ieid;

	/**
	 * @return	a mapping from message digest algorithms to message digest values
	 */
	public Map<String, String> getMessageDigests() {
		return messageDigests;
	}

	/**
	 * @param dfid
	 * @param enabled	If true the DataFile will only use enabled storage descriptions
	 * @throws FatalException 
	 */
	public DataFile(String dfid) throws FatalException {
		this.dfid = dfid;
		loadData();		
		loadMessageDigests();
	}

	private void loadMessageDigests() throws FatalException {
		DBConnection conn = DBConnection.getConnection();
		
		String sql = String.format("SELECT %s, %s from %s where %s = %s",
				ArchiveDatabase.COL_MESSAGE_DIGEST_CODE,
				ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE,
				ArchiveDatabase.TABLE_MESSAGE_DIGEST,
				ArchiveDatabase.COL_MESSAGE_DIGEST_DFID,
				SqlQuote.escapeString(dfid));
		
		ResultSet set;
		try {
			set = conn.executeQuery(sql);
			while(set.next()) {
				// get the storage instance object
				messageDigests.put(set.getString(
						ArchiveDatabase.COL_MESSAGE_DIGEST_CODE),
						set.getString(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE));
			}
			set.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(
					this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot load message digests from database for " + dfid,
					"Loading DataFile Info",
					e);
		}		
	}

	private void loadData() throws FatalException {
		DBConnection conn = DBConnection.getConnection();
		
		String sql = String.format("select %s, %s, %s from %s where %s = %s",
				ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH,
				ArchiveDatabase.COL_DATA_FILE_SIZE,
				ArchiveDatabase.COL_DATA_FILE_IEID,
				ArchiveDatabase.TABLE_DATA_FILE,
				ArchiveDatabase.COL_DATA_FILE_DFID,
				SqlQuote.escapeString(dfid));
		
		ResultSet set;
		try {
			set = conn.executeQuery(sql);
			while(set.next()) {
				// get the storage instance object
				this.path = set.getString(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
				this.size = set.getString(ArchiveDatabase.COL_DATA_FILE_SIZE);
				this.ieid = set.getString(ArchiveDatabase.COL_DATA_FILE_IEID);
			}
			set.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot load attributes from database for " + dfid, "Loading DataFile Info", e);
		}
	}

	/**
	 * @param scon
	 * @throws FatalException 
	 */
	public void deleteMetaData(TransactionConnection scon) throws FatalException {
		String where = String.format("%s = '%s'", ArchiveDatabase.COL_DATA_FILE_DFID, dfid);
		scon.delete(ArchiveDatabase.TABLE_DATA_FILE, where);
	}


	/**
	 * @return the dfid of the file
	 */
	public String getDfid() {
		return dfid;
	}

	/**
	 * @return the ieid of the file
	 */
	public String getIeid() {
		return ieid;
	}

	/**
	 * @return the path of the file
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return	the size of the file
	 */
	public String getSize() {
		return size;
	}


}
