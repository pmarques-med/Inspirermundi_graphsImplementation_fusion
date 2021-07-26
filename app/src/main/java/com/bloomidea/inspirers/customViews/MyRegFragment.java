package com.bloomidea.inspirers.customViews;

import android.os.Bundle;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bloomidea.inspirers.LeaderboardFragment;
import com.bloomidea.inspirers.PeopleFragment;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.StatisticsFragment;
import com.bloomidea.inspirers.TimelineFragment;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.NavAux;
import com.bloomidea.inspirers.model.User;

import java.util.GregorianCalendar;

import static com.bloomidea.inspirers.customViews.MyActiveActivity.NAVIGATION_TYPE_SCREEN;

/**
 * Created by michellobato on 28/04/17.
 */

public class MyRegFragment extends Fragment {
    private NavAux auxNav = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User active = AppController.getmInstance().getActiveUser();

        if(active!=null) {
            if (this instanceof LeaderboardFragment) {
                auxNav = new NavAux(null, active.getId(), getString(R.string.map_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof PeopleFragment){
                auxNav = new NavAux(null, active.getId(), getString(R.string.people_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof TimelineFragment){
                auxNav = new NavAux(null, active.getId(), getString(R.string.timeline_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof StatisticsFragment){
                auxNav = new NavAux(null, active.getId(), getString(R.string.statics_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }

            if (auxNav != null) {
                AppController.getmInstance().getNavigationDataSource().createNavigation(auxNav);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragent","Destroy");
    }
}
