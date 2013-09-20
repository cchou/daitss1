#!/usr/bin/ruby -w
# Test case on the reingest files whose identified format has 
# been changed since the last ingest.  This is possible in
# the case of added parsers, unknown->f1, or misidentification of
# the original ingest f1->f2.  
#
require 'testutils'

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

# manually alter the format
sql = "update DATA_FILE set FORMAT = 'APP_UNK' where FILE_TITLE = 'j2ktest_gs.pdf'"
query_no_result(sql)

sql = "update DATA_FILE set FORMAT = 'APP_UNK' where FILE_TITLE = 'FDA0000400.xml'"
query_no_result(sql)

sql = "update DATA_FILE set FORMAT = 'APP_UNK' where FILE_TITLE = 'rawfree.datafile'"
query_no_result(sql)

ok "formats successfully altered for test harness"

if run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate
  ok "Dissemination was a success"
else
  ng "Dissemination failed"
end

if run_reingest(true) == :reingest
  ok "ReIngested."
else
  ng "Rejected."
end

# verify the format of the reingested .pdf file is correctly recorded
sql = "select FORMAT from DATA_FILE where FILE_TITLE = 'j2ktest_gs.pdf'"

if query_strings(sql).grep(/APP_PDF/).any?
  ok "Database exhibits the correct format for PDF"
else
  ng "Cannot find the expected format for the PDF file"
end

# verify the format of the reingested .xml file is correctly recorded
sql = "select FORMAT from DATA_FILE where FILE_TITLE = 'FDA0000400.xml'"

if query_strings(sql).grep(/APP_XML/).any?
  ok "Database exhibits the correct format for XML"
else
  ng "Cannot find the expected format for the XML file"
end

# verify the format of the reingested text file is correctly recorded
sql = "select FORMAT from DATA_FILE where FILE_TITLE = 'rawfree.datafile'"

if query_strings(sql).grep(/TXT_PLAIN/).any?
  ok "Database exhibits the correct format for TEXT"
else
  ng "Cannot find the expected format for the TEXT file"
end
