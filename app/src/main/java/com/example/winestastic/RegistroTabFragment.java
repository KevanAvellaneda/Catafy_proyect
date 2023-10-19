package com.example.winestastic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class RegistroTabFragment extends Fragment {

    EditText nombre,email,telefono,password,confpassword;
    Button bntregistro;
    float op = 0;

    public View onCreativeView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.registro_tab_fragment, container, false);

        nombre = root.findViewById(R.id.nombre_reg);
        email = root.findViewById(R.id.correo_reg);
        telefono = root.findViewById(R.id.telefono_reg);
        password = root.findViewById(R.id.password_reg);
        confpassword = root.findViewById(R.id.confirm_reg);
        bntregistro =  root.findViewById(R.id.btn_registro);

        nombre.setTranslationY(800);
        email.setTranslationY(800);
        telefono.setTranslationY(800);
        password.setTranslationY(800);
        confpassword.setTranslationY(800);
        bntregistro.setTranslationY(800);


        nombre.setAlpha(op);
        email.setAlpha(op);
        telefono.setAlpha(op);
        password.setAlpha(op);
        confpassword.setAlpha(op);
        bntregistro.setAlpha(op);

        nombre.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        email.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        telefono.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        password.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        confpassword.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();
        bntregistro.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();

        return root;
    }

}
