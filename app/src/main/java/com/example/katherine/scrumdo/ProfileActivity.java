package com.example.katherine.scrumdo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity {
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

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
                TextView fname = (TextView)findViewById(R.id.fname);
                TextView lname = (TextView)findViewById(R.id.lname);
                TextView uname = (TextView)findViewById(R.id.uname);
                TextView password = (TextView)findViewById(R.id.password);
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

    public void logOutClock(View view){
        //do this on logout button click
        final String LOG_OUT = "event_logout";
        Intent intent = new Intent(LOG_OUT);
        //send the broadcast to all activities who are listening
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
