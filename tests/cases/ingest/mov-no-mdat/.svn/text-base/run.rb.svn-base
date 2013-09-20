#!/usr/bin/ruby -w

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the QUICKTIME_FILE
sql = "select * from QUICKTIME_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'thesis.mov')"

if query_strings(sql).any?
  ok "QUICKTIME_FILE table contains the associated record for the mov file"
else
  ng "cannot find the record for the mov file in the QUICKTIME_FILE table"
end

#check if the limitation is recorded for unsupported encoding
sql = "select SEVERE_ELEMENT from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'thesis.mov')"

#check anoamly
if query_strings(sql).grep(/A_QUICKTIME_BAD_MINF/).any?
  ok "Database exhibits the expected anomaly"
else
  ng "No anomaly is recorded for unsupported file"
end

#check limitation
if query_strings(sql).grep(/L_QUICKTIME_UNSUPPORTED_VIDEO_ENCODING/).any?
  ok "Database exhibits the expected limitation"
else
  ng "No limitation is recorded for unsupported file"
end
