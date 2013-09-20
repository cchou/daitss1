#!/usr/bin/ruby -w
#Test wave files with padded INFO chunk
require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the WAVE_FILE
sql = "select * from WAVE_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'thompson_kelvin_200508_EdD.wav')"

if query_strings(sql).any?
  ok "WAVE_FILE table contains the associated record for the wave file"
else
  ng "cannot find the record for the wave file in the WAVE_FILE table"
end
