package com.example.merchtrib.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Мерчандайзер");
            setSupportActionBar(toolbar);

        }

        email = findViewById(R.id.login);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
// Нажатие на кнопку регистрации
        loginButton.setOnClickListener(v -> {
            if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                // если поля не пустые
                if (isValid(email.getText().toString())) {
                    // Если почта верна
                    if (password.getText().toString().length() >= 6) {
                        //Если пароль больше 6 символов
                        loginButton.setClickable(false);
                        Toast.makeText(getApplicationContext(), "Проверяем...", Toast.LENGTH_SHORT).show();
                        //Войти
                        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                //Зарегистрировать
                                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            FirebaseDatabase.getInstance().getReference("users").child(user.getEmail().replace("@", "").replace(".", "").toLowerCase()).child("company").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue(String.class) == null) {
                                                        FirebaseDatabase.getInstance().getReference("users").child(user.getEmail().replace("@", "").replace(".", "")).setValue(new User(null, true));
                                                    }
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + task.getException() + " \n " + task2.getException(), Toast.LENGTH_LONG).show();
                                        email.setText("");
                                        password.setText("");
                                    }
                                });
                            }
                        });

                        loginButton.setClickable(true);
                    } else {
                        Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_LONG).show();
                        password.setText("");
                    }
                } else {
                    Toast.makeText(this, "E-mail не вырный", Toast.LENGTH_LONG).show();
                    email.setText("");
                    password.setText("");
                }
            } else {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show();
            }

        });
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}