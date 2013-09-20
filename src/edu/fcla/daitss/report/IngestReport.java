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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * @author franco
 * 
 * Class describing an Ingest.
 */
public class IngestReport extends PackageReport {

	private Date time;

	private DBConnection con;
	
	/* Events pertaining to this Ingest (IntEntity) */
	private Event[] events;
	
	List<Warning> anomalies;

	private boolean reIngest;
    
	/**
	 * create a new <code>IngestReport</code>. Only methods in this package can access this method.
	 * @param id
	 * @param con 
	 * @param reIngest true if this is not a SIP ingest but a dissemination-ingest
	 * @throws FatalException
	 * @throws PackageException 
	 */
	public IngestReport (String id, DBConnection con, boolean reIngest) throws FatalException, PackageException {

		/* IEID */
		this.ieid = id;
		
		/* database connection */
		this.con = con;

		this.reIngest = reIngest;
		
		/* select id, time, package name, data files */
		selectData();
	}

	/**
	 * @throws FatalException
	 * @throws PackageException 
	 * 
	 */
	private void selectData() throws FatalException, PackageException {
		try {
			
			/*
			 * Get all field for this ingest
			 * SELECT a.INGEST_TIME, a.ACCOUNT_PROJECT, i.PACKAGE_NAME
			 * FROM ADMIN a, INT_ENTITY i
			 * WHERE i.IEID = '$ieid' AND 
			 * 	i.IEID = a.OID
			 */			
			String sqlString = "SELECT a." + ArchiveDatabase.COL_ADMIN_INGEST_TIME + ", " +
				"a. " + ArchiveDatabase.COL_ADMIN_ACCOUNT_PROJECT + ", " +
				"i." + ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME + " " +
			"FROM " + ArchiveDatabase.TABLE_ADMIN + " a, " +
			ArchiveDatabase.TABLE_INT_ENTITY + " i " +
			"WHERE i." + ArchiveDatabase.COL_INT_ENTITY_IEID + " = " + SqlQuote.escapeString(this.ieid) + " AND " +
				"i." + ArchiveDatabase.COL_INT_ENTITY_IEID + " = a." + ArchiveDatabase.COL_ADMIN_OID;

			
			ResultSet result = con.executeQuery(sqlString);
			
			if (result.first()) {				

				/* Get the ingest time */
				SimpleDateFormat df = new SimpleDateFormat(Report.DAITSS_DATE_PATTERN);				
				String adminIngestTime = result.getString(ArchiveDatabase.COL_ADMIN_INGEST_TIME);
				this.time = df.parse(adminIngestTime);				
				
				/* Get the account info */	
				int apCode = result.getInt(ArchiveDatabase.COL_ADMIN_ACCOUNT_PROJECT);
				this.agreementInfo = new AgreementInfo(apCode, con);								

				/* Get the package name */
				this.packageName = result.getString(ArchiveDatabase.COL_INT_ENTITY_PACKAGE_NAME);
			} else {
				Informer.getInstance().fail(this,
						"selectData()",
						"No results for " + sqlString,
						"Selecting reporting element",
						new FatalException());
			}
			
			result.close();
			/* Get all related data files */
			selectDataFiles();
			
			/* Get all related events */
			selectEvents();
			
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					"selectData()",
					"Cannot select data for Ingest: " + this.ieid,
					"Selecting from the Database",
					e);
		} catch (ParseException e) {
			Informer.getInstance().fail(this,
					"selectData()",
					"Cannot Parse date format: " + Report.DAITSS_DATE_PATTERN,
					"Converting a string to a Date",
					e);
		}
	}

	/**
	 * @throws SQLException
	 * @throws FatalException
	 * 
	 */
	private void selectEvents() throws SQLException, FatalException {
		
		/*
		 * Get Associated Events
		 * 
		 * SELECT ID
		 * FROM EVENT
		 * WHERE OID = '$ieid'
		 */
		String sqlString = 
			"SELECT " + ArchiveDatabase.COL_EVENT_ID + " " +
			"FROM " + ArchiveDatabase.TABLE_EVENT + " " +
			"WHERE " + ArchiveDatabase.COL_EVENT_OID + "= " + SqlQuote.escapeString(this.ieid);
		ResultSet result = con.executeQuery(sqlString);

		List<Event> eventVec= new Vector<Event>();
		while (result.next()) {
			int eventId = result.getInt(ArchiveDatabase.COL_EVENT_ID);
			Event e = new Event(eventId, con);
			if(e.isReportable()) {
				eventVec.add(e);
			}
		}
		result.close();
		this.events = eventVec.toArray(new Event[eventVec.size()]);
	}

	/**
	 * @throws SQLException
	 * @throws FatalException
	 */
	private void selectDataFiles() throws FatalException, SQLException {		
		/* 
		 * (SELECT DFID FROM INT_ENTITY_GLOBAL_FILE WHERE IEID = '$ieid') 
		 * UNION
		 * (SELECT DFID  from DATA_FILE where IEID = '$ieid')
		 */
		String q1 = String.format("SELECT %s FROM %s WHERE %s = %s",
				ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_DFID,
				ArchiveDatabase.TABLE_INT_ENTITY_GLOBAL_FILE,
				ArchiveDatabase.COL_INT_ENTITY_GLOBAL_FILE_IEID,
				SqlQuote.escapeString(ieid));

		String q2 = String.format("SELECT %s from %s where %s = %s",
				ArchiveDatabase.COL_DATA_FILE_DFID,
				ArchiveDatabase.TABLE_DATA_FILE,
				ArchiveDatabase.COL_DATA_FILE_IEID,
				SqlQuote.escapeString(ieid));

		String sql = String.format("(%s) UNION (%s)", 
				q1,
				q2);
		
		ResultSet result = con.executeQuery(sql);
		
		/* Initialize this.files */
		List<DataFile> fileList = new Vector<DataFile>();
		while(result.next()) {
			
			/* get fields from table: dfid, packagePath, origin, size, presLevel */
			String dfid = result.getString(ArchiveDatabase.COL_DATA_FILE_DFID);
			
			/* make a file object */
			DataFile file = new DataFile(dfid, con);
			fileList.add(file);
		}
		result.close();
		this.files = fileList.toArray(new DataFile[fileList.size()]);
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.ieid = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.fcla.daitss.report.Xmlable#toXML(org.w3c.dom.Document)
	 */
	public Node toXML(Document doc) throws FatalException {
		
		/* ingest element */
		Element ingest = doc.createElement(reIngest ? "DISSEMINATION" : "INGEST");

		/* Attributes: id, ingest time, package name */
		ingest.setAttribute("IEID", this.ieid);
		ingest.setAttribute("INGEST_TIME", this.getSchemaDateTime());
		ingest.setAttribute("PACKAGE", this.packageName);
		
		/* Account info */
		ingest.appendChild(this.agreementInfo.toXML(doc));
		
		/* files */
		Element files = (Element) ingest.appendChild(doc.createElement("FILES"));
		for (int i = 0; i < this.files.length; i++) {
			DataFile dataFile = this.files[i];
			files.appendChild(dataFile.toXML(doc));
		}
		
		/* Events */
		if(this.events.length > 0) {
			Element events = (Element) ingest.appendChild(doc.createElement("EVENTS"));
			for (int i = 0; i < this.events.length; i++) {
				Event event = this.events[i];
				events.appendChild(event.toXML(doc));
			}
		}
		
		// TODO Copies
		
		return ingest;
	}

	/**
	 * @return the ingest date-time in XMLSchema format
	 * @throws FatalException 
	 */
	private String getSchemaDateTime() throws FatalException {
		SimpleDateFormat df = new SimpleDateFormat(DateTimeUtil.SCHEMA_DATE_PATTERN);
		String tz = ArchiveProperties.getInstance().getArchProperty("TIME_ZONE");
		df.setTimeZone(TimeZone.getTimeZone(tz));
		return df.format(this.time);
	}

	public String description() {
		return "Ingest of package " + this.packageName;
	}

    public String getFileName() {
        return ieid + ".ingest.xml";
    }

    public String getType() {
        // TIGER return an enum type for this
        return "INGEST";
    }
}
