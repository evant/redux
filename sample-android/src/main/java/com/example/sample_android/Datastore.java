package com.example.sample_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.sample_android.state.TodoItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Datastore {

    // poor-man's persistence
    private SharedPreferences prefs;

    public Datastore(Context context) {
        prefs = context.getSharedPreferences("datastore", Context.MODE_PRIVATE);
    }

    public void store(List<TodoItem> items) {
        StringBuilder data = new StringBuilder();
        for (Iterator<TodoItem> iterator = items.iterator(); iterator.hasNext(); ) {
            TodoItem item = iterator.next();
            data.append(item.id()).append(":").append(item.text()).append(":").append(item.done());
            if (iterator.hasNext()) {
                data.append(",");
            }
        }
        prefs.edit()
                .putString("data", data.toString())
                .apply();
    }

    public void get(final Callback callback) {
        // Yeah you can get them pretty fast, but let's pretend it's slow.
        new AsyncTask<Void, Void, List<TodoItem>>() {
            @Override
            protected List<TodoItem> doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return null;
                }
                String[] data = prefs.getString("data", "").split(",");
                List<TodoItem> items = new ArrayList<>(data.length);
                for (String s : data) {
                    String[] fields = s.split(":");
                    if (fields.length == 3) {
                        int id = Integer.parseInt(fields[0]);
                        String text = fields[1];
                        boolean done = Boolean.parseBoolean(fields[2]);
                        items.add(TodoItem.create(id, text, done));
                    }
                }
                return items;
            }

            @Override
            protected void onPostExecute(List<TodoItem> todoItems) {
                callback.onList(todoItems);
            }
        }.execute();
    }

    public interface Callback {
        void onList(List<TodoItem> items);
    }
}
