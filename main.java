package com.example.screentimetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText timeLimitInput = findViewById(R.id.timeLimitInput);
        Button startTrackingButton = findViewById(R.id.startTrackingButton);

        startTrackingButton.setOnClickListener(view -> {
            String input = timeLimitInput.getText().toString();
            if (!input.isEmpty()) {
                int timeLimit = Integer.parseInt(input); // Time limit in minutes
                Intent intent = new Intent(MainActivity.this, ScreenTimeService.class);
                intent.putExtra("timeLimit", timeLimit);
                startService(intent);
                Toast.makeText(this, "Screen time tracking started!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid time limit!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
