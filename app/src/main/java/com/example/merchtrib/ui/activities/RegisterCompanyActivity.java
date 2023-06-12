package com.example.merchtrib.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.Company;
import com.example.merchtrib.ui.objects.CompanyUser;
import com.example.merchtrib.ui.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterCompanyActivity extends AppCompatActivity {
    EditText name;
    EditText address;
    EditText phone;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

        name = findViewById(R.id.company);
        submit = findViewById(R.id.submit);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Регистрация компании");
            setSupportActionBar(toolbar);
        }

        //Зарегистрировать компанию
        submit.setOnClickListener(v -> {
            submit.setClickable(false);
            String title = name.getText().toString();
            if (!title.isEmpty() && title.length() >= 2) {

                FirebaseDatabase mBase = FirebaseDatabase.getInstance();
                FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
                String companyKey = mBase.getReference("companies").push().getKey();

                Company company = new Company(title, null, null, address.getText().toString(), phone.getText().toString());
                if (userFirebase != null) {
                    company.addUser(userFirebase.getUid(), new CompanyUser(userFirebase.getEmail()));
                }

                if (companyKey != null) {
                    mBase.getReference("companies").child(companyKey).setValue(company).addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            mBase.getReference("users").child(userFirebase.getUid()).child("companyID").setValue(companyKey).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + task.getException(), Toast.LENGTH_LONG).show();
                                    submit.setClickable(true);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
                        }

                    });
                }


            } else {
                Toast.makeText(getApplicationContext(), "Название должно быть больше двух букв", Toast.LENGTH_LONG).show();
                submit.setClickable(true);
            }
        });
    }


}