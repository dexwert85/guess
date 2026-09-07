package com.example.numbergame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView score;
    private TextView timerText;
    private EditText enter;
    private Button submit;

    private int scoreCount = 0;
    private int num;

    private int seconds = 20;
    private int tries = 0;

    private Thread timerThread;
    private boolean timerRunning = true;

    private final Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        score = findViewById(R.id.score);
        timerText = findViewById(R.id.timer);
        enter = findViewById(R.id.pick);
        submit = findViewById(R.id.submit);

        // Generate random number from 1 to 20
        num = rand.nextInt(20) + 1;

        // Get current score
        scoreCount = getIntent().getIntExtra("scoreCount", 0);

        score.setText("Score: " + scoreCount);
        timerText.setText("Time: 20");

        startTimer();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = enter.getText().toString().trim();

                // Empty input does not count as a try
                if (input.isEmpty()) {
                    score.setText("Enter a number");
                    return;
                }

                int guess;

                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {

                    // Invalid input does not count as a try
                    score.setText("Enter a valid number");
                    enter.setText("");
                    return;
                }

                // Number outside the range does not count as a try
                if (guess < 1 || guess > 20) {

                    score.setText("Enter a number from 1 to 20");

                } else if (guess < num) {

                    // Wrong try
                    tries++;

                    score.setText("Higher");

                    checkTries();

                } else if (guess > num) {

                    // Wrong try
                    tries++;

                    score.setText("Lower");

                    checkTries();

                } else {

                    // Correct answer
                    timerRunning = false;

                    scoreCount++;

                    Intent intent =
                            new Intent(MainActivity.this, ScoreView.class);

                    intent.putExtra("scoreCount", scoreCount);
                    intent.putExtra("status", "win");

                    startActivity(intent);
                    finish();
                }

                enter.setText("");
            }
        });
    }

    private void checkTries() {

        if (tries >= 5) {

            // Stop timer
            timerRunning = false;

            // Lose round, but do NOT lose a point
            Intent intent =
                    new Intent(MainActivity.this, ScoreView.class);

            intent.putExtra("scoreCount", scoreCount);
            intent.putExtra("status", "lose");

            startActivity(intent);
            finish();
        }
    }

    private void startTimer() {

        timerThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (timerRunning && seconds > 0) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    if (!timerRunning) {
                        return;
                    }

                    seconds--;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerText.setText("Time: " + seconds);
                        }
                    });
                }

                // Time is up
                if (seconds == 0 && timerRunning) {

                    timerRunning = false;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Lose one point because time ran out
                            if (scoreCount > 0) {
                                scoreCount--;
                            }

                            Intent intent =
                                    new Intent(
                                            MainActivity.this,
                                            ScoreView.class
                                    );

                            intent.putExtra("scoreCount", scoreCount);
                            intent.putExtra("status", "lose");

                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        timerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the timer thread when leaving the activity
        timerRunning = false;

        if (timerThread != null) {
            timerThread.interrupt();
        }
    }
}
