package com.aniketudit.udit.pmma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class ProjectUpdateStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_update_status);
        recyclerView=(RecyclerView) findViewById(R.id.projectUpdateRecyclerViewer);
        toolbar=(Toolbar) findViewById(R.id.projectUpdateStatusToolbar);
        toolbar.setTitle("Available Projects To Review");
        setSupportActionBar(toolbar);
        Intent intent =getIntent();
        ArrayList<String> list= new ArrayList<String>();
        list=intent.getStringArrayListExtra("projectArray");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProjectUpdateAdapter(this,list));
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
