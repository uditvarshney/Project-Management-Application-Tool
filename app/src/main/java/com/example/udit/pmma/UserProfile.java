package com.example.udit.pmma;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    CircleImageView circleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        init();
        toolbar.setTitle("Profile");
        MyDatabase myDatabase=new MyDatabase(this);
        myDatabase.queryData("CREATE TABLE IF NOT EXISTS USERPIC (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB)");
        Cursor c=myDatabase.queryUserData("SELECT * FROM USERPIC");

        if(c.getCount()>=1)
        {
            Log.d("cursorCount",String.valueOf(c.getCount()));
            Log.d("coooumnCount",String.valueOf(c.getColumnCount()));
            c.moveToFirst();
            Bitmap bmp= BitmapFactory.decodeByteArray(c.getBlob(2),0,c.getBlob(2).length);
            circleImageView.setImageBitmap(bmp);
        }
        //Log.d("cursorsize",String.valueOf(c.getCount()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SecondAdapter(this));
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(image,100);
            }
        });
    }

    private void init() {
        toolbar=(Toolbar) findViewById(R.id.userToolbar);
        recyclerView=(RecyclerView) findViewById(R.id.thumbnail2_viewer);
        circleImageView=(CircleImageView) findViewById(R.id.userProfilepic);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK)
        {
            Bundle extra=data.getExtras();
            Bitmap bmp= (Bitmap) extra.get("data");
            circleImageView.setImageBitmap(bmp);
            MyDatabase myDatabase=new MyDatabase(this);
            Cursor c=myDatabase.queryUserData("SELECT * FROM USERPIC");
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            if(c.getCount()>=1)
            {
                // update database
                myDatabase.updateData(byteArrayOutputStream.toByteArray());
            }
            else{
                myDatabase.insertUser("userPic",byteArrayOutputStream.toByteArray());
            }
        }
    }
}
