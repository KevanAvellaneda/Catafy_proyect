package com.example.winestastic;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Map_Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final float DEFAULT_ZOOM = 18f;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    private List<String> placesList;
    private List<String> eventoList;
    private TextView textViewPlaces;
    private TextView textViewEventos;


    private List<Marker> markerList = new ArrayList<>(); // Lista para almacenar los marcadores
    //mantenemos una lista de los marcadores creados y buscamos el marcador correspondiente en esa lista.
    private List<Marker> markereList = new ArrayList<>();

    private String targetStr = null;

    // Variable para controlar si el diálogo ya está abierto
    private boolean isAlertDialogVisible = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_,container, false);
        // Verificar si existe un marker al cual enfocar
        //targetStr = this.getArguments().getString("markerTitle");

        // Verificar si existen argumentos y obtener el markerTitle si está presente
        Bundle args = getArguments();
        if (args != null && args.containsKey("markerTitle")) {
            targetStr = args.getString("markerTitle");
        } else {
            targetStr = null; // O algún valor predeterminado si es necesario
        }

        // Inicializar FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        textViewPlaces = view.findViewById(R.id.vino); // TextView
        textViewEventos = view.findViewById(R.id.eventos); // TextView

        placesList = new ArrayList<>(); // Inicialización de la lista
        eventoList = new ArrayList<>(); // Inicialización de la lista
        // Configuramos el OnClickListener del TextView
        textViewPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndShowAllPlaces(); // Obtener y mostrar todos los vinedos antes de mostrar el diálogo
            }
        });
        textViewEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndShowAllEventos(); // Obtener y mostrar todos los eventos antes de mostrar el diálogo
            }
        });
        return view;
    }
    // Mostramos un diálogo con los vinedos que hay
    private void showPlacesListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogBasicCustomStyle);

        // Crear un TextView personalizado para el título
        TextView title = new TextView(getActivity());
        title.setText("¿A dónde quieres ir?");
        title.setGravity(Gravity.CENTER); // Centrar el texto en el TextView
        title.setTextSize(20); // Tamaño del texto del título (ajusta según sea necesario)
        title.setPadding(10, 55, 10, 5);
        title.setTextColor(Color.parseColor("#FFFFFF"));

        // Establecer el TextView personalizado como el título del AlertDialog
        builder.setCustomTitle(title);

        // Creamos un adaptador para la lista de vinedos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, placesList);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            // Método para cuando se hace clicki en un lugar de la lista
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                // Obtenemos el lugar seleccionado en la lista
                String selectedPlace = placesList.get(position);
                // Obtener las coordenadas del lugar seleccionado desde la base de datos y luego mover la cámara a esa ubicación
                db.collection("viñedos")
                        .whereEqualTo("nombre_vinedos", selectedPlace)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                GeoPoint location = documentSnapshot.getGeoPoint("marcador");
                                if (location != null) {
                                    moveCameraToLocation(location.getLatitude(), location.getLongitude());
                                    String title = documentSnapshot.getString("nombre_vinedos");

                                    // Buscar el marcador correspondiente en la lista
                                    // Cuando seleccionamos un elemento, iteramos sobre markerlist para encontrar el marcador con el mismo titulo
                                    // al llamar a onMarkerClick simula el comportamiento de hacer click en el marcador
                                    for (Marker marker : markerList) {
                                        if (marker.getTitle().trim().equalsIgnoreCase(title.trim())) {
                                            onMarkerClick(marker); // Llamar al método onMarkerClick con el marcador correspondiente
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            //Por si existe algún error
                        });
            }
        });
        // Mostramos el diálogo con los vinedos
        AlertDialog alertDialog = builder.create();
        // Restablecer la variable isDialogOpen cuando el diálogo se cierre
        alertDialog.setOnDismissListener(dialog -> {
            isAlertDialogVisible = false; //no visible
        });
        alertDialog.show();
        // Agregar margen inferior al contenido del AlertDialog
        Window window = alertDialog.getWindow();
        if (window != null) {
            View content = window.findViewById(android.R.id.content);
            if (content != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, getResources().getDisplayMetrics());
                params.setMargins(0, 0, 0, 20); // Establecer el margen inferior deseado
                content.setLayoutParams(params);
            }
        }
    }

    private void moveCameraToLocation(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
    }

    private void getAndShowAllPlaces() {
        // Obtenemos la lista completa de vinedos en Firestore y actualizamos el TextView
        if (isAlertDialogVisible) return; // Si ya hay un diálogo visible
        isAlertDialogVisible = true;

        db.collection("viñedos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        placesList.clear(); // Limpiamos la lista antes de actualizarla
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeName = document.getString("nombre_vinedos");
                            placesList.add(placeName);
                        }
                        // Luego de obtener los vinedos, mostramos el diálogo con la lista
                        showPlacesListDialog();
                    } else {
                        Log.e(TAG, "Error al obtener viñedos desde Firestore", task.getException());
                    }
                });
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void getAndShowAllEventos() {
        // Verificamos si el diálogo ya está abierto
        if (isAlertDialogVisible) return;
        isAlertDialogVisible = true;

        // Obtener la fecha de inicio del día actual
        Date startOfDay = getStartOfDay(new Date());

        // Obtenemos la lista completa de eventos de vinedos en Firestore y actualizamos el TextView
        db.collection("eventos")
                .whereGreaterThanOrEqualTo("fecha_eventoo", new Timestamp(startOfDay))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        placesList.clear(); // Limpiamos la lista antes de actualizarla
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeName = document.getString("nombre_evento");
                            placesList.add(placeName);
                        }
                        // Luego de obtener los eventos, mostramos el diálogo con la lista
                        showPlacesListDialogevento();
                    } else {
                        Log.e(TAG, "Error al obtener éventos desde Firestore", task.getException());
                    }
                });
    }

    private void showPlacesListDialogevento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogBasicCustomStyle);

        // Crear un TextView personalizado para el título
        TextView title = new TextView(getActivity());
        title.setText("¿A dónde quieres ir?");
        title.setGravity(Gravity.CENTER); // Centrar el texto en el TextView
        title.setTextSize(20); // Tamaño del texto del título (ajusta según sea necesario)
        title.setPadding(10, 55, 10, 5);
        title.setTextColor(Color.parseColor("#FFFFFF"));

        // Establecer el TextView personalizado como el título del AlertDialog
        builder.setCustomTitle(title);

        // Creamos un adaptador para la lista de eventos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_evento, placesList);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            // Método para cuando se hace clicki en un lugar de la lista
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                // Obtenemos el lugar seleccionado en la lista
                String selectedPlace = placesList.get(position);
                // Obtener las coordenadas del lugar seleccionado desde la base de datos y luego mover la cámara a esa ubicación
                db.collection("eventos")
                        .whereEqualTo("nombre_evento", selectedPlace)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                GeoPoint location = documentSnapshot.getGeoPoint("marcador");
                                if (location != null) {
                                    moveCameraToLocation(location.getLatitude(), location.getLongitude());
                                    String title = documentSnapshot.getString("nombre_evento");

                                    // Buscar el marcador correspondiente en la lista
                                    // Cuando seleccionamos un elemento, iteramos sobre markerlist para encontrar el marcador con el mismo titulo
                                    // al llamar a onMarkerClick simula el comportamiento de hacer click en el marcador
                                    for (Marker marker : markereList) {
                                        if (marker.getTitle().trim().equalsIgnoreCase(title.trim())) {
                                            onMarkerClick(marker); // Llamar al método onMarkerClick con el marcador correspondiente
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            //Por si existe algún error
                        });
            }
        });
        // Mostramos el diálogo con los lugares
        AlertDialog alertDialog = builder.create();
        // Restablecer la variable isDialogOpen cuando el diálogo se cierre
        alertDialog.setOnDismissListener(dialog -> {
            isAlertDialogVisible = false;
        });

        alertDialog.show();
        // Agregar margen inferior al contenido del AlertDialog
        Window window = alertDialog.getWindow();
        if (window != null) {
            View content = window.findViewById(android.R.id.content);
            if (content != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, getResources().getDisplayMetrics());
                params.setMargins(0, 0, 0, 20); // Establecer el margen inferior deseado
                content.setLayoutParams(params);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng jardin = new LatLng(20.58806, -100.38806);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(jardin));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jardin, 13));
        mMap = googleMap;

        // Obtener la fecha de inicio del día actual
        Date startOfDay = getStartOfDay(new Date());

        // Obtenemos la latitud y la longitud desde Firestore y agregamos marcadores al mapa
        db.collection("viñedos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Obtener datos de Firestore
                                GeoPoint location = document.getGeoPoint("marcador");
                                if (location != null) {
                                    String title = document.getString("nombre_vinedos");

                                    // cada que creamos un marcador tmb lo agregamos a la lista
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .title(title)
                                            .snippet("¿Cómo llegar?")
                                            .icon(bitmapDescriptor(getActivity().getApplicationContext(), R.drawable.vinoooo)));
                                    markerList.add(marker); // Agregar el marcador a la lista

                                    // Si el título coincide con el targetStr enfocar
                                    if (targetStr != null && title.trim().equals(targetStr.trim())) {
                                        onMarkerClick(marker);
                                    }
                                } else {
                                    Log.e(TAG, "El campo 'ubicacion' es nulo para el documento: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al obtener datos del documento: " + document.getId(), e);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error al obtener marcadores desde Firestore", task.getException());
                    }
                });

        // Obtenemos la latitud y la longitud desde Firestore y agregamos marcadores al mapa
        db.collection("eventos")
                .whereGreaterThanOrEqualTo("fecha_eventoo", new Timestamp(startOfDay))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Obtener datos de Firestore
                                GeoPoint location = document.getGeoPoint("marcador");
                                if (location != null) {
                                    String title = document.getString("nombre_evento");

                                    // cada que creamos un marcador tmb lo agregamos a la lista
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .title(title)
                                            .snippet("¿Cómo llegar?")
                                            .icon(bitmapDescriptor(getActivity().getApplicationContext(), R.drawable.megafono)));
                                    markereList.add(marker); // Agregar el marcador a la lista

                                    // Si el título coincide con el targetStr enfocar
                                    if (targetStr != null && title.trim().equals(targetStr.trim())) {
                                        onMarkerClick(marker);
                                    }
                                } else {
                                    Log.e(TAG, "El campo 'ubicacion' es nulo para el documento: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al obtener datos del documento: " + document.getId(), e);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error al obtener marcadores desde Firestore", task.getException());
                    }
                });


        // Configuramos el listener para los clicks en los marcadores
        mMap.setOnMarkerClickListener(this);

        // Configuramos el listener para los clicks en las ventanas de info de los marcadores
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // se crea objeto handler que se utiliza para retrasar la ejecucion de la accion de mostrar el infowindow
        // postDelayed se utiliza para ejecutar despues de cierto periodo, aqui son 500 milisegundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // como ya no mostramos en onMarkerClick siempre entra en esta condicion
                if (!marker.isInfoWindowShown()) {
                    marker.showInfoWindow();
                }
            }
        }, 500); // Retraso de 500 milisegundos

        if (mMap.getCameraPosition().zoom < 17) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Verificar si se tiene el permiso necesario para acceder a la ubicación
        int permiso = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permiso == PackageManager.PERMISSION_DENIED) {
            // Permiso denegado, mostrar un mensaje al usuario informándole sobre la necesidad de conceder el permiso
            mostrarMensajeDePermisoDenegado();
            //Toast.makeText(getActivity(), "Para obtener direcciones, necesitamos tu ubicación para mejorar la precisión de los resultados.", Toast.LENGTH_SHORT).show();
        } else {
            // Permiso concedido, continuar con la lógica para verificar la disponibilidad de la ubicación
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isLocationEnabled) {
                // Ubicación desactivada, mostrar un diálogo para permitir al usuario activarla
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Para obtener direcciones, necesitamos tu ubicación para mejorar la precisión de los resultados.")
                        .setCancelable(false)
                        .setPositiveButton("Activar ubicación", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // Ubicación desactivada, mostrar un diálogo para permitir al usuario activarla
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Será redirigído a la aplicación de Google Maps.")
                        .setCancelable(false)
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Ubicación disponible, obtener la ubicación del marcador y abrir la actividad de mapas
                                LatLng location = marker.getPosition();
                                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?&daddr=" + location.latitude + ',' + location.longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    // Método para mostrar un mensaje cuando los permisos de ubicación están denegados
    private void mostrarMensajeDePermisoDenegado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Para acceder al mapa es necesario habilitar los permisos de ubicación.\n\nPermisos > Ubicación > Permitir")
                .setCancelable(false)
                .setPositiveButton("Configuración de permisos", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Abrir la pantalla de configuración de la aplicación en el dispositivo
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
