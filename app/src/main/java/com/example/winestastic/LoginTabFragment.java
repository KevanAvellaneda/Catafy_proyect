package com.example.winestastic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginTabFragment extends Fragment {


    EditText email,password;
    TextView recuperarpass;
    Button btnlogin;
    float op = 0;



    public View onCreativeView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        email = root.findViewById(R.id.login_email);
        password = root.findViewById(R.id.login_password);
        recuperarpass = root.findViewById(R.id.login_recuperar);
        btnlogin = root.findViewById(R.id.btn_ingresar);

        email.setTranslationX(800);
        password.setTranslationX(800);
        recuperarpass.setTranslationX(800);
        btnlogin.setTranslationX(800);

        email.setAlpha(op);
        password.setAlpha(op);
        recuperarpass.setAlpha(op);
        btnlogin.setAlpha(op);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        recuperarpass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        btnlogin.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();


        return root;
    }
}