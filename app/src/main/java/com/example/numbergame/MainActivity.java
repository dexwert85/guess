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
    private EditText enter;
    private Button submit;

    private int scoreCount = 0;
    private int num;

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

        // Connect Java variables to XML views
        score = findViewById(R.id.score);
        enter = findViewById(R.id.pick);
        submit = findViewById(R.id.submit);

        // Generate random number from 1 to 20
        num = rand.nextInt(20) + 1;

        // Get previous score
        scoreCount = getIntent().getIntExtra("scoreCount", 0);

        score.setText("Score: " + scoreCount);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = enter.getText().toString().trim();

                if (input.isEmpty()) {
                    score.setText("Enter a number");
                    return;
                }

                int guess;

                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    score.setText("Enter a valid number");
                    enter.setText("");
                    return;
                }

                if (guess < 1 || guess > 20) {

                    score.setText("Enter a number from 1 to 20");

                } else if (guess < num) {

                    score.setText("Higher");

                } else if (guess > num) {

                    score.setText("Lower");

                } else {

                    // Player wins
                    scoreCount++;

                    Intent intent =
                            new Intent(MainActivity.this, ScoreView.class);

                    // Send score
                    intent.putExtra("scoreCount", scoreCount);

                    // Send status
                    intent.putExtra("status", "win");

                    startActivity(intent);
                    finish();
                }

                enter.setText("");
            }
        });
    }
}