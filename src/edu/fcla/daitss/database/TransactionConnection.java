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
package edu.fcla.daitss.database;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * An interfact that represents the transaction aspect
 * of the connection. The MySqlSharedConnection implements
 * this interface.	...
 */
public interface TransactionConnection {
	
	/**
	 * Close the connection to the Database.
	 */
	public void close() throws SQLException;
	
	/**
	 * Committing a transaction at the information-package application
	 * level.
	 * @throws FatalException
	 */
	void commitTransaction() throws FatalException;
					
	/**
	 * DELETE FROM table WHERE where.
	 * @param table  the name of the table whose data to be deleted
	 * @param where where clause
	 * @return Row count for DELETE statement
	 * @throws  FatalException
	 */		
	public abstract int delete(String table, String where)
		throws FatalException;	
	
	/**
	 * A public method for executing an sql query statement. To be used
	 * for complicated queries or statements that cannot be represented 
	 * in the 'convenience' methods (insert*, retrieve*).
	 * Some caller upper in the calling stack has to catch SQLExceptions 
	 * @param sql the sql statement string
	 * @return a ResultSet instance
	 * @throws SQLException
	 */
	public abstract ResultSet executeQuery(String sql) throws SQLException;

	public abstract int insert(String table, DBConnection.NameValueVector nameValues)
		throws FatalException;	
	/**
	 * Insert INTO table columns VALUE values.
	 * @param table the name of the table
	 * @param columns a vector of columns to be selected
	 * @param values a vector of corresponding values for the colunms
	 * @return The number of records inserted
	 * @throws  FatalException
	 */
	public abstract int insert(String table, Vector columns, Vector values)
		throws FatalException;

	/**
	 * Insert INTO table columns VALUE values.
	 * @param table the name of the table
	 * @param values a vector of corresponding values for the colunms
	 * @return The number of records inserted
	 * @throws FatalException
	 */
	  public abstract int insert(String table, Vector values) 
		  throws FatalException;

	/**
	 * Check if this connection has been closed.
	 * @return true if connection closed, false else.
	 */
	public abstract boolean isClosed();
		
	/**
	 * SELECT column FROM from WHERE where. 
	 * For selecting a single column
	 * @param column the column to be selected
	 * @param from the name of the table
	 * @param where where clause
	 * @return A table of data resulting from executing the query
	 * @throws FatalException
	 * @throws PackageException
	 */	
	
	public abstract ResultSet retrieve(String column, String from, String where)
		throws FatalException;
	
	
	/**
	 * SELECT columns FROM from WHERE where. 
	 * @param columns a vector of columns to be selected
	 * @param from the name of the table
	 * @param where where clause
	 * @return A table of data resulting from executing the query
	 * @throws  FatalException
	 */	
	public abstract ResultSet retrieve(Vector columns, String from, String where)
		throws FatalException;
	
	/**
	 * SELECT column FROM from WHERE where.
	 * Plain SELECT or SELECT...FOR UPDATE.
	 * @param columns a vector of columns to be selected
	 * @param from the name of the table
	 * @param where where clause
	 * @param locked whether it is an exclusive read.
	 * @return A table of data resulting from executing the query
	 * @throws  FatalException, PackageException
	 */
	
	public abstract ResultSet retrieve(Vector columns, String from, String where, boolean locked)
		throws FatalException;
	

	/**
	 * SELECT...GROUP BY.
	 * @param columns a vector of columns to be selected
	 * @param table the name of the table
	 * @param where where clause
	 * @param groupBy a vector of group by attributes
	 * @return A table of data resulting from executing the query
	 * @throws  FatalException, PackageException
	*/	
	
	public ResultSet retrieve(Vector columns, String table, String where, Vector groupBy)
		throws PackageException, FatalException;
	
		
	/**
	 * SELECT...GROUP BY...HAVING.
	 * @param columns a vector of columns to be selected 
	 * @param table the name of the table
	 * @param where where clause
	 * @param groupBy a vector of group by attributes
	 * @param having  having cluase
	 * @return A table of data resulting from executing the query
	 * @throws  FatalException, PackageException
	 */	
	
	public ResultSet retrieve(Vector columns, String table, String where, Vector groupBy, String having)
		throws FatalException;
	

	/**
	 * SELECT ...WHERE ... GROUP BY ... HAVING ... ORDER BY.
	 * @param columns a vector of columns to be selected 
	 * @param table the name of the table
	 * @param where where clause
	 * @param groupBy a vector of group by attributes
	 * @param having  having clause
	 * @param orderBy a vector of order by attributes
	 * @return A table of data resulting from executing the query
	 * @throws  FatalException, PackageException
	 */		
	public abstract ResultSet retrieve(Vector columns, String table, String where, Vector groupBy, String having, Vector orderBy)
			throws FatalException;
	
	/**
	 * Rolling back the the status before the the transaction starts.
	 * @throws  FatalException, PackageException
	 */
	void rollbackTransaction() throws SQLException;

	/**
	 * Start a Transaction.
	 * @throws PackageException
	 * @throws FatalException
	 */
	void startTransaction() throws FatalException;
	
	/**
	 * UPDATE table SET columns  = values
	 * Update some metadata in the data dictionary
	 * @param table  the name of the table whose data to be updated
	 * @param setColumns a vector of the columns to be updated
	 * @param values a vector of the values of the corresponding columns
	 * @param where where clause
	 * @return Row count for UPDATE statement
	 */
	public abstract int update(String table, Vector setColumns, Vector values, String where)
		throws FatalException;
		
	/**
	 * Get all the column names of a table
	 * @param table The name of the table.
	 * @return A vector of all the column names of the table.
	 * @throws SQLException
	 */
	public abstract Vector getAllColumns(String table) throws SQLException;		

}
