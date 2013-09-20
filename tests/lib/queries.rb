require 'dbi'
require 'timeout'

require 'config'
require 'net/http'
require 'digest'


# Methods for querying a daitss environment for testing
module Queries

  # Returns the path of the current test's log file
  def log_file
    File.join test_dir, 'run.log'
  end

  # Returns the path of the current test's error log file
  def error_log_file
    File.join test_dir, 'run.err'
  end

  # Streams the log
  def log
    open log_file
  end

  # Streams the error log
  def error_log
    open error_log_file
  end

  def query_no_result(sql)
    begin
      dbh = DBI.connect "dbi:Mysql:#{ pp 'db/database' }:#{ pp 'db/host' }", pp('db/username'), pp('db/password')
      sth = dbh.execute(sql)
    rescue DBI::DatabaseError => e
      raise "Cannot query the MySQL database: '#{e.errstr}', error: #{e.err}."
    end
  end    

  # Returns an array of rows. Each row as a Hash of column to value mappings
  def query_hashes(sql)
    begin
      dbh = DBI.connect "dbi:Mysql:#{ pp 'db/database' }:#{ pp 'db/host' }", pp('db/username'), pp('db/password')
      sth = dbh.execute(sql)
      result_set = []
      sth.fetch_hash  do |row|
        result_set << row
      end
      result_set
    rescue DBI::DatabaseError => e
      raise "Cannot query the MySQL database: '#{e.errstr}', error: #{e.err}."
    end
  end

  # Returns an Array of rows. Each row as an Array of values
  def query_arrays(sql)
    begin
      dbh = DBI.connect "dbi:Mysql:#{ pp 'db/database' }:#{ pp 'db/host' }", pp('db/username'), pp('db/password')
      dbh.select_all(sql)
    rescue DBI::DatabaseError => e
      raise "Cannot query the MySQL database: '#{e.errstr}', error: #{e.err}."
    end
  end

  # Returns an Array of rows. Each row as a String of values
  def query_strings(sql, delimiter="\t")
    begin
      dbh = DBI.connect "dbi:Mysql:#{ pp 'db/database' }:#{ pp 'db/host' }", pp('db/username'), pp('db/password')
      result_set = []
      dbh.select_all(sql).each do |a|
        result_set << a.join(delimiter)
      end
      result_set
    rescue DBI::DatabaseError => e
      raise "Cannot query the MySQL database: '#{e.errstr}', error: #{e.err}."
    end
  end

  # Get the list of mailed reports; Expects to find a directory with
  def mailed_reports
    $smtp_server.messages
  end

  # Deletes all spooled mail reports
  def delete_mailed_reports
    $smtp_server.flush_messages
  end

  # Streams the last email
  def cache_last_mailed_report
    raise "expecting report, cant find it" if $smtp_server.messages.empty?
    
    open(test_mailed_report_file, 'w') do |io|
      io.write $smtp_server.messages.last.body
    end
    
    delete_mailed_reports
  end

  # Returns an IO for reading of the report
  def mailed_report
    open test_mailed_report_file
  end

  # Return a path to an existing file that matches the glob_path within the root_dir.
  # If the no files match the glob_pattern the environment will crash.
  def get_daitss_file(root_dir, glob_path)

    # remove leading slash
    glob_path.sub!(/^\/+/, '')

    # get all files that match the pattern
    files = Dir.glob File.join(root_dir, glob_path)

    # get the lexographic fisrt file or crash
    if files.sort.any?
      open files[0]
    else
      report :crash, "Cannot get daitss file: #{glob_path}"
    end

  end

  # Returns a File object of the glob_path patterns first match under the prep/reject.
  # If the no files match the glob_pattern the environment will crash.
  def get_prep_reject(glob_path)
    get_daitss_file pp('prep/reject'), glob_path
  end

  # Returns a File object of the glob_path patterns first match under the prep/out.
  # If the no files match the glob_pattern the environment will crash.
  def get_prep_out(glob_path)
    get_daitss_file pp('prep/out'), glob_path
  end

  # Returns a File object of the glob_path patterns first match under the ingest/reject.
  # If the no files match the glob_pattern the environment will crash.
  def get_ingest_reject(glob_path)
    get_daitss_file pp('ingest/reject'), glob_path
  end

  # Returns a File object of the glob_path patterns first match under the ingest/out.
  # If the no files match the glob_pattern the environment will crash.
  def get_ingest_out(glob_path)
    get_daitss_file pp('ingest/out'), glob_path
  end

  # Returns true if the glob_path has any file matching in the root_dir
  def in_path?(root_dir, glob_path)
    Dir.glob(File.join(root_dir, glob_path)).any?
  end

  # Returns true if the glob_path matches something in prep/reject
  def in_prep_reject?(glob_path)
    in_path? pp('prep/reject'), glob_path
  end

  # Returns true if the glob_path matches something in prep/out
  def in_prep_out?(glob_path)
    in_path? pp('prep/out'), glob_path
  end

  # Returns true if the glob_path matches something in ingest/reject
  def in_ingest_reject?(glob_path)
    in_path? pp('ingest/reject'), glob_path
  end

  # Returns true if the glob_path matches something in ingest/out
  def in_ingest_out?(glob_path)
    in_path? pp('ingest/out'), glob_path
  end

  # Returns true if the glob_path matches something in reingest/in
  def in_reingest_in?(glob_path)
    in_path? pp('reingest/in'), glob_path
  end

  # Returns true if the glob_path matches something in reingest/in
  def in_disseminate_out?(glob_path)
    in_path? pp('disseminate/out'), glob_path
  end

  def http_head(url)
    if url =~ %r{http://([\w\.]+):(\d+)(.*)}
      Net::HTTP.start($1, $2) { |http| http.head($3 == "" ? '/' : $3) }
    else
      raise "bad url: #{ url.inspect } #{ url.class }"
    end
  end
  
  def dip_zip_file
    pattern = File.join pp('disseminate/out'), '*', '*.zip'
    Dir.glob(pattern).first
  end

  def dip_digests_file
    pattern = File.join pp('disseminate/out'), '*', '*.DIGESTS'
    Dir.glob(pattern).first
  end

  def dip_digest
    h = {}
    
    open dip_digests_file do |io|
      
      io.each do |line|
        line.chomp!
        
        case line
        when /^([\da-f]{40})\s+(.+)/
          h['sha1'] = $1
        when /^([\da-f]{32})\s+(.+)/
          h['md5'] = $1
        when /#/
        when /^$/
        else
          raise 'bad digest file line: #{line}'
        end
        
      end
      
    end
    h
  end

  def dip_md5
    open(dip_zip_file) do |io|
      Digest::MD5.hexdigest io.read
    end
  end
  
  def dip_sha1
    open(dip_zip_file) do |io|
      Digest::SHA1.hexdigest io.read
    end
  end

  def copies(ieid)
    query_arrays("select concat(SILO.URL, '/',COPY.PATH) from COPY, SILO where COPY.SILO = SILO.ID and COPY.IEID ='#{ieid}' ").map { |r| r[0] }
  end

end
