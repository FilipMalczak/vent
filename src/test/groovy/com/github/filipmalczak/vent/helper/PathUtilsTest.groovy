package com.github.filipmalczak.vent.helper

import spock.lang.Specification
import spock.lang.Unroll

import static com.github.filipmalczak.vent.helper.PathUtils.superPaths

class PathUtilsTest extends Specification {
    //this may not be the best spec, as we shouldn't care that much for order of superpaths, but screw it
    @Unroll
    def "path #path should have following superpaths: #superpaths"() {
        expect:
        superPaths(path) == superpaths

        where:
        path          || superpaths
        ""            || []
        "x"           || []
        "x.y"         || ["x"]
        "x.y.z"       || ["x", "x.y"]
        "x.y.z.a.b.c" || ["x", "x.y", "x.y.z", "x.y.z.a", "x.y.z.a.b"]
    }
}
