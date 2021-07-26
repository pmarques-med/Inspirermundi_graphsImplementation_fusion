package com.bloomidea.inspirers.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 16/03/17.
 */

public class User implements Serializable{
    private Long id;
    private String uid;
    private String token;
    private GregorianCalendar startDate;
    private String sessionName;
    private String sessionId;
    private boolean pushOn;
    private int userPoints;
    private String pictureUrl;
    private Bitmap picture;
    private String password;
    private int numGodChilds;
    private boolean localNotifications;
    private String userName;
    private BigDecimal mediaAvl;
    private int level;
    private String isoCountry;
    private boolean isActive;
    private String[] hobbies;
    private String[] languages;
    private boolean firstLogin;
    private String email;
    private String countryName;
    private String countryFlagUrl;
    private Bitmap countryFlag;
    private int totalRating;
    private BigDecimal statsWeek;
    private BigDecimal statsMonth;
    private BigDecimal statsEver;
    private BigDecimal actualBonus;

    private boolean connected;
    private String nidProfile;

    private String notificationsToken;

    private ArrayList<UserBadge> userBadges;

    private int timeCARAT;
    private Boolean weekPollAnswer;

    private String userProvidedId;

    private String deviceId;
    private GregorianCalendar userProvidedIdDate;

    private boolean acceptedTerms;

    private int unreadMessages;

    private boolean termsOff;

    public User(Long id, String uid, String token, GregorianCalendar startDate, String sessionName, String sessionId, boolean pushOn, int userPoints,
                String pictureUrl, Bitmap picture, String password, int numGodChilds, boolean localNotifications, String userName, BigDecimal mediaAvl,
                int level, String isoCountry, boolean isActive, String[] hobbies, String[] languages, boolean firstLogin, String email, String countryName,
                String countryFlagUrl, Bitmap countryFlag, int totalRating, BigDecimal statsWeek, BigDecimal statsMonth, BigDecimal statsEver, BigDecimal actualBonus,
                ArrayList userBadges, boolean connected, String nidProfile,String notificationsToken, int timeCARAT, Boolean weekPollAnswer, String userProvidedId, String deviceId,
                GregorianCalendar userProvidedIdDate, boolean acceptedTerms, int unreadMessages, boolean termsOff) {
        this.id = id;
        this.uid = uid;
        this.token = token;
        this.startDate = startDate;
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        this.pushOn = pushOn;
        this.userPoints = userPoints;
        this.pictureUrl = pictureUrl;
        this.picture = picture;
        this.password = password;
        this.numGodChilds = numGodChilds;
        this.localNotifications = localNotifications;
        this.userName = userName;
        this.mediaAvl = mediaAvl;
        this.level = level;
        this.isoCountry = isoCountry;
        this.isActive = isActive;
        this.hobbies = hobbies;
        this.languages = languages;
        this.firstLogin = firstLogin;
        this.email = email;
        this.countryName = countryName;
        this.countryFlagUrl = countryFlagUrl;
        this.countryFlag = countryFlag;
        this.totalRating = totalRating;
        this.statsWeek = statsWeek;
        this.statsMonth = statsMonth;
        this.statsEver = statsEver;
        this.actualBonus = actualBonus;
        this.userBadges = userBadges;
        this.connected = connected;
        this.nidProfile = nidProfile;
        this.notificationsToken = notificationsToken;
        this.timeCARAT = timeCARAT;
        this.weekPollAnswer = weekPollAnswer;
        this.userProvidedId = userProvidedId;
        this.deviceId = deviceId;
        this.userProvidedIdDate = userProvidedIdDate;
        this.acceptedTerms = acceptedTerms;
        this.unreadMessages = unreadMessages;
        this.termsOff = termsOff;
    }

