package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.PeopleAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyRegFragment;
import com.bloomidea.inspirers.customViews.SimpleDividerItemDecoration;
import com.bloomidea.inspirers.events.GodfatherAskedEvent;
import com.bloomidea.inspirers.events.NumGodchildsUpdated;
import com.bloomidea.inspirers.events.ResetMessageCounter;
import com.bloomidea.inspirers.events.ReviewCreatedEvent;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.Godchild;
import com.bloomidea.inspirers.model.Request;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.BadgesAux;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import rx.functions.Action1;


public class PeopleFragment extends MyRegFragment {
    private View rootView;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SimpleDividerItemDecoration itemDeco;
    private PeopleAdapter peopleAdapter;

    private String myUid;
    private ArrayList<Request> requestToMe;
    private ArrayList<Request> requestsByMe;
    private ArrayList<User> suggested;
    private User godfather;
    private ArrayList<Godchild> godchilds;

    private boolean needReloadInfo = false;

    public PeopleFragment() {
    }

    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null) {
            myUid = AppController.getmInstance().getActiveUser().getUid();

            rootView = inflater.inflate(R.layout.fragment_people, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.people_recyclerView);
            linearLayoutManager = new LinearLayoutManager(getActivity());

            peopleAdapter = new PeopleAdapter(getActivity(), new PeopleAdapter.PeopleAdapterListener() {
                @Override
                public void shareWithDoctorClicked() {
                    Intent i = new Intent(getActivity(), InviteDoctorActivity.class);

                    Utils.openIntent(getActivity(), i, R.anim.slide_in_left, R.anim.slide_out_left);
                }

                @Override
                public void searchClicked(String textToSearch) {
                    if(requestsByMe.size() < 1){
                        if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                            Intent i = new Intent(getActivity(), SearchActivity.class);
                            i.putExtra(SearchActivity.EXTRA_SEARCH_TEXT,textToSearch);

                            Utils.openIntent(getActivity(), i, R.anim.slide_in_left, R.anim.slide_out_left);
                        }
                    }else{
                        Utils.showMessageAlertDialog(getActivity(),null,getString(R.string.alert_request_full),null);
                        //Toast.makeText(getActivity(),R.string.alert_request_full,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void openMessages(User userProfile) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        Intent i = new Intent(getActivity(), MessagesActivity.class);
                        i.putExtra(MessagesActivity.EXTRA_USER, userProfile);

                        Utils.openIntent(getActivity(), i, R.anim.slide_in_left, R.anim.slide_out_left);

                        //peopleAdapter.resetCounter(userProfile.getUid());
                    }
                }

                @Override
                public void deleteGodchildBtnClick(Godchild godchild, int positionOnListItems) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        deleteGodchild(godchild, positionOnListItems);
                    }
                }

                @Override
                public void deleteGodfatherBtnClick(User godfather, int positionOnListItems) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        deleteGodfatherQuestion(godfather, positionOnListItems);
                    }
                }

                @Override
                public void sendBuzzBtnClick(Godchild godchild, int positionOnListItems) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        sendBuzzToUser(godchild, positionOnListItems);
                    }
                }

                @Override
                public void askBtnClick(User user, int positionOnListItems) {
                    if(requestsByMe.size() < 1){
                        if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                            askForAux(user, positionOnListItems);
                        }
                    }else{
                        Utils.showMessageAlertDialog(getActivity(),null,getString(R.string.alert_request_full),null);
                        //Toast.makeText(getActivity(),R.string.alert_request_full,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void acceptBtnClick(Request request, int positionOnListItems) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        acceptGodfather(request, positionOnListItems);
                    }
                }

                @Override
                public void denyBtnClick(Request request, int positionOnListItems) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        denyGodfather(request, positionOnListItems);
                    }
                }

                @Override
                public void deleteBtnClick(Request request, int positionOnListItems) {
                    if(Utils.isOnline(getActivity(),true,getActivity().getSupportFragmentManager())) {
                        deleteGodfatherRequest(request, positionOnListItems);
                    }
                }
            });

            loadInfo();

            AppController.getmInstance().getMyBus().toObserverable()
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object event) {

                            if(event instanceof GodfatherAskedEvent) {
                                //GodfatherAskedEvent askedEvent = (GodfatherAskedEvent) event;
                                needReloadInfo = true;
//                                loadInfo();
                            }if(event instanceof ReviewCreatedEvent) {
                                ReviewCreatedEvent eventAux = (ReviewCreatedEvent) event;

                                if(peopleAdapter != null){
                                    peopleAdapter.updateReviewsInfo(eventAux.getUid(), eventAux.getNewReview());
                                }
                            }if(event instanceof ResetMessageCounter){
                                ResetMessageCounter eventAux = (ResetMessageCounter) event;

                                if(peopleAdapter != null){
                                    peopleAdapter.resetCounter(eventAux.getUid());
                                }
                            }else if(event instanceof NumGodchildsUpdated){
                                needReloadInfo = true;
                            }
                        }
                    });
        }

        return rootView;
    }

    private void deleteGodchild(final Godchild godchild, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());

        APIInspirers.deleteGodchild(godchild.getNid(), godchild.getRevisionId(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();

                AppController.getmInstance().removeGodchild();

                peopleAdapter.godchilDeleted(godchild,positionOnListItems);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(), R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendBuzzToUser(Godchild godchild, final int positionOnListItems) {
        GregorianCalendar buzzDate = godchild.getDateLastBuzz();
        boolean canBuzz = true;

        if(buzzDate!=null){
            GregorianCalendar dateNewBuzz = new GregorianCalendar();
            dateNewBuzz.setTimeInMillis(buzzDate.getTimeInMillis());
            dateNewBuzz.add(Calendar.DAY_OF_MONTH,1);

            canBuzz = (new GregorianCalendar()).after(dateNewBuzz);
        }

        if(canBuzz) {
            int actualBuzz = godchild.getUserBuzz();
            final int newUserBuzz;

            if ((actualBuzz + 1) > 2) {
                newUserBuzz = 2;
            } else {
                newUserBuzz = actualBuzz + 1;
            }

            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());
            final GregorianCalendar buzzdate = new GregorianCalendar();

            APIInspirers.updateGodchildBuzz("" + newUserBuzz, godchild.getNid(), godchild.getRevisionId(), buzzdate, new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {
                    ringProgressDialogNoText.dismiss();

                    Utils.createNavigationAction(getString(R.string.send_buzz));

                    peopleAdapter.buzzSent(positionOnListItems, newUserBuzz, buzzdate);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ringProgressDialogNoText.dismiss();
                    Toast.makeText(getActivity(), R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(), R.string.last_buzz_less_24_hours, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteGodfatherQuestion(final User godfather, final int positionOnListItems) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteGodfather(godfather,positionOnListItems);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.cancel();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_godfather_question).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void deleteGodfather(User godfather, int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());
        APIInspirers.deleteGodfather(myUid, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();

                loadInfo();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(), R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptGodfather(Request request, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());
        APIInspirers.acceptGodfatherRequest(request.getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();

                loadInfo();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(), R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void denyGodfather(Request request, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());
        APIInspirers.declineGodfatherRequest(request.getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();
                peopleAdapter.godfatherDenied(positionOnListItems);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(), R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteGodfatherRequest(Request request, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());
        APIInspirers.deleteGodfatherRequest(request.getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();
                peopleAdapter.godfatherDeleted(positionOnListItems);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(), R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void askForAux(User user, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());

        APIInspirers.askGodfather(user.getUid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();

                Utils.createNavigationAction(getString(R.string.send_request_inspirer));

                peopleAdapter.godfatherAsked(response.optString("nid"),Request.STATUS_WAITING,Request.TYPE_GODCHILD,positionOnListItems);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(),R.string.problem_send_to_server,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadInfo() {
        clearAllInfo();

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());

        APIInspirers.getGodfather(myUid, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                godfather = InspirersJSONParser.parseGodfather(response);

                APIInspirers.getGodchilds(myUid, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        godchilds = InspirersJSONParser.parseListGodchilds(response);

                        AppController.getmInstance().updateUserTotalGodchilds(godchilds.size(), getActivity().getSupportFragmentManager(), new OkErrorListener() {
                            @Override
                            public void ok() {
                                Badge win = BadgesAux.verifyWinBagdeSeven(godchilds,AppController.getmInstance().getActiveUser().getUserBadges());

                                if(win!=null){
                                    UserBadge newUserBadge = AppController.getmInstance().createNewUserBadge(win);

                                    if(newUserBadge!=null){
                                        Utils.showWinBadge(getActivity().getSupportFragmentManager(), newUserBadge.getBadge(), null);
                                    }
                                }
                            }

                            @Override
                            public void error() {
                            }
                        });

                        if(godfather!=null){
                            requestToMe = new ArrayList<>();
                            requestsByMe = new ArrayList<>();
                            suggested = new ArrayList<>();


                            loadingEndInitList();
                            ringProgressDialogNoText.dismiss();
                        }else{
                            loadInfoNoGodphater(ringProgressDialogNoText);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tretaError(ringProgressDialogNoText);
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                tretaError(ringProgressDialogNoText);
            }
        });
    }

    private void loadingEndInitList() {
        peopleAdapter.initInfo(godfather==null,requestToMe,requestsByMe,suggested,godfather,godchilds);

        ArrayList<Integer> positionNoLine = new ArrayList<>();
        positionNoLine.add(new Integer(0));

        if(godfather == null){
            positionNoLine.add(new Integer(1));
        }

        recyclerView.setLayoutManager(linearLayoutManager);

        if(itemDeco!=null) {
            recyclerView.removeItemDecoration(itemDeco);
        }
        itemDeco = new SimpleDividerItemDecoration(getActivity(),positionNoLine);
        recyclerView.addItemDecoration(itemDeco);
        recyclerView.setAdapter(peopleAdapter);
    }

    private void tretaError(ProgressDialog ringProgressDialogNoText) {
        ringProgressDialogNoText.dismiss();
        Toast.makeText(getActivity(),R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
    }

    private void loadInfoNoGodphater(final ProgressDialog ringProgressDialogNoText) {
        APIInspirers.getRequestedGodfathers(myUid, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                requestsByMe = InspirersJSONParser.parseListRequest(response);

                APIInspirers.getRequestedToMeGodfathers(myUid, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        requestToMe = InspirersJSONParser.parseListRequest(response);

                        APIInspirers.getSuggestedGodfathers(myUid, new JSONArrayListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                suggested = InspirersJSONParser.parseListUsers(response);

                                ringProgressDialogNoText.dismiss();
                                loadingEndInitList();
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tretaError(ringProgressDialogNoText);
                            }
                        });
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tretaError(ringProgressDialogNoText);
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                tretaError(ringProgressDialogNoText);
            }
        });
    }

    private void clearAllInfo() {
        this.requestToMe = new ArrayList<>();
        this.requestsByMe = new ArrayList<>();
        this.suggested = new ArrayList<>();
        this.godfather = null;
        this.godchilds = new ArrayList<>();

        peopleAdapter.clearAll();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void notificationMessage(String uid) {
        if(peopleAdapter!=null){
            peopleAdapter.addMessageCounter(uid,1);
        }
    }

    public void resetMessages(String uid) {
        if(peopleAdapter!=null){
            peopleAdapter.resetCounter(uid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(needReloadInfo){
            needReloadInfo = false;
            loadInfo();
        }
    }
}
