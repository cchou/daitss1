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

import java.util.Vector;
import java.util.StringTokenizer;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.net.ProtocolException;

//import edu.fcla.daitss.core.util.MySqlConnection.NameValueVector.NameValue;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * An abstract class of a connection to the metadata storage device.
 * Includes a set of methods of data operations, such as open and 
 * close a connection to the database, retrieve data, insert data, update
 * data and delete data. 
 *
 */
public abstract class DBConnection {
	
	/** 
	 * NameValueVector represents a Vector containing a 
	 * database field name and value.
	 * 
	 * @author Andrea Goethals, FCLA
	 *
	 */
	public static class NameValueVector extends Vector{
		
		/**
		 * 
		 * NameValue represents a database name and value
		 * 
		 * @author Andrea Goethals, FCLA
		 *
		 */
		private class NameValue{
			String name = null;
			Object value = null;
			
			/**
			 * 
			 * @param name database field name
			 * @param value database field value
			 * @throws FatalException 
			 */
			public NameValue(String name, Object value) throws FatalException{
				String methodName = "NameValue(String, Object)";
			    if (name == null) {
				    Informer.getInstance().fail(
                            this,
                            methodName,
                            "Illegal argument",
                            "name: " + name,
                            new IllegalArgumentException());
				}
			    this.name = name;
				this.value = value;
			}
		}
		
		/**
		 * Adds a database field name and value to a Vector.
		 * 
		 * @param field database field
		 * @param value database field value
		 * @throws FatalException 
		 */
		public void addElement(String field, Object value) throws FatalException{
			if (value instanceof String) {
                // if this value is a String, it may be necessary to escape a
                // single quote (')
                super.addElement(
                        new NameValue(field, FileUtil.replaceString((String)value, "'", "\\'")) );                
            }
            else {
                // the value is not a String, no need to escape any characters
                super.addElement(
                        new NameValue(field, value));
                    
            }
		}
		
		/**
		 * Returns a database field's name at a particular
		 * index in a Vector.
		 * 
		 * @param index
		 * @return String object
		 */
		public String fieldAt(int index){
			return ((NameValue)super.elementAt(index)).name;
		}
		
		/**
		 * Returns a database field's value at a particular
		 * index in a Vector.
		 * 
		 * @param index
		 * @return the value at the index
		 */
		public Object valueAt(int index){
			return ((NameValue)super.elementAt(index)).value;
		}
	}	
    
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = 
		"edu.fcla.daitss.database.DBConnection";


	private static TransactionConnection sharedConnection = null;

