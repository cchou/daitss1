#!/usr/bin/ruby

require 'testutils'
require 'tempfile'

# Tests if multiple withdrawals with some failures and some successes work

output, report = setup_ingest false

# get entries from the setup packages
p_list = setup_packages.collect { |p| "'%s'" % p  }.join ', '
ieids = query_strings("select IEID from INT_ENTITY where PACKAGE_NAME in ( #{ p_list } ) and ENTITY_ID != ''")

# withdraw one of them to have a failure
withdrawn_ieid = ieids[0]
setup_withdraw :ieid => withdrawn_ieid, :type => 'archive', :contact_id => 1

# a package that does not exist
vapor_ieid = 'DNE0000000'
ieids << vapor_ieid

# make a csv file
Tempfile::open('csv') do |tf|
  tf.puts ieids.join(',')
  tf.flush
  
  if run_withdraw(:file => tf.path, :type=> 'archive', :contact_id => 1) == :fail
    ok "Withdraw failed"
  else
    ng "Withdraw did not failed"
  end
  
end

if log.grep(/intellectual entity #{ withdrawn_ieid } is already withdrawn/).any?
ok "Withdraw reported the withdrawn package properly"
else
ng "Withdraw did not report the withdrawn package properly"
end

if log.grep(/intellectual entity #{ vapor_ieid } does not exist in archive/).any?
ok "Withdraw reported the non existant package properly"
else
ng "Withdraw did not report the non existant package properly"
end

if log.read =~ /failed packages: (.*)$/ && $1.split(' ').size == 2
  ok "Withdraw reported the #{ ieids.size - 2 } bad packages"
else
  ng "Withdraw did not report the #{ ieids.size - 2 } bad packages"
end
