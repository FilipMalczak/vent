package com.github.filipmalczak.vent.helper;

import java.util.List;

import static java.util.Arrays.copyOfRange;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class PathUtils {
    public static List<String> superPaths(String path){
        String[] parts = path.split("[.]");
        return range(1, parts.length).
            mapToObj(i -> String.join(".", copyOfRange(parts, 0, i))).
            collect(toList());
    }
}
