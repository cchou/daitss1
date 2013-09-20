require 'erb'
require 'fileutils'
require 'find'
require 'open3'
require 'timeout'
require 'mailread'
require 'tempfile'
require 'tmpdir'

require 'config'
require 'queries'

# Provides methods that invoke DAITSS functions
module Daitss

  # Thirty minutes, max allowed time for process to run
  MAX_SECONDS = 60 * 30 * 100

  def disable_storage_instance(id)
    sql = "update STORAGE_INSTANCE set ENABLED = 'FALSE' where ID = #{id}"
    query_no_result sql
  end

  def corrupt_file(f)
    open(f, "a") do |fd|
      fd.puts "corruption!"
    end
  end

  # Write a temp file with garbage in it to tsm
  def corrupt_tsm_archive(c)

    management_class = if c[:instance] =~ /\w+@\w+:(\w+)/
                         $1
                       else
                         raise "Cannot extract a management class"
                       end

    description = if c[:identifier] =~ /.*\?description=(\w+)$/
                    $1
                  else
                    raise "Cannot extract a description"
                  end

    tf = Tempfile.new 'corrupt_tsm'
    corrupt_file tf.path

    path = tf.path
    new_identifier = "%s?description=%s" %  [brace_filespace(path), description]

    `sudo dsmc archive -archmc=#{management_class} #{path} -description=#{description} -optfile=#{test_tsm_optfile}`

    # update the storage description
    sql = "update STORAGE_DESC set identifier = '#{new_identifier}' where ID = #{c[:id]}"
    query_no_result sql
  end

  def brace_filespace(path)
    file_space = `df #{path}`.split[-1]
    "{#{file_space}}#{ path[file_space.length .. -1] }"
  end

  def get_copies(dfid)
    sql=<<"END_SQL"
