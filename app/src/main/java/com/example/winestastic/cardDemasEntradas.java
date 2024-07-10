package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class cardDemasEntradas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_demas_entradas);
    }

    private boolean handleBackPressed = false;

    @Override
    public void onBackPressed() {
        if (handleBackPressed) {
            super.onBackPressed();  // Llama a super.onBackPressed() solo si handleBackPressed es true
        } else {
            // Establece el resultado como RESULT_OK y finaliza la actividad
            setResult(RESULT_OK);
            finish();
        }
    }
}