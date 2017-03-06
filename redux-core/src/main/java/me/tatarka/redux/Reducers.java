package me.tatarka.redux;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to help you with composing your reducers.
 */
public class Reducers {

    private static final Reducer ID = new Reducer() {
        @Override
        public Object reduce(Object a, Object s) {
            return s;
        }
    };

    /**
     * A reducers that does nothing, i.e. returns the state as-is.
     */
    @SuppressWarnings("unchecked")
    public static <A, S> Reducer<A, S> id() {
        return ID;
    }

    /**
     * Runs all given reducers in sequence, giving the resulting state of one to the next.
     */
    @SafeVarargs
    public static <A, S> Reducer<A, S> all(final Reducer<A, S>... reducers) {
        if (reducers == null) {
            throw new NullPointerException("reducers == null");
        }
        return new Reducer<A, S>() {
            @Override
            public S reduce(A a, S s) {
                S newState = s;
                for (Reducer<A, S> reducer : reducers) {
                    newState = reducer.reduce(a, newState);
                }
                return newState;
            }
        };
    }

    /**
     * Runs all reducers in sequence until the state is changed.
     */
    @SafeVarargs
    public static <A, S> Reducer<A, S> first(final Reducer<A, S>... reducers) {
        if (reducers == null) {
            throw new NullPointerException("reducers == null");
        }
        return new Reducer<A, S>() {
            @Override
            public S reduce(A a, S s) {
                for (Reducer<A, S> reducer : reducers) {
                    S newState = reducer.reduce(a, s);
                    if (!newState.equals(s)) {
                        return newState;
                    }
                }
                return s;
            }
        };
    }

    public static <A, S> MatchReducer<A, S> match() {
        return new MatchReducer<>();
    }

    public static <A, S> MatchClassReducer<A, S> matchClass() {
        return new MatchClassReducer<>();
    }

    public static <A, S> MatchValueReducer<A, S> matchValue() {
        return new MatchValueReducer<>();
    }

    static abstract class BaseMatchReducer<A, S, M> implements Reducer<A, S> {
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
        public S reduce(A action, S state) {
            for(int i = 0; i < matchers.size(); i++) {
                if(match(matchers.get(i), action)) {
                    return (S) reducers.get(i).reduce(action, state);
                }
            }
            return state;
        }

        protected abstract boolean match(M matcher, A action);
    }


    public static class MatchReducer<A, S> extends BaseMatchReducer<A, S, Predicate> {

        MatchReducer() {
        }

        public <RA extends A> MatchReducer<A, S> when(Predicate<A, RA> predicate, Reducer<? super RA, S> reducer) {
            put(predicate, reducer);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected boolean match(Predicate predicate, A action) {
            return predicate.test(action);
        }
    }

    public static class MatchClassReducer<A, S> extends BaseMatchReducer<A, S, Class> {

        public <RA extends A> MatchClassReducer<A, S> when(Class<RA> actionClass, Reducer<? super RA, S> reducer) {
            put(actionClass, reducer);
            return this;
        }

        @Override
        protected boolean match(Class actionClass, A action) {
            return actionClass.isInstance(action);
        }
    }

    public static class MatchValueReducer<A, S> extends BaseMatchReducer<A, S, Object> {

        public <RA extends A> MatchValueReducer<A, S> when(RA value, Reducer<? super RA, S> reducer) {
            put(value, reducer);
            return this;
        }

        @Override
        protected boolean match(Object value, A action) {
            return value.equals(action);
        }
    }
}
