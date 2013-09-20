#!/usr/bin/ruby -w

require 'testutils'

pending 'something is wrong, needs to be looked at'

ENV['MOCK_EXIT_CODE_CLAMSCAN'] = "1"

if run_ingest == :reject
  ok "Virus infected package rejected"
else
  ng "Virus infected package not rejected"
end

if log.grep(/Virus found/).any?
  ok 'Virus reported'
else
  ng 'Virus not reported'
end
