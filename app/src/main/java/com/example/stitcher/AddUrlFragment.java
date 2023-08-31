package com.example.stitcher;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stitcher.controllers.handlers.UrlHandler;
import com.example.stitcher.models.Url;

import java.util.UUID;

public class AddUrlFragment extends Fragment {
    Button confirmBtn;
    Button cancelBtn;
    EditText urlTxt;
    AddUrlFragmentHandler fragmentHandler;
    TextView errorTxt;

    interface AddUrlFragmentHandler{
        void dismissFragment();
        void createNewUrl(Url url);
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

        if(context instanceof AddUrlFragmentHandler){
            fragmentHandler = (AddUrlFragmentHandler) context;
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
        errorTxt = getView().findViewById(R.id.new_url_error_txt);
        errorTxt.setVisibility(View.GONE);
    }

    private Boolean verifyInput(){
        String urlInput = String.valueOf(urlTxt.getText());

        return !urlInput.equals("");
    }

    private void setListeners(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentHandler.dismissFragment();
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
        UrlHandler urlHandler = new UrlHandler();

        if(verifyInput()){
            Url newUrl = new Url(UUID.randomUUID().toString(), urlTxt.getText().toString());
            fragmentHandler.createNewUrl(newUrl);
        }else{
            errorTxt.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorTxt.setVisibility(View.GONE);
                }
            }, 3000);
        }
    }
}
