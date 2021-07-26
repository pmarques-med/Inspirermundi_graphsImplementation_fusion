package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.FaseTime;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.TimelineWeek;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.model.UserSOSMedicine;
import com.bloomidea.inspirers.sync.InnerSyncTimelineItem;
import com.bloomidea.inspirers.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.bloomidea.inspirers.database.InspirersContract.Medicine;
import static com.bloomidea.inspirers.database.InspirersContract.Timeline;

/**
 * Created by michellobato on 17/03/17.
 */

public class TimelineDataSource {
    public static final int pointPerDay = 100;
    public static final int pointCARAT = 35;
    public static final int pointPollDay = 10;
    public static final int pointPollWeek = 35;

    private SQLiteDatabase database;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTIMEFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private String[] allColumns = { Timeline._ID,               //0
            Timeline.COLUMN_TIMELINE_MEDICINE_ID,               //1
            Timeline.COLUMN_TIMELINE_BADGE_ID,                  //2
            Timeline.COLUMN_TIMELINE_USER_ID,                   //3
            Timeline.COLUMN_TIMELINE_DATE,                      //4
            Timeline.COLUMN_TIMELINE_START_TIME,                //5
            Timeline.COLUMN_TIMELINE_END_TIME,                  //6
            Timeline.COLUMN_TIMELINE_TIME_POINTS,               //7
            Timeline.COLUMN_TIMELINE_WEEK_NUMBER,               //8
            Timeline.COLUMN_TIMELINE_IS_FIRST,                  //9
            Timeline.COLUMN_TIMELINE_IS_SOS,                    //10
            Timeline.COLUMN_TIMELINE_NID,                       //11
            Timeline.COLUMN_TIMELINE_NOTE,                      //12
            Timeline.COLUMN_TIMELINE_STATE,                     //13
            Timeline.COLUMN_TIMELINE_TAKEN,                     //14
            Timeline.COLUMN_TIMELINE_POINTS_WON,                //15
            Timeline.COLUMN_TIMELINE_DOSAGE,                    //16
            Timeline.COLUMN_TIMELINE_FASE_TIME_CODE,            //17
            Timeline.COLUMN_TIMELINE_POLL_ID,                   //18
            Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES,  //19
            Timeline.COLUMN_TIMELINE_NEED_SYNC,                 //20
            Timeline.COLUMN_TIMELINE_DELETED,                   //21
            Timeline.COLUMN_TIMELINE_MAIN_TIME,                 //22
            Timeline.COLUMN_TIMELINE_LATITUDE,                  //23
            Timeline.COLUMN_TIMELINE_LONGITUDE};                //24

    public TimelineDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createTimelineBadge(Long userId, UserBadge badge) {
        ContentValues values = new ContentValues();

        values.put(Timeline.COLUMN_TIMELINE_BADGE_ID, badge.getId());
        values.put(Timeline.COLUMN_TIMELINE_USER_ID, userId);
        values.put(Timeline.COLUMN_TIMELINE_DATE, badge.getDate().getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_START_TIME, badge.getDate().getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_END_TIME, badge.getDate().getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_WEEK_NUMBER, badge.getWeekNumber());
        values.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

        long insertId = database.insert(Timeline.TABLE_TIMELINE, null, values);

        return insertId!=-1;
    }

