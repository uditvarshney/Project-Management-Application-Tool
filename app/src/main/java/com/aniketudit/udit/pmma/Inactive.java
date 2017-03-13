package com.aniketudit.udit.pmma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Inactive extends AppCompatActivity{

    ProgressBar  progressBar;
    LinearLayout refreshButton;
    private  final String userkey="userId";
    public static final String loginPreferenece="mypref";
    SharedPreferences sharedPreferences;
    ConnectivityReceiver connectivityReceiver;
    int currentVersion= Build.VERSION.SDK_INT;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;
    private TextView userName;
    private Toolbar toolbar;
    CircleImageView circleImageView;
    public static int temp=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive);
        init();
        final Intent i=getIntent();
        if(i.getBooleanExtra("sendNotificaiton",false)){
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Token");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String serverKey=dataSnapshot.child("serverKey").getValue().toString();
                    String receiverToken=dataSnapshot.child("receiverToken").getValue().toString();
                    if(temp==0){
                        temp++;
                        RequestNotification requestNotification=new RequestNotification(Inactive.this,serverKey,receiverToken);
                        requestNotification.sendMessage("New registration (PMMA)",i.getStringExtra("userName")+" has registered to your app","R.mipmap.ic_launcher",i.getStringExtra("userId"),i.getStringExtra("userName"),i.getStringExtra("email"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        setSupportActionBar(toolbar);
        toolbar.setTitle("Project Status");
        MyDatabase myDatabase=new MyDatabase(this);
        myDatabase.queryData("CREATE TABLE IF NOT EXISTS USERPIC (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB)");
        Cursor c=myDatabase.queryUserData("SELECT * FROM USERPIC");
        if(c.getCount()>=1)
        {
            c.moveToFirst();
            //Log.d("username",c.getString(1));
            Bitmap bmp= BitmapFactory.decodeByteArray(c.getBlob(2),0,c.getBlob(2).length);
            //Log.d("byteLenght",String.valueOf(bmp.getByteCount()));
            circleImageView.setImageBitmap(bmp);
        }
        sharedPreferences =getSharedPreferences(loginPreferenece, Context.MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("userName",null));
        checkConnection();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection()){
                    final String userId = sharedPreferences.getString(userkey,null);
                    //Toast.makeText(getApplicationContext(),userId,Toast.LENGTH_LONG).show();
                    //Log.d("check",userId);
                    projectUpdate update =new projectUpdate();
                    update.execute(userId);
                }

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
    // initialize all the variable
    private void init() {
        progressBar =(ProgressBar) findViewById(R.id.inprogressBar);
        refreshButton =(LinearLayout) findViewById(R.id.refresh);
        toolbar=(Toolbar) findViewById(R.id.inactivetoolbar);
        userName=(TextView) findViewById(R.id.inuserName);
        circleImageView=(CircleImageView) findViewById(R.id.inactiveuserpic);
    }

    private class projectUpdate extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog =new ProgressDialog(Inactive.this);
            progressDialog.setMessage("fetching the data from the server");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            final String Id=params[0];
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            Log.d("check",Id);
            DatabaseReference myRef = database.getReference("PMMA");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("checking child","status");
                    if(dataSnapshot.child(Id).hasChild("projects"))
                    {
                        Log.d("child","child found");
                        if(dataSnapshot.child(Id).getChildrenCount() !=0)
                        {
                            Iterable<DataSnapshot> children = dataSnapshot.child(Id).child("projects").getChildren();
                            Intent intent =new Intent(getApplicationContext(),Selection.class);
                            final ArrayList<String> availableProject = new ArrayList<String>();
                            final ArrayList<String> projectStatus = new ArrayList<String>();
                            for(DataSnapshot child: children)
                            {
                                // store the assigned projects in the active activity
                                //Log.d("child name",child.getKey());
                                availableProject.add(child.getKey());
                                projectStatus.add(child.getValue().toString());
                            }
                            //intent.putStringArrayListExtra("projectArray",availableProject);
                            //Log.d("array find",availableProject.get(0));
                            intent.putStringArrayListExtra("projectArray",availableProject);
                            intent.putStringArrayListExtra("projectStatus",projectStatus);
                            progressDialog.dismiss();
                            finish();
                            startActivity(intent);
                        }
                        else
                        {
                            // no project has assigned to the user and user will switch to inactive active activity
                            //Intent intent =new Intent(getApplicationContext(),Inactive.class);
                            //finish();
                            //startActivity(intent);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"No project has assigned yet",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        // no project has assigned to the user and user will switch to inactive active activity
                        //Intent intent =new Intent(getApplicationContext(),Inactive.class);
                        //finish();
                        //startActivity(intent);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"No project has assigned yet",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressBar.setVisibility(View.GONE);
        }
    }
    private boolean checkConnection() {
        String message;
        if(ConnectivityReceiver.isNetworkAvailable(this))
        {
            //message = "Good! Connected to Internet";
            //color = Color.WHITE;
            //Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG).show();
            return true;
        }
        else {
            message = "Error! Check Your Internet Connection";
            //color = Color.RED;
            Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG).show();
            final AlertDialog.Builder alertBuilder =new AlertDialog.Builder(this);
            alertBuilder.setMessage(message);
            final boolean[] temp = {false};

            if(currentVersion<android.os.Build.VERSION_CODES.LOLLIPOP)
            {
                alertBuilder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MobileData mobileData=new MobileData();
                                if(mobileData.setMobileDataEnabled(Inactive.this,true)) {
                                    Log.d("mobile data", "ON");
                                    temp[0]=true;
                                }

                            }
                        })).start();
                    }
                });
            }
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog=alertBuilder.create();
            alertDialog.show();
            return temp[0];
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
