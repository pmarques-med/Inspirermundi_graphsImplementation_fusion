package com.bloomidea.inspirers.listener;

import com.android.volley.VolleyError;

import java.util.TreeMap;

/**
 * Created by michellobato on 29/09/14.
 */
public interface TreeMapListener {
    void onResponse(TreeMap response);
    void onErrorResponse(VolleyError error);
}
