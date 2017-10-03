package com.example.riki.myplaces;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Friendz extends AppCompatActivity implements IThreadWakeUp {

    //stari friends
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String apiKey;
    String idUser;
    boolean ok = false;
    boolean notOk = true;
    boolean alreadyFriend = false;
    int brojPrijatelja;
    int niz[];
    private static final int BT_DISCOVERABLE_TIME = 240;
    public static final int REQUEST_CODE_LOC = 10;

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendz);

        listItems.add("Friends");

        final Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");

        DownloadManager.getInstance().setThreadWakeUp(this);


        ListView friends = (ListView) findViewById(R.id.listViewFriends);
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {


                String val = (String) listItems.get(position);
                val.substring(0, val.indexOf(' ')); // "72"
                val = val.replaceAll("\\D+", "");
                ok = true;

                DownloadManager.getInstance().getAnyUser(apiKey, val);


                //TODO: Create a new profile activity that is non-editable, for opening profiles of user's friends
                //Or better yet, make a pop-up window with this information
            }
        });

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        friends.setAdapter(adapter);
        niz = new int[100];
        DownloadManager.getInstance().getFriends(apiKey);
        DownloadManager.getInstance().getUser(apiKey);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        final ImageView exit = (ImageView) findViewById(R.id.cancelButton);
        exit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                exit.startAnimation(animation);
                finish();

            }
        });

        final ImageView refresh = (ImageView) findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.startAnimation(animation);
                finish();
                startActivity(getIntent());
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddNewFriend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


                int accessCoarseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                int accessFineLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                List<String> listRequestPermission = new ArrayList<String>();

                if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                    listRequestPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
                    listRequestPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if (!listRequestPermission.isEmpty()) {
                    String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
                    requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
                }
                }

                  //  Snackbar.make(view, "Wait for incoming friend request or send one.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                        Toast.makeText(Friendz.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                        //finish();
                        return;
                    }
                ensureDiscoverable(bluetoothAdapter);
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOC:
                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        // Check if request is granted or not
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }

                    //TODO - Add your code here to start Discovery

                }
                break;
            default:
                return;
        }
    }

    private void addNewFriend() {
        Log.d(TAG, "Friends addNewFriend started");
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Intent serverIntent = new Intent(Friendz.this, DeviceListActivity1.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        };
        Thread btThread = new Thread(r);
        btThread.start();
    }

    //----------------------------------------------------------------------------------------------------------------------------------

    private final static String TAG = "BT";
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private String connectedDeviceName = null;

    private StringBuffer outStringBuffer;
    private BluetoothAdapter bluetoothAdapter = null;
    private ChatService chatService = null;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "MainActivity: handleMessage started");
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatService.STATE_CONNECTED:
                            Log.d(TAG, "MainActivity: handleMessage MESSAGE_STATE_CHANGE STATE_CONNECTED");     //for new devices
                           // setStatus(getString(R.string.title_connected_to, connectedDeviceName));
                            //chatArrayAdapter.clear();
                            sendFriendRequest();
                            break;
                        case ChatService.STATE_CONNECTING:
                            Log.d(TAG, "MainActivity: handleMessage MESSAGE_STATE_CHANGE STATE_CONNECTING");    //for paired devices??
                          //  setStatus(R.string.title_connecting);
                            sendFriendRequest();
                            break;
                        case ChatService.STATE_LISTEN:
                        case ChatService.STATE_NONE:
                            Log.d(TAG, "MainActivity: handleMessage MESSAGE_STATE_CHANGE STATE_NONE");
