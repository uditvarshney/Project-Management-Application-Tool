package com.example.udit.pmma;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Dell PC on 28-01-2017.
 */

public class OtherUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<String> list;
    public OtherUserAdapter(Context context, ArrayList<String> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View row=inflater.inflate(R.layout.otherprofilegallery,parent,false);
        Item item=new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(position!=list.size()-1){
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(list.get(position));
            Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(((Item)holder).imageView);
            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    ((Item)holder).timeView.setText(storageMetadata.getCustomMetadata("Time"));
                    ((Item)holder).dateView.setText(storageMetadata.getCustomMetadata("Date"));
                    ((Item)holder).locationView.setText(storageMetadata.getCustomMetadata("location"));
                    ((Item)holder).descriptionView.setText(storageMetadata.getCustomMetadata("Description"));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size()-1;
    }
    public class Item extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView dateView,timeView,locationView,descriptionView;
        public Item(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.otherProfilePic);
            dateView=(TextView) itemView.findViewById(R.id.date);
            timeView=(TextView) itemView.findViewById(R.id.time);
            locationView=(TextView) itemView.findViewById(R.id.location);
            descriptionView=(TextView) itemView.findViewById(R.id.description);
        }
    }
}
