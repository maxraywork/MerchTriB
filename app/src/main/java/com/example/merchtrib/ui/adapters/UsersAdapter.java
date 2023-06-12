package com.example.merchtrib.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private Context ctx;
    private ArrayList<String> users;
    private ImageButton delete;
    private TextView name;
    private String companyID;

    public UsersAdapter(Context ctx, ArrayList<String> users, String companyID) {
        this.ctx = ctx;
        this.users = users;
        this.companyID = companyID;

    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);

        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {
        String current = users.get(position);
        name.setText(current);
        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            AlertDialog dialog = builder.setTitle("Удаление")
                    .setMessage("Вы уверены что хотите удалить этого пользователя?")
                    .setPositiveButton("Да", (dialogg, id) -> {
                        // Закрываем окно
                        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
                        //If user is in waitList
                        String shortEmail = current.split(" ")[0].replace("@", "").replace(".", "").toLowerCase();
                        if (current.split(" ").length == 2) {

                            //If user is user
                            mDataBase.getReference("companies/" + companyID + "/usersWaitList/" + shortEmail).removeValue();
                            mDataBase.getReference("usersWaitList/" + shortEmail).removeValue();

                        } else {

                            mDataBase.getReference("companies/" + companyID + "/users").orderByChild("email").equalTo(current).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                            userSnapshot.getRef().removeValue();
                                            mDataBase.getReference("users/" + userSnapshot.getKey()).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        dialogg.cancel();
                    }).setNegativeButton("Отмена", (dialogg, id) -> {
                dialogg.cancel();
            }).create();
            dialog.setOnShowListener(arg0 -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx, R.color.primary));
            });
            builder.show();
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder {
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_name);
            delete = itemView.findViewById(R.id.delete);

        }

    }
}
