#!/usr/bin/ruby -w

require 'testutils'

pending 'this needs looking at'
# make sure the webcache is empty
clean_webcache

# check if the webcache is not empty
if webcache_files.size > 0
  ng "web cache not empty, populated with #{ webcache_files.size } files"
else
  ok "web cache empty"
end

# ingest
if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

# check if the webcache is not empty
if webcache_files.size > 0
  ok "web cache populated with #{ webcache_files.size } files"
else
  ng "web cache was not populated"
end
