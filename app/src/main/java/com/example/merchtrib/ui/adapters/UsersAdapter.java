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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private Context ctx;
    private ArrayList<String> mData;
    private ImageButton delete;
    private TextView name;
    private String companyName;

    public UsersAdapter(Context ctx, ArrayList<String> data, String companyName) {
        this.ctx = ctx;
        this.mData = data;
        this.companyName = companyName;

    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);

        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {
        String current = mData.get(position);
        name.setText(current);
        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            AlertDialog dialog = builder.setTitle("Удаление")
                    .setMessage("Вы уверены что хотите удалить этого пользователя?")
                    .setPositiveButton("Да", (dialogg, id) -> {
                        // Закрываем окно
                        String email = current.replace("@", "").replace(".", "").toLowerCase();
                        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
                        mDataBase.getReference("companies/" + companyName + "/users/" + email).removeValue();
                        mDataBase.getReference("users/" + email).removeValue();
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
        return mData.size();
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder {
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_name);
            delete = itemView.findViewById(R.id.delete);

        }

    }
}
