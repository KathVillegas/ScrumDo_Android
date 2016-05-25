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

    private SQLiteDatabase db;
    private Cursor cursor;

    public String projectName;
    public long projectId;
    public long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        projectId = (Long) intent.getExtras().get("projectId");
        projectName = (String) intent.getExtras().get("projectName");

        final TextView projName = (TextView)findViewById(R.id.projectName);
        projName.setText(projectName);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.viewMembers);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                showProjectMembers();
//                projName.setText("Clicked");
//            }
//        });

        }

    public void addTask(View view){

    }

    //CLICK TO VIEW MEMBERS
    public void viewMembers(View view){
        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db.query("MEMBERS", new String[]{"PROJECT_ID", "MEMBERS_ID"},
                "PROJECT_ID = ? ", new String[] {Long.toString(projectId)},
                null, null, null);
//
        final ArrayList<String> membersUnameList = new ArrayList<String>();

//        do{
//            Long memberId = cursor.getLong(1);
//            Cursor memberCursor = db.query("USERS", new String[] {"_id", "UNAME"},
//                    "_id = ? ", new String[] {Long.toString(memberId)},
//                            null, null, null );
//            if(memberCursor.moveToFirst()){
//               membersUnameList.add(memberCursor.getString(1));
//            }
//
//        }while(cursor.moveToNext());
//
//        final CharSequence[] memberItems = membersUnameList.toArray(new CharSequence[membersUnameList.size()]);
//        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
//        builderDialog.setTitle("Project Members");
//        builderDialog.setItems(memberItems,null);
//        AlertDialog dialog = builderDialog.create();
//        dialog.show();
    }
//    public void showProjectMembers(){
//        try {
//            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
//            SQLiteDatabase db = scrumDoDatabaseHelper.getReadableDatabase();
//
//            final ArrayList seletedItems = new ArrayList();
//            Cursor cursor = db.query("MEMBERS", new String[]{"PROJECT_ID", "MEMBER_ID"},
//                    "PROJECT_ID = ?",
//                    new String[]{Long.toString(projectId)},
//                    null, null, null);
//            final ArrayList<String> memList = new ArrayList<String>();
//            cursor.moveToFirst();
//            do{
//                    memList.add(cursor.getString(2));
//            }while(cursor.moveToNext());
//
//            final CharSequence[] items = memList.toArray(new CharSequence[memList.size()]);
//            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
//            builderDialog.setTitle("Project Members");
//            builderDialog.setMultiChoiceItems(items, null,
//                    new DialogInterface.OnMultiChoiceClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int indexSelected,
//                                            boolean isChecked) {
//                            if (isChecked) {
//                                seletedItems.add(items[indexSelected]);
//                            } else if (seletedItems.contains(items[indexSelected])) {
//                                seletedItems.remove(Integer.valueOf(indexSelected));
//                            }
//                        }
//                    })
//
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//
//
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//
//                        }
//                    });
//
//            AlertDialog dialog = builderDialog.create();
//            dialog.show();
//        }catch (SQLiteException e){}
//    }

}
