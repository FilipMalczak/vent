package com.github.filipmalczak.vent.helper;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.helper.Struct.set;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

@AllArgsConstructor
@Builder
public class StackTracer {
    private Class<?> basePackageClass;
    @Builder.Default
    private Set<String> defaultPackagesToFilterOut = set("java.util", "java.lang", "sun.reflect", "org.junit", "com.intellij", "reactor");
    @Builder.Default
    private Set<String> customPackagesToFilterOut = set();
    @Builder.Default
    private int defaultShorteningLevel = 3;
    @Builder.Default
    private boolean withMethod = true;
    @Builder.Default
    private boolean withLineNumber = true;
    @Builder.Default
    private boolean alwaysIncludeFirstFrame = true;

    @Getter(lazy = true) private final Set<String> packagesToFilterOut = ((Supplier<Set<String>>)(() -> {
        Set<String> out = set();
        out.addAll(defaultPackagesToFilterOut);
        out.addAll(customPackagesToFilterOut);
        return out;
    })).get();


    public List<Frame> getCurrentFrames(){
        try {
            throw new AnException();
        } catch (AnException e){
            return Stream.of(e.getStackTrace()).
                filter(ste -> !ste.getClassName().equals(this.getClass().getCanonicalName())).
                map(Frame::forStackFrame).
                collect(Collectors.toList());
        }
    }

    public List<Frame> getInterestingFrames(){
        List<Frame> currentFrames = getCurrentFrames();
        return (
            alwaysIncludeFirstFrame ?
                Stream.concat(
                    safeSublist(currentFrames, 0, -1).stream().
                        filter(f -> getPackagesToFilterOut().stream().noneMatch(p -> f.getPackageName().startsWith(p))),
                    Stream.of(currentFrames.get(currentFrames.size()-1))
                ) :
                currentFrames.stream().
                    filter(f -> getPackagesToFilterOut().stream().noneMatch(p -> f.getPackageName().startsWith(p)))
        ).collect(Collectors.toList());
    }

    private String formatFrame(Frame frame){
        StringBuilder result = new StringBuilder();
        result.append(frame.getShortenedPackage(basePackageClass, defaultShorteningLevel)).append(".").append(frame.getSimpleClassName());
        if (withMethod)
            result.append("#").append(frame.getMethodName());
        if (withLineNumber)
            result.append(":").append(frame.getLineNumber());
        return result.toString();
    }


    public String getCurrentHierarchy(int skipNFrames){
        return getInterestingFrames().stream().skip(skipNFrames).map(this::formatFrame).collect(joining(" -called-by-> "));
    }

    public String getCurrentHierarchy(){
        return getCurrentHierarchy(1); // skip 1 frame - from method introducing default arg
    }

    private static class AnException extends RuntimeException {}

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Frame {
        private final String fullyQualifiedClassName;
        private final String methodName;

        private final String fileName;
        private final int lineNumber;

        @Getter(lazy = true, value = AccessLevel.PRIVATE)
        private final String[] nameParts = fullyQualifiedClassName.split("[.]");

        public static Frame forStackFrame(StackTraceElement element){
            return new Frame(element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
        }

        public String getSimpleClassName(){
            return getNameParts()[getNameParts().length-1];
        }

        private List<String> getPackageParts(){
            return asList(getNameParts()).subList(0, getNameParts().length-1);
        }

        public String getPackageName(){
            return getPackageParts().stream().collect(joining("."));
        }

        /**
         *
         * @param partsToShorten negative value for "all", 0 for "none"
         */
        public String getShortenedPackage(int partsToShorten){
            if (partsToShorten < 0)
                partsToShorten = getPackageParts().size();
            return Stream.concat(
                safeSublist(getPackageParts(), 0, partsToShorten).stream().
                    map(p -> p.substring(0, 1)),
                safeSublist(getPackageParts(), partsToShorten).stream()
            ).collect(joining("."));
        }

        /**
         * All classes lying on the same level as argument or higher will have package shortened; deeper in the tree
         * will not be shortened (e.g. relativeTo = abc.def.ghi.Klass, this = abc.def.ghi.jkl.Klass2 -> a.d.g.jkl,
         * but if this = abc.def.ghi.Klass2 then result = a.d.g).
         *
         * Classes from different package subtree will be shortened according to second argument.
         */
        public String getShortenedPackage(Class<?> relativeTo, int rest){
            if (fullyQualifiedClassName.startsWith(relativeTo.getPackage().getName()))
                return getShortenedPackage(relativeTo.getPackage().getName().split("[.]").length);
            return getShortenedPackage(rest);
        }
    }

    /**
     * Will still throw if from &lt; 0, but if to &gt; list.size() then it will just return "to the end".
     * If from &gt; list.size(), will return empty list. If to &lt; 0, then it will be treated as
     * list.size() + to (python style).
     */
    private static <T> List<T> safeSublist(List<T> list, int from, int to){
        if (from >= list.size())
            return Collections.emptyList();
        if (to > list.size())
            to = list.size();
        if (to < 0)
            to = list.size() + to;
        return list.subList(from, to);
    }

    /**
     * Same as overload, but with default to = list.size().
     */
    private static <T> List<T> safeSublist(List<T> list, int from){
        return safeSublist(list, from, list.size());
    }
}
