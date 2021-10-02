package com.example.merchtrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.merchtrib.ui.fragments.HistoryFragment;
import com.example.merchtrib.ui.fragments.MainFragment;
import com.example.merchtrib.ui.fragments.SendTodayFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static Activity BackActivity;

    Fragment frag_main = new MainFragment();
    Fragment frag_send_today = new SendTodayFragment();
    Fragment frag_history = new HistoryFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackActivity = this;
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container_main) == null) {
            // start new Activity
        loadFragment(frag_main);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.main);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.send_today:
                    loadFragment(frag_send_today);
                    return true;
                case R.id.main:
                    loadFragment(frag_main);
                    return true;
                case R.id.history:
                    loadFragment(frag_history);
                    return true;
            }
            return false;
        });


    }


    public void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_main, fragment);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}