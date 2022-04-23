package com.example.merchtrib.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.Company;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterCompanyActivity extends AppCompatActivity {
EditText name;
Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

        name = findViewById(R.id.company);
        submit = findViewById(R.id.submit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Регистрация компании");
            setSupportActionBar(toolbar);

        }


        submit.setOnClickListener(v -> {
            submit.setClickable(false);
            String data = name.getText().toString();
            if (!data.isEmpty() && data.length() >= 2) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace("@", "").replace(".", "").toLowerCase();
                FirebaseDatabase mBase = FirebaseDatabase.getInstance();
                HashMap user = new HashMap();
                String result = transliterate(data);
                String companyName = result + System.currentTimeMillis();
                user.put("company", companyName);

                mBase.getReference("users").child(email).updateChildren(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mBase.getReference("companies").child(companyName).setValue(new Company(data, null, null)).addOnCompleteListener(t -> {
                            finish();
                        });
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + task.getException(), Toast.LENGTH_LONG).show();
                        submit.setClickable(true);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Название должно быть больше двух букв", Toast.LENGTH_LONG).show();
                submit.setClickable(true);
            }
        });
    }


    public static String transliterate(String message){
        char[] abcCyr =   {' ','а','б','в','г','д','е','ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц','ч', 'ш','щ','ъ','ы','ь','э', 'ю','я','А','Б','В','Г','Д','Е','Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч','Ш', 'Щ','Ъ','Ы','Ь','Э','Ю','Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        String[] abcLat = {"","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","sch", "","i", "","e","ju","ja","A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Sch", "","I", "","E","Ju","Ja","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }
}