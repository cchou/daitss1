#!/usr/bin/ruby -w
#Test case for packag containing text files.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

# check if an limitation is recorded
sql = " select * from DATA_FILE_SEVERE_ELEMENT where DFID in " +
  "(select DFID from DATA_FILE where FILE_TITLE = '00001.flac')"
if query_strings(sql).grep(/L_FILE_UNKNOWN/).any?
   ok "database exhibits expected limitation for unknown files"
else
   ng "no limitation is recorded for unknown file"
end

# make sure a warning is recorded in the report
if mailed_report.grep(/L_FILE_UNKNOWN/).any?
  ok "Mail correctly reported warning for unknown file"
else 
  ng "Mail failed to report a warning for unknown files"
end
