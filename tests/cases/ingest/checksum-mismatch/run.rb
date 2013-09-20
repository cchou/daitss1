#!/usr/bin/ruby -w

# We're going to run a package with a known checksum mismatch in the
# descriptor metatada. Ingest should fail while ingesting; the log 
# and the report should indicate the reason for rejecting

require 'testutils'

# did ingest reject?
if run_ingest == :reject
  ok "Ingest properly rejected this package."
else
  ng "Ingest did not reject this package."
end

# did the report tell us it was from a bad checksum?
if mailed_report.grep(/Checksum calculated for owings_r.pdf does not match submitted checksum/).any?
  ok "Mail correctly reported 'does not match submitted checksum'."
else 
  ng "Mail failed to report 'does not match submitted checksum'."
end

# did the log tell us it was from a bad checksum?
if log.grep(/Checksum calculated for owings_r.pdf does not match submitted checksum/).any?
  ok "The log indicated a checksum mismatch."
else
  ng "The log did not indicate a checksum mismatch."
end
