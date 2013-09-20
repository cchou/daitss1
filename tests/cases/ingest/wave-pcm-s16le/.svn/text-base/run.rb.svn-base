#!/usr/bin/ruby -w
#Test wave files with pcm signed 16 little encoding.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the WAVE_FILE
sql = "select * from WAVE_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'drmapan.wav')"

if query_strings(sql).any?
  ok "WAVE_FILE table contains the associated record for the wave file"
else
  ng "cannot find the record for the wave file in the WAVE_FILE table"
end

#check if the bitstream is created
sql = "select * from BS_AUDIO_WAVE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'drmapan.wav')"

if query_strings(sql).any?
   ok "find the audio wave bitstream for the wave file"
else
  ng "No wave bitstream is recorded"
end
