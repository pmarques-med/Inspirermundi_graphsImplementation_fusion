package com.bloomidea.inspirers.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.FlutiformDialog;
import com.bloomidea.inspirers.NoInternetDialog;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.WinBadgeDialog;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.listener.AlertDialogListener;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.listener.PictureLoadListener;
import com.bloomidea.inspirers.listener.StringListener;
import com.bloomidea.inspirers.listener.TreeMapListener;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.NavAux;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserMedicineAuxSync;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.model.UserSOSMedicine;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by michellobato on 15/02/17.
 */

public class Utils {
    private static final int ID_ALARM_MEDICINE = 2000;

    private static DecimalFormat formatterWeek = new DecimalFormat("00");
    private static SimpleDateFormat formatterStartEndWeek = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat formatterDateTimeline = new SimpleDateFormat("EEEE, dd MMM yyyy");

    public static void openIntent(Activity context, Intent intent, int animEnter, int animExit){
        Bundle translateBundle = ActivityOptions.makeCustomAnimation(context, animEnter, animExit).toBundle();
        context.startActivity(intent, translateBundle);
    }

    public static void openIntentForResult(Activity context,Intent intent,int animEnter,int animExit,int requestCode){
        Bundle translateBundle = ActivityOptions.makeCustomAnimation(context, animEnter, animExit).toBundle();
        context.startActivityForResult(intent, requestCode, translateBundle);
    }

    public static boolean isOnline(Context context, boolean mostrarAviso, FragmentManager fragmentManager) {
        boolean net = false;
        if (context != null){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            net = netInfo != null && netInfo.isConnectedOrConnecting();

            if (!net && mostrarAviso && fragmentManager!=null) {
                Utils.showNoInternet(context, fragmentManager);
            }
        }

        return net;
    }

