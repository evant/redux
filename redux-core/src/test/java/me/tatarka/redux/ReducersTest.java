package me.tatarka.redux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ReducersTest {

    @Test
    public void id_returns_existing_state() {
        String state = Reducers.<String, String>id().reduce("action", "test");

        assertEquals("test", state);
    }

    @Test
    public void all_runs_all_reducers() {
        Reducer<String, String> reducer1 = new Reducer<String, String>() {
            @Override
            public String reduce(String action, String state) {
                return state + "1";
            }
        };
        Reducer<String, String> reducer2 = new Reducer<String, String>() {
            @Override
            public String reduce(String action, String state) {
                return state + "2";
            }
        };
        String state = Reducers.all(reducer1, reducer2).reduce("action", "test");

        assertEquals("test12", state);
    }

    @Test
    public void first_runs_till_reducer_changes_state() {
        Reducer<String, String> reducer1 = Reducers.id();
        Reducer<String, String> reducer2 = new Reducer<String, String>() {
            @Override
            public String reduce(String action, String state) {
                return state + "1";
            }
        };
        Reducer<String, String> reducer3 = new Reducer<String, String>() {
            @Override
            public String reduce(String action, String state) {
                return state + "2";
            }
        };
        String state = Reducers.first(reducer1, reducer2, reducer3).reduce("action", "test");

        assertEquals("test1", state);
    }
}
