require 'rubygems'
require 'bundler'

Bundler.require

require_relative 'coap'

run Nightbus::CoAP.new
