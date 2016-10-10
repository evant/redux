package me.tatarka.redux;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.v4.content.Loader;

public abstract class StateLoader<S> extends Loader<S> {

    /**
     * Constructs a new {@code StateLoader} with the given {@link Store}.
     */
    public static <S> StateLoader create(Context context, final ObservableStore<S> store) {
        return new StateLoader(context) {
            @Override
            protected ObservableStore onCreateStore() {
                return store;
            }
        };
    }

    private ObservableStore<S> store;
    private final ResultHandler handler = new ResultHandler();

    public StateLoader(Context context) {
        super(context);
    }

    /**
     * Subclasses should override this method to provide the store.
     */
    protected abstract ObservableStore<S> onCreateStore();

    /**
     * Returns the store for this loader. This will lazily call {@link #onCreateStore()}.
     */
    @MainThread
    public final Store<S> store() {
        if (store == null) {
            store = onCreateStore();
        }
        return store;
    }

    @Override
    protected void onStartLoading() {
        // ensure store is set.
        store();
        store.addListener(handler);
    }

    @Override
    protected void onStopLoading() {
        store.removeListener(handler);
    }

    /**
     * Listens to state changes and posts them to the main thread.
     */
    class ResultHandler extends Handler implements ObservableStore.Listener<S> {

        ResultHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            deliverResult((S) msg.obj);
        }

        @Override
        public void onNewState(S state) {
            sendMessage(Message.obtain(this, 0, state));
        }
    }
}
