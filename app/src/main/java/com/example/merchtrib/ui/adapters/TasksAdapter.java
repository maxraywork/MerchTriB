package com.example.merchtrib.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.example.merchtrib.ui.activities.TaskActivity;
import com.example.merchtrib.ui.objects.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private Context ctx;
    private ArrayList<Task> mArrayTask;
    private ImageButton imageButton;
    private TextView name;
    private TextView address;
    private LinearLayout parentLayout;
    private String type;
    private boolean isAdmin;
    private String companyName;

    public TasksAdapter(Context ctx, ArrayList<Task> task, String type, boolean isAdmin, String companyName) {
        this.ctx = ctx;
        this.mArrayTask = task;
        this.type = type;
        this.isAdmin = isAdmin;
        this.companyName = companyName;

    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_task_list_item, parent, false);

        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TaskViewHolder holder, int position) {
        Task current = mArrayTask.get(position);
        name.setText(current.getName());
        address.setText(current.getAddress());
        if (current.getAddressLink() != null) {
            imageButton.setOnClickListener(view -> {
                Uri uri = Uri.parse(current.getAddressLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                ctx.startActivity(intent);
            });

        } else {
            imageButton.setVisibility(View.INVISIBLE);
        }

        parentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, TaskActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("id", current.id);
            intent.putExtra("name", current.name);
            intent.putExtra("address", current.address);
            intent.putExtra("addressLink", current.addressLink);
            ctx.startActivity(intent);
        });
        if (isAdmin) {
            parentLayout.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Удаление")
                        .setMessage("Вы уверены что хотите удалить это задание?")
                        .setPositiveButton("Да", (dialog, id) -> {
                            // Закрываем окно
                            FirebaseDatabase.getInstance().getReference("companies/" + companyName + "/tasks/" + type + "/" + current.id).removeValue();
                            FirebaseStorage.getInstance().getReference("uploads/" + current.id).delete();
                            dialog.cancel();
                        }).setNegativeButton("Отмена", (dialog, id) -> {
                            dialog.cancel();
                          });
                builder.create().show();
                return false;
            });
        }
    }


    @Override
    public int getItemCount() {
        return mArrayTask.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.task_item_button);
            name = itemView.findViewById(R.id.list_item_name);
            address = itemView.findViewById(R.id.list_item_link);
            parentLayout = itemView.findViewById(R.id.parent);

        }

    }
}