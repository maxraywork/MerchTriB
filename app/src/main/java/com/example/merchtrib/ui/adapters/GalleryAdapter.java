package com.example.merchtrib.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private Context ctx;
    private RoundedImageView ivGallery;
    ArrayList<Uri> mArrayUri;
    private OnImageListener mOnImageListener;

    public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri, OnImageListener mmOnImageListener) {
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
        this.mOnImageListener = mmOnImageListener;
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_task_item, parent, false);
        return new PhotoViewHolder(itemView, mOnImageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.PhotoViewHolder holder, int position) {
//            ivGallery.setImageURI(Uri.parse(mArrayUri.get(position)));
        Picasso.get().load(mArrayUri.get(position)).error(R.drawable.img_error).into(ivGallery);
        ivGallery.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Удаление")
                    .setMessage("Вы уверены что хотите удалить это фото?")
                    .setPositiveButton("Да", (dialog, id) -> {
                        // Закрываем окно
                        mOnImageListener.onImageClick(position);
                        dialog.cancel();
                    }).setNegativeButton("Нет", (dialog, id) -> {
                dialog.cancel();
            });
            builder.create().show();
            return false;
        });
    }



    @Override
    public int getItemCount() {
        return mArrayUri.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnImageListener mmOnImageListener;
        public PhotoViewHolder(@NonNull View itemView, OnImageListener OnImageListener) {
            super(itemView);
            ivGallery = (RoundedImageView) itemView.findViewById(R.id.photo);
            mmOnImageListener = OnImageListener;

//            ivGallery.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnImageListener.onImageClick(getAdapterPosition());
        }
    }

    public interface OnImageListener{
        void onImageClick(int position);
    }
}