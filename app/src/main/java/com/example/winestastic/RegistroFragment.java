package com.example.winestastic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


public class RegistroFragment extends Fragment {

    EditText reg_nombre, reg_correo, reg_telefono, reg_password, reg_confirpass;
    Button btn_registro;
    ProgressBar pbProgressLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    private static final String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_-])(?=\\S+$).{8,}$";
    private Pattern passwordPattern;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registro, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        reg_nombre = root.findViewById(R.id.reg_nombre);
        reg_correo = root.findViewById(R.id.reg_correo);
        reg_telefono = root.findViewById(R.id.reg_telefono);
        reg_password = root.findViewById(R.id.reg_password);
        reg_confirpass = root.findViewById(R.id.reg_confirpass);
        btn_registro = root.findViewById(R.id.btn_registro);
        //pbProgressLogin = root.findViewById(R.id.progress_login);

        // Expresión regular para validar contraseñas
        passwordPattern = Pattern.compile(regexPassword);

        // Click listener para el botón de registro
        btn_registro.setOnClickListener(view -> {
            String nameuser = reg_nombre.getText().toString().trim();
            String correouser = reg_correo.getText().toString().trim();
            String telefonouser = reg_telefono.getText().toString().trim();
            String passworduser = reg_password.getText().toString().trim();
            String confirmaruser = reg_confirpass.getText().toString().trim();

            if (!nameuser.isEmpty() && !correouser.isEmpty() && !telefonouser.isEmpty() && !passworduser.isEmpty() && !confirmaruser.isEmpty()) {
                if (passworduser.matches(regexPassword)) {
                    if (passworduser.equals(confirmaruser)) {
                        if (Patterns.PHONE.matcher(telefonouser).matches()) {
                            if (Patterns.EMAIL_ADDRESS.matcher(correouser).matches()) {
                                // Realizar la consulta si el teléfono ya está en uso
                                realizarConsulta(correouser, telefonouser, nameuser, passworduser);
                            } else {
                                mostrarMensaje("El correo es inválido");
                            }
                        } else {
                            mostrarMensaje("El número de teléfono debe contener 10 dígitos");
                        }
                    } else {
                        mostrarMensaje("Las contraseñas deben coincidir");
                    }
                } else {
                    mostrarMensaje("La contraseña debe tener al menos 8 caracteres (incluyendo mayúsculas, minúsculas, números y símbolos especiales)");
                }
            } else {
                mostrarMensaje("Los campos no deben estar vacíos");
            }
        });

        // TextWatcher para validar contraseña mientras se escribe
        reg_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validarPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return root;
    }

    // Método para validar contraseña
    private void validarPassword() {
        String validarpass = reg_password.getText().toString().trim();
        TextView avisopass = getView().findViewById(R.id.password_validation_message);

        if (reg_password.hasFocus()) {
            if (!validarpass.matches(regexPassword)) {
                avisopass.setVisibility(View.VISIBLE);
                avisopass.setText("La contraseña debe tener 8 caracteres (incluyendo mayúsculas, minúsculas, números y símbolos como @#$%^&+=_- ).");
            } else {
                avisopass.setVisibility(View.VISIBLE);
                avisopass.setText("Contraseña fuerte ");
            }
        } else {
            avisopass.setVisibility(View.GONE);
        }
    }

    // Método para realizar la consulta si el teléfono ya está en uso
    private void realizarConsulta(String correouser, String telefonouser, String nameuser, String passworduser) {
        // Realizar la consulta en la base de datos
        mFirestore.collection("usuarios").whereEqualTo("telefono", telefonouser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    registrarUsuario(nameuser, correouser, telefonouser, passworduser);
                } else {
                    mostrarMensaje("El número de teléfono ya está en uso");
                }
            } else {
                mostrarMensaje("Error al consultar la base de datos");
            }
        });
    }

    // Método para registrar un nuevo usuario
    private void registrarUsuario(String nameuser, String correouser, String telefonouser, String passworduser) {
        mAuth.createUserWithEmailAndPassword(correouser, passworduser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                String id = user.getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("nombre", nameuser);
                map.put("correo", correouser);
                map.put("telefono", telefonouser);

                mFirestore.collection("usuarios").document(id).set(map).addOnSuccessListener(unused -> {
                    user.sendEmailVerification();

                    redireccionarMain(); // Mover aquí la redirección
                    mostrarMensaje("Usuario registrado con éxito");

                }).addOnFailureListener(e -> mostrarMensaje("Error al guardar los datos"));
            } else {
                mostrarMensaje("Ya existe una cuenta registrada con este correo electrónico");
            }
        });
    }
    private void redireccionarMain() {
        // Obtener el contexto de la actividad
        Intent intent = new Intent(getActivity(), MainActivity.class);
        // Limpiar las actividades previas
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Método para mostrar mensajes Toast
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
