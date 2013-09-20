#!/usr/bin/ruby -w
#Test a package containing both PDFA conformed and non PDFA conformed PDFs.

require 'testutils'

ENV["MOCK_EXIT_CODE_GS"] = "1"

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

#make sure a limitation is recorded
sql = "select * from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'kim_y.pdf')"

if query_strings(sql).grep(/L_PDF_UNSUPPORTED_RESOURCE/).any?
  ok "Database shows expected PDF UNSUPPORTED_RESOURCE limitation"
else
  ng "Database is missing expected PDF unsupported resource limitation"
end
