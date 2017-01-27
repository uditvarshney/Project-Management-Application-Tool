package com.example.udit.pmma;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class Active extends AppCompatActivity {

    RecyclerView viewProject;
    public static final String loginPreferenece="mypref";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        toolbar=(Toolbar) findViewById(R.id.activetoolbar);
        toolbar.setTitle("Available Projects");
        setSupportActionBar(toolbar);
        Intent intent =getIntent();
        ArrayList<String> list= new ArrayList<String>();
        list=intent.getStringArrayListExtra("projectArray");
        viewProject=(RecyclerView) findViewById(R.id.recyclerView);
        viewProject.setLayoutManager(new LinearLayoutManager(this));
        viewProject.setAdapter(new Adapter(this,list));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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
