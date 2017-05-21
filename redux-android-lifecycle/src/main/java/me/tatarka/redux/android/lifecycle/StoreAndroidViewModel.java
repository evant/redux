package me.tatarka.redux.android.lifecycle;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import me.tatarka.redux.SimpleStore;

public abstract class StoreAndroidViewModel<S, SR extends SimpleStore<S>> extends AndroidViewModel {

    private final StoreViewModelDelegate<S, SR> delegate;

    public StoreAndroidViewModel(Application application, @NonNull SR store) {
        super(application);
        this.delegate = new StoreViewModelDelegate<>(store);
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
