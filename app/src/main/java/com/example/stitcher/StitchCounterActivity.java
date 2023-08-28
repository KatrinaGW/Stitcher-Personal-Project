package com.example.stitcher;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.controllers.CounterCollection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class StitchCounterActivity extends AppCompatActivity {
    private TextView countText;
    private Button addBtn;
    private Button subtractBtn;
    private Button backBtn;
    private TextView negativeErrorTxt;
    private EditText goalCounterValue;
    private int count = 0;
    private int countGoal;
    private FloatingActionButton tester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stitch_counter_activity);

        countText = findViewById(R.id.stitch_counter_value_txt);
        negativeErrorTxt = findViewById(R.id.counter_error_txt);
        setCountText();
        addBtn = findViewById(R.id.add_count_btn);
        subtractBtn = findViewById(R.id.subtract_count_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        goalCounterValue = findViewById(R.id.counter_goal_value);

        addBtn.setOnClickListener(v -> onChangeCount(1));
        subtractBtn.setOnClickListener(v -> onChangeCount(-1));
        backBtn.setOnClickListener(v -> onBackClicked());
        goalCounterValue.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                countGoal = Integer.parseInt(newText);

                setCountText();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        CounterCollection db = new CounterCollection();
    }

    private void onBackClicked(){
        startActivity(new Intent(StitchCounterActivity.this, MainActivity.class));
    }

    private void showError(){
        negativeErrorTxt.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                negativeErrorTxt.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void onChangeCount(int addend){
        if(count+addend < 0){
            showError();
        }else{
            count = count + addend;
            setCountText();
        }

    }

    private void setCountText(){
        countText.setText(Integer.toString(count));
        checkCount();
    }

    private void checkCount(){
        if(count == countGoal){
            countText.setTextColor(Color.GREEN);
        }

        if(count > countGoal){
            countText.setTextColor(Color.RED);
        }

        if(count < countGoal){
            countText.setTextColor(Color.BLACK);
        }
    }

}