//                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    Log.d(TAG, "MainActivity: handleMessage MESSAGE_WRITE");
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    Log.d(TAG, "MainActivity: handleMessage MESSAGE_READ");
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);

                    
                    Log.d(TAG, "readMessage:" + readMessage);


                    final String message = readMessage;

                    int _char = message.lastIndexOf("_");
                    String messageCheck = message.substring(0, _char + 1);
                    final String friendsUid = message.substring(_char + 1);


                        runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(Friendz.this)
                                        .setTitle("FRIEND REQUEST")
                                        .setMessage("Are you sure you want to become friends with a device " + connectedDeviceName + "?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                int idReceive = Integer.parseInt(message);
                                                contains(niz,idReceive);


                                                if(contains(niz,idReceive)){
                                                    Toast.makeText(Friendz.this, "You are already friends", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    alreadyFriend = true;
                                                    DownloadManager.getInstance().addFriend(idReceive, apiKey);
                                                    Toast.makeText(Friendz.this, "Well done, you made a new friend!", Toast.LENGTH_LONG).show();
                                                    alreadyFriend = true;
                                                    DownloadManager.getInstance().addPoints(apiKey,5);

                                                    Toast.makeText(Friendz.this, "You got 5 points!", Toast.LENGTH_LONG).show();

                                                }

                                                adapter.notifyDataSetChanged();

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(Friendz.this, "You declined friend request", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });
                    //}
                    break;
                case MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MainActivity: handleMessage MESSAGE_DEVICE_NAME");
                    connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectedDeviceName + "\nClose upper window and confirm friend request.", Toast.LENGTH_LONG).show();
                    break;

                case MESSAGE_TOAST:
                    Log.d(TAG, "MainActivity: handleMessage MESSAGE_TOAST");
                  //  Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public boolean contains(final int [] array, final int key) {

    boolean yes = false;
        for(int i=0; i<100;i++)
        {
            if(array[i]==key)
            yes = true;
        }
        return yes;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "MainActivity: onActivityResult started");
        Log.d(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == BT_DISCOVERABLE_TIME) {
                    //Toast.makeText(this,"Setup chat", Toast.LENGTH_SHORT).show();
                    setupChat();
                    addNewFriend();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    //finish();
                }
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        Log.d(TAG, "MainActivity: connectDevice started");
        String address = data.getExtras().getString(DeviceListActivity1.DEVICE_ADDRESS);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        try {
            chatService.connect(device, secure);
        } catch (Exception e) {
            Toast.makeText(this, "Error! Other user must click on + button.", Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }

    private void ensureDiscoverable(BluetoothAdapter bluetoothAdapter) {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_DISCOVERABLE_TIME);
        startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT);

    }

    private void sendMessage(String message) {

        if (chatService.getState() != ChatService.STATE_CONNECTED) {
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatService.write(send);
            outStringBuffer.setLength(0);
        }
    }

    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };


    private boolean setupChat() {
        Log.d(TAG, "MainActivity: setupChat started");

        chatService = new ChatService(this, handler);

        outStringBuffer = new StringBuffer("");

        if (chatService.getState() == ChatService.STATE_NONE) {
            chatService.start();
        }
        return true;
    }

    // BITNO!!!
    private void sendFriendRequest() {
        String message = idUser;
        Log.d(TAG, "MainActivity: addNewFriend sendingMessage:" + message);
        sendMessage(message);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: onStart started");
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: onResume started");

    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: onPause started");

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatService != null)
            chatService.stop();
    }


    @Override
    public void ResponseOk(final String s) //on ceka da se thread zavrsi odnosno da dobije podatke sa servera
    {

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
                        //setErrorMessage();
                    }
                });
            } else {
                try {

            if(!alreadyFriend){
                    if (ok) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                String firstName = null;
                                try {

                                    JSONObject reader = new JSONObject(s);
                                    firstName = reader.getString("first_name");
                                    String lastName = reader.getString("last_name");
                                    String email = reader.getString("email");
                                    String points = reader.getString("points");
                                    String phoneNumber = reader.getString("phone_number");
                                    String urlImage = reader.getString("avatar");

                                    Intent intent = new Intent(Friendz.this, FriendProfileActivity.class);
                                    intent.putExtra("api", apiKey);
                                    intent.putExtra("fname", firstName);
                                    intent.putExtra("lname", lastName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("phone", phoneNumber);
                                    intent.putExtra("url", urlImage);
                                    intent.putExtra("points", points);
                                    startActivity(intent);

                                    ok = false;


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                    } else {
                        if (notOk) {
                            JSONObject reader = new JSONObject(s);
                            idUser = reader.getString("id");
                            notOk = false;
                        }

                        JSONArray friends = new JSONArray(s);
                        JSONObject[] elements = new JSONObject[friends.length()];
                        brojPrijatelja = friends.length();
                        final String[] names = new String[friends.length()];
                        final String[] id = new String[friends.length()];
                        for (int i = 0; i < friends.length(); i++) {
                            elements[i] = friends.getJSONObject(i);
                            names[i] = elements[i].getString("name");
                            id[i] = elements[i].getString("id");
                            // listItems.remove(i);
                            final int iterator = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    String iii = id[iterator];
                                    niz[iterator] = Integer.parseInt(iii);
                                    listItems.add("#" + id[iterator] + " " + names[iterator]);
                                    adapter.notifyDataSetChanged();

                                }
                            });
                        }
                    }
                } else {
                alreadyFriend = false;
                }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

