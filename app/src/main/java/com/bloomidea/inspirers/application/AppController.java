package com.bloomidea.inspirers.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
//import android.support.multidex.MultiDexApplication;
//import android.support.v4.app.FragmentManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.multidex.MultiDexApplication;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.WinBadgeDialog;
import com.bloomidea.inspirers.database.AnswerDataSource;
import com.bloomidea.inspirers.database.BadgeDataSource;
import com.bloomidea.inspirers.database.InhalerDataSource;
import com.bloomidea.inspirers.database.MedicineDataSource;
import com.bloomidea.inspirers.database.MedicineScheduleDataSource;
import com.bloomidea.inspirers.database.MedicineTimeDataSource;
import com.bloomidea.inspirers.database.MySQLiteHelper;
import com.bloomidea.inspirers.database.NavigationDataSource;
import com.bloomidea.inspirers.database.PollDataSource;
import com.bloomidea.inspirers.database.QuestionDataSource;
import com.bloomidea.inspirers.database.TimelineDataSource;
import com.bloomidea.inspirers.database.UserDataSource;
import com.bloomidea.inspirers.events.LogoutEvent;
import com.bloomidea.inspirers.events.MedicineTakenEvent;
import com.bloomidea.inspirers.events.NumGodchildsUpdated;
import com.bloomidea.inspirers.events.PollAnswered;
import com.bloomidea.inspirers.events.PollsUpdated;
import com.bloomidea.inspirers.events.ReviewCreatedEvent;
import com.bloomidea.inspirers.events.UserBadgeWon;
import com.bloomidea.inspirers.events.UserInfoUpdated;
import com.bloomidea.inspirers.events.UserNewPoints;
import com.bloomidea.inspirers.events.UserStatsModifiedEvent;
import com.bloomidea.inspirers.listener.ArrayListListener;
import com.bloomidea.inspirers.listener.HashMapListener;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.listener.OkErrorPollListener;
import com.bloomidea.inspirers.listener.StringListener;
import com.bloomidea.inspirers.listener.TreeMapListener;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.Country;
import com.bloomidea.inspirers.model.Level;
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
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.BadgesAux;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.MyGcmListenerService;
import com.bloomidea.inspirers.utils.MyRxBus;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by michellobato on 16/02/17.
 */

public class AppController extends MultiDexApplication {

    public static final int INHALER_DETECTION_TOTAL = 1;
    public static final int INHALER_DETECTION_MAX_TIME_SECONDS = 10;
    public static final int POLL_DAILY_INIT_TIME = 18;

    private static AppController mInstance;

    private Typeface morebiRoundedBold;
    private Typeface morebiRoundedBlack;
    private Typeface morebiRoundedMedium;
    private Typeface morebiRoundedRegular;


    //DATABASE
    private MySQLiteHelper dbHelper;
    private UserDataSource userDataSource;
    private BadgeDataSource badgeDataSource;
    private TimelineDataSource timelineDataSource;
    private MedicineDataSource medicineDataSource;
    private InhalerDataSource inhalerDataSource;
    private MedicineTimeDataSource medicineTimeDataSource;
    private MedicineScheduleDataSource medicineSheduleDataSource;
    private AnswerDataSource answerDataSource;
    private QuestionDataSource questionDataSource;
    private PollDataSource pollDataSource;
    private NavigationDataSource navigationDataSource;

    private User activeUser;

    private MyRxBus myBus;

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue requestQueue;

    private HashMap<String, Country> countryList;
    private HashMap<Integer,Level> listLevels;
    private HashMap<String, Badge> listBadges;
    private CharSequence[] listLanguages;

    private static boolean activityVisible;


    private TreeMap<String,Poll> listPolls;

    // Constants
    public static final String AUTHORITY = "com.bloomidea.inspirers.sync.DummyProvider";
    public static final String ACCOUNT_TYPE = "com.bloomidea.inspirers";
    public static final String ACCOUNT_NAME = "Sync Inspirers";
    public static final long SYNC_INTERVAL_IN_SECONDS = 5400L;//1h30m

    private Account syncAccount;

    private String restLang="";

    private ArrayList<TimelineItem> timelineItemsNeedLocation = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MyGcmListenerService.NOTIFICATION_ID);

        //Locale.setDefault(new Locale("PT","pt"));
/*
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/inspirers.db";
                String backupDBPath = "backupname.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
Log.d("Error","Error");
        }
*/
        this.mInstance = this;

        this.restLang = getResources().getString(R.string.rest_lang);

        morebiRoundedBold = Typeface.createFromAsset(getBaseContext().getAssets(), "morebiRoundedBold.ttf");
        morebiRoundedBlack = Typeface.createFromAsset(getBaseContext().getAssets(), "morebiRoundedBlack.ttf");
        morebiRoundedMedium = Typeface.createFromAsset(getBaseContext().getAssets(), "morebiRoundedMedium.ttf");
        morebiRoundedRegular = Typeface.createFromAsset(getBaseContext().getAssets(), "morebiRoundedRegular.ttf");


        Log.d("LANGUAGE","LANG-"+restLang);

        if(getActiveUser()!=null) {
            int total = getTimelineDataSource().updateAllMissed(getActiveUser().getId());

            if(total>0){
                if(getUserDataSource().resetActualBonus(getActiveUser().getId())){
                    getActiveUser().setActualBonus(new BigDecimal(1));
                }
            }

            //verifyUserPoints();
        }

     //   GregorianCalendar dateAux = new GregorianCalendar(2017,0,1);
    //    dateAux.setFirstDayOfWeek(Calendar.MONDAY);


  //      Log.d("2017-01-01",dateAux.get(Calendar.YEAR)+"_"+dateAux.get(Calendar.WEEK_OF_YEAR));

