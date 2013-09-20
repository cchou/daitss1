package edu.fcla.daitss.format.spreadsheet.biff8;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Workbook implements HSSFListener {
	// Section 1: extracted metadata
	int numSheets = 0;	// number of sheets
	int numCharts = 0;  // number of chars
	int numVBA = 0;		// number of VBA macros
	int numExternRef = 0;// number of external references
	boolean use1904 = false;// does this workbook use 1904 date system
	long numOfFonts = 0; // number of fonts
	boolean sawWorkbookGlobal = false;
	int version = -1;
	boolean hasPivotTable = false;
	
	String title;		// workbook title
	String subject;
	String comments;
	String author;	
	int security;		// workbook security setting
	byte[] thumbnail;
	
	//contains a sorted list of all worksheets, sorted by the offset to the begining of the workbook stream
	SortedSet<Worksheet> sheets = new TreeSet<Worksheet>();
	Worksheet currentWorksheet = null;
	SummaryInformation summaryInfoStream = null;
	DocumentInputStream wbStream = null;
	int numOfBOFs = 0;
	int numOfEOFs = 0;
	
	/**
	 * build a workbook stream from the POIS file stream
	 * @param poifs
	 */
	Workbook(POIFSFileSystem poifs) {
		try {
			//get the workbook stream
			wbStream = poifs.createDocumentInputStream("Workbook");
			HSSFRequest request = new HSSFRequest();
			// listen for all records with the request listener
			request.addListenerForAllRecords(this);
			// create the event factory for our listener
			HSSFEventFactory facotry = new HSSFEventFactory();
			facotry.processEvents(request, wbStream);
		} catch (IOException e) {
			System.out.print("No Workbook stream");
			// TODO record anomaly
		}
	}
	
	/**
	 * clean up works
	 */
	protected void finalize() {
		// close out all the internal substream handlers
		try {
			wbStream.close();
		} catch (IOException ex) {
			
		}
	}
	
	/**
	 * get the workbook related metadata from the summary information stream
	 * @param si
	 */
	public void setSummary(SummaryInformation si) {
		summaryInfoStream = si;
		
		if (summaryInfoStream != null) {
			title = summaryInfoStream.getTitle();
			subject = summaryInfoStream.getSubject();
			comments = summaryInfoStream.getComments();
			author = summaryInfoStream.getLastAuthor();
			security = summaryInfoStream.getSecurity();
			thumbnail = summaryInfoStream.getThumbnail();
		}
		
		// TODO: check security code
	}
	
	/**
	 * process the BOF (Beginning of File) record.
	 * @param bof
	 */
	private void processBOF(BOFRecord bof) {
		int type = bof.getType();
		
		switch (type) {
		case (BOFRecord.TYPE_WORKBOOK):
			sawWorkbookGlobal = true;
			//TODO the workbook global needs to be the first stream.
			
			version  = bof.getVersion();
			//TODO make sure it's 0x06 for BIFF8
			break;
		case (BOFRecord.TYPE_WORKSHEET):
			Worksheet[] allSheets = (Worksheet[]) sheets.toArray(new Worksheet[sheets.size()]);
			currentWorksheet = allSheets[numSheets];
			numSheets += 1;
			break;
		case (BOFRecord.TYPE_CHART):
			numCharts += 1;
			break;
		case (BOFRecord.TYPE_VB_MODULE):
			numVBA += 1;
			break;
		case BOFRecord.TYPE_EXCEL_4_MACRO:
		case BOFRecord.TYPE_WORKSPACE_FILE:
			// don't do anything
			break;
			
		default:
			//TODO unknown substream type,record anomaly
			
			break;
		}
		numOfBOFs += 1;
	}
	
	/**
	 * the handler for incoming records in the workbook stream
	 */
	public void processRecord (Record record) {
		System.out.print(record.toString());
		int sid = record.getSid();
		
		//TODO record data must be less or equal to 8224
		
		switch (sid) {
			case BOFRecord.sid:
				processBOF((BOFRecord) record);
				break;
				
			case EOFRecord.sid:
				currentWorksheet = null;
				numOfEOFs += 1;
				break;
				
			case BoundSheetRecord.sid:
				BoundSheetRecord bsr = (BoundSheetRecord) record;
				String sheetname = bsr.getSheetname();
				Worksheet ws = new Worksheet(sheetname);
				Integer pos = new Integer(bsr.getPositionOfBof());
				ws.setBofOffset(pos);
				ws.setHidden(bsr.isHidden());
				sheets.add(ws);
				break;
				
			case DateWindow1904Record.sid:
				// look for Date system info.
				DateWindow1904Record date = (DateWindow1904Record) record;
				if (date.getWindowing() == 1)
					use1904 = true;
				else
					use1904 = false;
				break;
				
			case FontRecord.sid:
				numOfFonts += 1;
				break;
				
			case ExternCountRecord.sid:
				// get the number of external references
				ExternCountRecord ecr = (ExternCountRecord) record;
				numExternRef = ecr.getcxals();
				break;
				
			case RecordType.SXDB:
				// pivot table cache data
				hasPivotTable = true;
				break;
				
			default:
				// TODO make sure it is valid record
				// all other records, let the worksheet handles them
				if (currentWorksheet != null)
					currentWorksheet.processRecord(record);
			
				break;
		}
	}

	public void checkSubstreams() {
		if (numOfBOFs != numOfEOFs) {
			//TODO record anomaly
			System.out.print("BOF records does not pair up with EOF records in each substream");
		}
	}
	
	/**
	 * workbook version (BIFF version)
	 * @return
	 */
	public int getVersion() { 
		return version;
	}
}
