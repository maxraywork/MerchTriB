package com.example.merchtrib.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class TaskActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private RecyclerView rcvPhoto;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Название магазина");
            setSupportActionBar(toolbar);
        }

        rcvPhoto = findViewById(R.id.rev_photo);
        imageAdapter = new ImageAdapter(this);
        constraintLayout = findViewById(R.id.add_photo);

        rcvPhoto.setAdapter(imageAdapter);

        constraintLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });

    }

    private void requestPermissions(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(TaskActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Если ты не дашь разрешения, то не можешь пользоваться этим приложением\n\nПожалуйста, дай разрешения в [Настройки] > [Приложения]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectImagesFromGallery() {
        TedBottomPicker.with(TaskActivity.this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                       if (uriList != null && !uriList.isEmpty()){
                           imageAdapter.setData(uriList);
                       }
                    }
                });
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

        if (id == R.id.menu_task_comment) {
            // Toast.makeText(getApplicationContext(), "Добавить примечание", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SendingActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}