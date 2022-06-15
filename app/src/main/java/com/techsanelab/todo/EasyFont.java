package com.techsanelab.todo;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by nima on 4/19/2018 AD.
 */

public class EasyFont {
    private View view;
    private final Activity activity;
    private final Typeface typeface;
    private final Typeface typefaceBold;

    public EasyFont(Activity activity) {
        this.activity = activity;
        this.typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/" + font);
        this.typefaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/" + boldFont);
    }

    public EasyFont(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        this.typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/" + font);
        this.typefaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/" + boldFont);
    }


    ////////////////
    /*
     *  Insert your regular font and bold font here ...
     */
    private static final String font = "ralewayregular.ttf";
    private static final String boldFont = "ralewaybold.ttf";
    private static final String buttonFont = "ralewayregular.ttf";  // if you set this string empty button's font will be regular font
    private static final String editTextFont = "ralewaybold.ttf";    // if you set this string empty editText's font will be regular font
    ////////////////


    public static String getButtonFont() {
        return buttonFont;
    }

    public static String getEditTextFont() {
        return editTextFont;
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

    public Typeface getTypefaceBold() {
        return typefaceBold;
    }

    public void tf(int id) {
        TextView txt = activity.findViewById(id);
        if (txt == null)
            return;
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/" + font);
        txt.setTypeface(tf);
    }

    public void tfFragment(int id) {
        TextView txt = view.findViewById(id);
        if (txt == null)
            return;
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/" + font);
        txt.setTypeface(tf);
    }

    public void tfBoldFragment(int id) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/" + boldFont);
        TextView txt = view.findViewById(id);
        txt.setTypeface(tf);
    }

    public void tfBold(int id) {
        TextView txt = activity.findViewById(id);
        if (txt == null)
            return;
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/" + boldFont);
        txt.setTypeface(tf);
    }

    private void tfButton(int id) {
        TextView txt = activity.findViewById(id);
        if (txt == null)
            return;
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/" + buttonFont);
        txt.setTypeface(tf);
    }

    private void tfEditText(int id) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/" + editTextFont);
        TextView txt = activity.findViewById(id);
        txt.setTypeface(tf);
    }

    public void changeAllFonts() {
        changeAllFonts_helper(activity.getWindow().getDecorView());
    }

    public void changeAllFontsFragment(ConstraintLayout v) {
        for (int i = 0; i < v.getChildCount(); i++)
            changeAllFonts_helper(v.getChildAt(i));
    }

    public void changeAllFontsFragment_helper(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    //you can recursively call this method
                    changeAllFonts_helper(child);
                }
            } else if (v instanceof EditText) {
                if (getEditTextFont().isEmpty())
                    tf(v.getId());
                else
                    tfEditText(v.getId());
            } else if (v instanceof Button) {
                if (getButtonFont().isEmpty())
                    tfButton(v.getId());
                else
                    tfButton(v.getId());
            } else if (v instanceof TextView) {
                if (v.getId() == R.id.title)
                    tfBold(v.getId());
                else
                    tf(v.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void changeAllFonts_helper(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    //you can recursively call this method
                    changeAllFonts_helper(child);
                }
            } else if (v instanceof EditText) {
                if (getEditTextFont().isEmpty())
                    tf(v.getId());
                else
                    tfEditText(v.getId());
            } else if (v instanceof Button) {
                if (getButtonFont().isEmpty())
                    tfButton(v.getId());
                else
                    tfButton(v.getId());
            } else if (v instanceof TextView) {
                if (v.getId() == R.id.title)
                    tfBold(v.getId());
                else
                    tf(v.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
