#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :reject
  ok "Prep rejected."
else
  ng "Prep succeeded."
end

if log.grep(/No package descriptor found/).any?
  ok "Prep reported that the pacakge has no descriptor."
else
  ng "Prep did not report that the pacakge has no descriptor."
end
