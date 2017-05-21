package me.tatarka.redux.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.v4.content.Loader;
import android.util.Pair;

import me.tatarka.redux.SimpleStore;
import me.tatarka.redux.Store;

/**
 * @deprecated Use rx or LiveData instead to observe state changes. Use ViewModel to hold an instance.
 */
@Deprecated
public abstract class StateLoader<S, SR extends SimpleStore<S>> extends Loader<S> {

    private static boolean debugAll = false;

    /**
     * Sets 'debug' mode for all {@code StateLoader}s. This will only take effect on loaders
     * created <em>after</em> this is called so you should call it as early as possible.
     *
     * @see #setDebug(boolean)
     */
    public static void setDebugAll(boolean value) {
        debugAll = value;
    }

    /**
     * Constructs a new {@code StateLoader} with the given {@link Store}.
     */
    public static <S, SR extends SimpleStore<S>> StateLoader<S, SR> create(Context context, final SR store) {
        return new StateLoader<S, SR>(context) {
            @Override
            protected SR onCreateStore() {
                return store;
            }
        };
    }

    private SR store;
    private final ResultHandler handler = new ResultHandler();
    private boolean debug = debugAll;

    public StateLoader(Context context) {
        super(context);
    }

    /**
     * Sets 'debug' mode which provides the dispatch source in stacktraces.
     */
    public void setDebug(boolean value) {
        debug = value;
    }

    /**
     * Subclasses should override this method to provide the store.
     */
    protected abstract SR onCreateStore();

    /**
     * Returns the store for this loader. This will lazily call {@link #onCreateStore()}.
     */
    @MainThread
    public final SR getStore() {
        if (store == null) {
            store = onCreateStore();
        }
        return store;
    }

    @Override
    protected void onStartLoading() {
        getStore().addListener(handler);
    }

    @Override
    protected void onStopLoading() {
        getStore().removeListener(handler);
    }

    /**
     * Listens to state changes and posts them to the main thread.
     */
    class ResultHandler extends Handler implements SimpleStore.Listener<S> {

        ResultHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            if (debug) {
                Pair<S, Exception> pair = (Pair<S, Exception>) msg.obj;
                try {
                    deliverResult(pair.first);
                } catch (Exception e) {
                    appendStacktrace(e, pair.second);
                    throw e;
                }
            } else {
                deliverResult((S) msg.obj);
            }
        }

        @Override
        public void onNewState(S state) {
            Message message;
            if (debug) {
                message = Message.obtain(this, 0, Pair.create(state, new Exception()));
            } else {
                message = Message.obtain(this, 0, state);
            }
            sendMessage(message);
        }
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
