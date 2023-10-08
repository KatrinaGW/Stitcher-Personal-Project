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
import com.example.stitcher.models.Project;

import java.util.ArrayList;

public class StatusesArrayAdapter extends ArrayAdapter<String> {
    public StatusesArrayAdapter(Context context, ArrayList<String> statuses) {
        super(context, 0, statuses);
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

        String status = getItem(position);

        TextView statusView = view.findViewById(R.id.string_value_txt);

        statusView.setText(status);

        return view;
    }
}
