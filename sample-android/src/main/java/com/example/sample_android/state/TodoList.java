package com.example.sample_android.state;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class TodoList {

    public static TodoList initial() {
        return new AutoValue_TodoList(true, Collections.<TodoItem>emptyList());
    }

    public static TodoList create(boolean loading, List<TodoItem> items) {
        return new AutoValue_TodoList(loading, Collections.unmodifiableList(items));
    }
    
    public abstract boolean loading();

    public abstract List<TodoItem> items();
}
