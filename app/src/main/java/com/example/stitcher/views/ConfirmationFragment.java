package com.example.stitcher.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stitcher.R;
import com.example.stitcher.constants.ViewConstants;

public class ConfirmationFragment extends Fragment {
    private ConfirmationFragmentHandler fragmentHandler;
    private String header;
    private String confirmLabel;
    private String cancelLabel;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView headerTxt;

    interface ConfirmationFragmentHandler {
        void confirmed();
        void cancelled();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        header = this.getArguments().getString(ViewConstants.FRAGMENT_HEADER.getValue());
        confirmLabel = this.getArguments().getString(ViewConstants.FRAGMENT_CONFIRM_LABEL.getValue());
        cancelLabel = this.getArguments().getString(ViewConstants.FRAGMENT_CANCEL_LABEL.getValue());

        return inflater.inflate(R.layout.confirmation_fragment,
                container, false);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if(context instanceof ConfirmationFragment.ConfirmationFragmentHandler){
            fragmentHandler = (ConfirmationFragment.ConfirmationFragmentHandler) context;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        init();
    }

    private void setListeners(){
        this.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentHandler.confirmed();
            }
        });

        this.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentHandler.cancelled();
            }
        });
    }

    private void init(){
        Activity activity = this.getActivity();
        this.headerTxt = activity.findViewById(R.id.confirmation_diaglogue_header);
        this.confirmBtn = activity.findViewById(R.id.confirm_button);
        this.cancelBtn = activity.findViewById(R.id.cancel_button);

        this.headerTxt.setText(header);
        this.confirmBtn.setText(confirmLabel);
        this.cancelBtn.setText(cancelLabel);

        setListeners();
    }
}
