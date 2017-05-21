package me.tatarka.redux.android.lifecycle;

import android.arch.lifecycle.LiveData;
import android.os.Looper;
import android.support.annotation.Nullable;

import me.tatarka.redux.SimpleStore;

/**
 * Handles constructing a LiveData from a {@link me.tatarka.redux.SimpleStore}
 */
public class LiveDataAdapter {

    private static boolean debugAll = false;

    public static void setDebugAll(boolean value) {
        debugAll = value;
    }

    public static <S> LiveData<S> liveData(final SimpleStore<S> store) {
        return liveData(store, debugAll);
    }

    public static <S> LiveData<S> liveData(final SimpleStore<S> store, boolean debug) {
        return new SimpleStoreLiveData<>(store, debug);
    }

    private static class SimpleStoreLiveData<S> extends LiveData<S> implements SimpleStore.Listener<S> {
        final SimpleStore<S> store;
        final boolean debug;
        @Nullable
        volatile Exception exception;

        private SimpleStoreLiveData(SimpleStore<S> store, boolean debug) {
            this.store = store;
            this.debug = debug;
            setValue(store.getState());
        }

        @Override
        protected void onActive() {
            store.addListener(this);
        }

        @Override
        protected void onInactive() {
            store.removeListener(this);
        }

        @Override
        protected void setValue(S value) {
            if (debug && exception != null) {
                try {
                    super.setValue(value);
                } catch (Exception e) {
                    appendStacktrace(e, exception);
                    throw e;
                }
                exception = null;
            } else {
                super.setValue(value);
            }
        }

        @Override
        public void onNewState(S state) {
            if (isMainThread()) {
                setValue(state);
            } else {
                if (debug) {
                    exception = new Exception();
                }
                postValue(state);
            }
        }

        private static boolean isMainThread() {
            return Thread.currentThread() == Looper.getMainLooper().getThread();
        }

        private static void appendStacktrace(Throwable e, Throwable context) {
            StackTraceElement[] originalStacktrace = e.getStackTrace();
            StackTraceElement[] additionalStacktrace = context.getStackTrace();
            StackTraceElement[] combinedStacktrace = new StackTraceElement[originalStacktrace.length + additionalStacktrace.length];
            System.arraycopy(originalStacktrace, 0, combinedStacktrace, 0, originalStacktrace.length);
            System.arraycopy(additionalStacktrace, 0, combinedStacktrace, originalStacktrace.length, additionalStacktrace.length);
            e.setStackTrace(combinedStacktrace);
        }
    }
}
