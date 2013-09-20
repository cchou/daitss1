#!/usr/bin/ruby -w
#test a packing containing a quote in the filename for proper database sanitization of user input

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ng "Ingest rejected this package."
else
  ok "Ingest processed this package."
end
