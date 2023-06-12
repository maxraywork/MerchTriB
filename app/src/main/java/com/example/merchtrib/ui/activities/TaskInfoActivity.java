package com.example.merchtrib.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.objects.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TaskInfoActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    String taskID, userID, companyID;
    boolean isAdmin;
    ProgressBar progress_circular;
    TextInputLayout commentLayout, addressLayout, titleLayout, statusLayout, createdAtLayout, addressLinkLayout;
    TextInputEditText commentEdit, addressEdit, titleEdit, statusEdit, createdAtEdit, addressLinkEdit;

    HashMap<String, Object> changedHashMap = new HashMap<>();
    Task task;
    Menu menu;
    boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Информация");
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);


        Intent intent = getIntent();
        taskID = intent.getStringExtra("taskID");
        userID = intent.getStringExtra("userID");
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        companyID = sharedPreferences.getString("companyID", "");
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        mDatabase = FirebaseDatabase.getInstance().getReference("tasks/" + companyID + "/" + userID + "/" + taskID);

        progress_circular = findViewById(R.id.progress_circular);
        commentLayout = findViewById(R.id.commentLayout);
        commentEdit = findViewById(R.id.comment);
        addressLayout = findViewById(R.id.addressLayout);
        addressEdit = findViewById(R.id.address);
        addressLinkEdit = findViewById(R.id.addressLink);
        addressLinkLayout = findViewById(R.id.addressLinkLayout);
        statusLayout = findViewById(R.id.statusLayout);
        statusEdit = findViewById(R.id.status);
        titleLayout = findViewById(R.id.titleLayout);
        titleEdit = findViewById(R.id.title);
        createdAtLayout = findViewById(R.id.createdAtLayout);
        createdAtEdit = findViewById(R.id.createdAt);
        isFirstTime = true;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progress_circular.setVisibility(View.GONE);
                if (dataSnapshot.getValue(Task.class) != null) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        currentFocus.clearFocus();
                    }
                    task = dataSnapshot.getValue(Task.class);
                    updateUI();
                    if (isFirstTime) {
                        commentEdit.addTextChangedListener(generateTextWatcher("comment", task.getComment() != null ? task.getComment() : ""));
                        addressEdit.addTextChangedListener(generateTextWatcher("address", task.getAddress()));
                        addressLinkEdit.addTextChangedListener(generateTextWatcher("addressLink", task.getAddressLink()));
                        titleEdit.addTextChangedListener(generateTextWatcher("title", task.getTitle()));
                        isFirstTime = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private TextWatcher generateTextWatcher(String id, String originalValue) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(originalValue)) {
                    changedHashMap.put(id, editable.toString());
                    checkIfShowMenu();
                } else if (editable.toString().equals(originalValue)) {
                    changedHashMap.remove(id);
                }
                checkIfShowMenu();
            }
        };
    }

    private void checkIfShowMenu() {
        menu.setGroupVisible(R.id.menu_save_group, changedHashMap.size() > 0);
    }

    public void updateUI() {
        if (task.getAddress() != null) {
            if (isAdmin) {
                addressLayout.setEnabled(true);
            }
            addressEdit.setText(task.getAddress());
        } else {
            if (!isAdmin) {
                addressLayout.setEnabled(false);
            }
            addressEdit.setText("");
        }

        if (task.getTitle() != null) {
            if (isAdmin) {
                titleLayout.setEnabled(true);
            }
            titleEdit.setText(task.getTitle());
        } else {
            if (!isAdmin) {
                titleLayout.setEnabled(false);
            }
            titleEdit.setText("");
        }

        if (task.getStatus() != null) {
//            if (isAdmin) {
//                statusLayout.setEnabled(true);
//            }
            statusEdit.setText(task.getStatus());
        }
        else {
//            if (!isAdmin) {
//                statusLayout.setEnabled(false);
//            }
            statusEdit.setText("");
        }

        if (task.getTitle() != null) {
            if (isAdmin) {
                titleLayout.setEnabled(true);
            }
            titleEdit.setText(task.getTitle());
        } else {
            if (!isAdmin) {
                titleLayout.setEnabled(false);
            }
            titleEdit.setText("");
        }

        if (task.getAddressLink() != null) {
            addressLinkEdit.setText(task.getAddressLink());
        } else {
            addressLinkEdit.setText("");
        }
        if (isAdmin) {
            addressLinkLayout.setEnabled(true);
        }


        createdAtEdit.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(task.getTimestampCreatedLong())));

        if (task.getComment() != null) {
            commentEdit.setText(task.getComment());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_save) {
            uploadToDatabase();
        } else if (id == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    public void uploadToDatabase() {
        if (changedHashMap.size() > 0) {
            mDatabase.updateChildren(changedHashMap).addOnCompleteListener(task -> {
                Toast.makeText(this, "Успешно сохранено", Toast.LENGTH_SHORT).show();
                changedHashMap.clear();
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_info, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (changedHashMap.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle("Сохранить изменения")
                    .setMessage("Вы хотите сохранить изменения?")
                    .setPositiveButton("Да", (dialogIn, idIn) -> {
                        uploadToDatabase();
                        dialogIn.cancel();
                        super.onBackPressed();
                    }).setNegativeButton("Нет", (dialogIn, idIn) -> {
                        dialogIn.cancel();
                        super.onBackPressed();
                    }).setNeutralButton("Отмена", (dialogIn, idIn) -> {
                        dialogIn.cancel();
                    }).create();
            dialog.setOnShowListener(arg0 -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary));
            });
            builder.show();
        } else {
            super.onBackPressed();
        }
    }
}