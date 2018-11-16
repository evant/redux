package me.tatarka.redux;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to help you with composing your reducers.
 */
public class Reducers {

    private static final Reducer ID = new Reducer() {
        @Override
        public Object reduce(Object s, Object a) {
            return s;
        }
    };

    /**
     * A reducers that does nothing, i.e. returns the state as-is.
     */
    @SuppressWarnings("unchecked")
    public static <S, A> Reducer<S, A> id() {
        return ID;
    }

    /**
     * Runs all given reducers in sequence, giving the resulting state of one to the next.
     */
    @SafeVarargs
    public static <S, A> Reducer<S, A> all(final Reducer<S, A>... reducers) {
        if (reducers == null) {
            throw new NullPointerException("reducers == null");
        }
        return new Reducer<S, A>() {
            @Override
            public S reduce(S s, A a) {
                S newState = s;
                for (Reducer<S, A> reducer : reducers) {
                    newState = reducer.reduce(newState, a);
                }
                return newState;
            }
        };
    }

    /**
     * Runs all reducers in sequence until the state is changed.
     */
    @SafeVarargs
    public static <S, A> Reducer<S, A> first(final Reducer<S, A>... reducers) {
        if (reducers == null) {
            throw new NullPointerException("reducers == null");
        }
        return new Reducer<S, A>() {
            @Override
            public S reduce(S s, A a) {
                for (Reducer<S, A> reducer : reducers) {
                    S newState = reducer.reduce(s, a);
                    if (!newState.equals(s)) {
                        return newState;
                    }
                }
                return s;
            }
        };
    }

    public static <S, A> MatchReducer<S, A> match() {
        return new MatchReducer<>();
    }

    public static <S, A> MatchClassReducer<S, A> matchClass() {
        return new MatchClassReducer<>();
    }

    public static <S, A> MatchValueReducer<S, A> matchValue() {
        return new MatchValueReducer<>();
    }

    static abstract class BaseMatchReducer<S, A, M> implements Reducer<S, A> {
        private List<M> matchers = new ArrayList<>();
        private List<Reducer> reducers = new ArrayList<>();

        BaseMatchReducer() {
        }

        protected void put(M matcher, Reducer reducer) {
            if (matcher == null) {
                throw new NullPointerException("matcher == null");
            }
            if (reducer == null) {
                throw new NullPointerException("reducer == null");
            }
            matchers.add(matcher);
            reducers.add(reducer);
        }

        @Override
        @SuppressWarnings("unchecked")
        public S reduce(S state, A action) {
            for(int i = 0; i < matchers.size(); i++) {
                if(match(matchers.get(i), action)) {
                    return (S) reducers.get(i).reduce(state, action);
                }
            }
            return state;
        }

        protected abstract boolean match(M matcher, A action);
    }


    public static class MatchReducer<S, A> extends BaseMatchReducer<S, A, Predicate> {

        MatchReducer() {
        }

        public <RA extends A> MatchReducer<S, A> when(Predicate<A, RA> predicate, Reducer<S, ? super RA> reducer) {
            put(predicate, reducer);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected boolean match(Predicate predicate, A action) {
            return predicate.test(action);
        }
    }

    public static class MatchClassReducer<S, A> extends BaseMatchReducer<S, A, Class> {

        public <RA extends A> MatchClassReducer<S, A> when(Class<RA> actionClass, Reducer<S, ? super RA> reducer) {
            put(actionClass, reducer);
            return this;
        }

        @Override
        protected boolean match(Class actionClass, A action) {
            return actionClass.isInstance(action);
        }
    }

    public static class MatchValueReducer<S, A> extends BaseMatchReducer<S, A, Object> {

        public <RA extends A> MatchValueReducer<S, A> when(RA value, Reducer<S, ? super RA> reducer) {
            put(value, reducer);
            return this;
        }

        @Override
        protected boolean match(Object value, A action) {
            return value.equals(action);
        }
    }
}
