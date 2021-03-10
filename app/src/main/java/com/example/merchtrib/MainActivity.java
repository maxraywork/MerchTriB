package com.example.merchtrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merchtrib.ui.activities.CheckGeoActivity;
import com.example.merchtrib.ui.activities.HistoryActivity;
import com.example.merchtrib.ui.activities.SendTodayActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Intent intent = null;

    private static final List<Cat> cats = new ArrayList<Cat>();

    static {
        cats.add(new Cat("ООО “Фермер”", "Калининград, Горького,76, 2"));
        cats.add(new Cat("ООО “Виктория”", "Калининград, Южный, 2"));
        cats.add(new Cat("“Причал”", "Калининград, Горького,43, 2"));
        cats.add(new Cat("ООО “Макро”", "Калининград, Зеленая, 15, 64"));
        cats.add(new Cat("АОУ “Овощи”", "Калининград, Горького,76, 2"));
        cats.add(new Cat("“Маляева”", "Гусев, Улица 2"));
        cats.add(new Cat("ООО “Балтимол”", "кошечка"));
        cats.add(new Cat("АОУ “Овощи”", "Калининград, Горького,76, 2"));
        cats.add(new Cat("“Причал”", "Калининград, Горького,43, 2"));
        cats.add(new Cat("ООО “Фермер”", "Калининград, Горького,76, 2"));
        cats.add(new Cat("ООО “Балтимол”", "кошечка"));
        cats.add(new Cat("ООО “Макро”", "Калининград, Зеленая, 15, 64"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.main);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.send_today:
                       startActivity(new Intent(getApplicationContext(), SendTodayActivity.class));
                       overridePendingTransition(0,0);
                        finish();
                       return true;
                    case R.id.main:
                        return true;
                    case R.id.history:
                        startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        ArrayAdapter<Cat> adapter = new CatAdapter(this);
        ListView lv = (ListView) findViewById(R.id.list_of_tasks);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                intent = new Intent(MainActivity.this, CheckGeoActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });


        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM", Locale.getDefault());
        String date = formatter.format(new Date());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(date);
            setSupportActionBar(toolbar);
        }

    }

    private static class Cat {
        public final String name;
        public final String gender;

        public Cat(String name, String gender) {
            this.name = name;
            this.gender = gender;
        }
    }

    private class CatAdapter extends ArrayAdapter<Cat> {

        public CatAdapter(Context context) {
            super(context, R.layout.main_task_list_item, cats);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Cat cat = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.main_task_list_item, null);
            }
            ((TextView) convertView.findViewById(R.id.list_item_name))
                    .setText(cat.name);
            ((TextView) convertView.findViewById(R.id.list_item_link))
                    .setText(cat.gender);
            return convertView;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    // Заглушка, работа с меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Заглушка, работа с меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main_add) {
            Toast.makeText(getApplicationContext(), "Добавить точку", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_main_logOut) {
            Toast.makeText(getApplicationContext(), "Выйти", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }




}