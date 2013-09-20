#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :reject
  ok "Prep rejected."
else
  ng "Prep succeeded."
end

if log.grep(/Unknown non-xml package descriptor, descriptor will not be generated/).any?
  ok 'Prep reported that an unknown descriptor type was present'
else
  ng 'Prep did not report that an unknown descriptor type was present'
end
