package me.tatarka.redux.android.lifecycle;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;

import me.tatarka.redux.SimpleStore;

class StoreViewModelDelegate<S, SR extends SimpleStore<S>> {

    final SR store;
    @Nullable
    private LiveData<S> state;

    StoreViewModelDelegate(SR store) {
        this.store = store;
    }

    LiveData<S> getState() {
        if (state == null) {
            state = LiveDataAdapter.liveData(store);
        }
        return state;
    }
}
