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
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Url;

import java.util.ArrayList;

public class UrlsArrayAdapter extends ArrayAdapter<Url> {
    public UrlsArrayAdapter(Context context, ArrayList<Url> urlData) {
        super(context, 0, urlData);
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

        Url url = getItem(position);

        TextView counterName = view.findViewById(R.id.string_value_txt);

        counterName.setText(url.getUrl());

        return view;
    }
}
