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
import android.text.Editable;
import android.text.TextWatcher;
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

        Intent intent = getIntent();
    }

    public void signUpClick(View view){

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signInClick(View view){
        final EditText uname = (EditText)findViewById(R.id.uname);
        final EditText password = (EditText)findViewById(R.id.password);

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
                    password.setError("Wrong password!");
                }
            }else{
                uname.setError("User not found!");
            }
        }catch(SQLiteException e) {}

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}    });
        uname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uname.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


}
