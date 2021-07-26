package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.database.InspirersContract.Inhaler;
import com.bloomidea.inspirers.model.MedicineInhaler;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 22/03/17.
 */

public class InhalerDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { Inhaler._ID,    //0
            Inhaler.COLUMN_INHALER_MEDICINE_ID,     //1
            Inhaler.COLUMN_INHALER_ACTIVE,          //2
            Inhaler.COLUMN_INHALER_BARCODE,         //3
            Inhaler.COLUMN_INHALER_DOSAGE};         //4

    public InhalerDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createInhaler(Long medicineId, MedicineInhaler inhaler) {
        ContentValues values = new ContentValues();

        values.put(Inhaler.COLUMN_INHALER_MEDICINE_ID, medicineId);
        values.put(Inhaler.COLUMN_INHALER_ACTIVE, inhaler.isActive());
        values.put(Inhaler.COLUMN_INHALER_BARCODE, inhaler.getBarcode());
        values.put(Inhaler.COLUMN_INHALER_DOSAGE, inhaler.getDosage());

        long insertId = database.insert(Inhaler.TABLE_INHALER, null, values);

        inhaler.setId(insertId);

        return insertId!=-1;
    }

    private MedicineInhaler cursorToMedicineInhaler(Cursor cursor){
        return new MedicineInhaler(cursor.getLong(cursor.getColumnIndex(Inhaler._ID)),
                    cursor.getInt(cursor.getColumnIndex(Inhaler.COLUMN_INHALER_ACTIVE))==1,
                    cursor.getString(cursor.getColumnIndex(Inhaler.COLUMN_INHALER_BARCODE)),
                    cursor.getInt(cursor.getColumnIndex(Inhaler.COLUMN_INHALER_DOSAGE)));
    }

    public boolean changeDosage(int dosage, long inhalerId){
        ContentValues args = new ContentValues();
        args.put(InspirersContract.Timeline.COLUMN_TIMELINE_DOSAGE,dosage);

        return database.update(InspirersContract.Inhaler.TABLE_INHALER, args, InspirersContract.Inhaler._ID + " = ? ", new String[]{""+inhalerId}) > 0;
    }

    public ArrayList<MedicineInhaler> getMedicineInhalers(Long medicineId){
        ArrayList<MedicineInhaler> list = new ArrayList<>();

        Cursor cursor = database.query(Inhaler.TABLE_INHALER,
                allColumns, Inhaler.COLUMN_INHALER_MEDICINE_ID+" = ?",new String[]{""+medicineId}, null, null,Inhaler._ID + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToMedicineInhaler(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean deleteInhalers(long medicineId){
        String table = Inhaler.TABLE_INHALER;
        String whereClause = Inhaler.COLUMN_INHALER_MEDICINE_ID+" = ?";
        String[] whereArgs = new String[] {""+medicineId};

        return database.delete(table, whereClause, whereArgs) >= 0;
    }
}
