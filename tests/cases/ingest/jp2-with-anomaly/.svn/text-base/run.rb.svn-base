#!/usr/bin/ruby -w

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if the jpeg 2000 bitstream exists
sql = "select * from BS_IMAGE_JPEG2000 " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'UF00015234_adobe.jp2')"
if query_strings(sql).any?
   ok "database exhibits the jpeg 2000 bitstream for the jp2 file"
else
  ng "No jpeg 2000 bitstream is recorded in database"
end

#check if the expected anomaly exists
sql = "select * from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'UF00015234_adobe.jp2')"
if query_strings(sql).grep(/A_JPEG2K_BAD_LOC_BOX/).any?
   ok "database exhibits the expected anomaly for the jp2 file"
else
  ng "Missing the expected anomaly"
end

# check if the anomaly does not downgrade the preservation level
sql = "select PRES_LEVEL from DATA_FILE where FILE_TITLE = 'UF00015234_adobe.jp2'"
if query_strings(sql).grep(/FULL/).any?
   ok "the anomaly does not downgrade the preservation level of the jp2 file"
else
  ng "the anomaly change the preservation level"
end

