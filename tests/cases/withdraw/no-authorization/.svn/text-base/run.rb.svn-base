#!/usr/bin/ruby

require 'testutils'

# Tests if an ingested package can be withdrawn by a person with no permissions

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]
contact_id = 99

if run_withdraw(:ieid => ieid, :type=> 'account', :contact_id => contact_id) == :fail
  ok "Withdraw failed"
else
  ng "Withdraw was a success"
end

if log.grep /intellectual entity #{ieid} cannot be withdrawn by contact #{contact_id}/
  ok 'Withdraw reported that the contact cannot withdraw the package'
else
  ng 'Withdraw reported that the contact can withdraw the package'
end

if copies(ieid).any?
  ok "Copies are in the database"
else
  ng "No copies in the database"
end

if query_strings("select OUTCOME from EVENT where OID='#{ieid}' and EVENT_TYPE='WO'").any?
  ng 'Event recorded in database'
else
  ok 'Event not recorded in database'
end

if query_hashes("select * from DATA_FILE where IEID = '#{ieid}'").any?
  ok 'Data files exist'
else
  ng 'No data files exist'
end
