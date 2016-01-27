package com.mikepenz.materialdrawer.app;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback ,View.OnClickListener{

    View v;
    GoogleMap mMap;
    DBHelper helper;
    double myLocationLat, myLocationLong;
    double nearestLat,nearsetLong;
    Marker myMarker;
    Button btSetpath;
    int branchCode = -1;
    PolylineOptions lineOptions = null;

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
        btSetpath.setOnClickListener(this);

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


        List<Branch> allBranches = helper.getAllBranches();

        for (Branch b: allBranches){
            Log.d("Clivekumara", b.getBranchName());
            double v = distanceFrom(myLocationLat, myLocationLong, b.getLatitude(), b.getLongitude());
            Log.d("min dis",String.valueOf(myLocationLat));
            if(mindistance>v){
                mindistance =v;
                branchCode = b.getBranchCode();
                nearestLat = b.getLatitude();
                nearsetLong = b.getLongitude();

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
    public void onClick(View view) {

        Branch b = helper.getBranch(branchCode);


        switch (view.getId()){
            case R.id.btSetpath:
                getNearestBranch();
                clearMap();

                getPath(new LatLng(myLocationLat,myLocationLong),new LatLng(nearestLat,nearsetLong));

                break;


        }

    }

    private void getPath(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        //showDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, (String)null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                DirectionsJSONParser parser = new DirectionsJSONParser();
                List<List<HashMap<String, String>>> r = parser.parse(response);

                List<LatLng> points = null;

                MarkerOptions markerOptions = new MarkerOptions();

                // Traversing through all the routes
                for (int i = 0; i < r.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = r.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(2);
                    lineOptions.color(Color.RED);
                }
                if (mMap != null) {
                    // Drawing polyline in the Google Map for the i-th route

                    mMap.addPolyline(lineOptions);
                }

                //hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("APP", "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                //hideDialog();
            }
        });
        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void clearMap(){
        mMap.clear();
        setMarkers();

    }
}
