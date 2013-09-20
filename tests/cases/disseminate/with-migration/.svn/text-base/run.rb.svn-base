#!/usr/bin/ruby -w
# A simple test on re-ingest.  JPEG to JP2 migration is expected.

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

propFile = File.join test_dir, 'my.properties'
load_properties(propFile)

`mv my.properties local.properties`

if run_reingest(true) == :reingest
  ok "ReIngested."
else
  ng "Rejected."
end

`mv local.properties my.properties`

sql = "select * from EVENT " + 
      "where OID IN (select DFID from DATA_FILE where FILE_TITLE like '2004TDG_75.jpg')"

if query_strings(sql).grep(/M/).any?
  ok 'jpeg is migrated'
else
  ng 'jpeg is not migrated'
end

sql = "select * from RELATIONSHIP " + 
      "where DFID_1 IN (select DFID from DATA_FILE where FILE_TITLE like '2004TDG_75.jpg')"

if query_strings(sql).grep(/MIGRATED_TO/).any?
  ok 'migration relationship is created for migrated JPEG'
else
  ng 'no migration relationship for migrated JPEG'
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

