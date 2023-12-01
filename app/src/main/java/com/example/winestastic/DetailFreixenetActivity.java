package com.example.winestastic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailFreixenetActivity extends AppCompatActivity {
    private TextView titleText, addressText, textDescription;
    private ImageView vinedoImg;
    int contador;
    Button boton01, boton02;
    TextView cajaDeTexto;
    FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador = 0;
        setContentView(R.layout.activity_detailfreixenet);
        mFirestore = FirebaseFirestore.getInstance();
        titleText = findViewById(R.id.titleText);
        textDescription = findViewById(R.id.textDescription);
        addressText = findViewById(R.id.addressText);
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
        Intent intent = getIntent();
        String name = intent.getExtras().getString("titleTxt");

        mFirestore.collection("eventos").whereEqualTo("nombre_evento",name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String nombre = document.getString("nombre_vinedos");
                        String descripcion = document.getString("descripcion");
                        String ubicacion = document.getString("ubicacion_evento");

                        titleText.setText(nombre);
                        textDescription.setText(descripcion);
                        addressText.setText(ubicacion);


                    }
                }
            }
        });

    }
    //métodos




}