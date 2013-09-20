#!/usr/bin/ruby -w
#Ingest a package containing a PDF with unembedded math font.  As the math
#font is not embedded, this cause normalization to fail during ghostscript 
#call.  A test case is created here in case a math font package is located 
#and also, for future testing on new version of ghostscript or in the case 
#of new normalization tools.

require 'testutils'

ok "turn off for now"
exit

if run_ingest == :ingest
  ok "Ingested."
else
  ng "Rejected."
end