    public User(String uid, String token, GregorianCalendar startDate, String sessionName, String sessionId, boolean pushOn, int userPoints,
                String pictureUrl, Bitmap picture, String password, int numGodChilds, boolean localNotifications, String userName,
                BigDecimal mediaAvl, int level, String isoCountry, boolean isActive, String[] hobbies, String[] languages,
                boolean firstLogin, String email, String countryName, String countryFlagUrl, Bitmap countryFlag, int totalRating,
                BigDecimal statsWeek, BigDecimal statsMonth, BigDecimal statsEver, BigDecimal actualBonus, ArrayList userBadges,
                boolean connected, String nidProfile, String notificationsToken, int timeCARAT, Boolean weekPollAnswer, String userProvidedId, String deviceId,
                GregorianCalendar userProvidedIdDate, boolean acceptedTerms, int unreadMessages, boolean termsOff) {
        this.id = null;
        this.uid = uid;
        this.token = token;
        this.startDate = startDate;
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        this.pushOn = pushOn;
        this.userPoints = userPoints;
        this.pictureUrl = pictureUrl;
        this.picture = picture;
        this.password = password;
        this.numGodChilds = numGodChilds;
        this.localNotifications = localNotifications;
        this.userName = userName;
        this.mediaAvl = mediaAvl;
        this.level = level;
        this.isoCountry = isoCountry;
        this.isActive = isActive;
        this.hobbies = hobbies;
        this.languages = languages;
        this.firstLogin = firstLogin;
        this.email = email;
        this.countryName = countryName;
        this.countryFlagUrl = countryFlagUrl;
        this.countryFlag = countryFlag;
        this.totalRating = totalRating;
        this.statsWeek = statsWeek;
        this.statsMonth = statsMonth;
        this.statsEver = statsEver;
        this.actualBonus = actualBonus;
        this.userBadges = userBadges;
        this.connected = connected;
        this.nidProfile = nidProfile;
        this.notificationsToken = notificationsToken;
        this.timeCARAT = timeCARAT;
        this.weekPollAnswer = weekPollAnswer;
        this.userProvidedId = userProvidedId;
        this.deviceId = deviceId;
        this.userProvidedIdDate = userProvidedIdDate;
        this.acceptedTerms = acceptedTerms;
        this.unreadMessages = unreadMessages;
        this.termsOff = termsOff;
    }

    public boolean isTermsOff() {
        return termsOff;
    }

