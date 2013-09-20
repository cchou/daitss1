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

import edu.fcla.daitss.Withdrawal;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.entity.InformationPackage;
import edu.fcla.daitss.entity.IntEntity;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.report.delivery.DeliveryException;
import edu.fcla.daitss.report.delivery.EmailDelivery;

import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;
import edu.fcla.jmserver.Copy;

/**
 * @author franco
 *
 * Reporting function 
 */
public class Reporter {
 
    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.report.Reporter";
    
    /**
     * @param ie
     * @param reIngest true implies if this is a re ingest
     * @throws FatalException
     * @throws PackageException
     */
    public static void reportIngest(IntEntity ie, boolean reIngest) throws FatalException, PackageException {
        DBConnection con = DBConnection.getConnection();
        
        StackTraceElement ste = new Throwable().getStackTrace()[0];
        Informer.getInstance().info(
        		ste.getClassName(),
        		ste.getMethodName(),
        		"Generating ingest report",
        		"Reporting ingest for " + ie.getEntityId(), 
        		false);

        // generate the report
        IngestReport ir = new IngestReport(ie.getOid(), con, reIngest);
        
        try {
            con.close();
        } catch (SQLException e) {
            Informer.getInstance().fail(
                    ste.getClassName(),
                    ste.getMethodName(),
                    "cannot close DBConnection " + con.toString(),
                    "Closing database connection", e);
        }
        con = null;
        
        Informer.getInstance().info(
        		ste.getClassName(),
        		ste.getMethodName(),
        		"Ingest report generated, starting delivery",
        		"Reporting ingest for " + ie.getEntityId(), 
        		false);
        
        // deliver the report
        Reporter.deliver(ir, ie.getAcccount());
        
        Informer.getInstance().info(
        		ste.getClassName(),
        		ste.getMethodName(),
        		"Ingest report delivered, starting database update",
        		"Reporting ingest for " + ie.getEntityId(), 
        		false);

        // record the report
        ir.dbUpdate();
        
        Informer.getInstance().info(
        		new Throwable().getStackTrace()[0].getClassName(),
        		new Throwable().getStackTrace()[0].getMethodName(),
        		"Ingest report commited to database",
        		"Reporting ingest for " + ie.getEntityId(), 
        		false);

    }
    
    /**
     * @param pe
     * @param ip 
     * @param account
     * @throws FatalException 
     */
    public static void reportError(PackageException pe, InformationPackage ip, String account) throws FatalException {
    	
    	String methodName = "reportError(PackageException,SIP,String)";
    	
    	// check input
    	if (pe == null) {
	        Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "PackageException argument is null",
                    "PackageException pe",
                    new IllegalArgumentException());    		
    	}
    	
