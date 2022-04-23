package com.example.merchtrib.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTaskActivity extends AppCompatActivity {

    EditText name, address, addressLink;
    Button addButton;

    String companyName;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //получаем ссылку для работы с базой данных
        companyName = getSharedPreferences("data", MODE_PRIVATE).getString("companyName", "");
        mDatabaseReference = mFirebaseDatabase.getReference("companies/" + companyName + "/tasks/current");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Добавление задания");
            setSupportActionBar(toolbar);
        }

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        addressLink = findViewById(R.id.addressLink);
        addButton = findViewById(R.id.addButton);

    }


    public void addButtonClickHandler(View view) {
        String nameFinal = name.getText().toString();
        String id = nameFinal + System.currentTimeMillis();
        String addressFinal = address.getText().toString();
        String addressLinkFinal = addressLink.getText().toString();

        if (!nameFinal.isEmpty() && !addressFinal.isEmpty()) {
            if (!addressLinkFinal.isEmpty()) {
                mDatabaseReference.child(id).setValue(new Task(id, nameFinal, addressFinal, addressLinkFinal, null));
            } else {
                mDatabaseReference.child(id).setValue(new Task(id, nameFinal, addressFinal, null, null));
            }
            finish();
        }

    }
}