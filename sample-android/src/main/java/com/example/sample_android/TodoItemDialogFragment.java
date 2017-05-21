package com.example.sample_android;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sample_android.action.Add;
import com.example.sample_android.action.Edit;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.store.MainStore;

public class TodoItemDialogFragment extends DialogFragment {

    public static TodoItemDialogFragment newInstance() {
        return newInstance(-1);
    }

    public static TodoItemDialogFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        TodoItemDialogFragment fragment = new TodoItemDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    MainStore store;
    int id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = ViewModelProviders.of(getActivity()).get(TodoViewModel.class).getStore();
        id = getArguments().getInt("id", -1);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.item_dialog, null);
        final TextView textView = (TextView) view.findViewById(R.id.text);
        String text = null;
        for (TodoItem todoItem : store.getState().items()) {
            if (todoItem.id() == id) {
                text = todoItem.text();
                break;
            }
        }
        textView.setText(text);

        return new AlertDialog.Builder(getContext(), getTheme())
                .setTitle(id >= 0 ? "Edit" : "New")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = TextUtils.isEmpty(textView.getText()) ? "Item" : textView.getText().toString();
                        if (id >= 0) {
                            store.dispatch(Edit.create(id, text));
                        } else {
                            store.dispatch(Add.create(text));
                        }
                    }
                })
                .create();
    }
}
