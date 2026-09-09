package com.example.numbergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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
    private TextView scoreTv, timeTv;
    private EditText nameEt, pickEt;
    private Button submitBtn;
    private int score, guess, num, tries;
    private final Random rand = new Random();
    private CountDownTimer countDownTimer;
    private boolean isTimerStarted = false;

    private void initViews() {
        scoreTv = findViewById(R.id.score);
        timeTv = findViewById(R.id.timer);
        nameEt = findViewById(R.id.name);
        pickEt = findViewById(R.id.pick);
        submitBtn = findViewById(R.id.submit);
    }

    private void initValues() {
        num = rand.nextInt(20) + 1;
        score = getIntent().getIntExtra("score", 0);
        scoreTv.setText("Score: " + score);
    }

    private void saveHighScore() {
        String playerName = nameEt.getText().toString().trim();
        if (playerName.isEmpty()) {
            playerName = "Anonymous";
        }

        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int savedHighScore = prefs.getInt("high_score", 0);

        if (score > savedHighScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("high_score", score);
            editor.putString("high_score_name", playerName);
            editor.apply();
        }
    }

    private void checkTries() {
        if (tries >= 5) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            score--;

            saveHighScore();
            Intent intent = new Intent(MainActivity.this, ScoreView.class);
            intent.putExtra("score", score);
            intent.putExtra("status", "lose");
            startActivity(intent);
            finish();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                timeTv.setText("Time: " + secondsLeft);
            }

            @Override
            public void onFinish() {
                timeTv.setText("Time: 0");
                if (score > 0) {
                    score--;
                }
                saveHighScore();

                Intent intent = new Intent(MainActivity.this, ScoreView.class);
                intent.putExtra("score", score);
                intent.putExtra("status", "lose");
                startActivity(intent);
                finish();
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initValues();

        pickEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTimerStarted && s.length() > 0 && !nameEt.getText().toString().isEmpty()) {
                    isTimerStarted = true;
                    startTimer();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = pickEt.getText().toString().trim();
                if (input.isEmpty()) {
                    scoreTv.setText("Enter a number");
                    return;
                }

                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    scoreTv.setText("Enter a valid number");
                    pickEt.setText("");
                    return;
                }

                if (guess < 1 || guess > 20) {
                    scoreTv.setText("Enter a number from 1 to 20");
                } else if (guess < num) {
                    tries++;
                    scoreTv.setText("Higher");
                    checkTries();
                } else if (guess > num) {
                    tries++;
                    scoreTv.setText("Lower");
                    checkTries();
                } else {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }

                    score += 2;
                    saveHighScore();

                    Intent intent = new Intent(MainActivity.this, ScoreView.class);
                    intent.putExtra("score", score);
                    intent.putExtra("status", "win");
                    startActivity(intent);
                    finish();
                }
                pickEt.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}