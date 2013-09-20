#!/usr/bin/ruby -w
#Ingest a package containing a quicktime file with SMC
#video encoding

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the QUICKTIME_FILE
sql = "select * from QUICKTIME_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'HFSup.mov')"

if query_strings(sql).any?
  ok "QUICKTIME_FILE table contains the associated record for the ingested mov file"
else
  ng "cannot find the record in the QUICKTIME_FILE table for ingested mov file"
end

#make sure a video bitstream is created with correct encoding
sql = "select * from BS_VIDEO " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'HFSup.mov')"

if query_strings(sql).grep(/GRAPHICS/).any?
   ok "database exhibits the video bitstream with expected encoding for the mov file"
else
   ng "Either no video bitstream is recorded or recorded with unexpected video encoding"
end

#no audio
