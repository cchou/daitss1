#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :prep
  ok "Prep succeeded."
else
  ng "Prep rejected."
end

if log.grep(/Renaming descriptor: .* is now/).any?
  ok "Prep reported that the descriptor was renamed"
else
  ng "Prep did not report the descriptor was renamed"
end

if in_prep_out? 'FDA0000020/FDA0000020.xml'
  ok "descriptor file got renamed"
else
  ng "descriptor file did not get renmaed"
end
