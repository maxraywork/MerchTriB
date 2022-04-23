package com.example.merchtrib.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private Context ctx;
    private RoundedImageView ivGallery;
    ArrayList<Uri> mArrayUri;

    public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri) {
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_task_item, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.PhotoViewHolder holder, int position) {
//            ivGallery.setImageURI(Uri.parse(mArrayUri.get(position)));
        Picasso.get().load(mArrayUri.get(position)).error(R.drawable.img_error).into(ivGallery);
    }



    @Override
    public int getItemCount() {
        return mArrayUri.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = (RoundedImageView) itemView.findViewById(R.id.photo);
        }
    }
}