    public static boolean isEmailValid(String email) {
        String regExpn = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public static void showNoInternet(Context context, FragmentManager fragmentManager) {
        NoInternetDialog dialogdenunciaOptions = new NoInternetDialog();
        dialogdenunciaOptions.show(fragmentManager, "NO INTERNET");
    }

    public static void showWinBadge(FragmentManager fragmentManager, Badge badge, WinBadgeDialog.WinBadgeDialogListener listener) {
        WinBadgeDialog winBadgeDialog = new WinBadgeDialog();
        winBadgeDialog.setBadge(badge,listener);

        winBadgeDialog.show(fragmentManager, "BADGE");
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static ProgressDialog createRingProgressDialogNoText(Context context){
        ProgressDialog ringProgressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyleNoWindow);

        try {
            ringProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.d("RingDialogNoText", e.toString());
        }

        ringProgressDialog.setCancelable(false);

        ringProgressDialog.setContentView(R.layout.dialog_progress);

        return ringProgressDialog;
    }

    public static void openUrl(Activity activity, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    public static byte[] getPictureBlob(Bitmap picture) {
        byte[] bArray = null;
        if(picture != null){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bArray = bos.toByteArray();
        }

        return bArray;
    }

    public static void clearSession(Activity context, JSONObjectListener listener){
        if(AppController.getmInstance().getActiveUser()!=null) {
            String tokenNot = AppController.getmInstance().getActiveUser().getNotificationsToken();
            if(tokenNot!=null && !tokenNot.isEmpty()) {
                APIInspirers.unregisterUserNotifications(tokenNot, listener);
            }else{
                listener.onResponse(null);
            }
        }

        clearFacebookSession();
    }

    public static void clearFacebookSession() {
        LoginManager.getInstance().logOut();
    }

    private static Callback createPictureCallBack(final Context c, final ImageView img, final ProgressBar progressBar, final int idPictureDefault, final Transformation transform, final PictureLoadListener listener) {
        return new Callback() {
            @Override
            public void onSuccess() {
                if(listener!=null){
                    listener.onEndLoad(true);
                }

                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onError(Exception e) {
                if (transform != null) {
                    Picasso.get().load(idPictureDefault).transform(transform).into(img);
                } else {
                    Picasso.get().load(idPictureDefault).into(img);
                }

                if(listener!=null){
                    listener.onEndLoad(false);
                }

                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static void loadImageView(Context context, ImageView imageView, ProgressBar progressBar, String imgUrl, Bitmap img, int resourceDefault, PictureLoadListener listener){
        if(img!=null) {
            if(progressBar!=null)
                progressBar.setVisibility(View.GONE);

            imageView.setImageBitmap(img);

            if(listener!=null){
                listener.onEndLoad(true);
            }
        }else{
            if(imgUrl!=null && !imgUrl.isEmpty()){
                if(progressBar!=null)
                    progressBar.setVisibility(View.VISIBLE);

                Picasso.get().load(imgUrl)
                        .fit().centerCrop()
                        .into(imageView, Utils.createPictureCallBack(context, imageView, progressBar, resourceDefault, null, listener));


            }else{
                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);

                imageView.setImageResource(resourceDefault);

                if(listener!=null){
                    listener.onEndLoad(false);
                }
            }
        }
    }

    public static void showFlutiformDialog(Context context, android.app.FragmentManager fragmentManager, int width, int ypos, DialogInterface.OnDismissListener dismissListener) {
        FlutiformDialog flutiformDialog = new FlutiformDialog();
        flutiformDialog.setBoxValues(width,ypos, dismissListener);

        flutiformDialog.show(fragmentManager, "FLUTIFORM");
    }

    public static String getWeekNumber(GregorianCalendar date){
        String weekOfWearAux = ""+date.get(Calendar.WEEK_OF_YEAR);

        String weekFormatted = formatterWeek.format(date.get(Calendar.WEEK_OF_YEAR));
        int WeekNumberYear = date.get(Calendar.YEAR);

        if(weekOfWearAux.length() == 1){
            if(date.get(Calendar.MONTH) == Calendar.DECEMBER){
                WeekNumberYear += 1;
            }
        }

        return WeekNumberYear+"_"+weekFormatted;
    }

    public static String getStartEndOFWeek(Context context, String weekYearNumber){
        String[] aux = weekYearNumber.split("_");

        int weekYear = Integer.parseInt(aux[0]);
        int weekNumber = Integer.parseInt(aux[1]);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();

        //calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        calendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
        calendar.set(Calendar.YEAR, weekYear);

        String startDateInStr = formatterStartEndWeek.format(calendar.getTime());

        calendar.add(Calendar.DATE, 6);
        String endDaString = formatterStartEndWeek.format(calendar.getTime());

        return context.getResources().getString(R.string.week_dates,startDateInStr,endDaString);
    }

    public static String getTimelineStingDate(Context context,GregorianCalendar date) {
        String aux = formatterDateTimeline.format(date.getTime());

        date.set(Calendar.HOUR_OF_DAY,0);
        date.set(Calendar.MINUTE,0);
        date.set(Calendar.SECOND,0);
        date.set(Calendar.MILLISECOND,0);

        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);

        if(isToday(date)){
            aux = context.getString(R.string.today);
        }else{
            today.add(Calendar.DAY_OF_MONTH,1);

            if(sameDate(date,today)){
                aux = context.getString(R.string.tomorrow);
            }else {
                today.add(Calendar.DAY_OF_MONTH,-2);
                if(sameDate(date,today)){
                    aux = context.getString(R.string.yesterday);
                }
            }
        }

        return aux;
    }

    public static boolean isToday(GregorianCalendar day) {
        GregorianCalendar hoje = new GregorianCalendar();

        return day.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) && day.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean sameDate(GregorianCalendar date1, GregorianCalendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) && date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
    }

    public static void changeBtnBackgroundMedicine(ImageView btn, int backgroundResourceId){
        int paddingL = btn.getPaddingLeft();
        int paddingT = btn.getPaddingTop();
        int paddingR = btn.getPaddingRight();
        int paddingB = btn.getPaddingBottom();

        btn.setBackgroundResource(backgroundResourceId);
        btn.setPadding(paddingL,paddingT,paddingR,paddingB);
    }

    public static void animateBtnToTakeMedicine(Context context, View btn) {
        btn.clearAnimation();

        // Animation pulse = AnimationUtils.loadAnimation(context, R.anim.sclae2);

        // btn.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.sclae2);
        btn.startAnimation(anim);
        /*anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.sclae2);
                anim.setAnimationListener(this);
                btn.startAnimation(anim);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {


            }

            @Override
            public void onAnimationStart(Animation arg0) {


            }

        });*/


    }

    public static Bitmap getMarkerBitmapFromView(Context context, int total) {

        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_layout, null);
        TextView markerImageView = (TextView) customMarkerView.findViewById(R.id.total_textView);
        markerImageView.setText(""+(total>=100?"99+":""+total));
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public static void scheduleAlarmForUserNormalMedicine(UserNormalMedicine userMedicine, Context context) {
        GregorianCalendar now = new GregorianCalendar();

        ArrayList<GregorianCalendar> listDates = new ArrayList<>();


        /*for(int i=0; i<userMedicine.getDuration(); i++){
            GregorianCalendar aux = new GregorianCalendar();
            aux.setTimeInMillis(userMedicine.getStartDate().getTimeInMillis());
            aux.add(Calendar.DAY_OF_MONTH,i);

            listDates.add(aux);
        }*/

        int duration;
        if (userMedicine.getDuration() == 0){
            duration = 90;
        }else{
            duration = userMedicine.getDuration();
        }

        for(int i=0; i<duration; i++){
            GregorianCalendar aux = new GregorianCalendar();
            aux.setTimeInMillis(userMedicine.getStartDate().getTimeInMillis());
            aux.add(Calendar.DAY_OF_MONTH,i);

            // MARK4 - SELECT DAYS TO CREATE, CHECK DAYS WEEK
            for (MedicineSchedule auxSche : userMedicine.getSchedules()){
                Log.d("TAG_CREATE", ""+auxSche.getDays().getSelectedOption());

                if (auxSche.getDays().getSelectedOption() == MedicineDays.ALL_DAYS_OPTION){
                    listDates.add(aux);
                }else if (auxSche.getDays().getSelectedOption() == MedicineDays.SPEC_DAYS_OPTION){
                    for (Days auxDays : auxSche.getDays().getSelectedDays()){
                        if (auxDays.isSelected() && (auxDays.getCode() == aux.get(Calendar.DAY_OF_WEEK))){
                            listDates.add(aux);
                        }
                    }
                }else{
                    int interval = auxSche.getDays().getIntervalDays();
                    if (i == 0 || i%interval == 0){
                        listDates.add(aux);
                    }
                }
            }
        }

        for(GregorianCalendar date : listDates){
            for (MedicineSchedule schedule : userMedicine.getSchedules()){
                for(MedicineTime medTime : schedule.getTimes()) {
                    GregorianCalendar initDate = new GregorianCalendar();
                    initDate.setTimeInMillis(date.getTimeInMillis());
                    initDate.set(Calendar.HOUR_OF_DAY, medTime.getFaseTime().getHourInit());
                    initDate.set(Calendar.MINUTE, medTime.getFaseTime().getMinutesInit());

                    Long time = initDate.getTimeInMillis();

                    if (time > now.getTimeInMillis()) {
                        String id = new SimpleDateFormat("yyMMddhh").format(new Date(time));
                        id += medTime.getId();

                        int idAux = Integer.parseInt(id);

                        Intent intentAlarm = new Intent(context, AlarmReciever.class);
                        intentAlarm.putExtra(AlarmReciever.ALARM_TYPE, InternalBroadcastReceiver.TYPE_MEDICINE);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idAux, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                        // create the object
                        try {
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            //set the alarm for particular time
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                            }else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                            }

                            Log.d("Alarme", "Alarm Scheduled for " + (new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(time))) + "(" + idAux + ")");
                        } catch(Exception generic) {
                            Log.d("Expection","exception");
                        }
                    }
                }
            }
        }
    }

