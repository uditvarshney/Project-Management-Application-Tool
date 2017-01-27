package com.example.udit.pmma;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.udit.pmma.R.id.info;

public class UploadImages extends AppCompatActivity implements View.OnClickListener,LocationListener{

    ImageView frontView,leftView,rightView;
    Button uploadPicture;
    private StorageReference mStorageRef;
    TextView frontsideText,rightsideText,leftsideText,currentProject;
    ArrayList<Bitmap> bitmaps;
    ProgressDialog progressDialog;
    StorageMetadata storageMetadata;
    LocationManager locationManager;
    Location location;
    boolean isGPSEnabled = false;
    ProgressDialog progressDialog1;
    // flag for network status
    boolean isNetworkEnabled = false;
    AlertDialog.Builder alertDialog;
    String cityName=null;
    static int i=0;
    private String time,date;
    //get the selected project through intent
    // get userId through sharedPrefrence
    private String projectName=null;
    private String projectStatus;
    String imageLoaction;
    SharedPreferences sharedPreferences;
    private String userId,userName;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private static MyDatabase myDatabase;
    private ArrayList<byte[]> arrayList;
    int currentVersion= Build.VERSION.SDK_INT;
    private AlertDialog alertDialog1;
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        init();
        setSupportActionBar(toolbar);
        arrayList=new ArrayList<>();
        toolbar.setTitle("Update Project Status");
        myDatabase=new MyDatabase(getApplicationContext());
        myDatabase.queryData("CREATE TABLE IF NOT EXISTS PROJECTDETAILS (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_1 BLOB, image_2 BLOB, image_3 BLOB)");
        Intent i=getIntent();
        projectName=i.getStringExtra("projectName");
        sharedPreferences= getSharedPreferences("mypref",MODE_PRIVATE);
        userId=sharedPreferences.getString("userId",null);
        userName=sharedPreferences.getString("userName",null);
        Log.d("project Name",projectName);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("project").child(projectName).getRef();
        frontView.setOnClickListener(this);
        uploadPicture.setOnClickListener(this);
        leftView.setOnClickListener(this);
        rightView.setOnClickListener(this);
    }

    private void init() {
        frontView=(ImageView) findViewById(R.id.frontImage);
        uploadPicture=(Button) findViewById(R.id.uploadPic);
        leftView=(ImageView) findViewById(R.id.leftsideImage);
        rightView=(ImageView) findViewById(R.id.rightsideImage);
        frontsideText=(TextView)  findViewById(R.id.frontText);
        rightsideText= (TextView) findViewById(R.id.rightText);
        leftsideText=(TextView) findViewById(R.id.leftText);
        bitmaps=new ArrayList<Bitmap>(4);
        currentProject=(TextView) findViewById(R.id.currentProjectName);
        Intent i=getIntent();
        currentProject.setText(i.getStringExtra("projectName"));
        toolbar=(Toolbar) findViewById(R.id.uploadToolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.uploadPic:
                if(checkConnection()){
                    progressDialog=new ProgressDialog(this);
                    progressDialog.setMessage("Uploading Images");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    date=getDate();
                    time=getTime();
                    // setting the metadata for image
                    // fetching current location
                    getLocation city=new getLocation();
                    city.execute();
                }
                break;
            case R.id.leftsideImage:
                Intent camera=new Intent("android.media.action.IMAGE_CAPTURE");
                File file=new File(Environment.getExternalStorageDirectory()+File.separator+"image1.jpg");
                camera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
                startActivityForResult(camera,101);
                break;
            case R.id.rightsideImage:
                camera=new Intent("android.media.action.IMAGE_CAPTURE");
                file=new File(Environment.getExternalStorageDirectory()+File.separator+"image2.jpg");
                camera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
                startActivityForResult(camera,100);
                break;
            case R.id.frontImage:
                camera=new Intent("android.media.action.IMAGE_CAPTURE");
                file=new File(Environment.getExternalStorageDirectory()+File.separator+"image3.jpg");
                camera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
                startActivityForResult(camera,102);
                break;

        }
    }
    // uploading data to firebase storage

    private void uploadOnFirebseStorage() {
        //check Internet Connection
        if(bitmaps.size()>=1 && i<bitmaps.size())
        {
            // refrence to store data
            progressDialog.setMessage("Uploading Image"+(i+1));
            if(!progressDialog.isShowing()){
                progressDialog.show();
            }
            progressDialog.setProgress(0);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            // compressing the image and store to byteArrayOutputStream
            bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
            byte[] compressedImage=byteArrayOutputStream.toByteArray();
            arrayList.add(compressedImage);
            // path to store data selectedProject->userId->location
            Log.d("project Name",projectName);
            Log.d("city Name",cityName);
            Log.d("userId",userId);

            final StorageReference riversRef = mStorageRef.child(projectName).child(cityName).child(userId).child(i+".jpg");
            progressDialog.setMax(compressedImage.length);
            riversRef.putBytes(compressedImage,storageMetadata)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // images successfully uploaded
                                databaseReference.child(String.valueOf(i)).setValue(userName);
                                i++;
                                uploadOnFirebseStorage();
                                Log.d("Images Uploaded","true");
                                Toast.makeText(getApplicationContext(), "Successfully Uploaded", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // upload failed
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Upload Failed !"+"\n"+"Check your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // checking the progress o upload
                            progressDialog.setProgress((int) taskSnapshot.getBytesTransferred());
                            Log.d("progress"+i,String.valueOf(taskSnapshot.getBytesTransferred()));
                        }
                    });
            }
        else if(i==bitmaps.size())
        {
            i=0;
            if (arrayList.size()==1)
            {
                myDatabase.insertData(projectName,arrayList.get(0));
            }
            else if(arrayList.size()==2)
            {
                myDatabase.insertData(projectName,arrayList.get(0),arrayList.get(1));
            }
            else if(arrayList.size()==3)
            {
                myDatabase.insertData(projectName,arrayList.get(0),arrayList.get(1),arrayList.get(2));
            }
            if(progressDialog.isShowing())
                progressDialog.cancel();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(projectName,true);
            editor.commit();
            Intent intent=new Intent(this,UserProfile.class); // go to user profile
            startActivity(intent);
            finish();
        }
        else{
             Toast.makeText(getApplicationContext(),"At least One Image required",Toast.LENGTH_LONG).show();
        }

    }
    // setting capture image to image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 102 ) {

            File file=new File(Environment.getExternalStorageDirectory()+File.separator+"image3.jpg");
            Bitmap bitmap=decodeSampleBitmap(file.getAbsolutePath(),1000,700);
            frontView.setImageBitmap(bitmap);
            bitmaps.add(0,bitmap);
        }
        else if (requestCode == 100 ) {
            File file=new File(Environment.getExternalStorageDirectory()+File.separator+"image2.jpg");
            Bitmap bitmap=decodeSampleBitmap(file.getAbsolutePath(),1000,700);
            rightView.setImageBitmap(bitmap);
            bitmaps.add(1,bitmap);

        }
        else if (requestCode == 101 ) {
            File file=new File(Environment.getExternalStorageDirectory()+File.separator+"image1.jpg");
            Bitmap bitmap=decodeSampleBitmap(file.getAbsolutePath(),1000,700);
            leftView.setImageBitmap(bitmap);
            bitmaps.add(2,bitmap);
        }
    }
    public static Bitmap decodeSampleBitmap(String path,int requiredWidth,int requiredLength){
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        final int height=options.outHeight;
        final int width=options.outWidth;
        options.inPreferredConfig=Bitmap.Config.RGB_565;
        int inSamplesize=1;
        if(height>requiredLength) {
            inSamplesize = Math.round((float) height / (float) requiredLength);
        }
            int expectedWidth=width/inSamplesize;
            if(expectedWidth>requiredWidth){
                inSamplesize=Math.round((float)width/(float)requiredWidth);

            }
            options.inSampleSize=inSamplesize;
            options.inJustDecodeBounds=false;
         return BitmapFactory.decodeFile(path,options);

    }
    // getting system date
    private String getDate() {
        Calendar c = Calendar.getInstance();
        String sDate = c.get(Calendar.YEAR) + "-"
                + (c.get(Calendar.MONTH)+1)
                + "-" + (c.get(Calendar.DAY_OF_MONTH));
        return sDate;
    }
    // getting system time
    private String getTime(){
        Calendar c = Calendar.getInstance();
        String sTime = c.get(Calendar.HOUR_OF_DAY)
                + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        return sTime;
    }
    private class getLocation extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("newtork",String.valueOf(isNetworkEnabled));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Build.VERSION.SDK_INT>=23)
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},10);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,UploadImages.this);
                }
            });


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1=new ProgressDialog(UploadImages.this);
            progressDialog1.setMessage("fetching location");
            progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog1.setIndeterminate(false);
            progressDialog1.setCancelable(false);
            progressDialog1.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(progressDialog1.isShowing()){
            progressDialog1.setCancelable(true);
            progressDialog1.dismiss();
        }
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            cityName= addresses.get(0).getLocality();
            locationManager.removeUpdates(this);
            storageMetadata= new StorageMetadata.Builder().setCustomMetadata("Time",time).setCustomMetadata("Date",date).setCustomMetadata("location",cityName).build();
            databaseReference=databaseReference.child(cityName).child(userId).getRef();
            uploadOnFirebseStorage();
        } catch (IOException e) {
            // or show alert Dialog
            Toast.makeText(getApplicationContext(),"Connection Not Available or Low Connection"+"\n"+"Try Again",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        alertDialog.setCancelable(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("provider not available",provider);
        if(!isGPSEnabled)
        {
            alertDialog=new AlertDialog.Builder(this);
            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            //on pressing cancel button
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
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
                                if(mobileData.setMobileDataEnabled(UploadImages.this,true)) {
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
                    alertDialog1.dismiss();
                }
            });
            alertDialog1=alertBuilder.create();
            alertDialog.show();
            return temp[0];
        }
    }

}
