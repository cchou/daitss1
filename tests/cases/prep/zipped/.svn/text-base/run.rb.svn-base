#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :prep
  ok "Prep succeeded."
else
  ng "Prep rejected."
end

if log.grep(/successfully pre-processed/).any?
  ok 'Prep reported the zipped package was accepted'
else
  ng 'Prep did not report the zipped package was processed'
end
