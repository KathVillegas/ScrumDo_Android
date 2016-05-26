package com.example.katherine.scrumdo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProjectActivity extends Activity {

    private SQLiteDatabase db;
    private Cursor cursor;

    public String projectName;
    public long assignedUser = -1;
    public long projectId;
    public long userId;
    public final ArrayList<Long> memberIdList = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        projectId = (Long) intent.getExtras().get("projectId");
        projectName = (String) intent.getExtras().get("projectName");
        userId = (Long) intent.getExtras().get("userId");

        final TextView projName = (TextView)findViewById(R.id.projectName);
        projName.setText(projectName);
        populateTaskView();
        LinearLayout toDoLinearLayout = (LinearLayout)findViewById(R.id.toDoLayoutId);
        LinearLayout doingLinearLayout = (LinearLayout)findViewById(R.id.doingLayoutId);
        LinearLayout doneLinearLayout = (LinearLayout)findViewById(R.id.doneLayoutId);
        toDoLinearLayout.setOnTouchListener(new TouchListener());
        doingLinearLayout.setOnTouchListener(new TouchListener());
        doneLinearLayout.setOnTouchListener(new TouchListener());
        toDoLinearLayout.setOnDragListener(new DragListener());
        doingLinearLayout.setOnDragListener(new DragListener());
        doneLinearLayout.setOnDragListener(new DragListener());
    }

    public void addAssignedUser(View view){
        memberIdList.clear();
        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
            db = scrumDoDatabaseHelper.getReadableDatabase();

            final ArrayList seletedItems = new ArrayList();
            final ArrayList<String> UserList = new ArrayList<String>();

            cursor = db.query("MEMBERS", new String[]{"_id", "PROJECT_ID", "MEMBER_ID"},
                    "PROJECT_ID = ?", new String[]{Long.toString(projectId)},
                    null, null, null);

            if(cursor.moveToFirst()){
                do {
                    memberIdList.add(cursor.getLong(2));
                }while(cursor.moveToNext());
            }

            for(int i=0; i< memberIdList.size(); i++ ){
                db = scrumDoDatabaseHelper.getReadableDatabase();
                Cursor cursor2 = db.query("USERS",
                        new String[] {"_id", "UNAME"},
                        "_id=?",
                        new String[] {memberIdList.get(i).toString()},
                        null, null, null
                );

                cursor2.moveToFirst();
                UserList.add(cursor2.getString(1));
            }

            final CharSequence[] items = UserList.toArray(new CharSequence[UserList.size()]);
            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
            builderDialog.setTitle("Assign Task");
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
                            //deleting members to database
                            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                            db = scrumDoDatabaseHelper.getWritableDatabase();

                            for(int i=0; i< seletedItems.size(); i++ ){
                                SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();

                                cursor = db2.query("USERS", new String[]{"_id", "FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                                        "UNAME =?", new String[] {seletedItems.get(i).toString()},
                                        null, null, null);

                                if(cursor.moveToFirst()){
                                    assignedUser = cursor.getLong(0);
                                    Toast toast = Toast.makeText(ProjectActivity.this, Long.toString(assignedUser), Toast.LENGTH_LONG);
                                    toast.show();
                                    dialog.dismiss();
                                }

                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builderDialog.create();
            dialog.show();
        }catch (SQLiteException e){
            System.out.println(e.getMessage());
        }
    }
    public void addTask(View view){

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
        View promptView = layoutInflater.inflate(R.layout.add_task, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Add Task");

        final EditText TaskName = (EditText) promptView.findViewById(R.id.taskName);
        final EditText TaskDetail = (EditText) promptView.findViewById(R.id.taskDetail);
        final DatePicker taskDueDate = (DatePicker) promptView.findViewById(R.id.taskDueDate);
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
//
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = TaskName.getText().toString();
                String taskDetail = TaskDetail.getText().toString();
                int month = taskDueDate.getMonth() + 1;
                int day = taskDueDate.getDayOfMonth();
                int year = taskDueDate.getYear();
                String taskDueDate = month + "-" + day + "-" + year;

                if (taskName.length() == 0) {
                    TaskName.setError("Enter task name!");
                }
                else if (taskName.length() == 0) {
                    TaskDetail.setError("Please give a description!");
                }
                else if (assignedUser == -1) {
                    Toast toast = Toast.makeText(ProjectActivity.this, "Please assign the task to a member.", Toast.LENGTH_LONG);
                    toast.show();
                }else {
                    int p_Id = (int) projectId;
                    int a_User = (int) assignedUser;
                    //Saving to database
                    try {
                        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                        db = scrumDoDatabaseHelper.getWritableDatabase();
                        ScrumDoDatabaseHelper.insertTask(db, p_Id, taskName, taskDetail, taskDueDate, a_User);
                        alert.dismiss();
                        populateTaskView();
                        Toast toast = Toast.makeText(ProjectActivity.this, "Success.", Toast.LENGTH_LONG);
                        toast.show();
                    } catch (SQLiteException e){}

                }
            }
        });

        TaskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TaskName.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        TaskDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TaskDetail.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    public void addMember(){

        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
            db = scrumDoDatabaseHelper.getReadableDatabase();

            final ArrayList seletedItems = new ArrayList();
            final ArrayList<String> UserList = new ArrayList<String>();

            cursor = db.query("USERS", new String[]{"_id","FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                    null, null,
                    null, null, null);

            cursor.moveToFirst();
            do {
                if(!memberIdList.contains(cursor.getLong(0))){
                    if(userId != cursor.getLong(0))
                        UserList.add(cursor.getString(3));
                }

            }while(cursor.moveToNext());
            final CharSequence[] items = UserList.toArray(new CharSequence[UserList.size()]);
            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
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
                            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                            db = scrumDoDatabaseHelper.getWritableDatabase();

                            for(int i=0; i< seletedItems.size(); i++ ){
                                SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();

                                cursor = db2.query("USERS", new String[]{"_id", "FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                                        "UNAME =?", new String[] {seletedItems.get(i).toString()},
                                        null, null, null);

                                if(cursor.moveToFirst()){
                                    long memId = cursor.getLong(0);
                                    int proj_id = (int) projectId;
                                    int mem_id = (int) memId;
                                    ScrumDoDatabaseHelper.insertMembers(db, proj_id, mem_id);
                                    Toast toast = Toast.makeText(ProjectActivity.this, "Success", Toast.LENGTH_LONG);
                                    toast.show();
                                    dialog.dismiss();
                                }

                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builderDialog.create();
            dialog.show();
        }catch (SQLiteException e){
            System.out.println(e.getMessage());
        }
    }
    public void deleteMember(){
        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
            db = scrumDoDatabaseHelper.getReadableDatabase();

            final ArrayList seletedItems = new ArrayList();
            final ArrayList<String> UserList = new ArrayList<String>();

            for(int i=0; i< memberIdList.size(); i++ ){
                db = scrumDoDatabaseHelper.getReadableDatabase();
                Cursor cursor2 = db.query("USERS",
                        new String[] {"_id", "UNAME"},
                        "_id=?",
                        new String[] {memberIdList.get(i).toString()},
                        null, null, null
                );

                cursor2.moveToFirst();
                UserList.add(cursor2.getString(1));
            }

            final CharSequence[] items = UserList.toArray(new CharSequence[UserList.size()]);
            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
            builderDialog.setTitle("Delete Members");
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

                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //deleting members to database
                            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                            db = scrumDoDatabaseHelper.getWritableDatabase();

                            for(int i=0; i< seletedItems.size(); i++ ){
                                SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();

                                cursor = db2.query("USERS", new String[]{"_id", "FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                                        "UNAME =?", new String[] {seletedItems.get(i).toString()},
                                        null, null, null);

                                if(cursor.moveToFirst()){
                                    db = scrumDoDatabaseHelper.getWritableDatabase();
                                    db.delete("MEMBERS",
                                            "MEMBER_ID=? AND PROJECT_ID=?",
                                            new String[]{cursor.getString(0), Long.toString(projectId)}
                                    );
                                    Toast toast = Toast.makeText(ProjectActivity.this, "Success", Toast.LENGTH_LONG);
                                    toast.show();
                                    dialog.dismiss();
                                }

                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builderDialog.create();
            dialog.show();
        }catch (SQLiteException e){
            System.out.println(e.getMessage());
        }
    }
    public void populateMemberView(View view){
        memberIdList.clear();
        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
        View promptView = layoutInflater.inflate(R.layout.view_members, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Members");

        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db.query("MEMBERS", new String[] {"_id" , "PROJECT_ID", "MEMBER_ID"},
                "PROJECT_ID = ?", new String [] {Long.toString(projectId)},
                null, null, null);


        ListView memberList = (ListView)promptView.findViewById(R.id.listView);
        final ArrayList<String> memberNameList = new ArrayList<String>();

        if(cursor.moveToFirst()){
            do {
                memberIdList.add(cursor.getLong(2));
            }while(cursor.moveToNext());
        }

        for(int i=0; i< memberIdList.size(); i++ ){
            db = scrumDoDatabaseHelper.getReadableDatabase();
                Cursor cursor2 = db.query("USERS",
                        new String[] {"_id", "UNAME"},
                        "_id=?",
                        new String[] {memberIdList.get(i).toString()},
                        null, null, null
                );

            cursor2.moveToFirst();
            memberNameList.add(cursor2.getString(1));
        }
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<String> (getBaseContext(),
                android.R.layout.simple_list_item_1, memberNameList);

        memberList.setAdapter(memberAdapter);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("Add Members", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addMember();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Delete Members",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteMember();
                                dialog.dismiss();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

//    REATE TABLE TASKS ("
//                               + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
//                               + "PROJECT_ID INTEGER,"
//                               + "TASK_NAME TEXT,"
//                               + "TASK_DETAILS TEXT,"
//                               + "DUE_DATE TEXT,"
//                               + "ASSIGNED_USER_ID INTEGER,"
//                               + "LINK TEXT,"
//                               + "STATUS TEXT

    //populate the TASKS VIEW
    public void populateTaskView(){
        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db.query("TASKS", new String[]{"_id", "PROJECT_ID", "TASK_NAME", "STATUS"},
                "PROJECT_ID = ?", new String[]{Long.toString(projectId)},
                null, null, null);

        int numberOfRows = cursor.getCount();
        ArrayList<Long> taskIdList = new ArrayList<Long>();
        LinearLayout toDoLinearLayout = (LinearLayout) findViewById(R.id.toDoLayoutId);
        LinearLayout doingLinearLayout = (LinearLayout)findViewById(R.id.doingLayoutId);
        LinearLayout doneLinearLayout = (LinearLayout)findViewById(R.id.doneLayoutId);

        TextView[] taskViewArray = new TextView[numberOfRows];

        if(cursor.moveToFirst()){
            int i = 0;
            do{
                taskIdList.add(cursor.getLong(0));

                TextView newTaskView = new TextView(this);
                newTaskView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newTaskView.setText(cursor.getString(2));
                newTaskView.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
                newTaskView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)

                newTaskView.setOnTouchListener(new TouchListener());
                newTaskView.setOnDragListener(new DragListener());

                newTaskView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                if(cursor.getString(3).equals("todo")){
                    toDoLinearLayout.addView(newTaskView);
                }else if(cursor.getString(3).equals("doing")){
                    doingLinearLayout.addView(newTaskView);
                }else if(cursor.getString(3).equals("done")){
                    doneLinearLayout.addView(newTaskView);
                }

                taskViewArray[i] = newTaskView;
                i++;
            }while(cursor.moveToNext());
        }else{
            TextView newTaskView = new TextView(this);
            newTaskView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            newTaskView.setText("not inserted");
            newTaskView.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            newTaskView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            toDoLinearLayout.addView(newTaskView);
        }
    }
}
