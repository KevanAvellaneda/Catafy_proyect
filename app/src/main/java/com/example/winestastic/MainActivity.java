package com.example.winestastic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterEventos,adapterVinedos;
    private RecyclerView recyclerViewEventos, recyclerViewVinedos;


    private MeowBottomNavigation bottomNavigation;
    TextView txt_Nombre,txt_correo,txt_telefono,txt_Nombre2,txt_correo2;
    Button cerrar;
    RelativeLayout  menu, calendar, home, notifications, map;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore mFirestore;

    private Task<QuerySnapshot> eventosTask;
    private final Date today = new Date(); //fecha actual

    private final Calendar nextYear = Calendar.getInstance();

    private GoogleSignInClient mGoogleSignInClient;


    LinearLayout cardviewchatbot;
    ConstraintLayout card1;
    ConstraintLayout card2;
    ConstraintLayout card3;
    ConstraintLayout card4;


    RecyclerView recyclerView;
    ItemsAdapterVinedos itemsAdapterVinedos;
    ItemsAdapterEventos itemsAdapterEventos;
    ArrayList<ItemsDomainVinedos> items;
    ArrayList<ItemsDomainEventos> items2;

    ProgressBar pbProgressMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notifications = findViewById(R.id.notifications);
        LinearLayout notificationContainer = findViewById(R.id.notificationContainerr);
        LinearLayout notificationContainerNuevas = findViewById(R.id.notificationContainerNuevas);
        LinearLayout notificationContainerUltimos7Dias = findViewById(R.id.notificationContainerUltimos7Dias);
        LinearLayout notificationContainerUltimos30Dias = findViewById(R.id.notificationContainerUltimos30Dias);

        Date currentDate = new Date();

        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(currentDate);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0); // Hora del día a las 00:00
        calendarToday.set(Calendar.MINUTE, 0); // Mminutos a 0
        calendarToday.set(Calendar.SECOND, 0); // Segundos a 0
        calendarToday.set(Calendar.MILLISECOND, 0); // Milisegundos a 0

        // Guardamos la fecha de inicio del día actual
        Date todayStartTime = calendarToday.getTime();

        // Obtenemos el tiempo en milisegundos por si las dudas
        long todayStartTimeInMillis = calendarToday.getTimeInMillis();

        // Obtenemos la fecha de ayer
        Calendar calendarYesterday = Calendar.getInstance();
        calendarYesterday.setTime(currentDate);
        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendarYesterday.getTime();

        // Obtenemos la fecha de hace 7 días
        Calendar calendar7DaysAgo = Calendar.getInstance();
        calendar7DaysAgo.setTime(currentDate);
        calendar7DaysAgo.add(Calendar.DAY_OF_YEAR, -7);
        Date date7DaysAgo = calendar7DaysAgo.getTime();

        // Obtenemos la fecha de hace 30 días
        Calendar calendar30DaysAgo = Calendar.getInstance();
        calendar30DaysAgo.setTime(currentDate);
        calendar30DaysAgo.add(Calendar.DAY_OF_YEAR, -30);
        Date date30DaysAgo = calendar30DaysAgo.getTime();

        Button button3 = findViewById(R.id.button3);

        mFirestore = FirebaseFirestore.getInstance();
        pbProgressMain = findViewById(R.id.progress_main);



        recyclerView = findViewById(R.id.viewEventos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        items2 = new ArrayList<>();
        itemsAdapterEventos = new ItemsAdapterEventos(items2, this);
        recyclerView.setAdapter(itemsAdapterEventos);
        mFirestore.collection("eventos")
                .orderBy("fecha_eventoo")
                .whereGreaterThanOrEqualTo("fecha_eventoo", todayStartTime)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        pbProgressMain.setVisibility(View.VISIBLE);
                        if(error != null){
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Date notificationDate = dc.getDocument().getTimestamp("fecha").toDate();
                                // Comparar solo el año, mes y día de la fecha del evento
                                Calendar eventCalendar = Calendar.getInstance();
                                eventCalendar.setTime(notificationDate);
                                eventCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                eventCalendar.set(Calendar.MINUTE, 0);
                                eventCalendar.set(Calendar.SECOND, 0);
                                eventCalendar.set(Calendar.MILLISECOND, 0);
                                Date eventDateOnly = eventCalendar.getTime();

                                ItemsDomainEventos evento = dc.getDocument().toObject(ItemsDomainEventos.class);
                                items2.add(evento);
                                // Obtenemos la fecha del evento como un string que se pueda leer
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy 'a las' HH:mm:ss a", Locale.getDefault());
                                String fechaEvento = dateFormat.format(evento.getFecha_eventoo().toDate());

                                // Comparamos la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                                if (eventDateOnly.equals(todayStartTime) || eventDateOnly.after(todayStartTime)) {
                                    addNotification("¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el " + fechaEvento, notificationContainerNuevas, R.layout.layout_notificatione);
                                } else if (eventDateOnly.after(date7DaysAgo)) {
                                    addNotification("¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el " + fechaEvento, notificationContainerUltimos7Dias, R.layout.layout_notificatione);
                                } else if (eventDateOnly.after(date30DaysAgo)) {
                                    addNotification("¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el " + fechaEvento, notificationContainerUltimos30Dias, R.layout.layout_notificatione);
                                }
                            }
                        }
                        itemsAdapterEventos.notifyDataSetChanged();
                        pbProgressMain.setVisibility(View.GONE);
                    }
                });

        recyclerView = findViewById(R.id.viewViñedos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        items = new ArrayList<>();
        itemsAdapterVinedos = new ItemsAdapterVinedos(items, this);
        recyclerView.setAdapter(itemsAdapterVinedos);
        mFirestore.collection("viñedos").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                //items.clear(); // Limpiar la lista actual de lugares
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            // Obtenemos el índice o index en el que se insertó el nuevo elemento
                            int addedIndex = dc.getNewIndex();
                            //es menor o igual que el tamaño actual de la lista
                            if (addedIndex >= 0 && addedIndex <= items.size()) {
                                // El índice es válido, podemos agregar el elemento a la lista (insertamos el nuevo elemento a items)
                                items.add(addedIndex, dc.getDocument().toObject(ItemsDomainVinedos.class));
                                itemsAdapterVinedos.notifyItemInserted(addedIndex);
                            } else {
                                // El índice no es válido
                                Log.e("IndexOutOfBounds", "El índice está fuera de los límites válidos: " + addedIndex);
                            }
                            // Procesamos las notificaciones
                            Date notificationDate = dc.getDocument().getTimestamp("fecha").toDate();
                            ItemsDomainVinedos evento = dc.getDocument().toObject(ItemsDomainVinedos.class);
                            //items.add(evento);

                            // Comparamos la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                            if (notificationDate.after(todayStartTime)) {
                                addNotification(dc.getDocument().getString("nombre_vinedos")+ " está disponible, ¡Ven a Conocerlo!", notificationContainerNuevas, R.layout.layout_notification);
                            } else if (notificationDate.after(date7DaysAgo)) {
                                addNotification(dc.getDocument().getString("nombre_vinedos")+ " está disponible, ¡Ven a Conocerlo!", notificationContainerUltimos7Dias, R.layout.layout_notification);
                            } else if (notificationDate.after(date30DaysAgo)) {
                                addNotification(dc.getDocument().getString("nombre_vinedos")+ " está disponible, ¡Ven a Conocerlo!", notificationContainerUltimos30Dias, R.layout.layout_notification);
                            }
                            break;

                        case MODIFIED:
                            int modifiedIndex = dc.getOldIndex();
                            if (modifiedIndex >= 0 && modifiedIndex < items.size()) {
                                items.set(modifiedIndex, dc.getDocument().toObject(ItemsDomainVinedos.class));
                                itemsAdapterVinedos.notifyItemChanged(modifiedIndex);
                            }
                            break;
                        case REMOVED:
                            int removedIndex = dc.getOldIndex();
                            if (removedIndex >= 0 && removedIndex < items.size()) {
                                // Remover el elemento de la lista y notificar al adaptador
                                items.remove(removedIndex);
                                itemsAdapterVinedos.notifyItemRemoved(removedIndex);
                                // Procesar notificaciones
                                notificationDate = dc.getDocument().getTimestamp("fecha").toDate();

                                // Comparar la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                                if (notificationDate.after(todayStartTime)) {
                                    addNotification(dc.getDocument().getString("nombre_barbacoa")+ " ya no está disponible en Cadereyta :(", notificationContainerNuevas, R.layout.layout_notification);
                                }
                            }
                            break;
                    }
                }
                // Mostrar lugares al azar
                List<ItemsDomainVinedos> randomItems = new ArrayList<>(items);
                Collections.shuffle(randomItems);
                // Alteramos la lista para que tenga un máximo de 10 elementos
                if (randomItems.size() > 10) {
                    randomItems = randomItems.subList(0, 10);
                }
                items.clear();
                items.addAll(randomItems);
                itemsAdapterVinedos.notifyDataSetChanged();

                pbProgressMain.setVisibility(View.GONE);
            }
        });


        bottomNavigation = findViewById(R.id.bottomNavigation);
        cerrar = findViewById(R.id.cerrar_sesion);
        menu = findViewById(R.id.menu);
        calendar = findViewById(R.id.calendar);
        home = findViewById(R.id.home);
        notifications =findViewById(R.id.notifications);
        map = findViewById(R.id.map);
        txt_Nombre = findViewById(R.id.Mostrarnombre);
        txt_Nombre2 = findViewById(R.id.nombre2);
        txt_correo2 = findViewById(R.id.correo2);
        txt_correo = findViewById(R.id.Mostrarcorreo);
        txt_telefono = findViewById(R.id.Mostrartelefono);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        cardviewchatbot = findViewById(R.id.cardviewchat);
        bottomNavigation.show(3,true);
        //-------------Servicios Google----------------

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Configuracion para el uso de inicio de sesion con google

        card1 = findViewById(R.id.cardInicio1);
        card2 = findViewById(R.id.cardInicio2);
        card3 = findViewById(R.id.cardInicio3);
        card4 = findViewById(R.id.cardInicio4);




        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.menuanvorgesa));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_calendar_month_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.campana));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.baseline_public_24));

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){


                    case 1:

                        menu.setVisibility(View.VISIBLE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;

                    case 2:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;

                    case 3:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;

                    case 4:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.VISIBLE);
                        map.setVisibility(View.GONE);

                        break;

                    case 5:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);

                        break;

                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 1:

                        menu.setVisibility(View.VISIBLE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 2:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 3:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });


        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 4:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.VISIBLE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 5:

                        menu.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);

                        getLocalizacionn();

                        break;
                }

                return null;
            }
        });



        ////CALENDARIO//////

        // Inicializamos el selector de fechas
        final Date today = new Date(); //fecha actual
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(today, nextYear.getTime()).withSelectedDate(today);

        // Recuperación de eventos de la base de datos
        mFirestore.collection("eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Asignamos la tarea completada a la variable de instancia
                    eventosTask = task;
                    // Iteramos sobre la colección Eventos
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Obtenemos la información del evento (nombre y fecha)
                        String nombreEvento = document.getString("nombre_evento");
                        Date fecha = document.getDate("fecha_eventoo");

                        // Verificamos si la fecha del evento es nula

                        if (fecha != null) {
                            //Estamos verificando si la fecha del evento está en el día actual o en el futuro
                            if (!fecha.before(today)) {
                                // Marcamos la fecha del evento en el calendario
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(fecha);
                                datePicker.selectDate(cal.getTime());
                            }
                        }
                    }
                    datePicker.selectDate(today);
                } else {
                    Log.d(TAG, "Error al obtener eventos: ", task.getException());
                }
            }
        });
        // Definimos el listener para la Selección de fechas, busca si hay un evento en la fecha y mostramos su info
        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                // Verificamos si hay un evento en la fecha seleccionada
                // Creamos una lista para almacenar la información de los eventos encontrados para la fecha seleccionada
                List<String> informacionEventos = new ArrayList<>();
                if (eventosTask != null && eventosTask.isSuccessful()) {
                    for (QueryDocumentSnapshot document : eventosTask.getResult()) {
                        String nombreEvento = document.getString("nombre_evento");
                        Date fecha = document.getDate("fecha_eventoo");

                        // Verificamos si la fecha del evento coincide con la fecha seleccionada
                        if (fecha != null && mismoDia(fecha, date)) {
                            informacionEventos.add(nombreEvento);
                        }
                    }
                }
                // Mostramos la info de los eventos en el botón
                if (!informacionEventos.isEmpty()) {
                    StringBuilder eventosTexto = new StringBuilder("\nEventos para la fecha seleccionada:\n\n");
                    for (String evento : informacionEventos) {
                        eventosTexto.append(evento).append("\n\n");
                    }
                    button3.setText(eventosTexto.toString());
                } else {
                    button3.setText("No hay eventos para la fecha seleccionada :(");
                }

                // Mostramos un Toast con los nombres de los eventos si los hay en Firestore
                if (!informacionEventos.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Eventos seleccionados: " + informacionEventos, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No hay eventos para la fecha seleccionada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDateUnselected(Date date) {
            }

            // Aquí estamos verificando si dos fechas son el mismo día
            private boolean mismoDia(Date date1, Date date2) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(date2);
                return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
            }
        });

        // Decorador para cambiar el color de fondo de las celdas con eventos (EventDecorator)
        datePicker.setDecorators(Collections.singletonList(new EventDecorator()));

        ////FIN CALENDARIO//////

        Fragment fragment = new Map_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


        cardviewchatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, chatbot.class);
                startActivity(intent);
                finish();
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cardCatadeVinos.class);
                startActivity(intent);
                finish();
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cardVinedos.class);
                startActivity(intent);
                finish();
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cardVinos.class);
                startActivity(intent);
                finish();
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cardDemasEntradas.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // METODO PARA PEDIR UBICACION AL USUARIO chi --------------------------------------
    private void getLocalizacionn(){
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    // --> addNotification: Aquí configuramos las NOTIFICACIONES
    private void addNotification(String mensaje, LinearLayout notificationContainer, int layoutResId) {
        View notificationView = getLayoutInflater().inflate(layoutResId, null);
        TextView notificationMessage = notificationView.findViewById(R.id.notificationMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationMessage.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        notificationMessage.setText(mensaje);

        // Ajustar márgenes para la vista de notificación
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginPixels = (int) (12 * getResources().getDisplayMetrics().density);
        layoutParams.setMargins(0, 0, 0, marginPixels);

        // Aplicar los parámetros de diseño a la vista de notificación
        notificationView.setLayoutParams(layoutParams);

        // Agregar la nueva notificación al contenedor especificado
        notificationContainer.addView(notificationView);
    }

    private void mostrarSnackbar(String mensaje) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    // --> EventDecorator: Aquí cambiamos el color del una fecha del CALENDARIO para ver si hay un evento
    private class EventDecorator implements CalendarCellDecorator {
        @Override
        public void decorate(CalendarCellView cellView, Date date) {
            // Verificamos si la fecha tiene un evento asociado
            boolean tieneEvento = tieneEventoEnFecha(date);
            if (tieneEvento) {
                // Cambiamos el color de fondo de la celda si tiene un evento
                cellView.setBackgroundColor(Color.rgb(250, 143, 177)); // Ponemos de color la celda
            } else if (isToday(date)){
                cellView.setBackgroundColor(Color.rgb(178, 218, 250)); // Ponemos de color la celda
            } else {
                cellView.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
        // Checamos si hay un evento asociado a una fecha
        private boolean tieneEventoEnFecha(Date date) {
            // Creamos un objeto Calendar y establecemos su tiempo para que coincida con la fecha dada
            Calendar cal1 = Calendar.getInstance();//cal1 representa la fecha asociada a la celda del calendario actual que se está decorando
            cal1.setTime(date);

            if (eventosTask != null && eventosTask.isSuccessful()) {
                // Iteramoa sobre los resultados de Firestore
                for (QueryDocumentSnapshot document : eventosTask.getResult()) {
                    Date fecha = document.getDate("fecha_eventoo");
                    if (fecha != null) {
                        //Estamos verificando si la fecha del evento está en el día actual o en el futuro
                        if (!fecha.before(today) ) {
                            // Convertimos la fecha del evento a un objeto Calendar
                            Calendar cal2 = Calendar.getInstance();
                            cal2.setTime(fecha);

                            // Comparamos los campos de año, mes y día de cal1 (fecha de la celda del calendario) con los campos correspondientes de cal2 (fecha del evento)
                            // Si son iguales, significa que hay un evento asociado a la fecha dada
                            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                                // Si hay un evento en la fecha, devuelve verdadero
                                return true;
                            }
                        }
                    }
                }
            }
            // Si no hay eventos en la fecha dada, devuelve false
            return false;
        }

        // Verifica si es la fecha de hoy
        private boolean isToday(Date date){
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date);
            cal2.setTime(today);
            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

        }
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            irLogin();
        }else{
            verifyUser();
            cargardatos();
        }
    }

    private void verifyUser() {
        // Verifica si un usuario ha autenticado su correo
            user.reload();
            if(!user.isEmailVerified()){
                // Ubicación desactivada, mostrar un diálogo para permitir al usuario activarla
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Para continuar con la aplicación es necesario verificar tu correo.\n\nPor favor, revisa tu correo incluso tu spam.")
                        .setCancelable(false)
                        .setNegativeButton("Enviar correo", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                user.sendEmailVerification();
                                dialog.cancel();

                                AlertDialog.Builder confirmationBuilder = new AlertDialog.Builder(MainActivity.this);
                                confirmationBuilder.setMessage("Busca en tu correo electrónico el mensaje de verificación, da clic al enlace y vuelve a iniciar sesión.")
                                        .setCancelable(false)
                                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                logout();
                                            }
                                        });
                                confirmationBuilder.create().show();
                            }
                        })
                        .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logout();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    private void cargardatos(){
        mFirestore.collection("usuarios").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        String nombre = document.getString("nombre");
                        String correo = document.getString("correo");
                        String telefono = document.getString("telefono");

                        txt_Nombre.setText(nombre);
                        txt_correo.setText(correo);
                        txt_telefono.setText(telefono);
                        txt_Nombre2.setText(nombre);
                        txt_correo2.setText(correo);
                    }
                }

            }
        });

    }

    private void logout(){
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    irLogin();
                }else {
                    mostrarMensaje("No se logro cerrar sesion");
                }
            }
        });
    }

    private void irLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Pulse de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}