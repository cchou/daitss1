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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * @author franco
 *
 * Represents an Event.
 */
public class Event implements Xmlable {
		
 	 private int id;
	 String type;
	 private Date time;
	 private String procedure;
	 private String outcome;
	 private String note;
	 private String relOid;
	 
	 private DBConnection con;
     
     private String oid;
	 
	 Event(int id, DBConnection con) throws FatalException {
	 	
	 	/* Event id */
	 	this.id = id;
	 	
		/* database connection */
		this.con = con;
		
		/* Load this event from the database */
	 	selectData();	
	 }

	/**
	 * @throws FatalException
	 * 
	 */
	private void selectData() throws FatalException {

		try {
			
			/*
			 * SELECT *
			 * FROM EVENT
			 * WHERE ID='$id'
			 */			
			String sqlString = 
				"SELECT * " +
				"FROM " + ArchiveDatabase.TABLE_EVENT +	" " +
				"WHERE " + ArchiveDatabase.COL_EVENT_ID + "= " + SqlQuote.escapeInt(this.id);
			
			ResultSet result = con.executeQuery(sqlString);
			
			if (result.first()) {
				this.oid = result.getString(ArchiveDatabase.COL_EVENT_OID);
				
				this.type = result.getString(ArchiveDatabase.COL_EVENT_EVENT_TYPE);
				
				this.time = result.getTimestamp(ArchiveDatabase.COL_EVENT_DATE_TIME);
				
				this.procedure = result.getString(ArchiveDatabase.COL_EVENT_EVENT_PROCEDURE);
				
				this.outcome = result.getString(ArchiveDatabase.COL_EVENT_OUTCOME);
				
				this.note = result.getString(ArchiveDatabase.COL_EVENT_NOTE);

				this.relOid = result.getString(ArchiveDatabase.COL_EVENT_REL_OID);
			} else {
				Informer.getInstance().fail(this,
						"selectData()",
						"No results for " + sqlString,
						"Selecting reporting element",
						new FatalException());
			}		
			result.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					"selectData()",
					"Cannot select data for Event: " + this.id,
					"Selecting from the Database",
					e);
		}
	}

	/**
	 * @return true if this event is reportable, specified by the <code>report.event.filters</code>
	 * @throws FatalException
	 */
	public boolean isReportable() throws FatalException {
		ArchiveProperties p = ArchiveProperties.getInstance();
		String reportFilters = p.getArchProperty("REPORT_EVENT_FILTERS");
		String[] filters = reportFilters.split("\\s*,\\s*");
		List filterList = Arrays.asList(filters);
		return filterList.contains(this.type);
	}

	/* (non-Javadoc)
	 * @see edu.fcla.daitss.report.Xmlable#toXML(org.w3c.dom.Document)
	 */
	public Node toXML(Document doc) {
		Element event = doc.createElement("EVENT");
		
		Element procedure = (Element) event.appendChild(doc.createElement("PROCEDURE"));
		procedure.appendChild(doc.createTextNode(this.procedure));
		
		SimpleDateFormat df = new SimpleDateFormat(DateTimeUtil.SCHEMA_DATE_PATTERN);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String eventTime = df.format(this.time);
		event.setAttribute("TIME", eventTime);
		
		event.setAttribute("OUTCOME", this.outcome);
		
		Element note = (Element) event.appendChild(doc.createElement("NOTE"));
		note.appendChild(doc.createTextNode(this.note));
		
		if (this.relOid != null) {
			event.setAttribute("RELATIVE_ID", this.relOid);
		}
		
		return event;
	}

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }
    

    /**
     * @return Returns the note.
     */
    public String getNote() {
        return note;
    }
    

    /**
     * @return Returns the oid.
     */
    public String getOid() {
        return oid;
    }
    

    /**
     * @return Returns the outcome.
     */
    public String getOutcome() {
        return outcome;
    }
    

    /**
     * @return Returns the procedure.
     */
    public String getProcedure() {
        return procedure;
    }
    

    /**
     * @return Returns the relOid.
     */
    public String getRelOid() {
        return relOid;
    }
    

    /**
     * @return Returns the time.
     */
    public Date getTime() {
        return time;
    }
    
}
