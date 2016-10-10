package me.tatarka.redux;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.Loader;

public class StateLoader<S> extends Loader<S> {

    protected final ObservableStore<?, S> store;
    private final ResultHandler handler = new ResultHandler();

    public StateLoader(Context context, ObservableStore<?, S> store) {
        super(context);
        this.store = store;
    }

    @Override
    protected void onStartLoading() {
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
