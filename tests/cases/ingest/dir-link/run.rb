#!/usr/bin/ruby -w
#Test relative links that cannot be retrieved, thus only resolved to parent directory.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

