package com.bloomidea.inspirers.database;

import android.provider.BaseColumns;

/**
 * Created by michellobato on 15/03/17.
 */

public class InspirersContract {
    private InspirersContract(){}

    public static final class Users implements BaseColumns {
        private Users(){}

        public static final String TABLE_USER = "user";

        public static final String COLUMN_USER_UID = "uid";
        public static final String COLUMN_USER_TOKEN = "token";
        public static final String COLUMN_USER_START_DATE = "start_date";
        public static final String COLUMN_USER_SESSION_NAME = "session_name";
        public static final String COLUMN_USER_SESSION_ID = "session_id";
        public static final String COLUMN_USER_PUSH_ON = "push_on";
        public static final String COLUMN_USER_POINTS = "points";
        public static final String COLUMN_USER_PICTURE_URL = "picture_url";
        public static final String COLUMN_USER_PICTURE = "picture";
        public static final String COLUMN_USER_PASSWORD = "password";
        public static final String COLUMN_USER_NUM_GODCHILD = "num_godchild";
        public static final String COLUMN_USER_LOCAL_NOTIFICATION = "local_notifications";
        public static final String COLUMN_USER_NAME = "name";
        public static final String COLUMN_USER_MEDIA_AVL = "media_avl";
        public static final String COLUMN_USER_LEVEL = "level";
        public static final String COLUMN_USER_ISO_COUNTRY = "iso_country";
        public static final String COLUMN_USER_IS_ACTIVE = "is_active";
        public static final String COLUMN_USER_HOBBIES = "hobbies";
        public static final String COLUMN_USER_LANGUAGES = "languages";
        public static final String COLUMN_USER_FIRST_LOGIN = "first_login";
        public static final String COLUMN_USER_EMAIL = "email";
        public static final String COLUMN_USER_COUNTRY_NAME = "country_name";
        public static final String COLUMN_USER_COUNTRY_FLAG_URL = "country_flag_url";
        public static final String COLUMN_USER_COUNTRY_FLAG = "country_flag";
        public static final String COLUMN_USER_TOTAL_RATING = "total_rating";
        public static final String COLUMN_USER_STATS_WEEK = "stats_week";
        public static final String COLUMN_USER_STATS_MONTH = "stats_month";
        public static final String COLUMN_USER_STATS_EVER = "stats_ever";
        public static final String COLUMN_USER_ACTUAL_BONUS = "actual_bonus";
        public static final String COLUMN_USER_NEED_SYNC = "need_sync";
        public static final String COLUMN_USER_NID_PROFILE = "nid_profile";
        public static final String COLUMN_USER_NOTIF_TOKEN = "notifications_token";
        public static final String COLUMN_USER_SYNC_PIC = "sync_pic";
        public static final String COLUMN_USER_CARAT_TIME = "carat_time";
        public static final String COLUMN_USER_WEEK_POLL_ANSWER = "week_poll_answer";
        public static final String COLUMN_USER_PROVIDED_ID = "provided_id";
        public static final String COLUMN_USER_DEVICE_ID = "device_id";
        public static final String COLUMN_USER_PROVIDED_ID_DATE = "provided_id_date";
        public static final String COLUMN_USER_ACCEPTED_TERMS = "accepted_terms";
        public static final String COLUMN_USER_TERMS_OFF = "terms_off";
    }

    public static final class Medicine implements BaseColumns {
        private Medicine(){}

        public static final String TABLE_MEDICINE = "medicine";

