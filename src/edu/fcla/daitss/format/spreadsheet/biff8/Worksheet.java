package edu.fcla.daitss.format.spreadsheet.biff8;

import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class Worksheet implements Comparable<Worksheet> {
	long numOfRows = 0;
	long numOfColumns = 0;
	long numOfLinks = 0;
	long numOfEmbeddedImage = 0;
	boolean useFormula = false;
	boolean isPasswordProtected = false;
	boolean containFilteredList = false;
	
	String name;
	HSSFSheet hssfsheet = null;
	boolean hidden = false;
	Integer bofOffset = -1;
	int numOfRecords = 0;
	
	Worksheet (HSSFSheet ws, String wsname) {
		name = wsname;
		hssfsheet = ws;
	}
	
	Worksheet (String wsname) {
		name = wsname;
	
	}
	
	void setBofOffset(Integer offset) {
		bofOffset = offset;
	}
	
	Integer getBofOffset() {
		return bofOffset;
	}
	void setHidden(boolean wshidden) {
		hidden = wshidden;
	}
	void extract() {
		numOfRows = hssfsheet.getPhysicalNumberOfRows();
	}

	public int compareTo(Worksheet ws) {
		int retCode = 0;		
		Integer wsBofOffset = ws.getBofOffset();
		
		if (bofOffset < wsBofOffset)
			retCode = -1;
		if (bofOffset > wsBofOffset)
			retCode = 1;
		return retCode;
	}
	
	/**
	 * Process in worksheet substream records
	 */
	void processRecord(Record record) {
		numOfRecords += 1;
		switch(record.getSid()) {
		case IndexRecord.sid:
			// make sure IndexRecord is the first record in the worksheet,
			// TODO if not, record anomaly here
			if (numOfRecords != 1)
				System.out.print("IndexRecord must be right after BOF");
			break;
			
		case DimensionsRecord.sid: 
			// look for the dimension information about this worksheet
			DimensionsRecord dim = (DimensionsRecord) record;
			numOfRows = dim.getLastRow() - dim.getFirstRow();
			numOfColumns = dim.getLastCol() - dim.getFirstCol();
			break;

		case PasswordRecord.sid:
			//see if this worksheet is password protected
			isPasswordProtected = true;
			break;

		case FormulaRecord.sid:
			useFormula = true;
			break;
			
		case RecordType.IMDATA: // unsupoorted record in POI, 
			//see if this worksheet contains embedded image
			numOfEmbeddedImage += 1;
			break;
			
		case RecordType.HLINK: // unsupoorted record in POI, 
			//see if this worksheet contains hyper links 
			numOfLinks += 1;
			break;

		case RecordType.FILTERMODE: // unsupoorted record in POI, 
			//see if this worksheet contains filtered list
			containFilteredList = true;
			break;
		default :
		
			break;
		}
	}
}
