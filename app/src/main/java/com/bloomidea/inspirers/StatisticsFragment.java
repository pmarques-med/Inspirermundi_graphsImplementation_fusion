package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.R.string;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyRegFragment;
import com.bloomidea.inspirers.events.UserStatsModifiedEvent;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.TreeMapListener;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class StatisticsFragment extends MyRegFragment {
    private View rootView;

    /**
     * Added by Alexandre Rocha - Start point
     */
    final IntegerValueFormatter integerValueFormatter = new IntegerValueFormatter();

    private ArrayList<ListPoll> listPollsCARATTotal;
    private ArrayList<ListPoll> listPollsWeeklySymptoms;


    // Measurement units enum for pie graphs
    private enum Measurement {
        ADHERENCE, CARAT, WORK, LEISURE, HEALTH, NONE
    }

    /**
     * Added by Alexandre Rocha - End point
     */


    private Subscriber<? super Object> interfaceEvents = new Subscriber<Object>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Object o) {
            if (o instanceof UserStatsModifiedEvent) {
                //loadValues();
            }
        }
    };


    public StatisticsFragment() {
    }

    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);


        View aux = rootView.findViewById(R.id.linearLayout_stats);
        aux.setPadding(aux.getPaddingLeft(), aux.getPaddingTop(), aux.getPaddingRight(), getResources().getDimensionPixelOffset(R.dimen.bottom_stats_padding));

        /**
         * Added by Alexandre Rocha - Start point
         */
        //loadValues();
        loadCaratValues();
        /**
         * Added by Alexandre Rocha - End point
         */

        return rootView;
    }

    /**
     * Added by Alexandre Rocha - Start point
     */

