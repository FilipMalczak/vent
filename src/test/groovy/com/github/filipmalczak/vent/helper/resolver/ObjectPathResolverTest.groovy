package com.github.filipmalczak.vent.helper.resolver

import spock.lang.Specification
import spock.lang.Unroll

class ObjectPathResolverTest extends Specification {
    ObjectPathResolver resolver = new ObjectPathResolver()

    public static Map target = [
        a: [
            [
                b: 1,
                c: "a1"
            ],
            [
                b: 2,
                c: "a2",
                d: true
            ],
            [
                b: 3,
                c: "a3",
                d: false
            ]
        ],
        x: 1,
        y: [
            z: [2, 3, 5, 7, 11],
            w: true
        ]
    ]

    @Unroll
    def "Should resolve path #path to #expectedResult"(){
        expect:
        resolver.resolve(target, path).get() == expectedResult

        where:
        path        || expectedResult
        "x"         || 1
        "y.z"       || [2, 3, 5, 7, 11]
        "a[0]"      || [b: 1, c: "a1"]
        "a[0].b"    || 1
        "a[2].d"    || false
    }

    @Unroll
    def "Should resolve path #path as non-existent"(){
        expect:
        !resolver.resolve(target, path).exists()

        where:
        path << [
            "bcd",
            "a[10]",
            "a[0].d"
        ]
    }
}
