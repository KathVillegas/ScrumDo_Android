package com.example.katherine.scrumdo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Katherine on 14 May 2016.
 */
public class ScrumDoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "scrumDo";
    private static final int DB_VERSION = 1;

    ScrumDoDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE USERS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "FNAME TEXT,"
                + "LNAME TEXT,"
                + "UNAME TEXT,"
                + "PASSWORD TEXT,"
                + "IMAGE BLOB);"
        );

        db.execSQL("CREATE TABLE PROJECTS ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "ADMIN_ID INTEGER,"
                        + "PROJECT_NAME TEXT,"
                        + "DUE_DATE NUMERIC,"
                        + "STATUS TEXT);"
        );

        db.execSQL("CREATE TABLE TASKS ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "PROJECT_ID INTEGER,"
                        + "TASK_NAME TEXT,"
                        + "DUE_DATE NUMERIC,"
                        + "ASSIGNED_USER_ID INTEGER,"
                        + "LINK TEXT,"
                        + "STATUS TEXT);"
        );

        db.execSQL("CREATE TABLE COMMENTS ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "TASK_ID INTEGER,"
                        + "USER_ID INTEGER,"
                        + "COMMENT TEXT);"
        );

        db.execSQL("CREATE TABLE MEMBERS ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "PROJECT_ID INTEGER,"
                        + "MEMBER_ID INTEGER);"
        );
    }

    public static long insertUsers(SQLiteDatabase db, String fname, String lname, String uname, String pwd, byte[] image){
        ContentValues userValues = new ContentValues();
        userValues.put("FNAME", fname);
        userValues.put("LNAME", lname);
        userValues.put("UNAME", uname);
        userValues.put("PASSWORD", pwd);
        userValues.put("IMAGE", image);
        return db.insert("USERS", null, userValues);
    }

    public static long insertUsers(SQLiteDatabase db, String fname, String lname, String uname, String pwd, byte[] image){
        ContentValues userValues = new ContentValues();
        userValues.put("FNAME", fname);
        userValues.put("LNAME", lname);
        userValues.put("UNAME", uname);
        userValues.put("PASSWORD", pwd);
        userValues.put("IMAGE", image);
        return db.insert("USERS", null, userValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion){

    }
}