        public static final String COLUMN_MEDICINE_USER_ID = "medicine_user_id";
        public static final String COLUMN_MEDICINE_TYPE = "type";
        public static final String COLUMN_MEDICINE_NAME = "name";
        public static final String COLUMN_MEDICINE_NID = "nid";
        public static final String COLUMN_MEDICINE_SHARE = "share";
        public static final String COLUMN_MEDICINE_DURATION = "duration";
        public static final String COLUMN_MEDICINE_START_DATE = "start_date";
        public static final String COLUMN_MEDICINE_NOTES = "notes";
        public static final String COLUMN_MEDICINE_IS_SOS = "is_sos";
        public static final String COLUMN_MEDICINE_SEVERITY = "severity";
        public static final String COLUMN_MEDICINE_TRIGGER = "trigger";
        public static final String COLUMN_MEDICINE_HEALTH_SERVICE = "health_service";
        public static final String COLUMN_MEDICINE_SOS_DOSAGE = "sos_dosage";
        public static final String COLUMN_MEDICINE_NEED_SYNC = "need_sync";
        public static final String COLUMN_MEDICINE_DELETED = "deleted";
    }

    public static class Inhaler implements BaseColumns {
        private Inhaler(){}

        public static final String TABLE_INHALER = "inhaler";

        public static final String COLUMN_INHALER_MEDICINE_ID = "inhaler_medicine_id";
        public static final String COLUMN_INHALER_ACTIVE = "active";
        public static final String COLUMN_INHALER_BARCODE = "barcode";
        public static final String COLUMN_INHALER_DOSAGE = "dosage";
        public static final String COLUMN_INHALER_TYPE = "type";
    }

    public static final class MedicineTime implements BaseColumns {
        private MedicineTime(){}

        public static final String TABLE_MEDICINE_TIME = "medicine_time";

        public static final String COLUMN_MEDICINE_TIME_USER_ID = "medicine_time_user_id";
        public static final String COLUMN_MEDICINE_TIME_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_MEDICINE_TIME_DAY_FASE = "day_fase";
        public static final String COLUMN_MEDICINE_TIME_DOSAGE = "dosage";
    }

    public static final class MedicineSchedule implements BaseColumns {
        private MedicineSchedule(){}

        public static final String TABLE_MEDICINE_SCHEDULE = "medicine_schedule";

        public static final String COLUMN_MEDICINE_SCHEDULE_MEDICINE_ID = "medicine_id";
        public static final String COLUMN_MEDICINE_SCHEDULE_AUX_CODE = "schedule_aux_code";
        public static final String COLUMN_MEDICINE_SCHEDULE_AUX_DESC = "schedule_aux_desc";
        public static final String COLUMN_MEDICINE_SCHEDULE_AUX_INTERVAL = "schedule_aux_interval";
        public static final String COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTEDOPTION = "schedule_days_selectedoption";
        public static final String COLUMN_MEDICINE_SCHEDULE_DAYS_SELECTED = "schedule_days_selected";
        public static final String COLUMN_MEDICINE_SCHEDULE_DAYS_INTERVAL = "schedule_days_interval";
    }

    public static final class Badges implements BaseColumns {
        private Badges(){}

        public static final String TABLE_BADGES = "badges";

        public static final String COLUMN_BADGES_USER_ID = "badges_user_id";
        public static final String COLUMN_BADGES_IDENT = "ident";
        public static final String COLUMN_BADGES_IS_SYNC = "is_sync";
        public static final String COLUMN_BADGES_DATE_WON = "date_won";
        public static final String COLUMN_BADGES_WEEK_NUMBER = "week_number";

    }

    public static final class Timeline implements BaseColumns {
        private Timeline(){}

        public static final String TABLE_TIMELINE = "timeline";

