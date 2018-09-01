package com.github.filipmalczak.vent.mongo.utils;

import com.github.filipmalczak.vent.velvet.UnboundPath;
import com.github.filipmalczak.vent.velvet.Velvet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathUtils {
    //todo: test fixed bug: a[0].b will not yield superpath a, just a[0]!
    public static List<String> superPaths(String path){
        UnboundPath velvetPath = Velvet.parse(path);
        List superPaths = velvetPath.getSuperPaths();
        superPaths.remove(superPaths.size()-1);
        return superPaths;
    }
}
