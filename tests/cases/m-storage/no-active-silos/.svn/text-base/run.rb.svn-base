#!/usr/bin/ruby -w

require 'testutils'

$exit_on_crash = false

begin
  run_ingest
  ng 'ingest completed'
rescue CrashError => e
  ok 'ingest crashed'
end

"proper message in log".expect log.grep(/No active silos in Archive/).any?
