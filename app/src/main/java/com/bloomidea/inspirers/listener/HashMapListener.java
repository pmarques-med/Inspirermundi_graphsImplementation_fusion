package com.bloomidea.inspirers.listener;

import com.android.volley.VolleyError;

import java.util.HashMap;

/**
 * Created by michellobato on 29/09/14.
 */
public interface HashMapListener {
    void onResponse(HashMap response);
    void onErrorResponse(VolleyError error);
}
