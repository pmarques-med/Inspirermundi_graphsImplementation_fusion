package com.bloomidea.inspirers.listener;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by michellobato on 16/12/15.
 */
public interface JSONObjectListener {
    void onResponse(JSONObject response);
    void onErrorResponse(VolleyError error);
}
