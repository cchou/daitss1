#!/usr/bin/ruby -w
# Localization, normalizationa and migration should not be done for bit-level 
# preserved files.

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

# make format transformation, currently only normalization, 
# is not performed for quicktime files
# if N, M, and L not exists, PASSED
sql = "select EVENT_TYPE from EVENT " + 
      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'chama.mov')"

if query_strings(sql).grep(/N/).any?
  ng "Database exhibits the unexpected normalization event"
else
  ok "No normalization event for bit-level preserved files"
end

# make format transformation, currently only normalization, 
# is not performed for avi files
# if N, M, and L not exists, PASSED
sql = "select EVENT_TYPE from EVENT " + 
      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'welcome1.avi')"

if query_strings(sql).grep(/N/).any?
  ng "Database exhibits the unexpected normalization event"
else
  ok "No normalization event for bit-level preserved files"
end

# make format transformation, currently only normalization, 
# is not performed for wave files
# if N, M, and L not exists, PASSED
sql = "select EVENT_TYPE from EVENT " + 
      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'drmapan.wav')"

if query_strings(sql).grep(/N/).any?
  ng "Database exhibits the unexpected normalization event"
else
  ok "No normalization event for bit-level preserved files"
end

# make format transformation, currently only normalization, 
# is not performed for pdf files
# if N, M, and L not exists, PASSED
sql = "select EVENT_TYPE from EVENT " + 
      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'j2ktest_gs.pdf')"

if query_strings(sql).grep(/N/).any?
  ng "Database exhibits the unexpected normalization event"
else
  ok "No normalization event for bit-level preserved files"
end
