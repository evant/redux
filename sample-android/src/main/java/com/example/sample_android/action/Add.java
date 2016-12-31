package com.example.sample_android.action;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Add implements Action {
   
    public static Add create(String text) {
        return new AutoValue_Add(text);
    }

    public abstract String text();
}
