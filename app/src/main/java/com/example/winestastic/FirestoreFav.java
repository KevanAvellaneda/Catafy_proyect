package com.example.winestastic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreFav {
    public static void guardarFavoritoEnFirestore(Context context, String nombreVinedo, String idVinedos) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> favorito = new HashMap<>();
            favorito.put("nombre_vinedos", nombreVinedo);
            favorito.put("idVinedos", idVinedos);
            favorito.put("id_usuario", userId);

            FirebaseFirestore.getInstance().collection("favoritos")
                    .add(favorito)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("FirestoreFav", "Favorito guardado en Firestore con ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreFav", "Error al guardar favorito en Firestore", e);
                    });
        }
    }

    public static void eliminarFavoritoEnFirestore(Context context, String nombreVinedo, String idVinedos) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore.getInstance().collection("favoritos")
                    .whereEqualTo("idVinedos", idVinedos)
                    .whereEqualTo("id_usuario", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseFirestore.getInstance().collection("favoritos")
                                        .document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, nombreVinedo + " ha sido eliminado de tus favoritos", Toast.LENGTH_SHORT).show();
                                            Log.d("FirestoreFav", "Favorito eliminado de Firestore");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FirestoreFav", "Error al eliminar favorito de Firestore", e);
                                        });
                            }
                        } else {
                            Log.e("FirestoreFav", "Error al buscar favorito en Firestore para eliminar", task.getException());
                        }
                    });
        }
    }
}
