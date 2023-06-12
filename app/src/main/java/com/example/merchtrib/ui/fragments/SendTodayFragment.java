package com.example.merchtrib.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.merchtrib.ui.adapters.TasksAdapter;
import com.example.merchtrib.ui.objects.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SendTodayFragment extends Fragment {

    public ArrayList<Task> TASKS = new ArrayList<Task>();
    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    String userID, companyID;
    boolean isAdmin;
    RecyclerView lv;
    ValueEventListener tasksListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_today, container, false);

        lv = v.findViewById(R.id.list_of_tasks);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(),
                llm.getOrientation());
        lv.addItemDecoration(dividerItemDecoration);
        lv.setLayoutManager(llm);
        TextView emptyText = v.findViewById(R.id.emptyText);

        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");
        companyID = sharedPreferences.getString("companyID", "");
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ProgressBar progressBar = v.findViewById(R.id.progress_circular);


        tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TASKS = new ArrayList<>();
                // Get Post object and use the values to update the UI
                for (DataSnapshot dataTask : dataSnapshot.getChildren()) {
                    if (isAdmin) {
                        for (DataSnapshot userTasks : dataTask.getChildren()) {
                            Task currentTask = userTasks.getValue(Task.class);
                            if (Objects.equals(currentTask != null ? currentTask.getStatus() : null, "sent")) {
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
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        if (isAdmin) {
            mDatabase.child("tasks/" + companyID).addValueEventListener(tasksListener);
        } else {
            mDatabase.child("tasks/" + companyID + "/" + userID).orderByChild("status").equalTo("sent").addValueEventListener(tasksListener);
        }
        lv.setAdapter(new TasksAdapter(getContext() , TASKS, isAdmin, userID, companyID));


        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (toolbar != null) {
            toolbar.setTitle("История");
            if (activity != null) {
                activity.setSupportActionBar(toolbar);
            }
        }

        return v;

    }

    @Override
    public void onDestroy() {
        if (isAdmin) {
            mDatabase.child("tasks/" + companyID).removeEventListener(tasksListener);
        } else {
            mDatabase.child("tasks/" + companyID + "/" + userID).orderByChild("status").equalTo("sent").removeEventListener(tasksListener);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}