package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    FloatingActionButton fb,gmail;
    float op = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        fb = findViewById(R.id.fab_fb);
        gmail = findViewById(R.id.fab_gmail);


        tabLayout.addTab(tabLayout.newTab().setText("Iniciar sesión"));
        tabLayout.addTab(tabLayout.newTab().setText("Crear cuenta"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        fb.setTranslationY(300);
        gmail.setTranslationY(300);
        tabLayout.setTranslationY(300);

        fb.setAlpha(op);
        gmail.setAlpha(op);
        tabLayout.setAlpha(op);

        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        gmail.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();

    }
}