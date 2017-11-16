#!/usr/bin/env ruby

require 'date'

module Nightbus
  class CoAP
    LOG_FILENAME = '/Users/fuad/Code/fuadsaud/nightbus/benchmark/http-coap/t3'.freeze

    def call(env)
      puts env

      IO.write(LOG_FILENAME, "#{DateTime.now.strftime('%Q')}\n", mode: 'a')

      [200, {}, ['ok']]
    end
  end
end
