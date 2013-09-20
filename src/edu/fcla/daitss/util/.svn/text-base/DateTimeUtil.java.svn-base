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
package edu.fcla.daitss.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.text.ParseException;

import edu.fcla.daitss.exception.FatalException;

/**
 * DateTimeUtil encapsulates all the date and time functions needed by DAITSS.
 * The date/time format used in the archive is YYYY-MM-DD HH:MM:SS 
 * (24 hour clock) assumed to be in the archive's local time zone.
 * 
 * @author vicary, althea
 *
 */
public class DateTimeUtil {


	/**
	 * All report date times should be in GMT
	 * xsd:dateTime pattern for <code>SimpleDateFormat</code>
	 */
	public static String SCHEMA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	
	
	/**
	 * Converts the date format used by PDF files to the archive date format.
	 * This format closely follows the international standard ASN.1 (Abstract
	 * Syntax Notation One) defined in ISO/IEC 8824.
	 * 
	 * The date pattern is D:YYYYMMDDHHmmSSOHH'mm'
	 * where YYYY is the year
	 * MM is the month
	 * DD is the day (01-31)
	 * HH is the hour (00-23)
	 * mm is the minute (00-59)
	 * SS is the second (00-59)
	 * O is the relationship of local time to Universal Time(UT), denoted by
	 * one of the following characters +,-,or Z	
	 * @param date in format D:YYYYMMDDHHmmSSOHH'mm'
	 * @return a date/time in format  YYYY-MM-DD HH:MM:SS (24 hour clock)
	 * @throws IllegalArgumentException 
	 * @throws FatalException
	 */
	public static String convertDatePdf2Arch(String date)
		throws IllegalArgumentException, FatalException {
		String newDate = null;

		if (isBadPdfDate(date)) {
			throw new IllegalArgumentException("Bad pdf date time format.");
		} else 	if (date.charAt(0) == 'D' && date.charAt(1) == ':') { 
			// "D:" prefix is optional
			date = date.substring(2);
		}
		
		StringTokenizer st = new StringTokenizer(date, "D:Z+-'");

		// get the YYYYMMDDHHmmSS part
		// assuming that a field could not be present unless all its larger fields are present
		String dateStr = st.nextToken();
		int length = dateStr.length();

		String year = "";
		String month = "01";
		String day = "01";
		String hour = "00";
		String minute = "00";
		String second = "00";
	
		String zee = "";
		String deltaH = "";
		String deltaM = "";		

		switch (length) {
			case 18:
				deltaM = dateStr.substring(18,16);
			case 16:
				deltaH = dateStr.substring(14, 16);
			case 14 :
				second = dateStr.substring(12, 14);
			case 12 :
				minute = dateStr.substring(10, 12);
			case 10 :
				hour = dateStr.substring(8, 10);
			case 8 :
				day = dateStr.substring(6, 8);
			case 6 :
				month = dateStr.substring(4, 6);
			case 4 :
				year = dateStr.substring(0, 4);
				break;
			default :
				throw new IllegalArgumentException("Incorrect Date Format. Must be YYYYMMddhhmmss.");

		}

		//process the UT-difference part
		int deltah = 0; //how many hours they are different from ut
		int deltam = 0; //how many minutes they are different from ut
		try{
	
			if (!deltaH.equals("")){
				deltah = Integer.parseInt(deltaH);
			}
			if (!deltaM.equals("")){
				deltam = Integer.parseInt(deltaM);
			}
		}
		catch(NumberFormatException nfe){
			throw new IllegalArgumentException();
		}
		
		if (date.length() != length) {
			
			if (st.hasMoreTokens()) {
	
				if (st.countTokens() != 1 && st.countTokens() != 2) {
					throw new IllegalArgumentException("Incorrect time difference to UT");
				}
	
				zee = date.substring(length, length + 1);
	
				if (st.hasMoreTokens())
					deltaH = st.nextToken();
	
				//allow difference in the following formats hhmm, hh, mm, where hh:0-23, mm: 0-59
				if (Integer.valueOf(deltaH).intValue() > 59) {
					throw new IllegalArgumentException("Incorrect time difference to UT");
				}
				if (Integer.valueOf(deltaH).intValue() > 23) {
					deltaM = deltaH;
					deltaH = "00";
				}
	
				if (st.hasMoreTokens()) {
					if (deltaM.equals("")) {
						deltaM = st.nextToken();
					} else {
						throw new IllegalArgumentException("Incorrect time difference to UT");
					}
				} else {
					if (deltaM.equals(""))
						deltaM = "00";
				}
	
				//System.out.println("H:" + deltaH + ",M:" + deltaM);
	
				deltah = Integer.valueOf(deltaH).intValue();
				deltam = Integer.valueOf(deltaM).intValue();
	
				if (zee.equals("+")) {
					deltah = -deltah;
					deltam = -deltam;
				}
			}
		}
		newDate = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

		//convert their local time to archive local time
		newDate = normalizeDate(year + month + day + hour + minute + second,
					deltah, deltam);
		//System.out.println("their difference to ut: " + deltah + "," + deltam);

		return newDate;
	}
	/**
	 * Converts a date/time in YYYY:MM:DD HH:MM:SS (24 hour clock)
	 * into the format used in the archive database: YYYY-MM-DD HH:MM:SS
	 * (24 hour clock). Both are assumed to be in the same time zones.
	 * This is the date format used in Tiff files.
	 * 
	 * @param date a date/time in format YYYY:MM:DD HH:MM:SS (24 hour clock)
	 * @return a date/time in format  YYYY-MM-DD HH:MM:SS (24 hour clock)
	 * @throws IllegalArgumentException
	 */

