package com.example.winestastic;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        mFirestore = FirebaseFirestore.getInstance();

        iniciarImageSlider();
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
}