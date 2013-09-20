#!/usr/bin/ruby -w
#testcase for DAITSS ticket #141.  This text file was causing an array out of bound
#exeption while parsing for UTF8

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if the text bitstream is created
sql = "select * from BS_TEXT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '00083.txt')"

if query_strings(sql).any?
   ok "database exhibits the text bitstream for the text file"
else
  ng "No text bitstream is recorded in database"
end
