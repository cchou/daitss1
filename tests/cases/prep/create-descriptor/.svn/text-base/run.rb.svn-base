#!/usr/bin/ruby -w

require 'testutils'

if run_prep(:create_descriptor => true, :account => 'FDA', :project => 'FDA') == :prep
  ok "Prep succeeded."
else
  ng "Prep rejected."
end

# make sure a descriptor exists
if log.grep(/Creating METS descriptor for package/).any?
  ok "Prep reported creating a descriptor"
else
  ng "Prep did not report creating a descriptor"
end

if in_prep_out? 'FDA0000010/FDA0000010.xml'
  ok "descriptor file was created"
else
  ng "descriptor file was not created"
end