	/**
	 * Static method to get the correct connection of a subclass that
	 * do not need the transaction consistancy constraint. 
	 * 
	 * @return An instance of DBConnection
	 * @throws FatalException
	 */
	public static DBConnection getConnection() throws FatalException{
		
		String methodName = "getConnection()";
		
		String dbUrl = ArchiveProperties.getInstance().getArchProperty("DB_CONNECTION_URL");
		String dbDriver = ArchiveProperties.getInstance().getArchProperty("DB_DRIVER");
			
		String user = ArchiveProperties.getInstance().getArchProperty("DB_USER");
		String password = ArchiveProperties.getInstance().getArchProperty("DB_PASSWD");
			
		StringTokenizer st = new StringTokenizer(dbUrl,":");
		String protocol = st.nextToken();
		
		DBConnection jdbcConn = null;
		
		// check the db implementation
		try{
			if (protocol.equals("jdbc")){
				Class.forName(dbDriver);
				jdbcConn = 
					new MySqlConnection(DriverManager.getConnection(dbUrl, user, password));
			}
			else{
				Informer.getInstance().fail(
					CLASSNAME,
					methodName,
					"Unsupported protocol",
					dbDriver,
					new ProtocolException("Bad database access protocol"));
			}
		}
		catch(ClassNotFoundException cnfe){
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Could not find the database driver class.",
				dbDriver,
				cnfe);
		}
		catch(SQLException se){
			Informer.getInstance().fail(
				CLASSNAME,
				methodName,
				"Could not connect to the database",
				"user: " + user + " password: " + password + " dbUrl: " + dbUrl,
				 
				se);							
		}
		return jdbcConn;
	}
	
	/**
	 * Static method to get the correct connection of a subclass that
	 * supports the transaction consistancy constraint. 
	 * 
	 * @return An instance of TransactionConnection
	 * @throws FatalException
	 */	
	public static TransactionConnection getSharedConnection() throws FatalException {
		
		String methodName = "getSharedConnection()";
		
		if (sharedConnection == null || sharedConnection.isClosed()){
			
			String dbUrl = 
				ArchiveProperties.getInstance().getArchProperty("DB_CONNECTION_URL");
			String dbDriver = 
				ArchiveProperties.getInstance().getArchProperty("DB_DRIVER");
			
			String user = 
				ArchiveProperties.getInstance().getArchProperty("DB_USER");
			String password = 
				ArchiveProperties.getInstance().getArchProperty("DB_PASSWD");
			
			StringTokenizer st = new StringTokenizer(dbUrl,":");
			String protocol = st.nextToken();

			try{
				if (protocol.equals("jdbc")){
						Class.forName(dbDriver);
						Connection jdbcConn = DriverManager.getConnection(dbUrl, user, password);
						
						sharedConnection = new MySqlTransactionConnection(jdbcConn);
				}
				else{	
					Informer.getInstance().fail(
						CLASSNAME,
						methodName,
						"Unsupported protocol",
						dbDriver,
						new ProtocolException("Bad database access protocol"));
				}
			}
						
			catch(ClassNotFoundException cnfe){
				Informer.getInstance().fail(
					CLASSNAME,
					methodName,
					"Could not find the database driver class.",
					dbDriver,
					cnfe);
			}
			catch(SQLException se){
				Informer.getInstance().fail(
					CLASSNAME,
					methodName,
					"Could not connect to the Database",
					dbUrl,
					se);
			}
		}
		return sharedConnection;
	}	

	protected DBConnection(){
	}
	/**
	 * Close this connection.
	 * @throws SQLException
	 */
	public abstract void close() throws SQLException;
	
//deletes
  /**
   * DELETE FROM table WHERE where.
   * @param table  the name of the table whose data to be deleted
   * @param where where clause
   * @return Row count for DELETE statement
   * @throws FatalException
   */		
	public abstract int delete(String table, String where)
		throws  FatalException;


// general execute
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
		
	/**
	 * Get all the column names of a table
	 * @param table The name of the table.
	 * @return A vector of all the column names of the table
	 * @throws SQLException
	 */
	public abstract Vector getAllColumns(String table) throws SQLException;
	
	/**
	 * Execute stmtString, which is an INSERT statement.
	 * @param statmentString : The insert statement string
	 * @return  Row count for INSERT statement
	 * @throws FatalException
	 */
	/*
	public abstract int insert(String statmentString) 
		throws FatalException;
	*/	
	
/**
 * @param table
 * @param nameValues
 * @return integer denoting DB server response
 * @throws FatalException
 */
//inserts
	public abstract int insert(String table, NameValueVector nameValues)
		throws FatalException;

	/**
	 * Insert INTO table VALUE values.
	 * @param table the name of the table
	 * @param values a vector of corresponding values for the colunms
	 * @return The number of records inserted
	 * @throws FatalException
	 */
	  public abstract int insert(String table, Vector values) 
		  throws FatalException;
	

  /**
   * Insert INTO table columns VALUE values.
   * @param table the name of the table
   * @param columns a vector of columns to be selected
   * @param values a vector of corresponding values for the colunms
   * @return The number of records inserted
   * @throws FatalException
   */
	public abstract int insert(String table, Vector columns, Vector values) 
		throws FatalException;
	
	/**
	 * Check if this connection has been closed.
	 * @return boolean denoting DB server response
	 * @throws SQLException 
	 */
	public abstract boolean isClosed() throws SQLException;
	
