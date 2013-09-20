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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.Vector;
import java.util.Iterator;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

//test
import java.text.ParseException;

/**
 * A class subclassing DBConnection that reprensets a connection to the MySQL
 * databases. It implements the "retrieves", "inserts", "deletes" etc.
 * Instances of this class should be returned by DBConnection.getConnection();
 */
public class MySqlConnection extends DBConnection{
	
	/**
	 * Keyword for sql COLUMNS clause.
	 */
	public static final int COLUMNS = 3;
	
	/**
	 * Keyword for sql GROUP BY clause.
	 */
	public static final int GROUP = 0;
	
	/**
	 * The name of the maximum retrieved value
	 */
	public static final String MAX_RESULT = "MAX_RESULT"; 
	
	/**
	 * Keyword for sql ORDER BY clause.
	 */
	public static final int ORDER = 2;

	/**
	 * Keyword for sql SELECT clause.
	 */
	public static final int SELECT = 1;
	
	/**
	 * Type name constant for SQL characters
	 */
	public static final String SQL_CHARS = "SQL_CHARS";
	
	/**
	 * Type name constant for SQL Date
	 */
	public static final String SQL_DATE = "SQL_DATE";
	
	/**
	 * Type name constant for SQL integer
	 */
	public static final String SQL_INT = "SQL_INT";
	
	/**
	 * Keyword for sql VALUES clause.
	 */
	public static final int VALUES = 4;
	
	/**
	 * @param args
	 * @throws ParseException
	 * @throws FatalException
	 */
	public static void main(String[] args) throws ParseException, FatalException {
               
		try{
			DBConnection cnn = DBConnection.getConnection();

			/*Vector cols = new Vector();
			cols.addElement("DFID");
			String tbl = "DATA_FILE";
			
			//test1
			System.out.println("\nTest1");
			ResultSet rs = cnn.retrieveMax(cols, tbl, "");
			String keyStr = "";
			while(rs.next()){			
				keyStr = rs.getString(MAX_RESULT);
			}
			System.out.println("max DFID: " + keyStr);
			StringTokenizer st = new StringTokenizer(keyStr,"_");
			String alphaStr = st.nextToken().substring(1);
			String timeStr = st.nextToken();
			System.out.println("alpah, timeSTr:" + alphaStr +","+ timeStr);

			String stm = "INSERT INTO EVENT" 
			+"(OID, TYPE, DATE_TIME, OUTCOME, NOTE, REL_OID)" /* , PROCEDURE)"*/
			//+ " VALUES ('F20040621_AAAAAA', 'CPD', '2000-06-21 12:00:00',  'SUCCESS', 'Empty notes', 'X00000000_AAAAAA')";/*, '')";*/
			/*int affected = cnn.insert(stm);
			System.out.println("Insert done. Affected : " + affected);*/
			
			/*Vector cml = new Vector();
			cml.addElement("OID");
			cml.addElement("TYPE");
			cml.addElement("DATE_TIME");
			cml.addElement("OUTCOME");
			cml.addElement("NOTE");
			cml.addElement("REL_OID");
			
			String dstr = "2000-06-22 12:00:00";
			SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getInstance();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
			java.sql.Date sqld;
			java.util.Date dd = null; 
			//try{
				dd = sdf.parse(dstr);
			//}catch(ParseException pe){}
			sqld = new java.sql.Date(dd.getTime());
			Vector val = new Vector();
			//val.addElement(new Integer(59));
			val.addElement(new String("F20040622_AAAAAA"));
			val.addElement(new String("CPD"));
			val.addElement(sqld);
			//val.addElement("proc");
			val.addElement(new String("SUCCESS"));
			val.addElement(new String("None notes"));
			val.addElement(new String("X00000000_AAAAAA"));
			
			/*System.out.println(cnn.insert("EVENT", cml, val) + " is affected again."); */
			
			/* insert(table, values) is not suggested, if autoincrement key is used 
			 * and you have to give the autoincrement key to insert.*/
			//System.out.println(cnn.insert("EVENT", val) + " is affected again.");
	
			/*System.out.println(cnn.getAllColumns("EVENT").size());
			System.out.println(cnn.getAllColumns("TSM_STORAGE_DESC").size());
			cnn.insert("INSERT INTO DATA_FILE " 
			+ "(PATH, DESC, DFID, IEID, NODE, CREATE_DATE , FILE_COPY_DATE , DIP_VERSION , ORIGIN, ORIG_URI, FILE_TITLE , FILE_EXT, CONTENT_TYPE , MEDIA_TYPE, MEDIA_TYPE_VERSION , CREATOR_PROG, SIZE, BYTE_ORDER , IS_ROOT, IS_GLOBAL, IS_OBSOLETE, CAN_DELETE, ROLE, ANOMALIES, INHIBITORS, METADATA_CONFLICTS , QUIRKS, NUM_BITSTREAMS , PRES_LEVEL, VARIATION, PROFILES)" 
			+ "VALUES (\"C:\\althea\\doc\\runOID_windows.txt\", \"NOENE\" ,\"F00000000_AAAAAA\", \"E00000000_AAAAAA\", \"Althea1\", \"2003-06-22 12:12:12\", \"2004-06-22 12:12:12\", \"F00000000_AAAAAA\", \"DEPOSITOR\", \"http://www.fcla.edu\", \"dummy\", \"xsl\", \"I\", \"application/pdf\", \"2.0\", \"none\", 5, \"BE\", \"FALSE\", true, \"FALSE\", \"FALSE\", \"SCHEMA\", \"\", \"\", \"\", \"\", 0, \"BIT\", \"UNKOWN\", \"\")");
			cnn.close();*/
			Object str = null;
			//String str = new String("my string");
			//System.out.println("my string is the same as .toString " + ((str.toString()).equals(str)));
			System.out.println("Str is " + (String)str);
			
			Vector exps = new Vector();
			exps.addElement(null);
			exps.addElement("Second");
			exps.addElement(new Integer(3));
			
			//String cls = cnn.clause(MySqlConnection.VALUES, exps);
		}
		catch (FatalException fe){
			System.out.println(fe.getMessage());
		}
		//catch (SQLException se){
		//	System.out.println(se.getMessage());
		//}	

	}
	
