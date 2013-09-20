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

if get_prep_out('WF00000038/WF00000038.xml').grep(/<mets:mets/i).any?
  ok 'Prep did a MXF to METS conversion'
else
  ng 'Prep did not do a MXF to METS conversion'
end
