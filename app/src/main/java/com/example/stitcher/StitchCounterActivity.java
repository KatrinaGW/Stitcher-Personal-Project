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
import com.example.stitcher.models.Counter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class StitchCounterActivity extends AppCompatActivity {
    private TextView countText;
    private Button addBtn;
    private Button subtractBtn;
    private Button backBtn;
    private TextView negativeErrorTxt;
    private EditText goalCounterValue;

    private Counter counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stitch_counter_activity);

        Intent intent = getIntent();

        counter = intent.getParcelableExtra("selectedCounter");
        if(counter == null){
            counter = new Counter(UUID.randomUUID().toString(), 0, 0, "Testing");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        findElements();
        setTexts();
        setListeners();
    }

    private void setListeners(){
        addBtn.setOnClickListener(v -> onChangeCount(1));
        subtractBtn.setOnClickListener(v -> onChangeCount(-1));
        backBtn.setOnClickListener(v -> onBackClicked());
        goalCounterValue.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                counter.setGoal(Integer.parseInt(newText));

                setCountText();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setTexts(){
        setCountText();
        goalCounterValue.setText(String.valueOf(counter.getGoal()));
    }

    private void findElements(){
        countText = findViewById(R.id.stitch_counter_value_txt);
        negativeErrorTxt = findViewById(R.id.counter_error_txt);
        addBtn = findViewById(R.id.add_count_btn);
        subtractBtn = findViewById(R.id.subtract_count_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        goalCounterValue = findViewById(R.id.counter_goal_value);
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
        if(counter.getCount()+addend < 0){
            showError();
        }else{
            counter.addToCount(addend);
            setCountText();
        }

    }

    private void setCountText(){
        countText.setText(Integer.toString(counter.getCount()));
        checkCount();
    }

    private void checkCount(){
        if(counter.getCount() == counter.getGoal()){
            countText.setTextColor(Color.GREEN);
        }

        if(counter.getCount() > counter.getGoal()){
            countText.setTextColor(Color.RED);
        }

        if(counter.getCount() < counter.getGoal()){
            countText.setTextColor(Color.BLACK);
        }
    }

}
