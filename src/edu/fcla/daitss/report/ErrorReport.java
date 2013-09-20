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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.DateTimeUtil;

/**
 * @author franco
 *
 */
public class ErrorReport extends Report {
	
    private String packageName;
    
    private String account;
    
	private String message;

	private Date rejectTime;
	
	/**
	 * @param message
	 * @param packageName 
	 * @param account 
	 */
	public ErrorReport(String message, String packageName, String account) {
        this.message = message;
        this.packageName = packageName;
        this.account = account;
        this.rejectTime = new Date();
	}

	public Node toXML(Document doc) throws FatalException {
		
		// create the error element
		Element error = doc.createElement("ERROR");
                
		// set the REJECT_TIME attribute
		SimpleDateFormat df = new SimpleDateFormat(DateTimeUtil.SCHEMA_DATE_PATTERN);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String x = df.format(this.rejectTime);		
		error.setAttribute("REJECT_TIME", x);
		
		// message element
		Element message = (Element) error.appendChild(doc.createElement("MESSAGE"));
		message.appendChild(doc.createTextNode(this.message));

		// package element
        Element pkg = (Element) error.appendChild(doc.createElement("PACKAGE"));
        pkg.appendChild(doc.createTextNode(this.packageName));
        
		return error;
	}

	public String description() {
		return "Error: " + this.message;
	}

    public String getFileName() {
        return packageName + ".error.xml";
    }

    public String getType() {
        // TIGER return an enum type for this
        return "ERROR";
    }

    /**
     * @return the account this error message is for
     */
    public String getAccount() {
        return account;
    }

}
