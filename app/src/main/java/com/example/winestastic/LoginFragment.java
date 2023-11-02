package com.example.winestastic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {


    EditText log_correo,log_pass;
    TextView recuperarpass;

    String correo,password;
    Button login;

    FirebaseAuth mAuth;

    float op = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        log_correo = root.findViewById(R.id.log_corre);
        log_pass = root.findViewById(R.id.log_pass);
        recuperarpass = root.findViewById(R.id.log_recuperarpass);
        login = root.findViewById(R.id.btn_login);


        log_correo.setTranslationX(800);
        log_pass.setTranslationX(800);
        recuperarpass.setTranslationX(800);
        login.setTranslationX(800);

        log_correo.setAlpha(op);
        log_pass.setAlpha(op);
        recuperarpass.setAlpha(op);
        login.setAlpha(op);

        log_correo.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        log_pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        recuperarpass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = log_correo.getText().toString().trim();
                password = log_pass.getText().toString().trim();

                if(!correo.isEmpty() && !password.isEmpty()){
                        loginuser(correo,password);
                }else{
                    Toast.makeText(getActivity(), "Los campos no deben estar vacíos", Toast.LENGTH_SHORT).show();
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
        return root;
    }

    private void loginuser(String correo, String password){
        mAuth.signInWithEmailAndPassword(correo,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    getActivity().finish();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    Toast.makeText(getActivity(), "Bienvenido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostrarAlerta();
            }
        });

    }


    private void mostrarAlerta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error");
        builder.setMessage("El correo o contraseña son incorrectos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }



}