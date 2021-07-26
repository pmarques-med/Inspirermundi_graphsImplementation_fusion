package com.bloomidea.inspirers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.TreeMapListener;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDMMType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class InviteDoctorActivity extends MyActiveActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 300;

    private boolean shareAll = true;
    private View dates_box;
    private View start_date_box;
    private View end_date_box;
    private GregorianCalendar startDate = null;
    private GregorianCalendar endDate = null;
    private TextView startDateTextView;
    private TextView endDateTextView;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy");
    private SimpleDateFormat dateFormatterTime = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormatterFiles = new SimpleDateFormat("dd/MM/yyyy");

    private ArrayList<ListPoll> listPollsCARATTotal;
    private ArrayList<ListPoll> listPollsWeeklySymptoms;

    private ArrayList<Chart> graphsList = new ArrayList<>();


    final IntegerValueFormatter integerValueFormatter = new IntegerValueFormatter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_doctor);

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.icon_imageView)).setImageResource(R.drawable.share_with_doctor_icon_green);
        ((TextView) findViewById(R.id.title_textView)).setText(R.string.share_with_doctor_btn);

        ((TextView) findViewById(R.id.share_doctor_text)).setText(Html.fromHtml(getString(R.string.share_with_doctor_desc)));

        /**
         * Added by Alexandre Rocha - Start point
         **/

//        dates_box = findViewById(R.id.dates_box);
//        start_date_box = findViewById(R.id.start_date_box);
//        end_date_box = findViewById(R.id.end_date_box);
//        startDateTextView = (TextView) findViewById(R.id.start_date_textView);
//        endDateTextView = (TextView) findViewById(R.id.end_date_textView);
//
//        ((SwitchCompat) findViewById(R.id.share_all_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                shareAll = b;
//                configureDates();
//            }
//        });
//
//        start_date_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GregorianCalendar dateAux = startDate;
//                if(dateAux == null)
//                    dateAux = new GregorianCalendar();
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(InviteDoctorActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
//                        startDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
//                        startDate.set(Calendar.HOUR_OF_DAY,0);
//                        startDate.set(Calendar.MINUTE,0);
//                        startDate.set(Calendar.SECOND,0);
//                        startDate.set(Calendar.MILLISECOND,0);
//
//                        setDateText(startDateTextView,startDate);
//                    }
//                }, dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH));
//
//                datePickerDialog.show();
//            }
//        });
//
//        end_date_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GregorianCalendar dateAux = endDate;
//                if(dateAux == null)
//                    dateAux = new GregorianCalendar();
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(InviteDoctorActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
//                        endDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
//                        endDate.set(Calendar.HOUR_OF_DAY,23);
//                        endDate.set(Calendar.MINUTE,59);
//                        endDate.set(Calendar.SECOND,59);
//                        endDate.set(Calendar.MILLISECOND,0);
//
//                        setDateText(endDateTextView,endDate);
//                    }
//                }, dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH));
//
//                datePickerDialog.show();
//            }
//        });

        String userName = AppController.getmInstance().getActiveUser().getUserName();
        ((EditText)findViewById(R.id.send_invite_text_editText)).setText(getResources().getString(R.string.doctor_email_text_hint, userName, userName));

        //configureDates();



        loadCaratData();

        /**
         * Added by Alexandre Rocha - End point
         **/

        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText) findViewById(R.id.doctor_email)).getText().toString();


                /**
                 * Added by Alexandre Rocha - Start point
                 **/

