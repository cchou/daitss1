#!/usr/bin/ruby

# make sure disseminate is cleaning up after itself

require 'testutils'

# make sure the temp_dir is clean
clean_tempdir

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

if run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate
  ok "Dissemination was a success"
else
  ng "Dissemination failed"
end

# check for cleanup
if temp_files.empty?
  ok "tempdir is empty"
else
  ng "tempdir has files"
end
