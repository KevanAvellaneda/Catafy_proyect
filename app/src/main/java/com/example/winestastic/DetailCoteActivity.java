package com.example.winestastic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class DetailCoteActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFavorites;
    private TextView emptyView;
    private GifImageView errorGif;
    private ItemsAdapterVinedos adapter;
    private ArrayList<ItemsDomainVinedos> favoriteVinedosList;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cote);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        emptyView = findViewById(R.id.emptyView);
        errorGif = findViewById(R.id.errorGif);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        favoriteVinedosList = new ArrayList<>();
        adapter = new ItemsAdapterVinedos(favoriteVinedosList, this, ItemsAdapterVinedos.LAYOUT_CUSTOM);
        recyclerViewFavorites.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Favoritos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFavoriteVinedos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavoriteVinedos() {
        db.collection("favoritos")
                .whereEqualTo("id_usuario", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteVinedosList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idVinedos = document.getString("idVinedos");
                            db.collection("viñedos").document(idVinedos).get().addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    ItemsDomainVinedos vinedo = doc.toObject(ItemsDomainVinedos.class);
                                    favoriteVinedosList.add(vinedo);
                                    adapter.notifyDataSetChanged();
                                }
                                updateUI();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(DetailCoteActivity.this, "Error al cargar datos del viñedo", Toast.LENGTH_SHORT).show();
                                updateUI();
                            });
                        }
                        if (task.getResult().isEmpty()) {
                            updateUI();
                        }
                    } else {
                        Toast.makeText(DetailCoteActivity.this, "Error al obtener favoritos", Toast.LENGTH_SHORT).show();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        if (favoriteVinedosList.isEmpty()) {
            recyclerViewFavorites.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            errorGif.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFavorites.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            errorGif.setVisibility(View.GONE);
        }
    }
}