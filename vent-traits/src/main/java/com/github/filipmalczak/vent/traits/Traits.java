package com.github.filipmalczak.vent.traits;

import com.github.filipmalczak.vent.traits.utils.TypeHierarchy;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.stream.Stream;

public class Traits {
    private Traits() {} //todo lombokize static-method-only classes

    private static boolean isMarkedAsTrait(Class traitCandidate){
        try {
            if (traitCandidate.isInterface()) {
                return traitCandidate.getAnnotation(Trait.class) != null;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    /**
     * Type is a valid trait if it is an interface marked as a trait that has no methods.
     */
    public static boolean isValidTrait(@NonNull Class traitCandidate){
        return isMarkedAsTrait(traitCandidate) && traitCandidate.getDeclaredMethods().length == 0;
    }

    public static boolean hasTrait(@NonNull Class implementation, @NonNull Class trait){
        if (!isValidTrait(trait))
            throw new IllegalArgumentException("Provided type is not a valid trait!");
        return trait.isAssignableFrom(implementation);
    }

    public static boolean hasTrait(@NonNull Object instance, @NonNull Class trait){
        return hasTrait(instance.getClass(), trait);
    }

    public static Stream<Class<?>> getTraits(@NonNull Class implementation){
        return TypeHierarchy.getHierarchy(implementation).stream().
            filter(td -> isValidTrait(td.getType())).
            map(TypeHierarchy.TypeDistance::getType);
    }

    @SneakyThrows
    private static <T> T safeNew(Class<T> type){
        return type.newInstance();
    }
}
