package me.tatarka.redux.sample;

import me.tatarka.redux.*;
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

    static <T> Reducer<Replace<T>, T> replace() {
        return (action, state) -> action.newValue();
    }

    static Reducer<Increment, Integer> increment = (action, state) -> state + 1;

    static Reducer<Action, Person> updatePerson = Reducers.<Action, Person>matchClass()
            .when(ChangeName.class, (action, state) -> new Person(CompositeStateSample.<String>replace().reduce(action, state.name), state.age))
            .when(IncrementAge.class, (action, state) -> new Person(state.name, increment.reduce(action, state.age)));

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
