#!/usr/bin/ruby -w

require 'testutils'

def log_file_match (rexp)
  log.read.each_line { |line| return true if line =~ rexp }
  return false
end

# ingest

if run_ingest == :reject
  ok "Descriptor with invalid date format properly rejected."
else
  ng "Descriptor with invalid date format was improperly ingested."
end

# correct error?

if log_file_match(/'2006-7-27 T14:47:00Z' is not a valid value for 'dateTime'/)
  ok "Found the expected XML dateTime issue."
else
  ng "Could not find the expected XML dateTime issue."
end
