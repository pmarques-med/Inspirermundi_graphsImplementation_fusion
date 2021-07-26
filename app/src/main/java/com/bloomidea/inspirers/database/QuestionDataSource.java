package com.bloomidea.inspirers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;

import java.util.ArrayList;

/**
 * Created by michellobato on 26/04/17.
 */

public class QuestionDataSource {
    private SQLiteDatabase database;

    private String[] allColumns = { InspirersContract.Question._ID,     //0
            InspirersContract.Question.COLUMN_QUESTION_POLL_ID,         //1
            InspirersContract.Question.COLUMN_QUESTION_T,               //2
            InspirersContract.Question.COLUMN_QUESTION_TYPE,            //3
            InspirersContract.Question.COLUMN_QUESTION_WEB_ID};         //4

    public QuestionDataSource(MySQLiteHelper db) {
        this.database = db.getWritableDatabase();
    }

    public boolean createQuestions(ArrayList<Question> listQuestions, long poolID) {
        boolean ok = true;

        for(Question question : listQuestions) {
            ContentValues values = new ContentValues();

            values.put(InspirersContract.Question.COLUMN_QUESTION_POLL_ID, poolID);
            values.put(InspirersContract.Question.COLUMN_QUESTION_T, question.getQuestion());
            values.put(InspirersContract.Question.COLUMN_QUESTION_TYPE, question.getType());
            values.put(InspirersContract.Question.COLUMN_QUESTION_WEB_ID, question.getNid());

            long insertId = database.insert(InspirersContract.Question.TABLE_QUESTION, null, values);


            ok = insertId != -1;

            if(ok) {
                question.setId(new Long(insertId));

                if(question instanceof QuestionMultipleChoice){
                    QuestionMultipleChoice questMC = (QuestionMultipleChoice) question;

                    for(Answer a : questMC.getListAnswers()){
                        ok = AppController.getmInstance().getAnswerDataSource().createAnswer(a,insertId);

                        if(!ok){
                            break;
                        }
                    }
                }
            }else{
                break;
            }
        }

        return ok;
    }

    private Question cursorToQuestion(Cursor cursor) {
        Question aux = null;

        Long id = cursor.getLong(cursor.getColumnIndex(InspirersContract.Question._ID));
        String type = cursor.getString(cursor.getColumnIndex(InspirersContract.Question.COLUMN_QUESTION_TYPE));
        String text = cursor.getString(cursor.getColumnIndex(InspirersContract.Question.COLUMN_QUESTION_T));
        String nid = cursor.getString(cursor.getColumnIndex(InspirersContract.Question.COLUMN_QUESTION_WEB_ID));

        if(type.equals(Question.QUESTION_TYPE_MULTIPLE_CHOICE)){
            aux = new QuestionMultipleChoice(id,
                    nid,
                    text,
                    type,
                    AppController.getmInstance().getAnswerDataSource().getAswersForQuestion(id));
        }else if(type.equals(Question.QUESTION_TYPE_SLIDER)){
            aux = new QuestionSlider(id, nid, text, type, 0,false);
        }else{
            //QUESTION_TYPE_YES_NO
            aux = new QuestionYesNo(id,
                    nid,
                    text,
                    type,
                    null);
        }

        return aux;
    }

    public ArrayList<Question> getQuestions(Long pollId){
        ArrayList<Question> list = new ArrayList<>();

        Cursor cursor = database.query(InspirersContract.Question.TABLE_QUESTION,
                allColumns, InspirersContract.Question.COLUMN_QUESTION_POLL_ID+" = ?",new String[]{""+pollId}, null, null,InspirersContract.Question._ID + " ASC");


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            list.add(cursorToQuestion(cursor));

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    public boolean deleteQuestion(Long questionId) {
        String table = InspirersContract.Question.TABLE_QUESTION;
        String whereClause = InspirersContract.Question._ID+" = ?";
        String[] whereArgs = new String[] {""+questionId};

        return database.delete(table, whereClause, whereArgs) > 0;
    }
}
