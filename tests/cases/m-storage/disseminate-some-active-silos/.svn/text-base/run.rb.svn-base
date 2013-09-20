#!/usr/bin/ruby -w

require 'testutils'

# need to delete everything in dissemation out dir, otherwise this test won't work properly
if in_disseminate_out? "*"
  path_to_clear = pp('disseminate/out')
  puts "Warning, #{path_to_clear} is not empty. This can cause the last two checks of this test to fail."
end

output, report = setup_ingest

ieid = report.match(/<INGEST IEID="(\w+?)"/)[1]
 
old_md5 = query_arrays("select distinct MD5 from COPY where IEID = '#{ieid}'")[0]

# get copy urls for ieid
old_copies = query_arrays("select concat(SILO.URL, '/',COPY.PATH) from COPY, SILO where COPY.SILO = SILO.ID and COPY.IEID ='#{ieid}' ").map { |r| r[0] }
'should only be 2 copies'.expect old_copies.length == 2
'should only be silos 1 & 2'.expect old_copies.all? { |copy| copy =~ /silo-[12]/ }

# do a head on each copy to test if its good, and get its last-modified
old_times = {}
old_copies.each do |copy|
  resp = http_head(copy)
  status = resp.code.to_i
  "HEAD #{copy} => #{status.to_s}".expect(status == 200)
  old_times[copy] = Time.parse resp['Last-Modified'] if copy =~ /silo-2/
end

query_no_result "update SILO set ACTIVE = FALSE where URL like '%silo-1'"
query_no_result "update SILO set ACTIVE = TRUE where URL like '%silo-3'"

'dissemination was a success'.expect(run_disseminate(:ieid => ieid, :contact_id => 1) == :disseminate)
'event recorded in database'.expect query_strings("select OUTCOME from EVENT where OID='#{ieid}' and EVENT_TYPE='D'").any?
'package is in the rin dir'.expect in_reingest_in?(File.join('*', ieid))
're-ingested'.expect(run_reingest(true) == :reingest)

# make sure copies existing now are the same as the copies from before
new_copies = query_arrays("select concat(SILO.URL, '/',COPY.PATH) from COPY, SILO where COPY.SILO = SILO.ID and COPY.IEID ='#{ieid}' order by COPY.ID").map { |r| r[0] }
"copies after the re-ingest are not same".expect old_copies != new_copies
'should only be 2 copies'.expect new_copies.length == 2
'should only be silos 2 & 3'.expect new_copies.all? { |copy| copy =~ /silo-[23]/ }

# make sure that the old copy got deleted
copy_on_silo1 = old_copies.find { |c| c =~ /silo-1/ }
resp = http_head(copy_on_silo1)
status = resp.code.to_i
"HEAD #{copy_on_silo1} => #{status.to_s}".expect status == 404

# make sure the copies are newer than the old ones
new_copies.each do |copy|
  resp = http_head(copy)
  status = resp.code.to_i
  "HEAD #{copy} => #{status.to_s}".expect(status == 200)
  
  if copy =~ /silo-2/
    t1 = old_times[copy]
    t2 = Time.parse resp['Last-Modified']
    "difference in Last-Modified times: #{t2 - t1} seconds".expect(t1 < t2)  
  end
end

# make sure the copy md5 is different
new_md5 = query_arrays("select distinct MD5 from COPY where IEID = '#{ieid}'")[0]
"MD5 of copies are different".expect(old_md5 != new_md5)

"DIP is in the disseminate out dir".expect in_disseminate_out?(File.join('*', '*.zip'))

'digest file is in the disseminate out dir'.expect in_disseminate_out?(File.join('*', '*.DIGESTS'))
'digest md5 is good'.expect dip_digest['md5'] == dip_md5
'digest sha1 is good'.expect dip_digest['sha1'] == dip_sha1
