#!/usr/bin/ruby -w
#Test PDFs containing page dictionary.  The PDFs for this test case
#cannot be normalized by ghostscript and thus, will fail if using
#the default daitss.properties.  Hence, this test case temporary uses customized
#daitss.properties file until the ghostscript problem is fixed.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if the jpeg 2000 bitstream exists
sql = "select * from BS_PDF " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'araujo_i.pdf')"
if query_strings(sql).any?
   ok "database exhibits the PDF bitstream for the PDF file"
else
  ng "Expected PDF bitstream is not recorded in database"
end

#make sure there is no bogus A_PDF_WRONG_TYPE anomaly
sql = "select * from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'araujo_i.pdf')"
if query_strings(sql).grep(/A_PDF_WRONG_TYPE/).any?
   ng "Bogus PDF_WRONG_TYPE anomaly"
else
   ok "database does not show anomaly for the PDF file"
end

#make sure the "Lang" metadata is extracted correctly
sql = "select NATL_LANG from BS_PDF " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'araujo_i.pdf')"
if query_strings(sql).grep(/en-US/).any?
   ok "the natural language metadata is extracted correctly"
else
   ng "database does not show the expected natural language metadata for the PDF"
end