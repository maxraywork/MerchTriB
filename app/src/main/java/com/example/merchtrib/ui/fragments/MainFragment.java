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
import com.example.merchtrib.ui.activities.ErrorActivity;
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
    String userID, companyID;

    ValueEventListener eventListener;
    boolean isAdmin;
    RecyclerView lv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

//        user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");
        companyID = sharedPreferences.getString("companyID", "");
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

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

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TASKS = new ArrayList<>();
                // Get Post object and use the values to update the UI
                for (DataSnapshot dataTask : dataSnapshot.getChildren()) {
                    if (isAdmin) {
                        for (DataSnapshot userTasks : dataTask.getChildren()) {
                                Task currentTask = userTasks.getValue(Task.class);
                                if (Objects.equals(currentTask != null ? currentTask.getStatus() : null, "created")) {
                                    currentTask.setUserID(dataTask.getKey());
                                    TASKS.add(currentTask);
                                }
                        }

                    } else {
                        Task currentTask = dataTask.getValue(Task.class);
                        currentTask.setUserID(userID);
                        TASKS.add(currentTask);
                    }

                }
                lv.setAdapter(new TasksAdapter(getContext(), TASKS, isAdmin, userID, companyID));
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
//                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        if (isAdmin) {
            mDatabase.child("tasks/" + companyID).addValueEventListener(eventListener);
        } else {
            mDatabase.child("tasks/" + companyID + "/" + userID).orderByChild("status").equalTo("created").addValueEventListener(eventListener);
        }
        lv.setAdapter(new TasksAdapter(getContext(), TASKS, isAdmin, userID, companyID));


        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM", Locale.getDefault());
        String date = formatter.format(new Date());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (toolbar != null) {
//            if (isAdmin && companyNameOriginal != null) {
//                toolbar.setTitle(companyNameOriginal);
//            } else {
            toolbar.setTitle(date);
//            }
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
            mDatabase.removeEventListener(eventListener);
            sharedPreferences.edit().putString("companyID", null).putString("userID", null).putString("email", null).putBoolean("isAdmin", false).apply();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            FirebaseAuth.getInstance().signOut();
            Objects.requireNonNull(getActivity()).finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}