	public static String convertDateTiff2Arch(String date)
		throws IllegalArgumentException {
		if (isBadTiffDate(date))
			throw new IllegalArgumentException("DateTime in bad format.");

		Calendar cld = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		String newDateStr = "";
		Date newDate = new Date();
		try {
			newDate = sdf.parse(date);
		} catch (ParseException pe) {
			throw new IllegalArgumentException("Incorrect date/time format (YYYY:MM:DD HH:MM:SS)");
		}

		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		newDateStr = sdf.format(newDate);
		return newDateStr;

	}

	/**
	 * Get the current date string as "yyyyMMdd".
	 * @return the current date
	 */
	public static String getCurrentDate() {
		SimpleDateFormat pattern = new SimpleDateFormat("yyyyMMdd");
		Calendar now = Calendar.getInstance();
		pattern.setCalendar(now);
		return pattern.format(now.getTime());
	}

	/**
	 * Get the current date time string as "yyyyMMddHHmmss".
	 * @return the current date and time
	 */
	public static String getDateTimeStamp() {
		SimpleDateFormat pattern = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar now = Calendar.getInstance();
		pattern.setCalendar(now);
		return pattern.format(now.getTime());
	}
	
	/**
	 * Get the current date string as "dd".
	 * @return the current day of the month
	 */
	public static int getDayOfMonth() {
	    Calendar now = Calendar.getInstance();
	    return now.get(Calendar.DATE);
	}
	
	
	/**
	 * Get the current month as an integer between 1-12 inclusive.
	 * @return the current month
	 */
	public static int getMonth() {
	    Calendar now = Calendar.getInstance();
	    // need to add 1 to the month because months are numbered by index
	    // position 
	    return now.get(Calendar.MONTH) + 1;	    
	}
	
	/**
	 * Get the current time string as "HHmmss".
	 * @return the current time
	 */
	public static String getTimeStamp() {
		SimpleDateFormat pattern = new SimpleDateFormat("HHmmss");
		Calendar now = Calendar.getInstance();
		pattern.setCalendar(now);
		return pattern.format(now.getTime());
	}

	/**
	 * Get the current year as a 4 digit integer.
	 * @return the current year
	 */
	public static int getYear() {
	    Calendar now = Calendar.getInstance();
	    return now.get(Calendar.YEAR);
	}

