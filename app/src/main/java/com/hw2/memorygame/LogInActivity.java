package com.hw2.memorygame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * HW2
 * Roma Solovyov
 */
public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button startGameButton;
    private EditText nameBox;
    private EditText ageBox;
    private Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        startGameButton = findViewById(R.id.logIn_button);
        nameBox = findViewById(R.id.name_box);
        ageBox = findViewById(R.id.age_box);

        startGameButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (nameBox.getText().toString().isEmpty() || ageBox.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill out your name and age", Toast.LENGTH_SHORT).show();
            return;
        }

        intent = new Intent(this, MenuActivity.class);
        intent.putExtra("name", nameBox.getText().toString());
        intent.putExtra("age", Integer.valueOf(ageBox.getText().toString()));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
