package com.bloomidea.inspirers.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.MedicineInhaler;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.model.UserSOSMedicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michellobato on 16/03/17.
 */

public class APIInspirers {
    private static String TAG = APIInspirers.class.toString();
    public static String serverPath = "http://inspirermundi.com";
    //public static String serverPath = "http://bloom-professor-frink.bitnamiapp.com/staging/inspirer";
    //public static String serverPath = "http://54.198.130.201/projects/inspirer";
    private static String restServer= AppController.getmInstance().getRestLang()+"/rest/";

    private static int time_out = 36000;
    private static int maxRetries = 1;

    public static final int resultReviewLoad = 40;
    public static final int resultMessagesLoad = 44;

    private static final SimpleDateFormat badgesDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final SimpleDateFormat datetimeDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat dateOnlyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat datetimeUserDateFormatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    private static HashMap<String, String> myGetHeaders(String userCookie){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String cookie = userCookie!=null && !userCookie.isEmpty()?userCookie:AppController.getmInstance().getSessionCookie();

        if(cookie!=null) {
            headers.put("Cookie", cookie);
        }


        if(AppController.getmInstance().getActiveUser()!=null) {
            String userToken = AppController.getmInstance().getActiveUser().getToken();

            if (userToken != null && !userToken.isEmpty()) {
                headers.put("X-CSRF-Token", userToken);
            }
        }

        return headers;
    }

    public static void getStringObject(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, String tag_json_obj){
        StringRequest jsonObjReq = new StringRequest(url, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }
        };

