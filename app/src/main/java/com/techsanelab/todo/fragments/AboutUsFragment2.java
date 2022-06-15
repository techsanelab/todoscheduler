package com.techsanelab.todo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.techsanelab.todo.EasyFont;
import com.techsanelab.todo.R;

public class AboutUsFragment2 extends Fragment {
    EasyFont easyFont;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_us_2,container,false);
        easyFont = new EasyFont(getActivity(),view);
        easyFont.tfFragment(R.id.version);
        easyFont.tfFragment(R.id.description);
        return view;
    }
}