	/**
	 * A jdbc Connection member
	 */
	Connection connection;
	
	 /**
	  * Constructor of the class
	  * @param connection A jdbc connection object
	  */
	public MySqlConnection(Connection connection){
	 	this.connection = connection;
	}

/**
 * A private method constructing various sql clauses for SELECT and INSERT statement,
 * e.g. VALUES ("X00000000_AAAAAA", "0000-00-00 00:00:00", 0)
 * e.g. (OID, INGEST_TIME, AP)
 * Elements of exps are not allowed to be null.
 * 
 * @param exps the Vector holding the expressions.
 * @param keyword the keywrod for this clause.
 * @return The clause
 * @throws SQLException
 * @throws FatalException 
 */
	private String clause(Vector exps, int keyword) 
		throws SQLException, FatalException{
			
		String methodName = "clause(Vector, int)";
		String cls = " ";
		boolean noneNumeric = false;
			
		int exs = exps.size();
		
		//Starting constructing the clause with the first element in the vector
		if (exs>0){
			String typeStr=null;			
			try{
				typeStr = exps.elementAt(0).getClass().getName();
			}catch(NullPointerException npe){
				throw new SQLException("Can not get the type name of " + (String)exps.elementAt(0)
						 + " Error when constructing an SQL select or set Clause. ");
			}
			
			//Non-Numeric value needs quotation marks. e.g. Insert into... values ("gizp"...)
			noneNumeric 
					= typeStr.equals("java.lang.String")|| typeStr.equals("java.sql.Date") 
					|| typeStr.equals("java.lang.Boolean");					
			switch (keyword){
					case GROUP:
						cls += "\nGroup By ";
						break;
					case ORDER:
						cls += "\n ORDER BY ";
						break;
					case VALUES:
						cls += "\n VALUES (";
						break;
					case COLUMNS:
						cls += "\n (";
						break;
					case SELECT:
						break;
					default:
						throw new SQLException(
								"In a SELECT or INSERT SQL statment," +
								"only SELECT, GROUP BY, ORDER BY, VALUES are legal keywords." +
								keyword + " can not be mapped to a legal keyword.");					
			}
			
			Object element = exps.elementAt(0);
			if (element == null){
				throw new SQLException("Elements of the vector are not allwed to be null."
							+ " Error when constructing an SQL select or set Clause. ");
			}
			else
				element = element.toString();
			
			//nonNumeric values have to be quoted
			if (keyword == VALUES && noneNumeric == true)
				cls += SqlQuote.escapeString(element.toString());
			else
				cls += element;
		}
			
			//continue constructing the clause with the rest elements in the vector
			for (int i = 1; i< exs; i++){ 
				String typeStr=null;
				
				try{
					typeStr = exps.elementAt(i).getClass().getName();
				}catch(NullPointerException npe){
					throw new SQLException("Can not get the type name of " + (String)exps.elementAt(i)
					 + " Error when constructing an SQL select or set Clause. ");
				}
				
				noneNumeric = typeStr.equals("java.lang.String") 
							|| typeStr.equals("java.sql.Date")
							|| typeStr.equals("java.lang.Boolean"); 
				
				Object element = exps.elementAt(i);
				if (element == null){
					if (keyword == VALUES)
						element = new String("null");
					else
						throw new SQLException("Elements of the vector can not be null."
								 + " Error when constructing an SQL select or set Clause. ");
				}
				else
					element = element.toString();
				if (keyword == VALUES && noneNumeric == true)
					cls += ", " + SqlQuote.escapeString(element.toString());
				else			
					cls += ", " + element;
			}		
			if (keyword == VALUES || keyword == COLUMNS){
				cls += ")";
			}
		return cls;
	}

