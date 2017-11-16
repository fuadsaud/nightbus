#!/usr/bin/env ruby

module Nightbus
  class HTTP
    LOG_FILENAME = '/Users/fuad/Code/fuadsaud/nightbus/benchmark/coap-http/t3'.freeze

    def call(env)
      puts env

      IO.write(LOG_FILENAME, "#{DateTime.now.strftime('%Q')}\n", mode: 'a')

      [200, {}, ['ok']]
    end
  end
end
