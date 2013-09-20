#!/usr/bin/ruby -w

require 'testutils'
require 'fileutils'

# need to delete everything in dissemation out dir, otherwise this test won't work properly
if in_disseminate_out? "*"
  path_to_clear = pp('disseminate/out')
  puts "Warning, #{path_to_clear} is not empty. This can cause the last two checks of this test to fail." 
end

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]
 
old_md5 = query_arrays("select distinct MD5 from COPY where IEID = '#{ieid}'")[0]

# get copy urls for ieid
copies = query_arrays("select concat(SILO.URL, '/',COPY.PATH) from COPY, SILO where COPY.SILO = SILO.ID and COPY.IEID ='#{ieid}' ").map { |r| r[0] }
"copies are in the database".expect copies.any?

# do a head on each copy to test if its good, and get its last-modified
times = {}
copies.each do |copy|
  resp = http_head(copy)
  status = resp.code.to_i
  "HEAD #{copy} => #{status.to_s}".expect(status == 200)
  times[copy] = Time.parse resp['Last-Modified']
end

'dissemination was a success'.expect(run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate)
'event recorded in database'.expect query_strings("select OUTCOME from EVENT where OID='#{ieid}' and EVENT_TYPE='D'").any?
'package is in the rin dir'.expect in_reingest_in?(File.join('*', ieid))
're-ingested'.expect(run_reingest(true) == :reingest)

# make sure copies existing now are the same as the copies from before
new_copies = query_arrays("select concat(SILO.URL, '/',COPY.PATH) from COPY, SILO where COPY.SILO = SILO.ID and COPY.IEID ='#{ieid}' order by COPY.ID").map { |r| r[0] }
"copies after the re-ingest are the same".expect copies == new_copies

# make sure the copies are newer than the old ones
copies.each do |copy|
  resp = http_head(copy)
  status = resp.code.to_i
  "HEAD #{copy} => #{status.to_s}".expect(status == 200)
  
  t1 = times[copy]
  t2 = Time.parse resp['Last-Modified']
  "difference in Last-Modified times: #{t2 - t1} seconds".expect(t1 < t2)  
end

# make sure the copy md5 is different
new_md5 = query_arrays("select distinct MD5 from COPY where IEID = '#{ieid}'")[0]
"MD5 of copies are different".expect(old_md5 != new_md5)

"DIP is in the disseminate out dir".expect in_disseminate_out?(File.join('*', '*.zip'))

'digest file is in the disseminate out dir'.expect in_disseminate_out?(File.join('*', '*.DIGESTS'))
'digest md5 is good'.expect dip_digest['md5'] == dip_md5
'digest sha1 is good'.expect dip_digest['sha1'] == dip_sha1