	/**
	 * To construct a SET clause for Insert statement
	 * @param nameValues A vector of pairs :attribute names - attribute values
	 * @return the constructed SET clause
	 * @throws SQLException
	 * @throws FatalException
	 */
	protected String clauseSet(NameValueVector nameValues) 
		throws SQLException, FatalException{
		String methodName = "clauseSet(NameValueVector)";
		
		String cls = " SET";

		for (int i=0; i < nameValues.size(); i++) {
		    if (i > 0) {
		        cls += ", ";
		    }
		    cls += " " + nameValues.fieldAt(i) + " = " + (nameValues.valueAt(i) == null?"NULL":SqlQuote.escapeString(nameValues.valueAt(i).toString()));
		}
		
		/*
		int size = nameValues.size();

		boolean nonNumeric = true;
		if (size>0){
			Object element = nameValues.valueAt(0);
			if (element == null){// Does nothing. dbms will use its default value
								 // If no default value, equivalant to null value to this attribute
								 // If no null is allowed for this attribute, exceptions thrown
			}
			else{
				String typeStr = element.getClass().getName();
				if (typeStr == null)
					Informer.getInstance().fail(this,
							methodName,
							"Error constructing a clause",
							"SQL select or set clause",
							new SQLException("Can not get the type name of " + (String)element
									 + " Error when constructing an SQL select or set Clause. "));
				//Non-Numeric value needs quotation marks. e.g. Insert into... values ("gizp"...)
				nonNumeric = typeStr.equals("java.lang.String")|| typeStr.equals("java.sql.Date") 
						|| typeStr.equals("java.lang.Boolean");
				element = element.toString();
			
				cls += " SET " + nameValues.fieldAt(0)  + " = ";			
				
				if (nonNumeric == true)
						cls += "\"" + element + "\"";
				else
						cls += element;
			}//else
			for (int i = 1; i< size; i++){
				element = nameValues.valueAt(i);
				
				if (element==null){
				}
				else{
				
					//String typeStr = element.getClass().getName();
					String typeStr = element.getClass().getName();
					if (typeStr == null)
						Informer.getInstance().fail(this,
							methodName,
							"Error constructing a clause",
							"SQL select or set clause",
							new SQLException("Can not get the type name of " + (String)element
									 + " Error when constructing an SQL select or set Clause. "));				
			
					nonNumeric = typeStr.equals("java.lang.String")|| typeStr.equals("java.sql.Date") 
					|| typeStr.equals("java.lang.Boolean");			
					
					element = element.toString();
				//}
					cls += ", " + (String)nameValues.fieldAt(i) + " = ";
					if (nonNumeric == true)
						cls += "\"" + element + "\"";
					else
						cls += element;
					}
			}
		}
		*/
		return cls;		
	}
	
/**
 * A private method constructing the SET clause in UPDATE statement,
 * @param exps the Vector holding the expressions/values
 * @param columns the attributes to be set. 
 * @return The clause
 * @throws SQLException
 */
	private String clauseSet(Vector columns, Vector exps)
		throws SQLException{
		String cls = " ";
		boolean noneNumeric = false;
		int exs = exps.size();
		 
		cls += " SET ";

		for (int i = 0; i< exs; i++){
			if (i == 0)
				cls += columns.elementAt(i).toString() + " = ";
			else
				cls += ", " + columns.elementAt(i).toString() + " = ";
			// determine what kind of object this is.  if it is non numeric, it need " 
			String typeStr = null;			
			try{
				typeStr = exps.elementAt(i).getClass().getName();
			}catch(NullPointerException npe){
				throw new SQLException("Can not get the type name of " + exps.elementAt(i).toString()
				 + " Error when constructing an SQL SET Clause. ");
			}
			
			noneNumeric = typeStr.equals("java.lang.String") || typeStr.equals("java.sql.Date")
						|| typeStr.equals("java.lang.Boolean"); 
			
			Object element = exps.elementAt(i);
			if (element == null){
				element = new String("null");
			}
			else
				element = element.toString();
			
			//Manny - 10/11/07 -- because element is always a String (see if block directly above), cast as String and pass it to esacpeString for quoting and escaping regardless of numercality
			cls += SqlQuote.escapeString((String) element);
			
		}
		return cls;
	}

