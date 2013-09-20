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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * Lightweight database persistant class for report warning
 * @author franco
 *
 */
public class Warning implements Xmlable {

	private String code;
	private DBConnection con;
	private String description;

	/**
	 * @param code
	 * @param con
	 * @throws FatalException 
	 */
	public Warning(String code, DBConnection con) throws FatalException {
		this.code = code;
		this.con = con;
		selectData();
	}

	private void selectData() throws FatalException {
		/*
		 * SELECT * FROM SEVERE_ELEMENT WHERE CODE = '$code'
		 */
		try {
			String sql = String.format("SELECT * FROM %s WHERE %s = %s",
					ArchiveDatabase.TABLE_SEV_ELEM,
					ArchiveDatabase.COL_SEV_ELEM_CODE,
					SqlQuote.escapeString(this.code));
			ResultSet r = con.executeQuery(sql);
			if (r.first()) {
				this.description = r.getString(ArchiveDatabase.COL_SEV_ELEM_DESCRIPTION);
			} else {
				Informer.getInstance().fail(this,
						"selectData()",
						"No results for " + sql,
						"Selecting reporting anomaly",
						new FatalException());
			}
			r.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					"selectData()",
					"Cannot select data for Anomaly: " + this.code,
					"Selecting from the Database",
					e);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.fcla.daitss.report.Xmlable#toXML(org.w3c.dom.Document)
	 */
	public Node toXML(Document doc) {		
		Element anomaly = doc.createElement("WARNING");
		anomaly.setAttribute("CODE", this.code);
		anomaly.appendChild(doc.createTextNode(this.description));		
		return anomaly;
	}
}
