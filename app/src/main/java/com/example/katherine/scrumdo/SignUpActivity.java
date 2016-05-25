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
import android.text.Editable;
import android.text.TextWatcher;
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

    public void chooseImage(View view) {

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
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

    public void doneClick(View view) {
        final EditText fname = (EditText) findViewById(R.id.fname);
        final EditText lname = (EditText) findViewById(R.id.lname);
        final EditText uname = (EditText) findViewById(R.id.uname);
        final EditText pwd = (EditText) findViewById(R.id.password);
        EditText cpwd = (EditText) findViewById(R.id.confirmPassword);

        String Fname = fname.getText().toString();
        String Lname = lname.getText().toString();
        String Uname = uname.getText().toString();
        String Pass = pwd.getText().toString();

        if (!pwd.getText().toString().equals(cpwd.getText().toString())) {
            cpwd.setError("Did not match the password!");
        } else if (Fname.length() == 0) {
            fname.setError("Enter your First Name!");
        } else if (Lname.length() == 0) {
            lname.setError("Enter your Last Name!");
        } else if (Uname.length() == 0) {
            uname.setError("Enter your User Name!");
        } else if (Pass.length() == 0) {
            pwd.setError("Enter your Password!");
        } else if (bitmap == null) {
            Toast toast = Toast.makeText(this, "Choose an image", Toast.LENGTH_LONG);
            toast.show();
        } else {
            byte[] image = getBytes(bitmap);

            try {
                SQLiteOpenHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(this);
                db = scrumDoDatabaseHelper.getWritableDatabase();
                long id = ScrumDoDatabaseHelper.insertUsers(db, Fname, Lname, Uname, Pass, image);
                Toast toast = Toast.makeText(this, "Success", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("userId", id);
                startActivity(intent);

            } catch (SQLiteException e) {
            }

        }

        fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fname.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}    });
        lname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lname.setError(null);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}    });
        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwd.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}    });
    }
}
