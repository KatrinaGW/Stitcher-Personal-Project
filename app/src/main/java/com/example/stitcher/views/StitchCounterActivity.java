package com.example.stitcher.views;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.R;
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.handlers.CounterHandler;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;

import java.util.UUID;
import java.util.function.Function;

public class StitchCounterActivity extends AppCompatActivity {
    private TextView countText;
    private Button addBtn;
    private Button subtractBtn;
    private Button backBtn;
    private TextView negativeErrorTxt;
    private TextView savedTxt;
    private EditText goalCounterValue;
    private EditText counterNameValue;
    private Button saveBtn;
    private Button deleteBtn;

    private Counter counter;
    private Project parentProject;
    private boolean isNew;
    private Url backNavigateUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stitch_counter_activity);

        Intent intent = getIntent();

        counter = intent.getParcelableExtra(ViewConstants.SELECTED_COUNTER.getValue());
        isNew = counter == null;
        if(isNew){
            counter = new Counter(UUID.randomUUID().toString(), 0, 0, "");
        }

        parentProject = intent.getParcelableExtra(ViewConstants.PARENT_PROJECT.getValue());
        backNavigateUrl = intent.getParcelableExtra(ViewConstants.BACK_NAVIGATE_URL.getValue());
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
        backBtn.setOnClickListener(v -> {
            onBackClicked();
        });
        goalCounterValue.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();

                if(!newText.equals("")){
                    counter.setGoal(Integer.parseInt(newText));
                }else{
                    counter.setGoal(0);
                }


                setCountText();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        counterNameValue.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                counter.setName(newText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CounterHandler.deleteCounter(counter, parentProject)
                            .thenAccept(success ->
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(success){
                                                Intent projectIntent = new Intent(StitchCounterActivity.this, DisplayProject.class);
                                                projectIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), parentProject);
                                                startActivity(projectIntent);
                                            }else{
                                                Log.e(TAG, "Something went wrong when deleting the counter!");
                                            }
                                        }
                                    }))
                            .exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    Log.e(TAG, "ERROR", throwable);
                                    return null;
                                }
                            });

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyInput()){
                    if(!isNew){
                        CounterHandler.saveCounterState(counter)
                                .thenAccept(success -> {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeKeyboardIfOpen();
                                            showSavedMsg();
                                        }
                                    });
                                })
                                .exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        return null;
                                    }
                                });
                    }else{
                        CounterHandler.createNewCounter(counter, parentProject)
                                        .thenAccept(success -> {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    closeKeyboardIfOpen();
                                                    showSavedMsg();
                                                }
                                            });
                                        })
                                .exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        Log.e(TAG, "ERROR", throwable);
                                        return null;
                                    }
                                });

                    }
                }else{
                    closeKeyboardIfOpen();

                    showError(R.string.no_name_error);
                }

            }
        });
    }

    private void closeKeyboardIfOpen(){
        View view = getCurrentFocus();

        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean verifyInput(){
        return !counter.getName().equals("");
    }

    private void setTexts(){
        setCountText();
        goalCounterValue.setText(String.valueOf(counter.getGoal()));
        counterNameValue.setText(counter.getName());
    }

    private void findElements(){
        countText = findViewById(R.id.stitch_counter_value_txt);
        negativeErrorTxt = findViewById(R.id.counter_error_txt);
        addBtn = findViewById(R.id.add_count_btn);
        subtractBtn = findViewById(R.id.subtract_count_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        goalCounterValue = findViewById(R.id.counter_goal_value);
        counterNameValue = findViewById(R.id.counter_label_value);
        saveBtn = findViewById(R.id.counter_save_btn);
        deleteBtn = findViewById(R.id.counter_delete_btn);
        savedTxt = findViewById(R.id.saved_txt);

        deleteBtn.setVisibility(isNew ? View.GONE : View.VISIBLE);
    }

    private void onBackClicked(){
        Intent newIntent;
        if(backNavigateUrl == null){
            newIntent = new Intent(StitchCounterActivity.this, DisplayProject.class);
            newIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), parentProject);
        }else{
            newIntent = new Intent(StitchCounterActivity.this, UrlWebviewActivity.class);
            newIntent.putExtra(ViewConstants.PARENT_PROJECT.getValue(), parentProject);
            newIntent.putExtra(ViewConstants.SELECTED_URL.getValue(), backNavigateUrl);
        }

        startActivity(newIntent);
    }

    private void showError(int messageId){
        negativeErrorTxt.setText(messageId);
        negativeErrorTxt.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                negativeErrorTxt.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void showSavedMsg(){
        savedTxt.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                savedTxt.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void onChangeCount(int addend){
        if(counter.getCount()+addend < 0){
            showError(R.string.negative_count_error);
        }else{
            counter.addToCount(addend);
            setCountText();
        }

    }

    private void setCountText(){
        countText.setText(Integer.toString(counter.getCount()));
        countText.setTextColor(HelperFunctions.getCounterColourCode(counter));
    }

}
