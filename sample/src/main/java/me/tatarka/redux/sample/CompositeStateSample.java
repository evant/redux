package me.tatarka.redux.sample;

import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Reducer;
import me.tatarka.redux.Reducers;
import me.tatarka.redux.SimpleStore;
import me.tatarka.redux.rx.ObservableAdapter;

public class CompositeStateSample {

    // state

    static class Person {
        final String name;
        final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person(" + name + ", " + age + ")";
        }
    }

    // reducers

    static <T> Reducer<T, Replace<T>> replace() {
        return (state, action) -> action.newValue();
    }

    static Reducer<Integer, Increment> increment = (state, action) -> state + 1;

    static Reducer<Person, Action> updatePerson = Reducers.<Person, Action>matchClass()
            .when(ChangeName.class, (state, action) -> new Person(CompositeStateSample.<String>replace().reduce(state.name, action), state.age))
            .when(IncrementAge.class, (state, action) -> new Person(state.name, increment.reduce(state.age, action)));

    // actions

    interface Action {}

    interface Replace<T> extends Action {
        T newValue();
    }

    interface Increment extends Action {
    }

    static class ChangeName implements Replace<String> {
        final String newName;

        ChangeName(String newName) {
            this.newName = newName;
        }

        @Override
        public String toString() {
            return "ChangeName(" + newName + ")";
        }

        @Override
        public String newValue() {
            return newName;
        }
    }

    static class IncrementAge implements Increment {
        @Override
        public String toString() {
            return "IncrementAge";
        }

    }

    public static void main(String[] args) {
        SimpleStore<Person> store = new SimpleStore<>(new Person("nobody", 0));
        Dispatcher<Action, Action> dispatcher = Dispatcher.forStore(store, updatePerson)
                .chain(new LogMiddleware<>(store));
        ObservableAdapter.observable(store).subscribe(person -> System.out.println("state: " + person));
        dispatcher.dispatch(new ChangeName("Bob"));
        dispatcher.dispatch(new IncrementAge());
    }
}
