#!/usr/bin/ruby -w
# A simple test on re-ingest.  No migration is expected.

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

# retrieve the DFID of the AIP descriptor 

sql = "select DFID from DATA_FILE where FILE_TITLE like '%AIP%'"

oldAIPDesc = query_strings(sql)

if run_reingest(true) == :reingest
  ok "ReIngested."
else
  ng "Rejected."
end

# make sure a second ingest event is recorded
sql = "select * from EVENT " + 
      "where OID IN (select IEID from INT_ENTITY where PACKAGE_NAME like 'FDA0000400')"

if query_strings(sql).grep(/I/).any?
  ok "database exhibits a second ingest (for reingest) event"
else
  ng "No dissemination event is recorded in database"
end

# make sure the old AIP descriptor is now marked as OBSOLETE
sql = "select IS_OBSOLETE from DATA_FILE where DFID = '#{oldAIPDesc}'"

if query_strings(sql).grep(/TRUE/).any?
  ok "the OLD AIP descriptor is now obsolete"
else
  ng "the old AIP descriptor is not marked as obsolete"
end

# make sure an email is sent
if mailed_report.grep(/<DISSEMINATION/).any?
  ok "Mail correctly reported 'Email Delivered'."
else 
  ng "Mail failed to report 'Email Delivered'."
end

if log.grep(/Email Delivery/).any?
   ok "The log indicate an email has been sent"
else
   ng "The log does not indicate an email delivery"
end

# Make sure the integrity of the delivered package
dissDir = TestConfig.instance["disseminate"]["out"]
`unzip #{dissDir}FDA/#{packageName}.zip`
numAIP = 0
result = Dir.glob("./#{packageName}/**/*").each { |e|
  if e.include?('AIP_')
    numAIP += 1
  end
}

#make sure there is only one AIP descriptor
if numAIP == 1
  ok "only one AIP descriptor exist in the disseminated zip"
else
  ng "either there is no AIP descriptor or there is more than one AIP descriptors, should be one and only one."
end

#clean up
`rm -r #{packageName}`
