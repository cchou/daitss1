package edu.fcla.daitss.format.spreadsheet.biff8;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndian;

public class ExternCountRecord extends Record {
	   public final static short sid = 0x16;
	   private short             cxals;
	   
	   /**
	     * Constructs an empty ExternCountRecord 
	     */
	    public ExternCountRecord()
	    {
	    }

	    /**
	     * Constructs a ExternCountRecord and sets its fields appropriately
	     * @param in the RecordInputstream to read the record from
	     */

	    public ExternCountRecord(RecordInputStream in)
	    {
	        super(in);
	    }

	    protected void validateSid(short id)
	    {
	        if (id != sid)
	        {
	            throw new RecordFormatException("NOT AN EXTERN COUNT RECORD");
	        }
	    }

	    protected void fillFields(RecordInputStream in)
	    {
	    	cxals  = in.readShort();
	    }
	    public int serialize(int offset, byte [] data)
	    {
	        LittleEndian.putShort(data, 0 + offset, sid);
	        LittleEndian.putShort(data, 2 + offset,
	                              (( short ) 0x02));   // 2 byte length
	        LittleEndian.putShort(data, 4 + offset, getcxals());
	        return getRecordSize();
	    }

	    public int getRecordSize()
	    {
	        return 6;
	    }
	    public short getSid()
	    {
	        return sid;
	    }
	    public short getcxals() { return cxals;}
}
