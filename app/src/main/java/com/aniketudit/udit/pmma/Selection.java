package com.aniketudit.udit.pmma;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class Selection extends AppCompatActivity {

    Button mcheckStatus,mupdateProject;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    TextView textView;
    CircleImageView circleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        init();
        toolbar.setTitle("Project Status");
        setSupportActionBar(toolbar);
        MyDatabase myDatabase=new MyDatabase(this);
        myDatabase.queryData("CREATE TABLE IF NOT EXISTS USERPIC (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB)");
        Cursor c=myDatabase.queryUserData("SELECT * FROM USERPIC");
        //Log.d("cursorCount",String.valueOf(c.getColumnName(1)));
        //Log.d("coooumnCount",String.valueOf(c.getColumnName(2)));
        if(c.getCount()>=1)
        {
            c.moveToFirst();
            Log.d("username",c.getString(1));
            Bitmap bmp= BitmapFactory.decodeByteArray(c.getBlob(2),0,c.getBlob(2).length);
            circleImageView.setImageBitmap(bmp);
            Toast.makeText(getApplicationContext(),c.getString(1),Toast.LENGTH_LONG);
        }
        sharedPreferences=getSharedPreferences("mypref",MODE_PRIVATE);
        textView.setText(sharedPreferences.getString("userName",null));
        Intent i =getIntent();
        final ArrayList<String> list;
        final ArrayList<String> projectStatus;
        list=i.getStringArrayListExtra("projectArray");
        projectStatus=i.getStringArrayListExtra("projectStatus");
        mcheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ProjectUpdateStatus.class);
                intent.putStringArrayListExtra("projectArray",list);
                startActivity(intent);
            }
        });
        mupdateProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Active.class);
                intent.putStringArrayListExtra("projectArray",list);
                intent.putStringArrayListExtra("projectStatus",projectStatus);
                startActivity(intent);
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(image,100);
            }
        });
    }

    private void init() {
        mcheckStatus=(Button) findViewById(R.id.checkStatus);
        mupdateProject=(Button) findViewById(R.id.updateStatus);
        textView=(TextView) findViewById(R.id.selectuserName);
        toolbar=(Toolbar) findViewById(R.id.selectionToolbar);
        circleImageView=(CircleImageView) findViewById(R.id.selectionuserpic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_userprofile) {
            Intent intent=new Intent(getApplicationContext(),UserProfile.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Cursor c=myDatabase.queryUserData("SELECT * FROM USERPIC;");
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            if(c.getCount()>=1)
            {
                // update database
                myDatabase.updateData(byteArrayOutputStream.toByteArray());
            }
            else{
                myDatabase.queryData("CREATE TABLE IF NOT EXISTS USERPIC (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB);");
                myDatabase.insertUser("userPic",byteArrayOutputStream.toByteArray());
            }
        }
    }
}
