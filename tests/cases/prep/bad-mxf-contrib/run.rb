#!/usr/bin/ruby -w

require 'testutils'

if run_prep == :reject
  ng "Prep rejected."
else
  ok "Prep succeeded."
end

if log.grep(/MXF descriptor found.*converting to METS/).any?
  ok 'Prep reported that an MXF to METS conversion was made'
else
  ng 'Prep did not report that an MXF to METS conversion was made'
end

if get_prep_out('UFE1001137/UFE1001137.xml').grep(/#{ENV['USER']}/).any? &&
  get_prep_out('UFE1001137/UFE1001137.xml').grep(/MXF2METS/).any?
  ok 'Prep recovered the missing contrib attribute for program and user'
else
  ng 'Prep did not recover the missing contrib attribute for program and user'
end
