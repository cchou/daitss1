#!/usr/bin/ruby -w

#Ingest a package containing an avi file with cvid video encoding 
#and pcm signed 16 le audio encoding.
require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the AVI_FILE
sql = "select * from AVI_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '90_20_10hz.avi')"

if query_strings(sql).any?
  ok "AVI_FILE table contains the associated record for the ingested avi file"
else
  ng "cannot find the record in the AVI_FILE table for ingested avi file"
end

#check if the video bitstream is created with correct encoding
sql = "select * from BS_VIDEO " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '90_20_10hz.avi')"

if query_strings(sql).grep(/CINEPAK VIDEO/).any?
   ok "database exhibits the video bitstream with expected encoding for the avi file"
else
  ng "Either no video bitstream is recorded or recorded with unexpected video encoding"
end

#check if the audio bitstream is created with correct encoding
sql = "select * from BS_AUDIO " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '90_20_10hz.avi')"

if query_strings(sql).grep(/LINEAR/).any?
   ok "database exhibits the audio bitstream with expected encoding for the avi file"
else
  ng "Either no audio bitstream is recorded in database or recorded with unexpected audio encoding"
end
