package com.example.sample_android.state;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TodoItem {
    
    public static TodoItem create(int id, String text, boolean done) {
        return new AutoValue_TodoItem(id, text, done);
    }
    
    public abstract int id();

    public abstract String text();
   
    public abstract boolean done();
}
