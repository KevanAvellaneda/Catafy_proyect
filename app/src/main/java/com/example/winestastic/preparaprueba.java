package com.example.winestastic;

import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class preparaprueba extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FirebaseFirestore db;
    private List<PageData> pages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparaprueba);

        viewPager = findViewById(R.id.viewPager);
        db = FirebaseFirestore.getInstance();
        pages = new ArrayList<>();

        loadPagesFromFirestore();
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
                }
            } else {
                // Manejar errores
            }
        });
    }
}