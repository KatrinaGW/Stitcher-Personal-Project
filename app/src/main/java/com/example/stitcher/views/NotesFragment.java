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
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.array_adapters.NotesArrayAdapter;
import com.example.stitcher.controllers.array_adapters.StatusesArrayAdapter;
import com.example.stitcher.models.Notes;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class NotesFragment extends Fragment {
    private NotesArrayAdapter notesArrayAdapter;
    private ListView notesListView;
    private NotesFragmentHandler notesFragmentHandler;
    private ArrayList<Notes> notes;

    interface NotesFragmentHandler {
        void noteChosen(int index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        notes = this.getArguments().getParcelableArrayList(ViewConstants.NOTES_FIELD.getValue());

        return inflater.inflate(R.layout.fragment_notes,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        notesListView = getView().findViewById(R.id.strings_listview);
        setAdapters();
        setListeners();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof NotesFragment.NotesFragmentHandler){
            notesFragmentHandler = (NotesFragment.NotesFragmentHandler) context;
        }
    }

    private void setAdapters(){
        Context activity = this.getActivity();

        ArrayList<String> noteTitles = notes.stream().map(Notes::getTitle).collect(Collectors.toCollection(ArrayList::new));

        notesArrayAdapter = new NotesArrayAdapter(activity, noteTitles);
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
