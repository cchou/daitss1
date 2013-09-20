#!/usr/bin/ruby -w

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if the jpeg bitstream is created
sql = "select * from BS_IMAGE_JPEG " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '2004TDG_75.jpg')"

if query_strings(sql).any?
   ok "database exhibits the image jpeg bitstream for the jpeg file"
else
  ng "No jpeg bitstream is recorded in database"
end
