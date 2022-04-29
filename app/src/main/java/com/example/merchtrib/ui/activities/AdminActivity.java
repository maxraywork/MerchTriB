package com.example.merchtrib.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.fragments.MainFragment;
import com.example.merchtrib.ui.fragments.SendTodayFragment;
import com.example.merchtrib.ui.fragments.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {

    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    MainFragment mainFragment = new MainFragment();
    SendTodayFragment oldFragment = new SendTodayFragment();
    UsersFragment usersFragment = new UsersFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container_main) == null) {
            // start new Activity
            loadFragment(mainFragment);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.tasks);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tasks:
                    loadFragment(mainFragment);
                    return true;
                case R.id.users:
                    loadFragment(usersFragment);
                    return true;
                case R.id.tasks_old:
                    loadFragment(oldFragment);
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