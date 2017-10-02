package com.example.riki.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewWifiActivity extends AppCompatActivity {

    private TextView name;
    private TextView password;
    private TextView createdBy;
    private ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wifi);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.435));


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        name = (TextView) findViewById(R.id.name);
        password = (TextView) findViewById(R.id.password);
        createdBy = (TextView) findViewById(R.id.createdBy);
        cancel = (ImageView) findViewById(R.id.cancelButton);

        name.setText(intent.getExtras().getString("name"));
        if(!intent.getExtras().getString("password").equals("null")){
            password.setText(intent.getExtras().getString("password"));
        } else {
            password.setText("No password");
        }
        createdBy.setText(intent.getExtras().getString("createdBy"));

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
