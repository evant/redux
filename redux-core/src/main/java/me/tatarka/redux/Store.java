package me.tatarka.redux;

/**
 * Stores your immutable state and allows you to dispatch actions to change it.
 */
public interface Store<A, S> {

    S state();
    
    void setState(S state);
    
    void dispatch(A action);
}
