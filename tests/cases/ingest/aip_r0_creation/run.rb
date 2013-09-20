#!/usr/bin/ruby -w

require 'testutils'

pending 'this test is broken'
# ingest
if run_ingest == :ingest
  ng "Ingested."
else
  ok "Rejected."
end

# check to see if the silos contain stuff from the report
if mailed_report.grep(/<ERROR /).any?
  ok "Report Sent"
else
  ng "No Report"
end

if log.grep(/\d+ repeated files exist/).any?
  ok 'duplicate files reported'
else
  ng 'duplicate files not reported'
end
