package com.example.speedyscript;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textPrompt, textResult;
    EditText editTextInput;
    Button buttonStart;

    long startTime;
    String promptText = "Improve your typing speed with this simple Android app!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textPrompt = findViewById(R.id.textPrompt);
        textResult = findViewById(R.id.textResult);
        editTextInput = findViewById(R.id.editTextInput);
        buttonStart = findViewById(R.id.buttonStart);

        textPrompt.setText(promptText);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
    }

    private void startTest() {
        editTextInput.setText("");
        editTextInput.setEnabled(true);
        editTextInput.requestFocus();
        textResult.setText("");
        startTime = System.currentTimeMillis();

        Toast.makeText(this, "Typing test started. Begin typing now!", Toast.LENGTH_SHORT).show();

        // Detect when user finishes and presses DONE or outside the field
        editTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                endTest();
            }
        });
    }

    private void endTest() {
        long endTime = System.currentTimeMillis();
        String userInput = editTextInput.getText().toString().trim();

        double timeTakenSec = (endTime - startTime) / 1000.0;

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
    }
}
