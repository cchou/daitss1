#!/usr/bin/ruby -w
#Test PDFs containing cycled outline.  This cause JHOVE to be in infinite loop.
#Though we have fixed our copy of JHOVE code, this test case could be used
#for future JHOVE release.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

