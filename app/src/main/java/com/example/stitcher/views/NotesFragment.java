package com.example.stitcher.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stitcher.R;
import com.example.stitcher.constants.Statuses;
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.array_adapters.NotesArrayAdapter;
import com.example.stitcher.controllers.array_adapters.StatusesArrayAdapter;
import com.example.stitcher.controllers.handlers.NotesHandler;
import com.example.stitcher.models.Notes;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotesFragment extends Fragment {
    private NotesArrayAdapter notesArrayAdapter;
    private ListView notesListView;
    private NotesFragmentHandler notesFragmentHandler;
    private ArrayList<Notes> notes;
    private Button noteFragmentBtn;
    private boolean enteringNote = false;
    private EditText noteTextArea;
    private Notes chosenNote = null;
    private Button closeBtn;
    private EditText noteTitleTxt;

    interface NotesFragmentHandler {
        void closed();
        void noteCreated(String noteTitle, String noteBody);
        void noteUpdated(Notes note, String newBody, String newTitle);
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
        noteFragmentBtn = getView().findViewById(R.id.note_fragment_btn);
        noteTextArea = getView().findViewById(R.id.note_text_area);
        closeBtn = getView().findViewById(R.id.note_fragment_close_btn);
        noteTitleTxt = getView().findViewById(R.id.note_title_txt);
        setAdapters();
        setListeners();

        setEnteringNote(enteringNote);
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
                //notesFragmentHandler.noteChosen(position);
                chosenNote = notes.get(position);
                enteringNote = true;
                setEnteringNote(enteringNote);
            }
        });

        noteFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enteringNote){
                    if(chosenNote == null){
                        notesFragmentHandler.noteCreated(noteTitleTxt.getText().toString(), noteTextArea.getText().toString());
                    }else{
                        notesFragmentHandler.noteUpdated(chosenNote, noteTextArea.getText().toString(), noteTitleTxt.getText().toString());
                    }

                }else{
                    enteringNote = !enteringNote;
                    setEnteringNote(enteringNote);
                }


            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesFragmentHandler.closed();
            }
        });
    }

    private void setEnteringNote(boolean enteringNote){
        notesListView.setVisibility(enteringNote || notes.isEmpty() ? View.GONE : View.VISIBLE);
        notesListView.setEnabled(!enteringNote);
        noteFragmentBtn.setText(enteringNote ? "Save" : "New Note");
        noteTextArea.setVisibility(enteringNote ? View.VISIBLE : View.GONE);
        noteTextArea.setEnabled(enteringNote);
        noteTitleTxt.setVisibility(enteringNote ? View.VISIBLE : View.GONE);
        noteTitleTxt.setEnabled(enteringNote);

        if(enteringNote && chosenNote != null){
            noteTextArea.setText(chosenNote.getNotes());
            noteTitleTxt.setText(chosenNote.getTitle());
        }
    }
}
