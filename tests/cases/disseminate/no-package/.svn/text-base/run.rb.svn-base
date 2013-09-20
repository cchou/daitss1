#!/usr/bin/ruby -w

require 'testutils'

if run_disseminate(:contact_id => '1', :ieid => 'DNE0000000') == :fail
  ok "Dissemination has failed"
else
  ng "Dissemination was a success"
end
