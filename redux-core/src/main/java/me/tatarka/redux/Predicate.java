package me.tatarka.redux;

public interface Predicate<A, RA extends A> {

    boolean test(A action);
}
