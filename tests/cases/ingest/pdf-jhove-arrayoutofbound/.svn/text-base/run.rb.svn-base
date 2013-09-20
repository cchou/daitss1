#!/usr/bin/ruby -w
#Test a package containing a pdf which cause a null pointer in pjx->PDFReferenceObject.

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

#make sure PDF bitstream is recorded in the database
sql = "select * from BS_PDF " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'michel_m.pdf')"

if query_strings(sql).any?
  ok "Database shows expected PDF bitstream"
else
  ng "Database is missing expected PDF bitstream"
end


