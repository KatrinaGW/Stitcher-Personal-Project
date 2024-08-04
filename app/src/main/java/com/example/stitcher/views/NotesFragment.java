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
import com.example.stitcher.controllers.array_adapters.NotesArrayAdapter;
import com.example.stitcher.controllers.array_adapters.StatusesArrayAdapter;

public class NotesFragment extends Fragment {
    private NotesArrayAdapter notesArrayAdapter;
    private ListView notesListView;
    private NotesFragmentHandler notesFragmentHandler;

    interface NotesFragmentHandler {
        void noteChosen(int index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_notes,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        notesListView = getView().findViewById(R.id.strings_listview);
        setAdapters(savedInstanceState);
        setListeners();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof NotesFragment.NotesFragmentHandler){
            notesFragmentHandler = (NotesFragment.NotesFragmentHandler) context;
        }
    }

    private void setAdapters(Bundle bundle){
        Context activity = this.getActivity();

        notesArrayAdapter = new NotesArrayAdapter(activity, bundle.getStringArrayList("noteTitles"));
        notesListView.setAdapter(notesArrayAdapter);
    }

    private void setListeners(){
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                notesFragmentHandler.noteChosen(position);
            }
        });
    }
}
