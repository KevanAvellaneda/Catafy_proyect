package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

public class cardDemasEntradas extends ImageSliderActivity {



    List<Versions> versionsList;
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // name of document that contains the images for slider
        nameImageDocument = "entradas";
        super.onCreate(savedInstanceState);

        //incrustar activity contact
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_tasting, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);
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


    private void setRecyclerView() {
        VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
    }

}