    public boolean createTimelineUserMedicine(Long userId, UserNormalMedicine userMedicine, GregorianCalendar nowDate, Boolean addMore) {
        boolean ok = true;

        ArrayList<GregorianCalendar> listDates = new ArrayList<>();
        ArrayList<MedicineSchedule> listDatesSchedule = new ArrayList<>();

        int duration;
        if (userMedicine.getDuration() == 0){
            duration = 30;
        }else{
            duration = userMedicine.getDuration();
        }



        for(int i=0; i<duration; i++){

            Long date = addMore ? nowDate.getTimeInMillis() : userMedicine.getStartDate().getTimeInMillis();

            GregorianCalendar aux = new GregorianCalendar();
            aux.setTimeInMillis(date);
            aux.add(Calendar.DAY_OF_MONTH,i);

            // MARK4 - SELECT DAYS TO CREATE, CHECK DAYS WEEK
            for (MedicineSchedule auxSche : userMedicine.getSchedules()){
                Log.d("TAG_CREATE", ""+auxSche.getDays().getSelectedOption());

                if (auxSche.getDays().getSelectedOption() == MedicineDays.ALL_DAYS_OPTION){
                    listDates.add(aux);
                    listDatesSchedule.add(auxSche);
                }else if (auxSche.getDays().getSelectedOption() == MedicineDays.SPEC_DAYS_OPTION){
                    for (Days auxDays : auxSche.getDays().getSelectedDays()){
                        if (auxDays.isSelected() && (auxDays.getCode() == aux.get(Calendar.DAY_OF_WEEK))){
                            listDates.add(aux);
                            listDatesSchedule.add(auxSche);
                        }
                    }
                }else{
                    int interval = auxSche.getDays().getIntervalDays();
                    if (i == 0 || i%interval == 0){
                        listDates.add(aux);
                        listDatesSchedule.add(auxSche);
                    }
                }
            }
        }

        boolean mainTime = true;
        boolean firstDateComplete = false;

        int count = 0;
        for(GregorianCalendar date : listDates) {
            if(!firstDateComplete){
                mainTime=true;
                firstDateComplete = true;
            }else{
                mainTime = false;
            }

            int totalMedsBD = getTotalMedsOnDateNotDeleted(date, userId);
            int totalMeds;

            if (dateFormat.format(date.getTime()).equals(dateFormat.format(nowDate.getTime()))) {
                int timesForToday = 0;


                //for (MedicineSchedule schedule : userMedicine.getSchedules()){
                    for (MedicineTime time : listDatesSchedule.get(count).getTimes()) {
                        GregorianCalendar initDate = new GregorianCalendar();
                        initDate.setTimeInMillis(nowDate.getTimeInMillis());
                        initDate.set(Calendar.HOUR_OF_DAY, time.getFaseTime().getHourInit());
                        initDate.set(Calendar.MINUTE, time.getFaseTime().getMinutesInit());

                        if (initDate.after(nowDate) || dateTIMEFormat.format(initDate.getTime()).equals(dateTIMEFormat.format(nowDate.getTime()))) {
                            timesForToday++;
                        }
                    }
                //}


                Log.d("TIMESTODAY", "" + timesForToday);
                totalMeds = totalMedsBD + timesForToday;
            } else {
                int totalMedSche = 0;
                for (MedicineSchedule schedule : userMedicine.getSchedules()) {
                    totalMedSche = totalMedSche + schedule.getTimes().size();
                }
                totalMeds = totalMedsBD + totalMedSche;
            }

            int totalPointOFDAYWON = getTotalPointsWonOnDateNotDeleted(date, userId);
            //Log.d("totalPointOFDAYWON",(new SimpleDateFormat("yyyy-dd-MM").format(date.getTime()))+"|"+totalPointOFDAYWON);

            String weekNumber = Utils.getWeekNumber(date);

            int pointRemain = pointPerDay - totalPointOFDAYWON;

            if (pointRemain < 0) {
                pointRemain = 0;
            }

            if (totalMeds == 0) {
                totalMeds = 1;
            }

            int points = pointRemain / totalMeds;

            //for (MedicineSchedule schedule : userMedicine.getSchedules()){
                for (MedicineTime time : listDatesSchedule.get(count).getTimes()) {
                    GregorianCalendar initDate = new GregorianCalendar();
                    initDate.setTimeInMillis(date.getTimeInMillis());
                    initDate.set(Calendar.HOUR_OF_DAY, time.getFaseTime().getHourInit());
                    initDate.set(Calendar.MINUTE, time.getFaseTime().getMinutesInit());

                    if (initDate.after(nowDate) || dateTIMEFormat.format(initDate.getTime()).equals(dateTIMEFormat.format(nowDate.getTime()))) {
                        GregorianCalendar endDate = new GregorianCalendar();
                        //TAG M6
                        endDate.setTimeInMillis(initDate.getTimeInMillis());
                        endDate.add(Calendar.HOUR_OF_DAY, FaseTime.END_TIME_HOUR_ADD);

                        //GregorianCalendar endDate = new GregorianCalendar();
                        //endDate.setTimeInMillis(date.getTimeInMillis());
                        //endDate.set(Calendar.HOUR_OF_DAY, time.getFaseTime().getHourEnd());
                        //endDate.set(Calendar.MINUTE, time.getFaseTime().getMinutesEnd());

                        ContentValues values = new ContentValues();

                        values.put(Timeline.COLUMN_TIMELINE_MEDICINE_ID, userMedicine.getId());
                        values.put(Timeline.COLUMN_TIMELINE_USER_ID, userId);
                        values.put(Timeline.COLUMN_TIMELINE_DATE, date.getTimeInMillis());
                        values.put(Timeline.COLUMN_TIMELINE_START_TIME, initDate.getTimeInMillis());
                        values.put(Timeline.COLUMN_TIMELINE_END_TIME, endDate.getTimeInMillis());
                        values.put(Timeline.COLUMN_TIMELINE_TIME_POINTS, points);
                        values.put(Timeline.COLUMN_TIMELINE_WEEK_NUMBER, weekNumber);
                        values.put(Timeline.COLUMN_TIMELINE_DOSAGE, time.getDosage());
                        values.put(Timeline.COLUMN_TIMELINE_FASE_TIME_CODE, time.getFaseTime().getHourInitDesc());
                        values.put(Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES, 0);
                        values.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);
                        values.put(Timeline.COLUMN_TIMELINE_MAIN_TIME, mainTime);

                        long insertId = database.insert(Timeline.TABLE_TIMELINE, null, values);

                        ok = insertId != -1;

                        if (!ok) {
                            break;
                        }
                    } else {
                        firstDateComplete = false;
                        mainTime = false;
                    }
                }

            //}

            if (totalMedsBD > 0) {
                ContentValues values = new ContentValues();
                values.put(Timeline.COLUMN_TIMELINE_TIME_POINTS, points);
                values.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

                int updateRows = database.update(Timeline.TABLE_TIMELINE, values,
                        "strftime('%Y-%m-%d', " + Timeline.COLUMN_TIMELINE_DATE + " / 1000, 'unixepoch') = strftime('%Y-%m-%d', ? / 1000, 'unixepoch') AND NOT(" + Timeline.COLUMN_TIMELINE_IS_SOS + ") AND " + Timeline.COLUMN_TIMELINE_MEDICINE_ID + " IS NOT NULL AND NOT(" + Timeline.COLUMN_TIMELINE_DELETED + ") AND " +
                                Timeline.COLUMN_TIMELINE_USER_ID + "=? AND " + Timeline.COLUMN_TIMELINE_STATE + " IS NULL",
                        new String[]{"" + date.getTimeInMillis(), "" + userId});

                ok = updateRows > 0;

                if (!ok) {
                    break;
                }
            }
            count++;
        }