//selects
    
    /**
     * SELECT columns FROM table WHERE where.
     * Plain SELECT at default isolation level. 
     * @param columns a vector of columns to be selected
     * @param from the name of the table
     * @param where where clause
     * @return A tabel of data resulting from executing the query
     * @throws FatalException
     */ 
    public abstract ResultSet retrieve(String columns, String from, String where) 
        throws FatalException;
    
	/**
	 * SELECT columns FROM table WHERE where.
	 * Plain SELECT at default isolation level. 
	 * Vector seems to be general than ResultSet.
	 * The implementation mtould transform ResultSet to Vector.
	 * @param columns a vector of columns to be selected
	 * @param from the name of the table
	 * @param where where clause
	 * @return A tabel of data resulting from executing the query
	 * @throws FatalException
	 */	
	public abstract ResultSet retrieve(Vector columns, String from, String where) 
		throws FatalException;
		
	/**
	 * Plain SELECT or SELECT .. FOR UPDATE.
	 * @param columns a vector of columns to be selected
	 * @param table name of the table
	 * @param where where clause
	 * @param locked whether it is an exclusive read.
	 * @return A tabel of data resulting from executing the query
	 * @throws FatalException
	 */	
	
	public abstract ResultSet retrieve(Vector columns, String table, String where, boolean locked) 
		throws FatalException;
	
	
	/**
	 * SELECT...GROUP BY.
	 * @param columns a vector of columns to be selected
	 * @param table the name of the table
	 * @param where where clause
	 * @param groupBy a vector of group by attributes
	 * @return A tabel of data resulting from executing the query
	 * @throws FatalException
	 */	
	
	public abstract ResultSet retrieve(Vector columns, String table, String where, Vector groupBy) 
		throws FatalException;


	/**
	 * SELECT...GROUP BY...HAVING.
	 * @param columns a vector of columns to be selected 
	 * @param table the name of the table
	 * @param where where clause
	 * @param groupBy a vector of group by attributes
	 * @param having  having cluase
	 * @return A tabel of data resulting from executing the query
	 * @throws FatalException
	 */	

	public abstract ResultSet retrieve(Vector columns, String table, String where, Vector groupBy, String having)
		throws FatalException;

		
	/**
	 * For the preparedStatement, where two extra paramenters for the 
	 * arguments and the types of these arguments are needed.
	 * SELECT columns FROM table WHERE where.
	 * Plain SELECT at default isolation level. 
	 * Vector seems to be general than ResultSet.
	 * The implementation mtould transform ResultSet to Vector.
	 * @param columns a vector of columns to be selected
	 * @param table the name of the table
	 * @param where where clause
	 * @param args a vector of argument names
	 * @param types a vector of type constants of the corresponding arguments
	 * @return A table of data resulting from executing the query
	 * @throws FatalException
	 */		

	public abstract ResultSet retrieve(Vector columns, String table, String where,
		Vector args, Vector types)
		throws FatalException;

	
	/**
	 * SELECT MAX(column) FROM table WHERE where, used by OIDServer to get Max(DFID).
	 * The single column version of the above one.
	 * Plain SELECT at default isolation level.
	 * Vector seems to be general than ResultSet.
	 * The implementation mtould transform ResultSet to Vector. 
	 * @param column the column to be aggregated on
	 * @param from the name of the table
	 * @param where where clause
	 * @return A row of data resulting from executing the Max query
	 * @throws FatalException
	 */	

	public abstract ResultSet retrieveMax(String column, String from, String where) 
		throws  FatalException;


	/**
	 * SELECT MAX(columns) FROM table WHERE where, used by OIDServer to get Max(DFID).
	 * Plain SELECT at default isolation level.
	 * Vector seems to be general than ResultSet.
	 * The implementation mtould transform ResultSet to Vector. 
	 * @param columns the columns to be aggregated on
	 * @param from the name of the table
	 * @param where where clause
	 * @return A row of data resulting from executing the Max query
	 * @throws FatalException
	 */	
	public abstract ResultSet retrieveMax(Vector columns, String from, String where) 
		throws FatalException;
	 
	
//updates	
  /**
   * UPDATE table SET columns  = values.
   * Update some metadata in the data dictionary.
   * @param table  the name of the table whose data to be updated
   * @param setColumns a vector of the columns to be updated
   * @param values a vector of the values of the corresponding columns
   * @param where where clause
   * @return Row count for UPDATE statement
   * @throws FatalException
   */
	public abstract int update(String table, Vector setColumns, Vector values, String where)
		throws  FatalException;
     
}
