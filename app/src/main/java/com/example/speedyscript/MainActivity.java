package com.example.speedyscript;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView textPrompt, textResult;
    EditText editTextInput;
    Button buttonStart, buttonHistory;

    long startTime;
    CountDownTimer countDownTimer;
    boolean isTestRunning = false;

    String promptText = "";
    String[] promptArray = {
            "Improve your typing speed with this simple Android app!",
            "Practice typing every day to increase your skills.",
            "The quick brown fox jumps over the lazy dog.",
            "Coding is a skill that improves with consistent effort.",
            "Android development is fun and rewarding."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textPrompt = findViewById(R.id.textPrompt);
        textResult = findViewById(R.id.textResult);
        editTextInput = findViewById(R.id.editTextInput);
        buttonStart = findViewById(R.id.buttonStart);
        buttonHistory = findViewById(R.id.buttonHistory);

        editTextInput.setEnabled(false);

        buttonStart.setOnClickListener(v -> startTest());

        buttonHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void startTest() {
        // Pick random prompt
        int randomIndex = new Random().nextInt(promptArray.length);
        promptText = promptArray[randomIndex];
        textPrompt.setText(promptText);

        editTextInput.setText("");
        editTextInput.setEnabled(true);
        editTextInput.requestFocus();
        textResult.setText("");
        startTime = System.currentTimeMillis();
        isTestRunning = true;

        Toast.makeText(this, "Typing test started. Time: 60 seconds!", Toast.LENGTH_SHORT).show();
        startCountdown();

        // Optional: End test if user exits input
        editTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && isTestRunning) {
                endTest();
            }
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                buttonStart.setText("Time Left: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                if (isTestRunning) {
                    isTestRunning = false;
                    endTest();
                    buttonStart.setText("Start Typing Test");
                }
            }
        }.start();
    }

    private void endTest() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long endTime = System.currentTimeMillis();
        String userInput = editTextInput.getText().toString().trim();

        double timeTakenSec = (endTime - startTime) / 1000.0;
        if (timeTakenSec == 0) timeTakenSec = 1; // Avoid divide by zero

        int wordCount = userInput.isEmpty() ? 0 : userInput.split("\\s+").length;
        int correctWords = 0;

        String[] originalWords = promptText.trim().split("\\s+");
        String[] typedWords = userInput.split("\\s+");

        for (int i = 0; i < Math.min(originalWords.length, typedWords.length); i++) {
            if (originalWords[i].equalsIgnoreCase(typedWords[i])) {
                correctWords++;
            }
        }

        double wpm = (wordCount / timeTakenSec) * 60;
        double accuracy = (correctWords / (double) originalWords.length) * 100;

        String result = String.format(Locale.getDefault(),
                "Time: %.2f sec\nWPM: %.2f\nAccuracy: %.2f%%",
                timeTakenSec, wpm, accuracy);

        textResult.setText(result);
        editTextInput.setEnabled(false);
        isTestRunning = false;

        saveResultToDatabase(timeTakenSec, wpm, accuracy);
    }

    private void saveResultToDatabase(double time, double wpm, double accuracy) {
        TypingResultDatabaseHelper dbHelper = new TypingResultDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("INSERT INTO Results (timeTaken, wpm, accuracy) VALUES (?, ?, ?)",
                new Object[]{time, wpm, accuracy});

        db.close();
    }
}
