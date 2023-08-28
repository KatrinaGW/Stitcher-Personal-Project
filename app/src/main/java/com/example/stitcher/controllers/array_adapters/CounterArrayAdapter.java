package com.example.stitcher.controllers.array_adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stitcher.R;
import com.example.stitcher.models.Counter;

import java.util.ArrayList;

public class CounterArrayAdapter extends ArrayAdapter<Counter> {
    public CounterArrayAdapter(Context context, ArrayList<Counter> counterData) {
        super(context, 0, counterData);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.counters_listview,
                    parent, false);
        } else {
            view = convertView;
        }

        Counter counter = getItem(position);

        TextView counterName = view.findViewById(R.id.counter_name_txt);

        counterName.setText(counter.getName());

        return view;
    }
}
