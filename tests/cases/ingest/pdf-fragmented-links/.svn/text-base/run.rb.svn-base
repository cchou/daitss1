#!/usr/bin/ruby -w
#Test PDFs containing fragmented links

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#check if the fragmented link is recorded as broken
sql = "select BROKEN_LINKS from DISTRIBUTED where PARENT IN " +
           "(select DFID from DATA_FILE where FILE_TITLE = 'Krawietz_Sabine_Anna_200705_MA.pdf')"

if query_strings(sql).grep(/manuscript/).any?
  ok "Database correctly shows fragmented out-of-package local links as broken"
else
  ng "The fragmented link is not recorded as broken"
end

#make sure http and email links are ignored
sql = "select IGNORED_LINKS from DISTRIBUTED where PARENT IN " +
           "(select DFID from DATA_FILE where FILE_TITLE = 'Krawietz_Sabine_Anna_200705_MA.pdf')"

if query_strings(sql).grep(/http/).any?
  ok "Database correctly shows http links as ignored"
else
  ng "The http link is not recorded as ignored"
end

if query_strings(sql).grep(/mailto/).any?
  ok "Database correctly shows mailto links as ignored"
else
  ng "The mailto link is not recorded as ignored"
end

# fail if the global file links now become broken
if mailed_report.grep(%r'XMLSchema_LOC.xsd</BROKEN_LINK>').any?
  ng "Mail report indicates the global files are now broken."
else
  ok "global files are not broken."
end
