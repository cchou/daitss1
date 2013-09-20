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
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;


class AgreementInfo implements Xmlable {
	
	int apCode;
	private String account;
	private String project;
	private String subaccount;
	private DBConnection con;
	
	AgreementInfo(int code, DBConnection con) throws FatalException, PackageException {
		
		/* database connection */
		this.con = con;

		/* set the code */
		this.apCode = code;
		
		/* set the sub account */
		this.subaccount = null;
		
		/* select the fields from the database */
		selectData();
	}
	
	AgreementInfo(int code, String subaccount, DBConnection con) throws FatalException, PackageException {
		
		/* database connection */
		this.con = con;

		/* set the code */
		this.apCode = code;
		
		/* set the subaccount */
		this.subaccount = subaccount;
		
		/* select the fields from the database */
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
             * get account and project codes
             * 
			 * SELECT a.CODE as ACCOUNT, p.CODE as PROJECT
             * FROM ACCOUNT a, PROJECT p, ACCOUNT_PROJECT ap
             * WHERE ap.ID = '$apCode' AND
             *       ap.ACCOUNT = a.CODE AND
             *       ap.PROJECT = p.CODE
			 */
			String sqlString = 
                "SELECT a." + ArchiveDatabase.COL_ACCOUNT_CODE + ", " +
                    "p." + ArchiveDatabase.COL_PROJECT_CODE + " " +
                "FROM " + ArchiveDatabase.TABLE_ACCOUNT + " a, " +
                ArchiveDatabase.TABLE_PROJECT + " p, " +
                ArchiveDatabase.TABLE_ACCOUNT_PROJECT + " ap " +                    
                "WHERE ap." + ArchiveDatabase.COL_ACCOUNT_PROJECT_ID + " = " + SqlQuote.escapeInt(this.apCode) + " AND " +
                    "ap." + ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT + " = a." + ArchiveDatabase.COL_ACCOUNT_CODE + " AND " +
                    "ap." + ArchiveDatabase.COL_ACCOUNT_PROJECT_PROJECT + " = p.CODE";
			
			ResultSet result = con.executeQuery(sqlString);
			if ( result.first() ) {
				this.account = result.getString("a." + ArchiveDatabase.COL_ACCOUNT_CODE);				
				this.project = result.getString("p." + ArchiveDatabase.COL_PROJECT_CODE);
			} else {
				Informer.getInstance().fail(this,
						"selectData()",
						"No results for " + sqlString,
						"Selecting reporting element",
						new FatalException());
			}
			result.close();
            /* verify that the subaccount is proper or null */
            verifySubaccount();

			
		} catch (SQLException e) {
			Informer.getInstance().fail(this,
					"selectData()",
					"Cannot select data for AccountInfo: " + this.apCode,
					"Selecting from the Database",
					e);
		}
	}

	private void verifySubaccount() throws SQLException, FatalException, PackageException {
        /* 
         * if subaccount is present then
         * verify | subaccount X account | === 1 
         */
        if (this.subaccount != null) {
            /*
             * SELECT COUNT( * )
             * FROM SUB_ACCOUNT s, ACCOUNT a 
             * WHERE s.ACCOUNT = a.CODE 
             * AND s.CODE = '$sCode'
             * AND a.CODE ='$aCode'
             */
            String sqlString = 
                "SELECT COUNT( * ) " +
                "FROM " + ArchiveDatabase.TABLE_SUB_ACCOUNT + " s, " + ArchiveDatabase.TABLE_ACCOUNT + " a " +
                "WHERE s." + ArchiveDatabase.COL_SUB_ACCOUNT_ACCOUNT + " = a." + ArchiveDatabase.COL_ACCOUNT_CODE + " " +
                "AND s." + ArchiveDatabase.COL_SUB_ACCOUNT_CODE + " = " + SqlQuote.escapeString(this.subaccount) +
                "AND a." + ArchiveDatabase.COL_ACCOUNT_CODE + " = " +   SqlQuote.escapeString(this.account);
            ResultSet result = con.executeQuery(sqlString);
            if (result.first()) {
                if (result.getInt(1) != 1) {
                    Informer.getInstance()
                        .error(this, "selectData()",
                            "Too many subaccounts named " + this.subaccount + " (" + result.getInt(1) + ") returned for account " + this.account,
                            "Selecting reporting element",
                            new FatalException());
                }
            } else {
                Informer.getInstance()
                        .fail(this, "selectData()",
                                "No results for " + sqlString,
                                "Selecting reporting element",
                                new FatalException());
            }
            result.close();
        }
    }

    /* (non-Javadoc)
	 * @see edu.fcla.daitss.report.Xmlable#toXML(org.w3c.dom.Document)
	 */
	public Node toXML(Document doc) throws FatalException {

		/* daitss ns */
		ArchiveProperties p = ArchiveProperties.getInstance();
		String daitss_ns = p.getArchProperty("NS_DAITSS");
		
		/* AgreementInfo */		
		Element aInfo = doc.createElementNS(daitss_ns, "AGREEMENT_INFO");
		
		/* Account */
		aInfo.setAttribute("ACCOUNT", this.account);
		
		
		/* SubAccount */
		if (this.subaccount != null) { 
			aInfo.setAttribute("SUB_ACCOUNT", this.subaccount);
		}

		
		/* Project */
		aInfo.setAttribute("PROJECT", this.project);
		
		return aInfo;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}
	

	/**
	 * @return the sub account, or null if non existant
	 */
	public String getSubaccount() {
		return subaccount;
	}
	
}