package com.example.stitcher;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stitcher.controllers.array_adapters.CounterArrayAdapter;
import com.example.stitcher.controllers.array_adapters.UrlsArrayAdapter;
import com.example.stitcher.models.Counter;

import java.util.ArrayList;

public class ProjectCountersFragment extends Fragment {
    private ArrayList<Counter> counters;
    private CounterArrayAdapter counterArrayAdapter;
    private ListView countersListview;
    private ProjectCountersFragmentHandler fragmentHandler;

    interface ProjectCountersFragmentHandler {
        void counterChosen(Counter counter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        counters = this.getArguments().getParcelableArrayList(ViewConstants.FRAGMENT_PROJECT_COUNTERS.getValue());

        return inflater.inflate(R.layout.fragment_project_counters,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        countersListview = getView().findViewById(R.id.counters_listview);
        setAdapters();
        setListeners();
        System.out.println("FRAGMENT CREATED");
        System.out.println(counters);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof ProjectCountersFragment.ProjectCountersFragmentHandler){
            fragmentHandler = (ProjectCountersFragment.ProjectCountersFragmentHandler) context;
        }
    }

    private void setAdapters(){
        Context activity = this.getActivity();

        counterArrayAdapter = new CounterArrayAdapter(activity, counters);
        countersListview.setAdapter(counterArrayAdapter);
    }

    private void setListeners(){
        countersListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentHandler.counterChosen(counters.get(position));
            }
        });
    }
}
