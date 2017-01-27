package com.example.udit.pmma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfile extends AppCompatActivity {

    ArrayList<String> userData;
    Toolbar toolbar;
    TextView textView;
    private String userName;
    private CircleImageView circleImageView;
    private ImageView pic1,pic2,pic3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        init();
        userData=getIntent().getStringArrayListExtra("othersWork");
        userName=userData.get(userData.size()-1);
        toolbar.setTitle(userName);
        setSupportActionBar(toolbar);
        textView.setText(userName);
        for(int i=0;i<userData.size();i++)
        {
            if(i==userData.size()-1){
                textView.setText(userData.get(i));
            }
            else if(i==0)
            {
                Glide.with(this).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference(userData.get(0)))
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(pic1);
            }
            else if(i==1)
            {
                Glide.with(this).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference(userData.get(i)))
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(pic2);
            }
            else if(i==2)
            {
                Glide.with(this).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference(userData.get(i)))
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(pic3);
            }
        }

        //Glide.with(this).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference(userData.get(1)))
        //        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(pic2);
        //Glide.with(this).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference(userData.get(2)))
        //        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(pic3);
    }

    private void init() {
        userData=new ArrayList<>();
        toolbar=(Toolbar) findViewById(R.id.otherProfileToolbar);
        textView=(TextView) findViewById(R.id.otheruserName);
        circleImageView=(CircleImageView) findViewById(R.id.otherUserPic);
        pic1=(ImageView) findViewById(R.id.othersProfilePic1);
        pic2=(ImageView) findViewById(R.id.othersProfilePic2);
        pic3=(ImageView) findViewById(R.id.othersProfilePic3);
    }
}
