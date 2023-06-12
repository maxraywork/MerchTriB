package com.example.merchtrib.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.CompanyUser;
import com.example.merchtrib.ui.objects.User;
import com.example.merchtrib.ui.objects.UsersWaitListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;
    Button signUpButton;
    TextView resetPassword;
    FirebaseAuth mAuth;
    boolean isInResetPassword;

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
        resetPassword = findViewById(R.id.resetPassword);
        signUpButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();
        // Нажатие на кнопку регистрации
        loginButton.setOnClickListener(v -> {
            if (isInResetPassword) {
                callResetPassword();
            } else {
                login();
            }
        });
        signUpButton.setOnClickListener(v -> {
            register();
        });

        isInResetPassword = false;
        resetPassword.setOnClickListener(view -> {
            resetPasswordClickHandler();
        });


    }

    public void callResetPassword() {
        if (!email.getText().toString().isEmpty()) {
            // если поля не пустые
            if (isValid(email.getText().toString())) {
                mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Письмо для восстановления пароля отправлено", Toast.LENGTH_LONG).show();
                                isInResetPassword = false;
                                resetPasswordClickHandler();
                            }
                        });
            } else {
                Toast.makeText(this, "E-mail не вырный", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "E-mail не заполнен", Toast.LENGTH_LONG).show();
        }
    }

    public void resetPasswordClickHandler() {
        //Return to regular login
        if (isInResetPassword) {
            isInResetPassword = false;
            password.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            resetPassword.setText(R.string.resetPassword);
            loginButton.setText(R.string.logIn);
            return;
        }
        // Check if email field is empty, then hide password field and change text
        if ((!isValid(email.getText().toString()) || email.getText().toString().isEmpty()) && !isInResetPassword) {
            isInResetPassword = true;
            password.setVisibility(View.GONE);
            resetPassword.setText(R.string.returnToLogin);
            loginButton.setText(R.string.resetPasswordMainButton);
            signUpButton.setVisibility(View.GONE);
        } else {
            callResetPassword();
        }

    }

    public void login() {
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
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    loginButton.setClickable(true);
                } else {
                    Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_LONG).show();
//                    password.setText("");
                }
            } else {
                Toast.makeText(this, "E-mail не вырный", Toast.LENGTH_LONG).show();
//                email.setText("");
//                password.setText("");
            }
        } else {
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show();
        }

    }

    public void register() {
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            // если поля не пустые
            if (isValid(email.getText().toString())) {
                // Если почта верна
                if (password.getText().toString().length() >= 6) {
                    //Зарегистрировать
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            if (user != null) {
                                String shortEmail = user.getEmail().replace("@", "").replace(".", "").toLowerCase();
                                mDatabase.getReference("usersWaitList").child(shortEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UsersWaitListItem waitList = dataSnapshot.getValue(UsersWaitListItem.class);
                                        if (waitList != null && Objects.equals(waitList.getEmail(), user.getEmail())) {
                                            //Create employee
                                            mDatabase.getReference("users").child(user.getUid()).setValue(new User(waitList.getCompanyID(), false, user.getEmail()));
                                            mDatabase.getReference("companies").child(waitList.companyID).child("users").child(user.getUid()).setValue(new CompanyUser(user.getEmail()));
                                            mDatabase.getReference("usersWaitList/" + shortEmail).removeValue();
                                            mDatabase.getReference("companies/" + waitList.getCompanyID() + "/usersWaitList/" + shortEmail).removeValue();
                                        } else {
                                            //Create admin
                                            mDatabase.getReference("users").child(user.getUid()).setValue(new User(null, true, user.getEmail()));
                                        }
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), task2.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                    email.setText("");
//                                    password.setText("");
                        }
                    });
                } else {
                    Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_LONG).show();
//                    password.setText("");
                }
            } else {
                Toast.makeText(this, "E-mail не вырный", Toast.LENGTH_LONG).show();
//                email.setText("");
//                password.setText("");
            }
        } else {
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show();
        }
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