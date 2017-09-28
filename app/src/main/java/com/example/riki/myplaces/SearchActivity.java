package com.example.riki.myplaces;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp {

    LocationManager locationManager;
    GoogleMap map;
    Snackbar snackbar;
    private ConstraintLayout constraintLayout;
    private double currentLat;
    private double currentLon;
    private boolean snackbarShown;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        apiKey = intent.getExtras().getString("api");

        DownloadManager.getInstance().setThreadWakeUp(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        ImageView wifiIcon = (ImageView) findViewById(R.id.wifi_icon);

        if (mWifi.isConnected()) {
            wifiIcon.setImageResource(R.drawable.wifi);
        } else {
            wifiIcon.setImageResource(R.drawable.no_wifi);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        map.clear();

        if (snackbarShown) {
            snackbar.dismiss();
            snackbarShown = false;
        }

        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

        LatLng position = new LatLng(currentLat, currentLon);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void ResponseOk(String s) {
        if (s.isEmpty()) {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje
        } else {
            String html = "<!DOCTYPE html>";
            if (s.toLowerCase().contains(html.toLowerCase())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                    }
                });
            } else {
                try {
                    JSONArray friends = new JSONArray(s);
                    for (int i = 0; i < friends.length(); i++) {
                        final JSONObject friend = friends.getJSONObject(i);
                        String lat = friend.getString("latitude");
                        String lon = friend.getString("longitude");
                        if(lat != null || lon != null){
                            Bitmap bmp;
                            if (!friend.getString("avatar").equals("default.jpg")) {
                                URL url = new URL("https://wi-finder-server.herokuapp.com/" + friend.getString("avatar"));
                                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                addMarker(friend, Bitmap.createScaledBitmap(bmp, 80, 80, false));
                            } else{
                                bmp = null;
                                addMarker(friend, bmp);
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (!checkCurrentTimeOfDay()) {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
            map.setMapStyle(style);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1
            );
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            snackbar = Snackbar.make(constraintLayout, getString(R.string.turn_on_location), Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            snackbarShown = true;
        }

        DownloadManager.getInstance().getFriends(apiKey);

    }

    private boolean checkCurrentTimeOfDay() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
        return currentHour < 18 && currentHour > 6;
    }

    public static float distanceBetween(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private void addMarker(JSONObject friend, Bitmap bmp) {

        try {

            String lat = friend.getString("latitude");
            String lon = friend.getString("longitude");
            String name = friend.getString("name");

            LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            if (friend.getString("avatar").equals("default.jpg")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.def));
            } else
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
            markerOptions.title(name);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    Marker marker = map.addMarker(markerOptions);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
