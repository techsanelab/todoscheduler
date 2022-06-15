package com.techsanelab.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.techsanelab.todo.util.IabHelper;
import com.techsanelab.todo.util.IabResult;
import com.techsanelab.todo.util.Inventory;
import com.techsanelab.todo.util.Purchase;

public class SubsHelper {

    private static final String TAG = "SubsHelper";

    // SKUs for our products: the premium upgrade (non-consumable)
    public static final String SKU_SUB1 = "sub1";
    public static final String SKU_SUB2 = "sub2";
//    public IabHelper.QueryInventoryFinishedListener mGotInventoryListener;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10101;
    private static IabHelper mHelper;
    private static boolean isPremium;
    public static boolean prm;
    private static SubsHelper mInstance;
    public static Boolean lock;

    public static SubsHelper getInstance() {
        if (mInstance == null)
            mInstance = new SubsHelper();
        return mInstance;
    }

    public static IabHelper setup(Context context) {
        lock = true;
        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCtAULWDGxJxfrOaWQVmaaLCdgasJphQsP2noDzKpFzXUR5RwTso9gMW0Cap8w7KEOh8dlb0XK+I9NoBpavVPaR5vUfSffJdGVl1lrY0XoNvYEy4qqg6BlflpZX5FXEMUGn56+tYURp8+3VVThtNOwtk19f+4dNNJD7b9JGLlA5g769ZycxcYIpJZX/cmL47jAyi6XCuCRUL4DpSqfehFKrg98iljFcobeaT3I+EAECAwEAAQ==";
        if (context != null) {
            mHelper = new IabHelper(context, base64EncodedPublicKey);
            mHelper.startSetup(result -> {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                mHelper.queryInventoryAsync(SubsHelper.mGotInventoryListener);
            });
            Log.d(TAG, "setup: " + prm);

            return mHelper;
        }
        return null;
    }

    public static IabHelper getmHelper() {
        return mHelper;
    }

    public static void clear(){
        mHelper = null;
    }

    public static boolean isLock() {
        return lock;
    }

    // is user premium or not?
    public static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            lock = false;
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            } else {
                Log.d(TAG, "Query inventory was successful.");

                // does the user have the premium upgrade?
                isPremium = inventory.hasPurchase(SKU_SUB1) || inventory.hasPurchase(SKU_SUB2);

                if (result.isFailure()) {
                    // handle error
                    Log.d(TAG, "onQueryInventoryFinished: Failure");
                    lock = false;
                    return;
                }

                // update UI accordingly
                prm = isPremium;
                lock = false;
                Log.d(TAG, "User is " + (isPremium ? "PREMIUM" : "NOT PREMIUM"));
            }
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");

        }

    };


    // purchase finished
    public static IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            } else if (purchase.getSku().equals(SKU_SUB1)) {
                return;
            } else if (purchase.getSku().equals(SKU_SUB2)) {
                // give user access to premium content and update the UI
                return;
            }
        }
    };


    public static String getSkuSub1() {
        return SKU_SUB1;
    }

    public static String getSkuSub2() {
        return SKU_SUB2;
    }

    public static int getRcRequest() {
        return RC_REQUEST;
    }

    public boolean isPremium(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        return prefs.getBoolean(Utils.PREMIUM_KEY, false);
    }

    public static void setIsPremium(Context context, boolean isPremium) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Utils.PREMIUM_KEY, isPremium);
        editor.commit();
    }


}
