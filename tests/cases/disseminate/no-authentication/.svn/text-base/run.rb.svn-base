#!/usr/bin/ruby

require 'testutils'

# Tests if an ingested package can be disseminated by an unknown person

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

bad_contact_id = query_strings('select max(ID) + 1 from CONTACT')[0].to_i

if run_disseminate(:ieid => ieid, :contact_id => bad_contact_id) == :fail
  ok "Dissemination failed"
else
  ng "Dissemination was a success"
end
