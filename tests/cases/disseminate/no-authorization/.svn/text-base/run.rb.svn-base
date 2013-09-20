#!/usr/bin/ruby

require 'testutils'

# Tests if an ingested package can be disseminated by a person with no permissions

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]
contact_id = 99
if run_disseminate(:ieid => ieid, :contact_id => contact_id) == :fail
  ok "Dissemination failed"
else
  ng "Dissemination was a success"
end
