package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.ScheduleAux;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by michellobato on 22/03/17.
 */

public class MedicineScheduleDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { InspirersContract.MedicineSchedule._ID,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_MEDICINE_ID,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_CODE,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_DESC,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_INTERVAL,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTEDOPTION,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTED,
            InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_INTERVAL};        //4

    public MedicineScheduleDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createMedicineSchedule(Long userId, Long medicineId, MedicineSchedule medicineSchedule) {
        ContentValues values = new ContentValues();

        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_MEDICINE_ID,medicineId);
        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_CODE,medicineSchedule.getSelection().getCode());
        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_DESC,medicineSchedule.getSelection().getDesc());
        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_INTERVAL,medicineSchedule.getSelection().getIntervalHours());
        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTEDOPTION,medicineSchedule.getDays().getSelectedOption());


        ArrayList<String> aux = new ArrayList<>();

        if(medicineSchedule.getDays().getSelectedDays() != null)
        for(Days d : medicineSchedule.getDays().getSelectedDays()){
            if (d.isSelected()){
                aux.add(""+d.getCode());
            }
        }

        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTED,TextUtils.join(",",aux));
        values.put(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_INTERVAL,medicineSchedule.getDays().getIntervalDays());

        long insertId = database.insert(InspirersContract.MedicineSchedule.TABLE_MEDICINE_SCHEDULE, null, values);

        medicineSchedule.setId(insertId);

        return insertId!=-1;
    }

    private MedicineSchedule cursorToMedicineSchedule(Cursor cursor){
        Long id = cursor.getLong(cursor.getColumnIndex(InspirersContract.MedicineSchedule._ID));

        String daysCodes = cursor.getString(cursor.getColumnIndex(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTED));
        ArrayList<Days> finalDays = null;

        if (daysCodes != null && !daysCodes.equals("")){
            ArrayList<String> arraydays = new ArrayList<String>(Arrays.asList(daysCodes.split(",")));

            ArrayList<Days> days = new ArrayList<Days>();
            days.add(new Days(Days.SUNDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_sunday)));
            days.add(new Days(Days.MONDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_monday)));
            days.add(new Days(Days.TUESDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_tuesday)));
            days.add(new Days(Days.WEDNESDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_wednesday)));
            days.add(new Days(Days.THURSDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_thursday)));
            days.add(new Days(Days.FRIDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_friday)));
            days.add(new Days(Days.SATURDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_saturday)));

            for (Days auxDay : days){
                for (String dayCode : arraydays){
                    if (auxDay.getCode() == Integer.parseInt(dayCode)){
                        auxDay.setSelected(true);
                    }
                }
            }
            finalDays = new ArrayList<Days>();
            finalDays = days;
        }

        return new MedicineSchedule(cursor.getLong(cursor.getColumnIndex(InspirersContract.MedicineSchedule._ID)),
                new ScheduleAux(cursor.getInt(cursor.getColumnIndex(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_CODE)),
                        cursor.getString(cursor.getColumnIndex(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_DESC)),
                        cursor.getInt(cursor.getColumnIndex(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_INTERVAL))),
                AppController.getmInstance().getMedicineTimeDataSource().getMedicineTimes(id),
                new MedicineDays(cursor.getInt(cursor.getColumnIndex(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTEDOPTION)),
                        finalDays,
                        cursor.getInt(cursor.getColumnIndex(InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_INTERVAL)))
                );
    }

    public ArrayList<MedicineSchedule> getMedicineSchedule(Long medicineId){
        ArrayList<MedicineSchedule> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.MedicineSchedule.TABLE_MEDICINE_SCHEDULE,
                allColumns, InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_MEDICINE_ID+" = ?",new String[]{""+medicineId}, null, null,InspirersContract.MedicineSchedule._ID + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToMedicineSchedule(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean deleteMedicinesSchedule(Long medicineId) {
        String table = InspirersContract.MedicineSchedule.TABLE_MEDICINE_SCHEDULE;
        String whereClause = InspirersContract.MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_MEDICINE_ID+" = ?";
        String[] whereArgs = new String[] {""+medicineId};

        return database.delete(table, whereClause, whereArgs) > 0;
    }
}
