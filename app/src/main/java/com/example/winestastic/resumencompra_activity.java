package com.example.winestastic;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class resumencompra_activity extends AppCompatActivity {
    private VideoView vv1;
    private TextView nombreEventoTextView, numberOfTicketsTextView, totalPriceTextView;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumencompra);

        vv1 = findViewById(R.id.vv1);
        numberOfTicketsTextView = findViewById(R.id.cash);
        totalPriceTextView = findViewById(R.id.cash);
        vv1.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videoxd));
        vv1.start();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Mostrar el botón para regresar y eliminar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener el ID de la compra desde el Intent
        String purchaseId = getIntent().getStringExtra("compraId");

        // Cargar los datos de la compra usando el ID
        cargarDatosCompra(purchaseId);
    }

    private void cargarDatosCompra(String purchaseId) {
        if (purchaseId != null) {
            db.collection("resumen").document(purchaseId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        int numberOfTickets = document.getLong("numeroTickets").intValue();
                        double totalPrice = document.getDouble("precioTotal");

                        numberOfTicketsTextView.setText(String.valueOf(numberOfTickets));
                        totalPriceTextView.setText(String.format("$%.2f", totalPrice));
                    } else {
                        Toast.makeText(resumencompra_activity.this, "No se encontraron datos de la compra", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(resumencompra_activity.this, "Error al cargar los datos de la compra", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "ID de compra no válido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}