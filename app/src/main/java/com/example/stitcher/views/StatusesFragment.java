package com.example.stitcher.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stitcher.R;
import com.example.stitcher.constants.Statuses;
import com.example.stitcher.controllers.array_adapters.StatusesArrayAdapter;

public class StatusesFragment extends Fragment {
    private StatusesArrayAdapter statusesArrayAdapter;
    private ListView statusesListview;
    private StatusesFragmentHandler statusesFragmentHandler;

    interface StatusesFragmentHandler {
        void statusChosen(String status);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_statuses,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        statusesListview = getView().findViewById(R.id.strings_listview);
        setAdapters();
        setListeners();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof StatusesFragment.StatusesFragmentHandler){
            statusesFragmentHandler = (StatusesFragment.StatusesFragmentHandler) context;
        }
    }

    private void setAdapters(){
        Context activity = this.getActivity();

        statusesArrayAdapter = new StatusesArrayAdapter(activity, Statuses.getAllValues());
        statusesListview.setAdapter(statusesArrayAdapter);
    }

    private void setListeners(){
        statusesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                statusesFragmentHandler.statusChosen(Statuses.getAllValues().get(position));
            }
        });
    }
}