select sd.ID, si.METHOD, si.INSTANCE, sd.IDENTIFIER
from STORAGE_DESC sd, STORAGE_INSTANCE si
where DFID='#{dfid}' and sd.STORAGE_INSTANCE = si.ID
END_SQL
    query_arrays(sql).map { |row| { :id => row[0], :method => row[1], :instance => row[2], :identifier => row[3] } }
  end

  def corrupt_copies(copies)
    copies.each do |c|
      case c[:method]
      when 'TIVOLI'
        corrupt_tsm_archive c
      when 'FILE'
        corrupt_file File.join(c[:instance], c[:identifier])
      else
        raise "Cannot detect storage method: " + c[:method]
      end
    end
  end

  def corrupt_all_copies(dfid)
    corrupt_copies get_copies(dfid)
  end

  def corrupt_some_copies(dfid)
    corrupt_copies get_copies(dfid)[0..-2]
  end

  def run_fixity_check(*dfids)
    begin
      # run the fixity check
      output = nil
      begin
        Timeout.timeout(MAX_SECONDS) do
          output = `fixitycheck #{ dfids.join ' ' } 2>&1`
        end
      rescue Timeout::Error
        raise "Fixity Check exceeded the max runtime of #{MAX_SECONDS} seconds."
      end

      # save the output to the log file
      FileUtils::rm_rf log_file
      open(log_file, "w") do |file|
        output.each do |line|
          file.puts line
        end
      end

      case $?.exitstatus
      when 0
        :success
      when 1
        :error
      when 2
        raise "Fixity Check crashed: " + output
      else
        raise "Fixity Check reported unexpected return code: #{$?.exitstatus}:\n" + output
      end

    rescue => message
      report :crash, "Caught in fixity check  processing: '" + (message || '') + "'."
    end
  end

  def setup_withdraw(options={})

    begin

      # clean up
      delete_mailed_reports

      # run withdrawal
      output = nil
      error  = nil
      flags = []
      flags << "-t #{ options[:type] }" if options[:type]
      flags << "-c #{ options[:contact_id] }" if options[:contact_id]

      if [options[:ieid], options[:file]].compact.size != 1
        raise ":ieid exclusively or :file must be specified"
      end

      flags << "-i #{ options[:ieid] }" if options[:ieid]
      flags << "-f #{ options[:file] }" if options[:file]

      begin
        Timeout.timeout(MAX_SECONDS) do
          output = `withdraw #{ flags.join ' ' } 2>&1`
        end
      rescue Timeout::Error
        raise "Withdrawal exceeded the max runtime of #{MAX_SECONDS} seconds."
      end

      # save the output to the log file
      FileUtils::rm_rf log_file
      open(log_file, "w") do |file|
        output.each do |line|
          file.puts line
        end
      end

      case $?.exitstatus
      when 0
        cache_last_mailed_report
      when 1
        raise "Withdrawal failed: " + output
      when 2
        raise "Withdrawal crashed: " + output
      else
        raise "Withdrawal reported unexpected return code: #{$?.exitstatus}:\n" + output
      end

    rescue => message
      report :crash, "Caught in withrawal processing: '" + (message || '') + "'."
    end

  end

  # Ingest packages into the archive for setting up a test. This
  # method will crash if ingest does not complete successfully.
  def setup_ingest(crash_on_reject=true)

    # clean out DAITSS
    check_workflow 'ingest'
    clean_workflow 'ingest'
    clean_workflow 'reingest'
    clean_global_files
    delete_mailed_reports

    # load the DAITSS state
    load_properties
    copy_setup_packages 'ingest'
    
    # prepare the database
    load_migrations
    load_global_data
    load_local_data if File.file? local_sql
        
    if ENV['SETUP'] == 'true'
      puts 'done with setup'
      exit
    end

    # run the process
    output = nil
    Timeout::timeout(MAX_SECONDS) do
      output = `ingest start 2>&1`
    end

    if $?.exitstatus != 0
      report :crash, "ingest crashed:  " + output
    end

    if crash_on_reject && Queries::in_ingest_reject?('*')
      report :crash, "ingest rejected: " + output
    end

    # save the last mailed report to the test
    cache_last_mailed_report

    # get the report
    report = mailed_report.read

    # return the pair of output and the report
    [output, report]

  end

  # Runs the fixity checker of DAITSS  and returns a symbol :pass or :fail
  def run_fixity(dfid)

    begin
      Timeout.timeout(MAX_SECONDS) do
        output = `fixity #{dfid} 2>&1`
      end
    rescue Timeout::Error
      raise "Fixity exceeded the max runtime of #{MAX_SECONDS} seconds."
    end

    # save the output to the log file
    FileUtils::rm_rf log_file
    open(log_file, "w") do |file|
      output.each do |line|
        file.puts line
      end
    end

    case $?.exitstatus
    when 0
      # great, pat on the back
    when 1
      raise "Withdrawal failed: " + output
    when 2
      raise "Withdrawal crashed: " + output
    else
      raise "Withdrawal reported unexpected return code: #{$?.exitstatus}:\n" + output
    end
  end

  # Runs the ingest function of DAITSS and returns a symbol :ingest or
  # :reject; exceptions will result in a :crash, which is
  # automatically reported.
  def run_ingest
    gest 'ingest'
  end

  # Runs the reingest function of DAITSS and returns a symbol
  # :reingest or :reject; exceptions will result in a :crash, which is
  # automatically reported.
  #
  # if clean state is false the environment will not be reset, 
  # i.e. when you just ingested a package and want to disseminate it
  def run_reingest(clean_state=false)
    gest 'reingest', clean_state
  end

  # Either 'ingest' or 'reingest'
  def gest(re_or_in, clean_state=false)
    begin

      unless clean_state
        # clean out directories
        check_workflow re_or_in
        clean_workflow 'ingest'
        clean_workflow 'reingest'
        clean_global_files
        
        # clean out the mail spool
        delete_mailed_reports

        # load the properties
        load_properties
        
        # copy pacakages into the workflow
        copy_packages re_or_in

        # prepare the database
        load_migrations
        load_global_data
        load_local_data if File.file? local_sql
      end
      
      if ENV['SETUP'] == 'true'
        puts 'done with setup'
        exit
      end
      
      output = nil
      error  = nil

      begin
        
        Timeout.timeout(MAX_SECONDS) do

          Open3::popen3('ingest start') do |stdin, stdout, stderr|
            output = stdout.read
            error  = stderr.read
          end
          
        end
        
      rescue Timeout::Error
        report(:crash, re_or_in.capitalize  +  " exceeded the max runtime of #{MAX_SECONDS} seconds.")
      end

      # save the output to the log file
      FileUtils::rm_rf log_file
      open(log_file, "w") do |file|
        output.each do |line|
          file.puts line
        end
      end

      # save the error output to the error log file
      FileUtils::rm_rf error_log_file
      if error.any?
        open(error_log_file, "w") do |file|
          error.each do |line|
            file.puts line
          end
        end
      end

      # save the report
      cache_last_mailed_report

      # if we had error output, we must have aborted..
      report(:crash, re_or_in.capitalize + " did not exit gracefully, see #{error_log_file}.") if error.any?

      # if something is left in the work directory, we must have aborted (w/o error)
      if Dir.entries( pp("#{re_or_in}/work") ).sort != ['.', '..']
        report :crash, "Stuff left in the #{re_or_in} work dir."
      end

    rescue => message
      puts message.backtrace if $exit_on_crash
      report :crash, "Caught in #{re_or_in} processing: '" + (message || 'no message') + "'."
    end

    if Dir.entries( pp("#{re_or_in}/reject") ).sort == ['.', '..']
      re_or_in.to_sym
    else
      :reject
    end
  end

  # Runs the prep function of DAITSS and returns either :reject or :prep symbols; exceptions
  # will result in a :crash,  which is automatically reported.
  #
  # recognized options:
  # * create_descriptors => true|false
  # * account
  # * project
  def run_prep(options={})

    begin
      # prepare the workflow for testing
      check_workflow 'prep'
      clean_workflow 'prep'

      # load the state
      load_properties
      copy_packages 'prep'

      # prepare the database
      load_migrations
      load_global_data
      load_local_data if File.file? local_sql

      # run prep
      output = nil
      error  = nil

      # command line flags
      flags = []
      flags << "-d" if options[:create_descriptor]
      flags <<  "-a #{options[:account]} -p #{options[:project]}" if (options[:account] and options[:project])
      flags << "-r" if options[:prune]

      begin
        Timeout.timeout(MAX_SECONDS) do
          Open3::popen3("prep start #{flags.join ' ' }") do |stdin, stdout, stderr|
            output = stdout.read
            error  = stderr.read
          end
        end
      rescue Timeout::Error
        report(:crash, "Prep exceeded the max runtime of #{MAX_SECONDS} seconds.")
      end

      # save the output to the log file
      FileUtils::rm_rf log_file
      open(log_file, "w") do |file|
        output.each do |line|
          file.puts line
        end
      end

      # save the error output to the error log file
      FileUtils::rm_rf error_log_file
      if error.any?
        open(error_log_file, "w") do |file|
          error.each do |line|
            file.puts line
          end
        end
      end

      # if we had error output, we must have aborted..
      report(:crash, "Prep did not exit gracefully, see #{error_log_file}.") if error.grep(/Premature exit of preprocessor/).any?

      # if something is left in the work directory, we must have aborted (w/o error)
      if Dir.entries(pp('prep/work')).sort != ['.', '..']
        report :crash, "Stuff left in the prep work dir."
      end

    rescue => message
      puts message.backtrace
      report(:crash, "Caught in prep processing: '" + (message || '') + "'.")
    end

    if Dir.entries(pp('prep/reject')).sort == ['.', '..']
      :prep
    else
      :reject
    end
  end

  # Runs withdraw
  def run_withdraw(options={})

    begin

      # clean up
      delete_mailed_reports

      # run withdrawal
      flags = []
      flags << "-t #{ options[:type] }" if options[:type]
      flags << "-c #{ options[:contact_id] }" if options[:contact_id]

      if [options[:ieid], options[:file]].compact.size != 1
        report :crash, ":ieid exclusively or :file must be specified"
      end

      flags << "-i #{ options[:ieid] }" if options[:ieid]
      flags << "-f #{ options[:file] }" if options[:file]

      output = nil
      begin
        Timeout.timeout(MAX_SECONDS) do
          output = `withdraw #{ flags.join ' ' } 2>&1`
        end
      rescue Timeout::Error
        raise "Withdrawal exceeded the max runtime of #{MAX_SECONDS} seconds."
      end

      # save the output to the log file
      FileUtils::rm_rf log_file
      open(log_file, "w") do |file|
        output.each do |line|
          file.puts line
        end
      end

      case $?.exitstatus
      when 0
        cache_last_mailed_report
        :withdraw
      when 1
        :fail
      when 2
        report :crash, "Withdrawal crashed: " + output
      else
        report :crash, "Withdrawal reported unexpected return code: #{$?}:\n" + output
      end

    rescue => message
      report :crash, "Caught in withrawal processing: '" + (message || '') + "'."
    end

  end

  # Runs dissemination
  def run_disseminate(options={})

    begin

      # run withdrawal
      output = nil
      error  = nil
      flags = []
      flags << "-c #{ options[:contact_id] }" if options[:contact_id]

      if [options[:ieid], options[:file]].compact.size != 1
        report :crash, ":ieid exclusively or :file must be specified"
      end

      flags << "-i #{ options[:ieid] }" if options[:ieid]
      flags << "-f #{ options[:file] }" if options[:file]

      flags << "-s #{ options[:storage_instance] }" if options[:storage_instance]

      begin
        Timeout.timeout(MAX_SECONDS) do
          output = `disseminate #{ flags.join ' ' } 2>&1`
        end
      rescue Timeout::Error
        raise "Dissemination exceeded the max runtime of #{MAX_SECONDS} seconds."
      end

      # save the output to the log file
      FileUtils::rm_rf log_file
      open(log_file, "w") do |file|
        output.each do |line|
          file.puts line
        end
      end

      case $?.exitstatus
      when 0
        :disseminate
      when 1
        :fail
      when 2
        report :crash, "Dissemination crashed: " + output
      else
        report :crash, "Dissemination reported unexpected return code: #{$?}:\n" + output
      end

    rescue => message
      report :crash, "Caught in dissemination processing: '" + (message || '') + "'."
    end

  end

  ############## helpers ####################


  # Assert the given workflows have the correct permissions
  def check_workflow(*workflows)
    workflows.each do |workflow|
      pp(workflow).values.each do |d|
        raise "#{d} does not exist" unless File.exist? d
        raise "#{d} is not a directory" unless File.directory? d
        raise "#{d} is not writable" unless File.stat(d).writable_real?
      end
    end
  end

  # Clean out the workflow dirs
  def clean_workflow(*workflows)
    begin
      workflows.each do |workflow|
        pp(workflow).values.each do |d|
          FileUtils::rm_rf Dir.glob(File.join(d, '*'))
        end
      end
    rescue
      raise "Problem cleaning out workflow directory", caller
    end
  end

  # Clean out global files
  def clean_global_files
    begin
      all_global_files = Dir.glob File.join( pp('globals-dir') , '*')
      FileUtils::rm_rf all_global_files
    rescue => message
      raise "Problem cleaning out global files directory: #{message}", caller
    end
  end

  # Initialize the database. If init.sql exists than it will be loaded also.
  def initialize_database

    test_sql = File.join test_dir, 'init.sql'

    if File.exist? test_sql
      test_init_sql = File.join setup_dir, 'initialize-database.sql'

      command  = "mysql -u#{ pp 'db/username' } -h#{ pp 'db/host' } "
      command += "-p#{ pp 'db/password' } " if pp('db/password')
      command += "#{ pp 'db/database'}"

      command = "cat #{test_init_sql} #{test_sql} | #{command} 2>&1"
	
      output = `#{command}`
      raise "Could not initialize database: '#{command}' produced error '#{output}'" unless output.empty?
    end

  end

  def mysql_user_info
    "-u#{ pp 'db/username' } -h#{ pp 'db/host' }" + ( pp('db/password') ?  " -p#{ pp 'db/password' } " : " " ) + "#{ pp 'db/database'}"
  end

  # load all migrations in $DAITSS_HOME/database/migrations
  def load_migrations
    # get all the files in order from the migrations dir
    migrations_pattern = File.join ENV['DAITSS_HOME'], 'database', 'migrations', '*.sql'
    migrations = Dir.glob(migrations_pattern).select { |sql_file| File.basename(sql_file) =~ /\d+_\w+/ }.sort
    
    # load each migration into the database
    migrations.each do |migration|      
      `mysql #{ mysql_user_info } < #{migration}`
      raise "Problem loading migration #{ File.basename migration }" if $? != 0
    end
    
  end
  
  # load the global.sql into the database
  def load_global_data
    `mysql #{ mysql_user_info } < #{ global_sql }`
    raise "Problem loading global data" if $? != 0
  end
  
  # load the local.sql into the database
  def load_local_data
    `mysql #{ mysql_user_info } < #{ local_sql }`
    raise "Problem loading local data" if $? != 0
  end

  # Returns the path to the DAITSS properties file
  def properties_file
    File.join pp('daitss-home'), 'config', 'daitss.properties'
  end

  # Copies the private key and known hosts files to specific places
  def copy_ssh_info
    if File.exist?(test_ssh_identity) and File.exist?(test_ssh_known_hosts)
      FileUtils::cp test_ssh_identity, pp('ssh/identity')
      FileUtils::cp test_ssh_known_hosts, pp('ssh/known-hosts')
    end
  end

  # Write daitss.properties file into the daitss env, updating the global properties with the specified local ones
  def load_properties(localfile=local_properties())
    # erb = ERB.new open(global_properties).read
    # props = erb.result(config_binding)

    raw = read_properties global_properties
    props = {}
    raw.each do |name, value|
      props[name] = ERB.new(value).result(config_binding)
    end

    if File.exist? localfile
      lprops = read_properties localfile
      props.update lprops
    end
    
    open(properties_file, 'w') do |file|
      props.each do |name, value|
        file.puts "#{name}=#{value}"
      end
    end
    
  end

  def read_properties(file)
    h = {}
    
    open(file) do |io|
      
      io.read.split("\n").each do |line|
        
        if line =~ /[^#]*=.*/
          name, value = line.split(/\s*\=\s*/, 2)
          h[name] = value
        end
        
      end
      
    end
    
    h
  end

  # Copies packages to the ingest/in dir if there are any
  def copy_packages(subsystem)

    report :crash, "Unknown subsystem #{subsystem}" unless ['prep', 'ingest', 'reingest'].member? subsystem

    packages.each do |package|
      src_path = File.join test_dir, 'packages', package
      begin
        FileUtils.cp_r src_path, pp("#{subsystem}/in")
        files_to_delete = []
        Find.find File.join( pp("#{subsystem}/in"), package) do |path|
          if path =~ /\.svn$/
            files_to_delete << path
            Find.prune
          end
        end
        FileUtils::rm_rf files_to_delete
      rescue => message
        report :crash, "Problem copying package directories: '" + message + "'"
      end
    end
  end

  # Copies setup packages to the ingest/in dir if there are any
  def copy_setup_packages(subsystem)

    raise "Unknown subsystem #{subsystem}" unless ['prep', 'ingest', 'reingest'].member? subsystem

    setup_packages.each do |package|
      # copy the package
      src_path = File.join test_setup_package_dir, package
      dest_path =  File.join pp("#{subsystem}/in"), package
      FileUtils::cp_r src_path, dest_path

      # remove all the .svn files if there are any
      files_to_delete = []
      Find.find File.join( pp("#{subsystem}/in"), package) do |path|
        if path =~ /\.svn$/
          files_to_delete << path
          Find.prune
        end
      end
      FileUtils::rm_rf files_to_delete
    end

  end

  # clean out the webcache dir
  def clean_webcache
    pattern = File.join pp('webcache-dir'), '*'
    FileUtils::rm_rf Dir.glob(pattern)
  end

  # Returns the files in the webcache
  def webcache_files
    pattern = File.join pp('webcache-dir'), '**/*'
    Dir.glob(pattern).select { |f| File.file? f }
  end

  # clean out the tempdir dir
  def clean_tempdir
    pattern = File.join pp('temp-dir'), '*'
    FileUtils::rm_rf Dir.glob(pattern)
  end

  # Returns the files in the tempdir
  def temp_files
    pattern = File.join pp('temp-dir'), '**/*'
    Dir.glob(pattern).select { |f| File.file? f }
  end

  def erase_rin_dir(&block)
    FileUtils.rm_rf pp('reingest/in')
    yield block
    FileUtils.mkdir pp('reingest/in')
  end

  def start_mserver
    location = `mktemp -d`
    port = 4990
    silo = "silo0" 
    `ruby -I #{pp('mserver-dir')}/lib #{pp('mserver-dir')}/mserver/mserver --port #{port} --silo #{silo}:#{location}`
  end

end
