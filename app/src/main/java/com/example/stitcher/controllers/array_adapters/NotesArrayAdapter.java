package com.example.stitcher.controllers.array_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stitcher.R;

import java.util.ArrayList;

public class NotesArrayAdapter extends ArrayAdapter<String> {
    public NotesArrayAdapter(Context context, ArrayList<String> notes) {
        super(context, 0, notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.strings_listview,
                    parent, false);
        } else {
            view = convertView;
        }

        String note = getItem(position);

        TextView noteView = view.findViewById(R.id.string_value_txt);

        noteView.setText(note);

        return view;
    }
}
