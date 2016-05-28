package com.example.katherine.scrumdo;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

    public LinearLayout changeColumn(LinearLayout newLayout){
        return newLayout;
    }
}