require 'rubygems'
require 'bundler'

Bundler.require

require_relative 'http'

run Nightbus::HTTP.new
