package com.example.merchtrib.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.adapters.GalleryAdapter;
import com.example.merchtrib.ui.adapters.TasksAdapter;
import com.example.merchtrib.ui.objects.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskActivity extends AppCompatActivity implements GalleryAdapter.OnImageListener {

    private RecyclerView gvGallery;
    private GalleryAdapter galleryAdapter;
    private FloatingActionButton addPhotoButton;

    public ArrayList<Uri> mArrayUri = new ArrayList<>();
    public ArrayList<String> mArrayUriDone = new ArrayList<>();
    public ArrayList<Uri> mArrayUriAbsolute = new ArrayList<>();
    public ArrayList<Uri> mArrayLocal = new ArrayList<>();
    int PICK_IMAGE_MULTIPLE = 1;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private ProgressBar progressBar;

    GalleryAdapter.OnImageListener context;
    Context origContext;

    String title = "Ошибка",
            idTask = "",
            type = "current",
            companyName, userEmail;
TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        context = this;
        origContext = this;

        emptyText = findViewById(R.id.emptyText);
        Intent intent = getIntent();
        title = intent.getStringExtra("name");
        idTask = intent.getStringExtra("id");
        type = intent.getStringExtra("type") == null ? "current" : intent.getStringExtra("type");
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        companyName = preferences.getString("companyName", "");
        userEmail = preferences.getString("userEmailShort", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/" + idTask);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("companies/" + companyName + "/tasks");

        progressBar = findViewById(R.id.progress_circular);
        gvGallery = findViewById(R.id.rev_photo);
        gvGallery.setLayoutManager(new GridLayoutManager(this, 3));
        addPhotoButton = findViewById(R.id.floatingActionButton);

        mDatabaseRef.child(type).child(idTask).child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayUriAbsolute = new ArrayList<>();
                // Get Post object and use the values to update the UI
                for (DataSnapshot dataUri : dataSnapshot.getChildren()) {
                    mArrayUriAbsolute.add(Uri.parse(dataUri.getValue(String.class)));
                }
                mArrayUri.clear();
                mArrayUri.addAll(mArrayUriAbsolute);
                mArrayUri.addAll(mArrayLocal);
                gvGallery.setAdapter(new GalleryAdapter(origContext, mArrayUri, context));
                progressBar.setVisibility(View.INVISIBLE);
                if (mArrayUri.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addPhotoButton.setOnClickListener(view -> requestPermissions(0));

    }

    private void requestPermissions(int i) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (i == 0) {
                    selectImagesFromGallery();
                } else if (i == 1) {
                    addImagesInRecycler(mArrayUri);
                }

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(TaskActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Если вы не дадите разрешения, то не сможете пользоваться этим приложением\n\nПожалуйста, дайте разрешения в [Настройки] > [Приложения]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectImagesFromGallery() {
        try {

            // initialising intent
            Intent intent = new Intent();

            // setting type to select to be image
            intent.setType("image/*");

            // allowing multiple image to be selected
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Выберите фотографии"), PICK_IMAGE_MULTIPLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
//                mArrayUri = (ArrayList<Uri>) mArrayUriAbsolute.clone();
                for (int i = 0; i < cout; i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    mArrayLocal.add(imageurl);
                }
                mArrayUri.clear();
                mArrayUri.addAll(mArrayUriAbsolute);
                mArrayUri.addAll(mArrayLocal);

                addImagesInRecycler(mArrayUri);

            } else if (data.getData() != null) {
                mArrayLocal.add(data.getData());
                mArrayUri.clear();
                mArrayUri.addAll(mArrayUriAbsolute);
                mArrayUri.addAll(mArrayLocal);
                addImagesInRecycler(mArrayUri);
            } else {
                Toast.makeText(this, "Вы не выбрали ни одной картинки, ошибка", Toast.LENGTH_LONG).show();
            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "Вы не выбрали ни одной картинки", Toast.LENGTH_LONG).show();
        }
    }

    private void addImagesInRecycler(ArrayList<Uri> mArrayUri) {
        GalleryAdapter galleryAdapter = new GalleryAdapter(origContext, mArrayUri, this);

        gvGallery.setAdapter(galleryAdapter);

    }

    @Override
    public void onImageClick(int position) {
        if (position < mArrayUriAbsolute.size()) {
            if (mArrayUri.get(position) == mArrayUriAbsolute.get(position)) {
                mDatabaseRef.child(type).child(idTask).child("images").child(String.valueOf(position)).removeValue();
            }
        } else {
            if (mArrayUri.get(position) == mArrayLocal.get(position - mArrayUriAbsolute.size())) {
                mArrayLocal.remove(position - mArrayUriAbsolute.size());
            }
            mArrayUri.clear();
            mArrayUri.addAll(mArrayUriAbsolute);
            mArrayUri.addAll(mArrayLocal);
            addImagesInRecycler(mArrayUri);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Заглушка, работа с меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_task_clear) {
            // Toast.makeText(getApplicationContext(), "Добавить примечание", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, SendingActivity.class));
            mArrayUri.clear();
            mArrayLocal.clear();
            for (Uri imageUri : mArrayUriAbsolute) {
                FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(imageUri)).delete();
            }
            mArrayUriAbsolute = new ArrayList<>();
            mDatabaseRef.child(type).child(idTask).child("images").removeValue();
            addImagesInRecycler(mArrayUri);
        } else if (id == R.id.menu_task_send) {
            mArrayUriDone.clear();
            Intent intentSending = new Intent(this, SendingActivity.class);
            startActivity(intentSending);
            for (int i = mArrayUriAbsolute.size(); i < mArrayUri.size(); i++) {
                uploadFiles(mArrayUri.get(i));
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFiles(Uri uri) {
        if (mArrayUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));


            fileReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uriAbsolute -> {
                            mArrayUriDone.add(uriAbsolute.toString());
                            mArrayUriAbsolute.add(uriAbsolute);
                            if (mArrayUriAbsolute.size() == mArrayUri.size()) {
                                ArrayList<String> data = new ArrayList<>();
                                for (int i = 0; i < mArrayUriAbsolute.size(); i++) {
                                    data.add(String.valueOf(mArrayUriAbsolute.get(i)));
                                }
                                mDatabaseRef.child(type).child(idTask).child("images").setValue(data);
                                if (type.equals("current")) {
                                    mDatabaseRef.child(type).child(idTask).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            mDatabaseRef.child("done").child(idTask).setValue(dataSnapshot.getValue());
                                            mDatabaseRef.child(type).child(idTask).removeValue();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                SendingActivity.activity.finish();
                                Toast.makeText(this, "Успешно отправлено", Toast.LENGTH_LONG).show();
                                this.finish();

                            }
                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Что-то пошло не так: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        mArrayUri = new ArrayList<>();
                        addImagesInRecycler(mArrayUri);
                    });
        } else {
            Toast.makeText(this, "Файл не выбран", Toast.LENGTH_LONG).show();
        }
    }

}