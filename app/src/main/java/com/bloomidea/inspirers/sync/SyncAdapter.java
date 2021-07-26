package com.bloomidea.inspirers.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.model.MedicineInhaler;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.SyncPollCommentAux;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String pictureFolderName = "/frames";

    ContentResolver mContentResolver;
    int i = 0;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        i++;
        Log.d("AQUI","SYNC Alterado "+i);

        User activeUser = AppController.getmInstance().getActiveUser();

        if(activeUser!=null) {
            final Long userToSyncID = activeUser.getId();
            final String localDeviceId = activeUser.getDeviceId();
            final String userToSYncUID = activeUser.getUid();

            APIInspirers.requestActiveDevice(activeUser.getUid(), new JSONArrayListener() {
                @Override
                public void onResponse(JSONArray response) {
                    if(response.length()>0) {
                        try {
                            String deviceIdServer = response.getJSONObject(0).getString("active device");



                            if(deviceIdServer.equals(localDeviceId)) {
                                runSyncForUser(userToSyncID, userToSYncUID);
                            }
                        } catch (JSONException e) {
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR",error.toString());
                }
            });
        }

    }

    private void runSyncForUser(long userToSyncID,final  String userToSYncUID){
        ArrayList<InnerSyncUser> usersToSync = AppController.getmInstance().getUserDataSource().getUsersNeedSync(userToSyncID);

        if(usersToSync!=null && !usersToSync.isEmpty()){
            for (final InnerSyncUser userAux : usersToSync) {
                final String imgName = "user" + userAux.getUser().getUid() + "_" + Utils.getTodayDateToStringFileName() + ".png";

                APIInspirers.sendAvatarImgToServer(imgName, userAux.isSyncPic() ? userAux.getUser().getPicture() : null, new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String faid;
                        String faid1 = "";
                        if (response != null) {
                            try {
                                faid1 = response.getString("fid");
                            } catch (JSONException e) {
                            }
                        }

                        faid = faid1;

                        APIInspirers.editUserInfo(userAux.getUser(), faid, new JSONObjectListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String pictureUrl = null;

                                if (userAux.isSyncPic()) {
                                    pictureUrl = APIInspirers.serverPath + "/sites/default/files/" + imgName;
                                }

                                AppController.getmInstance().getUserDataSource().setUserInfoSynced(userAux.getUser().getId(), pictureUrl);

                                runOtherInfoSync(userAux.getUser().getId(), userToSYncUID);
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error Sync", error.toString());
                            }
                        });
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SyncError", error.toString());
                    }
                });
            }
        }else{
            runOtherInfoSync(userToSyncID, userToSYncUID);
        }
    }

    private void runOtherInfoSync(long userToSyncID, String userToSyncUID){
        //SYNC BADGES AND TIMELINE BADGES
        ArrayList<InnerSyncBadges> badgesToSync = AppController.getmInstance().getBadgeDataSource().getBadgesNeedSync(userToSyncID);

        for (final InnerSyncBadges badgeAux : badgesToSync) {
            APIInspirers.createBadge(badgeAux.getUid(), badgeAux.getBadge(), new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("response", response.toString());

                    AppController.getmInstance().getBadgeDataSource().setBadgeSynced(badgeAux.getBadge().getId());

                    ArrayList<TimelineItem> timelineItemNeedSyncForBadge = AppController.getmInstance().getTimelineDataSource().getTimelineItemNeedSyncForBadge(badgeAux.getBadge().getId());

                    for (final TimelineItem auxItem : timelineItemNeedSyncForBadge) {
                        APIInspirers.createTimeLineItemServer(badgeAux.getUid(), auxItem, false, new JSONObjectListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("resp", "" + response.toString());
                                try {
                                    AppController.getmInstance().getTimelineDataSource().setTimelineItemSynced(auxItem.getId(), response.getString("nid"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("error", "" + error.toString());
                            }
                        });
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("SyncErrorBadge", error.toString());
                }
            });
        }


        //SYNC POLL ANSWER AND TIMELINE
        ArrayList<InnerSyncPoll> pollSync = AppController.getmInstance().getPollDataSource().getListAnsweredPollsToSync(AppController.getmInstance().getPollModelsBD(), userToSyncID);

        Log.d("POLLS SYNC", String.valueOf(pollSync));

        for (final InnerSyncPoll auxInnerPoll : pollSync) {
            Log.d("POLLS SYNC 2", auxInnerPoll.toString());


            APIInspirers.sendMyAnswerPool(auxInnerPoll.getUid(), auxInnerPoll.getListPoll().getPoll(), new JSONArrayListener() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("responsePoll", response.toString());
                    try {
                        if (response.getJSONObject(0).getString("status").equals("saved")) {
                            String nid = response.getJSONObject(0).getString("nid");
                            AppController.getmInstance().getPollDataSource().updatePollSync(auxInnerPoll.getListPoll().getMyPollId(), nid);

                            ArrayList<TimelineItem> timelineItemNeedSyncForPoll = AppController.getmInstance().getTimelineDataSource().getTimelineItemNeedSyncForPoll(auxInnerPoll.getListPoll().getPoll().getId());

                            for (final TimelineItem auxItem : timelineItemNeedSyncForPoll) {
                                APIInspirers.createTimeLineItemServer(auxInnerPoll.getUid(), auxItem, false, new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("resp", "" + response.toString());
                                        try {
                                            AppController.getmInstance().getTimelineDataSource().setTimelineItemSynced(auxItem.getId(), response.getString("nid"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("error", "" + error.toString());
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("SyncErrorPoll2", "get value error");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("SyncErrorPoll", error.toString());
                }
            });
        }


        //SYNC CARAT POLL COMMENT
        ArrayList<SyncPollCommentAux> listComment = AppController.getmInstance().getPollDataSource().getMyPollsCommentNeedUpdate(userToSyncID);

        for (SyncPollCommentAux aux : listComment) {
            if (aux.getNid() != null && !aux.getNid().isEmpty()) {
                final long id = aux.getId();
                APIInspirers.updatePollComment(aux.getNid(), aux.getComment(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("OK", "" + response.toString());
                        AppController.getmInstance().getPollDataSource().setMyPollCommentSynced(id);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "" + error.toString());
                    }
                });
            }
        }

        //SYNC TIMELINE MEDICINE STATUS EXTRA
        syncMedicineTimeline(AppController.getmInstance().getTimelineDataSource().getTimelineItemNeedSyncWithNid(userToSyncID));

        //SYNC MEDICINE AND TIMELINE
        ArrayList<InnerSyncMedicine> listMedicine = AppController.getmInstance().getMedicineDataSource().getMedicineNeedSync(userToSyncID);
        for (InnerSyncMedicine medicine : listMedicine) {
            if (medicine.getUserMedicine().getNid() == null || medicine.getUserMedicine().getNid().isEmpty()) {
                createMedicineOnServer(medicine);
            } else {
                updateMedicineOnServer(medicine);
            }
        }

        //SYNC PICTURES
        syncPicturesFromFolder(userToSyncUID);
    }

    private void createMedicineOnServer(final InnerSyncMedicine medicine) {
        APIInspirers.createMedicineServer(AppController.getmInstance().getActiveUser().getUid(), medicine.getUserMedicine(), medicine.isDeleted(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resp",response.toString());
                try {
                    String nid = response.getString("nid");
                    medicine.getUserMedicine().setNid(nid);
                    AppController.getmInstance().getMedicineDataSource().setMedicineSynced(medicine.getUserMedicine().getId(),nid);

                    if(medicine.getUserMedicine() instanceof UserNormalMedicine) {
                        UserNormalMedicine auxMed = (UserNormalMedicine) medicine.getUserMedicine();

                        // MARK3 - CREATE SCHEDULE ON SERVER
                        for (MedicineSchedule schedule : auxMed.getSchedules()){

                            APIInspirers.createMedicineScheduleServer(nid, schedule, new JSONObjectListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("OK",response.toString());
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error", "onErrorResponse: "+error.toString());
                                }
                            });

                            for (MedicineTime medTime: schedule.getTimes()) {
                                APIInspirers.createMedicineTimeServer(nid, medTime, new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("OK",response.toString());
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("Error", "onErrorResponse: "+error.toString());
                                    }
                                });
                            }
                        }

                        for (MedicineInhaler medicineInhaler: auxMed.getInhalers()) {
                            APIInspirers.createMedicineInhalerServer(nid, medicineInhaler, new JSONObjectListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("OK_inhaler",response.toString());
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error_inhaler", "onErrorResponse: "+error.toString());
                                }
                            });
                        }
                    }

                    syncMedicineTimeline(AppController.getmInstance().getTimelineDataSource().getTimelineItemNeedSyncForMedicine(medicine.getUserMedicine().getId()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error2", "onErrorResponse2: "+error.toString());
            }
        });
    }

    private void updateMedicineOnServer(final InnerSyncMedicine medicine) {
        APIInspirers.deleteMedicineInhalerTimesOnServer(medicine.getUserMedicine().getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                APIInspirers.updateMedicineServer( medicine.getUserMedicine().getNid(), medicine.getUserMedicine(), medicine.isDeleted(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("resp",response.toString());
                        //try {
                            String nid = medicine.getUserMedicine().getNid();
                            //medicine.getUserMedicine().setNid(nid);
                            AppController.getmInstance().getMedicineDataSource().setMedicineSynced(medicine.getUserMedicine().getId(),nid);

                            if(medicine.getUserMedicine() instanceof UserNormalMedicine) {
                                UserNormalMedicine auxMed = (UserNormalMedicine) medicine.getUserMedicine();

                                // MARK2 - UPDATE ON SERVER, SCHEDULE!

                                for (MedicineSchedule schedule : auxMed.getSchedules()){

                                    APIInspirers.createMedicineScheduleServer(nid, schedule, new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("OK",response.toString());
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("Error", "onErrorResponse: "+error.toString());
                                        }
                                    });

                                    for (MedicineTime medTime: schedule.getTimes()) {
                                        APIInspirers.createMedicineTimeServer(nid, medTime, new JSONObjectListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("OK",response.toString());
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("Error", "onErrorResponse: "+error.toString());
                                            }
                                        });
                                    }
                                }

                                for (MedicineInhaler medicineInhaler: auxMed.getInhalers()) {
                                    APIInspirers.createMedicineInhalerServer(nid, medicineInhaler, new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("OK_inhaler",response.toString());
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("Error_inhaler", "onErrorResponse: "+error.toString());
                                        }
                                    });
                                }
                            }

                            syncMedicineTimeline(AppController.getmInstance().getTimelineDataSource().getTimelineItemNeedSyncForMedicine(medicine.getUserMedicine().getId()));
                        //} catch (JSONException e) {
                        //    e.printStackTrace();
                        //}
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error2", "onErrorResponse2: "+error.toString());
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error21", "onErrorResponse21: "+error.toString());
            }
        });
    }

    private void syncMedicineTimeline(ArrayList<InnerSyncTimelineItem> syncList) {
        for(final InnerSyncTimelineItem aux : syncList){
            if(aux.getTimelineItem().getNid() == null || aux.getTimelineItem().getNid().isEmpty()) {
                APIInspirers.createTimeLineItemServer(AppController.getmInstance().getActiveUser().getUid(), aux.getTimelineItem(), aux.isDeleted(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("resp", "" + response.toString());
                        try {
                            AppController.getmInstance().getTimelineDataSource().setTimelineItemSynced(aux.getTimelineItem().getId(), response.getString("nid"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "onErrorResponse: " + error.toString());
                    }
                });
            }else{
                APIInspirers.updateTimeLineItemServer(aux.getTimelineItem().getNid(), aux.getTimelineItem(), aux.isDeleted(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("resp", "" + response.toString());

                        AppController.getmInstance().getTimelineDataSource().setTimelineItemSynced(aux.getTimelineItem().getId(), aux.getTimelineItem().getNid());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error1", "onErrorResponse: " + error.toString());
                        Log.d("Error2", "onErrorResponse: " + error.getMessage());
                        Log.d("Error3", "onErrorResponse: " + error.networkResponse.data.toString());
                    }
                });
            }
        }
    }

    private void syncPicturesFromFolder(String userUID) {
        if(AppController.getmInstance().getActiveUser()!=null) {
            File picFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + pictureFolderName);

            ArrayList<File> files = Utils.getListFiles(picFolder);

            for (final File f : files) {
                if (f.getName().contains("user" + userUID)) {
                    String filenameAux = f.getName();

                    if (!f.getName().contains(".")) {
                        filenameAux += ".png";
                    }

                    final String filename = filenameAux;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);

                    if (bitmap != null) {
                        Log.d("imagem", "" + filename);

                        APIInspirers.sendAvatarImgToServer(filename, bitmap, new JSONObjectListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String fidAux = null;
                                if (response != null) {
                                    try {
                                        fidAux = response.getString("fid");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                APIInspirers.createImageOnServer(fidAux, filename, new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Ok", "Ok creating image");

                                        f.delete();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("Error", "Error creating image");
                                    }
                                });
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error", "Error sending image");
                            }
                        });
                    }
                }
            }
        }
    }


    /*
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        i++;
        Log.d("AQUI","SYNC Alterado "+i);

        ArrayList<InnerSyncUser> usersToSync = AppController.getmInstance().getUserDataSource().getUsersNeedSync();

        for (final InnerSyncUser userAux : usersToSync){
            final String imgName = "user" + userAux.getUser().getUid() + "_" + Utils.getTodayDateToStringFileName() + ".png";

            APIInspirers.sendAvatarImgToServer(imgName, userAux.isSyncPic() ? userAux.getUser().getPicture() : null, new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {

                    final String faid;
                    String faid1 = "";
                    if (response != null) {
                        try {
                            faid1 = response.getString("fid");
                        } catch (JSONException e) {
                        }
                    }

                    faid = faid1;

                    APIInspirers.editUserInfo(userAux.getUser(), faid, new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String pictureUrl = null;

                            if(userAux.isSyncPic()){
                                pictureUrl = APIInspirers.serverPath + "/sites/default/files/"+imgName;
                            }

                            AppController.getmInstance().getUserDataSource().setUserInfoSynced(userAux.getUser().getId(), pictureUrl);

                            ArrayList<InnerSyncBadges> badgesToSync = AppController.getmInstance().getBadgeDataSource().getBadgesNeedSync();

                            for(final InnerSyncBadges badgeAux : badgesToSync){
                                APIInspirers.createBadge(badgeAux.getUid(), badgeAux.getBadge(), new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("response",response.toString());

                                        AppController.getmInstance().getBadgeDataSource().setBadgeSynced(badgeAux.getBadge().getId());
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("SyncErrorBadge",error.toString());
                                    }
                                });
                            }


                            ArrayList<ListPoll> pollSync = AppController.getmInstance().getPollDataSource().getListAnsweredPollsToSync(userAux.getUser().getId(), AppController.getmInstance().getPollModelsBD());

                            for(final ListPoll auxPoll : pollSync){
                                APIInspirers.sendMyAnswerPool(userAux.getUser().getUid(), auxPoll.getPoll(), new JSONArrayListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.d("responsePoll",response.toString());
                                        try {
                                            if(response.getJSONObject(0).getString("status").equals("saved")){
                                                String nid = response.getJSONObject(0).getString("nid");
                                                AppController.getmInstance().getPollDataSource().updatePollSync(auxPoll.getMyPollId(), nid);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d("SyncErrorPoll2","get value error");
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("SyncErrorPoll",error.toString());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error Sync",error.toString());
                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("SyncError", error.toString());
                }
            });
        }

        ArrayList<SyncPollCommentAux> listComment = AppController.getmInstance().getPollDataSource().getMyPollsCommentNeedUpdate();

        for(SyncPollCommentAux aux : listComment){
            final long id = aux.getId();
            APIInspirers.updatePollComment(aux.getNid(), aux.getComment(), new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("OK",""+response.toString());
                    AppController.getmInstance().getPollDataSource().setMyPollCommentSynced(id);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR",""+error.toString());
                }
            });
        }
    }

    */
}