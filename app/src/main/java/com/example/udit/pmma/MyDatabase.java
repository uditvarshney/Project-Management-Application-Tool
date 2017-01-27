package com.example.udit.pmma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Dell PC on 20-01-2017.
 */

public class MyDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DatabaseName="projects";
    private static final String Tablename="projectDetails";
    private static final String projectName=null;
    private static final String image1="front_image";
    private static final String image2="rightSide_image";
    private static final String image3="leftSideImage";
    public MyDatabase(Context context) {
        super(context, DatabaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //String query;
        //query = "CREATE TABLE"+ Tablename+"("+projectName+"Text,"+image1+"BOLB,"+image2+"BOLB,"+image3+"BOLB);";
        //database.execSQL(query);
        Log.d("databaseClass","animals Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXIST "+DatabaseName);
        //onCreate(db);
    }
    public void queryData(String sql){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }
    public void insertData(String project,byte[] image_1,byte[] image_2,byte[] image_3){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="INSERT INTO PROJECTDETAILS VALUES (NULL,?,?,?,?)";
        SQLiteStatement statement=sqLiteDatabase.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1,project);
        statement.bindBlob(2,image_1);
        statement.bindBlob(3,image_2);
        statement.bindBlob(4,image_3);
        statement.executeInsert();
    }
    public void insertData(String project,byte[] image_1,byte[] image_2){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="INSERT INTO PROJECTDETAILS VALUES (NULL,?,?,?,?)";
        SQLiteStatement statement=sqLiteDatabase.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1,project);
        statement.bindBlob(2,image_1);
        statement.bindBlob(3,image_2);
        statement.executeInsert();
    }
    public void insertData(String project,byte[] image_1){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="INSERT INTO PROJECTDETAILS VALUES (NULL,?,?,?,?)";
        SQLiteStatement statement=sqLiteDatabase.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1,project);
        statement.bindBlob(2,image_1);
        statement.executeInsert();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database=getReadableDatabase();
        return database.rawQuery(sql,null);
    }
    public void insertUser(String user,byte[] image)
    {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="INSERT INTO USERPIC VALUES (NULL,?,?)";
        SQLiteStatement statement=sqLiteDatabase.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1,user);
        statement.bindBlob(2,image);
        statement.executeInsert();
    }
    public Cursor queryUserData(String sql){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        return sqLiteDatabase.rawQuery(sql,null);
    }
    public void updateData(byte[] pic)
    {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        //contentValues.put("name","userPic");
        contentValues.put("image_1",pic);
        sqLiteDatabase.update("USERPIC",contentValues,"name=?",new  String[]{"userPic"});
    }

}
