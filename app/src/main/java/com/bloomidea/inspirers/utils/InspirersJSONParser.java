package com.bloomidea.inspirers.utils;

import android.app.Activity;
import android.util.Log;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.Country;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.FaseTime;
import com.bloomidea.inspirers.model.Godchild;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.MedicineInhaler;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.Message;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;
import com.bloomidea.inspirers.model.Ranking;
import com.bloomidea.inspirers.model.Request;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.model.ScheduleAux;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserMedicineAuxSync;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.model.UserSOSMedicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by michellobato on 16/03/17.
 */

public class InspirersJSONParser {
    public final static String LANGUAGE_SEPARATOR = ";";
    public final static String HOBBIES_SEPARATOR = ";";
    /*public static User parseUser(JSONObject aux) throws JSONException {
        JSONObject user = aux.getJSONObject("user");

        String pictureUrl = "";
        try {
            JSONObject auxObject = user.getJSONObject("field_picture");
            pictureUrl = APIInspirers.serverPath+"/sites/default/files/"+auxObject.getJSONArray("und").getJSONObject(0).getString("filename");
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        int points = 0;
        try {
            JSONObject auxObject = user.getJSONObject("field_points");
            points = auxObject.getJSONArray("und").getJSONObject(0).getInt("value");
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        int numGodChilds = 0;
        try {
            JSONObject auxObject = user.getJSONObject("field_godchild");
            numGodChilds = auxObject.getJSONArray("und").length();
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        String isoCountry = "";
        try {
            JSONObject auxObject = user.getJSONObject("field_country");
            isoCountry = auxObject.getJSONArray("und").getJSONObject(0).getString("iso2");;
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        String[] hobbies = null;
        try {
            JSONObject auxObject = user.getJSONObject("field_interests");
            hobbies = auxObject.getJSONArray("und").getJSONObject(0).getString("value").split(",");
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        String[] languages = null;
        try {
            JSONObject auxObject = user.getJSONObject("field_languages");
            languages = auxObject.getJSONArray("und").getJSONObject(0).getString("value").split(",");
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        BigDecimal statsWeek = BigDecimal.ZERO;
        try {
            JSONObject auxObject = user.getJSONObject("field_accessionweek");
            statsWeek = new BigDecimal(auxObject.getJSONArray("und").getJSONObject(0).getDouble("value"));
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        BigDecimal statsMonth = BigDecimal.ZERO;
        try {
            JSONObject auxObject = user.getJSONObject("field_accessionmonth");
            statsMonth = new BigDecimal(auxObject.getJSONArray("und").getJSONObject(0).getDouble("value"));
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        BigDecimal statsEver = BigDecimal.ZERO;
        try {
            JSONObject auxObject = user.getJSONObject("field_accessionever");
            statsEver = new BigDecimal(auxObject.getJSONArray("und").getJSONObject(0).getDouble("value"));
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }

        BigDecimal actualBonus = BigDecimal.ONE;
        try {
            JSONObject auxObject = user.getJSONObject("field_currentbonus");
            actualBonus = new BigDecimal(auxObject.getJSONArray("und").getJSONObject(0).getDouble("value"));
        }catch (Exception e){
            Log.d("parseUser",e.toString());
        }


//        String tidLocationAux = "";
//        try {
//            JSONObject auxObject = user.getJSONObject("field_localizacao");
//            tidLocationAux = auxObject.getJSONArray("und").getJSONObject(0).getString("tid");
//        }catch (JSONException e){
//            Log.d("parseUser",e.toString());
//        }
//
//        GregorianCalendar birthDateAux = null;
//        try {
//            JSONObject auxObject = user.getJSONObject("field_data_de_nascimento");
//            String stringBirthDateAux = auxObject.getJSONArray("und").getJSONObject(0).getString("value");
//            birthDateAux = new GregorianCalendar();
//            birthDateAux.setTime(birthDateDateFormat.parse(stringBirthDateAux));
//        }catch (JSONException e){
//            Log.d("parseUser",e.toString());
//        } catch (ParseException e) {
//            Log.d("parseDate", e.toString());
//            birthDateAux = null;
//        }
//
//        int newMessages = 0;
//
//        try {
//            JSONObject auxObject = user.getJSONObject("field_not_mensagens");
//            newMessages = auxObject.getJSONArray("und").length();
//        }catch (JSONException e){
//            Log.d("parseUser", e.toString());
//        }


        return new User(user.getString("uid"),
                "",
                null,//GregorianCalendar startDate,
                aux.getString("session_name"),
                aux.getString("sessid"),
                true,//boolean pushOn,
                points,
                pictureUrl,
                null,
                "",
                numGodChilds,
                true,//boolean localNotifications,
                user.getJSONObject("field_name_user").getJSONArray("und").getJSONObject(0).getString("value"),
                java.math.BigDecimal.ZERO,//BigDecimal mediaAvl,
                user.getJSONObject("field_level").getJSONArray("und").getJSONObject(0).getInt("value"),
                isoCountry,
                true,
                hobbies,
                languages,
                true,//boolean firstLogin,
                user.getString("mail"),
                "",//String countryName,
                "",//String countryFlagUrl,
                null,//Bitmap countryFlag,
                0,// int totalRating,
                statsWeek,
                statsMonth,
                statsEver,
                actualBonus,
                new ArrayList<UserBadge>(),
                false,
                "",// por nidProfiledireito
                "",
                -28);
    }*/

