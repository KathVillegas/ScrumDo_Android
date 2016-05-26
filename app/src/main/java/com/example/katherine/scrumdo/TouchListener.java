package com.example.katherine.scrumdo;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Michaela on 5/26/2016.
 */
public final class TouchListener implements View.OnTouchListener {

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }
}
