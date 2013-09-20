#!/usr/bin/ruby -w
#Test ingest's mime type case sensitivity usage with a SIP descriptor containing "application/PDF"

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end

