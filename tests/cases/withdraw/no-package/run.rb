#!/usr/bin/ruby -w

require 'testutils'

if run_withdraw(:type => 'account', :contact_id => '1', :ieid => 'DNE0000000') == :fail
  ok "Withdrawal has failed"
else
  ng "Withdrawal was a success"
end

if log.grep(/intellectual entity DNE0000000 does not exist in archive/).any?
  ok "Withdrawal reported 'package does not exist'"
else
  ng "Withdrawal reported 'pacakge does exist'"
end
