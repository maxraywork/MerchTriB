package com.example.merchtrib.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.merchtrib.R;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getEmojiByUnicode(0x1F62D));
            setSupportActionBar(toolbar);
        }

        getWindow().setStatusBarColor(getResources().getColor(R.color.Error, this.getTheme()));
    }

    public String getEmojiByUnicode(int cry_smile){
        return new String(Character.toChars(cry_smile));
    }

    public void repeat_error(View view) {
        Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}