package me.tatarka.redux.android.lifecycle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import me.tatarka.redux.SimpleStore;

public abstract class StoreViewModel<S, SR extends SimpleStore<S>> extends ViewModel {

    private final StoreViewModelDelegate<S, SR> delegate;

    public StoreViewModel(@NonNull SR store) {
        delegate = new StoreViewModelDelegate<>(store);
    }

    @NonNull
    @MainThread
    public final SR getStore() {
        return delegate.store;
    }

    @MainThread
    public LiveData<S> getState() {
        return delegate.getState();
    }
}
