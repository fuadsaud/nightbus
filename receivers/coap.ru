#!/usr/bin/env ruby

require 'bundler/inline'

gemfile do
  source 'https://rubygems.org'

  gem 'david'
  gem 'cbor'
  gem 'rack'
end

require 'rack'

module Nightbus
  class Coap
    def call(env)
      puts env

      [200, {}, ['ok']]
    end
  end
end

run Nightbus::Coap.new