	/**
	 * Checks if the date string given is bad pdf date format or not.
	 * @param date : the pdf date string to be checked upon
	 * @return a boolean value representing if the input date string is bad or not
	 */
	public static boolean isBadPdfDate(String date) {
		boolean isBadFormat = false;
		int len = 0;
		if (date != null 
			&& (len = date.length()) >= 4 
			&& len < 24) {
			if (date.charAt(0) == 'D' && date.charAt(1) == ':') {
				date = date.substring(2);
			} 

			if ((len = date.length()) < 4
				||(!Character.isDigit(date.charAt(0))
					|| !Character.isDigit(date.charAt(1))
					|| !Character.isDigit(date.charAt(2))
					|| !Character.isDigit(date.charAt(3)))) {
				isBadFormat = true;
			}
			
			StringTokenizer st = new StringTokenizer(date,"Z+-'");
	
			for (int i = 0; i < len; i++ ){
					char ch = date.charAt(i);
					if (!Character.isDigit(ch)
					&& ch != 'Z'
					&& ch != '+'
					&& ch != '-'
					&& ch != '\''){
								isBadFormat = true;
					}
			}			
		} else
			isBadFormat = true;
		return isBadFormat;
	}
	
	
	/**
	 * Checks if the date string given is valid format (YYYY:MM:DD HH:MM:SS) or not. 
	 * @param date : The date string to be checked upon
	 * @return a boolean value representing if the input date string is bad or not
	 */
	public static boolean isBadTiffDate(String date) {
		StringTokenizer st = new StringTokenizer(date, " :");
		boolean badFormat = false;

		try {
			if (st.countTokens() == 6) {
				String y = st.nextToken();
				if (y.length() != 4 || Integer.parseInt(y) < 0) {

					badFormat = true;
				}

				String m = st.nextToken();
				if (m.length() != 2
					|| Integer.parseInt(m) > 12
					|| Integer.parseInt(m) < 1) {

					badFormat = true;
				}
				String d = st.nextToken();
				if (d.length() != 2
					|| Integer.parseInt(d) > 31
					|| Integer.parseInt(d) < 1) {

					badFormat = true;
				}
				String h = st.nextToken();
				if (h.length() != 2
					|| Integer.parseInt(h) > 23
					|| Integer.parseInt(h) < 0) {

					badFormat = true;
				}

				String mn = st.nextToken();
				if (mn.length() != 2
					|| Integer.parseInt(mn) > 59
					|| Integer.parseInt(mn) < 0) {

					badFormat = true;
				}

				String s = st.nextToken();
				if (s.length() != 2
					|| Integer.parseInt(s) > 59
					|| Integer.parseInt(s) < 0) {

					badFormat = true;
				}

			} else {
				badFormat = true;
			}
		} catch (NumberFormatException nfe) {
			badFormat = true;
		}

		return badFormat;

	}
	
	/**
	 * Determines whether or not a date/time is in the archive's
	 * date/time format (YYYY-MM-DD HH:MM:SS).
	 * I added the pattern matching because I needed something now, but it
	 * can be improved on by making sure that the different parts of the 
	 * date are within allotted numeric range.-chris  
	 * @param date	a date/time
	 * @return <code>true</code> if its in the archive's date/time
	 * 	format, else <code>false</code>
	 */
	public static boolean isInArchiveDateFormat(String date) {		

		try{
			String year = date.substring(0,4);
			String month = date.substring(5,7);
			String day = date.substring(8,10);
			String hour = date.substring(11,13);
			String min = date.substring(14,16);
			String sec = date.substring(17,19);
			
			int yearInt = Integer.parseInt(year);
			int monthInt = Integer.parseInt(month);
			int dayInt = Integer.parseInt(day);
			int hourInt = Integer.parseInt(hour);
			int minInt = Integer.parseInt(min);
			int secInt = Integer.parseInt(sec);
			
			if (yearInt>=0 &&
				monthInt >= 1 && monthInt <= 12 &&
				dayInt >= 1 && dayInt <= 31 &&
				hourInt >= 0 && hourInt < 24 &&
				minInt >= 0 && minInt < 60 &&
				secInt >= 0 && secInt < 60)
				{
					return date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
				}
			else
					return false;				
		}
		catch(IndexOutOfBoundsException iobe){
			return false;
		}		
		catch(NumberFormatException nfe){
			return false;
		}
	}
	
