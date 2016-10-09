package com.example.sample_android.action;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Check implements UpdateItem {
    
    public static Check create(int id, boolean checked) {
        return new AutoValue_Check(id, checked);
    }
    
    public abstract int id();
    
    public abstract boolean checked();
}
