package edu.fcla.daitss.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.fcla.daitss.database.*;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.Silo;


/**
	This class is glue. The jmserver jar is clean with automated tests. 
	This bears the burden of the DAITSS cruft .
	*/
public class StorageHelper {

	/**
	 * given a ieid, return the copies
	 * @param ieid
	 * @return
	 * @throws FatalException
	 */
	public static List<Copy> copies(String ieid) throws FatalException {
		Vector<Copy> copies = new Vector<Copy>();
		
		DBConnection con = DBConnection.getConnection();
		String sql = String.format("select * from %s, %s where %s = %s and %s = 0 and %s = %s", 
				ArchiveDatabase.TABLE_COPY,
        ArchiveDatabase.TABLE_SILO,
				ArchiveDatabase.COL_COPY_IEID,
				SqlQuote.escapeString(ieid),
        ArchiveDatabase.COL_SILO_RETIRED,
        ArchiveDatabase.COL_COPY_SILO,
        ArchiveDatabase.TABLE_SILO + "." + ArchiveDatabase.COL_SILO_ID);
		try {
			ResultSet set = con.executeQuery(sql);
			while (set.next()) {
				String siloId = set.getString(ArchiveDatabase.COL_COPY_SILO);
				String url = siloURL(siloId);
				String path = set.getString(ArchiveDatabase.COL_COPY_PATH);
				copies.add(new Copy(new Silo(url), path));
			}
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot load copies", "Storage", e);
		}
		
		return copies;
	}

	/**
	 * given a silo id return the silo URL 
	 * @param siloId
	 * @return
	 * @throws FatalException
	 */
	private static String siloURL(String siloId) throws FatalException {
		
		String sql = String.format("select * from %s where %s = %d",
				ArchiveDatabase.TABLE_SILO,
				ArchiveDatabase.COL_SILO_ID,
				Integer.parseInt(siloId));
		
		DBConnection con = DBConnection.getConnection();
		ResultSet set;
		String url = null;
		
		try {
			set = con.executeQuery(sql);
			if (!set.first()) {
				Informer.getInstance().fail("Cannot select silo with ID " + siloId, "Selecting silos", null);
			}
			url =  set.getString(ArchiveDatabase.COL_SILO_URL);
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot select silo with ID " + siloId, "Selecting silos", e);
		}
		
		return url;
	}

	/**
	 * return all the active silos
	 * @return
	 * @throws FatalException
	 */
	public static List<Silo> activeSilos() throws FatalException {
		Vector<Silo> silos = new Vector<Silo>();
		DBConnection con = DBConnection.getConnection();
		String sql = String.format("select * from %s where %s", ArchiveDatabase.TABLE_SILO, ArchiveDatabase.COL_SILO_ACTIVE);
		try {
			ResultSet set = con.executeQuery(sql);
			while (set.next()) {
				silos.add(new Silo(set.getString(ArchiveDatabase.COL_SILO_URL)));
			}
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot load copies", "Storage", e);
		}
		return silos;
	}

	/**
	 * given a set of copies, a tarball and the md5 of the tarball, insert the copies into the database
	 * @param scon
	 * @param ieid
	 * @param copies
	 * @param tarballMD5
	 * @throws FatalException
	 */
	public static void dbInsertCopies(TransactionConnection scon, String ieid, List<Copy> copies, String tarballMD5) throws FatalException {
		for(Copy copy : copies) {			
			try {
				
				int siloId = dbSiloId(copy.silo());
				
				Vector columns = new Vector();
				columns.add(ArchiveDatabase.COL_COPY_IEID);
				columns.add(ArchiveDatabase.COL_COPY_SILO); 
				columns.add(ArchiveDatabase.COL_COPY_PATH);
				columns.add(ArchiveDatabase.COL_COPY_MD5);
				
				Vector values = new Vector();
				values.add(ieid);
				values.add(siloId);
				values.add(copy.path());
				values.add(tarballMD5);
				
				int insert = scon.insert(ArchiveDatabase.TABLE_COPY, columns, values);
				if (insert != 1) {
					Informer.getInstance().fail("Cannot insert copied into the database", "Storage", null);
				}
			} catch (SQLException e) {
				Informer.getInstance().fail("Cannot insert copied into the database", "Storage", e);
			}
		}	
	}
	
	
	
	/**
	 * given a silo get its database id
	 *
	 * @param  
	 * @return 
	 * @throws SQLException 
	 * @throws FatalException 
	 */
	private static int dbSiloId(Silo silo) throws SQLException, FatalException {
		DBConnection con = DBConnection.getConnection();
		String sql = String.format("select %s from %s where %s = %s",
			ArchiveDatabase.COL_SILO_ID,
			ArchiveDatabase.TABLE_SILO,
			ArchiveDatabase.COL_SILO_URL,
			SqlQuote.escapeString(silo.url()));
		System.out.println(sql);
		ResultSet set = con.executeQuery(sql);
		if (!set.first()) {
			Informer.getInstance().fail("Cannot get id of silo " + silo.url(), "Storage", new FatalException());
		}
		int siloId = set.getInt(ArchiveDatabase.COL_SILO_ID);
		set.close();
		con.close();
		return siloId;
	}

	public static int dbCopyId(Copy copy) throws FatalException, SQLException {
		DBConnection con = DBConnection.getConnection();
		String sql = String.format("select %s from %s where %s = %s",
				ArchiveDatabase.COL_COPY_ID,
				ArchiveDatabase.TABLE_COPY,
				ArchiveDatabase.COL_COPY_PATH,
				SqlQuote.escapeString(copy.path()));
			ResultSet set = con.executeQuery(sql);
			if (!set.first()) {
				Informer.getInstance().fail("Cannot get id of copy " + copy, "Storage", new FatalException());
			}
			int id = set.getInt(ArchiveDatabase.COL_SILO_ID);
			set.close();
			con.close();
			return id;
	}
	
	public static void dbDeleteCopy(TransactionConnection con, int copyId) throws FatalException, SQLException {
		String sql = String.format("delete from %s where %s = %s",
				ArchiveDatabase.TABLE_COPY,
				ArchiveDatabase.COL_COPY_ID,
				copyId);
		con.executeQuery(sql);
	}
}
