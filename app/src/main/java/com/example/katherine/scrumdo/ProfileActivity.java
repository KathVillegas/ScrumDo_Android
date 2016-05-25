package com.example.katherine.scrumdo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends Activity {
    private SQLiteDatabase db;
    private Cursor cursor;
    public static String ProjectName;
    public static int month;
    public static int day;
    public static int year;
    public  TextView password;
    public long projId;
    public long userId;
    public String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProject();
            }
        });

        Intent intent = getIntent();
        userId = (Long) intent.getExtras().get("userId");

        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
            db = scrumDoDatabaseHelper.getReadableDatabase();

            cursor = db.query("USERS", new String[]{"FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                    "_id = ?",
                    new String[]{Long.toString(userId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                TextView fname = (TextView) findViewById(R.id.fname);
                TextView lname = (TextView) findViewById(R.id.lname);
                TextView uname = (TextView) findViewById(R.id.uname);
                TextView password = (TextView) findViewById(R.id.password);
                ImageView profileImage = (ImageView) findViewById(R.id.profileImage);

                fname.setText(cursor.getString(0));
                lname.setText(cursor.getString(1));
                uname.setText(cursor.getString(2));
                user = cursor.getString(2);
                password.setText(cursor.getString(3));
                byte[] image = cursor.getBlob(4);

                Bitmap userImage = getImage(image);
                profileImage.setImageBitmap(userImage);
            }

            populateProjectView();

        }catch (SQLiteException e){
            System.out.println(e.getMessage());
        }

    }
    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void logOutClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // method for addingProject showMembers() is called
    public void showAddProject(){

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(ProfileActivity.this);
        View promptView = layoutInflater.inflate(R.layout.add_project, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Add Project");


        final EditText projName = (EditText) promptView.findViewById(R.id.projName);
        final DatePicker dueDate = (DatePicker) promptView.findViewById(R.id.dueDate);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        populateProjectView();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectName = projName.getText().toString();
                month = dueDate.getMonth() + 1;
                day = dueDate.getDayOfMonth();
                year = dueDate.getYear();
                String dueDate = month + "-" + day + "-" + year;

                if (ProjectName.length() == 0) {
                    projName.setError("Enter project name!");
                } else {

                    int u_Id = (int) userId;
                    //Saving to database
                    try {
                        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProfileActivity.this);
                        db = scrumDoDatabaseHelper.getWritableDatabase();
                        projId = ScrumDoDatabaseHelper.insertProject(db, u_Id, ProjectName, dueDate);
                        populateProjectView();
                        addMembers();
                        alert.dismiss();
                    } catch (SQLiteException e){}

                }
            }
        });

        projName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projName.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

//  Function for Adding Members. Called on showAddProject()
    public void addMembers(){
        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProfileActivity.this);
            db = scrumDoDatabaseHelper.getReadableDatabase();

            final ArrayList seletedItems = new ArrayList();
            cursor = db.query("USERS", new String[]{"FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                     null, null,
                    null, null, null);
            final ArrayList<String> UserList = new ArrayList<String>();
            cursor.moveToFirst();
                do{
                    if(!user.equals(cursor.getString(2))){
                        UserList.add(cursor.getString(2));
                    }
                }while(cursor.moveToNext());

            final CharSequence[] items = UserList.toArray(new CharSequence[UserList.size()]);
            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProfileActivity.this);
            builderDialog.setTitle("Add Members");
            builderDialog.setMultiChoiceItems(items, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected,
                                            boolean isChecked) {
                            if (isChecked) {
                                seletedItems.add(items[indexSelected]);
                            } else if (seletedItems.contains(items[indexSelected])) {
//                                seletedItems.remove(Integer.valueOf(indexSelected));
                                seletedItems.add(items[indexSelected]);
                            }
                        }
                    })

                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //saving members to database
                            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProfileActivity.this);
                            db = scrumDoDatabaseHelper.getWritableDatabase();

                            for(int i=0; i< seletedItems.size(); i++ ){
                                SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();

                                cursor = db2.query("USERS", new String[]{"_id", "FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                                        "UNAME =?", new String[] {seletedItems.get(i).toString()},
                                        null, null, null);

                                if(cursor.moveToFirst()){
                                    long memId = cursor.getLong(0);
                                    int proj_id = (int) projId;
                                    int mem_id = (int) memId;
                                    ScrumDoDatabaseHelper.insertMembers(db, proj_id, mem_id);
                                    Toast toast = Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_LONG);
                                    toast.show();
                                }

                            }
                            Intent intent = new Intent(ProfileActivity.this, ProjectActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("projectId", projId);
                            intent.putExtra("projectName", ProjectName);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            populateProjectView();
                            Intent intent = new Intent(ProfileActivity.this, ProjectActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("projectId", projId);
                            intent.putExtra("projectName", ProjectName);
                            startActivity(intent);
                        }
                    });

            AlertDialog dialog = builderDialog.create();
            dialog.show();
        }catch (SQLiteException e){
            System.out.println(e.getMessage());
        }
    }

    public void onListItemClick(ListView listView, long projId){
        Intent intent = new Intent(ProfileActivity.this, ProjectActivity.class);
        intent.putExtra("projectId", projId);
        startActivity(intent);
    }

    // Populate the Project View.
    public void populateProjectView(){
        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db.query("PROJECTS", new String[] {"_id", "ADMIN_ID", "PROJECT_NAME"},
                "ADMIN_ID = ?", new String [] {Long.toString(userId)},
                null, null, null);

        ListView projectList = (ListView)findViewById(R.id.projectView);
        final ArrayList<String> projNameList = new ArrayList<String>();
        final ArrayList<Long> projIdList = new ArrayList<Long>();

        if(cursor.moveToFirst()){
            do {
                projIdList.add(cursor.getLong(0));
                projNameList.add(cursor.getString(2));
            }while(cursor.moveToNext());
        }
        ArrayAdapter<String> projectAdapter = new ArrayAdapter<String> (getBaseContext(),
                android.R.layout.simple_list_item_1, projNameList);

        projectList.setAdapter(projectAdapter);
        AdapterView.OnItemClickListener projectListItemClick =
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> listView, View v,int position,long id) {
                            Long clickedProjId = projIdList.get(position);
                            String clickedProjName = projNameList.get(position);

                            Intent intent = new Intent(ProfileActivity.this, ProjectActivity.class);
                            intent.putExtra("projectId",clickedProjId);
                            intent.putExtra("projectName",clickedProjName);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        }
                    };

        projectList.setOnItemClickListener(projectListItemClick);
    }
}
