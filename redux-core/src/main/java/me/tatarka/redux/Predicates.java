package me.tatarka.redux;

public class Predicates {

    private static final Predicate ALWAYS = new Predicate() {
        @Override
        public boolean test(Object action) {
            return true;
        }
    };

    private static final Predicate NEVER = new Predicate() {
        @Override
        public boolean test(Object action) {
            return false;
        }
    };

    @SuppressWarnings("unchecked")
    public static <A> Predicate<A, A> always() {
        return ALWAYS;
    }

    @SuppressWarnings("unchecked")
    public static <A> Predicate<A, A> never() {
        return NEVER;
    }

    public static <A, RA extends A> Predicate<A, RA> is(final RA value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                return value.equals(action);
            }
        };
    }

    public static <A, RA extends A> Predicate<A, RA> instanceOf(final Class<RA> actionClass) {
        if (actionClass == null) {
            throw new NullPointerException("actionClass == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A o) {
                return actionClass.isInstance(o);
            }
        };
    }

    public static <A, RA extends A> Predicate<A, RA> negate(final Predicate<A, RA> predicate) {
        if (predicate == null) {
            throw new NullPointerException("predicate == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                return !predicate.test(action);
            }
        };
    }

    @SafeVarargs
    public static <A, RA extends A> Predicate<A, RA> any(final Predicate<A, RA>... predicates) {
        if (predicates == null) {
            throw new NullPointerException("predicates == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                for (Predicate<A, RA> predicate : predicates) {
                    if (predicate.test(action)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @SafeVarargs
    public static <A, RA extends A> Predicate<A, RA> any(final RA... values) {
        if (values == null) {
            throw new NullPointerException("values == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                for (RA value : values) {
                    if (value.equals(action)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @SafeVarargs
    public static <A, RA extends A> Predicate<A, RA> any(final Class<RA>... actionClasses) {
        if (actionClasses == null) {
            throw new NullPointerException("actionClasses == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                for (Class<RA> actionClass : actionClasses) {
                    if (actionClass.isInstance(action)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @SafeVarargs
    public static <A, RA extends A> Predicate<A, RA> all(final Predicate<A, RA>... predicates) {
        if (predicates == null) {
            throw new NullPointerException("predicates == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                for (Predicate<A, RA> predicate : predicates) {
                    if (!predicate.test(action)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @SafeVarargs
    public static <A, RA extends A> Predicate<A, RA> all(final RA... values) {
        if (values == null) {
            throw new NullPointerException("values == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                for (RA value : values) {
                    if (!value.equals(action)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @SafeVarargs
    public static <A, RA extends A> Predicate<A, RA> all(final Class<RA>... actionClasses) {
        if (actionClasses == null) {
            throw new NullPointerException("actionClasses == null");
        }
        return new Predicate<A, RA>() {
            @Override
            public boolean test(A action) {
                for (Class<RA> actionClass : actionClasses) {
                    if (!actionClass.isInstance(action)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
