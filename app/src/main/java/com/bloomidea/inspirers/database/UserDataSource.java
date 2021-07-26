package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Country;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.sync.InnerSyncUser;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static com.bloomidea.inspirers.database.InspirersContract.Users;

/**
 * Created by michellobato on 16/03/17.
 */

public class UserDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { Users._ID,      //0
            Users.COLUMN_USER_UID,                  //1
            Users.COLUMN_USER_TOKEN,                //2
            Users.COLUMN_USER_START_DATE,           //3
            Users.COLUMN_USER_SESSION_NAME,         //4
            Users.COLUMN_USER_SESSION_ID,           //5
            Users.COLUMN_USER_PUSH_ON,              //6
            Users.COLUMN_USER_POINTS,               //7
            Users.COLUMN_USER_PICTURE_URL,          //8
            Users.COLUMN_USER_PICTURE,              //9
            Users.COLUMN_USER_PASSWORD,             //10
            Users.COLUMN_USER_NUM_GODCHILD,         //11
            Users.COLUMN_USER_LOCAL_NOTIFICATION,   //12
            Users.COLUMN_USER_NAME,                 //13
            Users.COLUMN_USER_MEDIA_AVL,            //14
            Users.COLUMN_USER_LEVEL,                //15
            Users.COLUMN_USER_ISO_COUNTRY,          //16
            Users.COLUMN_USER_IS_ACTIVE,            //17
            Users.COLUMN_USER_HOBBIES,              //18
            Users.COLUMN_USER_LANGUAGES,            //19
            Users.COLUMN_USER_FIRST_LOGIN,          //20
            Users.COLUMN_USER_EMAIL,                //21
            Users.COLUMN_USER_COUNTRY_NAME,         //22
            Users.COLUMN_USER_COUNTRY_FLAG_URL,     //23
            Users.COLUMN_USER_COUNTRY_FLAG,         //24
            Users.COLUMN_USER_TOTAL_RATING,         //25
            Users.COLUMN_USER_STATS_WEEK,           //26
            Users.COLUMN_USER_STATS_MONTH,          //27
            Users.COLUMN_USER_STATS_EVER,           //28
            Users.COLUMN_USER_ACTUAL_BONUS,         //29
            Users.COLUMN_USER_NID_PROFILE,          //30
            Users.COLUMN_USER_NOTIF_TOKEN,          //31
            Users.COLUMN_USER_SYNC_PIC,             //32
            Users.COLUMN_USER_CARAT_TIME,           //33
            Users.COLUMN_USER_WEEK_POLL_ANSWER,     //34
            Users.COLUMN_USER_PROVIDED_ID,          //35
            Users.COLUMN_USER_DEVICE_ID,            //36
            Users.COLUMN_USER_PROVIDED_ID_DATE,     //37
            Users.COLUMN_USER_ACCEPTED_TERMS,       //38
            Users.COLUMN_USER_TERMS_OFF};           //39

    public UserDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createUser(User user, boolean createtimelineitem) {
        ContentValues values = new ContentValues();

        values.put(Users.COLUMN_USER_UID, user.getUid());
        values.put(Users.COLUMN_USER_TOKEN, user.getToken());
        values.put(Users.COLUMN_USER_START_DATE, user.getStartDate()!=null?user.getStartDate().getTimeInMillis():0);
        values.put(Users.COLUMN_USER_SESSION_NAME, user.getSessionName());
        values.put(Users.COLUMN_USER_SESSION_ID, user.getSessionId());
        values.put(Users.COLUMN_USER_PUSH_ON, user.isPushOn());
        values.put(Users.COLUMN_USER_POINTS, user.getUserPoints());
        values.put(Users.COLUMN_USER_PICTURE_URL, user.getPictureUrl());
        values.put(Users.COLUMN_USER_PICTURE, Utils.getPictureBlob(user.getPicture()));
        values.put(Users.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(Users.COLUMN_USER_NUM_GODCHILD, user.getNumGodChilds());
        values.put(Users.COLUMN_USER_LOCAL_NOTIFICATION, user.isLocalNotifications());
        values.put(Users.COLUMN_USER_NAME, user.getUserName());
        values.put(Users.COLUMN_USER_MEDIA_AVL, user.getMediaAvl().doubleValue());
        values.put(Users.COLUMN_USER_LEVEL, user.getLevel());
        values.put(Users.COLUMN_USER_ISO_COUNTRY, user.getIsoCountry());
        values.put(Users.COLUMN_USER_IS_ACTIVE, user.isActive());
        values.put(Users.COLUMN_USER_HOBBIES, user.getHobbiesStringWithSeparator(InspirersJSONParser.HOBBIES_SEPARATOR));
        values.put(Users.COLUMN_USER_LANGUAGES, user.getLanguagesStringWithSeparator(InspirersJSONParser.LANGUAGE_SEPARATOR));
        values.put(Users.COLUMN_USER_FIRST_LOGIN, user.isFirstLogin());
        values.put(Users.COLUMN_USER_EMAIL, user.getEmail());
        values.put(Users.COLUMN_USER_COUNTRY_NAME, user.getCountryName());
        values.put(Users.COLUMN_USER_COUNTRY_FLAG_URL, user.getCountryFlagUrl());
        values.put(Users.COLUMN_USER_COUNTRY_FLAG, Utils.getPictureBlob(user.getCountryFlag()));
        values.put(Users.COLUMN_USER_TOTAL_RATING, user.getTotalRating());
        values.put(Users.COLUMN_USER_STATS_WEEK, user.getStatsWeek().doubleValue());
        values.put(Users.COLUMN_USER_STATS_MONTH, user.getStatsMonth().doubleValue());
        values.put(Users.COLUMN_USER_STATS_EVER, user.getStatsEver().doubleValue());
        values.put(Users.COLUMN_USER_ACTUAL_BONUS, user.getActualBonus().doubleValue());
        values.put(Users.COLUMN_USER_NID_PROFILE, user.getNidProfile());
        values.put(Users.COLUMN_USER_NOTIF_TOKEN, user.getNotificationsToken());
        values.put(Users.COLUMN_USER_WEEK_POLL_ANSWER, user.getWeekPollAnswer()==null?null:(user.getWeekPollAnswer()?1:0));
        values.put(Users.COLUMN_USER_PROVIDED_ID, user.getUserProvidedId());
        values.put(Users.COLUMN_USER_DEVICE_ID, user.getDeviceId());
        values.put(Users.COLUMN_USER_PROVIDED_ID_DATE, user.getUserProvidedIdDate()!=null?user.getUserProvidedIdDate().getTimeInMillis():-1);
        values.put(Users.COLUMN_USER_ACCEPTED_TERMS, user.isAcceptedTerms());
        values.put(Users.COLUMN_USER_TERMS_OFF, user.isTermsOff());

        long insertId = database.insert(Users.TABLE_USER, null, values);

        boolean ok = insertId!=-1;

        if(ok) {
            user.setId(new Long(insertId));

            if(user.getUserBadges()!=null && !user.getUserBadges().isEmpty()){
                AppController.getmInstance().setUserBadgesNoSync(user.getUserBadges(), createtimelineitem);
            }
        }

        return ok;
    }

    private User cursorToUser(Cursor cursor) {
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Users.COLUMN_USER_START_DATE)));

        byte[] byteArrayPicture = cursor.getBlob(cursor.getColumnIndex(Users.COLUMN_USER_PICTURE));
        Bitmap picture = null;

        if(byteArrayPicture!=null) {
            picture = BitmapFactory.decodeByteArray(byteArrayPicture, 0, byteArrayPicture.length);
        }

        byte[] byteArrayFlag = cursor.getBlob(cursor.getColumnIndex(Users.COLUMN_USER_COUNTRY_FLAG));
        Bitmap countryFlag = null;

        if(byteArrayFlag!=null) {
            countryFlag = BitmapFactory.decodeByteArray(byteArrayFlag, 0, byteArrayFlag.length);
        }

        String[] hobbies = null;
        String hobbiesAux = cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_HOBBIES));

        if(hobbiesAux != null && !hobbiesAux.isEmpty()){
            hobbies = hobbiesAux.split(InspirersJSONParser.HOBBIES_SEPARATOR);
        }

        String[] languages = null;
        String languagesAux = cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_LANGUAGES));

        if(languagesAux != null && !languagesAux.isEmpty()){
            languages = languagesAux.split(InspirersJSONParser.LANGUAGE_SEPARATOR);
        }

        Boolean weekPollAnswer = null;
        if(!cursor.isNull(cursor.getColumnIndex(Users.COLUMN_USER_WEEK_POLL_ANSWER))){
            weekPollAnswer = cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_WEEK_POLL_ANSWER)) == 1;
        }

        long auxL = cursor.getLong(cursor.getColumnIndex(Users.COLUMN_USER_PROVIDED_ID_DATE));
        GregorianCalendar userProvidedIdDate = null;
        if(auxL != -1) {
            userProvidedIdDate = new GregorianCalendar();
            userProvidedIdDate.setTimeInMillis(auxL);
        }

        Long userId = cursor.getLong(cursor.getColumnIndex(Users._ID));

        User user = new User(userId,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_UID)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_TOKEN)),
                startDate,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_SESSION_NAME)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_SESSION_ID)),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_PUSH_ON)) == 1,
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_POINTS)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_PICTURE_URL)),
                picture,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_PASSWORD)),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_NUM_GODCHILD)),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_LOCAL_NOTIFICATION)) == 1,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_NAME)),
                new BigDecimal(cursor.getDouble(cursor.getColumnIndex(Users.COLUMN_USER_MEDIA_AVL))),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_LEVEL)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_ISO_COUNTRY)),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_IS_ACTIVE)) == 1,
                hobbies,
                languages,
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_FIRST_LOGIN)) == 1,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_EMAIL)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_COUNTRY_NAME)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_COUNTRY_FLAG_URL)),
                countryFlag,
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_TOTAL_RATING)),
                new BigDecimal(cursor.getDouble(cursor.getColumnIndex(Users.COLUMN_USER_STATS_WEEK))),
                new BigDecimal(cursor.getDouble(cursor.getColumnIndex(Users.COLUMN_USER_STATS_MONTH))),
                new BigDecimal(cursor.getDouble(cursor.getColumnIndex(Users.COLUMN_USER_STATS_EVER))),
                new BigDecimal(cursor.getDouble(cursor.getColumnIndex(Users.COLUMN_USER_ACTUAL_BONUS))),
                AppController.getmInstance().getBadgeDataSource().getUserBadges(userId),
                false,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_NID_PROFILE)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_NOTIF_TOKEN)),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_CARAT_TIME)),
                weekPollAnswer,
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_PROVIDED_ID)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_DEVICE_ID)),
                userProvidedIdDate,
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_ACCEPTED_TERMS)) == 1,
                0,
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_TERMS_OFF)) == 1);

        return user;
    }

    public User getActiveUser() {

        User user = null;

        Cursor cursor = database.query(Users.TABLE_USER, allColumns, Users.COLUMN_USER_IS_ACTIVE+" = ?",new String[]{"1"}, null, null, null);

        if(cursor.moveToFirst()){
            user = cursorToUser(cursor);
        }

        cursor.close();

        return user;
    }

    public User getUserByUid(String uid) {
        User user = null;

        Cursor cursor = database.query(Users.TABLE_USER, allColumns, Users.COLUMN_USER_UID+" = ?",new String[]{uid}, null, null, null);

        if(cursor.moveToFirst()){
            user = cursorToUser(cursor);
        }

        cursor.close();

        return user;
    }

    public boolean activateUser(String uid, String sessionId, String sessionName, String deviceId) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_IS_ACTIVE,false);

        database.update(Users.TABLE_USER, args, null, null);

        args = new ContentValues();
        args.put(Users.COLUMN_USER_IS_ACTIVE,true);
        args.put(Users.COLUMN_USER_SESSION_ID,sessionId);
        args.put(Users.COLUMN_USER_SESSION_NAME,sessionName);
        args.put(Users.COLUMN_USER_DEVICE_ID,deviceId);

        return database.update(Users.TABLE_USER, args, Users.COLUMN_USER_UID + " = ? ", new String[]{uid}) > 0;
    }

    public boolean updateUserToken(String uid, String token) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_TOKEN,token);

        return database.update(Users.TABLE_USER, args, Users.COLUMN_USER_UID + " = ? ", new String[]{uid}) > 0;
    }

    public boolean updateUserCountry(String uid, Country country) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_COUNTRY_FLAG_URL,country.getFlag());
        args.put(Users.COLUMN_USER_COUNTRY_FLAG, Utils.getPictureBlob(null));
        args.put(Users.COLUMN_USER_COUNTRY_NAME,country.getName());

        return database.update(Users.TABLE_USER, args, Users.COLUMN_USER_UID + " = ? ", new String[]{uid}) > 0;
    }

    public boolean deactivateUser(String uid) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_IS_ACTIVE,0);
        args.put(Users.COLUMN_USER_NOTIF_TOKEN,"");

        return database.update(Users.TABLE_USER, args, Users.COLUMN_USER_UID + " = ? ", new String[]{uid}) > 0;
    }

    public boolean updateUserPicture(String uid, Bitmap aux) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_PICTURE,Utils.getPictureBlob(aux));

        return database.update(Users.TABLE_USER, args, Users.COLUMN_USER_UID + " = ? ", new String[]{uid}) > 0;
    }

    public boolean updateUserPoints(long userId, int newUserPoints, BigDecimal newMultiplier) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_POINTS,newUserPoints);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        if(newMultiplier!=null) {
            args.put(Users.COLUMN_USER_ACTUAL_BONUS,newMultiplier.doubleValue());
        }

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateUserStats(Long userId, BigDecimal statsGlobal, BigDecimal statsLastMonth, BigDecimal statsLast7Days) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_STATS_EVER,statsGlobal.doubleValue());
        args.put(Users.COLUMN_USER_STATS_MONTH,statsLastMonth.doubleValue());
        args.put(Users.COLUMN_USER_STATS_WEEK,statsLast7Days.doubleValue());
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateUserLevel(Long userId, int level) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_LEVEL,level);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateUserNumGodchilds(Long userId, int totalGodchilds) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_NUM_GODCHILD,totalGodchilds);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateUserInfo(Long userId, String name, String userProvidedId, GregorianCalendar userProvidedIdDate, String countryName, String countryFlagUrl,String countryIso,  Bitmap newPicture, ArrayList<String> newLanguages, ArrayList<String> newHobbies) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_NAME, name);
        args.put(Users.COLUMN_USER_PROVIDED_ID, userProvidedId);
        args.put(Users.COLUMN_USER_PROVIDED_ID_DATE, userProvidedIdDate!=null?userProvidedIdDate.getTimeInMillis():-1);
        args.put(Users.COLUMN_USER_FIRST_LOGIN, false);

        if(countryName!=null) {
            args.put(Users.COLUMN_USER_COUNTRY_NAME, countryName);
            args.put(Users.COLUMN_USER_COUNTRY_FLAG_URL, countryFlagUrl);
            args.put(Users.COLUMN_USER_ISO_COUNTRY, countryIso);
            args.putNull(Users.COLUMN_USER_COUNTRY_FLAG);
        }

        if(newPicture!=null) {
            args.put(Users.COLUMN_USER_PICTURE_URL, "");
            args.put(Users.COLUMN_USER_PICTURE, Utils.getPictureBlob(newPicture));
            args.put(Users.COLUMN_USER_SYNC_PIC, 1);
        }

        String l = "";

        if(newLanguages!=null && newLanguages.size()!=0){
            for(String s : newLanguages){
                l+=InspirersJSONParser.LANGUAGE_SEPARATOR+s;
            }

            l = l.substring(1);
        }

        args.put(Users.COLUMN_USER_LANGUAGES,l);

        String h = "";

        if(newHobbies!=null && newHobbies.size()!=0){
            for(String s : newHobbies){
                h+=InspirersJSONParser.HOBBIES_SEPARATOR+s;
            }

            h = h.substring(1);
        }

        args.put(Users.COLUMN_USER_HOBBIES,h);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateUserStudyID(Long userId, String userProvidedId, GregorianCalendar userProvidedIdDate) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_PROVIDED_ID, userProvidedId);
        args.put(Users.COLUMN_USER_PROVIDED_ID_DATE, (userProvidedIdDate!=null)?userProvidedIdDate.getTimeInMillis():-1);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateNotifications(Long userId, boolean active) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_PUSH_ON, active);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateNotificationsToken(Long userId, String token) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_NOTIF_TOKEN, token);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public ArrayList<InnerSyncUser> getUsersNeedSync(long userToSyncID) {
        ArrayList<InnerSyncUser> list = new ArrayList<>();
        Cursor cursor = database.query(Users.TABLE_USER, allColumns, Users.COLUMN_USER_NEED_SYNC+" = ? AND "+Users._ID +" = ?",new String[]{"1",""+userToSyncID}, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Log.d("Teste",""+cursor.getCount());
            list.add(new InnerSyncUser(cursorToUser(cursor),cursor.getInt(cursor.getColumnIndex(Users.COLUMN_USER_SYNC_PIC))==1));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean setUserInfoSynced(Long userId, String pictureUrl) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_NEED_SYNC,0);

        if(pictureUrl!=null && !pictureUrl.isEmpty()){
            args.put(Users.COLUMN_USER_PICTURE_URL,pictureUrl);
            args.put(Users.COLUMN_USER_SYNC_PIC,0);
        }

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public String getUserUidFromId(Long userId) {
        String uid = null;

        Cursor cursor = database.query(Users.TABLE_USER, allColumns, Users._ID+" = ?",new String[]{""+userId}, null, null, null);

        if(cursor.moveToFirst()){
            uid = cursor.getString(cursor.getColumnIndex(Users.COLUMN_USER_UID));
        }

        cursor.close();

        return uid;
    }

    public boolean updateUserCaratTime(Long userId, int caratTime) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_CARAT_TIME,caratTime);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean resetActualBonus(Long userId) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_ACTUAL_BONUS,1);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateReviewInfoNoSync(Long userId, int total, double avr) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_TOTAL_RATING,total);
        args.put(Users.COLUMN_USER_MEDIA_AVL,avr);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateWeekPollAnswer(Long userId, boolean answer) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_WEEK_POLL_ANSWER,answer?1:0);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public void deleteAllForUser(Long userId) {
        database.execSQL("DELETE FROM "+ Users.TABLE_USER+" WHERE "+Users._ID + " = "+userId);
    }

    public boolean updateUserAcceptedTerms(Long userId) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_ACCEPTED_TERMS, true);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }

    public boolean updateUserTermsOff(Long userId) {
        ContentValues args = new ContentValues();
        args.put(Users.COLUMN_USER_TERMS_OFF, true);
        args.put(Users.COLUMN_USER_NEED_SYNC,1);

        return database.update(Users.TABLE_USER, args, Users._ID + " = ? ", new String[]{""+userId}) > 0;
    }
}
