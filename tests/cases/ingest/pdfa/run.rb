#!/usr/bin/ruby -w
#Test a package containing both PDFA conformed and non PDFA conformed PDFs.

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

#make sure pdfa conformed file is associated with the pdfa format attribute
sql = "select FORMAT_ATTRIBUTE from DATA_FILE_FORMAT_ATTRIBUTE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'TestingPDFA.pdf')"

if query_strings(sql).grep(/APP_PDF_PDFA_1B_CONF/).any?
  ok "Database shows expected PDFA format attribute"
else
  ng "Database is missing expected PDFA format attribute"
end

#make sure non-pdfa conformed file does not has the pdfa format attribute
sql = "select FORMAT_ATTRIBUTE from DATA_FILE_FORMAT_ATTRIBUTE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'jk2test_ID_missing.pdf')"

if query_strings(sql).grep(/APP_PDF_PDFA_1A_CONF/).any?
  ng "Database shows the unexpected PDFA format attribute"
else
  ok "Database correctly contain no PDFA format attribute"
end
