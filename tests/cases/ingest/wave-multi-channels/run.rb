#!/usr/bin/ruby -w
#Test multi-channel wave files.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#4 channels
#check if we successfully create the record in the WAVE_FILE
sql = "select * from WAVE_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '4ch.wav')"

if query_strings(sql).any?
  ok "WAVE_FILE table contains the associated record for the 4-channels wave file"
else
  ng "cannot find the record for the wave file in the WAVE_FILE table for 4-channels wave file"
end

#make sure the bitstream is created
sql = "select * from BS_AUDIO_WAVE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '4ch.wav')"

if query_strings(sql).any?
   ok "find the audio wave bitstream for the wave file"
else
  ng "No wave bitstream is recorded"
end

#6 channels
#check if we successfully create the record in the WAVE_FILE
sql = "select * from WAVE_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '6_Channel_ID.wav')"

if query_strings(sql).any?
  ok "WAVE_FILE table contains the associated record for the wave file"
else
  ng "cannot find the record for the wave file in the WAVE_FILE table"
end

#make sure the bitstream is created
sql = "select * from BS_AUDIO_WAVE " +
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '6_Channel_ID.wav')"

if query_strings(sql).any?
   ok "find the audio wave bitstream for the 6-channels wave file"
else
  ng "No wave bitstream is recorded"
end

#check if we successfully create the record in the WAVE_FILE
sql = "select * from WAVE_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '8_Channel_ID.wav')"

if query_strings(sql).any?
  ok "WAVE_FILE table contains the associated record for the wave file"
else
  ng "cannot find the record for the wave file in the WAVE_FILE table"
end

#make sure the bitstream is created
sql = "select * from BS_AUDIO_WAVE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '8_Channel_ID.wav')"

if query_strings(sql).any?
   ok "find the audio wave bitstream for the 8-channels wave file"
else
  ng "No wave bitstream is recorded"
end
