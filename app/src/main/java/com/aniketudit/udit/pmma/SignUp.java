package com.aniketudit.udit.pmma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity{

    EditText muserName,memail,mphone,mage,mlocation,userEmail,userPassword;
    Button registerUser,loginUser;
    private UserInfo userInfo;
    private String userName,email,phone,age,location;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId=null;
    private SharedPreferences sharedPreferences;
    public static final String loginPreferenece="mypref";
    int currentVersion= Build.VERSION.SDK_INT;
    AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    private CircleImageView circleImageView;
    private static int temp=0,temp2=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        MyDatabase myDatabase=new MyDatabase(this);
        myDatabase.queryData("CREATE TABLE IF NOT EXISTS USERPIC (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB)");
        Cursor c=myDatabase.queryUserData("SELECT * FROM USERPIC");
        TabHost th=(TabHost)findViewById(R.id.tab_host);
        th.setup();
        TabHost.TabSpec spec=th.newTabSpec("tag1");
        spec.setIndicator("Sign Up");
        spec.setContent(R.id.tab1);
        th.addTab(spec);
        spec=th.newTabSpec("tag2");
        spec.setIndicator("Log In");
        spec.setContent(R.id.tab2);
        th.addTab(spec);
        if(c.getCount()>=1)
        {
            c.moveToFirst();
            Bitmap bmp= BitmapFactory.decodeByteArray(c.getBlob(2),0,c.getBlob(2).length);
            circleImageView.setImageBitmap(bmp);
        }
        mAuth = FirebaseAuth.getInstance();
        Intent i=getIntent();
        mlocation.setText(i.getStringExtra("cityName"));
        sharedPreferences =getSharedPreferences(loginPreferenece, Context.MODE_PRIVATE);
        // register the user
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check Internet Connection
                if(checkConnection()){
                    getUserInfo();
                    if(isValid(userName,email,phone,age,location))
                    {
                        // show progress dialog
                        try {
                            signUp();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        checkUserInfo(userName,email,phone,age,location);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Check Internet Connection",Toast.LENGTH_LONG).show();
            }
        });
        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()){
                    if(!userEmail.getText().toString().isEmpty() && !userPassword.getText().toString().isEmpty()){
                        String authuser=userEmail.getText().toString().trim();
                        String passuser=userPassword.getText().toString().trim();
                        progressDialog =new ProgressDialog(SignUp.this);
                        progressDialog.setMessage("Logging ...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        mAuth.signInWithEmailAndPassword(authuser, passuser)
                                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful()){
                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                            editor.putString("loginStatus","success");
                                            editor.commit();
                                        }
                                        if (!task.isSuccessful()) {
                                            //Log.w(TAG, "signInWithEmail", task.getException());
                                            Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Check Internet Connection",Toast.LENGTH_LONG).show();
            }
        });

        // listener for user login and logout
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId=user.getUid();
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("userId",userId);
                    editor.commit();
                    if(userName==null){
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("PMMA").child(userId).getRef();
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userName=dataSnapshot.child("userName").getValue().toString();
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                //editor.putString("userId",userId);
                                editor.putString("userName",userName);
                                editor.commit();
                                if(dataSnapshot.child("projects").hasChildren()){
                                    Iterable<DataSnapshot> children=dataSnapshot.child("projects").getChildren();
                                    final ArrayList<String> availableProject = new ArrayList<String>();
                                    final ArrayList<String> projectStatus = new ArrayList<String>();
                                    for(DataSnapshot child:children){
                                        availableProject.add(child.getKey());
                                        projectStatus.add(child.getValue().toString());
                                    }
                                    Intent intent=new Intent(SignUp.this,Selection.class);
                                    intent.putExtra("projectArray",availableProject);
                                    intent.putStringArrayListExtra("projectStatus",projectStatus);
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Intent i=new Intent(SignUp.this,Inactive.class);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    Log.d("userId: ",user.getUid());
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(image,100);
            }
        });
    }

    private void init() {
        muserName=(EditText) findViewById(R.id.userText);
        memail=(EditText) findViewById(R.id.emailText);
        mphone=(EditText) findViewById(R.id.phoneText);
        mage=(EditText) findViewById(R.id.ageText);
        mlocation=(EditText) findViewById(R.id.locationText);
        registerUser=(Button) findViewById(R.id.newUser);
        circleImageView=(CircleImageView) findViewById(R.id.sign_upUserpic);
        loginUser=(Button) findViewById(R.id.remoteUser);
        userEmail=(EditText) findViewById(R.id.emailId);
        userPassword=(EditText) findViewById(R.id.password);
    }
    public void signUp() throws IOException{
        progressDialog =new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Registering new user");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Thread thread=new Thread() {
            @Override
            public void run() {
                mAuth.createUserWithEmailAndPassword(email, phone).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("loginStatus","success");
                            editor.putString("userName",userName);
                            editor.commit();
                            userInfo = new UserInfo();
                            userInfo.setUserName(userName);
                            userInfo.setEmail(email);
                            userInfo.setPhone(phone);
                            userInfo.setAge(age);
                            userInfo.setLocation(location);
                            storeData();
                        }
                        else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {
                            userName=null;
                            Toast.makeText(getApplicationContext(), "User with this email Id already exists", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                        if (!task.isSuccessful()) {
                            userName=null;
                            Toast.makeText(getApplicationContext(), "Registration not successful may be due to bad network connection", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                    }
                });
            }
        };
        thread.start();

    }

    private void storeData() {

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference mRef = database.getReference("PMMA");
            mRef.child(userId).setValue(userInfo);
            mRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.d("ChildCalled","true");
                            Log.d("ChildCalled","true");
                            progressDialog.dismiss();
                            if(temp==0){
                                temp++;
                                Intent intent=new Intent(SignUp.this,Inactive.class);
                                intent.putExtra("sendNotificaiton",true);
                                intent.putExtra("userName",userName);
                                intent.putExtra("userId",userId);
                                intent.putExtra("email",email);
                                startActivity(intent);
                                finish();
                            }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (RuntimeException e)
        {
            Log.d("database exception",e.toString());
        }
    }

    public void getUserInfo()
    {
        userName=muserName.getText().toString().trim();
        email= memail.getText().toString().trim();
        phone= mphone.getText().toString().trim();
        age= mage.getText().toString().trim();
        location= mlocation.getText().toString().trim();
    }
    public void checkUserInfo(String userName,String email,String phone,String age,String location){
        if(userName.isEmpty())
        {
            muserName.setError("User name is required");
        }
        if(email.isEmpty())
            memail.setError("Email is required");
        if(phone.isEmpty())
            mphone.setError("phone no is required");
        if(age.isEmpty())
        {
            mage.setError("age is required");
        }
        if(location.isEmpty())
        {
            mlocation.setError("location is required");
        }
    }
    public boolean isValid(String userName,String email,String phone,String age,String location){
        if(userName.isEmpty() || email.isEmpty() || phone.isEmpty() || age.isEmpty() || location.isEmpty())
            return false;
        else
            return  true;
    }
    //check internet connection
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
                                if(mobileData.setMobileDataEnabled(SignUp.this,true)) {
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
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Toolbar toolbar=(Toolbar) findViewById(R.id.signUptoolbar);
        toolbar.setTitle("Sign Up");
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
                //myDatabase.queryData("CREATE TABLE IF NOT EXISTS USERPIC (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB);");
                myDatabase.insertUser("userPic",byteArrayOutputStream.toByteArray());
            }
        }
    }

}
