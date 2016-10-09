package me.tatarka.redux.sample;

import me.tatarka.redux.ObservableStore;
import me.tatarka.redux.Reducer;
import me.tatarka.redux.Reducers;

public class CounterSample {

    // reducers

    static final Reducer<Increment, Integer> increment = (action, state) -> state + 1;
    static final Reducer<Add, Integer> add = (action, state) -> state + action.value;

    static final Reducer<Object, Integer> counter = Reducers.<Object, Integer>matchClass()
            .when(Increment.class, increment)
            .when(Add.class, add);

    // actions

    static class Increment {
        @Override
        public String toString() {
            return "Increment";
        }
    }

    static class Add {
        final int value;

        Add(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Add(" + value + ")";
        }
    }

    public static void main(String[] args) {
        ObservableStore<Object, Integer> store = ObservableStore.create(0, counter, new LogMiddleware<>());
        store.observable().subscribe(count -> System.out.println("state: " + count));
        store.dispatch(new Increment());
        store.dispatch(new Add(2));
    }
}
