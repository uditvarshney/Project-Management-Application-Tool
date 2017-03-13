package com.aniketudit.udit.pmma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell PC on 15-01-2017.
 */

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<String> items,projectStatus;
    SharedPreferences sharedPreferences;
    public Adapter(Context context, ArrayList<String> items, ArrayList<String> projectStatus)
    {
        this.context=context;
        this.items=items;
        this.projectStatus=projectStatus;
        sharedPreferences=context.getSharedPreferences("mypref",Context.MODE_PRIVATE);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View row=inflater.inflate(R.layout.card_layout,parent,false);
        Item item=new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((Item)holder).textView.setText(items.get(position));
            ((Item)holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(projectStatus.get(position).equals("false") && sharedPreferences.getString(items.get(position),"false").equals("false")) {
                        Intent intent = new Intent(context, UploadImages.class);
                        intent.putExtra("projectName", items.get(position));
                        context.startActivity(intent);
                    }
                    else {
                        //  send to userProfile
                        Intent intent=new Intent(context,UserProfile.class);
                        context.startActivity(intent);
                    }
                }
            });


        //Log.d("project name",((Item)holder).textView.getText().toString());

    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public  class Item extends RecyclerView.ViewHolder{
        TextView textView;
        CardView cardView;
        public Item(View itemView) {
            super(itemView);
            textView=(TextView) itemView.findViewById(R.id.projectName);
            cardView=(CardView) itemView.findViewById(R.id.cardAdapter);
        }
    }
}
