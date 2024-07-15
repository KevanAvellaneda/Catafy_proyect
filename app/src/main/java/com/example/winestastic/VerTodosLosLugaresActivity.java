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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

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
    private ArrayList<ItemsDomainVinedos> allItems; // Lista de todos los elementos cargados
    private ItemsAdapterVinedos itemsAdapterVinedos;
    //private ScrollView scrollView;
    private NestedScrollView scrollView;
    protected Class lastActivity = MainActivity.class;

    // Constantes
    private static final int PAGE_SIZE = 3; // Tamaño de la página
    private int currentPage = 0;
    private List<List<ItemsDomainVinedos>> paginatedItems = new ArrayList<>(); // Lista de páginas

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
        allItems = new ArrayList<>();
        itemsAdapterVinedos = new ItemsAdapterVinedos(items, this, ItemsAdapterVinedos.LAYOUT_CUSTOM);
        recyclerView.setAdapter(itemsAdapterVinedos);

        // Cargar los primeros elementos
        loadAllItems();

        // Configuración de botones de paginación
        findViewById(R.id.nextPageButton).setOnClickListener(v -> loadNextPage());
        findViewById(R.id.prevPageButton).setOnClickListener(v -> loadPreviousPage());

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

    // Este método carga todos los elementos de la colección viñedos desde Firestore y los almacena en la lista allItems.
    private void loadAllItems() {
        pbProgressMain.setVisibility(View.VISIBLE);
        mFirestore.collection("viñedos")
                .orderBy("nombre_vinedos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        allItems.clear();
                        for (DocumentSnapshot document : documents) {
                            ItemsDomainVinedos item = document.toObject(ItemsDomainVinedos.class);
                            allItems.add(item);
                        }
                        // Llama al método initializePagination() para dividir los elementos cargados en páginas
                        initializePagination();
                    } else {
                        Log.e("Firestore error", "Error getting documents: ", task.getException());
                    }
                    pbProgressMain.setVisibility(View.GONE);
                });
    }

    // Este método divide los elementos en allItems en páginas,
    // según el tamaño de página definido (PAGE_SIZE), y almacena estas páginas en paginatedItems.
    private void initializePagination() {
        paginatedItems.clear();
        // calcula el num total de elementos en allitems
        int totalItems = allItems.size();
        // dividimos allitems en sublistas (paginas) del tamano del pagesize
        for (int i = 0; i < totalItems; i += PAGE_SIZE) {
            int end = Math.min(i + PAGE_SIZE, totalItems);
            // agregamos cada sublista a paginatedItems
            paginatedItems.add(new ArrayList<>(allItems.subList(i, end)));
        }
        currentPage = 0;
        // Si paginatedItems no está vacío, carga los elementos de la primera página en items
        // y notifica al adaptador para que actualice el RecyclerView.
        if (!paginatedItems.isEmpty()) {
            items.clear();
            items.addAll(paginatedItems.get(currentPage));
            itemsAdapterVinedos.notifyDataSetChanged();
        }
    }

    // Método para cargar la página siguiente
    private void loadNextPage() {
        if (currentPage < paginatedItems.size() - 1) {
            currentPage++;
            items.clear();
            items.addAll(paginatedItems.get(currentPage));
            itemsAdapterVinedos.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No hay más viñedos disponibles.", Toast.LENGTH_SHORT).show();
            //Log.d("Paginación", "No hay más páginas.");
        }
    }

    // Método para cargar la página anterior
    private void loadPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            items.clear();
            items.addAll(paginatedItems.get(currentPage));
            itemsAdapterVinedos.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Estás en la primera página.", Toast.LENGTH_SHORT).show();
            //Log.d("Paginación", "Estás en la primera página.");
        }
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
        if(allItems == null)
        {
            return;
        }
        // Convertir el texto de búsqueda y el nombre de los lugares a minúsculas para una comparación sin distinción de mayúsculas y minúsculas
        String lowerCaseQuery = query.toLowerCase();
        // Filtrar los lugares por nombre basado en el texto de búsqueda sin distinción de mayúsculas y minúsculas ni acentos
        ArrayList<ItemsDomainVinedos> filteredList = new ArrayList<>();
        for (ItemsDomainVinedos item : allItems) {
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