package me.tatarka.redux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PredicatesTest {

    @Test
    public void always_returns_true() {
        boolean result = Predicates.always().test("test");

        assertTrue(result);
    }

    @Test
    public void never_returns_false() {
        boolean result = Predicates.never().test("test");

        assertFalse(result);
    }

    @Test
    public void is_returns_true_when_equal() {
        boolean result = Predicates.is("test").test("test");

        assertTrue(result);
    }

    @Test
    public void is_returns_false_when_not_equal() {
        boolean result = Predicates.is("not test").test("test");

        assertFalse(result);
    }

    @Test
    public void instanceOf_returns_true_when_class_matches() {
        boolean result = Predicates.instanceOf(String.class).test("test");

        assertTrue(result);
    }

    @Test
    public void instanceOf_returns_false_when_class_doesnt_match() {
        boolean result = Predicates.<Object, Integer>instanceOf(Integer.class).test("test");

        assertFalse(result);
    }

    @Test
    public void instanceOf_returns_true_when_subclass_matches() {
        boolean result = Predicates.instanceOf(Object.class).test("test");

        assertTrue(result);
    }

    @Test
    public void negate_reverse_result() {
        boolean result = Predicates.negate(Predicates.never()).test("test");

        assertTrue(result);
    }

    @Test
    public void any_returns_true_if_one_is_true() {
        boolean result = Predicates.any(Predicates.never(), Predicates.always()).test("test");

        assertTrue(result);
    }

    @Test
    public void any_returns_false_if_none_is_true() {
        boolean result = Predicates.any(Predicates.never(), Predicates.never()).test("test");

        assertFalse(result);
    }
}
