package me.tatarka.redux;

public interface Thunk<A, R> {
    void run(Dispatcher<A, R> dispatcher);
}
