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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;





/**
 * @author franco
 * 
 * Daitss report class.
 * 
 * a report is comprised of a header and one or more
 */
public abstract class Report implements Xmlable {
	
	/**
	 * daitss:DATE pattern for <code>SimpleDateFormat</code> 
	 */
	public static String DAITSS_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/* Agreement Information */
	protected AgreementInfo agreementInfo;

    /* Generation time and date */
    protected Date date;

	/**
	 * @return the agreement info
	 */
	public AgreementInfo getAgreementInfo() {
		return agreementInfo;
	}
	
	/**
	 * @return a description of the report
	 */
	public abstract String description();

    /**
     * Default constructor, used to set the creation date of the object.
     */
    public Report() {
        date = new Date();
    }
    
	/**
	 * @return a string representation of the xml
	 * @throws FatalException
	 */
	public String serializeXML() throws FatalException {
		
        /* get a new document */
		Document doc = XPaths.newDocument();
		
		ArchiveProperties p = ArchiveProperties.getInstance();
		
		/* convert to xml */
		Element report = doc.createElement("REPORT");
		
		// get the url of the stylesheet and put it in here as a procinst 
		ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet",
				String.format("type=\"text/xsl\" href=\"%s\"",
						ArchiveProperties.getInstance().getArchProperty("REPORT_STYLESHEET")));

		doc.insertBefore(pi, doc.getDocumentElement());
        
        /* default (daitss) namespace */
		report.setAttribute("xmlns", p.getArchProperty("NS_DAITSS"));

        /* xsi namespace */
		report.setAttribute("xmlns:" + XPaths.PREFIX_XMLSCHEMAINSTANCE, p.getArchProperty("NS_XMLSCHEMA_INSTANCE"));
        
        /* schema location */
		report.setAttribute(p.getArchProperty("DES_SCHEMA_LOCATION"), p.getArchProperty("DES_DAITSS_REPORT_SCHEMA"));
        
        /* append the report to the document */
		doc.appendChild(report);

        /* append the actual report data to the report */
        report.appendChild(this.toXML(doc));
        
		/* output format */
		OutputFormat of = new OutputFormat();
        of.setIndenting(true);
		
		/* output stream */
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		/* serializer */
		XMLSerializer ser = new XMLSerializer(os, of);
		
		try {

			/* serialize xml to a string */			
			ser.serialize(doc);			
		} catch (IOException e) {
			Informer.getInstance().fail(
					this,
					"outputXML(MimeMessage)",
					"Cannot serialize xml to output stream.",
					"Serializing XML.",
					e);
		}
		
		String xmlString = os.toString();		
		return xmlString;
		
	}

    /**
     * @return Returns a file name suggestion of the report.
     */
    public abstract String getFileName();
    
    /**
     * @return Returns the type of this date. 
     * // TIGER make an enum with the types of dates and make this return type an instance of the enum. 
     */
    public abstract String getType();
    
    /**
     * Save a record of this report to the database.
     * @throws FatalException 
     */
    public void dbUpdate() throws FatalException {
        
        /* database connection object */
        TransactionConnection tcon = DBConnection.getSharedConnection();
        
        /*
         * INSERT INTO REPORT (TYPE, DATE)
         * VALUES ('$type', NOW())
         */
        try {
	                
	        String cols = String.format("%s, %s", 
	        		ArchiveDatabase.COL_REPORT_TYPE,
	        		ArchiveDatabase.COL_REPORT_DATE);
	        
			String vals = String.format("%s, %s",
					SqlQuote.escapeString(getType()),
					SqlQuote.escapeString(DateTimeUtil.now()));
			
			String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
					ArchiveDatabase.TABLE_REPORT, 
					cols,
					vals);
			
            tcon.executeQuery(sql);
        } catch (SQLException e1) {
            try {
                tcon.rollbackTransaction();
            } catch (SQLException e) {            
                Informer.getInstance().fail(
                    this,
                    "save()",                        
                    "Cannot rollback transaction",
                    "Inserting data",
                    new FatalException());        
            }
            Informer.getInstance().fail(
                    this,
                    "save()",                        
                    "Problem inserting a record of report generation",
                    "Inserting data",
                    new FatalException());     
        }
        
        
        /*
         * if this is a package report make the relation in REPORT_INT_ENTITY
         * when possible move to PackageReport
         * keep here for now until more report types are determined, if possible move
         */
        if (this instanceof PackageReport) {
            PackageReport pr = (PackageReport) this;
            
            /* 
             * INSERT INTO REPORT_INT_ENTITY 
             * (ID, REPORT, INT_ENTITY)
             * VALUES ('', LAST_INSERT_ID(),'$ieid')
             */                        
            try {
            	String cols = String.format("%s, %s", 
            			ArchiveDatabase.COL_REPORT_INT_ENTITY_REPORT,
            			ArchiveDatabase.COL_REPORT_INT_ENTITY_INT_ENTITY);
            	
            	String vals = String.format("%s, %s",
            			"LAST_INSERT_ID()",
            			SqlQuote.escapeString(pr.getIeid()));
            	
            	String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
            			ArchiveDatabase.TABLE_REPORT_INT_ENTITY,
            			cols,
            			vals);
                tcon.executeQuery(sql);
            } catch (SQLException e ) {
                try {
                    tcon.rollbackTransaction();
                } catch (SQLException e1) {
                    Informer.getInstance().fail(
                            this,
                            "save()",
                            "Cannot rollback transaction",
                            "Inserting data", new FatalException());
                }
                Informer.getInstance().fail(
                        this,
                        "save()",
                        "Problem inserting a record of package report generation.",
                        "Inserting data", new FatalException());
            }            
            
        }
        
        /* commit the transaction */
        tcon.commitTransaction();
    }
}
