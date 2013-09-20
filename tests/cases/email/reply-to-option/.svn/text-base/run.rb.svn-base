#!/usr/bin/ruby -w
#Test case for packag containing text files.

require 'testutils'

$dont_flush_messages = true
if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

if $smtp_server.messages.last.header['Reply-to'] == 'foo@bar.baz'
  ok "Mail has proper reply-to"
else
  ng "Mail has does not have proper reply-to"
end
