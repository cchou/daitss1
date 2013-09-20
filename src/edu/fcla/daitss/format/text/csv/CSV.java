package edu.fcla.daitss.format.text.csv;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.format.text.Text;

public class CSV extends Text {
	private String numOfColumns;
	private int numOfRows = 0;
	
	public CSV(DataFile df) throws FatalException {
		super(df);
		
		this.setBsTable(ArchiveDatabase.TABLE_BS_CSV);
		this.getCompression().setCompression(Compression.COMP_NOT_APPLICABLE);
		this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		this.setLocation("0");
	}
	
	/**
	 * set the number of rows in this CSV
	 * @param _numOfRows number of rows
	 */
	public void setNumOfRows(int _numOfRows) {
		this.numOfRows = _numOfRows;
	}
	
	/**
	 * set the number of collumns in this CSV.  The number of columns is
	 * a comma separated list of colums.
	 * @param _numOfColumns number of columns
	 */
	public void setNumOfColumns(String _numOfColumns) {
		this.numOfColumns = _numOfColumns;
	}
	
	/**
	 * fill in the database column-value pairs for this document
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();

		columns.add(ArchiveDatabase.COL_BS_CSV_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_CSV_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_CSV_NUM_ROWS);
		values.add(this.numOfRows);
		columns.add(ArchiveDatabase.COL_BS_CSV_NUM_COLUMNS);
		values.add(this.numOfColumns);
	}
		
	/**
	 * Insert this document into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_CSV, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this document
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		super.dbUpdate();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BS_CSV_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_CSV, columns, colValues, whereClause);
	}
	
	public Document toXML() throws FatalException {
	    /* Daitss Document */
	    Document doc = super.toXML();
	    
	    /* Daitss Element */
	    Element rootElement = doc.getDocumentElement();
	    
	    /* Daitss Namespace */
	    String namespace = rootElement.getNamespaceURI();	    
	    
	    /* Bs CSV */
	    Element bsCSVElement = doc.createElementNS(namespace, "BS_TEXT_CSV");
	    rootElement.appendChild(bsCSVElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    org.w3c.dom.Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsCSVElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    org.w3c.dom.Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsCSVElement.appendChild(bsidElement);

	    /* num of rows */
	    Element numOfRowsElement = doc.createElementNS(namespace, "NUM_OF_ROWS");
	    String numOfRowsValue = Long.toString(this.numOfRows);
	    org.w3c.dom.Text numOfRowText = doc.createTextNode(numOfRowsValue);
	    numOfRowsElement.appendChild(numOfRowText);
	    bsCSVElement.appendChild(numOfRowsElement);

	    /* num of columns */
	    Element numOfColumnsElement = doc.createElementNS(namespace, "NUM_OF_COLUMNS");
	    org.w3c.dom.Text numOfColulmnsText = doc.createTextNode(this.numOfColumns);
	    numOfColumnsElement.appendChild(numOfColulmnsText);
	    bsCSVElement.appendChild(numOfColumnsElement);
	    return doc;
	}
}