//    private void loadValues() {
//        if(rootView!=null) {
//            User aux = AppController.getmInstance().getActiveUser();
//
//            GregorianCalendar firstMedDate = AppController.getmInstance().getTimelineDataSource().getFirstMedicineDate(aux.getId());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//            if (firstMedDate != null) {
//                ((TextView) rootView.findViewById(R.id.init_date_textView)).setText(getResources().getString(R.string.stats_started_in, dateFormat.format(firstMedDate.getTime())));
//            } else {
//                rootView.findViewById(R.id.init_date_textView).setVisibility(View.GONE);
//            }
//
//            ((DonutProgress) rootView.findViewById(R.id.stats_begining_donut_progress)).setUnfinishedStrokeColor(Color.parseColor(aux.getStatsEverColor()));
//            ((DonutProgress) rootView.findViewById(R.id.stats_begining_donut_progress)).setProgress(100 - aux.getStatsEver().intValue());
//            ((TextView) rootView.findViewById(R.id.percent_beginning_textview)).setText(aux.getStatsEver().setScale(0, RoundingMode.HALF_UP).toPlainString());
//
//            ((DonutProgress) rootView.findViewById(R.id.stats_month_donut_progress)).setUnfinishedStrokeColor(Color.parseColor(aux.getStatsMonthColor()));
//            ((DonutProgress) rootView.findViewById(R.id.stats_month_donut_progress)).setProgress(100 - aux.getStatsMonth().intValue());
//            ((TextView) rootView.findViewById(R.id.percent_month_textview)).setText(aux.getStatsMonth().setScale(0, RoundingMode.HALF_UP).toPlainString());
//
//            ((DonutProgress) rootView.findViewById(R.id.stats_7_days_donut_progress)).setUnfinishedStrokeColor(Color.parseColor(aux.getStatsWeekColor()));
//            ((DonutProgress) rootView.findViewById(R.id.stats_7_days_donut_progress)).setProgress(100 - aux.getStatsWeek().intValue());
//            ((TextView) rootView.findViewById(R.id.percent_7_days_textview)).setText(aux.getStatsWeek().setScale(0, RoundingMode.HALF_UP).toPlainString());
//        }
//    }


    /**
     * loads all the CARAT questionnaires answered by the user form the server
     */
    private void loadCaratValues() {

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getContext());

        APIInspirers.requestMyCaratList(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(final JSONArray responseArray) {
                ringProgressDialogNoText.dismiss();
                AppController.getmInstance().getPolls(new TreeMapListener() {
                    @Override
                    public void onResponse(TreeMap response) {

                        listPollsCARATTotal = InspirersJSONParser.parseListPollAnswered(responseArray, (Poll) response.get(Poll.POLL_TYPE_CARAT));

                        if (listPollsCARATTotal.isEmpty()) {
                            // Toast.makeText(getActivity(),"The user hasn't answered any CART questionnaires", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), R.string.No_CARAT_data_available, Toast.LENGTH_SHORT).show();

                        } else {

                            ArrayList<ListPoll> checkedCARATList = checkCARATListDoestContainRepeatedDates(listPollsCARATTotal);

                            loadWeeklySymptomsValues(checkedCARATList);

                            Map<String, Integer> caratData = organizeCARATTotalAndAdherenceData(checkedCARATList);
                            ArrayList<Float> adherenceData = organizeAdherenceDataForLineGraph(checkedCARATList);
                            generateCARATTotalGraph((LineChart) rootView.findViewById(R.id.areaGraphCARATTotal), caratData, ContextCompat.getDrawable(rootView.getContext(), R.drawable.gradient_carat_total));
                            generateCARATTotalAdherenceGraph((LineChart) rootView.findViewById(R.id.timeLineGraphAdherenceVSCARATTotal), caratData, adherenceData);

                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(), R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Queries the database for the patient's adherence values in a given week
     *
     * @param startDate the beginning of the week
     * @return the adhesion value
     */
    private BigDecimal loadAdherenceValuesByWeek(GregorianCalendar startDate) {

        int daysDiff = calculateWeeklyAdherenceDateFromToday(startDate, new GregorianCalendar());

        BigDecimal adherenceValue = AppController.getmInstance().getTimelineDataSource().getWeeklyStatsCustomInterval(AppController.getmInstance().getActiveUser().getId(), daysDiff);

        return adherenceValue;

    }

    /**
     * Queries the database for the patient's adherence values in a given custom time interval
     *
     * @param startDate the start date for the time interval
     * @param endDate   the end date for the time interval
     * @return the adhesion value
     */
    private BigDecimal loadAdherenceValuesByCustomInterval(GregorianCalendar startDate, GregorianCalendar endDate) {

        ArrayList<Integer> daysDiffs = calculateCustomIntervalAdherenceDateFromToday(startDate, endDate, new GregorianCalendar());

        BigDecimal adherenceValue = AppController.getmInstance().getTimelineDataSource().getStatsCustomInterval(AppController.getmInstance().getActiveUser().getId(), daysDiffs);

        return adherenceValue;
    }

    /**
     * loads all the weekly symptoms questionnaires answered by the user
     *
     * @param listPollsCARATTotal the list with all the patient's CARAT answers
     */
    private void loadWeeklySymptomsValues(final ArrayList<ListPoll> listPollsCARATTotal) {

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getContext());

        APIInspirers.requestMySintList(AppController.getmInstance().getActiveUser().getUid(), Poll.POLL_TYPE_WEEKLY, new JSONArrayListener() {
            @Override
            public void onResponse(final JSONArray responseArrayWeekly) {

                ringProgressDialogNoText.dismiss();
                AppController.getmInstance().getPolls(new TreeMapListener() {
                    @Override
                    public void onResponse(TreeMap response) {

                        listPollsWeeklySymptoms = InspirersJSONParser.parseListPollAnswered(responseArrayWeekly, (Poll) response.get(Poll.POLL_TYPE_WEEKLY));

                        if (listPollsWeeklySymptoms.isEmpty()) {
                            //Toast.makeText(getActivity(),"The user hasn't answered any symptoms questionnaires", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), R.string.No_symptoms_data_available, Toast.LENGTH_SHORT).show(); //Rute 13OUT2020
                        } else {

                            int symptomsListSize = listPollsWeeklySymptoms.size();
                            int caratListSize = listPollsCARATTotal.size();

                            //limits the list to the last 24 weeks in case the list is longer than that
                            if (symptomsListSize > 24) {

                                listPollsWeeklySymptoms = new ArrayList<>(listPollsWeeklySymptoms.subList(symptomsListSize - 24, symptomsListSize));
                            }


//                            //setups the slide bar taking in consideration whether the user the most recent questionnaire answer is a CARAT questionnaire or a weekly symptoms questionnaire
//                            if(listPollsWeeklySymptoms.get(symptomsListSize-1).getAsweredDate().getTime().after(listPollsCARATTotal.get(caratListSize-1).getAsweredDate().getTime())){
//                                setupSlideBar(symptomsListSize-1);
//                            }
//                            else{
//                                setupSlideBar(caratListSize-1);
//                            }

                            setupSlideBar(symptomsListSize - 1);

                            updateSymptomsData(listPollsWeeklySymptoms, symptomsListSize - 1);
                            //organizeSymptomsData(listPollsWeeklySymptoms,symptomsListSize-1);

                            selectWeekWithSlideBarPieCharts(listPollsWeeklySymptoms);

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

    private ArrayList<ListPoll> checkCARATListDoestContainRepeatedDates(ArrayList<ListPoll> listPollsCARATTotal) {

        GregorianCalendar auxDate;
        String auxDate2;
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<ListPoll> checkedCARATList = new ArrayList<>();


        for (ListPoll listPoll : listPollsCARATTotal) {
            auxDate = listPoll.getAsweredDate();

            //auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.ENGLISH);
            auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());  //Rute 13OUT2020
            if (!dates.contains(auxDate2)) {
                dates.add(auxDate2);
                checkedCARATList.add(listPoll);
            }
        }

        //limits the CARAT list to the latest 6 answers
        if (checkedCARATList.size() > 6) {

            checkedCARATList = new ArrayList<>(checkedCARATList.subList(checkedCARATList.size() - 6, checkedCARATList.size()));

        }

        return checkedCARATList;
    }


    /**
     * Prepares the CARAT answers data for graph creation
     *
     * @param listPollsCARATTotal the list containing all the patient's CARAT questionnaires answers
     */
    public Map<String, Integer> organizeCARATTotalAndAdherenceData(ArrayList<ListPoll> listPollsCARATTotal) {

        int auxScore;
        GregorianCalendar auxDate;
        String auxDate2;
        Map<String, Integer> caratTotalData = new LinkedHashMap<>();

        //limits the CARAT list to the latest 6 answers
        if (listPollsCARATTotal.size() > 6) {

            listPollsCARATTotal = new ArrayList<>(listPollsCARATTotal.subList(listPollsCARATTotal.size() - 6, listPollsCARATTotal.size()));

        }

        for (ListPoll listPoll : listPollsCARATTotal) {
            auxScore = calculateCARATTotalScore(listPoll.getPoll());
            auxDate = listPoll.getAsweredDate();

//            auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.ENGLISH);
            auxDate2 = auxDate.get(Calendar.DAY_OF_MONTH) + "/" + auxDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());  //Rute 13OUT2020

            if (!caratTotalData.containsKey(auxDate2)) {
                caratTotalData.put(auxDate2, auxScore);
            }

        }

        //generateCARATTotalGraph(caratTotalData);
        //generateCARATTotalAdherenceGraph(caratTotalData,organizeAdherenceDataForLineGraph(listPollsCARATTotal));

        return caratTotalData;
    }

    /**
     * Prepares the adherence data for graph creation
     *
     * @param listPollsCARATTotal the list congaing all the usable CARAT questionnaire answers
     * @return
     */
    public ArrayList<Float> organizeAdherenceDataForLineGraph(ArrayList<ListPoll> listPollsCARATTotal) {

        BigDecimal auxAdherence;
        ListPoll listPollAux;
        ArrayList<Float> adherenceValues = new ArrayList<>();

        if (listPollsCARATTotal.size() > 1) {

            for (int i = 0; i < listPollsCARATTotal.size(); i++) {

                listPollAux = listPollsCARATTotal.get(i);

                if (i == listPollsCARATTotal.size() - 1) {
                    auxAdherence = loadAdherenceValuesByCustomInterval(listPollsCARATTotal.get(i - 1).getAsweredDate(), listPollAux.getAsweredDate());
                } else {
                    auxAdherence = loadAdherenceValuesByCustomInterval(listPollAux.getAsweredDate(), listPollsCARATTotal.get(i + 1).getAsweredDate());
                }

                adherenceValues.add(auxAdherence.floatValue());
            }
        }
        return (adherenceValues);
    }


    public void updateSymptomsData(ArrayList<ListPoll> listPollsWeeklySymptoms, int answerOrderPos) {

        // boolean userHasCarat;
        int caratAux = 0;
        Map<String, Integer> symptomsData;

        BigDecimal adherenceAux = loadAdherenceValuesByWeek(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate());


        try {
            caratAux = getCaratTotalScoreByDate(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate());
            // userHasCarat=true;
        } catch (NoSuchElementException exp) {
            //userHasCarat=false;
            caratAux = -1;
        }

        symptomsData = organizeSymptomsData(listPollsWeeklySymptoms, answerOrderPos, adherenceAux, caratAux);

        //if there is a symptoms questionnaire answer for the next week, the textbox uses that date as the end of the current week
        if (answerOrderPos < listPollsWeeklySymptoms.size() - 1) {
            updateSlideBarTextView(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate(), listPollsWeeklySymptoms.get(answerOrderPos + 1).getAsweredDate());
        } else {
            updateSlideBarTextView(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate(), null);
        }
        generateSummaryPieGraphs((PieChart) rootView.findViewById(R.id.pieGraphAdherence), (PieChart) rootView.findViewById(R.id.pieGraphCaratTotal), (PieChart) rootView.findViewById(R.id.pieGraphSymptomsWork),
                (PieChart) rootView.findViewById(R.id.pieGraphSymptomsLeisure), (PieChart) rootView.findViewById(R.id.pieGraphSymptomsHealth), symptomsData);


    }

    /**
     * Prepares symptoms questionnaires answers data for graph creation
     *
     * @param listPollsWeeklySymptoms the list containing all the usable weekly symptoms questionnaire answers
     * @param answerOrderPos          the order position of the week which data will be displayed
     */
    public Map<String, Integer> organizeSymptomsData(ArrayList<ListPoll> listPollsWeeklySymptoms, int answerOrderPos, BigDecimal adherenceAux, int caratAux) {


        Map<String, Integer> values = new TreeMap<>();

        int workAux = 0;
        //int caratAux=0;
        int leisureAux = 0;
        int healthAux = 0;

//        boolean userHasWork;
//        boolean userHasCarat;
//        boolean userHasLeisure;
//        boolean userHasHealth;


        // BigDecimal adherenceAux = loadAdherenceValuesByWeek(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate());


        //if the patient has only answered to a CARAT questionnaire this week, there wont be any symptoms data to show
        if (listPollsWeeklySymptoms.size() < answerOrderPos + 1) {
//            userHasWork=false;
//            userHasLeisure=false;
//            userHasHealth=false;
            workAux = -1;
            leisureAux = -1;
            healthAux = -1;
        } else {
            ArrayList<Question> listQuestions = listPollsWeeklySymptoms.get(answerOrderPos).getPoll().getListQuestions();

            if (((QuestionYesNo) listQuestions.get(0)).isYes()) {

                //userHasWork=true;
                workAux = ((QuestionSlider) listQuestions.get(1)).getTotalSelected();
            } else {
                //userHasWork=false;
                workAux = -1;
            }

            //userHasLeisure=true;
            leisureAux = ((QuestionSlider) listQuestions.get(2)).getTotalSelected();

            //userHasHealth=true;
            healthAux = ((QuestionSlider) listQuestions.get(3)).getTotalSelected();
        }


        //get corresponding week carat data
//        try {
//            caratAux = getCaratTotalScoreByDate(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate());
//            userHasCarat=true;
//        }
//        catch (NoSuchElementException exp){
//            userHasCarat=false;
//        }

        values.put("Adherence", adherenceAux.intValue());
        values.put("Carat", caratAux);
        values.put("Leisure", leisureAux);
        values.put("Work", workAux);
        values.put("Health", healthAux);

        return values;

        //updateSlideBarTextView(listPollsWeeklySymptoms.get(answerOrderPos).getAsweredDate());
        //generateSummaryPieGraphs(userHasWork,userHasCarat, userHasLeisure,userHasHealth,adherenceAux, caratAux, workAux, leisureAux,healthAux);
    }

    /**
     * Calculates the CARAT Total score from a given poll/questioner answer
     *
     * @param poll the CARAT answer
     * @return the CARAT Total score
     */
    public int calculateCARATTotalScore(Poll poll) {

        int totalScore = 0;

        QuestionMultipleChoice auxQ;
        for (Question q : poll.getListQuestions()) {
            auxQ = (QuestionMultipleChoice) q;

            for (Answer a : auxQ.getListAnswers()) {
                if (a.isSelected()) {
                    if (a.getNid().equals("14")) {
                        totalScore += 3;

                    } else if (a.getNid().equals("15")) {
                        totalScore += 2;

                    } else if (a.getNid().equals("16")) {
                        totalScore += 1;

                    } else if (a.getNid().equals("19")) {
                        totalScore += 2;
                    } else if (a.getNid().equals("18")) {
                        totalScore += 3;
                    }
                }
            }

        }

        return totalScore;
    }

    /**
     * Returns a CARAT Total score from a given week
     *
     * @param date the date belonging to the desired week
     * @return the CARAT Total score
     */
    public int getCaratTotalScoreByDate(GregorianCalendar date) {

        boolean stopCondition = false;
        int i = 0;
        int listSize = listPollsCARATTotal.size();
        int firstDayOfWeekAux;
        GregorianCalendar aux;

        firstDayOfWeekAux = date.get(GregorianCalendar.DAY_OF_WEEK);
        date.setFirstDayOfWeek(firstDayOfWeekAux);
        while (!stopCondition) {

            if (i < listSize) {

                aux = listPollsCARATTotal.get(i).getAsweredDate();
                aux.setFirstDayOfWeek(firstDayOfWeekAux);

                if (aux.get(Calendar.WEEK_OF_YEAR) == (date.get(Calendar.WEEK_OF_YEAR))) {

                    stopCondition = true;
                } else {
                    i++;
                }
            } else {
                // throw new NoSuchElementException("The user does not have a CARAT questionnaire from the desired week");
                throw new NoSuchElementException(getResources().getString(string.No_CARAT_data_available_week)); //Rute 13OU2020
            }
        }

        return calculateCARATTotalScore(listPollsCARATTotal.get(i).getPoll());
    }


    /**
     * Calculates the necessary date (using today as a reference) to query the database for the weekly adhesion value for that date
     *
     * @param answerDate the beginning of the week
     * @return the days interval between today and the given date
     */
    public int calculateWeeklyAdherenceDateFromToday(GregorianCalendar answerDate, GregorianCalendar todayDate) {


        long startDate = answerDate.getTime().getTime();
        long today = todayDate.getTime().getTime();


        //startDate and endDate are in milliseconds, so the difference must be converted to days
        long daysIntervalToToday = (today - startDate) / (1000 * 60 * 60 * 24);

        return (int) daysIntervalToToday;

    }

    /**
     * Calculates the necessary dates (using today as a reference) to query the database for the adhesion value for that time interval
     *
     * @param startDate the beginning of the time interval
     * @param endDate   the end of the time interval
     * @return the days intervals between today and the start date, as well as today and the end date
     */
    public ArrayList<Integer> calculateCustomIntervalAdherenceDateFromToday(GregorianCalendar startDate, GregorianCalendar endDate, GregorianCalendar todayDate) {

        long today = todayDate.getTime().getTime();
        long startDate2 = startDate.getTime().getTime();
        long endDate2 = endDate.getTime().getTime();


        //startDate and endDate are in milliseconds, so the difference must be converted to days
        int daysIntervalFromStartToToday = (int) ((today - startDate2) / (1000 * 60 * 60 * 24));
        int daysIntervalFromEndToToday = (int) ((today - endDate2) / (1000 * 60 * 60 * 24));

        ArrayList<Integer> dates = new ArrayList<>();
        dates.add(daysIntervalFromStartToToday);
        dates.add(daysIntervalFromEndToToday);

        return dates;

    }


    /**
     * Generate CARAT Total area graph
     *
     * @param caratTotalData the usable CARAT Total data to generate the graph
     */
    public void generateCARATTotalGraph(LineChart areaGraphCARATTotal, Map<String, Integer> caratTotalData, Drawable caratGradient) {

        //LineChart areaGraphCARATTotal = rootView.findViewById(R.id.areaGraphCARATTotal);

        //x axis labels
        XAxis xAxis = areaGraphCARATTotal.getXAxis();
        ArrayList<String> xAxisValues = new ArrayList<>();

        //CARAT Total data
        List<Entry> caratTotalEntries = new ArrayList<>();

        int aux = 0;

        //fill x axis labels and graph data with carat values
        for (Map.Entry<String, Integer> set : caratTotalData.entrySet()) {
            xAxisValues.add(set.getKey());
            caratTotalEntries.add(new Entry(aux, set.getValue()));
            aux++;
        }

        // LineDataSet caratTotalEntriesDataSet = new LineDataSet(caratTotalEntries, "CARAT Total");
        LineDataSet caratTotalEntriesDataSet = new LineDataSet(caratTotalEntries, "CARAT Total");
        caratTotalEntriesDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratTotalEntriesDataSet.setDrawValues(true);
        caratTotalEntriesDataSet.setCubicIntensity(0.1f);
        caratTotalEntriesDataSet.setDrawCircles(true);
        caratTotalEntriesDataSet.setLineWidth(0f);
        caratTotalEntriesDataSet.setCircleRadius(0.9f);
        caratTotalEntriesDataSet.setCircleColor(Color.BLACK);


//        Drawable gradient = ContextCompat.getDrawable(rootView.getContext(),R.drawable.gradient_carat_total);
//        assert gradient != null;
//        gradient.setAlpha(255);
//        caratTotalEntriesDataSet.setFillDrawable(gradient);
//        caratTotalEntriesDataSet.setDrawFilled(true);
//        caratTotalEntriesDataSet.setFillAlpha(255);

        assert caratGradient != null;
        caratGradient.setAlpha(255);
        caratTotalEntriesDataSet.setFillDrawable(caratGradient);
        caratTotalEntriesDataSet.setDrawFilled(true);
        caratTotalEntriesDataSet.setFillAlpha(255);


        //CARAT Total Threshold data
        List<Entry> caratTotalThreshold = new ArrayList<>();

        for (aux = 0; aux < caratTotalEntries.size(); aux++) {
            caratTotalThreshold.add(new Entry(aux, 24));
        }


        LineDataSet caratTotalThresholdEntriesDataSet = new LineDataSet(caratTotalThreshold, "Threshold");
        caratTotalThresholdEntriesDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        caratTotalThresholdEntriesDataSet.setCubicIntensity(0.1f);
        caratTotalThresholdEntriesDataSet.setColor(Color.BLACK);
        caratTotalThresholdEntriesDataSet.setLineWidth(3f);
        caratTotalThresholdEntriesDataSet.setDrawValues(false);
        caratTotalThresholdEntriesDataSet.setDrawCircles(false);


        LineData caratTotalLineData = new LineData(caratTotalEntriesDataSet, caratTotalThresholdEntriesDataSet);
        caratTotalLineData.setValueFormatter(new IntegerValueFormatter());
        caratTotalLineData.setValueTextSize(11f);

        areaGraphCARATTotal.setData(caratTotalLineData);


        areaGraphCARATTotal.getDescription().setText("");

        areaGraphCARATTotal.getAxisLeft().setDrawGridLines(false);
        areaGraphCARATTotal.getAxisRight().setDrawGridLines(false);

        areaGraphCARATTotal.getAxisLeft().setAxisMinimum(0f);
        areaGraphCARATTotal.getAxisLeft().setAxisMaximum(31f);
        areaGraphCARATTotal.getAxisRight().setAxisMinimum(0f);
        areaGraphCARATTotal.getAxisRight().setAxisMaximum(31f);

        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setLabelCount(xAxisValues.size(), true);

        areaGraphCARATTotal.setDrawBorders(true);
        areaGraphCARATTotal.getLegend().setEnabled(false);

        areaGraphCARATTotal.invalidate(); // refresh

    }


    /**
     * Generates the line graph comparing the patient's CARAT Total and adhesion data
     *
     * @param caratTotalData the usable CARAT Total data to generate the graph
     * @param adherenceData  the usable adhesion data to generate the graph
     */
    public void generateCARATTotalAdherenceGraph(LineChart lineGraphAdherenceVSCARATT, Map<String, Integer> caratTotalData, ArrayList<Float> adherenceData) {

        // final LineChart lineGraphAdherenceVSCARATT = rootView.findViewById(R.id.timeLineGraphAdherenceVSCARATTotal);

        //x axis labels
        XAxis xAxis = lineGraphAdherenceVSCARATT.getXAxis();
        ArrayList<String> xAxisValues = new ArrayList<>();

        //CARAT Total data
        List<Entry> caratTotalEntries = new ArrayList<>();

        int i = 0;

        //fill x axis labels and graph data with carat values
        for (Map.Entry<String, Integer> set : caratTotalData.entrySet()) {
            xAxisValues.add(set.getKey());
            caratTotalEntries.add(new Entry(i, set.getValue() * 100 / 30));
            i++;
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setLabelCount(xAxisValues.size(), true);
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
        yAxisRight.setAxisMaximum(103f);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setAxisLineColor(caratTotalEntriesDataSet.getColor());
        yAxisRight.setAxisLineWidth(3f);

        //adherence data
        List<Entry> adherenceEntries = new ArrayList<>();
        i = 0;
        for (float value : adherenceData) {
            adherenceEntries.add(new Entry(i, value));
            i++;
        }
        LineDataSet adherenceEntriesDataSet = new LineDataSet(adherenceEntries, "Adherence (%)");
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
        i = 0;
        for (Entry aux : caratTotalEntries) {
            caratTotalThresholdEntries.add(new Entry(i, 24 * 100 / 30));
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

        //configure y axis text views
        TextView rightAxisLabel = rootView.findViewById(R.id.textViewRightAxisLabel);
        rightAxisLabel.setTextColor(caratTotalEntriesDataSet.getColor());

        TextView leftAxisLabel = rootView.findViewById(R.id.textViewLeftAxisLabel);
        leftAxisLabel.setTextColor(adherenceEntriesDataSet.getColor());

        lineGraphAdherenceVSCARATT.getDescription().setText("");
        lineGraphAdherenceVSCARATT.setDrawBorders(true);
        lineGraphAdherenceVSCARATT.getLegend().setEnabled(false);


        lineGraphAdherenceVSCARATT.invalidate(); // refresh
    }

    /**
     * Generates all five pie graphs that summarizes the user health state
     *
     * @param symptomsData the data for all the parameters measured by the pie graphs
     */

    private void generateSummaryPieGraphs(PieChart pieGraphAdherence, PieChart pieGraphCARATTotal, PieChart pieGraphSymptomsWork, PieChart pieGraphSymptomsLeisure,
                                          PieChart pieGraphSymptomsHealth, Map<String, Integer> symptomsData) {

        int adherenceValue = symptomsData.get("Adherence");
        int caratTotalValue = symptomsData.get("Carat");
        int symptomsWorkValue = symptomsData.get("Work");
        int symptomsLeisureValue = symptomsData.get("Leisure");
        int symptomsHealthValue = symptomsData.get("Health");


        int centerTextSize = 16;
        float graphNameTextSize = 13.5f;

        //adherence pie graph
        //final PieChart pieGraphAdherence = rootView.findViewById(R.id.pieGraphAdherence);

        ArrayList<PieEntry> adherenceEntries = new ArrayList<>();
        adherenceEntries.add(new PieEntry(adherenceValue));
        adherenceEntries.add(new PieEntry(100 - adherenceValue));

        PieDataSet pieAdherenceDataSet = new PieDataSet(adherenceEntries, "");
        pieAdherenceDataSet.setDrawValues(false);
        setPieSlicesColours(Measurement.ADHERENCE, pieAdherenceDataSet);
        PieData pieAdherenceData = new PieData(pieAdherenceDataSet);

        pieGraphAdherence.setData(pieAdherenceData);

        pieGraphAdherence.setCenterText(String.valueOf(adherenceValue) + "%");
        pieGraphAdherence.setCenterTextSize(centerTextSize);

        // pieGraphAdherence.getDescription().setText("Adherence");
        pieGraphAdherence.getDescription().setText(getResources().getString(R.string.adherence)); //Rute 13OUT2020
        pieGraphAdherence.getDescription().setTextColor(Color.BLACK);
        pieGraphAdherence.getDescription().setTextSize(graphNameTextSize);
        pieGraphAdherence.getLegend().setEnabled(false);
        pieGraphAdherence.invalidate();


        //CARAT total pie graph
        //final PieChart pieGraphCARATTotal = rootView.findViewById(R.id.pieGraphCaratTotal);

        //pieGraphCARATTotal.getDescription().setText("CARAT Total");
        pieGraphCARATTotal.getDescription().setText(getResources().getString(R.string.CARAT_T)); //Rute 13OUT2020


        pieGraphCARATTotal.getDescription().setTextColor(Color.BLACK);
        pieGraphCARATTotal.getDescription().setTextSize(graphNameTextSize);
        //pieGraphAdherence.getDescription().setPosition(pieGraphAdherence.getDescription().getPosition().x/2, pieGraphAdherence.getDescription().getPosition().y*2);
        pieGraphCARATTotal.getLegend().setEnabled(false);

        ArrayList<PieEntry> caratTotalEntries = new ArrayList<>();

        if (caratTotalValue >= 0) {
            caratTotalEntries.add(new PieEntry(caratTotalValue));
            caratTotalEntries.add(new PieEntry(30 - caratTotalValue));

            PieDataSet pieCaratTotalDataSet = new PieDataSet(caratTotalEntries, "");
            pieCaratTotalDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.CARAT, pieCaratTotalDataSet);
            PieData pieCaratTotalData = new PieData(pieCaratTotalDataSet);

            pieGraphCARATTotal.setData(pieCaratTotalData);

            pieGraphCARATTotal.setCenterText(String.valueOf(caratTotalValue));
            pieGraphCARATTotal.setCenterTextSize(centerTextSize);
        } else {
            caratTotalEntries.add(new PieEntry(100));
            PieDataSet pieWorkDataSet = new PieDataSet(caratTotalEntries, "");
            pieWorkDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.NONE, pieWorkDataSet);
            PieData pieWorkData = new PieData(pieWorkDataSet);

            pieGraphCARATTotal.setData(pieWorkData);
            //pieGraphCARATTotal.setCenterText("No data");
            pieGraphCARATTotal.setCenterText(getResources().getString(R.string.No_data)); //Rute 13OUT2020 to be tested
            pieGraphCARATTotal.setCenterTextSize(centerTextSize);
        }

        pieGraphCARATTotal.invalidate();


        //work symptoms pie graph
        //final PieChart pieGraphSymptomsWork = rootView.findViewById(R.id.pieGraphSymptomsWork);

        //pieGraphSymptomsWork.getDescription().setText("Symptoms - Work/School");
        pieGraphSymptomsWork.getDescription().setText(getResources().getString(R.string.Symptoms_WS)); //Rute 13OUT2020


        pieGraphSymptomsWork.getDescription().setTextColor(Color.BLACK);
        pieGraphSymptomsWork.getDescription().setTextSize(graphNameTextSize);
        pieGraphSymptomsWork.getLegend().setEnabled(false);

        ArrayList<PieEntry> workEntries = new ArrayList<>();
        if (symptomsWorkValue >= 0) {

            workEntries.add(new PieEntry(symptomsWorkValue));
            workEntries.add(new PieEntry(100 - symptomsWorkValue));

            PieDataSet pieWorkDataSet = new PieDataSet(workEntries, "");
            pieWorkDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.WORK, pieWorkDataSet);
            PieData pieWorkData = new PieData(pieWorkDataSet);

            pieGraphSymptomsWork.setData(pieWorkData);

            pieGraphSymptomsWork.setCenterText(String.valueOf(symptomsWorkValue) + "%");
            pieGraphSymptomsWork.setCenterTextSize(centerTextSize);
        } else {
            workEntries.add(new PieEntry(100));
            PieDataSet pieWorkDataSet = new PieDataSet(workEntries, "");
            pieWorkDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.NONE, pieWorkDataSet);
            PieData pieWorkData = new PieData(pieWorkDataSet);

            pieGraphSymptomsWork.setData(pieWorkData);

            //pieGraphSymptomsWork.setCenterText("No data");
            pieGraphSymptomsWork.setCenterText(getResources().getString(R.string.No_data)); //Rute 13OUT2020 to be tested
            pieGraphSymptomsWork.setCenterTextSize(centerTextSize);
        }

        pieGraphSymptomsWork.invalidate();


        //leisure symptoms pie graph
        //final PieChart pieGraphSymptomsLeisure = rootView.findViewById(R.id.pieGraphSymptomsLeisure);

        //pieGraphSymptomsLeisure.getDescription().setText("Symptoms - Leisure");
        pieGraphSymptomsLeisure.getDescription().setText(getResources().getString(R.string.Symptoms_Leisure)); //Rute 13OUT2020

        pieGraphSymptomsLeisure.getDescription().setTextColor(Color.BLACK);
        pieGraphSymptomsLeisure.getDescription().setTextSize(graphNameTextSize);
        pieGraphSymptomsLeisure.getLegend().setEnabled(false);


        ArrayList<PieEntry> leisureEntries = new ArrayList<>();
        if (symptomsLeisureValue >= 0) {

            leisureEntries.add(new PieEntry(symptomsLeisureValue));
            leisureEntries.add(new PieEntry(100 - symptomsLeisureValue));

            PieDataSet pieLeisureDataSet = new PieDataSet(leisureEntries, "");
            pieLeisureDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.LEISURE, pieLeisureDataSet);
            PieData pieLeisureData = new PieData(pieLeisureDataSet);

            pieGraphSymptomsLeisure.setData(pieLeisureData);

            pieGraphSymptomsLeisure.setCenterText(String.valueOf(symptomsLeisureValue) + "%");
            pieGraphSymptomsLeisure.setCenterTextSize(centerTextSize);
        } else {
            leisureEntries.add(new PieEntry(100));
            PieDataSet pieLeisureDataSet = new PieDataSet(workEntries, "");
            pieLeisureDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.NONE, pieLeisureDataSet);
            PieData pieLeisureData = new PieData(pieLeisureDataSet);

            pieGraphSymptomsLeisure.setData(pieLeisureData);

            // pieGraphSymptomsLeisure.setCenterText("No data");
            pieGraphSymptomsLeisure.setCenterText(getResources().getString(R.string.No_data)); //Rute 13OUT2020 to be tested
            pieGraphSymptomsLeisure.setCenterTextSize(centerTextSize);
        }

        pieGraphSymptomsLeisure.invalidate();


        //Health symptoms pie graph
        //final PieChart pieGraphSymptomsHealth = rootView.findViewById(R.id.pieGraphSymptomsHealth);

        //pieGraphSymptomsHealth.getDescription().setText("Symptoms - Health");
        pieGraphSymptomsHealth.getDescription().setText(getResources().getString(R.string.Symptoms_Health));
        pieGraphSymptomsHealth.getDescription().setTextColor(Color.BLACK);
        pieGraphSymptomsHealth.getDescription().setTextSize(graphNameTextSize);
        //pieGraphAdherence.getDescription().setPosition(pieGraphAdherence.getDescription().getPosition().x/2, pieGraphAdherence.getDescription().getPosition().y*2);
        pieGraphSymptomsHealth.getLegend().setEnabled(false);

        ArrayList<PieEntry> healthEntries = new ArrayList<>();
        if (symptomsHealthValue >= 0) {

            healthEntries.add(new PieEntry(symptomsHealthValue));
            healthEntries.add(new PieEntry(100 - symptomsHealthValue));

            PieDataSet pieHealthDataSet = new PieDataSet(healthEntries, "");
            pieHealthDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.HEALTH, pieHealthDataSet);
            PieData pieHealthData = new PieData(pieHealthDataSet);

            pieGraphSymptomsHealth.setData(pieHealthData);

            pieGraphSymptomsHealth.setCenterText(symptomsHealthValue + "%");
            pieGraphSymptomsHealth.setCenterTextSize(centerTextSize);
        } else {
            healthEntries.add(new PieEntry(100));
            PieDataSet pieHealthDataSet = new PieDataSet(healthEntries, "");
            pieHealthDataSet.setDrawValues(false);
            setPieSlicesColours(Measurement.NONE, pieHealthDataSet);
            PieData pieHealthData = new PieData(pieHealthDataSet);

            pieGraphSymptomsHealth.setData(pieHealthData);

            // pieGraphSymptomsHealth.setCenterText("No data");
            pieGraphSymptomsHealth.setCenterText(getResources().getString(R.string.No_data)); //Rute13OUT2020
            pieGraphSymptomsHealth.setCenterTextSize(centerTextSize);
        }

        pieGraphSymptomsHealth.invalidate();
    }


    /**
     * Sets the pie graphs colours
     *
     * @param measure the pie graph's measure (adherence, CARAT, leisure, health, work or none)
     * @param dataSet the pie graph's data set
     */
    private void setPieSlicesColours(Measurement measure, PieDataSet dataSet) {

        //colour values for pie graphs
        int grey = Color.rgb(220, 220, 220);
        int[] greenAndGreyColourPieSlicesScheme = {Color.rgb(100, 255, 100), grey};
        int[] redAndGrayColourPieSlicesScheme = {Color.rgb(255, 80, 80), grey};
        int[] blueAndGreyColourPieSlicesScheme = {Color.rgb(80, 80, 255), grey};
        int[] greyColourPieSliceScheme = {grey};

        int[] color = null;

        switch (measure) {

            case ADHERENCE:

                if (dataSet.getEntryForIndex(0).getValue() < 80) {
                    color = redAndGrayColourPieSlicesScheme;
                } else {
                    color = greenAndGreyColourPieSlicesScheme;
                }

                break;

            case CARAT:
                if (dataSet.getEntryForIndex(0).getValue() < 24) {
                    color = redAndGrayColourPieSlicesScheme;
                } else {
                    color = greenAndGreyColourPieSlicesScheme;
                }
                break;

            case LEISURE:
            case HEALTH:
            case WORK:

                color = blueAndGreyColourPieSlicesScheme;
                break;

            case NONE:
                color = greyColourPieSliceScheme;
                break;
        }

        dataSet.setColors(color);

    }

    /**
     * Setups the slide bar
     *
     * @param answerPos the position to place the slide bar
     */
    private void setupSlideBar(int answerPos) {

        SeekBar slideBar = rootView.findViewById(R.id.seekBarSummaryPieGraphsWeek);
        slideBar.setMax(answerPos);
        slideBar.setProgress(answerPos);
    }

    /**
     * Checks whether the slide bar has been used and reorganizes the data for the pie graphs to correspond to the selected week
     *
     * @param listPollsWeeklySymptoms the list of the usable weekly symptoms questionnaires answers
     */
    private void selectWeekWithSlideBarPieCharts(final ArrayList<ListPoll> listPollsWeeklySymptoms) {

        SeekBar slideBar = rootView.findViewById(R.id.seekBarSummaryPieGraphsWeek);

        slideBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                updateSymptomsData(listPollsWeeklySymptoms, progress);
                //organizeSymptomsData(listPollsWeeklySymptoms,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Updates the text view above the slide bar with the correct week
     *
     * @param startDate the start date of the week
     * @param endDate   the end date of the week
     */
    private void updateSlideBarTextView(GregorianCalendar startDate, GregorianCalendar endDate) {

        TextView textViewSlideBar = rootView.findViewById(R.id.textViewSummaryPieGraphsWeek);

        Calendar startDateAux = (Calendar) startDate.clone();

        int weekStart, weekEnd = 0;
        String monthStart, monthEnd = "";

        if (endDate == null) {

            weekStart = startDateAux.get(Calendar.DAY_OF_MONTH);
            // monthStart = startDateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.ENGLISH);
            monthStart = startDateAux.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());  //Rute 13OUT2020

            startDateAux.add(Calendar.DAY_OF_MONTH, 6);
            weekEnd = startDateAux.get(Calendar.DAY_OF_MONTH);
            // monthEnd = startDateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.ENGLISH);
            monthEnd = startDateAux.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());  //Rute 13OUT2020
        } else {

            Calendar endDateAux = (Calendar) endDate.clone();

            weekStart = startDateAux.get(Calendar.DAY_OF_MONTH);
            // monthStart = startDateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.ENGLISH);
            monthStart = startDateAux.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

            weekEnd = endDateAux.get(Calendar.DAY_OF_MONTH);
            // monthEnd = endDateAux.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.ENGLISH);
            monthEnd = endDateAux.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        }


        // textViewSlideBar.setText("Week: " + weekStart + "/" + monthStart + " - " + weekEnd + "/" + monthEnd);
        textViewSlideBar.setText(+weekStart + "/" + monthStart + " - " + weekEnd + "/" + monthEnd); //Rute 13OUT2020

    }


    /**
     * Added by Alexandre Rocha - End point
     **/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        AppController.getmInstance().getMyBus().toObserverable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(interfaceEvents);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        interfaceEvents.unsubscribe();
    }
}
