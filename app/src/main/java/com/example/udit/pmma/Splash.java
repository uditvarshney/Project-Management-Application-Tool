package com.example.udit.pmma;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimerTask;

public class Splash extends AppCompatActivity implements LocationListener{

    SharedPreferences sharedPreferences;
    public static final String loginPreferenece="mypref";
    public static final String loginStatus ="loginStatus";
    public static final String userkey ="userId";
    public TextView mTextView;
    public ProgressBar progressBar;
    private ProgressDialog progressDialog;
    LocationManager locationManager;
    Location location;
    double latitude;
    double longitude;
    String cityName;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
            if(Build.VERSION.SDK_INT>=23 &&(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Need Multiple Permissions");
                    builder.setMessage("This app needs Storage and Location permissions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(Splash.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},105);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},105);

            }
        else{
            sharedPreferences =getSharedPreferences(loginPreferenece, Context.MODE_PRIVATE);
            Log.d("login status",sharedPreferences.getString(loginStatus,"fails"));
            if(sharedPreferences.getString(loginStatus,"fails").equals("success"))
            {
                // checking if user is already login and user will switch to either to
                // inactive or active activity
                Log.d("database","fetching data");
                final String userId = sharedPreferences.getString(userkey,null);
                if(userId == null)
                {
                    // if user uninstall the app so userId in the SQLite database get lost so relogin

                }
                projectUpdate update =new projectUpdate();
                update.execute(userId);
            }
            else
            {
                // user is not logged in switch to login activity
                getLocation();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==105){
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }
            if(allgranted){
                proceedAfterPermission();
            }
            else if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Storage and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Splash.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},105);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

        }
    }

    public void proceedAfterPermission() {
        Toast.makeText(getApplicationContext(),"All permission granted",Toast.LENGTH_LONG).show();
        sharedPreferences =getSharedPreferences(loginPreferenece, Context.MODE_PRIVATE);
        Log.d("login status",sharedPreferences.getString(loginStatus,"fails"));
        if(sharedPreferences.getString(loginStatus,"fails").equals("success"))
        {
            // checking if user is already login and user will switch to either to
            // inactive or active activity
            Log.d("database","fetching data");
            final String userId = sharedPreferences.getString(userkey,null);
            if(userId == null)
            {
                // if user uninstall the app so userId in the SQLite database get lost so relogin

            }
            projectUpdate update =new projectUpdate();
            update.execute(userId);
        }
        else
        {
            // user is not logged in switch to login activity
            getLocation();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d("locationUpdated","hkjkgj");
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String cityName= addresses.get(0).getLocality();
            locationManager.removeUpdates(this);
            Intent intent=new Intent(Splash.this,SignUp.class);
            intent.putExtra("cityName",cityName);
            startActivity(intent);
            finish();
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
    private void getLocation(){
        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("newtork",String.valueOf(isNetworkEnabled));

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,Splash.this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,Splash.this);

    }

    private class projectUpdate extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(Splash.this);
            progressDialog.setMessage("fetching the data from the server");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            final String Id=params[0];
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("PMMA");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(Id).hasChild("projects"))
                    {
                        if(dataSnapshot.child(Id).getChildrenCount() !=0)
                        {
                            Iterable<DataSnapshot> children = dataSnapshot.child(Id).child("projects").getChildren();
                            Intent intent =new Intent(getApplicationContext(),Selection.class);    // check out this
                            final ArrayList<String> availableProject = new ArrayList<String>();
                            for(DataSnapshot child: children)
                            {
                                // store the assigned projects in the active activity
                                availableProject.add(child.getKey());
                            }
                            intent.putExtra("projectArray",availableProject);
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            // no project has assigned to the user and user will switch to inactive active activity
                            Intent intent =new Intent(getApplicationContext(),Inactive.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        // no project has assigned to the user and user will switch to inactive active activity
                        Intent intent =new Intent(getApplicationContext(),Inactive.class);
                        finish();
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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
}
