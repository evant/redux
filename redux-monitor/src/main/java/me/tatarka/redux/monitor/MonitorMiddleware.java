package me.tatarka.redux.monitor;


import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import io.github.sac.BasicListener;
import io.github.sac.Socket;
import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;

public class MonitorMiddleware<S, A, R> implements Middleware<A, R> {

    private final Config config;
    private final Store<S> store;

    private final LinkedBlockingQueue<Message<S, A>> queue = new LinkedBlockingQueue<>();
    private Thread socketThread;

    public MonitorMiddleware(Store<S> store) {
        this(store, Config.DEFAULT);
    }

    public MonitorMiddleware(Store<S> store, Config config) {
        this.store = store;
        this.config = config;
        if (config.autoStart) {
            start();
        }
    }

    public void start() {
        if (socketThread == null) {
            socketThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket socket = new Socket("ws://" + config.host + ":" + config.port + "/socketcluster/");
                    socket.setListener(new BasicListener() {
                        @Override
                        public void onConnected(Socket socket, Map<String, List<String>> headers) {
                            System.out.println("Connected");
                        }

                        @Override
                        public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                            System.out.println("Disconnected: " + closedByServer);
                        }

                        @Override
                        public void onConnectError(Socket socket, WebSocketException exception) {
                            exception.printStackTrace();
                        }

                        @Override
                        public void onAuthentication(Socket socket, Boolean status) {

                        }

                        @Override
                        public void onSetAuthToken(String token, Socket socket) {

                        }
                    });
                    socket.connect();

                    while (true) {
                        try {
                            Message<S, A> message = queue.take();
                            if (message == Message.END) {
                                socket.disconnect();
                                break;
                            }
                            JSONObject json = new JSONObject();
                            json.put("type", "ACTION");
                            JSONObject action = new JSONObject();
                            action.put("action", jsonify(message.action, true));
                            action.put("timestamp", new Date().getTime());
                            json.put("action", action);
                            json.put("payload", jsonify(message.state, false));
                            json.put("instanceId", store.getClass());

                            socket.emit("log-noid", json);
                        } catch (InterruptedException e) {
                            break;
                        } catch (JSONException e) {
                            break;
                        }
                    }
                }
            });
            socketThread.start();
        }
    }

    private JSONObject jsonify(Object object, boolean addType) throws JSONException {
        JSONObject json = new JSONObject();
        if (addType) {
            json.put("type", object.getClass().getName());
        }
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic()
                        || Modifier.isStatic(field.getModifiers())
                        || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(object);
                if (value == null || isFramework(field.getType())) {
                    json.put(field.getName(), value);
                } else {
                    json.put(field.getName(), jsonify(value, false));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return json;
    }

    private boolean isFramework(Class objectClass) {
        if (objectClass.isPrimitive()) {
            return true;
        }
        String name = objectClass.getName();
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
            return true;
        }
        for (Class basicClass : config.basicClasses) {
            if (basicClass.equals(objectClass)) {
                return true;
            }
        }
        return false;
    }

    public void stop() {
        if (socketThread != null) {
            queue.add(Message.END);
            socketThread = null;
        }
    }

    @Override
    public R dispatch(Dispatcher<A, R> dispatcher, Next<A, R> next, A action) {
        R r = next.next(action);
        S state = store.getState();
        Message<S, A> message = new Message<>(state, action);
        queue.add(message);
        return r;
    }

    public static class Config {

        public static final Config DEFAULT = new Config("localhost", 8000);

        final String host;
        final int port;
        final boolean autoStart;
        final Class[] basicClasses;

        public Config(String host, int port) {
            this(host, port, true, new Class[0]);
        }

        Config(String host, int port, boolean autoStart, Class[] basicClasses) {
            this.host = host;
            this.port = port;
            this.autoStart = autoStart;
            this.basicClasses = basicClasses;
        }

        public static class Builder {
            private String host = "localhost";
            private int port = 8000;
            private boolean autoStart = true;
            private Class[] basicClasses = new Class[0];

            public Builder host(String host) {
                this.host = host;
                return this;
            }

            public Builder port(int port) {
                this.port = port;
                return this;
            }

            public Builder basicClasses(Class... classes) {
                basicClasses = classes;
                return this;
            }

            public Config build() {
                return new Config(host, port, autoStart, basicClasses);
            }
        }
    }

    private static class Message<S, A> {
        static final Message END = new Message(null, null);

        final S state;
        final A action;

        Message(S state, A action) {
            this.state = state;
            this.action = action;
        }
    }
}