	/**
	 * Determines whether or not <code>date1</code> is later than
	 * <code>date2</code>. Both dates are assumed to be in the archive's
	 * date/time format: YYYY-MM-DD HH:MM:SS (but this should be
	 * checked with the isInArchiveDateFormat(String) method in this
	 * class).
	 * 
	 * @param date1	a date/time in the archive format
	 * @param date2	a date/time in the archive format
	 * @return	<code>true</code> if <code>date1</code> is later
	 * 	than <code>date2</code>, else <code>false</code>
	 */
	public static boolean isLaterThan(String date1, String date2){
		boolean isLater = false; // needs to be false in the finished method
		
		try{
			if (DateTimeUtil.isInArchiveDateFormat(date1) && 
				DateTimeUtil.isInArchiveDateFormat(date2)){
					
					SimpleDateFormat pattern = (SimpleDateFormat)DateFormat.getInstance();
					pattern.applyPattern("yyyy-MM-dd HH:mm:ss");
					
					Date ddate1 = pattern.parse(date1);
					Date ddate2 = pattern.parse(date2);
					
					if (ddate1.after(ddate2))
						isLater = true;
			}
		}
		catch(ParseException pe){	
		}
		finally{
			return isLater;
		}	
	}


	/**
	 * Module for testing
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
        
        System.out.println(now());
		DateTimeUtil dtu = new DateTimeUtil();
		String time = "";
		
	    System.out.println("current date: " + DateTimeUtil.getCurrentDate());
	    System.out.println("current year: " + DateTimeUtil.getYear());
	    System.out.println("current month: " + DateTimeUtil.getMonth());
	    System.out.println("current day: " + DateTimeUtil.getDayOfMonth());
	    		    
		time = "1234-12-12 12:12:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "123412-12 12:12:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "1234-12-12  12:12:12";
		
		System.out.println("now: " + now());
		System.out.println(isLaterThan(now(),"2004-05-16 12:12:12"));
		/*System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "1234-13-12 12:12:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "1234-12-32 12:12:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "1234-12-12 25:12:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));			
		time = "1234-12-12 12:61:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "1234-12-12 12:12:61";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));
		time = "1234-12-12 12:12";
		System.out.println(time + " is valid archive date: " + isInArchiveDateFormat(time));									
		/*
		boolean isBad = DateTimeUtil.isBadFormat1("2004:01:22 12:30:5c");
		System.out.println("\n2004:01:22 12:30:5c, isBad is " + isBad);
		isBad = DateTimeUtil.isBadFormat1("2004:01:22 ");
		System.out.println("\n2004:01:22 , isBad is " + isBad);
		isBad = DateTimeUtil.isBadFormat1("2004:1:22 12:30:55");
		System.out.println("\n2004:1:22 12:30:55, isBad is " + isBad);
		isBad = DateTimeUtil.isBadFormat1("2004:01:22 12:69:55");
		System.out.println("\n2004:01:22 12:69:55, isBad is " + isBad);
		isBad = DateTimeUtil.isBadFormat1("\n2004:01:22 12:30:55");
		System.out.println("2004:01:22 12:30:55, isBad is " + isBad);

		time = DateTimeUtil.convertDate1("2004:01:22 12:30:55");
		System.out.println("\nconverted2 time: " + time + "\n");

		isBad = DateTimeUtil.isBadFormat2("20030122");
		time = DateTimeUtil.convertDate2("D:20030122-08'00'");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("D:20030122025959+08'26'");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("D:20030122025959Z");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("D:20030122025959-55'");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("20030122025959-23'25'");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("D:20030122025959");
		System.out.println("\nD:2003012202595c,converted3 time: " + time + "\n");
		
		/////////////////////new test dates from Andrea
		time = DateTimeUtil.convertDate2("D:20021120141548-08'00'");
		System.out.println("\nconverted3 time: " + time + "\n");									

		time = DateTimeUtil.convertDate2("D:200308181750'00'");
		System.out.println("\nconverted3 time: " + time + "\n");
		
		time = DateTimeUtil.convertDate2("D:20020205100240-07'00'");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("D:20011129091656-08'00'");
		System.out.println("\nconverted3 time: " + time + "\n");

		time = DateTimeUtil.convertDate2("D:20031111091916-05'00'");
		System.out.println("\nconverted3 time: " + time + "\n");
		
		time = DateTimeUtil.convertDate2("D:20030618145703-08'00'");
		System.out.println("\nconverted3 time: " + time + "\n");
		
		time = DateTimeUtil.convertDate2("D:20001025114701");
		System.out.println("\nconverted3 time: " + time + "\n");
		*/									

	}

	/**
	 * Convert time to the archive's local timezone.
	 * 
	 * See the configSystem configuration file to set the difference between GMT
	 * and the archive's local timezone.
	 * 
	 * time in our zone = time in their zone + our difference from GMT - their difference from GMT 
	 * 
	 * @param dateStr the date in the file creator's timezone
	 * @param diff2utH creator's time difference in hours from GMT
	 * @param diff2utM creator's time difference in minutes from GMT
	 * @return String object
	 * @throws IllegalArgumentException 
	 * @throws FatalException, IllegalArgument throws exception when ArchiveProperties.getInstance() failed.
	 */
	private static String normalizeDate(String dateStr, int diff2utH, int diff2utM) 
			throws IllegalArgumentException,FatalException {
		String normalized = new String("");

		Date date = new Date();

		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getInstance();
		sdf.applyPattern("yyyyMMddHHmmss");

		Calendar then = Calendar.getInstance();

		try {
			date = sdf.parse(dateStr);

			then.setTime(date);
			
/*			this is not needed any more as archive use UTC as the time for data storage.
 			int archDiff2utH = Integer.parseInt(ArchiveProperties.getInstance().getArchProperty("TIME_LOCAL_TO_GMT_HOUR"));
			int archDiff2utM
			= Integer.parseInt(ArchiveProperties.getInstance().getArchProperty("TIME_LOCAL_TO_GMT_MINUTE"));
			
			then.add(Calendar.HOUR_OF_DAY, archDiff2utH);
			then.add(Calendar.HOUR_OF_DAY, archDiff2utM);
*/
			then.add(Calendar.HOUR_OF_DAY, diff2utH);
			then.add(Calendar.MINUTE, diff2utM);

		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException();
		} catch (ParseException pe){
			throw new IllegalArgumentException();
		}
		
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		date = then.getTime();
		normalized = sdf.format(date);
		return normalized;
	}

	/**
	 * Method to return the current time in the form:
	 * yyyy-MM-dd HH:mm:ss
	 * 
	 * @return the current time in a string (yyyy-MM-dd HH:mm:ss)
	 */
	public static String now() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		     
		Date d = new Date();		
		return sdf.format(d);
	}

	/**
	 * @param format
	 * @return String object
	 */
	public static String now(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);		
		Date d = new Date();		
		return sdf.format(d);		
	}
	/**
	 * Convert a Macintosh Date (number of seconds since midnight 1904) to
	 * a Date in Archive Date format.
	 * @param MacTime 
	 * @return String object
	 */
	public static String Mac2Arch(long MacTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		
		// convert Mac time (seconds since midnight 1904) to UNIX time (ms since midnight 1970)
		// the difference in seconds among MacTime and UNIX time
		long diffInSeconds = 2082844800; // from http://www.mactech.com/articles/mactech/Vol.16/16.04/Apr00Tips/
		long UnixTime = MacTime - diffInSeconds;
		// conver to milliseconds.
		UnixTime = UnixTime * 1000;
		
		Date d = new Date(UnixTime);		
		return sdf.format(d);
	}
	/**
	 * convert wave format (YYYY-MM-DD) to Archive format (YYYY-MM-DD HH:MM:SS)
	 * @param date
	 * @return String object
	 * @throws IllegalArgumentException
	 */
	public static String Wave2Arch(String date) throws IllegalArgumentException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String newDateStr = "";
		Date newDate = new Date();
		try {
			newDate = sdf.parse(date);
		} catch (ParseException pe) {
			throw new IllegalArgumentException("Incorrect date/time format (YYYY-MM-DD)");
		}

		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		newDateStr = sdf.format(newDate);
		return newDateStr;
	}
	
	/**
	 * convert a SQL Timestamp object to Archive format (YYYY-MM-DD HH:MM:SS)
	 * @param ts - in YYYY-MM-DD HH:MM:SS.NS
	 * @return String object
	 * @throws IllegalArgumentException
	 */
	public static String Timestamp2Arch(Timestamp ts) throws IllegalArgumentException{
		String tsString = ts.toString(); // in YYYY-MM-DD HH:MM:SS.NS
		int ind = tsString.lastIndexOf(".");
		if (ind > 0) 
			tsString = tsString.substring(0, ind);
		return tsString;
	}
}
