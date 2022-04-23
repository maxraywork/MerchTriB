package com.example.merchtrib.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.adapters.UsersAdapter;
import com.example.merchtrib.ui.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class UsersFragment extends Fragment {

    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    String companyName;
    TextView emptyText;
    ProgressBar progressBar;
    ArrayList<String> users = new ArrayList<>();
    Button addButton;
    EditText email;
    FirebaseDatabase mAddData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        companyName = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).getString("companyName", "");
        progressBar = v.findViewById(R.id.progress_circular);
        mAddData = FirebaseDatabase.getInstance();
        mDatabase = mAddData.getReference("companies/" + companyName + "/users");
        emptyText = v.findViewById(R.id.emptyText);
        addButton = v.findViewById(R.id.addButton);
        email = v.findViewById(R.id.email);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.INVISIBLE);
                    users = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {

                    progressBar.setVisibility(View.INVISIBLE);
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        users.add(item.getValue(String.class));
                    }

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                }
                recyclerView.setAdapter(new UsersAdapter(getContext(), users, companyName));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        recyclerView = v.findViewById(R.id.list_of_tasks);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(llm);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (toolbar != null) {
            toolbar.setTitle("Работники");
            if (activity != null) {
                activity.setSupportActionBar(toolbar);
            }
        }

        addButton.setOnClickListener(view -> {
            if (!email.getText().toString().isEmpty() && isValid(email.getText().toString())) {
                String originalEmail = email.getText().toString();
                String emailShort = originalEmail.replace("@","").replace(".","").toLowerCase();
                mAddData.getReference("users").child(emailShort).setValue(new User(companyName, false));
                mAddData.getReference("companies").child(companyName).child("users").child(emailShort).setValue(originalEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            email.setText("");
                        } else {
                            Toast.makeText(getContext(), "Что-то пошло не так: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Поле пустое или введена не электронная почта", Toast.LENGTH_LONG).show();
            }
        });


        return v;
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}