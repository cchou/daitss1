#!/usr/bin/ruby -w
#Test case for packag containing text files.

require 'testutils'

pending 'this is not implemented or on a milestone'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

# make sure the report has the filename in the subject
if mailed_report.grep(/^Subject: .* [\w\.]+.xml$/).any?
  ok "Mail has filename in subject"
else
  ng "Mail subject is vanilla"
end
