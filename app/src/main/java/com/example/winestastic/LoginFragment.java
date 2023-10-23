package com.example.winestastic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginFragment extends Fragment {


    EditText log_correo,log_pass;
    TextView recuperarpass;

    Button login;

    float op = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_login, container, false);

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

        return root;
    }
}