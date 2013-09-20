#!/usr/bin/ruby -w

require 'testutils'

"package ingested".expect run_ingest == :ingest

active_silo_ids = (query_arrays 'select ID from SILO where ACTIVE').sort
silos_with_copies = (query_arrays 'select distinct SILO from COPY').sort

"only active silos should have copies".expect active_silo_ids == silos_with_copies
