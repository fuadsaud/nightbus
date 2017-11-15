#!/usr/bin/env ruby

module Nightbus
  class HTTP
    def call(env)
      puts env

      [200, {}, ['ok']]
    end
  end
end
