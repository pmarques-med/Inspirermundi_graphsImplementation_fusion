package com.bloomidea.inspirers;

import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;

import junit.extensions.ActiveTestSuite;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class StatisticsFragmentTest {

    private ArrayList<ListPoll> caratListPoll;
    private ArrayList<ListPoll> caratListPollLonger;
    private ArrayList<ListPoll> symptomsListPoll;

    private StatisticsFragment context;

    private ActiveTestSuite test;


    public StatisticsFragmentTest() {

        context = StatisticsFragment.newInstance();


        //Answers data preparation
        Answer caratNormalAnswer1 = new Answer((long) 1, "14", "Never", true);
        Answer caratNormalAnswer2 = new Answer((long) 2, "15", "Up to 1 or 2 days a week", false);
        Answer caratNormalAnswer3 = new Answer((long) 3, "16", "More than 2 days a week", false);
        Answer caratNormalAnswer4 = new Answer((long) 4, "17", "Almost every or every day", false);

        Answer caratLastAnswer1 = new Answer((long) 1, "18", "Im not taking medicine", true);
        Answer caratLastAnswer2 = new Answer((long) 2, "14", "Never", false);
        Answer caratLastAnswer3 = new Answer((long) 3, "19", "Less than 7 days", false);
        Answer caratLastAnswer4 = new Answer((long) 4, "20", "7 or more days", false);

        ArrayList<Answer> caratNormalAnswers = new ArrayList<>(Arrays.asList(caratNormalAnswer1, caratNormalAnswer2, caratNormalAnswer3, caratNormalAnswer4));
        ArrayList<Answer> caratLastAnswers = new ArrayList<>(Arrays.asList(caratLastAnswer1, caratLastAnswer2, caratLastAnswer3, caratLastAnswer4));


        //Question data preparation
        QuestionMultipleChoice caratQuestion1 = new QuestionMultipleChoice((long) 111, "252", "Stuffy Nose?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion2 = new QuestionMultipleChoice((long) 112, "257", "Sneezing?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion3 = new QuestionMultipleChoice((long) 113, "262", "Itchy Nose?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion4 = new QuestionMultipleChoice((long) 114, "267", "Runny Nose?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion5 = new QuestionMultipleChoice((long) 115, "272", "Shortness of breath/dyspnea?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion6 = new QuestionMultipleChoice((long) 116, "277", "Whistling breath/Wheeze?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion7 = new QuestionMultipleChoice((long) 117, "282", "Chest tightness with physical effort?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion8 = new QuestionMultipleChoice((long) 118, "287", "Tiredness / difficulty in doing your daily activities or tasks?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion9 = new QuestionMultipleChoice((long) 119, "292", "Woke up at night because of your asthma / rhinitis / allergy?", "Multiple", caratNormalAnswers);
        QuestionMultipleChoice caratQuestion10 = new QuestionMultipleChoice((long) 110, "297", "Increase the use of your medicine", "Multiple", caratLastAnswers);

        QuestionYesNo symptomsQuestion1WithWork = new QuestionYesNo((long) 211, "307", "Are you employed (getting paid) or studying at the moment?", "True/false", true);
        QuestionYesNo symptomsQuestion1WithoutWork = new QuestionYesNo((long) 212, "307", "Are you employed (getting paid) or studying at the moment?", "True/false", false);
        QuestionSlider symptomsQuestion2 = new QuestionSlider((long) 22, "6721", "During the last 7 days, to what extent has your asthma affected your ability " +
                "to perform your normal work / school activities?", "Slider", 75, false);
        QuestionSlider symptomsQuestion3 = new QuestionSlider((long) 23, "308", "During the last 7 days, to what extent has your asthma affected your ability " +
                "to perform your normal  <b>non-<b>work / school activities?", "Slider", 50, false);
        QuestionSlider symptomsQuestion4 = new QuestionSlider((long) 24, "310", "In general, is your health good or bad? (min: Worst health I can imagine;" +
                "max: Best health I can imagine)", "Slider", 40, false);


        ArrayList<Question> caratPoll1Questions = new ArrayList<Question>(Arrays.asList(caratQuestion1, caratQuestion2, caratQuestion3, caratQuestion4, caratQuestion5, caratQuestion6, caratQuestion7,
                caratQuestion8, caratQuestion9, caratQuestion10));

        ArrayList<Question> symptomsPollQuestionsWithWork = new ArrayList<>(Arrays.asList(symptomsQuestion1WithWork, symptomsQuestion2, symptomsQuestion3, symptomsQuestion4));
        ArrayList<Question> symptomsPollQuestionsWithoutWork = new ArrayList<>(Arrays.asList(symptomsQuestion1WithoutWork, symptomsQuestion2, symptomsQuestion3, symptomsQuestion4));


        //Poll data preparation
        GregorianCalendar originalDate = (GregorianCalendar) Calendar.getInstance();
        GregorianCalendar dateAux = new GregorianCalendar(originalDate.get(Calendar.YEAR), originalDate.get(Calendar.MONTH), originalDate.get(Calendar.DAY_OF_MONTH));

        Poll caratPoll1 = new Poll((long) 1, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "11");
        Poll symptomsPoll1 = new Poll((long) 21, "weekly", "weekly", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), symptomsPollQuestionsWithoutWork, "21");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        Poll caratPoll2 = new Poll((long) 2, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "12");
        Poll symptomsPoll2 = new Poll((long) 22, "weekly", "weekly", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), symptomsPollQuestionsWithWork, "22");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        Poll caratPoll3 = new Poll((long) 3, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "12");
        Poll symptomsPoll3 = new Poll((long) 23, "weekly", "weekly", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), symptomsPollQuestionsWithWork, "23");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        Poll caratPoll4 = new Poll((long) 4, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "13");
        Poll symptomsPoll4 = new Poll((long) 24, "weekly", "weekly", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), symptomsPollQuestionsWithWork, "24");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        Poll caratPoll5 = new Poll((long) 5, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "14");
        Poll symptomsPoll5 = new Poll((long) 25, "weekly", "weekly", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), symptomsPollQuestionsWithWork, "25");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        Poll caratPoll6 = new Poll((long) 6, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "16");
        Poll symptomsPoll6 = new Poll((long) 26, "weekly", "weekly", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), symptomsPollQuestionsWithWork, "26");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);


        Poll caratPoll7 = new Poll((long) 7, "carat_form", "carat_form", new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), caratPoll1Questions, "17");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);


        dateAux = new GregorianCalendar(originalDate.get(Calendar.YEAR), originalDate.get(Calendar.MONTH), originalDate.get(Calendar.DAY_OF_MONTH));

        ListPoll caratListPoll1 = new ListPoll(null, caratPoll1, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "111", "");
        ListPoll symptomsListPoll1 = new ListPoll(null, symptomsPoll1, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "211", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        ListPoll caratListPoll2 = new ListPoll(null, caratPoll2, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "112", "");
        ListPoll symptomsListPoll2 = new ListPoll(null, symptomsPoll2, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "212", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        ListPoll caratListPoll3 = new ListPoll(null, caratPoll3, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "113", "");
        ListPoll symptomsListPoll3 = new ListPoll(null, symptomsPoll3, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "213", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        ListPoll caratListPoll4 = new ListPoll(null, caratPoll4, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "114", "");
        ListPoll symptomsListPoll4 = new ListPoll(null, symptomsPoll4, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "214", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        ListPoll caratListPoll5 = new ListPoll(null, caratPoll5, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "115", "");
        ListPoll symptomsListPoll5 = new ListPoll(null, symptomsPoll5, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "215", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);

        ListPoll caratListPoll6 = new ListPoll(null, caratPoll6, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "116", "");
        ListPoll symptomsListPoll6 = new ListPoll(null, symptomsPoll6, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "216", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);


        ListPoll caratListPoll7 = new ListPoll(null, caratPoll7, new GregorianCalendar(dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH)), "117", "");
        dateAux.add(Calendar.DAY_OF_MONTH, 6);


        caratListPoll = new ArrayList<>(Arrays.asList(caratListPoll1, caratListPoll2, caratListPoll3, caratListPoll4, caratListPoll5, caratListPoll6));
        caratListPollLonger = new ArrayList<>(Arrays.asList(caratListPoll1, caratListPoll2, caratListPoll3, caratListPoll4, caratListPoll5, caratListPoll6, caratListPoll7));

        symptomsListPoll = new ArrayList<>(Arrays.asList(symptomsListPoll1, symptomsListPoll2, symptomsListPoll3, symptomsListPoll4, symptomsListPoll5, symptomsListPoll6));

    }

    @Test
    public void ensureLongerCaratListIsShortened() {

        GregorianCalendar aux;
        Map<String, Integer> result = context.organizeCARATTotalAndAdherenceData(caratListPollLonger);

        LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();

        for (int i = caratListPollLonger.size() - 6; i < caratListPollLonger.size(); i++) {

            aux = caratListPollLonger.get(i).getAsweredDate();

            expected.put(aux.get(Calendar.DAY_OF_MONTH) + "/" + aux.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH), context.calculateCARATTotalScore(caratListPollLonger.get(i).getPoll()));

        }
        assertEquals(result, expected);
    }

    @Test
    public void ensureNormalSizedCaratListIsNotShortened() {

        GregorianCalendar aux;
        Map<String, Integer> result = context.organizeCARATTotalAndAdherenceData(caratListPoll);

        LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();

        for (int i = 0; i < caratListPoll.size(); i++) {

            aux = caratListPoll.get(i).getAsweredDate();

            expected.put(aux.get(Calendar.DAY_OF_MONTH) + "/" + aux.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH), context.calculateCARATTotalScore(caratListPollLonger.get(i).getPoll()));

        }
        assertEquals(result, expected);
    }


    @Test
    public void ensureShorterSymptomsListResultsInPieGraphsDataShowingOnlyCaratAndAdherenceValues(){

        int adherenceValue = 75;
        int caratValue = 24;

        Map<String,Integer> expected = new TreeMap<>();

        expected.put("Adherence", adherenceValue);
        expected.put("Carat", caratValue);
        expected.put("Leisure", -1);
        expected.put("Work",-1);
        expected.put("Health",-1);


        Map<String,Integer> result = context.organizeSymptomsData(symptomsListPoll,symptomsListPoll.size(), (BigDecimal.valueOf(adherenceValue)), caratValue);

        assertEquals(expected,result);
    }

    @Test
    public void ensureSymptomsListWithNoWorkValueResultsInPieGraphsDataWithoutWorkValue(){

        int adherenceValue = 75;
        int caratValue = 24;

        ListPoll poll = symptomsListPoll.get(0);

        Map<String,Integer> expected = new TreeMap<>();

        expected.put("Adherence", adherenceValue);
        expected.put("Carat", caratValue);
        expected.put("Leisure", ((QuestionSlider)poll.getPoll().getListQuestions().get(2)).getTotalSelected());
        expected.put("Work",-1);
        expected.put("Health",((QuestionSlider)poll.getPoll().getListQuestions().get(3)).getTotalSelected());


        Map<String,Integer> result = context.organizeSymptomsData(symptomsListPoll,0, (BigDecimal.valueOf(adherenceValue)), caratValue);

        assertEquals(expected,result);
    }

    @Test
    public void ensureSymptomsListWithAllValueResultsInPieGraphsDataWithoutAllValues(){

        int adherenceValue = 75;
        int caratValue = 24;

        ListPoll poll = symptomsListPoll.get(1);

        Map<String,Integer> expected = new TreeMap<>();

        expected.put("Adherence", adherenceValue);
        expected.put("Carat", caratValue);
        expected.put("Leisure", ((QuestionSlider)poll.getPoll().getListQuestions().get(2)).getTotalSelected());
        expected.put("Work",((QuestionSlider)poll.getPoll().getListQuestions().get(1)).getTotalSelected());
        expected.put("Health",((QuestionSlider)poll.getPoll().getListQuestions().get(3)).getTotalSelected());


        Map<String,Integer> result = context.organizeSymptomsData(symptomsListPoll,1, (BigDecimal.valueOf(adherenceValue)), caratValue);

        assertEquals(expected,result);
    }

    @Test
    public void ensureCalculateWeeklyAdherenceDateFromTodayReturnsCorrectDayDifference() {

        GregorianCalendar answerDate = new GregorianCalendar(2020,1,1);
        GregorianCalendar todayDate = new GregorianCalendar(2020,1,8);

        int result = context.calculateWeeklyAdherenceDateFromToday(answerDate,todayDate);

        assertEquals(7,result);
    }

    @Test
    public void ensureCalculateCustomIntervalAdherenceDateFromTodayReturnsCorrectDayDifferences() {

        GregorianCalendar startDate = new GregorianCalendar(2020,1,1);
        GregorianCalendar endDate = new GregorianCalendar(2020,1,8);
        GregorianCalendar todayDate = new GregorianCalendar(2020,1,13);

        ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(12,5));

        ArrayList<Integer> result = context.calculateCustomIntervalAdherenceDateFromToday(startDate, endDate, todayDate);

        assertEquals(expected,result);
    }

    @Test
    public void ensureCalculateCARATTotalScoreReturnsCorrectScore() {

        int expected = 30;
        int result = context.calculateCARATTotalScore(caratListPoll.get(0).getPoll());

        assertEquals(expected,result);
    }

}