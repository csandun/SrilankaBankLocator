package com.mikepenz.materialdrawer.app;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback ,View.OnClickListener{

    View v;
    GoogleMap mMap;
    DBHelper helper;
    double myLocationLat, myLocationLong;
    Marker myMarker;
    Button btSetpath;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);

        helper = new DBHelper(getActivity());

        btSetpath = (Button) v.findViewById(R.id.btSetpath);

        return v;
    }

    private void setMarkers() {
        List<Branch> allBranches = helper.getAllBranches();
        for (Branch branch:allBranches){
            LatLng branchLocaiton = new LatLng(branch.getLatitude(), branch.getLongitude());
            mMap.addMarker(new MarkerOptions().position(branchLocaiton).title(branch.getBranchName()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        setMarkers();
        getNearestBranch();
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            myLocationLat = location.getLatitude();
            myLocationLong = location.getLongitude();

            if(myMarker != null){
                myMarker.remove();
            }

            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            myMarker = mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .snippet(
                                    "Lat:" + location.getLatitude() + "Lng:"
                                            + location.getLongitude())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_man))
                            .title("My Locaiton"));
            if(mMap != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
            }
        }
    };

    private void getNearestBranch(){
        double mindistance = 1000000000;
        int branchCode = -1;

        List<Branch> allBranches = helper.getAllBranches();

        for (Branch b: allBranches){
            Log.d("Clivekumara", b.getBranchName());
            double v = distanceFrom(myLocationLat, myLocationLong, b.getLatitude(), b.getLongitude());
            Log.d("min dis",String.valueOf(myLocationLat));
            if(mindistance>v){
                mindistance =v;
                branchCode = b.getBranchCode();
                Log.d("Map",String.valueOf(branchCode));
            }
        }
        //Log.d("min dis",String.valueOf(mindistance));
        //Log.d("min dis",String.valueOf(branchCode));
    }

    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return new Double(dist * meterConversion).floatValue();    // this will return distance
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btSetpath:

                break;


        }

    }
}
