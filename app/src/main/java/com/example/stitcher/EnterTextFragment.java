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


public class EnterTextFragment extends Fragment {
    Button confirmBtn;
    Button cancelBtn;
    EditText enterTxt;
    EnterTextFragmentHandler fragmentHandler;
    TextView errorTxt;
    int errorCode;
    int hintCode;

    interface EnterTextFragmentHandler {
        void dismissFragment();
        void createNew(String input);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        errorCode = this.getArguments().getInt(ViewConstants.FRAGMENT_ERROR_MSG.getValue());
        hintCode = this.getArguments().getInt(ViewConstants.FRAGMENT_HINT_MSG.getValue());

        return inflater.inflate(R.layout.fragment_enter_text,
                container, false);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof EnterTextFragmentHandler){
            fragmentHandler = (EnterTextFragmentHandler) context;
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        init();
        setListeners();
    }

    private void init(){
        enterTxt = getView().findViewById(R.id.enter_text_edittext);
        enterTxt.setHint(hintCode);
        confirmBtn = getView().findViewById(R.id.text_confirm_btn);
        cancelBtn = getView().findViewById(R.id.text_cancel_btn);
        errorTxt = getView().findViewById(R.id.txt_error_msg);
        errorTxt.setVisibility(View.GONE);
    }

    private Boolean verifyInput(String input){
        return !input.equals("");
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
        String inputTxt = enterTxt.getText().toString();

        if(verifyInput(inputTxt)){
            fragmentHandler.createNew(inputTxt);
        }else{
            errorTxt.setText(errorCode);
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
