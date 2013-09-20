#!/usr/bin/ruby -w

require 'testutils'

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

if copies(ieid).any?
  ok "Copies are in the database"
else
  ng "No copies in the database"
end

if run_withdraw(:ieid => ieid, :type=> 'account', :contact_id => 1) == :withdraw
  ok "Withdraw was a success"
else
  ng "Withdraw failed"
end

if mailed_report.grep(/WITHDRAWAL IEID="#{ieid}"/).any?
  ok "Withdrawal correctly reported"
else
  ng "Withdrawal not correctly reported"
end

if query_strings("select OUTCOME from EVENT where OID='#{ieid}' and EVENT_TYPE='WO'").any?
  ok 'Event recorded in database'
else
  ng 'Event not recorded in database'
end

if query_strings("select * from DATA_FILE where IEID = '#{ieid}'").any?
  ng 'Data files exist'
else
  ok 'No data files exist'
end

if copies(ieid).any?
  ng "Copies are in the database"
else
  ok "No copies in the database"
end