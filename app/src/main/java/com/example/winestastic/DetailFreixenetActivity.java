package com.example.winestastic;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailFreixenetActivity extends AppCompatActivity {
    private TextView titleText, addressText, textDescription, horarioTextView;
    private TextView calificacionScore, calificacionTotal;
    private RatingBar calificacionBar;
    private FirebaseFirestore mFirestore;
    private String idEvento;
    private RecyclerView recyclerViewComentarios;
    private OpinionAdapter comentarioAdapter;
    private List<Opinion> opinionesList;

    // Para cargar las imagenes, este es de Kevan
    private RecyclerView imagesRecycler1;
    private List<String> items;
    private int totalCalificaciones;
    private float promedioCalificaciones;
    private boolean enviandoComentario = false;
    //////

    LinearLayout ubicacionD2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfreixenet);

        ubicacionD2 = findViewById(R.id.ubicacionD);

        // Inicializamos Firestore
        mFirestore = FirebaseFirestore.getInstance();

        //Aquí encontramos las referencias a los elementos de la interfaz de usuario
        titleText = findViewById(R.id.titleText);
        textDescription = findViewById(R.id.textDescription);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        addressText = findViewById(R.id.addressText);
        ImageView vinedoImg = findViewById(R.id.vinedoImg);

        horarioTextView = findViewById(R.id.horarioTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));

        EditText editTextComentario = findViewById(R.id.editTextComentario);
        RatingBar ratingBarOpinion = findViewById(R.id.ratingBarOpinion);
        Button botonEnviarOpinion = findViewById(R.id.botonEnviarOpinion);
        Button botonMostrarComentarios = findViewById(R.id.verMasComentariosButton);


        /*Para cargar las imagenes en el recycler view(Kevan)
        imagesRecycler1 = findViewById(R.id.imagesRecycler);
        imagesRecycler1.setHasFixedSize(true);
        imagesRecycler1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        items = new ArrayList<>();
        itemsAdapterHistoria = new ItemsAdapterHistoria(items, this);
        imagesRecycler1.setAdapter(itemsAdapterHistoria);*/

        // Inicializamos la lista de opiniones y también su adaptador
        opinionesList = new ArrayList<>();
        comentarioAdapter = new OpinionAdapter(opinionesList);
        recyclerViewComentarios.setAdapter(comentarioAdapter);

        // Calificación general
        calificacionScore = findViewById(R.id.calificacionLugarScore);
        calificacionTotal = findViewById(R.id.calificacionLugarTotal);
        calificacionBar = findViewById(R.id.calificacionLugarBar);

        // Obtenemos el ID del viñedo actual
        idEvento = obteneridEvento();

        //cargarImagenesDesdeFirestore(idEvento);

        // Obtenemos el ID del usuario ya autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        // Obtenemos y mostramos las opiniones que hay
        obtenerYMostrarOpiniones();
        configSwipe();

        // Botón de enviar opinión
        botonEnviarOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarOpinion(editTextComentario, ratingBarOpinion, userId, idEvento);
            }
        });
        // Obtenemos el nombre del viñedo
        obtenerInformacionEvento();

        botonMostrarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentsActivity();
            }
        });

        ubicacionD2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailFreixenetActivity.this, MainActivity.class);
                intent.putExtra("selectedItemId", 5); // Selecciona el ítem con el ID 5
                intent.putExtra("markerTitle", titleText.getText());
                //con esta linea limpiamos las actividades para que no se muestren mas que una sola en lugar de cada que abramos un lugar
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //Mostrar el botón para regresar y eliminar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void configSwipe() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Simulamos una actualización de 2 segundos
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        // Refrescar la actividad actual
                        recreate();
                    }
                }, 600);
            }
        });
        // Configurar el menú de favoritos
        invalidateOptionsMenu();
    }

    /* Cargar las imagenes en el recyclerview desde firestore
    private void cargarImagenesDesdeFirestore(String lugarId) {
        mFirestore.collection("imagesall")
                .whereEqualTo("idEvento", lugarId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener la URL de la imagen de cada documento y agregarla a la lista
                            String imageUrl = document.getString("url");
                            items.add(imageUrl);
                        }
                        // Notificar al adaptador sobre el cambio en los datos
                        itemsAdapterHistoria.notifyDataSetChanged();
                    } else {
                    }
                });
    }*/

    // Método para obtener y mostrar las opiniones que hay
    private void obtenerYMostrarOpiniones() {
        mFirestore.collection("opiniones")
                .whereEqualTo("idEvento", idEvento)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Reiniciamos la lista de opiniones
                        opinionesList.clear();
                        // Reiniciamos los valores del total y promedio de opiniones
                        totalCalificaciones = 0;
                        promedioCalificaciones = 0;

                        // Iteramos sobre los documentos de la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extrae campos del documento
                            String idUsuario = document.getString("idUsuario");
                            String comentario = document.getString("comentario");
                            float calificacion = document.getDouble("calificacion").floatValue();
                            //Timestamp fecha = (Timestamp) document.getData().get("timestamp");
                            Date fecha = document.getDate("timestamp");

                            totalCalificaciones += 1;
                            promedioCalificaciones += calificacion;

                            // Condición para solo mostrar primeros 3 comentarios
                            if(totalCalificaciones <= 3) {
                                // Crea un nuevo objeto Opinion con los datos del documento
                                Opinion nuevaOpinion = new Opinion(idUsuario, comentario, calificacion, idEvento, null, fecha);

                                // Agrega la nueva opinión a la lista
                                opinionesList.add(nuevaOpinion);

                            }
                        }

                        // Obtener y mostrar promedio general
                        promedioCalificaciones = promedioCalificaciones / totalCalificaciones;
                        String promedio = String.format("%.1f", promedioCalificaciones);
                        calificacionScore.setText(promedio);
                        calificacionBar.setRating(promedioCalificaciones);
                        calificacionTotal.setText(totalCalificaciones + "");

                        // Verificar si la caja de comentarios está vacía
                        verificarComentariosVacios();

                        // Notifica al adaptador que los datos han cambiado
                        comentarioAdapter.notifyDataSetChanged();
                    } else {
                        // Manejo de error en caso de fallo en la consulta
                        Log.e("DetailFreixenetActivity", "Error al obtener las opiniones", task.getException());
                    }
                });
    }

    // Método para enviar una opinión
    private void enviarOpinion(EditText editTextComentario, RatingBar ratingBarOpinion, String userId, String idEvento) {
        try {
            // Obtenemos los datos de la interfaz de usuario
            String comentario = editTextComentario.getText().toString();
            float calificacion = ratingBarOpinion.getRating();

            // Validamos los campos
            if (comentario.isEmpty()) {
                Toast.makeText(DetailFreixenetActivity.this, "Por favor, ingrese su comentario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validamos los campos
            if (calificacion < 1.0) {
                Toast.makeText(DetailFreixenetActivity.this, "Por favor, establezca un puntaje", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificamos el ID de usuario antes de enviar la opinión
            if (userId != null) {
                // Consultamos en Firestore para verificar si el usuario ya ha dejado un comentario hoy en ese puesto
                verificarComentarioHoyEnEvento(editTextComentario, ratingBarOpinion, userId, idEvento, comentario, calificacion);
            }
        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            Toast.makeText(DetailFreixenetActivity.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Este Método es para verificar si el usuario ya ha dejado un comentario en un puesto el día de hoy
    private void verificarComentarioHoyEnEvento(EditText editTextComentario, RatingBar ratingBarOpinion, String userId, String idEvento, String comentario, float calificacion) {
        if (enviandoComentario) {
            // Si ya se está enviando un comentario, no hagas nada
            return;
        }
        enviandoComentario = true;

        // Obtenemos la fecha actual
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date truncatedDate = calendar.getTime();
        // Convertimos la fecha actual a un formato que Firestore entienda
        Timestamp fechaActualFirestore = new Timestamp(truncatedDate);

        // Consultamos en Firestore para ver si el usuario ya ha dejado un comentario hoy en ese puesto
        mFirestore.collection("opiniones")
                .whereEqualTo("idUsuario", userId)
                .whereEqualTo("idEvento", idEvento)
                .whereGreaterThanOrEqualTo("timestamp", fechaActualFirestore)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Contador de comentarios del usuario (hoy)
                        // Obtenemos el número de elementos en el resultado de la consulta a Firestore
                        int comentariosHoy = task.getResult().size();

                        // Verificamos si el usuario ha dejado el máximo de comentarios permitidos por hoy
                        if (comentariosHoy >= 1) {
                            // Mostramos un mensaje de límite de comentarios
                            Toast.makeText(DetailFreixenetActivity.this, "Has alcanzado el límite de comentarios por hoy", Toast.LENGTH_SHORT).show();
                        } else {
                            // Si no ha alacanzado el limite de comentarios le permitimos al usuario enviar un nuevo comentario
                            // Consultamos en Firestore para obtener el id del usuario
                            mFirestore.collection("usuarios")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // Extraemos el id de usuario
                                            String idUsuario = documentSnapshot.getString("id");

                                            // Creamos un nuevo objeto Opinion
                                            Opinion nuevaOpinion = new Opinion(idUsuario, comentario, calificacion, null, idEvento);

                                            // Agregamos la nueva opinión a la colección de opiniones en Firestore
                                            mFirestore.collection("opiniones")
                                                    .add(nuevaOpinion)
                                                    .addOnSuccessListener(documentReference -> {
                                                        enviandoComentario = false;
                                                        Toast.makeText(DetailFreixenetActivity.this, "Opinión enviada con éxito", Toast.LENGTH_SHORT).show();

                                                        // Limpiamos los campos de la interfaz de usuario después de enviar una nueva opinión
                                                        editTextComentario.setText("");
                                                        ratingBarOpinion.setRating(0.0f);

                                                        // Actualizamos y mostramos las opiniones después de enviar una nueva
                                                        obtenerYMostrarOpiniones();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Mensaje de error en caso de fallo al enviar la opinión
                                                        enviandoComentario = false;
                                                        Toast.makeText(DetailFreixenetActivity.this, "Error al enviar la opinión", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Manejo de errores
                                        Log.e("DetailFreixenetActivity", "Error al obtener el documento del usuario", e);
                                    });
                        }
                    } else {
                        // Manejo de errores
                        Log.e("DetailFreixenetActivity", "Error al verificar comentario en puesto", task.getException());
                    }
                });
    }

    // Método para obtener la información de los eventos ?
    private void obtenerInformacionEvento() {
        // Obtenemos el nombre de los viñedos desde la intención
        Intent intent = getIntent();
        String name = (intent != null) ? intent.getStringExtra("titleTxt") : null;
        idEvento = obteneridEvento(); // Asegúrate de obtener el idEvento correctamente

        Log.d("DetailFreixenetActivity", "Nombre del evento recibido: " + name);
        Log.d("DetailFreixenetActivity", "ID de evento obtenido: " + idEvento);

        // Verificamos la existencia del nombre y que idEvento no sea vacío
        if (name != null && !idEvento.isEmpty()) {
            // Consultamos en Firestore para obtener información de los viñedos
            mFirestore.collection("eventos").document(idEvento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Extraemos los campos del documento
                            String nombre = document.getString("nombre_evento");
                            String info = document.getString("descripcion");
                            String ubicacion = document.getString("ubicacion_evento");
                            Timestamp horario = document.getTimestamp("fecha_eventoo");
                            String imageUrl = document.getString("url");

                            // Convertimos el Timestamp a un String con un formato de fecha específico para los Eventos
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy 'a las' HH:mm:ss a");
                            String horarioStr = sdf.format(horario.toDate());
                            horarioTextView.setText(horarioStr);
                            // Configuramos los elementos de la interfaz de usuario con la información obtenida
                            Log.d("MyExceptionHandler -> nombre", nombre);
                            Log.d("MyExceptionHandler -> name", name);
                            titleText.setText(nombre);
                            textDescription.setText(info);
                            addressText.setText(ubicacion);
                            //horarioTextView.setText(horario);

                            // Cargar la imagen usando Glide o cualquier otra biblioteca de carga de imágenes
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Log.d("DetailFreixenetActivity", "URL de la imagen: " + imageUrl);
                                ImageView vinedoImg = findViewById(R.id.vinedoImg);
                                Glide.with(DetailFreixenetActivity.this)
                                        .load(imageUrl)
                                        .into(vinedoImg);
                            } else {
                                // Manejo de caso donde no hay URL de imagen
                                Log.e("DetailFreixenetActivity", "La URL de la imagen es nula o vacía.");
                            }
                        } else {
                            // Manejo de errores
                            Log.e("DetailFreixenetActivity", "Documento no encontrado en Firestore para el ID: " + idEvento);
                        }
                    } else {
                        // Manejo de errores
                        Log.e("DetailFreixenetActivity", "Error al obtener la información de los eventos desde Firestore", task.getException());
                    }
                }
            });
        } else {
            // Manejo de errores
            if (name == null) {
                Log.e("DetailFreixenetActivity", "El nombre del evento es nulo en la intención.");
            }
            if (idEvento.isEmpty()) {
                Log.e("DetailFreixenetActivity", "El ID de evento es inválido o vacío.");
            }
        }
    }


    // Método para verificar si la caja de comentarios está vacía
    private void verificarComentariosVacios() {

        // Verificamos si la lista de opiniones está vacía
        if (opinionesList.isEmpty()) {
            // Si la lista está vacía, mostramos el mensaje de ninguna opinión
            findViewById(R.id.noOpinionMessage).setVisibility(View.VISIBLE);

            // Ocultar vistas que muestran puntaje
            findViewById(R.id.calificacionLugarScore).setVisibility(View.GONE);
            findViewById(R.id.calificacionLugarBar).setVisibility(View.GONE);
            findViewById(R.id.calificacionLugarTotal).setVisibility(View.GONE);
            findViewById(R.id.verMasComentariosButton).setVisibility(View.GONE);
        } else {
            // Si hay opiniones, ocultamos el mensaje
            findViewById(R.id.noOpinionMessage).setVisibility(View.GONE);

            // Mostrar Score
            findViewById(R.id.calificacionLugarScore).setVisibility(View.VISIBLE);
            findViewById(R.id.calificacionLugarBar).setVisibility(View.VISIBLE);
            findViewById(R.id.calificacionLugarTotal).setVisibility(View.VISIBLE);
            findViewById(R.id.verMasComentariosButton).setVisibility(View.VISIBLE);
        }
    }

    // Método para obtener el ID del evento actual
    private String obteneridEvento() {
        Intent intent = getIntent();
        if (intent != null) {
            // Obtenemos el ID del evento desde la intención
            String idEvento = intent.getStringExtra("idEvento");
            Log.e("DetailFreixenetActivity", "Intent recibida correctamente.");
            Log.e("DetailFreixenetActivity", "ID de evento obtenido: " + idEvento);
            if (idEvento != null && !idEvento.isEmpty()) {
                return idEvento;
            } else {
                Toast.makeText(this, "Error: ID de evento no válida", Toast.LENGTH_SHORT).show();
                Log.e("DetailFreixenetActivity", "ID de evento no válido: " + idEvento);
            }
        } else {
            Log.e("DetailFreixenetActivity", "Intent es nulo.");
        }
        return "";
    }


    private void showCommentsActivity() {

        Intent intent = new Intent(this, Reviews.class);
        intent.putExtra("tipoReferencia", Reviews.EVENTO);
        intent.putExtra("idEvento", idEvento);
        intent.putExtra("titleTxt", titleText.getText());
        intent.putExtra("totalCalificaciones", totalCalificaciones);
        intent.putExtra("promedioCalificaciones", promedioCalificaciones);
        this.startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
