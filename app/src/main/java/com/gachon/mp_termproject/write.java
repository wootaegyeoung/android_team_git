package com.gachon.mp_termproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class write extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        EditText titleEditText = findViewById(R.id.title);
        String title = titleEditText.getText().toString();



        TextView textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setText(title);

    }
}