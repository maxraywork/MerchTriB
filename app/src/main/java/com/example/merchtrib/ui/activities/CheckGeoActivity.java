package com.example.merchtrib.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.merchtrib.R;

import java.util.Timer;
import java.util.TimerTask;

public class CheckGeoActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 500;
    Timer t = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_geo);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.wait);
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);


        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t.cancel();

                        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                        startActivity(intent);
                        MainActivity.BackActivity.finish();
                        finish();

                    }
                });
            }
        }, SPLASH_TIME_OUT, SPLASH_TIME_OUT);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        t.cancel();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        t.cancel();
    }
}