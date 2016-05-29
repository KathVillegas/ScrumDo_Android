package com.example.katherine.scrumdo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Michaela on 5/26/2016.
 */
public class DragListener implements View.OnDragListener {

    @SuppressWarnings("ResourceType")
    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
//                v.setBackgroundDrawable(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
//                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                TextView view = (TextView) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                LinearLayout container = (LinearLayout) v;
                container.addView(view);
                Log.d("CONTAINER", "Container ID = " + container.getId());
                Log.d("OWNER", "Owner ID = " + owner.getId());

                ScrumDoDatabaseHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(view.getContext());
                SQLiteDatabase db = scrumDoDatabaseHelper.getWritableDatabase();

                CharSequence taskText = view.getText();
                Cursor cursor = db.query("TASKS", new String[] {"TASK_NAME", "STATUS"},
                        "TASK_NAME=?", new String[] {taskText.toString()}, null, null, null);

                if(cursor.moveToFirst()) {
                    if (container.getId() == 2131492986) {
                        view.setBackgroundColor(Color.parseColor("#FFD526"));
                        ScrumDoDatabaseHelper.updateTaskStatus(db, cursor.getString(0), "doing");
                    } else if (container.getId() == 2131492987) {
                        view.setBackgroundColor(Color.parseColor("#106003"));
                        ScrumDoDatabaseHelper.updateTaskStatus(db, cursor.getString(0), "done");
                        addLink(cursor.getString(0), event);
                    } else if (container.getId() == 2131492985) {
                        view.setBackgroundColor(Color.parseColor("#A01D22"));
                        ScrumDoDatabaseHelper.updateTaskStatus(db, cursor.getString(0), "todo");
                    }
                }
                view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
//                v.setBackgroundDrawable(normalShape);
            default:
                break;
        }
        return true;
    }

    public void addLink(final String taskName,  DragEvent event){
        final TextView view = (TextView) event.getLocalState();
        ScrumDoDatabaseHelper scrumDoDatabaseHelper = new ScrumDoDatabaseHelper(view.getContext());
        final SQLiteDatabase db = scrumDoDatabaseHelper.getWritableDatabase();

        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View promptView = layoutInflater.inflate(R.layout.add_link, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Add Link");

        final EditText link = (EditText) promptView.findViewById(R.id.linkText);
        TextView taskLabelName = (TextView) promptView.findViewById(R.id.taskName);
        taskLabelName.setText(taskName);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = link.getText().toString();

                if (linkText.length() == 0) {
                    link.setError("Enter link!");
                } else {
                    try {
                        ScrumDoDatabaseHelper.addLink(db, taskName, linkText);
                        Toast toast = Toast.makeText(view.getContext(), "Success.", Toast.LENGTH_LONG);
                        toast.show();
                        alert.dismiss();
                    } catch (SQLiteException e) {
                    }

                }
            }
        });

        link.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                link.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public LinearLayout changeColumn(LinearLayout newLayout){
        return newLayout;
    }
}