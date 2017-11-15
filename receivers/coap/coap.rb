#!/usr/bin/env ruby
#
module Nightbus
  class CoAP
    def call(env)
      puts env

      [200, {}, ['ok']]
    end
  end
end
