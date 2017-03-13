package com.aniketudit.udit.pmma;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Splash extends AppCompatActivity implements LocationListener{

    private SharedPreferences sharedPreferences;
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
    private AlertDialog alertDialog;
    int currentVersion= Build.VERSION.SDK_INT;
    private AlertDialog alertDialog1;
    private boolean isConnected=false;
    private String userId;
    private NetworkReceiver networkReceiver;
    private NetworkReceiver1 networkReceiver1;
    private AlertDialog.Builder builder1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkConnection();
            if(Build.VERSION.SDK_INT>=23 &&(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
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
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},105);
            }
        else{
            sharedPreferences =getSharedPreferences(loginPreferenece, Context.MODE_PRIVATE);
            //Log.d("login status",sharedPreferences.getString(loginStatus,"fails"));
            if(sharedPreferences.getString(loginStatus,"fails").equals("success"))
            {
                // checking if user is already login and user will switch to either to
                // inactive or active activity
                //Log.d("database","fetching data");
                userId = sharedPreferences.getString(userkey,null);
                if(userId == null)
                {
                    // if user uninstall the app so userId in the SQLite database get lost so relogin

                }
                if(checkConnection()){
                    projectUpdate update =new projectUpdate();
                    update.execute(userId);
                }
                else
                {
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    networkReceiver=new NetworkReceiver();
                    registerReceiver(networkReceiver,filter);
                }

            }
            else
            {
                // user is not logged in switch to login activity
                if (checkConnection())
                    getLocation();
                else
                {
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    networkReceiver1=new NetworkReceiver1();
                    registerReceiver(networkReceiver1,filter);
                }
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
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
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
        //Toast.makeText(getApplicationContext(),"All permission granted",Toast.LENGTH_LONG).show();
        sharedPreferences =getSharedPreferences(loginPreferenece, Context.MODE_PRIVATE);
        //Log.d("login status",sharedPreferences.getString(loginStatus,"fails"));
        if(sharedPreferences.getString(loginStatus,"fails").equals("success"))
        {
            // checking if user is already login and user will switch to either to
            // inactive or active activity
            //Log.d("database","fetching data");
            final String userId = sharedPreferences.getString(userkey,null);
            if(userId == null)
            {
                // if user uninstall the app so userId in the SQLite database get lost so relogin

            }
            if(checkConnection()){
                projectUpdate update =new projectUpdate();
                update.execute(userId);
            }
            else
            {
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                networkReceiver=new NetworkReceiver();
                registerReceiver(networkReceiver,filter);
            }
        }
        else
        {
            // user is not logged in switch to login activity
            if (checkConnection())
                getLocation();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        //Log.d("locationUpdated","hkjkgj");
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            if(alertDialog!=null && alertDialog.isShowing()){
                Log.d("dialogDismissed","true");
                alertDialog.dismiss();
            }
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
        /*if(alertDialog.isShowing()){
            Log.d("providerEnable",provider);
            alertDialog.setCancelable(true);
            alertDialog.dismiss();
        }*/
    }

    @Override
    public void onProviderDisabled(String provider) {

        if(!isGPSEnabled)
        {
            Log.d("provider not available",provider);
            builder1=new AlertDialog.Builder(this);
            // Setting Dialog Title
            builder1.setTitle("GPS is settings");

            // Setting Dialog Message
            builder1.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            builder1.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    //builder1.setCancelable(true);
                    dialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            //builder1.setCancelable(false);
            //on pressing cancel button
            builder1.show();
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
            DatabaseReference myRef = database.getReference("PMMA").child(Id).getRef();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild("projects"))
                    {
                        if(dataSnapshot.child("projects").getChildrenCount() !=0)
                        {
                            Iterable<DataSnapshot> children = dataSnapshot.child("projects").getChildren();
                            Intent intent =new Intent(getApplicationContext(),Selection.class);    // check out this
                            final ArrayList<String> availableProject = new ArrayList<String>();
                            final ArrayList<String> projectStatus = new ArrayList<String>();
                            for(DataSnapshot child: children)
                            {
                                // store the assigned projects in the active activity
                                String projectName=child.getKey().toString();
                                availableProject.add(projectName);
                                String value=child.getValue().toString();
                                projectStatus.add(value);
                                if(!sharedPreferences.getString(projectName,"false").equals(value)){
                                    SharedPreferences.Editor editorStatus=sharedPreferences.edit();
                                    editorStatus.putString(projectName,value);
                                    editorStatus.commit();
                                }
                            }
                            intent.putExtra("projectArray",availableProject);
                            intent.putStringArrayListExtra("projectStatus",projectStatus);
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
            alertBuilder.setCancelable(false);
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
                                if(mobileData.setMobileDataEnabled(Splash.this,true)) {
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
                    if(checkConnection())
                        alertDialog1.dismiss();
                    else{
                        alertDialog1.dismiss();
                        finish();
                    }
                }
            });
            alertDialog1=alertBuilder.create();
            alertDialog1.show();
            return temp[0];
        }
    }
    public class NetworkReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkAvailable(context);
        }
        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                //Log.v(LOG_TAG, "You are connected to Internet!");
                //Toast.makeText(context, "Internet availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
                alertDialog1.setCancelable(true);
                if (alertDialog1.isShowing())
                    alertDialog1.dismiss();
                projectUpdate update =new projectUpdate();
                update.execute(userId);
                unregisterReceiver(networkReceiver);
                return true;
            }
            else {
                //Log.v(LOG_TAG, "You are not connected to Internet!");
                //Toast.makeText(context, "Internet NOT availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
    public class NetworkReceiver1 extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkAvailable(context);
        }
        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                //Log.v(LOG_TAG, "You are connected to Internet!");
                //Toast.makeText(context, "Internet availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
                alertDialog1.setCancelable(true);
                if (alertDialog1.isShowing())
                    alertDialog1.dismiss();
                getLocation();
                unregisterReceiver(networkReceiver1);
                return true;
            }
            else {
                //Log.v(LOG_TAG, "You are not connected to Internet!");
                //Toast.makeText(context, "Internet NOT availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

}
