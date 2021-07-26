package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.model.NavAux;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 26/04/17.
 */

public class NavigationDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { InspirersContract.Navigation._ID,       //0
            InspirersContract.Navigation.COLUMN_NAVIGATION_USER_ID,         //1
            InspirersContract.Navigation.COLUMN_NAVIGATION_DESCRIPTION,     //2
            InspirersContract.Navigation.COLUMN_NAVIGATION_START_TIME,      //3
            InspirersContract.Navigation.COLUMN_NAVIGATION_END_TIME,        //4
            InspirersContract.Navigation.COLUMN_NAVIGATION_TYPE};           //5

    public NavigationDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createNavigation(NavAux navigation){
        ContentValues values = new ContentValues();

        values.put(InspirersContract.Navigation.COLUMN_NAVIGATION_USER_ID, navigation.getUserId());
        values.put(InspirersContract.Navigation.COLUMN_NAVIGATION_DESCRIPTION, navigation.getDescription());
        values.put(InspirersContract.Navigation.COLUMN_NAVIGATION_START_TIME, navigation.getStartTime().getTimeInMillis());
        values.put(InspirersContract.Navigation.COLUMN_NAVIGATION_TYPE, navigation.getType());

        long insertId = database.insert(InspirersContract.Navigation.TABLE_NAVIGATION, null, values);

        navigation.setId(insertId);

        boolean ok = insertId != -1;

        return ok;
    }

    public boolean updateNavigationEndTime(NavAux navigation) {
        ContentValues args = new ContentValues();
        args.put(InspirersContract.Navigation.COLUMN_NAVIGATION_END_TIME, navigation.getEndTime().getTimeInMillis());

        return database.update(InspirersContract.Navigation.TABLE_NAVIGATION, args, InspirersContract.Navigation._ID + " = ? ", new String[]{""+navigation.getId()}) > 0;
    }

    private NavAux cursorToNavAux(Cursor cursor){
        Long id = cursor.getLong(cursor.getColumnIndex(InspirersContract.Navigation._ID));

        GregorianCalendar auxStartTime = new GregorianCalendar();
        auxStartTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(InspirersContract.Navigation.COLUMN_NAVIGATION_START_TIME)));

        GregorianCalendar auxEndTime = null;
        if(!cursor.isNull(cursor.getColumnIndex(InspirersContract.Navigation.COLUMN_NAVIGATION_END_TIME))){
            auxEndTime = new GregorianCalendar();
            auxEndTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(InspirersContract.Navigation.COLUMN_NAVIGATION_END_TIME)));
        }

        return new NavAux(id,
                cursor.getLong(cursor.getColumnIndex(InspirersContract.Navigation.COLUMN_NAVIGATION_USER_ID)),
                cursor.getString(cursor.getColumnIndex(InspirersContract.Navigation.COLUMN_NAVIGATION_DESCRIPTION)),
                auxStartTime,
                auxEndTime,
                cursor.getString(cursor.getColumnIndex(InspirersContract.Navigation.COLUMN_NAVIGATION_TYPE)));
    }

    public ArrayList<NavAux> getUserNavigationInfo(Long userId) {
        ArrayList<NavAux> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.Navigation.TABLE_NAVIGATION,
                allColumns,null,null, null, null,InspirersContract.Navigation._ID + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToNavAux(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }
}
