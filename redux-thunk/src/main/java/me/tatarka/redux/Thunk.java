package me.tatarka.redux;

public interface Thunk<S> {
    void run(Store<S> store);
}
