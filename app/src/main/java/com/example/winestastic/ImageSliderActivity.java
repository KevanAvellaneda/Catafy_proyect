package com.example.winestastic;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.winestastic.databinding.ActivityScrollingBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageSliderActivity extends AppCompatActivity {
    FirebaseFirestore mFirestore;
    String nameImageDocument = "main";

    //  SCROLLING
    protected String title = "";
    protected Class lastActivity = MainActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        mFirestore = FirebaseFirestore.getInstance();

        iniciarImageSlider();


        setupActionBar();
//        configSwipe();
    }


    private void iniciarImageSlider() {


        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        // initializing the slider view.
        SliderView sliderView = findViewById(R.id.slider);

        mFirestore
                .collection("static_info")
                .document(nameImageDocument)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                         @Override
                                         public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                             if (error != null) {
                                                 Log.w(TAG, "Listen failed.", error);
                                                 return;
                                             }

                                             if (value != null && value.exists()) {
                                                 Log.d(TAG, "Current data: " + value.getData());
                                                 Map<String, Object> data = value.getData();
                                                 if (data != null) {

                                                     try {

                                                         JSONArray sliderArray = new JSONArray((List) data.get("slider"));


                                                         for (int i = 0; i < sliderArray.length(); i++) {
                                                             JSONObject sliderObject = sliderArray.getJSONObject(i);
                                                             String image = sliderObject.optString("image");
                                                             String destination = sliderObject.optString("destination");

                                                             if (destination != null)
                                                                 sliderDataArrayList.add(new SliderData(image, destination));
                                                             else
                                                                 sliderDataArrayList.add(new SliderData(image));

                                                         }

                                                     } catch (JSONException e) {
                                                         Log.e(TAG, "JSON parsing error: ", e);
                                                     }
                                                 }


                                             } else {
                                                 Log.d(TAG, "Current data: null");
                                             }


                                             SliderAdapter adapter = new SliderAdapter(getBaseContext(), sliderDataArrayList);

                                             // cycle direction: left to right
                                             sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

                                             sliderView.setSliderAdapter(adapter);

                                             sliderView.setScrollTimeInSec(6);

                                             sliderView.setAutoCycle(true);
                                             sliderView.startAutoCycle();

                                         }
                                     }

                );
    }


    private void configSwipe() {
//        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Simulamos una actualización de 2 segundos
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        binding.swipe.setRefreshing(false);
//                        // Refrescar la actividad actual
//                        //recreate();
//                    }
//                }, 600);
//            }
//        });
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(this, lastActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setupActionBar(){


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(title);

        //toolBarLayout.setCollapsedTitleTextColor("");
        //toolBarLayout.setCollapsedTitleTextColor(R.color.flexible_text_color);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
}