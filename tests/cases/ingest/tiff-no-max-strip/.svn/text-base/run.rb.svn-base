#!/usr/bin/ruby -w

# issue #80
# missing MAX_STRIPE_BYTE value in TIFF results in invalid aip descriptor

require 'testutils'

if run_ingest == :ingest
  ok "Ingested"
else
  ng "Rejected"
end

#make sure the tiff bitstream is created
sql = "select * from BS_IMAGE_TIFF " +
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '60.tif')"

if query_strings(sql).any?
   ok "database exhibits the image tiff bitstream for the tiff file"
else
  ng "No tiff bitstream is recorded in database"
end

#make sure missing MAX_STRIP_BYTES is recorded as NULL
sql = "select MAX_STRIP_BYTES from BS_IMAGE_TIFF " +
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '60.tif')"

result = query_arrays(sql)

if result.size > 0 && result[0][0].nil?
   ok "database exhibits NULL value for missing MAX_STRIP_BYTES"
else
   ng "Missing MAX_STRIP_BYTES is not NULL "
end

