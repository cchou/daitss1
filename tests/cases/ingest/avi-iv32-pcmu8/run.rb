#!/usr/bin/ruby -w

#Ingest a package containing an avi file with Indeo 3.2 video encoding 
#and pcm unsigned 8 audio encoding.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the AVI_FILE
sql = "select * from AVI_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'video.avi')"

if query_strings(sql).any?
  ok "AVI_FILE table contains the associated record for the ingested avi file"
else
  ng "cannot find the record in the AVI_FILE table for ingested avi file"
end

#check if the video bitstream is created
sql = "select * from BS_VIDEO " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'video.avi')"

if query_strings(sql).grep(/INDEO VIDEO 32/).any?
   ok "database exhibits the video bitstream with expected encoding for the avi file"
else
  ng "Either no video bitstream is recorded or is recorded with unexpected encoding"
end

#check if the audio bitstream is created
sql = "select * from BS_AUDIO " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'video.avi')"

if query_strings(sql).grep(/LINEAR/).any?
   ok "database exhibits the audio bitstream with expected encoding for the avi file"
else
  ng "either no audio bitstream is recorded or is recorded with unexpected encoding"
end
