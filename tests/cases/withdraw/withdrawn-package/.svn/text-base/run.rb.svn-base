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

setup_withdraw wd_options

if run_withdraw(wd_options) == :fail
  ok "Withdraw failed"
else
  ng "Withdraw was a success"
end

if log.grep /intellectual entity #{ieid} is already withdrawn/
  ok 'Withdraw reported that the contact cannot withdraw the package'
else
  ng 'Withdraw reported that the contact can withdraw the package'
end
