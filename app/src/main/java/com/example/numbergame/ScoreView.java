package com.example.numbergame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreView extends AppCompatActivity {

    private TextView result;
    private TextView statusText;
    private Button restartBtn;
    private Button exitBtn;

    private int scoreCount;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score_view);

        // Connect Java variables to XML views
        result = findViewById(R.id.result);
        statusText = findViewById(R.id.textView);
        restartBtn = findViewById(R.id.restart);
        exitBtn = findViewById(R.id.exit);

        // Get score
        scoreCount = getIntent().getIntExtra("scoreCount", 0);

        // Get status
        status = getIntent().getStringExtra("status");

        // Display score
        result.setText("Score: " + scoreCount);

        // Display Win or Lose
        if ("win".equals(status)) {
            statusText.setText("Win!");
        } else if ("lose".equals(status)) {
            statusText.setText("Lose!");
        } else {
            statusText.setText("Game Over");
        }

        // Restart button
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =
                        new Intent(ScoreView.this, MainActivity.class);

                // Keep the score when restarting
                intent.putExtra("scoreCount", scoreCount);

                startActivity(intent);
                finish();
            }
        });

        // Exit button
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
}
