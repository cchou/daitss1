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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.Withdrawal;
import edu.fcla.daitss.Withdrawal.Type;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.entity.light.DataFile;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;

/**
 * 
 * Report describing a Withdrawal.
 * 
 * @author franco
 *
 */
public class WithdrawalReport extends PackageReport {

	private DBConnection con;
	private List<DataFile> dataFiles;
	private Date time;
	private String note;

	/**
	 * @return	The note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param w
	 * @param con
	 * @throws FatalException 
	 */
	public WithdrawalReport(Withdrawal w, DBConnection con) throws FatalException {
		//this.w = w;
		dataFiles = w.getDataFiles();
		this.con = con;
		this.ieid = w.getIeid();
		this.packageName = w.getPackageName();
		loadEventInfo();
	}

	private void loadEventInfo() throws FatalException {
		try {
			String sql = String.format(
							"SELECT %s, %s FROM %s WHERE %s = %s AND (%s = %s OR %s = %s)",
							ArchiveDatabase.COL_EVENT_DATE_TIME,
							ArchiveDatabase.COL_EVENT_NOTE,
							ArchiveDatabase.TABLE_EVENT,
							ArchiveDatabase.COL_EVENT_OID, SqlQuote.escapeString(ieid),
							ArchiveDatabase.COL_EVENT_EVENT_TYPE, SqlQuote.escapeString(Type.archive.getCode()),
							ArchiveDatabase.COL_EVENT_EVENT_TYPE, SqlQuote.escapeString(Type.owner.getCode()));
			ResultSet r = con.executeQuery(sql);
			if (r.first()) {
				this.time = r.getTimestamp(ArchiveDatabase.COL_EVENT_DATE_TIME);
				this.note = r.getString(ArchiveDatabase.COL_EVENT_NOTE);
			} else {
				Informer.getInstance().fail(this,
						new Throwable().getStackTrace()[0].getMethodName(),
						"Withdrawal event not found for " + ieid,
						"Withdrawing", new FatalException());
			}
			r.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot select withdrawal event from database for " + ieid,
					"Withdrawing", e);
		}
	}

	@Override
	public String description() {
		return "Withdrawal of package " + this.packageName;
	}

	@Override
	public String getFileName() {
		return ieid + "-withdrawal.xml";
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

	@Override
	public String getType() {
		return "WITHDRAWAL";
	}

	public Node toXML(Document doc) throws FatalException {
		/* ingest element */
		Element withdrawal = doc.createElement("WITHDRAWAL");

		/* Attributes: id, ingest time, package name */
		withdrawal.setAttribute("IEID", this.ieid);
		withdrawal.setAttribute("WITHDRAWAL_TIME", this.getSchemaDateTime());
		withdrawal.setAttribute("PACKAGE_NAME", this.packageName);
		withdrawal.setAttribute("NOTE", this.note);
		
		/* Account info */
		//withdrawal.appendChild(this.agreementInfo.toXML(doc));
		
		/* files */
		for(edu.fcla.daitss.entity.light.DataFile dataFile : dataFiles) {
			Element file = doc.createElement("FILE");
			// message digests
			for(Entry<String, String> e : dataFile.getMessageDigests().entrySet()) {
				String code = e.getKey();
				String value = e.getValue();
				
				Element md = doc.createElement("MESSAGE_DIGEST");
				md.setAttribute("ALGORITHM", code.toUpperCase());
				md.appendChild(doc.createTextNode(value));
				
				file.appendChild(md);
			}
			
			// dfid
			file.setAttribute("DFID", dataFile.getDfid());
			
			// path
			file.setAttribute("PATH", dataFile.getPath());
			
			// size
			file.setAttribute("SIZE", dataFile.getSize());
			withdrawal.appendChild(file);
		}
		
		return withdrawal;
	}

}
