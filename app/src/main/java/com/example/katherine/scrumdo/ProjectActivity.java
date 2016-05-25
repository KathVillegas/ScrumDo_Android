package com.example.katherine.scrumdo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProjectActivity extends Activity {
    public String projectName;
    public long projectId;
    public long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_project);
//        ListView membersList = getListView();

        Intent intent = getIntent();
        userId = (Long) getIntent().getExtras().get("userId");
        projectId = (Long) getIntent().getExtras().get("projectId");
        projectName = (String) getIntent().getExtras().get("projectName");

        final TextView projName = (TextView)findViewById(R.id.projectName);
        projName.setText(projectName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.viewMembers);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showProjectMembers();
                projName.setText("Clicked");
            }
        });

//        try {
//            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
//            SQLiteDatabase db = scrumDoDatabaseHelper.getReadableDatabase();
//
//            Cursor cursor = db.query("MEMBERS", new String[]{"PROJECT_ID", "MEMBER_ID"},
//                    "PROJECT_ID = ?",
//                    new String[]{Long.toString(projectId)},
//                    null, null, null);
//
//            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
//                    android.R.layout.simple_list_item_1,
//                    cursor, new String[] {"MEMBER_ID"},
//                    new int[] {android.R.id.text1},
//                    1);
//
//            membersList.setAdapter(listAdapter);
//        }catch (SQLiteException e){}
        }

    public void showProjectMembers(){
        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
            SQLiteDatabase db = scrumDoDatabaseHelper.getReadableDatabase();

            final ArrayList seletedItems = new ArrayList();
            Cursor cursor = db.query("MEMBERS", new String[]{"PROJECT_ID", "MEMBER_ID"},
                    "PROJECT_ID = ?",
                    new String[]{Long.toString(projectId)},
                    null, null, null);
            final ArrayList<String> memList = new ArrayList<String>();
            cursor.moveToFirst();
            do{
                    memList.add(cursor.getString(2));
            }while(cursor.moveToNext());

            final CharSequence[] items = memList.toArray(new CharSequence[memList.size()]);
            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
            builderDialog.setTitle("Project Members");
            builderDialog.setMultiChoiceItems(items, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected,
                                            boolean isChecked) {
                            if (isChecked) {
                                seletedItems.add(items[indexSelected]);
                            } else if (seletedItems.contains(items[indexSelected])) {
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    })

                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog dialog = builderDialog.create();
            dialog.show();
        }catch (SQLiteException e){}
    }


}
