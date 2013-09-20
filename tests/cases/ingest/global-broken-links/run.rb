#!/usr/bin/ruby -w
# Test case global broken links

require 'testutils'

if run_ingest == :ingest
  ok "Ingested"
else
  ng "Rejected"
end

if log.grep(/<BROKEN_LINK>.*_LOC.xsd<\/BROKEN_LINK>/).any?
  ng "Broken links for localized schema files found"
else
  ok "No broken links for localized schema files found"
end

