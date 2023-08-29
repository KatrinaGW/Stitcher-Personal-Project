package com.example.stitcher;

import android.content.Intent;
import android.os.Bundle;

import com.example.stitcher.models.Counter;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.stitcher.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button stitchCounterButton;
    private Button allCountersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        stitchCounterButton = findViewById(R.id.stitch_counter_btn);
        allCountersButton = findViewById(R.id.all_counters);

        allCountersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CountersActivity.class));
            }
        });


        stitchCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("TESTING TRUE");
                startActivity(new Intent(MainActivity.this, StitchCounterActivity.class));
            }
        });
    }

}