//        getTimelineDataSource().getTimeLine(AppController.getmInstance().getActiveUser().getId());
    }

    public void getPolls(final TreeMapListener listener){
        if(listPolls==null || listPolls.isEmpty()){
            if(Utils.isOnline(this,false,null)) {
                APIInspirers.getPolls(new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listPolls = new TreeMap<String, Poll>();
                        TreeMap<String,Poll> auxPollsMemory = getPollDataSource().getPolls();

                        TreeMap<String,Poll> newPollsServer = InspirersJSONParser.parseListPolls(response);

                        for(Poll newPoll : newPollsServer.values()){
                            Poll auxMem = auxPollsMemory.get(newPoll.getPoolType());

                            if(auxMem!=null) {
                                if (newPoll.getUpdatedDate().after(auxMem.getUpdatedDate())) {
                                    long oldId = auxMem.getId();
                                    if(AppController.getmInstance().getPollDataSource().deletePoll(auxMem)){
                                        if(getPollDataSource().createPoll(newPoll)){
                                            getTimelineDataSource().updateTimelinePollId(oldId,newPoll.getId());
                                            listPolls.put(newPoll.getPoolType(),newPoll);
                                        }
                                    }
                                }else{
                                    listPolls.put(auxMem.getPoolType(),auxMem);
                                }
                            }else{
                                if(getPollDataSource().createPoll(newPoll)){
                                    listPolls.put(newPoll.getPoolType(),newPoll);
                                }

                            }
                        }

                        if(listener!=null)
                            listener.onResponse(listPolls);

                        Log.d("term",""+newPollsServer.size());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(listener!=null)
                            listener.onErrorResponse(error);
                    }
                });
            }else{
                listPolls = getPollDataSource().getPolls();
                if(listener!=null)
                    listener.onResponse(listPolls);
            }
        }else{
            if(listener!=null)
                listener.onResponse(listPolls);
        }
    }

    public Account getSyncAccount() {
        if(this.syncAccount == null) {
            this.syncAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
            AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);

            if (accountManager.addAccountExplicitly(this.syncAccount, null, null)) {
                Log.d("OK", "ok");
                getContentResolver().setSyncAutomatically(this.syncAccount, AUTHORITY, true);

                ContentResolver.addPeriodicSync(this.syncAccount, AUTHORITY,Bundle.EMPTY, SYNC_INTERVAL_IN_SECONDS);
            } else {
                Log.d("Error s", "error");
            }
        }

        return this.syncAccount;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public NavigationDataSource getNavigationDataSource(){
        if(navigationDataSource == null){
            navigationDataSource = new NavigationDataSource(getDbHelper());
        }

        return navigationDataSource;
    }

    public AnswerDataSource getAnswerDataSource(){
        if(answerDataSource == null){
            answerDataSource = new AnswerDataSource(getDbHelper());
        }

        return answerDataSource;
    }

    public PollDataSource getPollDataSource(){
        if(pollDataSource == null){
            pollDataSource = new PollDataSource(getDbHelper());
        }

        return pollDataSource;
    }

    public QuestionDataSource getQuestionDataSource(){
        if(questionDataSource == null){
            questionDataSource = new QuestionDataSource(getDbHelper());
        }

        return questionDataSource;
    }

    public UserDataSource getUserDataSource(){
        if(userDataSource == null){
            userDataSource = new UserDataSource(getDbHelper());
        }

        return userDataSource;
    }

    public BadgeDataSource getBadgeDataSource(){
        if(badgeDataSource == null){
            badgeDataSource = new BadgeDataSource(getDbHelper());
        }

        return badgeDataSource;
    }

    public TimelineDataSource getTimelineDataSource(){
        if(timelineDataSource == null){
            timelineDataSource = new TimelineDataSource(getDbHelper());
        }

        return timelineDataSource;
    }

    public MedicineDataSource getMedicineDataSource(){
        if(medicineDataSource == null){
            medicineDataSource = new MedicineDataSource(getDbHelper(),this);
        }

        return medicineDataSource;
    }

    public InhalerDataSource getInhalerDataSource(){
        if(inhalerDataSource == null){
            inhalerDataSource = new InhalerDataSource(getDbHelper());
        }

        return inhalerDataSource;
    }

    public MedicineTimeDataSource getMedicineTimeDataSource(){
        if(medicineTimeDataSource == null){
            medicineTimeDataSource = new MedicineTimeDataSource(getDbHelper());
        }

        return medicineTimeDataSource;
    }

    public MedicineScheduleDataSource getMedicineScheduleDataSource(){
        if(medicineSheduleDataSource == null){
            medicineSheduleDataSource = new MedicineScheduleDataSource(getDbHelper());
        }

        return medicineSheduleDataSource;
    }

    public MySQLiteHelper getDbHelper(){
        if(dbHelper==null){
            dbHelper = new MySQLiteHelper(this);
        }

        return dbHelper;
    }

    public User getActiveUser() {
        if(activeUser == null){
            activeUser = getUserDataSource().getActiveUser();
        }

        return activeUser;
    }

    public String getSessionCookie(){
        if (getActiveUser()==null){
            return null;
        }

        return activeUser.getSessionName() + "=" + activeUser.getSessionId();
    }

    public static AppController getmInstance() {
        return mInstance;
    }

    public Typeface getMorebiRoundedBold() {
        return morebiRoundedBold;
    }

    public Typeface getMorebiRoundedBlack() {
        return morebiRoundedBlack;
    }

    public Typeface getMorebiRoundedMedium() {
        return morebiRoundedMedium;
    }

    public Typeface getMorebiRoundedRegular() {
        return morebiRoundedRegular;
    }


    public MyRxBus getMyBus(){
        if(myBus == null){
            myBus = new MyRxBus();
        }

        return myBus;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    public void localLoginUser(User auxUser, boolean exists, boolean createUserPollsAuto, boolean createtimelineitem) {
        this.activeUser = auxUser;
        this.activeUser.setActive(true);

        if(exists){
            getUserDataSource().activateUser(auxUser.getUid(), auxUser.getSessionId(), auxUser.getSessionName(), auxUser.getDeviceId());
        }else{
            getUserDataSource().createUser(this.activeUser, createtimelineitem);
        }

        if(createUserPollsAuto) {
            checkUserPolls();
        }
    }

    public void getCountryList(final HashMapListener listener){
        if(countryList==null){
            APIInspirers.loadCountryList(new JSONArrayListener() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("COUNTRY",response.toString());
                    countryList = InspirersJSONParser.parseListCountry(response);

                    if(listener!=null)
                        listener.onResponse(countryList);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(listener!=null)
                        listener.onErrorResponse(error);
                }
            });
        }else{
            if(listener!=null)
                listener.onResponse(countryList);
        }
    }

    public void getCountryListAsArrayList(final ArrayListListener listener){
        if(countryList==null){
            getCountryList(new HashMapListener() {
                @Override
                public void onResponse(HashMap response) {
                    if(listener!=null) {
                        ArrayList<Country> aux = new ArrayList<>();
                        aux.addAll(countryList.values());
                        Collections.sort(aux, new Comparator<Country>() {
                            @Override
                            public int compare(Country country, Country t1) {
                                return country.getName().compareTo(t1.getName());
                            }
                        });


                        listener.onResponse(aux);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(listener!=null)
                        listener.onErrorResponse(error);
                }
            });
        }else{
            if(listener!=null) {
                ArrayList<Country> aux = new ArrayList<>();
                aux.addAll(countryList.values());
                Collections.sort(aux, new Comparator<Country>() {
                    @Override
                    public int compare(Country country, Country t1) {
                        return country.getName().compareTo(t1.getName());
                    }
                });

                listener.onResponse(aux);
            }
        }
    }

    public void getUserToken(final StringListener listener){
        APIInspirers.getUserToken(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getActiveUser().setToken(response);
                getUserDataSource().updateUserToken(getActiveUser().getUid(), response);

                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });

    }

    public void updateUserCountryInfo(Country country) {
        //getActiveUser()
        this.activeUser.setCountryFlagUrl(country.getFlag());
        this.activeUser.setCountryFlag(null);
        this.activeUser.setCountryName(country.getName());
        this.activeUser.setIsoCountry(country.getIso());

        getUserDataSource().updateUserCountry(activeUser.getUid(), country);
    }

    public void loggout(Activity context) {
        Utils.deleteAllUserAlarms(context);

        if(activeUser!=null && getUserDataSource().deactivateUser(activeUser.getUid())) {
            this.activeUser = null;

            AppController.getmInstance().getMyBus().send(new LogoutEvent());
        }

    }

    public void updateUserPicture(Bitmap aux) {
        getActiveUser().setPicture(aux);

        getUserDataSource().updateUserPicture(getActiveUser().getUid(), aux);
    }

    public HashMap<Integer,Level> getListLevels(){
        if(listLevels == null){
            listLevels = new HashMap<>();

            Resources res = getResources();
            TypedArray values = res.obtainTypedArray(R.array.levels);

            for(int i=0; i< values.length();i++){
                listLevels.put(new Integer(i+1), new Level(i+1, Integer.parseInt(values.getString(i))));
            }
        }

        return listLevels;
    }

    public CharSequence[] getListLanguages(){
        if(listLanguages == null){
            Resources res = getResources();
            TypedArray values = res.obtainTypedArray(R.array.languages_names);

            listLanguages = new CharSequence[values.length()];

            for(int i=0; i< values.length();i++){
                listLanguages[i] = values.getString(i);
            }

            Arrays.sort(listLanguages);
        }

        return listLanguages;
    }

    public HashMap<String, Badge> getListBadges() {
        if(listBadges == null){
            listBadges = new HashMap<>();

            Resources res = getResources();
            TypedArray codes = res.obtainTypedArray(R.array.badge_code);
            TypedArray names = res.obtainTypedArray(R.array.badge_name);
            TypedArray descs = res.obtainTypedArray(R.array.badge_desc);
            TypedArray pictures = res.obtainTypedArray(R.array.badge_picture);

            for(int i=0; i< codes.length();i++){
                listBadges.put(codes.getString(i),new Badge(names.getString(i),descs.getString(i),pictures.getResourceId(i,-1),codes.getString(i)));
            }
        }

        return listBadges;
    }

    public Badge getBadge(String badgeCode) {
        return getListBadges().get(badgeCode);
    }

    public void setUserBadgesNoSync(ArrayList<UserBadge> userBadges, boolean createtimelineitem) {
        if(getBadgeDataSource().createBadges(getActiveUser().getId(), userBadges, false, createtimelineitem)){
            getActiveUser().setUserBadges(userBadges);
        }
    }

    public void takeMedicine(TimelineItem item, boolean onTime, int positionAdapter, final FragmentManager fragmentManager, final OkErrorListener listener){
        int pointsToWin = item.getTimePoints();
        BigDecimal newMultiplier = null;

        if(onTime){
            pointsToWin = getActiveUser().getActualBonus().multiply(new BigDecimal(pointsToWin)).setScale(0,BigDecimal.ROUND_DOWN).intValue();

            if(getActiveUser().getActualBonus().compareTo(new BigDecimal(3.5)) == -1){
                newMultiplier = getActiveUser().getActualBonus().add(new BigDecimal(0.5));
            }
        }else{
            if(item.getTimePoints() >= 15) {
                pointsToWin = 15;
            }else{
                pointsToWin = item.getTimePoints();
            }
        }

        pointsToWin = pointsToWin * AppController.getmInstance().getExtraMultiplier();

        GregorianCalendar dateTaken = new GregorianCalendar();
        String newState = onTime?TimelineItem.STATE_DONE:TimelineItem.STATE_LATE;

        if(getTimelineDataSource().markMedicineTaken(pointsToWin,newState,item.getId(),dateTaken)){
            if(onTime){
                if(item.getMedicine() != null) {
                    Utils.deleteAlarmForTimelineItem(item, this);
                }

                Utils.createNavigationAction(getString(R.string.take_med));
            }else{
                Utils.createNavigationAction(getString(R.string.take_med_late));
            }

            item.markMedicineTaken(pointsToWin,newState,dateTaken);

            getMyBus().send(new MedicineTakenEvent(onTime, item, getActiveUser().getActualBonus(), positionAdapter,item, pointsToWin));

            userWinPoints(pointsToWin, newMultiplier, fragmentManager, new OkErrorListener() {
                @Override
                public void ok() {

                    updateUserStats(true);
                    listener.ok();
                }

                @Override
                public void error() {
                    listener.ok();
                }
            });
        }else{
            listener.error();
        }
    }

    public int getExtraMultiplier(){
        int extraM = 1;

        if(getActiveUser() != null && getActiveUser().getUserProvidedIdDate()!=null){
            GregorianCalendar endBonusDate = new GregorianCalendar();
            endBonusDate.setTimeInMillis(getActiveUser().getUserProvidedIdDate().getTimeInMillis());
            endBonusDate.add(Calendar.DAY_OF_MONTH,14);

            if((new GregorianCalendar()).before(endBonusDate)){
                extraM = 3;
            }
        }

        return extraM;
    }

    public void userWinPoints(int pointsToWin, BigDecimal newMultiplierUsed, FragmentManager fragmentManager, OkErrorListener listener) {
        int newUserPoints = getActiveUser().getUserPoints() + pointsToWin;

        if(getUserDataSource().updateUserPoints(getActiveUser().getId(), newUserPoints,newMultiplierUsed)){
            getActiveUser().setUserPoints(newUserPoints);

            if(newMultiplierUsed != null) {
                getActiveUser().setActualBonus(newMultiplierUsed);
            }

            verifyUserPoints();

            verifyUserBadgePointsWin(fragmentManager, listener);

            getMyBus().send(new UserNewPoints());

            AppController.getmInstance().forceSyncManual();
        }else{
            listener.error();
        }
    }

    public Level getNextLevel(int actualLevel){
        Level nextLevel = AppController.getmInstance().getListLevels().get(actualLevel+1);

        if(nextLevel == null){
            nextLevel = AppController.getmInstance().getListLevels().get(AppController.getmInstance().getListLevels().size());
        }
        return nextLevel;
    }

    public Level getLevel(int actualLevel){
        Level level = AppController.getmInstance().getListLevels().get(actualLevel);

        if(level == null){
            level = AppController.getmInstance().getListLevels().get(AppController.getmInstance().getListLevels().size());
        }

        return level;
    }

    /**
     * Verify if actual points changes user level and update level if changed
     */
    public void verifyUserPoints(){
        Level aux = null;
        Level le;
        for(int i=0;i< getListLevels().size(); i++){
            le = getListLevels().get(i+1);

            //Log.d("AUX",le.getLevel()+" - " + le.getPoints());

            if(getActiveUser().getUserPoints()<le.getPoints()){
                break;
            }else{
                aux=le;
            }
        }

        if(aux == null){
            aux = AppController.getmInstance().getListLevels().get(AppController.getmInstance().getListLevels().size());
        }

        if(getUserDataSource().updateUserLevel(getActiveUser().getId(),aux.getLevel())) {
            getActiveUser().setLevel(aux.getLevel());
        }

        /*
        Level nextLevel = getNextLevel(getActiveUser().getLevel());

        if(getActiveUser().getUserPoints() >= nextLevel.getPoints()){
            if(getUserDataSource().updateUserLevel(getActiveUser().getId(),nextLevel.getLevel())) {
                getActiveUser().setLevel(nextLevel.getLevel());
            }
        }*/

    }

    private void verifyUserBadgePointsWin(FragmentManager fragmentManager, final OkErrorListener listener) {
        int userP = getActiveUser().getUserPoints();
        ArrayList auxUB = getActiveUser().getUserBadges();

        Badge win = BadgesAux.verifyWinBagdeEight(userP,auxUB);
        if(win==null){
            win = BadgesAux.verifyWinBagdeNine(userP,auxUB);
            if(win==null){
                win = BadgesAux.verifyWinBagdeTen(userP,auxUB);
                if(win==null){
                    win = BadgesAux.verifyWinBagdeEleven(userP,auxUB);
                }
            }
        }

        if(win != null){
            UserBadge newUserBadge = createNewUserBadge(win);

            if(newUserBadge!=null){
                Utils.showWinBadge(fragmentManager, newUserBadge.getBadge(), new WinBadgeDialog.WinBadgeDialogListener() {
                    @Override
                    public void onDismiss() {
                        listener.ok();
                    }
                });
            }else{
                listener.ok();
            }
        }else{
            listener.ok();
        }
    }

    public void updateUserStats(boolean notify){
        Long userId = getActiveUser().getId();

        BigDecimal statsGlobal = getTimelineDataSource().getStatsGlobal(userId);
        BigDecimal statsLastMonth = getTimelineDataSource().getStatsLastMonth(userId);
        BigDecimal statsLast7Days = getTimelineDataSource().getStatsLast7Days(userId);

        if(getUserDataSource().updateUserStats(userId,statsGlobal, statsLastMonth, statsLast7Days)){
            getActiveUser().setStatsEver(statsGlobal);
            getActiveUser().setStatsMonth(statsLastMonth);
            getActiveUser().setStatsWeek(statsLast7Days);

            if(notify) {
                getMyBus().send(new UserStatsModifiedEvent());
            }

            AppController.getmInstance().forceSyncManual();
        }
    }

    public UserBadge createNewUserBadge(Badge win) {
        GregorianCalendar today = new GregorianCalendar();
        UserBadge newUserBadge = new UserBadge(today, Utils.getWeekNumber(today), win);

        if(AppController.getmInstance().getBadgeDataSource().createBadge(AppController.getmInstance().getActiveUser().getId(), newUserBadge, true)){
            getActiveUser().addBadge(newUserBadge);
            AppController.getmInstance().getMyBus().send(new UserBadgeWon());


            AppController.getmInstance().forceSyncManual();
        }else{
            newUserBadge = null;
        }

        return newUserBadge;
    }

    public void createReview(final String uidOwner, String reviewsNid, final Review auxR, Context context, final FragmentManager fragmentManager, final OkErrorListener listener) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(context);

        APIInspirers.createReview(reviewsNid, auxR, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();
                auxR.setNid(response.optString("nid"));
                int pointToWin = 10 * AppController.getmInstance().getExtraMultiplier();

                getActiveUser().updateReviewsInfo(auxR);
                updateUserReviewsInfo(getActiveUser().getId(),getActiveUser().getTotalRating(),getActiveUser().getMediaAvl());

                userWinPoints(pointToWin,null,fragmentManager,listener);

                getMyBus().send(new ReviewCreatedEvent(auxR,uidOwner));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                listener.error();
            }
        });
    }

    public boolean updateUserReviewsInfo(Long id, int totalRating, BigDecimal mediaAvl) {
        return getUserDataSource().updateReviewInfoNoSync(id,totalRating,mediaAvl.doubleValue());
    }

    public void acceptNewWarrior(com.bloomidea.inspirers.model.Request request, final Context context, final FragmentManager fragmentManager, final OkErrorListener listener){
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(context);
        APIInspirers.acceptWarrior(request.getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();

                if(getUserDataSource().updateUserNumGodchilds(getActiveUser().getId(), getActiveUser().getNumGodChilds()+1)){
                    getActiveUser().acceptedNewWarrior();

                    getMyBus().send(new NumGodchildsUpdated());

                    verifyUserBadgeWarriorsWin(fragmentManager, listener);


                    AppController.getmInstance().forceSyncManual();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                listener.error();
            }
        });
    }

    public void updateUserTotalGodchilds(int total, FragmentManager fragmentManager, OkErrorListener listener){
        if(getUserDataSource().updateUserNumGodchilds(getActiveUser().getId(), total)){
            getActiveUser().setNumGodChilds(total);


            verifyUserBadgeWarriorsWin(fragmentManager, listener);


            AppController.getmInstance().forceSyncManual();
        }
    }

    private void verifyUserBadgeWarriorsWin(FragmentManager fragmentManager, final OkErrorListener listener) {
        int userTotalGodChilds = getActiveUser().getNumGodChilds();
        ArrayList auxUB = getActiveUser().getUserBadges();

        Badge win = BadgesAux.verifyWinBadgeFour(userTotalGodChilds,auxUB);
        if(win==null){
            win = BadgesAux.verifyWinBagdeFive(userTotalGodChilds,auxUB);
        /*    if(win==null){
                win = BadgesAux.verifyWinBagdeTen(userP,auxUB);
                if(win==null){
                    win = BadgesAux.verifyWinBagdeEleven(userP,auxUB);
                }
            }*/
        }

        if(win != null){
            UserBadge newUserBadge = createNewUserBadge(win);

            if(newUserBadge!=null){
                Utils.showWinBadge(fragmentManager, newUserBadge.getBadge(), new WinBadgeDialog.WinBadgeDialogListener() {
                    @Override
                    public void onDismiss() {
                        listener.ok();
                    }
                });
            }else{
                listener.ok();
            }
        }else{
            listener.ok();
        }
    }

    public void removeGodchild() {
        if(getUserDataSource().updateUserNumGodchilds(getActiveUser().getId(), getActiveUser().getNumGodChilds()-1)){
            getActiveUser().setNumGodChilds(getActiveUser().getNumGodChilds()-1);


            AppController.getmInstance().forceSyncManual();
        }
    }

    public boolean updateUserInfo(String name,String userProvidedId, String countryName, String countryFlagUrl,String countryIso, Bitmap newPicture, ArrayList<String> newLanguages, ArrayList<String> newHobbies) {
        GregorianCalendar dateAux = getUserProvidedIdDate(userProvidedId, getActiveUser());

        boolean ok = getUserDataSource().updateUserInfo(getActiveUser().getId(),name,userProvidedId,dateAux,countryName,countryFlagUrl,countryIso,newPicture,newLanguages,newHobbies);

        if(ok){
            Utils.createNavigationAction(getString(R.string.change_perfil_action));

            getActiveUser().setFirstLogin(false);
            getActiveUser().setUserName(name);
            getActiveUser().setUserProvidedId(userProvidedId,dateAux);

            if(countryName!=null) {
                getActiveUser().setCountryName(countryName);
                getActiveUser().setIsoCountry(countryIso);
                getActiveUser().setCountryFlagUrl(countryFlagUrl);
                getActiveUser().setCountryFlag(null);
            }

            if(newPicture!=null) {
                getActiveUser().setPictureUrl("");
                getActiveUser().setPicture(newPicture);
            }

            String[] arraylang = new String[newLanguages.size()];
            int i = 0;
            for(String auxL : newLanguages) {
                arraylang[i] = auxL;
                i++;
            }

            getActiveUser().setLanguages(arraylang);

            String[] arrayhobbies = new String[newHobbies.size()];
            arrayhobbies = newHobbies.toArray(arrayhobbies);

            getActiveUser().setHobbies(arrayhobbies);

            getMyBus().send(new UserInfoUpdated());


            AppController.getmInstance().forceSyncManual();
        }

        return ok;
    }

    private GregorianCalendar getUserProvidedIdDate(String userStudyID, User user){
        GregorianCalendar dateAux = user.getUserProvidedIdDate();

        if(dateAux == null && userStudyID!=null && !userStudyID.isEmpty()){
            dateAux = new GregorianCalendar();
        }

        return dateAux;
    }

    public boolean updateUserStudyID(String userStudyID) {
        GregorianCalendar dateAux = getUserProvidedIdDate(userStudyID, getActiveUser());

        boolean ok = getUserDataSource().updateUserStudyID(getActiveUser().getId(),userStudyID, dateAux);

        if(ok){
            //Utils.createNavigationAction(getString(R.string.change_perfil_action));

            getActiveUser().setUserProvidedId(userStudyID, dateAux);

            //getMyBus().send(new UserInfoUpdated());


            //AppController.getmInstance().forceSyncManual();
        }

        return ok;
    }

    public boolean deleteMedicine(UserMedicine medicineToDelete) {
        boolean ok = AppController.getmInstance().getMedicineDataSource().deleteMedicine(AppController.getmInstance().getActiveUser().getId(), medicineToDelete);

        if(ok && medicineToDelete instanceof UserNormalMedicine){
            Utils.deleteAlarmForUserNormalMedicine((UserNormalMedicine) medicineToDelete,this);
        }

        return ok;
    }

    public boolean activateMedicine(UserMedicine medicineToActive) {
        boolean ok = AppController.getmInstance().getMedicineDataSource().activateMedicine(AppController.getmInstance().getActiveUser().getId(), medicineToActive);

        if(ok && medicineToActive instanceof UserNormalMedicine){
            Utils.scheduleAlarmForUserNormalMedicine((UserNormalMedicine) medicineToActive,this);
        }

        return ok;
    }

    public void updateUserNotifications(Activity context, boolean active) {
        if(AppController.getmInstance().getUserDataSource().updateNotifications(getActiveUser().getId(), active)){
            getActiveUser().setPushOn(active);

            if(active) {
                Utils.getRegId(context);
                Utils.createAllUserAlarms(context);
            }else{
                APIInspirers.unregisterUserNotifications(getActiveUser().getNotificationsToken(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateNotificationToken("");
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Utils.deleteAllUserAlarms(context);
            }
        }
    }

    public void updateNotificationToken(String token) {
        AppController.getmInstance().getUserDataSource().updateNotificationsToken(getActiveUser().getId(),token);
        getActiveUser().setNotificationsToken(token);
    }


    public void forceSyncManual(){
        Log.d("Sync Manual","Forced");

        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(), AUTHORITY, settingsBundle);
    }


    public void checkUserPolls(){
        final User auxActiveUser = getActiveUser();
        if(auxActiveUser != null){
            getPolls(new TreeMapListener() {
                @Override
                public void onResponse(TreeMap response) {
                    if(response!=null && !response.isEmpty()){
                        TreeMap<String,Poll> auxPools = response;
                        for(Poll p: auxPools.values()){
                            TimelineItem pollEx = getTimelineDataSource().getTimeLineItemPollNotComplete(auxActiveUser.getId(), p.getPoolType());

                            if(pollEx != null){
                                GregorianCalendar date = new GregorianCalendar();
                                date.set(Calendar.HOUR_OF_DAY,0);
                                date.set(Calendar.MINUTE,0);
                                date.set(Calendar.SECOND,0);
                                date.set(Calendar.MILLISECOND,0);

                                GregorianCalendar startTime = new GregorianCalendar();
                                //startTime.set(Calendar.HOUR_OF_DAY, 0);

                                if(p.getPoolType().equals(Poll.POLL_TYPE_DAILY)){
                                    startTime.set(Calendar.HOUR_OF_DAY, POLL_DAILY_INIT_TIME);
                                }else {
                                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                                }

                                startTime.set(Calendar.MINUTE,0);
                                startTime.set(Calendar.SECOND,0);
                                startTime.set(Calendar.MILLISECOND,0);

                                GregorianCalendar endTime = new GregorianCalendar();
                                endTime.set(Calendar.HOUR_OF_DAY,23);
                                endTime.set(Calendar.MINUTE,59);
                                endTime.set(Calendar.SECOND,59);
                                endTime.set(Calendar.MILLISECOND,0);

                                getTimelineDataSource().updatePollTimelineTime(pollEx.getId(), date, startTime, endTime);

                            }else{
                                int timeToRemove;

                                if(p.getPoolType().equals(Poll.POLL_TYPE_CARAT)){
                                    timeToRemove = getActiveUser().getTimeCARAT();
                                }else if(p.getPoolType().equals(Poll.POLL_TYPE_DAILY)){
                                    timeToRemove = -1;
                                }else{
                                    timeToRemove = -7;
                                }

                                GregorianCalendar lastDate = new GregorianCalendar();
                                lastDate.add(Calendar.DAY_OF_YEAR,timeToRemove);

                                if (getTimelineDataSource().getTimeLineItemPollNotCompleteLast(auxActiveUser.getId(), p.getPoolType(), lastDate) == null){
                                    GregorianCalendar date = new GregorianCalendar();
                                    date.set(Calendar.HOUR_OF_DAY,0);
                                    date.set(Calendar.MINUTE,0);
                                    date.set(Calendar.SECOND,0);

                                    GregorianCalendar startTime = new GregorianCalendar();

                                    if(p.getPoolType().equals(Poll.POLL_TYPE_DAILY)){
                                        startTime.set(Calendar.HOUR_OF_DAY, POLL_DAILY_INIT_TIME);
                                    }else {
                                        startTime.set(Calendar.HOUR_OF_DAY, 0);
                                    }

                                    startTime.set(Calendar.MINUTE,0);
                                    startTime.set(Calendar.SECOND,0);

                                    GregorianCalendar endTime = new GregorianCalendar();
                                    endTime.set(Calendar.HOUR_OF_DAY,23);
                                    endTime.set(Calendar.MINUTE,59);
                                    endTime.set(Calendar.SECOND,59);

                                    getTimelineDataSource().createTimelinePool(auxActiveUser.getId(), p, date, startTime, endTime);
                                }
                            }
                        }

                        getMyBus().send(new PollsUpdated());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    private boolean verifyAllAnswered(Poll poll) {
        boolean allAns = true;

        for (Question q : poll.getListQuestions()){
            boolean hasH = false;
            if(q instanceof QuestionMultipleChoice) {
                QuestionMultipleChoice qMC = (QuestionMultipleChoice) q;
                for (Answer a : qMC.getListAnswers()) {
                    if (a.isSelected()) {
                        hasH = true;
                        break;
                    }
                }
            }else if(q instanceof QuestionSlider){
                hasH = ((QuestionSlider) q).isUserSelected();
            }else{
                //QuestionYesNo
                hasH = ((QuestionYesNo) q).isYes() != null;
            }

            if(!hasH){
                allAns = false;
                break;
            }
        }
        return allAns;
    }

    public void answerPool(final Activity context, Poll pollWithAnswers,final Long timelineItemId,final FragmentManager fragmentManager, final OkErrorPollListener listener) {
        if(verifyAllAnswered(pollWithAnswers)) {
            final long insertId = getPollDataSource().createAnswerPoll(pollWithAnswers,getActiveUser().getId(),true);
            if(insertId != -1){
                TimelineItem item = getTimelineDataSource().getTimelineItem(timelineItemId);

                int pointToWin = item.getTimePoints() * AppController.getmInstance().getExtraMultiplier();

                getTimelineDataSource().setPollAnswered(pointToWin, timelineItemId, new GregorianCalendar());
                getMyBus().send(new PollAnswered(timelineItemId));

                userWinPoints(pointToWin, null, fragmentManager, new OkErrorListener() {
                    @Override
                    public void ok() {
                        listener.ok(insertId);
                    }

                    @Override
                    public void error() {
                        listener.ok(insertId);
                    }
                });
            }else{
                listener.error();
            }
        }else{
            Toast.makeText(context,R.string.not_all_answered,Toast.LENGTH_SHORT).show();
            listener.error();
        }
    }

    /*
    public void answerPool(final Activity context, Poll poll,final Long timelineItemId,final FragmentManager fragmentManager, final OkErrorListener listener) {
        if(verifyAllAnswered(poll)) {
            if (Utils.isOnline(context, true, fragmentManager)) {
                final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(context);
                APIInspirers.sendMyAnswerPool(AppController.getmInstance().getActiveUser().getUid(), poll, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("ok", "ok");
                        ringProgressDialogNoText.dismiss();

                        TimelineItem item = getTimelineDataSource().getTimelineItem(timelineItemId);

                        getTimelineDataSource().setPollAnswered(item.getTimePoints(), timelineItemId, new GregorianCalendar());
                        getMyBus().send(new PollAnswered(timelineItemId));

                        userWinPoints(item.getTimePoints(), null, fragmentManager, new OkErrorListener() {
                            @Override
                            public void ok() {
                                listener.ok();
                            }

                            @Override
                            public void error() {
                                listener.ok();
                            }
                        });
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialogNoText.dismiss();

                        Toast.makeText(context, R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
                        listener.error();
                    }
                });
            }else{
                listener.error();
            }
        }else{
            Toast.makeText(context,R.string.not_all_answered,Toast.LENGTH_SHORT).show();
            listener.error();
        }
    }*/

    public void updateUserCaratTime(int selectedCARATOptionTime) {
        if(getUserDataSource().updateUserCaratTime(getActiveUser().getId(),selectedCARATOptionTime)){
            getActiveUser().setTimeCARAT(selectedCARATOptionTime);
        }
    }

    public void updateWeekPollAnswer(boolean weekPollAnswer){
        if(getUserDataSource().updateWeekPollAnswer(getActiveUser().getId(),weekPollAnswer)){
            getActiveUser().setWeekPollAnswer(weekPollAnswer);
        }
    }

    public Poll getPollModel(String pollType){
        Poll aux;
        if(listPolls!=null){
            aux = listPolls.get(pollType);
        }else{
            TreeMap<String,Poll> auxPollsMemory = getPollDataSource().getPolls();
            aux = auxPollsMemory.get(pollType);
        }

        return aux;
    }

    public TreeMap<String,Poll> getPollModelsBD(){
        return getPollDataSource().getPolls();
    }

    public String getRestLang() {
        return restLang;
    }

    public void updateAllTimelineItemLocation(Location location) {
        if(location != null && !timelineItemsNeedLocation.isEmpty()) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            if(AppController.getmInstance().getTimelineDataSource().updateLatitudeLongitude(timelineItemsNeedLocation,lat,lon)){
                timelineItemsNeedLocation.clear();
            }
        }

        forceSyncManual();

        Log.d("LOCATION","LOCATION: " + location.toString());
    }

    public void addTimelineItemNeedLocation(TimelineItem auxTimelineItem) {
        timelineItemsNeedLocation.add(auxTimelineItem);
    }

    public void resetDatabaseUser(Long id) {
        //Utils.deleteAllUserAlarms(this);

        getTimelineDataSource().deleteAllForUser(id);
        getPollDataSource().deleteAllMyPollForUser(id);
        getBadgeDataSource().deleteAllForUser(id);
        getMedicineDataSource().deleteAllForUser(id);
        getUserDataSource().deleteAllForUser(id);
    }


    public boolean updateUserAcceptedTerms() {
        boolean ok = getUserDataSource().updateUserAcceptedTerms(getActiveUser().getId());

        if(ok){
            getActiveUser().setAcceptedTerms(true);

            AppController.getmInstance().forceSyncManual();
        }

        return ok;
    }

    public boolean updateUserTermsOff() {
        boolean ok = getUserDataSource().updateUserTermsOff(getActiveUser().getId());

        if(ok){
            getActiveUser().setTermsOff(true);

            AppController.getmInstance().forceSyncManual();
        }

        return ok;
    }
}