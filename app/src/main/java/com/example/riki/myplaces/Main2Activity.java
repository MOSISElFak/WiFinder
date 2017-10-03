package com.example.riki.myplaces;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements IThreadWakeUp {

    public static final int REQUEST_CODE_LOC = 10;

    boolean clickEnabled;
    User user;
    String ajdi;
    ImageView v,v1,v2,v3,v4,v5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        final String apiKey = intent.getExtras().getString("api");
        ajdi = apiKey;
        clickEnabled = true;
        DownloadManager.getInstance().setThreadWakeUp(this);



        if (getIntent().getBooleanExtra("EXIT", false)) {

            Intent intent1 = new Intent(Main2Activity.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        }

        if (getIntent().getBooleanExtra("remembered", true))
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(!connected){
            Snackbar snackbar = Snackbar
                    .make(constraintLayout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);

            snackbar.show();

            clickEnabled = false;

        }

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        v = (ImageView) findViewById(R.id.imageViewMap);
        v.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


                    int accessCoarseLocation = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    int accessFineLocation = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

                    List<String> listRequestPermission = new ArrayList<String>();

                    if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                        listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    }
                    if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
                        listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    }

                    if (!listRequestPermission.isEmpty()) {
                        String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
                        requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
                    }
                }
                v.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this, MapActivity.class);
                intent.putExtra("api", apiKey);
                intent.putExtra("user", user);
                startActivity(intent);

            }
        });

        v.setEnabled(false);
        v1 = (ImageView) findViewById(R.id.imageViewHelp);
        v1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v1.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this, HelpActivity.class);
                intent.putExtra("api", apiKey);
                startActivity(intent);

            }
        });

        v1.setEnabled(false);
        v2 = (ImageView) findViewById(R.id.imageViewProfile);
        v2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v2.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
                intent.putExtra("api", apiKey);
                startActivity(intent);
            }
        });
        v2.setEnabled(false);
        v3 = (ImageView) findViewById(R.id.imageViewPlay);
        v3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    int accessCoarseLocation = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    int accessFineLocation = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

                    List<String> listRequestPermission = new ArrayList<String>();

                    if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                        listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    }
                    if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
                        listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    }

                    if (!listRequestPermission.isEmpty()) {
                        String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
                        requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
                    }
                }
                v3.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this,SearchActivity.class);
                intent.putExtra("api", apiKey);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        v3.setEnabled(false);
        v4 = (ImageView) findViewById(R.id.imageViewRank);
        v4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v4.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this,RankingActivity.class);
                intent.putExtra("api",apiKey);
                intent.putExtra("id",user.id);
                intent.putExtra("points",user.points);
                intent.putExtra("name",user.name);
                startActivity(intent);

            }
        });
        v4.setEnabled(false);
       v5 = (ImageView) findViewById(R.id.imageViewFriends);
        v5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                v5.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this,Friendz.class);
                intent.putExtra("api", apiKey);
                startActivity(intent);
            }
        });
        v5.setEnabled(false);
/*
        v.setEnabled(false);
        v1.setEnabled(false);
        v2.setEnabled(false);
        v3.setEnabled(false);
        v4.setEnabled(false);
        v5.setEnabled(false);
*/


        DownloadManager.getInstance().getUser(apiKey);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!clickEnabled)
            return true;

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void ResponseOk(String s) {
        if(s.isEmpty())
        {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje
        }
        else
        {
            String html = "<!DOCTYPE html>";
            if(s.toLowerCase().contains(html.toLowerCase()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                    }
                });
            }
            else {
                try{




                    JSONObject data = new JSONObject(s);
                    String lat = data.getString("latitude");
                    String longi = data.getString("latitude");
                    double latitude1, longitude1;
                    if(lat.equals("null"))
                    {
                        latitude1= 0;
                    }
                    else
                    {
                        latitude1 =  Double.parseDouble(lat);
                    }
                    if (longi.equals("null"))
                    {
                        longitude1 = 0;
                    }
                    else
                    {
                        longitude1 = Double.parseDouble(longi);
                    }

                    user = new User(
                            data.getInt("id"),
                            data.getString("name"),
                            data.getString("first_name"),
                            data.getString("last_name"),
                            data.getString("email"),
                            data.getString("phone_number"),
                            latitude1,
                            longitude1,
                            data.getInt("points"),
                            data.getString("avatar")
                    );

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            v.setEnabled(true);
                            v1.setEnabled(true);
                            v2.setEnabled(true);
                            v3.setEnabled(true);
                            v4.setEnabled(true);
                            v5.setEnabled(true);
                        }
                    });



                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }
    }


//        final ImageView v4 = (ImageView) findViewById(R.id.imageViewRank);
//        v4.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                v4.startAnimation(animation);
//                Intent map = new Intent(Main2Activity.this, RankingActivity.class);
//                startActivity(map);
//                return true;
//            }
//        });

@Override
    public synchronized void onResume() {
        super.onResume();
       DownloadManager.getInstance().getUser(ajdi);

    }
}
