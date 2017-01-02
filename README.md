# redux for java/android (name tbd)
Redux ported to java/android

I've seen a few of these floating around, but this one has some specific benefits over other implementations.
* Any object can be used as an action or state.
* Built-in functions to help compose reducers
* Middleware that's actually implemented like you'd expect.
* Thunk and rxjava dispatchers.
* A fully-fleshed-out android sample.

## Download

```groovy
buildscript {
  repositories {
    maven {
      url "https://oss.sonatype.org/content/repositories/snapshots"
    }
  }
}

dependencies {
  compile 'me.tatarka.redux:redux-core:1.0-SNAPSHOT'
  compile 'me.tatarka.redux:redux-android:1.0-SNAPSHOT'
  compile 'me.tatarka.redux:redux-thunk:1.0-SNAPSHOT'
  compile 'me.tatarka.redux:redux-rx:1.0-SNAPSHOT'
  compile 'me.tatarka.redux:redux-replay:1.0-SNAPSHOT'
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
ObserveStore.observable(store).subscribe(state -> { ... });
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

I suggest you use the `StateLoader` as it will tie your state updates with the activity/fragment lifecycle and ensure callbacks happen on the main thread.
```java
public class MyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<State> {
  SimpleStore<State> store = ...; // obtain this from somewhere, singleton maybe.

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getSupportLoaderManager().initLoader(0, null, this);
  }
  
  @Override
  public Loader<State> onCreateLoader(int id, Bundle args) {
    return StateLoader.create(this, store);
  }
  
  @Override
  public void onLoadFinished(Loader<State> loader, State data) {
    ...;
  }

  @Override
  public void onLoaderReset(Loader<TodoList> loader) {
  }
}
```

You may also subclass `StateLoader` if you want the loader to manage the lifecycle of the store.
```java
public class MyStateLoader extends StateLoader<MyStore, State> {
  public TodoStateLoader(Context context) {
    super(context);
  }

  @Override
  protected MyStore onCreateStore() {
    return new MyStore(..);
  }
}
```

Since the loader relays state changes to the main thread, you may lose important stack trace info.
You can get it back by setting `StateLoader#debug(true)` or `StateLoader.debugAll(true)`. This 
creates an expensive stacktrace on every dispatch so you probably don't want it on release. A common
pattern would be to put `StateLoader.debugAll(BuildConfig.DEBUG)` in your setup code.

## Composing Reducers

It's common you'd want to switch on actions values or class type. `Reducers.matchValue()` and `Reducers.matchClass()` makes this easy.
```java
Reducer<String, State> reducer = Reducers.matchValue()
  .when("action1", new Action1Reducer())
  .when("action2", new Action2Reducer());

Reducer<Object, State> reducer = Reducers.matchClass()
  .when(Action1.class, new Action1Reducer())
  .when(Action2.class, new Action2Reducer());
```

There is also `Reducers.match()` which takes a predicate for more complicated matching setups.
```java
Reducer<Object, State> reducer = Reducers.match()
  .when(Predicates.is("action1"), new Action1Reducer())
  .when(Predicates.instanceOf(Action2.class), new Action2Reducer());
```

You can also run a sequence of reducers with `Reducers.all(reducer1, reducer2, ...)` or run reducers until one changes the state with `Reducers.first(reducer1, reducer2, ...)`.

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

Don't want to have to worry about passing around the store and dispatchers? You can subclass `SimpleStore` and create
your own dispatch methods. This also simplifies generics a bit when using throughout your app.

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
    return ObserveStore.observable(this);
  }
}
```

Now you can just pass the single store around and call `store.dispatch()`.