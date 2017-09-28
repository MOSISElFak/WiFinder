package com.example.riki.myplaces;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp {

    GoogleMap map;
    Snackbar snackbar;
    LocationManager locationManager;

    private boolean selCoorsEnabled = false;
    private FloatingActionButton addWifi;
    private int state = 0;
    private int timer = 0;
    private HashMap<Marker, Integer> markerPlaceIdMap;
    private String apiKey;
    private int safeZone;
    private int iterator;
    private Bitmap bmp;
    private ArrayList<WiFi> wiFis;
    private boolean loadedWifis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        DownloadManager.getInstance().setThreadWakeUp(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        apiKey = intent.getExtras().getString("api");
        safeZone = intent.getExtras().getInt("safe_zone");

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

        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        if (loadedWifis) {
            map.clear();
            addWiFis();
            state = 2;
            DownloadManager.getInstance().addLocation(location.getLatitude(), location.getLongitude(), apiKey);
        }

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
                if (state == 1) {
                    try{
                        JSONArray wifis = new JSONArray(s);
                        wiFis = new ArrayList<WiFi>();
                        for(int i = 0; i < wifis.length(); i++){
                            final JSONObject wifi = wifis.getJSONObject(i);
                            final LatLng position = new LatLng(wifi.getDouble("latitude"), wifi.getDouble("longitude"));
                            Random rnd = new Random();
                            WiFi wiFi = new WiFi(
                                    wifi.getInt("id"),
                                    wifi.getString("name"),
                                    wifi.getString("password"),
                                    wifi.getDouble("latitude"),
                                    wifi.getDouble("longitude"),
                                    wifi.getInt("created_by")
                            );
                            wiFis.add(wiFi);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addWiFis();
                                loadedWifis = true;
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (state == 2) {
                    state = 3;
                    DownloadManager.getInstance().getFriends(apiKey);
                }

                if(state == 3){
                    try {
                        JSONArray friends = new JSONArray(s);
                        for (int i = 0; i < friends.length(); i++) {
                            final JSONObject friend = friends.getJSONObject(i);
                            String lat = friend.getString("latitude");
                            String lon = friend.getString("longitude");
                            if (lat != null || lon != null) {
                                Bitmap bmp;
                                if (!friend.getString("avatar").equals("default.jpg")) {
                                    URL url = new URL("https://wi-finder-server.herokuapp.com/" + friend.getString("avatar"));
                                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    addMarker(friend, Bitmap.createScaledBitmap(bmp, 80, 80, false));
                                } else {
                                    bmp = null;
                                    addMarker(friend, bmp);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        addWifi = (FloatingActionButton) findViewById(R.id.addWiFi);
        if (safeZone != 0) {
            addWifi.setVisibility(View.INVISIBLE);
        } else {
            addWifi.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selCoorsEnabled = true;
                    addWifi.setEnabled(false);
                    addWifi.setVisibility(View.INVISIBLE);
                    snackbar = Snackbar.make(coordinatorLayout, getString(R.string.safe_zone), Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            });
        }


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selCoorsEnabled) {
                    double lon = latLng.longitude;
                    double lat = latLng.latitude;
                    //addWifi.setVisibility(View.VISIBLE);
                    selCoorsEnabled = false;
                    snackbar.dismiss();
                    state = 3;
                    /*DownloadManager.getInstance().createSafeZone(apiKey, lat, lon);*/
                }
            }
        });

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

        state = 1;
        DownloadManager.getInstance().getUserWifis(apiKey);
    }

    private void addMarker(JSONObject friend, Bitmap bmp) {

        try {
            String lat = friend.getString("latitude");
            String lon = friend.getString("longitude");
            String name = friend.getString("name");

            //Float distanceFromMarker = distanceBetween((float)myNewLat,(float)myNewLon,(float)marker.getPosition().latitude, (float)marker.getPosition().longitude);

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
                    markerPlaceIdMap.put(marker, iterator);
                    iterator++;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean checkCurrentTimeOfDay() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
        return currentHour < 18 && currentHour > 6;
    }

    public void addWiFis() {
        for (int i = 0; i < wiFis.size(); i++) {
            LatLng position = new LatLng(wiFis.get(i).latitude, wiFis.get(i).longitude);
            Random rnd = new Random();
            map.addCircle(new CircleOptions()
                    .center(position)
                    .radius(50)
                    .strokeWidth(0f)
                    .fillColor(rnd.hashCode()));
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(position);
            Drawable wifiPin = getResources().getDrawable(R.drawable.wifi_pin);
            Bitmap bmp = ((BitmapDrawable) wifiPin).getBitmap();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 80, 80, false)));
            markerOptions.title(wiFis.get(i).name);
            map.addMarker(markerOptions);
        }
    }


}
