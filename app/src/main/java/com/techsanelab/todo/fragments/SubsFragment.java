package com.techsanelab.todo.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import com.techsanelab.todo.R;
import com.techsanelab.todo.SubsHelper;
import com.techsanelab.todo.Utils;
import com.techsanelab.todo.util.IabHelper;

public class SubsFragment extends Fragment {
    private static final String TAG = "SubsFragment";
    private Activity activity;
    private Context context;

    // The helper object
    IabHelper mHelper;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subs, container, false);
        Objects.requireNonNull(getActivity()).getWindow().setStatusBarColor(Utils.manipulateColor(Objects.requireNonNull(getContext()).getColor(R.color.premiumColorDark), 0.9F));
        activity = getActivity();
        context = getContext();

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    mHelper = SubsHelper.setup(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };



        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.buy1).setOnClickListener(view -> {
            try {

                /*
                * Sample code for triggering IABHelper to do a purchase.
                * */

//                mHelper.iabConnection.flagEndAsync();
//                mHelper.launchPurchaseFlow(activity, SubsHelper.getSkuSub1(), SubsHelper.getRcRequest()
//                        , SubsHelper.mPurchaseFinishedListener, "payload-string");
            } catch (Exception e) {
                Log.e(TAG, "onClick: ", e);
                Toast.makeText(getContext(), getString(R.string.purchase_warning), Toast.LENGTH_LONG).show();
            }
        });

        /*
        * Buttons on click listeners
        * */

//        view.findViewById(R.id.subs1).setOnClickListener(view -> {
//
//        });
//
//        view.findViewById(R.id.subs2).setOnClickListener(view -> {
//
//        });
//
//        view.findViewById(R.id.buy2).setOnClickListener(view -> {
//
//        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onCreateView: " + SubsHelper.lock);
        view.findViewById(R.id.frame).setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
            SubsHelper.getmHelper().dispose();
            SubsHelper.clear();
        }
        mHelper = null;
    }


}
