#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :prep
  ok "Prep succeeded."
else
  ng "Prep rejected."
end

if log.grep(/is not an acceptable package and will be ignored/).any?
  ok 'Prep rejected the gzipped tarball'
else
  ng 'Prep did not reject the gzipped tarball'
end