        return ok;
    }


    public TimelineItem createTimelineSOSUserMedicine(Long userId, UserSOSMedicine userMedicine) {
        String weekNumber = Utils.getWeekNumber(userMedicine.getStartDate());//userMedicine.getStartDate().get(Calendar.YEAR)+"_"+userMedicine.getStartDate().get(Calendar.WEEK_OF_YEAR);

        ContentValues values = new ContentValues();

        GregorianCalendar auxDate = new GregorianCalendar();
        auxDate.setTimeInMillis(userMedicine.getStartDate().getTimeInMillis());
        auxDate.set(Calendar.HOUR_OF_DAY,0);
        auxDate.set(Calendar.MINUTE,0);
        auxDate.set(Calendar.SECOND,0);
        auxDate.set(Calendar.MILLISECOND,0);

        values.put(Timeline.COLUMN_TIMELINE_MEDICINE_ID, userMedicine.getId());
        values.put(Timeline.COLUMN_TIMELINE_USER_ID, userId);
        values.put(Timeline.COLUMN_TIMELINE_DATE, auxDate.getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_START_TIME, userMedicine.getStartDate().getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_END_TIME, userMedicine.getStartDate().getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_TIME_POINTS, 0);
        values.put(Timeline.COLUMN_TIMELINE_WEEK_NUMBER, weekNumber);
        values.put(Timeline.COLUMN_TIMELINE_IS_SOS, true);
        values.put(Timeline.COLUMN_TIMELINE_STATE, TimelineItem.STATE_DONE);
        values.put(Timeline.COLUMN_TIMELINE_TAKEN, userMedicine.getStartDate().getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_POINTS_WON, 0);
        values.put(Timeline.COLUMN_TIMELINE_DOSAGE, userMedicine.getSosDosage());
        values.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

        long insertId = database.insert(Timeline.TABLE_TIMELINE, null, values);

        boolean ok = insertId!=-1;

        TimelineItem aux = null;

        if(ok){
            aux = getTimelineItem(insertId);
        }

        return aux;
    }

    public int getTotalMedsOnDateNotDeleted(GregorianCalendar date, long userId){
        int total = 0;
        //select count(*) from Teste where strftime('%Y-%m-%d', date / 1000, 'unixepoch') = "2017-03-24"
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                "strftime('%Y-%m-%d', "+Timeline.COLUMN_TIMELINE_DATE+" / 1000, 'unixepoch') = strftime('%Y-%m-%d', ? / 1000, 'unixepoch') AND NOT("+Timeline.COLUMN_TIMELINE_IS_SOS+") AND "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+Timeline.COLUMN_TIMELINE_USER_ID +"=? AND "+Timeline.COLUMN_TIMELINE_STATE+" IS NULL AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",
                new String[]{""+date.getTimeInMillis(),""+userId}, null, null, null);

        total = cursor.getCount();

        return total;
    }

    public int getTotalPointsWonOnDateNotDeleted(GregorianCalendar date, long userId){
        int total = 0;
        //select count(*) from Teste where strftime('%Y-%m-%d', date / 1000, 'unixepoch') = "2017-03-24"
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                "strftime('%Y-%m-%d', "+Timeline.COLUMN_TIMELINE_DATE+" / 1000, 'unixepoch') = strftime('%Y-%m-%d', ? / 1000, 'unixepoch') AND NOT("+Timeline.COLUMN_TIMELINE_IS_SOS+") AND "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+Timeline.COLUMN_TIMELINE_USER_ID +"=? AND "+Timeline.COLUMN_TIMELINE_STATE + " IS NOT NULL AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",
                new String[]{""+date.getTimeInMillis(),""+userId}, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            total += cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_TIME_POINTS));
            cursor.moveToNext();
        }

        cursor.close();

        return total;
    }

    public ArrayList<TimelineWeek> getTimeLine(long userId){
        TimelineItem activeTimelineItem = getActiveTimelineItem(userId);

        GregorianCalendar todayAux = new GregorianCalendar();
        todayAux.add(Calendar.DAY_OF_MONTH, -7);
        String lastWeek = Utils.getWeekNumber(todayAux);
        boolean hasLateMedicenePreviousWeek = hasLateMedicineOnWeek(lastWeek);

        ArrayList<TimelineWeek> list = new ArrayList<>();
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND NOT("+ Timeline.COLUMN_TIMELINE_DELETED +")",
                new String[]{""+userId}, Timeline.COLUMN_TIMELINE_WEEK_NUMBER, null, Timeline.COLUMN_TIMELINE_WEEK_NUMBER+" ASC");

        cursor.moveToFirst();


        GregorianCalendar dateAux = new GregorianCalendar();
        String thisWeek = Utils.getWeekNumber(dateAux);//dateAux.get(Calendar.YEAR)+"_"+dateAux.get(Calendar.WEEK_OF_YEAR);

        String auxWeekNumber;
        TimelineWeek auxTimeLineWeek = null;

        boolean open = false;

        while (!cursor.isAfterLast()) {
            auxWeekNumber = cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_WEEK_NUMBER));
            /*before
            * if (!open && thisWeek.equals(auxWeekNumber)){
                            open = true;
                        }
            * */

            //TAG M6
            if (!open && thisWeek.equals(auxWeekNumber)){
                open = true;
            } else if(activeTimelineItem!=null && auxWeekNumber.equals(activeTimelineItem.getWeekNumber())){
                open = true;
            } else if(hasLateMedicenePreviousWeek && auxWeekNumber.equals(lastWeek)){
                open = true;
            }

            auxTimeLineWeek = new TimelineWeek(auxWeekNumber,open);

            //if(auxTimeLineWeek.isOpen()){
                auxTimeLineWeek.setItems(getTimeLineItems(userId,auxWeekNumber));
            //}

            list.add(auxTimeLineWeek);

            cursor.moveToNext();
        }

        cursor.close();

        for(TimelineWeek tw : list) {
            Log.d("WEEK",tw.getWeekNumber()+"_"+tw.isOpen()+"-"+tw.getItems().size());
        }

        return list;
    }

    private boolean hasLateMedicineOnWeek(String week) {
        boolean exist;

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE,
                allColumns, Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+
                        Timeline.COLUMN_TIMELINE_STATE+" IS NULL AND "+
                        Timeline.COLUMN_TIMELINE_WEEK_NUMBER+" = ? AND "+
                        "strftime('%s',"+Timeline.COLUMN_TIMELINE_END_TIME+" / 1000, 'unixepoch', '10 hours') > strftime('%s',strftime('%Y-%m-%d %H:%M','now')) AND "+
                        "NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",
                new String[]{""+week}, null, null,null);

        exist = cursor.moveToFirst();

        cursor.close();

        return exist;
    }

    public boolean hasMissMedicineOnWeek(String week) {
        boolean exist;

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE,
                allColumns, Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+
                        Timeline.COLUMN_TIMELINE_STATE+" = ? AND "+
                        Timeline.COLUMN_TIMELINE_WEEK_NUMBER+" = ? AND "+
                        "strftime('%s',"+Timeline.COLUMN_TIMELINE_END_TIME+" / 1000, 'unixepoch', '10 hours') > strftime('%s',strftime('%Y-%m-%d %H:%M','now')) AND "+
                        "NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",
                new String[]{TimelineItem.STATE_MISSED+""+week}, null, null,null);

        exist = cursor.moveToFirst();

        cursor.close();

        return exist;
    }

    public boolean hasTakenMedicineOnWeek(String week) {
        boolean exist;

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE,
                allColumns, Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+
                        Timeline.COLUMN_TIMELINE_STATE+" = ? AND "+
                        Timeline.COLUMN_TIMELINE_WEEK_NUMBER+" = ? AND "+
                        "strftime('%s',"+Timeline.COLUMN_TIMELINE_END_TIME+" / 1000, 'unixepoch', '10 hours') > strftime('%s',strftime('%Y-%m-%d %H:%M','now')) AND "+
                        "NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",
                new String[]{TimelineItem.STATE_DONE,""+week}, null, null,null);

        exist = cursor.moveToFirst();

        cursor.close();

        return exist;
    }

    private TimelineItem cursorToTimelineItem(Cursor cursor){
        UserMedicine auxUserMedicine = null;
        boolean isSOS = cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_IS_SOS)) == 1;

        if(!cursor.isNull(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_MEDICINE_ID))){
            long idMed = cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_MEDICINE_ID));
            if(isSOS){
                auxUserMedicine = AppController.getmInstance().getMedicineDataSource().getUserSOSMedicine(idMed);
            }else {
                auxUserMedicine = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicine(idMed);
            }
        }

        UserBadge auxUserBadge = null;
        if(!cursor.isNull(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_BADGE_ID))){
            auxUserBadge = AppController.getmInstance().getBadgeDataSource().getUserBadge(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_BADGE_ID)));
        }

        String auxNote = null;
        if(!cursor.isNull(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_NOTE))){
            auxNote = cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_NOTE));
        }

        GregorianCalendar auxTaken = null;
        if(!cursor.isNull(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_TAKEN))){
            auxTaken = new GregorianCalendar();
            auxTaken.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_TAKEN)));
        }

        String auxState = null;
        if(!cursor.isNull(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_STATE))){
            auxState = cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_STATE));
        }

        GregorianCalendar auxDate = new GregorianCalendar();
        auxDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_DATE)));

        GregorianCalendar auxStartTime = new GregorianCalendar();
        auxStartTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_START_TIME)));

        GregorianCalendar auxEndTime = new GregorianCalendar();
        auxEndTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_END_TIME)));

        FaseTime auxFaseTime = null;
        String faseTimeCode = cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_FASE_TIME_CODE));

        if(faseTimeCode != null && !faseTimeCode.isEmpty()){
            auxFaseTime = new FaseTime(faseTimeCode);
        }

        Poll auxUserPoll = null;
        if(!cursor.isNull(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_POLL_ID))){
            auxUserPoll = AppController.getmInstance().getPollDataSource().getPoll(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_POLL_ID)));
        }

        return new TimelineItem(cursor.getLong(cursor.getColumnIndex(Timeline._ID)),
                auxUserMedicine,
                auxUserBadge,
                auxDate,
                auxStartTime,
                auxEndTime,
                cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_TIME_POINTS)),
                cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_WEEK_NUMBER)),
                isSOS,
                auxNote,
                auxState,
                auxTaken,
                cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_POINTS_WON)),
                cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_DOSAGE)),
                auxFaseTime,
                auxUserPoll,
                cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES)),
                cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_NID)),
                cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_MAIN_TIME)) == 1,
                cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_LATITUDE)),
                cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_LONGITUDE)));

    }

    public ArrayList<TimelineItem> getTimeLineItems(long userId, String weekNumber){
        /*ArrayList<TimelineItem> list = new ArrayList<>();
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_WEEK_NUMBER + " = ?",
                new String[]{""+userId,weekNumber},null, null, Timeline.COLUMN_TIMELINE_WEEK_NUMBER+","+Timeline.COLUMN_TIMELINE_DATE+","+Timeline.COLUMN_TIMELINE_TAKEN+","+Timeline.COLUMN_TIMELINE_START_TIME+","+Timeline._ID+" ASC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;*/


        String[] tableColumns = new String[] {
                Timeline._ID,
                Timeline.COLUMN_TIMELINE_MEDICINE_ID,
                Timeline.COLUMN_TIMELINE_BADGE_ID,
                Timeline.COLUMN_TIMELINE_USER_ID,
                Timeline.COLUMN_TIMELINE_DATE,
                Timeline.COLUMN_TIMELINE_START_TIME,
                Timeline.COLUMN_TIMELINE_END_TIME,
                Timeline.COLUMN_TIMELINE_TIME_POINTS,
                Timeline.COLUMN_TIMELINE_WEEK_NUMBER,
                Timeline.COLUMN_TIMELINE_IS_FIRST,
                Timeline.COLUMN_TIMELINE_IS_SOS,
                Timeline.COLUMN_TIMELINE_NID,
                Timeline.COLUMN_TIMELINE_NOTE,
                Timeline.COLUMN_TIMELINE_STATE,
                Timeline.COLUMN_TIMELINE_TAKEN,
                Timeline.COLUMN_TIMELINE_POINTS_WON,
                Timeline.COLUMN_TIMELINE_DOSAGE,
                Timeline.COLUMN_TIMELINE_FASE_TIME_CODE,
                Timeline.COLUMN_TIMELINE_POLL_ID,
                Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES,
                Timeline.COLUMN_TIMELINE_MAIN_TIME,
                Timeline.COLUMN_TIMELINE_LATITUDE,
                Timeline.COLUMN_TIMELINE_LONGITUDE,
                "(CASE WHEN "+Timeline.COLUMN_TIMELINE_TAKEN+" IS NOT NULL THEN "+Timeline.COLUMN_TIMELINE_TAKEN+" ELSE (CASE WHEN "+Timeline.COLUMN_TIMELINE_START_TIME+" IS NOT NULL THEN "+Timeline.COLUMN_TIMELINE_START_TIME+" ELSE strftime('%s',strftime('%Y-%m-%d %H:%M',"+Timeline.COLUMN_TIMELINE_DATE+" / 1000, 'unixepoch'))*1000 END) END) AS taken_aux"
                //"(CASE WHEN "+Timeline.COLUMN_TIMELINE_TAKEN+" IS NOT NULL THEN "+Timeline.COLUMN_TIMELINE_TAKEN+" ELSE strftime('%s','now')*1000 END) AS taken_aux"
        };

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, tableColumns,
                Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_WEEK_NUMBER + " = ? AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",
                new String[]{""+userId,weekNumber},null, null,
                Timeline.COLUMN_TIMELINE_WEEK_NUMBER+",strftime('%s',strftime('%Y-%m-%d',"+Timeline.COLUMN_TIMELINE_DATE+" / 1000, 'unixepoch'))*1000,taken_aux,"+Timeline.COLUMN_TIMELINE_START_TIME+","+Timeline._ID+" ASC");
        cursor.moveToFirst();

        ArrayList<TimelineItem> list = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public int updateAllMissed(Long userId){
        /*ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_STATE,TimelineItem.STATE_MISSED);
        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON,0);

        return database.update(Timeline.TABLE_TIMELINE, args, "strftime('%s',strftime('%Y-%m-%d',"+ Timeline.COLUMN_TIMELINE_START_TIME+" / 1000, 'unixepoch')) <= strftime('%s',strftime('%Y-%m-%d','now','-1 day')) AND " +
                "NOT("+Timeline.COLUMN_TIMELINE_IS_SOS+") AND "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+
                Timeline.COLUMN_TIMELINE_STATE+" IS NULL AND timeline_user_id = ?", new String[]{""+userId});*/

        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_STATE,TimelineItem.STATE_MISSED);
        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON,0);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);

        return database.update(Timeline.TABLE_TIMELINE, args,
                "strftime('%s',"+Timeline.COLUMN_TIMELINE_END_TIME+" / 1000, 'unixepoch', '10 hours') <= strftime('%s',strftime('%Y-%m-%d %H:%M','now')) AND " +
                "NOT("+Timeline.COLUMN_TIMELINE_IS_SOS+") AND "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND "+
                Timeline.COLUMN_TIMELINE_STATE+" IS NULL AND timeline_user_id = ? AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")", new String[]{""+userId});
    }

    public boolean updateTimelineState(Long id, String newState) {
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_STATE,newState);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);

        if(newState.equals(TimelineItem.STATE_MISSED)){
            args.put(Timeline.COLUMN_TIMELINE_POINTS_WON,0);
        }

        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+id}) > 0;
    }

    public boolean updateTimelineStateNotes(Long id, String newState, String notes) {
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_STATE,newState);
        args.put(Timeline.COLUMN_TIMELINE_NOTE, notes);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);
        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON,0);

        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+id}) > 0;
    }

    public TimelineItem getActiveTimelineItem(long userId) {
        TimelineItem itemTimeline = null;


        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND NOT("+Timeline.COLUMN_TIMELINE_IS_SOS+") AND "+
                Timeline.COLUMN_TIMELINE_END_TIME+" > strftime('%s','now')*1000 AND "+Timeline.COLUMN_TIMELINE_TAKEN+" IS NULL AND "+Timeline.COLUMN_TIMELINE_STATE + " IS NULL AND "+
                "NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";


        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                whereClause,
                new String[]{""+userId},null, null, Timeline.COLUMN_TIMELINE_START_TIME+","+Timeline._ID+" ASC","1");

        if(cursor.moveToFirst()){
            itemTimeline = cursorToTimelineItem(cursor);
        }


        cursor.close();

        if(itemTimeline == null){
            whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +Timeline.COLUMN_TIMELINE_POLL_ID+" IS NOT NULL AND "+
                    Timeline.COLUMN_TIMELINE_END_TIME+" > strftime('%s','now')*1000 AND "+Timeline.COLUMN_TIMELINE_TAKEN+" IS NULL";


            cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                    whereClause,
                    new String[]{""+userId},null, null, Timeline.COLUMN_TIMELINE_START_TIME+","+Timeline._ID+" ASC","1");

            if(cursor.moveToFirst()){
                itemTimeline = cursorToTimelineItem(cursor);
            }


            cursor.close();
        }

        return itemTimeline;
    }

    public boolean markMedicineTaken(int pointsWon, String newState, long timelineItemId, GregorianCalendar dateTaken){
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON,pointsWon);
        args.put(Timeline.COLUMN_TIMELINE_STATE,newState);
        args.put(Timeline.COLUMN_TIMELINE_TAKEN,dateTaken.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);


        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+timelineItemId}) > 0;
    }

    public boolean changeTimelineItenDate(GregorianCalendar startTime, GregorianCalendar endTime,  long timelineItemId){
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_START_TIME,startTime.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_END_TIME,endTime.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);


        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+timelineItemId}) > 0;
    }

    public BigDecimal getStatsGlobal(Long userId){
        BigDecimal value = new BigDecimal(0);

        String[] tableColumns = new String[] {
                "count(_id) as total",
                "SUM(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_DONE+"\" then 1 else " +
                        "(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_LATE+"\" then 1 else 0 end) end) as done_med"
        };

        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND not("+Timeline.COLUMN_TIMELINE_IS_SOS+") and "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL and "+Timeline.COLUMN_TIMELINE_STATE+" IS NOT NULL AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";


        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, tableColumns, whereClause, new String[]{""+userId}, null, null, null);

        if(cursor.moveToFirst()){
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            int totalMedDone = cursor.getInt(cursor.getColumnIndex("done_med"));

            BigDecimal totalMedBig = new BigDecimal(total);
            BigDecimal totalMedDoneBig = new BigDecimal(totalMedDone);

            if(totalMedBig.compareTo(BigDecimal.ZERO) != 0) {
                value = (new BigDecimal(100).multiply(totalMedDoneBig)).divide(totalMedBig, 0, RoundingMode.HALF_DOWN);
            }
        }

        cursor.close();

        return value;
    }

    public BigDecimal getStatsLastMonth(Long userId) {
        BigDecimal value = new BigDecimal(0);

        String[] tableColumns = new String[] {
                "count(_id) as total",
                "SUM(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_DONE+"\" then 1 else " +
                        "(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_LATE+"\" then 1 else 0 end) end) as done_med"
        };

        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND not("+Timeline.COLUMN_TIMELINE_IS_SOS+") and "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL and "+Timeline.COLUMN_TIMELINE_STATE+" IS NOT NULL AND "+
                Timeline.COLUMN_TIMELINE_START_TIME + " BETWEEN strftime('%s',datetime('now','-1 month'))*1000 AND strftime('%s',datetime('now'))*1000 AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, tableColumns, whereClause, new String[]{""+userId}, null, null, null);

        if(cursor.moveToFirst()){
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            int totalMedDone = cursor.getInt(cursor.getColumnIndex("done_med"));

            BigDecimal totalMedBig = new BigDecimal(total);
            BigDecimal totalMedDoneBig = new BigDecimal(totalMedDone);

            if(totalMedBig.compareTo(BigDecimal.ZERO) != 0) {
                value = (new BigDecimal(100).multiply(totalMedDoneBig)).divide(totalMedBig, 0, RoundingMode.HALF_DOWN);
            }
        }

        cursor.close();

        return value;
    }

    public BigDecimal getStatsLast7Days(Long userId) {
        BigDecimal value = new BigDecimal(0);

        String[] tableColumns = new String[] {
                "count(_id) as total",
                "SUM(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_DONE+"\" then 1 else " +
                        "(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_LATE+"\" then 1 else 0 end) end) as done_med"
        };

        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND not("+Timeline.COLUMN_TIMELINE_IS_SOS+") and "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL and "+Timeline.COLUMN_TIMELINE_STATE+" IS NOT NULL AND "+
                Timeline.COLUMN_TIMELINE_START_TIME + " BETWEEN strftime('%s',datetime('now','-7 day'))*1000 AND strftime('%s',datetime('now'))*1000 AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, tableColumns, whereClause, new String[]{""+userId}, null, null, null);

        if(cursor.moveToFirst()){
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            int totalMedDone = cursor.getInt(cursor.getColumnIndex("done_med"));

            BigDecimal totalMedBig = new BigDecimal(total);
            BigDecimal totalMedDoneBig = new BigDecimal(totalMedDone);

            if(totalMedBig.compareTo(BigDecimal.ZERO) != 0) {
                value = (new BigDecimal(100).multiply(totalMedDoneBig)).divide(totalMedBig, 0, RoundingMode.HALF_DOWN);
            }
        }

        cursor.close();

        return value;
    }

    /**
     * Added by Alexandre Rocha - Start point
     **/


    /**
     * Calculates medication adherence for a given week
     * @param userId the user's ID
     * @param daysDiffToTodayAux the day difference between today and the start of the desired week
     * @return the user's medication adherence for the desired week
     */
    public BigDecimal getWeeklyStatsCustomInterval(Long userId, int daysDiffToTodayAux) {

        BigDecimal value = new BigDecimal(0);
        String startDate, endDate;

        //in case start and end day are the same
        if(daysDiffToTodayAux == 0){
            startDate = ", '-1 day'";
            endDate="";
        }

        else{
            startDate = ", '-" + daysDiffToTodayAux + " day'";

            int endDateAux = daysDiffToTodayAux - 6;

            // if the week hasn't finished yet, its calculated the adherence up until today
            if(endDateAux<=0){
                endDate="";
            }
            else {
                endDate = ", '-" + endDateAux + " day'";
            }
        }

       // String daysDiffToToday = "-" + daysDiffToTodayAux + " day";
        //String endDate;



        String[] tableColumns = new String[] {
                "count(_id) as total",
                "SUM(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_DONE+"\" then 1 else " +
                        "(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_LATE+"\" then 1 else 0 end) end) as done_med"
        };

        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND not("+Timeline.COLUMN_TIMELINE_IS_SOS+") and "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL and "+Timeline.COLUMN_TIMELINE_STATE+" IS NOT NULL AND "+
                Timeline.COLUMN_TIMELINE_START_TIME + " BETWEEN strftime('%s',datetime('now' " + startDate + "))*1000 AND strftime('%s',datetime('now' " + endDate + "))*1000 AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, tableColumns, whereClause, new String[]{""+userId}, null, null, null);

        if(cursor.moveToFirst()){
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            int totalMedDone = cursor.getInt(cursor.getColumnIndex("done_med"));

            BigDecimal totalMedBig = new BigDecimal(total);
            BigDecimal totalMedDoneBig = new BigDecimal(totalMedDone);

            if(totalMedBig.compareTo(BigDecimal.ZERO) != 0) {
                value = (new BigDecimal(100).multiply(totalMedDoneBig)).divide(totalMedBig, 0, RoundingMode.HALF_DOWN);
            }
        }

        cursor.close();

        return value;
    }


    /**
     * Calculates medication adherence for a given custom time interval
     * @param userId the user9s ID
     * @param daysDiffsToToday a list containing the days difference between today and the start of the time interval, as well as the days difference between today and the end of the time interval, respectively
     * @return the user's medication adherence for the desired custom time interval
     */
    public BigDecimal getStatsCustomInterval(Long userId, ArrayList<Integer> daysDiffsToToday) {
        BigDecimal value = new BigDecimal(0);

        String daysDiffFromStartToToday = "-" + daysDiffsToToday.get(0) + " day";

        String daysDiffsFromEndToToday = "-" + daysDiffsToToday.get(1) + " day";

        String[] tableColumns = new String[] {
                "count(_id) as total",
                "SUM(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_DONE+"\" then 1 else " +
                        "(case when "+Timeline.COLUMN_TIMELINE_STATE+"=\""+TimelineItem.STATE_LATE+"\" then 1 else 0 end) end) as done_med"
        };

        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND not("+Timeline.COLUMN_TIMELINE_IS_SOS+") and "+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL and "+Timeline.COLUMN_TIMELINE_STATE+" IS NOT NULL AND "+
                Timeline.COLUMN_TIMELINE_START_TIME + " BETWEEN strftime('%s',datetime('now','"+daysDiffFromStartToToday+"'))*1000 AND strftime('%s',datetime('now','"+daysDiffsFromEndToToday+"'))*1000 AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, tableColumns, whereClause, new String[]{""+userId}, null, null, null);

        if(cursor.moveToFirst()){
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            int totalMedDone = cursor.getInt(cursor.getColumnIndex("done_med"));

            BigDecimal totalMedBig = new BigDecimal(total);
            BigDecimal totalMedDoneBig = new BigDecimal(totalMedDone);

            if(totalMedBig.compareTo(BigDecimal.ZERO) != 0) {
                value = (new BigDecimal(100).multiply(totalMedDoneBig)).divide(totalMedBig, 0, RoundingMode.HALF_DOWN);
            }
        }

        cursor.close();

        return value;
    }

    /**
     * Added by Alexandre Rocha - End point
     */


    public boolean updateTimelineNote(Long timelineId, String newNote) {
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_NOTE,newNote);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);

        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+timelineId}) > 0;
    }

    public GregorianCalendar getFirstMedicineDate(long userId) {
        GregorianCalendar firstMedDate = null;

        String whereClause = Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND NOT("+Timeline.COLUMN_TIMELINE_IS_SOS+") AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";


        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                whereClause,
                new String[]{""+userId},null, null, Timeline.COLUMN_TIMELINE_START_TIME+","+Timeline._ID+" ASC","1");

        if(cursor.moveToFirst()){
            firstMedDate = new GregorianCalendar();
            firstMedDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_DATE)));
        }


         cursor.close();

        return firstMedDate;
    }

    public ArrayList<TimelineItem> getAllUserNormalMedicinesShareDoctor(Long userId){
        ArrayList<TimelineItem> list = new ArrayList<>();

        String MY_QUERY = "SELECT tl.* " +
                "FROM "+Timeline.TABLE_TIMELINE+" AS tl LEFT OUTER JOIN "+ Medicine.TABLE_MEDICINE+" AS med ON " +
                "med."+Medicine._ID+" = tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" " +
                "WHERE med."+Medicine.COLUMN_MEDICINE_SHARE+" AND tl."+Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +
                "tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND NOT(tl."+Timeline.COLUMN_TIMELINE_DELETED+") ORDER BY tl."+Timeline._ID+" DESC" ;

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{""+userId});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<TimelineItem> getAllUserNormalMedicinesShareDoctor(Long userId, GregorianCalendar startDate, GregorianCalendar endDate){
        ArrayList<TimelineItem> list = new ArrayList<>();

        String MY_QUERY = "SELECT tl.* " +
                "FROM "+Timeline.TABLE_TIMELINE+" AS tl LEFT OUTER JOIN "+ Medicine.TABLE_MEDICINE+" AS med ON " +
                "med."+Medicine._ID+" = tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" " +
                "WHERE med."+Medicine.COLUMN_MEDICINE_SHARE+" AND tl."+Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +
                "tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND " +
                "tl."+Timeline.COLUMN_TIMELINE_DATE+" BETWEEN ? and ? AND NOT(tl."+Timeline.COLUMN_TIMELINE_DELETED+") ORDER BY tl."+Timeline._ID+" DESC" ;

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{""+userId,""+startDate.getTimeInMillis(),""+endDate.getTimeInMillis()});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    /*public boolean deleteMedicinesTiemLine(Long medicineId) {
        String table = Timeline.TABLE_TIMELINE;
        String whereClause = Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ?";
        String[] whereArgs = new String[] {""+medicineId};

        return database.delete(table, whereClause, whereArgs) > 0;
    }*/

    public TimelineItem getTimeLineItemPollNotComplete(Long userId, String pollType){
        TimelineItem item = null;

        String MY_QUERY = "SELECT tl.* " +
                "FROM "+Timeline.TABLE_TIMELINE+" AS tl LEFT OUTER JOIN "+ InspirersContract.Poll.TABLE_POLL+" AS p ON " +
                "p."+InspirersContract.Poll._ID+" = tl."+Timeline.COLUMN_TIMELINE_POLL_ID+" " +
                "WHERE p."+InspirersContract.Poll.COLUMN_POLL_TYPE+" = ? AND tl."+Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +
                "tl."+Timeline.COLUMN_TIMELINE_POLL_ID+" IS NOT NULL AND tl."+Timeline.COLUMN_TIMELINE_STATE+" IS NULL ORDER BY tl."+Timeline._ID+" DESC" ;

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{pollType,""+userId});

        if(cursor.moveToFirst()){
            item = cursorToTimelineItem(cursor);
        }


        cursor.close();

        return item;
    }

    public TimelineItem getTimeLineItemPollNotCompleteLast(Long userId, String pollType, GregorianCalendar lastDate){
        TimelineItem item = null;

        String MY_QUERY = "SELECT tl.* " +
                "FROM "+Timeline.TABLE_TIMELINE+" AS tl LEFT OUTER JOIN "+ InspirersContract.Poll.TABLE_POLL+" AS p ON " +
                "p."+InspirersContract.Poll._ID+" = tl."+Timeline.COLUMN_TIMELINE_POLL_ID+" " +
                "WHERE p."+InspirersContract.Poll.COLUMN_POLL_TYPE+" = ? AND tl."+Timeline.COLUMN_TIMELINE_USER_ID+" = ? AND " +
                "tl."+Timeline.COLUMN_TIMELINE_POLL_ID+" IS NOT NULL AND tl."+Timeline.COLUMN_TIMELINE_STATE+" = '"+TimelineItem.STATE_DONE+"' AND "+
                "tl."+Timeline.COLUMN_TIMELINE_TAKEN+" BETWEEN ? and ? ORDER BY tl."+Timeline._ID+" DESC" ;

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{pollType,""+userId,""+lastDate.getTimeInMillis(),""+(new GregorianCalendar()).getTimeInMillis()});

        if(cursor.moveToFirst()){
            item = cursorToTimelineItem(cursor);
        }


        cursor.close();

        return item;
    }

    public boolean updatePollTimelineTime(long timlineId, GregorianCalendar date, GregorianCalendar startTime, GregorianCalendar endTime) {
        String weekNumber = Utils.getWeekNumber(date);

        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_WEEK_NUMBER, weekNumber);
        args.put(Timeline.COLUMN_TIMELINE_DATE, date.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_START_TIME, startTime.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_END_TIME, endTime.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+timlineId}) > 0;
    }

    public boolean createTimelinePool(Long userId, Poll poll,GregorianCalendar date, GregorianCalendar startTime, GregorianCalendar endTime) {
        String weekNumber = Utils.getWeekNumber(date);

        ContentValues values = new ContentValues();

        values.put(Timeline.COLUMN_TIMELINE_POLL_ID, poll.getId());
        values.put(Timeline.COLUMN_TIMELINE_USER_ID, userId);
        values.put(Timeline.COLUMN_TIMELINE_DATE, date.getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_START_TIME, startTime.getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_END_TIME, endTime.getTimeInMillis());
        values.put(Timeline.COLUMN_TIMELINE_TIME_POINTS, poll.getPoolType().equals(Poll.POLL_TYPE_CARAT)?pointCARAT:(poll.getPoolType().equals(Poll.POLL_TYPE_WEEKLY)?pointPollWeek:pointPollDay));
        values.put(Timeline.COLUMN_TIMELINE_WEEK_NUMBER, weekNumber);
        values.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

        long insertId = database.insert(Timeline.TABLE_TIMELINE, null, values);

        boolean ok = insertId!=-1;

        return ok;
    }

    public TimelineItem getTimelineItem(Long timelineItemId) {
        TimelineItem timelineItem = null;

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE,
                allColumns, Timeline._ID+" = ?",new String[]{""+timelineItemId}, null, null,null);


        if (cursor.moveToFirst()) {
            timelineItem = cursorToTimelineItem(cursor);
        }

        cursor.close();

        return timelineItem;
    }

    public boolean setPollAnswered(int pointsWon, long timelineItemId, GregorianCalendar dateAnswer) {
        ContentValues args = new ContentValues();

        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON, pointsWon);
        args.put(Timeline.COLUMN_TIMELINE_STATE,TimelineItem.STATE_DONE);
        args.put(Timeline.COLUMN_TIMELINE_TAKEN,dateAnswer.getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);


        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+timelineItemId}) > 0;
    }


    public boolean deleteMedicinesFutureDaysTimeLineAndUpdateMain(Long medicineId) {
        String table = Timeline.TABLE_TIMELINE;
        String whereClause = Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_STATE+" IS NULL AND "+Timeline.COLUMN_TIMELINE_DATE+" >= ? AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")";

        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        //today.add(Calendar.DAY_OF_MONTH,1);

        String[] whereArgs = new String[] {""+medicineId,""+(today.getTimeInMillis())};

        ContentValues values = new ContentValues();

        values.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);
        values.put(Timeline.COLUMN_TIMELINE_DELETED,true);

        int total = database.update(table,values,whereClause,whereArgs);
        //int total = database.delete(table, whereClause, whereArgs);
        //Log.d("TOTAL",""+total);
        //Log.d("TOTALTIME",""+today.getTimeInMillis());

        ContentValues values2 = new ContentValues();
        values2.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);
        values2.put(Timeline.COLUMN_TIMELINE_MAIN_TIME,false);

        database.update(Timeline.TABLE_TIMELINE,values2,Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_MAIN_TIME,new String[] {""+medicineId});

        return total >= 0;
    }

    /*
    public boolean deleteMedicinesFutureDaysTimeLine(Long medicineId) {
        String table = Timeline.TABLE_TIMELINE;
        String whereClause = Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_STATE+" IS NULL AND "+Timeline.COLUMN_TIMELINE_DATE+" >= ?";

        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        //today.add(Calendar.DAY_OF_MONTH,1);

        String[] whereArgs = new String[] {""+medicineId,""+(today.getTimeInMillis())};

        int total = database.delete(table, whereClause, whereArgs);
        Log.d("TOTAL",""+total);
        Log.d("TOTALTIME",""+today.getTimeInMillis());


        return total >= 0;
    }*/

    public boolean updateTimelinePollId(long oldId, Long newId) {
        ContentValues args = new ContentValues();

        args.put(Timeline.COLUMN_TIMELINE_POLL_ID, newId);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);

        return database.update(Timeline.TABLE_TIMELINE, args, Timeline.COLUMN_TIMELINE_POLL_ID + " = ? ", new String[]{""+oldId}) > 0;
    }

    public boolean existSOSLast24H(){
        boolean exist = false;

        GregorianCalendar date = new GregorianCalendar();

        long now = date.getTimeInMillis();

        date.add(Calendar.HOUR_OF_DAY,-24);
        long nowMinus24 = date.getTimeInMillis();

        Cursor cursor = database.query(Timeline.TABLE_TIMELINE,
                allColumns, Timeline.COLUMN_TIMELINE_IS_SOS+" AND "+ Timeline.COLUMN_TIMELINE_TAKEN+" BETWEEN ? AND ? AND NOT("+Timeline.COLUMN_TIMELINE_DELETED+")",new String[]{""+nowMinus24, ""+now}, null, null,null);

        exist = cursor.moveToFirst();

        cursor.close();

        return exist;
    }

    public boolean updateRecognitionFailedTimes(Long itemId, int failedTimes){
        ContentValues args = new ContentValues();

        args.put(Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES, failedTimes);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);


        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+itemId}) > 0;
    }

    public ArrayList<TimelineItem> getAllTimelineForMedicine(Long medicine_id){
        ArrayList<TimelineItem> list = new ArrayList<>();

        String MY_QUERY = "SELECT tl.* " +
                "FROM "+Timeline.TABLE_TIMELINE+" AS tl LEFT OUTER JOIN "+ Medicine.TABLE_MEDICINE+" AS med ON " +
                "med."+Medicine._ID+" = tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" " +
                "WHERE med."+Medicine.COLUMN_MEDICINE_SHARE+" AND tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ? AND " +
                "tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND NOT(tl."+ Timeline.COLUMN_TIMELINE_DELETED+") ORDER BY tl."+Timeline._ID+" DESC" ;

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{""+medicine_id});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public TimelineItem getLastTimelineForMedicine(Long medicine_id){
        ArrayList<TimelineItem> list = new ArrayList<>();

        String MY_QUERY = "SELECT tl.* " +
                "FROM "+Timeline.TABLE_TIMELINE+" AS tl LEFT OUTER JOIN "+ Medicine.TABLE_MEDICINE+" AS med ON " +
                "med."+Medicine._ID+" = tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" " +
                "WHERE tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ? AND " +
                "tl."+Timeline.COLUMN_TIMELINE_MEDICINE_ID+" IS NOT NULL AND NOT(tl."+ Timeline.COLUMN_TIMELINE_DELETED+") ORDER BY tl."+Timeline._ID+" DESC" ;

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{""+medicine_id});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list.get(1);
    }

    public void deleteAllForUser(Long userId) {
        database.execSQL("DELETE FROM "+ Timeline.TABLE_TIMELINE+" WHERE "+Timeline.COLUMN_TIMELINE_USER_ID + " = "+userId);
    }

    public boolean createTimelineItem(Long userId, TimelineItem item) {
        ContentValues args = new ContentValues();

        if(item.isMedicine()){
            args.put(Timeline.COLUMN_TIMELINE_MEDICINE_ID, item.getMedicine().getId());
        }else if(item.getBadge() != null){
            args.put(Timeline.COLUMN_TIMELINE_BADGE_ID, item.getBadge().getId());
        }else if(item.getPoll() != null){
            args.put(Timeline.COLUMN_TIMELINE_POLL_ID, item.getPoll().getId());
        }

        args.put(Timeline.COLUMN_TIMELINE_USER_ID,userId);
        args.put(Timeline.COLUMN_TIMELINE_DATE,item.getDate().getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_START_TIME,item.getStartTime().getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_END_TIME,item.getEndTime().getTimeInMillis());
        args.put(Timeline.COLUMN_TIMELINE_TIME_POINTS,item.getTimePoints());
        args.put(Timeline.COLUMN_TIMELINE_WEEK_NUMBER,item.getWeekNumber());
        args.put(Timeline.COLUMN_TIMELINE_IS_SOS,item.isSOS());
        args.put(Timeline.COLUMN_TIMELINE_NID,item.getNid());
        args.put(Timeline.COLUMN_TIMELINE_NOTE,item.getTimeLineNote());

        if(item.getState()!=null) {
            args.put(Timeline.COLUMN_TIMELINE_STATE, item.getState());
        }

        if(item.getDateTaken()!=null){
            args.put(Timeline.COLUMN_TIMELINE_TAKEN, item.getDateTaken().getTimeInMillis());
        }

        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON, item.getPointWon());
        args.put(Timeline.COLUMN_TIMELINE_DOSAGE, item.getDosage());

        if(item.getFaseTime()!=null) {
            args.put(Timeline.COLUMN_TIMELINE_FASE_TIME_CODE, item.getFaseTime().getHourInitDesc());
        }

        args.put(Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES, item.getRecognitionFailedTimes());
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, false);
        args.put(Timeline.COLUMN_TIMELINE_MAIN_TIME, item.isMainTime());

        args.put(Timeline.COLUMN_TIMELINE_LATITUDE, item.getLatitude());
        args.put(Timeline.COLUMN_TIMELINE_LONGITUDE, item.getLongitude());

        long insertId = database.insert(Timeline.TABLE_TIMELINE, null, args);

        boolean ok = insertId!=-1;

        if(ok){
            item.setId(insertId);
        }

        return ok;
    }

    public ArrayList<TimelineItem> getTimelineItemNeedSyncForBadge(Long badgeId){
        ArrayList<TimelineItem> list = new ArrayList<>();
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                Timeline.COLUMN_TIMELINE_BADGE_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_NEED_SYNC,
                new String[]{""+badgeId},null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<TimelineItem> getTimelineItemNeedSyncForPoll(Long pollId){
        ArrayList<TimelineItem> list = new ArrayList<>();
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                Timeline.COLUMN_TIMELINE_POLL_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_NEED_SYNC,
                new String[]{""+pollId},null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToTimelineItem(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<InnerSyncTimelineItem> getTimelineItemNeedSyncForMedicine(Long pollId){
        ArrayList<InnerSyncTimelineItem> list = new ArrayList<>();
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                Timeline.COLUMN_TIMELINE_MEDICINE_ID+" = ? AND "+Timeline.COLUMN_TIMELINE_NEED_SYNC,
                new String[]{""+pollId},null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(new InnerSyncTimelineItem(
                    cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_USER_ID)),
                    cursorToTimelineItem(cursor),
                    cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_DELETED)) == 1));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public ArrayList<InnerSyncTimelineItem> getTimelineItemNeedSyncWithNid(long userToSyncID){
        ArrayList<InnerSyncTimelineItem> list = new ArrayList<>();
        Cursor cursor = database.query(Timeline.TABLE_TIMELINE, allColumns,
                Timeline.COLUMN_TIMELINE_NEED_SYNC+" AND "+Timeline.COLUMN_TIMELINE_NID + " IS NOT NULL AND "+Timeline.COLUMN_TIMELINE_USER_ID + " = ?",
                new String[]{""+userToSyncID},null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(new InnerSyncTimelineItem(
                    cursor.getString(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_USER_ID)),
                    cursorToTimelineItem(cursor),
                    cursor.getInt(cursor.getColumnIndex(Timeline.COLUMN_TIMELINE_DELETED)) == 1));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean setTimelineItemSynced(Long itemId, String nid) {
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,0);
        args.put(Timeline.COLUMN_TIMELINE_NID,nid);

        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+itemId}) > 0;
    }

    public boolean updateLatitudeLongitude(ArrayList<TimelineItem> timelineItemsNeedLocation, double lat, double lon) {
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_LATITUDE,lat);
        args.put(Timeline.COLUMN_TIMELINE_LONGITUDE,lon);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC, true);

        String[] ids = new String[timelineItemsNeedLocation.size()];
        String ids_qustion = "";

        int i = 0;
        for(TimelineItem item : timelineItemsNeedLocation){
            ids[i] = ""+item.getId();
            ids_qustion+=",?";

            i++;
        }

        ids_qustion = ids_qustion.substring(1);

        return database.update(Timeline.TABLE_TIMELINE, args, InspirersContract.Timeline._ID+" IN ("+ids_qustion+")", ids) > 0;

    }

    public boolean updatePointsWon(long timelineItemId, int newPoints) {
        ContentValues args = new ContentValues();
        args.put(Timeline.COLUMN_TIMELINE_POINTS_WON,newPoints);
        args.put(Timeline.COLUMN_TIMELINE_NEED_SYNC,true);


        return database.update(Timeline.TABLE_TIMELINE, args, Timeline._ID + " = ? ", new String[]{""+timelineItemId}) > 0;

    }
}
