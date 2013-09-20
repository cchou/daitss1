#!/usr/bin/ruby -w

require 'testutils'

if run_ingest == :reject
  ok "Ingest rejected."
else
  ng "Ingest did not reject."
end

if mailed_report.grep(/No initial descriptor found/).any?
  ok "Mail correctly reported 'no initial descriptor found'."
else 
  ng "Mail failed to report 'no initial descriptor found'."
end
  
if log.grep(/No initial descriptor found/).any?
  ok  "Log correctly reported 'no initial descriptor found'."
else 
  ng "Log failed to report 'no initial descriptor found'."
end
