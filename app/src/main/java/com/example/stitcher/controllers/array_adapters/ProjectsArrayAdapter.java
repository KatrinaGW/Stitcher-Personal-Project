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
import com.example.stitcher.models.Url;

import java.util.ArrayList;

public class ProjectsArrayAdapter extends ArrayAdapter<Project> {
    public ProjectsArrayAdapter(Context context, ArrayList<Project> projectData) {
        super(context, 0, projectData);
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

        Project project = getItem(position);

        TextView projectName = view.findViewById(R.id.string_value_txt);

        projectName.setText(project.getName());

        return view;
    }
}
