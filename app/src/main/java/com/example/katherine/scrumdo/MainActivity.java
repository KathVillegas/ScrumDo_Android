package com.example.katherine.scrumdo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Register mMessageReceiver to receive messages.
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter(LOG_OUT));
    }

    public void signUpClick(View view){

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signInClick(View view){
        EditText uname = (EditText)findViewById(R.id.uname);
        EditText password = (EditText)findViewById(R.id.password);

        try{
            SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
            SQLiteDatabase db = scrumDoDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("USERS",
                    new String[] {"_id", "UNAME", "PASSWORD"},
                    "UNAME=?",
                    new String[] {uname.getText().toString()},
                    null, null, null
            );

            if(cursor.moveToFirst()){
                if(password.getText().toString().equals(cursor.getString(2))){
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("userId", cursor.getLong(0));
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(this, "Wrong Password!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }else{
                Toast toast = Toast.makeText(this, "User Not Found", Toast.LENGTH_LONG);
                toast.show();
            }
        }catch(SQLiteException e) {}


    }


}
