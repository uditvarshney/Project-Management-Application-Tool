package com.aniketudit.udit.pmma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class OtherProfile extends AppCompatActivity {

    ArrayList<String> userData;
    Toolbar toolbar;
    TextView textView;
    private String userName;
    private ImageView pic1,pic2,pic3;
    private String projectName;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        init();
        userData=getIntent().getStringArrayListExtra("othersWork");
        projectName=getIntent().getStringExtra("projectName");
        userName=userData.get(userData.size()-1);
        toolbar.setTitle(userName);
        setSupportActionBar(toolbar);
        textView.setText(projectName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OtherUserAdapter(this,userData));
    }

    private void init() {
        userData=new ArrayList<>();
        toolbar=(Toolbar) findViewById(R.id.otherProfileToolbar);
        textView=(TextView) findViewById(R.id.otherProjectName);
        recyclerView=(RecyclerView) findViewById(R.id.otherUsersData);
    }
}
