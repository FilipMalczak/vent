package com.github.filipmalczak.vent.traits.utils;

import lombok.Value;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class TypeHierarchy {
    private TypeHierarchy() {}

    @Value(staticConstructor = "of") //fixme not the most meaningful name
    public static class TypeDistance {
        private Class type;
        private int distance;
    }


    /**
     * @param root type for which the hierarchy will be discovered
     * @return complete hierarchy for root, up to Object
     */
    public static List<TypeDistance> getHierarchy(Class root){
        return getHierarchy(root, -1);
    }

    /**
     * @param root type for which the hierarchy will be discovered
     * @param depth 0 means "return only root", 1 - "root and its direct supertypes", n - "hierarchy of depth n-1 and
     *              all their supertypes"; negative value means "return the whole hierarchy up to Object"
     * @return list of types (root type or its supertype) with distances (see depth) sorted by distance, ascending
     */
    public static List<TypeDistance> getHierarchy(Class root, int depth){
//        List<TypeDistance> result = new LinkedList<>();
//        visitHierarchy(root, depth, 0, result);
//        return result;
        Deque<TypeDistance> toVisit = new LinkedList<>();
        toVisit.offer(TypeDistance.of(root, 0));
        List<TypeDistance> visited = new LinkedList<>();
        while (!toVisit.isEmpty() && (depth < 0 || toVisit.peek().getDistance() <= depth)){
            TypeDistance current = toVisit.pop();
            Class currentType = current.getType();
            if (visited.stream().noneMatch(td -> td.getType().equals(currentType))) {
                int nextDistance = current.getDistance() + 1;
                visited.add(current);
                for (Class superInterface : currentType.getInterfaces())
                    toVisit.offer(TypeDistance.of(superInterface, nextDistance));
                Class superclass = currentType.getSuperclass();
                if (superclass != null)
                    toVisit.offer(TypeDistance.of(superclass, nextDistance));
            }
        }
        return visited;
    }
}
