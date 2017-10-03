package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.riki.myplaces.User;
public class RankingActivity extends AppCompatActivity implements IThreadWakeUp {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<User> useri = new ArrayList<User>();
    ArrayList<String> listNum = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapterHiden;
    String apiKey;
    String idUser,pointsUser,nameUser;
    int popointsUser;
   // User Ouruser[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        final Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");
        idUser = String.valueOf(intent.getExtras().getString("id"));
        nameUser = intent.getExtras().getString("name");
        popointsUser = intent.getExtras().getInt("points");

     //   listNum.add("hihi");
    //    listItems.add( popointsUser + " points "+ "                      :                       " + nameUser);




        DownloadManager.getInstance().setThreadWakeUp(this);

        ListView friends = (ListView) findViewById(R.id.rankList);
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {


                String val =(String) listItems.get(position);
                val.substring(0,val.indexOf(' ')); // "72"
                val = val.replaceAll("\\D+","");


                // DownloadManager.getInstance().getAnyUser(apiKey,val);


                //TODO: Create a new profile activity that is non-editable, for opening profiles of user's friends
                //Or better yet, make a pop-up window with this information
            }
        });

        adapterHiden = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, listNum);

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

       // adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, useri);

        friends.setAdapter(adapter1);


        DownloadManager.getInstance().getUserFriends(apiKey);

    }


    @Override
    public void ResponseOk(final String s) {


        if (s.isEmpty()) {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje
        } else {
            /*String html = "<!DOCTYPE html>";
            if(s.toLowerCase().contains(html.toLowerCase()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        setErrorMessage();
                    }
                });
            }
            else {*/
            try {

                 final Comparator<User> DESCENDING_COMPARATOR = new Comparator<User>() {
                    // Overriding the compare method to sort the age
                    public int compare(User d, User d1) {
                        return d.points - d1.points;
                    }
                };

                final Comparator<User> comparator = new Comparator<User>(){

                    public int compare(User emp1, User emp2) {
                        return (emp1.points - emp2.points);
                    }

                };

                final JSONArray friends = new JSONArray(s);
                JSONObject[] elements = new JSONObject[friends.length()];
                final String[] names = new String[friends.length()];
                final String[] id = new String[friends.length()];
                final int[] points = new int[friends.length()];
                final User[] OurUser = new User[friends.length()];
                String val[] = new String[friends.length()];

                for (int i = 0; i < friends.length(); i++) {
                    elements[i] = friends.getJSONObject(i);
                    names[i] = elements[i].getString("name");
                    id[i] = elements[i].getString("id");
                    points[i] = elements[i].getInt("points");
                    final int iterator = i;

                    OurUser[i] = new User(
                            names[i],
                            points[i]
                    );

                    useri.add(OurUser[i]);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            Collections.sort(listItems);
                            Collections.reverse(listItems);

                            adapter1.notifyDataSetChanged();
                        }
                    });
                }

               useri = bubbleSort(useri);
               // adapter.notifyDataSetChanged();

                for(int i=0;i<friends.length();i++ ){
                    final int iterator = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            int a = useri.get(iterator).points;
                            String b = useri.get(iterator).name;
                            listItems.add(b + " has " + a + " points");
                            adapter1.notifyDataSetChanged();
                        }
                    });

                }





            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }



    public static ArrayList<User> bubbleSort(ArrayList<User> numArray) {

        int n = numArray.size();
        User temp = null;

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {

                if (numArray.get(j-1).points < numArray.get(j).points) {
                    temp = numArray.get(j-1);
                    numArray.set(j-1,numArray.get(j));
                    numArray.set(j,temp);
                }
            }
        }
        return numArray;
    }

    public final class UserComparator implements Comparator<User> {

        public int compareTo(User d1, User d2) {
            return d1.points - d2.points;
        }

        @Override
        public int compare(User o1, User o2) {
            return o1.points - o2.points;
        }
    }
}
