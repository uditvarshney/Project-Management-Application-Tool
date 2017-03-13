package com.aniketudit.udit.pmma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SelectCity extends AppCompatActivity {

    private String projectName;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseImageLoader firebaseImageLoader;
    private ArrayList<String> currentWorkingCity;
    RecyclerView currentCityRecyclerViewer;
    private Toolbar toolbar;
    // check internet connection
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        currentCityRecyclerViewer=(RecyclerView) findViewById(R.id.cityRecyclerView);
        toolbar=(Toolbar) findViewById(R.id.selectCityToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("City");
        firebaseImageLoader=new FirebaseImageLoader();
        projectName=getIntent().getStringExtra("projectName");
        databaseReference= FirebaseDatabase.getInstance().getReference("project").child(projectName).getRef();
        currentWorkingCity=new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    //Log.d("childFound","true");
                    Iterable<DataSnapshot> children=dataSnapshot.getChildren();
                    for(DataSnapshot child:children)
                    {
                        currentWorkingCity.add(child.getKey());

                    }
                    //Log.d("currentVity",currentWorkingCity.toString());
                    currentCityRecyclerViewer.setLayoutManager(new LinearLayoutManager(SelectCity.this));
                    currentCityRecyclerViewer.setAdapter(new CityAdapter(SelectCity.this,currentWorkingCity,projectName));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Check Internet Connection",Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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
}
