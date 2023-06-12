package com.example.merchtrib.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.merchtrib.ui.objects.CompanyUser;
import com.example.merchtrib.ui.objects.UsersWaitListItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class UsersFragment extends Fragment {

    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    String companyID;

    SharedPreferences sharedPreferences;
    TextView emptyText;
    ProgressBar progressBar;
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> usersWaitList = new ArrayList<>();
    Button addButton;
    EditText email;
    FirebaseDatabase mAddData;

    ArrayList<String> combinedList = new ArrayList<>();
    ValueEventListener valueEventListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        companyID = sharedPreferences.getString("companyID", "");
        progressBar = v.findViewById(R.id.progress_circular);
        mAddData = FirebaseDatabase.getInstance();
        mDatabase = mAddData.getReference();
        emptyText = v.findViewById(R.id.emptyText);
        addButton = v.findViewById(R.id.addButton);
        email = v.findViewById(R.id.email);


        mDatabase.child("companies/" + companyID + "/users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.INVISIBLE);
                users = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {

                    progressBar.setVisibility(View.INVISIBLE);
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (!Objects.equals(item.getValue(CompanyUser.class).getEmail(), sharedPreferences.getString("email", ""))) {
                            users.add(item.getValue(CompanyUser.class).getEmail());
                        }
                    }


                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                }
                recyclerView.setAdapter(new UsersAdapter(getContext(), updateCombinedList(), companyID));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersWaitList = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    emptyText.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        usersWaitList.add(item.getValue(UsersWaitListItem.class).getEmail() + " (Приглашён)");
                    }

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (updateCombinedList().size() == 0) {
                        emptyText.setVisibility(View.VISIBLE);
                    }
                }
                recyclerView.setAdapter(new UsersAdapter(getContext(), updateCombinedList(), companyID));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.child("companies/" + companyID + "/usersWaitList").addValueEventListener(valueEventListener);


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
            toolbar.setTitle(R.string.TitleUsers);
            if (activity != null) {
                activity.setSupportActionBar(toolbar);
            }
        }

        addButton.setOnClickListener(view -> {
            if (!email.getText().toString().isEmpty() && isValid(email.getText().toString())) {
                String originalEmail = email.getText().toString();
                String emailShort = originalEmail.replace("@", "").replace(".", "").toLowerCase();

                mDatabase.child("usersWaitList").child(emailShort).setValue(new UsersWaitListItem(originalEmail, companyID));
                mDatabase.child("companies").child(companyID).child("usersWaitList").child(emailShort).setValue(new UsersWaitListItem(originalEmail, null)).addOnCompleteListener(task -> {
                    Toast.makeText(getContext(), "Успешно добавлен", Toast.LENGTH_SHORT).show();
                    email.setText("");
                });

            } else {
                Toast.makeText(getContext(), "Поле пустое или введена не электронная почта", Toast.LENGTH_LONG).show();
            }
        });


        return v;
    }

    // Function to update the combinedList by combining list1Items and list2Items
    public ArrayList<String> updateCombinedList() {
        // Clear the existing combinedList

        combinedList.clear();

        // Add all items from list1Items to combinedList
        combinedList.addAll(usersWaitList);

        // Add all items from list2Items to combinedList
        combinedList.addAll(users);

        return combinedList;

        // Pass the combinedList to your RecyclerView adapter or update the UI as needed
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

    @Override
    public void onDestroy() {
        mDatabase.child("companies/" + companyID + "/usersWaitList").removeEventListener(valueEventListener);
        super.onDestroy();
    }
}