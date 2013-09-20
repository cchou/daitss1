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
csv_file = nil
Tempfile::open('') do |tf|
  tf.puts ieids.join(',')
  csv_file = tf.path
end

if run_disseminate(:file => csv_file, :contact_id => 1) == :fail
  ok "Disseminate failed"
else
  ng "Disseminate did not fail"
end
