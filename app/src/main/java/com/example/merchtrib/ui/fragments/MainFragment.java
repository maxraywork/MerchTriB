package com.example.merchtrib.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.activities.AddTaskActivity;
import com.example.merchtrib.ui.activities.LoginActivity;
import com.example.merchtrib.ui.activities.MainActivity;
import com.example.merchtrib.ui.activities.TaskActivity;
import com.example.merchtrib.ui.adapters.TasksAdapter;
import com.example.merchtrib.ui.objects.Task;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainFragment extends Fragment {

    public ArrayList<Task> TASKS = new ArrayList<>();
    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    String userEmail, companyName, companyNameOriginal;
    boolean isAdmin;
    private FirebaseUser user;
    RecyclerView lv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

//        user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("userEmailShort", "");
        companyName = sharedPreferences.getString("companyName", "");
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        companyNameOriginal = sharedPreferences.getString("companyNameOriginal", null);

        lv = v.findViewById(R.id.list_of_tasks);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(),
                llm.getOrientation());
        lv.addItemDecoration(dividerItemDecoration);
        lv.setLayoutManager(llm);

        TextView emptyText = v.findViewById(R.id.emptyText);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ProgressBar progressBar = v.findViewById(R.id.progress_circular);
        mDatabase.child("companies/" + companyName + "/tasks/current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TASKS = new ArrayList<>();
                // Get Post object and use the values to update the UI
                for (DataSnapshot dataTask: dataSnapshot.getChildren()) {
                     TASKS.add(dataTask.getValue(Task.class));

                }
                lv.setAdapter(new TasksAdapter(getContext() , TASKS, "current", isAdmin, companyName));
                progressBar.setVisibility(View.INVISIBLE);
                if (!TASKS.isEmpty()) {
                    emptyText.setVisibility(View.INVISIBLE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        lv.setAdapter(new TasksAdapter(getContext() , TASKS, "current",  isAdmin, companyName));



        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM", Locale.getDefault());
        String date = formatter.format(new Date());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (toolbar != null) {
            if (isAdmin && companyNameOriginal != null) {
                toolbar.setTitle(companyNameOriginal);
            } else {
                toolbar.setTitle(date);
            }
            if (activity != null) {
                activity.setSupportActionBar(toolbar);
            }
        }

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
            startActivity(new Intent(this.getActivity(), AddTaskActivity.class));
        } else if (id == R.id.menu_main_logOut) {
            FirebaseAuth.getInstance().signOut();
            sharedPreferences.getAll().clear();
            startActivity(new Intent(getActivity(), MainActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}