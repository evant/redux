package me.tatarka.redux;

/**
 * A reducer takes in an action and the current state and returns a new state as a result. The
 * incoming state must be treated as immutable.
 *
 * @param <S> the state type.
 * @param <A> the action type.
 */
public interface Reducer<S, A> {
    S reduce(S state, A action);
}
