#!/usr/bin/env ruby

module Nightbus
  class HTTP
    LOG_FILENAME = '/Users/fuad/Code/fuadsaud/nightbus/benchmark/coap-http/throughput'.freeze

    def call(env)
      puts env

      IO.write(log_filename(env['SERVER_PORT']), "#{DateTime.now.strftime('%Q')}\n", mode: 'a')

      [200, {}, ['ok']]
    end

    private

    def log_filename(suffix)
      "#{LOG_FILENAME}-#{suffix}"
    end
  end
end
