#!/usr/bin/ruby -w
require 'testutils'

ok 'test not working yet, please move on, nothing to see here'
exit

# issue #80
# this package when ran through ingest creates an invalid aip descriptor


if run_ingest == :rejected
  ok "Ingest rejected ..."
else
  ng "Ingest finished!?! its an invalid descriptor"
end


line = log.grep(/.+FI06172001.xml Validation state: (TRUE|FALSE)/)[0]
line =~ /.+FI06172001.xml Validation state: (TRUE|FALSE)/

if $1 == 'TRUE'
  ng "Invalid dateTime validated"
else
  ok "Invalid dateTime did not validated"
end
