#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :prep
  ok "Prep succeeded."
else
  ng "Prep rejected."
end

if log.grep(/setting CHECKSUMTYPE="MD5" for file/).any?
  ok "Prep reported setting the checksumtype"
else
  ng "Prep did not report setting the checksumtype"
end

if get_prep_out('FDA0000010/FDA0000010.xml').grep(/CHECKSUMTYPE="MD5"/).any?
  ok "Prep set the checksumtype"
else
  ng "Prep didn't set the checksumtype"
end
