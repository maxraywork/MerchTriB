package com.example.merchtrib.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity {

    EditText name, address, addressLink, comment;
    Button addButton;

    String companyID;
    String userID;

    SharedPreferences user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //получаем ссылку для работы с базой данных
        user = getSharedPreferences("user", MODE_PRIVATE);
        companyID = user.getString("companyID", "");
        userID = user.getString("userID", "");
        mDatabaseReference = mFirebaseDatabase.getReference("tasks/" + companyID + "/" + userID).push().getRef();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Добавление задания");
            setSupportActionBar(toolbar);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        addressLink = findViewById(R.id.addressLink);
        addButton = findViewById(R.id.addButton);
        comment = findViewById(R.id.comment);

    }


    public void addButtonClickHandler(View view) {
        // Обрабатываем нажатие на кнопку
        String nameFinal = name.getText().toString();
        String addressFinal = address.getText().toString();
        String addressLinkFinal = addressLink.getText().toString();
        String commentFinal = comment.getText().toString();

        if (!nameFinal.isEmpty() && !addressFinal.isEmpty()) {
            Task task = new Task(mDatabaseReference.getKey(), nameFinal, addressFinal, null, "created", null);
            if (!addressLinkFinal.isEmpty()) {
                task.setAddressLink(addressLinkFinal);
            }
            if (!commentFinal.isEmpty()) {
                task.setComment(commentFinal);
            }
            mDatabaseReference.setValue(task);
            finish();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}