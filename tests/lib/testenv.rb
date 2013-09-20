require 'config'

# Methods that are used to make test cases
module Testing

  # Returns the path to the base directory of the current test
  def test_dir
    abs_path = File.expand_path $0
    File.dirname abs_path
  end

  # Loads the file named local.sql into the database
  def local_sql
    File.join test_dir, 'local.sql'
  end
  
  # Returns the path to the integration tests base directory
  def base_dir
    lib = File.dirname __FILE__
    File.dirname lib
  end

  # Returns the path to the boilerplate files directory
  def setup_dir
    File.join base_dir, 'share'
  end

  # Loads the system wide sql data
  def global_sql
    File.join setup_dir, 'global.sql'
  end

  # Returns the name of the current test
  def test_name
    File.basename test_dir
  end

  # Returns a path to the test package directory
  def test_package_dir
    File.join test_dir, 'packages'
  end

  # Returns the paths to the packages for this test
  def packages
    if File.directory? test_package_dir
      Dir.entries(test_package_dir).reject { |p| [ '..', '.', '.svn' ].include? p }
    else
      raise "#{test_package_dir} is not a valid dir or does not exist"
    end
  end

  # Returns a path to the test setup-package directory
  def test_setup_package_dir
    File.join test_dir, 'setup-packages'
  end

  # Returns the paths to the packages for this test
  def setup_packages
    Dir.entries(test_setup_package_dir).reject { |p| [ '..', '.', '.svn' ].include? p }
  end

  # properties specific to the test
  def local_properties
    File.join test_dir, 'local.properties'
  end
  
  # global properties applicable to all tests
  def global_properties
    File.join setup_dir, 'global.properties'  
  end
  
  def test_mailed_report_file
    File.join test_dir, 'run.report'
  end

  # No Good. Call this when a test fails
  def ng(message)
    report :ng, message
  end

  # Oh Kaay. Call this when a test passes
  def ok(message)
    report :ok, message #if $verbose
  end

  def crash(message)
    report :crash, message
  end

  def pending(message)
    report :pending, message
  end
  
  # global variable defining behavior when crashing
  # when true, exit, when false, throw an exception
  $exit_on_crash = true

  # Prints a message to standard output.
  # Will exit program if status is equal to :crash
  def report(status, message = '')
    raise CrashError, error_log.read, caller if status == :crash && !$exit_on_crash
    puts "#{status.id2name.upcase}\t#{test_name}\t#{message}"

    case status
    when :crash
      caller.each { |line| puts "\t#{test_name}\t" + line }
      exit 1
    when :pending
      exit 0
    end

  end

  class CrashError < Exception; end

end


class String
  
  def expect(fact)
    
    if fact
      ok to_s
    else
      ng to_s
    end
    
  end
  
end
