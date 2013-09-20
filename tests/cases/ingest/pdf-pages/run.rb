#!/usr/bin/ruby -w
#Test PDFs containing page dictionary

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

