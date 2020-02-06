package com.example.sample_android;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleRegistryOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sample_android.action.Action;
import com.example.sample_android.action.Check;
import com.example.sample_android.action.LoadActionCreator;
import com.example.sample_android.action.Remove;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.state.TodoList;
import com.example.sample_android.store.MainStore;

import java.util.Collections;
import java.util.List;

import me.tatarka.redux.ReplayMiddleware;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LifecycleRegistryOwner {

    private final LifecycleRegistry registry = new LifecycleRegistry(this);

    MainStore store;
    ReplayMiddleware<TodoList, Action, Action> replayMiddleware;
    Adapter adapter = new Adapter();
    ActionListAdapter actionListAdapter = new ActionListAdapter();
    ProgressBar loading;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodoItemDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setAdapter(adapter);

        RecyclerView actionList = (RecyclerView) findViewById(R.id.action_list);
        actionList.setAdapter(actionListAdapter);

        loading = (ProgressBar) findViewById(R.id.loading);

        TodoViewModel viewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        store = viewModel.getStore();
        replayMiddleware = store.getReplayMiddleware();

        if (savedInstanceState == null) {
            store.dispatch(new LoadActionCreator(new Datastore(this)).load());
        }

        viewModel.getState().observe(this, new Observer<TodoList>() {
            @Override
            public void onChanged(TodoList data) {
                loading.setVisibility(data.loading() ? View.VISIBLE : View.GONE);
                if (data.loading()) {
                    fab.hide();
                } else {
                    fab.show();
                }
                adapter.setState(data);
                actionListAdapter.setState(replayMiddleware.actions());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return registry;
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<TodoItem> items = Collections.emptyList();

        public void setState(final TodoList list) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return items.size();
                }

                @Override
                public int getNewListSize() {
                    return list.items().size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return items.get(oldItemPosition).id() == list.items().get(newItemPosition).id();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return items.get(oldItemPosition).equals(list.items().get(newItemPosition));
                }
            });
            this.items = list.items();
            result.dispatchUpdatesTo(this);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(store, LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final MainStore store;
            final CheckBox checkBox;
            final ImageButton edit;
            final ImageButton delete;

            TodoItem item;

            public Holder(final MainStore store, View itemView) {
                super(itemView);
                checkBox = (CheckBox) itemView.findViewById(R.id.item);
                edit = (ImageButton) itemView.findViewById(R.id.edit);
                delete = (ImageButton) itemView.findViewById(R.id.delete);
                this.store = store;

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (item.done() != isChecked) {
                            store.dispatch(Check.create(item.id(), isChecked));
                        }
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TodoItemDialogFragment.newInstance(item.id()).show(getSupportFragmentManager(), "dialog");
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        store.dispatch(Remove.create(item.id()));
                    }
                });
            }

            public void bind(TodoItem item) {
                this.item = item;
                checkBox.setText(item.text());
                checkBox.setChecked(item.done());
            }
        }
    }

    class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.Holder> {

        List<Action> actions = Collections.emptyList();

        public void setState(List<Action> actions) {
            this.actions = actions;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind(position, actions.get(position));
        }

        @Override
        public int getItemCount() {
            return actions.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            final TextView textView;
            int index;

            public Holder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.action);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (replayMiddleware.isDisabled(index)) {
                            replayMiddleware.enable(index);
                        } else {
                            replayMiddleware.disable(index);
                        }
                    }
                });
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        EditActionDialogFragment.newInstance(index).show(getSupportFragmentManager(), "dialog");
                        return true;
                    }
                });
            }

            public void bind(int index, Object action) {
                this.index = index;
                if (replayMiddleware.isDisabled(index)) {
                    SpannableString str = new SpannableString(action.toString());
                    str.setSpan(new StrikethroughSpan(), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(str);
                } else {
                    textView.setText(action.toString());
                }
            }
        }
    }
}
