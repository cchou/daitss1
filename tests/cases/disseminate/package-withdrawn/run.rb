#!/usr/bin/ruby

require 'testutils'

# Tests if an withdrawn package can be withdrawn

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]

contact_id = 1

wd_options = {
  :ieid => ieid, 
  :type=> 'account', 
  :contact_id => contact_id
}

dis_options = {
  :ieid => ieid,
  :contact_id => contact_id
}

setup_withdraw wd_options

if run_disseminate(dis_options) == :fail
  ok "Dissemiantion failed"
else
  ng "Dissemination was a success"
end
