package com.example.winestastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {



    private MeowBottomNavigation bottomNavigation;
    Button cerrar;
    RelativeLayout  menu, home, calendar, map;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        cerrar = findViewById(R.id.cerrar_sesion);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        calendar = findViewById(R.id.calendar);
        map = findViewById(R.id.map);
        mAuth = FirebaseAuth.getInstance();




        bottomNavigation.show(2,true);


        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.menuanvorgesa));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_calendar_month_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.baseline_public_24));

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){


                    case 1:

                        menu.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;

                    case 2:

                        menu.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        calendar.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;

                    case 3:

                        menu.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        calendar.setVisibility(View.VISIBLE);
                        map.setVisibility(View.GONE);

                        break;

                    case 4:

                        menu.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);

                        break;

                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 1:

                        menu.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 2:

                        menu.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        calendar.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 3:

                        menu.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        calendar.setVisibility(View.VISIBLE);
                        map.setVisibility(View.GONE);

                        break;
                }

                return null;
            }
        });


        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 4:

                        menu.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        calendar.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);

                        break;
                }

                return null;
            }
        });






        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });




        Date today = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 30);

        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(today, nextYear.getTime()).withSelectedDate(today);

        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                Toast.makeText(MainActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });




    }

    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            irLogin();
        }
    }

    private void logout(){
        mAuth.signOut();
        irLogin();
    }

    private void irLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }





    
}