#!/usr/bin/ruby -w

require 'testutils'

if run_prep(:prune => true) == :reject
  ng "Prep rejected."
else
  ok "Prep succeeded."
end

if log.grep(/Pruning undescribed file .*Idaho_fire_time1\.tif/).any?
  ok "Prep reported that undescribed file was removed."
else 
  ng "Prep did not report that undescribe file was removed"
end

unless in_prep_out? 'FDA0000010/Idaho_fire_time1.tif'
  ok "Prep removed undescribed file."
else
  ng "Prep did not remove undescribed file."
end