        public static final String COLUMN_TIMELINE_MEDICINE_ID = "timeline_medicine_id";
        public static final String COLUMN_TIMELINE_BADGE_ID = "timeline_badge_id";
        public static final String COLUMN_TIMELINE_USER_ID = "timeline_user_id";
        public static final String COLUMN_TIMELINE_DATE = "date";
        public static final String COLUMN_TIMELINE_START_TIME = "start_time";
        public static final String COLUMN_TIMELINE_END_TIME = "end_time";
        public static final String COLUMN_TIMELINE_TIME_POINTS = "time_points";
        public static final String COLUMN_TIMELINE_WEEK_NUMBER = "week_number";
        public static final String COLUMN_TIMELINE_IS_FIRST = "is_first";
        public static final String COLUMN_TIMELINE_IS_SOS = "is_sos";
        public static final String COLUMN_TIMELINE_NID = "nid";
        public static final String COLUMN_TIMELINE_NOTE = "note";
        public static final String COLUMN_TIMELINE_STATE = "state";
        public static final String COLUMN_TIMELINE_TAKEN = "taken";
        public static final String COLUMN_TIMELINE_POINTS_WON = "points_won";
        public static final String COLUMN_TIMELINE_DOSAGE = "dosage";
        public static final String COLUMN_TIMELINE_FASE_TIME_CODE = "fase_time_code";
        public static final String COLUMN_TIMELINE_POLL_ID = "timeline_poll_id";
        public static final String COLUMN_TIMELINE_RECOGNITION_FAILED_TIMES = "recognition_failed_times";
        public static final String COLUMN_TIMELINE_NEED_SYNC = "need_sync";
        public static final String COLUMN_TIMELINE_DELETED = "deleted";
        public static final String COLUMN_TIMELINE_MAIN_TIME = "main_time";
        public static final String COLUMN_TIMELINE_LATITUDE = "latitude";
        public static final String COLUMN_TIMELINE_LONGITUDE = "longitude";
    }

    public static final class Navigation implements BaseColumns {
        public static final String TABLE_NAVIGATION = "navigation";

        public static final String COLUMN_NAVIGATION_USER_ID = "navigation_user_id";
        public static final String COLUMN_NAVIGATION_DESCRIPTION = "description";
        public static final String COLUMN_NAVIGATION_START_TIME = "nav_start_time";
        public static final String COLUMN_NAVIGATION_END_TIME = "nav_end_time";
        public static final String COLUMN_NAVIGATION_TYPE = "type";
    }

    public static final class Poll implements BaseColumns {
        public static final String TABLE_POLL = "poll";

        public static final String COLUMN_POLL_TITLE = "title";
        public static final String COLUMN_POLL_TYPE = "type";
        public static final String COLUMN_POLL_UPDATE_DATE = "update_date";
        public static final String COLUMN_POLL_NID = "nid";
    }

    public static final class Question implements BaseColumns {
        public static final String TABLE_QUESTION = "question";

        public static final String COLUMN_QUESTION_POLL_ID = "poll_id";
        public static final String COLUMN_QUESTION_T = "question_text";
        public static final String COLUMN_QUESTION_TYPE = "question_type";
        public static final String COLUMN_QUESTION_WEB_ID = "web_id";
    }

    public static final class Answer implements BaseColumns {
        public static final String TABLE_ANSWER = "answer";

        public static final String COLUMN_ANSWER_QUESTION_ID = "question_id";
        public static final String COLUMN_ANSWER_T = "answer_text";
        public static final String COLUMN_ANSWER_WEB_ID = "answer_web_id";
    }

    public static final class MyPoll implements BaseColumns{
        public static final String TABLE_MY_POLL = "myPoll";

        public static final String COLUMN_MY_POLL_POLL_ID = "poll_id";
        public static final String COLUMN_MY_POLL_USER_ID = "user_id";
        public static final String COLUMN_MY_POLL_CREATED_DATE = "created_date";
        public static final String COLUMN_MY_POLL_NID = "nid";
        public static final String COLUMN_MY_POLL_NEED_SYNC = "need_sync";
        public static final String COLUMN_MY_POLL_COMMENT = "comment";
        public static final String COLUMN_MY_POLL_NEED_SYNC_COMMENT = "need_sync_comment";
    }

    public static final class MyPollAnswer implements BaseColumns{
        public static final String TABLE_MY_POLL_ANSWER = "myPollAnswer";

        public static final String COLUMN_MY_POLL_ANSWER_MY_POLL_ID = "my_poll_id";
        public static final String COLUMN_MY_POLL_QUESTION_ID = "question_id";
        public static final String COLUMN_MY_POLL_QUESTION_ANSWER = "question_answer";
    }
}