    public static HashMap<String, Country> parseListCountry(JSONArray response) {
        HashMap<String, Country> listCountries = new HashMap<String, Country>();

        try {
            JSONObject aux;

            Country auxC = null;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                auxC = parseCountry(aux);

                listCountries.put(auxC.getIso(),auxC);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listCountries;
    }

    private static Country parseCountry(JSONObject aux) throws JSONException {
        return new Country(aux.getString("country id"),
                aux.getString("name"),
                aux.getString("flagurl:"),
                aux.getString("latitude"),
                aux.getString("longitude"),
                aux.getString("iso2"));
    }

    public static ArrayList<UserBadge> parseListUserBadges(JSONArray response) {
        ArrayList<UserBadge> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseUserBadge(aux));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static UserBadge parseUserBadge(JSONObject aux)  throws JSONException{
        long auxDate = aux.getLong("date");
        GregorianCalendar data = new GregorianCalendar();
        data.setTimeInMillis(auxDate*1000);

        Badge auxBadge = AppController.getmInstance().getBadge(aux.getString("achievement"));

        String weekNumberAux = Utils.getWeekNumber(data);//aux.getString("week_number")

        return new UserBadge(data, weekNumberAux,auxBadge);
    }

    public static ArrayList<Ranking> parseListRankings(JSONArray response) {
        ArrayList<Ranking> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseRanking(aux));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Ranking parseRanking(JSONObject aux)  throws JSONException{
        return new Ranking(aux.getJSONObject("users").getInt("total"),
                Double.parseDouble(aux.isNull("latitude")?"0":aux.getString("latitude")),
                Double.parseDouble(aux.isNull("latitude")?"0":aux.getString("longitude")));
    }

    public static ArrayList<User> parseListUsers(JSONArray response) {
        ArrayList<User> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseUser2(aux,false,null,null,false));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static User parseUser2(JSONObject user, boolean connectedDefault, String sessionName, String sessionId, boolean firstLogin) throws JSONException{
        Log.d("USER_INFO", user.toString());
        int points = 0;
        try {
            points = user.getInt("points");
        }catch (Exception e){
            Log.d("parseUser1",e.toString());
        }

        String pictureUrl = "";
        try {
            pictureUrl = user.getString("picture");
        }catch (Exception e){
            Log.d("parseUser2",e.toString());
        }

        int numGodChilds = 0;
        try {
            String auxString = user.getString("godchild total");
            auxString = auxString.replaceAll("\n","");
            auxString = auxString.replaceAll(" ","");

            numGodChilds = Integer.parseInt(auxString);
        }catch (Exception e){
            Log.d("parseUser3",e.toString());
        }

        String[] hobbies = null;
        try {
            hobbies = !user.getString("interests").equalsIgnoreCase("null")?user.getString("interests").split(HOBBIES_SEPARATOR):null;
        }catch (Exception e){
            Log.d("parseUser4",e.toString());
        }

        String[] languages = null;
        try {
            languages = !user.getString("languages").equalsIgnoreCase("null")?user.getString("languages").split(LANGUAGE_SEPARATOR):null;
        }catch (Exception e){
            Log.d("parseUser5",e.toString());
        }

        BigDecimal mediaAval = new BigDecimal(0);
        try{
            mediaAval = new BigDecimal(Double.parseDouble(user.getString("media")));
        }catch (Exception e){
            Log.d("parseUser6",e.toString());
        }

        BigDecimal statsWeek = BigDecimal.ZERO;
        try {
            statsWeek = new BigDecimal(user.getDouble("accessionweek"));
        }catch (Exception e){
            Log.d("parseUser7",e.toString());
        }

        BigDecimal statsMonth = BigDecimal.ZERO;
        try {
            statsMonth = new BigDecimal(user.getDouble("accessionmonth"));
        }catch (Exception e){
            Log.d("parseUser8",e.toString());
        }

        BigDecimal statsEver = BigDecimal.ZERO;
        try {
            statsEver = new BigDecimal(user.getDouble("accessionever"));
        }catch (Exception e){
            Log.d("parseUser9",e.toString());
        }

        BigDecimal actualBonus = BigDecimal.ONE;
        try {
            actualBonus = new BigDecimal(user.getDouble("currentbonus"));
        }catch (Exception e){
            Log.d("parseUser10",e.toString());
        }

        ArrayList<UserBadge> userBadges = new ArrayList<UserBadge>();

        try {
            String json = user.getString("achievements").replace("&quot;","\"");

            JSONArray obj = new JSONArray(json);

            for(int i=0;i<obj.length();i++){
                userBadges.add(parseUserBadge(obj.getJSONObject(i)));
            }

            Log.d("My App", obj.toString());

        } catch (Exception e) {
            Log.d("parseUser11",e.toString());
        }

        GregorianCalendar auxDateUserProvidedId = null;

        if(user.optLong("rest_data_study",-1)!=-1) {
            auxDateUserProvidedId = new GregorianCalendar();
            auxDateUserProvidedId.setTimeInMillis(user.getLong("rest_data_study") * 1000);
        }

        return new User(user.getString("uid"),
                "",
                null,//GregorianCalendar startDate,
                sessionName,
                sessionId,
                true,//boolean pushOn,
                points,
                pictureUrl,
                null,
                "",
                numGodChilds,
                true,//boolean localNotifications,
                user.getString("name"),
                mediaAval,//BigDecimal mediaAvl,
                user.getInt("level"),
                "",
                true,
                hobbies,
                languages,
                firstLogin,//boolean firstLogin,
                user.optString("e-mail"),
                user.isNull("country")?"":user.getString("country"),//String countryName,
                user.getString("flagurl"),//String countryFlagUrl,
                null,//Bitmap countryFlag,
                user.optInt("reviews total",0),
                statsWeek,
                statsMonth,
                statsEver,
                actualBonus,
                userBadges,
                user.optInt("connection",connectedDefault?1:0) == 1,
                user.optString("nid profile",""),
                "",
                -28,
                null,
                user.isNull("custom user id")?"":user.getString("custom user id"),
                "",
                auxDateUserProvidedId,
                user.optString("rest_terms","0").equals("1"),
                user.optInt("unread_messages",0),
                user.optString("rest_terms_off","0").equals("1"));
    }

    public static ArrayList<Review> parseListReviews(JSONArray response) {
        ArrayList<Review> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseReview(aux));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Review parseReview(JSONObject aux)  throws JSONException{
        GregorianCalendar auxDate = new GregorianCalendar();
        auxDate.setTimeInMillis(aux.getLong("post date")*1000);

        return new Review(null,
                aux.getString("id"),
                aux.getString("picture"),
                aux.getString("name").trim(),
                aux.getString("author uid"),
                aux.getString("comment").trim(),
                new BigDecimal(aux.getDouble("evaluation")),
                auxDate);
    }

    public static ArrayList<Request> parseListRequest(JSONArray response) {
        ArrayList<Request> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseRequest(aux));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Request parseRequest(JSONObject aux)  throws JSONException{
        GregorianCalendar auxDate = null;
        long auxT = aux.optLong("created",0);

        if(auxT > 0) {
            auxDate = new GregorianCalendar();
            auxDate.setTimeInMillis(aux.getLong("created") * 1000);
        }

        return new Request(aux.getString("nid"),
                aux.getString("request type"),
                aux.getString("status"),
                parseUser2(aux,false,null,null,false),
                auxDate);
    }

    public static User parseGodfather(JSONArray response) {
        User aux = null;

        if(response.length()>0){
            try {
                aux = parseUser2(response.getJSONObject(0),true,null,null,false);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("PARSE ERROR","parseFirstUser");
            }
        }

        return aux;
    }

    public static ArrayList<Godchild> parseListGodchilds(JSONArray response) {
        ArrayList<Godchild> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseGodchild(aux));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Godchild parseGodchild(JSONObject aux)  throws JSONException{
        GregorianCalendar auxDate = null;
        long auxT = aux.optLong("created",0);

        if(auxT > 0) {
            auxDate = new GregorianCalendar();
            auxDate.setTimeInMillis(auxT * 1000);
        }

        GregorianCalendar auxDateLastBuzz = null;
        auxT = aux.optLong("last buzz date",0);

        if(auxT > 0) {
            auxDateLastBuzz = new GregorianCalendar();
            auxDateLastBuzz.setTimeInMillis(auxT * 1000);
        }

        return new Godchild(aux.getString("field collection item id"),
                "",//request type
                aux.getString("status"),
                parseUser2(aux,true,null,null,false),
                auxDate,
                aux.optInt("state buzz",0),
                aux.getString("revision_id"),
                auxDateLastBuzz);
    }

    public static ArrayList<Message> parseListMessages(String myUid, JSONArray response) {
        ArrayList<Message> list = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                list.add(parseMessage(myUid, aux));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Message parseMessage(String myUid, JSONObject aux)  throws JSONException{
        GregorianCalendar auxDate = null;
        long auxT = aux.optLong("post date",0);

        if(auxT > 0) {
            auxDate = new GregorianCalendar();
            auxDate.setTimeInMillis(auxT * 1000);
        }

        String msgUid = aux.getString("author uid");

        return new Message(msgUid.equals(myUid),
                aux.getString("comment"),
                auxDate);
    }

    public static TreeMap<String, Poll> parseListPolls(JSONObject response) {
        TreeMap<String, Poll> list = new TreeMap<>();

        try {
            JSONArray auxFormsList = response.getJSONArray("forms_list");

            JSONObject aux;
            Poll auxPool;

            for(int i = 0; i < auxFormsList.length(); i++){
                aux = auxFormsList.getJSONObject(i);

                auxPool = parsePoll(aux);

                list.put(auxPool.getPoolType(), auxPool);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Poll parsePoll(JSONObject aux)  throws JSONException{
        GregorianCalendar auxDate = null;
        long auxT = aux.optLong("update_date",0);

        if(auxT > 0) {
            auxDate = new GregorianCalendar();
            auxDate.setTimeInMillis(auxT * 1000);
        }

        JSONArray answersList = aux.getJSONArray("answers_list");

        ArrayList<Question> auxQuestion = new ArrayList<>();

        for(int i=0; i < answersList.length(); i++){
            auxQuestion.add(parseQuestion(answersList.getJSONObject(i)));
        }

        return new Poll(null,
                aux.getString("form_type"),
                aux.getString("form_type"),
                auxDate,
                auxQuestion,
                aux.getString("form_nid"));
    }

    private static Question parseQuestion(JSONObject aux) throws JSONException{
        Question questionAux = null;

        String questionType = aux.has("type")?aux.getString("type"):Question.QUESTION_TYPE_MULTIPLE_CHOICE;
        String questionId = aux.has("questionid")?aux.getString("questionid"):aux.getString("fcid");
        String question = aux.getString("question");

        if(questionType.equals(Question.QUESTION_TYPE_MULTIPLE_CHOICE)){
            ArrayList<Answer> listAnswers = new ArrayList<>();
            JSONArray auxAnswers = aux.getJSONArray("answers");

            JSONObject ans;
            for(int i = 0; i< auxAnswers.length(); i++){
                ans = auxAnswers.getJSONObject(i);
                listAnswers.add(new Answer(null,
                        ans.getString("answerid"),
                        ans.getString("answer"),
                        false));
            }


            questionAux = new QuestionMultipleChoice(null, questionId, question, questionType, listAnswers);
        }else if(questionType.equals(Question.QUESTION_TYPE_SLIDER)){
            questionAux = new QuestionSlider(null, questionId, question, questionType,0,false);
        }else{
            //Question.QUESTION_TYPE_YES_NO
            questionAux = new QuestionYesNo(null, questionId, question, questionType, null);
        }

        return questionAux;
    }

    public static ArrayList<ListPoll> parseListPollAnswered(JSONArray response, Poll pollModel) {
        ArrayList<ListPoll> auxList = new ArrayList<>();

        JSONObject auxPollA;
        for(int i = 0; i < response.length(); i++){
            try {
                auxPollA = response.getJSONObject(i);
                Poll auxPool = new Poll(pollModel);

                JSONArray resp = auxPollA.getJSONArray("answers_list");
                for(int j = 0; j < resp.length(); j++){
                    String idQ = resp.getJSONObject(j).getString("questionid");
                    String ansId = resp.getJSONObject(j).getString("question");

                    for(Question q : auxPool.getListQuestions()){
                        if(q.getNid().equals(idQ)){
                            if(q instanceof QuestionMultipleChoice){
                                    for(Answer a : ((QuestionMultipleChoice) q).getListAnswers()){
                                        if(a.getNid().equals(ansId)){
                                            a.setSelected(true);
                                            break;
                                        }
                                    }

                            }else{
                                if(q instanceof QuestionYesNo){
                                    ((QuestionYesNo) q).setYes(ansId.equalsIgnoreCase("true"));
                                    break;
                                }else if(q instanceof QuestionSlider){
                                    ((QuestionSlider) q).setTotalSelected(Integer.parseInt(ansId),false);
                                    break;
                                }
                            }
                        }
                    }
                }

                GregorianCalendar dateA = new GregorianCalendar();
                dateA.setTimeInMillis(auxPollA.getLong("date")*1000);

                String comment = auxPollA.optString("field_comments","");

                auxList.add(new ListPoll(null, auxPool,dateA,auxPollA.getString("nid"), comment));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return auxList;
    }

    public static HashMap<String, UserMedicineAuxSync> parseListMedicine(Activity activity, JSONArray response) {
        HashMap<String, UserMedicineAuxSync> auxList = new HashMap<>();

        try {
            JSONObject aux;
            UserMedicineAuxSync auxMed;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                auxMed = parseUserMedicine(activity,aux);
                auxList.put(auxMed.getUserMedicine().getNid(),auxMed);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auxList;
    }

    private static UserMedicineAuxSync parseUserMedicine(Activity activity, JSONObject aux) throws JSONException {
        boolean isSOS = aux.getString("is sos").equals("1");
        UserMedicine auxMedicine;
        MedicineType medicineType = MedicineTypeAux.getMedicineType(aux.getString("type"), activity);

        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(aux.getLong("start date")*1000);

        String nid = aux.getString("nid");

        String inhalers = aux.getString("inhalers");

        ArrayList<MedicineInhaler> inhalersList = new ArrayList<MedicineInhaler>();

        try {
            String json = aux.getString("inhalers").replace("&quot;","\"");

            JSONArray obj = new JSONArray(json);
            JSONObject auxObj;
            for(int i=0;i<obj.length();i++){
                auxObj = obj.getJSONObject(i);
                inhalersList.add(new MedicineInhaler(auxObj.getString("field_med_active").equals("1"),
                        auxObj.getString("field_med_barcode"),
                        auxObj.getInt("field_med_dosage")));
            }

            Log.d("My App", obj.toString());

        } catch (Exception e) {
            Log.d("parseUser11",e.toString());
        }

        String medicineName = aux.getString("node_title");
        boolean shareWithDoctor = aux.getString("share").equals("1");
        String notes = aux.getString("notes");

        boolean deleted = aux.getString("published").equals("0");

        if(isSOS){
            auxMedicine = new UserSOSMedicine(medicineType,
                    medicineName,
                    shareWithDoctor,
                    startDate,
                    notes,
                    inhalersList,
                    aux.getInt("sos dosage"),
                    aux.getInt("severety"),
                    aux.getString("trigger"),
                    HealthServicesAux.getHealthService(aux.getString("health service"),activity),
                    aux.getInt("sos dosage"),
                    nid);
        }else{

            //MARK1 - CREATE SCHEDULE FROM SERVICES

            ArrayList<MedicineSchedule> listSchedule = new ArrayList<>();

            ArrayList<MedicineTime> listTimes = new ArrayList<>();

            try {
                String json = aux.getString("medicine times").replace("&quot;","\"");

                JSONArray obj = new JSONArray(json);
                JSONObject auxObj;
                for(int i=0;i<obj.length();i++){
                    auxObj = obj.getJSONObject(i);
                    listTimes.add(new MedicineTime(new FaseTime(auxObj.getString("field_med_day_fase")),auxObj.getInt("field_med_dosage")));
                }

                Log.d("My App", obj.toString());

            } catch (Exception e) {
                Log.d("parseUser11",e.toString());
            }

            if (!aux.getString("schedule").equals("[]")){
                try {
                    String json = aux.getString("schedule").replace("&quot;","\"");

                    JSONArray obj = new JSONArray(json);
                    JSONObject auxObj;
                    for(int i=0;i<obj.length();i++){
                        auxObj = obj.getJSONObject(i);

                        Integer code = 0;
                        String codeString = "";
                        if (auxObj.getInt("field_sche_code") == 0){
                            if (listTimes.size() <= 12 && listTimes.size() > 0 && auxObj.getInt("field_sche_interval") == 0){
                                code = listTimes.size();
                                codeString = ScheduleAux.getDescCode(code);
                            }
                        }else{
                            code = auxObj.getInt("field_sche_code");
                            codeString = ScheduleAux.getDescCode(code);
                        }

                        ScheduleAux scheAux = new ScheduleAux(code, codeString, auxObj.getInt("field_sche_interval"));

                        Integer intervalDays = auxObj.getInt("field_sche_days_interval");
                        ArrayList<Days> auxDays = null;
                        Integer optionDay = auxObj.getInt("field_sche_days_option");

                        if (!auxObj.getString("field_sche_days_selected").equals("")){
                            ArrayList<String> arraydays = new ArrayList<String>(Arrays.asList(auxObj.getString("field_sche_days_selected").split(",")));

                            ArrayList<Days> days = new ArrayList<Days>();
                            days.add(new Days(Days.SUNDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_sunday)));
                            days.add(new Days(Days.MONDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_monday)));
                            days.add(new Days(Days.TUESDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_tuesday)));
                            days.add(new Days(Days.WEDNESDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_wednesday)));
                            days.add(new Days(Days.THURSDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_thursday)));
                            days.add(new Days(Days.FRIDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_friday)));
                            days.add(new Days(Days.SATURDAY_OPTION, false, AppController.getmInstance().getString(R.string.day_saturday)));

                            for (Days auxDay : days){
                                for (String dayCode : arraydays){
                                    if (auxDay.getCode() == Integer.parseInt(dayCode)){
                                        auxDay.setSelected(true);
                                    }
                                }
                            }
                            auxDays = days;
                        }

                        MedicineDays auxMedDays = new MedicineDays(optionDay, auxDays, intervalDays);

                        listSchedule.add(new MedicineSchedule(scheAux,
                                listTimes,
                                auxMedDays));
                    }

                    Log.d("My App", obj.toString());

                } catch (Exception e) {
                    Log.d("parseUser11",e.toString());
                }
            }else{
                listSchedule.add(new MedicineSchedule(new ScheduleAux(1, ScheduleAux.getDescCode(1), 0),
                        listTimes,
                        new MedicineDays(MedicineDays.ALL_DAYS_OPTION, null, 0)));
            }

            auxMedicine = new UserNormalMedicine(medicineType,
                    medicineName,
                    shareWithDoctor,
                    startDate,
                    notes,
                    inhalersList,
                    aux.getInt("sos dosage"),
                    aux.getInt("duration"),
                    listSchedule,
                    nid);
        }

        return new UserMedicineAuxSync(auxMedicine,deleted);
    }


    public static ArrayList<TimelineItem> parseListTimeline(Activity activity,
                                                            HashMap<String, UserMedicineAuxSync> auxMedicines,
                                                            ArrayList<UserBadge> badges,
                                                            TreeMap<String,Poll> polls,  JSONArray response) {
        ArrayList<TimelineItem> auxList = new ArrayList<>();

        try {
            JSONObject aux;

            for(int i = 0; i < response.length(); i++){
                aux = response.getJSONObject(i);
                String entityNid = aux.getString("entity");
                String entityType = aux.getString("entity type");
                if (entityType.equals("medicine")) {
                    if (auxMedicines.containsKey(entityNid)) {
                        auxList.add(parseTimelineItem(activity,auxMedicines,badges,polls,aux));
                    }
                }else {
                    auxList.add(parseTimelineItem(activity, auxMedicines, badges, polls, aux));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auxList;
    }

    private static TimelineItem parseTimelineItem(Activity activity, HashMap<String, UserMedicineAuxSync> auxMedicines,
                                                  ArrayList<UserBadge> badges,
                                                  TreeMap<String,Poll> polls, JSONObject aux) throws JSONException {
        String entityType = aux.getString("entity type");
        String entityNid = aux.getString("entity");
        String badgeEntity = aux.isNull("badge entity")?null:aux.getString("badge entity");

        UserMedicine medicine = null;
        UserBadge badge = null;
        Poll poll = null;

        if(badgeEntity != null){
            for(UserBadge userBadge : badges){
                if(userBadge.getBadge().getCode().equals(badgeEntity)){
                    badge = userBadge;
                    break;
                }
            }
        }else {
            if (entityType.equals("medicine")) {
                if (auxMedicines.containsKey(entityNid)){
                    medicine = auxMedicines.get(entityNid).getUserMedicine();
                }
            } else if (entityType.equals("carat") || entityType.equals("symptoms")) {
                for (Poll p : polls.values()) {
                    if (p.getNid().equals(entityNid)) {
                        poll = p;
                        break;
                    }
                }
            }
        }

        GregorianCalendar date = new GregorianCalendar();
        date.setTimeInMillis(aux.getLong("date")*1000);
        date.set(Calendar.HOUR_OF_DAY,0);
        date.set(Calendar.MINUTE,0);
        date.set(Calendar.SECOND,0);
        date.set(Calendar.MILLISECOND,0);

        GregorianCalendar startTime = new GregorianCalendar();
        startTime.setTimeInMillis(aux.getLong("start time")*1000);

        GregorianCalendar endTime = new GregorianCalendar();
        endTime.setTimeInMillis(aux.getLong("end time")*1000);

        GregorianCalendar dateTaken = null;

        if(!aux.isNull("taken")) {
            dateTaken = new GregorianCalendar();
            dateTaken.setTimeInMillis(aux.getLong("taken") * 1000);
        }

        return new TimelineItem(null,
                medicine,
                badge,
                date,
                startTime,
                endTime,
                aux.getInt("time points"),
                aux.getString("week number"),
                aux.getString("is sos").equals("1"),
                aux.getString("note"),
                aux.isNull("state")?null:aux.getString("state"),
                dateTaken,
                aux.getInt("points won"),
                aux.getInt("dosage"),
                aux.isNull("fase time code")?null:new FaseTime(aux.getString("fase time code")),
                poll,
                aux.getInt("recognition failed times"),
                aux.getString("nid"),
                aux.getString("main time").equals("1"),
                aux.isNull("rest_latitude")?"":aux.getString("rest_latitude"),
                aux.isNull("rest_longitude")?"":aux.getString("rest_longitude"));
    }
}
