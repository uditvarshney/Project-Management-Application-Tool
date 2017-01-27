package com.example.udit.pmma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectReview extends AppCompatActivity {

    StorageReference storageReference;
    private String projectName,cityName;
    DatabaseReference databaseReference,userReference;
    private HashMap<Integer,ArrayList<String> > userDetails;
    private  String userName;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_review);
        init();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Project Review");
        storageReference=FirebaseStorage.getInstance().getReference();
        Intent i=getIntent();
        projectName=i.getStringExtra("projectName");
        cityName=i.getStringExtra("cityName");
        userDetails=new HashMap<>();
        userReference=FirebaseDatabase.getInstance().getReference("PMMA");
        // get the reference for taking the path from the real time database to store in array to populate them in glide
        getUserDetails();
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(),recyclerView,new GalleryAdapter.ClickListener(){

            @Override
            public void onClick(View view, int position) {
                Log.d("position",String.valueOf(position));
                Log.d("view",view.toString());
                Intent intent=new Intent(getApplicationContext(),OtherProfile.class);
                intent.putStringArrayListExtra("othersWork",userDetails.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d("secondPosition",String.valueOf(position));
            }
        }));
    }

    private void init() {
        recyclerView=(RecyclerView) findViewById(R.id.gallaryRecyclerView);
        pDialog = new ProgressDialog(this);
        toolbar=(Toolbar) findViewById(R.id.projectReviewToolbar);
        //imageView=(ImageView) findViewById(R.id.temp);
    }

    private void getUserDetails() {
        databaseReference=FirebaseDatabase.getInstance().getReference("project").child(projectName).child(cityName).getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    Iterable<DataSnapshot> children=dataSnapshot.getChildren();
                    int i=0;
                    for(DataSnapshot child:children)
                    {
                        if(child.hasChildren()){
                            String userId=child.getKey();
                            //getUserName(userId);
                            ArrayList<String> path=new ArrayList<String>();
                            Iterable<DataSnapshot> subchildren=child.getChildren();
                            //path.add(userName);
                            for(DataSnapshot subchild:subchildren){
                                userName=subchild.getValue().toString();
                                path.add(projectName+"/"+cityName+"/"+userId+"/"+subchild.getKey()+".jpg");
                            }
                            path.add(userName);
                            userDetails.put(i,path);
                        }
                        Log.d("userDetails",userDetails.get(i).toString());
                        i++;
                    }
                    populateImages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateImages() {
        mAdapter = new GalleryAdapter(getApplicationContext(), userDetails);
        mAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
