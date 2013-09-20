#!/usr/bin/ruby -w

# skeleton ingest test

require 'testutils'

require 'digest/md5'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

# check the log
data = log.read
if data =~ /image.tiff\s+Message: Identical DataFile already processed in package/
  ng "one of the files was cut according to the log (stupid but valid)"
else
  ok "log doesn't specify if a file is cut"
end

log.rewind
if data =~ %r{\tMessage: Putting tarball at (http://127.0.0.1:5000/silo-1/\w+)}

  url = $1
  files = `curl -s #{url} | tar tf -`.split

  ieid = url.split('/')[-1]

  original_tiffs = files.grep( /image.tiff/ ).size
  if original_tiffs == 2
    ok "two files in tarball"
    
    # make sure they are different
    image_data = Digest::MD5.hexdigest `curl -s #{url} | tar Oxf - #{ieid}/image.tiff`
    folder_image_data = Digest::MD5.hexdigest `curl -s #{url} | tar Oxf - #{ieid}/folder/image.tiff`
    
    if image_data == folder_image_data
      ng 'data is the same in storage'
    else
      ok 'data is different in storage'
    end

  else
    ng "#{original_tiffs} tiffs exist when there should be 2"
  end

else
  error 'tarball not located in log'
end
