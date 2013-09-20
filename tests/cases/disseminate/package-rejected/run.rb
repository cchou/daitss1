#!/usr/bin/ruby -w

# Test the rejection of an originally ingested package.

require 'testutils'

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]
report.to_s =~ / PACKAGE="(\w+?)"/
packageName = $1

if run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate
  ok "Dissemination was a success"
else
  ng "Dissemination failed"
end

dataDir = TestConfig.instance["reingest"]["in"]

# make re-ingest fail by altering the checksum in the database
`echo ' ' >> #{dataDir}#{packageName}/#{ieid}/guo_x.pdf`
if ($? != 0)
  ng "checksum not altered, test harness failed"
else
  ok "checksum altered successfully"
end

# originalFiles = `find /var/daitss/ingest/rin`
# originalList = originalFiles.split(' ')
# originalList.each {|e| e.sub!("/var/daitss/ingest/rin/#{packageName}/", "") }
# puts originalList
 

originalList = Dir.glob("#{dataDir}#{packageName}/**/*").each { |e|
  e.sub!("#{dataDir}#{packageName}/", "")  }

if run_reingest(true) == :reingest
  ng "Package is re-ingested, it suppose to be rejected.  Test harness not working correctly."
else
  ok "Package is not re-ingested as expected"
end

# Make sure the original ingested package is delivered.
dissDir = TestConfig.instance["disseminate"]["out"]
`unzip #{dissDir}FDA/#{packageName}.zip`
newList = Dir.glob("./#{packageName}/**/*").each {|e| 
  e.sub!("./#{packageName}/", "")  }

if originalList.eql?(newList)
  ok "the original package is sent"
else
  ng "dissemination does not send out the original package"
end

#clean up
`rm -r #{packageName}`

