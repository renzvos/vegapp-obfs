package com.aamys.fresh;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RenzvosWork {
    AppCompatActivity appCompatActivity;
    public RenzvosWork(AppCompatActivity activity)
    {appCompatActivity = activity;
    }

    public  void WorkPermit() {


        RequestQueue queue = Volley.newRequestQueue(appCompatActivity.getApplicationContext());
        String url = "https://renzvos.com/workperm/vegapp.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1"))
                        {
                            appCompatActivity.finishAffinity();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