    public void setTermsOff(boolean termsOff) {
        this.termsOff = termsOff;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isPushOn() {
        return pushOn;
    }

    public void setPushOn(boolean pushOn) {
        this.pushOn = pushOn;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumGodChilds() {
        return numGodChilds;
    }

    public void setNumGodChilds(int numGodChilds) {
        this.numGodChilds = numGodChilds;
    }

    public boolean isLocalNotifications() {
        return localNotifications;
    }

    public void setLocalNotifications(boolean localNotifications) {
        this.localNotifications = localNotifications;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getMediaAvlTreated5Stars() {
        return mediaAvl.multiply(new BigDecimal(5)).divide(new BigDecimal(100),1, RoundingMode.HALF_UP);
    }

    public void setMediaAvl(BigDecimal mediaAvl) {
        this.mediaAvl = mediaAvl;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getIsoCountry() {
        return isoCountry;
    }

    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryFlagUrl() {
        return countryFlagUrl;
    }

    public void setCountryFlagUrl(String countryFlagUrl) {
        this.countryFlagUrl = countryFlagUrl;
    }

    public Bitmap getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(Bitmap countryFlag) {
        this.countryFlag = countryFlag;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public BigDecimal getStatsWeek() {
        return statsWeek;
    }

    public void setStatsWeek(BigDecimal statsWeek) {
        this.statsWeek = statsWeek;
    }

    public BigDecimal getStatsMonth() {
        return statsMonth;
    }

    public void setStatsMonth(BigDecimal statsMonth) {
        this.statsMonth = statsMonth;
    }

    public BigDecimal getStatsEver() {
        return statsEver;
    }

    public void setStatsEver(BigDecimal statsEver) {
        this.statsEver = statsEver;
    }

    public BigDecimal getActualBonus() {
        return actualBonus;
    }

    public void setActualBonus(BigDecimal actualBonus) {
        this.actualBonus = actualBonus;
    }

    public String getHobbiesStringWithSeparator(String separator){
        String h = "";

        if(hobbies!=null && hobbies.length!=0){
            for(int i=0;i<hobbies.length;i++){
                h+=separator+hobbies[i];
            }

            h = h.substring(1);
        }

        return h;
    }

    public String getLanguagesStringWithSeparator(String separator){
        String l = "";

        if(languages!=null && languages.length!=0){
            for(int i=0;i<languages.length;i++){
                l+=separator+languages[i];
            }

            l = l.substring(1);
        }

        return l;
    }



    public ArrayList<String> getLanguagesAsArrayList() {
        ArrayList<String> aux = new ArrayList();

        if(languages != null){
            for(int i = 0 ; i<languages.length; i++){
                aux.add(languages[i]);
            }
        }

        return aux;
    }

    public ArrayList<String> getHobbiesAsArrayList() {
        ArrayList<String> aux = new ArrayList();

        if(hobbies != null){
            for(int i = 0 ; i<hobbies.length; i++){
                aux.add(hobbies[i]);
            }
        }

        return aux;
    }

    public String getToken() {
        return token;
    }

    public String getStatsWeekColor(){
        return getColorForVal(statsWeek);
    }

    public String getStatsMonthColor(){
        return getColorForVal(statsMonth);
    }

    public String getStatsEverColor(){
        return getColorForVal(statsEver);
    }

    private String getColorForVal(BigDecimal value){
        String color = "#66cc99"; //green

        if(value.compareTo(new BigDecimal(33)) <= 0){
            color = "#ff3333"; //red
        }else if(value.compareTo(new BigDecimal(66)) <=0){
            color = "#ffcc00"; // yellow
        }

        return color;
    }

    public ArrayList<UserBadge> getUserBadges() {
        return userBadges;
    }

    public void setUserBadges(ArrayList<UserBadge> userBadges) {
        this.userBadges = userBadges;
    }

    public void addBadge(UserBadge newUserBadge) {
        if(userBadges==null){
            userBadges = new ArrayList<>();
        }

        userBadges.add(newUserBadge);
    }

    public boolean isConnected() {
        return connected;
    }

    public void updateReviewsInfo(Review newReview) {
        totalRating += 1;

        BigDecimal review = newReview.getReview();

        mediaAvl = mediaAvl.add(review).divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMediaAvl() {
        return mediaAvl;
    }

    public void acceptedNewWarrior() {
        numGodChilds += 1;
    }

    public String getNidProfile() {
        return nidProfile;
    }

    public String getNotificationsToken() {
        return notificationsToken;
    }

    public void setNotificationsToken(String notificationsToken) {
        this.notificationsToken = notificationsToken;
    }

    public int getTimeCARAT() {
        return timeCARAT;
    }

    public void setTimeCARAT(int timeCARAT) {
        this.timeCARAT = timeCARAT;
    }

    public void setReviewInfo(int total, BigDecimal avr) {
        this.totalRating = total;
        this.mediaAvl = new BigDecimal(avr.doubleValue());
    }

    public Boolean getWeekPollAnswer() {
        return weekPollAnswer;
    }

    public void setWeekPollAnswer(Boolean weekPollAnswer) {
        this.weekPollAnswer = weekPollAnswer;
    }

    public String getUserProvidedId() {
        return userProvidedId;
    }

    public void setUserProvidedId(String userProvidedId,GregorianCalendar userProvidedIdDate) {
        this.userProvidedId = userProvidedId;
        this.userProvidedIdDate = userProvidedIdDate;
    }

    public GregorianCalendar getUserProvidedIdDate() {
        return userProvidedIdDate;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}
