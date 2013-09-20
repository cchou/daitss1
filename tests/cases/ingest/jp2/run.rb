#!/usr/bin/ruby -w

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if the jpeg 2000 bitstream exists
sql = "select * from BS_IMAGE_JPEG2000 " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'WF00010000.jp2')"
if query_strings(sql).any?
   ok "database exhibits the jpeg 2000 bitstream for the jp2 file"
else
  ng "No jpeg 2000 bitstream is recorded in database"
end

#check if there is any anomoly
sql = "select * from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'WF00010000.jp2')"
if query_strings(sql).any?
   ng "database exhibits unexpected anomaly for the jp2 file"
else
   ok "no anomaly"
end

#check if the jpeg 2000 bitstream exists
sql = "select * from BS_IMAGE_JPEG2000 " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'UF00015234_kakadu.jp2')"
if query_strings(sql).any?
   ok "database exhibits the jpeg 2000 bitstream for the jp2 file"
else
  ng "No jpeg 2000 bitstream is recorded in database"
end

#check if there is any anomoly
sql = "select * from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'UF00015234_kakadu.jp2')"
if query_strings(sql).any?
   ng "database exhibits unexpected anomaly for the jp2 file"
else
   ok "no anomaly"
end