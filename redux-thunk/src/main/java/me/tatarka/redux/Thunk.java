package me.tatarka.redux;

public interface Thunk<A, S> {
    void run(Store<A, S> store);
}
