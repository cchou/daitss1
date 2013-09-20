require 'gserver'
require 'mailread'
require 'socket'

class SMTPServer < GServer

  attr_reader :last_from

  def initialize(port)
    @messages = []
    @mutex = Mutex.new
    super(port)
  end

  def messages
    @mutex.synchronize { @messages.clone }
  end
  
  def flush_messages
    @mutex.synchronize { @messages = [] }    
  end
    
  def serve(io)
    
    buffer = StringIO.new
    reading_data = false
    
    io.puts "220 #{Socket::gethostname}"
    
    io.each do |line|
      
      line.chomp!

      case line
      when /^(HELO)/
        io.puts "250 Hello there"

      when /^QUIT/
        io.puts "bye" unless io.eof?
        break

      when /^MAIL FROM\:/
        line =~ /^MAIL FROM\:.*<(.+@.+)>/
        @last_from = $1
        io.puts "250 OK"

      when /^RCPT TO\:/
        io.puts "250 OK"

      when /^DATA/
        io.puts "354 Enter message, ending with \".\" on a line by itself"
        reading_data = true
        
      when /^.$/
        io.puts "250 OK"
        reading_data = false
        
      else
        if reading_data
          buffer.puts line
        else
          io.puts "500 ERROR"
        end
        
      end
      
    end  
    
    io.puts "221 bye" unless io.eof?
    io.close
    buffer.rewind
    @mutex.synchronize { @messages << Mail.new(buffer) }    
    
  end

end
