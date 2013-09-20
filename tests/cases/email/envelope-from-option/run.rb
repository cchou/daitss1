#!/usr/bin/ruby -w
#Test case for packag containing text files.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

if $smtp_server.last_from == 'envelope.from@bar.baz'
  ok "Mail has proper envelope from"
else
  ng "Mail has does not have proper envelope from"
end
