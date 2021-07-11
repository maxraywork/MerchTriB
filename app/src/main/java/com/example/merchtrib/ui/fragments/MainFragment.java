package com.example.merchtrib.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.activities.CheckGeoActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    GoogleMap map;
    private SupportMapFragment mapFragment;

    Intent intent = null;
    private static final List<Cat> cats = new ArrayList<Cat>();

    static {
        cats.add(new Cat("ООО “Фермер”", "Калининград, Горького,76, 2"));
        cats.add(new Cat("ООО “Виктория”", "Калининград, Южный, 2"));
        cats.add(new Cat("“Причал”", "Калининград, Горького,43, 2"));
        cats.add(new Cat("ООО “Макро”", "Калининград, Зеленая, 15, 64"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayAdapter<MainFragment.Cat> adapter = new CatAdapter(getContext());
        ListView lv = (ListView) v.findViewById(R.id.list_of_tasks);
        lv.setAdapter(adapter);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM", Locale.getDefault());
        String date = formatter.format(new Date());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (toolbar != null) {
            toolbar.setTitle(date);
            activity.setSupportActionBar(toolbar);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), CheckGeoActivity.class);
                startActivity(intent);

            }
        });


        return v;

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Заглушка, работа с меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main_add) {
            Toast.makeText(getContext(), "Добавить точку", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_main_logOut) {
            Toast.makeText(getContext(), "Выйти", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    private static class Cat {
        public final String name;
        public final String gender;

        public Cat(String name, String gender) {
            this.name = name;
            this.gender = gender;
        }
    }

    private class CatAdapter extends ArrayAdapter<MainFragment.Cat> {

        public CatAdapter(Context context) {
            super(context, R.layout.main_task_list_item, cats);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainFragment.Cat cat = getItem(position);


            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.main_task_list_item, null);

            }
            ImageButton imageButton = convertView.findViewById(R.id.task_item_button);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("https://goo.gl/maps/KH3ySujVXzdRizYv6");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            ((TextView) convertView.findViewById(R.id.list_item_name))
                    .setText(cat.name);
            ((TextView) convertView.findViewById(R.id.list_item_link))
                    .setText(cat.gender);


            return convertView;
        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}