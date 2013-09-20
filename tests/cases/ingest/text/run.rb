#!/usr/bin/ruby -w
#Test case for packag containing text files.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#make sure the text bitstream exists
sql = "select * from BS_TEXT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'test.txt')"

if query_strings(sql).any?
   ok "database exhibits the text bitstream for the text file"
else
  ng "No text bitstream is recorded in database"
end
