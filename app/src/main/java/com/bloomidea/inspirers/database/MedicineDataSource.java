package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.database.InspirersContract.Medicine;
import com.bloomidea.inspirers.model.MedicineInhaler;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.model.UserSOSMedicine;
import com.bloomidea.inspirers.sync.InnerSyncMedicine;
import com.bloomidea.inspirers.utils.HealthServicesAux;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 22/03/17.
 */

public class MedicineDataSource {
    private SQLiteDatabase database;
    private Context context;

    private String[] allColumns = { Medicine._ID,       //0
            Medicine.COLUMN_MEDICINE_USER_ID,           //1
            Medicine.COLUMN_MEDICINE_TYPE,              //2
            Medicine.COLUMN_MEDICINE_NAME,              //3
            Medicine.COLUMN_MEDICINE_NID,               //4
            Medicine.COLUMN_MEDICINE_SHARE,             //5
            Medicine.COLUMN_MEDICINE_DURATION,          //6
            Medicine.COLUMN_MEDICINE_START_DATE,        //7
            Medicine.COLUMN_MEDICINE_NOTES,             //8
            Medicine.COLUMN_MEDICINE_IS_SOS,            //9
            Medicine.COLUMN_MEDICINE_SEVERITY,          //10
            Medicine.COLUMN_MEDICINE_TRIGGER,           //11
            Medicine.COLUMN_MEDICINE_HEALTH_SERVICE,    //12
            Medicine.COLUMN_MEDICINE_SOS_DOSAGE,        //13
            Medicine.COLUMN_MEDICINE_NEED_SYNC,         //14
            Medicine.COLUMN_MEDICINE_DELETED};          //15


    public MedicineDataSource(MySQLiteHelper db, Context context) {
        this.context = context;
        this.database = db.getWritableDatabase();
    }

