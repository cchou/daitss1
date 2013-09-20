#!/usr/bin/ruby -w

require 'testutils'

pending 'this needs looking at'

ENV["MOCK_EXIT_CODE_CLAMSCAN"] = "1"

if run_ingest == :reject
  ok "Unexpected virus scanner return value resulted in package rejected"
else
  ng "Unexpected virus scanner return value did not result in package rejected"
end

if log.grep(/Virus found/).any?
  ok 'Virus reported'
else
  ng 'Virus not reported'
end
