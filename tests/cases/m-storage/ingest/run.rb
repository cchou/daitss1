#!/usr/bin/ruby -w

require 'testutils'

# ingest
if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

# check to see if the silos contain stuff from the report
if mailed_report.grep(/<INGEST /).any?
  ok "Report Sent"
else
  ng "No Report"
end

# did the tarballs end up in storage?
if mailed_report.read =~ /<INGEST IEID="(\w+)"/ && $1
  ok "ieid found: #{ $1 }"
else
  ng "no ieid found"
end
ieid = $1

# get copy urls for ieid
copies = query_arrays "select concat(SILO.URL, '/',COPY.PATH) from COPY, SILO where COPY.SILO = SILO.ID and COPY.IEID ='#{ieid}' "
if copies.any?
  ok "Copies are in the database"
else
  ng "No copies in the database"
end

copies.each do |row|
  copy = row[0]
  status = http_head(copy).code.to_i
  
  if status == 200
    ok "Copy #{ copy } is good"
  else
    ng "Copy #{ copy } is bad (#{ status })"
  end
  
end