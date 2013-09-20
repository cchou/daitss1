#!/usr/bin/ruby -w

# skeleton ingest test

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

# check the log
data = log.read
if data =~ /(image|picture).tiff\s+Message: Identical DataFile already processed in package/
  ok "one of the files was cut according to the log (stupid but valid)"
else
  ng "log doesn't specify if a file is cut"
end

log.rewind
if data =~ %r{\tMessage: Putting tarball at (http://127.0.0.1:5000/silo-1/\w+)}
  files = `curl -s #{$1} | tar tf -`.split

  original_tiffs = files.grep( %r{(folder/picture|image).tiff} ).size
  if original_tiffs  == 1
    ok "only one file in tarball"
  else
    ng "#{original_tiffs} tiffs exist when there should be 1"
  end

else
  error 'tarball not located in log'
end
