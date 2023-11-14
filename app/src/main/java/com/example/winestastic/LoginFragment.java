package com.example.winestastic;

import static android.service.controls.ControlsProviderService.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class LoginFragment extends Fragment {


    EditText log_correo,log_pass;
    TextView recuperarpass,regwhit;
    ImageView fb,gmail;
    Button btnlogin;
    float op = 0;
    String correo,password;
    FirebaseFirestore mFirestore;

    //Uso de la API mAuth de firebase para el inicio de sesion
    FirebaseAuth mAuth;

    //Variables para el funcionamiento de inicio de sesion con google
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        log_correo = root.findViewById(R.id.log_corre);
        log_pass = root.findViewById(R.id.log_pass);
        recuperarpass = root.findViewById(R.id.log_recuperarpass);
        regwhit = root.findViewById(R.id.regwhit);
        btnlogin = root.findViewById(R.id.btn_login);
        fb = root.findViewById(R.id.reg_fb);
        gmail = root.findViewById(R.id.reg_gmail);
        mFirestore = FirebaseFirestore.getInstance();


        //-------------Servicios Google----------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


        //Configuracion para el uso de inicio de sesion con google

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //-------------fin de Servicios Google----------------

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = log_correo.getText().toString().trim();
                password = log_pass.getText().toString().trim();

                if(!correo.isEmpty() && !password.isEmpty()){
                    loginuser(correo,password);
                }else{
                    mostrarMensaje("Los campos no deben de estar vacios");
                }
            }
        });

        recuperarpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getContext(),RecuperarActivity.class));
            }
        });
        root.post(() -> setViewAnimations());
        return root;
    }


    //----------Metodos para iniciar sesion con Google------------------

    //Inicio de sesio con Google

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        if(user != null){
            redireccionarMain();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }



    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre = user.getDisplayName();

                                Map<Object, String> map = new HashMap<>();
                                map.put("id", uid);
                                map.put("nombre", nombre);
                                map.put("correo", correo);
                                map.put("telefono", "");

                                mFirestore.collection("usuarios").document(uid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Redirige solo cuando la creación de la cuenta sea exitosa
                                        Toast.makeText(getActivity(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                        redireccionarMain();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }else{
                            updateUI(null);
                        }


                    }
                });
    }



    //----------Fin de Metodos para iniciar sesion con Google------------------


    //----------Metodos para iniciar sesion con email y contraseña------------------

    //Inicio de sesion con email y contraseña
    private void loginuser(String correo, String password){
        mAuth.signInWithEmailAndPassword(correo,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),MainActivity.class));
                    mostrarMensaje("Bienvenido");
                }else{
                    mostrarMensaje("Error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostrarMensaje("El correo o contraseña son incorrectos");
            }
        });

    }
    //Fin de Inicio de sesion con email y contraseña

    //----------Fin de Metodos para iniciar sesion con email y contraseña------------------

    //Mostrar mensajes
    private void mostrarMensaje(String mensaje){
        Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
    }
    //Fin Mostrar mensajes

    //Animaciones
    private void setViewAnimations() {
        setViewAnimation(log_correo, 300);
        setViewAnimation(log_pass, 400);
        setViewAnimation(recuperarpass, 400);
        setViewAnimation(btnlogin, 500);
        setViewAnimation(regwhit, 500);
        setViewAnimation(fb, 600);
        setViewAnimation(gmail, 600);
    }

    private void setViewAnimation(View view, int startDelay) {
        view.setTranslationX(800);
        view.setAlpha(op);
        view.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(800)
                .setStartDelay(startDelay)
                .start();
    }
    //Fin de Animaciones

    private void redireccionarMain(){
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}