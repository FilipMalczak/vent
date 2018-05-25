package com.github.filipmalczak.vent.traits;

import com.github.filipmalczak.vent.traits.utils.TypeHierarchy;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeHierarchyTest {
    @Test
    public void objectAsSuperClassNoInterfaces(){
        assertEquals(
            asList(
                TypeHierarchy.TypeDistance.of(TypeHierarchyTest.class, 0),
                TypeHierarchy.TypeDistance.of(Object.class, 1)
            ),
            TypeHierarchy.getHierarchy(TypeHierarchyTest.class)
        );
    }

    interface I1 {}

    interface I2 extends I1 {}

    interface I3 extends I1, I2 {}

    static class C1 implements I1 {}

    static class C11 extends C1 {}

    static class C12 extends C1 implements I2 {}

    static class C13 extends C1 implements I3 {}

    static class C2 implements I2 {}

    static class C21 extends C2 {}

    static class C22 extends C21 implements I3 {}

    //todo more test cases

    private void assertEqualsIgnoreOrderingOfTheSameDistance(List<TypeHierarchy.TypeDistance> expected,
                                                             List<TypeHierarchy.TypeDistance> actual){
        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
        //fixme potential off-by-one
        for (int i=0; i<actual.size()-1; ++i){
            assertTrue(actual.get(i).getDistance() <= actual.get(i+1).getDistance());
        }
    }

    @Test
    public void onlyOneInterface(){
        assertEqualsIgnoreOrderingOfTheSameDistance(
            asList(
                TypeHierarchy.TypeDistance.of(C1.class, 0),
                TypeHierarchy.TypeDistance.of(Object.class, 1),
                TypeHierarchy.TypeDistance.of(I1.class, 1)
            ),
            TypeHierarchy.getHierarchy(C1.class)
        );
    }

    @Test
    public void oneInterfaceWithTwoSuperInterfacesAndOneSuperClass(){
        assertEqualsIgnoreOrderingOfTheSameDistance(
            asList(
                TypeHierarchy.TypeDistance.of(C13.class, 0),
                TypeHierarchy.TypeDistance.of(C1.class, 1),
                TypeHierarchy.TypeDistance.of(I3.class, 1),
                TypeHierarchy.TypeDistance.of(I1.class, 2),
                TypeHierarchy.TypeDistance.of(I2.class, 2),
                TypeHierarchy.TypeDistance.of(Object.class, 2)
            ),
            TypeHierarchy.getHierarchy(C13.class)
        );
    }

    @Test
    public void oneSuperclass(){
        assertEqualsIgnoreOrderingOfTheSameDistance(
            asList(
                TypeHierarchy.TypeDistance.of(C21.class, 0),
                TypeHierarchy.TypeDistance.of(C2.class, 1),
                TypeHierarchy.TypeDistance.of(I2.class, 2),
                TypeHierarchy.TypeDistance.of(Object.class, 2),
                TypeHierarchy.TypeDistance.of(I1.class, 3)
                ),
            TypeHierarchy.getHierarchy(C21.class)
        );
    }

    @Test
    public void interfaceWithSuperinterfacesAndSuperClassWithSuperClass(){
        assertEqualsIgnoreOrderingOfTheSameDistance(
            asList(
                TypeHierarchy.TypeDistance.of(C22.class, 0),
                TypeHierarchy.TypeDistance.of(C21.class, 1),
                TypeHierarchy.TypeDistance.of(I3.class, 1),
                TypeHierarchy.TypeDistance.of(C2.class, 2),
                TypeHierarchy.TypeDistance.of(I2.class, 2),
                TypeHierarchy.TypeDistance.of(I1.class, 2),
                TypeHierarchy.TypeDistance.of(Object.class, 3)
            ),
            TypeHierarchy.getHierarchy(C22.class)
        );
    }

    @Test
    public void onlySuperInterfaces(){
        assertEqualsIgnoreOrderingOfTheSameDistance(
            asList(
                TypeHierarchy.TypeDistance.of(I3.class, 0),
                TypeHierarchy.TypeDistance.of(I1.class, 1),
                TypeHierarchy.TypeDistance.of(I2.class, 1)
            ),
            TypeHierarchy.getHierarchy(I3.class)
        );
    }

    @Test
    public void object(){
        assertEqualsIgnoreOrderingOfTheSameDistance(
            asList(
                TypeHierarchy.TypeDistance.of(Object.class, 0)
            ),
            TypeHierarchy.getHierarchy(Object.class)
        );
    }
}