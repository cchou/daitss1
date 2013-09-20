#!/usr/bin/ruby -w

require 'testutils'

# We're going to run a package with a known size mismatch in the
# descriptor metatada, as well as the application/pdx mime type. We
# want it to be ingested and to show up in the database, but with
# severe_elements that indictate the size and type problems.

if run_ingest == :reject
  ng "Ingest rejected."
else
  ok "Ingest did not reject."
end

if log.grep(/file size extracted from.*does not match submitted value/).empty?
  ng "Ingest missed file size mismatch in descriptor."
else 
  ok "Ingest correctly shows file size mismatch in descriptor."
end

# if query_strings("select DFID from DATA_FILE where FILE_TITLE = 'owings_r.pdf'").any?

sql = "select DFID from DATA_FILE where FILE_TITLE = 'owings_r.pdf'"

if (query_strings(sql).any?) 
  ok "Database shows proper ingest of package document."  
else
  ng "Database is missing results of ingest of package document."  
end


sql = "select SEVERE_ELEMENT from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'owings_r.pdf')"

if query_strings(sql).grep(/M_FILE_SIZE_MISMATCH/).any?
  ok "Database shows expected FILE_SIZE_MISMATCH"
else
  ng "Database is missing expected FILE_SIZE_MISMATCH"
end

if query_strings(sql).grep(/M_MIME_TYPE_MISMATCH/).any?
  ok "Database shows expected MIME_TYPE_MISMATCH"
else
  ng "Database is missing expected MIME_TYPE_MISMATCH"
end

