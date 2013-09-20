#!/usr/bin/ruby -w
#Test a wave file with alaw encoding.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if we successfully create the record in the WAVE_FILE
sql = "select * from WAVE_FILE " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'addf8-Alaw-GW.wav')"

if query_strings(sql).any?
  ok "WAVE_FILE table contains the associated record for the wave file"
else
  ng "cannot find the record for the wave file in the WAVE_FILE table"
end

#check if the limitation is recorded for unsupported encoding
sql = "select SEVERE_ELEMENT from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'addf8-Alaw-GW.wav')"

if query_strings(sql).grep(/L_WAVE_UNSUPPORTED_ENCODING/).any?
  ok "Database exhibits the expected limitation"
else
  ng "No limitation is recorded for unsupported file"
end
