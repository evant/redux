# redux for java/android (name tbd)
Redux ported to java/android

I've seen a few of these floating around, but this one has some specific benefits over other implementations.
* Any object can be used as an action or state.
* Built-in functions to help compose reducers
* Middleware that's actually implemented like you'd expect.
* A port of the thunk middleware.
* A fully-fleshed android sample.

## Usage

Create a store.
```java
ObservableStore<State> store = new Observable<>(initialState, reducer, middleware...);
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

Or with rxjava (you must explicitly declare it as a dependency).
```java
store.observable().subscribe(state -> { ... });
```

Dispatch actions.
```java
store.dispatch(new MyAction());
```

## Android

I suggest you use the `StateLoader` as it will tie your state updates with the activity/fragment lifecycle and ensure callbacks happen on the main thread.
```java
public class MyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<State> {
  ObservableStore<State> store = ...; // obtain this from somewhere, singleton maybe.

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
public class MyStateLoader extends StateLoader<State> {
  public TodoStateLoader(Context context) {
    super(context);
  }

  @Override
  protected ObservableStore<TodoList> onCreateStore() {
    return new ObservableStore<>(...);
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

## Thunk Middleware

Allows you to dispatch async functions as actions.

```java
ObservableStore<State> store = new ObservableStore<>(initialState, reducer, new ThunkMiddleware<State>());

store.dispatch(new Thunk<State>() {
  @Override
  public void run(Store<State> store) {
    store.dispatch(new StartLoading());
    someAsyncCall(new Runnable() {
      @Override
      public void run() {
        store.dispatch(new StopLoading());
      }
    }
  }
});
```

## Observable Middleware

Alternatively, you can use the `ObservableMiddleware` to dispatch a stream of actions.

```java
ObservableStore<State> store = new ObservableStore<>(initialState, reducer, new ObservableMiddleware<State>());

store.dispatch(callThatReturnsObservable()
    .map(result -> new StopLoading())
    .startWith(Observable.just(new StartLoading())));
```
