package com.example.stitcher;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AddUrlFragment extends Fragment {
    Button confirmBtn;
    Button cancelBtn;
    EditText urlTxt;
    AddUrlFragmentDismisser dismisser;

    interface AddUrlFragmentDismisser{
        void dismissFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_add_url,
                container, false);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof AddUrlFragmentDismisser){
            dismisser = (AddUrlFragmentDismisser) context;
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        init();
        setListeners();
    }

    private void init(){
        urlTxt = getView().findViewById(R.id.add_url_edittext);
        confirmBtn = getView().findViewById(R.id.url_confirm_btn);
        cancelBtn = getView().findViewById(R.id.url_cancel_btn);
    }

    private void setListeners(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisser.dismissFragment();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClicked();
            }
        });
    }

    private void onConfirmClicked(){
        /**
         * Logic to confirm new URL
         *
         * dismisser.dismissFragment();
         */
    }


}
