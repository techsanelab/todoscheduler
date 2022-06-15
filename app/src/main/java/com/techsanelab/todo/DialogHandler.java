package com.techsanelab.todo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogHandler {


    public enum DialogImageSize {
        BIG(500),
        MEDIUM(300),
        SMALL(200);

        private int value;

        DialogImageSize(int value) {
            this.value = value;
        }
    }

    public static void customDialog(Context context, Activity activity, String title, String message, int resId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.layout_dialog, null);
        EasyFont easyFont = new EasyFont(activity, dialoglayout);
        ((ImageView) dialoglayout.findViewById(R.id.image)).setImageResource(resId);
        TextView t = ((TextView) dialoglayout.findViewById(R.id.title));
        TextView m = ((TextView) dialoglayout.findViewById(R.id.message));
        t.setTypeface(easyFont.getTypefaceBold());
        t.setText(title);
        m.setTypeface(easyFont.getTypeface());
        m.setText(message);
        builder.setView(dialoglayout);
        builder.show();
    }

    public static void customDialog(Context context, Activity activity, String title, String message, Drawable drawable) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.layout_dialog, null);
        EasyFont easyFont = new EasyFont(activity, dialoglayout);
        ((ImageView) dialoglayout.findViewById(R.id.image)).setImageDrawable(drawable);
        if (drawable == null)
            ((ImageView) dialoglayout.findViewById(R.id.image)).setVisibility(View.GONE);
        TextView t = ((TextView) dialoglayout.findViewById(R.id.title));
        TextView m = ((TextView) dialoglayout.findViewById(R.id.message));
        t.setTypeface(easyFont.getTypefaceBold());
        t.setText(title);
        m.setTypeface(easyFont.getTypeface());
        m.setText(message);
        builder.setView(dialoglayout);
        builder.show();
    }

    public static void customDialog(Context context, Activity activity, String title, String message, Drawable drawable, DialogImageSize dialogImageSize) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> dialog.dismiss());

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.layout_dialog, null);
        EasyFont easyFont = new EasyFont(activity, dialoglayout);
        ImageView image = ((ImageView) dialoglayout.findViewById(R.id.image));
        image.setImageDrawable(drawable);
        image.getLayoutParams().height = dialogImageSize.value;
        image.getLayoutParams().width = dialogImageSize.value;
        if (drawable == null)
            image.setVisibility(View.GONE);
        TextView t = ((TextView) dialoglayout.findViewById(R.id.title));
        TextView m = ((TextView) dialoglayout.findViewById(R.id.message));
        t.setTypeface(easyFont.getTypefaceBold());
        t.setText(title);
        m.setTypeface(easyFont.getTypeface());
        m.setText(message);
        image.requestLayout();
        builder.setView(dialoglayout);
        builder.show();
    }
}
