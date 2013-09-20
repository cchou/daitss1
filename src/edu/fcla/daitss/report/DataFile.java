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
package edu.fcla.daitss.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;


/**
 * @author franco
 *
 */
public class DataFile implements Xmlable {
	
	/* elements */
	String comment;
	MessageDigest messageDigest[];

	
	/* attributes */
	String dfid;
	String path;
	long size;
	String preservation;
	String origin;
	boolean global;

	/* database connection */
	private DBConnection con;
	
	/* Events pertaining to this file */
	private Event events[];
    private String[] brokenLinks;
	private List<Warning> warnings;
	
	DataFile(String dfid, DBConnection con) throws FatalException {
		/* file id */
		this.dfid = dfid;
		
		/* database connection */
		this.con = con;

		/* select fields and attributes from the database */
		selectData();
	}
	
	/**
	 * @throws FatalException
	 * 
	 */
	private void selectData() throws FatalException {
		try {
			/* Variable for storing SQL */
			String sqlString;
			/* Variable for sql result set */
			ResultSet result;
			
			/*
			 * SELECT PACKAGE_PATH, ORIGIN, IS_GLOBAL, SIZE, PRES_LEVEL
			 * FROM DATA_FILE
			 * WHERE DFID='${dfid}'
			 * 
			 */
			sqlString = 
				"SELECT " + ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH + ", " +
				ArchiveDatabase.COL_DATA_FILE_ORIGIN + ", " +
				ArchiveDatabase.COL_DATA_FILE_IS_GLOBAL + ", " +
				ArchiveDatabase.COL_DATA_FILE_SIZE + ", " +
				ArchiveDatabase.COL_DATA_FILE_PRES_LEVEL + " " +
				"FROM " + ArchiveDatabase.TABLE_DATA_FILE + " " +
				"WHERE " + ArchiveDatabase.COL_DATA_FILE_DFID + "= " + SqlQuote.escapeString(this.dfid);
			
			result = con.executeQuery(sqlString);
			
			if (result.first()) {
				this.path = result.getString(ArchiveDatabase.COL_DATA_FILE_PACKAGE_PATH);
				this.size = result.getLong(ArchiveDatabase.COL_DATA_FILE_SIZE);
				this.preservation = result.getString(ArchiveDatabase.COL_DATA_FILE_PRES_LEVEL);
				this.origin = result.getString(ArchiveDatabase.COL_DATA_FILE_ORIGIN);
				this.global = result.getBoolean(ArchiveDatabase.COL_DATA_FILE_IS_GLOBAL);
			} else {
				Informer.getInstance().fail(this,
						"selectData()",
						"No results for " + sqlString,
						"Selecting reporting element",
						new FatalException());
			}
			result.close();
			/* select the message digests */
			selectMessageDigests();
			
			/* select the events */
			selectEvents();
            
            /* select the broken links */
            selectBrokenLinks();
			
            /* select the anomalies */
            selectWarnings();
            
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					"selectData()",
					"Cannot select file: " + this.dfid,
					"Selecting from the Database",
					e);
		}			
	}
	
	private void selectWarnings() throws SQLException, FatalException {
		/*
		 * Get Associated Anomalies
		 * 
		 * SELECT se.CODE
		 * FROM `DATA_FILE_SEVERE_ELEMENT` map, `SEVERE_ELEMENT` se
		 * WHERE map.DFID = 'F20051006_AAAAAM'
		 * AND se.REPORT = 'TRUE'
		 * AND se.CODE = map.SEVERE_ELEMENT
		 */
		String tables = String.format("%s map, %s se",
				ArchiveDatabase.TABLE_DATA_FILE_SEV_ELEM,
				ArchiveDatabase.TABLE_SEV_ELEM);
		
		String mapCondition = String.format("map.%s = %s",
				ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_DFID,
				SqlQuote.escapeString(this.dfid));
		
		String reportCondition = String.format("se.%s = 'TRUE'",
				ArchiveDatabase.COL_SEV_ELEM_REPORT);
		
		String joinCondition = String.format("se.%s =  map.%s",
				ArchiveDatabase.COL_SEV_ELEM_CODE,
				ArchiveDatabase.COL_DATA_FILE_SEV_ELEM_SEV_ELEM);
		
		String conditions = String.format("%s AND %s AND %s",
				mapCondition,
				reportCondition,
				joinCondition);
		
		String sql = String.format("SELECT se.%s as code FROM %s WHERE %s",
				ArchiveDatabase.COL_SEV_ELEM_CODE,
				tables,
				conditions);				
		
		ResultSet result = con.executeQuery(sql);

		warnings = new Vector<Warning>();
		
		while (result.next()) {
			String code = result.getString("code");
			Warning a = new Warning(code, con);
			warnings.add(a);
		}
		result.close();
	}
	
	private void selectBrokenLinks() throws SQLException {
        /*
         * Get Broken Links
         * 
         * SELECT BROKEN_LINKS
         * FROM DISTRIBUTED
         * WHERE PARENT = '$dfid'
         * 
         */
        String sqlString = 
            "SELECT " + ArchiveDatabase.COL_DISTRIBUTED_BROKEN_LINKS + " " +
            "FROM " + ArchiveDatabase.TABLE_DISTRIBUTED + " " +
            "WHERE " + ArchiveDatabase.COL_DISTRIBUTED_PARENT + "= " + SqlQuote.escapeString(this.dfid);
        ResultSet result = con.executeQuery(sqlString);

        if (result.first() && !result.getString(ArchiveDatabase.COL_DISTRIBUTED_BROKEN_LINKS).trim().equals("")) {
            String brokenLinks = result.getString(ArchiveDatabase.COL_DISTRIBUTED_BROKEN_LINKS);
            this.brokenLinks = brokenLinks.split("\\|");
        } else {
            this.brokenLinks = new String[0];
        }
        result.close();
    }

    /**
	 * @throws SQLException
	 * @throws FatalException
	 */
	private void selectEvents() throws SQLException, FatalException {
		
		/*
		 * Get Associated Events
		 * 
		 * SELECT ID
		 * FROM EVENT
		 * WHERE OID = '$dfid'
		 */
		String sqlString = 
			"SELECT " + ArchiveDatabase.COL_EVENT_ID + " " +
			"FROM " + ArchiveDatabase.TABLE_EVENT + " " +
			"WHERE " + ArchiveDatabase.COL_EVENT_OID + "= " + SqlQuote.escapeString(this.dfid);
		ResultSet result = con.executeQuery(sqlString);

		List<Event> eventVec = new Vector<Event>();
		while(result.next()) {
			int eventId = result.getInt(ArchiveDatabase.COL_EVENT_ID);
			Event e = new Event(eventId, con);
			if (e.isReportable()) {
				eventVec.add(e);
			}
		}
		result.close();
		this.events = eventVec.toArray(new Event[eventVec.size()]);
	}

	/**
	 * @throws SQLException
	 * 
	 */
	private void selectMessageDigests() throws SQLException {
		
		/*
		 * Get all the message digests with this file
		 * 
		 * SELECT CODE, VALUE
		 * FROM MESSAGE_DIGEST
		 * WHERE DFID = '$dfid' AND ORIGIN = 'ARCHIVE'
		 */
		String sqlString = "SELECT " + ArchiveDatabase.COL_MESSAGE_DIGEST_CODE + ", " +
		ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE + " " +
		"FROM " + ArchiveDatabase.TABLE_MESSAGE_DIGEST + " " +
		"WHERE " +
		ArchiveDatabase.COL_MESSAGE_DIGEST_DFID + "= " + SqlQuote.escapeString(this.dfid) +" AND " +
		ArchiveDatabase.COL_MESSAGE_DIGEST_ORIGIN + "='ARCHIVE'";
		
		ResultSet result = con.executeQuery(sqlString);
		
		List mdList = new Vector();				
		while(result.next()) {			
			String algorithm = result.getString(ArchiveDatabase.COL_MESSAGE_DIGEST_CODE);
			String value = result.getString(ArchiveDatabase.COL_MESSAGE_DIGEST_VALUE);
			mdList.add(new MessageDigest(value, algorithm));
		}
		result.close();
		this.messageDigest = (MessageDigest[]) mdList.toArray(new MessageDigest[0]);		
	}

	/* (non-Javadoc)
	 * @see edu.fcla.daitss.report.Xmlable#toXML(org.w3c.dom.Document)
	 */
	public Node toXML(Document doc) {
		Element file = doc.createElement("FILE");
		
		/* attributes */
		file.setAttribute("DFID", this.dfid);
		file.setAttribute("PATH", this.path);
		file.setAttribute("SIZE", Long.toString(this.size));
		file.setAttribute("PRESERVATION", this.preservation);
		file.setAttribute("ORIGIN", this.origin);
		file.setAttribute("GLOBAL", Boolean.toString(this.global));
		
		/* Message digests */
		for (int i = 0; i < messageDigest.length; i++) {
			MessageDigest md = messageDigest[i];
			file.appendChild(md.toXML(doc));
		}
				
		/* Events */
		for (int i = 0; i < this.events.length; i++) {
			Event event = this.events[i];
			file.appendChild(event.toXML(doc));
		}
        
        /* Broken Links */
        for (int i = 0; i < this.brokenLinks.length; i++) {
            String bLink = this.brokenLinks[i];
            Element brokenLink = doc.createElement("BROKEN_LINK");
            brokenLink.appendChild(doc.createTextNode(bLink));
            file.appendChild(brokenLink);
        }
                
        /* Anomalies */
        for(Warning a : warnings) {
        	file.appendChild(a.toXML(doc));
        }
        
		return file;			
	}
}