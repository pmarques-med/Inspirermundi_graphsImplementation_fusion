package com.bloomidea.inspirers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.bloomidea.inspirers.database.InspirersContract.Answer;
import static com.bloomidea.inspirers.database.InspirersContract.Badges;
import static com.bloomidea.inspirers.database.InspirersContract.Inhaler;
import static com.bloomidea.inspirers.database.InspirersContract.Medicine;
import static com.bloomidea.inspirers.database.InspirersContract.MedicineTime;
import static com.bloomidea.inspirers.database.InspirersContract.MedicineSchedule;
import static com.bloomidea.inspirers.database.InspirersContract.MyPoll;
import static com.bloomidea.inspirers.database.InspirersContract.MyPollAnswer;
import static com.bloomidea.inspirers.database.InspirersContract.Navigation;
import static com.bloomidea.inspirers.database.InspirersContract.Poll;
import static com.bloomidea.inspirers.database.InspirersContract.Question;
import static com.bloomidea.inspirers.database.InspirersContract.Timeline;
import static com.bloomidea.inspirers.database.InspirersContract.Users;
/**
 * Created by michellobato on 15/03/17.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    //Table: Levels
    /*private static final String DATABASE_CREATE_LEVELS = "CREATE TABLE "+ Levels.TABLE_LEVELS+" ("+
            ""+ Levels._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Levels.COLUMN_LEVELS_LEVEL + " INTEGER NOT NULL, "+
            ""+ Levels.COLUMN_LEVELS_POINTS + " INTEGER NOT NULL"+
            ");";
*/
    //Table: Users
    private static final String DATABASE_CREATE_USERS = "CREATE TABLE "+ Users.TABLE_USER+" ("+
            ""+ Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Users.COLUMN_USER_UID + " STRING NOT NULL, "+
            ""+ Users.COLUMN_USER_TOKEN + " STRING NOT NULL, "+
            ""+ Users.COLUMN_USER_START_DATE + " BIGINT NOT NULL, "+
            ""+ Users.COLUMN_USER_SESSION_NAME + " STRING NOT NULL, "+
            ""+ Users.COLUMN_USER_SESSION_ID + " STRING NOT NULL, "+
            ""+ Users.COLUMN_USER_PUSH_ON + " BOOLEAN DEFAULT true NOT NULL, " +
            ""+ Users.COLUMN_USER_POINTS + " INTEGER DEFAULT (0) NOT NULL, "+
            ""+ Users.COLUMN_USER_PICTURE_URL + " STRING, "+
            ""+ Users.COLUMN_USER_PICTURE + " BLOB, "+
            ""+ Users.COLUMN_USER_PASSWORD + " STRING, "+
            ""+ Users.COLUMN_USER_NUM_GODCHILD + " INTEGER DEFAULT (0) NOT NULL, "+
            ""+ Users.COLUMN_USER_LOCAL_NOTIFICATION + " BOOLEAN DEFAULT true NOT NULL, "+
            ""+ Users.COLUMN_USER_NAME + " STRING NOT NULL, "+
            ""+ Users.COLUMN_USER_MEDIA_AVL + " NUMERIC (10, 2) NOT NULL, "+
            ""+ Users.COLUMN_USER_LEVEL + " INTEGER DEFAULT(0) NOT NULL, "+
            ""+ Users.COLUMN_USER_ISO_COUNTRY + " STRING, "+
            ""+ Users.COLUMN_USER_IS_ACTIVE + " BOOLEAN DEFAULT true NOT NULL, "+
            ""+ Users.COLUMN_USER_HOBBIES + " STRING, "+
            ""+ Users.COLUMN_USER_LANGUAGES + " STRING, "+
            ""+ Users.COLUMN_USER_FIRST_LOGIN + " BOOLEAN DEFAULT true NOT NULL, "+
            ""+ Users.COLUMN_USER_EMAIL + " STRING, "+
            ""+ Users.COLUMN_USER_COUNTRY_NAME + " STRING, "+
            ""+ Users.COLUMN_USER_COUNTRY_FLAG_URL + " STRING, "+
            ""+ Users.COLUMN_USER_COUNTRY_FLAG + " BLOB, "+
            ""+ Users.COLUMN_USER_TOTAL_RATING + " INTEGER DEFAULT (0) NOT NULL, "+
            ""+ Users.COLUMN_USER_STATS_WEEK + " NUMERIC (10, 2) NOT NULL DEFAULT (0), "+
            ""+ Users.COLUMN_USER_STATS_MONTH + " NUMERIC (10, 2) NOT NULL DEFAULT (0), "+
            ""+ Users.COLUMN_USER_STATS_EVER + " NUMERIC (10, 2) NOT NULL DEFAULT (0), "+
            ""+ Users.COLUMN_USER_ACTUAL_BONUS + " NUMERIC (10, 2) NOT NULL DEFAULT (1), "+
            ""+ Users.COLUMN_USER_NEED_SYNC + " BOOLEAN DEFAULT false NOT NULL, "+
            ""+ Users.COLUMN_USER_NID_PROFILE + " STRING, "+
            ""+ Users.COLUMN_USER_NOTIF_TOKEN + " STRING, "+
            ""+ Users.COLUMN_USER_SYNC_PIC + " BOOLEAN DEFAULT false NOT NULL, "+
            ""+ Users.COLUMN_USER_CARAT_TIME + " INTEGER DEFAULT -28 NOT NULL, "+
            ""+ Users.COLUMN_USER_WEEK_POLL_ANSWER + " BOOLEAN, "+
            ""+ Users.COLUMN_USER_PROVIDED_ID + " STRING, "+
            ""+ Users.COLUMN_USER_DEVICE_ID + " STRING,"+
            ""+ Users.COLUMN_USER_PROVIDED_ID_DATE + " BIGINT, "+
            ""+ Users.COLUMN_USER_ACCEPTED_TERMS + " BOOLEAN DEFAULT false NOT NULL, "+
            ""+ Users.COLUMN_USER_TERMS_OFF + " BOOLEAN DEFAULT false NOT NULL"+
            ");";

    //Table: Medicine
    private static final String DATABASE_CREATE_MEDICINE = "CREATE TABLE "+ Medicine.TABLE_MEDICINE+" ("+
            ""+ Medicine._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Medicine.COLUMN_MEDICINE_USER_ID+" INTEGER REFERENCES "+Users.TABLE_USER+"("+Users._ID+") NOT NULL, " +
            ""+ Medicine.COLUMN_MEDICINE_TYPE + " STRING NOT NULL, "+
            ""+ Medicine.COLUMN_MEDICINE_NAME + " STRING NOT NULL, "+
            ""+ Medicine.COLUMN_MEDICINE_NID + " STRING, "+
            ""+ Medicine.COLUMN_MEDICINE_SHARE + " BOOLEAN NOT NULL, "+
            ""+ Medicine.COLUMN_MEDICINE_DURATION + " INTEGER, "+
            ""+ Medicine.COLUMN_MEDICINE_START_DATE + " BIGINT NOT NULL, "+
            ""+ Medicine.COLUMN_MEDICINE_NOTES + " STRING, "+
            ""+ Medicine.COLUMN_MEDICINE_IS_SOS + " BOOLEAN, "+
            ""+ Medicine.COLUMN_MEDICINE_SEVERITY + " INTEGER, "+
            ""+ Medicine.COLUMN_MEDICINE_TRIGGER + " STRING, "+
            ""+ Medicine.COLUMN_MEDICINE_HEALTH_SERVICE + " STRING, "+
            ""+ Medicine.COLUMN_MEDICINE_SOS_DOSAGE + " INTEGER, "+
            ""+ Medicine.COLUMN_MEDICINE_NEED_SYNC + " BOOLEAN, "+
            ""+ Medicine.COLUMN_MEDICINE_DELETED + " BOOLEAN NOT NULL DEFAULT FALSE"+
            ");";

    //Table: Inhaler
    private static final String DATABASE_CREATE_INHALER = "CREATE TABLE "+ Inhaler.TABLE_INHALER+" ("+
            ""+ Inhaler._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Inhaler.COLUMN_INHALER_MEDICINE_ID+" INTEGER REFERENCES "+Medicine.TABLE_MEDICINE+"("+Medicine._ID+") NOT NULL, " +
            ""+ Inhaler.COLUMN_INHALER_ACTIVE + " BOOLEAN, "+
            ""+ Inhaler.COLUMN_INHALER_BARCODE + " STRING NOT NULL, "+
            ""+ Inhaler.COLUMN_INHALER_DOSAGE + " INTEGER"+
            ");";

    //Table: Medicine Time
    private static final String DATABASE_CREATE_MEDICINE_TIME = "CREATE TABLE "+ MedicineTime.TABLE_MEDICINE_TIME+" ("+
            ""+ MedicineTime._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ MedicineTime.COLUMN_MEDICINE_TIME_USER_ID+" INTEGER REFERENCES "+Users.TABLE_USER+"("+Users._ID+") NOT NULL, " +
            ""+ MedicineTime.COLUMN_MEDICINE_TIME_SCHEDULE_ID+" INTEGER REFERENCES "+ MedicineSchedule.TABLE_MEDICINE_SCHEDULE+"("+MedicineSchedule._ID+") NOT NULL, " +
            ""+ MedicineTime.COLUMN_MEDICINE_TIME_DAY_FASE + " STRING NOT NULL, "+
            ""+ MedicineTime.COLUMN_MEDICINE_TIME_DOSAGE + " INTEGER NOT NULL"+
            ");";

    //Table: Medicine Schedule
    private static final String DATABASE_CREATE_MEDICINE_SCHEDULE = "CREATE TABLE "+ MedicineSchedule.TABLE_MEDICINE_SCHEDULE+" ("+
            ""+ MedicineSchedule._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_MEDICINE_ID+" INTEGER REFERENCES "+Medicine.TABLE_MEDICINE+"("+Medicine._ID+") NOT NULL, " +
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_CODE+" INTEGER NOT NULL," +
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_DESC+" STRING NOT NULL, "+
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_AUX_INTERVAL+" INTEGER NOT NULL, "+
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTEDOPTION+" INTEGER NOT NULL, "+
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTED+" STRING, "+
            ""+ MedicineSchedule.COLUMN_MEDICINE_SCHEDULE_DAYS_INTERVAL+" INTEGER"+
            ");";


    //Table: Badges
    private static final String DATABASE_CREATE_BADGES = "CREATE TABLE "+ Badges.TABLE_BADGES+" ("+
            ""+ Badges._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Badges.COLUMN_BADGES_USER_ID+" INTEGER REFERENCES "+Users.TABLE_USER+"("+Users._ID+") NOT NULL, " +
            ""+ Badges.COLUMN_BADGES_IDENT + " STRING NOT NULL, "+
            ""+ Badges.COLUMN_BADGES_IS_SYNC + " BOOLEAN, "+
            ""+ Badges.COLUMN_BADGES_DATE_WON + " BIGINT NOT NULL, "+
            ""+ Badges.COLUMN_BADGES_WEEK_NUMBER + " STRING NOT NULL"+
            ");";

    //Table: Timeline
    private static final String DATABASE_CREATE_TIMELINE = "CREATE TABLE "+ Timeline.TABLE_TIMELINE+" ("+
            ""+ Timeline._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Timeline.COLUMN_TIMELINE_MEDICINE_ID+" INTEGER REFERENCES "+Medicine.TABLE_MEDICINE+"("+Medicine._ID+"), " +
            ""+ Timeline.COLUMN_TIMELINE_BADGE_ID+" INTEGER REFERENCES "+Badges.TABLE_BADGES+"("+Badges._ID+"), " +
            ""+ Timeline.COLUMN_TIMELINE_USER_ID+" INTEGER REFERENCES "+Users.TABLE_USER+"("+Users._ID+") NOT NULL, " +
            ""+ Timeline.COLUMN_TIMELINE_DATE + " BIGINT NOT NULL, "+
            ""+ Timeline.COLUMN_TIMELINE_START_TIME + " BIGINT, "+
            ""+ Timeline.COLUMN_TIMELINE_END_TIME + " BIGINT, "+
            ""+ Timeline.COLUMN_TIMELINE_TIME_POINTS + " INTEGER, "+
            ""+ Timeline.COLUMN_TIMELINE_WEEK_NUMBER + " STRING NOT NULL, "+
            ""+ Timeline.COLUMN_TIMELINE_IS_FIRST + " BOOLEAN, "+
            ""+ Timeline.COLUMN_TIMELINE_IS_SOS + " BOOLEAN DEFAULT FALSE, "+
            ""+ Timeline.COLUMN_TIMELINE_NID + " STRING, "+
            ""+ Timeline.COLUMN_TIMELINE_NOTE + " STRING, "+
            ""+ Timeline.COLUMN_TIMELINE_STATE + " STRING, "+
            ""+ Timeline.COLUMN_TIMELINE_TAKEN + " BIGINT, "+
            ""+ Timeline.COLUMN_TIMELINE_POINTS_WON + " INTEGER DEFAULT(0) NOT NULL, "+
            ""+ Timeline.COLUMN_TIMELINE_DOSAGE + " INTEGER, "+
            ""+ Timeline.COLUMN_TIMELINE_FASE_TIME_CODE + " STRING, "+
            ""+ Timeline.COLUMN_TIMELINE_POLL_ID+" INTEGER REFERENCES "+Poll.TABLE_POLL+"("+Poll._ID+"), " +
            ""+ Timeline.COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES+" INTEGER, " +
            ""+ Timeline.COLUMN_TIMELINE_NEED_SYNC+" BOOLEAN, " +
            ""+ Timeline.COLUMN_TIMELINE_DELETED+" BOOLEAN NOT NULL DEFAULT FALSE," +
            ""+ Timeline.COLUMN_TIMELINE_MAIN_TIME+" BOOLEAN NOT NULL DEFAULT FALSE, " +
            ""+ Timeline.COLUMN_TIMELINE_LATITUDE + " STRING, "+
            ""+ Timeline.COLUMN_TIMELINE_LONGITUDE + " STRING"+
            ");";

    //Table: Poll
    private static final String DATABASE_CREATE_POLL = "CREATE TABLE "+ Poll.TABLE_POLL+" ("+
            ""+ Poll._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Poll.COLUMN_POLL_TITLE + " STRING NOT NULL, "+
            ""+ Poll.COLUMN_POLL_TYPE + " STRING NOT NULL, "+
            ""+ Poll.COLUMN_POLL_UPDATE_DATE + " BIGINT NOT NULL, "+
            ""+ Poll.COLUMN_POLL_NID + " STRING NOT NULL"+
            ");";

    //Table: Question
    private static final String DATABASE_CREATE_QUESTION = "CREATE TABLE "+ Question.TABLE_QUESTION+" ("+
            ""+ Question._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Question.COLUMN_QUESTION_POLL_ID+" INTEGER REFERENCES "+Poll.TABLE_POLL+"("+Poll._ID+"), " +
            ""+ Question.COLUMN_QUESTION_T + " STRING NOT NULL, "+
            ""+ Question.COLUMN_QUESTION_TYPE + " STRING NOT NULL, "+
            ""+ Question.COLUMN_QUESTION_WEB_ID + " STRING NOT NULL"+
            ");";

    //Table: Answer
    private static final String DATABASE_CREATE_ANSWER = "CREATE TABLE "+ Answer.TABLE_ANSWER+" ("+
            ""+ Answer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Answer.COLUMN_ANSWER_QUESTION_ID+" INTEGER REFERENCES "+Question.TABLE_QUESTION+"("+Question._ID+"), " +
            ""+ Answer.COLUMN_ANSWER_T + " STRING NOT NULL, "+
            ""+ Answer.COLUMN_ANSWER_WEB_ID + " STRING NOT NULL"+
            ");";

    //Table: Navigation
    private static final String DATABASE_CREATE_NAVIGATION = "CREATE TABLE "+ Navigation.TABLE_NAVIGATION+" ("+
            ""+ Navigation._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ Navigation.COLUMN_NAVIGATION_USER_ID+" INTEGER REFERENCES "+Users.TABLE_USER+"("+Users._ID+"), " +
            ""+ Navigation.COLUMN_NAVIGATION_DESCRIPTION + " STRING NOT NULL, "+
            ""+ Navigation.COLUMN_NAVIGATION_START_TIME + " BIGINT NOT NULL, "+
            ""+ Navigation.COLUMN_NAVIGATION_END_TIME + " BIGINT, "+
            ""+ Navigation.COLUMN_NAVIGATION_TYPE + " STRING NOT NULL"+
            ");";

    //Table: MyPoll
    private static final String DATABASE_CREATE_MY_POLL = "CREATE TABLE "+ MyPoll.TABLE_MY_POLL+" ("+
            ""+ MyPoll._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ MyPoll.COLUMN_MY_POLL_POLL_ID+" INTEGER REFERENCES "+Poll.TABLE_POLL+"("+Poll._ID+"), " +
            ""+ MyPoll.COLUMN_MY_POLL_USER_ID+" INTEGER REFERENCES "+Users.TABLE_USER+"("+Users._ID+"), " +
            ""+ MyPoll.COLUMN_MY_POLL_CREATED_DATE + " STRING NOT NULL, "+
            ""+ MyPoll.COLUMN_MY_POLL_NID + " STRING, "+
            ""+ MyPoll.COLUMN_MY_POLL_NEED_SYNC + " BOOLEAN NOT NULL, "+
            ""+ MyPoll.COLUMN_MY_POLL_COMMENT + " STRING, "+
            ""+ MyPoll.COLUMN_MY_POLL_NEED_SYNC_COMMENT + " BOOLEAN NOT NULL"+
            ");";

    //Table: MyPoll
    private static final String DATABASE_CREATE_MY_POLL_ANSWER = "CREATE TABLE "+ MyPollAnswer.TABLE_MY_POLL_ANSWER+" ("+
            ""+ MyPollAnswer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            ""+ MyPollAnswer.COLUMN_MY_POLL_ANSWER_MY_POLL_ID+" INTEGER REFERENCES "+MyPoll.TABLE_MY_POLL+"("+MyPoll._ID+"), " +
            ""+ MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID +" INTEGER REFERENCES "+Question.TABLE_QUESTION+"("+Question._ID+"), " +
            ""+ MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER + " STRING NOT NULL"+
            ");";

    private static final String DATABASE_NAME = "inspirers.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("CREATE DB",""+DATABASE_VERSION);

        //db.execSQL(DATABASE_CREATE_LEVELS);
        db.execSQL(DATABASE_CREATE_USERS);
        db.execSQL(DATABASE_CREATE_MEDICINE);
        db.execSQL(DATABASE_CREATE_INHALER);
        db.execSQL(DATABASE_CREATE_MEDICINE_SCHEDULE);
        db.execSQL(DATABASE_CREATE_MEDICINE_TIME);
        db.execSQL(DATABASE_CREATE_BADGES);


        db.execSQL(DATABASE_CREATE_POLL);
        db.execSQL(DATABASE_CREATE_QUESTION);
        db.execSQL(DATABASE_CREATE_ANSWER);


        db.execSQL(DATABASE_CREATE_TIMELINE);


        db.execSQL(DATABASE_CREATE_NAVIGATION);

        db.execSQL(DATABASE_CREATE_MY_POLL);
        db.execSQL(DATABASE_CREATE_MY_POLL_ANSWER);

        Log.d("CREATED DB",""+DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("UPDATE DB",i + " -> " + i1);
    }
}
