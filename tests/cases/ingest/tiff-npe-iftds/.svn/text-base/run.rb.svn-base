#!/usr/bin/ruby -w
#Test case for tiff files.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#make sure the tiff bitstream is created
sql = "select * from BS_IMAGE_TIFF " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'CATALOGUE_Page_011.tif')"

if query_strings(sql).any?
   ok "database exhibits the image tiff bitstream for the tiff file"
else
  ng "No tiff bitstream is recorded in database"
end
