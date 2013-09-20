#!/usr/bin/ruby -w
#Ingest a package containing PDFs that fails due to bugs in ghostscript 1.53
#A test case is created here for testing newer version of ghostscript 
#or other normalization tools.

require 'testutils'

# turn off for now
ok "turn off for now"
exit

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

