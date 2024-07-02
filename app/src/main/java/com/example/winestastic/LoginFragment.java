package com.example.winestastic;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    EditText log_correo, log_pass;
    ProgressBar pbProgressLogin;
    Button btnlogin;
    TextView recuperarpass, regwhit;
    GoogleSignInClient mGoogleSignInClient;
    FloatingActionButton gmail;
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    float op = 0;
    FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        log_correo = root.findViewById(R.id.log_corre);
        log_pass = root.findViewById(R.id.log_pass);
        recuperarpass = root.findViewById(R.id.log_recuperarpass);
        regwhit = root.findViewById(R.id.regwhit);
        btnlogin = root.findViewById(R.id.btn_login);
        gmail = root.findViewById(R.id.login_gmail);
        pbProgressLogin = root.findViewById(R.id.progress_login);
        mFirestore = FirebaseFirestore.getInstance();

        // Click listener para el botón de iniciar sesión
        btnlogin.setOnClickListener(view -> {
            String correo = log_correo.getText().toString().trim();
            String password = log_pass.getText().toString().trim();
            if (!correo.isEmpty() && !password.isEmpty()) {
                loginuser(correo, password);
            } else {
                mostrarMensaje("Los campos no deben estar vacíos");
            }
        });

        // Click listener para el botón de recuperar contraseña
        recuperarpass.setOnClickListener(view -> {
            getActivity();
            startActivity(new Intent(getContext(), RecuperarActivity.class));
        });

        // Inicialización de Google SignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // Click listener para el botón de iniciar sesión con Google
        root.findViewById(R.id.login_gmail).setOnClickListener(view -> signIn());

        // Llamada a setViewAnimations para iniciar las animaciones
        setViewAnimations();

        return root;
    }

    // Método para iniciar sesión con correo y contraseña
    private void loginuser(String correo, String password) {
        pbProgressLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pbProgressLogin.setVisibility(View.GONE);
                getActivity().finish();
                startActivity(new Intent(getContext(), MainActivity.class));
                mostrarMensaje("Bienvenido");
            } else {
                pbProgressLogin.setVisibility(View.GONE);
                mostrarMensaje("Error al iniciar sesión");
            }
        }).addOnFailureListener(e -> {
            pbProgressLogin.setVisibility(View.GONE);
            mostrarMensaje("El correo o la contraseña son incorrectos");
        });
    }

    // Método para mostrar mensajes Toast
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
    }

    // Método para iniciar sesión con Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Manejar error de Google Sign In
            }
        }
    }

    // Método para autenticar con Firebase usando Google
    private void firebaseAuthWithGoogle(String idToken) {
        pbProgressLogin.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pbProgressLogin.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre = user.getDisplayName();

                                Map<String, Object> map = new HashMap<>();
                                map.put("id", uid);
                                map.put("nombre", nombre);
                                map.put("correo", correo);
                                map.put("telefono", "");

                                mFirestore.collection("usuarios").document(uid).set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Redirige solo cuando la creación de la cuenta sea exitosa
                                                mostrarMensaje("Usuario registrado con éxito");
                                                redireccionarMain();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mostrarMensaje("Error al guardar datos");
                                            }
                                        });

                            } else {
                                // Si el usuario no es nuevo, redirigir directamente
                                redireccionarMain();
                            }
                        } else {
                            pbProgressLogin.setVisibility(View.GONE);
                            mostrarMensaje("Error al autenticar con Google");
                            updateUI(null);
                        }
                    }
                });
    }

    // Método para actualizar la interfaz de usuario después de la autenticación
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            redireccionarMain();
        }
    }

    // Método para redireccionar a MainActivity
    private void redireccionarMain() {
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    private void setViewAnimations() {
        setViewAnimation(log_correo, 300);
        setViewAnimation(log_pass, 400);
        setViewAnimation(recuperarpass, 400);
        setViewAnimation(btnlogin, 500);
        setViewAnimation(regwhit, 500);
        setViewAnimation(gmail, 600);
    }

    private void setViewAnimation(View view, int startDelay) {
        view.setTranslationX(800);
        view.setAlpha(op);
        view.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(startDelay).start();
    }

}