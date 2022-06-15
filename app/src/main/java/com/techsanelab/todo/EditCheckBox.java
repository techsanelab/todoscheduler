package com.techsanelab.todo;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Custom Edit text view containing a check box for todos editor and menus.
 */
public class EditCheckBox extends ConstraintLayout {

    private static final String TAG = "EditCheckBox";

    private CheckBox checkBox;
    private EditText editText;
    private Activity parent;
    private ConstraintLayout layout = this;

    private int value;
    private float dimension = 0; // TODO: use a default from R.dimen...


    public EditCheckBox(Context context, Activity parent) {
        super(context);
        this.parent = parent;
        init(null, 0);
    }

    public EditCheckBox(Context context, AttributeSet attrs, Activity parent) {
        super(context, attrs);
        this.parent = parent;
        init(attrs, 0);
    }

    public EditCheckBox(Context context, AttributeSet attrs, int defStyle, Activity parent) {
        super(context, attrs, defStyle);
        this.parent = parent;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        EasyFont easyFont = new EasyFont(parent);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EditCheckBox, defStyle, 0);

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        dimension = a.getDimension(
                R.styleable.EditCheckBox_Dimension,
                dimension);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.edit_check_box, layout, true);
        Typeface tf = easyFont.getTypeface();
        editText = findViewById(R.id.et);
        editText.setTypeface(tf);
        checkBox = findViewById(R.id.rb);

        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (code == KeyEvent.KEYCODE_ENTER)
                        ((AddTodoActivity) parent).addEditListener(getValue());
                    else if (code == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {
                        ((AddTodoActivity) parent).removeEditListener(getValue());
                    } else if (code == KeyEvent.KEYCODE_DEL && editText.getText().length() != 0) {
                        int len = editText.getText().length();
                        editText.getText().delete(len - 1, len);
                    }
                    return true;
                }
                Log.d(TAG, "onKey: " + code);
                return false;
            }
        });

    }

    public EditText getEditText() {
        return editText;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void incValue() {
        this.value++;
    }

    public void decValue() {
        this.value--;
    }

    public String getText(){
        return editText.getText().toString();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public boolean isChecked(){
        return checkBox.isChecked();
    }

    public void setCheck(Boolean state){
        checkBox.setChecked(state);
    }


}
