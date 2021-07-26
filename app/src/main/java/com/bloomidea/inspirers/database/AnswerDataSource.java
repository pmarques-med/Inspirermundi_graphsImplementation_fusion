package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.model.Answer;

import java.util.ArrayList;

/**
 * Created by michellobato on 26/04/17.
 */

public class AnswerDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { InspirersContract.Answer._ID,     //0
            InspirersContract.Answer.COLUMN_ANSWER_QUESTION_ID,       //1
            InspirersContract.Answer.COLUMN_ANSWER_T,                 //2
            InspirersContract.Answer.COLUMN_ANSWER_WEB_ID};           //3

    public AnswerDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createAnswer(Answer answer, long questionID) {
        ContentValues values = new ContentValues();

        values.put(InspirersContract.Answer.COLUMN_ANSWER_QUESTION_ID, questionID);
        values.put(InspirersContract.Answer.COLUMN_ANSWER_T, answer.getText());
        values.put(InspirersContract.Answer.COLUMN_ANSWER_WEB_ID, answer.getNid());

        long insertId = database.insert(InspirersContract.Answer.TABLE_ANSWER, null, values);

        boolean ok = insertId!=-1;

        if(ok) {
            answer.setId(new Long(insertId));
        }

        return ok;
    }

    private Answer cursorToAnswer(Cursor cursor) {
        return new Answer(cursor.getLong(cursor.getColumnIndex(InspirersContract.Answer._ID)),
                cursor.getString(cursor.getColumnIndex(InspirersContract.Answer.COLUMN_ANSWER_WEB_ID)),
                cursor.getString(cursor.getColumnIndex(InspirersContract.Answer.COLUMN_ANSWER_T)),
                false);
    }

    public ArrayList<Answer> getAswersForQuestion(Long questionId){
        ArrayList<Answer> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.Answer.TABLE_ANSWER,
                allColumns, InspirersContract.Answer.COLUMN_ANSWER_QUESTION_ID+" = ?",new String[]{""+questionId}, null, null,InspirersContract.Answer._ID + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToAnswer(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean deleteAnswersForQuestion(Long questionId) {
        String table = InspirersContract.Answer.TABLE_ANSWER;
        String whereClause = InspirersContract.Answer.COLUMN_ANSWER_QUESTION_ID+" = ?";
        String[] whereArgs = new String[] {""+questionId};

        return database.delete(table, whereClause, whereArgs) > 0;
    }
}
