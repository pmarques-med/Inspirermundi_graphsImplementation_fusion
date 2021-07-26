package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;
import com.bloomidea.inspirers.model.SyncPollCommentAux;
import com.bloomidea.inspirers.sync.InnerSyncPoll;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by michellobato on 26/04/17.
 */

public class PollDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { InspirersContract.Poll._ID,     //0
            InspirersContract.Poll.COLUMN_POLL_TITLE,               //1
            InspirersContract.Poll.COLUMN_POLL_TYPE,                //2
            InspirersContract.Poll.COLUMN_POLL_UPDATE_DATE,         //4
            InspirersContract.Poll.COLUMN_POLL_NID};                //4

    public PollDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createPoll(Poll poll){
        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put(InspirersContract.Poll.COLUMN_POLL_TITLE, poll.getTitle());
        values.put(InspirersContract.Poll.COLUMN_POLL_TYPE, poll.getPoolType());
        values.put(InspirersContract.Poll.COLUMN_POLL_UPDATE_DATE, poll.getUpdatedDate().getTimeInMillis());
        values.put(InspirersContract.Poll.COLUMN_POLL_NID, poll.getNid());

        long insertId = database.insert(InspirersContract.Poll.TABLE_POLL, null, values);

        poll.setId(insertId);

        boolean ok = insertId != -1;

        if (ok) {
            ok = AppController.getmInstance().getQuestionDataSource().createQuestions(poll.getListQuestions(), insertId);
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    private Poll cursorToPoll(Cursor cursor){
        Long id = cursor.getLong(cursor.getColumnIndex(InspirersContract.Poll._ID));

        GregorianCalendar auxUpdatedDate = new GregorianCalendar();
        auxUpdatedDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(InspirersContract.Poll.COLUMN_POLL_UPDATE_DATE)));

        return new Poll(id,
                cursor.getString(cursor.getColumnIndex(InspirersContract.Poll.COLUMN_POLL_TITLE)),
                cursor.getString(cursor.getColumnIndex(InspirersContract.Poll.COLUMN_POLL_TYPE)),
                auxUpdatedDate,
                AppController.getmInstance().getQuestionDataSource().getQuestions(id),
                cursor.getString(cursor.getColumnIndex(InspirersContract.Poll.COLUMN_POLL_NID)));
    }

    public TreeMap<String,Poll> getPolls(){
        TreeMap<String,Poll> list = new TreeMap<>();

        Cursor cursor = database.query(InspirersContract.Poll.TABLE_POLL,
                allColumns,null,null, null, null,InspirersContract.Poll._ID + " ASC");


        cursor.moveToFirst();

        Poll auxP = null;
        while (!cursor.isAfterLast()) {
            auxP = cursorToPoll(cursor);
            list.put(auxP.getPoolType(),auxP);

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean deletePoll(Poll poll){
        database.beginTransaction();

        boolean ok = true;

        for(Question q : poll.getListQuestions()){
            if(q instanceof QuestionMultipleChoice){
                ok = AppController.getmInstance().getAnswerDataSource().deleteAnswersForQuestion(q.getId());
            }

            if(ok){
                ok = AppController.getmInstance().getQuestionDataSource().deleteQuestion(q.getId());

                if(!ok){
                    break;
                }
            }else{
                break;
            }
        }

        if(ok) {
            String table = InspirersContract.Poll.TABLE_POLL;
            String whereClause = InspirersContract.Poll._ID + " = ?";
            String[] whereArgs = new String[]{"" + poll.getId()};

            ok = database.delete(table, whereClause, whereArgs) > 0;
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }

    public Poll getPoll(Long pollId){
        Poll poll = null;

        Cursor cursor = database.query(InspirersContract.Poll.TABLE_POLL,
                allColumns, InspirersContract.Poll._ID+" = ?",new String[]{""+pollId}, null, null,null);


        if(cursor.moveToFirst()) {
            poll = cursorToPoll(cursor);
        }

        cursor.close();

        return poll;
    }

    public long createAnswerPoll(Poll pollWithAnswers, Long userId, boolean needSync){
        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_POLL_ID, pollWithAnswers.getId());
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID, userId);
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE, new GregorianCalendar().getTimeInMillis());
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC, needSync);
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT, "");
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC_COMMENT, "0");

        long insertId = database.insert(InspirersContract.MyPoll.TABLE_MY_POLL, null, values);

        boolean ok = insertId != -1;

        if (ok) {
            for(Question q : pollWithAnswers.getListQuestions()){
                ContentValues values2 = new ContentValues();

                values2.put(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_ANSWER_MY_POLL_ID, insertId);
                values2.put(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID, q.getId());

                String valueAnswer = "";
                if(q instanceof QuestionMultipleChoice){
                    for(Answer a : ((QuestionMultipleChoice) q).getListAnswers()) {
                        if(a.isSelected()){
                            valueAnswer = ""+a.getId();
                            break;
                        }
                    }
                }else if(q instanceof QuestionYesNo){
                    valueAnswer = ((QuestionYesNo) q).isYes()?"1":"0";
                }else{
                    //QuestionSlider
                    valueAnswer = "" + ((QuestionSlider) q).getTotalSelected();
                }
                values2.put(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER, valueAnswer);

                long insertId2 = database.insert(InspirersContract.MyPollAnswer.TABLE_MY_POLL_ANSWER, null, values2);

                ok = insertId2 != -1;

                if(!ok){
                    break;
                }
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return insertId;
    }

    public ArrayList<ListPoll> getListAnsweredPolls(Long userId, Poll pollModel, int totalToLoad, int page){
        ArrayList<ListPoll> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.MyPoll.TABLE_MY_POLL,
                new String[]{InspirersContract.MyPoll._ID, InspirersContract.MyPoll.COLUMN_MY_POLL_POLL_ID,InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE, InspirersContract.MyPoll.COLUMN_MY_POLL_NID, InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT},
                InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID + " = ? AND "+InspirersContract.MyPoll.COLUMN_MY_POLL_POLL_ID + " = ?",
                new String[]{""+userId,""+pollModel.getId()}, null, null,
                InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE + " DESC",(totalToLoad*page)+","+totalToLoad);

        cursor.moveToFirst();

        Long myPollIdAux;

        while (!cursor.isAfterLast()) {
            myPollIdAux = cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll._ID));
            GregorianCalendar createdDate = new GregorianCalendar();
            createdDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE)));


            Poll auxPoll = new Poll(pollModel);

            Cursor cursor2 = database.query(InspirersContract.MyPollAnswer.TABLE_MY_POLL_ANSWER,
                    new String[]{InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID,InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER},
                    InspirersContract.MyPollAnswer.COLUMN_MY_POLL_ANSWER_MY_POLL_ID + " = ?",new String[]{""+myPollIdAux}, null, null,
                    InspirersContract.MyPollAnswer._ID + " ASC");

            cursor2.moveToFirst();
            Long questionIDAux;
            String questionAnswerAux;
            while (!cursor2.isAfterLast()) {
                questionIDAux = cursor2.getLong(cursor2.getColumnIndex(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID));
                questionAnswerAux = cursor2.getString(cursor2.getColumnIndex(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER));

                fillQuestionAnswer(auxPoll,questionIDAux,questionAnswerAux);

                cursor2.moveToNext();
            }


            list.add(new ListPoll(cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll._ID)),
                    auxPoll,
                    createdDate,
                    cursor.getString(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_NID)),
                    cursor.getString(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT))));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    private void fillQuestionAnswer(Poll auxPoll, Long questionIDAux ,String questionAnswerAux){
        for(Question q : auxPoll.getListQuestions()){
            if(q.getId().intValue() == questionIDAux){
                if(q instanceof QuestionMultipleChoice){
                    for(Answer a : ((QuestionMultipleChoice) q).getListAnswers()){
                        if(a.getId().intValue() == Integer.parseInt(questionAnswerAux)){
                            a.setSelected(true);
                            break;
                        }
                    }
                }else if(q instanceof QuestionYesNo){
                    ((QuestionYesNo) q).setYes(questionAnswerAux!=null && questionAnswerAux.equals("1"));
                }else{
                    //QuestionSlider
                    ((QuestionSlider) q).setTotalSelected(Integer.parseInt(questionAnswerAux), false);
                }

                break;
            }
        }
    }

    public ArrayList<InnerSyncPoll> getListAnsweredPollsToSync(TreeMap<String,Poll> pollModes, long userToSyncID){
        ArrayList<InnerSyncPoll> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.MyPoll.TABLE_MY_POLL,
                new String[]{InspirersContract.MyPoll._ID, InspirersContract.MyPoll.COLUMN_MY_POLL_POLL_ID,InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE, InspirersContract.MyPoll.COLUMN_MY_POLL_NID, InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT, InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID},
                InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC + " AND "+ InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID+" = ?",
                new String[]{""+userToSyncID}, null, null,
                InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE + " ASC");

        cursor.moveToFirst();

        Long myPollIdAux;
        Long myPollUserId;

        while (!cursor.isAfterLast()) {
            myPollUserId = cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID));
            myPollIdAux = cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_POLL_ID));


            GregorianCalendar createdDate = new GregorianCalendar();
            createdDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE)));

            Poll pollModel = null;
            for (Poll p : pollModes.values()) {
                if (p.getId().intValue() == myPollIdAux.intValue()) {
                    pollModel = p;
                    break;
                }
            }

            myPollIdAux = cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll._ID));

            if (pollModel != null) {
                Poll auxPoll = new Poll(pollModel);

                Cursor cursor2 = database.query(InspirersContract.MyPollAnswer.TABLE_MY_POLL_ANSWER,
                        new String[]{InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID, InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER},
                        InspirersContract.MyPollAnswer.COLUMN_MY_POLL_ANSWER_MY_POLL_ID + " = ?", new String[]{"" + myPollIdAux}, null, null,
                        InspirersContract.MyPollAnswer._ID + " ASC");

                cursor2.moveToFirst();
                Long questionIDAux;
                String questionAnswerAux;
                while (!cursor2.isAfterLast()) {
                    questionIDAux = cursor2.getLong(cursor2.getColumnIndex(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID));
                    questionAnswerAux = cursor2.getString(cursor2.getColumnIndex(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER));

                    fillQuestionAnswer(auxPoll, questionIDAux, questionAnswerAux);

                    cursor2.moveToNext();
                }

                list.add(new InnerSyncPoll(AppController.getmInstance().getUserDataSource().getUserUidFromId(myPollUserId),
                            new ListPoll(cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll._ID)),
                            auxPoll,
                            createdDate,
                            cursor.getString(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_NID)),
                            cursor.getString(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT)))));

                cursor.moveToNext();
            }
        }

        cursor.close();

        return list;
    }

    public boolean updatePollSync(Long myPollId, String nidServer) {
        ContentValues args = new ContentValues();
        args.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC,"0");
        args.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NID,nidServer);

        return database.update(InspirersContract.MyPoll.TABLE_MY_POLL, args, InspirersContract.MyPoll._ID+" = ?", new String[]{""+myPollId}) > 0;
    }

    public boolean updateMyPollComment(Long myPollId, String comment) {
        ContentValues args = new ContentValues();
        args.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC_COMMENT,"1");
        args.put(InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT,comment);

        return database.update(InspirersContract.MyPoll.TABLE_MY_POLL, args, InspirersContract.MyPoll._ID+" = ?", new String[]{""+myPollId}) > 0;
    }

    public ArrayList<SyncPollCommentAux> getMyPollsCommentNeedUpdate(long userToSyncID){
        ArrayList<SyncPollCommentAux> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.MyPoll.TABLE_MY_POLL,
                new String[]{InspirersContract.MyPoll._ID, InspirersContract.MyPoll.COLUMN_MY_POLL_NID, InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT},
                InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC_COMMENT + " AND "+ InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID + " = ?",
                new String[]{""+userToSyncID}, null, null,
                InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE + " ASC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(new SyncPollCommentAux(cursor.getLong(cursor.getColumnIndex(InspirersContract.MyPoll._ID)),
                    cursor.getString(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_NID)),
                    cursor.getString(cursor.getColumnIndex(InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT))));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean setMyPollCommentSynced(Long myPollId) {
        ContentValues args = new ContentValues();
        args.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC_COMMENT,"0");

        return database.update(InspirersContract.MyPoll.TABLE_MY_POLL, args, InspirersContract.MyPoll._ID+" = ?", new String[]{""+myPollId}) > 0;
    }

    public void deleteAllMyPollForUser(Long userId) {
        database.execSQL("DELETE FROM myPollAnswer where myPollAnswer._id IN (select myPollAnswer._id from myPollAnswer inner join myPoll on mypoll._id = myPollAnswer.my_poll_id where myPoll.user_id = "+userId+")");
        database.execSQL("DELETE FROM "+ InspirersContract.MyPoll.TABLE_MY_POLL+" WHERE "+InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID + " = "+userId);
    }

    public boolean createAnswerToPollFromWeb(Long userId, ListPoll auxP) {
        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_POLL_ID, auxP.getPoll().getId());
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_USER_ID, userId);
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_CREATED_DATE, auxP.getAsweredDate().getTimeInMillis());
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NID, auxP.getNid());
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC, false);
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_COMMENT, auxP.getComment());
        values.put(InspirersContract.MyPoll.COLUMN_MY_POLL_NEED_SYNC_COMMENT, "0");

        long insertId = database.insert(InspirersContract.MyPoll.TABLE_MY_POLL, null, values);

        boolean ok = insertId != -1;

        if (ok) {
            auxP.setMyPollId(insertId);

            for(Question q : auxP.getPoll().getListQuestions()){
                ContentValues values2 = new ContentValues();

                values2.put(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_ANSWER_MY_POLL_ID, insertId);
                values2.put(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ID, q.getId());

                String valueAnswer = "";
                if(q instanceof QuestionMultipleChoice){
                    for(Answer a : ((QuestionMultipleChoice) q).getListAnswers()) {
                        if(a.isSelected()){
                            valueAnswer = ""+a.getId();
                            break;
                        }
                    }
                }else if(q instanceof QuestionYesNo){
                    if (((QuestionYesNo) q).isYes() != null){
                        valueAnswer = ((QuestionYesNo) q).isYes()?"1":"0";
                    }else{
                        valueAnswer = "0";
                    }
                }else{
                    //QuestionSlider
                    valueAnswer = "" + ((QuestionSlider) q).getTotalSelected();
                }
                values2.put(InspirersContract.MyPollAnswer.COLUMN_MY_POLL_QUESTION_ANSWER, valueAnswer);

                long insertId2 = database.insert(InspirersContract.MyPollAnswer.TABLE_MY_POLL_ANSWER, null, values2);

                ok = insertId2 != -1;

                if(!ok){
                    break;
                }else{
                    q.setId(insertId2);
                }
            }
        }

        if(ok) {
            database.setTransactionSuccessful();
        }

        database.endTransaction();

        return ok;
    }
}
