package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class cardCatadeVinos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_catade_vinos);
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(cardCatadeVinos.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}