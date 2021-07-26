package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.MessagesAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.events.ResetMessageCounter;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.model.Message;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends MyActiveActivity {
    public static final String EXTRA_USER = "EXTRA_USER";
    private User user;
    private String myUid;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private boolean hasMore = true;
    private boolean loading = false;

    private int page = 0;
    private boolean firstScrollEnd = false;

    private Point size;

    private EditText msgTextBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        myUid = AppController.getmInstance().getActiveUser().getUid();

        msgTextBox = (EditText) findViewById(R.id.send_msg_editText);

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(Html.fromHtml(user.getUserName()));

        Utils.loadImageView(this,((CircleImageView) findViewById(R.id.user_picture_imageView)),null,user.getPictureUrl(),user.getPicture(),R.drawable.default_avatar,null);

        recyclerView = (RecyclerView) findViewById(R.id.messages_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        messagesAdapter = new MessagesAdapter(this,user);

        recyclerView.setLayoutManager(linearLayoutManager);

        int remove = getResources().getDimensionPixelSize(R.dimen.main_top_bar_h) + getResources().getDimensionPixelSize(R.dimen.main_menu_h);
        int spaceTop = (size.y)-remove-150;

        //recyclerView.addItemDecoration(new SpacesItemDecoration(spaceTop, 0));
        recyclerView.setAdapter(messagesAdapter);
        //recyclerView.scrollToPosition(messagesAdapter.getItemCount()-1);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!loading && firstScrollEnd) {
                    Log.d("POS",""+linearLayoutManager.findLastVisibleItemPosition());
                    if (linearLayoutManager.findLastVisibleItemPosition() == (messagesAdapter.getItemCount()-1)) {
                        Log.d("LoadMore", "Scroll");
                        loadMessages(true);
                    }
                }
            }

        });

        loadMessages(false);

        msgTextBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(msgTextBox.getText().toString());
                    handled = true;

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(msgTextBox.getWindowToken(), 0);
                }
                return handled;
            }
        });


        findViewById(R.id.send_msg_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount()-1);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(msgTextBox.getWindowToken(), 0);

                sendMessage(msgTextBox.getText().toString());
            }
        });

        markMessagesRead();
    }

    private void sendMessage(final String msgText){
        if(!msgText.isEmpty()) {
            if(Utils.isOnline(this,true,getSupportFragmentManager())) {
                final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

                APIInspirers.sendMessage(myUid, user.getUid(), msgText, new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ringProgressDialogNoText.dismiss();

                        messagesAdapter.addNewMessage(new Message(true, msgText, new GregorianCalendar()));
                        recyclerView.smoothScrollToPosition(0);

                        msgTextBox.setText("");
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialogNoText.dismiss();
                        Toast.makeText(MessagesActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void loadMessages(boolean loadMore) {
        if(!loading && hasMore) {
            loading = true;

            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

            if(loadMore){
                page+=1;
            }

            APIInspirers.getConversation(page, user.getUid(), myUid, new JSONArrayListener() {
                @Override
                public void onResponse(JSONArray response) {
                    loading = false;
                    ringProgressDialogNoText.dismiss();

                    ArrayList<Message> results = InspirersJSONParser.parseListMessages(myUid, response);

                    hasMore = !results.isEmpty() && results.size() == APIInspirers.resultMessagesLoad;

                    messagesAdapter.addAllToConversationList(results);

                    if(page == 0) {
                        recyclerView.scrollToPosition(0);
                        firstScrollEnd = true;
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    loading = false;
                    ringProgressDialogNoText.dismiss();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public boolean isSameUser(String uid) {
        return user.getUid().equals(uid);
    }

    public void addMessageRecived(String msg) {
        if(messagesAdapter!=null){
            messagesAdapter.addNewMessage(new Message(false,msg,new GregorianCalendar()));
            recyclerView.smoothScrollToPosition(0);

            markMessagesRead();
        }
    }

    private void markMessagesRead(){
        AppController.getmInstance().getMyBus().send(new ResetMessageCounter(user.getUid()));


        APIInspirers.markAllRead(myUid, user.getUid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("markAllRead","ok");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("markAllRead","error");
            }
        });
    }
}
