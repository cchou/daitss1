#!/usr/bin/ruby -w
# Test case for preserving the PRO files.  The archive logic in this test case
# should be set for preserving PRO as BIT while TEXT files are preserved as
# full (UF agreement).

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

# make sure the pro files are preserved as bit
sql = "select PRES_LEVEL from DATA_FILE where FILE_TITLE = '00004.pro'"

if query_strings(sql).grep(/BIT/).any?
  ok "PRO files are preserved as BIT, as expected"
else
  ng "PRO files are not preserved as BIT"
end

# make sure the pro file containing DEL(7f) characters are identified as PRO
sql = "select FORMAT from DATA_FILE where FILE_TITLE = '00001.pro'"

if query_strings(sql).grep(/APP_PRO/).any?
  ok "PRO file with DEL characters is identified as APP_PRO"
else
  ng "PRO file with DEL characters is not identified as APP_PRO"
end

# make sure there is no warning for pro file
if mailed_report.grep(/WARNING/).any?
  ng "There is unexpected warning in the report"
else 
  ok "No Warning"
end

# make sure the text files are preserved as full.
sql = "select PRES_LEVEL from DATA_FILE where FILE_TITLE = '00004.txt'"

if query_strings(sql).grep(/FULL/).any?
  ok "text files are preserved as FULL, as expected"
else
  ng "text file are not preserved as FULL"
end