    	if (ip == null) {
	        Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "SIP argument is null",
                    "SIP s",
                    new IllegalArgumentException());    		
    	}
    	
    	if (account == null) {
	        Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "String argument is null",
                    "String account",
                    new IllegalArgumentException());    		
    	}
    	    	
        // get info about the package
        String packageName = ip.getPackageName();
                
        // get the message from the pe 
        String message;
        if(pe.getMessage() == null || pe.getMessage().equals(pe.getLocalMessage())) {
        	message = String.format("%s\n%s",
        			pe.getLocalMessage(),
        			pe.getLocalSubject());
        } else {
        	message = String.format("%s\n%s\n%s",
        			pe.getMessage(),
        			pe.getLocalMessage(),
        			pe.getLocalSubject());
        }            	
        
        // make the error report
        ErrorReport er = new ErrorReport(message, packageName, account);
        
        // deliver the report
        Reporter.deliver(er, er.getAccount());
        
        // record the report
        er.dbUpdate();	        	    
    }
    
    /**
     * @param report
     * @param account 
     * @throws FatalException
     */
    public static void deliver(Report report, String account) throws FatalException {
        
        /* deliver */
        String[] recipients = getRecipients(account);
        try {
            new EmailDelivery(report, recipients).deliver();
        } catch (DeliveryException e) {
            
        	StringBuffer buffer = new StringBuffer();
        	for(String r : recipients) {
        		buffer.append(r);
        		buffer.append(", ");
        	}        	
        	buffer.delete(buffer.lastIndexOf(", "), buffer.length());
        	
            /* fail because we cannot email */
            Informer.getInstance().warning(
                    Reporter.class.getName(),
                    "deliver()",
                    "Cannot deliver report set to " + buffer.toString() + " : " + e.getMessage(),
                    "Delivering report");
        }
        
    }

   private static String[] getRecipients(String accountCode) throws FatalException {
        String methodName = "getRecipients(String)";
        
        /*
         * SELECT REPORT_EMAIL
         * FROM ACCOUNT
         * WHERE CODE='$accountCode'
         */
        String sqlString =
            "SELECT " + ArchiveDatabase.COL_ACCOUNT_REPORT_EMAIL + " " +
            "FROM " + ArchiveDatabase.TABLE_ACCOUNT + " " + 
            "WHERE " + ArchiveDatabase.COL_ACCOUNT_CODE + "= " + SqlQuote.escapeString(accountCode);
        DBConnection con = DBConnection.getConnection();
        List<String> rList = null;
        String[] recipients = null;
        try {
            ResultSet result = con.executeQuery(sqlString);
            rList = new Vector<String>();
            while (result.next()) {                               
                rList.add(result.getString(1));
            }
            result.close();
            
            if (rList.isEmpty()) {
            	String defaultAccount = ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT");
            	if(accountCode.equals(defaultAccount)) {
	            	// fail	
	                Informer.getInstance().fail(
	                        Reporter.class.getName(),
	                        "getRecipients()",
	                        "The default account " + defaultAccount + " does not exist in the database.",
	                        "Selecting report email address for account: "+ accountCode, 
	                        new FatalException());
            	} else {
            		//warn and recurse to default account
                    Informer.getInstance().warning(
                            Reporter.class.getName(),
                            "getRecipients()",
                            "Account does not exist, redirecting report to " + defaultAccount,
                            "Selecting report email address for account: "+ accountCode);
                    recipients =  getRecipients(defaultAccount);
            	}
            } else {
            	recipients = rList.toArray(new String[rList.size()]);
            }
                        
        } catch (SQLException e) {
            Informer.getInstance().fail(
                    Reporter.class.getName(),
                    "getRecipients()",
                    "Cannot select data: " + sqlString,
                    "Selecting report email address for account: " + accountCode,
                    e);
            
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                Informer.getInstance().warning(CLASSNAME,
                        methodName,
                        "Error closing database connection: " + e.getMessage(),
                        con.toString());
            }
            
            con = null;
        }
        
        return recipients;
    }
    
    /**
     * @param args
     * @throws FatalException
     */
    public static void main(String[] args) throws FatalException {
        String[] addrs = Reporter.getRecipients("UF");
        for (String address : addrs) {
            System.out.println(address);
        }
    }

	/**
	 * @param w
	 * @throws FatalException 
	 */
	public static void reportWithdrawal(Withdrawal w) throws FatalException {
		DBConnection con = DBConnection.getConnection();
		WithdrawalReport r = new WithdrawalReport(w, con);
		
		// deliver the report 
		Reporter.deliver(r, w.getAccount());
		
		// record the report
		r.dbUpdate();
	}

	/**
	 * @param e
	 * @throws FatalException 
	 */
	public static void reportIncompleteContentRemoval(String ieid, List<Copy> copies) throws FatalException {
        String message = "Residual content:\n";
        for(Copy copy : copies){
        	message += copy.toString() + "\n";
        }        
        String account = ArchiveProperties.getInstance().getArchProperty("DAITSS_ACCOUNT");
        ErrorReport report = new ErrorReport(message, ieid, account);
        Reporter.deliver(report, report.getAccount());
        report.dbUpdate();
	}
    
}
