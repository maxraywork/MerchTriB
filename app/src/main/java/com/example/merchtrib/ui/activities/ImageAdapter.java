package com.example.merchtrib.ui.activities;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchtrib.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.List;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.PhotoViewHolder>{

    private Context mContext;
    private List<Uri> mListPhoto;

    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Uri> list) {
        this.mListPhoto = list;
        notifyDataSetChanged();
    }
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_task_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.PhotoViewHolder holder, int position) {
        Uri uri = mListPhoto.get(position);
        if (uri == null) {
            return;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            if (bitmap != null) {
                holder.photo.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (mListPhoto != null) {
            return mListPhoto.size();
        }
        return 0;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.photo);
        }
    }
}