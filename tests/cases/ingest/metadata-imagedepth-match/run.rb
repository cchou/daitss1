#!/usr/bin/ruby -w

require 'testutils'

# Run a package with multi-color components images.
# compares the depth in the descriptor verse extracted depth.
# the result should match.  This test case is created for fixing
# bug number #34 in trac.

if run_ingest == :reject
  ng "Ingest rejected."
else
  ok "Ingest did not reject."
end

sql = "select SEVERE_ELEMENT from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = '1.tif')"

if query_strings(sql).grep(/M_DEPTH_MISMATCH/).any?
  ng "Database shows a bogus M_DEPTH_MISMATCH"
else
  ok "Database shows no bogus M_DEPTH_MISTMATCH"
end

