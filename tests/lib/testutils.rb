# Master file for the DAITSS test harness. requiring this file will require everything needed to test.

require 'daitss'
require 'queries'
require 'testenv'
require 'smtpd'

include Testing
include Daitss
include Queries

# start a fake smtp server
SMTP_PORT = 2500
ENV['SMTP_PORT'] = SMTP_PORT.to_s
$smtp_server = SMTPServer.new SMTP_PORT
$smtp_server.start
at_exit { $smtp_server.stop }
