#!/usr/bin/ruby

require 'testutils'

# Tests if an ingested package can be disseminated if there is a COPY table record in a retired silo.

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

# insert retired silo
query_no_result "INSERT INTO SILO (ID, URL, ACTIVE, NOTES, COMMON_NAME, RETIRED) VALUES ('', 'http://localhost/retired', 0, '', '', 1)"

# insert copy record for retired silo
silo_id = query_strings("SELECT ID FROM SILO WHERE RETIRED = 1")[0]

query_no_result "INSERT INTO COPY (ID, IEID, SILO, PATH, MD5) VALUES ('', '#{ieid}', '#{silo_id}', '#{ieid}', '00000000000000000000000000000000')"

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
