#!/usr/bin/ruby -w
#Test case for packag containing csv files.

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

#make sure the csv text bitstream exists
sql = "select * from BS_TEXT_CSV " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'fixedColumn.csv')"

if query_strings(sql).any?
   ok "database exhibits the csv bitstream for the fixed column csv file"
else
   ng "No csv text bitstream is recorded in database"
end

sql = "select * from BS_TEXT_CSV " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'quoted.csv')"

if query_strings(sql).any?
   ok "database exhibits the csv bitstream for the quoted csv file"
else
   ng "No csv text bitstream is recorded in database"
end

sql = "select * from BS_TEXT_CSV " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'variablecolumn.csv')"

if query_strings(sql).any?
   ok "database exhibits the csv bitstream for the variable column csv file"
else
   ng "No csv text bitstream is recorded in database"
end

#check for anomaly
sql = "select * from DATA_FILE_SEVERE_ELEMENT " + 
      "where DFID IN (select DFID from DATA_FILE where FILE_TITLE = 'variablecolumn.csv')"

if query_strings(sql).grep(/CSV_VARIABLE_COLUMNS/).any?
   ok "database exhibits the expected variable column anomaly"
else
   ng "no anomaly for variable columns!"
end
