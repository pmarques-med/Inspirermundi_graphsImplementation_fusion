package com.bloomidea.inspirers.listener;

import com.android.volley.VolleyError;

import org.json.JSONArray;

/**
 * Created by michellobato on 16/12/14.
 */
public interface JSONArrayListener {
    void onResponse(JSONArray response);
    void onErrorResponse(VolleyError error);
}
