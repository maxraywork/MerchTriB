package com.example.merchtrib.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.activities.AdminActivity;
import com.example.merchtrib.ui.activities.RegisterCompanyActivity;
import com.example.merchtrib.ui.activities.LoginActivity;
import com.example.merchtrib.ui.fragments.MainFragment;
import com.example.merchtrib.ui.fragments.SendTodayFragment;
import com.example.merchtrib.ui.objects.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static Activity BackActivity;

    Fragment frag_main = new MainFragment();
    Fragment frag_send_today = new SendTodayFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUser();


    }

    public void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // Проверить пользователя
            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(User.class) != null) {

                        User userData = dataSnapshot.getValue(User.class);
                        boolean isCompany = false;
                        if (userData != null) {
                            isCompany = userData.getCompanyID() != null && !userData.getCompanyID().isEmpty();
                        }
                        if (isCompany) {
                            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                            preferences.edit().putString("companyID", userData.getCompanyID()).putString("userID", user.getUid()).putString("email", user.getEmail()).putBoolean("isAdmin", userData.isAdmin()).apply();
                        }
                        if (userData.isAdmin()) {
                            if (isCompany) {
                                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                            } else {
                                startActivity(new Intent(getApplicationContext(), RegisterCompanyActivity.class));
                            }
                            finish();
                        } else {
                            updateUI();
                        }
                    } else {
                        user.delete();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void updateUI() {
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        BackActivity = this;
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container_main) == null) {
            // start new Activity
            loadFragment(frag_main);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.main);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.send_today:
                    loadFragment(frag_send_today);
                    return true;
                case R.id.main:
                    loadFragment(frag_main);
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