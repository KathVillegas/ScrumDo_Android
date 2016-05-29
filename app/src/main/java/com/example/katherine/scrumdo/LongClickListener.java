package com.example.katherine.scrumdo;

import android.content.ClipData;
import android.view.View;


/**
 * Created by Michaela on 5/28/2016.
 */
public class LongClickListener implements View.OnLongClickListener{

    @Override
    public boolean onLongClick(View view) {
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
        return true;
    }
}
