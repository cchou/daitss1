#!/usr/bin/ruby -w

#Ingest a package containing PDFs which use Microsoft font.  As we use ADPL 
#ghostscrpt 1.53, font mapping for those MS fonts needs to be mannual put in.
#A test case is created here for future testing on new version of ghostscript
#or in the case of new normalization tools.

# NOTE: this test requires bigger than default java heap size. the default of 64M crashed the test on my mac. it passed with 128MB

require 'testutils'

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

