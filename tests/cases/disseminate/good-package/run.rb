#!/usr/bin/ruby

require 'testutils'

# Tests if an ingested package can be withdrawn by a person with the
# proper permissions.

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

if run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate
  ok "Dissemination was a success"
else
  ng "Dissemination failed"
end

if query_strings("select OUTCOME from EVENT where OID='#{ieid}' and EVENT_TYPE='D'").any?
  ok 'Event recorded in database'
else
  ng 'Event not recorded in database'
end
