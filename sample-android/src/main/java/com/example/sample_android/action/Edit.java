package com.example.sample_android.action;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Edit implements UpdateItem {
    
    public static Edit create(int id, String text) {
        return new AutoValue_Edit(id, text);
    }
    
    public abstract int id();
    
    public abstract String text();
}
