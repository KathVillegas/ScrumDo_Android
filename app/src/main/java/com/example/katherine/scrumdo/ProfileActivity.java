package com.example.katherine.scrumdo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileActivity extends Activity {
    private SQLiteDatabase db;
    public static String ProjectName = null;
    public static int month;
    public static int day;
    public static int year;
    public  TextView fname;
    public  TextView lname;
    public  TextView uname;
    public  TextView password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showAddProject();

            }
        });

        Intent intent = getIntent();
        long userId = (Long) getIntent().getExtras().get("userId");

        try{
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
            SQLiteDatabase db = scrumDoDatabaseHelper.getReadableDatabase();

            Cursor cursor = db.query("USERS", new String[]{"FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                    "_id = ?",
                    new String[] {Long.toString(userId)},
                    null, null, null);

            if(cursor.moveToFirst()){
                 fname = (TextView)findViewById(R.id.fname);
                 lname = (TextView)findViewById(R.id.lname);
                 uname = (TextView)findViewById(R.id.uname);
                 password = (TextView)findViewById(R.id.password);
                ImageView profileImage = (ImageView)findViewById(R.id.profileImage);

                fname.setText(cursor.getString(0));
                lname.setText(cursor.getString(1));
                uname.setText(cursor.getString(2));
                password.setText(cursor.getString(3));
                byte[] image = cursor.getBlob(4);

                Bitmap userImage = getImage(image);
                profileImage.setImageBitmap(userImage);
            }
        }catch (SQLiteException e){}

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

                if (ProjectName.length() == 0) {
                    projName.setError("Enter project name!");
                } else {
                    fname.setText(ProjectName);
                    lname.setText(String.valueOf(month));
                    uname.setText(String.valueOf(day));
                    password.setText(String.valueOf(year));

                    //Saving to database

                    addMembers();
                    alert.dismiss();
                }
            }
        });
    }

    public void addMembers(){
        final CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
        // arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();

        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProfileActivity.this);
        builderDialog.setTitle("Add Members");
        builderDialog.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            // write your code when user Uchecked the checkbox
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        AlertDialog dialog = builderDialog.create();
        dialog.show();
    }
}
