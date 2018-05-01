package com.github.filipmalczak.vent.velvet

import spock.lang.Specification
import spock.lang.Unroll

class VelvetTest extends Specification {
    @Unroll
    def "#path for #target yield exists()=#expected"(){
        expect:
        Velvet.parse(path).bind(target).exists() == expected

        where:
        path       | target        || expected
        "a"        | [a: 1]        || true
        "a.b"      | [a: [b: 2]]   || true
        "a[0]"     | [a: [1, 2]]   || true
        "a[0].b"   | [a: [[b: 1]]] || true
        "a"        | null          || false
        "a"        | [:]           || false
        "a.b"      | [a: 1]        || false
        "b"        | [a: [b: 2]]   || false
        "a[3]"     | [a: [1, 2]]   || false
        "a[0].b"   | [a: [1, 2]]   || false
        "a[0].b"   | [a: [[c: 1]]] || false
    }

    @Unroll
    def "getting #path from #target should yield #expected"(){
        expect:
        Velvet.parse(path).bind(target).get() == expected

        where:
        path       | target        || expected
        "a"        | [a: 1]        || 1
        "a.b"      | [a: [b: 2]]   || 2
        "a[0]"     | [a: [1, 2]]   || 1
        "a[0].b"   | [a: [[b: 1]]] || 1
    }

    @Unroll
    def "getting #path from #target should throw"(){
        when:
        Velvet.parse(path).bind(target).get()

        then:
        thrown UnresolvablePathException

        where:
        path       | target
        "a"        | null
        "a"        | [:]
        "a.b"      | [a: 1]
        "b"        | [a: [b: 2]]
        "a[3]"     | [a: [1, 2]]
        "a[0].b"   | [a: [1, 2]]
        "a[0].b"   | [a: [[c: 1]]]
    }
}