	/**
	 * To close the connection before jvm's gabbage collection.
	 * @throws SQLException 
	 */
	public void close() throws SQLException  {
		this.connection.close();
	}

	/**
	 * DELETE  FROM  table
	 * 	WHERE where.
	 * @param table table to be deleted.
	 * @param where where condition.
	 * @return row count affected by the DELETE statement
	 * @throws FatalException
	 */
	public int delete(String table, String where)
		throws FatalException{
			
		String methodName ="delete(String, String)";	
		int affected = 0;
		
		String sqlStmt = "DELETE FROM " + table;
		sqlStmt += " WHERE " +  where;
		
		try {
			affected = executeUpdate(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error in SQL update, quit",
			  "Update: " + sqlStmt,
			  se);
		}								
		return affected;
	}

	/**
	 * A public method for executing an sql query statement. To be used
	 * for complicated queries or statements that cannot be represented 
	 * in the 'convenience' methods (insert*, retrieve*).
	 * Some caller upper in the calling stack has to catch SQLExceptions 
	 * @param sql the sql statement string
	 * @return a ResultSet instance
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String sql) throws SQLException{
		ResultSet rs = null;
			
		Statement  stmt = this.connection.createStatement();	
        
		if (stmt.execute(sql)) {
			rs = stmt.getResultSet();
		}

		return rs;
	}
	
	
	
	/**
	 * For preparedSatement: requiring two additional vector arguments for the 
	 * arguments and types.
	 * e.g. [1, 2004-05-11] and [Int, Date] or ["UNKNOWN",2] and [String, Int]
	 * @param sql the SQL statement String.
	 * @param args a vector of parameters for the preparedStatment.
	 * @param types a vector of types of the corresponding parameters in the parameter vector. 
	 * @return result set representing query result.
	 * @throws SQLException
	 */
	
	private ResultSet executeQuery(String sql, 
		Vector args,
		Vector types) throws SQLException{

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		pstmt = 
				((java.sql.Connection)connection).prepareStatement(sql);
			
		int size = args.size();
		if (size != types.size())
			throw new SQLException("In prepared statement, the number of arguments " +
				"and that of the argument types does not match.");
					
		for (int j = 1; j<=size;j++){
				// only limited types handled for now
			String type = (String)types.elementAt(j-1); 
			if (type.equals(MySqlConnection.SQL_INT)){
				pstmt.setInt(j,
					((Integer)args.elementAt(j-1)).intValue());
			}
			else if (type.equals(MySqlConnection.SQL_DATE)){	
				pstmt.setDate(j,
					(java.sql.Date)args.elementAt(j-1));
			}
			else if (type.equals(MySqlConnection.SQL_CHARS)){
				pstmt.setString(j,
					(String)args.elementAt(j-1));
			}
		}
		return rs;
	}
	
	
	/**
	 * The private method for executing an sql update statement
	 * Some caller upper in the calling stack has to catch SQLExceptions 
	 * @param sql the SQL statement String.
	 * @return An integer indicating the row count.
	 * @throws SQLException
	 */
	private int executeUpdate(String sql)throws SQLException {
		boolean boolResult;
		int rows = 0;
				
		Statement stmt = 
			((java.sql.Connection)connection).createStatement();
			
		boolResult = stmt.execute(sql);
		if (boolResult == false)
			rows = stmt.getUpdateCount();
			
		return rows;
	}

	/**
	 * Get all the column names of a table
	 * @param table The name of the table.
	 * @return A vector of all the column names of the table
	 * @throws SQLException
	 */
	public Vector getAllColumns(String table) throws SQLException{
		Vector columns;
		columns = new Vector();
		DatabaseMetaData dbm = this.connection.getMetaData();
		ResultSet rs = dbm.getColumns(null,null,table,null/*"([a-z]|[A-Z])*(_([a-z]|[A-Z])*)?"*/);
		int i = 0;
		while (rs.next()){
			String name = rs.getString("COLUMN_NAME");
			if (/*!name.equalsIgnoreCase("path")&&*/ !name.equalsIgnoreCase("DESC"))//path, the reserved word
				columns.addElement(name);
		}
		return columns;
	}
	
