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

        result = findViewById(R.id.result);
        statusText = findViewById(R.id.textView);
        restartBtn = findViewById(R.id.restart);
        exitBtn = findViewById(R.id.exit);
        scoreCount = getIntent().getIntExtra("score", 0);
        status = getIntent().getStringExtra("status");
        result.setText("Score: " + scoreCount);

        if ("win".equals(status)) {
            statusText.setText("Win!");
        } else if ("lose".equals(status)) {
            statusText.setText("Lose!");
        } else {
            statusText.setText("Game Over");
        }

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =
                        new Intent(ScoreView.this, MainActivity.class);
                intent.putExtra("score", scoreCount);

                startActivity(intent);
                finish();
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
}