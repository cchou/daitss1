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
 * Created on Jan 21, 2004
 * 
 * @author Althea L.
 *
 * Project: myDaitss
 *
 */
package edu.fcla.daitss.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;

/**
 * A class subclassing MySqlConnection that reprensets a tarnsaction connection to the MySQL
 * databases. It inherits implementions of the "retrieves", "inserts", "deletes" etc.
 * This class also implements TransactionConnection to allow transaction handling.
 * Instances of this method should be returned by calling DBConnection.getSharedConnection();
 */
public class MySqlTransactionConnection extends MySqlConnection 
	implements TransactionConnection{

	/**
	 * @param connection A jdbc Connection instance
	 * @throws FatalException 
	 */
	public MySqlTransactionConnection(Connection connection) 
		throws SQLException, FatalException {		
		super(connection);		
		connection.setAutoCommit(false);
	}
	
	/**
	 * To start a transaction in this connection.
	 * @throws FatalException
	 */
	public void startTransaction() throws FatalException{
		
		String methodName = "startTransaction()";
		try {
			connection.setAutoCommit(false);
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error in starting a connection",
			  "Database access",
			  se);
		}					
	}

	/**
	 * To commit the current transaction in this connection.
	 * @throws FatalException
	 */
	public void commitTransaction() throws FatalException{

		String methodName = "commitTransaction()";		
		try {
			connection.commit();
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error in closing a connection",
			  "Database access",
			  se);
		}		
	}

	/**
	 * To rollback the current transaction in this connection to the starting point.
	 * @throws FatalException
	 */
	public void rollbackTransaction() throws SQLException {
	    String methodName = "rollbackTransaction()";
	    connection.rollback();
	    /*		
		try {
			connection.rollback();
		}
		catch(SQLException se){
		  Informer.getInstance().fail(
			  this,
			  methodName,
			  "Error in closing a connection",
			  "Database access",
			  se);
		}				
		*/
	}
	
	public static void main(String[] args) {

		try{
			TransactionConnection tcnn = DBConnection.getSharedConnection();

			tcnn.startTransaction();

			Vector cols = new Vector();
			cols.addElement("CODE");
			String tbl = "SEVERITY";
			
			//test1
			System.out.println("\nTest1");
			ResultSet rs = tcnn.retrieve(cols, tbl, "");
			while(rs.next()){
				System.out.println("code: " + rs.getString("CODE"));
			}
			
			//test2
			String where = "DESCRIPTION=\"Reject the package\"";
			System.out.println("\nTest2");
			rs = tcnn.retrieve(cols, tbl, where);
			while(rs.next()){
				System.out.println("code: " + rs.getString("CODE"));
			}
			
			//test3			
			Vector orderby = new Vector();
			orderby.addElement("CODE");
			//where = "";
			System.out.println("\nTest3");
			rs = tcnn.retrieve(cols, tbl, where, null ,"",orderby);
			while(rs.next()){
				System.out.println("code: " + rs.getString("CODE"));
			}
			
			tcnn.commitTransaction();
			
			tcnn.close();	
		}
		catch (FatalException fe){
			System.out.println(fe.getMessage());
		}
		catch (SQLException se){
			System.out.println(se.getMessage());
		}
	}
}
