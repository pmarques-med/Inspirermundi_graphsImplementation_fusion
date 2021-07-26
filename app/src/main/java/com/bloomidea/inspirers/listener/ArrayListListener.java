package com.bloomidea.inspirers.listener;

import com.android.volley.VolleyError;

import java.util.List;

/**
 * Created by michellobato on 29/09/14.
 */
public interface ArrayListListener {
    void onResponse(List response);
    void onErrorResponse(VolleyError error);
}
