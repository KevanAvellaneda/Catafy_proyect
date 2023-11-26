package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class chatbot extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Versions> versionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerView);

        initData();
        setRecyclerView();
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(chatbot.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setRecyclerView() {
        VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        versionsList = new ArrayList<>();

        versionsList.add(new Versions("Primer pregunta frecuente", "Aqui ira la primer descripcion de la pregunta frecuente que depende la respuesta estara el texto"));
        versionsList.add(new Versions("Segunda pregunta frecuente", "Aqui ira la primer descripcion de la pregunta frecuente que depende la respuesta estara el texto"));
        versionsList.add(new Versions("Hola", "ajasldfjasldfjsaldfjksdfljsdffljk"));
        versionsList.add(new Versions("Estoy cansado jefe", "estoy cansado de tanto correr en la nieve como un gorrion asustado"));
        versionsList.add(new Versions("Esto es una prueba", "Realmente es un texto al azar, no le encuentro logica"));
        versionsList.add(new Versions("Por que sigues leyendo?", "Vete a comer o algo, ya son las 3 casi las 4"));


    }
}