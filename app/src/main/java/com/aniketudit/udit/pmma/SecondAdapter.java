package com.aniketudit.udit.pmma;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dell PC on 21-01-2017.
 */

public class SecondAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Cursor cursor;
    public SecondAdapter(Context context)
    {
        this.context=context;
        this.cursor=cursor;
        MyDatabase myDatabase=new MyDatabase(context);
        myDatabase.queryData("CREATE TABLE IF NOT EXISTS PROJECTDETAILS (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB, image_2 BLOB, image_3 BLOB)");
        String sql="select * from PROJECTDETAILS";
        cursor=myDatabase.getData(sql);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View row=inflater.inflate(R.layout.gallery_thumbnail2,parent,false);
        Item item=new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("position",String.valueOf(position));
        if(cursor.moveToNext()){
            Log.d("currentPosition",String.valueOf(cursor.getPosition()));
            //if(cursor.moveToPosition(position+1)) {
            Log.d("moveed","true");
            ((Item) holder).textView.setText(cursor.getString(1));
            Log.d("subcolumns",String.valueOf(cursor.getColumnCount()));

            for (int i = 2; i <= cursor.getColumnCount()-1; i++) {
                if(cursor.getBlob(i)!=null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(cursor.getBlob(i), 0, cursor.getBlob(i).length);
                    if (i == 2)
                        ((Item) holder).imageView1.setImageBitmap(bmp);
                    else if (i == 3)
                        ((Item) holder).imageView2.setImageBitmap(bmp);
                    else if (i == 4)
                        ((Item) holder).imageView3.setImageBitmap(bmp);
                }
            }

        }

        //}
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    public class Item extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView1,imageView2,imageView3;
        public Item(View itemView) {
            super(itemView);
            textView=(TextView) itemView.findViewById(R.id.currentProjectStatus);
            imageView1=(ImageView) itemView.findViewById(R.id.imageView_1);
            imageView2=(ImageView) itemView.findViewById(R.id.imageView_2);
            imageView3=(ImageView) itemView.findViewById(R.id.imageView_3);
        }
    }
}
