package com.techsanelab.todo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.techsanelab.todo.EasyFont;
import com.techsanelab.todo.CustomCalendar;
import com.techsanelab.todo.R;

public class AboutUsFragment extends Fragment {

    private static final String TAG = "WeFragment";
    RadioGroup radioGroup;
    TextInputEditText description;
    EasyFont easyFont;
    private RequestQueue mRequestQueue;
    MaterialButton send_email;
    Context context;
    View view;
    int selectedRadio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_us,container,false);
        radioGroup = view.findViewById(R.id.radio_group);
        description = view.findViewById(R.id.description);
        easyFont = new EasyFont(getActivity(),view);
        send_email = view.findViewById(R.id.send_email);
        context = getContext();
        mRequestQueue = Volley.newRequestQueue(getContext());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectedRadio = i;
            }
        });


        return view;
    }

    public void sendEmail() throws IOException {
        String url = "";
        final String key = "";

        send_email.setActivated(false);

        StringRequest
                jsonObjReq
                = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        Toast.makeText(context, getString(R.string.success_mail), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                String creds = String.format("%s:%s", "api", key);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                Log.d(TAG, "getHeaders: " + creds);
                headers.put("Authorization", auth);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> p = new HashMap<String, String>();
                p.put("from", "Mailgun Sandbox <postmaster@sandbox4e031abff4e34817840b2f52f01b77b7.mailgun.org>");
                p.put("to", "doneappinfo@gmail.com");
                p.put("subject", "comment");
                String text = String.format("%s%s%s", description.getText().toString(),
                        selectedRadio,
                        new CustomCalendar().toString());
                p.put("text", text);
                return p;
            }
        };

        mRequestQueue.add(jsonObjReq);
    }

}
