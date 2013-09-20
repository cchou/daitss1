#!/usr/bin/ruby -w

require 'testutils'

output, report = setup_ingest
ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

original_copies = query_arrays "select * from COPY where IEID='#{ieid}' order by ID"

query_no_result 'update SILO set ACTIVE = FALSE'
"dissemination completed".expect(run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate)

$exit_on_crash=false
begin
  run_reingest(true) != :reingest
  ng 'reingest completed'
rescue CrashError => e
  ok 'reingest crashed'
end

"proper message in log".expect log.grep(/Message: No active silos available/)

current_copies = query_arrays "select * from COPY where IEID='#{ieid}' order by ID"
"original silos exist".expect original_copies == current_copies