//                if(checkData(email)){
//                    ArrayList<TimelineItem> medicineList;
//                    String filename;
//                    if(shareAll) {
//                        medicineList = AppController.getmInstance().getTimelineDataSource().getAllUserNormalMedicinesShareDoctor(AppController.getmInstance().getActiveUser().getId());
//                        filename = getString(R.string.csv_all_file_name);
//                    }else{
//                        medicineList = AppController.getmInstance().getTimelineDataSource().getAllUserNormalMedicinesShareDoctor(AppController.getmInstance().getActiveUser().getId(),startDate,endDate);
//                        filename= getString(R.string.csv_time_interval_file_name);
//                    }
//
//                    if(medicineList == null || medicineList.isEmpty()){
//                        Toast.makeText(InviteDoctorActivity.this,R.string.noting_to_share,Toast.LENGTH_SHORT).show();
//                    }else{
//                        if(checkPermissions()) {
//                            Utils.createNavigationAction(getString(R.string.send_report_doctor));
//
//                            EditText editText = ((EditText) findViewById(R.id.send_invite_text_editText));
//
//
//                            String csv = convertToCsvString(medicineList);
//                            Uri csvFile = saveCSVToFile(csv,filename);
//
//                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_with_doctor_email_subject));
//                            sendIntent.putExtra(Intent.EXTRA_STREAM, csvFile);
//                            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
//                            sendIntent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString().isEmpty() ? editText.getHint().toString() : editText.getText().toString());
//                            sendIntent.setType("text/html");
//                            startActivity(sendIntent);
//                        }
//                    }
//                }
//           }
//        });


                if(checkData(email)){

                    GregorianCalendar today = new GregorianCalendar();
                    int month = today.get(Calendar.MONTH) + 1;

                    String todayString = "(" + today.get(Calendar.DAY_OF_MONTH) + "." + month + "." + today.get(Calendar.YEAR) + ")";

                    String filename = todayString + " report.pdf";

                    if(checkPermissions()) {
                        Utils.createNavigationAction(getString(R.string.send_report_doctor));

                        EditText editText = ((EditText) findViewById(R.id.send_invite_text_editText));


                        Uri pdfFile = organizesImagesForPDF(filename);

                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_with_doctor_email_subject));
                        sendIntent.putExtra(Intent.EXTRA_STREAM, pdfFile);
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        sendIntent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString().isEmpty() ? editText.getHint().toString() : editText.getText().toString());
                        sendIntent.setType("text/html");
                        startActivity(sendIntent);
                    }
                }
            }
            // }
        });
    }

    private boolean checkPermissions(){
        boolean permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!permissionCheck) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_WRITE);
        }

        return permissionCheck;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findViewById(R.id.send_btn).performClick();
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    private Uri saveCSVToFile(String csv, String fileName){
        File file   = null;
        File root   = Environment.getExternalStorageDirectory();
        if (root.canWrite()){
            File dir    =   new File (root.getAbsolutePath() + "/PersonData");
            dir.mkdirs();
            file   =   new File(dir, fileName+".csv");
            FileOutputStream out   =   null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.write(csv.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri u1  = null;

        if(file!=null) {
            u1 = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);//Uri.fromFile(file);
        }

        return u1;
    }

    private String convertToCsvString(ArrayList<TimelineItem> mediciItems){
        String result = getString(R.string.csv_file_header)+"\n";

        for(TimelineItem item : mediciItems){
            if(item.getDateTaken() != null){
                result += "\""+dateFormatter.format(item.getDateTaken().getTime()) + "\",";
                result += dateFormatterTime.format(item.getDateTaken().getTime()) + ",";
            }else{
                result += "---"+",";
                result += "---"+",";
            }

            result += (item.getMedicine().getMedicineName()) + ",";
            result += (item.getDosage()) + ",";
            result += (item.getMedicine().getMedicineType().getName()) + ",";
            result += ((item.isSOS()) ? getResources().getString(R.string.yes) : getResources().getString(R.string.no)) + ",";
            result += ((item.getState() != null && item.getState().equals(TimelineItem.STATE_LATE)) ? getResources().getString(R.string.yes) : getResources().getString(R.string.no)) + ",";
            result += (item.getFaseTime()==null?"---":item.getFaseTime().getDesc()) + ",";

            int duration = (item.getMedicine() instanceof UserNormalMedicine ? ((UserNormalMedicine) item.getMedicine()).getDuration() : 1);

            result += getResources().getQuantityString(R.plurals.duration_days, duration, duration) + ",";
            result += dateFormatterFiles.format(item.getMedicine().getStartDate().getTime());

            result += "\n";

        }

        return result;
    }

    private boolean checkData(String email) {
        boolean ok = false;

        if(email == null || email.isEmpty()){
            Toast.makeText(this,R.string.doctor_email_empty_error,Toast.LENGTH_SHORT).show();
        }else if(!Utils.isEmailValid(email)){
            Toast.makeText(this,R.string.doctor_email_invalid,Toast.LENGTH_SHORT).show();
        }else if(!shareAll && (startDate == null || endDate == null)){
            Toast.makeText(this,R.string.doctor_dates_empty_error,Toast.LENGTH_SHORT).show();
        }else{
            ok = true;
        }

        return ok;
    }

    private void setDateText(TextView textView, GregorianCalendar dateAux){
        if(dateAux!=null) {
            textView.setText(dateFormat.format(dateAux.getTime()));
        }else {
            textView.setText(R.string.date_init_text);
        }
    }

    private void configureDates() {
        if(shareAll){
            dates_box.setAlpha(0.3f);
            startDate = null;
            endDate = null;

            setDateText(startDateTextView,null);
            setDateText(endDateTextView,null);

            start_date_box.setClickable(false);
            end_date_box.setClickable(false);
        }else{
            dates_box.setAlpha(1);

            start_date_box.setClickable(true);
            end_date_box.setClickable(true);
        }
    }

    /**
     * Added by Alexandre Rocha - Start point
     **/



    /**
     * loads all the CARAT questionnaires answered by the user form the server
     */
    private void loadCaratData(){

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getApplicationContext());



        APIInspirers.requestMyCaratList(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(final JSONArray responseArray) {
                ringProgressDialogNoText.dismiss();
                AppController.getmInstance().getPolls(new TreeMapListener() {
                    @Override
                    public void onResponse(TreeMap response) {

                        listPollsCARATTotal = InspirersJSONParser.parseListPollAnswered(responseArray,(Poll) response.get(Poll.POLL_TYPE_CARAT));

                        if(listPollsCARATTotal.isEmpty()){
                           //Toast.makeText(getApplicationContext(),"The user hasn't answered any CART questionnaires", Toast.LENGTH_SHORT).show();
                           Toast.makeText(getApplicationContext(),R.string.No_CARAT_data_available, Toast.LENGTH_SHORT).show();//RUTE 13OUT2020
                        } else{

                            ArrayList<ListPoll> checkedCARATList = checkCARATListDoestContainRepeatedDates(listPollsCARATTotal);

                            loadWeeklySymptomsValues();

                            Map<Integer, Integer> tableData = organizeCARATTotalTableData(checkedCARATList);
                            fillTable(tableData);

                            Map<String,Map<String,Integer>> allCaratGraphsData = organizeCARATGraphData(checkedCARATList);
                            ArrayList<Float> adherenceData = organizeAdherenceDataForLineGraph(checkedCARATList);

                            generateCARATUGraph(allCaratGraphsData.get("CARAT U"));
                            generateCARATLGraph(allCaratGraphsData.get("CARAT L"));
                            generateCARATTotalGraph(allCaratGraphsData.get("CARAT Total"));
                            generateCARATotalAndAdherenceGraph(allCaratGraphsData.get("CARAT Total"),adherenceData);

                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getApplicationContext(),R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Queries the database for the patient's adherence values in a given custom time interval
     * @param startDate the start date for the time interval
     * @param endDate the end date for the time interval
     * @return the adhesion value
     */
    private BigDecimal loadAdherenceValuesByCustomInterval(GregorianCalendar startDate, GregorianCalendar endDate){

        ArrayList<Integer> daysDiffs = calculateCustomIntervalAdherenceDateFromToday(startDate, endDate, new GregorianCalendar());

        BigDecimal adherenceValue = AppController.getmInstance().getTimelineDataSource().getStatsCustomInterval(AppController.getmInstance().getActiveUser().getId(), daysDiffs);

        return adherenceValue;
    }


    /**
     * Queries the database for the patient's adherence values in a given week
     * @param startDate the beginning of the week
     * @return the adhesion value
     */
    private BigDecimal loadAdherenceValuesByWeek(GregorianCalendar startDate){

        int daysDiff = calculateWeeklyAdherenceDateFromToday(startDate, new GregorianCalendar());

        BigDecimal adherenceValue = AppController.getmInstance().getTimelineDataSource().getWeeklyStatsCustomInterval(AppController.getmInstance().getActiveUser().getId(), daysDiff);

        return adherenceValue;

    }

    /**
     * Loads all the patient's weekly symptoms questionnaires answers but utilizes only the latest answer
     */
    private void loadWeeklySymptomsValues(){

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getApplicationContext());

        APIInspirers.requestMySintList(AppController.getmInstance().getActiveUser().getUid(), Poll.POLL_TYPE_WEEKLY, new JSONArrayListener() {
            @Override
            public void onResponse(final JSONArray responseArrayWeekly) {

                ringProgressDialogNoText.dismiss();
                AppController.getmInstance().getPolls(new TreeMapListener() {
                    @Override
                    public void onResponse(TreeMap response) {

                        listPollsWeeklySymptoms = InspirersJSONParser.parseListPollAnswered(responseArrayWeekly, (Poll) response.get(Poll.POLL_TYPE_WEEKLY));

                        if(listPollsWeeklySymptoms.isEmpty()){
                           // Toast.makeText(getApplicationContext(),"The user hasn't answered any symptoms questionnaires", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), R.string.No_symptoms_data_available, Toast.LENGTH_SHORT).show(); //Rute 13OUT2020

                        }else {

                            int symptomsListSize = listPollsWeeklySymptoms.size();
                            //organizeSymptomsData(listPollsWeeklySymptoms.get(symptomsListSize-1),listPollsCARATTotal);
                            updateSymptomsData(listPollsWeeklySymptoms.get(symptomsListSize-1));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private ArrayList<ListPoll> checkCARATListDoestContainRepeatedDates(ArrayList<ListPoll> listPollsCARATTotal){

        GregorianCalendar auxDate;
        String auxDate2;
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<ListPoll> checkedCARATList = new ArrayList<>();



        for (ListPoll listPoll : listPollsCARATTotal) {
            auxDate = listPoll.getAsweredDate();

            //auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.ENGLISH);
            auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault());


            if(!dates.contains(auxDate2)){
                dates.add(auxDate2);
                checkedCARATList.add(listPoll);
            }
        }

        //limits the CARAT list to the latest 6 answers
        if(checkedCARATList.size()>6) {

            checkedCARATList = new ArrayList<>(checkedCARATList.subList(checkedCARATList.size() - 6, checkedCARATList.size()));

        }

        return checkedCARATList;
    }



    /**
     * Calculates the necessary dates (using today as a reference) to query the database for the adhesion value for that time interval
     * @param startDate the beginning of the time interval
     * @param endDate the end of the time interval
     * @return the days intervals between today and the start date, as well as today and the end date
     */
    public ArrayList<Integer> calculateCustomIntervalAdherenceDateFromToday (GregorianCalendar startDate, GregorianCalendar endDate, GregorianCalendar todayDate){

        long today = todayDate.getTime().getTime();
        long startDate2 = startDate.getTime().getTime();
        long endDate2 = endDate.getTime().getTime();


        //startDate and endDate are in milliseconds, so the difference must be converted to days
        int daysIntervalFromStartToToday = (int) ((today-startDate2) / (1000 * 60 * 60 * 24));
        int daysIntervalFromEndToToday = (int) ((today-endDate2) / (1000 * 60 * 60 * 24));

        ArrayList<Integer> dates = new ArrayList<>();
        dates.add(daysIntervalFromStartToToday);
        dates.add(daysIntervalFromEndToToday);

        return dates;
    }

    /**
     * Calculates the necessary date (using today as a reference) to query the database for the weekly adhesion value for that date
     * @param answerDate the beginning of the week
     * @return the days interval between today and the given date
     */
    public int calculateWeeklyAdherenceDateFromToday(GregorianCalendar answerDate, GregorianCalendar todayDate){


        long startDate = answerDate.getTime().getTime();
        long today = todayDate.getTime().getTime();

        //startDate and endDate are in milliseconds, so the difference must be converted to days
        long daysIntervalToToday = (today-startDate) / (1000 * 60 * 60 * 24);

        return (int) daysIntervalToToday;

    }


    /**
     * Fetches all the corresponding CARAT scores for a given date
     * @param date the date for the query
     * @return the desired Carat scores in a ArrayList
     */
    public Map<String,Integer> getCaratDataScoreByDate(GregorianCalendar date, ArrayList<ListPoll> listPollsCARATTotal){


        Map<String, Integer> caratScores = new TreeMap<>();

        boolean stopCondition = false;
        Poll pollAux;
        int i=0;
        int listSize=listPollsCARATTotal.size();
        int firstDayOfWeekAux;
        GregorianCalendar aux;

        firstDayOfWeekAux = date.get(GregorianCalendar.DAY_OF_WEEK);
        date.setFirstDayOfWeek(firstDayOfWeekAux);

        while (!stopCondition){

            if(i<listSize) {

                aux = listPollsCARATTotal.get(i).getAsweredDate();
                aux.setFirstDayOfWeek(firstDayOfWeekAux);
                if(aux.get(Calendar.WEEK_OF_YEAR) == (date.get(Calendar.WEEK_OF_YEAR))){

                    stopCondition=true;
                }
                else{
                    i++;
                }
            }
            else {
               throw new NoSuchElementException("The user does not have a CARAT questionnaire from the desired week");
               // throw new NoSuchElementException(getResources().getString(R.string.No_CARAT_data_available_week)); //Rute 13OU2020

            }
        }

        pollAux = listPollsCARATTotal.get(i).getPoll();


        caratScores.put("CARAT U",calculateCARATUScore(pollAux));
        caratScores.put("CARAT L",calculateCARATLScore(pollAux));
        caratScores.put("CARAT Total",calculateCARATTotalScore(pollAux));

        return caratScores;
    }



    /**
     * Fetches the answers for the most recent Carat questionnaire and organizes them in order to be displayed in the table
     * @param listPollsCARATTotal the list containing all of the patient's Carat questionnaires answers
     */
    public Map<Integer, Integer> organizeCARATTotalTableData(ArrayList<ListPoll> listPollsCARATTotal) {

        Map<Integer, Integer> answersEntries = new TreeMap<>();
        int listCaratSizeAux = listPollsCARATTotal.size();
        int listQuestionSizeAux = listPollsCARATTotal.get(listCaratSizeAux-1).getPoll().getListQuestions().size();
        int i = 1;

        for (Question question : listPollsCARATTotal.get(listCaratSizeAux - 1).getPoll().getListQuestions()) {

            QuestionMultipleChoice auxQ = (QuestionMultipleChoice) question;

            for (Answer a : auxQ.getListAnswers()) {
                if (a.isSelected()) {

                    if (i < listQuestionSizeAux) {

                        if (a.getNid().equals("14")) {
                            answersEntries.put(i, 3);

                        } else if (a.getNid().equals("15")) {
                            answersEntries.put(i, 2);

                        } else if (a.getNid().equals("16")) {
                            answersEntries.put(i, 1);

                        } else if (a.getNid().equals("17")) {
                            answersEntries.put(i, 0);
                        }

                    } else {

                        if (a.getNid().equals("20")) {
                            answersEntries.put(i, 0);

                        } else if (a.getNid().equals("19")) {
                            answersEntries.put(i, 1);

                        } else if (a.getNid().equals("14")) {
                            answersEntries.put(i, 2);

                        } else if (a.getNid().equals("18")) {
                            answersEntries.put(i, 3);
                        }
                    }
                }
            }
            i++;
        }

        return answersEntries;

        //fillTable(answersEntries);
    }


    /**
     * Organizes all the CARAT data in order to construct all the Carat graphs
     * @param listPollsCARATTotal all the patient's CARAT questionnaires answers
     */
    public Map<String,Map<String,Integer>> organizeCARATGraphData (ArrayList<ListPoll> listPollsCARATTotal){

        int auxScoreTotal,auxScoreL,auxScoreU;
        Poll auxPoll;
        GregorianCalendar auxDate;
        String auxDate2;
        Map<String,Integer> caratTotalData = new LinkedHashMap<>();
        Map<String,Integer> caratLData = new LinkedHashMap<>();
        Map<String,Integer> caratUData = new LinkedHashMap<>();
        //ArrayList<ListPoll> shortestListPollsCARATTotal = new ArrayList<>();

        Map<String,Map<String,Integer>> allCaratGraphsData = new TreeMap<>();


        //uses latest 6 CARAT answers in case there is more than 6 answers
        if(listPollsCARATTotal.size()>6) {

            listPollsCARATTotal = new ArrayList<>(listPollsCARATTotal.subList(listPollsCARATTotal.size() - 6, listPollsCARATTotal.size()));

        }
        for (ListPoll listPoll : listPollsCARATTotal) {

            auxPoll = listPoll.getPoll();

            auxScoreTotal = calculateCARATTotalScore(auxPoll);
            auxScoreU = calculateCARATUScore(auxPoll);
            auxScoreL = calculateCARATLScore(auxPoll);

            auxDate = listPoll.getAsweredDate();

            //auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.ENGLISH);
             auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()); //Rute 13OUT2020

            caratTotalData.put(auxDate2,auxScoreTotal);
            caratLData.put(auxDate2,auxScoreL);
            caratUData.put(auxDate2,auxScoreU);

            // shortestListPollsCARATTotal.add(listPoll);
        }


        allCaratGraphsData.put("CARAT Total", caratTotalData);
        allCaratGraphsData.put("CARAT U", caratUData);
        allCaratGraphsData.put("CARAT L", caratLData);

        return allCaratGraphsData;
//        ArrayList<Float> adherenceData = organizeAdherenceDataForLineGraph(listPollsCARATTotal);
//
//        generateCARATUGraph(caratUData);
//        generateCARATLGraph(caratLData);
//        generateCARATTotalGraph(caratTotalData);
//        generateCARATotalAndAdherenceGraph(caratTotalData,adherenceData);
    }



    /**
     * Organizes the adherence data in order to generate the CARAT Total and Adherence graph
     * @param listPollsCARATTotal the usable patient's CARAT questionnaires answers
     * @return the patient's organized adherence data
     */
    private ArrayList<Float> organizeAdherenceDataForLineGraph(ArrayList<ListPoll> listPollsCARATTotal){

        BigDecimal auxAdherence;
        ListPoll listPollAux;
        ArrayList<Float> adherenceValues = new ArrayList<>();


        for (int i =0; i<listPollsCARATTotal.size();i++) {

            listPollAux = listPollsCARATTotal.get(i);

            if(i==listPollsCARATTotal.size()-1){
                auxAdherence = loadAdherenceValuesByCustomInterval(listPollsCARATTotal.get(i-1).getAsweredDate(),listPollAux.getAsweredDate());
            }
            else {
                auxAdherence = loadAdherenceValuesByCustomInterval(listPollAux.getAsweredDate(),listPollsCARATTotal.get(i+1).getAsweredDate());
            }

            adherenceValues.add(auxAdherence.floatValue());
        }

        return (adherenceValues);
    }


    public void updateSymptomsData(ListPoll listPollsWeeklySymptoms){

        // boolean userHasCarat;
        Map<String,Integer> caratAux;
        Map<String,Integer> symptomsData;
        int caratTotalAux,caratUAux,caratLAux;

        BigDecimal adherenceAux = loadAdherenceValuesByWeek(listPollsWeeklySymptoms.getAsweredDate());


        try {
            caratAux = getCaratDataScoreByDate(listPollsWeeklySymptoms.getAsweredDate(), listPollsCARATTotal);
            caratTotalAux = caratAux.get("CARAT Total");
            caratUAux = caratAux.get("CARAT U");
            caratLAux = caratAux.get("CARAT L");

            // userHasCarat=true;
        }
        catch (NoSuchElementException exp){
            //userHasCarat=false;
            caratTotalAux = -1;
            caratUAux = -1;
            caratLAux = -1;
        }

        symptomsData = organizeSymptomsData(listPollsWeeklySymptoms,adherenceAux,caratTotalAux, caratUAux, caratLAux);
        generateSummaryRadarGraph((RadarChart)findViewById(R.id.doctorGraphSummary),symptomsData,listPollsWeeklySymptoms.getAsweredDate());


    }


    /**
     * Prepares weekly symptoms data for the summary radar graph creation
     * @param listPollsWeeklySymptoms the patient's symptoms data for the latest full week
     */
    public Map<String, Integer> organizeSymptomsData(ListPoll listPollsWeeklySymptoms, BigDecimal adherenceAux, int caratTotalAux, int caratUAux, int caratLAux){

        Map<String,Integer> values = new TreeMap<>();

        int workAux=0;
        //int caratAux=0;
        int leisureAux=0;
        int healthAux=0;

        ArrayList<Question> listQuestions = listPollsWeeklySymptoms.getPoll().getListQuestions();

        if (((QuestionYesNo) listQuestions.get(0)).isYes()) {

            //userHasWork=true;
            workAux = ((QuestionSlider) listQuestions.get(1)).getTotalSelected();
        }

        else {
            //userHasWork=false;
            workAux=-1;
        }

        //userHasLeisure=true;
        leisureAux = ((QuestionSlider) listQuestions.get(2)).getTotalSelected();

        //userHasHealth=true;
        healthAux = ((QuestionSlider) listQuestions.get(3)).getTotalSelected();



        //get corresponding week carat data
//        try {
//            caratAux = getCaratTotalScoreByDate(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate());
//            userHasCarat=true;
//        }
//        catch (NoSuchElementException exp){
//            userHasCarat=false;
//        }

        values.put("Adherence",adherenceAux.intValue());
        values.put("CARAT Total",caratTotalAux);
        values.put("CARAT U", caratUAux);
        values.put("CARAT L", caratLAux);
        values.put("Leisure",leisureAux);
        values.put("Work",workAux);
        values.put("Health",healthAux);

        return values;

        // generateSummaryRadarGraph(userHasWork,userHasCarat, adherenceAux, caratAux, workAux, leisureAux,healthAux,listPollsWeeklySymptoms.getAsweredDate());
    }


    /**
     * Calculates the Carat Total score from a given answer/poll
     * @param poll the carat questionnaire answer
     * @return the resulting Carat Total score
     */
    public int calculateCARATTotalScore(Poll poll){

        int totalScore = 0;

        QuestionMultipleChoice auxQ;
        for(Question q : poll.getListQuestions()){
            auxQ = (QuestionMultipleChoice) q;

            for(Answer a : auxQ.getListAnswers()){
                if(a.isSelected()){
                    if(a.getNid().equals("14")){
                        totalScore += 3;

                    }else if(a.getNid().equals("15")){
                        totalScore += 2;

                    }else if(a.getNid().equals("16")){
                        totalScore += 1;

                    }else if(a.getNid().equals("19")){
                        totalScore += 2;
                    }else if(a.getNid().equals("18")){
                        totalScore += 3;
                    }
                }
            }

        }

        return totalScore;
    }

    /**
     * Calculates the CARAT Upper Airways score from a given answer/poll
     * @param poll the patient's CARAT questionnaire answer
     * @return the corresponding CARAT Upper Airways score
     */
    public int calculateCARATUScore(Poll poll){

        int caratUScore = 0;

        QuestionMultipleChoice auxQ;
        ArrayList<Question> questionList = poll.getListQuestions();


        for(int i=0;i<4;i++) {

            auxQ = (QuestionMultipleChoice) questionList.get(i);

            for (Answer a : auxQ.getListAnswers()) {
                if (a.isSelected()) {
                    if (a.getNid().equals("14")) {
                        caratUScore += 3;

                    } else if (a.getNid().equals("15")) {
                        caratUScore += 2;

                    } else if (a.getNid().equals("16")) {
                        caratUScore += 1;

                    }
                }

            }
        }

        return caratUScore;
    }


    /**
     * Calculates the CARAT Lower Airways score from a given answer/poll
     * @param poll the patient's CARAT questionnaire answer
     * @return the corresponding CARAT Upper Airways score
     */
    public int calculateCARATLScore(Poll poll){

        int caratLScore = 0;

        QuestionMultipleChoice auxQ;
        ArrayList<Question> questionList = poll.getListQuestions();


        for(int i=4;i<questionList.size();i++) {

            auxQ = (QuestionMultipleChoice) questionList.get(i);

            for(Answer a : auxQ.getListAnswers()){
                if(a.isSelected()){
                    if(a.getNid().equals("14")){
                        caratLScore += 3;

                    }else if(a.getNid().equals("15")){
                        caratLScore += 2;

                    }else if(a.getNid().equals("16")){
                        caratLScore += 1;

                    }else if(a.getNid().equals("19")){
                        caratLScore += 2;
                    }else if(a.getNid().equals("18")){
                        caratLScore += 3;
                    }
                }
            }
        }

        return caratLScore;
    }


    /**
     * Fills the Carat table with the information already prepared
     * @param answersEntries The patient's latest Carat questionnaire answer already organized in a map for the table construction
     */
    public void fillTable(Map<Integer, Integer> answersEntries) {

        TextView textViewAux;

        for (Map.Entry<Integer, Integer> answer : answersEntries.entrySet()) {

            textViewAux = findTextViewByRowAndAnswerSlot(answer.getKey(), answer.getValue());

            textViewAux.setBackgroundResource(R.drawable.table_textview_blue_fill);

            int aux = answer.getValue();

            setAnswerText(textViewAux, answer.getKey(), aux);

            if (aux < 3) {

                for (int i = aux; i <= 3; i++) {
                    textViewAux = findTextViewByRowAndAnswerSlot(answer.getKey(), i);
                    textViewAux.setBackgroundResource(R.drawable.table_textview_blue_fill);
                }
            }
        }

    }


    /**
     * Finds the table text view located in the given row an answer slot
     * @param row the row where the text view is located
     * @param answerSlot the answer slot (column) where the text view is located
     * @return the desired text view
     */
    private TextView findTextViewByRowAndAnswerSlot(int row, int answerSlot) {

        String textViewToFind = "textView" + String.valueOf(row) + String.valueOf(answerSlot);
        int idAux = (getResources().getIdentifier(textViewToFind, "id", getPackageName()));

        return findViewById(idAux);
    }

    /**
     * Set the answer text for a given text view
     * @param answerTextView the answer text view
     * @param questionSlot the question slot (row) where the text view is located
     * @param answerSlot the answer slot (column) where the text view is located
     */
    private void setAnswerText(TextView answerTextView, int questionSlot, int answerSlot) {

        answerTextView.setTextColor(Color.WHITE);
        answerTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        if (questionSlot != 10) {

            switch (answerSlot) {

                case 0:
                   // answerTextView.setText("All days");
                    answerTextView.setText(R.string.All_days);
                    break;

                case 1:
                   // answerTextView.setText(">2 days");
                    answerTextView.setText(R.string.more_than_2);
                    break;

                case 2:
                   // answerTextView.setText("<=2 days");
                    answerTextView.setText(R.string.equal_orless_2);
                    break;
                case 3:
                  // answerTextView.setText("Never");
                    answerTextView.setText(R.string.never);
                    break;
            }
        } else {

            switch (answerSlot) {

                case 0:
                    //answerTextView.setText(">=7 days");
                    answerTextView.setText(R.string.equal_ormore_than_7);
                    break;

                case 1:
                    //answerTextView.setText("<7 days");
                    answerTextView.setText(R.string.less_than_7);
                    break;

                case 2:
                    //answerTextView.setText("Never");
                    answerTextView.setText(R.string.never);
                    break;

                case 3:
                    //answerTextView.setText("N/medic.");
                    answerTextView.setText(R.string.No_med);
                    break;

            }
        }
    }



    /**
     * Generates the area graph with the Carat Total data
     * @param caratTotalData the Carat Total data from the patient
     */
    public void generateCARATTotalGraph(Map<String, Integer> caratTotalData){

        LineChart areaGraphCARATTotal = findViewById(R.id.doctorGraphCARATT);

        //x axis labels
        XAxis xAxis = areaGraphCARATTotal.getXAxis();
        ArrayList<String> xAxisValues = new ArrayList<>();

        //CARAT Total data
        List<Entry> caratTotalEntries = new ArrayList<>();

        int aux=0;

        //fill x axis labels and graph data with carat values
        for(Map.Entry<String, Integer> set: caratTotalData.entrySet()){
            xAxisValues.add(set.getKey());
            caratTotalEntries.add(new Entry(aux,set.getValue()));
            aux++;
        }

        //customize x axis
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setLabelCount(xAxisValues.size(),true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);



        LineDataSet caratTotalEntriesDataSet = new LineDataSet(caratTotalEntries, "CARAT Total");
        caratTotalEntriesDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratTotalEntriesDataSet.setDrawValues(true);
        caratTotalEntriesDataSet.setCubicIntensity(0.1f);
        caratTotalEntriesDataSet.setDrawCircles(true);
        caratTotalEntriesDataSet.setLineWidth(0f);
        caratTotalEntriesDataSet.setCircleRadius(0.9f);
        caratTotalEntriesDataSet.setCircleColor(Color.BLACK);


        Drawable gradient = ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_carat_total);
        assert gradient != null;
        gradient.setAlpha(255);
        caratTotalEntriesDataSet.setFillDrawable(gradient);
        caratTotalEntriesDataSet.setDrawFilled(true);
        caratTotalEntriesDataSet.setFillAlpha(255);


        //CARAT Total Threshold data
        List<Entry> caratTotalThreshold = new ArrayList<>();

        for(aux=0;aux<caratTotalEntries.size();aux++){
            caratTotalThreshold.add(new Entry(aux, 24));
        }


        LineDataSet caratTotalThresholdEntriesDataSet = new LineDataSet(caratTotalThreshold, "Threshold");
        caratTotalThresholdEntriesDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratTotalThresholdEntriesDataSet.setCubicIntensity(0.1f);
        caratTotalThresholdEntriesDataSet.setColor(Color.BLACK);
        caratTotalThresholdEntriesDataSet.setLineWidth(3f);
        caratTotalThresholdEntriesDataSet.setDrawValues(false);
        caratTotalThresholdEntriesDataSet.setDrawCircles(false);



        LineData caratTotalLineData = new LineData(caratTotalEntriesDataSet,caratTotalThresholdEntriesDataSet);
        caratTotalLineData.setValueFormatter(new IntegerValueFormatter());
        caratTotalLineData.setValueTextSize(11f);

        areaGraphCARATTotal.setData(caratTotalLineData);

        Description descAux = areaGraphCARATTotal.getDescription();
        areaGraphCARATTotal.getDescription().setText("");
        descAux.setTextSize(20f);
        descAux.setTypeface(Typeface.DEFAULT_BOLD);
        descAux.setPosition(620,37);

        areaGraphCARATTotal.getAxisLeft().setDrawGridLines(false);
        areaGraphCARATTotal.getAxisRight().setDrawGridLines(false);

        areaGraphCARATTotal.getAxisLeft().setAxisMinimum(0f);
        areaGraphCARATTotal.getAxisLeft().setAxisMaximum(31f);
        areaGraphCARATTotal.getAxisRight().setAxisMinimum(0f);
        areaGraphCARATTotal.getAxisRight().setAxisMaximum(31f);

        areaGraphCARATTotal.setDrawBorders(true);
        areaGraphCARATTotal.getLegend().setEnabled(false);

        areaGraphCARATTotal.invalidate(); // refresh

        graphsList.add(areaGraphCARATTotal);
    }


    /**
     * Generates the area graph with the Carat Lower Airways data
     * @param caratLData the Carat Lower Airways from the patient
     */
    public void generateCARATLGraph(Map<String, Integer> caratLData){

        LineChart areaGraphCARATL = findViewById(R.id.doctorGraphCARATL);

        //x axis labels
        XAxis xAxis = areaGraphCARATL.getXAxis();
        ArrayList<String> xAxisValues = new ArrayList<>();

        //CARAT Total data
        List<Entry> caratLEntries = new ArrayList<>();


        int aux=0;

        //fill x axis labels and graph data with carat values
        for(Map.Entry<String, Integer> set: caratLData.entrySet()){
            xAxisValues.add(set.getKey());
            caratLEntries.add(new Entry(aux,set.getValue()));
            aux++;
        }

        //customize x axis
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setLabelCount(xAxisValues.size(),true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineDataSet caratLEntriesDataSet = new LineDataSet(caratLEntries, "CARAT L");

        Drawable caratLGradient = ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_carat_l);
        assert caratLGradient != null;
        caratLGradient.setAlpha(255);
        caratLEntriesDataSet.setFillDrawable(caratLGradient);
        caratLEntriesDataSet.setDrawFilled(true);
        caratLEntriesDataSet.setFillAlpha(255);

        caratLEntriesDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratLEntriesDataSet.setCubicIntensity(0.1f);
        caratLEntriesDataSet.setDrawValues(true);
        caratLEntriesDataSet.setFillDrawable(caratLGradient);
        caratLEntriesDataSet.setDrawFilled(true);
        caratLEntriesDataSet.setLineWidth(0f);
        caratLEntriesDataSet.setCircleColor(Color.BLACK);
        caratLEntriesDataSet.setCircleRadius(0.9f);
        caratLEntriesDataSet.setFillAlpha(255);


        //CARAT L Threshold data
        List<Entry> caratLThreshold = new ArrayList<>();

        for(aux=0;aux<caratLEntries.size();aux++){
            caratLThreshold.add(new Entry(aux, 15));
        }

        LineDataSet caratLThresholdDataSet = new LineDataSet(caratLThreshold, "Threshold");
        caratLThresholdDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratLThresholdDataSet.setCubicIntensity(0.1f);
        caratLThresholdDataSet.setColor(Color.BLACK);
        caratLThresholdDataSet.setDrawValues(false);
        caratLThresholdDataSet.setDrawCircles(false);
        caratLThresholdDataSet.setLineWidth(2f);


        LineData caratLIndividualLineData = new LineData(caratLEntriesDataSet, caratLThresholdDataSet);
        caratLIndividualLineData.setValueFormatter(integerValueFormatter);
        caratLIndividualLineData.setValueTextSize(11f);

        areaGraphCARATL.setData(caratLIndividualLineData);


        areaGraphCARATL.getAxisLeft().setDrawGridLines(false);
        areaGraphCARATL.getAxisRight().setDrawGridLines(false);
        areaGraphCARATL.getAxisRight().setAxisMinimum(0f);
        areaGraphCARATL.getAxisRight().setAxisMaximum(19f);
        areaGraphCARATL.getAxisLeft().setAxisMinimum(0f);
        areaGraphCARATL.getAxisLeft().setAxisMaximum(19f);

        areaGraphCARATL.getDescription().setText("");


        areaGraphCARATL.setDrawBorders(true);

        areaGraphCARATL.getLegend().setEnabled(false);


        areaGraphCARATL.invalidate(); // refresh

        graphsList.add(areaGraphCARATL);
    }


    /**
     * Generates the area graph with the Carat Upper Airways data
     * @param caratUData the Carat Upper Airways data from the patient
     */
    public void generateCARATUGraph(Map<String, Integer> caratUData){

        LineChart areaGraphCARATU = findViewById(R.id.doctorGraphCARATU);

        //x axis labels
        XAxis xAxis = areaGraphCARATU.getXAxis();
        ArrayList<String> xAxisValues = new ArrayList<>();

        //CARAT Total data
        List<Entry> caratUEntries = new ArrayList<>();


        int aux=0;

        //fill x axis labels and graph data with carat values
        for(Map.Entry<String, Integer> set: caratUData.entrySet()){
            xAxisValues.add(set.getKey());
            caratUEntries.add(new Entry(aux,set.getValue()));
            aux++;
        }

        //customize x axis
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setLabelCount(xAxisValues.size(),true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        LineDataSet caratUEntriesDataSet = new LineDataSet(caratUEntries, "CARAT U");

        Drawable caratUGradient = ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_carat_u);
        assert caratUGradient != null;
        caratUGradient.setAlpha(255);
        caratUEntriesDataSet.setFillDrawable(caratUGradient);
        caratUEntriesDataSet.setDrawFilled(true);
        caratUEntriesDataSet.setFillAlpha(255);

        caratUEntriesDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratUEntriesDataSet.setCubicIntensity(0.1f);
        caratUEntriesDataSet.setDrawValues(true);
        caratUEntriesDataSet.setLineWidth(0f);
        caratUEntriesDataSet.setCircleColor(Color.BLACK);
        caratUEntriesDataSet.setCircleRadius(0.9f);

        //CARAT Total Threshold data
        List<Entry> caratUThreshold = new ArrayList<>();

        for(aux=0;aux<caratUEntries.size();aux++){
            caratUThreshold.add(new Entry(aux, 8));
        }

        LineDataSet caratUThresholdDataSet = new LineDataSet(caratUThreshold, "Threshold");
        caratUThresholdDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratUThresholdDataSet.setCubicIntensity(0.1f);
        caratUThresholdDataSet.setColor(Color.BLACK);
        caratUThresholdDataSet.setDrawValues(false);
        caratUThresholdDataSet.setDrawCircles(false);
        caratUThresholdDataSet.setLineWidth(2f);


        LineData caratUIndividualLineData = new LineData(caratUEntriesDataSet, caratUThresholdDataSet);
        caratUIndividualLineData.setValueFormatter(integerValueFormatter);
        caratUIndividualLineData.setValueTextSize(11f);

        areaGraphCARATU.setData(caratUIndividualLineData);


        areaGraphCARATU.getAxisLeft().setDrawGridLines(false);
        areaGraphCARATU.getAxisRight().setDrawGridLines(false);
        areaGraphCARATU.getAxisRight().setAxisMinimum(0f);
        areaGraphCARATU.getAxisRight().setAxisMaximum(12.5f);
        areaGraphCARATU.getAxisLeft().setAxisMinimum(0f);
        areaGraphCARATU.getAxisLeft().setAxisMaximum(12.5f);

        areaGraphCARATU.getDescription().setText("");

        areaGraphCARATU.setDrawBorders(true);

        areaGraphCARATU.getLegend().setEnabled(false);


        areaGraphCARATU.invalidate(); // refresh

        graphsList.add(areaGraphCARATU);

    }


    /**
     * Generates the line graph with the Carat Total and Adherence data
     * @param caratTotalData the Carat Total data from the patient
     * @param adherenceData the Adherence data from the patient
     */
    public void generateCARATotalAndAdherenceGraph(Map<String, Integer> caratTotalData, ArrayList<Float>adherenceData){

        final LineChart lineGraphAdherenceVSCARATT = findViewById(R.id.doctorGraphAdherenceVSCARAT);


        //x axis labels
        XAxis xAxis = lineGraphAdherenceVSCARATT.getXAxis();
        ArrayList<String> xAxisValues = new ArrayList<>();

        //CARAT Total data
        List<Entry> caratTotalEntries = new ArrayList<>();

        int i=0;

        //fill x axis labels and graph data with Carat values
        for(Map.Entry<String, Integer> set: caratTotalData.entrySet()){
            xAxisValues.add(set.getKey());
            caratTotalEntries.add(new Entry(i,set.getValue()));
            i++;
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setLabelCount(xAxisValues.size(),true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineDataSet caratTotalEntriesDataSet = new LineDataSet(caratTotalEntries, "CARAT T");
        caratTotalEntriesDataSet.setColor(Color.RED);
        caratTotalEntriesDataSet.setLineWidth(5f);
        caratTotalEntriesDataSet.setCircleRadius(0.9f);
        caratTotalEntriesDataSet.setCircleColor(Color.BLACK);
        caratTotalEntriesDataSet.setDrawValues(true);

        //configures right y axis
        YAxis yAxisRight = lineGraphAdherenceVSCARATT.getAxisRight();
        caratTotalEntriesDataSet.setAxisDependency(yAxisRight.getAxisDependency());
        yAxisRight.setAxisMinimum(0f);
        yAxisRight.setAxisMaximum(31f);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setAxisLineColor(caratTotalEntriesDataSet.getColor());
        yAxisRight.setAxisLineWidth(3f);

        //adherence data
        List<Entry> adherenceEntries = new ArrayList<>();
        i=0;
        for(float value : adherenceData) {
            adherenceEntries.add(new Entry(i,value));
            i++;
        }
        LineDataSet adherenceEntriesDataSet = new LineDataSet(adherenceEntries, "Adesão (%)");
        adherenceEntriesDataSet.setColor(Color.BLUE);
        adherenceEntriesDataSet.setCircleColor(Color.BLACK);
        adherenceEntriesDataSet.setCircleRadius(0.9f);
        adherenceEntriesDataSet.setLineWidth(5f);
        adherenceEntriesDataSet.setDrawValues(true);

        //configure left y axis
        YAxis yAxisLeft = lineGraphAdherenceVSCARATT.getAxisLeft();
        adherenceEntriesDataSet.setAxisDependency(yAxisLeft.getAxisDependency());
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(103f);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisLineColor(adherenceEntriesDataSet.getColor());
        yAxisLeft.setAxisLineWidth(3f);


        //carat threshold data
        List<Entry> caratTotalThresholdEntries = new ArrayList<>();
        i=0;
        for(Entry aux : caratTotalEntries) {
            caratTotalThresholdEntries.add(new Entry(i,24));
            i++;
        }
        LineDataSet caratTotalThresholdDataSet = new LineDataSet(caratTotalThresholdEntries, "Threshold");
        caratTotalThresholdDataSet.setDrawValues(false);
        caratTotalThresholdDataSet.setDrawCircles(false);
        caratTotalThresholdDataSet.setColor(Color.BLACK);
        caratTotalThresholdDataSet.setLineWidth(2f);
        caratTotalThresholdDataSet.setAxisDependency(yAxisRight.getAxisDependency());


        LineData adherenceVSCARATTLineData = new LineData(caratTotalThresholdDataSet, caratTotalEntriesDataSet, adherenceEntriesDataSet);
        adherenceVSCARATTLineData.setValueFormatter(integerValueFormatter);
        adherenceVSCARATTLineData.setValueTextSize(11f);

        lineGraphAdherenceVSCARATT.setData(adherenceVSCARATTLineData);

        //configure y axis textviews
        TextView rightAxisLabel = findViewById(R.id.textViewDoctorRightAxisLabel);
        rightAxisLabel.setTextColor(caratTotalEntriesDataSet.getColor());

        TextView leftAxisLabel = findViewById(R.id.textViewDoctorLeftAxisLabel);
        leftAxisLabel.setTextColor(adherenceEntriesDataSet.getColor());

        lineGraphAdherenceVSCARATT.getDescription().setText("");
        lineGraphAdherenceVSCARATT.setDrawBorders(true);
        lineGraphAdherenceVSCARATT.invalidate(); // refresh

        lineGraphAdherenceVSCARATT.getLegend().setEnabled(false);


        graphsList.add(lineGraphAdherenceVSCARATT);
    }


    /**
     * Generates the summary radar graph
     * @param date the start of the week for the given data
     */
    public void generateSummaryRadarGraph(RadarChart radarGraphSummary, Map<String,Integer> symptomsData, GregorianCalendar date){

        //final RadarChart radarGraphSummary = findViewById(R.id.doctorGraphSummary);

        //customize x axis labels and add x values
        XAxis xAxis = radarGraphSummary.getXAxis();

        ArrayList<String> symptoms = new ArrayList<>();

        List<RadarEntry> summaryEntries = new ArrayList<>();

        int adherenceAux = symptomsData.get("Adherence");
        int caratTotalAux = symptomsData.get("CARAT Total");
        int caratUAux = symptomsData.get("CARAT U");
        int caratLAux = symptomsData.get("CARAT L");
        int workAux = symptomsData.get("Work");
        int leisureAux = symptomsData.get("Leisure");
        int healthAux = symptomsData.get("Health");


        if(workAux>0 && caratTotalAux>0) {
           // symptoms.add("CARAT Total (%)");
            //symptoms.add("CARAT Lower (%)");
           // symptoms.add("Adhesion__");
            //symptoms.add("Work/School");
           // symptoms.add("Leisure");
           // symptoms.add("Health");
            // symptoms.add("CARAT Upper (%)");

            symptoms.add(getResources().getString(R.string.CARAT_T)); //Rute 13OUT2020
            symptoms.add(getResources().getString(R.string.CARAT_Lower_Airways)); //Rute 13OUT2020
            symptoms.add(getResources().getString(R.string.adherence)); //Rute 13OUT2020
            symptoms.add(getResources().getString(R.string.Symptoms_WS)); //Rute 13OUT2020
            symptoms.add(getResources().getString(R.string.Symptoms_Leisure)); //Rute 13OUT2020
            symptoms.add(getResources().getString(R.string.Symptoms_Health)); //Rute 13OUT2020
            symptoms.add(getResources().getString(R.string.CARAT_Upper_Airways)); //Rute 13OUT2020

            //Carat Total
            summaryEntries.add(new RadarEntry(caratTotalAux *100/30));

            //Carat Lower
            summaryEntries.add(new RadarEntry(caratLAux* 100/18));

            //Adherence
            summaryEntries.add(new RadarEntry(adherenceAux));

            //Work
            summaryEntries.add(new RadarEntry(workAux));

            //Leisure
            summaryEntries.add(new RadarEntry(leisureAux));

            //Health
            summaryEntries.add(new RadarEntry(healthAux));

            //Carat Upper
            summaryEntries.add(new RadarEntry(caratUAux *100/12));
        }
        else{
            if(workAux>0){
              //  symptoms.add("Adhesion");
              //  symptoms.add("Work/School");
              //  symptoms.add("Leisure");
              //  symptoms.add("Health");

                symptoms.add(getResources().getString(R.string.adherence)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.Symptoms_WS)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.Symptoms_Leisure)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.Symptoms_Health)); //Rute 13OUT2020


                //Adherence
                summaryEntries.add(new RadarEntry(adherenceAux));

                //Work
                summaryEntries.add(new RadarEntry(workAux));

                //Leisure
                summaryEntries.add(new RadarEntry(leisureAux));

                //Health
                summaryEntries.add(new RadarEntry(healthAux));

            }
            else{
                //symptoms.add("CARAT Total (%)");
                //symptoms.add("CARAT Lower (%)");
               // symptoms.add("Adhesion");
               // symptoms.add("Leisure");
               // symptoms.add("Health");
               // symptoms.add("CARAT Upper (%)");

                symptoms.add(getResources().getString(R.string.CARAT_T)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.CARAT_Lower_Airways)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.adherence)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.Symptoms_WS)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.Symptoms_Leisure)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.Symptoms_Health)); //Rute 13OUT2020
                symptoms.add(getResources().getString(R.string.CARAT_Upper_Airways)); //Rute 13OUT2020

                //Carat Total
                summaryEntries.add(new RadarEntry(caratTotalAux *100/30));

                //Carat Lower
                summaryEntries.add(new RadarEntry(caratLAux* 100/18));

                //Adherence
                summaryEntries.add(new RadarEntry(adherenceAux));

                //Leisure
                summaryEntries.add(new RadarEntry(leisureAux));

                //Health
                summaryEntries.add(new RadarEntry(healthAux));

                //Carat Upper
                summaryEntries.add(new RadarEntry(caratUAux *100/12));
            }
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(symptoms));

        //customize y axis labels
        YAxis yAxis = radarGraphSummary.getYAxis();
        yAxis.setAxisMaximum(60f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(2);
        yAxis.setDrawLabels(false);



        RadarDataSet summaryDataSet = new RadarDataSet(summaryEntries, getWeekInText(date));
        summaryDataSet.setColor(Color.BLUE);
        summaryDataSet.setLineWidth(2f);


        RadarData summaryRadarData = new RadarData(summaryDataSet);

        summaryRadarData.setDrawValues(true);
        summaryRadarData.setValueFormatter(integerValueFormatter);
        summaryRadarData.setValueTextSize(11f);
        radarGraphSummary.setData(summaryRadarData);

        radarGraphSummary.getDescription().setText("");

        radarGraphSummary.getLegend().setWordWrapEnabled(true);
        radarGraphSummary.getLegend().setYOffset(70f);

        radarGraphSummary.invalidate(); // refresh

        graphsList.add(radarGraphSummary);
    }


    /**
     * Returns a string with the week for a given date
     * @param date the start of the week
     */
    public String getWeekInText(GregorianCalendar date){

        Calendar dateAux=(Calendar) date.clone();

        int weekStart;
        String monthStart;
        int weekEnd;
        String monthEnd;


        weekStart = dateAux.get(Calendar.DAY_OF_MONTH);
        //monthStart = dateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.ENGLISH);
        monthStart = dateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault()); //Rute 13OUT2020
        //(Locale.getDefault().getLanguage().contentEquals("es"))
        dateAux.add(Calendar.DAY_OF_MONTH,6);
        weekEnd = dateAux.get(Calendar.DAY_OF_MONTH);
        //monthEnd = dateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.ENGLISH);
        monthEnd = dateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault());  //Rute 13OUT2020
        //return "Week: " + weekStart + "/" + monthStart + " - " + weekEnd + "/" + monthEnd;
        return  + weekStart + "/" + monthStart + " - " + weekEnd + "/" + monthEnd;
    }


    /**
     * Organizes and calls all the necessary methods to generate the necessary image files for all the graphs and CARAT table, followed by the creation of the PDF report
     */
    private Uri organizesImagesForPDF(String pdfFileName){

        List<String> graphsNames= new ArrayList<>();
        String fileName = "graph0";
        String folderName = "graphs";
        int cont = 0;

        String tableFileName = saveTableToJPEG(folderName);

        graphsNames.add(tableFileName);

        for (Chart graph : graphsList) {
            cont++;
            fileName = fileName.replace(fileName.substring(fileName.length() - 1), String.valueOf(cont));


            generateJPGFromGraph(graph, fileName, folderName);

            graphsNames.add(fileName);
        }


        return generatePDFWithAllImages(graphsNames, folderName, pdfFileName);
    }

    /**
     * Generates a JPG file from a given graph
     * @param graph the graph to be saved as a JPG file
     * @param fileName the desired name for the JPG file
     * @param folderName the folder name under the DCIM directory where the JPG file will be saved
     */
    private void generateJPGFromGraph(Chart graph, String fileName, String folderName){

        checkPermissions();
        graph.saveToGallery(fileName,folderName,null, Bitmap.CompressFormat.JPEG,100);
    }

    /**
     * Saves the Carat table as a JPEG file
     * @param folderName the name of the folder in the DCIM folder where the image file will be saved
     * @return the name of the image file name ("table")
     */
    private String saveTableToJPEG(String folderName){
        TableLayout caratTableLayout =  findViewById(R.id.caratTableLayout2);

        String imageName = "table";

        Bitmap bitmap = Bitmap.createBitmap(caratTableLayout.getWidth(), caratTableLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        caratTableLayout.draw(canvas);


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator +  folderName + File.separator + imageName + ".jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageName;
    }


    /**
     * Generates a PDF file with all the graphs and Carat table as individual image files
     * @param graphsNames a list containing all the files names
     * @param folderName the name of the folder in the DCIM folder where the image files are stored
     * @param pdfFileName the desired name for the PDF file
     */
    public Uri generatePDFWithAllImages(List<String> graphsNames, String folderName, String pdfFileName){

        PDFBoxResourceLoader.init(getApplicationContext());

        Uri finalPdfFile = null;

        try(PDDocument pdfDocument = new PDDocument()) {

            File graphsPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + folderName);

            for(String fileName : graphsNames){

                PDPage page = new PDPage(PDRectangle.A4);
                pdfDocument.addPage(page);

                File graphFile = new File(graphsPath.getPath() + File.separator + fileName + ".jpg");

                FileInputStream imageInput = new FileInputStream(graphFile);

                PDImageXObject imageXObject = JPEGFactory.createFromStream(pdfDocument,imageInput);


                float startX = 0;
                float startY = 0;


                PDPageContentStream pageContentStream = new PDPageContentStream(pdfDocument,page);

                if (fileName.equals("table")){

                    pageContentStream.beginText();

                    pageContentStream.newLineAtOffset(130f,770f);
                    pageContentStream.setFont(PDMMType1Font.HELVETICA_BOLD,30f);

                  //  pageContentStream.showText("Última resposta CARAT");
                    pageContentStream.showText(getResources().getString(R.string.last_CARAT)); //Rute 13OUT2020
                    pageContentStream.endText();

                    pageContentStream.drawImage(imageXObject, startX+50, startY,page.getMediaBox().getWidth()-100,page.getMediaBox().getHeight()-100);

                }

                else if(fileName.equals("graph1") || fileName.equals("graph2") || fileName.equals("graph3")){

                    pageContentStream.beginText();

                    pageContentStream.newLineAtOffset(230f, 770f);
                    pageContentStream.setFont(PDMMType1Font.HELVETICA_BOLD,30f);

                    switch (fileName){

                        case "graph1":
                            //pageContentStream.showText("CARAT U");
                            pageContentStream.showText(getResources().getString(R.string.CARAT_Upper_Airways)); //Rute 13OUT2020
                            break;

                        case "graph2":
                            //pageContentStream.showText("CARAT L");
                            pageContentStream.showText(getResources().getString(R.string.CARAT_Lower_Airways)); //Rute 13OUT2020
                            break;

                        case "graph3":
                           //pageContentStream.showText("CARAT T");
                            pageContentStream.showText(getResources().getString(R.string.CARAT_Total)); //Rute 13OUT2020
                            break;
                    }

                    pageContentStream.endText();

                    pageContentStream.drawImage(imageXObject, startX+50, startY,page.getMediaBox().getWidth()-100,page.getMediaBox().getHeight()-100);
                }
                else {
                    if(fileName.equals("graph4")){

                        pageContentStream.beginText();

                        pageContentStream.setFont(PDMMType1Font.HELVETICA_BOLD, 20f);
                        pageContentStream.newLineAtOffset(25f, 760f);
                        pageContentStream.setNonStrokingColor(0,0,255);
                        //pageContentStream.showText("Adesão (%)");
                        pageContentStream.showText(getResources().getString(R.string.adherence)); //Rute 13OUT2020
                        pageContentStream.endText();

                        pageContentStream.beginText();
                        pageContentStream.newLineAtOffset(455f, 760f);
                        pageContentStream.setNonStrokingColor(255,0,0);
                        //pageContentStream.showText("CARAT Total");
                        pageContentStream.showText(getResources().getString(R.string.CARAT_Total)); //Rute 13OUT2020
                        pageContentStream.endText();

                        pageContentStream.drawImage(imageXObject, startX+50, startY,page.getMediaBox().getWidth()-100,page.getMediaBox().getHeight()-100);

                    }
                    else {

                        pageContentStream.beginText();

                        pageContentStream.newLineAtOffset(120f,770f);
                        pageContentStream.setFont(PDMMType1Font.HELVETICA_BOLD,30f);

                        //pageContentStream.showText("Resumo de última semana");
                        pageContentStream.showText(getResources().getString(R.string.Latest_week_summary)); //Rute 13OUT2020
                        pageContentStream.endText();

                        pageContentStream.drawImage(imageXObject, startX+50, startY,page.getMediaBox().getWidth()-100,page.getMediaBox().getHeight()-100);
                    }
                }

                pageContentStream.close();
                imageInput.close();

            }


            File filePath = new File(graphsPath + File.separator + pdfFileName);

            pdfDocument.save(filePath);

            pdfDocument.close();

            //FileUtils.deleteDirectory(graphsPath);

            //Toast.makeText(this,"PDF report successfully generated!",Toast.LENGTH_SHORT).show();
            Toast.makeText(this,R.string.pdf_summary,Toast.LENGTH_SHORT).show();

            finalPdfFile =  FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", filePath);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return finalPdfFile;
    }


    /**
     * Added by Alexandre Rocha - End point
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