	/**
	 * Get all nullable column names of a table
	 * @param table The name of the table
	 * @return a vector of all the nullable column names of the table
	 * @throws SQLException
	 */
	public Vector getAllNullables(String table) throws SQLException{
		Vector columns;
		columns = new Vector();
		DatabaseMetaData dbm = this.connection.getMetaData();
		ResultSet rs = dbm.getColumns(null,null,table,null/*"([a-z]|[A-Z])*(_([a-z]|[A-Z])*)?"*/);
		int i = 0;
		while (rs.next()){
			String name = rs.getString("COLUMN_NAME");
			int nuable = rs.getInt("NULLABLE");
			if (nuable == DatabaseMetaData.attributeNullable
					|| nuable == DatabaseMetaData.attributeNullableUnknown)//path, the reserved word
				columns.addElement(name);
		}
		return columns;
	}
/**
 * Execute stmtString, which is an INSERT statement.
 * @param stmtString The insert statement string.
 * @return  The number of records inserted.
 * @throws FatalException
 */
	
  public int insert(String stmtString) 
		  throws  FatalException{
		  	
		String methodName = "insert(String)";  	
		int affectedNum = 0;

		try{
		  affectedNum = executeUpdate(stmtString);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error of SQL update, quit",
			  "Insert: " + stmtString,
			  se);
		}			
		return affectedNum;
	}	
   
  /**
   * Insert INTO table SET field1 = val1, field2 = val2, ...
   * @param table The name of the table to be inserted.
   * @param nameValues A vector of the corresponding values for the attributes, use null to identify the null value.
   * @return The number of records inserted.
   * @throws FatalException
   */
	public int insert(String table, NameValueVector nameValues) 
		throws FatalException{
		String methodName = "insert(String, NameValueVector)";
		
		int affected = 0;
		String stmtString = "";
		try{                                              
			stmtString = "INSERT INTO " + table;
			stmtString += this.clauseSet(nameValues);
            
            Informer.getInstance().info(
                    this,
                    methodName,
                    "SQL insert",
                    "Insert: " + stmtString,
                    false);     
            
            
			affected = executeUpdate(stmtString);		  
		  
		}catch(SQLException sqle){
			Informer.getInstance().fail(
			this,
			methodName,
			"Can not insert into table: " + table,
			"Error in SQL insert: " + stmtString,
			sqle);
	  
		}
	  return affected;
  }

  /**
   * Insert INTO table VALUES (values)
   * @param table The name of the table to be inserted.
   * @param values A vector of the corresponding values for the attributes, use null to identify the null value.
   * @return The number of records inserted.
   * @throws FatalException
   */
	public int insert(String table, Vector values) 
		throws FatalException{
		String methodName = "insert(String, Vector)";
		
		int affected = 0;
	  try{
	  	  // all columns
		  Vector columns = this.getAllColumns(table);	  
		  // non-null column names
		  Vector nncolumns = new Vector();
		  // non-null values
		  Vector nnvalues = new Vector();
		  // build non-null vectors
		  for (Iterator cit = columns.iterator(), vit = values.iterator(); cit.hasNext() && vit.hasNext();){
		  	Object ele = vit.next();
		  	if (ele!=null){
		  		nncolumns.add(cit.next());
		  		nnvalues.add(ele);
		  	}
		  	else
		  		cit.next();
		  }
		  
		  affected = insert(table, nncolumns, nnvalues);
		  
	  }catch(SQLException sqle){
	  	 Informer.getInstance().fail(
			this,
			methodName,
			"Cannot get column names for table " + table,
			"Error in SQL query" ,
			sqle);
	  }catch(NullPointerException npe){
	  	 Informer.getInstance().fail(
				this,
				methodName,
				"Null table name",
				"Error in SQL query",
				npe);	  	
	  }
	  return affected;
  }

	  /**
	   * Insert INTO table columns VALUES (values)
	   * @param table The name of the table to be inserted.
	   * @param columns A vector of the attribute names, no null objects allowed in the vector. 
	   * @param values A vector of the corresponding values for the attributes, no null objects allowed in the vector.
	   * @return The number of records inserted.
	   * @throws FatalException
	   */
	public int insert(String table, Vector columns, Vector values) 
  		throws FatalException{
		  String methodName = "insert(String, Vector, Vector)";
		  
		  String stmtString = "INSERT INTO " + table;
		  int affectedNum=0;
		  
		  if (columns == null || values == null)
		  	Informer.getInstance().fail(this,
					methodName,
					"Error of SQL insert: Null column vector or null value vector in this update",
					"SQL insert",
					new SQLException("Can not construct the insert statement"));
		  if (table == null)
		  	Informer.getInstance().fail(this,
					methodName,
					"Error of SQL insert: Null table name in this update",
					"SQL insert",
					new SQLException("Can not construct the insert statement"));
	  
		int exs = values.size();
		int col = columns.size();		 
		if (exs != col)
		  	Informer.getInstance().fail(this,
					methodName,
					"Error of SQL insert: unmatched number of columns and attributes",
					"SQL insert",
					new SQLException("Error when constructing the insert clause. "
							+ "The number of the attributes and of the values don't match"));			
	  
	  try{			
		stmtString += clause(columns, MySqlConnection.COLUMNS);	
		stmtString += clause(values, MySqlConnection.VALUES);		
 
 	  	Informer.getInstance().info(
			this,
			methodName,
			"SQL insert",
			"Insert: " + stmtString,
			false);

	  	affectedNum = executeUpdate(stmtString);
	  }
	  catch(SQLException se){
	  	Informer.getInstance().fail(
			this,
			methodName,
			"Error of SQL insert, quit",
			"Insert: " + stmtString,
			se);
	  }			
	  return affectedNum;
  }
	
	/**
	 * To check if the connection has been closed for some reason.
	 * @return True if it is closed, otherwise false.
	 */
	public boolean isClosed() {
		
		String methodName = "isClosed()";
		boolean isclosed = true;		
				
		try {
			isclosed = this.connection.isClosed();
		}
		catch(SQLException se){
            // if there was an error checking the connection state, 
            // assume is is closed (or equivalent to closed)
            isclosed = true;
        }
		
		return isclosed;				
	}
	
	/**
	 * SELECT column FROM table WHERE where- the "one column" version of the above method.
	 * Plain SELECT at default isolation level. 
	 * @param table table name
	 * @param column column name
	 * @param where where clause
	 * @return a ResultSet object.
	 * @throws FatalException	 
	*/	
	public ResultSet retrieve(String column, String table, String where) 
		throws FatalException{
			String methodName = "retrieve(String, String, String)";
			ResultSet rs = null;
			String sqlStmt = "SELECT " + column + " FROM " + table;
			
			if (!where.equals(""))
				sqlStmt += " WHERE " + where;
			else
				sqlStmt += " WHERE 1";

			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query " + sqlStmt,
				false);				
			
			try{
				rs = executeQuery(sqlStmt);
			}
			catch(SQLException se){
			  Informer.getInstance().fail(
				this,
				methodName,
				"Error of SQL query, quit",
				"Query: " + sqlStmt,
				se);
			}				
			return rs;
	}
	
	/**
	 * SELECT columns FROM table WHERE where.
	 * Plain SELECT at default isolation level. 
   	 * @param table table name
     * @param columns a vector of the attribute names.
     * @param where where clause
	 * @return a ResultSet object.	 
	 * @throws FatalException
 	*/	
	public ResultSet retrieve(Vector columns, String table, String where) 
		throws  FatalException {
			
		String methodName = "retrieve(Vector, String, String)";
		ResultSet rs = null;
		String sqlStmt = "";
		try{
			sqlStmt = "SELECT " + clause(columns, MySqlConnection.SELECT);
			sqlStmt +=  " FROM " + table;
			if (!where.equals(""))
				sqlStmt += " where " + where;
		
			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);
			
			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
				this,
				methodName,
				"Error in SQL retrieval, quit",
				"Query: " + sqlStmt,
				se);
		}		
		return rs;
	}

	/**
	 * Plain SELECT or SELECT .. FOR UPDATE.
	 * @param columns columns to be selected.
	 * @param table the table to be retrieved from.
	 * @param where the where condition for the records to be retrieved.
	 * @param locked Used to identify whether it is an exclusive read.
	 * @return a ResultSet object
	 * @throws FatalException
	 */
	
	public ResultSet retrieve(Vector columns, String table, String where, boolean locked)
		throws  FatalException{
			
		String methodName = "retrive(Vector, String, String, boolean)";
		String sqlStmt = "";	
		ResultSet rs = null;

		try{
			sqlStmt = "SELECT " + clause(columns, MySqlConnection.SELECT);
			sqlStmt +=  " FROM " + table;
			if (!where.equals(""))
				sqlStmt += " WHERE " + where;
		
			if (locked == true)
				sqlStmt += " FOR UPDATE";
		
			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);	
					
			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
				this,
				methodName,
				"Error of SQL query, quit",
				"Query: " + sqlStmt,
				  se);
		}					
		return rs;		
	}
	
	
	/**
	 * SELECT ... GROUP BY.
	 * Caller must catch SQLException.
	 * @param columns columns to be selected.
	 * @param table the table to be retrieved from.
	 * @param where the where condition for the records to be retrieved.
	 * @param groupBy group results according the groupBy attribute
	 * @return a ResultSet object.
	 * @throws FatalException
 	*/	
	
	public ResultSet retrieve(Vector columns, String table, String where, Vector groupBy)
		throws FatalException{
			
		String methodName = "retrieve(Vector, String, String, Vector)";;
		String sqlStmt = "";	
		ResultSet rs = null;
		try{
			sqlStmt = "SELECT " + clause(columns, MySqlConnection.SELECT); 
			sqlStmt += " FROM " + table;
			if (!where.equals(""))
				sqlStmt += " WHERE " + where;

			if (groupBy!= null && groupBy.size()>0)
				sqlStmt += 	clause(groupBy, MySqlConnection.GROUP);			

			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);
						
			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
			Informer.getInstance().fail(
				this,
				methodName,
				"Error of SQL query, quit",
				"Query: " + sqlStmt,
				se);
		}				
		return rs;
	}
	
	/**
	 * SELECT ... GROUP BY ... HAVING.
	 * Caller must catch SQLException.
	 * @param columns Columns to be selected. 
	 * @param table The table to be retrieved from.
	 * @param where The where condition for the records to be retrieved.
	 * @param groupBy Group by cluases.
	 * @param having Having cluases.
	 * @return a ResultSet object.
	 * @throws FatalException
	 */	 
	
	public ResultSet retrieve(Vector columns, String table, String where, Vector groupBy, String having)
		throws FatalException{
			
		String methodName = "retrive(Vector, String, String, Vector, String)";	
		String sqlStmt = "";
		ResultSet rs = null;
		
		try{
			sqlStmt = "SELECT " + clause(columns, SELECT); 
			sqlStmt += " FROM " + table;
			if (!where.equals(""))
				sqlStmt += " WHERE " + where;

			if (groupBy!= null && groupBy.size()>0)
				sqlStmt += 	clause(groupBy, MySqlConnection.GROUP);	
		
			if (!having.equals(""))
				sqlStmt += " HAVING " + having;		

			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);	
			
			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
				this,
				methodName,
				"Error in SQL query, quit",
				"Query: " + sqlStmt,
				se);
		}			
				
		return rs;
	}
	
	/**
	 * SELECT ...WHERE ... GROUP BY ... HAVING ... ORDER BY.
	 * Caller must catch SQLException.
	 * @param columns columns to be selected. 
	 * @param table the name of the table.
	 * @param where where clause.
	 * @param groupBy group by clauses.
	 * @param having having clauses.
	 * @param orderBy order by clauses.
	 * @return a ResultSet object.
	 * @throws FatalException
	 */		
	
	public ResultSet retrieve(Vector columns, String table, String where, Vector groupBy, String having, Vector orderBy)
		throws FatalException{
			
		String methodName = 
					"retrieve(Vector, String, String, Vector, String, Vector)";	
		String sqlStmt = ""; 
		ResultSet rs = null;
		
		try{	
			sqlStmt = "SELECT " + clause(columns, SELECT); 
			sqlStmt += "\nFROM " + table;
			if (!where.equals(""))
					sqlStmt += "\nWHERE " + where;
	
			if (groupBy!= null && groupBy.size()>0)
				sqlStmt += 	clause(groupBy, MySqlConnection.GROUP);	
		
			if (!having.equals(""))	
				sqlStmt += " HAVING " + having;
		
			if (orderBy!=null && orderBy.size()>0)
				sqlStmt += clause(orderBy, MySqlConnection.ORDER);		
	
			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);			

			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error in SQL query, quit",
			  "Query: " + sqlStmt,
			  se);
		}			
					
		return rs;
	}
	
	/**
	 * For preparedStatement.
	 * SELECT columns FROM table WHERE where.
	 * Plain SELECT at default isolation level. 
   	 * @param table table name
     * @param columns a vector of the attribute names.
     * @param where where clause
     * @param args ?
     * @param types ?
	 * @return a ResultSet object.
	 * @throws FatalException	 
	*/	
	
	public ResultSet retrieve(Vector columns, String table, String where,
		Vector args, Vector types) 
		throws FatalException {
		
		String methodName = "retrieve(Vector, String, String, Vector, Vector)";	
			
		ResultSet rs = null;
		String sqlStmt = "";
		
		try{
			sqlStmt = "SELECT " + clause(columns, MySqlConnection.SELECT);
			sqlStmt +=  " FROM " + table;
			if (!where.equals(""))
				sqlStmt += " where " + where;
			
			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);
			rs = executeQuery(sqlStmt, args, types);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
		  		this,
				methodName,
				"Error of parameterized SQL query, quit",
				"Query: " + sqlStmt,
				se);
		}		
		return rs;
	}	
	
	/**
	 * SELECT MAX(column) FROM table WHERE where, not sure if multiple columns work.
	 * Plain SELECT at default isolation level.
	 * Use by OIDServer for Max(DFID).
	 * Used on single column.
     * @param table the name of the table to be inserted.
     * @param column a vector of the attribute names. 
     * @param where the where condition for the records to be retrieved.
	 * @return a ResultSet object.	
	 * @throws FatalException 
	*/
	
	public ResultSet retrieveMax(String column, String table, String where) 
		throws  FatalException {
		
		String methodName = "retrieveMax(String, String, String";	
			
		ResultSet rs = null;
	
		String sqlStmt = "SELECT MAX(" + column + ") AS " + MAX_RESULT;
	
		sqlStmt +=  " FROM " + table;
		if (!where.equals(""))
				sqlStmt += " where " + where;
			
		Informer.getInstance().info(
			this,
			methodName,
			"SQL query",
			"Query: " + sqlStmt,
			false);		
		try{
			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error of SQL query, quit",
			  "Query: " + sqlStmt,
			  se);
		}					
		return rs;
	}
		
	/**
	 * SELECT MAX(columns) FROM table WHERE where, not sure if multiple columns work.
	 * Plain SELECT at default isolation level.
	 * Use by OIDServer for Max(DFID).
	 * Used on multiple columns.
     * @param table the name of the table to be inserted.
     * @param columns a vector of the attribute names. 
     * @param where the where condition for the records to be retrieved.
	 * @return a ResultSet object.
	 * @throws FatalException	 
	*/	
	public ResultSet retrieveMax(Vector columns, String table, String where) 
		throws  FatalException {
		
		String methodName = "retrieveMax(Vector,String,String)";
		String sqlStmt = "";
		ResultSet rs = null;
	
		try{			
			sqlStmt = "SELECT MAX(" + 
				clause(columns, MySqlConnection.SELECT) + ") AS " + 
				MAX_RESULT;
			
			sqlStmt +=  " FROM " + table;
			if (!where.equals(""))
				sqlStmt += " where " + where;
			
			Informer.getInstance().info(
				this,
				methodName,
				"SQL query",
				"Query: " + sqlStmt,
				false);	
				
			rs = executeQuery(sqlStmt);
		}
		catch(SQLException se){
			Informer.getInstance().fail(
				this,
				methodName,
				"Error of SQL query, quit",
				"Query: " + sqlStmt,
				se);
		}		
	
		return rs;
	}

//updates
	/**
	 * UPDATE table 
	 * 	SET columns[1] = values[1], columns[2] = values[2],...
	 *  WHERE where
	 * caller must catch SQLException.
	 * @param table table name.
	 * @param columns name of the columns to be updated.
	 * @param values expressions to be assigned to the columns.
	 * @param where where clause
	 * @return the number of recordes updated.
	 * @throws FatalException
	 */
	public int update(String table, Vector columns, Vector values, String where)
		throws FatalException{
		
		String methodName ="update(String, Vector, Vector, String)";	
		int affected = 0;
		
		String sqlStmt = " UPDATE " + table;
		try {
			sqlStmt += clauseSet(columns,values);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Bad SQL update syntacs, the number of values and attributes don't match",
			  columns.toString() + " and " + values.toString(),
			  se);
		}						
		sqlStmt += " WHERE " +  where; 

		Informer.getInstance().info(
			this,
			methodName,
			"SQL update",
			"Update: " + sqlStmt,
			false);
						
		try {
			affected = executeUpdate(sqlStmt);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error in SQL update, quit",
			  "Update: " + sqlStmt,
			  se);
		}				
		return affected;
	}
}
