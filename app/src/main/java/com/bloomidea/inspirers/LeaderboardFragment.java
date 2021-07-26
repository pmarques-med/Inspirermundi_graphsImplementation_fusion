package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyRegFragment;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.model.Ranking;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.util.ArrayList;


public class LeaderboardFragment extends MyRegFragment {
    private View rootView;
    private GoogleMap myGoogleMap;
    private MapView mMapView;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                myGoogleMap = googleMap;
                myGoogleMap.getUiSettings().setMapToolbarEnabled(false);

                loadMapInfo();
            }
        });

        rootView.findViewById(R.id.leader_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline(getActivity(),true, getActivity().getSupportFragmentManager())) {
                    Intent i = new Intent(getActivity(), LeaderboardActivity.class);

                    Utils.openIntent(getActivity(), i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        rootView.findViewById(R.id.people_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline(getActivity(),true, getActivity().getSupportFragmentManager())) {
                    Intent i = new Intent(getActivity(), PeopleActivity.class);

                    Utils.openIntent(getActivity(), i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        return rootView;
    }

    private void loadMapInfo(){
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(getActivity());

        APIInspirers.getMapLeaderboardInfo(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                ringProgressDialogNoText.dismiss();

                ArrayList<Ranking> rankings = InspirersJSONParser.parseListRankings(response);

                int generalTotal = 0;

                LatLng firstLocation = null;

                for(Ranking r : rankings) {
                    generalTotal+=r.getTotal();

                    if(firstLocation == null){
                        firstLocation = new LatLng(r.getLatitude(), r.getLongitude());
                    }

                    myGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(r.getLatitude(), r.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(Utils.getMarkerBitmapFromView(getActivity(), r.getTotal())))
                            .flat(true));
                }

                if(firstLocation!=null){
                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(firstLocation));
                }

                ((TextView) rootView.findViewById(R.id.my_empire_text)).setText(getResources().getString(R.string.my_empire,""+generalTotal, ""+rankings.size()));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(getActivity(),R.string.load_map_info_error,Toast.LENGTH_SHORT).show();
                Log.d("res","error");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
