package com.example.riki.myplaces;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp, GoogleMap.OnMarkerClickListener {

    static final int ENTER_WIFI_DETAILS = 1;  // The request code

    GoogleMap map;
    Snackbar snackbar;
    LocationManager locationManager;

    private boolean selCoorsEnabled = false;
    private boolean hasPassword = false;
    private FloatingActionButton addWifi;
    private int state = 0;
    private int timer = 0;
    private HashMap<Marker, Integer> markerPlaceIdMap;
    private String apiKey;
    private int iterator;
    private Bitmap bmp;
    private TextView points;
    private User user;
    private ArrayList<WiFi> wiFis;
    private ArrayList<User> friendz;
    private boolean loadedWifis = false;
    private double selectedLatitude;
    private double selecteLongitude;

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        DownloadManager.getInstance().setThreadWakeUp(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        friendz = new ArrayList<User>();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        apiKey = intent.getExtras().getString("api");
        user = (User) intent.getSerializableExtra("user");

        points = (TextView) findViewById(R.id.points);
        points.setText("Points: " + user.points);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        if (loadedWifis) {
            map.clear();
            addWiFis();
            state = 2;
            DownloadManager.getInstance().locationFriends(apiKey, location.getLatitude(), location.getLongitude());
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
                    try {
                        JSONArray wifis = new JSONArray(s);
                        wiFis = new ArrayList<WiFi>();
                        for (int i = 0; i < wifis.length(); i++) {
                            final JSONObject wifi = wifis.getJSONObject(i);
                            final LatLng position = new LatLng(wifi.getDouble("latitude"), wifi.getDouble("longitude"));
                            Random rnd = new Random();
                            WiFi wiFi = new WiFi(
                                    wifi.getInt("id"),
                                    wifi.getString("name"),
                                    wifi.getString("password"),
                                    wifi.getDouble("latitude"),
                                    wifi.getDouble("longitude"),
                                    wifi.getInt("created_by"),
                                    wifi.getString("user")
                            );
                            wiFis.add(wiFi);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addWiFis();
                                loadedWifis = true;
                                /*state = 2;
                                DownloadManager.getInstance().getFriends(apiKey);*/
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (state == 2 || state == 3) {
                    try {
                        friendz.clear();
                        JSONArray friends = new JSONArray(s);
                        for (int i = 0; i < friends.length(); i++) {
                            final JSONObject friend = friends.getJSONObject(i);
                            String lat = friend.getString("latitude");
                            String lon = friend.getString("longitude");
                            if (!lat.equals("null") && !lon.equals("null")) {
                                User fr = new User(
                                        friend.getInt("id"),
                                        friend.getString("name"),
                                        friend.getString("first_name"),
                                        friend.getString("last_name"),
                                        friend.getString("email"),
                                        friend.getString("phone_number"),
                                        friend.getDouble("latitude"),
                                        friend.getDouble("longitude"),
                                        friend.getInt("points"),
                                        friend.getString("avatar")
                                );
                                friendz.add(fr);
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

                if (state == 4) {
                    try {
                        JSONObject wifi = new JSONObject(s);
                        user.points += 10;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                points.setText("Points: " + user.points);
                            }
                        });
                        addWifiMarker(wifi);
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

        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        addWifi = (FloatingActionButton) findViewById(R.id.addWiFi);

        addWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selCoorsEnabled = true;
                addWifi.setEnabled(false);
                addWifi.setVisibility(View.INVISIBLE);
                snackbar = Snackbar.make(constraintLayout, getString(R.string.safe_zone), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        });


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selCoorsEnabled) {
                    selecteLongitude = latLng.longitude;
                    selectedLatitude = latLng.latitude;
                    addWifi.setVisibility(View.VISIBLE);
                    addWifi.setEnabled(true);
                    selCoorsEnabled = false;
                    snackbar.dismiss();
                    Intent intent = new Intent(MapActivity.this, AddWifiActivity.class);
                    startActivityForResult(intent, ENTER_WIFI_DETAILS);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag().equals("wifi")) {
                    for (int i = 0; i < wiFis.size(); i++) {
                        if (wiFis.get(i).equals(marker.getTitle())) {
                            Intent intent = new Intent(MapActivity.this, ViewWifiActivity.class);
                            intent.putExtra("name", wiFis.get(i).name);
                            intent.putExtra("password", wiFis.get(i).password);
                            intent.putExtra("createdBy", wiFis.get(i).user);
                            startActivity(intent);
                        }
                    }
                } else {
                    Intent intent = new Intent(MapActivity.this, FriendProfileActivity.class);
                    for (int i = 0; i < friendz.size(); i++) {
                        if (friendz.get(i).equals(marker.getTitle())) {
                            intent.putExtra("api", apiKey);
                            intent.putExtra("fname", friendz.get(i).firstName);
                            intent.putExtra("lname", friendz.get(i).lastName);
                            intent.putExtra("email", friendz.get(i).email);
                            intent.putExtra("phone", friendz.get(i).phoneNumber);
                            intent.putExtra("url", friendz.get(i).avatar);
                            intent.putExtra("points", friendz.get(i).points);
                            startActivity(intent);
                        }
                    }


                }

                return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ENTER_WIFI_DETAILS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                if (data.hasExtra("password")) {
                    String password = data.getStringExtra("password");
                    state = 4;
                    hasPassword = true;
                    DownloadManager.getInstance().addWifi(apiKey, name, password, selectedLatitude, selecteLongitude);
                } else {
                    state = 4;
                    hasPassword = false;
                    DownloadManager.getInstance().addWifi(apiKey, name, null, selectedLatitude, selecteLongitude);
                }
            }
        }
    }

    private void addWifiMarker(JSONObject wifi) {
        try {
            String lat = wifi.getString("latitude");
            String lon = wifi.getString("longitude");
            String name = wifi.getString("name");
            WiFi wiFi = null;
            if(hasPassword){
                wiFi = new WiFi(
                        wifi.getInt("id"),
                        wifi.getString("name"),
                        wifi.getString("password"),
                        wifi.getDouble("latitude"),
                        wifi.getDouble("longitude"),
                        wifi.getInt("created_by"),
                        wifi.getString("user")
                );
            } else {
                wiFi = new WiFi(
                        wifi.getInt("id"),
                        wifi.getString("name"),
                        "null",
                        wifi.getDouble("latitude"),
                        wifi.getDouble("longitude"),
                        wifi.getInt("created_by"),
                        wifi.getString("user")
                );
            }

            wiFis.add(wiFi);

            final LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            final Random rnd = new Random();
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            Drawable wifiPin = getResources().getDrawable(R.drawable.wifi_pin);
            Bitmap bmp = ((BitmapDrawable) wifiPin).getBitmap();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 75, 100, false)));
            markerOptions.title(name);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    map.addCircle(new CircleOptions()
                            .center(loc)
                            .radius(50)
                            .strokeWidth(0f)
                            .fillColor(Color.argb(120, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))));
                    Marker marker = map.addMarker(markerOptions);
                    Toast.makeText(MapActivity.this, "Nice! You got 10 points for adding a new Wifi spot!", Toast.LENGTH_SHORT).show();
                    marker.setTag("wifi");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMarker(JSONObject friend, Bitmap bmp) {

        try {
            String lat = friend.getString("latitude");
            String lon = friend.getString("longitude");
            String name = friend.getString("name");

            //Float distanceFromMarker = distanceBetween((float)myNewLat,(float)myNewLon,(float)marker.getPosition().latitude, (float)marker.getPosition().longitude);

            if (!lat.equals("null") && !lon.equals("null")) {
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
                        marker.setTag("friend");
                        /*markerPlaceIdMap.put(marker, iterator);
                        iterator++;*/
                    }
                });
            }

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
                    .fillColor(Color.argb(120, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))));
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(position);
            Drawable wifiPin = getResources().getDrawable(R.drawable.wifi_pin);
            Bitmap bmp = ((BitmapDrawable) wifiPin).getBitmap();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 75, 100, false)));
            markerOptions.title(wiFis.get(i).name);
            Marker marker = map.addMarker(markerOptions);
            marker.setTag("wifi");
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
}
