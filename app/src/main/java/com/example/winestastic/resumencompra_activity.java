package com.example.winestastic;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Token;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class resumencompra_activity extends AppCompatActivity {
    private VideoView vv1;
    private TextView nombreEventoTextView, numberOfTicketsTextView, totalPriceTextView;
    private Button btnTokenize;
    private TextView outputLabel;
    private EditText cardNumber;
    private EditText cardHolder;
    private EditText cvcNumber;
    private EditText month;
    private EditText year;
    private Activity activity = this;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumencompra);

        vv1 = findViewById(R.id.vv1);
        totalPriceTextView = findViewById(R.id.cash);
        numberOfTicketsTextView = findViewById(R.id.cash);
        outputLabel = findViewById(R.id.outputLabel);
        btnTokenize = findViewById(R.id.btnTokenize);
        cardHolder = findViewById(R.id.cardHolder);
        cardNumber = findViewById(R.id.cardNumber);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        cvcNumber = findViewById(R.id.cvcNumber);

        btnTokenize.setOnClickListener(view -> {

            Card card = new Card(cardHolder.getText().toString(),
                    cardNumber.getText().toString(),
                    cvcNumber.getText().toString(),
                    month.getText().toString(),
                    year.getText().toString());


            Token token = new Token(activity);


            token.onCreateTokenListener(new Token.CreateToken() {
                @Override
                public void onCreateTokenReady(JSONObject data) {
                    try {
                        // Imprimir el JSON completo para depurar
                        outputLabel.setText("Token response: " + data.toString());

                        // Acceder al campo 'id'
                        String tokenId = data.getString("id");
                        outputLabel.setText("Token id: " + tokenId);

                        /* Obtener el ID de la compra desde el Intent
                        String purchaseId = getIntent().getStringExtra("compraId");*/

                        /*Enviar el token y el ID de la compra al método enviarTokenAlBackend
                        enviarTokenAlBackend(tokenId, purchaseId);*/

                    } catch (Exception error) {
                        outputLabel.setText("Error: " + error.toString());
                    }
                }

               /* private void enviarTokenAlBackend(String tokenId, String purchaseId) {
                    new Thread(() -> {
                        try {
                            // URL de tu endpoint en el servidor backend
                            URL url = new URL("https://your-backend-url.com/process_payment");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json; utf-8");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setDoOutput(true);

                            // Crear el cuerpo de la solicitud en formato JSON
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("token", tokenId);
                            jsonParam.put("purchaseId", purchaseId);

                            // Escribir los datos en el flujo de salida
                            try (OutputStream os = connection.getOutputStream()) {
                                byte[] input = jsonParam.toString().getBytes("utf-8");
                                os.write(input, 0, input.length);
                            }

                            // Leer la respuesta del servidor
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                                StringBuilder response = new StringBuilder();
                                String responseLine;
                                while ((responseLine = br.readLine()) != null) {
                                    response.append(responseLine.trim());
                                }
                                // Mostrar la respuesta del servidor
                                runOnUiThread(() -> {
                                    outputLabel.setText("Response from server: " + response.toString());
                                });
                            }
                        } catch (Exception e) {
                            // Manejar excepciones
                            runOnUiThread(() -> {
                                outputLabel.setText("Error sending token to backend: " + e.toString());
                            });
                        }
                    }).start();
                }*/
            });
            token.create(card);//Create token
        });

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

        // Inicializa Conekta
        Conekta.setPublicKey("key_E3HEgzhNf2NVDXJtmbqevzK");
        Conekta.setApiVersion("2.0.0");
        Conekta.collectDevice(this);
    }


        private void cargarDatosCompra (String purchaseId){
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
        public boolean onSupportNavigateUp () {
            onBackPressed();
            return true;
        }
    }