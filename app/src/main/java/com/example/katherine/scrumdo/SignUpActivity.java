package com.example.katherine.scrumdo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SignUpActivity extends Activity {
    public static final int GET_FROM_GALLERY = 3;
    public static Bitmap bitmap = null;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();

    }
    public void chooseImage(View view){

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    public void doneClick(View view){
        EditText fname = (EditText)findViewById(R.id.fname);
        EditText lname = (EditText)findViewById(R.id.lname);
        EditText uname = (EditText)findViewById(R.id.uname);
        EditText pwd = (EditText)findViewById(R.id.password);
        EditText cpwd = (EditText)findViewById(R.id.confirmPassword);

        String Fname = fname.getText().toString();
        String Lname = lname.getText().toString();
        String Uname = uname.getText().toString();
        String Pass = pwd.getText().toString();

        if(!pwd.getText().toString().equals(cpwd.getText().toString())){
            Toast toast = Toast.makeText(this, "Password did not match", Toast.LENGTH_LONG);
            toast.show();
        }else if(Fname.length() == 0){
            Toast toast = Toast.makeText(this, "Enter your First Name", Toast.LENGTH_LONG);
            toast.show();
        }else if(Lname.length() == 0){
            Toast toast = Toast.makeText(this, "Enter your Last Name", Toast.LENGTH_LONG);
            toast.show();
        }
        else if(Uname.length() == 0){
            Toast toast = Toast.makeText(this, "Enter your Username", Toast.LENGTH_LONG);
            toast.show();
        }
        else if(Pass.length() == 0){
            Toast toast = Toast.makeText(this, "Enter your Password", Toast.LENGTH_LONG);
            toast.show();
        }
        else if(bitmap == null){
            Toast toast = Toast.makeText(this, "Choose an image", Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            byte[] image = getBytes(bitmap);

            try{
                SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
                db = scrumDoDatabaseHelper.getWritableDatabase();
                long id = ScrumDoDatabaseHelper.insertUsers(db, Fname, Lname, Uname, Pass, image);
                Toast toast = Toast.makeText(this, "Success", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("userId", id);
                startActivity(intent);

            }catch (SQLiteException e){}

        }

    }
}
