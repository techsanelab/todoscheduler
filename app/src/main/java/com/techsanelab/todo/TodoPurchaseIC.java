package com.techsanelab.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.techsanelab.todo.aipurchase.OnScopeHappened;
import com.techsanelab.todo.aipurchase.SaveFeatureInterface;

public class TodoPurchaseIC {

    private Context context;

    public TodoPurchaseIC(Context context) {
        this.context = context;
    }

    public TodoPurchase createInstance() {
        TodoPurchase todoPurchase = new TodoPurchase(5, 10, 4);
        todoPurchase.setLogger((title, message) -> Log.d("Todo Purchase", String.format("%s: %s", title, message)));
        todoPurchase.setOnScopeHappened(new OnScopeHappened() {
            @Override
            public void highChance() {
                SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Utils.PURCHASE_KEY, true);
                editor.commit();
            }

            @Override
            public void promotion() {
                return;
            }

            @Override
            public void show() {
                SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Utils.PURCHASE_KEY, true);
                editor.commit();
            }

            @Override
            public void down() {
                return;
            }

            @Override
            public void sinusoid() {
                return;
            }
        });

        todoPurchase.setSaveFeature(new SaveFeatureInterface() {
            @Override
            public boolean save() {
                SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Utils.TODO_PURCHASE_KEY + "acc", todoPurchase.getAcc());
                editor.putInt(Utils.TODO_PURCHASE_KEY + "periodacc", todoPurchase.getPeriodAcc());
                editor.putInt(Utils.TODO_PURCHASE_KEY + "index", todoPurchase.getIndex());
                editor.putStringSet(Utils.TODO_PURCHASE_KEY + "list", todoPurchase.getPeriodItems());
                return editor.commit();
            }

            @Override
            public void resetOnPeriod() {
                todoPurchase.reset();
            }
        });

        return todoPurchase;
    }
}