    public boolean createUserMedicine(Long userId, UserNormalMedicine userMedicine, GregorianCalendar nowDate, boolean createTimelineItems, boolean deleted, boolean needSync){
        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put(Medicine.COLUMN_MEDICINE_USER_ID, userId);

        if(userMedicine.getNid() != null || !userMedicine.getNid().isEmpty()){
            values.put(Medicine.COLUMN_MEDICINE_NID, userMedicine.getNid());
        }

        values.put(Medicine.COLUMN_MEDICINE_TYPE, userMedicine.getMedicineType().getCode());
        values.put(Medicine.COLUMN_MEDICINE_NAME, userMedicine.getMedicineName());
        values.put(Medicine.COLUMN_MEDICINE_SHARE, userMedicine.isShareWithDoctor());
        values.put(Medicine.COLUMN_MEDICINE_DURATION, userMedicine.getDuration());
        values.put(Medicine.COLUMN_MEDICINE_START_DATE, userMedicine.getStartDate().getTimeInMillis());
        values.put(Medicine.COLUMN_MEDICINE_NOTES, userMedicine.getNote());
        values.put(Medicine.COLUMN_MEDICINE_SOS_DOSAGE, userMedicine.getTotalSOSDosages());
        values.put(Medicine.COLUMN_MEDICINE_IS_SOS, false);
        values.put(Medicine.COLUMN_MEDICINE_NEED_SYNC, needSync);
        values.put(Medicine.COLUMN_MEDICINE_DELETED, deleted);

        long insertId = database.insert(Medicine.TABLE_MEDICINE, null, values);

        userMedicine.setId(insertId);

        boolean ok = insertId != -1;

        if (ok) {
            for(MedicineInhaler inhaler : userMedicine.getInhalers()){
                ok = AppController.getmInstance().getInhalerDataSource().createInhaler(insertId, inhaler);

                if(!ok){
                    break;
                }
            }

            if(ok){

                for(MedicineSchedule schedule : userMedicine.getSchedules()){

                    ok = AppController.getmInstance().getMedicineScheduleDataSource().createMedicineSchedule(userId, insertId, schedule);

                    if(!ok){
                        break;
                    }

                    for(MedicineTime time : schedule.getTimes()){
                        ok = AppController.getmInstance().getMedicineTimeDataSource().createMedicineTime(userId,schedule.getId(),time);

                        if(!ok){
                            break;
                        }
                    }
                }
            }

            if(ok && createTimelineItems){
                ok = AppController.getmInstance().getTimelineDataSource().createTimelineUserMedicine(userId,userMedicine, nowDate, false);
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    public TimelineItem createSOSUserMedicine(Long userId, UserSOSMedicine userMedicine, boolean createTimelineItems, boolean deleted, boolean needSync){
        TimelineItem auxItem = null;

        database.beginTransaction();

        ContentValues values = new ContentValues();

        if(userMedicine.getNid() != null || !userMedicine.getNid().isEmpty()){
            values.put(Medicine.COLUMN_MEDICINE_NID, userMedicine.getNid());
        }

        values.put(Medicine.COLUMN_MEDICINE_USER_ID, userId);
        values.put(Medicine.COLUMN_MEDICINE_TYPE, userMedicine.getMedicineType().getCode());
        values.put(Medicine.COLUMN_MEDICINE_NAME, userMedicine.getMedicineName());
        values.put(Medicine.COLUMN_MEDICINE_SHARE, userMedicine.isShareWithDoctor());
        values.put(Medicine.COLUMN_MEDICINE_START_DATE, userMedicine.getStartDate().getTimeInMillis());
        values.put(Medicine.COLUMN_MEDICINE_NOTES, userMedicine.getNote());
        values.put(Medicine.COLUMN_MEDICINE_IS_SOS, true);
        values.put(Medicine.COLUMN_MEDICINE_SEVERITY, userMedicine.getSeverity());
        values.put(Medicine.COLUMN_MEDICINE_TRIGGER, userMedicine.getTrigger());
        values.put(Medicine.COLUMN_MEDICINE_HEALTH_SERVICE, userMedicine.getHealthService().getCode());
        values.put(Medicine.COLUMN_MEDICINE_SOS_DOSAGE, userMedicine.getSosDosage());
        values.put(Medicine.COLUMN_MEDICINE_NEED_SYNC, needSync);
        values.put(Medicine.COLUMN_MEDICINE_DELETED, deleted);

        long insertId = database.insert(Medicine.TABLE_MEDICINE, null, values);

        userMedicine.setId(insertId);

        boolean ok = insertId != -1;

        if (ok) {
            for(MedicineInhaler inhaler : userMedicine.getInhalers()){
                ok = AppController.getmInstance().getInhalerDataSource().createInhaler(insertId, inhaler);

                if(!ok){
                    break;
                }
            }

            if(ok && createTimelineItems){
                auxItem = AppController.getmInstance().getTimelineDataSource().createTimelineSOSUserMedicine(userId,userMedicine);

                ok = auxItem!=null;
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return auxItem;
    }

    private UserNormalMedicine cursorToUserNormalMedicine(Cursor cursor){
        Long id = cursor.getLong(cursor.getColumnIndex(Medicine._ID));

        GregorianCalendar auxStartDate = new GregorianCalendar();
        auxStartDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_START_DATE)));

        String auxMedicineTypeCode = cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_TYPE));

        return new UserNormalMedicine(id,
                MedicineTypeAux.getMedicineType(auxMedicineTypeCode, context),
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_NAME)),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_SHARE)) == 1,
                auxStartDate,
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_NOTES)),
                AppController.getmInstance().getInhalerDataSource().getMedicineInhalers(id),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_SOS_DOSAGE)),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_DURATION)),
                AppController.getmInstance().getMedicineScheduleDataSource().getMedicineSchedule(id),
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_NID)));
    }

    public ArrayList<UserNormalMedicine> getUserNormalMedicines(Long userId){
        ArrayList<UserNormalMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_IS_SOS+") AND NOT("+Medicine.COLUMN_MEDICINE_DELETED+")",new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToUserNormalMedicine(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<UserNormalMedicine> getUserNormalTaken(Long userId){
        ArrayList<UserNormalMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_IS_SOS+") AND NOT("+Medicine.COLUMN_MEDICINE_DELETED+")",new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToUserNormalMedicine(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<UserMedicine> getUserNormalMedicinesAux(Long userId){
        ArrayList<UserMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_IS_SOS+") AND NOT("+Medicine.COLUMN_MEDICINE_DELETED+")",new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToUserNormalMedicine(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<UserMedicine> getUserNormalMedicinesDisable(Long userId){
        ArrayList<UserMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_IS_SOS+") AND "+Medicine.COLUMN_MEDICINE_DELETED,new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToUserNormalMedicine(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<UserMedicine> getUserNormalMedicinesContinuous(Long userId){
        ArrayList<UserMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_IS_SOS+") AND "+Medicine.COLUMN_MEDICINE_DELETED,new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToUserNormalMedicine(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }


    public ArrayList<UserMedicine> getUserMedicines(Long userId){
        ArrayList<UserMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_DELETED+")",new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            boolean isSOS = cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_IS_SOS)) == 1;
            if(isSOS) {
                list.add(cursorToUserSOSMedicine(cursor));
            }else{
                list.add(cursorToUserNormalMedicine(cursor));
            }

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public UserNormalMedicine getUserNormalMedicine(Long medicineId){
        UserNormalMedicine medicine = null;

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine._ID+" = ?",new String[]{""+medicineId}, null, null,null);


        if (cursor.moveToFirst()) {
            medicine = cursorToUserNormalMedicine(cursor);
        }

        cursor.close();

        return medicine;
    }

    private UserSOSMedicine cursorToUserSOSMedicine(Cursor cursor){
        Long id = cursor.getLong(cursor.getColumnIndex(Medicine._ID));

        GregorianCalendar auxStartDate = new GregorianCalendar();
        auxStartDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_START_DATE)));

        String auxMedicineTypeCode = cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_TYPE));

        String auxHealthServiceCode = cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_HEALTH_SERVICE));

        return new UserSOSMedicine(id,
                MedicineTypeAux.getMedicineType(auxMedicineTypeCode, context),
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_NAME)),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_SHARE)) == 1,
                auxStartDate,
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_NOTES)),
                AppController.getmInstance().getInhalerDataSource().getMedicineInhalers(id),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_SOS_DOSAGE)),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_SEVERITY)),
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_TRIGGER)),
                HealthServicesAux.getHealthService(auxHealthServiceCode, context),
                cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_SOS_DOSAGE)),
                cursor.getString(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_NID)));
    }

    public UserSOSMedicine getUserSOSMedicine(Long medicineId){
        UserSOSMedicine medicine = null;

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine._ID+" = ?",new String[]{""+medicineId}, null, null,null);


        if (cursor.moveToFirst()) {
            medicine = cursorToUserSOSMedicine(cursor);
        }

        cursor.close();

        return medicine;
    }

    public UserMedicine getUserMedicine(long medicineId){
        UserMedicine medicine = null;

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine._ID+" = ?",new String[]{""+medicineId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");


        if(cursor.moveToFirst()){
            boolean isSOS = cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_IS_SOS)) == 1;
            if(isSOS) {
                medicine = cursorToUserSOSMedicine(cursor);
            }else{
                medicine = cursorToUserNormalMedicine(cursor);
            }

        }

        cursor.close();

        return medicine;
    }

    public int getTotalMedsWithDeleted(Long userId) {
        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND NOT("+Medicine.COLUMN_MEDICINE_IS_SOS+")",new String[]{""+userId}, null, null,Medicine.COLUMN_MEDICINE_START_DATE + " ASC");

        return cursor.getCount();
    }


    /*
    public boolean deleteMedicine(long userId, UserMedicine medicineToDelete){
        database.beginTransaction();

        String table = Medicine.TABLE_MEDICINE;
        String whereClause = Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND "+Medicine._ID+" = ?";
        String[] whereArgs = new String[] { ""+userId, ""+medicineToDelete.getId()};
        boolean ok = database.delete(table, whereClause, whereArgs) > 0;

        if(ok){
            ok = AppController.getmInstance().getInhalerDataSource().deleteInhalers(medicineToDelete.getId());
        }

        if(ok){
            if(medicineToDelete instanceof UserNormalMedicine) {
                ok = AppController.getmInstance().getMedicineTimeDataSource().deleteMedicinesTimes(medicineToDelete.getId());
            }
        }

        if(ok){
            ok = AppController.getmInstance().getTimelineDataSource().deleteMedicinesTiemLine(medicineToDelete.getId());
        }

        if(ok){
            if(medicineToDelete instanceof UserNormalMedicine){
                UserNormalMedicine auxMed = (UserNormalMedicine) medicineToDelete;

                ArrayList<GregorianCalendar> listDates = new ArrayList<>();


                for(int i=0; i<auxMed.getDuration(); i++){
                    GregorianCalendar aux = new GregorianCalendar();
                    aux.setTimeInMillis(auxMed.getStartDate().getTimeInMillis());
                    aux.add(Calendar.DAY_OF_MONTH,i);

                    listDates.add(aux);
                }

                for(GregorianCalendar date : listDates){
                    int totalMedsBD = AppController.getmInstance().getTimelineDataSource().getTotalMedsOnDate(date,userId);

                    if(totalMedsBD>0) {
                        int points = TimelineDataSource.pointPerDay/totalMedsBD;

                        ContentValues values = new ContentValues();
                        values.put(InspirersContract.Timeline.COLUMN_TIMELINE_TIME_POINTS, points);

                        int updateRows = database.update(InspirersContract.Timeline.TABLE_TIMELINE, values,
                                "strftime('%Y-%m-%d', " + InspirersContract.Timeline.COLUMN_TIMELINE_DATE + " / 1000, 'unixepoch') = strftime('%Y-%m-%d', ? / 1000, 'unixepoch') AND NOT(" + InspirersContract.Timeline.COLUMN_TIMELINE_IS_SOS + ") AND " + InspirersContract.Timeline.COLUMN_TIMELINE_MEDICINE_ID + " IS NOT NULL AND "+InspirersContract.Timeline.COLUMN_TIMELINE_USER_ID +"=?",
                                new String[]{""+date.getTimeInMillis(),""+userId});

                        ok = updateRows > 0;

                        if (!ok) {
                            break;
                        }
                    }
                }
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }
*/
    public boolean deleteMedicine(long userId, UserMedicine medicineToDelete){
        database.beginTransaction();

        ContentValues args = new ContentValues();

        args.put(Medicine.COLUMN_MEDICINE_NEED_SYNC, true);
        args.put(Medicine.COLUMN_MEDICINE_DELETED, true);

        boolean ok = database.update(Medicine.TABLE_MEDICINE, args, Medicine._ID + " = ? ", new String[]{""+medicineToDelete.getId()}) > 0;

        if(ok){
            ok = AppController.getmInstance().getTimelineDataSource().deleteMedicinesFutureDaysTimeLineAndUpdateMain(medicineToDelete.getId());
        }

        if(ok){
            if(medicineToDelete instanceof UserNormalMedicine){
                UserNormalMedicine auxMed = (UserNormalMedicine) medicineToDelete;

                ArrayList<GregorianCalendar> listDates = new ArrayList<>();


                for(int i=0; i<auxMed.getDuration(); i++){
                    GregorianCalendar aux = new GregorianCalendar();
                    aux.setTimeInMillis(auxMed.getStartDate().getTimeInMillis());
                    aux.add(Calendar.DAY_OF_MONTH,i);

                    listDates.add(aux);
                }

                for(GregorianCalendar date : listDates){
                    int totalMedsBD = AppController.getmInstance().getTimelineDataSource().getTotalMedsOnDateNotDeleted(date,userId);

                    if(totalMedsBD>0) {
                        int totalPointOFDAYWON = AppController.getmInstance().getTimelineDataSource().getTotalPointsWonOnDateNotDeleted(date,userId);

                        int pointRemain = TimelineDataSource.pointPerDay - totalPointOFDAYWON;

                        if(pointRemain<0){
                            pointRemain = 0;
                        }

                        if(totalMedsBD == 0){
                            totalMedsBD = 1;
                        }

                        int points = pointRemain/totalMedsBD;

                        ContentValues values = new ContentValues();
                        values.put(InspirersContract.Timeline.COLUMN_TIMELINE_TIME_POINTS, points);
                        values.put(InspirersContract.Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

                        int updateRows = database.update(InspirersContract.Timeline.TABLE_TIMELINE, values,
                                "strftime('%Y-%m-%d', " + InspirersContract.Timeline.COLUMN_TIMELINE_DATE + " / 1000, 'unixepoch') = strftime('%Y-%m-%d', ? / 1000, 'unixepoch') AND NOT(" + InspirersContract.Timeline.COLUMN_TIMELINE_IS_SOS + ") AND " + InspirersContract.Timeline.COLUMN_TIMELINE_MEDICINE_ID + " IS NOT NULL AND NOT("+InspirersContract.Timeline.COLUMN_TIMELINE_DELETED+") AND "+
                                        InspirersContract.Timeline.COLUMN_TIMELINE_USER_ID +" = ? AND "+InspirersContract.Timeline.COLUMN_TIMELINE_STATE + " IS NULL",
                                new String[]{""+date.getTimeInMillis(),""+userId});

                        ok = updateRows > 0;

                        if (!ok) {
                            break;
                        }
                    }
                }
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    public boolean activateMedicine(long userId, UserMedicine activateToDelete){
        database.beginTransaction();

        ContentValues args = new ContentValues();

        args.put(Medicine.COLUMN_MEDICINE_NEED_SYNC, true);
        args.put(Medicine.COLUMN_MEDICINE_DELETED, false);

        boolean ok = database.update(Medicine.TABLE_MEDICINE, args, Medicine._ID + " = ? ", new String[]{""+activateToDelete.getId()}) > 0;

        if(ok){
            ok = AppController.getmInstance().getTimelineDataSource().deleteMedicinesFutureDaysTimeLineAndUpdateMain(activateToDelete.getId());
        }

        if(ok){
            if(activateToDelete instanceof UserNormalMedicine){
                UserNormalMedicine auxMed = (UserNormalMedicine) activateToDelete;

                ArrayList<GregorianCalendar> listDates = new ArrayList<>();


                for(int i=0; i<auxMed.getDuration(); i++){
                    GregorianCalendar aux = new GregorianCalendar();
                    aux.setTimeInMillis(auxMed.getStartDate().getTimeInMillis());
                    aux.add(Calendar.DAY_OF_MONTH,i);

                    listDates.add(aux);
                }

                for(GregorianCalendar date : listDates){
                    int totalMedsBD = AppController.getmInstance().getTimelineDataSource().getTotalMedsOnDateNotDeleted(date,userId);

                    if(totalMedsBD>0) {
                        int totalPointOFDAYWON = AppController.getmInstance().getTimelineDataSource().getTotalPointsWonOnDateNotDeleted(date,userId);

                        int pointRemain = TimelineDataSource.pointPerDay - totalPointOFDAYWON;

                        if(pointRemain<0){
                            pointRemain = 0;
                        }

                        if(totalMedsBD == 0){
                            totalMedsBD = 1;
                        }

                        int points = pointRemain/totalMedsBD;

                        ContentValues values = new ContentValues();
                        values.put(InspirersContract.Timeline.COLUMN_TIMELINE_TIME_POINTS, points);
                        values.put(InspirersContract.Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

                        int updateRows = database.update(InspirersContract.Timeline.TABLE_TIMELINE, values,
                                "strftime('%Y-%m-%d', " + InspirersContract.Timeline.COLUMN_TIMELINE_DATE + " / 1000, 'unixepoch') = strftime('%Y-%m-%d', ? / 1000, 'unixepoch') AND NOT(" + InspirersContract.Timeline.COLUMN_TIMELINE_IS_SOS + ") AND " + InspirersContract.Timeline.COLUMN_TIMELINE_MEDICINE_ID + " IS NOT NULL AND NOT("+InspirersContract.Timeline.COLUMN_TIMELINE_DELETED+") AND "+
                                        InspirersContract.Timeline.COLUMN_TIMELINE_USER_ID +" = ? AND "+InspirersContract.Timeline.COLUMN_TIMELINE_STATE + " IS NULL",
                                new String[]{""+date.getTimeInMillis(),""+userId});

                        ok = updateRows > 0;

                        if (!ok) {
                            break;
                        }
                    }
                }
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    public UserSOSMedicine getLastInsertedSOSUserMedicine(long userId){
        UserSOSMedicine medicine = null;

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_USER_ID+" = ? AND "+Medicine.COLUMN_MEDICINE_IS_SOS + " AND NOT("+Medicine.COLUMN_MEDICINE_DELETED+")",new String[]{""+userId}, null, null,Medicine._ID + " ASC");


        if(cursor.moveToFirst()){
           medicine = cursorToUserSOSMedicine(cursor);
        }

        cursor.close();

        return medicine;
    }

    public boolean updateUserMedicine(Long userId, UserNormalMedicine newUserMedicine, GregorianCalendar nowDate){
        database.beginTransaction();

        Long medicineId = newUserMedicine.getId();

        ContentValues values = new ContentValues();

        values.put(Medicine.COLUMN_MEDICINE_TYPE, newUserMedicine.getMedicineType().getCode());
        values.put(Medicine.COLUMN_MEDICINE_NAME, newUserMedicine.getMedicineName());
        values.put(Medicine.COLUMN_MEDICINE_SHARE, newUserMedicine.isShareWithDoctor());
        values.put(Medicine.COLUMN_MEDICINE_DURATION, newUserMedicine.getDuration());
        values.put(Medicine.COLUMN_MEDICINE_START_DATE, newUserMedicine.getStartDate().getTimeInMillis());
        values.put(Medicine.COLUMN_MEDICINE_SOS_DOSAGE, newUserMedicine.getTotalSOSDosages());
        values.put(Medicine.COLUMN_MEDICINE_NOTES, newUserMedicine.getNote());
        values.put(Medicine.COLUMN_MEDICINE_IS_SOS, false);
        values.put(Medicine.COLUMN_MEDICINE_NEED_SYNC, true);


        boolean ok = database.update(Medicine.TABLE_MEDICINE, values, Medicine._ID + " = ? ", new String[]{""+medicineId}) > 0;

        if (ok) {
            ok = AppController.getmInstance().getInhalerDataSource().deleteInhalers(medicineId);

            if(ok) {
                for (MedicineInhaler inhaler : newUserMedicine.getInhalers()) {
                    ok = AppController.getmInstance().getInhalerDataSource().createInhaler(medicineId, inhaler);

                    if (!ok) {
                        break;
                    }
                }
            }

            if(ok){
                ok = AppController.getmInstance().getMedicineTimeDataSource().deleteMedicinesTimes(medicineId);

                if(ok) {
                    for (MedicineSchedule schedule : newUserMedicine.getSchedules()){

                        ok = AppController.getmInstance().getMedicineScheduleDataSource().createMedicineSchedule(userId,medicineId,schedule);

                        if (!ok) {
                            break;
                        }

                        for (MedicineTime time : schedule.getTimes()) {
                            ok = AppController.getmInstance().getMedicineTimeDataSource().createMedicineTime(userId,schedule.getId(),time);

                            if (!ok) {
                                break;
                            }
                        }
                    }
                }
            }

            if(ok){
                ok = AppController.getmInstance().getTimelineDataSource().deleteMedicinesFutureDaysTimeLineAndUpdateMain(medicineId);

                if(ok) {
                    ok = AppController.getmInstance().getTimelineDataSource().createTimelineUserMedicine(userId, newUserMedicine, nowDate, false);
                }
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    public void deleteAllForUser(Long userId) {
        database.execSQL("Delete from inhaler where _id in (select inhaler._id from inhaler inner join medicine on medicine._id = inhaler.inhaler_medicine_id where medicine.medicine_user_id = "+userId+")");
        database.execSQL("Delete from medicine_time where _id in (select medicine_time._id from medicine_time inner join medicine on medicine._id = medicine_time.medicine_id where medicine.medicine_user_id = "+userId+")");
        database.execSQL("DELETE FROM "+ Medicine.TABLE_MEDICINE+" WHERE "+Medicine.COLUMN_MEDICINE_USER_ID + " = "+userId);
    }


    public ArrayList<InnerSyncMedicine> getMedicineNeedSync(long userToSyncID) {
        ArrayList<InnerSyncMedicine> list = new ArrayList<>();

        Cursor cursor = database.query(Medicine.TABLE_MEDICINE,
                allColumns, Medicine.COLUMN_MEDICINE_NEED_SYNC+" = 1 AND "+Medicine.COLUMN_MEDICINE_USER_ID+" = ?",
                new String[]{""+userToSyncID}, null, null,null);


        cursor.moveToFirst();

        UserMedicine auxMed;
        while (!cursor.isAfterLast()) {
            boolean isSOS = cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_IS_SOS)) == 1;
            if(isSOS) {
                auxMed = cursorToUserSOSMedicine(cursor);
            }else{
                auxMed = cursorToUserNormalMedicine(cursor);
            }

            list.add(new InnerSyncMedicine(
                    AppController.getmInstance().getUserDataSource().getUserUidFromId(cursor.getLong(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_USER_ID))),
                    auxMed,
                    cursor.getInt(cursor.getColumnIndex(Medicine.COLUMN_MEDICINE_DELETED)) == 1));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean setMedicineSynced(Long medicineId, String nid) {
        ContentValues args = new ContentValues();
        args.put(Medicine.COLUMN_MEDICINE_NID,nid);
        args.put(Medicine.COLUMN_MEDICINE_NEED_SYNC,"0");

        return database.update(Medicine.TABLE_MEDICINE, args, InspirersContract.MyPoll._ID+" = ?", new String[]{""+medicineId}) > 0;
    }
}
