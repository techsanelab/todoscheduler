package com.techsanelab.todo.bottomsheet;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.techsanelab.todo.EasyFont;

public class BottomSheetHandler {

    BottomSheetBehavior bottomSheetBehavior;
    View background;
    int height;
    Activity parent;
    EasyFont easyFont;

    public BottomSheetHandler(BottomSheetBehavior bottomSheetBehavior, Activity parent, View background, int height) {
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.background = background;
        this.height = height;
        this.parent = parent;
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setDraggable(true);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        easyFont = new EasyFont(parent);
    }

    public BottomSheetHandler(int id, Activity parent, View background, int height) {
        this.bottomSheetBehavior = BottomSheetBehavior.from((LinearLayout) parent.findViewById(id));
        this.background = background;
        this.height = height;
        this.parent = parent;
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setDraggable(true);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        easyFont = new EasyFont(parent);
    }

    public void toggle() {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            if (HasSheets.class.isAssignableFrom(parent.getClass())) {
                ((HasSheets) parent).closeAllSheets();
            }
            this.expand();
            return;
        } else {
            this.close();
            return;
        }
    }

    public void expand() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setPeekHeight(height, true);
        background.setVisibility(View.VISIBLE);
    }

    public void close() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0, true);
        background.setVisibility(View.GONE);
    }

    public void boldTitle(int id) {
        easyFont.tfBold(id);
    }

    public int getState() {
        return bottomSheetBehavior.getState();
    }

}
