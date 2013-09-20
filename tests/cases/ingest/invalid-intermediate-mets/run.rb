#!/usr/bin/ruby -w

require 'testutils'

# ingest
if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end
