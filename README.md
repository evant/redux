# redux for java/android (name tbd)
Redux ported to java/android

I've seen a few of these floating around, but this one has some specific benefits over other
implementations.
* Any object can be used as an action or state.
* Built-in functions to help compose reducers
* Middleware that's actually implemented like you'd expect.
* Thunk and rxjava dispatchers.
* A fully-fleshed-out android sample.

## Download

```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile "me.tatarka.redux:redux-core:0.10"
  compile "me.tatarka.redux:redux-android:0.10"
  compile "me.tatarka.redux:redux-android-lifecycle:0.10"
  compile "me.tatarka.redux:redux-thunk:0.10"
  compile "me.tatarka.redux:redux-rx:0.10"
  compile "me.tatarka.redux:redux-rx2:0.10"
}
```

## Usage

Create a store.
```java
SimpleStore<State> store = new SimpleStore(initialState);
```

Get the current state.
```java
State state = store.state();
```

Listen to state changes.
```java
store.addListener(new Listener<State>() {
  @Override
  public void onNewState(State state) {
    ...
  }
});
```

Or with rxjava (using redux-rx).
```java
ObservableAdapter.observable(store).subscribe(state -> { ... });
```

Or with rxjava2 (using redux-rx2).
```java
FlowableAdapter.flowable(store).subscribe(state -> { ... });
```

Create a dispatcher with optional middleware.
```java
Dispatcher<Action, Action> dispatcher = Dispatcher.forStore(store, reducer)
    .chain(middleware...);
```

Dispatch actions.
```java
dispatcher.dispatch(new MyAction());
```

## Android

You can observe your store with `LiveData` which will properly tie into the android lifecycle.
```java
LiveDataAdapter.liveData(store).observe(this, state -> { ... });
```

You can use `StoreViewModel` to keep your store around for the lifetime of an activity/fragment
surviving configuration changes.
```java
public class MyViewModel extends StoreViewModel<State, MyViewModel> {
  public MyViewModel() {
    super(new MyStore());
  }
}
```

```java
public class MyActivity extends LifecycleActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyViewModel viewModel = ViewModelProviders.of(this).get(MyViewModel.class);
    MyStore store = viewModel.getStore();
    viewModel.getState().observe(this, state -> { ... });
  }
}
```

Since `LiveData` relays state changes to the main thread, you may lose important stack trace info.
You can get it back by calling `LiveDataAdapter.liveData(store, true)` or
`LiveDataAdapter.setDebugAll(true)`. This creates an expensive stacktrace on every dispatch so you
probably don't want it on release. A common pattern would be to put
`LiveDataAdapter.setDebugAll(BuildConfig.DEBUG)` in your setup code.

## Composing Reducers

It's common you'd want to switch on actions values or class type. `Reducers.matchValue()` and
`Reducers.matchClass()` makes this easy.
```java
Reducer<State, String> reducer = Reducers.matchValue()
  .when("action1", new Action1Reducer())
  .when("action2", new Action2Reducer());

Reducer<State, Object> reducer = Reducers.matchClass()
  .when(Action1.class, new Action1Reducer())
  .when(Action2.class, new Action2Reducer());
```

There is also `Reducers.match()` which takes a predicate for more complicated matching setups.
```java
Reducer<State, Object> reducer = Reducers.match()
  .when(Predicates.is("action1"), new Action1Reducer())
  .when(Predicates.instanceOf(Action2.class), new Action2Reducer());
```

You can also run a sequence of reducers with `Reducers.all(reducer1, reducer2, ...)` or run reducers
until one changes the state with `Reducers.first(reducer1, reducer2, ...)`.

## Thunk Dispatcher

Allows you to dispatch async functions as actions.

```java
SimpleStore<State> store = new SimpleStore<>(initialState);
ThunkDispatcher<Action, Action> dispatcher = new ThunkDispatcher<>(Dispatcher.forStore(store, reducer));

dispatcher.dispatch(new Thunk<Action, Action>() {
  @Override
  public void run(Dispatcher<Action, Action> dispatcher) {
    dispatcher.dispatch(new StartLoading());
    someAsyncCall(new Runnable() {
      @Override
      public void run() {
        dispatcher.dispatch(new StopLoading());
      }
    }
  }
});
```

## Observable Dispatcher

Alternatively, you can use the `ObservableDispatcher` to dispatch a stream of actions.

```java
SimpleStore<State> store = new SimpleStore<>(initialState);
ObservableDispatcher<Action> dispatcher = new ObservableDispatcher<>(Dispatcher.forStore(store, reducer));

dispatcher.dispatch(callThatReturnsObservable()
    .map(result -> new StopLoading())
    .startWith(Observable.just(new StartLoading())));
```

## Subclassing a Store

Don't want to have to worry about passing around the store and dispatchers? You can subclass
`SimpleStore` and create your own dispatch methods. This also simplifies generics a bit when using
throughout your app.

```java
public class MyStore extends SimpleStore<State> {

  private final Dispatcher<Action, Action> dispatcher;
  private final ObservableDispatcher<Action> observableDispatcher;

  public MyStore() {
    super(new State());
    dispatcher = Dispatcher.forStore(this, new MyReducer())
      .chain(new LogMiddleware<>("ACTION"));
    observableDispatcher = new ObservableDispatcher<>(dispatcher);
  }

  public Action dispatch(Action action) {
    return dispatcher.dispatch(action);
  }

  public Subscription dispatch(Observable<Action> actions) {
    return observableDispatcher.dispatch(actions);
  }

  public Observable<Action> observable() {
    return ObservableAdapter.observable(this);
  }
}
```

Now you can just pass the single store around and call `store.dispatch()`.

## Debug Utilities

### Android LogMiddleware

You can log all actions on android with the built-in `LogMiddleware`.

```java
dispatcher = Dispatcher.forStore(store, reducer)
  .chain(new LogMiddleware<Action, Action>("ACTION"));
```

### ReplayMiddleware

You can disable/enable actions and see how that effects your ui with the replay middleware. It will
replay your modified actions back on the initial state.

```groovy
compile "me.tatarka.redux:redux-replay:0.10"
```

```java
replay = new ReplayMiddleware<State, Action, Action>(store, reducer);
dispatcher = Dispatcher.forStore(store, reducer)
  .chain(replay);

replay.actions() // lists all actions that have been dispatched
replay.disable(index) // disables action at given index
replay.enable(index) // enables action at the given index
```

The sample android app includes a debug drawer to let you interact with this middleware.

### Redux Debugging tools integration.

You can connect to [RemoteDev Server](https://github.com/zalmoxisus/remotedev-server) to interact
with various redux debugging UI's. Currently only displaying actions/state is supported.

```
npm install -g remotedev-server
remotedev --hostname=localhost --port=8000
```

```groovy
compile "me.tatarka.redux:redux-monitor:0.10"
```

```java
dispatcher = Dispatcher.forStore(store, reducer)
  .chain(new MonitorMiddleware(store));
```
