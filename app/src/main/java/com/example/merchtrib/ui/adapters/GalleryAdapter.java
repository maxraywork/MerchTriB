package com.example.merchtrib.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private Context ctx;
    private int pos;
    private LayoutInflater inflater;
    private RoundedImageView ivGallery;
    ArrayList<Uri> mArrayUri;

    public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri) {
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.from(parent.getContext()).inflate(R.layout.image_task_item, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.PhotoViewHolder holder, int position) {
        pos = position;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ivGallery.setImageURI(mArrayUri.get(position));

    }



    @Override
    public int getItemCount() {
        Logger.getLogger("com.dataart.demo.java.logging.SomeClass").log(Level.INFO, String.valueOf(mArrayUri.size()));
        return mArrayUri.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = (RoundedImageView) itemView.findViewById(R.id.photo);
        }
    }
}