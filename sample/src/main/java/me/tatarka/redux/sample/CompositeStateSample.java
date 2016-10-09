package me.tatarka.redux.sample;

import me.tatarka.redux.ObservableStore;
import me.tatarka.redux.Reducer;
import me.tatarka.redux.Reducers;

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

    static <T> Reducer<Replace<T>, T> replace() {
        return (action, state) -> action.newValue();
    }

    static Reducer<Increment, Integer> increment = (action, state) -> state + 1;

    static Reducer<Object, Person> updatePerson = Reducers.<Object, Person>matchClass()
            .when(ChangeName.class, (action, state) -> new Person(CompositeStateSample.<String>replace().reduce(action, state.name), state.age))
            .when(IncrementAge.class, (action, state) -> new Person(state.name, increment.reduce(action, state.age)));

    // actions

    interface Replace<T> {
        T newValue();
    }

    interface Increment {
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
        ObservableStore<Object, Person> store = ObservableStore.create(new Person("nobody", 0), updatePerson, new LogMiddleware<>());
        store.observable().subscribe(person -> System.out.println("state: " + person));
        store.dispatch(new ChangeName("Bob"));
        store.dispatch(new IncrementAge());
    }
}
