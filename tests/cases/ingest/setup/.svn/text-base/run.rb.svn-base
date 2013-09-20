#!/usr/bin/ruby -w

require 'testutils'

initialize_database
if run_ingest == :reject
  ng "Ingest rejected."
else
  ok "Ingest succeeded."
end
