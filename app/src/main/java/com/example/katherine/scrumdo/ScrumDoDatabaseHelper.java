package com.example.katherine.scrumdo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
                        + "DUE_DATE TEXT,"
                        + "STATUS TEXT);"
        );

        db.execSQL("CREATE TABLE TASKS ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "PROJECT_ID INTEGER,"
                        + "TASK_NAME TEXT,"
                        + "TASK_DETAILS TEXT,"
                        + "DUE_DATE TEXT,"
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

    public static long insertProject(SQLiteDatabase db, int adminId, String projName, String dueDate){
        ContentValues projectValues = new ContentValues();
        projectValues.put("ADMIN_ID", adminId);
        projectValues.put("PROJECT_NAME", projName);
        projectValues.put("DUE_DATE", dueDate);
        projectValues.put("STATUS", "ongoing");
        return db.insert("PROJECTS", null, projectValues);
    }

    public static void insertMembers(SQLiteDatabase db, int projId, int memberId){
        ContentValues memberValues = new ContentValues();
        memberValues.put("PROJECT_ID", projId);
        memberValues.put("MEMBER_ID", memberId);
        db.insert("MEMBERS", null, memberValues);
    }

    public static void insertTask(SQLiteDatabase db, int projId, String taskName,String taskDetail, String dueDate, int assignUser){
        ContentValues taskValues = new ContentValues();
        taskValues.put("PROJECT_ID", projId);
        taskValues.put("TASK_NAME", taskName);
        taskValues.put("TASK_DETAILS", taskDetail);
        taskValues.put("DUE_DATE", dueDate);
        taskValues.put("ASSIGNED_USER_ID", assignUser);
        taskValues.put("STATUS", "todo");
        db.insert("TASKS", null, taskValues);
    }

    public static void insertComment(SQLiteDatabase db, int taskId, int userId, String comment){
        ContentValues taskValues = new ContentValues();
        taskValues.put("TASK_ID", taskId);
        taskValues.put("USER_ID", userId);
        taskValues.put("COMMENT", comment);
        db.insert("COMMENTS", null, taskValues);
    }

    public static void updateTaskStatus(SQLiteDatabase db, String taskName, String newStatus){
        ContentValues tasksStatus = new ContentValues();
        tasksStatus.put("STATUS", newStatus);
        db.update("TASKS",tasksStatus,"TASK_NAME = ? ", new String[]{taskName});
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion){

    }
}
