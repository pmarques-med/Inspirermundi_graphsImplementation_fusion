package com.bloomidea.inspirers.adapter;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.customViews.ProgressBar;
import com.bloomidea.inspirers.model.Message;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;

/**
 * Created by michellobato on 06/04/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MY_MSG = 103;
    private static final int TYPE_OTHER_MSG = 104;

    private Activity context;

    private ArrayList<Message> conversation;

    private User user;

    public MessagesAdapter(Activity context, User user) {
        this.context = context;
        this.user = user;
        this.conversation = new ArrayList<>();
    }

    public void addAllToConversationList(ArrayList<Message> results) {
        conversation.addAll(results);

        notifyDataSetChanged();
    }

    public void addNewMessage(Message newMessage) {
        conversation.add(0,newMessage);

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return conversation.get(position).isMyMsg()?TYPE_MY_MSG:TYPE_OTHER_MSG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_MY_MSG) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_message, parent, false);
            viewHolder = new ViewHolderMyMessage(view);
        } else{
            //TYPE_OTHER_MSG
            view = LayoutInflater.from(context).inflate(R.layout.item_other_message, parent, false);
            viewHolder = new ViewHolderOtherMessage(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message auxMessage = conversation.get(position);

        if(holder instanceof ViewHolderMyMessage){
            ViewHolderMyMessage auxHolder = ((ViewHolderMyMessage) holder);
            auxHolder.msg_textView.setText(auxMessage.getMsg());
        }else{
            //ViewHolderOtherMessage
            ViewHolderOtherMessage auxHolder = ((ViewHolderOtherMessage) holder);
            auxHolder.msg_textView.setText(auxMessage.getMsg());

            Utils.loadImageView(context,auxHolder.avatar_img,auxHolder.avatar_progressbar,user.getPictureUrl(),user.getPicture(),R.drawable.default_avatar,null);
        }
    }

    @Override
    public int getItemCount() {
        return conversation.size();
    }

    public static class ViewHolderMyMessage extends RecyclerView.ViewHolder {
        public TextView msg_textView;

        public ViewHolderMyMessage(View v) {
            super(v);

            msg_textView = (TextView) v.findViewById(R.id.msg_text);
        }
    }

    public static class ViewHolderOtherMessage extends RecyclerView.ViewHolder {
        public TextView msg_textView;
        public ImageView avatar_img;
        public ProgressBar avatar_progressbar;

        public ViewHolderOtherMessage(View v) {
            super(v);

            msg_textView = (TextView) v.findViewById(R.id.msg_text);
            avatar_img = (ImageView) v.findViewById(R.id.avatar_img);
            avatar_progressbar = (ProgressBar) v.findViewById(R.id.avatar_progressbar);
        }
    }
}
