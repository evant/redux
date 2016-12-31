package com.example.sample_android.action;

import com.example.sample_android.state.TodoItem;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Load implements Action {

    public static Load create(List<TodoItem> items) {
        return new AutoValue_Load(items);
    }

    public abstract List<TodoItem> items();
}
