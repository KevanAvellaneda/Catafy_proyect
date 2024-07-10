package com.example.winestastic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class VerTodosLosLugaresActivity extends AppCompatActivity {
    // Variables
    private RecyclerView recyclerView;
    private ProgressBar pbProgressMain;
    private FirebaseFirestore mFirestore;
    private ArrayList<ItemsDomainVinedos> items;
    private ItemsAdapterVinedos itemsAdapterVinedos;
    //private ScrollView scrollView;
    private NestedScrollView scrollView;
    protected Class lastActivity = MainActivity.class;
    private DocumentSnapshot lastVisible; // Último documento visible en la lista

    // Constantes
    private static final int PAGE_SIZE = 5; // Tamaño de la página

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_ver_lugares);
        configSwipe();

        // Inicialización de Firebase y otros elementos de la interfaz de usuario
        mFirestore = FirebaseFirestore.getInstance();

        // Inicialización del ProgressBar
        pbProgressMain = findViewById(R.id.progress_main);

        // Configuración de RecyclerView para las barbacoas
        recyclerView = findViewById(R.id.recycler_view_ver_lugares);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        items = new ArrayList<>();
        itemsAdapterVinedos = new ItemsAdapterVinedos(items, this, ItemsAdapterVinedos.LAYOUT_CUSTOM);
        recyclerView.setAdapter(itemsAdapterVinedos);

        // Cargar los primeros elementos
        loadFirstPage();

        // Inicializar el ScrollView
        scrollView = findViewById(R.id.scrollView);

        // Listener para el scroll del RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Si hemos llegado al final de la lista y hay más elementos por cargar
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }
        });

        //SearchView
        SearchView searchView = findViewById(R.id.search_view);

        // Configuramos el listener para el cambio de texto en el SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Este método se llama cuando se presiona "Enter" o se envía una consulta

                // Realizar la búsqueda con el texto ingresado
                performSearch(query);

                // Devolver true para indicar que la búsqueda fue manejada
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Este método se llama cuando cambia el texto en el SearchView

                // Si el texto está vacío, mostrar todos los lugares nuevamente
                if (newText.isEmpty()) {
                    showAllPlaces();
                } else {
                    // Realizar la búsqueda en tiempo real mientras el usuario escribe
                    performSearch(newText);
                }

                // Devolver false para permitir que el SearchView maneje los cambios de texto
                return false;
            }
        });


        // Mostrar el botón para regresar y eliminar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Método para cargar la primera página de elementos
    private void loadFirstPage() {
        pbProgressMain.setVisibility(View.VISIBLE);
        mFirestore.collection("viñedos")
                .limit(PAGE_SIZE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            items.add(document.toObject(ItemsDomainVinedos.class));
                        }
                        itemsAdapterVinedos.notifyDataSetChanged();
                        // Guardar la referencia al último documento visible
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    } else {
                        Log.e("Firestore error", "Error getting documents: ", task.getException());
                    }
                    pbProgressMain.setVisibility(View.GONE);
                });
    }

    // Método para cargar más elementos cuando el usuario se desplaza hacia abajo
    private void loadMoreItems() {
        pbProgressMain.setVisibility(View.VISIBLE);
        mFirestore.collection("viñedos")
                .startAfter(lastVisible)
                .limit(PAGE_SIZE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            for (DocumentSnapshot document : documents) {
                                items.add(document.toObject(ItemsDomainVinedos.class));
                            }
                            itemsAdapterVinedos.notifyDataSetChanged();
                            // Actualizar la referencia al último documento visible
                            lastVisible = documents.get(documents.size() - 1);
                        }else {
                            Log.d("Paginación", "Se alcanzó el final de la lista.");
                        }
                    } else {
                        Log.e("Firestore error", "Error getting documents: ", task.getException());
                    }
                    pbProgressMain.setVisibility(View.GONE);
                });
    }

    private void configSwipe() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simula una actualización de 2 segundos
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Detiene la animación de actualización
                swipeRefreshLayout.setRefreshing(false);

                recreate();
            }, 600);
        });
    }

    // Aquí realixzamos un método para la búsqueda por texto ingresado
    private void performSearch(String query) {
        if(items == null)
        {
            return;
        }
        // Convertir el texto de búsqueda y el nombre de los lugares a minúsculas para una comparación sin distinción de mayúsculas y minúsculas
        String lowerCaseQuery = query.toLowerCase();
        // Filtrar los lugares por nombre basado en el texto de búsqueda sin distinción de mayúsculas y minúsculas ni acentos
        ArrayList<ItemsDomainVinedos> filteredList = new ArrayList<>();
        for (ItemsDomainVinedos item : items) {
            if (item.getNombre_vinedos() != null && removeAccents(item.getNombre_vinedos()).toLowerCase().contains(removeAccents(lowerCaseQuery))) {
                filteredList.add(item);
            }
        }

        // Actualizar el RecyclerView con los resultados obtenidos
        itemsAdapterVinedos.setFilter(filteredList);
    }

    // Método para eliminar los acentos de una cadena
    private String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // Método para mostrar todos los lugares nuevamente
    private void showAllPlaces() {
        itemsAdapterVinedos.setFilter(items);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}