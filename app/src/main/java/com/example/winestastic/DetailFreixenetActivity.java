package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.os.Bundle;

public class DetailFreixenetActivity extends AppCompatActivity {
    private TextView titleText, addressText, textDescription;
    private ImageView vinedoImg;
    int contador;
    Button boton01, boton02;
    TextView cajaDeTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador = 0;
        setContentView(R.layout.activity_detailfreixenet);

        boton01 = findViewById(R.id.botonRestar);
        boton01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador --;
                cajaDeTexto.setText(Integer.toString(contador));
            }
        });
        boton02 = findViewById(R.id.botonSumar);
        boton02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador ++;
                cajaDeTexto.setText(Integer.toString(contador));
            }
        });
        cajaDeTexto = findViewById(R.id.textcont);
        cajaDeTexto.setText(Integer.toString(contador));

    }
    //métodos
}