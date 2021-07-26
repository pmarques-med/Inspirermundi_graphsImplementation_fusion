package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.database.InspirersContract.Badges;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.sync.InnerSyncBadges;

import java.util.ArrayList;
import java.util.GregorianCalendar;


/**
 * Created by michellobato on 17/03/17.
 */

public class BadgeDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { Badges._ID,     //0
            Badges.COLUMN_BADGES_USER_ID,           //1
            Badges.COLUMN_BADGES_IDENT,             //2
            Badges.COLUMN_BADGES_IS_SYNC,           //3
            Badges.COLUMN_BADGES_DATE_WON,          //4
            Badges.COLUMN_BADGES_WEEK_NUMBER};      //5

    public BadgeDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createBadges(Long userId, ArrayList<UserBadge> badges, boolean toSync, boolean createTimelineItem) {
        database.beginTransaction();

        boolean ok = true;

        for(UserBadge userBadge : badges) {
            ContentValues values = new ContentValues();

            values.put(Badges.COLUMN_BADGES_USER_ID, userId);
            values.put(Badges.COLUMN_BADGES_IDENT, userBadge.getBadge().getCode());
            values.put(Badges.COLUMN_BADGES_IS_SYNC, toSync);
            values.put(Badges.COLUMN_BADGES_DATE_WON, userBadge.getDate().getTimeInMillis());
            values.put(Badges.COLUMN_BADGES_WEEK_NUMBER, userBadge.getWeekNumber());

            long insertId = database.insert(Badges.TABLE_BADGES, null, values);

            ok = insertId != -1;

            if (ok) {
                userBadge.setId(insertId);

                if(ok && createTimelineItem) {
                    ok = AppController.getmInstance().getTimelineDataSource().createTimelineBadge(userId, userBadge);
                }

                if(!ok){
                    break;
                }
            }else{
                break;
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    public boolean createBadge(Long userId, UserBadge newBadge, boolean toSync) {
        database.beginTransaction();


        ContentValues values = new ContentValues();

        values.put(Badges.COLUMN_BADGES_USER_ID, userId);
        values.put(Badges.COLUMN_BADGES_IDENT, newBadge.getBadge().getCode());
        values.put(Badges.COLUMN_BADGES_IS_SYNC, toSync);
        values.put(Badges.COLUMN_BADGES_DATE_WON, newBadge.getDate().getTimeInMillis());
        values.put(Badges.COLUMN_BADGES_WEEK_NUMBER, newBadge.getWeekNumber());

        long insertId = database.insert(Badges.TABLE_BADGES, null, values);

        boolean ok = insertId != -1;

        if (ok) {
            newBadge.setId(insertId);

            ok = AppController.getmInstance().getTimelineDataSource().createTimelineBadge(userId,newBadge);
        }


        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    private UserBadge cursorToUserBadge(Cursor cursor){
        GregorianCalendar aux = new GregorianCalendar();
        aux.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Badges.COLUMN_BADGES_DATE_WON)));

        Badge badgeAux = AppController.getmInstance().getBadge(cursor.getString(cursor.getColumnIndex(Badges.COLUMN_BADGES_IDENT)));

        return new UserBadge(cursor.getLong(cursor.getColumnIndex(Badges._ID)),
                aux,
                cursor.getString(cursor.getColumnIndex(Badges.COLUMN_BADGES_WEEK_NUMBER)),
                badgeAux);
    }

    public ArrayList<UserBadge> getUserBadges(Long userId){
        ArrayList<UserBadge> list = new ArrayList<>();

        Cursor cursor = database.query(Badges.TABLE_BADGES,
                allColumns, Badges.COLUMN_BADGES_USER_ID+" = ?",new String[]{""+userId}, null, null,Badges.COLUMN_BADGES_DATE_WON + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToUserBadge(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public UserBadge getUserBadge(Long badgeId){
        UserBadge userBadge = null;

        Cursor cursor = database.query(Badges.TABLE_BADGES,
                allColumns, Badges._ID+" = ?",new String[]{""+badgeId}, null, null,null);


        //cursor.moveToFirst();

        if(cursor.moveToFirst()) {
            userBadge = cursorToUserBadge(cursor);
        }

        cursor.close();

        return userBadge;
    }

    public ArrayList<InnerSyncBadges> getBadgesNeedSync(long userToSyncID) {
        ArrayList<InnerSyncBadges> list = new ArrayList<>();

        Cursor cursor = database.query(Badges.TABLE_BADGES,
                allColumns, Badges.COLUMN_BADGES_IS_SYNC+" = 1 AND "+Badges.COLUMN_BADGES_USER_ID+" = ?",new String[]{""+userToSyncID}, null, null,Badges.COLUMN_BADGES_DATE_WON + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(new InnerSyncBadges(
                    AppController.getmInstance().getUserDataSource().getUserUidFromId(cursor.getLong(cursor.getColumnIndex(Badges.COLUMN_BADGES_USER_ID))),
                    cursorToUserBadge(cursor)));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean setBadgeSynced(Long badgeId) {
        ContentValues args = new ContentValues();
        args.put(Badges.COLUMN_BADGES_IS_SYNC,0);

        return database.update(Badges.TABLE_BADGES, args, Badges._ID + " = ? ", new String[]{""+badgeId}) > 0;
    }

    public void deleteAllForUser(Long userId) {
        database.execSQL("DELETE FROM "+ Badges.TABLE_BADGES+" WHERE "+Badges.COLUMN_BADGES_USER_ID + " = "+userId);
    }
}