    public static void deleteAlarmForUserNormalMedicine(UserNormalMedicine userMedicine, Context context) {
        GregorianCalendar now = new GregorianCalendar();

        ArrayList<GregorianCalendar> listDates = new ArrayList<>();


        for(int i=0; i<userMedicine.getDuration(); i++){
            GregorianCalendar aux = new GregorianCalendar();
            aux.setTimeInMillis(userMedicine.getStartDate().getTimeInMillis());
            aux.add(Calendar.DAY_OF_MONTH,i);

            listDates.add(aux);
        }

        for(GregorianCalendar date : listDates) {
            for (MedicineSchedule schedule : userMedicine.getSchedules()){
                for(MedicineTime medTime : schedule.getTimes()) {
                    GregorianCalendar initDate = new GregorianCalendar();
                    initDate.setTimeInMillis(date.getTimeInMillis());
                    initDate.set(Calendar.HOUR_OF_DAY, medTime.getFaseTime().getHourInit());
                    initDate.set(Calendar.MINUTE, medTime.getFaseTime().getMinutesInit());
                    Long time = initDate.getTimeInMillis();

                    if (time > now.getTimeInMillis()) {
                        String id = new SimpleDateFormat("yyMMddhh").format(new Date(time));
                        id += medTime.getId();

                        int idAux = Integer.parseInt(id);

                        Intent intentAlarm = new Intent(context, AlarmReciever.class);
                        intentAlarm.putExtra(AlarmReciever.ALARM_TYPE, InternalBroadcastReceiver.TYPE_MEDICINE);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idAux, intentAlarm, FLAG_UPDATE_CURRENT);
                        pendingIntent.cancel();

                        // create the object
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        alarmManager.cancel(pendingIntent);

                        Log.d("Alarme", "Alarm deleted for " + (new SimpleDateFormat("dd/MM/yyyy HH:mm").format(initDate.getTime())) + "(" + idAux + ")");
                    }
                }
            }
        }
    }

    public static void deleteAlarmForTimelineItem(TimelineItem timelineItem, Context context) {

        if(timelineItem.getMedicine() instanceof UserNormalMedicine) {
            long timelineMedTimeIdFase = -1;
            for (MedicineSchedule schedule : ((UserNormalMedicine) timelineItem.getMedicine()).getSchedules()){
                for(MedicineTime medTime : schedule.getTimes()) {
                    if(medTime.getFaseTime().getHourInitDesc().equals(timelineItem.getFaseTime().getHourInitDesc())) {
                        timelineMedTimeIdFase = medTime.getId();
                        break;
                    }
                }
            }

            GregorianCalendar now = new GregorianCalendar();

            Long time = timelineItem.getStartTime().getTimeInMillis();
            GregorianCalendar initDate = new GregorianCalendar();
            initDate.setTimeInMillis(time);

            if (time > now.getTimeInMillis()) {
                String id = new SimpleDateFormat("yyMMddhh").format(new Date(time));
                id += timelineMedTimeIdFase;

                int idAux = Integer.parseInt(id);

                Intent intentAlarm = new Intent(context, AlarmReciever.class);
                intentAlarm.putExtra(AlarmReciever.ALARM_TYPE, InternalBroadcastReceiver.TYPE_MEDICINE);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idAux, intentAlarm, FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();

                // create the object
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);

                Log.d("Alarme", "Alarm deleted for " + (new SimpleDateFormat("dd/MM/yyyy HH:mm").format(initDate.getTime())) + "(" + idAux + ")");
            }
        }
    }

    private static boolean checkPlayServices(Activity context, int PLAY_SERVICES_RESOLUTION_REQUEST) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog( context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("checkPlayServices", "This device is not supported.");
                //    finish();
            }
            return false;
        }
        return true;
    }

    public static void getRegId(Activity context) {
        int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

        if (checkPlayServices(context,PLAY_SERVICES_RESOLUTION_REQUEST)) {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            context.startService(intent);
        }
    }

    public static void createAllUserAlarms(Context context) {
        ArrayList<UserNormalMedicine> userNormalMedicines = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicines(AppController.getmInstance().getActiveUser().getId());

        for(UserNormalMedicine medicine : userNormalMedicines){
            Utils.scheduleAlarmForUserNormalMedicine(medicine, context);
        }
    }

    public static void deleteAllUserAlarms(Context context) {
        ArrayList<UserNormalMedicine> userNormalMedicines = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicines(AppController.getmInstance().getActiveUser().getId());

        for(UserNormalMedicine medicine : userNormalMedicines){
            Utils.deleteAlarmForUserNormalMedicine(medicine, context);
        }
    }

    public static String getTodayDateToStringFileName() {
        DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");

        Date today = Calendar.getInstance().getTime();

        return df.format(today);
    }

    public static void setRadioGroupEnable(RadioGroup radioGroup, boolean enable){
        for(int i = 0; i < radioGroup.getChildCount(); i++){
            (radioGroup.getChildAt(i)).setEnabled(enable);
        }
    }

    public static void createNavigationAction(String actiondDesc){
        User active = AppController.getmInstance().getActiveUser();

        if(active!=null) {
            NavAux auxNav = new NavAux(null, active.getId(), actiondDesc, new GregorianCalendar(), new GregorianCalendar(), MyActiveActivity.NAVIGATION_TYPE_ACTION);

            AppController.getmInstance().getNavigationDataSource().createNavigation(auxNav);
        }
    }

    public static void showMessageAlertDialog(Context context, String title, String message, final AlertDialogListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if(title!=null && !title.isEmpty())
            alertDialog.setTitle(title);

        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.okClick();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void loginUserAux(final Activity activity, final ProgressDialog ringProgressDialog, JSONObject response, final OkErrorListener listener) {
        try {

            Log.d("LOGIN",response.toString());

            final String uid = response.getJSONObject("user").getString("uid");
            final String sessionName = response.getString("session_name");
            final String sessionId = response.getString("sessid");

            String serverDeviceId = "";

            try {
                JSONObject auxObject = response.getJSONObject("user").getJSONObject("field_active_device");
                serverDeviceId = auxObject.getJSONArray("und").getJSONObject(0).getString("value");
            }catch (Exception e){
                Log.d("parseUser",e.toString());
            }

            final User auxUser = AppController.getmInstance().getUserDataSource().getUserByUid(uid);
            final boolean exists = auxUser!=null;

            String localUserDeviceId = UUID.randomUUID().toString() + "-" +uid;

            if(exists){
                localUserDeviceId = auxUser.getDeviceId();
            }
            final String finalLocalUserDeviceId = localUserDeviceId;
            if(serverDeviceId.isEmpty()){
                if(!exists){
                    //auxUser = InspirersJSONParser.parseUser(response);
                    APIInspirers.getUserInfo(uid,sessionName,sessionId, new JSONArrayListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            User auxUser2 = null;
                            try {
                                auxUser2 = InspirersJSONParser.parseUser2(response.getJSONObject(0),true,sessionName,sessionId,true);
                                auxUser2.setDeviceId(finalLocalUserDeviceId);

                                loginUser2(activity,ringProgressDialog, auxUser2,exists,false,listener);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ringProgressDialog.dismiss();
                                listener.error();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ringProgressDialog.dismiss();
                            listener.error();
                        }
                    });
                }else{
                    auxUser.setSessionId(sessionId);
                    auxUser.setSessionName(sessionName);
                    auxUser.setDeviceId(localUserDeviceId);

                    loginUser2(activity,ringProgressDialog, auxUser,exists,false,listener);
                }
            }else if(serverDeviceId.equals(localUserDeviceId)){
                auxUser.setSessionId(sessionId);
                auxUser.setSessionName(sessionName);
                //auxUser.setDeviceId(localUserDeviceId);

                loginUser2(activity,ringProgressDialog, auxUser,exists,false, listener);
            }else {
                ringProgressDialog.dismiss();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                resetUserAndContinueLogin(activity,ringProgressDialog, uid, auxUser, sessionId, sessionName, finalLocalUserDeviceId, listener);
                                //listener.ok();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                listener.error();
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                builder.setMessage(R.string.device_msg).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();

            }

            /*
            if(!exists){
                //auxUser = InspirersJSONParser.parseUser(response);
                APIInspirers.getUserInfo(uid,sessionName,sessionId, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        User auxUser2 = null;
                        try {
                            auxUser2 = InspirersJSONParser.parseUser2(response.getJSONObject(0),true,sessionName,sessionId,true);
                            loginUser2(activity, ringProgressDialog, auxUser2,false,listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialog.dismiss();
                    }
                });
            }else{
                auxUser.setSessionId(sessionId);
                auxUser.setSessionName(sessionName);
                loginUser2(activity,ringProgressDialog, auxUser,exists,listener);
            }
*/


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, R.string.login_error, Toast.LENGTH_SHORT).show();

            ringProgressDialog.dismiss();
        }
    }

    private static void resetUserAndContinueLogin(final Activity activity, final ProgressDialog ringProgressDialog, String uid, User auxUser, final String sessionId, final String sessionName, final String finalLocalUserDeviceId, final OkErrorListener listener) {
        ringProgressDialog.show();

        if(auxUser!=null){
            AppController.getmInstance().resetDatabaseUser(auxUser.getId());
        }

        APIInspirers.getUserInfo(uid,sessionName,sessionId, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                User auxUser2 = null;
                try {
                    auxUser2 = InspirersJSONParser.parseUser2(response.getJSONObject(0),true,sessionName,sessionId,true);
                    auxUser2.setDeviceId(finalLocalUserDeviceId);

                    loginUser2(activity,ringProgressDialog, auxUser2,false,true, listener);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ringProgressDialog.dismiss();
                    listener.error();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();
                listener.error();
            }
        });
    }

    private static void loginUser2(final Activity activity, final ProgressDialog ringProgressDialog, final User auxUser, final boolean exists, final boolean recreateAllFromWeb, final OkErrorListener listener) {
        AppController.getmInstance().localLoginUser(auxUser, exists, !recreateAllFromWeb, !recreateAllFromWeb);

        Utils.createNavigationAction(activity.getString(R.string.login_action));

        AppController.getmInstance().getUserToken(new StringListener() {
            @Override
            public void onResponse(String response) {
                APIInspirers.updateUserDeviceID(auxUser.getUid(), auxUser.getDeviceId());

                if(recreateAllFromWeb){
                    recreateUserInfoFromWeb(activity, auxUser,listener);
                }else{
                    listener.ok();
                }

                ringProgressDialog.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(recreateAllFromWeb){
                    recreateUserInfoFromWeb(activity, auxUser,listener);
                }else {
                    listener.ok();
                }

                ringProgressDialog.dismiss();
            }
        });
    }

    private static void recreateUserInfoFromWeb(final Activity activity, final User auxUser, final OkErrorListener listener) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(activity);

        APIInspirers.getMedicineServer(new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("TESTE MED", response.toString());
                final HashMap<String, UserMedicineAuxSync> listMedicine = InspirersJSONParser.parseListMedicine(activity,response);

                for(UserMedicineAuxSync userMed : listMedicine.values()){
                    if(userMed.getUserMedicine() instanceof UserNormalMedicine) {
                        AppController.getmInstance().getMedicineDataSource().createUserMedicine(auxUser.getId(), (UserNormalMedicine) userMed.getUserMedicine(),new GregorianCalendar(),false,userMed.isDeleted(),false);
                    }else{
                        //UserSOSMedicine
                        AppController.getmInstance().getMedicineDataSource().createSOSUserMedicine(auxUser.getId(),(UserSOSMedicine) userMed.getUserMedicine(), false,userMed.isDeleted(),false);
                    }
                }

                APIInspirers.requestMyCaratList(auxUser.getUid(), new JSONArrayListener() {
                    @Override
                    public void onResponse(final JSONArray responseArrayCarat) {
                        Log.d("TESTE CARAT", "OK "+responseArrayCarat);
                        APIInspirers.requestMySintList(auxUser.getUid(), Poll.POLL_TYPE_DAILY, new JSONArrayListener() {
                            @Override
                            public void onResponse(final JSONArray responseArrayDaily) {
                                Log.d("TESTE DAILY", "OK " +responseArrayDaily);

                                APIInspirers.requestMySintList(auxUser.getUid(), Poll.POLL_TYPE_WEEKLY, new JSONArrayListener() {
                                    @Override
                                    public void onResponse(final JSONArray responseArrayWeekly) {
                                        Log.d("TESTE WEEKLY", "OK "+ responseArrayWeekly);

                                        AppController.getmInstance().getPolls(new TreeMapListener() {
                                            @Override
                                            public void onResponse(TreeMap response) {
                                                final ArrayList<ListPoll> listCARAT = InspirersJSONParser.parseListPollAnswered(responseArrayCarat,(Poll) response.get(Poll.POLL_TYPE_CARAT));
                                                final ArrayList<ListPoll> listDaily = InspirersJSONParser.parseListPollAnswered(responseArrayDaily,(Poll) response.get(Poll.POLL_TYPE_DAILY));
                                                final ArrayList<ListPoll> listWeekly = InspirersJSONParser.parseListPollAnswered(responseArrayWeekly,(Poll) response.get(Poll.POLL_TYPE_WEEKLY));

                                                for(ListPoll auxP : listCARAT){
                                                    AppController.getmInstance().getPollDataSource().createAnswerToPollFromWeb(auxUser.getId(), auxP);
                                                }

                                                for(ListPoll auxP : listDaily){
                                                    AppController.getmInstance().getPollDataSource().createAnswerToPollFromWeb(auxUser.getId(), auxP);
                                                }

                                                for(ListPoll auxP : listWeekly){
                                                    AppController.getmInstance().getPollDataSource().createAnswerToPollFromWeb(auxUser.getId(), auxP);
                                                }


                                                Log.d("TESTE POLLS", "OK");

                                                APIInspirers.getTimelineServer(new JSONArrayListener() {
                                                    @Override
                                                    public void onResponse(final JSONArray response) {
                                                        Log.d("TESTE TIMELINE", response.toString());

                                                        AppController.getmInstance().getPolls(new TreeMapListener() {
                                                            @Override
                                                            public void onResponse(TreeMap responsePoll) {

                                                                ArrayList<TimelineItem> listTimeLine = InspirersJSONParser.parseListTimeline(activity,listMedicine,auxUser.getUserBadges(),responsePoll,response);

                                                                for(TimelineItem item : listTimeLine){
                                                                    AppController.getmInstance().getTimelineDataSource().createTimelineItem(auxUser.getId(),item);
                                                                }

                                                                Utils.createAllUserAlarms(activity);

                                                                ringProgressDialogNoText.dismiss();
                                                                listener.ok();
                                                            }

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.d("TESTE POLL", "ERROR");

                                                                AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                                                                AppController.getmInstance().loggout(activity);
                                                                listener.error();
                                                                ringProgressDialogNoText.dismiss();
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.d("TESTE TIMELINE", "ERROR");

                                                        AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                                                        AppController.getmInstance().loggout(activity);
                                                        listener.error();
                                                        ringProgressDialogNoText.dismiss();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("TESTE POLLS", "ERROR");

                                                AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                                                AppController.getmInstance().loggout(activity);
                                                listener.error();
                                                ringProgressDialogNoText.dismiss();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("TESTE WEEKLY", "ERROR");

                                        AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                                        AppController.getmInstance().loggout(activity);
                                        listener.error();
                                        ringProgressDialogNoText.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("TESTE DAILY", "ERROR");

                                AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                                AppController.getmInstance().loggout(activity);
                                listener.error();
                                ringProgressDialogNoText.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TESTE CARAT", "ERROR");

                        AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                        AppController.getmInstance().loggout(activity);
                        listener.error();
                        ringProgressDialogNoText.dismiss();
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTE MED", "ERROR");

                AppController.getmInstance().resetDatabaseUser(auxUser.getId());
                AppController.getmInstance().loggout(activity);
                listener.error();
            }
        });
    }


    public static boolean canOpenGodchild(int userLevel, int userNumberOfGodchilds){
        return (userLevel >= 10 && userNumberOfGodchilds == 0) ||
                (userLevel >= 20 && userNumberOfGodchilds <= 1) ||
                (userLevel >= 30 && userNumberOfGodchilds <= 2) ||
                (userLevel >= 40 && userNumberOfGodchilds <= 3) ||
                (userLevel >= 50 && userNumberOfGodchilds <= 4);
    }

    public static String cantOpenGodchildReason(Context context, int userLevel){

        int level = 0;
        if(userLevel < 10){
            level = 10;
        }else if(userLevel < 20){
            level = 20;
        }else if(userLevel < 30){
            level = 30;
        }else if(userLevel < 40){
            level = 40;
        }else if(userLevel < 50){
            level = 50;
        }

        String msg = context.getResources().getString(R.string.order_warrior_error, "" + level);
        if(level == 0){
            msg = context.getResources().getString(R.string.order_warrior_no_more);
        }

        return msg;
    }

    public static String cantAcceptGodchildReason(Context context, int userLevel){

        int level = 0;
        if(userLevel < 10){
            level = 10;
        }else if(userLevel < 20){
            level = 20;
        }else if(userLevel < 30){
            level = 30;
        }else if(userLevel < 40){
            level = 40;
        }else if(userLevel < 50){
            level = 50;
        }

        String msg = context.getResources().getString(R.string.order_warrior_error2, "" + level);
        if(level == 0){
            msg = context.getResources().getString(R.string.order_warrior_no_more);
        }

        return msg;
    }

    public static ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();

        if(files!=null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    inFiles.add(file);
                }
            }
        }

        return inFiles;
    }

    public static void configureCounter(TextView counter, int total){
        if(total > 0){
            counter.setVisibility(View.VISIBLE);
            String auxT = ""+total;

            if(total > 10){
                auxT = "9+";
            }

            counter.setText(auxT);
        }else{
            counter.setVisibility(View.GONE);
        }
    }
}
