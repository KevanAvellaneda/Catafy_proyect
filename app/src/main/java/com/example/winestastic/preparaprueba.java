package com.example.winestastic;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class preparaprueba extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FirebaseFirestore db;
    private List<PageData> pages;
    private WormDotsIndicator dotsIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparaprueba);

        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        dotsIndicator.setDotIndicatorColor(getResources().getColor(R.color.wine));
        dotsIndicator.setStrokeDotsIndicatorColor(getResources().getColor(R.color.black));
        db = FirebaseFirestore.getInstance();
        pages = new ArrayList<>();

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Delicio");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadPagesFromFirestore();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPagesFromFirestore() {
        CollectionReference collection = db.collection("important_data");
        collection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String title = document.getString("titulo");
                        String description = document.getString("descripcion");
                        String imageUrl = document.getString("url"); // Asegúrate de tener este campo en Firestore

                        pages.add(new PageData(imageUrl, title, description));
                    }
                    viewPagerAdapter = new ViewPagerAdapter(pages);
                    viewPager.setAdapter(viewPagerAdapter);

                    // Asocia el adaptador al DotsIndicator después de establecer el adaptador
                    dotsIndicator.setViewPager2(viewPager);
                    // Configurar la página inicial del ViewPager
                    int initialPosition = getIntent().getIntExtra("position", 0);
                    viewPager.setCurrentItem(initialPosition, true);
                }
            } else {
                // Manejar errores
            }
        });
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
}