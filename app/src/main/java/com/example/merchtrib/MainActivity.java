package com.example.merchtrib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.merchtrib.ui.activities.HistoryActivity;
import com.example.merchtrib.ui.activities.SendTodayActivity;
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
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Drawer result = null;
    private AccountHeader headerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM", Locale.getDefault());
        String date = formatter.format(new Date());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(date);
            setSupportActionBar(toolbar);
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withCompactStyle(true)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(R.drawable.ic_launcher_background))
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(true)
                .withSavedInstance(savedInstanceState)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_main).withIcon(R.drawable.ic_menu_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.menu_send_today).withIcon(R.drawable.ic_nav_send_today).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.menu_history).withIcon(R.drawable.ic_nav_history).withIdentifier(2).withSelectable(false),
                        new SectionDrawerItem().withName(R.string.nav_section_out),
                        new SecondaryDrawerItem().withName(R.string.nav_write_to_creator).withIcon(R.drawable.ic_nav_write_to_creator).withIdentifier(3)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(@NotNull View view) {

                    }

                    @Override
                    public void onDrawerSlide(@NotNull View view, float v) {

                    }

                    @Override
                    public void onDrawerClosed(@NotNull View view) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(@Nullable View view, int i, @NotNull IDrawerItem<?> drawerItem) {
                        Intent intent = null;
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(MainActivity.this, SendTodayActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(MainActivity.this, HistoryActivity.class);
                            }
                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }

                })
                .build();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
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