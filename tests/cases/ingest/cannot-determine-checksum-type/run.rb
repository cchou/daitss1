#!/usr/bin/ruby -w

require 'testutils'

pending 'this needs looking at'

if run_ingest == :reject
  ok "Ingest properly rejected the package."
else
  ng "Ingest did not reject the package."
end
