package com.aniketudit.udit.pmma;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell PC on 18-01-2017.
 */

public class ProjectUpdateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<String> projectList;
    public ProjectUpdateAdapter(Context context, ArrayList<String> projectList)
    {
        this.context=context;
        this.projectList=projectList;
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
        ((Item)holder).textView.setText(projectList.get(position));
        ((Item)holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SelectCity.class);
                intent.putExtra("projectName",projectList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
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