        Log.d("URL", url);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(time_out, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private static void getJSONObject(String url, final JSONObjectListener listener, String tag_json_obj, boolean cancel){
        if(cancel){
            AppController.getmInstance().getRequestQueue().cancelAll(tag_json_obj);
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                serverPath+restServer+url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                listener.onErrorResponse(error);
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }
        };

        Log.d("URL", serverPath + restServer + url);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(time_out, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private static void getJSONArray(String url, final JSONArrayListener listener, String tag_json_obj, boolean cancel, final String headerToken){

        if(cancel){
            AppController.getmInstance().getRequestQueue().cancelAll(tag_json_obj);
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(serverPath+restServer+url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                listener.onResponse(jsonArray);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                listener.onErrorResponse(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(headerToken);
            }
        };

        Log.d("URL", serverPath+restServer+url);

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(time_out,maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonArrayRequest, tag_json_obj);
    }


    private static void postJSONObject(String url, Map<String,Object> params, final JSONObjectListener listener, String tag_json_obj){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                serverPath+restServer+url, params==null ? new JSONObject():new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                listener.onErrorResponse(error);

                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }
        };

        Log.d("URL", serverPath+restServer+url);

        if(params!=null)
            Log.d("postJSONObject params=", (new JSONObject(params)).toString());

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(time_out, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void postJSONArray(String url, final Map<String,Object> params, final JSONArrayListener listener, String tag_json_obj){

        JsonArrayRequestWithParams jsonArrayRequest = new JsonArrayRequestWithParams(Request.Method.POST,
                serverPath+restServer+url,(params==null ? new JSONObject():new JSONObject(params)),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                listener.onResponse(jsonArray);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                listener.onErrorResponse(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }
        };

        if(params!=null)
            Log.d("postJSONArray params=", (new JSONObject(params)).toString());

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(time_out,maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonArrayRequest, tag_json_obj);
    }



    private static void putJSONObject(String url,Map<String,Object> params, final JSONObjectListener listener, String tag_json_obj){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                serverPath+restServer+url, params==null ? new JSONObject():new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                listener.onErrorResponse(error);

                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }
        };

        Log.d("URL", serverPath+restServer+url);

        if(params!=null)
            Log.d("putJSONObject params=", (new JSONObject(params)).toString());

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(time_out, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private static void deleteJSONObject(String url, Map<String,Object> params, final JSONObjectListener listener, String tag_json_obj){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                serverPath+restServer+url, (params!=null?new JSONObject(params):null),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                listener.onErrorResponse(error);

                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }
        };

        Log.d("URL", serverPath+restServer+url);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(time_out, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private static void deleteStringRequest(String url, final Map<String,String> params, final JSONObjectListener listener, String tag_json_obj){
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, serverPath+restServer+url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(new JSONObject());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myGetHeaders(null);
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            ;
        };

        Log.d("URL", serverPath+restServer+url);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(time_out, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getmInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    public static void getUserToken(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        getStringObject(serverPath+"/services/session/token",listener, errorListener,"getUserToken");
    }

    public static void fboauth_login(String accessToken, final JSONObjectListener listener) {
        String url = "fboauth/connect.json";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", accessToken);

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("FBL: ", response.toString());
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERRO_LOGIN:", error.toString());
                listener.onErrorResponse(error);
            }
        }, "fboauth_login");
    }

    public static void loadCountryList(JSONArrayListener listener){
        String url = "views/countries.json?display_id=countrieslist";

        getJSONArray(url, listener, "loadCountryList", true, null);
    }

    public static void getUserAchievment(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=achievements&args[0]="+uid;

        getJSONArray(url, listener, "getUserAchievment", true, null);
    }

    public static void getMapLeaderboardInfo(String uid, JSONArrayListener listener){
        String url = "custom/country_godchilds.json";

        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("userauthor",uid);

        postJSONArray(url, params, listener, "getMapLeaderboardInfo");
    }

    public static void getUserRank(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=userrank&args[0]="+uid;

        getJSONArray(url, listener, "getUserRank", true, null);
    }

    public static void getLeaderboardList(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=leaderboardlist&args[0]="+uid;

        getJSONArray(url, listener, "getLeaderboardList", true, null);
    }

    public static void getReviews(String nid, int page, JSONArrayListener listener){
        String url = "views/reviews.json?display_id=reviews&offset="+(page*resultReviewLoad)+"&limit="+resultReviewLoad+ "&args[0]="+nid;

        getJSONArray(url, listener, "getReviews", true, null);
    }

    public static void warriorsRequestedToMe(String uid, JSONArrayListener listener){
        String url = "views/requests.json?display_id=requeststome&filters[field_user_target]="+uid+"&filters[field_request_type]=godchild";

        getJSONArray(url, listener, "warriorsRequestedToMe", true, null);
    }

    public static void warriorsRequestedByMe(String uid, JSONArrayListener listener){
        String url = "views/requests.json?display_id=myrequests&filters[uid_raw]="+uid+"&filters[field_request_type]=godfather";

        getJSONArray(url, listener, "warriorsRequestedByMe", true, null);
    }

    public static void warriorsRequestsSuggested(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=cangodchild";

        getJSONArray(url, listener, "warriorsRequestsSuggested", true, null);
    }

    public static void getUserInfo(String uid,String sessionName, String sessionId, JSONArrayListener listener){
        String url = "views/users.json?display_id=userinfo&args[0]="+uid;
        String cookie = null;

        if(sessionName != null && !sessionName.isEmpty()){
            cookie = sessionName + "=" + sessionId;
        }

        getJSONArray(url, listener, "getUserInfo", true, cookie);
    }

    public static void askWarrior(String targetId, final JSONObjectListener listener){
        String url = "node.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", "requests");
        try {
            params.put("field_status", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", com.bloomidea.inspirers.model.Request.STATUS_WAITING))));
            params.put("field_request_type", new JSONObject().put("und", new JSONObject().put("value", com.bloomidea.inspirers.model.Request.TYPE_GODFATHER)));
            params.put("field_user", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("target_id", "username ("+targetId+")"))));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("askWarrior", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("askWarrior", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "askWarrior");
    }

    public static void acceptWarrior(String nid, JSONObjectListener listener){
        updateRequestStatus(nid, com.bloomidea.inspirers.model.Request.STATUS_ACCEPT,listener);
    }

    public static void declineWarrior(String nid, JSONObjectListener listener){
        updateRequestStatus(nid, com.bloomidea.inspirers.model.Request.STATUS_REJECT,listener);
    }

    public static void deleteWarrior(String nid, JSONObjectListener listener){
        updateRequestStatus(nid, com.bloomidea.inspirers.model.Request.STATUS_DELETE,listener);
    }

    private static void updateRequestStatus(String nid, String status, final JSONObjectListener listener) {
        String url = "node/"+nid;

        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            params.put("field_status", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", status))));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateRequestStatus", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("updateRequestStatus", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "updateRequestStatus");
    }

    public static void createReview(String reviewsNid, Review review, final JSONObjectListener listener){
        String url = "comment.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", "requests");
        try {
            params.put("nid", reviewsNid);
            params.put("comment_body", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", review.getReviewText()))));
            params.put("field_evaluation", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", review.getReview().doubleValue()))));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createReview", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createReview", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createReview");
    }

    public static void getGodfather(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=godfather&args[0]="+uid;

        getJSONArray(url, listener, "getGodfather", true, null);
    }

    public static void getGodchilds(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=godchild&args[0]="+uid;

        getJSONArray(url, listener, "getGodchilds", true, null);
    }

    public static void getRequestedGodfathers(String uid, JSONArrayListener listener){
        String url = "views/requests.json?display_id=myrequests&filters[uid_raw]="+uid+"&filters[field_request_type]=godchild";

        getJSONArray(url, listener, "getRequestedGodfathers", true, null);
    }

    public static void getRequestedToMeGodfathers(String uid, JSONArrayListener listener){
        String url = "views/requests.json?display_id=requeststome&filters[field_user_target]="+uid+"&filters[field_request_type]=godfather";

        getJSONArray(url, listener, "getRequestedToMeGodfathers", true, null);
    }

    public static void getSuggestedGodfathers(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=cangodfatherlist&uid_raw="+uid;//+"&filters[godfather]="+uid;

        getJSONArray(url, listener, "getRequestedToMeGodfathers", true, null);
    }

    public static void askGodfather(String targetId, final JSONObjectListener listener){
        String url = "node.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", "requests");
        try {
            params.put("field_status", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", com.bloomidea.inspirers.model.Request.STATUS_WAITING))));
            params.put("field_request_type", new JSONObject().put("und", new JSONObject().put("value", com.bloomidea.inspirers.model.Request.TYPE_GODCHILD)));
            params.put("field_user", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("target_id", "username ("+targetId+")"))));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("askWarrior", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("askWarrior", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "askWarrior");
    }

    public static void acceptGodfatherRequest(String nid, JSONObjectListener listener){
        updateRequestStatus(nid, com.bloomidea.inspirers.model.Request.STATUS_ACCEPT,listener);
    }

    public static void declineGodfatherRequest(String nid, JSONObjectListener listener){
        updateRequestStatus(nid, com.bloomidea.inspirers.model.Request.STATUS_REJECT,listener);
    }

    public static void deleteGodfatherRequest(String nid, JSONObjectListener listener){
        updateRequestStatus(nid, com.bloomidea.inspirers.model.Request.STATUS_DELETE,listener);
    }

    public static void deleteGodfather(String uid, final JSONObjectListener listener){
        String url = "user/"+uid;

        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            params.put("field_godfather", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("target_id", ""))));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateRequestStatus", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("updateRequestStatus", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "deleteGodfather");
    }

    public static void updateGodchildBuzz(String buzz,String targetId, String revisionId,GregorianCalendar buzzdate, final JSONObjectListener listener) {
        String url = "entity_field_collection_item/"+targetId;

        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            params.put("field_name", "field_godchild");
            params.put("item_id", targetId);
            params.put("revision_id", revisionId);
            params.put("default_revision", "1");
            params.put("field_state_buzz", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", buzz))));
            params.put("field_last_buzz_date", createDateFieldJsonObject(datetimeDateFormatter,buzzdate));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateGodchildBuzz", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("updateGodchildBuzz", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "updateGodchildBuzz");
    }

    public static void deleteGodchild(String targetId, String revisionId, final JSONObjectListener listener) {
        String url = "entity_field_collection_item/"+targetId;

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("field_name", "field_godchild");
        params.put("item_id", targetId);
        params.put("revision_id", revisionId);
        params.put("default_revision", "1");

        deleteStringRequest(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("deleteGodchild", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("deleteGodchild", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "deleteGodchild");
    }

    public static void getConversation(int page, String uid1, String uid2, JSONArrayListener listener){
        String url = "views/messages.json?display_id=messages&args[0]="+uid1+"&args[1]="+uid2+"&offset="+(page*resultMessagesLoad)+"&limit="+resultMessagesLoad;

        getJSONArray(url, listener, "getConversation", true, null);
    }

    public static void sendMessage(String uidSender, String uidReciver, String msgText, final JSONObjectListener listener){
        String url = "custom/create-messages.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("usersent", uidSender);
        params.put("userreceive", uidReciver);
        params.put("message", msgText);

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("sendMessage", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sendMessage", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "sendMessage");

    }

    public static void searchGodfathers(String textSearch, String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=godfathersearch&uid_raw="+uid;

        if(textSearch!=null && !textSearch.isEmpty()){
            try {
                url+="&field_name_user_value="+java.net.URLEncoder.encode(textSearch, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        getJSONArray(url, listener, "searchGodfathers", true, null);
    }

    public static void registerDeviceNotifications(String language, String token, final JSONObjectListener listener) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("token", token);
        params.put("type", "android");

        if(language!=null && !language.isEmpty())
            params.put("language",language);

        String url = "push_notifications";

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, "registerDeviceNotifications");
    }

    public static void unregisterUserNotifications(String token, final JSONObjectListener listener){
        String url = "push_notifications/"+token;

        deleteJSONObject(url, null, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPOSTE Delete", response.toString());
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, "unregisterUserNotifications");
    }

    public static void sendAvatarImgToServer(String nameImg, Bitmap bitmap, JSONObjectListener listener){
        sendImgToServer(nameImg, bitmap, "public://", listener);
    }

    private static void sendImgToServer(String nameImg, Bitmap bitmap, String publicImgPathServer, final JSONObjectListener listener){
        if(bitmap!=null) {

            String encodedImage = BitMapToString(bitmap);//Base64.encodeToString(byteArray, Base64.DEFAULT);

            HashMap<String, Object> params = new HashMap<String, Object>();

            params.put("file", encodedImage);

            params.put("filename", nameImg);
            params.put("filepath", publicImgPathServer+"/" + nameImg);

            postJSONObject("file.json", params, new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {
                    listener.onResponse(response);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onErrorResponse(error);
                }
            }, "sendImgToServer");


        }else{
            listener.onResponse(null);
        }
    }

    private static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp= Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){

            baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            try{
                System.gc();
                temp=Base64.encodeToString(b, Base64.DEFAULT);
            }catch(OutOfMemoryError e2){
                baos=new  ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,25, baos);
                b=baos.toByteArray();
                try{
                    System.gc();
                    temp=Base64.encodeToString(b, Base64.DEFAULT);
                }catch(OutOfMemoryError e3){
                    baos=new  ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,10, baos);
                    b=baos.toByteArray();
                    System.gc();
                    temp=Base64.encodeToString(b, Base64.DEFAULT);

                    Log.d("OFM3", "Out of memory error catched3");
                }

                Log.d("OFM2", "Out of memory error catched2");
            }
            Log.d("OFM", "Out of memory error catched");
        }

        System.gc();
        return temp;
    }

    public static void editUserInfo(User userInfo, String fidAvatar, JSONObjectListener listener){
        String url = "user/"+userInfo.getUid()+".json";

        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            params.put("field_name_user", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userInfo.getUserName()))));
            params.put("field_points", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userInfo.getUserPoints()))));
            params.put("field_level", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userInfo.getLevel()))));

            if(userInfo.getIsoCountry()!=null && !userInfo.getIsoCountry().isEmpty()) {
                params.put("field_country", new JSONObject().put("und", new JSONObject().put("iso2", userInfo.getIsoCountry())));
            }

            params.put("field_accessionweek", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""+userInfo.getStatsWeek().doubleValue()))));
            params.put("field_accessionmonth", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""+userInfo.getStatsMonth().doubleValue()))));
            params.put("field_accessionever", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""+userInfo.getStatsEver().doubleValue()))));
            params.put("field_currentbonus", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""+userInfo.getActualBonus().doubleValue()))));

            if(fidAvatar!=null && !fidAvatar.isEmpty()){
                params.put("field_picture", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("fid",fidAvatar))));
            }

            if(userInfo.getLanguages() != null && userInfo.getLanguages().length>0){
                params.put("field_languages", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userInfo.getLanguagesStringWithSeparator(InspirersJSONParser.LANGUAGE_SEPARATOR)))));
            }else{
                params.put("field_languages", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""))));
            }

            if(userInfo.getHobbies() != null && userInfo.getHobbies().length>0){
                params.put("field_interests", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userInfo.getHobbiesStringWithSeparator(InspirersJSONParser.HOBBIES_SEPARATOR)))));
            }else{
                params.put("field_interests", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""))));
            }

            if(userInfo.getUserProvidedId()!=null){
                params.put("field_custom_user_id", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userInfo.getUserProvidedId()))));
            }else{
                params.put("field_custom_user_id", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",""))));

            }

            if(userInfo.isAcceptedTerms()) {
                params.put("field_accept_terms", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", "1"))));
            }

            if(userInfo.isTermsOff()) {
                params.put("field_terms_off", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", "1"))));
            }

            //params.put("field_accept_terms", new JSONObject().put("und",userInfo.isAcceptedTerms()?new JSONObject().put("value","1"):JSONObject.NULL));
            if(userInfo.getUserProvidedIdDate()!=null) {
                params.put("field_data_study_id", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", new JSONObject().put("date",datetimeUserDateFormatter.format(userInfo.getUserProvidedIdDate().getTime()))))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, listener, "editarUtilizador");
    }

    public static void createBadge(String uid, UserBadge badge, final JSONObjectListener listener){
        String url = "entity_field_collection_item.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("field_name", "field_achievements");
        params.put("hostEntityType", "user");
        params.put("hostEntityId", uid);
        try {
            params.put("field_date", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", badgesDateFormatter.format(badge.getDate().getTime())))));
            params.put("field_week_number", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", badge.getWeekNumber()))));
            params.put("field_achievement", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("tid", badge.getBadge().getCode()))));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createBadge", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createBadge", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createBadge");
    }

    public static void getPolls(JSONObjectListener listener){
        String url = "forms.json";

        getJSONObject(url, listener, "getPolls", true);
    }

    public static void sendMyAnswerPool(String uid, Poll poll, final JSONArrayListener listener){
        if(poll.getPoolType().equals(Poll.POLL_TYPE_CARAT)){
            sendMyAnswerCARAT(uid,poll,listener);
        }else{
            sendMyAnswerSymptoms(uid,poll,listener);
        }
    }

    private static void sendMyAnswerCARAT(String uid, Poll poll, final JSONArrayListener listener){
        String url = "custom/carat_answers.json";

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("userauthor", uid);
        params.put("date", new GregorianCalendar().getTimeInMillis()/1000);

        JSONArray arrayAnswers = new JSONArray();
        try {
            for(Question q : poll.getListQuestions()){
                JSONObject aswerObj = new JSONObject();

                aswerObj.put("questionid",q.getNid());

                QuestionMultipleChoice qMC = (QuestionMultipleChoice) q;

                for(Answer a : qMC.getListAnswers()){
                    if(a.isSelected()){
                        aswerObj.put("ref",a.getNid());
                        break;
                    }
                }

                arrayAnswers.put(aswerObj);
            }

            params.put("answers",arrayAnswers);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONArray(url, params, listener, "sendMyAnswerCARAT");
    }

    private static void sendMyAnswerSymptoms(String uid, Poll poll, final JSONArrayListener listener){
        String url = "custom/symptoms_answers.json";

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("userauthor", uid);
        params.put("type", poll.getPoolType());
        params.put("date", new GregorianCalendar().getTimeInMillis()/1000);

        JSONArray arrayAnswers = new JSONArray();
        try {
            for(Question q : poll.getListQuestions()){
                JSONObject aswerObj = new JSONObject();

                aswerObj.put("questionid",q.getNid());

                if(q instanceof QuestionSlider){
                    aswerObj.put("ref",""+((QuestionSlider) q).getTotalSelected());
                }else{
                    //QuestionYesNo
                    aswerObj.put("ref",""+(((QuestionYesNo) q).isYes()!=null && ((QuestionYesNo) q).isYes()?"true":"false"));
                }

                arrayAnswers.put(aswerObj);
            }

            params.put("answers",arrayAnswers);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONArray(url, params, listener, "sendMyAnswerSymptoms");
    }

    public static void requestMyCaratList(String uid, final JSONArrayListener listener){
        String url = "custom/carat_results.json";
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("userauthor", uid);

        postJSONArray(url, params, listener, "requestMyCaratList");
    }

    public static void requestMySintList(String uid, String type, final JSONArrayListener listener){
        String url = "custom/symptons_results.json";
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("userauthor", uid);
        params.put("type", type);

        postJSONArray(url, params, listener, "requestMySintList");
    }

    public static void requestAvaliationNidForUser(String uid, JSONArrayListener listener){
        String url = "views/profile_reviews.json?display_id=profile_review&args[0]="+uid;

        getJSONArray(url, listener, "requestAvaliationNidForUser", true, null);
    }

    public static void recoverPassword(String email, final JSONArrayListener listener){
        String url = "user/request_new_password.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", email);

        postJSONArray(url, params, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("recoverPassword", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("recoverPassword", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "recoverPassword");
    }

    public static void login(String user, final String pass, final JSONObjectListener listener){
        String url="user/logintoboggan.json";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", user);
        params.put("pass", pass);

        postJSONObject(url, params, listener, "login");
    }

    public static void signUp(String language, String fidAvatar, String name, String email, String password, final JSONObjectListener listener){
        String url = "user/register.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("mail", email);
        params.put("pass", password);

        if(language!=null && !language.isEmpty()) {
            params.put("language", language);
        }

        try {
            params.put("field_name_user", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",name))));

            if(fidAvatar!=null && !fidAvatar.isEmpty()){
                params.put("field_picture", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("fid", fidAvatar))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("CRIARCONTA", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CRIARCONTA", "ERRO1 - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "signUp");
    }

    public static void updatePollComment(String nid, String comment, final JSONObjectListener listener) {
        String url = "node/"+nid;

        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            params.put("field_comments", new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", comment))));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updatePollComment", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("updatePollComment", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "updatePollComment");
    }

    public static void createMedicineServer(String uid, UserMedicine userMedicine, boolean deleted, final JSONObjectListener listener){
        String url = "entity_node.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", "medicine");

        try {
            params.put("uid",uid);
            params.put("title", userMedicine.getMedicineName());

            if(deleted) {
                params.put("status", JSONObject.NULL);
            }

            params.put("field_med_type", createTextFieldJSONObject(userMedicine.getMedicineType().getCode()));
            params.put("field_med_share", createBooleanSingleONOffJSONObject(userMedicine.isShareWithDoctor()));
            params.put("field_med_start_date", createDateFieldJsonObject(datetimeDateFormatter,userMedicine.getStartDate()));

            if(userMedicine instanceof UserNormalMedicine){
                UserNormalMedicine aux = (UserNormalMedicine) userMedicine;

                params.put("field_med_duration", createTextFieldJSONObject(""+aux.getDuration()));
            }else{
                UserSOSMedicine aux = (UserSOSMedicine) userMedicine;

                params.put("field_item_is_sos", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value","1"))));
                params.put("field_med_severety",createTextFieldJSONObject(""+aux.getSeverity()));
                params.put("field_med_trigger", createTextFieldJSONObject(aux.getTrigger()));
                params.put("field_med_health_service", createTextFieldJSONObject(aux.getHealthService().getCode()));
                //params.put("field_med_sos_dosage", createTextFieldJSONObject(""+aux.getSosDosage()));
            }

            params.put("field_med_sos_dosage", createTextFieldJSONObject(""+userMedicine.getTotalSOSDosages()));
            params.put("field_med_notes", createTextFieldJSONObject(userMedicine.getNote()));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createMedicineServer", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createMedicineServer", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createMedicineServer");
    }

    public static void updateMedicineServer(String nid, UserMedicine userMedicine, boolean deleted, final JSONObjectListener listener){
        String url = "entity_node/"+nid;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", "medicine");

        try {
            //params.put("uid",uid);
            //params.put("title", userMedicine.getMedicineName());
            if(deleted) {
                params.put("status", JSONObject.NULL);
            }

            params.put("field_med_type", createTextFieldJSONObject(userMedicine.getMedicineType().getCode()));
            params.put("field_med_share", createBooleanSingleONOffJSONObject(userMedicine.isShareWithDoctor()));
            params.put("field_med_start_date", createDateFieldJsonObject(datetimeDateFormatter,userMedicine.getStartDate()));

            if(userMedicine instanceof UserNormalMedicine){
                UserNormalMedicine aux = (UserNormalMedicine) userMedicine;

                params.put("field_med_duration", createTextFieldJSONObject(""+aux.getDuration()));
            }else{
                UserSOSMedicine aux = (UserSOSMedicine) userMedicine;

                params.put("field_item_is_sos", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value","1"))));
                params.put("field_med_severety",createTextFieldJSONObject(""+aux.getSeverity()));
                params.put("field_med_trigger", createTextFieldJSONObject(aux.getTrigger()));
                params.put("field_med_health_service", createTextFieldJSONObject(aux.getHealthService().getCode()));
                //params.put("field_med_sos_dosage", createTextFieldJSONObject(""+aux.getSosDosage()));
            }
            params.put("field_med_sos_dosage", createTextFieldJSONObject(""+userMedicine.getTotalSOSDosages()));
            params.put("field_med_notes", createTextFieldJSONObject(userMedicine.getNote()));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createMedicineServer", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createMedicineServer", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createMedicineServer");
    }


    public static void createMedicineScheduleServer(String medicineNid, MedicineSchedule medSchedule, final JSONObjectListener listener){
        String url = "entity_field_collection_item.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("field_name", "field_med_schedule");
        params.put("hostEntityType", "node");
        params.put("hostEntityId", medicineNid);

        try {
            params.put("field_sche_code", createTextFieldJSONObject(""+medSchedule.getSelection().getCode()));
            params.put("field_sche_code_desc", createTextFieldJSONObject(""+medSchedule.getSelection().getDesc()));
            params.put("field_sche_interval", createTextFieldJSONObject(""+medSchedule.getSelection().getIntervalHours()));


            ArrayList<String> aux = new ArrayList<>();

            String daysselected = "";
            if(medSchedule.getDays().getSelectedDays() != null)
                for(Days d : medSchedule.getDays().getSelectedDays()){
                    if (d.isSelected()){
                        aux.add(""+d.getCode());
                    }
                }

            daysselected = TextUtils.join(",",aux);

            params.put("field_sche_days_selected", createTextFieldJSONObject(daysselected));
            params.put("field_sche_days_option", createTextFieldJSONObject(""+medSchedule.getDays().getSelectedOption()));
            params.put("field_sche_days_interval", createTextFieldJSONObject(""+medSchedule.getDays().getIntervalDays()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createMedicineSchedule", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createMedicineSchedule", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createMedicineTime");
    }

    public static void createMedicineTimeServer(String medicineNid, MedicineTime medTime, final JSONObjectListener listener){
        String url = "entity_field_collection_item.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("field_name", "field_med_times");
        params.put("hostEntityType", "node");
        params.put("hostEntityId", medicineNid);
        try {
            params.put("field_med_day_fase", createTextFieldJSONObject(medTime.getFaseTime().getHourInitDesc()));
            params.put("field_med_dosage", createTextFieldJSONObject(""+medTime.getDosage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createMedicineTime", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createMedicineTime", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createMedicineTime");
    }

    public static void createMedicineInhalerServer(String medicineNid, MedicineInhaler medicineInhaler, final JSONObjectListener listener){
        String url = "entity_field_collection_item.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("field_name", "field_med_inhalers");
        params.put("hostEntityType", "node");
        params.put("hostEntityId", medicineNid);
        try {
            params.put("field_med_active", createBooleanSingleONOffJSONObject(medicineInhaler.isActive()));
            params.put("field_med_barcode", createTextFieldJSONObject(medicineInhaler.getBarcode()));
            params.put("field_med_dosage", createTextFieldJSONObject(""+medicineInhaler.getDosage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createInhaler", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createInhaler", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createInhaler");
    }

    public static void createTimeLineItemServer(String uid, TimelineItem timelineItem, boolean deleted, final JSONObjectListener listener){
       String url = "entity_node.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        String targetId = "";
        if(timelineItem.getMedicine()!=null){
            targetId = timelineItem.getMedicine().getNid();
        }else if(timelineItem.getBadge()!=null){
            targetId = timelineItem.getBadge().getBadge().getCode();
        }else if(timelineItem.getPoll() != null){
            targetId = timelineItem.getPoll().getNid();
        }

        try {
            params.put("type", "timeline_item");
            params.put("uid",uid);

            if(deleted) {
                params.put("status", JSONObject.NULL);
            }

            params.put(timelineItem.getBadge()!=null?"field_item_entity_badge":"field_item_entity_ref", createEntityRefAutocompleteJSONObject(targetId));

            params.put("field_item_date", createDateFieldJsonObject(dateOnlyDateFormatter,timelineItem.getDate()));
            params.put("field_item_start_time", createDateFieldJsonObject(datetimeDateFormatter,timelineItem.getStartTime()));
            params.put("field_item_end_time",createDateFieldJsonObject(datetimeDateFormatter,timelineItem.getEndTime()));

            params.put("field_item_timepoints", createTextFieldJSONObject(""+timelineItem.getTimePoints()));
            params.put("field_item_week_number", createTextFieldJSONObject(timelineItem.getWeekNumber()));

            //params.put("field_item_is_first", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",timelineItem.is()?"1":"0"))));
            params.put("field_item_is_sos", createBooleanSingleONOffJSONObject(timelineItem.isSOS()));

            params.put("field_item_note", createTextFieldJSONObject(timelineItem.getTimeLineNote()));

            if(timelineItem.getState()!=null) {
                params.put("field_item_state", createTextFieldJSONObject(timelineItem.getState()));
            }

            if(timelineItem.getDateTaken()!=null) {
                params.put("field_item_taken", createDateFieldJsonObject(datetimeDateFormatter,timelineItem.getDateTaken()));
                params.put("field_item_points_won", createTextFieldJSONObject(""+timelineItem.getTimePoints()));
            }

            params.put("field_item_dosage", createTextFieldJSONObject(""+timelineItem.getDosage()));

            if(timelineItem.getFaseTime()!=null) {
                params.put("field_item_fase_time_code", createTextFieldJSONObject(timelineItem.getFaseTime().getHourInitDesc()));
            }

            params.put("field_recognitionfailedtimes", createTextFieldJSONObject(""+timelineItem.getRecognitionFailedTimes()));

            params.put("field_item_main_time", createBooleanSingleONOffJSONObject(timelineItem.isMainTime()));

            if(timelineItem.getLatitude()!=null && !timelineItem.getLatitude().isEmpty()) {
                params.put("field_latitude", createTextFieldJSONObject(timelineItem.getLatitude()));
                params.put("field_longitude", createTextFieldJSONObject(timelineItem.getLongitude()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createTimeLineItem", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createTimeLineItem", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createTimeLineItemServer");
    }

    public static void updateTimeLineItemServer(String nid, TimelineItem timelineItem, boolean deleted, final JSONObjectListener listener){
        String url = "entity_node/"+nid;

        HashMap<String, Object> params = new HashMap<String, Object>();
        /*String targetId = "";
        if(timelineItem.getMedicine()!=null){
            targetId = timelineItem.getMedicine().getNid();
        }else if(timelineItem.getBadge()!=null){
            targetId = timelineItem.getBadge().getBadge().getCode();
        }else if(timelineItem.getPoll() != null){
            targetId = timelineItem.getPoll().getNid();
        }*/

        try {
            //params.put("type", "timeline_item");
            //params.put("uid",uid);
            //params.put("type", "timeline_item");
            //params.put("item_id", nid);

            if(deleted) {
                params.put("status", JSONObject.NULL);
            }

            //params.put(timelineItem.getBadge()!=null?"field_item_entity_badge":"field_item_entity_ref", createEntityRefAutocompleteJSONObject(targetId));

            params.put("field_item_date", createDateFieldJsonObject(dateOnlyDateFormatter,timelineItem.getDate()));
            params.put("field_item_start_time", createDateFieldJsonObject(datetimeDateFormatter,timelineItem.getStartTime()));
            params.put("field_item_end_time",createDateFieldJsonObject(datetimeDateFormatter,timelineItem.getEndTime()));

            params.put("field_item_timepoints", createTextFieldJSONObject(""+timelineItem.getTimePoints()));
            params.put("field_item_week_number", createTextFieldJSONObject(timelineItem.getWeekNumber()));

            //params.put("field_item_is_first", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",timelineItem.is()?"1":"0"))));
            params.put("field_item_is_sos", createBooleanSingleONOffJSONObject(timelineItem.isSOS()));

            params.put("field_item_note", createTextFieldJSONObject(timelineItem.getTimeLineNote()));

            if(timelineItem.getState()!=null) {
                params.put("field_item_state", createTextFieldJSONObject(timelineItem.getState()));
            }

            if(timelineItem.getDateTaken()!=null) {
                params.put("field_item_taken", createDateFieldJsonObject(datetimeDateFormatter,timelineItem.getDateTaken()));
                params.put("field_item_points_won", createTextFieldJSONObject(""+timelineItem.getPointWon()));
            }

            params.put("field_item_dosage", createTextFieldJSONObject(""+timelineItem.getDosage()));

            if(timelineItem.getFaseTime()!=null) {
                params.put("field_item_fase_time_code", createTextFieldJSONObject(timelineItem.getFaseTime().getHourInitDesc()));
            }

            params.put("field_recognitionfailedtimes", createTextFieldJSONObject(""+timelineItem.getRecognitionFailedTimes()));

            params.put("field_item_main_time", createBooleanSingleONOffJSONObject(timelineItem.isMainTime()));

            if(timelineItem.getLatitude()!=null && !timelineItem.getLatitude().isEmpty()) {
                params.put("field_latitude", createTextFieldJSONObject(timelineItem.getLatitude()));
                params.put("field_longitude", createTextFieldJSONObject(timelineItem.getLongitude()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("createTimeLineItem", response.toString());

                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("createTimeLineItem", "ERRO - " + error.getMessage());
                listener.onErrorResponse(error);
            }
        }, "createTimeLineItemServer");
    }

    private static JSONObject createTextFieldJSONObject(String value) throws JSONException{
        return new JSONObject().put("und",  new JSONArray().put(new JSONObject().put("value", value)));
    }

    private static JSONObject createEntityRefAutocompleteJSONObject(String targetId) throws JSONException{
        return new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("target_id", ""+targetId+"")));
    }

    private static JSONObject createDateFieldJsonObject(SimpleDateFormat formater, GregorianCalendar value) throws JSONException{
        return new JSONObject().put("und", new JSONArray().put(0, new JSONObject().put("value", formater.format(value.getTime()))));
    }

    private static JSONObject createBooleanSingleONOffJSONObject(boolean value) throws JSONException{
        return new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",value?"1":"0")));
    }

    public static void logOutFromServer(final JSONObjectListener listener){
        String url = "user/logout.json";

        postJSONArray(url, null, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                listener.onResponse(null);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, "logOutFromServer");
    }

    public static void requestActiveDevice(String uid, JSONArrayListener listener){
        String url = "views/users.json?display_id=activedevice&args[0]="+uid;

        getJSONArray(url, listener, "requestActiveDevice", true, null);
    }

    public static void userLoggedServer(final JSONObjectListener listener){
        String url = "custom/isloggedin";

        HashMap<String,Object> params = new HashMap<String, Object>();

        postJSONObject(url, params, listener, "userLoggedServer");
    }

    public static void updateUserDeviceID(String uid, String userDeviceId){
        String url = "user/"+uid+".json";

        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            params.put("field_active_device", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",userDeviceId))));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        putJSONObject(url, params, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateUserDeviceID","OK");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("updateUserDeviceID","error");
            }
        }, "updateUserDeviceID");
    }

    public static void getMedicineServer(JSONArrayListener listener){
        String url = "views/medicine.json?display_id=medicine_items";

        getJSONArray(url, listener, "getMedicineServer", true, null);
    }

    public static void getTimelineServer(JSONArrayListener listener){
        String url = "views/time_line.json?display_id=timeline_items";

        getJSONArray(url, listener, "getTimelineServer", true, null);
    }

    public static void deleteMedicineInhalerTimesOnServer(String nid, final JSONObjectListener listener){
        String url = "custom/delete_med_fieldcollections.json";

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("nid", nid);

        postJSONObject(url, params, listener, "deleteMedicineITS");
    }

    public static void createImageOnServer(String fidImg, String fileName, JSONObjectListener listener){
        String url = "node.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", "image");
        try {
            params.put("field_image_field", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("fid",fidImg))));
            params.put("field_image_name", new JSONObject().put("und",new JSONArray().put(0,new JSONObject().put("value",fileName))));
            params.put("field_device", new JSONObject().put("und", new JSONObject().put("value", "Android")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJSONObject(url, params, listener, "askWarrior");
    }

    public static void markAllRead(String uidSender, String uidReciver, final JSONObjectListener listener){
        String url = "custom/mark_chat_as_read.json";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("usersent", uidSender);
        params.put("userreceive", uidReciver);

        postJSONObject(url, params, listener, "markAllRead");

    }
}
