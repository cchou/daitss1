#!/usr/bin/ruby -w
#
# Localization, normalizationa and migration should not be done for full-level 
# preserved files.

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

# make sure format transformation, currently only localization, 
# is performed for xml files
# if N, M, and L exists, PASSED
sql = "select EVENT_TYPE from EVENT " + 
      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'FDA0000400.xml')"

if query_strings(sql).grep(/L/).any?
  ok "Database exhibits the expected localization event"
else
  ng "No localization event for full-level preserved xml files"
end

# the following check is currently turned off till daitss 2.0

# make sure format transformation, currently only normalization, 
# is performed for quicktime files
# if N, M, and L exists, PASSED
# sql = "select EVENT_TYPE from EVENT " + 
#      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'chama.mov')"

#if query_strings(sql).grep(/N/).any?
#  ok "Database exhibits the expected normalization event"
#else
#  ng "No normalization event for ful-level preserved mov files"
#end

# make format transformation, currently only normalization, 
# is performed for avi files
# if N, M, and L exists, PASSED
#sql = "select EVENT_TYPE from EVENT " + 
#      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'welcome1.avi')"

#if query_strings(sql).grep(/N/).any?
#  ok "Database exhibits the expected normalization event"
#else
#  ng "No normalization event for full-level preserved avi files"
#end

# make sure format transformation, currently only normalization, 
# is performed for wave files
# if N, M, and L exists, PASSED
#sql = "select EVENT_TYPE from EVENT " + 
#      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'drmapan.wav')"

#if query_strings(sql).grep(/N/).any?
#  ok "Database exhibits the expected normalization event"
#else
#  ng "No normalization event for full-level preserved wav files"
#end

# make sure format transformation, currently only normalization, 
# is performed for pdf files
# if N, M, and L exists, PASSED
#sql = "select EVENT_TYPE from EVENT " + 
#      "where OID IN (select DFID from DATA_FILE where FILE_TITLE = 'j2ktest_gs.pdf')"

#if query_strings(sql).grep(/N/).any?
#  ok "Database exhibits the expected normalization event"
#else
#  ng "No normalization event for full-level preserved pdf files"
#end
