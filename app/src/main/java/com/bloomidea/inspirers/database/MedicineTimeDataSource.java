package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.model.FaseTime;
import com.bloomidea.inspirers.model.MedicineTime;

import java.util.ArrayList;

/**
 * Created by michellobato on 22/03/17.
 */

public class MedicineTimeDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { InspirersContract.MedicineTime._ID,         //0
            InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_USER_ID,        //1
            InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_SCHEDULE_ID,    //2
            InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_DAY_FASE,       //3
            InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_DOSAGE};        //4

    public MedicineTimeDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createMedicineTime(Long userId, Long scheduleId, MedicineTime medicineTime) {
        ContentValues values = new ContentValues();

        values.put(InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_USER_ID, userId);
        values.put(InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_SCHEDULE_ID, scheduleId);
        values.put(InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_DAY_FASE, medicineTime.getFaseTime().getHourInitDesc());
        values.put(InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_DOSAGE, medicineTime.getDosage());

        long insertId = database.insert(InspirersContract.MedicineTime.TABLE_MEDICINE_TIME, null, values);

        medicineTime.setId(insertId);

        return insertId!=-1;
    }

    private MedicineTime cursorToMedicineTime(Cursor cursor){
        return new MedicineTime(cursor.getLong(cursor.getColumnIndex(InspirersContract.MedicineTime._ID)),
                new FaseTime(cursor.getString(cursor.getColumnIndex(InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_DAY_FASE))),
                cursor.getInt(cursor.getColumnIndex(InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_DOSAGE)));
    }

    public ArrayList<MedicineTime> getMedicineTimes(Long medicineId){
        ArrayList<MedicineTime> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.MedicineTime.TABLE_MEDICINE_TIME,
                allColumns, InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_SCHEDULE_ID+" = ?",new String[]{""+medicineId}, null, null,InspirersContract.MedicineTime._ID + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToMedicineTime(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean deleteMedicinesTimes(Long medicineId) {
        String table = InspirersContract.MedicineTime.TABLE_MEDICINE_TIME;
        String whereClause = InspirersContract.MedicineTime.COLUMN_MEDICINE_TIME_SCHEDULE_ID+" = ?";
        String[] whereArgs = new String[] {""+medicineId};

        return database.delete(table, whereClause, whereArgs) > 0;
    }
}
