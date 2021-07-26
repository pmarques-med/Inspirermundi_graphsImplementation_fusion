package com.bloomidea.inspirers.utils;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;

import java.util.List;

/**
 * Created by michellobato on 27/09/17.
 */

public class ListPollsLoader extends AsyncTaskLoader<List<ListPoll>> {
    private int totalToLoad;
    private int page;
    private Poll model;

    public ListPollsLoader(Context context, int totalToLoad, int page, Poll model) {
        super(context);
        this.totalToLoad = totalToLoad;
        this.page = page;
        this.model = model;
    }

    @Override
    public List<ListPoll> loadInBackground() {
        return AppController.getmInstance().getPollDataSource().getListAnsweredPolls(AppController.getmInstance().getActiveUser().getId(),model, totalToLoad, page);
    }
}