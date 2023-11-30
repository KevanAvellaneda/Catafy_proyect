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

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(cardDemasEntradas.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}