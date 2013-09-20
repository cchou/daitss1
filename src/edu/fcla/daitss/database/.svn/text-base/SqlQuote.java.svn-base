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

public class SqlQuote {
	
	/**
	 * escapes a string for use in MySQL query by escaping problem characters
	 * 
	 * @param parameter - string to be replaced
	 * @return Returns sanitized version of original string
	 */
	public static String escapeString(String parameter)
	{
		//look for pre-escaped quotes, and un-escape them to prevent double escaping
		parameter = parameter.replace("\\'", "'");
		parameter = parameter.replace("\\\"", "\"");
		
		//replace all quotes with escaped versions, and surround result with single quotes		
		parameter = parameter.replace("'", "''");
		parameter = parameter.replace("\"", "\\\"");
		
		parameter = "'" + parameter + "'";
		
		//testing -- retrieve stack trace for display
		/*Throwable myThrowable = new Throwable();
		StackTraceElement[] frames = myThrowable.getStackTrace();
		
		String esacpeCall = frames[0].toString();
		String caller = frames[1].toString();
		
		//testing -- dump out the stack trace information
		//System.out.println(esacpeCall);
		System.out.println("escapeString call from: " + caller + "\n");*/
		
		return parameter;
	}
	
	/**
	 * escapes an integer for us in MySQL queries
	 * 
	 * @param parameter
	 * @return santized string version of original
	 */
	public static String escapeInt(int parameter)
	{
		//cast into an integer, surround result with single quotes
		Integer myInt = new Integer(parameter);
		String returnVal = myInt.toString();
		returnVal = "'" + returnVal + "'";

		//testing -- retrieve stack trace for display
		/*Throwable myThrowable = new Throwable();
		StackTraceElement[] frames = myThrowable.getStackTrace();
		
		String esacpeCall = frames[0].toString();
		String caller = frames[1].toString();
		
		//testing -- dump out the stack trace information
		//System.out.println(esacpeCall);
		System.out.println("escapeInt call from: " + caller + "\n");*/		
		
		return returnVal;
	}
	

}
