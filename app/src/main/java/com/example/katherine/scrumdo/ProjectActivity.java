package com.example.katherine.scrumdo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    public String assignedname;
    public long projectId;
    public long userId;
    public long taskId;
    public final ArrayList<Long> memberIdList = new ArrayList<Long>();
    public final ArrayList<String> commentsList = new ArrayList<String>();
    public final ArrayList<String> fullComments = new ArrayList<String>();
    public final ArrayList<Long> fromUsersIdList = new ArrayList<Long>();

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

        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
        SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db2.query("PROJECTS", new String[]{"_id", "ADMIN_ID"},
                "_id =?", new String[] {Long.toString(projectId)},
                null, null, null);
        cursor.moveToFirst();

        if(cursor.getLong(1) != userId) {
            Button addTask = (Button)findViewById(R.id.addTask);
            Button viewMembers = (Button) findViewById(R.id.viewMem);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.deleteProject);

            addTask.setVisibility(View.GONE);
            viewMembers.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
        else{
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.deleteProject);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteProject();
                }
            });
        }
    }

    public void deleteProject(){
        try {
            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
            builderDialog.setTitle("Delete Project");
            builderDialog.setMessage("Are you sure you want to delete the project?");
            builderDialog
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                            SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();
                            db = scrumDoDatabaseHelper.getWritableDatabase();

                            //delete task
                            cursor = db2.query("TASKS", new String[]{"_id"},
                                    "PROJECT_ID =?", new String[]{Long.toString(projectId)},
                                    null, null, null);
                            if (cursor.moveToFirst()) {
                                do {
                                    db.delete("TASKS",
                                            "_id=?",
                                            new String[]{cursor.getString(0)}
                                    );
                                    Toast toast = Toast.makeText(ProjectActivity.this, "Delete Success", Toast.LENGTH_LONG);
                                    toast.show();
                                } while (cursor.moveToNext());
                            }
                            //delete member
                            cursor = db2.query("MEMBERS", new String[]{"_id"},
                                    "PROJECT_ID =?", new String[]{Long.toString(projectId)},
                                    null, null, null);

                            if (cursor.moveToFirst()) {
                                do {
                                    db.delete("MEMBERS",
                                            "_id=?",
                                            new String[]{cursor.getString(0)}
                                    );
                                    Toast toast = Toast.makeText(ProjectActivity.this, "Delete Success", Toast.LENGTH_LONG);
                                    toast.show();
                                } while (cursor.moveToNext());
                            }

                            //delete project
                            db.delete("PROJECTS",
                                    "_id=?",
                                    new String[]{Long.toString(projectId)}
                            );
                            Toast toast = Toast.makeText(ProjectActivity.this, "Delete Success", Toast.LENGTH_LONG);
                            toast.show();

                            dialog.dismiss();
                            Intent intent = new Intent(ProjectActivity.this, ProfileActivity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
    public void addAssignedUser(View view){
        memberIdList.clear();
        try {
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
            db = scrumDoDatabaseHelper.getReadableDatabase();
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
            builderDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected){
                            assignedname = items[indexSelected].toString();
                            Toast.makeText(getApplicationContext(),
                                    "The member is "+items[indexSelected], Toast.LENGTH_LONG).show();
                        }
                    })

                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                            SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();
                            db = scrumDoDatabaseHelper.getWritableDatabase();

                            cursor = db2.query("USERS", new String[]{"_id", "FNAME", "LNAME", "UNAME", "PASSWORD", "IMAGE"},
                                    "UNAME =?", new String[]{assignedname},
                                    null, null, null);

                            if (cursor.moveToFirst()) {
                                assignedUser = cursor.getLong(0);
                                Toast toast = Toast.makeText(ProjectActivity.this, Long.toString(assignedUser), Toast.LENGTH_LONG);
                                toast.show();
                                dialog.dismiss();
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
                        taskId = ScrumDoDatabaseHelper.insertTask(db, p_Id, taskName, taskDetail, taskDueDate, a_User);
                        alert.dismiss();
                        resetLayouts();
                        populateTaskView();
                        Toast toast = Toast.makeText(ProjectActivity.this, "Success.", Toast.LENGTH_LONG);
                        toast.show();
                        showTaskDetails();
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
    public void showTaskDetails(){
        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
        SQLiteDatabase db2 = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db2.query("TASKS", new String[]{"_id", "TASK_NAME", "TASK_DETAILS", "DUE_DATE", "ASSIGNED_USER_ID"},
                "_id =?", new String[] {Long.toString(taskId)},
                null, null, null);

        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
        View promptView = layoutInflater.inflate(R.layout.view_task, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
        alertDialogBuilder.setView(promptView);
//        if(cursor.moveToFirst()){
//            alertDialogBuilder.setTitle(cursor.getString(1));
//
//            TextView details = (TextView)findViewById(R.id.detailsPlace);
//            TextView assigned = (TextView)findViewById(R.id.assignedTask);
//            TextView duedate = (TextView)findViewById(R.id.dueDatePlace);
//
//            details.setText(cursor.getString(2));
//            duedate.setText(cursor.getString(3));
//
//            Cursor cursor2 = db2.query("USERS", new String[]{"_id", "UNAME"},
//                    "_id =?", new String[] {cursor.getString(4)},
//                    null, null, null);
//
//            if(cursor2.moveToFirst()){
//                assigned.setText(cursor2.getString(1));
//            }
            alertDialogBuilder.setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            // create an alert dialog
            final AlertDialog alert = alertDialogBuilder.create();
            alert.show();


//        }


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

        cursor = db.query("MEMBERS", new String[]{"_id", "PROJECT_ID", "MEMBER_ID"},
                "PROJECT_ID = ?", new String[]{Long.toString(projectId)},
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

    //populate the TASKS VIEW (using textviews)
    public void populateTaskView(){
        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getReadableDatabase();

        LinearLayout toDoLinearLayout = (LinearLayout)findViewById(R.id.toDoLayoutId);
        LinearLayout doingLinearLayout = (LinearLayout)findViewById(R.id.doingLayoutId);
        LinearLayout doneLinearLayout = (LinearLayout)findViewById(R.id.doneLayoutId);

        toDoLinearLayout.setOnDragListener(new DragListener());
        doingLinearLayout.setOnDragListener(new DragListener());
        doneLinearLayout.setOnDragListener(new DragListener());

        cursor = db.query("TASKS", new String[]{"_id", "PROJECT_ID", "TASK_NAME", "TASK_DETAILS", "DUE_DATE", "ASSIGNED_USER_ID", "STATUS"},
                "PROJECT_ID = ?", new String[]{Long.toString(projectId)},
                null, null, null);

        int numberOfRows = cursor.getCount();

        TextView[] textViewArray = new TextView[numberOfRows];

        ArrayList<Long> taskIdList = new ArrayList<Long>();

        if(cursor.moveToFirst()) {
            int i = 0;
            do {
                final int taskId = cursor.getInt(0);
                TextView newTextView = new TextView(this);
                final CharSequence title = cursor.getString(2);
                newTextView.setText(title);

                newTextView.setTextColor(Color.parseColor("#FFFFFF"));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 5, 5, 0);
                newTextView.setLayoutParams(lp);
                newTextView.setPadding(5, 5, 5, 5);
                newTextView.setGravity(Gravity.CENTER);

                final CharSequence detail = cursor.getString(3);
                final CharSequence dueDate = cursor.getString(4);

                Cursor cursor2 = db.query("USERS", new String[]{"_id", "UNAME"},
                        "_id = ?", new String[]{Integer.toString(cursor.getInt(5))},
                        null, null, null);

                cursor2.moveToFirst();
                final CharSequence uname = cursor2.getString(1);


                newTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       viewTaskDetails(v, title.toString(), detail.toString(), dueDate.toString(), uname.toString(), taskId);
                    }
                });


                if (cursor.getString(6).equals("todo")) {
                    newTextView.setBackgroundColor(Color.parseColor("#A01D22"));
                    toDoLinearLayout.addView(newTextView);
                } else if (cursor.getString(6).equals("doing")) {
                    newTextView.setBackgroundColor(Color.parseColor("#FFD526"));
                    doingLinearLayout.addView(newTextView);
                } else if (cursor.getString(6).equals("done")) {
                    newTextView.setBackgroundColor(Color.parseColor("#106003"));
                    doneLinearLayout.addView(newTextView);
                }

                newTextView.setOnLongClickListener(new LongClickListener());

                textViewArray[i] = newTextView;
                i++;

            } while (cursor.moveToNext());
        }
    }

    public void resetLayouts(){
        LinearLayout todoLayout = (LinearLayout) findViewById(R.id.toDoLayoutId);
        LinearLayout doingLayout = (LinearLayout) findViewById(R.id.doingLayoutId);
        LinearLayout doneLayout = (LinearLayout) findViewById(R.id.doneLayoutId);

        todoLayout.removeAllViews();
        doingLayout.removeAllViews();
        doneLayout.removeAllViews();
    }

    public void viewTaskDetails(View v, String title, String detail, String dueDate, String uname, final Integer taskId){
//        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();

        View promptView = layoutInflater.inflate(R.layout.view_task, null);
        final TextView taskDescription = (TextView) promptView.findViewById(R.id.textDetailId);
        final TextView taskAssignedUser = (TextView) promptView.findViewById(R.id.taskAssignedContentId);
        final TextView taskDueDate = (TextView )promptView.findViewById(R.id.dueDateContentId);
        final EditText taskComment = (EditText) promptView.findViewById(R.id.taskCommentId);
        final Button sendBtn = (Button) promptView.findViewById(R.id.sendBtn);
        final Button commentBtn = (Button) promptView.findViewById(R.id.viewCommentsBtn);
        final Button deleteBtn = (Button) promptView.findViewById(R.id.deleteBtn);

        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle(title);

        taskDescription.setText(detail);
        taskDueDate.setText(dueDate);
        taskAssignedUser.setText(uname);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        final AlertDialog alert = alertDialogBuilder.create();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = taskComment.getText().toString();
                Log.d("COMMENT LENGTH", Integer.toString(comment.length()));
                if (comment.length() > 0) {
                    sendComment(comment, taskId);
                    taskComment.setText("");
                    alert.dismiss();
                }
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllComments(taskId);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ProjectActivity.this);
                    builderDialog.setTitle("Delete Task");
                    builderDialog.setMessage("Are you sure you want to delete this task?");
                    builderDialog
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(ProjectActivity.this);
                                    SQLiteDatabase db2 = scrumDoDatabaseHelper.getWritableDatabase();

                                    //delete comments
                                    Cursor cursor2 = db2.query("COMMENTS", new String[]{"TASK_ID"},
                                            "TASK_ID =?", new String[]{Integer.toString(taskId)},
                                            null, null, null);

                                    if (cursor2.moveToFirst()) {
                                        do {
                                            db2.delete("COMMENTS",
                                                    "TASK_ID = ?",
                                                    new String[]{Integer.toString(cursor2.getInt(0))}
                                            );

                                        } while (cursor2.moveToNext());
                                    }
                                    //delete task
                                    db2.delete("TASKS", "_id = ?", new String[]{Integer.toString(taskId)});
                                    Toast toast = Toast.makeText(ProjectActivity.this, "Success", Toast.LENGTH_LONG);
                                    toast.show();
                                    resetLayouts();
                                    populateTaskView();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builderDialog.create();
                    dialog.show();
                    alert.dismiss();
                } catch (SQLiteException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        // create an alert dialog
        alert.show();
    }

    public void sendComment(String comment, int taskId){
        ScrumDoDatabaseHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getWritableDatabase();
        ScrumDoDatabaseHelper.insertComment(db, taskId, (int) userId, comment);

        Log.d("INSERT COMMENT", "SUCCESS!");
        Toast toast = Toast.makeText(ProjectActivity.this, "Success", Toast.LENGTH_LONG);
        toast.show();
    }

    public void viewAllComments(int taskId){
        commentsList.clear();
        fromUsersIdList.clear();
        fullComments.clear();
        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
        View promptView = layoutInflater.inflate(R.layout.view_comments, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Comments");

        SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
        db = scrumDoDatabaseHelper.getReadableDatabase();

        cursor = db.query("COMMENTS", new String[]{"_id", "TASK_ID", "USER_ID", "COMMENT"},
                "TASK_ID = ?", new String[]{Integer.toString(taskId)},
                null, null, null);


        ListView commentsListView = (ListView)promptView.findViewById(R.id.commentsListView);

        if(cursor.moveToFirst()){
            do {
                fromUsersIdList.add(cursor.getLong(2));
                commentsList.add(cursor.getString(3));
            }while(cursor.moveToNext());
        }

        for(int i=0; i< fromUsersIdList.size(); i++ ){
            db = scrumDoDatabaseHelper.getReadableDatabase();
            Cursor cursor2 = db.query("USERS",
                    new String[] {"_id", "UNAME"},
                    "_id=?",
                    new String[] {fromUsersIdList.get(i).toString()},
                    null, null, null
            );

            cursor2.moveToFirst();
            String fullComment = "From: "+ cursor2.getString(1) +"\n"+commentsList.get(i);
            fullComments.add(fullComment);
        }

        ArrayAdapter<String> commentAdapter = new ArrayAdapter<String> (getBaseContext(),
                android.R.layout.simple_list_item_1, fullComments);

        commentsListView.setAdapter(commentAdapter);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }
}