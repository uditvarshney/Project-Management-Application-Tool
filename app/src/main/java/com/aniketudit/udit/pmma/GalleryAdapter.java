package com.aniketudit.udit.pmma;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dell PC on 19-01-2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private HashMap<Integer,ArrayList<String> > images;
    private Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail_1,thumbnail_2,thumbnail_3;
        public TextView textView;
        public MyViewHolder(View view) {
            super(view);
            thumbnail_1 = (ImageView) view.findViewById(R.id.thumbnail1);
            thumbnail_2 = (ImageView) view.findViewById(R.id.thumbnail2);
            thumbnail_3 = (ImageView) view.findViewById(R.id.thumbnail3);
            textView=(TextView) view.findViewById(R.id.userImages);
        }
    }


    public GalleryAdapter(Context context, HashMap<Integer, ArrayList<String>> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Image image = images.get(position);
        for(int i=0;i<images.get(position).size();i++){
            String ref=images.get(position).get(i);
            StorageReference storageReference=null;
            if(i!=images.get(position).size()-1)
                storageReference= FirebaseStorage.getInstance().getReference(ref);

            if(i==images.get(position).size()-1 ){
                holder.textView.setText(images.get(position).get(i));
                Log.d("userName",images.get(position).get(i));
            }
            else if(i==0){
                Glide.with(mContext).using(new FirebaseImageLoader())
                        .load(storageReference)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.thumbnail_1);
            }
            else if(i==1){
                Glide.with(mContext).using(new FirebaseImageLoader())
                        .load(storageReference)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.thumbnail_2);
            }
            else if(i==2){
                Glide.with(mContext).using(new FirebaseImageLoader())
                        .load(storageReference)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.thumbnail_3);
            }

        }

        //Log.d("path_usit",ref);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
