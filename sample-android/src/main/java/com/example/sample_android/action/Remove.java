package com.example.sample_android.action;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Remove implements Action {
    
    public static Remove create(int id) {
        return new AutoValue_Remove(id);
    }
    
    public abstract int id();
}
