require 'singleton'
require 'yaml'

# All the properties from the file DAITSS_TEST_CONFIG is set to.
class TestConfig < Hash

  include Singleton

  # Loads test environment configurations in from files in the 
  # colon delimited list in the environment variable DAITSS_TEST_CONFIG.
  # Precidence descends left to right. If a file in the path does not
  # exist it will be silently skipped. An exception will be raised if
  # any problems are encountered parsing files.
  def initialize
    ENV['DAITSS_TEST_CONFIG'].split(':').reverse.each do |cfg_file|
      begin
        merge! YAML.load_file(cfg_file) if File.exist? cfg_file
      rescue
        raise "Cannot load configurations from file: " + cfg_file
      end
    end
  end

end

# Resolves the value of a property path
def pp(path, delimiter='/')

  resolve_hash_path = lambda do |aHash, *path|
    raise "Cannot find segment: #{path.first}" unless aHash.key? path.first
    raise 'No value for path' if path.empty?

    case aHash[path.first]
    when Hash
      if path == [path.first]
        aHash[path.first]
      else
        resolve_hash_path.call aHash[path.first], *path[1..-1]
      end
    else
      aHash[path.first]
    end
    
  end

  begin
    resolve_hash_path.call TestConfig.instance, *path.split(delimiter)
  rescue => message
    raise "Cannot resolve path #{path}: #{message}"
  end

end

# returns the binding from this lexical scope
def config_binding
  binding
end
