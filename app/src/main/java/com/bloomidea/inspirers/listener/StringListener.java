package com.bloomidea.inspirers.listener;

import com.android.volley.VolleyError;

/**
 * Created by michellobato on 29/09/14.
 */
public interface StringListener {
    void onResponse(String response);
    void onErrorResponse(VolleyError error);
}
